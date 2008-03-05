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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

public class CompressedContentAttribute extends BinaryAttribute<InputStream> {

   public CompressedContentAttribute(DynamicAttributeDescriptor attributeType, String defaultValue) {
      super(attributeType);
   }

   public void setValue(InputStream value) {
      try {
         setRawContent(Lib.compressFile(value, getAttributeType().getName()));

      } catch (IOException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public InputStream getValue() {
      try {
         return new ByteArrayInputStream(Lib.decompressBytes(getRawContentStream()));

      } catch (IOException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws IOException {
      setRawContent(Lib.compressFile(value, getAttributeType().getName()));
   }
}