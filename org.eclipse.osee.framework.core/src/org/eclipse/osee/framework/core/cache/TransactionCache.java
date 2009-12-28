/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class TransactionCache implements IOseeCache<TransactionRecord> {

   private ITransactionDataAccessor accessor;

   private final Map<Integer, TransactionRecord> transactionIdCache = new HashMap<Integer, TransactionRecord>();

   private final OseeCacheEnum cacheId;
   private boolean ensurePopulatedRanOnce;

   public TransactionCache() {
      this.cacheId = OseeCacheEnum.TRANSACTION_CACHE;
      this.ensurePopulatedRanOnce = false;
   }

   public void setAccessor(ITransactionDataAccessor accessor) {
      this.accessor = accessor;
   }

   protected ITransactionDataAccessor getDataAccessor() {
      return accessor;
   }

   @Override
   public void cache(TransactionRecord... types) throws OseeCoreException {
      Conditions.checkNotNull(types, "types to cache");
      ensurePopulated();
      for (TransactionRecord type : types) {
         cache(type);
      }
   }

   @Override
   public void cache(TransactionRecord type) throws OseeCoreException {
      Conditions.checkNotNull(type, "type to cache");
      ensurePopulated();
      transactionIdCache.put(type.getId(), type);
   }

   @Override
   public void decache(TransactionRecord... types) throws OseeCoreException {
      Conditions.checkNotNull(types, "types to de-cache");
      for (TransactionRecord type : types) {
         decache(type);
      }
   }

   @Override
   public void decache(TransactionRecord type) throws OseeCoreException {
      Conditions.checkNotNull(type, "type to de-cache");
      ensurePopulated();
      if (type.getId() != IOseeStorable.UNPERSISTTED_VALUE) {
         transactionIdCache.remove(type.getId());
      }
   }

   @Override
   public Collection<TransactionRecord> getAll() throws OseeCoreException {
      ensurePopulated();
      return new ArrayList<TransactionRecord>(transactionIdCache.values());
   }

   public TransactionRecord getOrLoad(int txId) throws OseeCoreException {
      ensurePopulated();
      TransactionRecord transactionRecord = transactionIdCache.get(txId);
      if (transactionRecord == null) {
         loadTransactions(Collections.singletonList(txId));
         transactionRecord = transactionIdCache.get(txId);
         if (transactionRecord == null) {
            if (txId == 1) { // handle bootstrap case for system root branch creation
               return transactionRecord;
            }
            throw new OseeStateException(String.format("Transaction Record[%s] was not found", txId));
         }
      }
      return transactionRecord;
   }

   @Override
   public TransactionRecord getById(int txId) throws OseeCoreException {
      ensurePopulated();
      return transactionIdCache.get(txId);
   }

   @Override
   public OseeCacheEnum getCacheId() {
      return cacheId;
   }

   @Override
   public int size() {
      return transactionIdCache.size();
   }

   @Override
   public Collection<TransactionRecord> getAllDirty() throws OseeCoreException {
      Set<TransactionRecord> dirtys = new HashSet<TransactionRecord>();
      for (TransactionRecord record : transactionIdCache.values()) {
         if (record.isDirty()) {
            dirtys.add(record);
         }
      }
      return dirtys;
   }

   @Override
   public void storeAllModified() throws OseeCoreException {
   }

   @Override
   public void storeItems(TransactionRecord... items) throws OseeCoreException {
   }

   @Override
   public void storeItems(Collection<TransactionRecord> toStore) throws OseeCoreException {
   }

   public TransactionRecord getTransaction(Branch branch, TransactionVersion revision) throws OseeCoreException {
      TransactionRecord toReturn = null;
      if (TransactionVersion.BASE == revision) {
         toReturn = branch.getBaseTransaction();
      }
      if (toReturn == null) {
         toReturn = getDataAccessor().loadTransactionRecord(this, branch, revision);

      }
      return toReturn;
   }

   public Collection<TransactionRecord> getTransactions(Branch branch) throws OseeCoreException {
      return Collections.emptyList();
   }

   public void loadTransactions(Collection<Integer> transactionIds) throws OseeCoreException {
      getDataAccessor().loadTransactionRecord(this, transactionIds);
   }

   @Override
   public synchronized void ensurePopulated() throws OseeCoreException {
      if (!ensurePopulatedRanOnce) {
         ensurePopulatedRanOnce = true;
         reloadCache();
      }
   }

   public synchronized void reloadCache() throws OseeCoreException {
      getDataAccessor().load(this);
   }

   public void decacheAll() {
   }
}