/*********************************************************************
 * Copyright (c) 2015 Boeing
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

import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asResponse;
import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.ExportedBranch;
import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.TransferFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.Transaction;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.BranchLocation;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.rest.model.transaction.TransferDBType;
import org.eclipse.osee.orcs.rest.model.transaction.TransferFileLockUtil;
import org.eclipse.osee.orcs.rest.model.transaction.TransferInitData;
import org.eclipse.osee.orcs.rest.model.transaction.TransferOpType;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public class TransactionEndpointImpl implements TransactionEndpoint {

   private final OrcsApi orcsApi;
   private final int PURGE_TXS_LIMIT = 1000;
   private final IResourceManager resourceManager;
   private final TupleQuery tupleQuery;
   private final TupleEndpointImpl tupleEndpointImpl;

   @Context
   private UriInfo uriInfo;
   public TransactionEndpointImpl(OrcsApi orcsApi, IResourceManager resourceManager) {
      this.orcsApi = orcsApi;
      this.resourceManager = resourceManager;
      this.tupleEndpointImpl = new TupleEndpointImpl(orcsApi, CoreBranches.COMMON);
      this.tupleQuery = orcsApi.getQueryFactory().tupleQuery();
   }

   protected void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   @Override
   public List<Transaction> getAllTxs() {
      return OrcsRestUtil.asTransactions(orcsApi.getTransactionFactory().getAllTxs());
   }

   @Override
   public Transaction getTx(TransactionId tx) {
      return OrcsRestUtil.asTransaction(orcsApi.getTransactionFactory().getTx(tx));
   }

   @Override
   public TransactionResult create(TransactionBuilder tx) {
      TransactionToken token = tx.commit();
      TransactionResult result = new TransactionResult();
      result.setTx(token);
      XResultData resultData = new XResultData();
      resultData.setTxId(token.getIdString());
      resultData.setIds(
         tx.getTxDataReadables().stream().map(readable -> readable.getIdString()).collect(Collectors.toList()));
      result.setResults(resultData);
      result.setFailedGammas(tx.getGammaIdsFailed());
      return result;
   }

   @Override
   public List<ChangeItem> compareTxs(TransactionId txId1, TransactionId txId2) {
      return orcsApi.getTransactionFactory().compareTxs(txId1, txId2);
   }

   @Override
   public TransactionBuilderData exportTxsDiff(TransactionId txId1, TransactionId txId2) {
      TransactionBuilderDataFactory tbdf = new TransactionBuilderDataFactory(orcsApi, resourceManager);
      try {
         return tbdf.loadFromChanges(txId1, txId2);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public Response setTxComment(TransactionId txId, String comment) {
      return OrcsRestUtil.asResponse(orcsApi.getTransactionFactory().setTxComment(txId, comment));
   }

   @Override
   public Response purgeTxs(String txIds) {
      orcsApi.userService().requireRole(CoreUserGroups.OseeAccessAdmin);
      return asResponse(orcsApi.getTransactionFactory().purgeTxs(txIds));
   }

   @Override
   public Response purgeUnusedBackingDataAndTransactions() {
      orcsApi.userService().requireRole(CoreUserGroups.OseeAccessAdmin);
      orcsApi.getTransactionFactory().purgeUnusedBackingDataAndTransactions();
      return Response.ok().build();
   }

   @Override
   public Response replaceWithBaselineTxVersion(BranchId branchId, TransactionId txId, ArtifactId artId,
      String comment) {
      return OrcsRestUtil.asResponse(
         orcsApi.getTransactionFactory().replaceWithBaselineTxVersion(branchId, txId, artId, comment));
   }

   @Override
   public List<ChangeItem> getArtifactHistory(ArtifactId artifact, BranchId branch) {
      return orcsApi.getTransactionFactory().getArtifactHistory(artifact, branch);
   }

   @Override
   public TransferInitData initTransactionTransfer(TransferInitData data) {
      if (data == null) {
         TransferInitData example = new TransferInitData();
         BranchLocation bl = new BranchLocation();
         BranchLocation blCommon = new BranchLocation();
         blCommon.setBaseTxId(TransactionId.valueOf(500));
         blCommon.setBranchId(BranchId.valueOf(570L));
         bl.setBaseTxId(TransactionId.valueOf(400));
         bl.setBranchId(BranchId.valueOf(8L));
         example.setBranchLocations(Arrays.asList(blCommon, bl));
         example.setExportId(TransactionId.valueOf(124388928743L));
         example.setTransferDBType(TransferDBType.SOURCE);
         XResultData results = new XResultData();
         results.error(
            "null input given - fill out the returned json and use it to set properly init the transaction transfer");
         example.setResults(results);
         return example;
      }
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Adding tuple for transfer init");
      List<BranchLocation> branchLocations = data.getBranchLocations();
      for (BranchLocation branchLoc : branchLocations) {
         // this sets up the branch as a exported branch with an export ID
         tx.addTuple4(ExportedBranch, data.getExportId(), branchLoc.getBranchId(), branchLoc.getBaseTxId(),
            data.getTransferDBType());
         tx.addTuple4(TransferFile, branchLoc.getBranchId(), TransferOpType.PREV_TX, branchLoc.getBaseTxId(),
            TransactionId.valueOf(Lib.generateUuid()));

      }
      tx.commit();
      //TODO add checks to make sure the transfer data has all necessary values
      // create new XResultData to collect any errors or info about whether or not this succeeds not
      // check to see if the export id is already used, and return error in XResultData if it is
      return data;
   }

   @Override
   public XResultData generateTransferFile(TransactionId exportId) {
      XResultData results = new XResultData();
      DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      Date date = new Date();

      // Method to check if transfer is locked
      results = transferActiveHelper(exportId, results);
      // Check to see if there is a transfer
      if (results.getErrorCount() >= 1) {
         results.errorf("%s", "Transfer in progress for exportID", exportId.getIdString());
         throw new OseeCoreException("Transfer in progress");
      } else {
         try {
            TransferDataStoreImpl transfer = new TransferDataStoreImpl(this);
            // Return XResultData
            results = transfer.transferTransactions(exportId, results);

         } catch (Exception e) {
            results.errorf("%s", String.format("Error in generating transfer files: ",
               e.getMessage() + " at time: " + dateFormat.format(date)));
            throw new OseeCoreException(
               "Error in generating transfer files, exception:  " + e + "\n At time: " + dateFormat.format(
                  date) + "\n");
         }
      }
      int errors = results.getErrorCount();
      if (errors == 0) {
         results.success("%s", String.format("Transfer files successfully generated at: ", dateFormat.format(date)));
      }
      if (results.isSuccess()) {
         results.clear();
         results.dispose();
      }
      return results;
   }

   private XResultData transferActiveHelper(TransactionId exportId, XResultData results) {
      try {
         results.logf("Checking if transfer currently in progress for exportID:: %s", exportId.getIdString());
         int warning = results.getWarningCount();
         int error = results.getErrorCount();
         boolean transferActive = TransferFileLockUtil.isLocked(orcsApi.getKeyValueOps(), exportId.getId());
         if (transferActive && (error != 0 && warning != 0)) {
            results.errorf("Transfer is active for the current export ID: %s", exportId);
         } else {
            // Transfer not in progress, begin lock
            TransferFileLockUtil.lock(orcsApi.getKeyValueOps(), exportId.getId());
            results.success("Transfer in progress");
         }
      } catch (Exception ex) {
         results.errorf("%s", String.format(
            "Error in checking if a transfer is active Current Export ID: " + exportId + " ", ex.getMessage()));
      }
      return results;
   }

   public XResultData applyTransferFile(String dirName) {
      XResultData results = new XResultData();
      File manifestFile = new File(String.format("%s%s%s", dirName, File.separator, "Manifest.md"));
      String manifest = null;
      ArrayList<String> dirs;
      ArrayList<ArrayList<String>> subfiles = new ArrayList<ArrayList<String>>();
      HashMap<String, String> txMap = new HashMap<String, String>();
      // Extract file directories
      try {
         manifest = Lib.fileToString(manifestFile);
         dirs = Lib.readListFromDir(dirName, null);
         for (int i = 0; i < dirs.size(); i++) {
            if (!dirs.get(i).equals("Manifest")) {
               ArrayList<String> tempfiles =
                  Lib.readListFromDir(String.format("%s%s%s", dirName, File.separator, dirs.get(i)), null);
               tempfiles.add(dirs.get(i));
               subfiles.add(tempfiles);
            }
         }
      } catch (IOException e) {
         results.errorf("%s",
            String.format("IO Exception while reading manifest and transaction files. %s", e.getMessage()));
         return results;
      }
      //validate Manifest
      String[] mdLines = manifest.split("\n");
      String strBuildId = "", strExportId = "";
      String strBranchId = "", strLocalTx = "", strUniqueTx = "", strOp = "";
      int readKeyCt = 0, subfileCt = 0;
      try {
         for (int i = 0; i < mdLines.length; i++) {
            if (mdLines[i].contains("BuildID")) {
               strBuildId = mdLines[i].split(":")[1].trim();
               readKeyCt++;
               continue;
            }
            if (mdLines[i].contains("ExportID")) {
               strExportId = mdLines[i].split(":")[1].trim();
               readKeyCt++;
               continue;
            }
            if (mdLines[i].contains("PrevTX")) {
               //verify subfiles ct from previous branch id
               if (subfileCt != 0) {
                  for (int j = 0; j < subfiles.size(); j++) {
                     if (subfiles.get(j).get(subfiles.get(j).size() - 1).equals(strBranchId)) {
                        if (subfiles.get(j).size() - 1 != subfileCt) {
                           results.errorf("%s", "Number of files does not match with Manifest.");
                           return results;
                        }
                        break;
                     }
                  }
                  subfileCt = 0;
               }
               strBranchId = mdLines[i].split("\\|")[1].trim();
               strLocalTx = mdLines[i].split("\\|")[2].trim();
               strUniqueTx = mdLines[i].split("\\|")[3].trim();
               strOp = mdLines[i].split("\\|")[4].trim();
               txMap.put(strBranchId, strLocalTx);
               txMap.put(strBranchId + "-" + strLocalTx, strUniqueTx + "-" + strOp);
               readKeyCt++;
               continue;
            }
            if (mdLines[i].contains("List of Directories")) {
               //verify subfiles ct from previous branch id
               if (subfileCt != 0) {
                  for (int j = 0; j < subfiles.size(); j++) {
                     if (subfiles.get(j).get(subfiles.get(j).size() - 1).equals(strBranchId)) {
                        if (subfiles.get(j).size() - 1 != subfileCt) {
                           results.errorf("%s", "Number of files does not match with Manifest.");
                           return results;
                        }
                        break;
                     }
                  }
               }
               break;
            }
            //read transaction detail
            if (readKeyCt > 2) {
               strBranchId = mdLines[i].split("\\|")[1].trim();
               strLocalTx = mdLines[i].split("\\|")[2].trim();
               strUniqueTx = mdLines[i].split("\\|")[3].trim();
               strOp = mdLines[i].split("\\|")[4].trim();
               txMap.put(strBranchId + "-" + strLocalTx, strUniqueTx + "-" + strOp);
               for (int j = 0; j < subfiles.size(); j++) {
                  if (subfiles.get(j).get(subfiles.get(j).size() - 1).equals(strBranchId)) {
                     if (!subfiles.get(j).get(subfileCt).equals(strLocalTx)) {
                        results.errorf("%s", "File name is mismatched or not in correct order.");
                     }
                     break;
                  }
               }
               subfileCt++;
               continue;
            }
         }
      } catch (Exception e) {
         results.errorf("%s", e.getMessage());
         return results;
      }

      if (readKeyCt < 3) {
         results.errorf("%s", "Manifest is missing data.");
         return results;
      }

      if (results.isFailed()) {
         return results;
         //End Manifest Validation
      }

      // Check to make sure there is a matching destination type init for this DB (or should that be by branch - needs investigation)
      // Check the PrevTX to make sure the DBs are in alignment using the manifest info
      // for each transaction, do the orcs/txs create command for the applicable transaction
      // update the tuple table (the TransferFile one) with the new tx but the same unique transaction id, e.g.:
      // tx.addTuple4(TransferFile, branch, newTxId, txId for processed JSON file, TransferOpType.ADD);
      // write an info for the xresult data to tell for all successful transactions
      // otherwise write errors for unsuccessful transfer transaction and purge all imported transactions.

      ArrayList<String> transIdList = new ArrayList<String>();
      //HashMap<BranchId, GammaId> gammaIdMap = new HashMap<BranchId, GammaId>();
      try {
         for (int i = 0; i < subfiles.size(); i++) {
            BranchId branchId = BranchId.valueOf(subfiles.get(i).get(subfiles.get(i).size() - 1));
            //TransactionBuilder txTupleBuilder =
            //   orcsApi.getTransactionFactory().createTransaction(branchId, "Upload transfer tuples");

            String strManBaseTxId = txMap.get(branchId.toString());

            List<TransactionId> txIds = new ArrayList<>();
            //tupleQuery.getTuple4E3E4FromE1E2(TupleType, branchId, e1, e2, consumer);
            tupleQuery.getTuple4E3E4FromE1E2(TransferFile, CoreBranches.COMMON, branchId, TransferOpType.PREV_TX,
               (E3, E4) -> {
                  txIds.add(E3);
               });

            if (txIds.isEmpty()) {
               //if there is no preTx add the new prevTx so that it can pass the validation
               //txIds.add(TransationId.valueOf(strManBaseTxId));
            } else if (!txIds.get(0).toString().equals(strManBaseTxId)) {
               results.errorf("%s", String.format("Previous Tx %s of %s does not match with current value in database.",
                  strManBaseTxId, branchId.toString()));
               break;
            }

            //List<GammaId> tuples = new ArrayList<>();
            //tupleQuery.getTuple4GammaFromE1E2(TransferFile, CoreBranches.COMMON, branchId, TransferOpType.PREV_TX, tuples::add);
            /*
             * tupleQuery.getTuple4GammaFromE1E2(TransferFile, branchId, branchId, TransferOpType.PREV_TX, tuples::add);
             * if(tuples.isEmpty() ) { results.errorf("%s", String.format("Can not get gamma Id from %s and Prev_TX.",
             * branchId.toString())); } else gammaIdMap.put(branchId, tuples.get(0));
             */
            //import transactions to branchId
            int j = 0;
            try {
               for (j = 0; j < subfiles.get(i).size() - 1; j++) {
                  File transFile = new File(String.format("%s%s%s%s%s.json", dirName, File.separator,
                     branchId.toString(), File.separator, subfiles.get(i).get(j)));
                  String transStr = Lib.fileToString(transFile);
                  TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi, resourceManager);
                  TransactionBuilder trans = txBdf.loadFromJson(transStr);

                  trans.createArtifact(CoreArtifactTypes.AcronymPlainText, "Test Artifact");

                  //trans.addkeyValueOps(id, name);
                  //make small change to get the transaction id
                  TransactionToken token = trans.commit();
                  transIdList.add(token.getIdString());
               }
            } catch (Exception e) {
               results.errorf("Failed at %s - %s.json: %s.", branchId.toString(), subfiles.get(i).get(j),
                  e.getMessage());
            }

            if (results.isFailed()) {
               break;
            }
         }
      } catch (Exception e) {
         results.errorf("%s", e.getMessage());
      }

      if (results.isFailed()) {
         try {
            StringBuilder transIds = new StringBuilder("");
            for (int i = 0; i < transIdList.size(); i++) {
               if (i % PURGE_TXS_LIMIT == 0 && transIds.length() > 0) {
                  orcsApi.getTransactionFactory().purgeTxs(transIds.toString());
                  results.log(String.format("Purged succesfully the transaction ids: %s.", transIds.toString()));
                  //transIds = new StringBuilder("");
                  transIds.setLength(0);
               }
               if (transIds.length() > 0) {
                  transIds.append(",").append(transIdList.get(i));
               } else {
                  transIds.append(transIdList.get(i));
               }
            }
            if (transIds.length() > 0) {
               orcsApi.getTransactionFactory().purgeTxs(transIds.toString());
               results.log(String.format("Purged succesfully the transaction ids: %s.", transIds.toString()));
            }

         } catch (Exception e) {
            results.errorf("%s", String.format("Roll back failed while purging transaction ids %s.", e.getMessage()));
         }

      } else {
         //update prevTX tuple

         StringBuilder transIds = new StringBuilder("");
         for (int i = 0; i < transIdList.size(); i++) {
            if (i % PURGE_TXS_LIMIT == 0 && transIds.length() != 0) {
               //orcsApi.getTransactionFactory().purgeTxs(transIds.toString()); //for test
               results.log(String.format("Imported succesfully the transaction ids: %s.", transIds.toString()));
               //transIds.setLength(0);
               transIds = new StringBuilder("");
            }
            if (transIds.length() == 0) {
               transIds.append(transIdList.get(i));
            } else {
               transIds.append(",").append(transIdList.get(i));
            }
         }
         if (transIds.length() != 0) {
            //orcsApi.getTransactionFactory().purgeTxs(transIds.toString()); //for test
            results.log(String.format("Imported succesfully the transaction ids: %s.", transIds.toString()));
         }
      }
      return results;
   }

   @Override
   public Response downloadTransferFile() {
      return downloadTransferFile("n/a");
   }

   @Override
   public Response downloadTransferFile(String currentFlag) {
      if (currentFlag.equals("true")) {
         //call generateTransferFile otherwise look for the latest existing files
      }
      //where to get the path???
      String downloadDataPath = orcsApi.getSystemProperties().getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);

      if (downloadDataPath == null) {
         downloadDataPath = System.getProperty("user.home");
      }

      File downloadTransDir = new File(downloadDataPath + File.separator + "fileTransfer");
      if (!downloadTransDir.isDirectory()) {
         return Response.noContent().build();
      }

      String filename = "";
      filename = OrcsRestUtil.getLatestFile(downloadTransDir.toString(), "OSEETransfer", "zip");

      File downloadZip = new File(String.format("%s%s%s", downloadTransDir.getPath(), File.separator, filename));

      if (downloadZip.exists()) {
         try {
            return Response.ok(Files.readAllBytes(downloadZip.toPath())).type("application/zip").header(
               "Content-Disposition", "attachment; filename=\"" + filename + "\"").build();
         } catch (IOException ex) {
            return Response.serverError().build();
         }
      }
      return Response.noContent().build();
   }

   @Override
   public Response uploadTransferFile(InputStream zip) {
      String serverDataPath = orcsApi.getSystemProperties().getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverDataPath == null) {
         serverDataPath = System.getProperty("user.home");
      }
      File serverApplicDir = new File(String.format("%s%sOSSEDataTransferUploads", serverDataPath, File.separator));
      if (!serverApplicDir.exists()) {
         serverApplicDir.mkdirs();
         try {
            FileWriter readme =
               new FileWriter(String.format("%s%s%s", serverApplicDir.getPath(), File.separator, "readme.txt"));
            readme.write(
               "This folder contains OSEE data transfer files which were uploaded via rest api and imported into database.");
            readme.close();
         } catch (IOException e) {
            return Response.serverError().build();
         }
      }

      Date date = Calendar.getInstance().getTime();
      DateFormat dateFormat = new SimpleDateFormat("yyyymmmdd_hhmmss");
      String timedId = String.format("%sOSEETransferFile-%s", File.separator, dateFormat.format(date));
      String timedIdDir = String.format("%s%s%s", serverApplicDir.getPath(), File.separator, timedId);
      String sourceNameDir = String.format("%s%ssource", timedIdDir, File.separator);
      OutputStream outStream = null;
      ZipInputStream zis = null;
      String transDir = null;
      try {
         new File(timedIdDir).mkdir();
         String fileZip = String.format("%s.zip", sourceNameDir);
         File uploadedZip = new File(fileZip);
         byte[] buffer = zip.readAllBytes();

         outStream = new FileOutputStream(uploadedZip);
         outStream.write(buffer);

         zis = new ZipInputStream(new FileInputStream(fileZip));
         ZipEntry zipEntry = zis.getNextEntry();
         File unzipLocation = new File(sourceNameDir);
         unzipLocation.mkdirs();
         while (zipEntry != null) {
            File uploadedDirectory = new File(unzipLocation, zipEntry.getName());
            if (transDir == null) {
               transDir = uploadedDirectory.getPath();
            }
            if (zipEntry.isDirectory()) {
               if (!uploadedDirectory.isDirectory() && !uploadedDirectory.mkdirs()) {
                  zis.close();
                  Lib.close(outStream);
                  throw new IOException("Failed to create directory " + uploadedDirectory);
               }
            } else {
               // for Windows-created archives
               File parent = uploadedDirectory.getParentFile();
               if (!parent.isDirectory() && !parent.mkdirs()) {
                  zis.close();
                  Lib.close(outStream);
                  throw new IOException("Failed to create directory " + parent);
               }
               // write file content
               try (FileOutputStream fos = new FileOutputStream(uploadedDirectory);) {
                  int len;
                  while ((len = zis.read(buffer)) > 0) {
                     fos.write(buffer, 0, len);
                  }
               }

            }
            zipEntry = zis.getNextEntry();
         }

         zip.close();
         outStream.close();
         zis.closeEntry();
         zis.close();
      } catch (Exception ex) {
         //throw new OseeCoreException(ex, "OSEE Upload Transfer file Failed");
         return Response.serverError().build();
      } finally {
         if (zis != null) {
            Lib.close(zis);
         }
         Lib.close(outStream);
      }

      XResultData results = applyTransferFile(transDir);
      results.log(String.format("The file is extracted to %s.", sourceNameDir));
      if (results.isOK()) {
         return Response.ok().entity(String.format("\nResult: %s", results.toString())).build();
      } else {
         return Response.serverError().entity(String.format("\nResult: %s", results.toString())).build();
      }
   }

   // external API to check for transfer file locks, internally, just use the util class TransferFileLockUtil
   @Override
   public XResultData lock(TransactionId exportId) {
      XResultData results = new XResultData();
      boolean transferLocked = TransferFileLockUtil.lock(orcsApi.getKeyValueOps(), exportId.getId());
      if (transferLocked) {
         results.success("Export ID %s locked", exportId.toString());
      } else {
         results.errorf("Export ID %s already locked", exportId.toString());
      }
      return results;
   }

   @Override
   public XResultData unlock(TransactionId exportId) {
      XResultData results = new XResultData();
      boolean transferUnLocked = TransferFileLockUtil.unLock(orcsApi.getKeyValueOps(), exportId.getId());
      if (transferUnLocked) {
         results.success("Export ID %s not locked", exportId.toString());
      } else {
         results.errorf("Export ID %s already not locked", exportId.toString());
      }
      return results;
   }

   @Override
   public XResultData isLocked(TransactionId exportId) {
      XResultData results = new XResultData();
      boolean transferIsLocked = TransferFileLockUtil.isLocked(orcsApi.getKeyValueOps(), exportId.getId());
      if (transferIsLocked) {
         results.success("Export ID %s locked", exportId.toString());
      } else {
         results.success("Export ID %s not locked", exportId.toString());
      }
      return results;
   }

}