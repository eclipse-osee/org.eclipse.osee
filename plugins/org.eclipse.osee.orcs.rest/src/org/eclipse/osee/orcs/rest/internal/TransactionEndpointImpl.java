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
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.Transaction;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.rest.model.transaction.TransferInitData;
import org.eclipse.osee.orcs.rest.model.transaction.TransferOpType;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public class TransactionEndpointImpl implements TransactionEndpoint {

   private final OrcsApi orcsApi;

   @Context
   private UriInfo uriInfo;

   public TransactionEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
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

      TransactionBuilderDataFactory tbdf = new TransactionBuilderDataFactory(orcsApi);
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
         throw new OseeCoreException("initTransactionTransfer given null data");
      }
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Adding tuple for transfer init");
      List<BranchId> branches = data.getBranchIds();
      for (BranchId branch : branches) {
         // this sets up the branch as a exported branch with an export ID
         // currently
         tx.addTuple3(ExportedBranch, data.getExportId(), branch, data.getTransferDBType());

         // hopefully, both source and destination db will be set with the same init, setting the same prevTX (I think this is right)
         tx.addTuple4(TransferFile, branch, data.getBaseTxId(), TransactionId.valueOf(Lib.generateUuid()),
            TransferOpType.PREV_TX);
      }
      //TODO add checks to make sure the transfer data has all necessary values
      // create new XResultData to collect any errors or info about whether or not this succeeds not
      // check to see if the export id is already used, and return error in XResultData if it is
      return data;
   }

   @Override
   public XResultData generateTransferFile(TransactionId exportId) {
      XResultData results = new XResultData();
      // TODO
      // get the branch for the export ID from the Exported branch tuple table
      // get the tuple with the prevTX from the TransferFile tuple table
      // create a new directory using the Lib (OSEE file writing utility package) method for creating a new directory
      // find all of the transactions between the prev tx and the current last transaction of the branch
      // for each transaction, do the exportTxsDiff (method above), generate a json file, then write the json file into the directory
      // update the TransferFile tuple table with the transaction Id (e.g. matching transaction ID from source branch, a new unique tx as above like:
      // tx.addTuple4(TransferFile, branch, currentTxId, TransactionId.valueOf(Lib.generateUuid()), TransferOpType.ADD);
      // write the manifest.md file (I couln't decide if it was best to have the manifest be a md file, or if it would be easier to parse if the manifest were json
      // if json, instead write json for manifest file (e.g. this isn't written in stone, consider creatively using the XResultData to contain the contents of the manifest)
      // zip contents, then return xresult data with zipped file location
      return results;
   }

   @Override
   public XResultData applyTransferFile(String fileName) {
      XResultData results = new XResultData();
      // TODO
      // Extract file contents
      // Check to make sure there is a matching destination type init for this DB (or should that be by branch - needs investigation)
      // Check the PrevTX to make sure the DBs are in alignment using the manifest info
      // for each transaction, do the orcs/txs create command for the applicable transaction
      // update the tuple table (the TransferFile one) with the new tx but the same unique transaction id, e.g.:
      // tx.addTuple4(TransferFile, branch, newTxId, txId for processed JSON file, TransferOpType.ADD);
      // write an info for the xresult data to tell for each successful transaction
      // write errors for unsuccessfule transfer transactions
      return results;
   }

}