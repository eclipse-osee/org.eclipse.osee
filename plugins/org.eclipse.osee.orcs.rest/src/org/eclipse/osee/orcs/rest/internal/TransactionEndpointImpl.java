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
import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.TransferLocked;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
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
import org.eclipse.osee.orcs.rest.model.transaction.TransferDBType;
import org.eclipse.osee.orcs.rest.model.transaction.TransferInitData;
import org.eclipse.osee.orcs.rest.model.transaction.TransferOpType;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public class TransactionEndpointImpl implements TransactionEndpoint {

   private final OrcsApi orcsApi;
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
         results.logf("%s",
            String.format("Checking if transfer currently in progress for exportID:: %s", exportId.getIdString()));
         int warning = results.getWarningCount();
         int error = results.getErrorCount();
         boolean transferActive = tupleQuery.doesTuple2Exist(TransferLocked, CoreBranches.COMMON, exportId);
         // Check if Transfer has been "locked" and is in the TupleTable2 via method
         if (transferActive && (error != 0 && warning != 0)) {
            results.errorf("%s", String.format("Transfer is active for the current export ID: ", exportId));
            results.setErrorCount(1);
         } else {
            // Transfer not in progress, begin lock
            results.setInfoCount(1);
            results.addRaw("Transfer in progress");
            // Add to tuple2table and commit the transaction
            TransactionBuilder txTupleTransferInProgress =
               orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Adding exportId to Tuple2Table");
            txTupleTransferInProgress.addTuple2(TransferLocked, CoreBranches.COMMON, exportId);
            txTupleTransferInProgress.commit();
         }
      } catch (Exception ex) {
         results.errorf("%s", String.format(
            "Error in checking if a transfer is active Current Export ID: " + exportId + " ", ex.getMessage()));
      }
      return results;
   }

   @Override
   public XResultData applyTransferFile(String fileName) {
      XResultData results = new XResultData();
      return results;
   }

}