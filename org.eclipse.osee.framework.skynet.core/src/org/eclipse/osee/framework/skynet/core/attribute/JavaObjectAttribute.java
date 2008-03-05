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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

public class JavaObjectAttribute extends Attribute<Object> {

   public JavaObjectAttribute(DynamicAttributeDescriptor attributeType, String defaultValue) {
      super(attributeType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public Object getValue() {
      Object obj = null;
      try {
         ByteArrayInputStream inputStream = getRawContentStream();
         if (inputStream != null) {
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            obj = objectStream.readObject();
            objectStream.close();
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return obj;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void setValue(Object value) {
      try {
         ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
         ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
         objectStream.writeObject(value);
         objectStream.flush();
         objectStream.close();
         setRawContent(byteStream.toByteArray());
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws IOException {
      throw new UnsupportedOperationException();
   }
}
