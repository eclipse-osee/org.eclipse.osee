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
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientArtifactTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientAttributeTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientOseeEnumTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientRelationTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.DatabaseBranchAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.DatabaseTransactionRecordAccessor;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Roberto E. Escobar
 */
public class ClientCachingServiceProxy implements IOseeCachingService {

   private IOseeModelFactoryService modelFactory;
   private JdbcService jdbcService;

   private IOseeCachingService proxiedService;

   public void setModelFactory(IOseeModelFactoryService modelFactory) {
      this.modelFactory = modelFactory;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void start() {
      proxiedService = createService(modelFactory);
   }

   public void stop() {
      if (proxiedService != null) {
         proxiedService = null;
      }
   }

   private IOseeCachingService getProxiedService() {
      return proxiedService;
   }

   @Override
   public BranchCache getBranchCache() {
      return getProxiedService().getBranchCache();
   }

   @Override
   public TransactionCache getTransactionCache() {
      return getProxiedService().getTransactionCache();
   }

   @Override
   public ArtifactTypeCache getArtifactTypeCache() {
      return getProxiedService().getArtifactTypeCache();
   }

   @Override
   public AttributeTypeCache getAttributeTypeCache() {
      return getProxiedService().getAttributeTypeCache();
   }

   @Override
   public RelationTypeCache getRelationTypeCache() {
      return getProxiedService().getRelationTypeCache();
   }

   @Override
   public OseeEnumTypeCache getEnumTypeCache() {
      return getProxiedService().getEnumTypeCache();
   }

   @Override
   public Collection<?> getCaches() {
      return getProxiedService().getCaches();
   }

   @Override
   public IOseeCache<?, ?> getCache(OseeCacheEnum cacheId) throws OseeCoreException {
      return getProxiedService().getCache(cacheId);
   }

   @Override
   public void reloadAll() throws OseeCoreException {
      getProxiedService().reloadAll();
   }

   @Override
   public void clearAll() {
      getProxiedService().clearAll();
   }

   private IOseeCachingService createService(IOseeModelFactoryService factory) {
      JdbcClient jdbcClient = jdbcService.getClient();
      TransactionCache transactionCache = new TransactionCache();
      DatabaseBranchAccessor clientBranchAccessor =
         new DatabaseBranchAccessor(jdbcClient, transactionCache, factory.getBranchFactory());
      BranchCache branchCache = new BranchCache(clientBranchAccessor, transactionCache);

      TransactionRecordFactory txFactory = factory.getTransactionFactory();

      transactionCache.setAccessor(new DatabaseTransactionRecordAccessor(jdbcClient, branchCache, txFactory));
      OseeEnumTypeCache oseeEnumTypeCache =
         new OseeEnumTypeCache(new ClientOseeEnumTypeAccessor(factory.getOseeEnumTypeFactory()));

      AttributeTypeCache attributeTypeCache =
         new AttributeTypeCache(new ClientAttributeTypeAccessor(factory.getAttributeTypeFactory(), oseeEnumTypeCache));

      ArtifactTypeCache artifactTypeCache =
         new ArtifactTypeCache(new ClientArtifactTypeAccessor(factory.getArtifactTypeFactory(), attributeTypeCache,
            branchCache));

      RelationTypeCache relationTypeCache =
         new RelationTypeCache(new ClientRelationTypeAccessor(factory.getRelationTypeFactory(), artifactTypeCache));

      return new OseeCachingService(branchCache, transactionCache, artifactTypeCache, attributeTypeCache,
         relationTypeCache, oseeEnumTypeCache);
   }
}
