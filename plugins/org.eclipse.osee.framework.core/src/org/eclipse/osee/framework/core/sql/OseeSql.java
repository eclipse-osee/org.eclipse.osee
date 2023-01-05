/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.sql;

import java.util.Properties;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;

/**
 * @author Ryan D. Brooks
 */
public enum OseeSql {

   TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS("SELECT txs.transaction_id, txs.gamma_id, txs.app_id FROM osee_artifact art, osee_txs txs WHERE art.art_id = ? AND art.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES("SELECT txs.transaction_id, txs.gamma_id, txs.app_id FROM osee_attribute atr, osee_txs txs WHERE atr.attr_id = ? AND atr.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS("SELECT txs.transaction_id, txs.gamma_id, txs.app_id FROM osee_relation_link rel, osee_txs txs WHERE rel.rel_link_id = ? AND rel.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS2("SELECT txs.transaction_id, txs.gamma_id, txs.app_id FROM osee_relation rel, osee_txs txs WHERE rel.gamma_id = txs.gamma_id AND txs.branch_id = ? AND rel.a_art_id = ? AND rel.b_art_id = ? AND rel.rel_type = ? AND txs.tx_current <> " + TxCurrent.NOT_CURRENT),

   MERGE_GET_ARTIFACTS_FOR_BRANCH("SELECT art.art_id FROM osee_txs txs, osee_artifact art WHERE txs.branch_id = ? and txs.gamma_id = art.gamma_id"),
   MERGE_GET_ATTRIBUTES_FOR_BRANCH("SELECT atr.art_id, atr.attr_id FROM osee_txs txs, osee_attribute atr WHERE txs.branch_id = ? and txs.gamma_id = atr.gamma_id"),
   MERGE_GET_RELATIONS_FOR_BRANCH("SELECT rel.a_art_id, rel.b_art_id FROM osee_txs txs, osee_relation_link rel WHERE txs.branch_id = ? and txs.gamma_id = rel.gamma_id"),
   MERGE_GET_RELATIONS_FOR_BRANCH2("SELECT rel.a_art_id, rel.b_art_id FROM osee_txs txs, osee_relation rel WHERE txs.branch_id = ? and txs.gamma_id = rel.gamma_id"),
   CONFLICT_GET_ARTIFACTS_DEST("SELECT%s art2.art_type_id, art1.art_id, txs1.mod_type AS source_mod_type, txs1.gamma_id AS source_gamma, txs2.mod_type AS dest_mod_type, txs2.gamma_id AS dest_gamma FROM osee_txs txs1, osee_attribute art1, osee_artifact art2, osee_txs txs2 WHERE txs1.branch_id = ? AND txs1.transaction_id <> ? AND txs1.tx_current in (1,2) AND txs1.gamma_id = art1.gamma_id AND art1.art_id = art2.art_id AND art2.gamma_id = txs2.gamma_id AND txs2.branch_id = ? AND ((txs2.tx_current = 1 AND txs2.gamma_id not in (SELECT txs.gamma_id FROM osee_txs txs WHERE branch_id = ? AND txs.transaction_id = ?)) OR (txs2.tx_current = 2 and txs2.transaction_id > ?)) ORDER BY art2.art_id", Strings.HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT),
   CONFLICT_GET_ARTIFACTS_SRC("SELECT%s art1.art_type_id, art1.art_id, txs1.mod_type AS source_mod_type, txs1.gamma_id AS source_gamma, txs2.mod_type AS dest_mod_type, txs2.gamma_id AS dest_gamma FROM osee_txs txs1, osee_artifact art1, osee_attribute art2, osee_txs txs2 WHERE txs1.branch_id = ? AND txs1.transaction_id <> ? AND txs1.tx_current in (1,2) AND txs1.gamma_id = art1.gamma_id AND art1.art_id = art2.art_id AND art2.gamma_id = txs2.gamma_id AND txs2.branch_id = ? AND ((txs2.tx_current = 1 AND txs2.gamma_id not in (SELECT txs.gamma_id FROM osee_txs txs WHERE txs.branch_id = ? AND txs.transaction_id = ?)) OR (txs2.tx_current = 2 and txs2.transaction_id > ?)) ORDER BY art1.art_id", Strings.HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT),
   CONFLICT_GET_ATTRIBUTES("SELECT%s atr1.art_id, txs1.mod_type, atr1.attr_type_id, atr1.attr_id, atr1.gamma_id AS source_gamma, atr1.value AS source_value, atr2.gamma_id AS dest_gamma, atr2.value as dest_value, txs2.mod_type AS dest_mod_type FROM osee_txs txs1, osee_attribute atr1, osee_attribute atr2, osee_txs txs2 WHERE txs1.branch_id = ? AND txs1.transaction_id <> ? AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.attr_id = atr2.attr_id AND atr2.gamma_id = txs2.gamma_id AND txs2.branch_id = ? AND txs2.tx_current in (1,2) AND NOT EXISTS (SELECT 1 FROM osee_txs txs WHERE txs.branch_id = ? AND txs.transaction_id = ? AND ((txs1.gamma_id = txs.gamma_id and txs1.mod_type = txs.mod_type) OR (txs2.gamma_id = txs.gamma_id and txs2.mod_type = txs.mod_type))) ORDER BY attr_id", Strings.HINTS__ORDERED__INDEX__ATTRIBUTE_CONFLICT),
   CONFLICT_GET_HISTORICAL_ATTRIBUTES("SELECT%s atr.attr_id, atr.art_id, source_gamma_id, dest_gamma_id, attr_type_id, mer.merge_branch_id, mer.dest_branch_id, value as source_value, status, mer.source_branch_id FROM osee_merge mer, osee_conflict con, osee_attribute atr Where mer.commit_transaction_id = ? AND mer.merge_branch_id = con.merge_branch_id And con.source_gamma_id = atr.gamma_id AND con.status in (" + ConflictStatus.COMMITTED.getValue() + ", " + ConflictStatus.INFORMATIONAL.getValue() + " ) order by attr_id", Strings.HintsOrdered),

   LOAD_HISTORICAL_ARTIFACTS("SELECT%s aj.id2, txs.branch_id, txs.gamma_id, txs.mod_type, art_type_id, guid, aj.id4, aj.id3 AS stripe_transaction_id, txs.app_id FROM osee_join_id4 aj, osee_artifact art, osee_txs txs WHERE aj.query_id = ? AND aj.id2 = art.art_id AND art.gamma_id = txs.gamma_id AND txs.transaction_id <= aj.id3 AND txs.branch_id = aj.id1 order by aj.id1, art.art_id, txs.transaction_id desc", Strings.HintsOrdered),
   LOAD_HISTORICAL_ARCHIVED_ARTIFACTS("SELECT%s aj.id2, txs.branch_id, txs.gamma_id, txs.mod_type, art_type_id, guid, aj.id4, aj.id3 AS stripe_transaction_id, txs.app_id FROM osee_join_id4 aj, osee_artifact art, osee_txs_archived txs WHERE aj.query_id = ? AND aj.id2 = art.art_id AND art.gamma_id = txs.gamma_id AND txs.transaction_id <= aj.id3 AND txs.branch_id = aj.id1 order by aj.id1, art.art_id, txs.transaction_id desc", Strings.HintsOrdered),

   LOAD_HISTORICAL_ATTRIBUTES("SELECT att.art_id, att.attr_id, att.value, att.gamma_id, att.attr_type_id, att.uri, aj.id1, txs.mod_type, aj.id4, aj.id3 as stripe_transaction_id, txs.app_id, txd.* FROM osee_join_id4 aj, osee_attribute att, osee_txs txs, osee_tx_details txd WHERE aj.query_id = ? AND aj.id2 = att.art_id AND att.gamma_id = txs.gamma_id AND txs.branch_id = aj.id1 AND txs.transaction_id <= aj.id3 AND txs.branch_id = txd.branch_id AND txs.transaction_id = txd.transaction_id ORDER BY txs.branch_id, att.art_id, att.attr_id, txs.transaction_id desc"),
   LOAD_HISTORICAL_ARCHIVED_ATTRIBUTES("SELECT att.art_id, att.attr_id, att.value, att.gamma_id, att.attr_type_id, att.uri, aj.id1, txs.mod_type, aj.id4, aj.id3 as stripe_transaction_id, txs.app_id, txd.* FROM osee_join_id4 aj, osee_attribute att, osee_txs_archived txs, osee_tx_details txd WHERE aj.query_id = ? AND aj.id2 = att.art_id AND att.gamma_id = txs.gamma_id AND txs.branch_id = aj.id1 AND txs.transaction_id <= aj.id3 AND txs.branch_id = txd.branch_id AND txs.transaction_id = txd.transaction_id order by txs.branch_id, att.art_id, att.attr_id, txs.transaction_id desc"),

   LOAD_CURRENT_ATTRIBUTES(Strings.SELECT_CURRENT_ATTRIBUTES_PREFIX + "= 1 order by al1.id2, al1.id1, att1.attr_id, txs.transaction_id desc", Strings.HintsOrdered),
   LOAD_CURRENT_ATTRIBUTES_WITH_DELETED(Strings.SELECT_CURRENT_ATTRIBUTES_PREFIX + "IN (1, 3) order by al1.id2, al1.id1, att1.attr_id, txs.transaction_id desc", Strings.HintsOrdered),
   LOAD_CURRENT_ARCHIVED_ATTRIBUTES(Strings.SELECT_CURRENT_ARCHIVED_ATTRIBUTES_PREFIX + "= 1 order by al1.id2, al1.id1, att1.attr_id, txs.transaction_id desc", Strings.HintsOrdered),
   LOAD_CURRENT_ARCHIVED_ATTRIBUTES_WITH_DELETED(Strings.SELECT_CURRENT_ARCHIVED_ATTRIBUTES_PREFIX + "IN (1, 3) order by al1.id2, al1.id1, att1.attr_id, txs.transaction_id desc", Strings.HintsOrdered),

   LOAD_RELATIONS("SELECT%s txs.mod_type, rel_link_id, a_art_id, b_art_id, rel_link_type_id, rel.gamma_id, rationale, txs.branch_id, aj.id4, txs.app_id FROM osee_join_id4 aj, osee_relation_link rel, osee_txs txs WHERE aj.query_id = ? AND (aj.id2 = rel.a_art_id OR aj.id2 = rel.b_art_id) AND rel.gamma_id = txs.gamma_id AND txs.tx_current = " + TxCurrent.CURRENT + " AND aj.id1 = txs.branch_id", Strings.HintsOrdered),
   LOAD_RELATIONS2("SELECT%s txs.mod_type,  a_art_id, b_art_id, rel_type, rel_order, rel_art_id, rel.gamma_id, txs.branch_id, aj.id4, txs.app_id FROM osee_join_id4 aj, osee_relation rel, osee_txs txs WHERE aj.query_id = ? AND (aj.id2 = rel.a_art_id OR aj.id2 = rel.b_art_id) AND rel.gamma_id = txs.gamma_id AND txs.tx_current = " + TxCurrent.CURRENT + " AND aj.id1 = txs.branch_id order by rel_type,a_art_id,rel_order", Strings.HintsOrdered),

   LOAD_CURRENT_ARTIFACTS(Strings.SELECT_CURRENT_ARTIFACTS_PREFIX + "= 1", Strings.HintsOrdered),
   LOAD_CURRENT_ARTIFACTS_WITH_DELETED(Strings.SELECT_CURRENT_ARTIFACTS_PREFIX + "in (1, 2)", Strings.HintsOrdered),
   LOAD_CURRENT_ARCHIVED_ARTIFACTS(Strings.SELECT_CURRENT_ARCHIVED_ARTIFACTS_PREFIX + "= 1", Strings.HintsOrdered),
   LOAD_CURRENT_ARCHIVED_ARTIFACTS_WITH_DELETED(Strings.SELECT_CURRENT_ARCHIVED_ARTIFACTS_PREFIX + "in (1, 2)", Strings.HintsOrdered),

   LOAD_EXCLUDED_ARTIFACT_IDS("select art_id from osee_artifact art, osee_txs txs where art.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.tx_current = 1 and not exists (select null from osee_tuple2 t2, osee_txs txsP where tuple_type = 2 and e1 = ? and t2.gamma_id = txsP.gamma_id and txsP.branch_id = ? and txsP.tx_current = 1 and e2 = txs.app_id)"),

   CHANGE_BRANCH_ATTRIBUTE_WAS("SELECT%s attxs1.attr_id, attxs1.value as was_value, txs1.mod_type, attxs1.uri FROM osee_join_id4 ja1, osee_attribute attxs1, osee_txs txs1, WHERE txs1.branch_id = ? AND txs1.tx_type = 1 AND attxs1.gamma_id = txs1.gamma_id AND attxs1.art_id = ja1.id2 AND txs1.branch_id = ja1.id1 AND ja1.query_id = ?", Strings.HintsOrdered),
   CHANGE_TX_ATTRIBUTE_WAS("SELECT%s att1.attr_id, att1.value as was_value, txs1.mod_type , att1.uri FROM osee_join_id4 al1, osee_attribute att1, osee_txs txs1 WHERE al1.id2 = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id < ? AND al1.query_id = ? AND txs1.branch_id = al1.id1 order by txs1.branch_id, att1.art_id, att1.attr_id, txs1.transaction_id desc", Strings.HintsOrdered),
   CHANGE_BRANCH_ATTRIBUTE_IS("SELECT%s art1.art_type_id, attr1.art_id, attr1.attr_id, attr1.gamma_id, attr1.attr_type_id, attr1.value as is_value, txs1.mod_type, attr1.uri FROM osee_txs txs1, osee_attribute attr1, osee_artifact art1 WHERE txs1.branch_id = ? AND txs1.transaction_id <> = ? AND txs1.tx_current in (" + TxCurrent.DELETED + ", " + TxCurrent.CURRENT + ", " + TxCurrent.ARTIFACT_DELETED + ") AND art1.art_id = attr1.art_id AND attr1.gamma_id = txs1.gamma_id", Strings.HintsOrdered),
   CHANGE_TX_ATTRIBUTE_IS("SELECT art.art_type_id, att.art_id, att.attr_id, att.gamma_id, att.attr_type_id, att.value as is_value, att.uri, txs_att.mod_type FROM osee_txs txs_art, osee_txs txs_att, osee_attribute att, osee_artifact art WHERE txs_art.branch_id = ? and txs_art.tx_current in (1,2) AND txs_art.gamma_id = art.gamma_id AND txs_att.branch_id = txs_art.branch_id and txs_att.transaction_id = ? AND txs_att.gamma_id = att.gamma_id AND att.art_id = art.art_id"),
   CHANGE_TX_ATTRIBUTE_IS_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_ATTRIBUTE_IS.sql + " and att.art_id =?"),

   CHANGE_BRANCH_RELATION("SELECT%s txs1.mod_type, rel1.gamma_id, rel1.b_art_id, rel1.a_art_id, rel1.rationale, rel1.rel_link_id, rel1.rel_link_type_id, art.art_type_id from osee_txs txs1, osee_relation_link rel1, osee_artifact art where txs1.branch_id = ? AND txs1.transaction_id <> ? AND txs1.tx_current in (" + TxCurrent.DELETED + ", " + TxCurrent.CURRENT + ", " + TxCurrent.ARTIFACT_DELETED + ") AND txs1.gamma_id = rel1.gamma_id AND rel1.a_art_id = art.art_id", Strings.HintsOrdered),
   CHANGE_TX_RELATION("SELECT txs.mod_type, rel.gamma_id, rel.b_art_id, rel.a_art_id, rel.rationale, rel.rel_link_id, rel.rel_link_type_id, art.art_type_id from osee_txs txs, osee_relation_link rel, osee_artifact art where txs.branch_id = ? AND txs.transaction_id = ? AND txs.gamma_id = rel.gamma_id AND rel.a_art_id = art.art_id"),
   CHANGE_TX_RELATION_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_RELATION.sql + " and (rel.a_art_id = ? or rel.b_art_id = ?)"),

   CHANGE_BRANCH_RELATION2("SELECT%s txs1.mod_type, rel1.gamma_id, rel1.b_art_id, rel1.a_art_id, rel1.order_id, rel1.rel_type, art.art_type_id from osee_txs txs1, osee_relation rel1, osee_artifact art where txs1.branch_id = ? AND txs1.transaction_id <> ? AND txs1.tx_current in (" + TxCurrent.DELETED + ", " + TxCurrent.CURRENT + ", " + TxCurrent.ARTIFACT_DELETED + ") AND txs1.gamma_id = rel1.gamma_id AND rel1.a_art_id = art.art_id", Strings.HintsOrdered),
   CHANGE_TX_RELATION2("SELECT txs.mod_type, rel.gamma_id, rel.b_art_id, rel.a_art_id, rel.rel_order, rel.rel_type, art.art_type_id from osee_txs txs, osee_relation rel, osee_artifact art where txs.branch_id = ? AND txs.transaction_id = ? AND txs.gamma_id = rel.gamma_id AND rel.a_art_id = art.art_id"),
   CHANGE_TX_RELATION_FOR_SPECIFIC_ARTIFACT2(CHANGE_TX_RELATION.sql + " and (rel.a_art_id = ? or rel.b_art_id = ?)"),

   CHANGE_BRANCH_ARTIFACT("select%s art1.art_id, art1.art_type_id, art1.gamma_id, txs1.mod_type FROM osee_txs txs1, osee_artifact art1 WHERE txs1.branch_id = ? AND txs1.transaction_id <> ? AND txs1.gamma_id = art1.gamma_id AND txs1.mod_type in (" + ModificationType.DELETED.getIdString() + ", " + ModificationType.NEW.getIdString() + ", " + ModificationType.INTRODUCED.getIdString() + ") ", Strings.HintsOrdered),
   CHANGE_TX_ARTIFACT("select art.art_id, art.art_type_id, art.gamma_id, txs.mod_type FROM osee_txs txs, osee_artifact art WHERE txs.branch_id = ? and txs.transaction_id = ? AND txs.gamma_id = art.gamma_id AND txs.mod_type in (" + ModificationType.DELETED.getIdString() + ", " + ModificationType.NEW.getIdString() + ", " + ModificationType.INTRODUCED.getIdString() + ") "),
   CHANGE_TX_ARTIFACT_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_ARTIFACT.sql + " and art.art_id =?"),
   CHANGE_TX_MODIFYING("SELECT arj.id2, arj.id1, txs.transaction_id, arj.id4 from osee_join_id4 arj, osee_artifact art, osee_txs txs, osee_branch br where arj.query_id = ? AND arj.id2 = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = arj.id1 AND txs.transaction_id <= arj.id3 AND txs.branch_id = br.branch_id AND txs.transaction_id <> br.baseline_transaction_id", Strings.HintsOrdered),
   CHANGE_BRANCH_MODIFYING("SELECT count(txs.transaction_id) as tx_count, arj.id1, arj.id2, arj.id4 FROM osee_join_id4 arj, osee_artifact art, osee_txs txs, osee_branch br where arj.query_id = ? AND arj.id2 = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = arj.id1 and txs.branch_id = br.branch_id AND txs.transaction_id <> br.baseline_transaction_id group by arj.id2, arj.id1", Strings.HintsOrdered),

   IS_ARTIFACT_ON_BRANCH("SELECT%s count(1) from osee_artifact av1, osee_txs txs1 where av1.art_id = ? and av1.gamma_id = txs1.gamma_id and txs1.branch_id = ?", Strings.HintsOrdered),
   GET_CURRENT_BRANCH_CATEGORIES("select category from osee_txs txs, osee_branch_category bc where txs.branch_id = ? and txs.tx_current = " + TxCurrent.CURRENT + " and txs.gamma_id = bc.gamma_id"),
   ARTIFACT_TO_RELATED_B_ARTIFACT_ID("with links as (select GAMMA_ID, a_art_id, b_art_id from OSEE_RELATION_LINK where REL_SIDE_HERE in (ART_IDS_HERE) and REL_LINK_TYPE_ID = REL_TYPE_LINKE_ID_HERE) select links.a_art_id, links.b_art_id from links, osee_txs txs where txs.BRANCH_ID = BRANCH_ID_HERE and txs.TX_CURRENT = 1 and txs.MOD_TYPE not in (3,5,9,10) and txs.GAMMA_ID = links.gamma_id"),
   ARTIFACT_TO_RELATED_B_ARTIFACT_ID2("with links as (select GAMMA_ID, a_art_id, b_art_id from OSEE_RELATION where REL_SIDE_HERE in (ART_IDS_HERE) and REL_TYPE = REL_TYPE_HERE) select links.a_art_id, links.b_art_id from links, osee_txs txs where txs.BRANCH_ID = BRANCH_ID_HERE and txs.TX_CURRENT = 1 and txs.MOD_TYPE not in (3,5,9,10) and txs.GAMMA_ID = links.gamma_id"),

   ARTIFACT_TOKENS_RELATED_TO_ARTIFACT_QUERY("select * from osee_attribute attr, OSEE_ARTIFACT art where attr.attr_type_id = 1152921504606847088 and art.ART_ID=attr.ART_ID and attr.ART_ID in (with links as (select GAMMA_ID, a_art_id, b_art_id from OSEE_RELATION_LINK where REL_SIDE_HERE in (ART_IDS_HERE) and REL_LINK_TYPE_ID = REL_TYPE_LINKE_ID_HERE) select links.OPPOSITE_REL_SIDE_HERE from links, osee_txs txs where txs.BRANCH_ID = BRANCH_ID_HERE and txs.TX_CURRENT = 1 and txs.MOD_TYPE not in (3,5,9,10) and txs.GAMMA_ID = links.gamma_id)"),
   ARTIFACT_TOKENS_RELATED_TO_ARTIFACT_QUERY2("select * from osee_attribute attr, OSEE_ARTIFACT art where attr.attr_type_id = 1152921504606847088 and art.ART_ID=attr.ART_ID and attr.ART_ID in (with links as (select GAMMA_ID, a_art_id, b_art_id from OSEE_RELATION where REL_SIDE_HERE in (ART_IDS_HERE) and REL_TYPE = REL_TYPE_HERE) select links.OPPOSITE_REL_SIDE_HERE from links, osee_txs txs where txs.BRANCH_ID = BRANCH_ID_HERE and txs.TX_CURRENT = 1 and txs.MOD_TYPE not in (3,5,9,10) and txs.GAMMA_ID = links.gamma_id)"),
   LOAD_CURRENT_RELATION_ORDER_MINMAX("SELECT min(rel.rel_order) || ',' ||max(rel.rel_order) from osee_txs tx, osee_relation rel where tx.branch_id = ? and tx.tx_current = 1 and tx.gamma_id = rel.gamma_id and rel.a_art_id = ? and rel.rel_type = ?"),

   SELECT_RELATION_GAMMA_RT_A_ART_B_ART_ORDER_REL_ART("SELECT gamma_id FROM osee_relation WHERE rel_type=? AND a_art_id = ? AND b_art_id = ? and rel_order = ? and rel_art_id = ?");

   private final String sql;
   private final String hints;

   private OseeSql(String sql, String hints) {
      this.sql = sql;
      this.hints = hints;
   }

   private OseeSql(String sql) {
      this(sql, null);
   }

   public String getSql() {
      return sql;
   }

   public String getHints() {
      return hints;
   }

   public static Properties getSqlProperties(boolean areHintsSupported) {
      Properties sqlProperties = new Properties();
      for (OseeSql oseeSql : OseeSql.values()) {
         String sql;

         if (oseeSql.hints == null) {
            sql = oseeSql.sql;
         } else if (areHintsSupported) {
            sql = String.format(oseeSql.sql, oseeSql.hints);
         } else {
            sql = String.format(oseeSql.sql, "");
         }

         sqlProperties.setProperty(oseeSql.toString(), sql);
      }
      return sqlProperties;
   }

   public static boolean useOracleHints(Properties properties) {
      boolean useOracleHints = false;
      String useOracleHintsStr = (String) properties.get("useOracleHints");
      if (org.eclipse.osee.framework.jdk.core.util.Strings.isValid(useOracleHintsStr)) {
         try {
            useOracleHints = Boolean.valueOf(useOracleHintsStr);
         } catch (Exception ex) {
            // do nothing
         }
      }
      return useOracleHints;
   }

   public static class Strings {
      private static final String HintsOrdered = "/*+ ordered */";

      private static final String HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT =
         " /*+ ordered index(atr1) index(atr2) index(txs2) */";
      private static final String HINTS__ORDERED__INDEX__ATTRIBUTE_CONFLICT =
         " /*+ ordered index(atr1) index(atr2) index(txs2) */";

      private static final String SELECT_CURRENT_ATTRIBUTES_PREFIX =
         "SELECT%s att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.id1, al1.id4, txs.mod_type, txs.app_id, txd.* FROM osee_join_id4 al1, osee_attribute att1, osee_txs txs, osee_tx_details txd WHERE al1.query_id = ? AND al1.id2 = att1.art_id AND att1.gamma_id = txs.gamma_id AND txs.branch_id = al1.id1 AND txs.branch_id = txd.branch_id AND txs.transaction_id = txd.transaction_id AND txs.tx_current ";

      private static final String SELECT_CURRENT_ARTIFACTS_PREFIX =
         "SELECT%s aj.id2, txs.gamma_id, mod_type, art_type_id, guid, txs.branch_id, txs.app_id, aj.id4 FROM osee_join_id4 aj, osee_artifact art, osee_txs txs WHERE aj.query_id = ? AND aj.id2 = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = aj.id1 AND txs.tx_current ";

      private static final String SELECT_CURRENT_ARCHIVED_ATTRIBUTES_PREFIX =
         "SELECT%s att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.id1, txs.mod_type, txs.app_id, al1.id4, txd.* FROM osee_join_id4 al1, osee_attribute att1, osee_txs_archived txs, osee_tx_details txd WHERE al1.query_id = ? AND al1.id2 = att1.art_id AND att1.gamma_id = txs.gamma_id AND txs.branch_id = al1.id1 AND txs.branch_id = txd.branch_id AND txs.transaction_id = txd.transaction_id AND txs.tx_current ";

      private static final String SELECT_CURRENT_ARCHIVED_ARTIFACTS_PREFIX =
         "SELECT%s aj.id2, txs.gamma_id, mod_type, art_type_id, guid, txs.branch_id, txs.app_id, aj.id4 FROM osee_join_id4 aj, osee_artifact art, osee_txs_archived txs WHERE aj.query_id = ? AND aj.id2 = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = aj.id1 AND txs.tx_current ";

   }
}