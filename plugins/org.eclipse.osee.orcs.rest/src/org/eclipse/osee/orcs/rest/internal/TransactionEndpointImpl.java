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

import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asIntegerList;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asResponse;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asTransaction;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asTransactions;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.rest.model.CompareResults;
import org.eclipse.osee.orcs.rest.model.Transaction;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

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
   public Response purgeTxs(String txIds) {
      boolean modified = false;
      List<Integer> txsToDelete = asIntegerList(txIds);
      if (!txsToDelete.isEmpty()) {
         ResultSet<TransactionReadable> results = newTxQuery().andTxIds(txsToDelete).getResults();
         if (!results.isEmpty()) {
            checkAllTxsFound("Purge Transaction", txsToDelete, results);
            List<TransactionReadable> list = Lists.newArrayList(results);
            Callable<?> op = newTxFactory().purgeTransaction(list);
            executeCallable(op);
            modified = true;
         }
      }
      return asResponse(modified);
   }

   private void checkAllTxsFound(String opName, List<Integer> txIds, ResultSet<TransactionReadable> result) {
      if (txIds.size() != result.size()) {
         Set<Integer> found = new HashSet<Integer>();
         for (TransactionReadable tx : result) {
            found.add(tx.getGuid());
         }
         SetView<Integer> difference = Sets.difference(Sets.newHashSet(txIds), found);
         if (!difference.isEmpty()) {
            throw new OseeWebApplicationException(
               Status.BAD_REQUEST,
               "%s Error - The following transactions from %s were not found - txs %s - Please remove them from the request and try again.",
               opName, txIds, difference);
         }
      }
   }

}
