/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.translation;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class CacheUpdateResponseTranslator<T> implements ITranslator<CacheUpdateResponse<T>> {

   private enum Entry {
      CACHE_ID,
      COUNT,
      ITEM;
   }

   private final IDataTranslationService service;
   private final Class<T> clazzType;

   public CacheUpdateResponseTranslator(IDataTranslationService service, Class<T> clazzType) {
      this.service = service;
      this.clazzType = clazzType;
   }

   private Class<T> getClassType() {
      return clazzType;
   }

   @Override
   public CacheUpdateResponse<T> convert(PropertyStore store) throws OseeCoreException {
      OseeCacheEnum cacheId = OseeCacheEnum.valueOf(store.get(Entry.CACHE_ID.name()));
      Class<T> clazz = getClassType();
      Collection<T> items = new ArrayList<T>();
      int numberOfItems = store.getInt(Entry.COUNT.name());
      for (int index = 0; index < numberOfItems; index++) {
         PropertyStore innerStore = store.getPropertyStore(createKey(index));
         T object = service.convert(innerStore, clazz);
         items.add(object);
      }
      return new CacheUpdateResponse<T>(cacheId, items);
   }

   @Override
   public PropertyStore convert(CacheUpdateResponse<T> object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.CACHE_ID.name(), object.getCacheId().name());

      Collection<T> items = object.getItems();
      store.put(Entry.COUNT.name(), items.size());
      int index = 0;
      for (T item : items) {
         store.put(createKey(index++), service.convert(item));
      }
      return store;
   }

   private String createKey(int index) {
      return String.format("%s_%s", Entry.ITEM.name(), index);
   }
}
