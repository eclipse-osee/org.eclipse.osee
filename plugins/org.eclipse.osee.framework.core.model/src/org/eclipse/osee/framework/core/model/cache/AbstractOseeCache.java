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
package org.eclipse.osee.framework.core.model.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.TypeUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeCache<K, T extends AbstractOseeType<K>> implements IOseeCache<K, T> {
   private final HashCollection<String, T> nameToTypeMap = new HashCollection<String, T>(true,
      CopyOnWriteArrayList.class);
   private final ConcurrentHashMap<Long, T> idToTypeMap = new ConcurrentHashMap<Long, T>();
   private final ConcurrentHashMap<K, T> guidToTypeMap = new ConcurrentHashMap<K, T>();

   private final IOseeDataAccessor<K, T> dataAccessor;
   private final OseeCacheEnum cacheId;
   private final boolean uniqueName;
   private final AtomicBoolean wasLoaded;
   private long lastLoaded;

   protected AbstractOseeCache(OseeCacheEnum cacheId, IOseeDataAccessor<K, T> dataAccessor, boolean uniqueName) {
      this.lastLoaded = 0;
      this.cacheId = cacheId;
      this.wasLoaded = new AtomicBoolean(false);
      this.dataAccessor = dataAccessor;
      this.uniqueName = uniqueName;
   }

   public void invalidate() {
      wasLoaded.set(false);
   }

   @Override
   public final synchronized void decacheAll() {
      clearAdditionalData();
      nameToTypeMap.clear();
      idToTypeMap.clear();
      guidToTypeMap.clear();
      wasLoaded.set(false);
   }

   protected void clearAdditionalData() {
      // for subclass overriding
   }

   @Override
   public OseeCacheEnum getCacheId() {
      return cacheId;
   }

   @Override
   public int size() {
      return guidToTypeMap.size();
   }

   public boolean existsByGuid(K guid) throws OseeCoreException {
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
      if (type.isIdValid()) {
         idToTypeMap.remove(TypeUtil.getId(type));
      }
   }

   /**
    * this method is intended for use by subclasses only. The calling method must synchronize the use of this view of
    * the views because it is not a copy. This method exists to improve performance for subclasses
    */
   protected synchronized Collection<T> getRawValues() throws OseeCoreException {
      ensurePopulated();
      return guidToTypeMap.values();
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
      guidToTypeMap.putIfAbsent(type.getGuid(), type);
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
         throw new OseeStateException("Item [%s:%s] does not have a unique name. Matching types [%s]", type.getName(),
            type.getGuid(), itemsFound);
      }
   }

   private void cacheById(T type) throws OseeCoreException {
      Conditions.checkNotNull(type, "type to cache");
      ensurePopulated();
      if (type.isIdValid()) {
         idToTypeMap.putIfAbsent(TypeUtil.getId(type), type);
      }
   }

   @Override
   public Collection<T> getAll() throws OseeCoreException {
      ensurePopulated();
      return new ArrayList<T>(guidToTypeMap.values());
   }

   @Override
   public T getById(Number typeId) throws OseeCoreException {
      ensurePopulated();
      return idToTypeMap.get(typeId.longValue());
   }

   public T getUniqueByName(String typeName) throws OseeCoreException {
      ensurePopulated();
      Collection<T> values = getByName(typeName);
      if (values.size() > 1) {
         throw new OseeStateException("Multiple items matching [%s] name exist", typeName);
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
         throw new OseeArgumentException("AbstractOseeCache expected 1 type but found [%d] types for [%s]",
            types.size(), typeName);
      }
      return types.iterator().next();
   }

   @Override
   public T getByGuid(K guid) throws OseeCoreException {
      ensurePopulated();
      if (guid instanceof String) {
         if (!GUID.isValid((String) guid)) {
            throw new OseeArgumentException("[%s] is not a valid guid", guid);
         }
      }
      return guidToTypeMap.get(guid);
   }

   public T get(Identity<K> token) throws OseeCoreException {
      ensurePopulated();
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
      if (wasLoaded.compareAndSet(false, true)) {
         try {
            reloadCache();
         } catch (OseeCoreException ex) {
            wasLoaded.set(false);
            throw ex;
         }
      }
   }

   public void storeByGuid(Collection<K> guids) throws OseeCoreException {
      ensurePopulated();
      Conditions.checkNotNull(guids, "guids to store");
      Collection<T> items = new HashSet<T>();
      for (K guid : guids) {
         T type = getByGuid(guid);
         if (type == null) {
            throw new OseeTypeDoesNotExist(String.format("Item was not found [%s]", guid));
         }
         items.add(type);
      }
      storeItems(items);
   }

   @Override
   public long getLastLoaded() {
      return lastLoaded;
   }

   private void setLastLoaded(long lastLoaded) {
      this.lastLoaded = lastLoaded;
   }

   @Override
   public synchronized boolean reloadCache() throws OseeCoreException {
      dataAccessor.load(this);
      OseeLog.log(this.getClass(), Level.INFO, "Loaded " + getCacheId().toString().toLowerCase());
      setLastLoaded(System.currentTimeMillis());
      wasLoaded.set(true);
      return true;
   }

   @Override
   public void storeItems(T... items) throws OseeCoreException {
      storeItems(Arrays.asList(items));
   }

   @Override
   public void storeItems(Collection<T> toStore) throws OseeCoreException {
      Conditions.checkDoesNotContainNulls(toStore, "items to store");
      if (!toStore.isEmpty()) {
         dataAccessor.store(toStore);
         synchronized (this) {
            for (T type : toStore) {
               decache(type);
               if (StorageState.PURGED != type.getStorageState()) {
                  cache(type);
               }
            }
         }
      }
   }

   public void cacheFrom(AbstractOseeCache<K, T> source) throws OseeCoreException {
      for (T type : source.getAll()) {
         cache(type);
      }
   }
}
