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
package org.eclipse.osee.framework.core.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class CacheUpdateRequestTranslator implements ITranslator<CacheUpdateRequest> {

   private enum Entry {
      CACHE_ID, ITEM_IDS;
   }

   @Override
   public CacheUpdateRequest convert(PropertyStore propertyStore) throws OseeCoreException {
      OseeCacheEnum cacheId = OseeCacheEnum.valueOf(propertyStore.get(Entry.CACHE_ID.name()));
      String[] itemId = propertyStore.getArray(Entry.ITEM_IDS.name());
      List<Integer> itemIds = new ArrayList<Integer>();
      if (itemId != null && itemId.length > 0) {
         for (String item : itemId) {
            if (item != null) {
               itemIds.add(Integer.valueOf(item));
            }
         }
      }
      return new CacheUpdateRequest(cacheId, itemIds);
   }

   @Override
   public PropertyStore convert(CacheUpdateRequest object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();

      store.put(Entry.CACHE_ID.name(), object.getCacheId().name());
      Collection<Integer> itemIds = object.getItemsIds();
      if (!itemIds.isEmpty()) {
         String[] itemStr = new String[itemIds.size()];
         int index = 0;
         for (Integer item : itemIds) {
            itemStr[index++] = (String.valueOf(item));
         }
         store.put(Entry.ITEM_IDS.name(), itemStr);
      }
      return store;
   }
}
