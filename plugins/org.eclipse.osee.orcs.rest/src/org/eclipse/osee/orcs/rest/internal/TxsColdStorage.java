/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Manages cold storage archival of transaction data for committed/rebaselined branches. Exports osee_txs_archived,
 * osee_tx_details, and osee_branch rows to compressed binary files, then purges them from the database.
 */
public class TxsColdStorage {

   private static final String MAGIC = "OSEE_TXS_COLD_V1";
   private static final int SCHEMA_VERSION = 2;
   private static final DateTimeFormatter FILE_DATE_FORMAT =
      DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneOffset.UTC);

   // @formatter:off
   private static final String SELECT_ELIGIBLE_BRANCHES =
      "SELECT b.BRANCH_ID, b.BRANCH_NAME, b.BRANCH_STATE, b.ARCHIVED FROM osee_branch b " +
      "WHERE b.BRANCH_STATE IN (?, ?, ?) " +
      "AND b.BRANCH_ID NOT IN (SELECT BRANCH_ID FROM osee_txs_cold_storage) " +
      "AND (EXISTS (SELECT 1 FROM osee_txs_archived t WHERE t.BRANCH_ID = b.BRANCH_ID) " +
      " OR EXISTS (SELECT 1 FROM osee_txs t2 WHERE t2.BRANCH_ID = b.BRANCH_ID)) " +
      "AND b.BRANCH_ID IN (" +
      "  SELECT txd.BRANCH_ID FROM osee_tx_details txd " +
      "  WHERE txd.BRANCH_ID = b.BRANCH_ID " +
      "  GROUP BY txd.BRANCH_ID " +
      "  HAVING MAX(txd.TIME) < ?" +
      ") " +
      "AND NOT EXISTS (" +
      "  SELECT 1 FROM osee_branch child " +
      "  WHERE (child.BASELINE_TRANSACTION_ID IN (SELECT txd2.TRANSACTION_ID FROM osee_tx_details txd2 WHERE txd2.BRANCH_ID = b.BRANCH_ID) " +
      "     OR child.PARENT_TRANSACTION_ID IN (SELECT txd3.TRANSACTION_ID FROM osee_tx_details txd3 WHERE txd3.BRANCH_ID = b.BRANCH_ID)) " +
      "  AND child.BRANCH_ID != b.BRANCH_ID" +
      ") " +
      "ORDER BY b.BRANCH_ID FETCH FIRST ? ROWS ONLY";

   private static final String SELECT_TXS_ARCHIVED_FOR_BRANCH =
      "SELECT BRANCH_ID, GAMMA_ID, TRANSACTION_ID, TX_CURRENT, MOD_TYPE, APP_ID " +
      "FROM osee_txs_archived WHERE BRANCH_ID = ? ORDER BY TRANSACTION_ID, GAMMA_ID";

   private static final String SELECT_TXS_FOR_BRANCH =
      "SELECT BRANCH_ID, GAMMA_ID, TRANSACTION_ID, TX_CURRENT, MOD_TYPE, APP_ID " +
      "FROM osee_txs WHERE BRANCH_ID = ? ORDER BY TRANSACTION_ID, GAMMA_ID";

   private static final String SELECT_TX_DETAILS_FOR_BRANCH =
      "SELECT BRANCH_ID, TRANSACTION_ID, AUTHOR, TIME, OSEE_COMMENT, TX_TYPE, COMMIT_ART_ID, BUILD_ID " +
      "FROM osee_tx_details WHERE BRANCH_ID = ? ORDER BY TRANSACTION_ID";

   private static final String SELECT_GAMMAS_ABOVE_BASELINE =
      "SELECT GAMMA_ID FROM osee_txs WHERE BRANCH_ID = ? AND TRANSACTION_ID > ? " +
      "UNION SELECT GAMMA_ID FROM osee_txs_archived WHERE BRANCH_ID = ? AND TRANSACTION_ID > ?";

   private static final String IS_GAMMA_ONLY_ON_BRANCH =
      "SELECT COUNT(1) FROM (" +
      "SELECT gamma_id FROM osee_txs WHERE gamma_id = ? AND branch_id <> ? " +
      "UNION ALL SELECT gamma_id FROM osee_txs_archived WHERE gamma_id = ? AND branch_id <> ?" +
      ") t1";

   private static final String SELECT_ARTIFACT_BY_GAMMA =
      "SELECT ART_ID, GAMMA_ID, ART_TYPE_ID, GUID FROM osee_artifact WHERE GAMMA_ID = ?";

   private static final String SELECT_ATTRIBUTE_BY_GAMMA =
      "SELECT ATTR_ID, GAMMA_ID, ART_ID, ATTR_TYPE_ID, VALUE, URI FROM osee_attribute WHERE GAMMA_ID = ?";

   private static final String SELECT_RELATION_BY_GAMMA =
      "SELECT REL_LINK_TYPE_ID, A_ART_ID, B_ART_ID, GAMMA_ID, REL_LINK_ID, RATIONALE FROM osee_relation_link WHERE GAMMA_ID = ?";

   private static final String SELECT_RELATION2_BY_GAMMA =
      "SELECT REL_TYPE, A_ART_ID, B_ART_ID, REL_ART_ID, REL_ORDER, GAMMA_ID FROM osee_relation WHERE GAMMA_ID = ?";

   private static final String SELECT_SEARCH_TAGS_BY_GAMMA =
      "SELECT GAMMA_ID, CODED_TAG_ID FROM osee_search_tags WHERE GAMMA_ID = ?";

   private static final String SELECT_BRANCH_ROW =
      "SELECT BRANCH_ID, BRANCH_TYPE, BRANCH_STATE, BRANCH_NAME, PARENT_BRANCH_ID, " +
      "PARENT_TRANSACTION_ID, BASELINE_TRANSACTION_ID, ASSOCIATED_ART_ID, ARCHIVED, INHERIT_ACCESS_CONTROL " +
      "FROM osee_branch WHERE BRANCH_ID = ?";

   private static final String INSERT_CATALOG =
      "INSERT INTO osee_txs_cold_storage (BRANCH_ID, BRANCH_NAME, EXPORT_FILE, EXPORT_DATE, TXS_ROW_COUNT, TX_DETAILS_ROW_COUNT, BRANCH_STATE) " +
      "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?)";

   private static final String INSERT_BRANCH =
      "INSERT INTO osee_branch (BRANCH_ID, BRANCH_TYPE, BRANCH_STATE, BRANCH_NAME, PARENT_BRANCH_ID, " +
      "PARENT_TRANSACTION_ID, BASELINE_TRANSACTION_ID, ASSOCIATED_ART_ID, ARCHIVED, INHERIT_ACCESS_CONTROL) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

   private static final String INSERT_TX_DETAILS =
      "INSERT INTO osee_tx_details (BRANCH_ID, TRANSACTION_ID, AUTHOR, TIME, OSEE_COMMENT, TX_TYPE, COMMIT_ART_ID, BUILD_ID) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

   private static final String INSERT_TXS_ARCHIVED =
      "INSERT INTO osee_txs_archived (BRANCH_ID, GAMMA_ID, TRANSACTION_ID, TX_CURRENT, MOD_TYPE, APP_ID) " +
      "VALUES (?, ?, ?, ?, ?, ?)";

   private static final String SELECT_CATALOG_FOR_BRANCH =
      "SELECT BRANCH_ID, BRANCH_NAME, EXPORT_FILE, EXPORT_DATE, TXS_ROW_COUNT, TX_DETAILS_ROW_COUNT, BRANCH_STATE " +
      "FROM osee_txs_cold_storage WHERE BRANCH_ID = ?";

   private static final String SELECT_ALL_CATALOG =
      "SELECT BRANCH_ID, BRANCH_NAME, EXPORT_FILE, EXPORT_DATE, TXS_ROW_COUNT, TX_DETAILS_ROW_COUNT, BRANCH_STATE " +
      "FROM osee_txs_cold_storage ORDER BY EXPORT_DATE DESC";

   private static final String DELETE_CATALOG = "DELETE FROM osee_txs_cold_storage WHERE BRANCH_ID = ?";

   private static final String UPDATE_BRANCH_BASELINE =
      "UPDATE osee_branch SET BASELINE_TRANSACTION_ID = ?, PARENT_TRANSACTION_ID = ? WHERE BRANCH_ID = ?";
   // @formatter:on

   private final JdbcClient jdbcClient;
   private final OrcsApi orcsApi;

   public TxsColdStorage(JdbcClient jdbcClient, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.orcsApi = orcsApi;
   }

   /**
    * Archives a single branch to cold storage (export only, no DB deletion). Used before an explicit purgeBranch call
    * so the data is preserved in cold storage before the purge removes it from the database.
    *
    * @param branchId the branch to archive
    * @return XResultData with details of what was archived
    */
   public XResultData archiveSingleBranch(BranchId branchId) {
      XResultData results = new XResultData();

      String coldPath = ColdStorageUtil.getColdStoragePath();
      if (coldPath == null) {
         results.error("Unable to determine server data path for cold storage");
         return results;
      }

      // Get branch info
      String[] branchName = {""};
      int[] branchState = {0};
      boolean[] isArchived = {false};
      long[] baselineTxId = {0L};
      jdbcClient.runQuery(stmt -> {
         branchName[0] = stmt.getString("BRANCH_NAME");
         branchState[0] = stmt.getInt("BRANCH_STATE");
         isArchived[0] = stmt.getInt("ARCHIVED") == 1;
         baselineTxId[0] = stmt.getLong("BASELINE_TRANSACTION_ID");
      }, SELECT_BRANCH_ROW, branchId);

      if (branchName[0].isEmpty()) {
         results.errorf("Branch %s not found", branchId);
         return results;
      }

      String fileName =
         "txs_cold_branch_" + branchId.getIdString() + "_" + FILE_DATE_FORMAT.format(Instant.now()) + ".gz";
      String filePath = coldPath + File.separator + fileName;

      try {
         int totalTxsRows = 0;
         int totalTxDetailsRows = 0;
         int totalOrphanedGammas = 0;

         try (FileOutputStream fos = new FileOutputStream(filePath);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            DataOutputStream dos = new DataOutputStream(gzos)) {

            dos.writeUTF(MAGIC);
            dos.writeInt(SCHEMA_VERSION);
            dos.writeLong(System.currentTimeMillis());
            dos.writeInt(1);

            long branchIdLong = branchId.getId();

            dos.writeUTF("BRANCH_START");
            dos.writeLong(branchIdLong);

            dos.writeUTF("BRANCH_DATA");
            List<Object[]> branchRows = new ArrayList<>();
            jdbcClient.runQuery(stmt -> {
               branchRows.add(readBranchRowFromDb(stmt));
            }, SELECT_BRANCH_ROW, branchId);
            dos.writeInt(branchRows.size());
            for (Object[] row : branchRows) {
               writeBranchRow(dos, row);
            }

            // Write tx_details rows
            dos.writeUTF("TX_DETAILS_DATA");
            List<TxDetailsRow> txDetailsRows = new ArrayList<>();
            jdbcClient.runQuery(stmt -> {
               txDetailsRows.add(new TxDetailsRow(stmt));
            }, SELECT_TX_DETAILS_FOR_BRANCH, branchId);
            dos.writeInt(txDetailsRows.size());
            for (TxDetailsRow row : txDetailsRows) {
               row.writeTo(dos);
            }

            // Write txs rows (from osee_txs_archived if branch is archived, osee_txs otherwise)
            dos.writeUTF("TXS_ARCHIVED_DATA");
            List<TxsArchivedRow> txsRows = new ArrayList<>();
            String txsQuery = isArchived[0] ? SELECT_TXS_ARCHIVED_FOR_BRANCH : SELECT_TXS_FOR_BRANCH;
            jdbcClient.runQuery(stmt -> {
               txsRows.add(new TxsArchivedRow(stmt));
            }, txsQuery, branchId);
            dos.writeInt(txsRows.size());
            for (TxsArchivedRow row : txsRows) {
               row.writeTo(dos);
            }

            // Collect gammas above baseline that will become orphaned after purge
            List<Long> orphanedGammas = new ArrayList<>();
            List<Long> impactedGammas = new ArrayList<>();
            jdbcClient.runQuery(stmt -> impactedGammas.add(stmt.getLong("GAMMA_ID")),
               SELECT_GAMMAS_ABOVE_BASELINE, branchId, baselineTxId[0], branchId, baselineTxId[0]);
            for (Long gamma : impactedGammas) {
               int otherRefs = jdbcClient.fetch(0, IS_GAMMA_ONLY_ON_BRANCH, gamma, branchId, gamma, branchId);
               if (otherRefs == 0) {
                  orphanedGammas.add(gamma);
               }
            }

            // Write orphaned backing data: artifacts
            dos.writeUTF("ARTIFACTS_DATA");
            List<Object[]> artRows = new ArrayList<>();
            for (Long gamma : orphanedGammas) {
               jdbcClient.runQuery(stmt -> {
                  artRows.add(new Object[] {
                     stmt.getLong("ART_ID"), stmt.getLong("GAMMA_ID"),
                     stmt.getLong("ART_TYPE_ID"), stmt.getString("GUID")});
               }, SELECT_ARTIFACT_BY_GAMMA, gamma);
            }
            dos.writeInt(artRows.size());
            for (Object[] row : artRows) {
               dos.writeLong((long) row[0]);
               dos.writeLong((long) row[1]);
               dos.writeLong((long) row[2]);
               dos.writeUTF((String) row[3]);
            }

            // Write orphaned backing data: attributes
            dos.writeUTF("ATTRIBUTES_DATA");
            List<Object[]> attrRows = new ArrayList<>();
            for (Long gamma : orphanedGammas) {
               jdbcClient.runQuery(stmt -> {
                  attrRows.add(new Object[] {
                     stmt.getLong("ATTR_ID"), stmt.getLong("GAMMA_ID"),
                     stmt.getLong("ART_ID"), stmt.getLong("ATTR_TYPE_ID"),
                     stmt.getString("VALUE"), stmt.getString("URI")});
               }, SELECT_ATTRIBUTE_BY_GAMMA, gamma);
            }
            dos.writeInt(attrRows.size());
            for (Object[] row : attrRows) {
               dos.writeLong((long) row[0]);
               dos.writeLong((long) row[1]);
               dos.writeLong((long) row[2]);
               dos.writeLong((long) row[3]);
               dos.writeUTF(row[4] != null ? (String) row[4] : "");
               dos.writeUTF(row[5] != null ? (String) row[5] : "");
            }

            // Write orphaned backing data: relations (legacy)
            dos.writeUTF("RELATIONS_DATA");
            List<Object[]> relRows = new ArrayList<>();
            for (Long gamma : orphanedGammas) {
               jdbcClient.runQuery(stmt -> {
                  relRows.add(new Object[] {
                     stmt.getLong("REL_LINK_TYPE_ID"), stmt.getLong("A_ART_ID"),
                     stmt.getLong("B_ART_ID"), stmt.getLong("GAMMA_ID"),
                     stmt.getLong("REL_LINK_ID"), stmt.getString("RATIONALE")});
               }, SELECT_RELATION_BY_GAMMA, gamma);
            }
            dos.writeInt(relRows.size());
            for (Object[] row : relRows) {
               dos.writeLong((long) row[0]);
               dos.writeLong((long) row[1]);
               dos.writeLong((long) row[2]);
               dos.writeLong((long) row[3]);
               dos.writeLong((long) row[4]);
               dos.writeUTF(row[5] != null ? (String) row[5] : "");
            }

            // Write orphaned backing data: relations2 (new format)
            dos.writeUTF("RELATIONS2_DATA");
            List<Object[]> rel2Rows = new ArrayList<>();
            for (Long gamma : orphanedGammas) {
               jdbcClient.runQuery(stmt -> {
                  rel2Rows.add(new Object[] {
                     stmt.getLong("REL_TYPE"), stmt.getLong("A_ART_ID"),
                     stmt.getLong("B_ART_ID"), stmt.getLong("REL_ART_ID"),
                     stmt.getInt("REL_ORDER"), stmt.getLong("GAMMA_ID")});
               }, SELECT_RELATION2_BY_GAMMA, gamma);
            }
            dos.writeInt(rel2Rows.size());
            for (Object[] row : rel2Rows) {
               dos.writeLong((long) row[0]);
               dos.writeLong((long) row[1]);
               dos.writeLong((long) row[2]);
               dos.writeLong((long) row[3]);
               dos.writeInt((int) row[4]);
               dos.writeLong((long) row[5]);
            }

            // Write orphaned search tags
            dos.writeUTF("SEARCH_TAGS_DATA");
            List<long[]> tagRows = new ArrayList<>();
            for (Long gamma : orphanedGammas) {
               jdbcClient.runQuery(stmt -> {
                  tagRows.add(new long[] {stmt.getLong("GAMMA_ID"), stmt.getLong("CODED_TAG_ID")});
               }, SELECT_SEARCH_TAGS_BY_GAMMA, gamma);
            }
            dos.writeInt(tagRows.size());
            for (long[] row : tagRows) {
               dos.writeLong(row[0]);
               dos.writeLong(row[1]);
            }

            dos.writeUTF("BRANCH_END");

            totalTxsRows = txsRows.size();
            totalTxDetailsRows = txDetailsRows.size();
            totalOrphanedGammas = orphanedGammas.size();
         }

         long fileSize = new File(filePath).length();

         // Insert catalog row (branch will be deleted by purgeBranch after this returns)
         jdbcClient.runPreparedUpdate(INSERT_CATALOG, branchId.getId(), branchName[0], fileName, totalTxsRows,
            totalTxDetailsRows, branchState[0]);

         results.logf("Archived branch %d (%s) to cold storage: %s (%d bytes, %d txs rows, %d tx_details rows, %d orphaned gammas archived)",
            branchId.getId(), branchName[0], fileName, fileSize, totalTxsRows, totalTxDetailsRows, totalOrphanedGammas);

      } catch (IOException ex) {
         results.errorf("Failed to write cold storage file: %s", ex.getMessage());
      }

      return results;
   }

   /**
    * Archives eligible branches to cold storage. Finds branches in committed, rebaselined, or deleted state whose last
    * transaction is older than retentionDays, exports their data to a compressed binary file, and purges them from the
    * database. Each branch is archived and purged atomically — the catalog row is inserted before the purge so that a
    * crash between steps leaves recoverable state.
    *
    * @param limit maximum number of branches to archive in this invocation
    * @param retentionDays minimum age (in days) since last transaction before a branch is eligible
    * @return XResultData with details of what was archived
    */
   public XResultData archiveBranches(int limit, int retentionDays) {
      XResultData results = new XResultData();

      String coldPath = ColdStorageUtil.getColdStoragePath();
      if (coldPath == null) {
         results.error("Unable to determine server data path for cold storage");
         return results;
      }

      Timestamp cutoffDate = new Timestamp(System.currentTimeMillis() - (long) retentionDays * 24 * 60 * 60 * 1000);

      List<BranchInfo> eligibleBranches = new ArrayList<>();
      jdbcClient.runQuery(stmt -> {
         eligibleBranches.add(
            new BranchInfo(stmt.getLong("BRANCH_ID"), stmt.getString("BRANCH_NAME"), stmt.getInt("BRANCH_STATE"),
               stmt.getInt("ARCHIVED") == 1));
      }, SELECT_ELIGIBLE_BRANCHES, BranchState.COMMITTED.getId(), BranchState.REBASELINED.getId(),
         BranchState.DELETED.getId(), cutoffDate, limit);

      if (eligibleBranches.isEmpty()) {
         results.log("No eligible branches found for cold storage archival");
         return results;
      }

      results.logf("Found %d eligible branches for cold storage", eligibleBranches.size());

      String fileName = "txs_cold_" + FILE_DATE_FORMAT.format(Instant.now()) + ".gz";
      String filePath = coldPath + File.separator + fileName;

      try {
         int totalTxsRows = 0;
         int totalTxDetailsRows = 0;

         try (FileOutputStream fos = new FileOutputStream(filePath);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            DataOutputStream dos = new DataOutputStream(gzos)) {

            // Write file header
            dos.writeUTF(MAGIC);
            dos.writeInt(SCHEMA_VERSION);
            dos.writeLong(System.currentTimeMillis());
            dos.writeInt(eligibleBranches.size());

            for (BranchInfo branchInfo : eligibleBranches) {
               long branchId = branchInfo.branchId;

               // Write branch section marker
               dos.writeUTF("BRANCH_START");
               dos.writeLong(branchId);

               // Write osee_branch row
               dos.writeUTF("BRANCH_DATA");
               List<Object[]> branchRows = new ArrayList<>();
               jdbcClient.runQuery(stmt -> {
                  branchRows.add(readBranchRowFromDb(stmt));
               }, SELECT_BRANCH_ROW, branchId);
               dos.writeInt(branchRows.size());
               for (Object[] row : branchRows) {
                  writeBranchRow(dos, row);
               }

               // Write osee_tx_details rows
               dos.writeUTF("TX_DETAILS_DATA");
               List<TxDetailsRow> txDetailsRows = new ArrayList<>();
               jdbcClient.runQuery(stmt -> {
                  txDetailsRows.add(new TxDetailsRow(stmt));
               }, SELECT_TX_DETAILS_FOR_BRANCH, branchId);
               dos.writeInt(txDetailsRows.size());
               for (TxDetailsRow row : txDetailsRows) {
                  row.writeTo(dos);
               }

               // Write txs rows (from osee_txs_archived if branch is archived, osee_txs otherwise)
               dos.writeUTF("TXS_ARCHIVED_DATA");
               List<TxsArchivedRow> txsRows = new ArrayList<>();
               String txsQuery = branchInfo.isArchived ? SELECT_TXS_ARCHIVED_FOR_BRANCH : SELECT_TXS_FOR_BRANCH;
               jdbcClient.runQuery(stmt -> {
                  txsRows.add(new TxsArchivedRow(stmt));
               }, txsQuery, branchId);
               dos.writeInt(txsRows.size());
               for (TxsArchivedRow row : txsRows) {
                  row.writeTo(dos);
               }

               // Write empty backing data sections to maintain consistent file format
               dos.writeUTF("ARTIFACTS_DATA");
               dos.writeInt(0);
               dos.writeUTF("ATTRIBUTES_DATA");
               dos.writeInt(0);
               dos.writeUTF("RELATIONS_DATA");
               dos.writeInt(0);
               dos.writeUTF("RELATIONS2_DATA");
               dos.writeInt(0);
               dos.writeUTF("SEARCH_TAGS_DATA");
               dos.writeInt(0);

               dos.writeUTF("BRANCH_END");

               totalTxsRows += txsRows.size();
               totalTxDetailsRows += txDetailsRows.size();

               results.logf("  Branch %d (%s): %d txs rows, %d tx_details rows", branchId, branchInfo.branchName,
                  txsRows.size(), txDetailsRows.size());
            }
         }

         // Now purge from DB and insert catalog rows.
         // Insert the catalog row BEFORE purging so that a crash between these steps leaves recoverable state.
         long fileSize = new File(filePath).length();

         for (BranchInfo branchInfo : eligibleBranches) {
            long branchId = branchInfo.branchId;

            String countTable = branchInfo.isArchived ? "osee_txs_archived" : "osee_txs";
            int txsRowCount =
               jdbcClient.fetch(0, "SELECT COUNT(1) FROM " + countTable + " WHERE BRANCH_ID = ?", branchId);
            int txDetailsRowCount =
               jdbcClient.fetch(0, "SELECT COUNT(1) FROM osee_tx_details WHERE BRANCH_ID = ?", branchId);

            // Insert catalog row BEFORE purge to ensure crash-recovery
            jdbcClient.runPreparedUpdate(INSERT_CATALOG, branchId, branchInfo.branchName, fileName, txsRowCount,
               txDetailsRowCount, branchInfo.branchState);

            try {
               // Delegate to existing PurgeBranchDatabaseCallable via OrcsBranch
               orcsApi.getBranchOps().purgeBranch(BranchId.valueOf(branchId), false).call();
            } catch (Exception ex) {
               results.errorf("Failed to purge branch %d (%s): %s — aborting remaining branches",
                  branchId, branchInfo.branchName, ex.getMessage());
               break;
            }
         }

         results.logf("Archive complete: %s (%d bytes, %d branches, %d total txs rows, %d total tx_details rows)",
            fileName, fileSize, eligibleBranches.size(), totalTxsRows, totalTxDetailsRows);

      } catch (IOException ex) {
         results.errorf("Failed to write cold storage file: %s", ex.getMessage());
      }

      return results;
   }

   /**
    * Restores a single branch from cold storage back into osee_branch, osee_tx_details, and osee_txs_archived.
    *
    * @param branchId the branch to restore
    * @return XResultData with details of what was restored
    */
   public XResultData restoreBranch(BranchId branchId) {
      XResultData results = new XResultData();

      String coldPath = ColdStorageUtil.getColdStoragePath();
      if (coldPath == null) {
         results.error("Unable to determine server data path for cold storage");
         return results;
      }

      // Look up catalog entry
      String[] exportFile = {null};
      jdbcClient.runQuery(stmt -> {
         exportFile[0] = stmt.getString("EXPORT_FILE");
      }, SELECT_CATALOG_FOR_BRANCH, branchId);

      if (exportFile[0] == null) {
         results.errorf("Branch %s not found in cold storage catalog", branchId);
         return results;
      }

      String filePath = coldPath + File.separator + exportFile[0];
      if (!new File(filePath).exists()) {
         results.errorf("Cold storage file not found: %s", filePath);
         return results;
      }

      try (FileInputStream fis = new FileInputStream(filePath);
         GZIPInputStream gzis = new GZIPInputStream(fis);
         DataInputStream dis = new DataInputStream(gzis)) {

         // Read and validate header
         String magic = dis.readUTF();
         if (!MAGIC.equals(magic)) {
            results.errorf("Invalid cold storage file format: %s", magic);
            return results;
         }
         int version = dis.readInt();
         if (version != SCHEMA_VERSION) {
            results.errorf("Unsupported schema version: %d", version);
            return results;
         }
         dis.readLong(); // export timestamp
         int branchCount = dis.readInt();

         long targetBranchId = branchId.getId();
         boolean found = false;

         for (int b = 0; b < branchCount; b++) {
            dis.readUTF(); // BRANCH_START
            long fileBranchId = dis.readLong();

            if (fileBranchId == targetBranchId) {
               found = true;
               restoreBranchData(dis, results);
               break;
            } else {
               skipBranchData(dis);
            }
         }

         if (!found) {
            results.errorf("Branch %s not found in archive file %s", branchId, exportFile[0]);
            return results;
         }

         // Remove catalog entry
         jdbcClient.runPreparedUpdate(DELETE_CATALOG, branchId);

         int remainingRefs =
            jdbcClient.fetch(0, "SELECT COUNT(1) FROM osee_txs_cold_storage WHERE EXPORT_FILE = ?", exportFile[0]);
         if (remainingRefs == 0) {
            results.logf("No remaining branches reference file %s - file can be cleaned up manually", exportFile[0]);
         }

         results.logf("Successfully restored branch %s from cold storage", branchId);

      } catch (IOException ex) {
         results.errorf("Failed to read cold storage file: %s", ex.getMessage());
      }

      return results;
   }

   /**
    * Lists all branches currently in cold storage.
    */
   public XResultData listColdStoredBranches() {
      XResultData results = new XResultData();
      List<String> entries = new ArrayList<>();

      jdbcClient.runQuery(stmt -> {
         entries.add(
            String.format("Branch %d (%s) - state: %d, file: %s, exported: %s, txs_rows: %d, tx_details_rows: %d",
               stmt.getLong("BRANCH_ID"), stmt.getString("BRANCH_NAME"), stmt.getInt("BRANCH_STATE"),
               stmt.getString("EXPORT_FILE"), stmt.getTimestamp("EXPORT_DATE").toString(),
               stmt.getLong("TXS_ROW_COUNT"), stmt.getLong("TX_DETAILS_ROW_COUNT")));
      }, SELECT_ALL_CATALOG);

      if (entries.isEmpty()) {
         results.log("No branches in cold storage");
      } else {
         results.logf("%d branches in cold storage:", entries.size());
         for (String entry : entries) {
            results.log("  " + entry);
         }
      }
      return results;
   }

   /**
    * Permanently discards a branch from cold storage. Removes the catalog entry and deletes the archive file if no
    * other branches reference it.
    */
   public XResultData purgeColdBranch(BranchId branchId) {
      XResultData results = new XResultData();

      String[] exportFile = {null};
      jdbcClient.runQuery(stmt -> {
         exportFile[0] = stmt.getString("EXPORT_FILE");
      }, SELECT_CATALOG_FOR_BRANCH, branchId);

      if (exportFile[0] == null) {
         results.errorf("Branch %s not found in cold storage catalog", branchId);
         return results;
      }

      jdbcClient.runPreparedUpdate(DELETE_CATALOG, branchId);

      int remainingRefs =
         jdbcClient.fetch(0, "SELECT COUNT(1) FROM osee_txs_cold_storage WHERE EXPORT_FILE = ?", exportFile[0]);
      if (remainingRefs == 0) {
         String coldPath = ColdStorageUtil.getColdStoragePath();
         if (coldPath != null) {
            File file = new File(coldPath + File.separator + exportFile[0]);
            if (file.exists()) {
               file.delete();
               results.logf("Deleted archive file %s (no remaining references)", exportFile[0]);
            }
         }
      }

      results.logf("Purged branch %s from cold storage catalog", branchId);
      return results;
   }

   /**
    * Generates a zip file containing SQL INSERT statements that would be executed to restore a branch from cold
    * storage.
    */
   public void previewBranch(BranchId branchId, java.util.zip.ZipOutputStream zipOut) throws IOException {
      String coldPath = ColdStorageUtil.getColdStoragePath();
      if (coldPath == null) {
         throw new IOException("Unable to determine cold storage path");
      }

      String[] exportFile = {null};
      jdbcClient.runQuery(stmt -> {
         exportFile[0] = stmt.getString("EXPORT_FILE");
      }, SELECT_CATALOG_FOR_BRANCH, branchId);

      if (exportFile[0] == null) {
         throw new IOException("Branch " + branchId + " not found in cold storage catalog");
      }

      String filePath = coldPath + File.separator + exportFile[0];

      try (FileInputStream fis = new FileInputStream(filePath);
         GZIPInputStream gzis = new GZIPInputStream(fis);
         DataInputStream dis = new DataInputStream(gzis)) {

         String magic = dis.readUTF();
         if (!MAGIC.equals(magic)) {
            throw new IOException("Invalid file format: " + magic);
         }
         dis.readInt(); // version
         dis.readLong(); // timestamp
         int branchCount = dis.readInt();

         long targetBranchId = branchId.getId();

         for (int b = 0; b < branchCount; b++) {
            dis.readUTF(); // BRANCH_START
            long fileBranchId = dis.readLong();

            if (fileBranchId == targetBranchId) {
               writePreviewSqlForBranch(dis, zipOut);
               return;
            } else {
               skipBranchData(dis);
            }
         }
         throw new IOException("Branch " + branchId + " not found in archive file");
      }
   }

   private void writePreviewSqlForBranch(DataInputStream dis, java.util.zip.ZipOutputStream zipOut) throws IOException {
      java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(zipOut));

      // BRANCH_DATA
      dis.readUTF(); // "BRANCH_DATA"
      int branchRowCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_branch.sql"));
      for (int i = 0; i < branchRowCount; i++) {
         Object[] row = readBranchRowFromFile(dis);
         writer.printf(
            "INSERT INTO osee_branch (BRANCH_ID, BRANCH_TYPE, BRANCH_STATE, BRANCH_NAME, PARENT_BRANCH_ID, PARENT_TRANSACTION_ID, BASELINE_TRANSACTION_ID, ASSOCIATED_ART_ID, ARCHIVED, INHERIT_ACCESS_CONTROL) VALUES (%d, %d, %d, '%s', %d, %d, %d, %d, %d, %d);%n",
            row[0], (short) row[1], (short) row[2], ColdStorageUtil.escapeSql((String) row[3]), row[4], row[5], row[6],
            row[7], (short) row[8], (short) row[9]);
      }
      writer.flush();
      zipOut.closeEntry();

      // TX_DETAILS_DATA
      dis.readUTF(); // "TX_DETAILS_DATA"
      int txDetailsCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_tx_details.sql"));
      for (int i = 0; i < txDetailsCount; i++) {
         Object[] row = readTxDetailsRow(dis);
         Timestamp ts = (Timestamp) row[3];
         writer.printf(
            "INSERT INTO osee_tx_details (BRANCH_ID, TRANSACTION_ID, AUTHOR, TIME, OSEE_COMMENT, TX_TYPE, COMMIT_ART_ID, BUILD_ID) VALUES (%d, %d, %d, '%s', '%s', %d, %d, %d);%n",
            row[0], row[1], row[2], ColdStorageUtil.formatTimestamp(ts), ColdStorageUtil.escapeSql((String) row[4]),
            (short) row[5], row[6], row[7]);
      }
      writer.flush();
      zipOut.closeEntry();

      // TXS_ARCHIVED_DATA
      dis.readUTF(); // "TXS_ARCHIVED_DATA"
      int txsCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_txs_archived.sql"));
      for (int i = 0; i < txsCount; i++) {
         Object[] row = readTxsArchivedRow(dis);
         writer.printf(
            "INSERT INTO osee_txs_archived (BRANCH_ID, GAMMA_ID, TRANSACTION_ID, TX_CURRENT, MOD_TYPE, APP_ID) VALUES (%d, %d, %d, %d, %d, %d);%n",
            row[0], row[1], row[2], (short) row[3], (short) row[4], row[5]);
      }
      writer.flush();
      zipOut.closeEntry();

      // Handle backing data sections — old batch-sweep archives go straight to BRANCH_END
      // after TXS_ARCHIVED_DATA, so check the next marker before assuming backing data exists.
      String marker = dis.readUTF();
      if ("BRANCH_END".equals(marker)) {
         return;
      }

      // marker should be "ARTIFACTS_DATA"
      int artCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_artifact.sql"));
      for (int i = 0; i < artCount; i++) {
         long artId = dis.readLong();
         long gammaId = dis.readLong();
         long artTypeId = dis.readLong();
         String guid = dis.readUTF();
         writer.printf("INSERT INTO osee_artifact (ART_ID, GAMMA_ID, ART_TYPE_ID, GUID) VALUES (%d, %d, %d, '%s');%n",
            artId, gammaId, artTypeId, ColdStorageUtil.escapeSql(guid));
      }
      writer.flush();
      zipOut.closeEntry();

      dis.readUTF(); // "ATTRIBUTES_DATA"
      int attrCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_attribute.sql"));
      for (int i = 0; i < attrCount; i++) {
         long attrId = dis.readLong();
         long gammaId = dis.readLong();
         long artId = dis.readLong();
         long attrTypeId = dis.readLong();
         String value = dis.readUTF();
         String uri = dis.readUTF();
         writer.printf(
            "INSERT INTO osee_attribute (ATTR_ID, GAMMA_ID, ART_ID, ATTR_TYPE_ID, VALUE, URI) VALUES (%d, %d, %d, %d, '%s', '%s');%n",
            attrId, gammaId, artId, attrTypeId, ColdStorageUtil.escapeSql(value), ColdStorageUtil.escapeSql(uri));
      }
      writer.flush();
      zipOut.closeEntry();

      dis.readUTF(); // "RELATIONS_DATA"
      int relCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_relation_link.sql"));
      for (int i = 0; i < relCount; i++) {
         long relTypeId = dis.readLong();
         long aArtId = dis.readLong();
         long bArtId = dis.readLong();
         long gammaId = dis.readLong();
         long relLinkId = dis.readLong();
         String rationale = dis.readUTF();
         writer.printf(
            "INSERT INTO osee_relation_link (REL_LINK_TYPE_ID, A_ART_ID, B_ART_ID, GAMMA_ID, REL_LINK_ID, RATIONALE) VALUES (%d, %d, %d, %d, %d, '%s');%n",
            relTypeId, aArtId, bArtId, gammaId, relLinkId, ColdStorageUtil.escapeSql(rationale));
      }
      writer.flush();
      zipOut.closeEntry();

      dis.readUTF(); // "RELATIONS2_DATA"
      int rel2Count = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_relation.sql"));
      for (int i = 0; i < rel2Count; i++) {
         long relType = dis.readLong();
         long aArtId = dis.readLong();
         long bArtId = dis.readLong();
         long relArtId = dis.readLong();
         int relOrder = dis.readInt();
         long gammaId = dis.readLong();
         writer.printf(
            "INSERT INTO osee_relation (REL_TYPE, A_ART_ID, B_ART_ID, REL_ART_ID, REL_ORDER, GAMMA_ID) VALUES (%d, %d, %d, %d, %d, %d);%n",
            relType, aArtId, bArtId, relArtId, relOrder, gammaId);
      }
      writer.flush();
      zipOut.closeEntry();

      dis.readUTF(); // "SEARCH_TAGS_DATA"
      int tagCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_search_tags.sql"));
      for (int i = 0; i < tagCount; i++) {
         long gammaId = dis.readLong();
         long codedTagId = dis.readLong();
         writer.printf("INSERT INTO osee_search_tags (GAMMA_ID, CODED_TAG_ID) VALUES (%d, %d);%n", gammaId, codedTagId);
      }
      writer.flush();
      zipOut.closeEntry();

      dis.readUTF(); // "BRANCH_END"
   }

   // ---- Private helpers ----

   private Object[] readBranchRowFromDb(JdbcStatement stmt) {
      return new Object[] {
         stmt.getLong("BRANCH_ID"),
         (short) stmt.getInt("BRANCH_TYPE"),
         (short) stmt.getInt("BRANCH_STATE"),
         stmt.getString("BRANCH_NAME"),
         stmt.getLong("PARENT_BRANCH_ID"),
         stmt.getLong("PARENT_TRANSACTION_ID"),
         stmt.getLong("BASELINE_TRANSACTION_ID"),
         stmt.getLong("ASSOCIATED_ART_ID"),
         (short) stmt.getInt("ARCHIVED"),
         (short) stmt.getInt("INHERIT_ACCESS_CONTROL")};
   }

   private void writeBranchRow(DataOutputStream dos, Object[] row) throws IOException {
      dos.writeLong((long) row[0]); // BRANCH_ID
      dos.writeShort((short) row[1]); // BRANCH_TYPE
      dos.writeShort((short) row[2]); // BRANCH_STATE
      dos.writeUTF((String) row[3]); // BRANCH_NAME
      dos.writeLong((long) row[4]); // PARENT_BRANCH_ID
      dos.writeLong((long) row[5]); // PARENT_TRANSACTION_ID
      dos.writeLong((long) row[6]); // BASELINE_TRANSACTION_ID
      dos.writeLong((long) row[7]); // ASSOCIATED_ART_ID
      dos.writeShort((short) row[8]); // ARCHIVED
      dos.writeShort((short) row[9]); // INHERIT_ACCESS_CONTROL
   }

   private void restoreBranchData(DataInputStream dis, XResultData results) throws IOException {
      // Read BRANCH_DATA section
      dis.readUTF(); // "BRANCH_DATA"
      int branchRowCount = dis.readInt();
      List<Object[]> branchRows = new ArrayList<>();
      for (int i = 0; i < branchRowCount; i++) {
         branchRows.add(readBranchRowFromFile(dis));
      }

      // Read TX_DETAILS_DATA section
      dis.readUTF(); // "TX_DETAILS_DATA"
      int txDetailsCount = dis.readInt();
      List<Object[]> txDetailsRows = new ArrayList<>();
      for (int i = 0; i < txDetailsCount; i++) {
         txDetailsRows.add(readTxDetailsRow(dis));
      }

      // Read TXS_ARCHIVED_DATA section
      dis.readUTF(); // "TXS_ARCHIVED_DATA"
      int txsCount = dis.readInt();
      List<Object[]> txsRows = new ArrayList<>();
      for (int i = 0; i < txsCount; i++) {
         txsRows.add(readTxsArchivedRow(dis));
      }

      // Read backing data sections — old batch-sweep archives go straight to BRANCH_END
      // after TXS_ARCHIVED_DATA, so check the next marker before assuming backing data exists.
      List<Object[]> artRows = new ArrayList<>();
      List<Object[]> attrRows = new ArrayList<>();
      List<Object[]> relRows = new ArrayList<>();
      List<Object[]> rel2Rows = new ArrayList<>();
      List<long[]> tagRows = new ArrayList<>();

      String marker = dis.readUTF();
      if (!"BRANCH_END".equals(marker)) {
         // marker should be "ARTIFACTS_DATA"
         int artCount = dis.readInt();
         for (int i = 0; i < artCount; i++) {
            artRows.add(new Object[] {dis.readLong(), dis.readLong(), dis.readLong(), dis.readUTF()});
         }

         dis.readUTF(); // "ATTRIBUTES_DATA"
         int attrCount = dis.readInt();
         for (int i = 0; i < attrCount; i++) {
            attrRows.add(new Object[] {dis.readLong(), dis.readLong(), dis.readLong(), dis.readLong(), dis.readUTF(), dis.readUTF()});
         }

         dis.readUTF(); // "RELATIONS_DATA"
         int relCount = dis.readInt();
         for (int i = 0; i < relCount; i++) {
            relRows.add(new Object[] {dis.readLong(), dis.readLong(), dis.readLong(), dis.readLong(), dis.readLong(), dis.readUTF()});
         }

         dis.readUTF(); // "RELATIONS2_DATA"
         int rel2Count = dis.readInt();
         for (int i = 0; i < rel2Count; i++) {
            rel2Rows.add(new Object[] {dis.readLong(), dis.readLong(), dis.readLong(), dis.readLong(), dis.readInt(), dis.readLong()});
         }

         dis.readUTF(); // "SEARCH_TAGS_DATA"
         int tagCount = dis.readInt();
         for (int i = 0; i < tagCount; i++) {
            tagRows.add(new long[] {dis.readLong(), dis.readLong()});
         }

         dis.readUTF(); // "BRANCH_END"
      }

      // Restore order: backing data first (so FKs are satisfied), then branch, tx_details, txs
      // 1. Insert backing data (artifacts, attributes, relations)
      if (!artRows.isEmpty()) {
         jdbcClient.runBatchUpdate(
            "INSERT INTO osee_artifact (ART_ID, GAMMA_ID, ART_TYPE_ID, GUID) VALUES (?, ?, ?, ?)", artRows);
      }
      if (!attrRows.isEmpty()) {
         jdbcClient.runBatchUpdate(
            "INSERT INTO osee_attribute (ATTR_ID, GAMMA_ID, ART_ID, ATTR_TYPE_ID, VALUE, URI) VALUES (?, ?, ?, ?, ?, ?)", attrRows);
      }
      if (!relRows.isEmpty()) {
         jdbcClient.runBatchUpdate(
            "INSERT INTO osee_relation_link (REL_LINK_TYPE_ID, A_ART_ID, B_ART_ID, GAMMA_ID, REL_LINK_ID, RATIONALE) VALUES (?, ?, ?, ?, ?, ?)", relRows);
      }
      if (!rel2Rows.isEmpty()) {
         jdbcClient.runBatchUpdate(
            "INSERT INTO osee_relation (REL_TYPE, A_ART_ID, B_ART_ID, REL_ART_ID, REL_ORDER, GAMMA_ID) VALUES (?, ?, ?, ?, ?, ?)", rel2Rows);
      }
      if (!tagRows.isEmpty()) {
         List<Object[]> tagData = new ArrayList<>();
         for (long[] row : tagRows) {
            tagData.add(new Object[] {row[0], row[1]});
         }
         jdbcClient.runBatchUpdate(
            "INSERT INTO osee_search_tags (GAMMA_ID, CODED_TAG_ID) VALUES (?, ?)", tagData);
      }

      // 2. Insert branch with temporary baseline; force archived = 1 since data goes into osee_txs_archived
      for (Object[] row : branchRows) {
         long realBaselineTxId = (long) row[6];
         long realParentTxId = (long) row[5];
         row[5] = 1L; // temporary PARENT_TRANSACTION_ID
         row[6] = 1L; // temporary BASELINE_TRANSACTION_ID
         row[8] = (short) 1; // force archived since txs rows restore into osee_txs_archived
         jdbcClient.runPreparedUpdate(INSERT_BRANCH, row);
         row[5] = realParentTxId;
         row[6] = realBaselineTxId;
      }

      // 3. Insert tx_details
      jdbcClient.runBatchUpdate(INSERT_TX_DETAILS, txDetailsRows);

      // 4. Update branch to real baseline
      for (Object[] row : branchRows) {
         long branchId = (long) row[0];
         long realBaselineTxId = (long) row[6];
         long realParentTxId = (long) row[5];
         jdbcClient.runPreparedUpdate(UPDATE_BRANCH_BASELINE, realBaselineTxId, realParentTxId, branchId);
      }

      // 5. Insert txs_archived
      jdbcClient.runBatchUpdate(INSERT_TXS_ARCHIVED, txsRows);

      results.logf("  Restored: %d branch rows, %d tx_details rows, %d txs_archived rows, %d artifacts, %d attributes, %d relations",
         branchRows.size(), txDetailsRows.size(), txsRows.size(), artRows.size(), attrRows.size(),
         relRows.size() + rel2Rows.size());
   }

   private void skipBranchData(DataInputStream dis) throws IOException {
      // Skip BRANCH_DATA
      dis.readUTF(); // "BRANCH_DATA"
      int branchRowCount = dis.readInt();
      for (int i = 0; i < branchRowCount; i++) {
         readBranchRowFromFile(dis);
      }

      // Skip TX_DETAILS_DATA
      dis.readUTF(); // "TX_DETAILS_DATA"
      int txDetailsCount = dis.readInt();
      for (int i = 0; i < txDetailsCount; i++) {
         readTxDetailsRow(dis);
      }

      // Skip TXS_ARCHIVED_DATA
      dis.readUTF(); // "TXS_ARCHIVED_DATA"
      int txsCount = dis.readInt();
      for (int i = 0; i < txsCount; i++) {
         readTxsArchivedRow(dis);
      }

      // Read the next section marker — old batch-sweep files go straight to BRANCH_END,
      // newer files (and single-branch archives) include backing data sections.
      String marker = dis.readUTF();
      if ("BRANCH_END".equals(marker)) {
         return;
      }

      // marker should be "ARTIFACTS_DATA"
      int artCount = dis.readInt();
      for (int i = 0; i < artCount; i++) {
         dis.readLong(); dis.readLong(); dis.readLong(); dis.readUTF();
      }

      // Skip ATTRIBUTES_DATA
      dis.readUTF(); // "ATTRIBUTES_DATA"
      int attrCount = dis.readInt();
      for (int i = 0; i < attrCount; i++) {
         dis.readLong(); dis.readLong(); dis.readLong(); dis.readLong(); dis.readUTF(); dis.readUTF();
      }

      // Skip RELATIONS_DATA
      dis.readUTF(); // "RELATIONS_DATA"
      int relCount = dis.readInt();
      for (int i = 0; i < relCount; i++) {
         dis.readLong(); dis.readLong(); dis.readLong(); dis.readLong(); dis.readLong(); dis.readUTF();
      }

      // Skip RELATIONS2_DATA
      dis.readUTF(); // "RELATIONS2_DATA"
      int rel2Count = dis.readInt();
      for (int i = 0; i < rel2Count; i++) {
         dis.readLong(); dis.readLong(); dis.readLong(); dis.readLong(); dis.readInt(); dis.readLong();
      }

      // Skip SEARCH_TAGS_DATA
      dis.readUTF(); // "SEARCH_TAGS_DATA"
      int tagCount = dis.readInt();
      for (int i = 0; i < tagCount; i++) {
         dis.readLong(); dis.readLong();
      }

      dis.readUTF(); // "BRANCH_END"
   }

   private Object[] readBranchRowFromFile(DataInputStream dis) throws IOException {
      return new Object[] {
         dis.readLong(), // BRANCH_ID
         dis.readShort(), // BRANCH_TYPE
         dis.readShort(), // BRANCH_STATE
         dis.readUTF(), // BRANCH_NAME
         dis.readLong(), // PARENT_BRANCH_ID
         dis.readLong(), // PARENT_TRANSACTION_ID
         dis.readLong(), // BASELINE_TRANSACTION_ID
         dis.readLong(), // ASSOCIATED_ART_ID
         dis.readShort(), // ARCHIVED
         dis.readShort() // INHERIT_ACCESS_CONTROL
      };
   }

   private Object[] readTxDetailsRow(DataInputStream dis) throws IOException {
      return new Object[] {
         dis.readLong(), // BRANCH_ID
         dis.readLong(), // TRANSACTION_ID
         dis.readLong(), // AUTHOR
         new Timestamp(dis.readLong()), // TIME
         dis.readUTF(), // OSEE_COMMENT
         dis.readShort(), // TX_TYPE
         dis.readLong(), // COMMIT_ART_ID
         dis.readLong() // BUILD_ID
      };
   }

   private Object[] readTxsArchivedRow(DataInputStream dis) throws IOException {
      return new Object[] {
         dis.readLong(), // BRANCH_ID
         dis.readLong(), // GAMMA_ID
         dis.readLong(), // TRANSACTION_ID
         dis.readShort(), // TX_CURRENT
         dis.readShort(), // MOD_TYPE
         dis.readLong() // APP_ID
      };
   }

   // ---- Inner data classes ----

   private static class BranchInfo {
      final long branchId;
      final String branchName;
      final int branchState;
      final boolean isArchived;

      BranchInfo(long branchId, String branchName, int branchState, boolean isArchived) {
         this.branchId = branchId;
         this.branchName = branchName;
         this.branchState = branchState;
         this.isArchived = isArchived;
      }
   }

   private static class TxDetailsRow {
      final long branchId;
      final long transactionId;
      final long author;
      final Timestamp time;
      final String comment;
      final int txType;
      final long commitArtId;
      final long buildId;

      TxDetailsRow(JdbcStatement stmt) {
         this.branchId = stmt.getLong("BRANCH_ID");
         this.transactionId = stmt.getLong("TRANSACTION_ID");
         this.author = stmt.getLong("AUTHOR");
         this.time = stmt.getTimestamp("TIME");
         this.comment = stmt.getString("OSEE_COMMENT");
         this.txType = stmt.getInt("TX_TYPE");
         this.commitArtId = stmt.getLong("COMMIT_ART_ID");
         this.buildId = stmt.getLong("BUILD_ID");
      }

      void writeTo(DataOutputStream dos) throws IOException {
         dos.writeLong(branchId);
         dos.writeLong(transactionId);
         dos.writeLong(author);
         dos.writeLong(time != null ? time.getTime() : 0L);
         dos.writeUTF(comment != null ? comment : "");
         dos.writeShort(txType);
         dos.writeLong(commitArtId);
         dos.writeLong(buildId);
      }
   }

   private static class TxsArchivedRow {
      final long branchId;
      final long gammaId;
      final long transactionId;
      final int txCurrent;
      final int modType;
      final long appId;

      TxsArchivedRow(JdbcStatement stmt) {
         this.branchId = stmt.getLong("BRANCH_ID");
         this.gammaId = stmt.getLong("GAMMA_ID");
         this.transactionId = stmt.getLong("TRANSACTION_ID");
         this.txCurrent = stmt.getInt("TX_CURRENT");
         this.modType = stmt.getInt("MOD_TYPE");
         this.appId = stmt.getLong("APP_ID");
      }

      void writeTo(DataOutputStream dos) throws IOException {
         dos.writeLong(branchId);
         dos.writeLong(gammaId);
         dos.writeLong(transactionId);
         dos.writeShort(txCurrent);
         dos.writeShort(modType);
         dos.writeLong(appId);
      }
   }
}
