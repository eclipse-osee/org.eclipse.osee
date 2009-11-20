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
package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IOseeStorableType;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.skynet.core.artifact.HttpMessage;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractServerDataAccessor<T extends IOseeStorableType> implements IOseeDataAccessor<T> {

   private final IOseeModelFactoryServiceProvider factoryProvider;

   protected AbstractServerDataAccessor(IOseeModelFactoryServiceProvider factoryProvider) {
      this.factoryProvider = factoryProvider;
   }

   protected IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
      return factoryProvider.getOseeFactoryService();
   }

   @SuppressWarnings("unchecked")
   @Override
   public void load(AbstractOseeCache<T> cache) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("request", "update");
      CacheUpdateRequest<T> updateRequest = new CacheUpdateRequest<T>();
      CacheUpdateResponse<T> updateResponse =
            HttpMessage.send(OseeServerContext.CACHE_CONTEXT, parameters, updateRequest, CacheUpdateResponse.class);
      updateCache(cache, updateResponse);

      for (T updated : updateResponse.getItems()) {
         T type = cache.getByGuid(updated.getGuid());
         if (type != null) {
            type.clearDirty();
         }
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public void store(Collection<T> types) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("request", "storage");

      CacheUpdateRequest<T> updateRequest = new CacheUpdateRequest<T>();
      CacheUpdateResponse<T> updateResponse =
            HttpMessage.send(OseeServerContext.CACHE_CONTEXT, parameters, updateRequest, CacheUpdateResponse.class);

      for (T updated : updateResponse.getItems()) {
         for (T type : types) {
            if (type.getGuid().equals(updated.getGuid())) {
               type.clearDirty();
            }
         }
      }
   }

   protected abstract void updateCache(AbstractOseeCache<T> cache, CacheUpdateResponse<T> updateResponse) throws OseeCoreException;
}
