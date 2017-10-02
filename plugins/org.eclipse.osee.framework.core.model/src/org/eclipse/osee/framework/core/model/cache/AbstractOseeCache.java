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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeCache<T extends NamedIdBase> implements IOseeCache<T> {
   private final HashCollection<String, T> nameToTypeMap = new HashCollection<>(true, CopyOnWriteArrayList.class);
   private final ConcurrentHashMap<Long, T> idToTypeMap = new ConcurrentHashMap<>();
   private final OseeCacheEnum cacheId;

   protected AbstractOseeCache(OseeCacheEnum cacheId) {
      this.cacheId = cacheId;
   }

   @Override
   public synchronized void decacheAll() {
      nameToTypeMap.clear();
      idToTypeMap.clear();
   }

   @Override
   public OseeCacheEnum getCacheId() {
      return cacheId;
   }

   @Override
   public int size() {
      return idToTypeMap.size();
   }

   public boolean existsByGuid(Long id) {
      ensurePopulated();
      return idToTypeMap.containsKey(id);
   }

   @Override
   public void decache(T... types) {
      Conditions.checkNotNull(types, "types to de-cache");
      for (T type : types) {
         decache(type);
      }
   }

   @Override
   public void decache(T type) {
      Conditions.checkNotNull(type, "type to de-cache");
      ensurePopulated();
      decacheByName(type);

      if (type.isValid()) {
         idToTypeMap.remove(type.getId());
      }
   }

   /**
    * this method is intended for use by subclasses only. The calling method must synchronize the use of this view of
    * the views because it is not a copy. This method exists to improve performance for subclasses
    */
   protected synchronized Collection<T> getRawValues() {
      ensurePopulated();
      return idToTypeMap.values();
   }

   private void decacheByName(T type) {
      Set<String> keysToRemove = new HashSet<>();

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
   public void cache(T... types) {
      Conditions.checkNotNull(types, "types to cache");
      for (T type : types) {
         cache(type);
      }
   }

   @Override
   public void cache(T type) {
      Conditions.checkNotNull(type, "type to cache");
      ensurePopulated();
      nameToTypeMap.put(type.getName(), type);
      idToTypeMap.putIfAbsent(type.getId(), type);
   }

   @Override
   public Collection<T> getAll() {
      ensurePopulated();
      return new ArrayList<T>(idToTypeMap.values());
   }

   @Override
   public T getById(Number typeId) {
      ensurePopulated();
      return idToTypeMap.get(typeId.longValue());
   }

   public T getUniqueByName(String typeName) {
      ensurePopulated();
      Collection<T> values = getByName(typeName);
      if (values.size() > 1) {
         throw new OseeStateException("Multiple items matching [%s] name exist", typeName);
      }
      return values.isEmpty() ? null : values.iterator().next();
   }

   public Collection<T> getByName(String typeName) {
      ensurePopulated();
      Collection<T> types = new ArrayList<>();
      Collection<T> values = nameToTypeMap.getValues(typeName);
      if (values != null) {
         types.addAll(values);
      }
      return types;
   }

   public T getBySoleName(String typeName) {
      ensurePopulated();
      Collection<T> types = getByName(typeName);
      if (types.size() != 1) {
         throw new OseeArgumentException("AbstractOseeCache expected 1 type but found [%d] types for [%s]",
            types.size(), typeName);
      }
      return types.iterator().next();
   }

   @Override
   public T getByGuid(Long id) {
      ensurePopulated();
      return idToTypeMap.get(id);
   }

   public T get(Id token) {
      ensurePopulated();
      return getByGuid(token.getId());
   }

   public void cacheFrom(AbstractOseeCache<T> source) {
      for (T type : source.getAll()) {
         cache(type);
      }
   }

   protected void ensurePopulated() {
      // Do nothing
   }
}