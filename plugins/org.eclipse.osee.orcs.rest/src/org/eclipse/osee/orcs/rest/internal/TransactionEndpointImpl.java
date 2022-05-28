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
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.Transaction;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
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
      return result;
   }

   @Override
   public List<ChangeItem> compareTxs(TransactionId txId1, TransactionId txId2) {
      return orcsApi.getTransactionFactory().compareTxs(txId1, txId2);
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
   public Response replaceWithBaselineTxVersion(UserId userId, BranchId branchId, TransactionId txId, ArtifactId artId, String comment) {
      return OrcsRestUtil.asResponse(
         orcsApi.getTransactionFactory().replaceWithBaselineTxVersion(userId, branchId, txId, artId, comment));

   }

   @Override
   public List<ChangeItem> getArtifactHistory(ArtifactId artifact, BranchId branch) {
      return orcsApi.getTransactionFactory().getArtifactHistory(artifact, branch);
   }
}