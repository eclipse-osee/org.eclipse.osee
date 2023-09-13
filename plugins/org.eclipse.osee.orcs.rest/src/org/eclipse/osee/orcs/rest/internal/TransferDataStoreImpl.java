/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.ExportedBranch;
import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.TransferFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.rest.model.transaction.BranchLocation;
import org.eclipse.osee.orcs.rest.model.transaction.ManifestData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransferDBType;
import org.eclipse.osee.orcs.rest.model.transaction.TransferOpType;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.TupleQuery;

public class TransferDataStoreImpl {

   private BranchId branchId;
   private TransactionId localTx;
   private TransactionId uniqueTx;
   private TransferOpType transferOpType;
   private OrcsApi orcsApi;
   private int index;
   private String transferDirectoryName;
   private String fileName;
   private String dirName;
   private String json;

   private TupleQuery tupleQuery;

   private final TransactionEndpointImpl transEndpoint;
   private final List<ManifestData> manifestData = new ArrayList<>();
   private final List<TransferFileRow> transferFileRows = new ArrayList<>();

   public TransferDataStoreImpl(TransactionEndpointImpl transEndpoint) {
      this.transEndpoint = transEndpoint;
   };

   private XResultData getPreviousTransaction() {
      return new XResultData();
   }

   public final XResultData transferTransactions(TransactionId exportId, XResultData results) {
      List<TransactionId> txIds = new ArrayList<>();
      List<TransactionReadable> txrs = new ArrayList<>();
      List<BranchLocation> branchLocations = branchLocations(exportId);
      String currentTransferDirectory = transferDirectoryHelper();
      int errorCount = results.getErrorCount();

      if (!branchLocations.isEmpty()) {
         for (BranchLocation branchLoc : branchLocations) {
            BranchId branchId = branchLoc.getBranchId();
            TransactionId txId = branchLoc.getBaseTxId();
            tupleQuery.getTuple4E3E4FromE1E2(TransferFile, CoreBranches.COMMON, branchId, TransferOpType.PREV_TX,
               (E3, E4) -> {
                  txIds.add(E3);
               });
            int iniTxId = txId.getIdIntValue();
            txrs = orcsApi.getQueryFactory().transactionQuery().andBranch(branchId).andTxId(Operator.GREATER_THAN,
               iniTxId).getResults().getList();
            TransactionId endTrans = applyTransactions(txrs, txId, results);
            if (errorCount == 0 && endTrans != null) {
               //TODO
               // Once transaction applied add tuples from transferFileRows

               // Update Exported Branch Table with Last Transaction Tuple 4 Entry tuple type 101 with same E's as Original transfer using Last (endTrans)

               // Build Manifest Data

               // Commit the Transactions in the transfer

               // txTupleTransfer.commit();

               String buildId = "10000";
               // Build Manifest File see design doc for additional info
               buildManifestFileHelper(currentTransferDirectory, buildId, exportId, manifestData);
            }

         }
      }

      return results;

   }

   private TransactionId applyTransactions(List<TransactionReadable> txrs, TransactionId initialTx,
      XResultData results) {
      Collections.sort(txrs, new TransactionIdComparator());
      TransactionId lastTx = TransactionId.SENTINEL;
      if (!txrs.isEmpty()) {
         // If Initial Has more than one this is an error exception should be thrown
         int last = txrs.size() - 1;
         lastTx = txrs.get(last);
         String currentTransferDirectory = transferDirectoryHelper();
         try {
            results = buildJSONExportDataHelper(txrs, initialTx, lastTx, branchId, currentTransferDirectory,
               manifestData, results);
         } catch (JsonProcessingException ex) {
            System.out.print(ex.getMessage());
         }
      }
      int errors = results.getErrorCount();
      if (errors != 0) {
         lastTx = null;
      }

      return lastTx;
   }

   // Query Branch Base Transaction based off supplied branch given
   // For each branch configured in the export ID return the maximum base transaction ID for that branch.
   private final List<BranchLocation> branchLocations(TransactionId exportId) {
      List<BranchLocation> branchLocations = new ArrayList<>();
      List<TransferDBType> transferTypes = new ArrayList<>();

      tupleQuery.getTuple4E2E3E4FromE1(ExportedBranch, CoreBranches.COMMON, exportId, (E2, E3, E4) -> {
         BranchLocation bl = new BranchLocation();
         bl.setBranchId(E2);
         bl.setBaseTxId(E3);
         branchLocations.add(bl);
         transferTypes.add(E4);
      });
      return getMaxBranchLocations(branchLocations);
   }

   // Set base tx to max
   private final List<BranchLocation> getMaxBranchLocations(List<BranchLocation> branchLocations) {
      List<BranchLocation> bLocs = new ArrayList<>();
      Map<BranchId, TransactionId> Hmap = new HashMap<>();
      for (BranchLocation branchLoc : branchLocations) {
         // Check each branch and retrieve highest tx
         if (Hmap.containsKey(branchLoc.getBranchId())) {
            TransactionId test = Hmap.get(branchLoc.getBranchId());
            if (test.isLessThan(branchLoc.getBaseTxId())) {
               Hmap.replace(branchLoc.getBranchId(), branchLoc.getBaseTxId());
            }
         } else {
            Hmap.put(branchLoc.getBranchId(), branchLoc.getBaseTxId());
         }
      }
      for (Map.Entry<BranchId, TransactionId> entry : Hmap.entrySet()) {
         BranchLocation bl = new BranchLocation();
         bl.setBranchId(entry.getKey());
         bl.setBaseTxId(entry.getValue());
         bLocs.add(bl);

      }
      return bLocs;
   }

   private boolean verifyValidTransactionToTransferHelper(String transactionComment) {
      String[] commentsToCheckAgainst =
         {"Adding transfer tuples", "User - Save Settings (IDE)", "Adding exportId to Tuple2Table"};

      for (int i = 0; i <= commentsToCheckAgainst.length - 1; i++) {
         if (commentsToCheckAgainst[i].equalsIgnoreCase(transactionComment.strip())) {
            return false;
         }
      }
      return true;
   }

   private String transferDirectoryHelper() {
      // Transfer File Naming Convention: OSEETransfer-YYYYMMDDhhmmss-#### where #### is a random to make the file unique.
      Random rand = new Random();
      String randomFour = String.format("%04d", rand.nextInt(10000));
      SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMddHHmmss");
      Date transferDateAndTime = new Date();
      String osee = orcsApi.getSystemProperties().getValue(
         OseeClient.OSEE_APPLICATION_SERVER_DATA) + File.separator + "transfers" + File.separator;
      return osee + "OSEETransfer-" + formatter.format(transferDateAndTime) + "-" + randomFour + File.separator;
   }

   private XResultData buildJSONExportDataHelper(List<TransactionReadable> txrs, TransactionId initialTx,
      TransactionId lastTx, BranchId branchId, String transferDirectory, List<ManifestData> manifestData,
      XResultData results) throws JsonProcessingException {

      TransactionBuilderData tbd = null;
      String json = null;
      for (TransactionReadable tx : txrs) {
         boolean validTxToProcess = verifyValidTransactionToTransferHelper(tx.getComment());
         ManifestData md = new ManifestData();
         TransactionId uniqueTx = TransactionId.valueOf(Lib.generateUuid());
         String fileName = tx.getIdString() + ".json";
         String path = transferDirectory + branchId + File.separator + fileName;
         if (validTxToProcess) {
            try {
               tbd = transEndpoint.exportTxsDiff(initialTx, tx); // This is the JSON send this mapper to return as a string
               System.out.print("TBD diff build Content: " + tbd.toString() + "\n");
            } catch (OseeCoreException ex) {
               System.out.print("Error in TBD diff build: " + ex + "\n");
               // Store into New Data Container below as a row in the list
               transferFileRows.add(new TransferFileRow(branchId, TransferOpType.EMPTY, tx, uniqueTx));
               // txTupleTransfer.addTuple4(TransferFile, branchId, TransferOpType.EMPTY, tx, uniqueTx);
               md.setTransferOpType(TransferOpType.EMPTY);
               md.setLocalTx(tx); // Populate Manifest with Tx & TransferOpType
               // Not at last transaction set initial to current tx (previous for diff on next loop)
               initialTx = tx;
               tbd = null; // Skip JSON output as this will be empty in the table (Manifest data only to populate)
            }
            if (tbd != null) {

               ObjectMapper mapper = new ObjectMapper();
               json = mapper.writeValueAsString(tbd);
               System.out.print(
                  "TBD Build Successful Value:" + tbd.toString() + " | Expected JSON Output: " + json + "\n");
               // Attempt to create file / directory structure if they do not exist
               try {
                  Path directoryPath = Paths.get(transferDirectory + branchId + File.separator);
                  if (!Files.exists(directoryPath)) {
                     Files.createDirectories(directoryPath);
                  }
                  File file = new File(path);
                  if (!file.exists()) {
                     file.createNewFile();
                  }
                  // Use OSEE Lib to write to file
                  Lib.writeStringToFile(json, file);
                  //                  System.out.println("\nFile Successfully Written: " + file + " \n ");
                  md.setDirName(branchId.toString());
                  md.setFileName(fileName);
               } catch (Exception ex) {

                  results.errorf("%s",
                     String.format("Error in writing file: ", ex.getMessage() + " location: " + path));
                  System.out.println("Error in writing to file at: " + path + " Exception: " + ex);
               }
            }
            long current = tx.getId();
            long next = lastTx.getId();
            // Check if at last transaction
            if (current == next) {
               md.setLocalTx(tx); // Populate Manifest with Tx
               // Re-check if TBD is null JSON diff output exists and populate with appropriate TransferOpType
               if (tbd != null) {
                  md.setTransferOpType(TransferOpType.PREV_TX);
                  // Add tx to Tuple4Table via tupleAdd4
                  // txTupleTransfer.addTuple4(TransferFile, branchId, TransferOpType.PREV_TX, tx, uniqueTx);
                  transferFileRows.add(new TransferFileRow(branchId, TransferOpType.PREV_TX, tx, uniqueTx));
               }

            } else {
               md.setLocalTx(tx); // Populate Manifest with Tx
               // Re-check if TBD is null JSON diff output exists and populate with appropriate TransferOpType
               if (tbd != null) {
                  md.setTransferOpType(TransferOpType.ADD);
                  // Add tx to Tuple4Table via tupleAdd4
                  // txTupleTransfer.addTuple4(TransferFile, branchId, TransferOpType.ADD, tx, uniqueTx);
                  transferFileRows.add(new TransferFileRow(branchId, TransferOpType.ADD, tx, uniqueTx));
                  // Not at last transaction set initial to current tx (previous for diff on next loop)
                  initialTx = tx;
               }
            }
            // Populate remaining available manifest data
            md.setBranchId(branchId);
            md.setUniqueTx(uniqueTx);
            md.setIndex(md.getIndex() + 1);
            manifestData.add(md);
         }
      }
      return results;
   }

   private void buildManifestFileHelper(String transferDirectory, String buildId, TransactionId exportId,
      List<ManifestData> manifestData) {
      Map<String, ArrayList<String>> Hmap = new HashMap<String, ArrayList<String>>();
      // File Output String
      String fileOutput = "";
      // New Line
      String newLine = "\n";
      // MD |
      String markdownPipe = " | ";
      String markdownSeparator = "| ------------- |:-----------------:| ------------------:| :-----:|";
      String filePath = transferDirectory + "manifest.MF";
      String dirName = "";
      String fileName = "";
      // Check if build id not null
      if (buildId != null) {
         fileOutput += "BuildId: " + buildId + newLine;
         fileOutput += "ExportId: " + exportId.toString() + newLine;
         if (!manifestData.isEmpty()) {
            fileOutput +=
               "| BranchId" + markdownPipe + "LocalTX" + markdownPipe + "UniqueTX" + markdownPipe + "Op" + markdownPipe + newLine + markdownSeparator + newLine;
            for (ManifestData md : manifestData) {
               fileOutput +=
                  markdownPipe + md.getBranchId() + markdownPipe + md.getLocalTx().getIdString() + markdownPipe + md.getUniqueTx().getIdString() + markdownPipe + md.getTransferOpType() + markdownPipe + newLine;
               dirName = md.getDirName();
               fileName = md.getFileName();
               if (Hmap.containsKey(dirName)) {
                  Hmap.get(dirName).add(fileName);
               } else {
                  Hmap.put(dirName, new ArrayList<String>());
                  Hmap.get(dirName).add(fileName);
               }
            }
         }
         fileOutput += "\nList of Directories/Contents\n";

         for (Map.Entry<String, ArrayList<String>> entry : Hmap.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
               fileOutput += newLine + " * " + entry.getKey() + newLine;
               String files = String.join(",", entry.getValue().toString());
               try {
                  files = "\n \t * " + files.replaceAll(",", "\n \t *");
                  files = files.replaceAll("[\\[\\](){}]", "");
               } catch (Exception ex) {
                  System.out.println("Error in string replace. Exception: " + ex.getMessage());
               }
               fileOutput += files + newLine;
            }
         }

         try {
            Path directoryPath = Paths.get(transferDirectory);
            if (!Files.exists(directoryPath)) {
               Files.createDirectories(directoryPath);
            }
            File file = new File(filePath);
            if (!file.exists()) {
               file.createNewFile();
            }
            // Use OSEE Lib to write to file
            Lib.writeStringToFile(fileOutput, file);
         } catch (Exception ex) {
            System.out.println(
               "Error in writing to file at: " + filePath + " Exception: " + ex.getMessage() + " Time: " + LocalTime.now());
         }
      }
      manifestData = null;
   }

   private static final class TransactionIdComparator implements Comparator<TransactionId> {
      @Override
      public int compare(TransactionId arg0, TransactionId arg1) {
         return arg0.getId().compareTo(arg1.getId());
      }
   };

   private final class TransferFileRow {
      private final BranchId branchId;
      private final TransferOpType transferOpType;
      private final TransactionId localId;
      private final TransactionId uniqueId;

      // Exported Branch, Transfer Type, Exported Transaction, Unique Common Transaction
      public TransferFileRow(BranchId branchId, TransferOpType transferOpType, TransactionId localId, TransactionId uniqueId) {
         this.branchId = branchId;
         this.transferOpType = transferOpType;
         this.localId = localId;
         this.uniqueId = uniqueId;
      }
   };

}
