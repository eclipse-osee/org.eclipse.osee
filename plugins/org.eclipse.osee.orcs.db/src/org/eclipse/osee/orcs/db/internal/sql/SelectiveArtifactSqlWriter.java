/*********************************************************************
 * Copyright (c) 2019 Boeing
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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.RelationTypeCriteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaFollowSearch;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaGetReferenceArtifact;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaPagination;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.db.internal.search.handlers.ChildrenFollowRelationSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.FollowRelationSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.FollowSearchSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.GetReferenceDetailsHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.PaginationSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerPriority;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Ryan D. Brooks
 */
public class SelectiveArtifactSqlWriter extends AbstractSqlWriter {
   private final List<AbstractJoinQuery> joinTables = new ArrayList<>();
   private static final SqlHandlerComparator HANDLER_COMPARATOR = new SqlHandlerComparator();
   private final AbstractSqlWriter parentWriter;
   private final List<Object> parameters = new ArrayList<>();
   private String fieldAlias;
   private String relsAlias;
   private String rels2Alias;
   private String attrSearchAlias;
   private SelectiveArtifactSqlWriter(AbstractSqlWriter parentWriter, SqlJoinFactory sqlJoinFactory, JdbcClient jdbcClient, QueryData rootQueryData) {
      super(sqlJoinFactory, jdbcClient, rootQueryData);
      this.parentWriter = parentWriter;
   }

   public SelectiveArtifactSqlWriter(SqlJoinFactory sqlJoinFactory, JdbcClient jdbcClient, QueryData rootQueryData) {
      this(null, sqlJoinFactory, jdbcClient, rootQueryData);
   }

   public SelectiveArtifactSqlWriter(AbstractSqlWriter parentWriter) {
      this(parentWriter, parentWriter.joinFactory, parentWriter.getJdbcClient(),
         new QueryData(parentWriter.rootQueryData));
   }

   @Override
   public void addParameter(Object parameter) {
      if (parentWriter == null) {
         parameters.add(parameter);
      } else {
         parentWriter.addParameter(parameter);
      }
   }

   @Override
   protected void addJoin(AbstractJoinQuery join) {
      if (parentWriter == null) {
         joinTables.add(join);
      } else {
         parentWriter.addJoin(join);
      }
   }

   public void runSql(Consumer<JdbcStatement> consumer, SqlHandlerFactory handlerFactory, int numArtifacts) {
      runSqlorFetch(consumer, handlerFactory, numArtifacts);
   }

   public int getCount(SqlHandlerFactory handlerFactory) {
      return runSqlorFetch(null, handlerFactory, 1);
   }

   private int runSqlorFetch(Consumer<JdbcStatement> consumer, SqlHandlerFactory handlerFactory, int numArtifacts) {
      try {
         build(handlerFactory);
         for (AbstractJoinQuery join : joinTables) {
            join.store();
         }
         if (rootQueryData.isCountQueryType()) {
            return getJdbcClient().fetch(-1, toSql(), parameters.toArray());
         } else {
            getJdbcClient().runQuery(consumer, numArtifacts * 20, toSql(), parameters.toArray());
         }
      } finally {
         for (AbstractJoinQuery join : joinTables) {
            try {
               join.close();
            } catch (Exception ex) {
               // Ensure we try to delete all join entries
            }
         }
         reset();
      }
      return 0;
   }

   public String toSql() {
      return output.toString();
   }

   @Override
   public Options getOptions() {
      return queryDataCursor.getOptions();
   }

   private void follow(SqlHandlerFactory handlerFactory, List<String> artWithAliases, String sourceArtTable) {
      List<SqlHandler<?>> handlers = new ArrayList<>();
      FollowRelationSqlHandler previousFollow = null;
      for (Criteria criteria : queryDataCursor.getOnlyCriteriaSet().stream().filter(
         a -> !a.isReferenceHandler()).collect(Collectors.toList())) {
         if (criteria instanceof CriteriaRelationTypeFollow) {
            FollowRelationSqlHandler handlerSlim;
            if (previousFollow == null) {
               handlerSlim = new FollowRelationSqlHandler(sourceArtTable);
            } else {
               handlerSlim = new FollowRelationSqlHandler(previousFollow);
            }
            handlerSlim.setData((CriteriaRelationTypeFollow) criteria);
            handlers.add(handlerSlim);
            previousFollow = handlerSlim;
            if (queryDataCursor.getParentQueryData() != null && this.rootQueryData.hasCriteriaType(
               CriteriaPagination.class)) {
               PaginationSqlHandler pHandler = new PaginationSqlHandler();
               pHandler.setData(new CriteriaPagination(0, 0));
               handlers.add(pHandler); //to ensure 0 rn is added in the right order via handler
            }
         } else {
            handlers.add(handlerFactory.createHandler(criteria));
         }
      }
      /*
       * If new relation type is not used in the parent Query but is used in the child query must add
       * ChildrenFollowRelationSqlHandler so that union of all artWiths will have same number of columns
       */
      if (!newRelationInCriteria(Collections.singletonList(queryDataCursor)) && newRelationInCriteria(
         queryDataCursor.getChildrenQueryData())) {
         handlers.add(new ChildrenFollowRelationSqlHandler()); //will be used to add 0 rel_type, 0 rel_order for non-new relation type artWith clauses
      }
      //sort handlers
      Collections.sort(handlers, HANDLER_COMPARATOR);
      String artWithAlias = write(handlers, "artWith");
      artWithAliases.add(artWithAlias);
      QueryData tempQueryDataCursor = queryDataCursor;
      for (QueryData childQueryData : queryDataCursor.getChildrenQueryData()) {
         queryDataCursor = childQueryData;
         follow(handlerFactory, artWithAliases, artWithAlias);
      }
      queryDataCursor = tempQueryDataCursor;
   }

   private boolean newRelationInCriteria(List<QueryData> queryDatas) {

      for (QueryData queryData : queryDatas) {
         for (Criteria criteria : queryData.getAllCriteria()) {
            if (criteria instanceof RelationTypeCriteria) {
               if (((RelationTypeCriteria) criteria).getType().isNewRelationTable()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public void build(SqlHandlerFactory handlerFactory) {
      List<String> artWithAliases = new ArrayList<>();
      /*
       * with artWith1 as (...), artWith2 as (...), ...
       */
      follow(handlerFactory, artWithAliases, null);
      String artWithAlias;

      if (artWithAliases.size() == 1) {
         artWithAlias = artWithAliases.get(0);
      } else {
         /*
          * arts as (select * from artWith1 union select * from artWith2...)
          */
         artWithAlias = startCommonTableExpression("arts");
         boolean firstAlias = true;
         for (String art : artWithAliases) {
            if (!firstAlias) {
               write(" union ");
            } else {
               firstAlias = false;
            }
            write("SELECT " + art + ".* from " + art);
         }
      }

      if (rootQueryData.isIdQueryType() || (rootQueryData.isCountQueryType() && !rootQueryData.hasCriteriaType(
         CriteriaFollowSearch.class))) {
         fieldAlias = artWithAlias;
      } else {
         String attsAlias = writeAttsCommonTableExpression(artWithAlias);
         if (rootQueryData.isAttributesOnlyQueryType() || rootQueryData.isTokenQueryType()) {
            fieldAlias = attsAlias;
         } else {
            if (OptionsUtil.getSingleLevelRelationsSearch(getOptions())) {
               String artWith1 = artWithAliases.get(0);
               writeRelsCommonTableExpression(artWith1);
               writeRelsCommonTableExpression2(artWith1);
            } else {
               writeRelsCommonTableExpression(artWithAlias);
               writeRelsCommonTableExpression2(artWithAlias);
            }

            if (rootQueryData.hasCriteriaType(CriteriaFollowSearch.class)) {
               writeFollowSearchCommonTableExpression(handlerFactory, attsAlias);
            }
            if (rootQueryData.hasCriteriaType(CriteriaGetReferenceArtifact.class)) {
               for (Criteria criteria : rootQueryData.getOnlyCriteriaSet().stream().filter(
                  a -> a.isReferenceHandler()).collect(Collectors.toList())) {
                  GetReferenceDetailsHandler handler = new GetReferenceDetailsHandler();
                  handler.setData((CriteriaGetReferenceArtifact) criteria);
                  writeReferenceClause(handler);
               }
               String refAll = startCommonTableExpression("reference_all");
               boolean first = true;
               for (String string : getAliasManager().getUsedAliases("reference")) {
                  if (first) {
                     write("select * from " + string);
                     first = false;
                  } else {
                     write(" union select * from " + string);
                  }
               }
               String refAtts1 = startCommonTableExpression("reference_atts");
               write(
                  "select " + refAll + ".art_id, " + refAll + ".art_type_id, " + refAll + ".app_id, " + refAll + ".transaction_id, " + refAll + ".mod_type, " + refAll + ".tx_current, 0 AS top,");
               if (OptionsUtil.getIncludeApplicabilityTokens(rootQueryData.getOptions())) {
                  write("' ' app_value, ");
               }
               if (this.rootQueryData.orderMechanism().contains("ATTRIBUTE")) {
                  write("' ' order_value, ");
               }
               if (rootQueryData.hasCriteriaType(CriteriaPagination.class)) {
                  write("0 rn, ");
               }
               write("att.attr_type_id AS type_id, att.value, att.uri, att.attr_id, source_art_id AS other_art_id, ");
               if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
                  write(
                     "-1 author, 'osee_comment' osee_comment,  -1 tx_type, -1 commit_art_id, -1 build_id, to_date('20010101','yyyyMMdd') time , -1 supp_tx_id, ");
               }
               write(refAll + ".source_attr_id as rel_type, 0 as rel_order,source_art_type_id as other_art_type_id \n");
               write("FROM " + refAll + ", osee_attribute att, osee_txs txs \n");
               write(
                  "WHERE " + refAll + ".art_id = att.art_id AND att.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND txs.branch_id = ? ");
               addParameter(getRootQueryData().getBranch().getId());
            }
            writeFieldsCommonTableExpression(artWithAlias, attsAlias);
         }
      }
      finishWithClause();
      if (rootQueryData.isCountQueryType()) {
         if (rootQueryData.hasCriteriaType(CriteriaFollowSearch.class)) {
            write("select count(distinct " + fieldAlias + ".art_id) from %s", fieldAlias);
         } else {
            write("SELECT count(*) FROM %s", fieldAlias);
         }
      } else {
         if (OptionsUtil.getIncludeLatestTransactionDetails(this.rootQueryData.getOptions())) {
            addParameter("'yyyyMmddHHmmss'");
            addParameter("'yyyyMmddHHmmss'");
            write(
               "SELECT " + fieldAlias + ".* , to_char(time,?) timeStr, to_char(max(time) over (partition by art_id),?) max_time FROM %s",
               fieldAlias);
         } else {
            write("SELECT * FROM %s", fieldAlias);
         }
      }
      if (rootQueryData.hasCriteriaType(CriteriaFollowSearch.class)) {
         write(", " + attrSearchAlias);
         write(" where (" + getJdbcClient().getDbType().getInStringSql(fieldAlias + ".art_path",
            "','||" + attrSearchAlias + ".art_id||','") + " > 0 or " + getJdbcClient().getDbType().getInStringSql(
               attrSearchAlias + ".art_path",
               "','||" + fieldAlias + ".art_id||','") + " > 0 or " + getJdbcClient().getDbType().getInStringSql(
                  attrSearchAlias + ".art_path", "','||" + fieldAlias + ".other_art_id||','") + " > 0 ) ");

      }
      if (rootQueryData.isCountQueryType() && rootQueryData.hasCriteriaType(CriteriaFollowSearch.class)) {
         write(" and " + fieldAlias + ".top = 1");
      }
      if (!rootQueryData.isCountQueryType()) {
         if (parentWriter == null && !rootQueryData.isSelectQueryType()) {
            write(" ORDER BY " + fieldAlias + ".art_id");
         } else if (this.rels2Alias != null) {
            write(" ORDER BY ");
            write(fieldAlias + ".top desc");
            if (this.rootQueryData.orderMechanism().equals("ATTRIBUTE") || this.rootQueryData.orderMechanism().equals(
               "RELATION AND ATTRIBUTE")) {
               addParameter(this.rootQueryData.orderByAttribute().getId());
               write(", CASE WHEN " + fieldAlias + ".type_id = ?");
               write(" THEN 0 ELSE 1 END ASC");
               addParameter(this.rootQueryData.orderByAttribute().getId());
               write(", CASE WHEN " + fieldAlias + ".type_id = ?");
               write(" THEN " + fieldAlias + ".VALUE ELSE \n");
               String orderAttrString = "'" + new String(new char[3998]).replace('\0', 'Z') + "'";
               addParameter(orderAttrString);
               write("?");
               write("\n" + "END " + (this.rootQueryData.orderByAttributeDirection().equals(
                  SortOrder.DESCENDING) ? "DESC" : "ASC"));
            }
            if (this.rootQueryData.orderMechanism().equals("RELATION") || this.rootQueryData.orderMechanism().equals(
               "RELATION AND ATTRIBUTE")) {
               if (this.output.toString().contains("top_rel_type")) {
                  write(
                     ", " + fieldAlias + ".top_rel_type, " + fieldAlias + ".top_rel_order, " + fieldAlias + ".rel_order");
               } else {
                  write(
                     ", case when " + fieldAlias + ".other_art_id = 0 then " + fieldAlias + ".other_art_id else 1 end, " + fieldAlias + ".rel_order");
               }

            }
         }
      }
   }

   private void writeFollowSearchCommonTableExpression(SqlHandlerFactory handlerFactory, String attsAlias) {

      FollowSearchSqlHandler followSearchHandler = (FollowSearchSqlHandler) handlerFactory.createHandler(
         rootQueryData.getCriteriaByType(CriteriaFollowSearch.class).get(0));

      boolean newRelationUsed = newRelationInCriteria(
         Collections.singletonList(queryDataCursor)) || newRelationInCriteria(queryDataCursor.getChildrenQueryData());
      attrSearchAlias = followSearchHandler.writeFollowSearchCommonTableExpression(this, attsAlias, newRelationUsed,
         (rootQueryData.hasCriteriaType(
            CriteriaPagination.class) ? rootQueryData.getCriteriaByType(CriteriaPagination.class).get(0) : null));
   }

   private void writeFieldsCommonTableExpression(String artWithAlias, String attsAlias) {
      fieldAlias = startCommonTableExpression("fields");
      writeSelectAndHint();
      writeSelectFields(attsAlias, "*");
      write(", 0 as rel_type, 0 as rel_order, 0 AS other_art_type_id FROM ");
      write(attsAlias);
      write("\n UNION ALL\n ");
      SelectiveArtifactSqlWriter relWriter = new SelectiveArtifactSqlWriter(this);
      relWriter.relsAlias = relsAlias;
      List<SqlHandler<?>> handlers = new ArrayList<SqlHandler<?>>();
      handlers.add(new SqlHandler<Criteria>() {
         private String artAlias;
         @Override
         public void addTables(AbstractSqlWriter writer) {
            writer.addTable(relsAlias);
            artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
         }

         @Override
         public void addPredicates(AbstractSqlWriter writer) {
            writer.writeEquals(relsAlias, "other_art_id", artAlias, "art_id");
         }

         @Override
         public int getPriority() {
            return SqlHandlerPriority.RELATED_TO_ART_IDS.ordinal();
         }
      });
      relWriter.write(handlers);
      write(relWriter.toSql());
      write("\n UNION ALL\n ");
      SelectiveArtifactSqlWriter relWriter2 = new SelectiveArtifactSqlWriter(this);
      relWriter2.rels2Alias = rels2Alias;
      List<SqlHandler<?>> handlers2 = new ArrayList<SqlHandler<?>>();
      handlers2.add(new SqlHandler<Criteria>() {
         private String artAlias;
         @Override
         public void addTables(AbstractSqlWriter writer) {
            writer.addTable(rels2Alias);
            artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
         }

         @Override
         public void addPredicates(AbstractSqlWriter writer) {
            writer.writeEquals(rels2Alias, "other_art_id", artAlias, "art_id");
         }

         @Override
         public int getPriority() {
            return SqlHandlerPriority.RELATED_TO_ART_IDS.ordinal();
         }
      });
      relWriter2.write(handlers2);
      write(relWriter2.toSql());
      String refAtts = getAliasManager().getFirstUsedAlias("reference_atts");
      if (refAtts != null) {
         write("\n union all \n");
         write("select " + refAtts + ".* from " + refAtts);
      }
   }

   private String writeAttsCommonTableExpression(String artWithAlias) {
      String attsAlias = startCommonTableExpression("atts");
      String attAlias = "att";
      String attTxsAlias = "txs";
      String txdAlias = "txd";
      writeUseNlTableHint(attAlias + " " + attTxsAlias);
      writeSelectFields(artWithAlias, "*", attAlias, "attr_type_id AS type_id", attAlias, "value", attAlias, "uri",
         attAlias, "attr_id");
      write(", 0 AS other_art_id");
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         writeSelectFields(txdAlias, "author", txdAlias, "osee_comment", txdAlias, "tx_type", txdAlias, "commit_art_id",
            txdAlias, "build_id");
         write("," + txdAlias + ".time ");
         write(", " + txdAlias + ".transaction_id supp_tx_id ");
      }
      write("\n FROM %s, osee_attribute att, osee_txs txs", artWithAlias);
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         write(", osee_tx_details txd");
      }
      write("\n WHERE ");
      writeEqualsAnd(artWithAlias, attAlias, "art_id");
      AttributeTypeId attributeType = rootQueryData.getAttributeType();
      if (attributeType.isValid()) {
         writeEqualsParameterAnd(attAlias, "attr_type_id", attributeType);
      }
      writeEqualsAnd(attAlias, attTxsAlias, "gamma_id");
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         writeEqualsAnd(attTxsAlias, txdAlias, "transaction_id");
      }
      writeTxBranchFilter(attTxsAlias);

      return attsAlias;
   }

   private void writeRelsCommonTableExpression(String artWithAlias) {
      relsAlias = startCommonTableExpression("rels");
      String relAlias = "rel";
      String relTxsAlias = "txs";
      String txdAlias = "txd";
      writeUseNlTableHint(relAlias + " " + relTxsAlias);
      writeSelectFields(artWithAlias, "*", relAlias, "rel_link_type_id AS type_id");
      write(
         ", CASE art_id WHEN a_art_id THEN 'B' ELSE 'A' END AS value, '' AS spare1, 0 AS spare2, CASE art_id WHEN a_art_id THEN b_art_id ELSE a_art_id END AS other_art_id ");
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         writeSelectFields(txdAlias, "author", txdAlias, "osee_comment", txdAlias, "tx_type", txdAlias, "commit_art_id",
            txdAlias, "build_id");
         write("," + txdAlias + ".time ");
         write(", " + txdAlias + ".transaction_id as supp_tx_id ");
      }
      write(",0 as rel_type, 0 as rel_order");
      write("\n FROM %s, osee_relation_link rel, osee_txs txs", artWithAlias);
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         write(", osee_tx_details txd");
      }
      write("\n WHERE ");
      write(artWithAlias);
      write(".art_id IN (a_art_id, b_art_id)");
      writeAnd();
      writeEqualsAnd(relAlias, relTxsAlias, "gamma_id");
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         writeEqualsAnd(relTxsAlias, txdAlias, "transaction_id");
      }
      writeTxBranchFilter(relTxsAlias);
   }

   private void writeRelsCommonTableExpression2(String artWithAlias) {
      rels2Alias = startCommonTableExpression("rels");

      String txdAlias = "txd";
      String relAlias = "rel";
      String relTxsAlias = "txs";
      writeUseNlTableHint(relAlias + " " + relTxsAlias);
      writeSelectFields(artWithAlias, "*", relAlias, "rel_type AS type_id");
      write(
         ", CASE art_id WHEN a_art_id THEN 'B' ELSE 'A' END AS value, '' AS spare1, 0 AS spare2, CASE art_id WHEN a_art_id THEN b_art_id ELSE a_art_id END AS other_art_id ");
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         writeSelectFields(txdAlias, "author", txdAlias, "osee_comment", txdAlias, "tx_type", txdAlias, "commit_art_id",
            txdAlias, "build_id");
         write("," + txdAlias + ".time ");
         write(", " + txdAlias + ".transaction_id supp_tx_id ");
      }
      write(", rel_type, rel_order");
      write("\n FROM %s, osee_relation rel, osee_txs txs", artWithAlias);
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         write(", osee_tx_details txd");
      }
      write("\n WHERE ");
      write(artWithAlias);
      write(".art_id IN (a_art_id, b_art_id)");
      writeAnd();
      writeEqualsAnd(relAlias, relTxsAlias, "gamma_id");
      if (OptionsUtil.getIncludeLatestTransactionDetails(rootQueryData.getOptions())) {
         writeEqualsAnd(relTxsAlias, txdAlias, "transaction_id");
      }
      writeTxBranchFilter(relTxsAlias);

   }

   @Override
   protected void writeSelectFields() {
      String artAlias = getMainTableAlias(OseeDb.ARTIFACT_TABLE);
      String txAlias = getMainTableAlias(OseeDb.TXS_TABLE);
      if (relsAlias == null && rels2Alias == null) {
         writeSelectFields(artAlias, "art_id", artAlias, "art_type_id", txAlias, "app_id", txAlias, "transaction_id",
            txAlias, "mod_type", txAlias, "tx_current");
         if (OptionsUtil.getIncludeApplicabilityTokens(rootQueryData.getOptions())) {
            writeSelectFields(getMainTableAlias(OseeDb.OSEE_KEY_VALUE_TABLE), "value app_value");
         }
         write(", ");
         write(queryDataCursor.getParentQueryData() == null ? "1" : "0");
         write(" AS top");

      } else if (relsAlias != null) {
         writeSelectFields(relsAlias, "*", artAlias, "art_type_id");
         write(" AS other_art_type_id");

      } else if (rels2Alias != null) {
         writeSelectFields(rels2Alias, "*", artAlias, "art_type_id");
         write(" AS other_art_type_id");
      }
   }

   @Override
   protected void writeSelect(Iterable<SqlHandler<?>> handlers) {
      String multiHint = this.getMultiTableHintParameter();
      if (multiHint.split(" ").length > 1) {
         writeUseNlTableHint(multiHint);
      } else {
         writeSelectAndHint();
      }
      if (rootQueryData.isCountQueryType() && !rootQueryData.hasCriteriaType(CriteriaFollowSearch.class)) {
         writeSelectFields(getMainTableAlias(OseeDb.ARTIFACT_TABLE), "art_id");
      } else {
         writeSelectFields();
         for (SqlHandler<?> handler : handlers) {
            handler.writeSelectFields(this);
         }
      }
   }

   @Override
   public void writeGroupAndOrder(Iterable<SqlHandler<?>> handlers) {
      // only add ordering on the outer query in build()
   }

   @Override
   protected void reset() {
      super.reset();
      parameters.clear();
      joinTables.clear();
      rootQueryData.reset();
   }

   @Override
   public String toString() {
      if (parentWriter == null) {
         StringBuilder strB = new StringBuilder();
         String[] tokens = output.toString().split("\\?");
         for (int i = 0; i < tokens.length; i++) {
            strB.append(tokens[i]);
            if (i < parameters.size()) {
               Object parameter = parameters.get(i);
               if (parameter instanceof Id) {
                  strB.append(((Id) parameter).getIdString());
               } else {
                  strB.append(parameter);
               }
            } else if (i < tokens.length - 1) {
               strB.append("?");
            }
         }
         return strB.toString();
      } else {
         return toSql();
      }
   }
}