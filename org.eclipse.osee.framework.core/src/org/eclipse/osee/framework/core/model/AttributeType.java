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
package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.OseeField;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Type information for dynamic attributes.
 * 
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public final class AttributeType extends AbstractOseeType implements Comparable<AttributeType>, IAttributeType {

   private static final String ATTRIBUTE_BASE_TYPE_ID_FIELD_KEY = "osee.base.attribute.type.id.field";
   private static final String ATTRIBUTE_PROVIDER_ID_FIELD_KEY = "osee.attribute.provider.id.field";
   private static final String ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY = "osee.attribute.default.value.field";
   private static final String ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY = "osee.attribute.enum.type.field";
   private static final String ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY = "osee.attribute.max.occurrence.field";
   private static final String ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY = "osee.attribute.min.occurrence.field";
   private static final String ATTRIBUTE_DESCRIPTION_FIELD_KEY = "osee.attribute.description.field";
   private static final String ATTRIBUTE_FILE_EXTENSION_FIELD_KEY = "osee.attribute.file.type.extension.field";
   private static final String ATTRIBUTE_TAGGER_ID_FIELD_KEY = "osee.attribute.tagger.id.field";

   public AttributeType(String guid, String typeName, String baseAttributeTypeId, String attributeProviderNameId, String fileTypeExtension, String defaultValue, int minOccurrences, int maxOccurrences, String description, String taggerId) {
      super(guid, typeName);
      initializeFields();
      setFields(typeName, baseAttributeTypeId, attributeProviderNameId, fileTypeExtension, defaultValue, null,
            minOccurrences, maxOccurrences, description, taggerId);
   }

   protected void initializeFields() {
      addField(ATTRIBUTE_BASE_TYPE_ID_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_PROVIDER_ID_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY, new OseeField<OseeEnumType>());
      addField(ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY, new OseeField<Integer>());
      addField(ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY, new OseeField<Integer>());
      addField(ATTRIBUTE_DESCRIPTION_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_FILE_EXTENSION_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_TAGGER_ID_FIELD_KEY, new OseeField<String>());
   }

   public void setFields(String name, String baseAttributeTypeId, String attributeProviderNameId, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId) {
      String fileExtensionToSet = fileTypeExtension != null ? fileTypeExtension : "";
      setName(name);
      setFieldLogException(ATTRIBUTE_BASE_TYPE_ID_FIELD_KEY, baseAttributeTypeId);
      setFieldLogException(ATTRIBUTE_PROVIDER_ID_FIELD_KEY, attributeProviderNameId);
      setFieldLogException(ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY, defaultValue);
      setFieldLogException(ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY, oseeEnumType);
      setFieldLogException(ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY, maxOccurrences);
      setFieldLogException(ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY, minOccurrences);
      setFieldLogException(ATTRIBUTE_DESCRIPTION_FIELD_KEY, description);
      setFieldLogException(ATTRIBUTE_FILE_EXTENSION_FIELD_KEY, fileExtensionToSet);
      setFieldLogException(ATTRIBUTE_TAGGER_ID_FIELD_KEY, taggerId);
   }

   public String getBaseAttributeTypeId() {
      return getFieldValueLogException("", ATTRIBUTE_BASE_TYPE_ID_FIELD_KEY);
   }

   public String getAttributeProviderId() {
      return getFieldValueLogException("", ATTRIBUTE_PROVIDER_ID_FIELD_KEY);
   }

   public String getDefaultValue() {
      return getFieldValueLogException(null, ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY);
   }

   public int getMaxOccurrences() {
      return getFieldValueLogException(0, ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY);
   }

   public int getMinOccurrences() {
      return getFieldValueLogException(0, ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY);
   }

   public String getDescription() {
      return getFieldValueLogException("", ATTRIBUTE_DESCRIPTION_FIELD_KEY);
   }

   public int getOseeEnumTypeId() {
      OseeEnumType oseeEnumType = getOseeEnumType();
      return oseeEnumType == null ? -1 : oseeEnumType.getId();
   }

   public OseeEnumType getOseeEnumType() {
      return getFieldValueLogException(null, ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY);
   }

   public void setOseeEnumType(OseeEnumType enumType) throws OseeCoreException {
      setField(ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY, enumType);
   }

   @Override
   public String toString() {
      return getName();
   }

   public String getFileTypeExtension() {
      return getFieldValueLogException("", ATTRIBUTE_FILE_EXTENSION_FIELD_KEY);
   }

   public int compareTo(AttributeType other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   /**
    * Get the registered tagger id for this attribute type
    * 
    * @return tagger id
    */
   public String getTaggerId() {
      return getFieldValueLogException("", ATTRIBUTE_TAGGER_ID_FIELD_KEY);
   }

   /**
    * Whether this attribute type is taggable.
    * 
    * @return <b>true</b> if this attribute type is taggable. <b>false</b> if this is not taggable.
    */
   public boolean isTaggable() {
      boolean toReturn = false;
      String taggerId = getTaggerId();
      if (taggerId != null) {
         toReturn = Strings.isValid(taggerId.trim());
      }
      return toReturn;
   }

   public boolean isEnumerated() {
      return getOseeEnumTypeId() != -1;
   }
}