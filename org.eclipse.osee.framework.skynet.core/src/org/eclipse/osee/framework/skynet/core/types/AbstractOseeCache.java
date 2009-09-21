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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.AbstractOseeType;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeCache<T extends IOseeStorableType> {
   private final HashMap<String, T> nameToTypeMap = new HashMap<String, T>();
   private final HashMap<Integer, T> idToTypeMap = new HashMap<Integer, T>();
   private final HashMap<String, T> guidToTypeMap = new HashMap<String, T>();

   private final IOseeTypeFactory factory;
   private final IOseeTypeDataAccessor<T> dataAccessor;
   private boolean duringPopulate;

   public AbstractOseeCache(IOseeTypeFactory factory, IOseeTypeDataAccessor<T> dataAccessor) {
      this.duringPopulate = false;
      this.factory = factory;
      this.dataAccessor = dataAccessor;
   }

   protected IOseeTypeDataAccessor<T> getDataAccessor() {
      return dataAccessor;
   }

   protected IOseeTypeFactory getDataFactory() {
      return factory;
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
      if (type.getId() != AbstractOseeType.UNPERSISTTED_VALUE) {
         idToTypeMap.remove(type.getId());
      }
   }

   public void cacheType(T type) throws OseeCoreException {
      if (type == null) {
         throw new OseeArgumentException("Caching a null value is not allowed");
      }
      nameToTypeMap.put(type.getName(), type);
      guidToTypeMap.put(type.getGuid(), type);
      cacheTypeById(type);
   }

   private void cacheTypeById(T type) throws OseeCoreException {
      if (type == null) {
         throw new OseeArgumentException("Caching a null value is not allowed");
      }
      if (type.getId() != AbstractOseeType.UNPERSISTTED_VALUE) {
         idToTypeMap.put(type.getId(), type);
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

   public Collection<T> getDirtyTypes() throws OseeCoreException {
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
      storeItems(getDirtyTypes());
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

   public void reloadCache() throws OseeCoreException {
      getDataAccessor().load(this, getDataFactory());
   }

   @SuppressWarnings("unchecked")
   public void storeItem(AbstractOseeType item) throws OseeCoreException {
      storeItems(Collections.singletonList((T) item));
   }

   private void storeItems(Collection<T> toStore) throws OseeCoreException {
      getDataAccessor().store(this, toStore);
      synchronized (idToTypeMap) {
         for (T type : toStore) {
            cacheTypeById(type);
         }
      }
   }
}
