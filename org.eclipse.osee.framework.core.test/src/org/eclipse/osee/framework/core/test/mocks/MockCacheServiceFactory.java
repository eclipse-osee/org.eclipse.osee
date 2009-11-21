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
package org.eclipse.osee.framework.core.test.mocks;

import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public final class MockCacheServiceFactory {

   private MockCacheServiceFactory() {
   }

   public static IOseeCachingServiceProvider createProvider() {
      BranchCache brCache = new BranchCache(new MockOseeDataAccessor<Branch>());
      TransactionCache txCache = new TransactionCache(new MockOseeTransactionDataAccessor());
      ArtifactTypeCache artCache = new ArtifactTypeCache(new MockOseeDataAccessor<ArtifactType>());
      AttributeTypeCache attrCache = new AttributeTypeCache(new MockOseeDataAccessor<AttributeType>());
      RelationTypeCache relCache = new RelationTypeCache(new MockOseeDataAccessor<RelationType>());
      OseeEnumTypeCache enumCache = new OseeEnumTypeCache(new MockOseeDataAccessor<OseeEnumType>());

      IOseeCachingService service = new OseeCachingService(brCache, txCache, artCache, attrCache, relCache, enumCache);
      return new MockOseeCachingServiceProvider(service);
   }

}
