/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.types;

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Audrey E Denk
 */
public class InterfaceLogicalTypeField {
   private String name;
   private String attributeType;
   private boolean required;
   private boolean editable;
   private String defaultValue = Strings.EMPTY_STRING;
   private AttributeTypeId attributeTypeId = AttributeTypeId.SENTINEL;
   /**
    * Note: this has a 1:1 mapping to the json attributes on the platform type interface in platformType.d.ts in
    * osee.web
    */
   private String jsonPropertyName = Strings.EMPTY_STRING;

   public InterfaceLogicalTypeField() {
      //
   }

   public InterfaceLogicalTypeField(String name, String type, boolean required, boolean editable, String defaultValue, AttributeTypeId attributeType, String jsonPropertyName) {
      this(name, type, required, editable, defaultValue, jsonPropertyName);
      this.setAttributeTypeId(attributeType);
   }

   public InterfaceLogicalTypeField(String name, String type, boolean required, boolean editable, String defaultValue, String jsonPropertyName) {
      this.setName(name);
      this.setAttributeType(type);
      this.setRequired(required);
      this.setEditable(editable);
      this.setDefaultValue(defaultValue);
      this.setJsonPropertyName(jsonPropertyName);
   }

   public InterfaceLogicalTypeField(String name, String type, boolean required, boolean editable, AttributeTypeId attributeType, String jsonPropertyName) {
      this(name, type, required, editable, jsonPropertyName);
      this.setAttributeTypeId(attributeType);
   }

   public InterfaceLogicalTypeField(String name, String type, boolean required, boolean editable, String jsonPropertyName) {
      this(name, type, required, editable, Strings.EMPTY_STRING, jsonPropertyName);
   }

   public boolean isRequired() {
      return required;
   }

   public void setRequired(boolean required) {
      this.required = required;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public String getAttributeType() {
      return attributeType;
   }

   public void setAttributeType(String attributeType) {
      this.attributeType = attributeType;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isEditable() {
      return editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   /**
    * @return the attributeTypeId
    */
   public AttributeTypeId getAttributeTypeId() {
      return attributeTypeId;
   }

   /**
    * @param attributeTypeId the attributeTypeId to set
    */
   public void setAttributeTypeId(AttributeTypeId attributeTypeId) {
      this.attributeTypeId = attributeTypeId;
   }

   /**
    * @return the jsonPropertyName
    */
   public String getJsonPropertyName() {
      return jsonPropertyName;
   }

   /**
    * @return the jsonPropertyName
    */
   public void setJsonPropertyName(String jsonPropertyName) {
      this.jsonPropertyName = jsonPropertyName;
   }
}