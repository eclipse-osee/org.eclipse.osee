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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.CacheUpdateRequest;
import org.eclipse.osee.framework.core.message.TransactionCacheUpdateResponse;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.ITransactionDataAccessor;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;

/**
 * @author Roberto E. Escobar
 */
public class ClientTransactionAccessor implements ITransactionDataAccessor {

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
   public void loadTransactionRecord(TransactionCache cache, Branch branch) {
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
               row.getAuthor(), row.getCommit(), row.getTxType());
         record.setBranchCache(branchCache);
         record.clearDirty();
      }
   }

}
