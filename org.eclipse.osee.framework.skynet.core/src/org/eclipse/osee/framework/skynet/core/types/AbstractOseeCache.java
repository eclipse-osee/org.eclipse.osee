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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.types.field.UniqueIdField;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeCache<T extends IOseeStorableType> {
   private final HashCollection<String, T> nameToTypeMap = new HashCollection<String, T>();
   private final HashMap<Integer, T> idToTypeMap = new HashMap<Integer, T>();
   private final HashMap<String, T> guidToTypeMap = new HashMap<String, T>();

   private final IOseeTypeFactory factory;
   private final IOseeDataAccessor<T> dataAccessor;
   private boolean duringPopulate;

   public AbstractOseeCache(IOseeTypeFactory factory, IOseeDataAccessor<T> dataAccessor) {
      this.duringPopulate = false;
      this.factory = factory;
      this.dataAccessor = dataAccessor;
   }

   public int size() {
      return guidToTypeMap.size();
   }

   protected IOseeDataAccessor<T> getDataAccessor() {
      return dataAccessor;
   }

   protected IOseeTypeFactory getDataFactory() {
      return factory;
   }

   public boolean existsByGuid(String guid) throws OseeCoreException {
      ensurePopulated();
      return guidToTypeMap.containsKey(guid);
   }

   public void decache(T... types) throws OseeCoreException {
      if (types == null) {
         throw new OseeArgumentException("De-Caching a null value is not allowed");
      }
      for (T type : types) {
         decache(type);
      }
   }

   public void decache(T type) throws OseeCoreException {
      if (type == null) {
         throw new OseeArgumentException("De-Caching a null value is not allowed");
      }
      ensurePopulated();
      guidToTypeMap.remove(type.getGuid());
      decacheByName(type);
      if (type.getId() != UniqueIdField.UNPERSISTTED_VALUE) {
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

   public void cache(T... types) throws OseeCoreException {
      if (types == null) {
         throw new OseeArgumentException("Caching a null value is not allowed");
      }
      for (T type : types) {
         cache(type);
      }
   }

   public void cache(T type) throws OseeCoreException {
      if (type == null) {
         throw new OseeArgumentException("Caching a null value is not allowed");
      }
      ensurePopulated();
      nameToTypeMap.put(type.getName(), type);
      guidToTypeMap.put(type.getGuid(), type);
      cacheById(type);
   }

   private void cacheById(T type) throws OseeCoreException {
      if (type == null) {
         throw new OseeArgumentException("Caching a null value is not allowed");
      }
      ensurePopulated();
      if (type.getId() != UniqueIdField.UNPERSISTTED_VALUE) {
         idToTypeMap.put(type.getId(), type);
      }
   }

   public Collection<T> getAll() throws OseeCoreException {
      ensurePopulated();
      return new ArrayList<T>(guidToTypeMap.values());
   }

   public T getById(int typeId) throws OseeCoreException {
      ensurePopulated();
      return idToTypeMap.get(typeId);
   }

   public T getUniqueByName(String typeName) throws OseeCoreException {
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

   public T getByGuid(String typeGuid) throws OseeCoreException {
      ensurePopulated();
      return guidToTypeMap.get(typeGuid);
   }

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

   public void storeAllModified() throws OseeCoreException {
      storeItems(getAllDirty());
   }

   public void ensurePopulated() throws OseeCoreException {
      if (guidToTypeMap.isEmpty()) {
         if (!duringPopulate) {
            duringPopulate = true;
            reloadCache();
            duringPopulate = false;
         }
      }
   }

   public void storeByGuid(Collection<String> guids) throws OseeCoreException {
      ensurePopulated();
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

   public void reloadCache() throws OseeCoreException {
      getDataAccessor().load(this, getDataFactory());
   }

   @SuppressWarnings("unchecked")
   public void storeItem(AbstractOseeType item) throws OseeCoreException {
      storeItems(Collections.singletonList((T) item));
   }

   public void storeItems(Collection<T> toStore) throws OseeCoreException {
      if (!toStore.isEmpty()) {
         getDataAccessor().store(this, toStore);
         synchronized (this) {
            for (T type : toStore) {
               decache(type);
               cache(type);
            }
         }
      }
   }
}
