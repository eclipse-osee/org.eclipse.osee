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
package org.eclipse.osee.framework.core.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeCache<T extends AbstractOseeType> implements IOseeCache<T> {
   private final HashCollection<String, T> nameToTypeMap = new HashCollection<String, T>();
   private final HashMap<Integer, T> idToTypeMap = new HashMap<Integer, T>();
   private final HashMap<String, T> guidToTypeMap = new HashMap<String, T>();

   private final IOseeDataAccessor<T> dataAccessor;
   private final OseeCacheEnum cacheId;
   private final boolean uniqueName;
   private boolean ensurePopulatedRanOnce;

   protected AbstractOseeCache(OseeCacheEnum cacheId, IOseeDataAccessor<T> dataAccessor, boolean uniqueName) {
      this.cacheId = cacheId;
      this.ensurePopulatedRanOnce = false;
      this.dataAccessor = dataAccessor;
      this.uniqueName = uniqueName;
   }

   public final synchronized void decacheAll() {
      clearAdditionalData();
      nameToTypeMap.clear();
      idToTypeMap.clear();
      guidToTypeMap.clear();
      this.ensurePopulatedRanOnce = false;
   }

   protected void clearAdditionalData() {

   }

   @Override
   public OseeCacheEnum getCacheId() {
      return cacheId;
   }

   @Override
   public int size() {
      return guidToTypeMap.size();
   }

   protected IOseeDataAccessor<T> getDataAccessor() {
      return dataAccessor;
   }

   public boolean existsByGuid(String guid) throws OseeCoreException {
      ensurePopulated();
      return guidToTypeMap.containsKey(guid);
   }

   @Override
   public void decache(T... types) throws OseeCoreException {
      Conditions.checkNotNull(types, "types to de-cache");
      for (T type : types) {
         decache(type);
      }
   }

   @Override
   public void decache(T type) throws OseeCoreException {
      Conditions.checkNotNull(type, "type to de-cache");
      ensurePopulated();
      guidToTypeMap.remove(type.getGuid());
      decacheByName(type);
      if (type.getId() != IOseeStorable.UNPERSISTTED_VALUE) {
         idToTypeMap.remove(type.getId());
      }
   }

   private void decacheByName(T type) {
      Set<String> keysToRemove = new HashSet<String>();

      for (String name : nameToTypeMap.keySet()) {
         Collection<T> items = nameToTypeMap.getValues(name);
         if (items != null && items.contains(type)) {
            keysToRemove.add(name);
         }
      }

      for (String key : keysToRemove) {
         nameToTypeMap.removeValue(key, type);
      }
   }

   @Override
   public void cache(T... types) throws OseeCoreException {
      Conditions.checkNotNull(types, "types to cache");
      for (T type : types) {
         cache(type);
      }
   }

   @Override
   public void cache(T type) throws OseeCoreException {
      Conditions.checkNotNull(type, "type to cache");
      ensurePopulated();
      nameToTypeMap.put(type.getName(), type);
      guidToTypeMap.put(type.getGuid(), type);
      cacheById(type);
      if (isNameUniquenessEnforced()) {
         checkNameUnique(type);
      }
   }

   public boolean isNameUniquenessEnforced() {
      return uniqueName;
   }

   private void checkNameUnique(T type) throws OseeCoreException {
      ensurePopulated();
      Collection<T> cachedTypes = getByName(type.getName());
      Set<String> itemsFound = new HashSet<String>();
      // TODO Need to revisit this based on deleted types
      //      for (T cachedType : cachedTypes) {
      //         if (!cachedType.getGuid().equals(type.getGuid()) && !cachedType.getModificationType().isDeleted()) {
      //            itemsFound.add(String.format("[%s:%s]", cachedType.getName(), cachedType.getGuid()));
      //         }
      //      }
      if (cachedTypes.size() > 1) {
         throw new OseeStateException(String.format("Item [%s:%s] does not have a unique name. Matching types - ",
               type.getName(), type.getGuid(), itemsFound));
      }
   }

   private void cacheById(T type) throws OseeCoreException {
      Conditions.checkNotNull(type, "type to cache");
      ensurePopulated();
      if (type.getId() != IOseeStorable.UNPERSISTTED_VALUE) {
         idToTypeMap.put(type.getId(), type);
      }
   }

   @Override
   public Collection<T> getAll() throws OseeCoreException {
      ensurePopulated();
      return new ArrayList<T>(guidToTypeMap.values());
   }

   @Override
   public T getById(int typeId) throws OseeCoreException {
      ensurePopulated();
      return idToTypeMap.get(typeId);
   }

   public T getUniqueByName(String typeName) throws OseeCoreException {
      ensurePopulated();
      Collection<T> values = getByName(typeName);
      if (values.size() > 1) {
         throw new OseeStateException(String.format("Multiple items matching [%s] name exist", typeName));
      }
      return values.isEmpty() ? null : values.iterator().next();
   }

   public Collection<T> getByName(String typeName) throws OseeCoreException {
      ensurePopulated();
      Collection<T> types = new ArrayList<T>();
      Collection<T> values = nameToTypeMap.getValues(typeName);
      if (values != null) {
         types.addAll(values);
      }
      return types;
   }

   public T getBySoleName(String typeName) throws OseeCoreException {
      ensurePopulated();
      Collection<T> types = getByName(typeName);
      if (types.size() != 1) {
         throw new OseeArgumentException(
               "AbstractOseeCache expected 1 type but found " + types.size() + " types for " + typeName);
      }
      return types.iterator().next();
   }

   public T getByGuid(String guid) throws OseeCoreException {
      ensurePopulated();
      if (!GUID.isValid(guid)) {
         throw new OseeArgumentException(String.format("[%s] is not a valid guid", guid));
      }
      return guidToTypeMap.get(guid);
   }

   public T get(Identity token) throws OseeCoreException {
      return getByGuid(token.getGuid());
   }

   @Override
   public Collection<T> getAllDirty() throws OseeCoreException {
      ensurePopulated();
      Collection<T> dirtyItems = new HashSet<T>();
      for (T type : guidToTypeMap.values()) {
         if (type.isDirty()) {
            dirtyItems.add(type);
         }
      }
      return dirtyItems;
   }

   @Override
   public void storeAllModified() throws OseeCoreException {
      storeItems(getAllDirty());
   }

   @Override
   public synchronized void ensurePopulated() throws OseeCoreException {
      if (!ensurePopulatedRanOnce) {
         ensurePopulatedRanOnce = true;
         reloadCache();
      }
   }

   public void storeByGuid(Collection<String> guids) throws OseeCoreException {
      ensurePopulated();
      Conditions.checkNotNull(guids, "guids to store");
      Collection<T> items = new HashSet<T>();
      for (String guid : guids) {
         T type = getByGuid(guid);
         if (type == null) {
            throw new OseeTypeDoesNotExist(String.format("Item was not found [%s]", guid));
         }
         items.add(type);
      }
      storeItems(items);
   }

   public synchronized void reloadCache() throws OseeCoreException {
      getDataAccessor().load(this);
   }

   @Override
   public void storeItems(T... items) throws OseeCoreException {
      Conditions.checkNotNull(items, "items to store");
      storeItems(Arrays.asList(items));
   }

   @Override
   public void storeItems(Collection<T> toStore) throws OseeCoreException {
      Conditions.checkNotNull(toStore, "items to store");
      if (!toStore.isEmpty()) {
         getDataAccessor().store(toStore);
         synchronized (this) {
            for (T type : toStore) {
               decache(type);
               cache(type);
            }
         }
      }
   }
}
