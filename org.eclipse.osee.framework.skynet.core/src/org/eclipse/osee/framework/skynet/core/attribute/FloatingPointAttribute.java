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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Ryan D. Brooks
 */
public class FloatingPointAttribute extends CharacterBackedAttribute<Double> {

   public FloatingPointAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public Double getValue() {
      String doubleString = getAttributeDataProvider().getValueAsString();
      return Strings.isValid(doubleString) ? Double.valueOf(doubleString) : null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void setValue(Double value) {
      getAttributeDataProvider().setValue(String.valueOf(value));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return getAttributeDataProvider().getDisplayableString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setFromString(java.lang.String)
    */
   @Override
   public void setFromString(String value) throws Exception {
      Double toSet = null;
      if (value == null || value.equals("")) {
         toSet = new Double(0);
      } else {
         toSet = new Double(value);
      }
      setValue(toSet);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#initializeDefaultValue()
    */
   @Override
   public void initializeDefaultValue() {
      getAttributeDataProvider().setValue(getAttributeType().getDefaultValue());
      String defaultValue = getAttributeType().getDefaultValue();
      if (!Strings.isValid(defaultValue)) {
         defaultValue = "0.0";
      }
      getAttributeDataProvider().setValue(defaultValue);
   }
}