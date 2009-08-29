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
import org.eclipse.osee.framework.skynet.core.artifact.BaseOseeType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;

/**
 * Type information for dynamic attributes.
 * 
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class AttributeType extends BaseOseeType implements Comparable<AttributeType> {
   public static final AttributeType[] EMPTY_ARRAY = new AttributeType[0];
   private final Class<? extends Attribute<?>> baseAttributeClass;
   private final Class<? extends IAttributeDataProvider> providerAttributeClass;
   private final String defaultValue;
   private final OseeEnumType oseeEnumType;
   private final int maxOccurrences;
   private final int minOccurrences;
   private final String tipText;
   private final String fileTypeExtension;
   private final String taggerId;
   private final String baseAttributeTypeId;
   private final String attributeProviderNameId;

   /**
    * Create a dynamic attribute descriptor. Descriptors can be acquired for application use from the
    * <code>ConfigurationPersistenceManager</code>.
    * 
    * @param baseAttributeClass
    * @param name
    * @param defaultValue
    * @param validityXml
    * @param minOccurrences
    * @param maxOccurrences
    * @param tipText
    */
   public AttributeType(String guid, String name, String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String tipText, String taggerId) {
      super(guid, name);
      if (minOccurrences < 0) {
         throw new IllegalArgumentException("minOccurrences must be greater than or equal to zero");
      }
      if (maxOccurrences < minOccurrences) {
         throw new IllegalArgumentException("maxOccurences can not be less than minOccurences");
      }
      this.baseAttributeTypeId = baseAttributeTypeId;
      this.attributeProviderNameId = attributeProviderNameId;
      this.baseAttributeClass = baseAttributeClass;
      this.providerAttributeClass = providerAttributeClass;
      this.defaultValue = defaultValue;
      this.oseeEnumType = oseeEnumType;
      this.maxOccurrences = maxOccurrences;
      this.minOccurrences = minOccurrences;
      this.tipText = tipText;
      this.fileTypeExtension = fileTypeExtension != null ? fileTypeExtension : "";
      this.taggerId = taggerId;
   }

   public String getBaseAttributeTypeId() {
      return baseAttributeTypeId;
   }

   public String getAttributeProviderId() {
      return attributeProviderNameId;
   }

   /**
    * @return Returns the baseAttributeClass.
    */
   public Class<? extends Attribute<?>> getBaseAttributeClass() {
      return baseAttributeClass;
   }

   /**
    * @return Returns the defaultValue.
    */
   public String getDefaultValue() {
      return defaultValue;
   }

   /**
    * @return Returns the maxOccurrences.
    */
   public int getMaxOccurrences() {
      return maxOccurrences;
   }

   /**
    * @return Returns the minOccurrences.
    */
   public int getMinOccurrences() {
      return minOccurrences;
   }

   public String getDescription() {
      return tipText;
   }

   public int getOseeEnumTypeId() {
      return oseeEnumType == null ? OseeEnumTypeManager.getDefaultEnumTypeId() : oseeEnumType.getTypeId();
   }

   public OseeEnumType getOseeEnumType() {
      return oseeEnumType;
   }

   @Override
   public String toString() {
      return getName();
   }

   public String getFileTypeExtension() {
      return fileTypeExtension;
   }

   public int compareTo(AttributeType other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AttributeType) {
         return super.equals(obj);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   /**
    * @return the providerAttributeClass
    */
   public Class<? extends IAttributeDataProvider> getProviderAttributeClass() {
      return providerAttributeClass;
   }

   /**
    * Get the registered tagger id for this attribute type
    * 
    * @return tagger id
    */
   public String getTaggerId() {
      return taggerId;
   }

   /**
    * Whether this attribute type is taggable.
    * 
    * @return <b>true</b> if this attribute type is taggable. <b>false</b> if this is not taggable.
    */
   public boolean isTaggable() {
      boolean toReturn = false;
      if (taggerId != null) {
         toReturn = Strings.isValid(taggerId.trim());
      }
      return toReturn;
   }

   public boolean isEnumerated() {
      return EnumeratedAttribute.class.isAssignableFrom(baseAttributeClass);
   }
}