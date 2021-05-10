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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Audrey E Denk
 */
public class InterfaceLogicalTypeField {
   private AttributeTypeToken attributeType;
   private boolean required;
   private String defaultValue = Strings.EMPTY_STRING;

   public InterfaceLogicalTypeField() {
      //
   }

   public InterfaceLogicalTypeField(AttributeTypeToken type, boolean required, String defaultValue) {
      this.setAttributeType(type);
      this.setRequired(required);
      this.setDefaultValue(defaultValue);
   }

   public InterfaceLogicalTypeField(AttributeTypeToken name, boolean required) {
      this(name, required, Strings.EMPTY_STRING);
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

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }
}