/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.data;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeTypes extends IdCollection<AttributeTypeToken> {

   Collection<AttributeTypeId> getAllTaggable();

   String getDescription(AttributeTypeId attrType);

   String getBaseAttributeTypeId(AttributeTypeId attrType);

   String getAttributeProviderId(AttributeTypeId attrType);

   String getDefaultValue(AttributeTypeId attrType);

   int getMaxOccurrences(AttributeTypeId attrType);

   int getMinOccurrences(AttributeTypeId attrType);

   EnumType getEnumType(AttributeTypeId attrType);

   String getFileTypeExtension(AttributeTypeId attrType);

   String getTaggerId(AttributeTypeId attrType);

   boolean isTaggable(AttributeTypeId attrType);

   boolean isEnumerated(AttributeTypeId attrType);

   String getMediaType(AttributeTypeId attrType);

   boolean hasMediaType(AttributeTypeId attrType);

   boolean isBooleanType(AttributeTypeId attrType);

   boolean isIntegerType(AttributeTypeId attrType);

   boolean isDateType(AttributeTypeId attrType);

   boolean isFloatingType(AttributeTypeId attrType);

   AttributeTypeId getByName(String attrTypeName);

   boolean isStringType(AttributeTypeId attrType);

   boolean isLongType(AttributeTypeId attrType);

}