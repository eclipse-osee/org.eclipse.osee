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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

public class OseeTypeCache {

   private final HashMap<String, RelationType> nameToRelationTypeMap = new HashMap<String, RelationType>();
   private final HashMap<Integer, RelationType> idToRelationTypeMap = new HashMap<Integer, RelationType>();
   private final HashMap<String, RelationType> guidToRelationTypeMap = new HashMap<String, RelationType>();

   private final HashMap<String, AttributeType> nameToAttributeTypeMap = new HashMap<String, AttributeType>();
   private final HashMap<Integer, AttributeType> idToAttributeTypeMap = new HashMap<Integer, AttributeType>();
   private final HashMap<String, AttributeType> guidToAttributeTypeMap = new HashMap<String, AttributeType>();

   private final HashMap<String, ArtifactType> nameToArtifactTypeMap = new HashMap<String, ArtifactType>();
   private final HashMap<Integer, ArtifactType> idToArtifactTypeMap = new HashMap<Integer, ArtifactType>();
   private final HashMap<String, ArtifactType> guidToArtifactTypeMap = new HashMap<String, ArtifactType>();

   private final HashCollection<ArtifactType, ArtifactType> artifactTypeToSuperTypeMap =
         new HashCollection<ArtifactType, ArtifactType>();

   private final CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>> artifactToAttributeMap =
         new CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>>();

   private final IOseeTypeDataAccessor dataAccessor;
   private final IOseeTypeFactory factory;

   public OseeTypeCache(IOseeTypeDataAccessor dataAccessor, IOseeTypeFactory factory) {
      this.dataAccessor = dataAccessor;
      this.factory = factory;
   }

   public void cacheArtifactType(ArtifactType artifactType) {
      nameToArtifactTypeMap.put(artifactType.getName(), artifactType);
      idToArtifactTypeMap.put(artifactType.getArtTypeId(), artifactType);
      guidToArtifactTypeMap.put(artifactType.getGuid(), artifactType);
   }

   public void cacheArtifactTypeInheritance(ArtifactType artifactType, ArtifactType superType) {
      artifactTypeToSuperTypeMap.put(artifactType, superType);
   }

   public void cacheAttributeType(AttributeType attributeType) {
      nameToAttributeTypeMap.put(attributeType.getName(), attributeType);
      idToAttributeTypeMap.put(attributeType.getAttrTypeId(), attributeType);
      guidToAttributeTypeMap.put(attributeType.getGuid(), attributeType);
   }

   public void cacheRelationType(RelationType relationType) {
      nameToRelationTypeMap.put(relationType.getTypeName(), relationType);
      idToRelationTypeMap.put(relationType.getRelationTypeId(), relationType);
      guidToRelationTypeMap.put(relationType.getGuid(), relationType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, AttributeType attributeType, Branch branch) {
      Collection<AttributeType> attributeTypes = artifactToAttributeMap.get(branch, artifactType);
      if (attributeTypes == null) {
         attributeTypes = new HashSet<AttributeType>();
         artifactToAttributeMap.put(branch, artifactType, attributeTypes);
      }
      attributeTypes.add(attributeType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, Collection<AttributeType> attributeTypes, Branch branch) {
      Collection<AttributeType> cachedItems = artifactToAttributeMap.get(branch, artifactType);
      if (cachedItems == null) {
         cachedItems = new HashSet<AttributeType>(attributeTypes);
         artifactToAttributeMap.put(branch, artifactType, cachedItems);
      } else {
         cachedItems.clear();
         cachedItems.addAll(attributeTypes);
      }
   }

   public Collection<ArtifactType> getAllArtifactTypes() {
      return new ArrayList<ArtifactType>(idToArtifactTypeMap.values());
   }

   public Collection<AttributeType> getAllAttributeTypes() {
      return new ArrayList<AttributeType>(idToAttributeTypeMap.values());
   }

   public Collection<RelationType> getAllRelationTypes() {
      return new ArrayList<RelationType>(idToRelationTypeMap.values());
   }

   public ArtifactType getArtifactTypeById(int artTypeId) {
      return idToArtifactTypeMap.get(artTypeId);
   }

   public ArtifactType getArtifactTypeByName(String artifactTypeName) {
      return nameToArtifactTypeMap.get(artifactTypeName);
   }

   public AttributeType getAttributeTypeById(int artTypeId) {
      return idToAttributeTypeMap.get(artTypeId);
   }

   public AttributeType getAttributeTypeByName(String artifactTypeName) {
      return nameToAttributeTypeMap.get(artifactTypeName);
   }

   public RelationType getRelationTypeById(int artTypeId) {
      return idToRelationTypeMap.get(artTypeId);
   }

   public RelationType getRelationTypeByName(String artifactTypeName) {
      return nameToRelationTypeMap.get(artifactTypeName);
   }

   public Collection<ArtifactType> getArtifactSuperType(ArtifactType artifactType) {
      Collection<ArtifactType> types = new HashSet<ArtifactType>();
      Collection<ArtifactType> stored = artifactTypeToSuperTypeMap.getValues(artifactType);
      if (stored != null) {
         types.addAll(stored);
      }
      return types;
   }

   public Collection<AttributeType> getAttributeTypes(ArtifactType artifactType) {
      Set<AttributeType> attributeTypes = new HashSet<AttributeType>();
      for (Entry<Pair<Branch, ArtifactType>, Collection<AttributeType>> entries : artifactToAttributeMap.entrySet()) {
         if (artifactType.equals(entries.getKey().getSecond())) {
            Collection<AttributeType> list = entries.getValue();
            if (list != null) {
               attributeTypes.addAll(list);
            }
         }
      }
      return attributeTypes;
   }

   /**
    * Takes branch hierarchy and artifact type hierarchy into account when determining valid attribute types
    * 
    * @param artifactType
    * @param branch
    * @return all attribute types that are valid for artifacts of the specified type on the specified branch
    */
   public Collection<AttributeType> getAttributeTypes(ArtifactType artifactType, Branch branch) {
      HashSet<AttributeType> attributeTypes = new HashSet<AttributeType>();
      getAttributeTypes(attributeTypes, artifactType, branch);
      return attributeTypes;
   }

   private void getAttributeTypes(HashSet<AttributeType> attributeTypes, ArtifactType artifactType, Branch branch) {
      Branch branchCursor = branch;
      while (true) {
         Collection<AttributeType> items = artifactToAttributeMap.get(branch, artifactType);
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

   public synchronized void ensureCachePopulated() throws OseeCoreException {
      if (idToArtifactTypeMap.isEmpty()) {
         dataAccessor.loadAllArtifactTypes(this, factory);
      }
      if (artifactToAttributeMap.isEmpty()) {
         dataAccessor.loadAllTypeValidity(this, factory);
      }
      if (idToAttributeTypeMap.isEmpty()) {
         dataAccessor.loadAllAttributeTypes(this, factory);
      }
   }

   synchronized public void ensureTypeValidityPopulated() throws OseeCoreException {

   }

   synchronized public void ensureAttributeTypePopulated() throws OseeCoreException {

   }

   synchronized public void ensureRelationTypePopulated() throws OseeCoreException {
      if (idToRelationTypeMap.isEmpty()) {
         dataAccessor.loadAllRelationTypes(this, factory);
      }
   }
}
