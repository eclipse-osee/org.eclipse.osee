/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.orcs.db.internal.health;

import java.util.function.BiConsumer;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;

/**
 * Purge artifact, attribute, and relation versions that are not addressed or nonexistent and purge empty transactions.
 * Additionally purge search tags referencing non-existent gammas.
 *
 * @author Ryan D. Brooks
 */
public class PurgeUnusedBackingDataAndTransactions {

   private static final String OBSOLETE_TAGS =
      "select gamma_id from osee_search_tags tag where not exists (select 1 from osee_attribute att where tag.gamma_id = att.gamma_id) %s";

   private static final String GAMMAS_CHECKED_NOT_ADDRESSED =
      "insert into osee_validate_gamma_id(gamma_id) select gamma_id from (select gamma_id, %s rn from %s where gamma_id not in (select gamma_id from osee_validate_gamma_id)) t2 where rn between 0 and 1000";
   private static final String GAMMAS_CHECKED_INVALID_ART_REFERENCES =
      "insert into osee_validate_gamma_id(gamma_id) select gamma_id from (select gamma_id, art_id, %s rn from (select distinct gamma_id, %s art_id from %s where gamma_id not in (select gamma_id from osee_validate_gamma_id)) t1 ) t2 where rn between 0 and 1000";

   private static final String NOT_ADDRESSED_GAMMAS =
      "with art as (select gamma_id from (select gamma_id, %s rn from %s where gamma_id not in (select gamma_id from osee_validate_gamma_id)) t2 where rn between 0 and 1000) " + "select gamma_id from art t1 where not exists (select 1 from osee_txs txs1 where t1.gamma_id = txs1.gamma_id union all select 1 from osee_txs_archived txs2 where t1.gamma_id = txs2.gamma_id)";

   private static final String EMPTY_TRANSACTIONS =
      "select branch_id, transaction_id from osee_tx_details txd where transaction_id <> 1 and not exists (select 1 from osee_txs txs1 where txs1.branch_id = txd.branch_id and txs1.transaction_id = txd.transaction_id) and not exists (select 1 from osee_txs_archived txs2 where txs2.branch_id = txd.branch_id and txs2.transaction_id = txd.transaction_id) and not exists (select 1 from osee_branch br where br.parent_branch_id = txd.branch_id and br.parent_transaction_id = txd.transaction_id)";

   private static final String NONEXISTENT_GAMMAS = "SELECT branch_id, gamma_id FROM %s txs WHERE " + //
      "NOT EXISTS (SELECT 1 FROM osee_attribute att WHERE txs.gamma_id = att.gamma_id union all " + //
      "SELECT 1 FROM osee_artifact art WHERE txs.gamma_id = art.gamma_id union all " + //
      "SELECT 1 FROM osee_relation_link rel WHERE txs.gamma_id = rel.gamma_id union all " + //
      "SELECT 1 FROM osee_tuple2 tup WHERE txs.gamma_id = tup.gamma_id union all " + //
      "SELECT 1 FROM osee_relation rel where txs.gamma_id = rel.gamma_id union all " + //
      "SELECT 1 from osee_branch_category cat where txs.gamma_id = cat.gamma_id )";

   private static final String DELETE_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_GAMMAS_BY_BRANCH = "DELETE FROM %s WHERE branch_id = ? and gamma_id = ?";

   private static final String DELETE_EMPTY_TRANSACTIONS =
      "DELETE FROM osee_tx_details WHERE branch_id = ? and transaction_id = ?";

   private static final String GET_INVALID_ART_REFERENCES =
      "with gammas_to_check(gamma_id, art_id) as (select gamma_id, art_id from (select gamma_id, art_id, %s rn from (select distinct gamma_id, %s art_id from %s where gamma_id not in (select gamma_id from osee_validate_gamma_id) ) t1 ) t2 where rn between 0 and 1000) " + //
         "select * from gammas_to_check gtc where not exists (select 1 from osee_artifact art where art.art_id = gtc.art_id)";

   private static final String GET_INVALID_ART_REFERENCES_ACL =
      "select item.art_id from osee_artifact_acl item where not exists (select 1 from osee_artifact art where art.art_id = item.art_id)";

   private static final String DELETE_ACL = "DELETE FROM osee_artifact_acl WHERE art_id = ?";

   private final JdbcClient jdbcClient;

   public PurgeUnusedBackingDataAndTransactions(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   private int purgeNotAddressedGammas(JdbcConnection connection, String tableName) {
      String selectSql = String.format(NOT_ADDRESSED_GAMMAS, jdbcClient.getDbType().getRowNum(), tableName);
      String gammasCheckSql =
         String.format(GAMMAS_CHECKED_NOT_ADDRESSED, jdbcClient.getDbType().getRowNum(), tableName);
      return purgeGammas(connection, selectSql, tableName, gammasCheckSql);
   }

   private int purgeAddressedButNonexistentGammas(JdbcConnection connection, String tableName) {
      return purgeData(connection, String.format(NONEXISTENT_GAMMAS, tableName),
         String.format(DELETE_GAMMAS_BY_BRANCH, tableName), this::addBranchGamma, Strings.EMPTY_STRING);
   }

   private int purgeEmptyTransactions(JdbcConnection connection) {
      return purgeData(connection, EMPTY_TRANSACTIONS, DELETE_EMPTY_TRANSACTIONS, this::addTx, Strings.EMPTY_STRING);
   }

   private int deleteObsoleteTags(JdbcConnection connection) {
      return purgeGammas(connection, String.format(OBSOLETE_TAGS, jdbcClient.getDbType().getLimitRowsReturned(1000)),
         "osee_search_tags", Strings.EMPTY_STRING);
   }

   private int purgeInvalidArtifactReferences(JdbcConnection connection, String table, String artColumn) {
      String selectSql =
         String.format(GET_INVALID_ART_REFERENCES, jdbcClient.getDbType().getRowNum(), artColumn, table);
      String gammasCheckSql =
         String.format(GAMMAS_CHECKED_INVALID_ART_REFERENCES, jdbcClient.getDbType().getRowNum(), artColumn, table);
      return purgeGammas(connection, selectSql, table, gammasCheckSql);
   }

   private int purgeInvalidArtfactReferencesAcl(JdbcConnection connection) {
      return purgeData(connection, GET_INVALID_ART_REFERENCES_ACL, DELETE_ACL, this::addArt, Strings.EMPTY_STRING);
   }

   private int purgeGammas(JdbcConnection connection, String selectSql, String table, String insertCheckedGammas) {
      return purgeData(connection, selectSql, String.format(DELETE_GAMMAS, table), this::addGamma, insertCheckedGammas);
   }

   private int purgeData(JdbcConnection connection, String selectSql, String purgeSQL,
      BiConsumer<OseePreparedStatement, JdbcStatement> consumer, String insertCheckedGammas) {
      OseePreparedStatement purgeStmt = jdbcClient.getBatchStatement(connection, purgeSQL);
      jdbcClient.runQueryWithMaxFetchSize(connection, stmt -> consumer.accept(purgeStmt, stmt), selectSql);
      if (!insertCheckedGammas.isEmpty()) {
         jdbcClient.runPreparedUpdate(insertCheckedGammas);
      }
      return purgeStmt.execute();
   }

   private void addBranchGamma(OseePreparedStatement purgeStmt, JdbcStatement stmt) {
      purgeStmt.addToBatch(stmt.getLong("branch_id"), stmt.getLong("gamma_id"));
   }

   private void addTx(OseePreparedStatement purgeStmt, JdbcStatement stmt) {
      purgeStmt.addToBatch(stmt.getLong("branch_id"), stmt.getLong("transaction_id"));
   }

   private void addGamma(OseePreparedStatement purgeStmt, JdbcStatement stmt) {
      purgeStmt.addToBatch(stmt.getLong("gamma_id"));
   }

   private void addArt(OseePreparedStatement purgeStmt, JdbcStatement stmt) {
      purgeStmt.addToBatch(stmt.getLong("art_id"));
   }

   public int[] purge() {
      int i = 0;
      int[] counts = new int[11];
      try (JdbcConnection connection = jdbcClient.getConnection()) {
         counts[i++] = purgeNotAddressedGammas(connection, "osee_artifact");
         counts[i++] = purgeNotAddressedGammas(connection, "osee_attribute");
         counts[i++] = purgeNotAddressedGammas(connection, "osee_relation_link");
         counts[i++] = purgeNotAddressedGammas(connection, "osee_relation");
         counts[i++] = purgeInvalidArtifactReferences(connection, "osee_relation_link", "a_art_id");
         counts[i++] = purgeInvalidArtifactReferences(connection, "osee_relation_link", "b_art_id");
         counts[i++] = purgeInvalidArtifactReferences(connection, "osee_relation", "a_art_id");
         counts[i++] = purgeInvalidArtifactReferences(connection, "osee_relation", "b_art_id");
         counts[i++] = purgeInvalidArtifactReferences(connection, "osee_attribute", "art_id");
         counts[i++] = purgeInvalidArtfactReferencesAcl(connection);
         counts[i++] = deleteObsoleteTags(connection);
         /**
          * TODO:Need to come up with efficient scheme to purge rows in osee_txs and txs_archived
          */
         //counts[i++] = purgeAddressedButNonexistentGammas(connection, "osee_txs");
         //counts[i++] = purgeAddressedButNonexistentGammas(connection, "osee_txs_archived");
         //counts[i++] = purgeEmptyTransactions(connection);
      }
      return counts;
   }
}