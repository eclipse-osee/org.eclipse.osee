/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.jdbc.SqlTable;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.HasOptions;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.CharJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractSqlWriter implements HasOptions {
   private static final String AND_NEW_LINE = " AND\n";
   private final List<String> tableEntries = new ArrayList<>();
   private final SqlAliasManager aliasManager = new SqlAliasManager();
   private final JdbcClient jdbcClient;
   private final SqlContext context;
   protected final StringBuilder output = new StringBuilder();
   protected final QueryData rootQueryData;
   protected final SqlJoinFactory joinFactory;
   private int level;
   private boolean firstField;
   private boolean firstCte = true;
   protected QueryData queryDataCursor;
   private String tupleAlias;
   private String tupleTxsAlias;
   private String multiTableHintParameter = "";

   public AbstractSqlWriter(SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryData rootQueryData) {
      this.joinFactory = joinFactory;
      this.jdbcClient = jdbcClient;
      this.context = context;
      this.rootQueryData = rootQueryData;
      this.queryDataCursor = rootQueryData;
   }

   public AbstractSqlWriter(SqlJoinFactory joinFactory, JdbcClient jdbcClient, QueryData rootQueryData) {
      this(joinFactory, jdbcClient, null, rootQueryData);
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
      if (context != null) {
         context.getJoins().clear();
         context.getParameters().clear();
      }
      getTableEntries().clear();
      aliasManager.reset();
      level = 0;
      firstCte = true;
      multiTableHintParameter = "";
   }

   protected void write(Iterable<SqlHandler<?>> handlers) {
      write(handlers, null);
   }

   protected String write(Iterable<SqlHandler<?>> handlers, String ctePrefix) {
      String cteAlias = null;
      writeToWithClause(handlers);
      if (ctePrefix == null) {
         finishWithClause();
      } else {
         cteAlias = startCommonTableExpression(ctePrefix);
      }

      computeTables(handlers);
      writeSelect(handlers);
      write("\n FROM ");
      writeTables();

      write("\n WHERE ");
      writePredicates(handlers);

      removeDanglingSeparator("\n WHERE ");

      writeGroupAndOrder(handlers);
      return cteAlias;
   }

   /**
    * Add a named recursive (if parameters is not null) query using a Common Table Expression (WITH statement)
    */
   public String startRecursiveCommonTableExpression(String prefix, String parameters) {
      boolean isRecursive = parameters != null;
      String cteAlias = getNextAlias(prefix);
      if (firstCte) {
         write("WITH ");
         firstCte = false;
      } else {
         write("),\n");
      }
      if (isRecursive) {
         write(jdbcClient.getDbType().getRecursiveWithSql());
         write(" ");
      }
      write(cteAlias);
      if (isRecursive) {
         write(" ");
         write(parameters);
      }
      write(" AS (\n ");
      return cteAlias;
   }

   /**
    * Write union keyword in recursive query using a Common Table Expression (WITH statement)
    */
   public void writeCteRecursiveUnion() {
      write("\n ");
      write(jdbcClient.getDbType().getCteRecursiveUnion());
      write("\n");
   }

   /**
    * Add a named non-recursive query using a Common Table Expression (WITH statement)
    */
   public String startCommonTableExpression(String prefix) {
      return startRecursiveCommonTableExpression(prefix, null);
   }

   protected void writeWithClause(Iterable<SqlHandler<?>> handlers) {
      writeToWithClause(handlers);
      finishWithClause();
   }

   private void writeToWithClause(Iterable<SqlHandler<?>> handlers) {
      for (SqlHandler<?> handler : handlers) {
         setHandlerLevel(handler);
         handler.writeCommonTableExpression(this);
      }
   }

   protected void finishWithClause() {
      if (!firstCte) {
         write(")\n");
      }
   }

   public SqlContext getContext() {
      return context;
   }

   protected void writeSelect(Iterable<SqlHandler<?>> handlers) {
      writeSelectAndHint();
      if (rootQueryData.isCountQueryType()) {
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

   public void writeTxBranchFilter(String txsAlias) {
      boolean allowDeleted =
         OptionsUtil.areDeletedArtifactsIncluded(getOptions()) || OptionsUtil.areDeletedAttributesIncluded(
            getOptions()) || OptionsUtil.areDeletedRelationsIncluded(getOptions());
      writeTxBranchFilter(txsAlias, allowDeleted);
   }

   public void writeTxBranchFilter(String txsAlias, boolean allowDeleted) {
      writeTxFilter(txsAlias, allowDeleted);
      writeBranchFilter(txsAlias);
   }

   public void writeBranchFilter(String txsAlias) {
      BranchId branch = rootQueryData.getBranch();
      if (branch.isValid()) {
         write(" AND ");
         writeEqualsParameter(txsAlias, "branch_id", branch);
      } else {
         throw new OseeArgumentException("writeBranchFilter: branch id must be valid not:" + branch);
      }
   }

   protected void writeTxFilter(String txsAlias, boolean allowDeleted) {
      if (OptionsUtil.isHistorical(getOptions())) {
         write(txsAlias);
         write(".transaction_id <= ?");
         addParameter(OptionsUtil.getFromTransaction(getOptions()));
         if (!allowDeleted) {
            write(" AND ");
            write(txsAlias);
            write(".mod_type <> ");
            write(ModificationType.DELETED.getIdString());
         }
      } else {
         writeTxCurrentFilter(txsAlias, allowDeleted);
      }
   }

   protected void writeTxCurrentFilter(String txsAlias, boolean allowDeleted) {
      write(txsAlias);
      write(".tx_current");
      if (allowDeleted) {
         write(" <> ");
         write(TxCurrent.NOT_CURRENT.getIdString());
      } else {
         write(" = ");
         write(TxCurrent.CURRENT.getIdString());
      }
   }

   protected abstract void writeGroupAndOrder(Iterable<SqlHandler<?>> handlers);

   protected void writeTables() {
      boolean first = true;
      for (String tableEntry : getTableEntries()) {
         if (first) {
            first = false;
         } else {
            write(", ");
         }
         write(tableEntry);
      }
      getTableEntries().clear();
   }

   protected void computeTables(Iterable<SqlHandler<?>> handlers) {
      for (SqlHandler<?> handler : handlers) {
         setHandlerLevel(handler);
         handler.addTables(this);
      }
      if (queryDataCursor.getView().isValid()) {
         tupleAlias = addTable(OseeDb.TUPLE2);
         tupleTxsAlias = addTable(OseeDb.TXS_TABLE);
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

      if (mainTableAliasExists(OseeDb.ARTIFACT_TABLE)) {
         if (!first) {
            write(" AND ");
         }
         String mainTableAlias = getMainTableAlias(OseeDb.ARTIFACT_TABLE);
         String mainTxsAlias = getMainTableAlias(OseeDb.TXS_TABLE);
         writeEqualsAnd(mainTableAlias, mainTxsAlias, "gamma_id");
         if (mainTableAliasExists(OseeDb.OSEE_KEY_VALUE_TABLE)) {
            String mainKvAlias = getMainTableAlias(OseeDb.OSEE_KEY_VALUE_TABLE);
            writeEqualsAnd(mainTxsAlias, "app_id", mainKvAlias, "key");
         }

         writeTxBranchFilter(mainTxsAlias);
         if (queryDataCursor.getAppId().isValid()) {
            write(" AND ");
            writeEqualsParameter(mainTxsAlias, "app_id", queryDataCursor.getAppId());
         }
         if (queryDataCursor.getView().isValid()) {
            write(" AND ");
            writeEqualsParameterAnd(tupleAlias, "tuple_type", CoreTupleTypes.ViewApplicability);
            writeEqualsParameterAnd(tupleAlias, "e1", queryDataCursor.getView());
            writeEqualsAnd(tupleAlias, tupleTxsAlias, "gamma_id");
            writeEqualsAnd(tupleAlias, "e2", getMainTableAlias(OseeDb.TXS_TABLE), "app_id");
            writeTxBranchFilter(tupleTxsAlias);
         }
      }
   }

   protected boolean mainTableAliasExists(SqlTable table) {
      return queryDataCursor.mainTableAliasExists(table);
   }

   public void writeAnd() {
      write(" AND ");
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

   protected boolean hasAlias(SqlTable table) {
      return getAliasManager().hasAlias(level, table, table.getObjectType());
   }

   public List<String> getAliases(SqlTable table) {
      return getAliasManager().getAliases(level, table, table.getObjectType());
   }

   public String getFirstAlias(SqlTable table) {
      return getFirstAlias(level, table, table.getObjectType());
   }

   public String getFirstAlias(int level, SqlTable table, ObjectType type) {
      return getAliasManager().getFirstAlias(level, table, type);
   }

   public String getLastAlias(SqlTable table) {
      ObjectType type = table.getObjectType();
      return getAliasManager().getLastAlias(table, type);
   }

   public String getNextAlias(SqlTable table) {
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

   public String addTable(SqlTable table) {
      return addTable(table, table.getObjectType());
   }

   public String addTable(RelationTypeToken relationType) {
      return addTable(getRelationTable(relationType));
   }

   public void addTable(String tableName) {
      getTableEntries().add(tableName);
   }

   public String addTable(SqlTable table, ObjectType objectType) {
      String alias = getNextAlias(table.getPrefix(), objectType);
      getTableEntries().add(String.format("%s %s", table.getName(), alias));
      if (multiTableHintParameter.length() > 0) {
         multiTableHintParameter = multiTableHintParameter + " " + alias;
      } else {
         multiTableHintParameter = alias;
      }
      return alias;
   }

   public String getMultiTableHintParameter() {
      return multiTableHintParameter;
   }

   public String getMainTableAlias(SqlTable table) {
      return queryDataCursor.getMainTableAlias(table, this::addTable);
   }

   public void writeTableNoAlias(SqlTable table) {
      write(table.getName());
   }

   public String writeTable(SqlTable table) {
      String alias = getNextAlias(table);
      write("%s %s", table.getName(), alias);
      return alias;
   }

   public String writeTable(RelationTypeToken relationType) {
      return writeTable(getRelationTable(relationType));
   }

   private SqlTable getRelationTable(RelationTypeToken relationType) {
      return relationType.isNewRelationTable() ? OseeDb.RELATION_TABLE2 : OseeDb.RELATION_TABLE;
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

   protected void addJoin(AbstractJoinQuery join) {
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

   @Override
   public Options getOptions() {
      return getContext().getOptions();
   }

   protected void writeUseNlTableHint(String hintParams) {
      writeSelectAndMultiTableHint("USE_NL", hintParams);
   }

   protected void writeSelectAndMultiTableHint(String hint, String hintParams) {
      write("SELECT");
      write(jdbcClient.getMultiTableHint(hint, hintParams));
      write(" ");
      firstField = true;
   }

   protected void writeSelectAndHint() {
      write("SELECT");
      write(jdbcClient.getOrderedHint());
      write(" ");
      firstField = true;
   }

   @Override
   public String toString() {
      return output.toString();
   }

   public void writePatternMatch(String table, String column, String pattern) {
      writePatternMatch(table + "." + column, pattern);
   }

   public void writePatternMatch(String field, String pattern) {
      write(jdbcClient.getDbType().getRegularExpMatchSql(field));
      addParameter(pattern);
   }

   public JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   public List<String> getTableEntries() {
      return tableEntries;
   }
}