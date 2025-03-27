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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.jdbc.SqlTable;
import org.eclipse.osee.orcs.OseeDb;

/**
 * Purge artifact, attribute, and relation versions that are not addressed or nonexistent and purge empty transactions.
 * Additionally purge search tags referencing non-existent gammas.
 *
 * @author Ryan D. Brooks
 */
public class PurgeUnusedBackingDataAndTransactions {

   private static final String INSERT_VALIDATED_GAMMAS =
      "insert into osee_validate_gamma_id select gamma_id from %s t1 where not exists (select null from osee_validate_gamma_id vgi where vgi.gamma_id = t1.gamma_id) ORDER BY gamma_id fetch first %s rows ONLY";
   private static final String INSERT_VALIDATED_GAMMAS_ART_REFERENCES =
      "insert into osee_validate_gamma_id(gamma_id) select gamma_id from (select gamma_id, art_id from (select distinct gamma_id, %s art_id from %s where gamma_id not in (select gamma_id from osee_validate_gamma_id)) t1 ) t2 fetch first %s rows only";
   private static final String OBSOLETE_TAGS =
      "select gamma_id from osee_search_tags tag where not exists (select 1 from osee_attribute att where tag.gamma_id = att.gamma_id)  fetch first %s rows ONLY";

   private static final String NOT_ADDRESSED_GAMMAS =
      "with items as (select gamma_id from %s t1 where t1.gamma_id not in (select gamma_id from osee_validate_gamma_id) ORDER BY gamma_id fetch first %s rows only)" + "select gamma_id from items where not exists (select gamma_id from osee_txs txs where txs.gamma_id = items.gamma_id) " + //
         "and not exists (select gamma_id from osee_txs_archived txs where txs.gamma_id = items.gamma_id)";

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
      "with gammas_to_check(gamma_id, art_id) as (select gamma_id, art_id from (select gamma_id, art_id from (select distinct gamma_id, %s art_id from %s where gamma_id not in (select gamma_id from osee_validate_gamma_id) ) t1 ) t2 fetch first %s rows only) " + //
         "select * from gammas_to_check gtc where not exists (select 1 from osee_artifact art where art.art_id = gtc.art_id)";

   private static final String GET_INVALID_ART_REFERENCES_ACL =
      "select item.art_id from osee_artifact_acl item where not exists (select 1 from osee_artifact art where art.art_id = item.art_id)";

   private static final String DELETE_ACL = "DELETE FROM osee_artifact_acl WHERE art_id = ?";

   private final JdbcClient jdbcClient;

   List<String> insertStatements = new ArrayList<>();
   List<String> uriResourcesToDelete = new ArrayList<>();

   public PurgeUnusedBackingDataAndTransactions(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   private int purgeNotAddressedGammas(JdbcConnection connection, SqlTable tableName, int rowCount) {
      String selectSql = String.format(NOT_ADDRESSED_GAMMAS, tableName, rowCount);
      String insertValidatedGammas = String.format(INSERT_VALIDATED_GAMMAS, tableName, rowCount);
      int rowsPurged = purgeGammas(connection, selectSql, tableName);
      jdbcClient.runPreparedUpdate(insertValidatedGammas);
      return rowsPurged;
   }

   private int purgeGammasByList(JdbcConnection connection, SqlTable table, List<Long> gammasToDelete) {
      String purgeSQL = String.format(DELETE_GAMMAS, table);
      String insertSelectSql = table.getSelectInsertString(" where gamma_id = ?");

      List<Object[]> data = gammasToDelete.stream().map(a -> new Object[] {a}).collect(Collectors.toList());

      for (Object[] gamma : data) {
         jdbcClient.runQuery(stmt -> insertStatements.add(stmt.getString("insertString")), insertSelectSql, gamma);
      }

      jdbcClient.runBatchUpdate(purgeSQL, data);

      return 0;
   }

   private int purgeAddressedButNonexistentGammas(JdbcConnection connection, SqlTable table) {
      return purgeData(connection, String.format(NONEXISTENT_GAMMAS, table),
         String.format(DELETE_GAMMAS_BY_BRANCH, table), this::addBranchGamma, table);
   }

   private int purgeEmptyTransactions(JdbcConnection connection) {
      return purgeData(connection, EMPTY_TRANSACTIONS, DELETE_EMPTY_TRANSACTIONS, this::addTx, OseeDb.TX_DETAILS_TABLE);
   }

   private int deleteObsoleteTags(JdbcConnection connection, int rowCount) {
      return purgeGammas(connection, String.format(OBSOLETE_TAGS, rowCount), OseeDb.OSEE_SEARCH_TAGS_TABLE);
   }

   private int purgeInvalidArtifactReferences(JdbcConnection connection, SqlTable table, String artColumn,
      int rowCount) {
      String selectSql = String.format(GET_INVALID_ART_REFERENCES, artColumn, table, rowCount);
      int rowsPurged = purgeGammas(connection, selectSql, table);
      String insertValidatedGammas = String.format(INSERT_VALIDATED_GAMMAS_ART_REFERENCES, artColumn, table, rowCount);
      jdbcClient.runPreparedUpdate(insertValidatedGammas);
      return rowsPurged;
   }

   private int purgeInvalidArtfactReferencesAcl(JdbcConnection connection) {
      return purgeData(connection, GET_INVALID_ART_REFERENCES_ACL, DELETE_ACL, this::addArt,
         OseeDb.OSEE_ARTIFACT_ACL_TABLE);
   }

   private int purgeGammas(JdbcConnection connection, String selectSql, SqlTable table) {
      return purgeData(connection, selectSql, String.format(DELETE_GAMMAS, table), this::addGamma, table);
   }

   private int purgeData(JdbcConnection connection, String selectSql, String purgeSQL,
      BiConsumer<OseePreparedStatement, JdbcStatement> consumer, SqlTable table) {

      String insertSelectSql = table.getSelectInsertString(" where gamma_id = ?");
      List<Object[]> data = new LinkedList<>();

      jdbcClient.runQuery(stmt -> data.add(new Object[] {stmt.getLong("gamma_id")}), selectSql);
      for (Object[] gamma : data) {
         jdbcClient.runQuery(stmt -> insertStatements.add(stmt.getString("insertString")), insertSelectSql, gamma);
      }

      int purgedRows = jdbcClient.runBatchUpdate(purgeSQL, data);
      return purgedRows;
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

   public int[] purgeUnused(int rowCount) {

      String serverPath = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverPath == null) {
         serverPath = System.getProperty("user.home");
      }
      if (serverPath.equals("null")) {
         return null;
      }
      Path purgeFolder = Paths.get(serverPath + File.separator + "purge");
      if (Files.exists(purgeFolder)) {
         serverPath = serverPath + File.separator + "purge";
      }
      int i = 0;
      int[] counts = new int[11];
      try (JdbcConnection connection = jdbcClient.getConnection()) {
         counts[i++] = purgeNotAddressedGammas(connection, OseeDb.ARTIFACT_TABLE, rowCount);
         counts[i++] = purgeNotAddressedGammas(connection, OseeDb.ATTRIBUTE_TABLE, rowCount);
         counts[i++] = purgeNotAddressedGammas(connection, OseeDb.RELATION_TABLE, rowCount);
         counts[i++] = purgeNotAddressedGammas(connection, OseeDb.RELATION_TABLE2, rowCount);
         counts[i++] = purgeInvalidArtifactReferences(connection, OseeDb.RELATION_TABLE, "a_art_id", rowCount);
         counts[i++] = purgeInvalidArtifactReferences(connection, OseeDb.RELATION_TABLE, "b_art_id", rowCount);
         counts[i++] = purgeInvalidArtifactReferences(connection, OseeDb.RELATION_TABLE2, "a_art_id", rowCount);
         counts[i++] = purgeInvalidArtifactReferences(connection, OseeDb.RELATION_TABLE2, "b_art_id", rowCount);
         counts[i++] = purgeInvalidArtifactReferences(connection, OseeDb.ATTRIBUTE_TABLE, "art_id", rowCount);
         counts[i++] = purgeInvalidArtfactReferencesAcl(connection);
         counts[i++] = deleteObsoleteTags(connection, rowCount);
         /**
          * TODO:Need to come up with efficient scheme to purge rows in osee_txs and txs_archived
          */
         //counts[i++] = purgeAddressedButNonexistentGammas(connection, "osee_txs");
         //counts[i++] = purgeAddressedButNonexistentGammas(connection, "osee_txs_archived");
         //counts[i++] = purgeEmptyTransactions(connection);
      }
      if (jdbcClient.getConfig().isProduction()) {
         Date date = new Date();
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
         String fileName = serverPath + File.separator + "insertStatements_" + dateFormat.format(date) + ".zip";
         File file = new File(fileName);
         try {
            file.createNewFile();

            FileOutputStream fop = new FileOutputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(fop);

            try (Writer writer = new OutputStreamWriter(zipOut)) {
               PrintWriter textWriter = new PrintWriter(writer);
               addFileToZip(zipOut, OseeDb.ARTIFACT_TABLE.getName(), insertStatements, textWriter);
               addFileToZip(zipOut, OseeDb.ATTRIBUTE_TABLE.getName(), insertStatements, textWriter);
               addFileToZip(zipOut, OseeDb.RELATION_TABLE.getName(), insertStatements, textWriter);
               addFileToZip(zipOut, OseeDb.RELATION_TABLE2.getName(), insertStatements, textWriter);
               addFileToZip(zipOut, OseeDb.OSEE_SEARCH_TAGS_TABLE.getName(), insertStatements, textWriter);
               zipOut.putNextEntry(new ZipEntry("ServerResourcesToDelete.txt"));
               for (String string : uriResourcesToDelete) {
                  textWriter.println(string);
               }
               textWriter.flush();
               zipOut.closeEntry();
            }
            fop.flush();
            fop.close();
         } catch (IOException ex) {
            throw OseeCoreException.wrap(ex);
         }
      }
      return counts;
   }

   public int[] purgeListOfGammas(List<Long> gammasToPurge, List<String> additionalStatements, String filePrefix) {
      String serverPath = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverPath == null) {
         serverPath = System.getProperty("user.home");
      }
      if (serverPath.equals("null")) {
         return null;
      }
      Path purgeFolder = Paths.get(serverPath + File.separator + "purge");
      if (Files.exists(purgeFolder)) {
         serverPath = serverPath + File.separator + "purge";
      }
      int i = 0;
      int[] counts = new int[5];
      if (gammasToPurge.size() > 0) {
         try (JdbcConnection connection = jdbcClient.getConnection()) {
            counts[i++] = purgeGammasByList(connection, OseeDb.ARTIFACT_TABLE, gammasToPurge);
            counts[i++] = purgeGammasByList(connection, OseeDb.ATTRIBUTE_TABLE, gammasToPurge);
            counts[i++] = purgeGammasByList(connection, OseeDb.RELATION_TABLE, gammasToPurge);
            counts[i++] = purgeGammasByList(connection, OseeDb.RELATION_TABLE2, gammasToPurge);
            counts[i++] = purgeGammasByList(connection, OseeDb.OSEE_SEARCH_TAGS_TABLE, gammasToPurge);
         }
      }
      insertStatements.addAll(additionalStatements);
      if (Strings.isValid(filePrefix)) {
         Date date = new Date();
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
         String fileName = serverPath + File.separator + "" + filePrefix + "_" + dateFormat.format(date) + ".zip";
         File file = new File(fileName);
         try {
            file.createNewFile();
            FileOutputStream fop = new FileOutputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(fop);

            try (Writer writer = new OutputStreamWriter(zipOut)) {
               PrintWriter textWriter = new PrintWriter(writer);
               addFileToZip(zipOut, OseeDb.ARTIFACT_TABLE.getName(), insertStatements, textWriter);
               addFileToZip(zipOut, OseeDb.ATTRIBUTE_TABLE.getName(), insertStatements, textWriter);
               addFileToZip(zipOut, OseeDb.RELATION_TABLE.getName(), insertStatements, textWriter);
               addFileToZip(zipOut, OseeDb.RELATION_TABLE2.getName(), insertStatements, textWriter);
               if (additionalStatements.size() > 0) {
                  zipOut.putNextEntry(new ZipEntry("additionalInsertStatements.sql"));
                  for (String string : additionalStatements) {
                     textWriter.println(string);
                  }
                  textWriter.flush();
                  zipOut.closeEntry();
               }

               zipOut.putNextEntry(new ZipEntry("ServerResourcesToDelete.txt"));
               for (String string : uriResourcesToDelete) {
                  textWriter.println(string);
               }
               textWriter.flush();
               zipOut.closeEntry();
            }
            fop.flush();
            fop.close();
         } catch (IOException ex) {
            throw OseeCoreException.wrap(ex);
         }
      }
      return counts;
   }

   private void addFileToZip(ZipOutputStream zipOut, String tableName, List<String> purge, PrintWriter textWriter)
      throws IOException {
      zipOut.putNextEntry(new ZipEntry("insert_" + tableName + ".sql"));
      for (String string : purge.stream().filter(a -> a.contains("INSERT INTO " + tableName + " (")).collect(
         Collectors.toList())) {
         if (string.contains("osee_attribute")) {
            String uri = string.substring(string.lastIndexOf(",") + 2, string.lastIndexOf("'"));
            if (!uri.isBlank()) {
               uriResourcesToDelete.add(uri);
            }
         }
         textWriter.println(string);
      }
      textWriter.flush();
      zipOut.closeEntry();

   }
}