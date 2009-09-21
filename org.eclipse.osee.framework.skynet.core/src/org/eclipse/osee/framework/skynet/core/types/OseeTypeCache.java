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
package org.eclipse.osee.framework.skynet.core.types;

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

   public OseeTypeCache(IOseeTypeFactory factory, ArtifactTypeCache artifactCache, AttributeTypeCache attributeCache, RelationTypeCache relationCache, OseeEnumTypeCache oseeEnumTypeCache) {
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

   public ArtifactTypeCache getArtifactTypeCache() {
      return artifactCache;
   }

   public AttributeTypeCache getAttributeTypeCache() {
      return attributeCache;
   }

   public RelationTypeCache getRelationTypeCache() {
      return relationCache;
   }

   public OseeEnumTypeCache getEnumTypeCache() {
      return oseeEnumTypeCache;
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
}
