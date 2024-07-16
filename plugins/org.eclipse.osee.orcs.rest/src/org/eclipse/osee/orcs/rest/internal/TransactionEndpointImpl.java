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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import org.eclipse.osee.orcs.rest.model.transaction.TransactionTransferManifest;
import org.eclipse.osee.orcs.rest.model.transaction.TransferDBType;
import org.eclipse.osee.orcs.rest.model.transaction.TransferFileLockUtil;
import org.eclipse.osee.orcs.rest.model.transaction.TransferInitData;
import org.eclipse.osee.orcs.rest.model.transaction.TransferOpType;
import org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public class TransactionEndpointImpl implements TransactionEndpoint {

   private final OrcsApi orcsApi;
   private final IResourceManager resourceManager;
   private final TupleQuery tupleQuery;

   @Context
   private UriInfo uriInfo;
   public TransactionEndpointImpl(OrcsApi orcsApi, IResourceManager resourceManager) {
      this.orcsApi = orcsApi;
      this.resourceManager = resourceManager;
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
      TransactionToken token = tx.commit(); //check relations array
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
      XResultData results = new XResultData();
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
         results.error(
            "null input given - fill out the returned json and use it to set properly init the transaction transfer");
         example.setResults(results);
         return example;
      }

      if (data.getTransferDBType().equals(TransferDBType.DESTINATION)) {
         //Verify export id. Destination db accepts only on export id
         List<TransactionId> exportIdList = new ArrayList<>();
         int intDbType = TransferDBType.DESTINATION.ordinal();
         tupleQuery.getTuple4E1FromTupleType(ExportedBranch, TransferTupleTypes.LongExportedDBType,
            Long.valueOf(intDbType), exportIdList::add);

         for (TransactionId id : exportIdList) {
            if (id.equals(data.getExportId())) {
               results.error(String.format("The export Id %s was already initialized.", data.getExportId().toString()));

            } else {
               results.error(String.format(
                  "Another export Id, %s, was initialized. This database does not accept this export id %s.",
                  id.toString(), data.getExportId().toString()));
            }
            data.setResults(results);
            return data;
         }
      } else if (data.getTransferDBType().equals(TransferDBType.SOURCE)) {
         //Verify export id. Destination db accepts only on export id
         List<TransactionId> exportIdList = new ArrayList<>();
         int intDbType = TransferDBType.SOURCE.ordinal();
         tupleQuery.getTuple4E1FromTupleType(ExportedBranch, TransferTupleTypes.LongExportedDBType,
            Long.valueOf(intDbType), exportIdList::add);

         for (TransactionId id : exportIdList) {
            if (id.equals(data.getExportId())) {
               results.error(String.format("The export Id %s was already initialized.", data.getExportId().toString()));
               data.setResults(results);
               return data;
            }
         }
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
   public XResultData generateTransferFile(TransactionId exportId, String mode) {
      XResultData results = new XResultData();

      //Verify export id
      List<TransactionId> exportIdList = new ArrayList<>();
      int intDbType = TransferDBType.SOURCE.ordinal();
      tupleQuery.getTuple4E1FromTupleType(ExportedBranch, TransferTupleTypes.LongExportedDBType,
         Long.valueOf(intDbType), exportIdList::add);

      boolean exportIdExist = false;
      for (TransactionId id : exportIdList) {
         if (id.equals(exportId)) {
            exportIdExist = true;
            break;
         }
      }
      if (!exportIdExist) {
         results.errorf("Export Id %s has not been initialized.", exportId.toString());
         return results;
      }

      boolean islocked = false;
      try {
         if (TransferFileLockUtil.isLocked(orcsApi.getKeyValueOps(), exportId.getId())) {
            results.errorf("%s", "Lock is on. Transfer in progress for exportID", exportId.getIdString());
            return results;
         }

         islocked = TransferFileLockUtil.lock(orcsApi.getKeyValueOps(), exportId.getId());

         TransferDataStoreImpl transfer = new TransferDataStoreImpl(this, orcsApi);
         // Return XResultData
         results = transfer.transferTransactions(exportId, results, mode);

      } catch (Exception ex) {
         results.errorf("%s", String.format("Error during generating transfer file: ", ex.getMessage()));
      } finally {
         if (islocked) {
            TransferFileLockUtil.unLock(orcsApi.getKeyValueOps(), exportId.getId());
         }
      }

      return results;
   }

   @Override
   public XResultData applyTransferFile(String location) {
      XResultData results = new XResultData();
      TransactionId exportId = TransactionId.SENTINEL;
      boolean islocked = false;

      try {
         TransactionTransferManifest manifest = new TransactionTransferManifest();
         results = manifest.parse(location);

         if (results.isSuccess()) {
            results = manifest.validate(tupleQuery);
         }

         if (results.isFailed()) {
            return results;
         }

         exportId = manifest.getExportID();
         if (TransferFileLockUtil.isLocked(orcsApi.getKeyValueOps(), exportId.getId())) {
            results.error("Lock is on; Another upload process is in progress. ");
         } else {
            islocked = TransferFileLockUtil.lock(orcsApi.getKeyValueOps(), exportId.getId());

            results = manifest.importAllTransactions(orcsApi, resourceManager);
            if (results.isFailed()) {
               manifest.purgeAllImportedTransaction(orcsApi);
            } else {
               results = manifest.addAllImportedTransToTupleTable(orcsApi);

               results.log(String.format("Imported succesfully the transaction ids: %s. ",
                  manifest.getAllImportedTransIds().toString()));
            }
         }
      } catch (Exception ex) {
         results.errorf("%s", String.format("Error during uploading transfer file: ", ex.getMessage()));
      } finally {
         if (islocked) {
            TransferFileLockUtil.unLock(orcsApi.getKeyValueOps(), exportId.getId());
         }
      }

      return results;
   }

   @Override
   public XResultData getTransferFileList(String strexportId) {
      XResultData results = new XResultData();
      try {
         String transferPath = orcsApi.getSystemProperties().getValue(
            OseeClient.OSEE_APPLICATION_SERVER_DATA) + File.separator + TransferDataStoreImpl.transferDir + File.separator;

         File directoryPath = new File(transferPath);
         FilenameFilter filefilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
               String lowercaseName = name.toLowerCase();
               if (!lowercaseName.endsWith(".zip")) {
                  return false;
               }

               if (strexportId == null) {
                  return true;
               }

               if (lowercaseName.contains(strexportId)) {
                  return true;
               }
               return false;
            }
         };

         List<String> filelist = Arrays.asList(directoryPath.list(filefilter));
         if (filelist.size() > 0) {
            results.setInfoCount(filelist.size());
            results.setIds(filelist);
         }

      } catch (Exception ex) {
         results.error(ex.getMessage());
      }
      return results;
   }

   @Override
   public XResultData getExportIdList() {
      XResultData results = new XResultData();
      try {
         List<TransactionId> exportIdList = new ArrayList<>();
         int intDbType = TransferDBType.SOURCE.ordinal();
         tupleQuery.getTuple4E1FromTupleType(ExportedBranch, TransferTupleTypes.LongExportedDBType,
            Long.valueOf(intDbType), exportIdList::add);

         List<String> idlist = new ArrayList<String>();
         for (TransactionId transId : exportIdList) {
            idlist.add(transId.getIdString());
         }

         if (exportIdList.size() > 0) {
            results.setInfoCount(exportIdList.size());
            results.setIds(idlist);
         }
      } catch (Exception ex) {
         results.error(ex.getMessage());
      }
      return results;
   }

   @Override
   public Response downloadTransferFile(String filename) {
      String transferPath = orcsApi.getSystemProperties().getValue(
         OseeClient.OSEE_APPLICATION_SERVER_DATA) + File.separator + TransferDataStoreImpl.transferDir;

      File downloadZip = new File(String.format("%s%s%s", transferPath, File.separator, filename));

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

   private String unzipTransferFile(InputStream zip) throws IOException {
      String serverDataPath = orcsApi.getSystemProperties().getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverDataPath == null) {
         serverDataPath = System.getProperty("user.home");
      }
      File serverApplicDir = new File(String.format("%s%sOSEEDataTransferUploads", serverDataPath, File.separator));
      if (!serverApplicDir.exists()) {
         serverApplicDir.mkdirs();
         try {
            try (FileWriter readme =
               new FileWriter(String.format("%s%s%s", serverApplicDir.getPath(), File.separator, "readme.txt"));) {
               readme.write("This folder contains OSEE data transfer files which were imported during debugging");
            }
         } catch (IOException e) {
            throw new OseeCoreException(e, "Failed to create directory. ");
         }
      }

      Date date = Calendar.getInstance().getTime();
      DateFormat dateFormat = new SimpleDateFormat("yyyymmmdd_hhmmss");
      String timedId = String.format("%sOSEETransferFile-%s", File.separator, dateFormat.format(date));
      String timedIdDir = String.format("%s%s%s", serverApplicDir.getPath(), File.separator, timedId);
      OutputStream outStream = null;
      ZipInputStream zis = null;
      String returnDir = timedIdDir;
      try {
         new File(timedIdDir).mkdir();
         String fileZip = String.format("%s.zip", timedIdDir);
         File uploadedZip = new File(fileZip);
         byte[] buffer = zip.readAllBytes();

         outStream = new FileOutputStream(uploadedZip);
         outStream.write(buffer);

         zis = new ZipInputStream(new FileInputStream(fileZip));
         ZipEntry zipEntry = zis.getNextEntry();
         File unzipLocation = new File(timedIdDir);
         unzipLocation.mkdirs();
         while (zipEntry != null) {
            File uploadedDirectory = new File(unzipLocation, zipEntry.getName());
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
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "OSEE Upload Transfer file Failed. ");
      } finally {
         zip.close();
         Lib.close(outStream);
         Lib.close(zis);
      }

      return returnDir;
   }

   @Override
   public XResultData uploadTransferFile(InputStream zip) {
      XResultData results = new XResultData();
      String transDir = "";
      try {
         transDir = unzipTransferFile(zip);
         zip.close();
      } catch (Exception e) {
         results.error("Failed to unzip the file.");
         return results;
      } finally {
         if (zip != null) {
            Lib.close(zip);
         }
      }

      results = applyTransferFile(transDir);
      String parentPath = Paths.get(transDir).getParent().toString();
      try {
         if (parentPath.endsWith("OSEEDataTransferUploads")) {
            File importTransferFolder = new File(parentPath);
            if (importTransferFolder.exists()) {
               deleteDir(importTransferFolder);
            }
         }
      } catch (Exception e) {
         results.error("Transfer folder for deletion not found. " + e.getMessage());
      }
      return results;
   }

   // external API to check for transfer file locks, internally, just use the util class TransferFileLockUtil
   @Override
   public XResultData lock(TransactionId exportId) {
      XResultData results = new XResultData();
      boolean transferLocked = TransferFileLockUtil.lock(orcsApi.getKeyValueOps(), exportId.getId());
      if (transferLocked) {
         results.logf("Export ID locked: %s", exportId.toString());
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
         results.logf("Export ID unlocked: %s", exportId.toString());
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
         results.logf("Export ID locked: %s", exportId.toString());
      } else {
         results.logf("Export ID %s not locked", exportId.toString());
      }
      return results;
   }

   private void deleteDir(File file) {
      File[] contents = file.listFiles();
      if (contents != null) {
         for (File f : contents) {
            if (!Files.isSymbolicLink(f.toPath())) {
               deleteDir(f);
            }
         }
      }
      file.delete();
   }

}