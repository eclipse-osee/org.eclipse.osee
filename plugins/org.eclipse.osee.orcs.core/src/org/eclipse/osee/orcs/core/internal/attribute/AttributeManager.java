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
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.orcs.core.ds.Attribute;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeManager extends AttributeContainer {

   void setAttributesNotDirty();

   void deleteAttributesByArtifact() ;

   void unDeleteAttributesByArtifact() ;

   <T> T getSoleAttributeValue(AttributeTypeId attributeType) ;

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, DeletionFlag flag, T defaultValue);

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultValue) ;

   <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType);

   <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType, DeletionFlag flag);

   String getSoleAttributeAsString(AttributeTypeId attributeType) ;

   String getSoleAttributeAsString(AttributeTypeId attributeType, String defaultValue) ;

   <T> List<T> getAttributeValues(AttributeTypeId attributeType) ;

   <T> void setSoleAttributeValue(AttributeTypeId attributeType, T value) ;

   void setSoleAttributeFromStream(AttributeTypeId attributeType, InputStream inputStream) ;

   void setSoleAttributeFromString(AttributeTypeId attributeType, String value) ;

   <T> void setAttributesFromValues(AttributeTypeId attributeType, T... values) ;

   <T> void setAttributesFromValues(AttributeTypeId attributeType, Collection<T> values) ;

   void setAttributesFromStrings(AttributeTypeId attributeType, String... values) ;

   void setAttributesFromStrings(AttributeTypeId attributeType, Collection<String> values) ;

   void deleteSoleAttribute(AttributeTypeId attributeType) ;

   void deleteAttributes(AttributeTypeId attributeType) ;

   void deleteAttributesWithValue(AttributeTypeId attributeType, Object value) ;

   <T> Attribute<T> createAttribute(AttributeTypeId attributeType) ;

   <T> Attribute<T> createAttribute(AttributeTypeId attributeType, T value) ;

   <T> Attribute<T> createAttributeFromString(AttributeTypeId attributeType, String value) ;

   List<Attribute<Object>> getAttributes() ;

   <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType) ;

   List<Attribute<Object>> getAttributes(DeletionFlag deletionFlag) ;

   <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType, DeletionFlag deletionFlag) ;

   <T> Attribute<T> getAttributeById(AttributeId attributeId) ;

   <T> Attribute<T> getAttributeById(AttributeId attributeId, DeletionFlag includeDeleted) ;
}
