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
package org.eclipse.osee.orcs.core.internal.artifact;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.data.Named;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeWriteable;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeManager extends Named, Identity<String> {

   void add(IAttributeType type, Attribute<? extends Object> attribute);

   void remove(IAttributeType type, Attribute<? extends Object> attribute);

   boolean isLoaded();

   void setLoaded(boolean value);

   /////////////////////////////////////////////////////////////////

   boolean areAttributesDirty();

   List<AttributeWriteable<Object>> getAttributesDirty();

   void setAttributesToNotDirty();

   int getAttributeCount(IAttributeType type) throws OseeCoreException;

   boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException;

   Collection<? extends IAttributeType> getExistingAttributeTypes() throws OseeCoreException;

   List<AttributeReadable<Object>> getAttributes() throws OseeCoreException;

   <T> List<AttributeReadable<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException;

   <T> List<AttributeWriteable<T>> getWriteableAttributes() throws OseeCoreException;

   <T> List<AttributeWriteable<T>> getWriteableAttributes(IAttributeType attributeType) throws OseeCoreException;

   String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException;

   String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException;

   <T> T getSoleAttributeValue(IAttributeType attributeType) throws OseeCoreException;

   <T> List<T> getAttributeValues(IAttributeType attributeType) throws OseeCoreException;

   <T> void setSoleAttributeValue(IAttributeType attributeType, T value) throws OseeCoreException;

   void setSoleAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException;

   void setSoleAttributeFromStream(IAttributeType attributeType, InputStream inputStream) throws OseeCoreException;

   <T> void setAttributesFromValues(IAttributeType attributeType, Collection<T> values) throws OseeCoreException;

   void setAttributesFromStrings(IAttributeType attributeType, Collection<String> values) throws OseeCoreException;

   void deleteSoleAttribute(IAttributeType attributeType) throws OseeCoreException;

   void deleteAttributesByArtifact() throws OseeCoreException;

   void deleteAttributes(IAttributeType attributeType) throws OseeCoreException;

   void deleteAttributesWithValue(IAttributeType attributeType, Object value) throws OseeCoreException;

   void createAttribute(IAttributeType attributeType) throws OseeCoreException;

   <T> void createAttribute(IAttributeType attributeType, T value) throws OseeCoreException;

   void createAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException;

}
