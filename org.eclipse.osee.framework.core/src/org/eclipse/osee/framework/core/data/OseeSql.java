/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.Properties;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

/**
 * @author Roberto E. Escobar
 */
public class OseeSql {

   public static final String QUERY_BUILDER_HINT = "QUERY_BUILDER_HINT";

   private static final String ORDERED_HINT = " /*+ ordered */";
   private static final String ORDERED_HINT_AND_TXS_INDEX = " /*+ ordered INDEX(txs1) */";
   private static final String HINTS__ORDERED__TXS_IDX__REL_IDX = " /*+ ordered INDEX(txs1) INDEX(rel1) */";
   private static final String HINTS__ORDERED__TXS_IDX__ART_IDX__ARTV_IDX__TXD_IDX =
         " /*+ ordered INDEX(txs1) INDEX(art1) INDEX(arv1) INDEX(txd1) */";
   private static final String HINTS__ORDERED__FIRST_ROWS = " /*+ ordered FIRST_ROWS */";
   private static final String HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT =
         " /*+ ordered index(atr1) index(atr2) index(txs2) */";
   private static final String HINTS__ORDERED__INDEX__ATTRIBUTE_CONFLICT =
         " /*+ ordered index(atr1) index(atr2) index(txs2) */";

   private static Boolean areHintsAllowed = null;

   private OseeSql() {
   }

   public static Properties getSqlProperties() throws OseeDataStoreException {
      Properties sqlProperties = new Properties();

      sqlProperties.put(OseeSql.QUERY_BUILDER_HINT, HINTS__ORDERED__FIRST_ROWS);

      Load.addSql(sqlProperties);
      Changes.addSql(sqlProperties);
      Conflicts.addSql(sqlProperties);
      Merge.addSql(sqlProperties);
      Transaction.addSql(sqlProperties);
      return sqlProperties;
   }

   public static boolean isHintsAllowed() throws OseeDataStoreException {
      if (areHintsAllowed == null) {
         areHintsAllowed = SupportedDatabase.areHintsSupported();
      }
      return areHintsAllowed;
   }

   private static String getFormattedSql(String sql, String sqlHints) throws OseeDataStoreException {
      return String.format(sql, isHintsAllowed() ? sqlHints : "");
   }

   public static final class Changes {

      public static final String SELECT_BRANCH_ATTRIBUTE_WAS_CHANGE = "BRANCH_ATTRIBUTE_WAS_CHANGE";
      public static final String SELECT_TRANSACTION_ATTRIBUTE_WAS_CHANGE = "TRANSACTION_ATTRIBUTE_WAS_CHANGE";
      public static final String SELECT_BRANCH_ATTRIBUTE_IS_CHANGES = "BRANCH_ATTRIBUTE_IS_CHANGES";
      public static final String SELECT_TRANSACTION_ATTRIBUTE_CHANGES = "TRANSACTION_ATTRIBUTE_CHANGES";
      public static final String SELECT_BRANCH_REL_CHANGES = "BRANCH_REL_CHANGES";
      public static final String SELECT_TRANSACTION_REL_CHANGES = "TRANSACTION_REL_CHANGES";
      public static final String SELECT_BRANCH_ARTIFACT_CHANGES = "BRANCH_ARTIFACT_CHANGES";
      public static final String SELECT_TRANSACTION_ARTIFACT_CHANGES = "TRANSACTION_ARTIFACT_CHANGES";
      public static final String SELECT_MODIFYING_TRANSACTION = "SELECT_MODIFYING_TRANSACTION";
      public static final String SELECT_MODIFYING_BRANCHES = "SELECT_MODIFYING_BRANCHES";

      private Changes() {
      }

      private static final String BRANCH_ATTRIBUTE_WAS_CHANGE =
            "SELECT%s attxs1.attr_id, attxs1.value as was_value, txs1.mod_type FROM osee_join_artifact ja1, osee_attribute attxs1, osee_txs txs1, osee_tx_details txd1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txd1.tx_type = 1 AND attxs1.gamma_id = txs1.gamma_id AND attxs1.art_id = ja1.art_id AND txd1.branch_id = ja1.branch_id AND ja1.query_id = ?";

      private static final String TRANSACTION_ATTRIBUTE_WAS_CHANGE =
            "SELECT%s att1.attr_id, att1.value as was_value, txs1.mod_type FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE  al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id < ? AND al1.query_id = ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by txd1.branch_id, att1.art_id, att1.attr_id, txd1.transaction_id desc";

      private static final String BRANCH_ATTRIBUTE_IS_CHANGES =
            "SELECT%s artxs1.art_type_id, attr1.art_id, attr1.attr_id, attr1.gamma_id, attr1.attr_type_id, attr1.value as is_value, txs1.mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_attribute attr1, osee_artifact artxs1  WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (" + TxChange.DELETED.getValue() + ", " + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") AND txd1.tx_type = 0 AND artxs1.art_id = attr1.art_id AND attr1.gamma_id = txs1.gamma_id";

      private static final String TRANSACTION_ATTRIBUTE_CHANGES =
            "SELECT%s artxs1.art_type_id, attr1.art_id, attr1.attr_id, attr1.gamma_id, attr1.attr_type_id, attr1.value as is_value, txs1.mod_type FROM osee_tx_details txd2, osee_txs txs1, osee_attribute attr1, osee_artifact artxs1 WHERE txd2.transaction_id = ? AND txd2.transaction_id = txs1.transaction_id AND txd2.tx_type = 0 AND artxs1.art_id = attr1.art_id AND attr1.gamma_id = txs1.gamma_id";

      private static final String BRANCH_REL_CHANGES =
            "SELECT%s txs1.mod_type, rel1.gamma_id, rel1.b_art_id, rel1.a_art_id, rel1.a_order, rel1.b_order, rel1.rationale, rel1.rel_link_id, rel1.rel_link_type_id from osee_tx_details txd1, osee_txs txs1, osee_relation_link rel1 where txs1.tx_current in (" + TxChange.DELETED.getValue() + ", " + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") AND txd1.tx_type = 0 AND txd1.branch_id = ? AND txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = rel1.gamma_id";

      private static final String TRANSACTION_REL_CHANGES =
            "SELECT%s txs1.mod_type, rel1.gamma_id, rel1.b_art_id, rel1.a_art_id, rel1.a_order, rel1.b_order, rel1.rationale, rel1.rel_link_id, rel1.rel_link_type_id from osee_tx_details txd1, osee_txs txs1, osee_relation_link rel1 where txd1.tx_type = 0 AND txd1.transaction_id = ? AND txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = rel1.gamma_id";

      private static final String BRANCH_ARTIFACT_CHANGES =
            "select%s art1.art_id, art1.art_type_id, atv1.gamma_id, txs1.mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_artifact_version atv1, osee_artifact art1 WHERE txd1.branch_id = ? AND txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = atv1.gamma_id AND (txs1.tx_current = " + TxChange.DELETED.getValue() + " OR txs1.mod_type = " + ModificationType.NEW.getValue() + ")  AND atv1.art_id = art1.art_id";

      private static final String TRANSACTION_ARTIFACT_CHANGES =
            "select%s art1.art_id, art1.art_type_id, atv1.gamma_id, txs1.mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_artifact_version atv1, osee_artifact art1 WHERE txd1.transaction_id = ? AND txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = atv1.gamma_id AND (txs1.mod_type = " + ModificationType.DELETED.getValue() + " OR txs1.mod_type = " + ModificationType.NEW.getValue() + ")  AND atv1.art_id = art1.art_id";

      private static final String MODIFYING_TRANSACTION =
            "SELECT arj.art_id, arj.branch_id, txd.transaction_id from osee_join_artifact arj, osee_artifact_version arv, osee_txs txs, osee_tx_details txd where arj.query_id = ? AND arj.art_id = arv.art_id AND arv.gamma_id = txs.gamma_id AND txs.transaction_id = txd.transaction_id AND txd.branch_id = arj.branch_id AND txd.transaction_id <= arj.transaction_id AND txd.tx_type = " + TransactionDetailsType.NonBaselined.getId();

      private static final String MODIFYING_BRANCHES =
            "SELECT count(txd.transaction_id), arj.branch_id, arj.art_id FROM osee_join_artifact arj, osee_artifact_version arv, osee_txs txs, osee_tx_details txd where arj.query_id = ? AND arj.art_id = arv.art_id AND arv.gamma_id = txs.gamma_id AND txs.transaction_id = txd.transaction_id AND tx_type = 0 group by arj.art_id,  arj.branch_id";

      private static void addSql(Properties sqlProperties) throws OseeDataStoreException {
         sqlProperties.put(SELECT_BRANCH_ATTRIBUTE_WAS_CHANGE, getFormattedSql(BRANCH_ATTRIBUTE_WAS_CHANGE,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_TRANSACTION_ATTRIBUTE_WAS_CHANGE, getFormattedSql(TRANSACTION_ATTRIBUTE_WAS_CHANGE,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_BRANCH_ATTRIBUTE_IS_CHANGES, getFormattedSql(BRANCH_ATTRIBUTE_IS_CHANGES,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_TRANSACTION_ATTRIBUTE_CHANGES, getFormattedSql(TRANSACTION_ATTRIBUTE_CHANGES,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_BRANCH_REL_CHANGES, getFormattedSql(BRANCH_REL_CHANGES, HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_TRANSACTION_REL_CHANGES, getFormattedSql(TRANSACTION_REL_CHANGES,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_BRANCH_ARTIFACT_CHANGES, getFormattedSql(BRANCH_ARTIFACT_CHANGES,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_TRANSACTION_ARTIFACT_CHANGES, getFormattedSql(TRANSACTION_ARTIFACT_CHANGES,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_MODIFYING_TRANSACTION, getFormattedSql(MODIFYING_TRANSACTION,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_MODIFYING_BRANCHES, getFormattedSql(MODIFYING_BRANCHES, HINTS__ORDERED__FIRST_ROWS));
      }
   }

   public static final class Load {

      private Load() {
      }

      public static final String SELECT_HISTORICAL_ARTIFACTS = "SELECT_HISTORICAL_ARTIFACTS";
      public static final String SELECT_HISTORICAL_ATTRIBUTES = "SELECT_HISTORICAL_ATTRIBUTES";
      public static final String SELECT_CURRENT_ATTRIBUTES = "SELECT_CURRENT_ATTRIBUTES";
      public static final String SELECT_CURRENT_ATTRIBUTES_WITH_DELETED = "SELECT_CURRENT_ATTRIBUTES_WITH_DELETED";
      public static final String SELECT_RELATIONS = "SELECT_RELATIONS";
      public static final String SELECT_CURRENT_ARTIFACTS = "SELECT_CURRENT_ARTIFACTS";
      public static final String SELECT_CURRENT_ARTIFACTS_WITH_DELETED = "SELECT_CURRENT_ARTIFACTS_WITH_DELETED";

      private static final String SELECT_HISTORICAL_ARTIFACTS_DEFINITION =
            "SELECT%s al1.art_id, txs1.gamma_id, mod_type, txd1.*, art_type_id, guid, human_readable_id, al1.transaction_id as stripe_transaction_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= al1.transaction_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by al1.branch_id, art1.art_id, txs1.transaction_id desc";

      private static final String SELECT_HISTORICAL_ATTRIBUTES_DEFINITION =
            "SELECT%s att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id, txs1.mod_type, txd1.transaction_id, al1.transaction_id as stripe_transaction_id FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= al1.transaction_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by txd1.branch_id, att1.art_id, att1.attr_id, txd1.transaction_id desc";

      private static final String SELECT_CURRENT_ATTRIBUTES_PREFIX =
            "SELECT%s att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id, txs1.mod_type FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id AND txs1.tx_current ";

      private static final String SELECT_CURRENT_ATTRIBUTES_DEFINITION =
            SELECT_CURRENT_ATTRIBUTES_PREFIX + "= 1 order by al1.branch_id, al1.art_id";

      private static final String SELECT_CURRENT_ATTRIBUTES_WITH_DELETED_DEFINITION =
            SELECT_CURRENT_ATTRIBUTES_PREFIX + "IN (1, 3) order by al1.branch_id, al1.art_id";

      private static final String SELECT_RELATIONS_DEFINITION =
            "SELECT%s rel_link_id, a_art_id, b_art_id, rel_link_type_id, a_order, b_order, rel1.gamma_id, rationale, al1.branch_id FROM osee_join_artifact al1, osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND (al1.art_id = rel1.a_art_id OR al1.art_id = rel1.b_art_id) AND rel1.gamma_id = txs1.gamma_id AND txs1.tx_current=1 AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id";

      private static final String SELECT_CURRENT_ARTIFACTS_PREFIX =
            "SELECT%s al1.art_id, txs1.gamma_id, mod_type, txd1.*, art_type_id, guid, human_readable_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current ";

      private static final String SELECT_CURRENT_ARTIFACTS_DEFINITION = SELECT_CURRENT_ARTIFACTS_PREFIX + "= 1";

      private static final String SELECT_CURRENT_ARTIFACTS_WITH_DELETED_DEFINITION =
            SELECT_CURRENT_ARTIFACTS_PREFIX + "in (1, 2)";

      private static void addSql(Properties sqlProperties) throws OseeDataStoreException {
         sqlProperties.put(SELECT_HISTORICAL_ARTIFACTS, getFormattedSql(SELECT_HISTORICAL_ARTIFACTS_DEFINITION,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_HISTORICAL_ATTRIBUTES, getFormattedSql(SELECT_HISTORICAL_ATTRIBUTES_DEFINITION,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_CURRENT_ATTRIBUTES, getFormattedSql(SELECT_CURRENT_ATTRIBUTES_DEFINITION,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_CURRENT_ATTRIBUTES_WITH_DELETED, getFormattedSql(
               SELECT_CURRENT_ATTRIBUTES_WITH_DELETED_DEFINITION, HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_RELATIONS, getFormattedSql(SELECT_RELATIONS_DEFINITION, HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_CURRENT_ARTIFACTS, getFormattedSql(SELECT_CURRENT_ARTIFACTS_DEFINITION,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_CURRENT_ARTIFACTS_WITH_DELETED, getFormattedSql(
               SELECT_CURRENT_ARTIFACTS_WITH_DELETED_DEFINITION, HINTS__ORDERED__FIRST_ROWS));
      }
   }

   public static final class Conflicts {

      public static final String SELECT_ARTIFACT_CONFLICTS = "SELECT_ARTIFACT_CONFLICTS";
      public static final String SELECT_ATTRIBUTE_CONFLICTS = "SELECT_ATTRIBUTE_CONFLICTS";
      public static final String SELECT_HISTORIC_ATTRIBUTE_CONFLICTS = "SELECT_HISTORIC_ATTRIBUTE_CONFLICTS";

      private Conflicts() {
      }

      private static final String ARTIFACT_CONFLICT_DEFINITION =
            "SELECT%s art1.art_type_id, arv1.art_id, txs1.mod_type AS source_mod_type, txs1.gamma_id AS source_gamma, txs2.mod_type AS dest_mod_type, txs2.gamma_id AS dest_gamma FROM osee_tx_details txd1, osee_txs txs1, osee_artifact_version arv1, osee_artifact_version arv2, osee_txs txs2, osee_tx_details txd2, osee_artifact art1 WHERE txd1.tx_type = 0 AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = arv1.gamma_id and arv1.art_id = art1.art_id AND arv1.art_id = arv2.art_id AND arv2.gamma_id = txs2.gamma_id AND txs2.transaction_id = txd2.transaction_id AND txd2.branch_id = ? AND ((txs2.tx_current = 1 AND txs2.gamma_id not in (SELECT txs.gamma_id FROM osee_txs txs WHERE txs.transaction_id = ?)) OR txs2.tx_current = 2)";

      private static final String ATTRIBUTE_CONFLICT_DEFINITION =
            "SELECT%s atr1.art_id, txs1.mod_type, atr1.attr_type_id, atr1.attr_id, atr1.gamma_id AS source_gamma, atr1.value AS source_value, atr2.gamma_id AS dest_gamma, atr2.value as dest_value, txs2.mod_type AS dest_mod_type FROM osee_tx_details txd1, osee_txs txs1, osee_attribute atr1, osee_attribute atr2, osee_txs txs2, osee_tx_details txd2 WHERE txd1.tx_type = 0 AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.attr_id = atr2.attr_id AND atr2.gamma_id = txs2.gamma_id AND txs2.transaction_id = txd2.transaction_id AND txd2.branch_id = ? AND ((txs2.tx_current = 1 AND txs2.gamma_id not in (SELECT txs.gamma_id FROM osee_txs txs WHERE txs.transaction_id = ? )) OR txs2.tx_current = 2)";

      private static final String HISTORICAL_ATTRIBUTE_CONFLICTS_DEFINITION =
            "SELECT%s atr.attr_id, atr.art_id, source_gamma_id, dest_gamma_id, attr_type_id, mer.merge_branch_id, mer.dest_branch_id, value as source_value, status FROM osee_merge mer, osee_conflict con,  osee_attribute atr Where mer.commit_transaction_id = ? AND mer.merge_branch_id = con.merge_branch_id And con.source_gamma_id = atr.gamma_id AND con.status in (" + ConflictStatus.COMMITTED.getValue() + ", " + ConflictStatus.INFORMATIONAL.getValue() + " ) order by attr_id";

      private static void addSql(Properties sqlProperties) throws OseeDataStoreException {
         sqlProperties.put(SELECT_ARTIFACT_CONFLICTS, getFormattedSql(ARTIFACT_CONFLICT_DEFINITION,
               HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT));

         sqlProperties.put(SELECT_ATTRIBUTE_CONFLICTS, getFormattedSql(ATTRIBUTE_CONFLICT_DEFINITION,
               HINTS__ORDERED__INDEX__ATTRIBUTE_CONFLICT));

         sqlProperties.put(SELECT_HISTORIC_ATTRIBUTE_CONFLICTS, getFormattedSql(
               HISTORICAL_ATTRIBUTE_CONFLICTS_DEFINITION, HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_ARTIFACT_CONFLICTS, getFormattedSql(ARTIFACT_CONFLICT_DEFINITION,
               HINTS__ORDERED__INDEX__ARTIFACT_CONFLICT));
      }
   }

   public static final class Merge {

      public static final String SELECT_ARTIFACTS_ON_A_BRANCH = "SELECT_ARTIFACT_BRANCH";
      public static final String SELECT_ATTRIBUTES_ON_A_BRANCH = "SELECT_ATTRIBUTE_BRANCH";
      public static final String SELECT_REL_LINKS_ON_A_BRANCH = "SELECT_RELATIONS_BRANCH";
      public static final String UPDATE_ARTIFACT_MERGE_BRANCH = "UPDATE_MERGE_BRANCH_ARTIFACT";

      private Merge() {
      }
      public static final String UPDATE_MERGE_BRANCH_ARTIFACT_DEFINITION =
            "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT %s ?, txs.gamma_id, txs.mod_type, CASE WHEN txs.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " WHEN txs.mod_type = 5 THEN " + TxChange.ARTIFACT_DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_attribute attr, osee_txs txs, osee_tx_details det WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = attr.gamma_id AND attr.art_id = ? AND not exists (SELECT 'x' FROM osee_txs txs1, osee_attribute attr1 WHERE txs1.transaction_id = ? AND txs1.gamma_id = attr1.gamma_id AND attr1.attr_id = attr.attr_id)";

      private static final String GET_ART_IDS_FOR_BRANCH_DEFINITION =
            "SELECT%s arv.art_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version arv WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = arv.gamma_id and det.branch_id = ?";

      private static final String GET_ATTRIBUTES_FOR_BRANCH_DEFINITION =
            "SELECT%s atr.art_id, atr.attr_id FROM  osee_tx_details det,  osee_txs txs, osee_attribute atr WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = atr.gamma_id and det.branch_id = ?";

      private static final String GET_RELATIONS_FOR_BRANCH_DEFINITION =
            "SELECT%s rel.a_art_id, rel.b_art_id FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.transaction_id = txs.transaction_id and txs.gamma_id = rel.gamma_id and det.branch_id = ?";

      private static void addSql(Properties sqlProperties) throws OseeDataStoreException {
         sqlProperties.put(SELECT_ARTIFACTS_ON_A_BRANCH, getFormattedSql(GET_ART_IDS_FOR_BRANCH_DEFINITION,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_ATTRIBUTES_ON_A_BRANCH, getFormattedSql(GET_ATTRIBUTES_FOR_BRANCH_DEFINITION,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(SELECT_REL_LINKS_ON_A_BRANCH, getFormattedSql(GET_RELATIONS_FOR_BRANCH_DEFINITION,
               HINTS__ORDERED__FIRST_ROWS));

         sqlProperties.put(UPDATE_MERGE_BRANCH_ARTIFACT_DEFINITION, getFormattedSql(UPDATE_ARTIFACT_MERGE_BRANCH,
               HINTS__ORDERED__FIRST_ROWS));
      }
   }

   public static class Transaction {

      public static final String SELECT_ALL_TRANSACTIONS = "SELECT_ALL_TRANSACTIONS";
      public static final String SELECT_MAX_AS_LARGEST_TX = "SELECT_MAX_AS_LARGEST_TX";
      public static final String SELECT_MAX_AND_MIN_TX = "SELECT_MAX_AND_MIN_TX";
      public static final String SELECT_TX_GAMMAS = "SELECT_TX_GAMMAS";
      public static final String SELECT_TX_GAMMAS_RANGE = "SELECT_TX_GAMMAS_RANGE";

      public static final String SELECT_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS = "SELECT_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS";
      public static final String SELECT_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES =
            "SELECT_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES";
      public static final String SELECT_PREVIOUS_TX_NOT_CURRENT_RELATIONS = "SELECT_PREVIOUS_TX_NOT_CURRENT_RELATIONS";

      private Transaction() {
      }

      private static final String GET_ALL_TRANSACTIONS = "SELECT * FROM osee_tx_details WHERE transaction_id = ?";

      private static final String GET_MAX_AS_LARGEST_TX =
            "SELECT max(transaction_id) as largest_transaction_id FROM osee_tx_details WHERE branch_id = ?";

      private static final String GET_MAX_AND_MIN_TX =
            "SELECT max(transaction_id) AS max_id, min(transaction_id) AS min_id FROM osee_tx_details WHERE branch_id = ?";

      private static final String GET_TX_GAMMAS =
            "SELECT txs.transaction_id, txs.gamma_id FROM osee_tx_details txd, osee_txs txs WHERE txd.transaction_id = txs.transaction_id AND txd.branch_id = ? AND txd.transaction_id = ? ORDER BY txs.transaction_id, txs.gamma_id";

      private static final String GET_TX_GAMMAS_RANGE =
            "SELECT txs.transaction_id, txs.gamma_id FROM osee_tx_details txd, osee_txs txs WHERE txd.transaction_id = txs.transaction_id AND txd.branch_id = ? AND txd.transaction_id > ? AND txd.transaction_id <= ? ORDER BY txs.transaction_id, txs.gamma_id";

      private static final String GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS =
            "SELECT txs1.transaction_id, txs1.gamma_id FROM osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE arv1.art_id = ? AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue();

      private static final String GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES =
            "SELECT txs1.transaction_id, txs1.gamma_id FROM osee_attribute atr1, osee_txs txs1, osee_tx_details txd1 WHERE atr1.attr_id = ? AND atr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue();

      private static final String GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS =
            "SELECT txs1.transaction_id, txs1.gamma_id FROM osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 WHERE rel1.rel_link_id = ? AND rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue();

      private static void addSql(Properties sqlProperties) throws OseeDataStoreException {
         sqlProperties.put(SELECT_ALL_TRANSACTIONS, GET_ALL_TRANSACTIONS);

         sqlProperties.put(SELECT_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS, GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS);

         sqlProperties.put(SELECT_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES, GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES);

         sqlProperties.put(SELECT_PREVIOUS_TX_NOT_CURRENT_RELATIONS, GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS);

         sqlProperties.put(SELECT_MAX_AS_LARGEST_TX, GET_MAX_AS_LARGEST_TX);
         sqlProperties.put(SELECT_MAX_AND_MIN_TX, GET_MAX_AND_MIN_TX);
         sqlProperties.put(SELECT_TX_GAMMAS, GET_TX_GAMMAS);
         sqlProperties.put(SELECT_TX_GAMMAS_RANGE, GET_TX_GAMMAS_RANGE);
      }
   }
}
