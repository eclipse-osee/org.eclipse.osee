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

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ryan D. Brooks
 */
public class BooleanAttribute extends Attribute<Boolean> {
   private static final String[] booleanChoices = new String[] {"yes", "no"};

   public BooleanAttribute(DynamicAttributeDescriptor attributeType, String defaultValue) {
      super(attributeType);
      setRawStringValue(defaultValue);
   }

   public Boolean getValue() {
      return getRawStringValue().equals(booleanChoices[0]);
   }

   public void setValue(Boolean value) {
      setRawStringValue(value ? booleanChoices[0] : booleanChoices[1]);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws IOException {
      throw new UnsupportedOperationException();
   }
}