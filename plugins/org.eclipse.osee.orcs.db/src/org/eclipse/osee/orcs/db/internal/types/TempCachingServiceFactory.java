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
package org.eclipse.osee.orcs.db.internal.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.accessor.DatabaseBranchAccessor;
import org.eclipse.osee.orcs.db.internal.accessor.DatabaseTransactionRecordAccessor;

/**
 * @author Roberto E. Escobar
 */
public class TempCachingServiceFactory implements CacheServiceFactory {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final ExecutorAdmin executorAdmin;
   private final IOseeModelFactoryService modelFactoryService;
   private final EventService eventService;

   public TempCachingServiceFactory(Log logger, IOseeDatabaseService dbService, ExecutorAdmin executorAdmin, IOseeModelFactoryService modelFactoryService, EventService eventService) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.executorAdmin = executorAdmin;
      this.modelFactoryService = modelFactoryService;
      this.eventService = eventService;
   }

   @Override
   public TempCachingService createCachingService(OrcsSession session, boolean needsPriming) {
      final TransactionCache txCache = new TransactionCache();
      final BranchCache branchCache =
         new BranchCache(new DatabaseBranchAccessor(logger, session, executorAdmin, eventService, dbService, txCache,
            modelFactoryService.getBranchFactory()), txCache);
      txCache.setAccessor(new DatabaseTransactionRecordAccessor(dbService, branchCache,
         modelFactoryService.getTransactionFactory()));

      final List<IOseeCache<?, ?>> caches = Arrays.<IOseeCache<?, ?>> asList(branchCache);

      return new TempCachingService() {

         @Override
         public BranchCache getBranchCache() {
            return branchCache;
         }

         @Override
         public Collection<?> getCaches() {
            return caches;
         }

         @Override
         public IOseeCache<?, ?> getCache(OseeCacheEnum cacheId) throws OseeCoreException {
            Conditions.checkNotNull(cacheId, "cache id to find");
            IOseeCache<?, ?> toReturn = null;
            if (OseeCacheEnum.TRANSACTION_CACHE == cacheId) {
               toReturn = txCache;
            } else if (OseeCacheEnum.BRANCH_CACHE == cacheId) {
               toReturn = branchCache;
            } else {
               throw new OseeArgumentException("Unable to find cache for id [%s]", cacheId);
            }
            return toReturn;
         }

         @Override
         public synchronized void reloadAll() throws OseeCoreException {
            getBranchCache().reloadCache();
         }

         @Override
         public synchronized void clearAll() {
            getBranchCache().decacheAll();
         }

         @Override
         public TransactionCache getTransactionCache() {
            throw new UnsupportedOperationException("Server should not call this");
         }

      };
   }
}
