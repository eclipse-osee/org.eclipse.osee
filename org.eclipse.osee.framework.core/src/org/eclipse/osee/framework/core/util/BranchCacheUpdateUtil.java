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
package org.eclipse.osee.framework.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.AbstractBranchCacheMessage;
import org.eclipse.osee.framework.core.data.BranchRow;
import org.eclipse.osee.framework.core.data.IArtifactFactory;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.translation.TranslationUtil;
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
   private final IArtifactFactory<?> artFactory;

   public BranchCacheUpdateUtil(BranchFactory factory, TransactionCache txCache, IArtifactFactory<?> artFactory) {
      super();
      this.factory = factory;
      this.txCache = txCache;
      this.artFactory = artFactory;
   }

   public Collection<Branch> updateCache(AbstractBranchCacheMessage cacheMessage, IOseeCache<Branch> cache) throws OseeCoreException {
      List<Branch> updatedItems = new ArrayList<Branch>();

      Map<Integer, Integer> branchToAssocArt = cacheMessage.getBranchToAssocArt();

      for (BranchRow srcItem : cacheMessage.getBranchRows()) {
         int branchId = srcItem.getBranchId();
         Branch updated =
               factory.createOrUpdate(cache, srcItem.getBranchId(), srcItem.getModType(), srcItem.getBranchGuid(),
                     srcItem.getBranchName(), srcItem.getBranchType(), srcItem.getBranchState(),
                     srcItem.getBranchArchived().isArchived());
         updatedItems.add(updated);

         updated.setBaseTransaction(getTx(cacheMessage.getBranchToBaseTx(), branchId));
         updated.setSourceTransaction(getTx(cacheMessage.getBranchToSourceTx(), branchId));

         Integer artifactId = branchToAssocArt.get(branchId);
         if (artifactId != null) {
            updated.setAssociatedArtifact(artFactory.createArtifact(artifactId));
         }
      }

      for (Entry<Integer, Integer> entry : cacheMessage.getChildToParent().entrySet()) {
         Branch parent = cache.getById(entry.getValue());
         if (parent != null) {
            Branch child = cache.getById(entry.getKey());
            if (child != null) {
               child.setParentBranch(parent);
            }
         }
      }
      for (Triplet<Integer, Integer, Integer> entry : cacheMessage.getMergeBranches()) {
         Branch sourceBranch = cache.getById(entry.getFirst());
         Branch destinationBranch = cache.getById(entry.getSecond());
         MergeBranch mergeBranch = (MergeBranch) cache.getById(entry.getThird());
         mergeBranch.setSourceBranch(sourceBranch);
         mergeBranch.setDestinationBranch(destinationBranch);
      }
      return updatedItems;
   }

   private TransactionRecord getTx(Map<Integer, Integer> branchToTx, Integer branchId) throws OseeCoreException {
      TransactionRecord tx = null;
      Integer txId = branchToTx.get(branchId);
      if (txId != null && txId > 0) {
         tx = txCache.getOrLoad(txId);
      }
      return tx;
   }

   public static void loadFromCache(AbstractBranchCacheMessage message, Collection<Branch> types) throws OseeCoreException {
      for (Branch br : types) {
         Integer branchId = br.getId();
         message.getBranchRows().add(
               new BranchRow(br.getId(), br.getGuid(), br.getName(), br.getBranchType(), br.getBranchState(),
                     br.getArchiveState(), br.getModificationType()));
         if (br.hasParentBranch()) {
            message.getChildToParent().put(branchId, br.getParentBranch().getId());
         }
         addTxRecord(message.getBranchToBaseTx(), branchId, br.getBaseTransaction());
         addTxRecord(message.getBranchToSourceTx(), branchId, br.getSourceTransaction());
         addAssocArtifact(message.getBranchToAssocArt(), branchId, br.getAssociatedArtifact());
         if (br.getBranchType().isMergeBranch()) {
            addMergeBranches(message.getMergeBranches(), (MergeBranch) br);
         }
      }
   }

   private static void addAssocArtifact(Map<Integer, Integer> map, Integer branchId, IBasicArtifact<?> art) {
      if (art != null) {
         map.put(branchId, art.getArtId());
      } else {
         map.put(branchId, -1);
      }
   }

   private static void addMergeBranches(List<Triplet<Integer, Integer, Integer>> srcDestMerge, MergeBranch mergeBranch) throws OseeCoreException {
      Integer src = mergeBranch.getSourceBranch() != null ? mergeBranch.getSourceBranch().getId() : -1;
      Integer dest = mergeBranch.getDestinationBranch() != null ? mergeBranch.getDestinationBranch().getId() : -1;
      Integer merge = mergeBranch.getId();
      srcDestMerge.add(new Triplet<Integer, Integer, Integer>(src, dest, merge));
   }

   private static void addTxRecord(Map<Integer, Integer> map, Integer branchId, TransactionRecord toAdd) {
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
      TranslationUtil.loadMap(message.getChildToParent(), store, Fields.CHILD_TO_PARENT);
      TranslationUtil.loadMap(message.getBranchToBaseTx(), store, Fields.BRANCH_TO_BASE_TX);
      TranslationUtil.loadMap(message.getBranchToSourceTx(), store, Fields.BRANCH_TO_SRC_TX);
      TranslationUtil.loadMap(message.getBranchToAssocArt(), store, Fields.BRANCH_TO_ASSOC_ART);
      TranslationUtil.loadArrayMap(message.getBranchAliases(), store, Fields.BRANCH_TO_ALIASES);
      TranslationUtil.loadTripletList(message.getMergeBranches(), store, Fields.SRC_DEST_MERGE);
   }

   public static void loadStore(PropertyStore store, AbstractBranchCacheMessage message) throws OseeCoreException {
      List<BranchRow> rows = message.getBranchRows();
      for (int index = 0; index < rows.size(); index++) {
         BranchRow row = rows.get(index);
         store.put(TranslationUtil.createKey(Fields.BRANCH_ROW, index), row.toArray());
      }
      store.put(Fields.BRANCH_COUNT.name(), rows.size());

      TranslationUtil.putMap(store, Fields.CHILD_TO_PARENT, message.getChildToParent());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_BASE_TX, message.getBranchToBaseTx());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_SRC_TX, message.getBranchToSourceTx());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_ASSOC_ART, message.getBranchToAssocArt());
      TranslationUtil.putArrayMap(store, Fields.BRANCH_TO_ALIASES, message.getBranchAliases());
      TranslationUtil.putTripletList(store, Fields.SRC_DEST_MERGE, message.getMergeBranches());
   }
}
