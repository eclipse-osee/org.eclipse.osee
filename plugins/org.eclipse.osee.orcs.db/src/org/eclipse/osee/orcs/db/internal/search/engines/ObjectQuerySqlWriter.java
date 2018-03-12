/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import static org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver.getColumnInfo;
import static org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver.getObjectField;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.ResultObjectDescription;
import org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerPriority;
import org.eclipse.osee.orcs.db.internal.search.handlers.XtraAttributeDataSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.XtraBranchDataSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.XtraRelationDataSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.XtraTxDataSqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.ObjectField;
import org.eclipse.osee.orcs.db.internal.sql.ObjectType;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerComparator;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * Used to create ORCS Script queries
 *
 * @author Roberto E. Escobar
 */
public class ObjectQuerySqlWriter extends AbstractSqlWriter {

   private static final SqlHandlerComparator HANDLER_COMPARATOR = new SqlHandlerComparator();
   private final Log logger;
   private final SqlFieldResolver fieldResolver;

   private WithClauseTxFilterData withTxFilterClause;

   public ObjectQuerySqlWriter(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryType queryType, QueryData queryData) {
      super(logger, joinFactory, jdbcClient, context, queryType);
      this.logger = logger;
      this.fieldResolver = new SqlFieldResolver(getAliasManager(), queryData.getSelectSets());
   }

   @Override
   public void build(List<SqlHandler<?>> handlers) {
      super.build(handlers);
      getContext().setObjectDescription(fieldResolver.getResult());
   }

   @Override
   protected void write(Iterable<SqlHandler<?>> handlers) {
      withTxFilterClause = null;
      fieldResolver.reset();
      computeTxFilterClause(handlers);
      computeTables(handlers);

      fieldResolver.resolve();

      computeWithClause(handlers);

      List<SqlHandler<?>> xtraHandlers;
      if (fieldResolver.hasUnresolvedFields()) {
         xtraHandlers = getFieldResolvers(fieldResolver.getUnresolved());

         computeTables(xtraHandlers);
         fieldResolver.resolve();
      } else {
         xtraHandlers = Collections.emptyList();
      }

      writeWithClause();
      writeSelect(handlers);
      write("\n FROM \n");
      writeTables();

      write("\n WHERE \n");
      writePredicates(Iterables.concat(handlers, xtraHandlers));

      removeDanglingSeparator("\n WHERE \n");

      writeGroupAndOrder();
   }

   @Override
   public void writeSelect(Iterable<SqlHandler<?>> handlers) {
      if (isCountQueryType()) {
         throw new UnsupportedOperationException("Count dynamic query not supported");
      } else {
         write("SELECT%s ", getSqlHint());
         writeSelects(fieldResolver.getResult());
      }
   }

   private void writeSelects(ResultObjectDescription result) {
      MutableBoolean isFirst = new MutableBoolean(true);
      for (DynamicData data : result.getDynamicData()) {
         writeSelects(data, isFirst);
      }
   }

   private void writeSelects(DynamicData data, MutableBoolean isFirst) {
      if (data instanceof DynamicObject) {
         DynamicObject object = (DynamicObject) data;
         for (DynamicData child : object.getChildren()) {
            writeSelects(child, isFirst);
         }
      } else {
         Map<String, String> columnInfo = getColumnInfo(data);
         for (Entry<String, String> entry : columnInfo.entrySet()) {
            if (!isFirst.getValue()) {
               write(", ");
            } else {
               isFirst.setValue(false);
            }
            String columnUuid = entry.getKey();
            String qualifiedColumn = entry.getValue();

            write(qualifiedColumn);
            write(" as ");
            write(columnUuid);
         }
      }
   }

   @Override
   public void writeGroupAndOrder() {
      if (OptionsUtil.isHistorical(getOptions())) {
         throw new UnsupportedOperationException("Historical dynamic query not supported");
      }
      if (!isCountQueryType()) {
         boolean isFirst = true;
         for (String value : fieldResolver.getSortFields()) {
            if (isFirst) {
               isFirst = false;
               write("\n ORDER BY ");
            } else {
               write(", ");
            }
            write(value);
         }
      } else {
         if (OptionsUtil.isHistorical(getOptions())) {
            throw new UnsupportedOperationException("Historical Count dynamic query not supported");
         }
      }
   }

   @Override
   public String getWithClauseTxBranchFilter(String txsAlias, boolean deletedPredicate) {
      StringBuilder sb = new StringBuilder();

      if (deletedPredicate) {
         boolean allowDeleted =
            OptionsUtil.areDeletedArtifactsIncluded(getOptions()) || OptionsUtil.areDeletedAttributesIncluded(
               getOptions()) || OptionsUtil.areDeletedRelationsIncluded(getOptions());
         writeTxFilter(txsAlias, sb, allowDeleted);
      } else {
         if (OptionsUtil.isHistorical(getOptions())) {
            sb.append(txsAlias);
            sb.append(".transaction_id <= ?");
            addParameter(OptionsUtil.getFromTransaction(getOptions()));
         }
      }

      if (withTxFilterClause != null) {
         sb.append("\n AND \n   ");
         sb.append(txsAlias);
         sb.append(".branch_id = ");

         sb.append("(");
         sb.append(withTxFilterClause.getSql());
         sb.append(")");

         if (withTxFilterClause.hasParameters()) {
            for (Object param : withTxFilterClause.getParameters()) {
               addParameter(param);
            }
         }
         if (withTxFilterClause.hasJoins()) {
            for (AbstractJoinQuery join : withTxFilterClause.getJoins()) {
               getContext().getJoins().add(join);
            }
         }
      }
      return sb.toString();
   }

   @Override
   public String getTxBranchFilter(String txsAlias) {
      boolean allowDeleted = //
         OptionsUtil.areDeletedArtifactsIncluded(getOptions()) || //
            OptionsUtil.areDeletedAttributesIncluded(getOptions()) || //
            OptionsUtil.areDeletedRelationsIncluded(getOptions());
      return getTxBranchFilter(txsAlias, allowDeleted);
   }

   @Override
   public String getTxBranchFilter(String txsAlias, boolean allowDeleted) {
      StringBuilder sb = new StringBuilder();
      writeTxFilter(txsAlias, sb, allowDeleted);
      if (hasAlias(TableEnum.BRANCH_TABLE)) {
         String alias = getFirstAlias(TableEnum.BRANCH_TABLE);
         sb.append(" AND ");
         sb.append(txsAlias);
         sb.append(".branch_id = ");
         sb.append(alias);
         sb.append(".branch_id");
      } else if (hasAlias(TableEnum.TX_DETAILS_TABLE)) {
         String alias = getFirstAlias(TableEnum.TX_DETAILS_TABLE);
         sb.append(" AND ");
         sb.append(txsAlias);
         sb.append(".transaction_id = ");
         sb.append(alias);
         sb.append(".transaction_id");
         sb.append(" AND ");
         sb.append(txsAlias);
         sb.append(".branch_id = ");
         sb.append(alias);
         sb.append(".branch_id");
      }
      return sb.toString();
   }

   private void writeTxFilter(String txsAlias, StringBuilder sb, boolean allowDeleted) {
      if (OptionsUtil.isHistorical(getOptions())) {
         sb.append(txsAlias);
         sb.append(".transaction_id <= ?");
         addParameter(OptionsUtil.getFromTransaction(getOptions()));
         if (!allowDeleted) {
            writeAndLn();
            sb.append(txsAlias);
            sb.append(".mod_type <> ");
            sb.append(ModificationType.DELETED.getIdString());
         }
      } else {
         sb.append(txsAlias);
         sb.append(".tx_current");
         if (allowDeleted) {
            sb.append(" IN (");
            sb.append(String.valueOf(TxChange.CURRENT));
            sb.append(", ");
            sb.append(String.valueOf(TxChange.DELETED));
            sb.append(", ");
            sb.append(String.valueOf(TxChange.ARTIFACT_DELETED));
            sb.append(")");
         } else {
            sb.append(" = ");
            sb.append(String.valueOf(TxChange.CURRENT));
         }
      }
   }

   private void computeTxFilterClause(Iterable<SqlHandler<?>> handlers) {
      List<SqlHandler<?>> branchHandlers = new ArrayList<>();
      List<SqlHandler<?>> txHandlers = new ArrayList<>();
      List<SqlHandler<?>> artHandlers = new ArrayList<>();

      for (SqlHandler<?> handler : handlers) {
         if (handler.getPriority() <= SqlHandlerPriority.ALL_BRANCHES.ordinal()) {
            branchHandlers.add(handler);
         } else if (handler.getPriority() <= SqlHandlerPriority.ALL_TXS.ordinal()) {
            txHandlers.add(handler);
         } else {
            artHandlers.add(handler);
         }
      }

      if (!artHandlers.isEmpty()) {
         TableEnum table = null;
         List<SqlHandler<?>> withQueryHandlers = null;
         if (!branchHandlers.isEmpty()) {
            withQueryHandlers = branchHandlers;
            table = TableEnum.BRANCH_TABLE;
         } else if (!txHandlers.isEmpty()) {
            withQueryHandlers = txHandlers;
            table = TableEnum.TX_DETAILS_TABLE;
         }

         if (withQueryHandlers != null && table != null) {
            computeTables(withQueryHandlers);
            computeWithClause(withQueryHandlers);

            write("\n FROM \n");
            writeTables();
            write("\n WHERE \n");
            writePredicates(withQueryHandlers);
            String tbAlias = getFirstAlias(table);

            StringBuilder withBuilder = new StringBuilder();
            withBuilder.append("SELECT DISTINCT ");
            withBuilder.append(tbAlias);
            withBuilder.append(".branch_id ");
            withBuilder.append(toString());

            SqlContext context = getContext();
            String sql = withBuilder.toString().replaceAll("\\s+", " ");
            List<AbstractJoinQuery> joins = Lists.newArrayList(context.getJoins());
            List<Object> parameters = Lists.newArrayList(context.getParameters());

            withTxFilterClause = new WithClauseTxFilterData(sql, joins, parameters);
            reset();
         }
      }
   }

   private List<SqlHandler<?>> getFieldResolvers(Iterable<DynamicData> datas) {
      List<SqlHandler<?>> toReturn = new ArrayList<>();
      Set<String> created = new HashSet<>();
      for (DynamicData data : datas) {
         int level = data.getLevel();
         ObjectField objectField = getObjectField(data);

         TableEnum table = objectField.getTable();
         ObjectType type = objectField.getType();

         String key = asKey(level, table, type);
         if (!created.contains(key)) {
            SqlHandler<?> handler = newSqlHandler(level, table, type);
            if (handler != null) {
               created.add(key);
               toReturn.add(handler);
            }
         }
      }
      Collections.sort(toReturn, HANDLER_COMPARATOR);
      return toReturn;
   }

   private String asKey(int level, TableEnum table, ObjectType type) {
      return String.format("%s.%s.%s", level, table, type);
   }

   private SqlHandler<?> newSqlHandler(int level, TableEnum table, ObjectType type) {
      SqlHandler<?> handler = null;
      SqlHandlerPriority priority = getXtraSqlHandlerPriority(table, type);
      switch (table) {
         case TX_DETAILS_TABLE:
            handler = new XtraTxDataSqlHandler(priority, type);
            break;
         case BRANCH_TABLE:
            handler = new XtraBranchDataSqlHandler(priority, type);
            break;
         case ATTRIBUTE_TABLE:
            handler = new XtraAttributeDataSqlHandler();
            break;
         case RELATION_TABLE:
            handler = new XtraRelationDataSqlHandler();
            break;
         default:
            break;
      }
      if (handler != null) {
         handler.setLevel(level);
         handler.setLogger(logger);
      }
      return handler;
   }

   private SqlHandlerPriority getXtraSqlHandlerPriority(TableEnum table, ObjectType type) {
      SqlHandlerPriority priority = SqlHandlerPriority.LAST;
      if (type != null) {
         switch (type) {
            case ARTIFACT:
               priority = SqlHandlerPriority.ARTIFACT_TX_DATA_XTRA;
               break;
            case ATTRIBUTE:
               priority = SqlHandlerPriority.ATTRIBUTE_TX_DATA_XTRA;
               break;
            case RELATION:
               priority = SqlHandlerPriority.RELATION_TX_DATA_XTRA;
               break;
            case BRANCH:
               priority = SqlHandlerPriority.BRANCH_TX_DATA_XTRA;
               break;
            default:
               break;
         }
      }
      return priority;
   }

   private static final class WithClauseTxFilterData {
      private final String sql;
      private final List<AbstractJoinQuery> withJoins;
      private final List<Object> withParameters;

      public WithClauseTxFilterData(String sql, List<AbstractJoinQuery> withJoins, List<Object> withParameters) {
         super();
         this.sql = sql;
         this.withJoins = withJoins;
         this.withParameters = withParameters;
      }

      public boolean hasJoins() {
         return withJoins != null && !withJoins.isEmpty();
      }

      public boolean hasParameters() {
         return withParameters != null && !withParameters.isEmpty();
      }

      public String getSql() {
         return sql;
      }

      public List<AbstractJoinQuery> getJoins() {
         return withJoins;
      }

      public List<Object> getParameters() {
         return withParameters;
      }

   }
}
