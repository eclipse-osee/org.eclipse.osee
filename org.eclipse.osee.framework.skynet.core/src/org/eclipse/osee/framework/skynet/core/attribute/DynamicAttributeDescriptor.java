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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Type information for dynamic attributes.
 * 
 * @author Robert A. Fisher
 */
public class DynamicAttributeDescriptor implements Comparable<DynamicAttributeDescriptor> {
   public static final DynamicAttributeDescriptor[] EMPTY_ARRAY = new DynamicAttributeDescriptor[0];
   private Class<? extends Attribute> baseAttributeClass;
   private int attrTypeId;
   private String namespace;
   private String name;
   private String defaultValue;
   private String validityXml;
   private int maxOccurrences;
   private int minOccurrences;
   private String tipText;

   // These arrays are going to be used for reflection
   private static final Class<?>[] reflectionSignature = new Class<?>[] {String.class};
   private final Object[] reflectionParams;

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
   public DynamicAttributeDescriptor(DynamicAttributeDescriptorCache cache, int attrTypeId, Class<? extends Attribute> baseAttributeClass, String namespace, String name, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText) throws SQLException {
      if (minOccurrences < 0) {
         throw new IllegalArgumentException("minOccurrences must be greater than or equal to zero");
      }
      if (maxOccurrences < minOccurrences) {
         throw new IllegalArgumentException("maxOccurences can not be less than minOccurences");
      }

      this.attrTypeId = attrTypeId;
      this.baseAttributeClass = baseAttributeClass;
      this.namespace = namespace == null ? "" : namespace;
      this.name = name;
      this.defaultValue = defaultValue;
      this.validityXml = validityXml;
      this.maxOccurrences = maxOccurrences;
      this.minOccurrences = minOccurrences;
      this.tipText = tipText;
      cache.cache(this);

      this.reflectionParams = new Object[] {name};
   }

   public DynamicAttributeManager createAttributeManager(Artifact parentArtifact, boolean initialized) {
      return new DynamicAttributeManager(parentArtifact, this, initialized);
   }

   /**
    * Creates a new <code>Attribute</code> that is consistent with this descriptor.
    */
   protected Attribute createAttribute() throws IllegalStateException {
      try {
         Attribute attribute =
               getBaseAttributeClass().getConstructor(reflectionSignature).newInstance(reflectionParams);

         if (defaultValue != null && !defaultValue.equals("null")) attribute.swagValue(defaultValue);
         attribute.setValidityXml(validityXml);

         return attribute;
      } catch (Exception ex) {
         throw new IllegalStateException(ex.getLocalizedMessage());
      }
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
   public Class<? extends Attribute> getBaseAttributeClass() {
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
      final DynamicAttributeDescriptor other = (DynamicAttributeDescriptor) obj;
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
   public int compareTo(DynamicAttributeDescriptor attributeType) {
      if (attributeType == null) {
         return -1;
      }
      return name.compareTo(attributeType.name);
   }
}