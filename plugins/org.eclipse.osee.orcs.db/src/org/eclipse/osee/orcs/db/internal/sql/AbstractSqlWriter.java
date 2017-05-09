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
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.HasOptions;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.CharJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractSqlWriter implements HasOptions {

   protected static final String AND_WITH_NEWLINES = "\n AND \n";

   private final StringBuilder output = new StringBuilder();
   private final List<String> tableEntries = new ArrayList<>();
   private final List<WithClause> withClauses = new ArrayList<>();
   private final SqlAliasManager aliasManager = new SqlAliasManager();

   private final Log logger;
   private final SqlJoinFactory joinFactory;
   private final JdbcClient jdbcClient;
   private final SqlContext context;
   private final QueryType queryType;
   private int level = 0;

   public AbstractSqlWriter(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryType queryType) {
      this.logger = logger;
      this.joinFactory = joinFactory;
      this.jdbcClient = jdbcClient;
      this.context = context;
      this.queryType = queryType;
   }

   public void build(SqlHandler<?>... handlers) throws OseeCoreException {
      build(Arrays.asList(handlers));
   }

   public void build(List<SqlHandler<?>> handlers) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(handlers, "SqlHandlers");
      reset();

      write(handlers);

      String sql = toString();
      context.setSql(sql);

      if (logger.isTraceEnabled()) {
         logger.trace("Sql Writer - [%s]", context);
      }
   }

   protected void reset() {
      output.delete(0, output.length());
      context.getJoins().clear();
      context.getParameters().clear();
      tableEntries.clear();
      withClauses.clear();
      aliasManager.reset();
      level = 0;
   }

   public boolean isCountQueryType() {
      return QueryType.COUNT == queryType;
   }

   protected void write(Iterable<SqlHandler<?>> handlers) throws OseeCoreException {
      computeTables(handlers);
      computeWithClause(handlers);

      writeWithClause();
      writeSelect(handlers);
      write("\n FROM \n");
      writeTables();
      write("\n WHERE \n");
      writePredicates(handlers);

      if (toString().endsWith("\n WHERE \n")) {
         removeDanglingSeparator("\n WHERE \n");
      }
      writeGroupAndOrder();
   }

   protected void writeWithClause() throws OseeCoreException {
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

   public void addWithClause(WithClause clause) {
      withClauses.add(clause);
   }

   protected void computeWithClause(Iterable<SqlHandler<?>> handlers) throws OseeCoreException {
      for (SqlHandler<?> handler : handlers) {
         setHandlerLevel(handler);
         handler.addWithTables(this);
      }
   }

   public SqlContext getContext() {
      return context;
   }

   protected abstract void writeSelect(Iterable<SqlHandler<?>> handlers) throws OseeCoreException;

   public abstract String getWithClauseTxBranchFilter(String txsAlias, boolean deletedPredicate) throws OseeCoreException;

   public abstract String getTxBranchFilter(String txsAlias) throws OseeCoreException;

   public abstract String getTxBranchFilter(String txsAlias, boolean allowDeleted) throws OseeCoreException;

   protected abstract void writeGroupAndOrder() throws OseeCoreException;

   protected void writeTables() throws OseeCoreException {
      int size = tableEntries.size();
      for (int i = 0; i < size; i++) {
         write(tableEntries.get(i));
         if (i + 1 < size) {
            write(", ");
         }
      }
   }

   protected void computeTables(Iterable<SqlHandler<?>> handlers) throws OseeCoreException {
      for (SqlHandler<?> handler : handlers) {
         setHandlerLevel(handler);
         handler.addTables(this);
      }
   }

   protected void setHandlerLevel(SqlHandler<?> handler) {
      level = handler.getLevel();
   }

   protected void writePredicates(Iterable<SqlHandler<?>> handlers) throws OseeCoreException {
      Iterator<SqlHandler<?>> iterator = handlers.iterator();
      while (iterator.hasNext()) {
         SqlHandler<?> handler = iterator.next();
         setHandlerLevel(handler);
         boolean modified = handler.addPredicates(this);
         if (modified && iterator.hasNext()) {
            writeAndLn();
         }
      }
      removeDanglingSeparator(AND_WITH_NEWLINES);
   }

   public void writeAndLn() throws OseeCoreException {
      write(AND_WITH_NEWLINES);
   }

   protected void removeDanglingSeparator(String token) {
      int length = output.length();
      int index = output.lastIndexOf(token);
      if (index == length - token.length()) {
         output.delete(index, length);
      }
   }

   protected boolean hasAlias(TableEnum table) {
      ObjectType type = getObjectType(table);
      return getAliasManager().hasAlias(level, table, type);
   }

   public List<String> getAliases(TableEnum table) {
      ObjectType type = getObjectType(table);
      return getAliasManager().getAliases(level, table, type);
   }

   public String getFirstAlias(TableEnum table) {
      ObjectType type = getObjectType(table);
      return getFirstAlias(level, table, type);
   }

   public String getFirstAlias(int level, TableEnum table, ObjectType type) {
      return getAliasManager().getFirstAlias(level, table, type);
   }

   public String getLastAlias(TableEnum table) {
      ObjectType type = getObjectType(table);
      return getLastAlias(table, type);
   }

   public String getLastAlias(TableEnum table, ObjectType type) {
      int level = getAliasManager().getLevel();
      return getAliasManager().getLastAlias(level, table, type);
   }

   public String getNextAlias(AliasEntry table) {
      ObjectType type = getObjectType(table);
      return getNextAlias(table, type);
   }

   private String getNextAlias(AliasEntry table, ObjectType type) {
      int level = getAliasManager().getLevel();
      return getAliasManager().getNextAlias(level, table, type);
   }

   private ObjectType getObjectType(AliasEntry entry) {
      ObjectType toReturn = ObjectType.UNKNOWN;
      if (entry instanceof TableEnum) {
         TableEnum table = (TableEnum) entry;
         switch (table) {
            case BRANCH_TABLE:
               toReturn = ObjectType.BRANCH;
               break;
            case TX_DETAILS_TABLE:
               toReturn = ObjectType.TX;
               break;
            case ARTIFACT_TABLE:
               toReturn = ObjectType.ARTIFACT;
               break;
            case ATTRIBUTE_TABLE:
               toReturn = ObjectType.ATTRIBUTE;
               break;
            case RELATION_TABLE:
               toReturn = ObjectType.RELATION;
               break;
            default:
               break;
         }
      }
      return toReturn;
   }

   public int nextAliasLevel() {
      return getAliasManager().nextLevel();
   }

   public SqlAliasManager getAliasManager() {
      return aliasManager;
   }

   public void addTable(String table) {
      tableEntries.add(table);
   }

   public String getOrCreateTableAlias(TableEnum table) {
      String alias = getFirstAlias(table);
      if (alias == null) {
         alias = addTable(table);
      }
      return alias;
   }

   public String getOrCreateTableAlias(TableEnum table, ObjectType objectType) {
      String alias = getFirstAlias(table);
      if (alias == null) {
         alias = addTable(table, objectType);
      }
      return alias;
   }

   public String addTable(AliasEntry table) {
      String alias = getNextAlias(table);
      tableEntries.add(String.format("%s %s", table.getName(), alias));
      return alias;
   }

   public String addTable(AliasEntry table, ObjectType objectType) {
      String alias = getNextAlias(table, objectType);
      tableEntries.add(String.format("%s %s", table.getName(), alias));
      return alias;
   }

   public void write(String data, Object... params) throws OseeCoreException {
      if (params != null && params.length > 0) {
         output.append(String.format(data, params));
      } else {
         output.append(data);
      }
   }

   public void writeEquals(String table1, String table2, String column) {
      write("%s.%s = %s.%s", table1, column, table2, column);
   }

   public void addParameter(Object data) {
      getContext().getParameters().add(data);
   }

   private void addJoin(AbstractJoinQuery join) {
      getContext().getJoins().add(join);
   }

   public CharJoinQuery writeCharJoin(Collection<String> ids) {
      CharJoinQuery joinQuery = joinFactory.createCharJoinQuery();
      ids.forEach(id -> joinQuery.add(id));
      addJoin(joinQuery);
      return joinQuery;
   }

   public IdJoinQuery writeJoin(Collection<? extends Id> ids) {
      IdJoinQuery joinQuery = joinFactory.createIdJoinQuery();
      ids.forEach(id -> joinQuery.add(id.getId()));
      addJoin(joinQuery);
      return joinQuery;
   }

   public IdJoinQuery writeIdJoin(Collection<? extends Number> ids) {
      IdJoinQuery joinQuery = joinFactory.createIdJoinQuery();
      ids.forEach(id -> joinQuery.add(id));
      addJoin(joinQuery);
      return joinQuery;
   }

   public IdJoinQuery writeIdentifiableJoin(Collection<? extends Identifiable<Long>> ids) {
      IdJoinQuery joinQuery = joinFactory.createIdJoinQuery();
      for (Identity<Long> id : ids) {
         joinQuery.add(id.getGuid());
      }
      addJoin(joinQuery);
      return joinQuery;
   }

   @Override
   public Options getOptions() {
      return getContext().getOptions();
   }

   protected String getSqlHint() throws OseeCoreException {
      String hint = Strings.EMPTY_STRING;
      if (!Conditions.hasValues(withClauses) && jdbcClient != null && jdbcClient.getConfig() != null) {
         hint = OseeSql.Strings.getHintsOrdered(jdbcClient.getConfig().getDbProps());
      }
      return hint;
   }

   @Override
   public String toString() {
      return output.toString();
   }

   public void writePatternMatch(String field, String expression) throws OseeCoreException {
      String pattern = jdbcClient.getDbType().getRegularExpMatchSql();
      write(pattern, field, expression);
   }
}