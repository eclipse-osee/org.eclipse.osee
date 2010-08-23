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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.message.BranchCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.BranchCacheUpdateUtil;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ClientBranchAccessor extends AbstractClientDataAccessor<Branch> {

   private final TransactionCache transactionCache;
   private BranchCache branchCache;
   private final BranchFactory branchFactory;

   public ClientBranchAccessor(BranchFactory branchFactory, TransactionCache transactionCache) {
      super();
      this.branchFactory = branchFactory;
      this.transactionCache = transactionCache;
   }

   public void setBranchCache(BranchCache branchCache) {
      this.branchCache = branchCache;
   }

   private BranchFactory getFactory() {
      return branchFactory;
   }

   @Override
   public void load(IOseeCache<Branch> cache) throws OseeCoreException {
      transactionCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected Collection<Branch> updateCache(IOseeCache<Branch> cache) throws OseeCoreException {
      BranchCacheUpdateResponse response = requestUpdateMessage(cache, CoreTranslatorId.BRANCH_CACHE_UPDATE_RESPONSE);
      return new BranchCacheUpdateUtil(getFactory(), transactionCache).updateCache(response, cache);
   }

   @Override
   public void store(Collection<Branch> types) throws OseeCoreException {
      store(branchCache, types);
   }

   public void store(IOseeCache<Branch> cache, Collection<Branch> types) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", CacheOperation.STORE.name());

      BranchCacheStoreRequest request = BranchCacheStoreRequest.fromCache(types);
      AcquireResult updateResponse =
         HttpClientMessage.send(OseeServerContext.CACHE_CONTEXT, parameters,
            CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST, request, null);

      if (updateResponse.wasSuccessful()) {
         sendChangeEvents(types);
         for (Branch type : types) {
            type.clearDirty();
         }
      }
   }

   private void sendChangeEvents(Collection<Branch> branches) {
      for (Branch branch : branches) {
         if (branch.getBranchState().isDeleted()) {
            try {
               OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Deleted, branch.getGuid()),
                  branch.getId());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

         try {
            if (branch.isFieldDirty(AbstractOseeType.NAME_FIELD_KEY)) {
               OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Renamed, branch.getGuid()),
                  branch.getId());
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

}
