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
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IOseeStorableType;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.util.Conditions;
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

   @Override
   public void load(IOseeCache<T> cache) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("request", "update");

      CacheUpdateRequest updateRequest = new CacheUpdateRequest(cache.getCacheId());
      CacheUpdateResponse<T> response =
            HttpMessage.send(OseeServerContext.CACHE_CONTEXT, parameters, updateRequest, CacheUpdateResponse.class);

      Conditions.checkExpressionFailOnTrue(cache.getCacheId() != response.getCacheId(),
            "Reponse does not match cache enum id - cache to update [%s] - reponse cache id [%s]", cache.getCacheId(),
            response.getCacheId());
      updateCache(cache, response.getItems());
      for (T item : response.getItems()) {
         T type = cache.getById(item.getId());
         if (type != null) {
            type.clearDirty();
         }
      }
   }

   @Override
   public void store(Collection<T> types) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("request", "storage");

      //      CacheUpdateRequest updateRequest = new CacheUpdateRequest();
      //      CacheUpdateResponse<T> updateResponse =
      //            HttpMessage.send(OseeServerContext.CACHE_CONTEXT, parameters, updateRequest, CacheUpdateResponse.class);

      //      for (T updated : updateResponse.getItems()) {
      //         for (T type : types) {
      //            if (type.getGuid().equals(updated.getGuid())) {
      //               type.clearDirty();
      //            }
      //         }
      //      }
   }

   protected abstract void updateCache(IOseeCache<T> cache, Collection<T> items) throws OseeCoreException;
}
