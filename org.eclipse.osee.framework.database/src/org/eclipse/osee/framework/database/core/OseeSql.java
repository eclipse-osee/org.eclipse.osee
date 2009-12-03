/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.core;

import java.sql.DatabaseMetaData;
import java.util.Properties;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;

/**
 * @author Ryan D. Brooks
 */
public enum OseeSql {

   TX_GET_ALL_TRANSACTIONS("SELECT * FROM osee_tx_details WHERE transaction_id = ?"),
   TX_GET_MAX_AS_LARGEST_TX("SELECT max(transaction_id) as largest_transaction_id FROM osee_tx_details WHERE branch_id = ?"),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS("SELECT txs1.transaction_id, txs1.gamma_id FROM osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE arv1.art_id = ? AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current <> " + TxChange.NOT_CURRENT.getValue()),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES("SELECT txs1.transaction_id, txs1.gamma_id FROM osee_attribute atr1, osee_txs txs1, osee_tx_details txd1 WHERE atr1.attr_id = ? AND atr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current <> " + TxChange.NOT_CURRENT.getValue()),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS("SELECT txs1.transaction_id, txs1.gamma_id FROM osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 WHERE rel1.rel_link_id = ? AND rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current <> " + TxChange.NOT_CURRENT.getValue()),

   MERGE_GET_ARTIFACTS_FOR_BRANCH("SELECT arv.art_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version arv WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = arv.gamma_id and det.branch_id = ?"),
   MERGE_GET_ATTRIBUTES_FOR_BRANCH("SELECT%s atr.art_id, atr.attr_id FROM  osee_tx_details det,  osee_txs txs, osee_attribute atr WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = atr.gamma_id and det.branch_id = ?", Strings.HINTS__ORDERED__FIRST_ROWS),
   MERGE_GET_RELATIONS_FOR_BRANCH("SELECT%s rel.a_art_id, rel.b_art_id FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = rel.gamma_id and det.branch_id = ?", Strings.HINTS__ORDERED__FIRST_ROWS),

   CONFLICT_GET_ARTIFACTS("SELECT%s art1.art_type_id, arv1.art_id, txs1.mod_type AS source_mod_type, txs1.gamma_id AS source_gamma, txs2.mod_type AS dest_mod_type, txs2.gamma_id AS dest_gamma FROM osee_tx_details txd1, osee_txs txs1, osee_artifact_version arv1, osee_artifact_version arv2, osee_txs txs2, osee_tx_details txd2, osee_artifact art1 WHERE txd1.tx_type = 0 AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = arv1.gamma_id and arv1.art_id = art1.art_id AND arv1.art_id = arv2.art_id AND arv2.gamma_id = txs2.gamma_id AND txs2.transaction_id = txd2.transaction_id AND txd2.branch_id = ? AND ((txs2.tx_current = 1 AND txs2.gamma_id not in (SELECT txs.gamma_id FROM osee_txs txs WHERE txs.transaction_id = ?)) OR txs2.tx_current = 2)", Strings.HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT),
   CONFLICT_GET_ATTRIBUTES("SELECT%s atr1.art_id, txs1.mod_type, atr1.attr_type_id, atr1.attr_id, atr1.gamma_id AS source_gamma, atr1.value AS source_value, atr2.gamma_id AS dest_gamma, atr2.value as dest_value, txs2.mod_type AS dest_mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_attribute atr1, osee_attribute atr2, osee_txs txs2, osee_tx_details txd2 WHERE txd1.tx_type = 0 AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.attr_id = atr2.attr_id AND atr2.gamma_id = txs2.gamma_id AND txs2.transaction_id = txd2.transaction_id AND txd2.branch_id = ? AND ((txs2.tx_current = 1 AND txs2.gamma_id not in (SELECT txs.gamma_id FROM osee_txs txs WHERE txs.transaction_id = ? )) OR txs2.tx_current = 2) ORDER BY attr_id", Strings.HINTS__ORDERED__INDEX__ATTRIBUTE_CONFLICT),
   CONFLICT_GET_HISTORICAL_ATTRIBUTES("SELECT%s atr.attr_id, atr.art_id, source_gamma_id, dest_gamma_id, attr_type_id, mer.merge_branch_id, mer.dest_branch_id, value as source_value, status FROM osee_merge mer, osee_conflict con,  osee_attribute atr Where mer.commit_transaction_id = ? AND mer.merge_branch_id = con.merge_branch_id And con.source_gamma_id = atr.gamma_id AND con.status in (" + ConflictStatus.COMMITTED.getValue() + ", " + ConflictStatus.INFORMATIONAL.getValue() + " ) order by attr_id", Strings.HINTS__ORDERED__FIRST_ROWS),

   LOAD_HISTORICAL_ARTIFACTS("SELECT%s aj.art_id, txs.branch_id, txs.gamma_id, txs.mod_type, txs.transaction_id, txd.tx_type, txd.osee_comment, txd.time, txd.author, txd.commit_art_id, art_type_id, guid, human_readable_id, aj.transaction_id as stripe_transaction_id FROM osee_join_artifact aj, osee_artifact art, osee_artifact_version arv, osee_txs txs, osee_tx_details txd WHERE aj.query_id = ? AND aj.art_id = art.art_id AND art.art_id = arv.art_id AND arv.gamma_id = txs.gamma_id AND txs.transaction_id <= aj.transaction_id AND txs.branch_id = aj.branch_id and txs.transaction_id = txd.transaction_id order by aj.branch_id, art.art_id, txs.transaction_id desc", Strings.HINTS__THE_INDEX),
   LOAD_HISTORICAL_ATTRIBUTES("SELECT att.art_id, att.attr_id, att.value, att.gamma_id, att.attr_type_id, att.uri, aj.branch_id, txs.mod_type, txs.transaction_id, aj.transaction_id as stripe_transaction_id FROM osee_join_artifact aj, osee_attribute att, osee_txs txs WHERE aj.query_id = ? AND aj.art_id = att.art_id AND att.gamma_id = txs.gamma_id AND txs.branch_id = aj.branch_id AND txs.transaction_id <= aj.transaction_id order by txs.branch_id, att.art_id, att.attr_id, txs.transaction_id desc"),
   LOAD_CURRENT_ATTRIBUTES(Strings.SELECT_CURRENT_ATTRIBUTES_PREFIX + "= 1 order by al1.branch_id, al1.art_id, att1.attr_id, txs1.transaction_id desc"),
   LOAD_CURRENT_ATTRIBUTES_WITH_DELETED(Strings.SELECT_CURRENT_ATTRIBUTES_PREFIX + "IN (1, 3) order by al1.branch_id, al1.art_id, att1.attr_id, txs1.transaction_id desc"),
   LOAD_ALL_CURRENT_ATTRIBUTES(Strings.SELECT_CURRENT_ATTRIBUTES_PREFIX + "IN (1, 2, 3) order by al1.branch_id, al1.art_id, att1.attr_id, txs1.transaction_id desc"),
   LOAD_RELATIONS("SELECT%s txs1.mod_type, rel_link_id, a_art_id, b_art_id, rel_link_type_id, rel1.gamma_id, rationale, al1.branch_id FROM osee_join_artifact al1, osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND (al1.art_id = rel1.a_art_id OR al1.art_id = rel1.b_art_id) AND rel1.gamma_id = txs1.gamma_id AND txs1.tx_current = " + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id", Strings.HINTS__ORDERED__FIRST_ROWS),
   LOAD_CURRENT_ARTIFACTS(Strings.SELECT_CURRENT_ARTIFACTS_PREFIX + "= 1", Strings.HINTS__ORDERED__FIRST_ROWS),
   LOAD_CURRENT_ARTIFACTS_WITH_DELETED(Strings.SELECT_CURRENT_ARTIFACTS_PREFIX + "in (1, 2)", Strings.HINTS__ORDERED__FIRST_ROWS),
   CHANGE_BRANCH_ATTRIBUTE_WAS("SELECT%s attxs1.attr_id, attxs1.value as was_value, txs1.mod_type FROM osee_join_artifact ja1, osee_attribute attxs1, osee_txs txs1, osee_tx_details txd1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txd1.tx_type = 1 AND attxs1.gamma_id = txs1.gamma_id AND attxs1.art_id = ja1.art_id AND txd1.branch_id = ja1.branch_id AND ja1.query_id = ?", Strings.HINTS__ORDERED__FIRST_ROWS),
   CHANGE_TX_ATTRIBUTE_WAS("SELECT%s att1.attr_id, att1.value as was_value, txs1.mod_type FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE  al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id < ? AND al1.query_id = ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by txd1.branch_id, att1.art_id, att1.attr_id, txd1.transaction_id desc", Strings.HINTS__ORDERED__FIRST_ROWS),
   CHANGE_BRANCH_ATTRIBUTE_IS("SELECT%s artxs1.art_type_id, attr1.art_id, attr1.attr_id, attr1.gamma_id, attr1.attr_type_id, attr1.value as is_value, txs1.mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_attribute attr1, osee_artifact artxs1  WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (" + TxChange.DELETED.getValue() + ", " + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") AND txd1.tx_type = 0 AND artxs1.art_id = attr1.art_id AND attr1.gamma_id = txs1.gamma_id", Strings.HINTS__ORDERED__FIRST_ROWS),

   CHANGE_TX_ATTRIBUTE_IS("SELECT art.art_type_id, att.art_id, att.attr_id, att.gamma_id, att.attr_type_id, att.value as is_value, txs.mod_type FROM osee_txs txs, osee_attribute att, osee_artifact art WHERE txs.transaction_id = ? AND txs.gamma_id = att.gamma_id AND att.art_id = art.art_id"),
   CHANGE_TX_ATTRIBUTE_IS_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_ATTRIBUTE_IS.sql + " and att.art_id =?"),
   CHANGE_BRANCH_RELATION("SELECT%s txs1.mod_type, rel1.gamma_id, rel1.b_art_id, rel1.a_art_id, rel1.rationale, rel1.rel_link_id, rel1.rel_link_type_id, art.art_type_id from osee_tx_details txd1, osee_txs txs1, osee_relation_link rel1, osee_artifact art where txs1.tx_current in (" + TxChange.DELETED.getValue() + ", " + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") AND txd1.tx_type = 0 AND txd1.branch_id = ? AND txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = rel1.gamma_id AND rel1.a_art_id = art.art_id", Strings.HINTS__ORDERED__FIRST_ROWS),
   CHANGE_TX_RELATION("SELECT txs.mod_type, rel.gamma_id, rel.b_art_id, rel.a_art_id, rel.rationale, rel.rel_link_id, rel.rel_link_type_id, art.art_type_id from osee_txs txs, osee_relation_link rel, osee_artifact art where txs.transaction_id = ? AND txs.gamma_id = rel.gamma_id AND rel.a_art_id = art.art_id"),
   CHANGE_TX_RELATION_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_RELATION.sql + " and (rel.a_art_id = ? or rel.b_art_id = ?)"),
   CHANGE_BRANCH_ARTIFACT("select%s art1.art_id, art1.art_type_id, atv1.gamma_id, txs1.mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_artifact_version atv1, osee_artifact art1 WHERE txd1.branch_id = ? AND txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = atv1.gamma_id AND txs1.mod_type in (" + ModificationType.DELETED.getValue() + ", " + ModificationType.NEW.getValue() + ", " + ModificationType.INTRODUCED.getValue() + ")  AND atv1.art_id = art1.art_id", Strings.HINTS__ORDERED__FIRST_ROWS),
   CHANGE_TX_ARTIFACT("select art.art_id, art.art_type_id, arv.gamma_id, txs.mod_type FROM osee_txs txs, osee_artifact_version arv, osee_artifact art WHERE txs.transaction_id = ? AND txs.gamma_id = arv.gamma_id AND txs.mod_type in (" + ModificationType.DELETED.getValue() + ", " + ModificationType.NEW.getValue() + ", " + ModificationType.INTRODUCED.getValue() + ")  AND arv.art_id = art.art_id"),
   CHANGE_TX_ARTIFACT_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_ARTIFACT.sql + " and art.art_id =?"),
   CHANGE_TX_MODIFYING("SELECT arj.art_id, arj.branch_id, txd.transaction_id from osee_join_artifact arj, osee_artifact_version arv, osee_txs txs, osee_tx_details txd where arj.query_id = ? AND arj.art_id = arv.art_id AND arv.gamma_id = txs.gamma_id AND txs.transaction_id = txd.transaction_id AND txd.branch_id = arj.branch_id AND txd.transaction_id <= arj.transaction_id AND txd.tx_type = " + TransactionDetailsType.NonBaselined.getId(), Strings.HINTS__ORDERED__FIRST_ROWS),
   CHANGE_BRANCH_MODIFYING("SELECT count(txd.transaction_id) as tx_count, arj.branch_id, arj.art_id FROM osee_join_artifact arj, osee_artifact_version arv, osee_txs txs, osee_tx_details txd where arj.query_id = ? AND arj.art_id = arv.art_id AND arv.gamma_id = txs.gamma_id AND txs.transaction_id = txd.transaction_id AND txd.branch_id = arj.branch_id and tx_type = 0 group by arj.art_id, arj.branch_id", Strings.HINTS__ORDERED__FIRST_ROWS),

   QUERY_BUILDER("%s", Strings.HINTS__ORDERED__FIRST_ROWS);

   private final String sql;
   private final String hints;

   private OseeSql(String sql, String hints) {
      this.sql = sql;
      this.hints = hints;
   }

   private OseeSql(String sql) {
      this(sql, null);
   }

   public static Properties getSqlProperties(DatabaseMetaData metaData) throws OseeDataStoreException {
      Properties sqlProperties = new Properties();
      boolean areHintsSupported = SupportedDatabase.areHintsSupported(metaData);
      for (OseeSql oseeSql : OseeSql.values()) {
         String sql;

         if (oseeSql.hints == null) {
            sql = oseeSql.sql;
         } else {
            String hints = areHintsSupported ? oseeSql.hints : "";
            sql = String.format(oseeSql.sql, hints);
         }

         sqlProperties.setProperty(oseeSql.toString(), sql);
      }
      return sqlProperties;
   }

   private static class Strings {
      private static final String HINTS__ORDERED__FIRST_ROWS = " /*+ ordered FIRST_ROWS */";
      private static final String HINTS__THE_INDEX = "/*+ INDEX(txs OSEE_TXS_B_G_C_M_T_IDX) */";

      private static final String HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT =
            " /*+ ordered index(atr1) index(atr2) index(txs2) */";
      private static final String HINTS__ORDERED__INDEX__ATTRIBUTE_CONFLICT =
            " /*+ ordered index(atr1) index(atr2) index(txs2) */";

      private static final String SELECT_CURRENT_ATTRIBUTES_PREFIX =
            "SELECT att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id, txs1.mod_type FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.branch_id = al1.branch_id AND txs1.tx_current ";

      private static final String SELECT_CURRENT_ARTIFACTS_PREFIX =
            "SELECT%s al1.art_id, txs1.gamma_id, mod_type, txd1.*, art_type_id, guid, human_readable_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current ";

   }
}