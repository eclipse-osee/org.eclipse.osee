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
package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.BranchCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.BranchCacheUpdateResponse.BranchRow;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.skynet.core.types.ShallowArtifact;

/**
 * @author Roberto E. Escobar
 */
public class ClientBranchAccessor extends AbstractClientDataAccessor<Branch> {

   private final TransactionCache transactionCache;

   public ClientBranchAccessor(IOseeModelFactoryServiceProvider factoryProvider, TransactionCache transactionCache) {
      super(factoryProvider);
      this.transactionCache = transactionCache;
   }

   protected BranchFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getBranchFactory();
   }

   @Override
   public void load(IOseeCache<Branch> cache) throws OseeCoreException {
      transactionCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected Collection<Branch> updateCache(IOseeCache<Branch> cache) throws OseeCoreException {
      List<Branch> updatedItems = new ArrayList<Branch>();

      BranchCacheUpdateResponse response = sendUpdateMessage(cache, CoreTranslatorId.BRANCH_CACHE_UPDATE_RESPONSE);
      Map<Integer, String[]> branchToAliases = response.getBranchAliases();

      Map<Integer, Integer> branchToBaseTx = response.getBranchToBaseTx();
      Map<Integer, Integer> branchToSourceTx = response.getBranchToSourceTx();
      Map<Integer, Integer> branchToAssocArt = response.getBranchToAssocArt();

      BranchFactory factory = getFactory();
      for (BranchRow srcItem : response.getBranchRows()) {
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
            TransactionRecord baseTx = transactionCache.getById(baseTxId);
            updated.setBaseTransaction(baseTx);
         }

         Integer srcTxId = branchToSourceTx.get(branchId);
         if (srcTxId != null) {
            TransactionRecord srcTx = transactionCache.getById(srcTxId);
            if (srcTx != null) {
               updated.setSourceTransaction(srcTx);
            }
         }
         Integer artifactId = branchToAssocArt.get(branchId);
         if (artifactId != null) {
            updated.setAssociatedArtifact(new ShallowArtifact((BranchCache) cache, artifactId));
         }
      }

      for (Entry<Integer, Integer> entry : response.getChildToParent().entrySet()) {
         Branch parent = cache.getById(entry.getValue());
         if (parent != null) {
            Branch child = cache.getById(entry.getKey());
            if (child != null) {
               child.setParentBranch(parent);
            }
         }
      }
      return updatedItems;
   }
}
