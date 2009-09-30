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
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.types.field.OseeField;

/**
 * Type information for dynamic attributes.
 * 
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class AttributeType extends AbstractOseeType implements Comparable<AttributeType> {

   private static final String ATTRIBUTE_BASE_TYPE_ID_FIELD_KEY = "osee.base.attribute.type.id.field";
   private static final String ATTRIBUTE_PROVIDER_ID_FIELD_KEY = "osee.attribute.provider.id.field";
   private static final String ATTRIBUTE_BASE_TYPE_CLASS_FIELD_KEY = "osee.base.attribute.type.class.field";
   private static final String ATTRIBUTE_PROVIDER_CLASS_FIELD_KEY = "osee.attribute.provider.class.field";
   private static final String ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY = "osee.attribute.default.value.field";
   private static final String ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY = "osee.attribute.enum.type.field";
   private static final String ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY = "osee.attribute.max.occurrence.field";
   private static final String ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY = "osee.attribute.min.occurrence.field";
   private static final String ATTRIBUTE_DESCRIPTION_FIELD_KEY = "osee.attribute.description.field";
   private static final String ATTRIBUTE_FILE_EXTENSION_FIELD_KEY = "osee.attribute.file.type.extension.field";
   private static final String ATTRIBUTE_TAGGER_ID_FIELD_KEY = "osee.attribute.tagger.id.field";

   /**
    * Create a dynamic attribute descriptor. Descriptors can be acquired for application use from the
    * <code>ConfigurationPersistenceManager</code>.
    * 
    * @param baseAttributeClass
    * @param typeName
    * @param defaultValue
    * @param validityXml
    * @param minOccurrences
    * @param maxOccurrences
    * @param tipText
    */
   public AttributeType(AbstractOseeCache<AttributeType> cache, String guid, String typeName, String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId) {
      super(cache, guid, typeName);
      setFields(typeName, baseAttributeTypeId, attributeProviderNameId, baseAttributeClass, providerAttributeClass,
            fileTypeExtension, defaultValue, oseeEnumType, minOccurrences, maxOccurrences, description, taggerId);
   }

   @Override
   protected void initializeFields() {
      addField(ATTRIBUTE_BASE_TYPE_ID_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_PROVIDER_ID_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_BASE_TYPE_CLASS_FIELD_KEY, new OseeField<Class<? extends Attribute<?>>>());
      addField(ATTRIBUTE_PROVIDER_CLASS_FIELD_KEY, new OseeField<Class<? extends IAttributeDataProvider>>());
      addField(ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY, new OseeField<OseeEnumType>());
      addField(ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY, new OseeField<Integer>());
      addField(ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY, new OseeField<Integer>());
      addField(ATTRIBUTE_DESCRIPTION_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_FILE_EXTENSION_FIELD_KEY, new OseeField<String>());
      addField(ATTRIBUTE_TAGGER_ID_FIELD_KEY, new OseeField<String>());
   }

   public void setFields(String name, String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId) {
      String fileExtensionToSet = fileTypeExtension != null ? fileTypeExtension : "";
      setName(name);
      setFieldLogException(ATTRIBUTE_BASE_TYPE_ID_FIELD_KEY, baseAttributeTypeId);
      setFieldLogException(ATTRIBUTE_PROVIDER_ID_FIELD_KEY, attributeProviderNameId);
      setFieldLogException(ATTRIBUTE_BASE_TYPE_CLASS_FIELD_KEY, baseAttributeClass);
      setFieldLogException(ATTRIBUTE_PROVIDER_CLASS_FIELD_KEY, providerAttributeClass);
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

   /**
    * @return Returns the baseAttributeClass.
    */
   public Class<? extends Attribute<?>> getBaseAttributeClass() {
      return getFieldValueLogException(null, ATTRIBUTE_BASE_TYPE_CLASS_FIELD_KEY);
   }

   /**
    * @return Returns the defaultValue.
    */
   public String getDefaultValue() {
      return getFieldValueLogException(null, ATTRIBUTE_DEFAULT_VALUE_FIELD_KEY);
   }

   /**
    * @return Returns the maxOccurrences.
    */
   public int getMaxOccurrences() {
      return getFieldValueLogException(0, ATTRIBUTE_MAX_OCCURRENCE_FIELD_KEY);
   }

   /**
    * @return Returns the minOccurrences.
    */
   public int getMinOccurrences() {
      return getFieldValueLogException(0, ATTRIBUTE_MIN_OCCURRENCE_FIELD_KEY);
   }

   public String getDescription() {
      return getFieldValueLogException("", ATTRIBUTE_DESCRIPTION_FIELD_KEY);
   }

   public int getOseeEnumTypeId() {
      OseeEnumType oseeEnumType = getOseeEnumType();
      return oseeEnumType == null ? OseeEnumTypeManager.getDefaultEnumTypeId() : oseeEnumType.getId();
   }

   public OseeEnumType getOseeEnumType() {
      return getFieldValueLogException(null, ATTRIBUTE_ENUM_TYPE_ID_FIELD_KEY);
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
    * @return the providerAttributeClass
    */
   public Class<? extends IAttributeDataProvider> getProviderAttributeClass() {
      return getFieldValueLogException(null, ATTRIBUTE_PROVIDER_CLASS_FIELD_KEY);
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
      return EnumeratedAttribute.class.isAssignableFrom(getBaseAttributeClass());
   }
}