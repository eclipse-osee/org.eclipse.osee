/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.data;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.Writeable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.annotations.WriteAttributes;

/**
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface AttributesWriteable extends Writeable, AttributesReadable, Modifiable {

   @WriteAttributes
   <T> List<AttributeWriteable<T>> getWriteableAttributes() throws OseeCoreException;

   @WriteAttributes
   <T> List<AttributeWriteable<T>> getWriteableAttributes(IAttributeType attributeType) throws OseeCoreException;

   <T> AttributeWriteable<T> createAttribute(IAttributeType attributeType) throws OseeCoreException;

   <T> AttributeWriteable<T> createAttribute(IAttributeType attributeType, T value) throws OseeCoreException;

   <T> AttributeWriteable<T> createAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException;

   <T> void setSoleAttributeValue(IAttributeType attributeType, T value) throws OseeCoreException;

   void setSoleAttributeFromStream(IAttributeType attributeType, InputStream inputStream) throws OseeCoreException;

   void setSoleAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException;

   <T> void setAttributesFromValues(IAttributeType attributeType, T... values) throws OseeCoreException;

   <T> void setAttributesFromValues(IAttributeType attributeType, Collection<T> values) throws OseeCoreException;

   void setAttributesFromStrings(IAttributeType attributeType, String... values) throws OseeCoreException;

   void setAttributesFromStrings(IAttributeType attributeType, Collection<String> values) throws OseeCoreException;

   void deleteSoleAttribute(IAttributeType attributeType) throws OseeCoreException;

   void deleteAttributes(IAttributeType attributeType) throws OseeCoreException;

   void deleteAttributesWithValue(IAttributeType attributeType, Object value) throws OseeCoreException;

}
