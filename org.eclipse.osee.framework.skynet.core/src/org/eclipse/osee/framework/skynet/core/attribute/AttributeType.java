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
import org.eclipse.osee.framework.skynet.core.artifact.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;

/**
 * Type information for dynamic attributes.
 * 
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class AttributeType extends AbstractOseeType implements Comparable<AttributeType> {
   private Class<? extends Attribute<?>> baseAttributeClass;
   private Class<? extends IAttributeDataProvider> providerAttributeClass;
   private String defaultValue;
   private OseeEnumType oseeEnumType;
   private int maxOccurrences;
   private int minOccurrences;
   private String description;
   private String fileTypeExtension;
   private String taggerId;
   private String baseAttributeTypeId;
   private String attributeProviderNameId;
   private final DirtyStateDetail dirtyStateDetails;

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
      this.dirtyStateDetails = new DirtyStateDetail();
      setFields(typeName, baseAttributeTypeId, attributeProviderNameId, baseAttributeClass, providerAttributeClass,
            fileTypeExtension, defaultValue, oseeEnumType, minOccurrences, maxOccurrences, description, taggerId);
   }

   public void setFields(String name, String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId) {
      String fileExtensionToSet = fileTypeExtension != null ? fileTypeExtension : "";
      getDirtyDetails().updateDirty(baseAttributeTypeId, attributeProviderNameId, baseAttributeClass,
            providerAttributeClass, fileExtensionToSet, defaultValue, oseeEnumType, minOccurrences, maxOccurrences,
            description, taggerId);
      setName(name);
      this.baseAttributeTypeId = baseAttributeTypeId;
      this.attributeProviderNameId = attributeProviderNameId;
      this.baseAttributeClass = baseAttributeClass;
      this.providerAttributeClass = providerAttributeClass;
      this.defaultValue = defaultValue;
      this.oseeEnumType = oseeEnumType;
      this.maxOccurrences = maxOccurrences;
      this.minOccurrences = minOccurrences;
      this.description = description;
      this.fileTypeExtension = fileExtensionToSet;
      this.taggerId = taggerId;
   }

   public DirtyStateDetail getDirtyDetails() {
      return dirtyStateDetails;
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
      return description;
   }

   public int getOseeEnumTypeId() {
      return oseeEnumType == null ? OseeEnumTypeManager.getDefaultEnumTypeId() : oseeEnumType.getId();
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

   @Override
   public boolean isDirty() {
      return getDirtyDetails().isDirty();
   }

   @Override
   public void clearDirty() {
      getDirtyDetails().clear();
   }

   public final class DirtyStateDetail {
      private boolean isFileExtensionDirty;
      private boolean isTaggerIdDirty;
      private boolean isDescriptionDirty;
      private boolean isMinOccurrencesDirty;
      private boolean isMaxOccurrencesDirty;
      private boolean isOseeEnumTypeDirty;
      private boolean isDefaultValueDirty;
      private boolean isAttributeProviderClassDirty;
      private boolean isBaseAttributeClassDirty;
      private boolean isAttributeProviderNameIdDirty;
      private boolean isBaseAttributeTypeIdDirty;

      private DirtyStateDetail() {
         clear();
      }

      protected void updateDirty(String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileExtensionToSet, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId) {
         isFileExtensionDirty |= isDifferent(getBaseAttributeTypeId(), baseAttributeTypeId);
         isTaggerIdDirty |= isDifferent(getAttributeProviderId(), attributeProviderNameId);
         isDescriptionDirty |= isDifferent(getBaseAttributeClass(), baseAttributeClass);
         isMinOccurrencesDirty |= isDifferent(getProviderAttributeClass(), providerAttributeClass);
         isMaxOccurrencesDirty |= isDifferent(getDefaultValue(), defaultValue);
         isOseeEnumTypeDirty |= isDifferent(getOseeEnumType(), oseeEnumType);
         isDefaultValueDirty |= isDifferent(getMaxOccurrences(), maxOccurrences);
         isAttributeProviderClassDirty |= isDifferent(getMinOccurrences(), minOccurrences);
         isBaseAttributeClassDirty |= isDifferent(getDescription(), description);
         isAttributeProviderNameIdDirty |= isDifferent(getFileTypeExtension(), fileExtensionToSet);
         isBaseAttributeTypeIdDirty |= isDifferent(getTaggerId(), taggerId);
      }

      public boolean isFileExtensionDirty() {
         return isFileExtensionDirty;
      }

      public boolean isTaggerIdDirty() {
         return isTaggerIdDirty;
      }

      public boolean isDescriptionDirty() {
         return isDescriptionDirty;
      }

      public boolean isMinOccurrencesDirty() {
         return isMinOccurrencesDirty;
      }

      public boolean isMaxOccurrencesDirty() {
         return isMaxOccurrencesDirty;
      }

      public boolean isOseeEnumTypeDirty() {
         return isOseeEnumTypeDirty;
      }

      public boolean isDefaultValueDirty() {
         return isDefaultValueDirty;
      }

      public boolean isAttributeProviderClassDirty() {
         return isAttributeProviderClassDirty;
      }

      public boolean isBaseAttributeClassDirty() {
         return isBaseAttributeClassDirty;
      }

      public boolean isAttributeProviderNameIdDirty() {
         return isAttributeProviderNameIdDirty;
      }

      public boolean isBaseAttributeTypeIdDirty() {
         return isBaseAttributeTypeIdDirty;
      }

      public boolean isNameDirty() {
         return AttributeType.super.isDirty();
      }

      public boolean isDirty() {
         return isFileExtensionDirty() || //
         isTaggerIdDirty() || // 
         isDescriptionDirty() || //
         isMinOccurrencesDirty() || //
         isMaxOccurrencesDirty() || //
         isOseeEnumTypeDirty() || //
         isDefaultValueDirty() || //
         isAttributeProviderClassDirty() || //
         isBaseAttributeClassDirty() || // 
         isAttributeProviderNameIdDirty() || //
         isBaseAttributeTypeIdDirty() || //
         isNameDirty();
      }

      private void clear() {
         AttributeType.super.clearDirty();
         isFileExtensionDirty = false;
         isTaggerIdDirty = false;
         isDescriptionDirty = false;
         isMinOccurrencesDirty = false;
         isMaxOccurrencesDirty = false;
         isOseeEnumTypeDirty = false;
         isDefaultValueDirty = false;
         isAttributeProviderClassDirty = false;
         isBaseAttributeClassDirty = false;
         isAttributeProviderNameIdDirty = false;
         isBaseAttributeTypeIdDirty = false;
      }
   }
}