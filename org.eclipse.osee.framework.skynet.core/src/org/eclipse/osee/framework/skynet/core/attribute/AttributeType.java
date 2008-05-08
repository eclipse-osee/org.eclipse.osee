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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.providers.AbstractAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IBinaryAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;

/**
 * Type information for dynamic attributes.
 * 
 * @author Robert A. Fisher
 */
public class AttributeType implements Comparable<AttributeType> {
   public static final AttributeType[] EMPTY_ARRAY = new AttributeType[0];
   private Class<? extends Attribute<?>> baseAttributeClass;
   private Class<? extends AbstractAttributeDataProvider> providerAttributeClass;
   private int attrTypeId;
   private String namespace;
   private String name;
   private String defaultValue;
   private String validityXml;
   private int maxOccurrences;
   private int minOccurrences;
   private String tipText;
   private String fileTypeExtension;

   // These arrays are going to be used for reflection
   private static final Class<?>[] attributeDataProviderSignature = new Class<?>[] {AttributeStateManager.class};

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
   public AttributeType(int attrTypeId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends AbstractAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String namespace, String name, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText) throws SQLException {
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
   }

   public DynamicAttributeManager createAttributeManager(Artifact parentArtifact, boolean initialized) {
      return new DynamicAttributeManager(parentArtifact, this, initialized);
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
    * @return Returns the AttributeDataProviderClass.
    */
   private Class<? extends AbstractAttributeDataProvider> getAttributeDataProviderClass() {
      return providerAttributeClass;
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
    * Creates a new <code>AttributeDataProvider</code> that is consistent with this descriptor.
    * 
    * @throws Exception
    */
   protected AbstractAttributeDataProvider createAttributeDataProvider(AttributeStateManager stateManager) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      return getAttributeDataProviderClass().getConstructor(attributeDataProviderSignature).newInstance(
            new Object[] {stateManager});
   }

   /**
    * Creates a new <code>Attribute</code> that is consistent with this descriptor.
    * 
    * @param artifact
    * @param attributeDataProvider
    * @return new attribute instance
    * @throws Exception
    */
   protected Attribute<?> createAttribute(Artifact artifact, IAttributeDataProvider attributeDataProvider) throws Exception {
      Attribute<?> toReturn = null;
      Object[] params = new Object[] {this, attributeDataProvider};
      try {
         Constructor<? extends Attribute<?>> constructor = getAttributeConstructor(artifact);
         toReturn = constructor.newInstance(params);
      } catch (Exception ex) {
         throw new Exception(String.format("Error creating attribute:\n Class: [%s] Params: [%s]",
               getBaseAttributeClass(), Arrays.deepToString(params)), ex);
      }
      return toReturn;
   }

   private Constructor<? extends Attribute<?>> getAttributeConstructor(Artifact artifact) throws SecurityException, NoSuchMethodException {
      Constructor<? extends Attribute<?>> constructor = null;
      Class<? extends Attribute<?>> attributeClass = getBaseAttributeClass();

      //TODO: JPhillips - This should be removed when the blob attribute conversion is complete
      if (artifact instanceof WordArtifact && name.equals("Word Formatted Content")) {
         WordArtifact wordArtifact = (WordArtifact) artifact;

         if (wordArtifact.isWholeWordArtifact()) {
            attributeClass = WordWholeDocumentAttribute.class;
         } else {
            attributeClass = WordTemplateAttribute.class;
         }
      }

      try {
         constructor =
               attributeClass.getConstructor(new Class[] {AttributeType.class, ICharacterAttributeDataProvider.class});

      } catch (Exception ex) {
         constructor =
               attributeClass.getConstructor(new Class[] {AttributeType.class, IBinaryAttributeDataProvider.class});
      }
      return constructor;
   }

}