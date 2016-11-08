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
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

@OseeAttribute("JavaObjectAttribute")
public final class JavaObjectAttribute extends BinaryAttribute<Object> {
   public static final String NAME = JavaObjectAttribute.class.getSimpleName();


   public JavaObjectAttribute(Long id) {
      super(id);
   }

   @Override
   public Object getValue() {
      return getObjectFromBytes(getDataProxy().getValueAsBytes());
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
         getLogger().error(ex, "");
      } finally {
         try {
            if (inputStream != null) {
               inputStream.reset();
            }
         } catch (IOException ex) {
            getLogger().error(ex, "Error resetting inputstream for attrId:[%s] gammaId:[%s]", getId(), getGammaId());
         } finally {
            Lib.close(objectStream);
         }
      }
      return obj;
   }

   @Override
   public boolean subClassSetValue(Object value) {
      try {
         ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
         ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
         objectStream.writeObject(value);
         objectStream.flush();
         objectStream.close();
         getDataProxy().setValue(ByteBuffer.wrap(byteStream.toByteArray()));
         getDataProxy().setDisplayableString(value != null ? value.getClass().getName() : "null");
      } catch (Exception ex) {
         getLogger().error(ex, "Error setting value");
      }
      return true;
   }

   @Override
   public Object convertStringToValue(String value) {
      if (value == null) {
         return null;
      }
      return getObjectFromBytes(ByteBuffer.wrap(value.getBytes()));
   }
}
