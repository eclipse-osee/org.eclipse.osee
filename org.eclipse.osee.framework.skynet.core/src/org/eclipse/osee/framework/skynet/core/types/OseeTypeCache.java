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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.BaseOseeType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeCache {

   private final ArtifactTypeCache artifactCache = new ArtifactTypeCache();
   private final AttributeTypeCache attributeCache = new AttributeTypeCache();
   private final RelationTypeCache relationCache = new RelationTypeCache();
   private final OseeEnumTypeCache oseeEnumTypeCache = new OseeEnumTypeCache();

   private final HashCollection<ArtifactType, ArtifactType> artifactTypeToSuperTypeMap =
         new HashCollection<ArtifactType, ArtifactType>();

   private final CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>> artifactToAttributeMap =
         new CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>>();

   private final IOseeTypeDataAccessor dataAccessor;
   private final IOseeTypeFactory factory;
   private boolean duringPopulate;

   public OseeTypeCache(IOseeTypeDataAccessor dataAccessor, IOseeTypeFactory factory) {
      this.dataAccessor = dataAccessor;
      this.factory = factory;
      this.duringPopulate = false;
   }

   private IOseeTypeDataAccessor getDataAccessor() {
      return dataAccessor;
   }

   private IOseeTypeFactory getDataFactory() {
      return factory;
   }

   public ArtifactTypeCache getArtifactTypeData() {
      return artifactCache;
   }

   public AttributeTypeCache getAttributeTypeData() {
      return attributeCache;
   }

   public RelationTypeCache getRelationTypeData() {
      return relationCache;
   }

   public OseeEnumTypeCache getEnumTypeData() {
      return oseeEnumTypeCache;
   }

   public void cacheArtifactTypeInheritance(ArtifactType artifactType, Collection<ArtifactType> superType) throws OseeCoreException {
      artifactTypeToSuperTypeMap.put(artifactType, superType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, AttributeType attributeType, Branch branch) throws OseeCoreException {
      Collection<AttributeType> attributeTypes = artifactToAttributeMap.get(branch, artifactType);
      if (attributeTypes == null) {
         attributeTypes = new HashSet<AttributeType>();
         artifactToAttributeMap.put(branch, artifactType, attributeTypes);
      }
      attributeTypes.add(attributeType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException {
      Collection<AttributeType> cachedItems = artifactToAttributeMap.get(branch, artifactType);
      if (cachedItems == null) {
         cachedItems = new HashSet<AttributeType>(attributeTypes);
         artifactToAttributeMap.put(branch, artifactType, cachedItems);
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
   public Collection<AttributeType> getAttributeTypes(ArtifactType artifactType, Branch branch) {
      HashSet<AttributeType> attributeTypes = new HashSet<AttributeType>();
      getAttributeTypes(attributeTypes, artifactType, branch);
      return attributeTypes;
   }

   private void getAttributeTypes(HashSet<AttributeType> attributeTypes, ArtifactType artifactType, Branch branch) {
      Branch branchCursor = branch;
      while (true) {
         Collection<AttributeType> items = artifactToAttributeMap.get(branchCursor, artifactType);
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

   private synchronized void ensurePopulated() throws OseeCoreException {
      if (!duringPopulate) {
         duringPopulate = true;
         getArtifactTypeData().ensureTypeCachePopulated();
         getEnumTypeData().ensureTypeCachePopulated();
         getAttributeTypeData().ensureTypeCachePopulated();
         ensureTypeValidityPopulated();
         getRelationTypeData().ensureTypeCachePopulated();
         duringPopulate = false;
      }
   }

   void ensureTypeValidityPopulated() throws OseeCoreException {
      if (artifactToAttributeMap.isEmpty()) {
         getDataAccessor().loadAllTypeValidity(this, getDataFactory());
      }
   }

   public abstract class OseeTypeCacheData<T extends BaseOseeType> {
      private final HashMap<String, T> nameToTypeMap = new HashMap<String, T>();
      private final HashMap<Integer, T> idToTypeMap = new HashMap<Integer, T>();
      private final HashMap<String, T> guidToTypeMap = new HashMap<String, T>();

      private OseeTypeCacheData() {
      }

      public boolean existsByGuid(String guid) throws OseeCoreException {
         ensurePopulated();
         return guidToTypeMap.containsKey(guid);
      }

      public void decacheType(T type) throws OseeCoreException {
         if (type == null) {
            throw new OseeArgumentException("Caching a null value is not allowed");
         }
         nameToTypeMap.remove(type.getName());
         guidToTypeMap.remove(type.getGuid());
         idToTypeMap.remove(type.getTypeId());
      }

      public void cacheType(T type) throws OseeCoreException {
         if (type == null) {
            throw new OseeArgumentException("Caching a null value is not allowed");
         }
         nameToTypeMap.put(type.getName(), type);
         guidToTypeMap.put(type.getGuid(), type);
         if (type.getTypeId() != BaseOseeType.UNPERSISTTED_VALUE) {
            idToTypeMap.put(type.getTypeId(), type);
         }
      }

      public Collection<T> getAllTypes() throws OseeCoreException {
         ensurePopulated();
         return new ArrayList<T>(guidToTypeMap.values());
      }

      public T getTypeById(int typeId) throws OseeCoreException {
         ensurePopulated();
         return idToTypeMap.get(typeId);
      }

      public T getTypeByName(String typeName) throws OseeCoreException {
         ensurePopulated();
         return nameToTypeMap.get(typeName);
      }

      public T getTypeByGuid(String typeGuid) throws OseeCoreException {
         ensurePopulated();
         return guidToTypeMap.get(typeGuid);
      }

      protected Collection<T> getDirtyTypes() throws OseeCoreException {
         ensurePopulated();
         Collection<T> dirtyItems = new ArrayList<T>();
         for (T type : guidToTypeMap.values()) {
            if (type.isDirty()) {
               dirtyItems.add(type);
            }
         }
         return dirtyItems;
      }

      public void storeAllModified() throws OseeCoreException {
         Collection<T> items = getDirtyTypes();
         synchronized (idToTypeMap) {
            storeItems(items);
            for (T type : items) {
               idToTypeMap.put(type.getTypeId(), type);
               type.persist();
            }
         }
      }

      protected void ensureTypeCachePopulated() throws OseeCoreException {
         if (guidToTypeMap.isEmpty()) {
            reloadCache();
         }
      }

      protected abstract void storeItems(Collection<T> items) throws OseeCoreException;

      public void storeByGuid(Collection<String> guids) throws OseeCoreException {
         ensurePopulated();
         List<T> items = new ArrayList<T>();
         for (String guid : guids) {
            T type = getTypeByGuid(guid);
            if (type == null) {
               throw new OseeTypeDoesNotExist(String.format("Item was not found [%s]", guid));
            }
            items.add(type);
         }
         if (!items.isEmpty()) {
            storeItems(items);
         }
      }

      public abstract void reloadCache() throws OseeCoreException;
   }

   public final class ArtifactTypeCache extends OseeTypeCacheData<ArtifactType> {
      @Override
      public void reloadCache() throws OseeCoreException {
         getDataAccessor().loadAllArtifactTypes(OseeTypeCache.this, getDataFactory());
      }

      @Override
      protected void storeItems(Collection<ArtifactType> items) throws OseeCoreException {
         getDataAccessor().storeArtifactType(items);
      }

      @SuppressWarnings("unchecked")
      @Override
      public void storeAllModified() throws OseeCoreException {
         Collection<ArtifactType> dirtyTypes = getDirtyTypes();
         super.storeAllModified();
         for (ArtifactType artifactType : dirtyTypes) {
            getDataAccessor().storeTypeInheritance(artifactType,
                  Collections.toSet(artifactType.getSuperArtifactTypes()));
         }
      }
   }

   public final class AttributeTypeCache extends OseeTypeCacheData<AttributeType> {
      @Override
      public void reloadCache() throws OseeCoreException {
         getDataAccessor().loadAllAttributeTypes(OseeTypeCache.this, getDataFactory());
      }

      @Override
      protected void storeItems(Collection<AttributeType> items) throws OseeCoreException {
         getDataAccessor().storeAttributeType(items);
      }
   }

   public final class RelationTypeCache extends OseeTypeCacheData<RelationType> {
      @Override
      public void reloadCache() throws OseeCoreException {
         getDataAccessor().loadAllRelationTypes(OseeTypeCache.this, getDataFactory());
      }

      @Override
      protected void storeItems(Collection<RelationType> items) throws OseeCoreException {
         getDataAccessor().storeRelationType(items);
      }
   }

   public final class OseeEnumTypeCache extends OseeTypeCacheData<OseeEnumType> {

      private final HashCollection<OseeEnumType, OseeEnumEntry> enumTypeToEntryMap =
            new HashCollection<OseeEnumType, OseeEnumEntry>();
      private final Map<OseeEnumEntry, OseeEnumType> enumEntryToEnumType = new HashMap<OseeEnumEntry, OseeEnumType>();

      public void cacheEnumEntries(OseeEnumType oseeEnumType, Collection<OseeEnumEntry> oseeEnumEntries) throws OseeCoreException {

         for (OseeEnumEntry entry : oseeEnumEntries) {
            if (entry.getName() == null) {
               throw new OseeArgumentException("EnumEntry name violation - null is not allowed");
            }
            if (entry.ordinal() < 0) {
               throw new OseeArgumentException("EnumEntry ordinal must be greater than zero");
            }
            for (OseeEnumEntry existingEntry : oseeEnumEntries) {
               if (entry.getName().equals(existingEntry.getName())) {
                  throw new OseeArgumentException(String.format("Unique enumEntry name violation - %s already exists.",
                        entry));
               }
               if (entry.ordinal() == existingEntry.ordinal()) {
                  throw new OseeArgumentException(
                        String.format(
                              "Unique enumEntry ordinal violation - ordinal [%d] is used by existing entry:[%s] and new entry:[%s]",
                              entry.ordinal(), existingEntry, entry));
               }
            }
         }
         List<OseeEnumEntry> existingEntries = getEnumEntries(oseeEnumType);
         for (OseeEnumEntry entries : Collections.setComplement(existingEntries, oseeEnumEntries)) {
            entries.setModificationType(ModificationType.DELETED);
         }
         for (OseeEnumEntry entry : oseeEnumEntries) {
            enumTypeToEntryMap.put(oseeEnumType, entry);
            enumEntryToEnumType.put(entry, oseeEnumType);
         }
      }

      public OseeEnumType getEnumType(OseeEnumEntry oseeEnumEntry) throws OseeCoreException {
         //         ensurePopulated();
         return enumEntryToEnumType.get(oseeEnumEntry);
      }

      public List<OseeEnumEntry> getEnumEntries(OseeEnumType oseeEnumType) throws OseeCoreException {
         ensurePopulated();
         List<OseeEnumEntry> itemsToReturn = new ArrayList<OseeEnumEntry>();
         Collection<OseeEnumEntry> entries = enumTypeToEntryMap.getValues(oseeEnumType);
         if (entries != null) {
            itemsToReturn.addAll(entries);
         }
         return itemsToReturn;
      }

      @Override
      public void reloadCache() throws OseeCoreException {
         getDataAccessor().loadAllOseeEnumTypes(OseeTypeCache.this, getDataFactory());
      }

      @Override
      protected void storeItems(Collection<OseeEnumType> items) throws OseeCoreException {
         getDataAccessor().storeOseeEnumType(items);
      }
   }
}
