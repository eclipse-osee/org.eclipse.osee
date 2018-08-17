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
package org.eclipse.osee.orcs.core.internal.attribute;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeManager extends AttributeContainer {

   void setAttributesNotDirty();

   void deleteAttributesByArtifact();

   void unDeleteAttributesByArtifact();

   <T> T getSoleAttributeValue(AttributeTypeId attributeType);

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, DeletionFlag flag, T defaultValue);

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultValue);

   <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType);

   <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType, DeletionFlag flag);

   String getSoleAttributeAsString(AttributeTypeId attributeType);

   String getSoleAttributeAsString(AttributeTypeId attributeType, String defaultValue);

   <T> List<T> getAttributeValues(AttributeTypeId attributeType);

   Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable();

   <T> void setSoleAttributeValue(AttributeTypeToken attributeType, T value);

   void setSoleAttributeFromStream(AttributeTypeToken attributeType, InputStream inputStream);

   void setSoleAttributeFromString(AttributeTypeToken attributeType, String value);

   <T> void setAttributesFromValues(AttributeTypeToken attributeType, Collection<T> values);

   void setAttributesFromStrings(AttributeTypeToken attributeType, String... values);

   void setAttributesFromStrings(AttributeTypeToken attributeType, Collection<String> values);

   void deleteSoleAttribute(AttributeTypeId attributeType);

   void deleteAttributes(AttributeTypeId attributeType);

   void deleteAttributesWithValue(AttributeTypeId attributeType, Object value);

   <T> Attribute<T> createAttribute(AttributeTypeToken attributeType);

   <T> Attribute<T> createAttribute(AttributeTypeToken attributeType, T value);

   List<Attribute<Object>> getAttributes();

   <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType);

   List<Attribute<Object>> getAttributes(DeletionFlag deletionFlag);

   <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType, DeletionFlag deletionFlag);

   <T> Attribute<T> getAttributeById(AttributeId attributeId);

   <T> Attribute<T> getAttributeById(AttributeId attributeId, DeletionFlag includeDeleted);
}
