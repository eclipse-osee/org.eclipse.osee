/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.HasOptions;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.CharJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractSqlWriter implements HasOptions {
   protected static final String AND_NEW_LINE = " AND\n";
   protected final StringBuilder output = new StringBuilder();

   private final List<String> tableEntries = new ArrayList<>();
   private final List<WithClause> withClauses = new ArrayList<>();
   private final SqlAliasManager aliasManager = new SqlAliasManager();
   private final HashMap<TableEnum, String> mainAliases = new HashMap<>();
   private final SqlJoinFactory joinFactory;
   private final JdbcClient jdbcClient;
   private final SqlContext context;
   private final QueryType queryType;
   private int level;
   private boolean firstField;

   public AbstractSqlWriter(SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryType queryType) {
      this.joinFactory = joinFactory;
      this.jdbcClient = jdbcClient;
      this.context = context;
      this.queryType = queryType;
   }

   public void build(SqlHandler<?>... handlers) {
      build(Arrays.asList(handlers));
   }

   public void build(List<SqlHandler<?>> handlers) {
      reset();

      write(handlers);

      String sql = toString();
      context.setSql(sql);
   }

   protected void reset() {
      output.delete(0, output.length());
      context.getJoins().clear();
      context.getParameters().clear();
      tableEntries.clear();
      withClauses.clear();
      aliasManager.reset();
      level = 0;
      firstField = true;
      mainAliases.clear();
   }

   public boolean isCountQueryType() {
      return QueryType.COUNT == queryType;
   }

   public boolean isTokenQueryType() {
      return QueryType.TOKEN == queryType;
   }

   protected void write(Iterable<SqlHandler<?>> handlers) {
      computeWithClause(handlers);
      computeTables(handlers);

      writeWithClauses();
      writeSelect(handlers);
      write("\n FROM ");
      writeTables();
      write("\n WHERE ");
      writePredicates(handlers);

      removeDanglingSeparator("\n WHERE ");
      writeGroupAndOrder();
   }

   protected void writeWithClauses() {
      if (Conditions.hasValues(withClauses)) {
         write("WITH");
         int size = withClauses.size();
         for (int i = 0; i < size; i++) {
            WithClause clause = withClauses.get(i);
            if (clause.isRecursive()) {
               write(jdbcClient.getDbType().getRecursiveWithSql());
            }
            write(" ");
            write(clause.getName());
            if (clause.hasParameters()) {
               write(" ");
               write(clause.getParameters());
            }
            write(" AS ( \n");
            write(clause.getBody());
            write("\n )");
            if (i + 1 < size) {
               write(", \n");
            }
         }
         write("\n");
      }
   }

   /**
    * Add a named query into a recursive WITH statement that is referenced in the main FROM clause
    */
   public String addRecursiveReferencedWithClause(String withClauseName, String parameters, String body) {
      withClauses.add(new WithClause(withClauseName, parameters, body));
      tableEntries.add(withClauseName);
      return withClauseName;
   }

   /**
    * Add a named query into a WITH statement that is not referenced in the main FROM clause
    */
   public String addWithClause(String prefix, String body) {
      String withClauseName = getNextAlias(prefix);
      withClauses.add(new WithClause(withClauseName, body));
      return withClauseName;
   }

   /**
    * Add a named query into a WITH statement that is referenced in the main FROM clause
    */
   public String addReferencedWithClause(String prefix, String body) {
      String withClauseName = addWithClause(prefix, body);
      tableEntries.add(withClauseName);
      return withClauseName;
   }

   protected void computeWithClause(Iterable<SqlHandler<?>> handlers) {
      for (SqlHandler<?> handler : handlers) {
         setHandlerLevel(handler);
         handler.addWithTables(this);
      }
   }

   public SqlContext getContext() {
      return context;
   }

   protected void writeSelect(Iterable<SqlHandler<?>> handlers) {
      writeSelectAndHint();
      if (isCountQueryType()) {
         if (OptionsUtil.isHistorical(getOptions())) {
            write("count(xTable.art_id) FROM (");
            writeSelectAndHint();
            write("1");
         } else {
            write("count(*)");
         }
      } else {
         writeSelectFields();
         for (SqlHandler<?> handler : handlers) {
            handler.writeSelectFields(this);
         }
      }
   }

   public void writeCommaIfNotFirst() {
      if (firstField) {
         firstField = false;
      } else {
         write(", ");
      }
   }

   protected abstract void writeSelectFields();

   public void writeSelectFields(String... tablesAndFields) {
      for (int i = 0; i < tablesAndFields.length; i++) {
         writeCommaIfNotFirst();
         String table = tablesAndFields[i++];
         String field = tablesAndFields[i];
         write(table);
         write(".");
         write(field);
      }
   }

   public abstract String getWithClauseTxBranchFilter(String txsAlias, boolean deletedPredicate);

   public void writeTxBranchFilter(String txsAlias) {
      boolean allowDeleted =
         OptionsUtil.areDeletedArtifactsIncluded(getOptions()) || OptionsUtil.areDeletedAttributesIncluded(
            getOptions()) || OptionsUtil.areDeletedRelationsIncluded(getOptions());
      writeTxBranchFilter(txsAlias, allowDeleted);
   }

   public abstract void writeTxBranchFilter(String txsAlias, boolean allowDeleted);

   protected void writeTxCurrentFilter(String txsAlias, StringBuilder sb, boolean allowDeleted) {
      sb.append(txsAlias);
      sb.append(".tx_current");
      if (allowDeleted) {
         sb.append(" <> ");
         sb.append(TxCurrent.NOT_CURRENT.getIdString());
      } else {
         sb.append(" = ");
         sb.append(TxCurrent.CURRENT.getIdString());
      }
   }

   protected abstract void writeGroupAndOrder();

   protected void writeTables() {
      boolean first = true;
      for (String tableEntry : tableEntries) {
         if (first) {
            first = false;
         } else {
            write(", ");
         }
         write(tableEntry);
      }
   }

   protected void computeTables(Iterable<SqlHandler<?>> handlers) {
      for (SqlHandler<?> handler : handlers) {
         setHandlerLevel(handler);
         handler.addTables(this);
      }
   }

   protected void setHandlerLevel(SqlHandler<?> handler) {
      level = handler.getLevel();
   }

   protected void writePredicates(Iterable<SqlHandler<?>> handlers) {
      Iterator<SqlHandler<?>> iterator = handlers.iterator();
      boolean first = true;
      while (iterator.hasNext()) {
         SqlHandler<?> handler = iterator.next();
         setHandlerLevel(handler);
         if (handler.hasPredicates()) {
            if (first) {
               first = false;
            } else {
               write(AND_NEW_LINE);
            }
            handler.addPredicates(this);
         }
      }

      String mainTableAlias = mainAliases.get(TableEnum.ARTIFACT_TABLE);
      if (mainTableAlias != null) {
         if (!first) {
            write(" AND ");
         }
         String mainTxsAlias = getMainTableAlias(TableEnum.TXS_TABLE);
         writeEqualsAnd(mainTableAlias, mainTxsAlias, "gamma_id");
         writeTxBranchFilter(mainTxsAlias);
      }
   }

   public void writeAndLn() {
      write(AND_NEW_LINE);
   }

   protected void removeDanglingSeparator(String token) {
      int length = output.length();
      int index = output.lastIndexOf(token);
      if (index == length - token.length()) {
         output.delete(index, length);
      }
   }

   protected boolean hasAlias(TableEnum table) {
      return getAliasManager().hasAlias(level, table, table.getObjectType());
   }

   public List<String> getAliases(TableEnum table) {
      return getAliasManager().getAliases(level, table, table.getObjectType());
   }

   public String getFirstAlias(TableEnum table) {
      return getFirstAlias(level, table, table.getObjectType());
   }

   public String getFirstAlias(int level, TableEnum table, ObjectType type) {
      return getAliasManager().getFirstAlias(level, table, type);
   }

   public String getLastAlias(TableEnum table) {
      ObjectType type = table.getObjectType();
      return getAliasManager().getLastAlias(table, type);
   }

   public String getNextAlias(TableEnum table) {
      return getNextAlias(table.getPrefix(), table.getObjectType());
   }

   private String getNextAlias(String prefix, ObjectType type) {
      return getAliasManager().getNextAlias(prefix, type);
   }

   public String getNextAlias(String prefix) {
      return getAliasManager().getNextAlias(prefix, ObjectType.UNKNOWN);
   }

   public SqlAliasManager getAliasManager() {
      return aliasManager;
   }

   public String addTable(TableEnum table) {
      return addTable(table, table.getObjectType());
   }

   public String addTable(TableEnum table, ObjectType objectType) {
      String alias = getNextAlias(table.getPrefix(), objectType);
      tableEntries.add(String.format("%s %s", table.getName(), alias));
      return alias;
   }

   public String getMainTableAlias(TableEnum table) {
      String alias = mainAliases.get(table);
      if (alias == null) {
         alias = addTable(table);
         mainAliases.put(table, alias);
      }
      return alias;
   }

   public void writeTableNoAlias(TableEnum table) {
      write(table.getName());
   }

   public String writeTable(TableEnum table) {
      String alias = getNextAlias(table);
      write("%s %s", table.getName(), alias);
      return alias;
   }

   public void write(String format, Object... params) {
      if (params != null && params.length > 0) {
         output.append(String.format(format, params));
      } else {
         output.append(format);
      }
   }

   public void write(String sql) {
      output.append(sql);
   }

   public void writeEquals(String table1, String table2, String column) {
      write("%s.%s = %s.%s", table1, column, table2, column);
   }

   public void writeEqualsAnd(String table1, String table2, String column) {
      writeEquals(table1, table2, column);
      write(" AND ");
   }

   public void writeEquals(String table1, String column1, String table2, String column2) {
      write("%s.%s = %s.%s", table1, column1, table2, column2);
   }

   public void writeEqualsAnd(String table1, String column1, String table2, String column2) {
      writeEquals(table1, column1, table2, column2);
      write(" AND ");
   }

   public void addParameter(Object parameter) {
      getContext().getParameters().add(parameter);
   }

   public void writeEqualsParameter(String table, String column, Object parameter) {
      output.append(table);
      output.append(".");
      writeEqualsParameter(column, parameter);
   }

   public void writeEqualsParameterAnd(String table, String column, Object parameter) {
      writeEqualsParameter(table, column, parameter);
      write(" AND ");
   }

   public void writeEqualsParameter(String column, Object parameter) {
      output.append(column);
      output.append(" = ?");
      addParameter(parameter);
   }

   public void writeEqualsParameterAnd(String column, Object parameter) {
      writeEqualsParameter(column, parameter);
      write(" AND ");
   }

   private void addJoin(AbstractJoinQuery join) {
      getContext().getJoins().add(join);
   }

   public CharJoinQuery writeCharJoin(Collection<String> ids) {
      CharJoinQuery joinQuery = joinFactory.createCharJoinQuery();
      joinQuery.addAll(ids);
      addJoin(joinQuery);
      return joinQuery;
   }

   public IdJoinQuery writeJoin(Collection<? extends Id> ids) {
      IdJoinQuery joinQuery = joinFactory.createIdJoinQuery();
      joinQuery.addAll(ids);
      addJoin(joinQuery);
      return joinQuery;
   }

   public IdJoinQuery writeIdJoin(Collection<? extends Number> ids) {
      IdJoinQuery joinQuery = joinFactory.createIdJoinQuery();
      joinQuery.addAll(ids);
      addJoin(joinQuery);
      return joinQuery;
   }

   @Override
   public Options getOptions() {
      return getContext().getOptions();
   }

   protected void writeSelectAndHint() {
      write("SELECT");
      if (!Conditions.hasValues(withClauses) && jdbcClient != null && jdbcClient.getConfig() != null) {
         write(OseeSql.Strings.getHintsOrdered(jdbcClient.getConfig().getDbProps()));
      }
      write(" ");
   }

   @Override
   public String toString() {
      return output.toString();
   }

   public void writePatternMatch(String field, String expression) {
      String pattern = jdbcClient.getDbType().getRegularExpMatchSql();
      write(pattern, field, expression);
   }
}