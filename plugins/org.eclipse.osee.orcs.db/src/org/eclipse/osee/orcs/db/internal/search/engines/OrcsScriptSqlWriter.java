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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerPriority;
import org.eclipse.osee.orcs.db.internal.search.handlers.XtraAttributeDataSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.XtraBranchDataSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.XtraRelationDataSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.XtraTxDataSqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.ObjectField;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerComparator;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * Used to create ORCS Script queries
 *
 * @author Roberto E. Escobar
 */
public class OrcsScriptSqlWriter extends AbstractSqlWriter {
   private final HashMap<TableEnum, String> mainAliases = new HashMap<>();

   private static final SqlHandlerComparator HANDLER_COMPARATOR = new SqlHandlerComparator();
   private final SqlFieldResolver fieldResolver;

   public OrcsScriptSqlWriter(SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryData queryData) {
      super(joinFactory, jdbcClient, context, queryData);
      this.fieldResolver = new SqlFieldResolver(getAliasManager(), queryData.getSelectSets());
   }

   @Override
   public void build(List<SqlHandler<?>> handlers) {
      super.build(handlers);
      getContext().setObjectDescription(fieldResolver.getResult());
   }

   @Override
   protected void write(Iterable<SqlHandler<?>> handlers) {
      fieldResolver.reset();
      computeTxFilterClause(handlers);
      computeTables(handlers);

      fieldResolver.resolve();

      writeWithClause(handlers);

      List<SqlHandler<?>> xtraHandlers;
      if (fieldResolver.hasUnresolvedFields()) {
         xtraHandlers = getFieldResolvers(fieldResolver.getUnresolved());

         computeTables(xtraHandlers);
         fieldResolver.resolve();
      } else {
         xtraHandlers = Collections.emptyList();
      }

      writeSelect(handlers);
      write("\n FROM \n");
      writeTables();

      write("\n WHERE ");
      writePredicates(Iterables.concat(handlers, xtraHandlers));

      removeDanglingSeparator("\n WHERE \n");

      writeGroupAndOrder();
   }

   @Override
   protected void writeSelectFields() {
      writeCommaIfNotFirst();
      MutableBoolean isFirst = new MutableBoolean(true);
      for (DynamicData data : fieldResolver.getResult().getDynamicData()) {
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
      if (!rootQueryData.isCountQueryType()) {
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
      }
   }

   @Override
   public void writeTxBranchFilter(String txsAlias, boolean allowDeleted) {
      writeTxFilter(txsAlias, allowDeleted);
      if (hasAlias(TableEnum.BRANCH_TABLE)) {
         String alias = getFirstAlias(TableEnum.BRANCH_TABLE);
         write(" AND ");
         write(txsAlias);
         write(".branch_id = ");
         write(alias);
         write(".branch_id");
      } else if (hasAlias(TableEnum.TX_DETAILS_TABLE)) {
         String alias = getFirstAlias(TableEnum.TX_DETAILS_TABLE);
         write(" AND ");
         write(txsAlias);
         write(".transaction_id = ");
         write(alias);
         write(".transaction_id");
         write(" AND ");
         write(txsAlias);
         write(".branch_id = ");
         write(alias);
         write(".branch_id");
      }
   }

   private void computeTxFilterClause(Iterable<SqlHandler<?>> handlers) {
      List<SqlHandler<?>> branchHandlers = new ArrayList<>();
      List<SqlHandler<?>> txHandlers = new ArrayList<>();
      List<SqlHandler<?>> artHandlers = new ArrayList<>();

      for (SqlHandler<?> handler : handlers) {
         if (handler.getPriority() <= SqlHandlerPriority.ALL_BRANCHES.ordinal()) {
            branchHandlers.add(handler);
         } else if (handler.getPriority() <= SqlHandlerPriority.TX_LAST.ordinal()) {
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
            writeWithClause(withQueryHandlers);

            write("\n FROM \n");
            writeTables();
            write("\n WHERE ");
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
            reset();
            mainAliases.clear();
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

   @Override
   public String getMainTableAlias(TableEnum table) {
      String alias = mainAliases.get(table);
      if (alias == null) {
         alias = addTable(table);
         mainAliases.put(table, alias);
      }
      return alias;
   }

   @Override
   protected boolean mainTableAliasExists(TableEnum table) {
      return mainAliases.containsKey(table);
   }
}