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
package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.Arrays;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerOseeEnumTypeAccessor extends AbstractServerDataAccessor<OseeEnumType> {

   public ServerOseeEnumTypeAccessor(IOseeModelFactoryServiceProvider factoryProvider) {
      super(factoryProvider);
   }

   protected OseeEnumTypeFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getOseeEnumTypeFactory();
   }

   @Override
   protected void updateCache(AbstractOseeCache<OseeEnumType> cache, CacheUpdateResponse<OseeEnumType> updateResponse) throws OseeCoreException {
      for (OseeEnumType updated : updateResponse.getItems()) {
         OseeEnumType type =
               getFactory().createOrUpdate(cache, updated.getId(), updated.getModificationType(), updated.getGuid(),
                     updated.getName());
         OseeEnumEntry[] entries = updated.values();
         type.setEntries(Arrays.asList(entries));
      }
   }
}
