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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeTypes extends IdCollection<IAttributeType> {

   Collection<? extends IAttributeType> getAllTaggable() throws OseeCoreException;

   String getDescription(IAttributeType attrType) throws OseeCoreException;

   String getBaseAttributeTypeId(AttributeTypeId attrType) throws OseeCoreException;

   String getAttributeProviderId(IAttributeType attrType) throws OseeCoreException;

   String getDefaultValue(IAttributeType attrType) throws OseeCoreException;

   int getMaxOccurrences(IAttributeType attrType) throws OseeCoreException;

   int getMinOccurrences(IAttributeType attrType) throws OseeCoreException;

   EnumType getEnumType(IAttributeType attrType) throws OseeCoreException;

   String getFileTypeExtension(IAttributeType attrType) throws OseeCoreException;

   String getTaggerId(IAttributeType attrType) throws OseeCoreException;

   boolean isTaggable(IAttributeType attrType) throws OseeCoreException;

   boolean isEnumerated(IAttributeType attrType) throws OseeCoreException;

   String getMediaType(IAttributeType attrType) throws OseeCoreException;

   boolean hasMediaType(IAttributeType attrType) throws OseeCoreException;

   boolean isBooleanType(IAttributeType attrType) throws OseeCoreException;

   boolean isIntegerType(IAttributeType attrType);

   boolean isDateType(AttributeTypeId attrType);

   boolean isFloatingType(IAttributeType attrType);

   IAttributeType getByName(String attrTypeName);

}