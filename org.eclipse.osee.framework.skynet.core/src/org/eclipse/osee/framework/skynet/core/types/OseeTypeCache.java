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

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeCache {

   private final ArtifactTypeCache artifactCache;
   private final AttributeTypeCache attributeCache;
   private final RelationTypeCache relationCache;
   private final OseeEnumTypeCache oseeEnumTypeCache;

   private boolean duringPopulate;

   public OseeTypeCache(IOseeTypeDataAccessor dataAccessor, IOseeTypeFactory factory) {
      this.duringPopulate = false;
      artifactCache = new ArtifactTypeCache(this, factory, dataAccessor);
      attributeCache = new AttributeTypeCache(this, factory, dataAccessor);
      relationCache = new RelationTypeCache(this, factory, dataAccessor);
      oseeEnumTypeCache = new OseeEnumTypeCache(this, factory, dataAccessor);
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

   public void cacheArtifactTypeInheritance(ArtifactType artifactType, Collection<ArtifactType> superType) throws OseeCoreException {
      getArtifactTypeCache().cacheArtifactTypeInheritance(artifactType, superType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, AttributeType attributeType, Branch branch) throws OseeCoreException {
      getArtifactTypeCache().cacheTypeValidity(artifactType, attributeType, branch);
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
