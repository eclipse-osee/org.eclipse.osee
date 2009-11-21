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

import java.util.Arrays;
import java.util.Collection;
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
      CACHE_ID,
      GUIDS;
   }

   @Override
   public CacheUpdateRequest convert(PropertyStore propertyStore) throws OseeCoreException {
      OseeCacheEnum cacheId = OseeCacheEnum.valueOf(propertyStore.get(Entry.CACHE_ID.name()));
      String[] guids = propertyStore.getArray(Entry.GUIDS.name());
      return new CacheUpdateRequest(cacheId, Arrays.asList(guids));
   }

   @Override
   public PropertyStore convert(CacheUpdateRequest object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();

      store.put(Entry.CACHE_ID.name(), object.getCacheId().name());
      Collection<String> guids = object.getGuids();
      if (!guids.isEmpty()) {
         store.put(Entry.GUIDS.name(), guids.toArray(new String[guids.size()]));
      }
      return store;
   }
}
