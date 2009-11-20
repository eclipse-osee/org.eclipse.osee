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
import org.eclipse.osee.framework.core.services.IOseeCachingService;

/**
 * @author Roberto E. Escobar
 */
public class MockOseeCachingService implements IOseeCachingService {

   private final BranchCache branchCache;
   private final TransactionCache transactionCache;

   private final ArtifactTypeCache artifactCache;
   private final AttributeTypeCache attributeCache;
   private final RelationTypeCache relationCache;
   private final OseeEnumTypeCache oseeEnumTypeCache;

   public MockOseeCachingService(BranchCache branchCache, TransactionCache transactionCache, ArtifactTypeCache artifactCache, AttributeTypeCache attributeCache, RelationTypeCache relationCache, OseeEnumTypeCache oseeEnumTypeCache) {
      this.branchCache = branchCache;
      this.transactionCache = transactionCache;

      this.artifactCache = artifactCache;
      this.attributeCache = attributeCache;
      this.relationCache = relationCache;
      this.oseeEnumTypeCache = oseeEnumTypeCache;

   }

   @Override
   public ArtifactTypeCache getArtifactTypeCache() {
      return artifactCache;
   }

   @Override
   public AttributeTypeCache getAttributeTypeCache() {
      return attributeCache;
   }

   @Override
   public BranchCache getBranchCache() {
      return branchCache;
   }

   @Override
   public OseeEnumTypeCache getEnumTypeCache() {
      return oseeEnumTypeCache;
   }

   @Override
   public RelationTypeCache getRelationTypeCache() {
      return relationCache;
   }

   @Override
   public TransactionCache getTransactionCache() {
      return transactionCache;
   }

}
