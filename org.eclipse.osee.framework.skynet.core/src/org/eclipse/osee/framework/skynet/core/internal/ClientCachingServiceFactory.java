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
import org.eclipse.osee.framework.skynet.core.internal.accessors.ServerArtifactTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ServerAttributeTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ServerBranchAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ServerOseeEnumTypeAccessor;
import org.eclipse.osee.framework.skynet.core.internal.accessors.ServerRelationTypeAccessor;

/**
 * @author Roberto E. Escobar
 */
public class ClientCachingServiceFactory {

   public ClientCachingServiceFactory() {
   }

   public IOseeCachingService createService(IOseeModelFactoryServiceProvider factoryProvider) {
      TransactionCache transactionCache = new TransactionCache(null);

      BranchCache branchCache = new BranchCache(new ServerBranchAccessor(factoryProvider, transactionCache));
      OseeEnumTypeCache oseeEnumTypeCache = new OseeEnumTypeCache(new ServerOseeEnumTypeAccessor(factoryProvider));

      AttributeTypeCache attributeTypeCache =
            new AttributeTypeCache(new ServerAttributeTypeAccessor(factoryProvider, oseeEnumTypeCache));

      ArtifactTypeCache artifactTypeCache =
            new ArtifactTypeCache(new ServerArtifactTypeAccessor(factoryProvider, attributeTypeCache, branchCache));

      RelationTypeCache relationTypeCache =
            new RelationTypeCache(new ServerRelationTypeAccessor(factoryProvider, artifactTypeCache));

      return new OseeCachingService(branchCache, transactionCache, artifactTypeCache, attributeTypeCache,
            relationTypeCache, oseeEnumTypeCache);
   }
}
