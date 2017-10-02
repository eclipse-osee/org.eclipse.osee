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
package org.eclipse.osee.framework.core.model.type;

import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeFactory implements IOseeTypeFactory {

   public AttributeType create(long guid, String name, String baseAttributeTypeId, String attributeProviderNameId, String fileTypeExtension, String defaultValue, int minOccurrences, int maxOccurrences, String tipText, String taggerId, String mediaType)  {
      Conditions.checkNotNullOrEmpty(name, "attribute type name");
      //      checkNameUnique(cache, name);
      Conditions.checkNotNullOrEmpty(baseAttributeTypeId, "attribute base type id");
      Conditions.checkNotNullOrEmpty(attributeProviderNameId, "attribute provider id");
      Conditions.checkExpressionFailOnTrue(minOccurrences > 0 && !Strings.isValid(defaultValue),
         "DefaultValue must be set for attribute [%s] with minOccurrences ", name, minOccurrences);

      Conditions.checkExpressionFailOnTrue(minOccurrences < 0, "minOccurrences must be greater than or equal to zero");
      Conditions.checkExpressionFailOnTrue(maxOccurrences < minOccurrences,
         "maxOccurences can not be less than minOccurences");

      return new AttributeType(guid, name, Strings.intern(baseAttributeTypeId), Strings.intern(attributeProviderNameId),
         Strings.intern(fileTypeExtension), defaultValue, minOccurrences, maxOccurrences, tipText,
         Strings.intern(taggerId), Strings.intern(mediaType));

   }

   public AttributeType createOrUpdate(AttributeTypeCache cache, long guid, String typeName, String baseAttributeTypeId, String attributeProviderNameId, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId, String mediaType)  {
      Conditions.checkNotNull(cache, "AttributeTypeCache");
      AttributeType attributeType = cache.getByGuid(guid);

      if (attributeType == null) {
         attributeType = create(guid, typeName, baseAttributeTypeId, attributeProviderNameId, fileTypeExtension,
            defaultValue, minOccurrences, maxOccurrences, description, taggerId, mediaType);
         attributeType.setOseeEnumType(oseeEnumType);
         cache.cache(attributeType);
      } else {
         attributeType.setFields(typeName, baseAttributeTypeId, attributeProviderNameId, fileTypeExtension,
            defaultValue, oseeEnumType, minOccurrences, maxOccurrences, description, taggerId, mediaType);
      }
      return attributeType;
   }
}
