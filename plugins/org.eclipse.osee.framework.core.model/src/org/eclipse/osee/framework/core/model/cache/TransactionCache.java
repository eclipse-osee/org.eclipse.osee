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
package org.eclipse.osee.framework.core.model.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class TransactionCache implements IOseeCache<String, TransactionRecord> {
   private ITransactionDataAccessor accessor;

   private final Map<Integer, TransactionRecord> transactionIdCache =
      new ConcurrentHashMap<Integer, TransactionRecord>();

   private final OseeCacheEnum cacheId;
   private boolean ensurePopulatedRanOnce;
   private long lastLoaded;

   public TransactionCache() {
      this.lastLoaded = 0;
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
      if (type.isIdValid()) {
         transactionIdCache.remove(type.getId());
      }
   }

   @Override
   public Collection<TransactionRecord> getAll() throws OseeCoreException {
      ensurePopulated();
      return new ArrayList<TransactionRecord>(transactionIdCache.values());
   }

   public TransactionRecord getOrLoad(int txId) throws OseeCoreException {
      TransactionRecord transactionRecord = getById(txId);
      if (transactionRecord == null) {
         loadTransactions(Collections.singletonList(txId));
         transactionRecord = getById(txId);
         if (transactionRecord == null) {
            throw new OseeStateException("Transaction Record[%s] was not found", txId);
         }
      }
      return transactionRecord;
   }

   @Override
   public TransactionRecord getById(Number txId) throws OseeCoreException {
      ensurePopulated();
      return transactionIdCache.get(txId.intValue());
   }

   @Override
   public TransactionRecord getByGuid(String guid) throws OseeCoreException {
      throw new OseeStateException("TransactionCache.getByGuid() is not implemented...");
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
   public Collection<TransactionRecord> getAllDirty() {
      Set<TransactionRecord> dirtys = new HashSet<TransactionRecord>();
      for (TransactionRecord record : transactionIdCache.values()) {
         if (record.isDirty()) {
            dirtys.add(record);
         }
      }
      return dirtys;
   }

   @Override
   public void storeAllModified() {
      // do nothing
   }

   @Override
   public void storeItems(TransactionRecord... items) {
      // do nothing
   }

   @Override
   public void storeItems(Collection<TransactionRecord> toStore) {
      // do nothing
   }

   public TransactionRecord getPriorTransaction(TransactionRecord transactionId) throws OseeCoreException {
      return getDataAccessor().getOrLoadPriorTransaction(this, transactionId.getId(), transactionId.getBranchId());
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

   public TransactionRecord getHeadTransaction(Branch branch) throws OseeCoreException {
      return accessor.getHeadTransaction(this, branch);
   }

   public Collection<TransactionRecord> getTransactions(Branch branch) {
      return Collections.emptyList();
   }

   public void loadTransactions(Collection<Integer> transactionIds) throws OseeCoreException {
      ensurePopulated();
      getDataAccessor().loadTransactionRecord(this, transactionIds);
   }

   @Override
   public synchronized void ensurePopulated() throws OseeCoreException {
      if (!ensurePopulatedRanOnce) {
         ensurePopulatedRanOnce = true;
         reloadCache();
      }
   }

   @Override
   public long getLastLoaded() {
      return lastLoaded;
   }

   private synchronized void setLastLoaded(long lastLoaded) {
      this.lastLoaded = lastLoaded;
   }

   @Override
   public synchronized boolean reloadCache() throws OseeCoreException {
      ITransactionDataAccessor dataAccessor = getDataAccessor();
      if (dataAccessor != null) {
         dataAccessor.load(this);
      } else {
         OseeLog.log(this.getClass(), Level.WARNING, "Transaction Data Accessor was null");
      }
      OseeLog.log(this.getClass(), Level.INFO, "Loaded " + getCacheId().toString().toLowerCase());
      setLastLoaded(System.currentTimeMillis());
      return true;
   }

   @Override
   public void decacheAll() {
      transactionIdCache.clear();
      this.ensurePopulatedRanOnce = false;
   }
}