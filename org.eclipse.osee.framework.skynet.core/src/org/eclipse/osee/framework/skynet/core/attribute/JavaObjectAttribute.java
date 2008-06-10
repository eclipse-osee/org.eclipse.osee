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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

public final class JavaObjectAttribute extends BinaryAttribute<Object> {

   public JavaObjectAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public Object getValue() {
      return getObjectFromBytes(getAttributeDataProvider().getValueAsBytes());
   }

   private Object getObjectFromBytes(ByteBuffer buffer) {
      Object obj = null;
      InputStream inputStream = null;
      ObjectInputStream objectStream = null;
      try {
         inputStream = Lib.byteBufferToInputStream(buffer);
         if (inputStream != null) {
            objectStream = new ObjectInputStream(inputStream);
            obj = objectStream.readObject();
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      } finally {
         try {
            if (inputStream != null) {
               inputStream.reset();
            }
         } catch (IOException ex) {
         }
         try {
            if (objectStream != null) {
               objectStream.close();
            }
         } catch (IOException ex) {
         }
      }
      return obj;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#subClassSetValue(java.lang.Object)
    */
   @Override
   public boolean subClassSetValue(Object value) {
      try {
         ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
         ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
         objectStream.writeObject(value);
         objectStream.flush();
         objectStream.close();
         getAttributeDataProvider().setValue(ByteBuffer.wrap(byteStream.toByteArray()));
         getAttributeDataProvider().setDisplayableString(value != null ? value.getClass().getName() : "null");
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#convertStringToValue(java.lang.String)
    */
   @Override
   protected Object convertStringToValue(String value) throws OseeCoreException {
      if (value == null) {
         return null;
      }
      return getObjectFromBytes(ByteBuffer.wrap(value.getBytes()));
   }
}
