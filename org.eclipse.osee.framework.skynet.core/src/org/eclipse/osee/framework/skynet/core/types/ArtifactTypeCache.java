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
public final class ArtifactTypeCache extends OseeTypeCacheData<ArtifactType> {

   private final HashCollection<ArtifactType, ArtifactType> artifactTypeToSuperTypeMap =
         new HashCollection<ArtifactType, ArtifactType>();

   private final CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>> artifactToAttributeMap =
         new CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>>();

   public ArtifactTypeCache(OseeTypeCache cache, IOseeTypeFactory factory, IOseeTypeDataAccessor dataAccessor) {
      super(cache, factory, dataAccessor);
   }

   @Override
   public void reloadCache() throws OseeCoreException {
      getDataAccessor().loadAllArtifactTypes(getCache(), getDataFactory());
      getDataAccessor().loadAllTypeValidity(getCache(), getDataFactory());
   }

   @Override
   protected void storeItems(Collection<ArtifactType> items) throws OseeCoreException {
      getDataAccessor().storeArtifactType(getCache(), items);
   }

   public ArtifactType createType(String guid, boolean isAbstract, String artifactTypeName) throws OseeCoreException {
      ArtifactType artifactType = getTypeByGuid(guid);
      if (artifactType == null) {
         artifactType = getDataFactory().createArtifactType(guid, isAbstract, artifactTypeName, getCache());
      } else {
         decacheType(artifactType);
         artifactType.setName(artifactTypeName);
         artifactType.setAbstract(isAbstract);
      }
      cacheType(artifactType);
      return artifactType;
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

   public void setArtifactSuperType(ArtifactType artifactType, Collection<ArtifactType> superTypes) throws OseeCoreException {
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
         cacheArtifactTypeInheritance(artifactType, superTypes);
      }
   }

   public Collection<AttributeType> getLocalAttributeTypes(ArtifactType artifactType, Branch branch) {
      Collection<AttributeType> types = new HashSet<AttributeType>();
      Collection<AttributeType> data = artifactToAttributeMap.get(artifactType, branch);
      if (data != null) {
         types.addAll(data);
      }
      return types;
   }

   public Map<Branch, Collection<AttributeType>> getLocalAttributeTypes(ArtifactType artifactType) {
      Map<Branch, Collection<AttributeType>> types = new HashMap<Branch, Collection<AttributeType>>();
      Map<Branch, Collection<AttributeType>> data = artifactToAttributeMap.getKeyedValues(artifactType);
      if (data != null) {
         types.putAll(data);
      }
      return types;
   }

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

}
