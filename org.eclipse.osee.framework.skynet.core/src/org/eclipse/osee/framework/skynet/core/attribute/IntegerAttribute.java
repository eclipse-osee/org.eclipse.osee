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
import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;

/**
 * @author Ryan D. Brooks
 */
public class IntegerAttribute extends CharacterBackedAttribute<Integer> {
   private ICharacterAttributeDataProvider dataProvider;

   public IntegerAttribute(DynamicAttributeDescriptor attributeType, ICharacterAttributeDataProvider dataProvider) {
      super(attributeType);
      this.dataProvider = dataProvider;
      String defaultValue = attributeType.getDefaultValue();
      if (Strings.isValid(defaultValue) != true) {
         defaultValue = "0";
      }
      dataProvider.setValue(defaultValue);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public Integer getValue() throws NumberFormatException {
      String integerString = dataProvider.getValueAsString();
      return Strings.isValid(integerString) ? Integer.valueOf(integerString) : null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void setValue(Integer value) {
      dataProvider.setValue(Integer.toString(value));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return dataProvider.getDisplayableString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setFromString(java.lang.String)
    */
   @Override
   public void setFromString(String value) throws Exception {
      Integer toSet = null;
      if (value == null || value.equals("")) {
         toSet = new Integer(0);
      } else {
         toSet = new Integer(value);
      }
      setValue(toSet);
   }
}