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
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Manages cold storage for purged transactions. Before a transaction is purged, its data (osee_txs, osee_tx_details,
 * and backing artifact/attribute/relation rows for orphaned gammas) is exported to a compressed binary file for
 * potential future restoration.
 *
 * @author Ryan D. Brooks
 */
public class TxPurgeColdStorage {

   private static final String MAGIC = "OSEE_TX_PURGE_V1";
   private static final int SCHEMA_VERSION = 1;

   // @formatter:off
   private static final String SELECT_TXS_FOR_TX =
      "SELECT BRANCH_ID, GAMMA_ID, TRANSACTION_ID, TX_CURRENT, MOD_TYPE, APP_ID " +
      "FROM osee_txs WHERE BRANCH_ID = ? AND TRANSACTION_ID = ?";

   private static final String SELECT_TX_DETAILS_FOR_TX =
      "SELECT BRANCH_ID, TRANSACTION_ID, AUTHOR, TIME, OSEE_COMMENT, TX_TYPE, COMMIT_ART_ID, BUILD_ID " +
      "FROM osee_tx_details WHERE BRANCH_ID = ? AND TRANSACTION_ID = ?";

   private static final String SELECT_ARTIFACTS_BY_GAMMA =
      "SELECT ART_ID, GAMMA_ID, ART_TYPE_ID, GUID FROM osee_artifact WHERE GAMMA_ID = ?";

   private static final String SELECT_ATTRIBUTES_BY_GAMMA =
      "SELECT ATTR_ID, GAMMA_ID, ART_ID, ATTR_TYPE_ID, VALUE, URI FROM osee_attribute WHERE GAMMA_ID = ?";

   private static final String SELECT_RELATIONS_BY_GAMMA =
      "SELECT REL_LINK_TYPE_ID, A_ART_ID, B_ART_ID, GAMMA_ID, REL_LINK_ID, RATIONALE FROM osee_relation_link WHERE GAMMA_ID = ?";

   private static final String SELECT_RELATIONS2_BY_GAMMA =
      "SELECT REL_TYPE, A_ART_ID, B_ART_ID, REL_ART_ID, REL_ORDER, GAMMA_ID FROM osee_relation WHERE GAMMA_ID = ?";

   private static final String INSERT_TXS =
      "INSERT INTO osee_txs (BRANCH_ID, GAMMA_ID, TRANSACTION_ID, TX_CURRENT, MOD_TYPE, APP_ID) " +
      "VALUES (?, ?, ?, ?, ?, ?)";

   private static final String INSERT_TX_DETAILS =
      "INSERT INTO osee_tx_details (BRANCH_ID, TRANSACTION_ID, AUTHOR, TIME, OSEE_COMMENT, TX_TYPE, COMMIT_ART_ID, BUILD_ID) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

   private static final String INSERT_ARTIFACT =
      "INSERT INTO osee_artifact (ART_ID, GAMMA_ID, ART_TYPE_ID, GUID) VALUES (?, ?, ?, ?)";

   private static final String INSERT_ATTRIBUTE =
      "INSERT INTO osee_attribute (ATTR_ID, GAMMA_ID, ART_ID, ATTR_TYPE_ID, VALUE, URI) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String INSERT_RELATION =
      "INSERT INTO osee_relation_link (REL_LINK_TYPE_ID, A_ART_ID, B_ART_ID, GAMMA_ID, REL_LINK_ID, RATIONALE) " +
      "VALUES (?, ?, ?, ?, ?, ?)";

   private static final String INSERT_RELATION2 =
      "INSERT INTO osee_relation (REL_TYPE, A_ART_ID, B_ART_ID, REL_ART_ID, REL_ORDER, GAMMA_ID) " +
      "VALUES (?, ?, ?, ?, ?, ?)";

   private static final String SELECT_IMPACTED_GAMMAS =
      "SELECT gamma_id FROM osee_txs WHERE branch_id = ? AND transaction_id = ?";
   // @formatter:on

   private final JdbcClient jdbcClient;
   private final OrcsApi orcsApi;

   public TxPurgeColdStorage(JdbcClient jdbcClient, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.orcsApi = orcsApi;
   }

   /**
    * Exports transaction data to cold storage before purging. Captures osee_txs, osee_tx_details, and backing data
    * (artifacts, attributes, relations) for the given transactions.
    *
    * @param txIds list of transaction IDs being purged
    * @return the filename of the cold storage archive, or null if export failed
    */
   public String exportTransactions(List<TransactionId> txIds) {
      String coldPath = getColdStoragePath();
      if (coldPath == null) {
         return null;
      }

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      String fileName = "tx_purge_" + txIds.get(0).getIdString();
      if (txIds.size() > 1) {
         fileName += "_to_" + txIds.get(txIds.size() - 1).getIdString();
      }
      fileName += "_" + dateFormat.format(new Date()) + ".gz";
      String filePath = coldPath + File.separator + fileName;

      try (FileOutputStream fos = new FileOutputStream(filePath);
           GZIPOutputStream gzos = new GZIPOutputStream(fos);
           DataOutputStream dos = new DataOutputStream(gzos)) {

         // Write header
         dos.writeUTF(MAGIC);
         dos.writeInt(SCHEMA_VERSION);
         dos.writeLong(System.currentTimeMillis());
         dos.writeInt(txIds.size());

         for (TransactionId txId : txIds) {
            BranchId branchId = orcsApi.getTransactionFactory().getTx(txId).getBranch();

            dos.writeUTF("TX_START");
            dos.writeLong(txId.getId());
            dos.writeLong(branchId.getId());

            // Export osee_tx_details row
            dos.writeUTF("TX_DETAILS");
            List<Object[]> txDetailsRows = new ArrayList<>();
            jdbcClient.runQuery(stmt -> {
               txDetailsRows.add(new Object[] {
                  stmt.getLong("BRANCH_ID"), stmt.getLong("TRANSACTION_ID"), stmt.getLong("AUTHOR"),
                  stmt.getTimestamp("TIME"), stmt.getString("OSEE_COMMENT"), stmt.getInt("TX_TYPE"),
                  stmt.getLong("COMMIT_ART_ID"), stmt.getLong("BUILD_ID")
               });
            }, SELECT_TX_DETAILS_FOR_TX, branchId, txId);
            dos.writeInt(txDetailsRows.size());
            for (Object[] row : txDetailsRows) {
               writeTxDetailsRow(dos, row);
            }

            // Export osee_txs rows
            dos.writeUTF("TXS_DATA");
            List<Object[]> txsRows = new ArrayList<>();
            jdbcClient.runQuery(stmt -> {
               txsRows.add(new Object[] {
                  stmt.getLong("BRANCH_ID"), stmt.getLong("GAMMA_ID"), stmt.getLong("TRANSACTION_ID"),
                  stmt.getInt("TX_CURRENT"), stmt.getInt("MOD_TYPE"), stmt.getLong("APP_ID")
               });
            }, SELECT_TXS_FOR_TX, branchId, txId);
            dos.writeInt(txsRows.size());
            for (Object[] row : txsRows) {
               writeTxsRow(dos, row);
            }

            // Collect gammas and export backing data
            List<Long> gammas = new ArrayList<>();
            for (Object[] row : txsRows) {
               gammas.add((long) row[1]);
            }

            // Export artifacts
            dos.writeUTF("ARTIFACTS");
            List<Object[]> artRows = new ArrayList<>();
            for (Long gamma : gammas) {
               jdbcClient.runQuery(stmt -> {
                  artRows.add(new Object[] {
                     stmt.getLong("ART_ID"), stmt.getLong("GAMMA_ID"), stmt.getLong("ART_TYPE_ID"),
                     stmt.getString("GUID")
                  });
               }, SELECT_ARTIFACTS_BY_GAMMA, gamma);
            }
            dos.writeInt(artRows.size());
            for (Object[] row : artRows) {
               dos.writeLong((long) row[0]);
               dos.writeLong((long) row[1]);
               dos.writeLong((long) row[2]);
               dos.writeUTF((String) row[3]);
            }

            // Export attributes
            dos.writeUTF("ATTRIBUTES");
            List<Object[]> attrRows = new ArrayList<>();
            for (Long gamma : gammas) {
               jdbcClient.runQuery(stmt -> {
                  attrRows.add(new Object[] {
                     stmt.getLong("ATTR_ID"), stmt.getLong("GAMMA_ID"), stmt.getLong("ART_ID"),
                     stmt.getLong("ATTR_TYPE_ID"), stmt.getString("VALUE"), stmt.getString("URI")
                  });
               }, SELECT_ATTRIBUTES_BY_GAMMA, gamma);
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

            // Export relations (legacy)
            dos.writeUTF("RELATIONS");
            List<Object[]> relRows = new ArrayList<>();
            for (Long gamma : gammas) {
               jdbcClient.runQuery(stmt -> {
                  relRows.add(new Object[] {
                     stmt.getLong("REL_LINK_TYPE_ID"), stmt.getLong("A_ART_ID"), stmt.getLong("B_ART_ID"),
                     stmt.getLong("GAMMA_ID"), stmt.getLong("REL_LINK_ID"), stmt.getString("RATIONALE")
                  });
               }, SELECT_RELATIONS_BY_GAMMA, gamma);
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

            // Export relations2 (new format)
            dos.writeUTF("RELATIONS2");
            List<Object[]> rel2Rows = new ArrayList<>();
            for (Long gamma : gammas) {
               jdbcClient.runQuery(stmt -> {
                  rel2Rows.add(new Object[] {
                     stmt.getLong("REL_TYPE"), stmt.getLong("A_ART_ID"), stmt.getLong("B_ART_ID"),
                     stmt.getLong("REL_ART_ID"), stmt.getInt("REL_ORDER"), stmt.getLong("GAMMA_ID")
                  });
               }, SELECT_RELATIONS2_BY_GAMMA, gamma);
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

            dos.writeUTF("TX_END");
         }

      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }

      return fileName;
   }

   /**
    * Restores a purged transaction from cold storage. Re-inserts backing data (artifacts, attributes, relations),
    * osee_tx_details, and osee_txs rows.
    *
    * @param fileName the archive file name
    * @param txId the transaction ID to restore
    * @return XResultData with restore details
    */
   public XResultData restoreTransaction(String fileName, TransactionId txId) {
      XResultData results = new XResultData();

      String coldPath = getColdStoragePath();
      if (coldPath == null) {
         results.error("Unable to determine cold storage path");
         return results;
      }

      // If fileName not provided, search for a file containing this transaction ID
      if (fileName == null || fileName.isEmpty()) {
         fileName = findArchiveForTransaction(coldPath, txId);
         if (fileName == null) {
            results.errorf("No archive file found containing transaction %s", txId);
            return results;
         }
      }

      String filePath = coldPath + File.separator + fileName;
      if (!new File(filePath).exists()) {
         results.errorf("Cold storage file not found: %s", filePath);
         return results;
      }

      try (FileInputStream fis = new FileInputStream(filePath);
           GZIPInputStream gzis = new GZIPInputStream(fis);
           DataInputStream dis = new DataInputStream(gzis)) {

         String magic = dis.readUTF();
         if (!MAGIC.equals(magic)) {
            results.errorf("Invalid file format: %s", magic);
            return results;
         }
         int version = dis.readInt();
         if (version != SCHEMA_VERSION) {
            results.errorf("Unsupported schema version: %d", version);
            return results;
         }
         dis.readLong(); // export timestamp
         int txCount = dis.readInt();

         long targetTxId = txId.getId();
         boolean found = false;

         for (int t = 0; t < txCount; t++) {
            dis.readUTF(); // "TX_START"
            long fileTxId = dis.readLong();
            long fileBranchId = dis.readLong();

            if (fileTxId == targetTxId) {
               found = true;
               restoreTransactionData(dis, results);
               break;
            } else {
               skipTransactionData(dis);
            }
         }

         if (!found) {
            results.errorf("Transaction %s not found in archive file %s", txId, fileName);
         } else {
            results.logf("Successfully restored transaction %s from %s", txId, fileName);
         }

      } catch (IOException ex) {
         results.errorf("Failed to read cold storage file: %s", ex.getMessage());
      }

      return results;
   }

   /**
    * Lists available purged transaction archive files.
    */
   public XResultData listPurgedTransactionArchives() {
      XResultData results = new XResultData();
      String coldPath = getColdStoragePath();
      if (coldPath == null) {
         results.error("Unable to determine cold storage path");
         return results;
      }

      File dir = new File(coldPath);
      File[] files = dir.listFiles((d, name) -> name.startsWith("tx_purge_") && name.endsWith(".gz"));
      if (files == null || files.length == 0) {
         results.log("No purged transaction archives found");
      } else {
         results.logf("%d purged transaction archive(s):", files.length);
         for (File file : files) {
            results.logf("  %s (%d bytes)", file.getName(), file.length());
         }
      }
      return results;
   }

   /**
    * Generates a zip file containing SQL INSERT statements that would be executed to restore a purged transaction.
    *
    * @param fileName optional archive file name (auto-discovered if null)
    * @param txId the transaction to preview
    * @param zipOut the output stream to write the zip to
    */
   public void previewTransaction(String fileName, TransactionId txId, java.util.zip.ZipOutputStream zipOut)
      throws IOException {

      String coldPath = getColdStoragePath();
      if (coldPath == null) {
         throw new IOException("Unable to determine cold storage path");
      }

      if (fileName == null || fileName.isEmpty()) {
         fileName = findArchiveForTransaction(coldPath, txId);
         if (fileName == null) {
            throw new IOException("No archive file found containing transaction " + txId);
         }
      }

      String filePath = coldPath + File.separator + fileName;

      try (FileInputStream fis = new FileInputStream(filePath);
           GZIPInputStream gzis = new GZIPInputStream(fis);
           DataInputStream dis = new DataInputStream(gzis)) {

         String magic = dis.readUTF();
         if (!MAGIC.equals(magic)) {
            throw new IOException("Invalid file format: " + magic);
         }
         dis.readInt(); // version
         dis.readLong(); // timestamp
         int txCount = dis.readInt();

         long targetTxId = txId.getId();

         for (int t = 0; t < txCount; t++) {
            dis.readUTF(); // TX_START
            long fileTxId = dis.readLong();
            long fileBranchId = dis.readLong();

            if (fileTxId == targetTxId) {
               writePreviewSqlForTransaction(dis, zipOut);
               return;
            } else {
               skipTransactionData(dis);
            }
         }
         throw new IOException("Transaction " + txId + " not found in archive");
      }
   }

   private void writePreviewSqlForTransaction(DataInputStream dis, java.util.zip.ZipOutputStream zipOut)
      throws IOException {
      java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(zipOut));

      // TX_DETAILS
      dis.readUTF(); // "TX_DETAILS"
      int txDetailsCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_tx_details.sql"));
      for (int i = 0; i < txDetailsCount; i++) {
         Object[] row = readTxDetailsRow(dis);
         writer.printf("INSERT INTO osee_tx_details (BRANCH_ID, TRANSACTION_ID, AUTHOR, TIME, OSEE_COMMENT, TX_TYPE, COMMIT_ART_ID, BUILD_ID) VALUES (%d, %d, %d, '%s', '%s', %d, %d, %d);%n",
            row[0], row[1], row[2], row[3], escapeSql((String) row[4]), (short) row[5], row[6], row[7]);
      }
      writer.flush();
      zipOut.closeEntry();

      // TXS_DATA
      dis.readUTF(); // "TXS_DATA"
      int txsCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_txs.sql"));
      for (int i = 0; i < txsCount; i++) {
         Object[] row = readTxsRow(dis);
         writer.printf("INSERT INTO osee_txs (BRANCH_ID, GAMMA_ID, TRANSACTION_ID, TX_CURRENT, MOD_TYPE, APP_ID) VALUES (%d, %d, %d, %d, %d, %d);%n",
            row[0], row[1], row[2], (short) row[3], (short) row[4], row[5]);
      }
      writer.flush();
      zipOut.closeEntry();

      // ARTIFACTS
      dis.readUTF(); // "ARTIFACTS"
      int artCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_artifact.sql"));
      for (int i = 0; i < artCount; i++) {
         long artId = dis.readLong();
         long gammaId = dis.readLong();
         long artTypeId = dis.readLong();
         String guid = dis.readUTF();
         writer.printf("INSERT INTO osee_artifact (ART_ID, GAMMA_ID, ART_TYPE_ID, GUID) VALUES (%d, %d, %d, '%s');%n",
            artId, gammaId, artTypeId, escapeSql(guid));
      }
      writer.flush();
      zipOut.closeEntry();

      // ATTRIBUTES
      dis.readUTF(); // "ATTRIBUTES"
      int attrCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_attribute.sql"));
      for (int i = 0; i < attrCount; i++) {
         long attrId = dis.readLong();
         long gammaId = dis.readLong();
         long artId = dis.readLong();
         long attrTypeId = dis.readLong();
         String value = dis.readUTF();
         String uri = dis.readUTF();
         writer.printf("INSERT INTO osee_attribute (ATTR_ID, GAMMA_ID, ART_ID, ATTR_TYPE_ID, VALUE, URI) VALUES (%d, %d, %d, %d, '%s', '%s');%n",
            attrId, gammaId, artId, attrTypeId, escapeSql(value), escapeSql(uri));
      }
      writer.flush();
      zipOut.closeEntry();

      // RELATIONS
      dis.readUTF(); // "RELATIONS"
      int relCount = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_relation_link.sql"));
      for (int i = 0; i < relCount; i++) {
         long relTypeId = dis.readLong();
         long aArtId = dis.readLong();
         long bArtId = dis.readLong();
         long gammaId = dis.readLong();
         long relLinkId = dis.readLong();
         String rationale = dis.readUTF();
         writer.printf("INSERT INTO osee_relation_link (REL_LINK_TYPE_ID, A_ART_ID, B_ART_ID, GAMMA_ID, REL_LINK_ID, RATIONALE) VALUES (%d, %d, %d, %d, %d, '%s');%n",
            relTypeId, aArtId, bArtId, gammaId, relLinkId, escapeSql(rationale));
      }
      writer.flush();
      zipOut.closeEntry();

      // RELATIONS2
      dis.readUTF(); // "RELATIONS2"
      int rel2Count = dis.readInt();
      zipOut.putNextEntry(new java.util.zip.ZipEntry("osee_relation.sql"));
      for (int i = 0; i < rel2Count; i++) {
         long relType = dis.readLong();
         long aArtId = dis.readLong();
         long bArtId = dis.readLong();
         long relArtId = dis.readLong();
         int relOrder = dis.readInt();
         long gammaId = dis.readLong();
         writer.printf("INSERT INTO osee_relation (REL_TYPE, A_ART_ID, B_ART_ID, REL_ART_ID, REL_ORDER, GAMMA_ID) VALUES (%d, %d, %d, %d, %d, %d);%n",
            relType, aArtId, bArtId, relArtId, relOrder, gammaId);
      }
      writer.flush();
      zipOut.closeEntry();

      dis.readUTF(); // "TX_END"
   }

   private String escapeSql(String value) {
      if (value == null) {
         return "";
      }
      return value.replace("'", "''");
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

   /**
    * Searches cold storage directory for an archive file containing the given transaction ID. Scans file headers to
    * find the matching transaction.
    */
   private String findArchiveForTransaction(String coldPath, TransactionId txId) {
      File dir = new File(coldPath);
      File[] files = dir.listFiles((d, name) -> name.startsWith("tx_purge_") && name.endsWith(".gz"));
      if (files == null) {
         return null;
      }

      long targetTxId = txId.getId();
      for (File file : files) {
         // Quick check: if the filename contains the tx ID, try it first
         if (file.getName().contains(String.valueOf(targetTxId))) {
            return file.getName();
         }
      }

      // If not found by name, scan file contents
      for (File file : files) {
         try (FileInputStream fis = new FileInputStream(file);
              GZIPInputStream gzis = new GZIPInputStream(fis);
              DataInputStream dis = new DataInputStream(gzis)) {

            String magic = dis.readUTF();
            if (!MAGIC.equals(magic)) {
               continue;
            }
            dis.readInt(); // version
            dis.readLong(); // timestamp
            int txCount = dis.readInt();

            for (int t = 0; t < txCount; t++) {
               dis.readUTF(); // TX_START
               long fileTxId = dis.readLong();
               if (fileTxId == targetTxId) {
                  return file.getName();
               }
               dis.readLong(); // branchId
               skipTransactionData(dis);
            }
         } catch (IOException ex) {
            // Skip corrupted files
         }
      }
      return null;
   }

   private void writeTxDetailsRow(DataOutputStream dos, Object[] row) throws IOException {
      dos.writeLong((long) row[0]);                      // BRANCH_ID
      dos.writeLong((long) row[1]);                      // TRANSACTION_ID
      dos.writeLong((long) row[2]);                      // AUTHOR
      dos.writeLong(((Timestamp) row[3]).getTime());     // TIME
      dos.writeUTF(row[4] != null ? (String) row[4] : ""); // OSEE_COMMENT
      dos.writeShort((int) row[5]);                      // TX_TYPE
      dos.writeLong((long) row[6]);                      // COMMIT_ART_ID
      dos.writeLong((long) row[7]);                      // BUILD_ID
   }

   private void writeTxsRow(DataOutputStream dos, Object[] row) throws IOException {
      dos.writeLong((long) row[0]);  // BRANCH_ID
      dos.writeLong((long) row[1]);  // GAMMA_ID
      dos.writeLong((long) row[2]);  // TRANSACTION_ID
      dos.writeShort((int) row[3]);  // TX_CURRENT
      dos.writeShort((int) row[4]);  // MOD_TYPE
      dos.writeLong((long) row[5]);  // APP_ID
   }

   private void restoreTransactionData(DataInputStream dis, XResultData results) throws IOException {
      // Read TX_DETAILS
      dis.readUTF(); // "TX_DETAILS"
      int txDetailsCount = dis.readInt();
      List<Object[]> txDetailsRows = new ArrayList<>();
      for (int i = 0; i < txDetailsCount; i++) {
         txDetailsRows.add(readTxDetailsRow(dis));
      }

      // Read TXS_DATA
      dis.readUTF(); // "TXS_DATA"
      int txsCount = dis.readInt();
      List<Object[]> txsRows = new ArrayList<>();
      for (int i = 0; i < txsCount; i++) {
         txsRows.add(readTxsRow(dis));
      }

      // Read ARTIFACTS
      dis.readUTF(); // "ARTIFACTS"
      int artCount = dis.readInt();
      List<Object[]> artRows = new ArrayList<>();
      for (int i = 0; i < artCount; i++) {
         artRows.add(new Object[] {dis.readLong(), dis.readLong(), dis.readLong(), dis.readUTF()});
      }

      // Read ATTRIBUTES
      dis.readUTF(); // "ATTRIBUTES"
      int attrCount = dis.readInt();
      List<Object[]> attrRows = new ArrayList<>();
      for (int i = 0; i < attrCount; i++) {
         attrRows.add(new Object[] {
            dis.readLong(), dis.readLong(), dis.readLong(), dis.readLong(), dis.readUTF(), dis.readUTF()
         });
      }

      // Read RELATIONS
      dis.readUTF(); // "RELATIONS"
      int relCount = dis.readInt();
      List<Object[]> relRows = new ArrayList<>();
      for (int i = 0; i < relCount; i++) {
         relRows.add(new Object[] {
            dis.readLong(), dis.readLong(), dis.readLong(), dis.readLong(), dis.readLong(), dis.readUTF()
         });
      }

      // Read RELATIONS2
      dis.readUTF(); // "RELATIONS2"
      int rel2Count = dis.readInt();
      List<Object[]> rel2Rows = new ArrayList<>();
      for (int i = 0; i < rel2Count; i++) {
         rel2Rows.add(new Object[] {
            dis.readLong(), dis.readLong(), dis.readLong(), dis.readLong(), dis.readInt(), dis.readLong()
         });
      }

      dis.readUTF(); // "TX_END"

      // Insert in order: backing data first, then tx_details, then txs
      for (Object[] row : artRows) {
         jdbcClient.runPreparedUpdate(INSERT_ARTIFACT, row);
      }
      for (Object[] row : attrRows) {
         jdbcClient.runPreparedUpdate(INSERT_ATTRIBUTE, row);
      }
      for (Object[] row : relRows) {
         jdbcClient.runPreparedUpdate(INSERT_RELATION, row);
      }
      for (Object[] row : rel2Rows) {
         jdbcClient.runPreparedUpdate(INSERT_RELATION2, row);
      }
      for (Object[] row : txDetailsRows) {
         jdbcClient.runPreparedUpdate(INSERT_TX_DETAILS, row);
      }
      jdbcClient.runBatchUpdate(INSERT_TXS, txsRows);

      results.logf("  Restored: %d tx_details, %d txs, %d artifacts, %d attributes, %d relations, %d relations2",
         txDetailsCount, txsCount, artCount, attrCount, relCount, rel2Count);
   }

   private void skipTransactionData(DataInputStream dis) throws IOException {
      // Skip TX_DETAILS
      dis.readUTF();
      int txDetailsCount = dis.readInt();
      for (int i = 0; i < txDetailsCount; i++) {
         readTxDetailsRow(dis);
      }

      // Skip TXS_DATA
      dis.readUTF();
      int txsCount = dis.readInt();
      for (int i = 0; i < txsCount; i++) {
         readTxsRow(dis);
      }

      // Skip ARTIFACTS
      dis.readUTF();
      int artCount = dis.readInt();
      for (int i = 0; i < artCount; i++) {
         dis.readLong(); dis.readLong(); dis.readLong(); dis.readUTF();
      }

      // Skip ATTRIBUTES
      dis.readUTF();
      int attrCount = dis.readInt();
      for (int i = 0; i < attrCount; i++) {
         dis.readLong(); dis.readLong(); dis.readLong(); dis.readLong(); dis.readUTF(); dis.readUTF();
      }

      // Skip RELATIONS
      dis.readUTF();
      int relCount = dis.readInt();
      for (int i = 0; i < relCount; i++) {
         dis.readLong(); dis.readLong(); dis.readLong(); dis.readLong(); dis.readLong(); dis.readUTF();
      }

      // Skip RELATIONS2
      dis.readUTF();
      int rel2Count = dis.readInt();
      for (int i = 0; i < rel2Count; i++) {
         dis.readLong(); dis.readLong(); dis.readLong(); dis.readLong(); dis.readInt(); dis.readLong();
      }

      dis.readUTF(); // "TX_END"
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

   private Object[] readTxsRow(DataInputStream dis) throws IOException {
      return new Object[] {
         dis.readLong(),  // BRANCH_ID
         dis.readLong(),  // GAMMA_ID
         dis.readLong(),  // TRANSACTION_ID
         dis.readShort(), // TX_CURRENT
         dis.readShort(), // MOD_TYPE
         dis.readLong()   // APP_ID
      };
   }
}
