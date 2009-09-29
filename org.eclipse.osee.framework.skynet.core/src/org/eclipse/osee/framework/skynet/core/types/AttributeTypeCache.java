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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeExtensionManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;

/**
 * @author Roberto E. Escobar
 */
public final class AttributeTypeCache extends AbstractOseeCache<AttributeType> {

   public AttributeTypeCache(IOseeTypeFactory factory, IOseeDataAccessor<AttributeType> dataAccessor) {
      super(factory, dataAccessor);
   }

   public AttributeType createType(String guid, String typeName, String baseAttributeTypeId, String attributeProviderNameId, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId) throws OseeCoreException {
      AttributeType attributeType = getByGuid(guid);
      Class<? extends Attribute<?>> baseAttributeClass =
            AttributeExtensionManager.getAttributeClassFor(baseAttributeTypeId);
      Class<? extends IAttributeDataProvider> providerAttributeClass =
            AttributeExtensionManager.getAttributeProviderClassFor(attributeProviderNameId);

      if (attributeType == null) {
         attributeType =
               getDataFactory().createAttributeType(this, guid, typeName, baseAttributeTypeId, attributeProviderNameId,
                     baseAttributeClass, providerAttributeClass, fileTypeExtension, defaultValue, oseeEnumType,
                     minOccurrences, maxOccurrences, description, taggerId);
      } else {
         decache(attributeType);
         attributeType.setFields(typeName, baseAttributeTypeId, attributeProviderNameId, baseAttributeClass,
               providerAttributeClass, fileTypeExtension, defaultValue, oseeEnumType, minOccurrences, maxOccurrences,
               description, taggerId);
      }
      cache(attributeType);
      return attributeType;
   }
}
