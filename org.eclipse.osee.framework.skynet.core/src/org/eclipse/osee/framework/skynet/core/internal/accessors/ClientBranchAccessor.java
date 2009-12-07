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
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.data.BranchCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.IArtifactFactory;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.util.BranchCacheUpdateUtil;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.HttpMessage;
import org.eclipse.osee.framework.skynet.core.types.ShallowArtifact;

/**
 * @author Roberto E. Escobar
 */
public class ClientBranchAccessor extends AbstractClientDataAccessor<Branch> {

   private final TransactionCache transactionCache;
   private final IOseeModelFactoryServiceProvider factoryProvider;
   private BranchCache branchCache;

   public ClientBranchAccessor(IOseeModelFactoryServiceProvider factoryProvider, TransactionCache transactionCache) {
      super(factoryProvider);
      this.factoryProvider = factoryProvider;
      this.transactionCache = transactionCache;
   }

   public void setBranchCache(BranchCache branchCache) {
      this.branchCache = branchCache;
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
      BranchCacheUpdateResponse response = requestUpdateMessage(cache, CoreTranslatorId.BRANCH_CACHE_UPDATE_RESPONSE);
      ShallowArtifactFactory artFactory = new ShallowArtifactFactory((BranchCache) cache);

      return (new BranchCacheUpdateUtil(getFactory(), transactionCache, artFactory)).updateCache(response, cache);
   }

   public void store(Collection<Branch> types) throws OseeCoreException {
      store(branchCache, types);
   }

   public void store(IOseeCache<Branch> cache, Collection<Branch> types) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("request", "storage");
      parameters.put("function", CacheOperation.STORE.name());

      BranchCacheStoreRequest request = BranchCacheStoreRequest.fromCache((BranchCache) cache, types);
      AcquireResult updateResponse =
            HttpMessage.send(OseeServerContext.CACHE_CONTEXT, parameters, CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST,
                  request, null);

      if (updateResponse.wasSuccessful()) {
         for (Branch type : types) {
            type.clearDirty();
         }
      }

   }

   private final static class ShallowArtifactFactory implements IArtifactFactory<Artifact> {

      private final BranchCache cache;

      public ShallowArtifactFactory(BranchCache cache) {
         super();
         this.cache = cache;
      }

      @Override
      public IBasicArtifact<Artifact> createArtifact(int artId) {
         return new ShallowArtifact(cache, artId);
      }

   }
}
