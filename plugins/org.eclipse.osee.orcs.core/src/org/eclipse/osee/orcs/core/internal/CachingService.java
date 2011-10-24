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
package org.eclipse.osee.orcs.core.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class CachingService implements IOseeCachingService {

   private IOseeCachingServiceFactory factory;
   private Log logger;
   private IOseeCachingService proxied;

   public void setFactoryService(IOseeCachingServiceFactory factory) {
      this.factory = factory;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   private Log getLogger() {
      return logger;
   }

   public void start() {
      proxied = factory.createCachingService(true);
      getLogger().info("ORCS Cache - Created");
   }

   public void stop() {
      proxied = null;
      getLogger().info("ORCS Cache - Destroyed");
   }

   @Override
   public BranchCache getBranchCache() {
      return proxied.getBranchCache();
   }

   @Override
   public TransactionCache getTransactionCache() {
      return proxied.getTransactionCache();
   }

   @Override
   public ArtifactTypeCache getArtifactTypeCache() {
      return proxied.getArtifactTypeCache();
   }

   @Override
   public AttributeTypeCache getAttributeTypeCache() {
      return proxied.getAttributeTypeCache();
   }

   @Override
   public RelationTypeCache getRelationTypeCache() {
      return proxied.getRelationTypeCache();
   }

   @Override
   public OseeEnumTypeCache getEnumTypeCache() {
      return proxied.getEnumTypeCache();
   }

   @Override
   public IdentityService getIdentityService() {
      return proxied.getIdentityService();
   }

   @Override
   public Collection<?> getCaches() {
      return proxied.getCaches();
   }

   @Override
   public IOseeCache<?, ?> getCache(OseeCacheEnum cacheId) throws OseeCoreException {
      return proxied.getCache(cacheId);
   }

   @Override
   public void reloadAll() throws OseeCoreException {
      proxied.reloadAll();
   }

   @Override
   public void clearAll() {
      proxied.clearAll();
   }

}
