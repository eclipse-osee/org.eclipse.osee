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
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeCache<T extends NamedIdBase> implements IOseeCache<T> {
   private final ConcurrentHashMap<String, T> nameToTypeMap = new ConcurrentHashMap<>();
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

   public boolean existsByName(String name) {
      ensurePopulated();
      return nameToTypeMap.containsKey(name);
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

      Iterator<T> iterator = nameToTypeMap.values().iterator();
      while (iterator.hasNext()) {
         if (iterator.next().equals(type)) {
            iterator.remove();
         }
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

   public T getByName(String typeName) {
      ensurePopulated();
      T value = nameToTypeMap.get(typeName);
      if (value == null) {
         throw new ItemDoesNotExist("Cache does not contain item with name [%s]", typeName);
      }
      return value;
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