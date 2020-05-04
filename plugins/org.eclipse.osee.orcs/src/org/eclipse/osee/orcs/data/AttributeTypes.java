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

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeTypes extends IdCollection<AttributeTypeToken> {

   String getAttributeProviderId(AttributeTypeId attrType);

   String getDefaultValue(AttributeTypeId attrType);

   int getMaxOccurrences(AttributeTypeId attrType);

   int getMinOccurrences(AttributeTypeId attrType);

   EnumType getEnumType(AttributeTypeId attrType);

   String getFileTypeExtension(AttributeTypeId attrType);

   /**
    * @return AttributeTypeToken or OseeTypeDoesNotExist
    */
   AttributeTypeToken getByName(String attrTypeName);

   boolean typeExists(String attrTypeName);

   boolean typeExists(AttributeTypeId attrTypeId);

   AttributeTypeToken getByNameOrSentinel(String attrTypeName);

}