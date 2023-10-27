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
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

public class TransferDataStoreImpl {

   private final OrcsApi orcsApi;
   private final TupleQuery tupleQuery;
   private final TransactionEndpointImpl transEndpoint;
   private final List<ManifestData> manifestData = new ArrayList<>();
   private final List<TransferFileRow> transferFileRows = new ArrayList<>();

   public TransferDataStoreImpl(TransactionEndpointImpl transEndpoint, OrcsApi orcsApi) {
      this.transEndpoint = transEndpoint;
      this.orcsApi = orcsApi;
      this.tupleQuery = orcsApi.getQueryFactory().tupleQuery();
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

      TransactionId maxTransactionNum = TransactionId.SENTINEL;

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
            maxTransactionNum = txrs.get(txrs.size() - 1);
            if (maxTransactionNum == txId || maxTransactionNum == TransactionId.SENTINEL) {
               results.error(
                  "Error in generating transfer files, ending (max) transaction is empty or equivalent to starting (base) transaction");
               break;
            } else {
               applyTransactions(txrs, txId, results, branchId, currentTransferDirectory, maxTransactionNum);
            }
            // Update Exported Branch Table with Last Transaction Tuple 4 Entry tuple type 101 with same E's as Original transfer using Last (endTrans)
            updateTransferFileRowPrevTX(branchId, maxTransactionNum, exportId);
         }
      }

      if (errorCount == 0) {
         try {
            // Once transaction applied add tuples from transferFileRows
            TransactionBuilder txTupleTransfer =
               orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Adding transfer tuples");
            for (TransferFileRow row : transferFileRows) {
               txTupleTransfer.addTuple4(TransferFile, row.getBranchId(), row.getTransferOpType(),
                  row.getTransactionId(), row.getUniqueId());
            }

            // Commit the Transactions in the transfer
            txTupleTransfer.commit();

            String buildId = "10000";
            // Build Manifest File see design doc for additional info
            buildManifestFileHelper(currentTransferDirectory, buildId, exportId, manifestData, results,
               maxTransactionNum);
         } catch (Exception ex) {
            System.out.println("Exception thrown in adding tuples: " + ex.getMessage());
            results.errorf("%s", "Error in addding tuples", ex.getMessage());
         }

      } else {
         results.addRaw("Transfer Failed, retry again");
         results.setWarningCount(1);
         results.setErrorCount(0); // Unlocks for next transfer
      }

      System.out.println("\nResults:\t" + results.toString());

      return results;
   }

   private XResultData applyTransactions(List<TransactionReadable> txrs, TransactionId initialTx, XResultData results,
      BranchId branchId, String currentTransferDirectory, TransactionId endTrans) {
      Collections.sort(txrs, new TransactionIdComparator());
      if (!txrs.isEmpty() && endTrans != TransactionId.SENTINEL) {
         try {
            buildJSONExportDataHelper(txrs, initialTx, endTrans, branchId, currentTransferDirectory, manifestData,
               results);
         } catch (JsonProcessingException ex) {
            System.out.print("Error in JSON Build: " + ex.getMessage());
         }
      }
      return results;
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
         {"Adding transfer tuples", "User - Save Settings (IDE)", "Adding exportId to Tuple2Table", "Delete tuple"};

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
         System.out.println(String.format("workfing tx %s", tx.getIdString()));
         String path = transferDirectory + branchId + File.separator + fileName;
         if (validTxToProcess) {
            try {
               tbd = transEndpoint.exportTxsDiff(initialTx, tx); // This is the JSON send this mapper to return as a string
            } catch (OseeCoreException ex) {
               // Store Tuple Data into transferFilRows
               transferFileRows.add(new TransferFileRow(branchId, TransferOpType.EMPTY, tx, uniqueTx));
               //  Populate Manifest with Tx & TransferOpType
               md.setTransferOpType(TransferOpType.EMPTY);
               md.setLocalTx(tx);
               tbd = null; // Skip JSON output as this will be empty in the table (Manifest data only to populate)
            }
            if (tbd != null) {

               ObjectMapper mapper = new ObjectMapper();
               json = mapper.writeValueAsString(tbd);
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
                  md.setDirName(branchId.toString());
                  md.setFileName(fileName);
               } catch (Exception ex) {
                  results.errorf("%s",
                     String.format("Error in writing file: ", ex.getMessage() + " location: " + path));
               }
            }
            long current = tx.getId();
            // Check if at last transaction
            if (current == lastTx.getId()) {
               md.setLocalTx(tx); // Populate Manifest with Tx
               // Re-check if TBD is null JSON diff output exists and populate with appropriate TransferOpType
               if (tbd != null) {
                  md.setTransferOpType(TransferOpType.PREV_TX);
                  // Store Tuple Data into transferFilRows
                  transferFileRows.add(new TransferFileRow(branchId, TransferOpType.PREV_TX, tx, uniqueTx));
               }

            } else {
               md.setLocalTx(tx); // Populate Manifest with Tx
               // Re-check if TBD is null JSON diff output exists and populate with appropriate TransferOpType
               if (tbd != null) {
                  md.setTransferOpType(TransferOpType.ADD);
                  // Store Tuple Data into transferFilRows
                  transferFileRows.add(new TransferFileRow(branchId, TransferOpType.ADD, tx, uniqueTx));
               }
               // Not at last transaction set initial to current tx (previous for diff on next loop) "iterate" to next transaction number
               initialTx = tx;
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

   private XResultData buildManifestFileHelper(String transferDirectory, String buildId, TransactionId exportId,
      List<ManifestData> manifestData, XResultData results, TransactionId maxTransaction) {
      Map<String, ArrayList<String>> Hmap = new HashMap<String, ArrayList<String>>();
      // File Output String
      String fileOutput = "";
      // New Line
      String newLine = "\n";
      // MD |
      String markdownPipe = " | ";
      String markdownSeparator = "| ------------- |:-----------------:| ------------------:| :-----:|";
      String filePath = transferDirectory + "manifest.md";
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
                  results.errorf("%s", "Error in string replace. Exception: ", ex.getMessage());
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
            results.errorf("%s", "Error in writting manifest file. Exception: ", ex.getMessage());
         }
      }
      if (results.getErrorCount() == 0) {
         results.addRaw(
            "\nManifest File written, transfer files generated, files generated check database for completion!");
      }
      manifestData = null;
      return results;
   }

   private void updateTransferFileRowPrevTX(BranchId branchId, TransactionId endTx, TransactionId exportId) {
      int index = 0;
      boolean flag = false;
      for (TransferFileRow row : transferFileRows) {
         if (row.getBranchId() == branchId && row.getTransferOpType() == TransferOpType.PREV_TX) {
            flag = true;
            break;
         } else {
            index++;
         }
      }
      if (flag == true) {
         transferFileRows.get(index).setTransactionId(endTx);
      } else {
         // Overwrite last transaction as PrevTx
         try {
            if (transferFileRows.size() <= index) {
               index = transferFileRows.size() - 1;
               for (int i = index; i >= 0; i--) {
                  index = i;
                  if (transferFileRows.get(index).getBranchId() == branchId && transferFileRows.get(
                     index).getTransferOpType() != TransferOpType.PREV_TX) {
                     transferFileRows.get(index).setTransferOpType(TransferOpType.PREV_TX);
                     transferFileRows.get(index).setTransactionId(endTx);
                  }

               }
            }
         } catch (Exception ex) {
            throw new OseeCoreException("Error in appending prevtx: " + ex.getMessage());
         }
      }

   }

   private static final class TransactionIdComparator implements Comparator<TransactionId> {
      @Override
      public int compare(TransactionId arg0, TransactionId arg1) {
         return arg0.getId().compareTo(arg1.getId());
      }
   };

   private final class TransferFileRow {
      private final BranchId branchId;
      private TransferOpType transferOpType;
      private TransactionId localId;
      private final TransactionId uniqueId;

      // Exported Branch, Transfer Type, Exported Transaction, Unique Common Transaction
      public TransferFileRow(BranchId branchId, TransferOpType transferOpType, TransactionId localId, TransactionId uniqueId) {
         this.branchId = branchId;
         this.transferOpType = transferOpType;
         this.localId = localId;
         this.uniqueId = uniqueId;
      }

      public BranchId getBranchId() {
         return branchId;
      }

      public TransferOpType getTransferOpType() {
         return transferOpType;
      }

      public void setTransferOpType(TransferOpType transferOpType) {
         this.transferOpType = transferOpType;
      }

      public TransactionId getTransactionId() {
         return localId;
      }

      public void setTransactionId(TransactionId localId) {
         this.localId = localId;
      }

      public TransactionId getUniqueId() {
         return uniqueId;
      }

   };

}
