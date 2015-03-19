/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asResponse;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asTransaction;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asTransactions;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.rest.model.CompareResults;
import org.eclipse.osee.orcs.rest.model.DeleteTransaction;
import org.eclipse.osee.orcs.rest.model.Transaction;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import com.google.common.collect.Lists;

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

   private ApplicationContext newContext() {
      return new ApplicationContext() {

         @Override
         public String getSessionId() {
            return null;
         }
      };
   }

   private TransactionQuery newTxQuery() {
      return orcsApi.getQueryFactory(newContext()).transactionQuery();
   }

   private OrcsBranch getBranchOps() {
      return orcsApi.getBranchOps(newContext());
   }

   private TransactionFactory newTxFactory() {
      return orcsApi.getTransactionFactory(newContext());
   }

   private TransactionReadable getTxById(int txId) {
      ResultSet<TransactionReadable> results = newTxQuery().andTxId(txId).getResults();
      return results.getExactlyOne();
   }

   @Override
   public List<Transaction> getAllTxs() {
      ResultSet<TransactionReadable> results = newTxQuery().getResults();
      return asTransactions(results);
   }

   @Override
   public Transaction getTx(int txId) {
      TransactionReadable tx = getTxById(txId);
      return asTransaction(tx);
   }

   @Override
   public CompareResults compareTxs(int txId1, int txId2) {
      TransactionReadable sourceTx = getTxById(txId1);
      TransactionReadable destinationTx = getTxById(txId2);
      Callable<List<ChangeItem>> callable = getBranchOps().compareBranch(sourceTx, destinationTx);
      List<ChangeItem> changes = executeCallable(callable);

      CompareResults data = new CompareResults();
      data.setChanges(changes);
      return data;
   }

   @Override
   public Response setTxComment(int txId, String comment) {
      TransactionReadable tx = getTxById(txId);
      boolean modified = false;
      if (Compare.isDifferent(tx.getComment(), comment)) {
         TransactionFactory txFactory = newTxFactory();
         txFactory.setTransactionComment(tx, comment);
         modified = true;
      }
      return asResponse(modified);
   }

   @Override
   public Response deleteTxs(DeleteTransaction deleteTxs) {
      boolean modified = false;
      if (!deleteTxs.isEmpty()) {
         ResultSet<TransactionReadable> results = newTxQuery().andTxIds(deleteTxs.getTransactions()).getResults();
         if (!results.isEmpty()) {
            Callable<?> op = newTxFactory().purgeTransaction(Lists.newLinkedList(results));
            executeCallable(op);
            modified = true;
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response deleteTxs(int txId) {
      TransactionReadable tx = getTxById(txId);
      boolean modified = false;
      if (tx != null) {
         Callable<?> op = newTxFactory().purgeTransaction(Collections.singleton(tx));
         executeCallable(op);
         modified = true;
      }
      return asResponse(modified);
   }
}
