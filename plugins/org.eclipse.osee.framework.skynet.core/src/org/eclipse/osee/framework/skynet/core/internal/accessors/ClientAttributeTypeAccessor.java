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
import java.util.Map;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.AttributeTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;

/**
 * @author Roberto E. Escobar
 */
public class ClientAttributeTypeAccessor extends AbstractClientDataAccessor<AttributeType> {

   private final AbstractOseeCache<OseeEnumType> enumCache;
   private final AttributeTypeFactory attributeTypeFactory;

   public ClientAttributeTypeAccessor(AttributeTypeFactory attributeTypeFactory, AbstractOseeCache<OseeEnumType> enumCache) {
      super();
      this.attributeTypeFactory = attributeTypeFactory;
      this.enumCache = enumCache;
   }

   private AttributeTypeFactory getFactory() {
      return attributeTypeFactory;
   }

   @Override
   public void load(IOseeCache<AttributeType> cache) throws OseeCoreException {
      enumCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected Collection<AttributeType> updateCache(IOseeCache<AttributeType> cache) throws OseeCoreException {
      List<AttributeType> updatedItems = new ArrayList<AttributeType>();

      AttributeTypeCacheUpdateResponse response =
         requestUpdateMessage(cache, CoreTranslatorId.ATTRIBUTE_TYPE_CACHE_UPDATE_RESPONSE);

      Map<Integer, Integer> attrToEnums = response.getAttrToEnums();
      AttributeTypeFactory factory = getFactory();
      for (AttributeType row : response.getAttrTypeRows()) {
         Integer uniqueId = row.getId();
         OseeEnumType oseeEnumType = null;
         Integer enumId = attrToEnums.get(uniqueId);
         if (enumId != null) {
            oseeEnumType = enumCache.getById(enumId);
         }
         factory.createOrUpdate(cache, row.getId(), row.getStorageState(), row.getGuid(), row.getName(),
            row.getBaseAttributeTypeId(), row.getAttributeProviderId(), row.getFileTypeExtension(),
            row.getDefaultValue(), oseeEnumType, row.getMinOccurrences(), row.getMaxOccurrences(),
            row.getDescription(), row.getTaggerId());
      }
      return updatedItems;
   }
}
