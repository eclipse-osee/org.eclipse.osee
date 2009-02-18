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
package org.eclipse.osee.framework.ui.data.model.editor.model;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataType extends DataType {

   private String defaultValue;
   private String validityXml;
   private String toolTipText;
   private String fileTypeExtension;
   private String taggerId;

   private int minOccurrence;
   private int maxOccurrence;

   private String baseAttributeClass;
   private String providerAttributeClass;

   public AttributeDataType() {
      this(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, 0, 0, EMPTY_STRING,
            EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);
   }

   public AttributeDataType(String typeId, String namespace, String name, String baseAttributeClass, String defaultValue, String fileTypeExtension, int maxOccurrence, int minOccurrence, String providerAttributeClass, String taggerId, String toolTipText, String validityXml) {
      super(typeId, namespace, name);
      this.baseAttributeClass = baseAttributeClass;
      this.defaultValue = defaultValue;
      this.fileTypeExtension = fileTypeExtension;
      this.maxOccurrence = maxOccurrence;
      this.minOccurrence = minOccurrence;
      this.providerAttributeClass = providerAttributeClass;
      this.taggerId = taggerId;
      this.toolTipText = toolTipText;
      this.validityXml = validityXml;
   }

   public AttributeDataType(String namespace, String name, String baseAttributeClass, String defaultValue, String fileTypeExtension, int maxOccurrence, int minOccurrence, String providerAttributeClass, String taggerId, String toolTipText, String validityXml) {
      this(EMPTY_STRING, namespace, name, baseAttributeClass, defaultValue, fileTypeExtension, maxOccurrence,
            minOccurrence, providerAttributeClass, taggerId, toolTipText, validityXml);
   }

   /**
    * @return the defaultValue
    */
   public String getDefaultValue() {
      return defaultValue;
   }

   /**
    * @param defaultValue the defaultValue to set
    */
   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   /**
    * @return the validityXml
    */
   public String getValidityXml() {
      return validityXml;
   }

   /**
    * @param validityXml the validityXml to set
    */
   public void setValidityXml(String validityXml) {
      this.validityXml = validityXml;
   }

   /**
    * @return the toolTipText
    */
   public String getToolTipText() {
      return toolTipText;
   }

   /**
    * @param toolTipText the toolTipText to set
    */
   public void setToolTipText(String toolTipText) {
      this.toolTipText = toolTipText;
   }

   /**
    * @return the fileTypeExtension
    */
   public String getFileTypeExtension() {
      return fileTypeExtension;
   }

   /**
    * @param fileTypeExtension the fileTypeExtension to set
    */
   public void setFileTypeExtension(String fileTypeExtension) {
      this.fileTypeExtension = fileTypeExtension;
   }

   /**
    * @return the taggerId
    */
   public String getTaggerId() {
      return taggerId;
   }

   /**
    * @param taggerId the taggerId to set
    */
   public void setTaggerId(String taggerId) {
      this.taggerId = taggerId;
   }

   /**
    * @return the minOccurrence
    */
   public int getMinOccurrence() {
      return minOccurrence;
   }

   /**
    * @param minOccurrence the minOccurrence to set
    */
   public void setMinOccurrence(int minOccurrence) {
      this.minOccurrence = minOccurrence;
   }

   /**
    * @return the maxOccurrence
    */
   public int getMaxOccurrence() {
      return maxOccurrence;
   }

   /**
    * @param maxOccurrence the maxOccurrence to set
    */
   public void setMaxOccurrence(int maxOccurrence) {
      this.maxOccurrence = maxOccurrence;
   }

   /**
    * @return the baseAttributeClass
    */
   public String getBaseAttributeClass() {
      return baseAttributeClass;
   }

   /**
    * @param baseAttributeClass the baseAttributeClass to set
    */
   public void setBaseAttributeClass(String baseAttributeClass) {
      this.baseAttributeClass = baseAttributeClass;
   }

   /**
    * @return the providerAttributeClass
    */
   public String getProviderAttributeClass() {
      return providerAttributeClass;
   }

   /**
    * @param providerAttributeClass the providerAttributeClass to set
    */
   public void setProviderAttributeClass(String providerAttributeClass) {
      this.providerAttributeClass = providerAttributeClass;
   }

}
