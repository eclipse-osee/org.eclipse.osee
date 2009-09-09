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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeCache {

   private final HashCollection<ArtifactType, ArtifactType> artifactTypeToSuperTypeMap =
         new HashCollection<ArtifactType, ArtifactType>();

   private final CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>> artifactToAttributeMap =
         new CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>>();

   private final ArtifactTypeCache artifactCache;
   private final AttributeTypeCache attributeCache;
   private final RelationTypeCache relationCache;
   private final OseeEnumTypeCache oseeEnumTypeCache;

   private final IOseeTypeDataAccessor dataAccessor;
   private final IOseeTypeFactory factory;
   private boolean duringPopulate;

   public OseeTypeCache(IOseeTypeDataAccessor dataAccessor, IOseeTypeFactory factory) {
      this.dataAccessor = dataAccessor;
      this.factory = factory;
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

   private IOseeTypeDataAccessor getDataAccessor() {
      return dataAccessor;
   }

   private IOseeTypeFactory getDataFactory() {
      return factory;
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
      artifactTypeToSuperTypeMap.put(artifactType, superType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, AttributeType attributeType, Branch branch) throws OseeCoreException {
      Collection<AttributeType> attributeTypes = artifactToAttributeMap.get(artifactType, branch);
      if (attributeTypes == null) {
         attributeTypes = new HashSet<AttributeType>();
         artifactToAttributeMap.put(artifactType, branch, attributeTypes);
      }
      attributeTypes.add(attributeType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException {
      Collection<AttributeType> cachedItems = artifactToAttributeMap.get(artifactType, branch);
      if (cachedItems == null) {
         cachedItems = new HashSet<AttributeType>(attributeTypes);
         artifactToAttributeMap.put(artifactType, branch, cachedItems);
      } else {
         cachedItems.clear();
         cachedItems.addAll(attributeTypes);
      }
   }

   public Collection<ArtifactType> getArtifactSuperType(ArtifactType artifactType) {
      Collection<ArtifactType> types = new HashSet<ArtifactType>();
      Collection<ArtifactType> stored = artifactTypeToSuperTypeMap.getValues(artifactType);
      if (stored != null) {
         types.addAll(stored);
      }
      return types;
   }

   public void addArtifactSuperType(ArtifactType artifactType, Collection<ArtifactType> superTypes) throws OseeCoreException {
      ensurePopulated();
      if (artifactType == null) {
         throw new OseeArgumentException("artifactType cannot be null");
      }
      if (superTypes.isEmpty() || superTypes == null) {
         if (!artifactType.getName().equals("Artifact")) {
            throw new OseeInvalidInheritanceException(String.format(
                  "Attempting to set [%s] as the root inheritance object - only [Artifact] is allowed", artifactType));
         }
      } else {
         if (superTypes.contains(artifactType)) {
            throw new OseeInvalidInheritanceException(String.format(
                  "Circular inheritance detected for artifact type [%s]", artifactType));
         }
         Collection<ArtifactType> existingSuperTypes = artifactTypeToSuperTypeMap.getValues(artifactType);
         if (existingSuperTypes != null) {

         }
         cacheArtifactTypeInheritance(artifactType, superTypes);
      }
   }

   /**
    * Takes branch hierarchy and artifact type hierarchy into account when determining valid attribute types
    * 
    * @param artifactType
    * @param branch
    * @return all attribute types that are valid for artifacts of the specified type on the specified branch
    */
   public Set<AttributeType> getAttributeTypes(ArtifactType artifactType, Branch branch) {
      HashSet<AttributeType> attributeTypes = new HashSet<AttributeType>();
      getAttributeTypes(attributeTypes, artifactType, branch);
      return attributeTypes;
   }

   private void getAttributeTypes(HashSet<AttributeType> attributeTypes, ArtifactType artifactType, Branch branch) {
      Branch branchCursor = branch;
      while (true) {
         Collection<AttributeType> items = artifactToAttributeMap.get(artifactType, branchCursor);
         if (items != null) {
            attributeTypes.addAll(items);
         }
         if (branchCursor.isSystemRootBranch()) {
            break;
         } else {
            branchCursor = branchCursor.getParentBranch();
         }
      }

      for (ArtifactType superType : artifactType.getSuperArtifactTypes()) {
         getAttributeTypes(attributeTypes, superType, branch);
      }
   }

   CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>> getArtifactToAttributeMap() {
      return artifactToAttributeMap;
   }

   public synchronized void ensurePopulated() throws OseeCoreException {
      if (!duringPopulate) {
         duringPopulate = true;
         getEnumTypeCache().ensurePopulated();
         getAttributeTypeCache().ensurePopulated();
         getArtifactTypeCache().ensurePopulated();

         ensureTypeValidityPopulated();
         getRelationTypeCache().ensurePopulated();
         duringPopulate = false;
      }
   }

   void ensureTypeValidityPopulated() throws OseeCoreException {
      if (artifactToAttributeMap.isEmpty()) {
         getDataAccessor().loadAllTypeValidity(this, getDataFactory());
      }
   }

}
