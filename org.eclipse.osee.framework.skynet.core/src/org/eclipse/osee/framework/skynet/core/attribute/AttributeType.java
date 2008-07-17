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

import java.sql.SQLException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;

/**
 * Type information for dynamic attributes.
 * 
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class AttributeType implements Comparable<AttributeType> {
   public static final AttributeType[] EMPTY_ARRAY = new AttributeType[0];
   private Class<? extends Attribute<?>> baseAttributeClass;
   private Class<? extends IAttributeDataProvider> providerAttributeClass;
   private int attrTypeId;
   private String namespace;
   private String name;
   private String defaultValue;
   private String validityXml;
   private int maxOccurrences;
   private int minOccurrences;
   private String tipText;
   private String fileTypeExtension;
   private String taggerId;

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
    * @throws SQLException
    */
   protected AttributeType(int attrTypeId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String namespace, String name, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws SQLException {
      if (minOccurrences < 0) {
         throw new IllegalArgumentException("minOccurrences must be greater than or equal to zero");
      }
      if (maxOccurrences < minOccurrences) {
         throw new IllegalArgumentException("maxOccurences can not be less than minOccurences");
      }

      this.attrTypeId = attrTypeId;
      this.baseAttributeClass = baseAttributeClass;
      this.providerAttributeClass = providerAttributeClass;
      this.namespace = namespace == null ? "" : namespace;
      this.name = name;
      this.defaultValue = defaultValue;
      this.validityXml = validityXml;
      this.maxOccurrences = maxOccurrences;
      this.minOccurrences = minOccurrences;
      this.tipText = tipText;
      this.fileTypeExtension = fileTypeExtension != null ? fileTypeExtension : "";
      this.taggerId = taggerId;
   }

   /**
    * @return Returns the attrTypeId.
    */
   public int getAttrTypeId() {
      return attrTypeId;
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

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @return the namespace
    */
   public String getNamespace() {
      return namespace;
   }

   /**
    * @return Returns the tipText.
    */
   public String getTipText() {
      return tipText;
   }

   /**
    * @return Returns the validityXml.
    */
   public String getValidityXml() {
      return validityXml;
   }

   public String toString() {
      return name;
   }

   public String getFileTypeExtension() {
      return fileTypeExtension;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final AttributeType other = (AttributeType) obj;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (namespace == null) {
         if (other.namespace != null) return false;
      } else if (!namespace.equals(other.namespace)) return false;
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(AttributeType attributeType) {
      if (attributeType == null) {
         return -1;
      }
      return name.compareTo(attributeType.name);
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
}