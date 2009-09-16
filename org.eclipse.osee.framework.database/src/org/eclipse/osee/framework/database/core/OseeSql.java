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
   TX_GET_MAX_AND_MIN_TX("SELECT max(transaction_id) AS max_id, min(transaction_id) AS min_id FROM osee_tx_details WHERE branch_id = ?"),
   TX_GET_TX_GAMMAS("SELECT txs.transaction_id, txs.gamma_id FROM osee_tx_details txd, osee_txs txs WHERE txd.transaction_id = txs.transaction_id AND txd.branch_id = ? AND txd.transaction_id = ? ORDER BY txs.transaction_id, txs.gamma_id"),
   TX_GET_TX_GAMMAS_RANGE("SELECT txs.transaction_id, txs.gamma_id FROM osee_tx_details txd, osee_txs txs WHERE txd.transaction_id = txs.transaction_id AND txd.branch_id = ? AND txd.transaction_id > ? AND txd.transaction_id <= ? ORDER BY txs.transaction_id, txs.gamma_id"),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS("SELECT txs1.transaction_id, txs1.gamma_id FROM osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE arv1.art_id = ? AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue()),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES("SELECT txs1.transaction_id, txs1.gamma_id FROM osee_attribute atr1, osee_txs txs1, osee_tx_details txd1 WHERE atr1.attr_id = ? AND atr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue()),
   TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS("SELECT txs1.transaction_id, txs1.gamma_id FROM osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 WHERE rel1.rel_link_id = ? AND rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue()),

   MERGE_GET_ARTIFACTS_FOR_BRANCH("SELECT%s arv.art_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version arv WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = arv.gamma_id and det.branch_id = ?"),
   MERGE_GET_ATTRIBUTES_FOR_BRANCH("SELECT%s atr.art_id, atr.attr_id FROM  osee_tx_details det,  osee_txs txs, osee_attribute atr WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = atr.gamma_id and det.branch_id = ?"),
   MERGE_GET_RELATIONS_FOR_BRANCH("SELECT%s rel.a_art_id, rel.b_art_id FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = rel.gamma_id and det.branch_id = ?"),

   CONFLICT_GET_ARTIFACTS("SELECT%s art1.art_type_id, arv1.art_id, txs1.mod_type AS source_mod_type, txs1.gamma_id AS source_gamma, txs2.mod_type AS dest_mod_type, txs2.gamma_id AS dest_gamma FROM osee_tx_details txd1, osee_txs txs1, osee_artifact_version arv1, osee_artifact_version arv2, osee_txs txs2, osee_tx_details txd2, osee_artifact art1 WHERE txd1.tx_type = 0 AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = arv1.gamma_id and arv1.art_id = art1.art_id AND arv1.art_id = arv2.art_id AND arv2.gamma_id = txs2.gamma_id AND txs2.transaction_id = txd2.transaction_id AND txd2.branch_id = ? AND ((txs2.tx_current = 1 AND txs2.gamma_id not in (SELECT txs.gamma_id FROM osee_txs txs WHERE txs.transaction_id = ?)) OR txs2.tx_current = 2)", Strings.HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT),
   CONFLICT_GET_ATTRIBUTES("SELECT%s atr1.art_id, txs1.mod_type, atr1.attr_type_id, atr1.attr_id, atr1.gamma_id AS source_gamma, atr1.value AS source_value, atr2.gamma_id AS dest_gamma, atr2.value as dest_value, txs2.mod_type AS dest_mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_attribute atr1, osee_attribute atr2, osee_txs txs2, osee_tx_details txd2 WHERE txd1.tx_type = 0 AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.attr_id = atr2.attr_id AND atr2.gamma_id = txs2.gamma_id AND txs2.transaction_id = txd2.transaction_id AND txd2.branch_id = ? AND ((txs2.tx_current = 1 AND txs2.gamma_id not in (SELECT txs.gamma_id FROM osee_txs txs WHERE txs.transaction_id = ? )) OR txs2.tx_current = 2) ORDER BY attr_id", Strings.HINTS__ORDERED__INDEX__ATTRIBUTE_CONFLICT),
   CONFLICT_GET_HISTORICAL_ATTRIBUTES("SELECT%s atr.attr_id, atr.art_id, source_gamma_id, dest_gamma_id, attr_type_id, mer.merge_branch_id, mer.dest_branch_id, value as source_value, status FROM osee_merge mer, osee_conflict con,  osee_attribute atr Where mer.commit_transaction_id = ? AND mer.merge_branch_id = con.merge_branch_id And con.source_gamma_id = atr.gamma_id AND con.status in (" + ConflictStatus.COMMITTED.getValue() + ", " + ConflictStatus.INFORMATIONAL.getValue() + " ) order by attr_id"),

   LOAD_HISTORICAL_ARTIFACTS("SELECT%s al1.art_id, txs1.gamma_id, mod_type, txd1.*, art_type_id, guid, human_readable_id, al1.transaction_id as stripe_transaction_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= al1.transaction_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by al1.branch_id, art1.art_id, txs1.transaction_id desc"),
   LOAD_HISTORICAL_ATTRIBUTES("SELECT%s att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id, txs1.mod_type, txd1.transaction_id, al1.transaction_id as stripe_transaction_id FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= al1.transaction_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by txd1.branch_id, att1.art_id, att1.attr_id, txd1.transaction_id desc"),
   LOAD_CURRENT_ATTRIBUTES(Strings.SELECT_CURRENT_ATTRIBUTES_PREFIX + "= 1 order by al1.branch_id, al1.art_id, att1.attr_id, txd1.transaction_id desc"),
   LOAD_CURRENT_ATTRIBUTES_WITH_DELETED(Strings.SELECT_CURRENT_ATTRIBUTES_PREFIX + "IN (1, 3) order by al1.branch_id, al1.art_id, att1.attr_id, txd1.transaction_id desc"),
   LOAD_ALL_CURRENT_ATTRIBUTES(Strings.SELECT_CURRENT_ATTRIBUTES_PREFIX + "IN (1, 2, 3) order by al1.branch_id, al1.art_id, att1.attr_id, txd1.transaction_id desc"),
   LOAD_RELATIONS("SELECT%s txs1.mod_type, rel_link_id, a_art_id, b_art_id, rel_link_type_id, a_order, b_order, rel1.gamma_id, rationale, al1.branch_id FROM osee_join_artifact al1, osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND (al1.art_id = rel1.a_art_id OR al1.art_id = rel1.b_art_id) AND rel1.gamma_id = txs1.gamma_id AND txs1.tx_current = " + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id"),
   LOAD_CURRENT_ARTIFACTS(Strings.SELECT_CURRENT_ARTIFACTS_PREFIX + "= 1"),
   LOAD_CURRENT_ARTIFACTS_WITH_DELETED(Strings.SELECT_CURRENT_ARTIFACTS_PREFIX + "in (1, 2)"),

   CHANGE_GET_TRANSACTIONS_PER_ARTIFACT("SELECT%s td1.transaction_id from osee_tx_details td1, osee_txs tx1, osee_artifact_version av1 where td1.branch_id = ? and td1.transaction_id = tx1.transaction_id and tx1.gamma_id = av1.gamma_id and av1.art_id = ?"),
   CHANGE_BRANCH_ATTRIBUTE_WAS("SELECT%s attxs1.attr_id, attxs1.value as was_value, txs1.mod_type FROM osee_join_artifact ja1, osee_attribute attxs1, osee_txs txs1, osee_tx_details txd1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txd1.tx_type = 1 AND attxs1.gamma_id = txs1.gamma_id AND attxs1.art_id = ja1.art_id AND txd1.branch_id = ja1.branch_id AND ja1.query_id = ?"),
   CHANGE_TX_ATTRIBUTE_WAS("SELECT%s att1.attr_id, att1.value as was_value, txs1.mod_type FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE  al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id < ? AND al1.query_id = ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by txd1.branch_id, att1.art_id, att1.attr_id, txd1.transaction_id desc"),
   CHANGE_BRANCH_ATTRIBUTE_IS("SELECT%s artxs1.art_type_id, attr1.art_id, attr1.attr_id, attr1.gamma_id, attr1.attr_type_id, attr1.value as is_value, txs1.mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_attribute attr1, osee_artifact artxs1  WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (" + TxChange.DELETED.getValue() + ", " + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") AND txd1.tx_type = 0 AND artxs1.art_id = attr1.art_id AND attr1.gamma_id = txs1.gamma_id"),
   CHANGE_TX_ATTRIBUTE_IS_PREFIX("SELECT%s artxs1.art_type_id, attr1.art_id, attr1.attr_id, attr1.gamma_id, attr1.attr_type_id, attr1.value as is_value, txs1.mod_type FROM osee_tx_details txd2, osee_txs txs1, osee_attribute attr1, osee_artifact artxs1 WHERE txd2.transaction_id = ? AND txd2.transaction_id = txs1.transaction_id AND artxs1.art_id = attr1.art_id AND attr1.gamma_id = txs1.gamma_id"),
   CHANGE_TX_ATTRIBUTE_IS(CHANGE_TX_ATTRIBUTE_IS_PREFIX.sql + " AND txd2.tx_type = 0"),
   CHANGE_TX_ATTRIBUTE_IS_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_ATTRIBUTE_IS_PREFIX.sql + " and attr1.art_id =?"),
   CHANGE_BRANCH_RELATION("SELECT%s txs1.mod_type, rel1.gamma_id, rel1.b_art_id, rel1.a_art_id, rel1.a_order, rel1.b_order, rel1.rationale, rel1.rel_link_id, rel1.rel_link_type_id, art.art_type_id from osee_tx_details txd1, osee_txs txs1, osee_relation_link rel1, osee_artifact art where txs1.tx_current in (" + TxChange.DELETED.getValue() + ", " + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") AND txd1.tx_type = 0 AND txd1.branch_id = ? AND txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = rel1.gamma_id AND rel1.a_art_id = art.art_id"),
   CHANGE_TX_RELATION_PREFIX("SELECT%s txs1.mod_type, rel1.gamma_id, rel1.b_art_id, rel1.a_art_id, rel1.a_order, rel1.b_order, rel1.rationale, rel1.rel_link_id, rel1.rel_link_type_id, art.art_type_id from osee_tx_details txd1, osee_txs txs1, osee_relation_link rel1, osee_artifact art where txd1.transaction_id = ? AND txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = rel1.gamma_id AND rel1.a_art_id = art.art_id"),
   CHANGE_TX_RELATION(CHANGE_TX_RELATION_PREFIX.sql + " AND txd1.tx_type = 0"),
   CHANGE_TX_RELATION_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_RELATION_PREFIX.sql + " and (rel1.a_art_id =? or rel1.b_art_id=?)"),
   CHANGE_BRANCH_ARTIFACT("select%s art1.art_id, art1.art_type_id, atv1.gamma_id, txs1.mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_artifact_version atv1, osee_artifact art1 WHERE txd1.branch_id = ? AND txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = atv1.gamma_id AND txs1.mod_type in (" + ModificationType.DELETED.getValue() + ", " + ModificationType.NEW.getValue() + ", " + ModificationType.INTRODUCED.getValue() + ")  AND atv1.art_id = art1.art_id"),
   CHANGE_TX_ARTIFACT_PREFIX("select%s art1.art_id, art1.art_type_id, atv1.gamma_id, txs1.mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_artifact_version atv1, osee_artifact art1 WHERE txd1.transaction_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = atv1.gamma_id AND txs1.mod_type in (" + ModificationType.DELETED.getValue() + ", " + ModificationType.NEW.getValue() + ", " + ModificationType.INTRODUCED.getValue() + ")  AND atv1.art_id = art1.art_id"),
   CHANGE_TX_ARTIFACT(CHANGE_TX_ARTIFACT_PREFIX.sql + " AND txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId()),
   CHANGE_TX_ARTIFACT_FOR_SPECIFIC_ARTIFACT(CHANGE_TX_ARTIFACT_PREFIX.sql + " and art1.art_id =?"),
   CHANGE_TX_MODIFYING("SELECT arj.art_id, arj.branch_id, txd.transaction_id from osee_join_artifact arj, osee_artifact_version arv, osee_txs txs, osee_tx_details txd where arj.query_id = ? AND arj.art_id = arv.art_id AND arv.gamma_id = txs.gamma_id AND txs.transaction_id = txd.transaction_id AND txd.branch_id = arj.branch_id AND txd.transaction_id <= arj.transaction_id AND txd.tx_type = " + TransactionDetailsType.NonBaselined.getId()),
   CHANGE_BRANCH_MODIFYING("SELECT count(txd.transaction_id) as tx_count, arj.branch_id, arj.art_id FROM osee_join_artifact arj, osee_artifact_version arv, osee_txs txs, osee_tx_details txd where arj.query_id = ? AND arj.art_id = arv.art_id AND arv.gamma_id = txs.gamma_id AND txs.transaction_id = txd.transaction_id AND txd.branch_id = arj.branch_id and tx_type = 0 group by arj.art_id, arj.branch_id"),

   QUERY_BUILDER("%s");

   private final String sql;
   private final String hints;

   private OseeSql(String sql, String hints) {
      this.sql = sql;
      this.hints = hints;
   }

   private OseeSql(String sql) {
      this(sql, Strings.HINTS__ORDERED__FIRST_ROWS);
   }

   public static Properties getSqlProperties() throws OseeDataStoreException {
      Properties sqlProperties = new Properties();
      boolean areHintsSupported = SupportedDatabase.areHintsSupported();
      for (OseeSql oseeSql : OseeSql.values()) {
         sqlProperties.setProperty(oseeSql.toString(), String.format(oseeSql.sql,
               areHintsSupported ? oseeSql.hints : ""));
      }
      return sqlProperties;
   }

   private static class Strings {
      private static final String HINTS__ORDERED__FIRST_ROWS = " /*+ ordered FIRST_ROWS */";

      private static final String HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT =
            " /*+ ordered index(atr1) index(atr2) index(txs2) */";
      private static final String HINTS__ORDERED__INDEX__ATTRIBUTE_CONFLICT =
            " /*+ ordered index(atr1) index(atr2) index(txs2) */";

      private static final String SELECT_CURRENT_ATTRIBUTES_PREFIX =
            "SELECT%s att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id, txs1.mod_type FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id AND txs1.tx_current ";

      private static final String SELECT_CURRENT_ARTIFACTS_PREFIX =
            "SELECT%s al1.art_id, txs1.gamma_id, mod_type, txd1.*, art_type_id, guid, human_readable_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current ";

   }
}