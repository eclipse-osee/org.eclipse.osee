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
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public final class JavaObjectAttribute extends BinaryAttribute<Object> {
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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } finally {
         try {
            if (inputStream != null) {
               inputStream.reset();
            }
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         try {
            if (objectStream != null) {
               objectStream.close();
            }
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return obj;
   }

   @Override
   protected boolean subClassSetValue(Object value) {
      try {
         ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
         ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
         objectStream.writeObject(value);
         objectStream.flush();
         objectStream.close();
         getAttributeDataProvider().setValue(ByteBuffer.wrap(byteStream.toByteArray()));
         getAttributeDataProvider().setDisplayableString(value.getClass().getName());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return true;
   }

   @Override
   public Object convertStringToValue(String value) {
      return getObjectFromBytes(ByteBuffer.wrap(value.getBytes()));
   }
}
