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

import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class BooleanAttribute extends EnumeratedAttribute {
   private static final String[] booleanChoices = new String[] {"yes", "no"};

   public BooleanAttribute(String name) {
      super(name);
   }

   @Override
   public String getTypeName() {
      return "Boolean";
   }

   @Override
   public void setValidityXml(String validityXml) throws SAXException {
      if (validityXml != null && !validityXml.equals("") && !validityXml.equals("null")) {
         throw new IllegalArgumentException(
               "BooleanAttribute does not allow a non-empty validityXml to be set (since it is predefined)");
      }
   }

   @Override
   public String[] getChoices() {
      return booleanChoices;
   }

   public boolean getValue() {
      return getStringData().equals(booleanChoices[0]);

   }

   public void setValue(boolean value) {
      setStringData(value ? booleanChoices[0] : booleanChoices[1]);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setStringData(java.lang.String)
    */
   @Override
   public void setStringData(String value) {
      if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) {
         super.setStringData(booleanChoices[0]);
      } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")) {
         super.setStringData(booleanChoices[1]);
      } else {
         throw new IllegalArgumentException(
               String.format("\"%s\" is not a valid value for a boolean attribute.", value));
      }
   }
}