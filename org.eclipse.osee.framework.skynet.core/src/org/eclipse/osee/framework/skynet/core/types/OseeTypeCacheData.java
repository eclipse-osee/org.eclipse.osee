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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.BaseOseeType;

/**
 * @author Roberto E. Escobar
 */
public abstract class OseeTypeCacheData<T extends BaseOseeType> {
   private final HashMap<String, T> nameToTypeMap = new HashMap<String, T>();
   private final HashMap<Integer, T> idToTypeMap = new HashMap<Integer, T>();
   private final HashMap<String, T> guidToTypeMap = new HashMap<String, T>();
   private final OseeTypeCache cache;
   private final IOseeTypeFactory factory;
   private final IOseeTypeDataAccessor dataAccessor;
   private boolean duringPopulate;

   public OseeTypeCacheData(OseeTypeCache cache, IOseeTypeFactory factory, IOseeTypeDataAccessor dataAccessor) {
      this.duringPopulate = false;
      this.cache = cache;
      this.factory = factory;
      this.dataAccessor = dataAccessor;
   }

   protected OseeTypeCache getCache() {
      return cache;
   }

   protected IOseeTypeDataAccessor getDataAccessor() {
      return dataAccessor;
   }

   protected IOseeTypeFactory getDataFactory() {
      return factory;
   }

   public boolean existsByGuid(String guid) throws OseeCoreException {
      getCache().ensurePopulated();
      return guidToTypeMap.containsKey(guid);
   }

   public void decacheType(T type) throws OseeCoreException {
      if (type == null) {
         throw new OseeArgumentException("Caching a null value is not allowed");
      }
      nameToTypeMap.remove(type.getName());
      guidToTypeMap.remove(type.getGuid());
      if (type.getTypeId() != BaseOseeType.UNPERSISTTED_VALUE) {
         idToTypeMap.remove(type.getTypeId());
      }
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
      getCache().ensurePopulated();
      return new ArrayList<T>(guidToTypeMap.values());
   }

   public T getTypeById(int typeId) throws OseeCoreException {
      getCache().ensurePopulated();
      return idToTypeMap.get(typeId);
   }

   public T getTypeByName(String typeName) throws OseeCoreException {
      getCache().ensurePopulated();
      return nameToTypeMap.get(typeName);
   }

   public T getTypeByGuid(String typeGuid) throws OseeCoreException {
      getCache().ensurePopulated();
      return guidToTypeMap.get(typeGuid);
   }

   protected Collection<T> getDirtyTypes() throws OseeCoreException {
      getCache().ensurePopulated();
      Collection<T> dirtyItems = new HashSet<T>();
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
      getCache().ensurePopulated();
      Collection<T> items = new HashSet<T>();
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

   protected abstract void storeItems(Collection<T> items) throws OseeCoreException;

   public abstract void reloadCache() throws OseeCoreException;
}
