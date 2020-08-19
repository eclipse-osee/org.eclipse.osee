/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.model.type;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.OseeField;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Type information for attributes.
 *
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class AttributeType extends AbstractOseeType implements AttributeTypeToken {

   private static final String ATTRIBUTE_BASE_TYPE_ID_FIELD_KEY = "osee.base.attribute.type.id.field";
   private static final String ATTRIBUTE_PROVIDER_ID_FIELD_KEY = "osee.attribute.provider.id.field";
   private static final String ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY = "osee.attribute.default.value.field";
   private static final String ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY = "osee.attribute.enum.type.field";
   private static final String ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY = "osee.attribute.max.occurrence.field";
   private static final String ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY = "osee.attribute.min.occurrence.field";
   private static final String ATTRIBUTE_DESCRIPTION_FIELD_KEY = "osee.attribute.description.field";
   private static final String ATTRIBUTE_FILE_EXTENSION_FIELD_KEY = "osee.attribute.file.type.extension.field";
   private static final String ATTRIBUTE_TAGGER_ID_FIELD_KEY = "osee.attribute.tagger.id.field";
   private static final String ATTRIBUTE_MEDIA_TYPE_FIELD_KEY = "osee.attribute.media.type.field";

   public AttributeType(Long guid, String typeName, String baseAttributeTypeId, String attributeProviderNameId, String fileTypeExtension, String defaultValue, int minOccurrences, int maxOccurrences, String description, String taggerId, String mediaType) {
      super(guid, typeName);
      initializeFields();
      setFields(typeName, baseAttributeTypeId, attributeProviderNameId, fileTypeExtension, defaultValue, null,
         minOccurrences, maxOccurrences, description, taggerId, mediaType);
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
      addField(ATTRIBUTE_MEDIA_TYPE_FIELD_KEY, new OseeField<String>());
   }

   public void setFields(String name, String baseAttributeTypeId, String attributeProviderNameId, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId, String mediaType) {
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
      setFieldLogException(ATTRIBUTE_MEDIA_TYPE_FIELD_KEY, mediaType);
   }

   @Override
   public String getDefaultValue() {
      return getFieldValueLogException(null, ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY);
   }

   public int getMaxOccurrences() {
      return getFieldValueLogException(0, ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY);
   }

   public int getMinOccurrences() {
      return getFieldValueLogException(0, ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY);
   }

   @Override
   public String getDescription() {
      return getFieldValueLogException("", ATTRIBUTE_DESCRIPTION_FIELD_KEY);
   }

   public long getOseeEnumTypeId() {
      OseeEnumType oseeEnumType = getOseeEnumType();
      return oseeEnumType == null ? -1 : oseeEnumType.getId();
   }

   public OseeEnumType getOseeEnumType() {
      return getFieldValueLogException(null, ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY);
   }

   public void setOseeEnumType(OseeEnumType enumType) {
      setField(ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY, enumType);
   }

   public String getFileTypeExtension() {
      return getFieldValueLogException("", ATTRIBUTE_FILE_EXTENSION_FIELD_KEY);
   }

   /**
    * Get the registered tagger id for this attribute type
    *
    * @return tagger id
    */
   public String getTaggerId() {
      return getFieldValueLogException("", ATTRIBUTE_TAGGER_ID_FIELD_KEY);
   }

   @Override
   public String getMediaType() {
      return getFieldValueLogException("", ATTRIBUTE_MEDIA_TYPE_FIELD_KEY);
   }

   @Override
   public String getFileExtension() {
      return getFieldValueLogException("", ATTRIBUTE_FILE_EXTENSION_FIELD_KEY);
   }

   /**
    * Whether this attribute type is taggable.
    *
    * @return <b>true</b> if this attribute type is taggable. <b>false</b> if this is not taggable.
    */
   @Override
   public boolean isTaggable() {
      boolean toReturn = false;
      String taggerId = getTaggerId();
      if (taggerId != null) {
         toReturn = Strings.isValid(taggerId.trim());
      }
      return toReturn;
   }

   @Override
   public boolean isEnumerated() {
      return getOseeEnumTypeId() != -1;
   }

}