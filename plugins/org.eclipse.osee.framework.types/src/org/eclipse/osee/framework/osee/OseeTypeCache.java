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
package org.eclipse.osee.framework.osee;

import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeCache {

   private final ArtifactTypeCache artifactCache;
   private final AttributeTypeCache attributeCache;
   private final RelationTypeCache relationCache;
   private final OseeEnumTypeCache oseeEnumTypeCache;

   private boolean duringPopulate;

   public OseeTypeCache(ArtifactTypeCache artifactCache, AttributeTypeCache attributeCache, RelationTypeCache relationCache, OseeEnumTypeCache oseeEnumTypeCache) {
      this.duringPopulate = false;
      this.artifactCache = artifactCache;
      this.attributeCache = attributeCache;
      this.relationCache = relationCache;
      this.oseeEnumTypeCache = oseeEnumTypeCache;
   }

   public void storeAllModified() throws OseeCoreException {
      getEnumTypeCache().storeAllModified();
      getAttributeTypeCache().storeAllModified();
      getArtifactTypeCache().storeAllModified();
      getRelationTypeCache().storeAllModified();
   }

   public synchronized void ensurePopulated() throws OseeCoreException {
      if (!duringPopulate) {
         duringPopulate = true;
         getEnumTypeCache().ensurePopulated();
         getAttributeTypeCache().ensurePopulated();
         getArtifactTypeCache().ensurePopulated();
         getRelationTypeCache().ensurePopulated();
         duringPopulate = false;
      }
   }

   public synchronized void clearAll() throws OseeCoreException {
      if (!duringPopulate) {
         duringPopulate = true;
         getEnumTypeCache().decacheAll();
         getAttributeTypeCache().decacheAll();
         getArtifactTypeCache().decacheAll();
         getRelationTypeCache().decacheAll();
         duringPopulate = false;
      }
   }

   public ArtifactTypeCache getArtifactTypeCache() {
      return artifactCache;
   }

   public AttributeTypeCache getAttributeTypeCache() {
      return attributeCache;
   }

   public OseeEnumTypeCache getEnumTypeCache() {
      return oseeEnumTypeCache;
   }

   public RelationTypeCache getRelationTypeCache() {
      return relationCache;
   }
}
