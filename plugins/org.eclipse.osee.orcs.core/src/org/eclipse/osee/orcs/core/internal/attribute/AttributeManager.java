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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Attribute;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeManager extends AttributeContainer {

   void setAttributesNotDirty();

   void deleteAttributesByArtifact() throws OseeCoreException;

   void unDeleteAttributesByArtifact() throws OseeCoreException;

   <T> T getSoleAttributeValue(AttributeTypeId attributeType) throws OseeCoreException;

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, DeletionFlag flag, T defaultValue);

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultValue) throws OseeCoreException;

   <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType);

   <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType, DeletionFlag flag);

   String getSoleAttributeAsString(AttributeTypeId attributeType) throws OseeCoreException;

   String getSoleAttributeAsString(AttributeTypeId attributeType, String defaultValue) throws OseeCoreException;

   <T> List<T> getAttributeValues(AttributeTypeId attributeType) throws OseeCoreException;

   <T> void setSoleAttributeValue(AttributeTypeId attributeType, T value) throws OseeCoreException;

   void setSoleAttributeFromStream(AttributeTypeId attributeType, InputStream inputStream) throws OseeCoreException;

   void setSoleAttributeFromString(AttributeTypeId attributeType, String value) throws OseeCoreException;

   <T> void setAttributesFromValues(AttributeTypeId attributeType, T... values) throws OseeCoreException;

   <T> void setAttributesFromValues(AttributeTypeId attributeType, Collection<T> values) throws OseeCoreException;

   void setAttributesFromStrings(AttributeTypeId attributeType, String... values) throws OseeCoreException;

   void setAttributesFromStrings(AttributeTypeId attributeType, Collection<String> values) throws OseeCoreException;

   void deleteSoleAttribute(AttributeTypeId attributeType) throws OseeCoreException;

   void deleteAttributes(AttributeTypeId attributeType) throws OseeCoreException;

   void deleteAttributesWithValue(AttributeTypeId attributeType, Object value) throws OseeCoreException;

   <T> Attribute<T> createAttribute(AttributeTypeId attributeType) throws OseeCoreException;

   <T> Attribute<T> createAttribute(AttributeTypeId attributeType, T value) throws OseeCoreException;

   <T> Attribute<T> createAttributeFromString(AttributeTypeId attributeType, String value) throws OseeCoreException;

   List<Attribute<Object>> getAttributes() throws OseeCoreException;

   <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType) throws OseeCoreException;

   List<Attribute<Object>> getAttributes(DeletionFlag deletionFlag) throws OseeCoreException;

   <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType, DeletionFlag deletionFlag) throws OseeCoreException;

   <T> Attribute<T> getAttributeById(Integer attributeId) throws OseeCoreException;

   <T> Attribute<T> getAttributeById(Integer attributeId, DeletionFlag includeDeleted) throws OseeCoreException;
}
