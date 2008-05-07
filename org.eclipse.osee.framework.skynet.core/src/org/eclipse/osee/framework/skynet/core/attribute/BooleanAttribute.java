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

import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;

/**
 * @author Ryan D. Brooks
 */
public class BooleanAttribute extends CharacterBackedAttribute<Boolean> {
   public static final String[] booleanChoices = new String[] {"yes", "no"};
   private ICharacterAttributeDataProvider dataProvider;

   public BooleanAttribute(DynamicAttributeDescriptor attributeType, ICharacterAttributeDataProvider dataProvider) {
      super(attributeType);
      this.dataProvider = dataProvider;
      dataProvider.setValue(attributeType.getDefaultValue());
   }

   public Boolean getValue() {
      return dataProvider.getValueAsString().equals(booleanChoices[0]);
   }

   public void setValue(Boolean value) {
      dataProvider.setValue(value ? booleanChoices[0] : booleanChoices[1]);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      String toDisplay = dataProvider.getDisplayableString();
      return Boolean.parseBoolean(toDisplay) ? "yes" : "no";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setFromString(java.lang.String)
    */
   @Override
   public void setFromString(String value) throws Exception {
      boolean result = value != null && value.equalsIgnoreCase(BooleanAttribute.booleanChoices[0]);
      setValue(Boolean.valueOf(result));
   }
}