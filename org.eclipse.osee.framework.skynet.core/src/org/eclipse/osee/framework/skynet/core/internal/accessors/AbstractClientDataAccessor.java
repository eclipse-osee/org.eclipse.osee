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
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.services.ITranslatorId;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractClientDataAccessor<T extends IOseeStorable> implements IOseeDataAccessor<T> {

   private final IOseeModelFactoryServiceProvider factoryProvider;

   protected AbstractClientDataAccessor(IOseeModelFactoryServiceProvider factoryProvider) {
      this.factoryProvider = factoryProvider;
   }

   protected IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
      return factoryProvider.getOseeFactoryService();
   }

   @Override
   public void load(IOseeCache<T> cache) throws OseeCoreException {
      Collection<T> updatedItems = updateCache(cache);
      for (T item : updatedItems) {
         item.clearDirty();
      }
   }

   @Override
   public void store(Collection<T> types) throws OseeCoreException {
      //do nothing
   }

   protected <J> J requestUpdateMessage(IOseeCache<T> cache, ITranslatorId txId) throws OseeCoreException {
      CacheUpdateRequest updateRequest = new CacheUpdateRequest(cache.getCacheId());
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", CacheOperation.UPDATE.name());

      return HttpClientMessage.send(OseeServerContext.CACHE_CONTEXT, parameters,
            CoreTranslatorId.OSEE_CACHE_UPDATE_REQUEST, updateRequest, txId);
   }

   protected abstract Collection<T> updateCache(IOseeCache<T> cache) throws OseeCoreException;
}
