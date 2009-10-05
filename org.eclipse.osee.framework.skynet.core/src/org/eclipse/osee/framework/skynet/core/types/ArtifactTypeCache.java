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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
public final class ArtifactTypeCache extends AbstractOseeCache<ArtifactType> {

   private final HashCollection<ArtifactType, ArtifactType> artifactTypeToSuperTypeMap =
         new HashCollection<ArtifactType, ArtifactType>();

   private final CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>> artifactToAttributeMap =
         new CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>>();

   public ArtifactTypeCache(IOseeTypeFactory factory, IOseeDataAccessor<ArtifactType> dataAccessor) {
      super(factory, dataAccessor);
   }

   public ArtifactType createType(String guid, boolean isAbstract, String artifactTypeName) throws OseeCoreException {
      ArtifactType artifactType = getByGuid(guid);
      if (artifactType == null) {
         artifactType = getDataFactory().createArtifactType(this, guid, isAbstract, artifactTypeName);
      } else {
         decache(artifactType);
         artifactType.setName(artifactTypeName);
         artifactType.setAbstract(isAbstract);
      }
      cache(artifactType);
      return artifactType;
   }

   public void cacheTypeValidity(ArtifactType artifactType, AttributeType attributeType, Branch branch) throws OseeCoreException {
      ensurePopulated();
      Collection<AttributeType> attributeTypes = artifactToAttributeMap.get(artifactType, branch);
      if (attributeTypes == null) {
         attributeTypes = new HashSet<AttributeType>();
         artifactToAttributeMap.put(artifactType, branch, attributeTypes);
      }
      attributeTypes.add(attributeType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException {
      if (branch == null) {
         throw new OseeArgumentException("branch cannot be null");
      }
      if (attributeTypes == null) {
         throw new OseeArgumentException("attribute type list cannot be null");
      }
      ensurePopulated();
      if (attributeTypes.isEmpty()) {
         artifactToAttributeMap.remove(artifactType, branch);
      } else {
         Collection<AttributeType> cachedItems = artifactToAttributeMap.get(artifactType, branch);
         if (cachedItems == null) {
            cachedItems = new HashSet<AttributeType>(attributeTypes);
            artifactToAttributeMap.put(artifactType, branch, cachedItems);
         } else {
            cachedItems.clear();
            cachedItems.addAll(attributeTypes);
         }
      }
   }

   public Collection<ArtifactType> getArtifactSuperType(ArtifactType artifactType) throws OseeCoreException {
      ensurePopulated();
      Collection<ArtifactType> types = new HashSet<ArtifactType>();
      Collection<ArtifactType> stored = artifactTypeToSuperTypeMap.getValues(artifactType);
      if (stored != null) {
         types.addAll(stored);
      }
      return types;
   }

   public void cacheArtifactSuperType(ArtifactType artifactType, Collection<ArtifactType> superTypes) throws OseeCoreException {
      if (artifactType == null) {
         throw new OseeArgumentException("artifactType cannot be null");
      }
      if (superTypes == null || superTypes.isEmpty()) {
         if (!artifactType.getName().equals("Artifact")) {
            throw new OseeInvalidInheritanceException(String.format(
                  "All artifacts must inherit from [Artifact] - attempted make [%s] have null inheritance",
                  artifactType));
         }
      } else {
         if (superTypes.contains(artifactType)) {
            throw new OseeInvalidInheritanceException(String.format(
                  "Circular inheritance detected for artifact type [%s]", artifactType));
         }
         ensurePopulated();
         artifactTypeToSuperTypeMap.removeValues(artifactType);
         artifactTypeToSuperTypeMap.put(artifactType, superTypes);
      }
   }

   public Collection<AttributeType> getLocalAttributeTypes(ArtifactType artifactType, Branch branch) throws OseeCoreException {
      ensurePopulated();
      Collection<AttributeType> types = new HashSet<AttributeType>();
      Collection<AttributeType> data = artifactToAttributeMap.get(artifactType, branch);
      if (data != null) {
         types.addAll(data);
      }
      return types;
   }

   public Map<Branch, Collection<AttributeType>> getLocalAttributeTypes(ArtifactType artifactType) throws OseeCoreException {
      ensurePopulated();
      Map<Branch, Collection<AttributeType>> types = new HashMap<Branch, Collection<AttributeType>>();
      Map<Branch, Collection<AttributeType>> data = artifactToAttributeMap.getKeyedValues(artifactType);
      if (data != null) {
         types.putAll(data);
      }
      return types;
   }

   public Set<AttributeType> getAttributeTypes(ArtifactType artifactType, Branch branch) throws OseeCoreException {
      ensurePopulated();
      Set<AttributeType> attributeTypes = new HashSet<AttributeType>();
      getAttributeTypes(attributeTypes, artifactType, branch);
      return attributeTypes;
   }

   private void getAttributeTypes(Set<AttributeType> attributeTypes, ArtifactType artifactType, Branch branch) throws OseeCoreException {
      Branch branchCursor = branch;
      while (true) {
         Collection<AttributeType> items = artifactToAttributeMap.get(artifactType, branchCursor);
         if (items != null) {
            attributeTypes.addAll(items);
         }
         if (branchCursor.getBranchType().isSystemRootBranch()) {
            break;
         } else {
            branchCursor = branchCursor.getParentBranch();
         }
      }

      for (ArtifactType superType : artifactType.getSuperArtifactTypes()) {
         getAttributeTypes(attributeTypes, superType, branch);
      }
   }

   public Collection<ArtifactType> getDescendants(ArtifactType artifactType, boolean isRecursionAllowed) throws OseeCoreException {
      ensurePopulated();
      Collection<ArtifactType> descendants = new HashSet<ArtifactType>();
      populateDescendants(artifactType, descendants, isRecursionAllowed);
      return descendants;
   }

   private void populateDescendants(ArtifactType artifactType, Collection<ArtifactType> descendants, boolean isRecursionAllowed) throws OseeCoreException {
      for (ArtifactType type : getAll()) {
         if (type.getSuperArtifactTypes().contains(artifactType)) {
            if (isRecursionAllowed) {
               populateDescendants(type, descendants, isRecursionAllowed);
            }
            descendants.add(type);
         }
      }
   }
}
