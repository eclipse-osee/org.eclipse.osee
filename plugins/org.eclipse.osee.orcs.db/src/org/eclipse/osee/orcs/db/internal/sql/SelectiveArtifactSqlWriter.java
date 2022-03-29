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
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreBranchCategoryTokens;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.db.internal.search.handlers.FollowRelationSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerPriority;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Ryan D. Brooks
 */
public class SelectiveArtifactSqlWriter extends AbstractSqlWriter {
   private final List<AbstractJoinQuery> joinTables = new ArrayList<>();
   private final AbstractSqlWriter parentWriter;
   private final List<Object> parameters = new ArrayList<>();
   private String fieldAlias;
   private String relsAlias;
   private String rels2Alias;

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
      List<SqlHandler<?>> handlers = new ArrayList<>(queryDataCursor.getOnlyCriteriaSet().size());

      FollowRelationSqlHandler previousFollow = null;
      for (Criteria criteria : queryDataCursor.getOnlyCriteriaSet()) {
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
         } else {
            handlers.add(handlerFactory.createHandler(criteria));
         }
      }

      String artWithAlias = write(handlers, "artWith");
      artWithAliases.add(artWithAlias);

      QueryData tempQueryDataCursor = queryDataCursor;
      for (QueryData childQueryData : queryDataCursor.getChildrenQueryData()) {
         queryDataCursor = childQueryData;
         follow(handlerFactory, artWithAliases, artWithAlias);
      }
      queryDataCursor = tempQueryDataCursor;
   }

   public void build(SqlHandlerFactory handlerFactory) {
      List<String> artWithAliases = new ArrayList<>();
      follow(handlerFactory, artWithAliases, null);

      String artWithAlias;
      if (artWithAliases.size() == 1) {
         artWithAlias = artWithAliases.get(0);
      } else {
         artWithAlias = startCommonTableExpression("arts");
         boolean firstAlias = true;
         if (!rootQueryData.getBranchCategories().isEmpty() && rootQueryData.getBranchCategories().contains(
            CoreBranchCategoryTokens.MIM)) {
            for (String art : artWithAliases) {
               if (!firstAlias) {
                  write(" union ");
               } else {
                  firstAlias = false;
               }
               //for each alias see if osee_relation is within its () and if so do not add top_rel_type and top_rel_order
               //otherwise add
               if (this.output.toString().contains("osee_relation rel")) {
                  Pattern pattern = Pattern.compile(art + "\\sAS\\s\\((.*?)\\)", Pattern.DOTALL);
                  Matcher regexMatcher = pattern.matcher(this.output.toString());
                  if (regexMatcher.find()) {//Finds Matching Pattern in String
                     if (regexMatcher.group(1).contains("osee_relation rel")) {
                        write("SELECT " + art + ".* from " + art);
                     } else {
                        write("SELECT " + art + ".*, 0 as rel_top_type, 0 as rel_top_order from " + art);
                     }
                  }
               } else {
                  write("SELECT " + art + ".* from " + art);
               }
            }
         } else {
            write(Collections.toString(artWithAliases, "SELECT * FROM ", " UNION SELECT * FROM ", null));
         }
      }

      if (rootQueryData.isIdQueryType() || rootQueryData.isCountQueryType()) {
         fieldAlias = artWithAlias;
      } else {
         String attsAlias = writeAttsCommonTableExpression(artWithAlias);

         if (rootQueryData.isAttributesOnlyQueryType() || rootQueryData.isTokenQueryType()) {
            fieldAlias = attsAlias;
         } else {
            writeRelsCommonTableExpression(artWithAlias);

            if (!rootQueryData.getBranchCategories().isEmpty() && rootQueryData.getBranchCategories().contains(
               CoreBranchCategoryTokens.MIM)) {
               writeRelsCommonTableExpression2(artWithAlias);
            }
            writeFieldsCommonTableExpression(artWithAlias, attsAlias);
         }
      }

      finishWithClause();

      if (rootQueryData.isCountQueryType()) {
         write("SELECT count(*) FROM %s", fieldAlias);
      } else {
         write("SELECT * FROM %s", fieldAlias);
      }

      if (parentWriter == null && !rootQueryData.isCountQueryType() && !rootQueryData.isSelectQueryType()) {
         write(" ORDER BY art_id");

      } else if (this.rels2Alias != null) {
         if (this.rootQueryData.hasCriteriaType(CriteriaRelatedTo.class)) {
            List<CriteriaRelatedTo> crt = this.rootQueryData.getCriteriaByType(CriteriaRelatedTo.class);
            if (crt.stream().anyMatch(a -> a.getType().isNewRelationTable())) {
               write(" ORDER BY top_rel_type, top_rel_order, rel_order");
            } else {
               write(" ORDER BY rel_order");
            }
         } else {
            write(" ORDER BY rel_order");
         }
      }
   }

   private void writeFieldsCommonTableExpression(String artWithAlias, String attsAlias) {
      fieldAlias = startCommonTableExpression("fields");

      writeSelectAndHint();
      writeSelectFields(attsAlias, "*");
      if (!rootQueryData.getBranchCategories().isEmpty() && rootQueryData.getBranchCategories().contains(
         CoreBranchCategoryTokens.MIM)) {
         write(", 0 as rel_type, 0 as rel_order, 0 AS other_art_type_id FROM ");
      } else {
         write(", 0 AS other_art_type_id FROM ");
      }
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

      if (!rootQueryData.getBranchCategories().isEmpty() && rootQueryData.getBranchCategories().contains(
         CoreBranchCategoryTokens.MIM)) {
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
      }
   }

   private String writeAttsCommonTableExpression(String artWithAlias) {
      String attsAlias = startCommonTableExpression("atts");
      String attAlias = "att";
      String attTxsAlias = "txs";
      writeUseNlTableHint(attAlias + " " + attTxsAlias);
      writeSelectFields(artWithAlias, "*", attAlias, "attr_type_id AS type_id", attAlias, "value", attAlias, "uri",
         attAlias, "attr_id");
      write(", 0 AS other_art_id");
      write("\n FROM %s, osee_attribute att, osee_txs txs", artWithAlias);
      write("\n WHERE ");
      writeEqualsAnd(artWithAlias, attAlias, "art_id");

      AttributeTypeId attributeType = rootQueryData.getAttributeType();
      if (attributeType.isValid()) {
         writeEqualsParameterAnd(attAlias, "attr_type_id", attributeType);
      }
      writeEqualsAnd(attAlias, attTxsAlias, "gamma_id");
      writeTxBranchFilter(attTxsAlias);
      return attsAlias;
   }

   private void writeRelsCommonTableExpression(String artWithAlias) {
      relsAlias = startCommonTableExpression("rels");
      String relAlias = "rel";
      String relTxsAlias = "txs";
      writeUseNlTableHint(relAlias + " " + relTxsAlias);
      writeSelectFields(artWithAlias, "*", relAlias, "rel_link_type_id AS type_id");
      if (!rootQueryData.getBranchCategories().isEmpty() && rootQueryData.getBranchCategories().contains(
         CoreBranchCategoryTokens.MIM)) {
         write(
            ", CASE art_id WHEN a_art_id THEN 'B' ELSE 'A' END AS value, '' AS spare1, 0 AS spare2, CASE art_id WHEN a_art_id THEN b_art_id ELSE a_art_id END AS other_art_id, 0 as rel_type, 0 as rel_order");
      } else {
         write(
            ", CASE art_id WHEN a_art_id THEN 'B' ELSE 'A' END AS value, '' AS spare1, 0 AS spare2, CASE art_id WHEN a_art_id THEN b_art_id ELSE a_art_id END AS other_art_id");
      }
      write("\n FROM %s, osee_relation_link rel, osee_txs txs", artWithAlias);
      write("\n WHERE ");
      write(artWithAlias);
      write(".art_id IN (a_art_id, b_art_id) AND ");
      writeEqualsAnd(relAlias, relTxsAlias, "gamma_id");
      writeTxBranchFilter(relTxsAlias);
   }

   private void writeRelsCommonTableExpression2(String artWithAlias) {
      rels2Alias = startCommonTableExpression("rels");
      String relAlias = "rel";
      String relTxsAlias = "txs";
      writeUseNlTableHint(relAlias + " " + relTxsAlias);
      writeSelectFields(artWithAlias, "*", relAlias, "rel_type AS type_id");
      write(
         ", CASE art_id WHEN a_art_id THEN 'B' ELSE 'A' END AS value, '' AS spare1, 0 AS spare2, CASE art_id WHEN a_art_id THEN b_art_id ELSE a_art_id END AS other_art_id, rel_type, rel_order");
      write("\n FROM %s, osee_relation rel, osee_txs txs", artWithAlias);
      write("\n WHERE ");
      write(artWithAlias);
      write(".art_id IN (a_art_id, b_art_id) AND ");
      writeEqualsAnd(relAlias, relTxsAlias, "gamma_id");
      writeTxBranchFilter(relTxsAlias);
   }

   @Override
   protected void writeSelectFields() {
      String artAlias = getMainTableAlias(OseeDb.ARTIFACT_TABLE);
      String txAlias = getMainTableAlias(OseeDb.TXS_TABLE);

      if (relsAlias == null && rels2Alias == null) {

         writeSelectFields(artAlias, "art_id", artAlias, "art_type_id", txAlias, "app_id", txAlias, "transaction_id",
            txAlias, "mod_type");
         write(", ");
         write(queryDataCursor.getParentQueryData() == null ? "1" : "0");
         write(" AS top");
         if (getTableEntries().stream().anyMatch(
            a -> a.contains(OseeDb.RELATION_TABLE2.getName().toLowerCase() + " "))) {
            write(", rel_type as top_rel_type, rel_order as top_rel_order");
         }
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
      if (rootQueryData.isCountQueryType()) {
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