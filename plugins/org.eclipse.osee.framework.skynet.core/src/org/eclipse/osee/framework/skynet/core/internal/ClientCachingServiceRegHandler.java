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

import java.util.Map;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientArtifactTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientAttributeTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientBranchAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientOseeEnumTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientRelationTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientTransactionAccessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class ClientCachingServiceRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] DEPENDENCIES = new Class<?>[] {IOseeModelFactoryService.class};

   private ServiceRegistration serviceRegistration;

   @Override
   public Class<?>[] getDependencies() {
      return DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IOseeModelFactoryService modelFactory = getService(IOseeModelFactoryService.class, services);
      IOseeCachingService cachingService = createService(modelFactory);

      serviceRegistration = context.registerService(IOseeCachingService.class.getName(), cachingService, null);
   }

   @Override
   public void onDeActivate() {
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }

   private IOseeCachingService createService(IOseeModelFactoryService factory) {
      TransactionCache transactionCache = new TransactionCache();
      ClientBranchAccessor clientBranchAccessor =
         new ClientBranchAccessor(factory.getBranchFactory(), transactionCache);
      BranchCache branchCache = new BranchCache(clientBranchAccessor);
      clientBranchAccessor.setBranchCache(branchCache);

      TransactionRecordFactory txFactory = factory.getTransactionFactory();

      transactionCache.setAccessor(new ClientTransactionAccessor(txFactory, branchCache));
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
