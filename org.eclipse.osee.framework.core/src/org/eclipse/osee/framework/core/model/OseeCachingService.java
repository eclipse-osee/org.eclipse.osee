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
package org.eclipse.osee.framework.core.model;

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
public class OseeCachingService implements IOseeCachingService {

   private final BranchCache branchCache;
   private final TransactionCache transactionCache;

   private final ArtifactTypeCache artifactTypeCache;
   private final AttributeTypeCache attributeTypeCache;
   private final RelationTypeCache relationTypeCache;
   private final OseeEnumTypeCache oseeEnumTypeCache;

   public OseeCachingService(BranchCache branchCache, TransactionCache transactionCache, ArtifactTypeCache artifactTypeCache, AttributeTypeCache attributeTypeCache, RelationTypeCache relationTypeCache, OseeEnumTypeCache oseeEnumTypeCache) {
      this.branchCache = branchCache;
      this.transactionCache = transactionCache;
      this.artifactTypeCache = artifactTypeCache;
      this.attributeTypeCache = attributeTypeCache;
      this.relationTypeCache = relationTypeCache;
      this.oseeEnumTypeCache = oseeEnumTypeCache;
   }

   @Override
   public BranchCache getBranchCache() {
      return branchCache;
   }

   @Override
   public TransactionCache getTransactionCache() {
      return transactionCache;
   }

   @Override
   public ArtifactTypeCache getArtifactTypeCache() {
      return artifactTypeCache;
   }

   @Override
   public AttributeTypeCache getAttributeTypeCache() {
      return attributeTypeCache;
   }

   @Override
   public OseeEnumTypeCache getEnumTypeCache() {
      return oseeEnumTypeCache;
   }

   @Override
   public RelationTypeCache getRelationTypeCache() {
      return relationTypeCache;
   }

}
