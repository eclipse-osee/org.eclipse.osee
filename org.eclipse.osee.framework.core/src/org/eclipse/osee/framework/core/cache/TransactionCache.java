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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IOseeStorableType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class TransactionCache {

   private final ITransactionDataAccessor accessor;

   private final Map<Integer, TransactionRecord> transactionIdCache = new HashMap<Integer, TransactionRecord>();
   private final boolean duringPopulate;

   public TransactionCache(ITransactionDataAccessor accessor) {
      this.accessor = accessor;
      this.duringPopulate = false;
   }

   protected ITransactionDataAccessor getDataAccessor() {
      return accessor;
   }

   public void cache(TransactionRecord... types) throws OseeCoreException {
      Conditions.checkNotNull(types, "types to cache");
      ensurePopulated();
      for (TransactionRecord type : types) {
         cache(type);
      }
   }

   public void cache(TransactionRecord type) throws OseeCoreException {
      Conditions.checkNotNull(type, "type to cache");
      ensurePopulated();
      transactionIdCache.put(type.getId(), type);
   }

   public void decache(TransactionRecord... types) throws OseeCoreException {
      Conditions.checkNotNull(types, "types to de-cache");
      for (TransactionRecord type : types) {
         decache(type);
      }
   }

   public void decache(TransactionRecord type) throws OseeCoreException {
      Conditions.checkNotNull(type, "type to de-cache");
      ensurePopulated();
      if (type.getId() != IOseeStorableType.UNPERSISTTED_VALUE) {
         transactionIdCache.remove(type.getId());
      }
   }

   public void ensurePopulated() throws OseeCoreException {
      //      if (transactionIdCache.isEmpty()) {
      //         if (!duringPopulate) {
      //            duringPopulate = true;
      //            reloadCache();
      //            duringPopulate = false;
      //         }
      //      }
   }

   public TransactionRecord getTransaction(Branch branch, TransactionVersion revision) throws OseeCoreException {
      TransactionRecord toReturn = null;
      if (TransactionVersion.BASE == revision) {
         toReturn = branch.getBaseTransaction();
      }
      if (toReturn == null) {
         getDataAccessor().loadTransactionRecord(this, branch, revision);

      }
      return toReturn;
   }

   public Collection<TransactionRecord> getTransactions(Branch branch) throws OseeCoreException {
      return Collections.emptyList();
   }

   public TransactionRecord getById(int txId) throws OseeCoreException {
      TransactionRecord transactionRecord = transactionIdCache.get(txId);
      if (transactionRecord == null) {
         loadTransactions(Collections.singletonList(txId));
         transactionRecord = transactionIdCache.get(txId);
         if (transactionRecord == null) {
            throw new OseeStateException(String.format("Transaction Record[%s] was not found", txId));
         }
      }
      return transactionRecord;
   }

   public void loadTransactions(Collection<Integer> transactionIds) throws OseeCoreException {
      getDataAccessor().loadTransactionRecord(this, transactionIds);
   }

   //
   //   public void reloadCache() throws OseeCoreException {
   //      //      getDataAccessor().load(this);
   //   }
}
