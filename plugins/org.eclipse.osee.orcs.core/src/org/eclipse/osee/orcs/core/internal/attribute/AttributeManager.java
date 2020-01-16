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

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType);

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue);

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType, T defaultValue);

   <T> Attribute<T> getSoleAttribute(AttributeTypeToken attributeType);

   <T> Attribute<T> getSoleAttribute(AttributeTypeToken attributeType, DeletionFlag flag);

   String getSoleAttributeAsString(AttributeTypeToken attributeType);

   String getSoleAttributeAsString(AttributeTypeToken attributeType, String defaultValue);

   <T> List<T> getAttributeValues(AttributeTypeToken attributeType);

   Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable();

   <T> void setSoleAttributeValue(AttributeTypeToken attributeType, T value);

   void setSoleAttributeFromStream(AttributeTypeToken attributeType, InputStream inputStream);

   void setSoleAttributeFromString(AttributeTypeToken attributeType, String value);

   <T> void setAttributesFromValues(AttributeTypeToken attributeType, Collection<T> values);

   void setAttributesFromStrings(AttributeTypeToken attributeType, String... values);

   <T> void setAttributesFromStrings(AttributeTypeToken attributeType, Collection<String> values);

   void deleteSoleAttribute(AttributeTypeToken attributeType);

   void deleteAttributes(AttributeTypeToken attributeType);

   void deleteAttributesWithValue(AttributeTypeToken attributeType, Object value);

   <T> Attribute<T> createAttribute(AttributeTypeToken attributeType);

   <T> Attribute<T> createAttribute(AttributeTypeToken attributeType, T value);

   <T> List<Attribute<T>> getAttributes();

   <T> List<Attribute<T>> getAttributes(AttributeTypeToken attributeType);

   <T> List<Attribute<T>> getAttributes(DeletionFlag deletionFlag);

   <T> List<Attribute<T>> getAttributes(AttributeTypeToken attributeType, DeletionFlag deletionFlag);

   <T> Attribute<T> getAttributeById(AttributeId attributeId);

   <T> Attribute<T> getAttributeById(AttributeId attributeId, DeletionFlag includeDeleted);
}