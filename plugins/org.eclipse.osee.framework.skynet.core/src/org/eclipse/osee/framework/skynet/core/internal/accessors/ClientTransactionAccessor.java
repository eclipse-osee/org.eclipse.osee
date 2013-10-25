/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.message.CacheUpdateRequest;
import org.eclipse.osee.framework.core.message.TransactionCacheUpdateResponse;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.ITransactionDataAccessor;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;

/**
 * @author Roberto E. Escobar
 */
public class ClientTransactionAccessor implements ITransactionDataAccessor {

   private static final String GET_PRIOR_TRANSACTION =
      "select transaction_id FROM osee_tx_details where branch_id = ? and transaction_id < ? order by transaction_id desc";
   private static final String TX_GET_MAX_AS_LARGEST_TX =
      "SELECT max(transaction_id) as largest_transaction_id FROM osee_tx_details WHERE branch_id = ?";
   private final TransactionRecordFactory txFactory;
   private final BranchCache branchCache;

   public ClientTransactionAccessor(TransactionRecordFactory txFactory, BranchCache branchCache) {
      super();
      this.txFactory = txFactory;
      this.branchCache = branchCache;
   }

   @Override
   public void loadTransactionRecord(TransactionCache cache, Collection<Integer> transactionIds) throws OseeCoreException {
      CacheUpdateRequest request = new CacheUpdateRequest(cache.getCacheId(), transactionIds);
      requestUpdateMessage(cache, request);

   }

   @Override
   public TransactionRecord loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) {
      return null;
   }

   @Override
   public void load(TransactionCache transactionCache) throws OseeCoreException {
      requestUpdateMessage(transactionCache, new CacheUpdateRequest(transactionCache.getCacheId()));
   }

   protected void requestUpdateMessage(TransactionCache cache, CacheUpdateRequest updateRequest) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", CacheOperation.UPDATE.name());

      TransactionCacheUpdateResponse response =
         HttpClientMessage.send(OseeServerContext.CACHE_CONTEXT, parameters,
            CoreTranslatorId.OSEE_CACHE_UPDATE_REQUEST, updateRequest, CoreTranslatorId.TX_CACHE_UPDATE_RESPONSE);
      for (TransactionRecord row : response.getTxRows()) {
         TransactionRecord record =
            txFactory.createOrUpdate(cache, row.getId(), row.getBranchId(), row.getComment(), row.getTimeStamp(),
               row.getAuthor(), row.getCommit(), row.getTxType(), branchCache);
         record.clearDirty();
      }
   }

   @Override
   public TransactionRecord getOrLoadPriorTransaction(TransactionCache cache, int transactionNumber, long branchId) throws OseeCoreException {
      int priorTransactionId =
         ConnectionHandler.runPreparedQueryFetchInt(transactionNumber, GET_PRIOR_TRANSACTION, branchId,
            transactionNumber);
      return cache.getOrLoad(priorTransactionId);
   }

   @Override
   public TransactionRecord getHeadTransaction(TransactionCache cache, Branch branch) throws OseeCoreException {
      return cache.getOrLoad(ConnectionHandler.runPreparedQueryFetchInt(-1, TX_GET_MAX_AS_LARGEST_TX, branch.getId()));
   }
}
