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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Manages cold storage archival of transaction data for committed/rebaselined branches. Exports osee_txs_archived,
 * osee_tx_details, and osee_branch rows to compressed binary files, then purges them from the database.
 *
 * @author Ryan D. Brooks
 */
public class TxsColdStorage {

   private static final String MAGIC = "OSEE_TXS_COLD_V1";
   private static final int SCHEMA_VERSION = 1;

   // @formatter:off
   private static final String SELECT_ELIGIBLE_BRANCHES =
      "SELECT b.BRANCH_ID, b.BRANCH_NAME, b.BRANCH_STATE FROM osee_branch b " +
      "WHERE b.BRANCH_STATE IN (?, ?, ?) " +
      "AND b.BRANCH_ID NOT IN (SELECT BRANCH_ID FROM osee_txs_cold_storage) " +
      "AND EXISTS (SELECT 1 FROM osee_txs_archived t WHERE t.BRANCH_ID = b.BRANCH_ID) " +
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

   private static final String SELECT_TX_DETAILS_FOR_BRANCH =
      "SELECT BRANCH_ID, TRANSACTION_ID, AUTHOR, TIME, OSEE_COMMENT, TX_TYPE, COMMIT_ART_ID, BUILD_ID " +
      "FROM osee_tx_details WHERE BRANCH_ID = ? ORDER BY TRANSACTION_ID";

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

      String coldPath = getColdStoragePath();
      if (coldPath == null) {
         results.error("Unable to determine server data path for cold storage");
         return results;
      }

      // Get branch info
      String[] branchName = {""};
      int[] branchState = {0};
      jdbcClient.runQuery(stmt -> {
         branchName[0] = stmt.getString("BRANCH_NAME");
         branchState[0] = stmt.getInt("BRANCH_STATE");
      }, SELECT_BRANCH_ROW, branchId);

      if (branchName[0].isEmpty()) {
         results.errorf("Branch %s not found", branchId);
         return results;
      }

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      String fileName = "txs_cold_branch_" + branchId.getIdString() + "_" + dateFormat.format(new Date()) + ".gz";
      String filePath = coldPath + File.separator + fileName;

      List<BranchInfo> branches = new ArrayList<>();
      branches.add(new BranchInfo(branchId.getId(), branchName[0], branchState[0]));

      try {
         int totalTxsRows = 0;
         int totalTxDetailsRows = 0;

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

            dos.writeUTF("TX_DETAILS_DATA");
            List<TxDetailsRow> txDetailsRows = new ArrayList<>();
            jdbcClient.runQuery(stmt -> {
               txDetailsRows.add(new TxDetailsRow(stmt));
            }, SELECT_TX_DETAILS_FOR_BRANCH, branchId);
            dos.writeInt(txDetailsRows.size());
            for (TxDetailsRow row : txDetailsRows) {
               row.writeTo(dos);
            }

            dos.writeUTF("TXS_ARCHIVED_DATA");
            List<TxsArchivedRow> txsRows = new ArrayList<>();
            jdbcClient.runQuery(stmt -> {
               txsRows.add(new TxsArchivedRow(stmt));
            }, SELECT_TXS_ARCHIVED_FOR_BRANCH, branchId);
            dos.writeInt(txsRows.size());
            for (TxsArchivedRow row : txsRows) {
               row.writeTo(dos);
            }

            dos.writeUTF("BRANCH_END");

            totalTxsRows = txsRows.size();
            totalTxDetailsRows = txDetailsRows.size();
         }

         long fileSize = new File(filePath).length();

         // Insert catalog row (branch will be deleted by purgeBranch after this returns)
         jdbcClient.runPreparedUpdate(INSERT_CATALOG, branchId.getId(), branchName[0], fileName, totalTxsRows,
            totalTxDetailsRows, branchState[0]);

         results.logf("Archived branch %d (%s) to cold storage: %s (%d bytes, %d txs rows, %d tx_details rows)",
            branchId.getId(), branchName[0], fileName, fileSize, totalTxsRows, totalTxDetailsRows);

      } catch (IOException ex) {
         results.errorf("Failed to write cold storage file: %s", ex.getMessage());
         throw OseeCoreException.wrap(ex);
      }

      return results;
   }

   /**
    * Archives eligible branches to cold storage. Finds branches in committed, rebaselined, or deleted state whose last
    * transaction is older than retentionDays, exports their data to a compressed binary file, and purges them from the
    * database.
    *
    * @param limit maximum number of branches to archive in this invocation
    * @param retentionDays minimum age (in days) since last transaction before a branch is eligible
    * @return XResultData with details of what was archived
    */
   public XResultData archiveBranches(int limit, int retentionDays) {
      XResultData results = new XResultData();

      String coldPath = getColdStoragePath();
      if (coldPath == null) {
         results.error("Unable to determine server data path for cold storage");
         return results;
      }

      Timestamp cutoffDate = new Timestamp(System.currentTimeMillis() - (long) retentionDays * 24 * 60 * 60 * 1000);

      List<BranchInfo> eligibleBranches = new ArrayList<>();
      jdbcClient.runQuery(stmt -> {
         eligibleBranches.add(
            new BranchInfo(stmt.getLong("BRANCH_ID"), stmt.getString("BRANCH_NAME"), stmt.getInt("BRANCH_STATE")));
      }, SELECT_ELIGIBLE_BRANCHES, BranchState.COMMITTED.getId(), BranchState.REBASELINED.getId(),
         BranchState.DELETED.getId(), cutoffDate, limit);

      if (eligibleBranches.isEmpty()) {
         results.log("No eligible branches found for cold storage archival");
         return results;
      }

      results.logf("Found %d eligible branches for cold storage", eligibleBranches.size());

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      String fileName = "txs_cold_" + dateFormat.format(new Date()) + ".gz";
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

               // Write osee_txs_archived rows
               dos.writeUTF("TXS_ARCHIVED_DATA");
               List<TxsArchivedRow> txsRows = new ArrayList<>();
               jdbcClient.runQuery(stmt -> {
                  txsRows.add(new TxsArchivedRow(stmt));
               }, SELECT_TXS_ARCHIVED_FOR_BRANCH, branchId);
               dos.writeInt(txsRows.size());
               for (TxsArchivedRow row : txsRows) {
                  row.writeTo(dos);
               }

               dos.writeUTF("BRANCH_END");

               totalTxsRows += txsRows.size();
               totalTxDetailsRows += txDetailsRows.size();

               results.logf("  Branch %d (%s): %d txs rows, %d tx_details rows", branchId, branchInfo.branchName,
                  txsRows.size(), txDetailsRows.size());
            }
         }

         // Now purge from DB and insert catalog rows
         long fileSize = new File(filePath).length();

         for (BranchInfo branchInfo : eligibleBranches) {
            long branchId = branchInfo.branchId;

            int txsRowCount = jdbcClient.fetch(0, "SELECT COUNT(1) FROM osee_txs_archived WHERE BRANCH_ID = ?",
               branchId);
            int txDetailsRowCount = jdbcClient.fetch(0, "SELECT COUNT(1) FROM osee_tx_details WHERE BRANCH_ID = ?",
               branchId);

            // Delegate to existing PurgeBranchDatabaseCallable via OrcsBranch
            orcsApi.getBranchOps().purgeBranch(BranchId.valueOf(branchId), false).call();

            // Insert catalog row
            jdbcClient.runPreparedUpdate(INSERT_CATALOG, branchId, branchInfo.branchName, fileName, txsRowCount,
               txDetailsRowCount, branchInfo.branchState);
         }

         results.logf("Archive complete: %s (%d bytes, %d branches, %d total txs rows, %d total tx_details rows)",
            fileName, fileSize, eligibleBranches.size(), totalTxsRows, totalTxDetailsRows);

      } catch (IOException ex) {
         results.errorf("Failed to write cold storage file: %s", ex.getMessage());
         throw OseeCoreException.wrap(ex);
      } catch (Exception ex) {
         results.errorf("Failed to purge branch: %s", ex.getMessage());
         throw OseeCoreException.wrap(ex);
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

      String coldPath = getColdStoragePath();
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
            String marker = dis.readUTF(); // BRANCH_START
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

         int remainingRefs = jdbcClient.fetch(0,
            "SELECT COUNT(1) FROM osee_txs_cold_storage WHERE EXPORT_FILE = ?", exportFile[0]);
         if (remainingRefs == 0) {
            results.logf("No remaining branches reference file %s - file can be cleaned up manually", exportFile[0]);
         }

         results.logf("Successfully restored branch %s from cold storage", branchId);

      } catch (IOException ex) {
         results.errorf("Failed to read cold storage file: %s", ex.getMessage());
         throw OseeCoreException.wrap(ex);
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
         entries.add(String.format("Branch %d (%s) - state: %d, file: %s, exported: %s, txs_rows: %d, tx_details_rows: %d",
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

      int remainingRefs = jdbcClient.fetch(0,
         "SELECT COUNT(1) FROM osee_txs_cold_storage WHERE EXPORT_FILE = ?", exportFile[0]);
      if (remainingRefs == 0) {
         String coldPath = getColdStoragePath();
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

   // ---- Private helpers ----

   private String getColdStoragePath() {
      String serverPath = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverPath == null) {
         serverPath = System.getProperty("user.home");
      }
      if ("null".equals(serverPath)) {
         return null;
      }
      Path purgeFolder = Paths.get(serverPath + File.separator + "purge");
      if (Files.exists(purgeFolder)) {
         serverPath = purgeFolder.toString();
      }
      Path coldFolder = Paths.get(serverPath + File.separator + "cold_storage");
      try {
         Files.createDirectories(coldFolder);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
      return coldFolder.toString();
   }

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
         (short) stmt.getInt("INHERIT_ACCESS_CONTROL")
      };
   }

   private void writeBranchRow(DataOutputStream dos, Object[] row) throws IOException {
      dos.writeLong((long) row[0]);   // BRANCH_ID
      dos.writeShort((short) row[1]); // BRANCH_TYPE
      dos.writeShort((short) row[2]); // BRANCH_STATE
      dos.writeUTF((String) row[3]);  // BRANCH_NAME
      dos.writeLong((long) row[4]);   // PARENT_BRANCH_ID
      dos.writeLong((long) row[5]);   // PARENT_TRANSACTION_ID
      dos.writeLong((long) row[6]);   // BASELINE_TRANSACTION_ID
      dos.writeLong((long) row[7]);   // ASSOCIATED_ART_ID
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

      dis.readUTF(); // "BRANCH_END"

      // Restore order to satisfy circular FKs:
      // 1. Insert branch with baseline_transaction_id = 1 (always exists on system root)
      // 2. Insert tx_details (needs branch to exist due to BRANCH_ID_FK1)
      // 3. Update branch to real baseline_transaction_id (now tx_details row exists)
      // 4. Insert txs_archived
      for (Object[] row : branchRows) {
         long realBaselineTxId = (long) row[6];
         long realParentTxId = (long) row[5];
         row[5] = 1L; // temporary PARENT_TRANSACTION_ID
         row[6] = 1L; // temporary BASELINE_TRANSACTION_ID
         jdbcClient.runPreparedUpdate(INSERT_BRANCH, row);
         // Restore real values after row reference
         row[5] = realParentTxId;
         row[6] = realBaselineTxId;
      }
      for (Object[] row : txDetailsRows) {
         jdbcClient.runPreparedUpdate(INSERT_TX_DETAILS, row);
      }
      for (Object[] row : branchRows) {
         long branchId = (long) row[0];
         long realBaselineTxId = (long) row[6];
         long realParentTxId = (long) row[5];
         jdbcClient.runPreparedUpdate(UPDATE_BRANCH_BASELINE, realBaselineTxId, realParentTxId, branchId);
      }
      jdbcClient.runBatchUpdate(INSERT_TXS_ARCHIVED, txsRows);

      results.logf("  Restored: %d branch rows, %d tx_details rows, %d txs_archived rows", branchRows.size(),
         txDetailsRows.size(), txsRows.size());
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

      dis.readUTF(); // "BRANCH_END"
   }

   private Object[] readBranchRowFromFile(DataInputStream dis) throws IOException {
      return new Object[] {
         dis.readLong(),  // BRANCH_ID
         dis.readShort(), // BRANCH_TYPE
         dis.readShort(), // BRANCH_STATE
         dis.readUTF(),   // BRANCH_NAME
         dis.readLong(),  // PARENT_BRANCH_ID
         dis.readLong(),  // PARENT_TRANSACTION_ID
         dis.readLong(),  // BASELINE_TRANSACTION_ID
         dis.readLong(),  // ASSOCIATED_ART_ID
         dis.readShort(), // ARCHIVED
         dis.readShort()  // INHERIT_ACCESS_CONTROL
      };
   }

   private Object[] readTxDetailsRow(DataInputStream dis) throws IOException {
      return new Object[] {
         dis.readLong(),                // BRANCH_ID
         dis.readLong(),                // TRANSACTION_ID
         dis.readLong(),                // AUTHOR
         new Timestamp(dis.readLong()), // TIME
         dis.readUTF(),                 // OSEE_COMMENT
         dis.readShort(),               // TX_TYPE
         dis.readLong(),                // COMMIT_ART_ID
         dis.readLong()                 // BUILD_ID
      };
   }

   private Object[] readTxsArchivedRow(DataInputStream dis) throws IOException {
      return new Object[] {
         dis.readLong(),  // BRANCH_ID
         dis.readLong(),  // GAMMA_ID
         dis.readLong(),  // TRANSACTION_ID
         dis.readShort(), // TX_CURRENT
         dis.readShort(), // MOD_TYPE
         dis.readLong()   // APP_ID
      };
   }

   // ---- Inner data classes ----

   private static class BranchInfo {
      final long branchId;
      final String branchName;
      final int branchState;

      BranchInfo(long branchId, String branchName, int branchState) {
         this.branchId = branchId;
         this.branchName = branchName;
         this.branchState = branchState;
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
         dos.writeLong(time.getTime());
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
