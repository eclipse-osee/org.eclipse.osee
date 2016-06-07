/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
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
      "select gamma_id from osee_search_tags tag where not exists (select 1 from osee_attribute att where tag.gamma_id = att.gamma_id)";

   private static final String NOT_ADDRESSESED_GAMMAS =
      "select gamma_id from %s t1 where not exists (select 1 from osee_txs txs1 where t1.gamma_id = txs1.gamma_id union all select 1 from osee_txs_archived txs2 where t1.gamma_id = txs2.gamma_id)";

   private static final String EMPTY_TRANSACTIONS =
      "select branch_id, transaction_id from osee_tx_details txd where transaction_id <> 1 and not exists (select 1 from osee_txs txs1 where txs1.branch_id = txd.branch_id and txs1.transaction_id = txd.transaction_id) and not exists (select 1 from osee_txs_archived txs2 where txs2.branch_id = txd.branch_id and txs2.transaction_id = txd.transaction_id)";

   private static final String NONEXISTENT_GAMMAS = "SELECT branch_id, gamma_id FROM %s txs WHERE " + //
      "NOT EXISTS (SELECT 1 FROM osee_attribute att WHERE txs.gamma_id = att.gamma_id union all " + //
      "SELECT 1 FROM osee_artifact art WHERE txs.gamma_id = art.gamma_id union all " + //
      "SELECT 1 FROM osee_relation_link rel WHERE txs.gamma_id = rel.gamma_id)";

   private static final String DELETE_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_GAMMAS_BY_BRANCH = "DELETE FROM %s WHERE branch_id = ? and gamma_id = ?";

   private static final String DELETE_EMPTY_TRANSACTIONS =
      "DELETE FROM osee_tx_details WHERE branch_id = ? and transaction_id = ?";

   private static final String GET_INVALID_ART_REFERENCES =
      "select item.gamma_id from %s item where not exists (select 1 from osee_artifact art where art.art_id = item.%s)";

   private static final String GET_INVALID_ART_REFERENCES_ACL =
      "select item.art_id from osee_artifact_acl item where not exists (select 1 from osee_artifact art where art.art_id = item.art_id)";

   private static final String DELETE_ACL = "DELETE FROM osee_artifact_acl WHERE art_id = ?";

   private final JdbcClient jdbcClient;

   public PurgeUnusedBackingDataAndTransactions(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   private int purgeNotAddressedGammas(String tableName) throws OseeCoreException {
      String selectSql = String.format(NOT_ADDRESSESED_GAMMAS, tableName);
      return purgeGammas(selectSql, tableName);
   }

   private int purgeAddressedButNonexistentGammas(String tableName) throws OseeCoreException {
      return purgeData(String.format(NONEXISTENT_GAMMAS, tableName), String.format(DELETE_GAMMAS_BY_BRANCH, tableName),
         this::addBranchGamma);
   }

   private int purgeEmptyTransactions() throws OseeCoreException {
      return purgeData(EMPTY_TRANSACTIONS, DELETE_EMPTY_TRANSACTIONS, this::addTx);
   }

   private int deleteObsoleteTags() {
      return purgeGammas(OBSOLETE_TAGS, "osee_search_tags");
   }

   private int purgeInvalidArtfactReferences(String table, String artColumn) {
      String selectSql = String.format(GET_INVALID_ART_REFERENCES, table, artColumn);
      return purgeGammas(selectSql, table);
   }

   private int purgeInvalidArtfactReferencesAcl() {
      return purgeData(GET_INVALID_ART_REFERENCES_ACL, DELETE_ACL, this::addArt);
   }

   private int purgeGammas(String selectSql, String table) {
      return purgeData(selectSql, String.format(DELETE_GAMMAS, table), this::addGamma);
   }

   private int purgeData(String selectSql, String purgeSQL, BiConsumer<OseePreparedStatement, JdbcStatement> consumer) {
      OseePreparedStatement purgeStmt = jdbcClient.getBatchStatement(purgeSQL);
      jdbcClient.runQuery(stmt -> consumer.accept(purgeStmt, stmt), JDBC__MAX_FETCH_SIZE, selectSql);
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
      counts[i++] = purgeNotAddressedGammas("osee_artifact");
      counts[i++] = purgeNotAddressedGammas("osee_attribute");
      counts[i++] = purgeNotAddressedGammas("osee_relation_link");
      counts[i++] = purgeInvalidArtfactReferences("osee_relation_link", "a_art_id");
      counts[i++] = purgeInvalidArtfactReferences("osee_relation_link", "b_art_id");
      counts[i++] = purgeInvalidArtfactReferences("osee_attribute", "art_id");
      counts[i++] = purgeInvalidArtfactReferencesAcl();
      counts[i++] = deleteObsoleteTags();
      counts[i++] = purgeAddressedButNonexistentGammas("osee_txs");
      counts[i++] = purgeAddressedButNonexistentGammas("osee_txs_archived");
      counts[i++] = purgeEmptyTransactions();
      return counts;
   }
}