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

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.Conditions;

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
   private final Collection<IOseeCache<?>> caches;

   public OseeCachingService(BranchCache branchCache, TransactionCache transactionCache, ArtifactTypeCache artifactTypeCache, AttributeTypeCache attributeTypeCache, RelationTypeCache relationTypeCache, OseeEnumTypeCache oseeEnumTypeCache) {
      this.branchCache = branchCache;
      this.transactionCache = transactionCache;
      this.artifactTypeCache = artifactTypeCache;
      this.attributeTypeCache = attributeTypeCache;
      this.relationTypeCache = relationTypeCache;
      this.oseeEnumTypeCache = oseeEnumTypeCache;
      caches = new HashSet<IOseeCache<?>>();
      caches.add(branchCache);
      caches.add(transactionCache);
      caches.add(artifactTypeCache);
      caches.add(attributeTypeCache);
      caches.add(relationTypeCache);
      caches.add(oseeEnumTypeCache);
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

   @Override
   public Collection<IOseeCache<?>> getCaches() {
      return caches;
   }

   @Override
   public IOseeCache<?> getCache(OseeCacheEnum cacheId) throws OseeCoreException {
      Conditions.checkNotNull(cacheId, "cache id to find");
      for (IOseeCache<?> cache : getCaches()) {
         if (cache.getCacheId().equals(cacheId)) {
            return cache;
         }
      }
      throw new OseeArgumentException(String.format("Unable to find cache for id [%s]", cacheId));
   }

   @Override
   public synchronized void reloadAll() throws OseeCoreException {
      getBranchCache().reloadCache();
      getTransactionCache().reloadCache();
      getEnumTypeCache().reloadCache();
      getAttributeTypeCache().reloadCache();
      getArtifactTypeCache().reloadCache();
      getRelationTypeCache().reloadCache();
   }

   @Override
   public synchronized void clearAll() {
      getBranchCache().decacheAll();
      getTransactionCache().decacheAll();
      getEnumTypeCache().decacheAll();
      getAttributeTypeCache().decacheAll();
      getArtifactTypeCache().decacheAll();
      getRelationTypeCache().decacheAll();
   }
}
