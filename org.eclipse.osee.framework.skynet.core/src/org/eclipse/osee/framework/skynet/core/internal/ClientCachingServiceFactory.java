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

import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientArtifactTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientAttributeTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientBranchAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientOseeEnumTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientRelationTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ClientTransactionAccessor;

/**
 * @author Roberto E. Escobar
 */
public class ClientCachingServiceFactory {

   public ClientCachingServiceFactory() {
   }

   public IOseeCachingService createService(IOseeModelFactoryServiceProvider factoryProvider) {
      TransactionCache transactionCache = new TransactionCache();
      ClientBranchAccessor clientBranchAccessor = new ClientBranchAccessor(factoryProvider, transactionCache);
      BranchCache branchCache = new BranchCache(clientBranchAccessor);
      clientBranchAccessor.setBranchCache(branchCache);
      transactionCache.setAccessor(new ClientTransactionAccessor(factoryProvider, branchCache));
      OseeEnumTypeCache oseeEnumTypeCache = new OseeEnumTypeCache(new ClientOseeEnumTypeAccessor(factoryProvider));

      AttributeTypeCache attributeTypeCache =
            new AttributeTypeCache(new ClientAttributeTypeAccessor(factoryProvider, oseeEnumTypeCache));

      ArtifactTypeCache artifactTypeCache =
            new ArtifactTypeCache(new ClientArtifactTypeAccessor(factoryProvider, attributeTypeCache, branchCache));

      RelationTypeCache relationTypeCache =
            new RelationTypeCache(new ClientRelationTypeAccessor(factoryProvider, artifactTypeCache));

      return new OseeCachingService(branchCache, transactionCache, artifactTypeCache, attributeTypeCache,
            relationTypeCache, oseeEnumTypeCache);
   }
}
