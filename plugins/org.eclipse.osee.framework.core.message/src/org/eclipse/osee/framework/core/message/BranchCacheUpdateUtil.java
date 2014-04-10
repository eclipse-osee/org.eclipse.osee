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
package org.eclipse.osee.framework.core.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Megumi Telles
 */
public final class BranchCacheUpdateUtil {

   private enum Fields {
      BRANCH_COUNT,
      BRANCH_ROW,
      CHILD_TO_PARENT,
      BRANCH_TO_BASE_TX,
      BRANCH_TO_SRC_TX,
      BRANCH_TO_ASSOC_ART,
      BRANCH_TO_ALIASES,
      SRC_DEST_MERGE;
   }

   private final BranchFactory factory;
   private final TransactionCache txCache;
   public static final Long DEFAULT_UUID = -1L;

   public BranchCacheUpdateUtil(BranchFactory factory, TransactionCache txCache) {
      this.factory = factory;
      this.txCache = txCache;
   }

   public Collection<Branch> updateCache(AbstractBranchCacheMessage cacheMessage, IOseeCache<Long, Branch> cache) throws OseeCoreException {
      List<Branch> updatedItems = new ArrayList<Branch>();

      Map<Long, Integer> branchToAssocArt = cacheMessage.getBranchToAssocArt();

      preLoadTransactions(cacheMessage);

      for (BranchRow srcItem : cacheMessage.getBranchRows()) {
         long branchId = srcItem.getBranchId();
         Branch updated =
            factory.createOrUpdate(cache, branchId, srcItem.getBranchName(), srcItem.getBranchType(),
               srcItem.getBranchState(), srcItem.getBranchArchived().isArchived(), srcItem.getStorageState());
         updatedItems.add(updated);

         Integer artifactId = branchToAssocArt.get(branchId);
         if (artifactId != null) {
            updated.setAssociatedArtifactId(artifactId);
         }

         updated.setBaseTransaction(getTx(cacheMessage.getBranchToBaseTx(), branchId));
         updated.setSourceTransaction(getTx(cacheMessage.getBranchToSourceTx(), branchId));
      }

      for (Entry<Long, Long> entry : cacheMessage.getChildToParent().entrySet()) {
         Branch parent = cache.getById(entry.getValue());
         if (parent != null) {
            Branch child = cache.getById(entry.getKey());
            if (child != null) {
               child.setParentBranch(parent);
            }
         }
      }
      for (Triplet<Long, Long, Long> entry : cacheMessage.getMergeBranches()) {
         IOseeBranch sourceBranch = entry.getFirst() > 0 ? cache.getByGuid(entry.getFirst()) : null;
         IOseeBranch destinationBranch = entry.getSecond() > 0 ? cache.getByGuid(entry.getSecond()) : null;

         Branch branch = cache.getByGuid(entry.getThird());
         MergeBranch mergeBranch = null;
         try {
            mergeBranch = (MergeBranch) branch;
            mergeBranch.setSourceBranch(sourceBranch);
            mergeBranch.setDestinationBranch(destinationBranch);
         } catch (ClassCastException ex) {
            throw new OseeCoreException(ex, "Problem casting branch [%s] to MergeBranch, source: [%s], dest: [%s]",
               branch, sourceBranch, destinationBranch);
         }
      }
      return updatedItems;
   }

   private void preLoadTransactions(AbstractBranchCacheMessage cacheMessage) throws OseeCoreException {
      Set<Integer> txIdsToLoad = new HashSet<Integer>();
      addValidTxIds(cacheMessage.getBranchToBaseTx().values(), txIdsToLoad);
      addValidTxIds(cacheMessage.getBranchToSourceTx().values(), txIdsToLoad);
      txCache.loadTransactions(txIdsToLoad);
   }

   private void addValidTxIds(Collection<Integer> source, Collection<Integer> destination) {
      for (Integer txId : source) {
         if (txId != null && txId > 0) {
            destination.add(txId);
         }
      }
   }

   private TransactionRecord getTx(Map<Long, Integer> branchToTx, Long branchId) throws OseeCoreException {
      TransactionRecord tx = null;
      Integer txId = branchToTx.get(branchId);
      if (txId != null && txId > 0) {
         tx = txCache.getOrLoad(txId);
      }
      return tx;
   }

   public static void loadFromCache(AbstractBranchCacheMessage message, Collection<Branch> types) throws OseeCoreException {
      for (Branch br : types) {
         Long branchId = br.getId();
         message.getBranchRows().add(
            new BranchRow(br.getId(), br.getName(), br.getBranchType(), br.getBranchState(), br.getArchiveState(),
               br.getStorageState()));
         if (br.hasParentBranch()) {
            message.getChildToParent().put(branchId, br.getParentBranch().getId());
         }
         addTxRecord(message.getBranchToBaseTx(), branchId, br.getBaseTransaction());
         addTxRecord(message.getBranchToSourceTx(), branchId, br.getSourceTransaction());
         addAssocArtifact(message.getBranchToAssocArt(), branchId, br.getAssociatedArtifactId());
         if (br.getBranchType().isMergeBranch()) {
            addMergeBranches(message.getMergeBranches(), (MergeBranch) br);
         }
      }
   }

   private static void addAssocArtifact(Map<Long, Integer> map, Long branchId, Integer artId) {
      if (artId != null) {
         map.put(branchId, artId);
      } else {
         map.put(branchId, -1);
      }
   }

   private static void addMergeBranches(List<Triplet<Long, Long, Long>> srcDestMerge, MergeBranch mergeBranch) throws OseeCoreException {
      Long src = mergeBranch.getSourceBranch() != null ? mergeBranch.getSourceBranch().getGuid() : DEFAULT_UUID;
      Long dest =
         mergeBranch.getDestinationBranch() != null ? mergeBranch.getDestinationBranch().getGuid() : DEFAULT_UUID;
      Long merge = mergeBranch.getGuid();
      srcDestMerge.add(new Triplet<Long, Long, Long>(src, dest, merge));
   }

   private static void addTxRecord(Map<Long, Integer> map, Long branchId, TransactionRecord toAdd) {
      if (toAdd != null) {
         map.put(branchId, toAdd.getId());
      } else {
         map.put(branchId, -1);
      }
   }

   public static void loadMessage(AbstractBranchCacheMessage message, PropertyStore store) {
      List<BranchRow> rows = message.getBranchRows();
      int rowCount = store.getInt(Fields.BRANCH_COUNT.name());
      for (int index = 0; index < rowCount; index++) {
         String[] rowData = store.getArray(TranslationUtil.createKey(Fields.BRANCH_ROW, index));
         rows.add(BranchRow.fromArray(rowData));
      }
      TranslationUtil.loadMapLong(message.getChildToParent(), store, Fields.CHILD_TO_PARENT);
      TranslationUtil.loadMap(message.getBranchToBaseTx(), store, Fields.BRANCH_TO_BASE_TX);
      TranslationUtil.loadMap(message.getBranchToSourceTx(), store, Fields.BRANCH_TO_SRC_TX);
      TranslationUtil.loadMap(message.getBranchToAssocArt(), store, Fields.BRANCH_TO_ASSOC_ART);
      TranslationUtil.loadArrayMap(message.getBranchAliases(), store, Fields.BRANCH_TO_ALIASES);
      TranslationUtil.loadTripletList(message.getMergeBranches(), store, Fields.SRC_DEST_MERGE);
   }

   public static void loadStore(PropertyStore store, AbstractBranchCacheMessage message) {
      List<BranchRow> rows = message.getBranchRows();
      for (int index = 0; index < rows.size(); index++) {
         BranchRow row = rows.get(index);
         store.put(TranslationUtil.createKey(Fields.BRANCH_ROW, index), row.toArray());
      }
      store.put(Fields.BRANCH_COUNT.name(), rows.size());

      TranslationUtil.putMapLong(store, Fields.CHILD_TO_PARENT, message.getChildToParent());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_BASE_TX, message.getBranchToBaseTx());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_SRC_TX, message.getBranchToSourceTx());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_ASSOC_ART, message.getBranchToAssocArt());
      TranslationUtil.putArrayMap(store, Fields.BRANCH_TO_ALIASES, message.getBranchAliases());
      TranslationUtil.putTripletList(store, Fields.SRC_DEST_MERGE, message.getMergeBranches());
   }
}
