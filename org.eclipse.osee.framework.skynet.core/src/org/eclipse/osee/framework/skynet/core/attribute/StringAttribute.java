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
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Ryan D. Brooks
 */
public class StringAttribute extends Attribute<String> {
   public StringAttribute(DynamicAttributeDescriptor attributeType, String defaultValue) {
      super(attributeType);
      setRawStringValue(defaultValue);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public String getValue() {
      byte[] rawContent = getRawContent();
      try {
         return rawContent == null ? getRawStringValue() : new String(rawContent, "UTF-8");
      } catch (UnsupportedEncodingException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
         return null;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void setValue(String value) {
      setRawStringValue(value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws IOException {
      throw new UnsupportedOperationException();
   }
}
