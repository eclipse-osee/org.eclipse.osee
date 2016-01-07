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
package org.eclipse.osee.framework.core.model.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchCache extends AbstractOseeLoadingCache<Long, Branch> {

   private final TransactionCache txCache;

   public BranchCache(IOseeDataAccessor<Long, Branch> dataAccessor, TransactionCache txCache) {
      super(OseeCacheEnum.BRANCH_CACHE, dataAccessor, false);
      this.txCache = txCache;
   }

   public MergeBranch findMergeBranch(IOseeBranch sourceBranch, IOseeBranch destinationBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      Conditions.checkNotNull(destinationBranch, "destination branch");
      MergeBranch toReturn = null;
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch()) && destinationBranch.equals(
               mergeBranch.getDestinationBranch())) {
               toReturn = mergeBranch;
               break;
            }
         }
      }
      return toReturn;
   }

   public MergeBranch findFirstMergeBranch(Branch sourceBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      MergeBranch toReturn = null;
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch())) {
               toReturn = mergeBranch;
               break;
            }
         }
      }
      return toReturn;
   }

   public List<MergeBranch> findAllMergeBranches(Branch sourceBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      List<MergeBranch> toReturn = new ArrayList<>();
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch())) {
               toReturn.add(mergeBranch);
            }
         }
      }
      return toReturn;
   }

   public synchronized <T extends IOseeBranch> List<T> getBranches(BranchFilter branchFilter) {
      Collection<Branch> allBranches = getRawValues();
      List<T> branches = new LinkedList<>();
      for (Branch branch : allBranches) {
         if (branchFilter.matches(branch)) {
            branches.add((T) branch);
         }
      }
      return branches;
   }

   public Branch getByUuid(long uuid) throws OseeCoreException {
      return super.getById(uuid);
   }

   @Override
   public synchronized boolean reloadCache() throws OseeCoreException {
      super.reloadCache();
      txCache.reloadCache();
      return true;
   }

   @Override
   public synchronized void decacheAll() {
      super.decacheAll();
      txCache.decacheAll();
   }

   public synchronized TransactionRecord getOrLoad(Integer txId) {
      return txCache.getOrLoad(txId);
   }

   public synchronized TransactionRecord getPriorTransaction(TransactionRecord tx) {
      return txCache.getPriorTransaction(tx);
   }

   public synchronized void cache(TransactionRecord record) {
      txCache.cache(record);
   }

   public synchronized TransactionRecord getHeadTransaction(Branch fullBranch) {
      return txCache.getHeadTransaction(fullBranch);
   }

   public synchronized Collection<TransactionRecord> getAllTx() {
      return txCache.getAll();
   }

   public synchronized TransactionRecord getByTxId(Integer item) {
      return txCache.getById(item);
   }

   public synchronized void loadTransactions(Collection<Integer> itemsIds) {
      txCache.loadTransactions(itemsIds);
   }

   public synchronized TransactionRecord getTransaction(Branch branch, TransactionVersion revision) {
      return txCache.getTransaction(branch, revision);
   }

}
