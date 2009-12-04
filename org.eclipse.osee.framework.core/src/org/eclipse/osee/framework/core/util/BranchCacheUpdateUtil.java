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
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.AbstractBranchCacheMessage;
import org.eclipse.osee.framework.core.data.BranchRow;
import org.eclipse.osee.framework.core.data.IArtifactFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Megumi Telles
 */
public class BranchCacheUpdateUtil {

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

      Map<Integer, String[]> branchToAliases = cacheMessage.getBranchAliases();

      Map<Integer, Integer> branchToBaseTx = cacheMessage.getBranchToBaseTx();
      Map<Integer, Integer> branchToSourceTx = cacheMessage.getBranchToSourceTx();
      Map<Integer, Integer> branchToAssocArt = cacheMessage.getBranchToAssocArt();

      for (BranchRow srcItem : cacheMessage.getBranchRows()) {
         int branchId = srcItem.getBranchId();
         Branch updated =
               factory.createOrUpdate(cache, srcItem.getBranchId(), srcItem.getModType(), srcItem.getBranchGuid(),
                     srcItem.getBranchName(), srcItem.getBranchType(), srcItem.getBranchState(),
                     srcItem.getBranchArchived().isArchived());
         updatedItems.add(updated);
         String[] aliases = branchToAliases.get(branchId);
         if (aliases != null && aliases.length > 0) {
            updated.setAliases(aliases);
         }

         Integer baseTxId = branchToBaseTx.get(branchId);
         if (baseTxId != null) {
            TransactionRecord baseTx = txCache.getOrLoad(baseTxId);
            updated.setBaseTransaction(baseTx);
         }

         Integer srcTxId = branchToSourceTx.get(branchId);
         if (srcTxId != null) {
            TransactionRecord srcTx = txCache.getOrLoad(srcTxId); //transactionCache.getById(srcTxId);
            if (srcTx != null) {
               updated.setSourceTransaction(srcTx);
            }
         }
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
      BranchCache brCache = (BranchCache) cache;
      for (Triplet<Integer, Integer, Integer> entry : cacheMessage.getMergeBranches()) {
         Branch sourceBranch = cache.getById(entry.getFirst());
         Branch destinationBranch = cache.getById(entry.getSecond());
         Branch mergeBranch = cache.getById(entry.getThird());
         brCache.cacheMergeBranch(mergeBranch, sourceBranch, destinationBranch);
      }
      return updatedItems;
   }

   //   public static AbstractBranchCacheMessage fromCache(BranchCache cache, Collection<Branch> types) throws OseeCoreException {
   //      List<BranchRow> rowData = new ArrayList<BranchRow>();
   //      Map<Integer, Integer> childToParent = new HashMap<Integer, Integer>();
   //      Map<Integer, Integer> branchToBaseTx = new HashMap<Integer, Integer>();
   //      Map<Integer, Integer> branchToSourceTx = new HashMap<Integer, Integer>();
   //      Map<Integer, Integer> branchToAssocArt = new HashMap<Integer, Integer>();
   //      Map<Integer, String[]> branchToAliases = new HashMap<Integer, String[]>();
   //
   //      for (Branch br : types) {
   //         Integer branchId = br.getId();
   //         rowData.add(new BranchRow(br.getId(), br.getGuid(), br.getName(), br.getBranchType(), br.getBranchState(),
   //               br.getArchiveState(), br.getModificationType()));
   //         Collection<String> aliases = br.getAliases();
   //         if (!aliases.isEmpty()) {
   //            branchToAliases.put(branchId, aliases.toArray(new String[aliases.size()]));
   //         }
   //
   //         if (br.hasParentBranch()) {
   //            childToParent.put(branchId, br.getParentBranch().getId());
   //         }
   //
   //         TransactionRecord txBase = br.getBaseTransaction();
   //         if (txBase != null) {
   //            branchToBaseTx.put(branchId, txBase.getId());
   //         }
   //         TransactionRecord srcBase = br.getSourceTransaction();
   //         if (srcBase != null) {
   //            branchToSourceTx.put(branchId, srcBase.getId());
   //         }
   //
   //         IBasicArtifact<?> art = br.getAssociatedArtifact();
   //         if (art != null) {
   //            branchToAssocArt.put(branchId, art.getArtId());
   //         }
   //      }
   //
   //      List<Triplet<Integer, Integer, Integer>> srcDestMerge = new ArrayList<Triplet<Integer, Integer, Integer>>();
   //      for (Entry<Pair<Branch, Branch>, Branch> entry : cache.getMergeBranches().entrySet()) {
   //         Integer src = entry.getKey().getFirst().getId();
   //         Integer dest = entry.getKey().getSecond().getId();
   //         Integer merge = entry.getValue().getId();
   //         srcDestMerge.add(new Triplet<Integer, Integer, Integer>(src, dest, merge));
   //      }
   //      return new AbstractBranchCacheMessage(rowData, childToParent, branchToBaseTx, branchToSourceTx, branchToAssocArt,
   //            branchToAliases, srcDestMerge);
   //   }
}
