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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.OseeEnumTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ClientOseeEnumTypeAccessor extends AbstractClientDataAccessor<OseeEnumType> {

   public ClientOseeEnumTypeAccessor(IOseeModelFactoryServiceProvider factoryProvider) {
      super(factoryProvider);
   }

   protected OseeEnumTypeFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getOseeEnumTypeFactory();
   }

   @Override
   protected Collection<OseeEnumType> updateCache(IOseeCache<OseeEnumType> cache) throws OseeCoreException {
      List<OseeEnumType> enumTypes = new ArrayList<OseeEnumType>();
      OseeEnumTypeCacheUpdateResponse response =
         requestUpdateMessage(cache, CoreTranslatorId.OSEE_ENUM_TYPE_CACHE_UPDATE_RESPONSE);

      OseeEnumTypeFactory factory = getFactory();

      for (String[] enumTypeRow : response.getEnumTypeRows()) {
         enumTypes.add(factory.createOrUpdate(cache, Integer.parseInt(enumTypeRow[0]),
            StorageState.valueOf(enumTypeRow[1]), enumTypeRow[2], enumTypeRow[3]));
      }

      for (String[] enumEntryRow : response.getEnumEntryRows()) {
         factory.createOrUpdate(cache, enumEntryRow[0], enumEntryRow[1], enumEntryRow[2],
            Integer.parseInt(enumEntryRow[3]));
      }
      return enumTypes;
   }
}
