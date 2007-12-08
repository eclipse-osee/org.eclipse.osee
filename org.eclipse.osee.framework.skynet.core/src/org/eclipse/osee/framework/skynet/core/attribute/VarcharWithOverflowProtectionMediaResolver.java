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
import java.util.Arrays;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;

/**
 * A string resolver that supports data in excess of 4000 characters. Only the first 4000 will be placed in the varchar,
 * and thus only this portion is visible to the searching API of Skynet.
 * 
 * @author Robert A. Fisher
 */
public class VarcharWithOverflowProtectionMediaResolver extends VarcharMediaResolver {

   private byte[] data;

   public VarcharWithOverflowProtectionMediaResolver() {
      super();
      this.data = new byte[0];
   }

   public byte[] getValue() {

      // If there is any blob data, then it has a full copy of the data
      if (getBlobData().length > 0)
         return getBlobData();
      // Otherwise the data is stored in the parent still
      else
         return super.getValue();
   }

   public boolean setValue(InputStream stream) {
      try {
         String value = Lib.inputStreamToString(stream);

         // Check the incoming value to handle potential overflow
         if (value.length() > 4000) {
            if (Arrays.equals(value.getBytes(), data)) return false;
            data = value.getBytes();
            varchar = value.substring(0, 3999);
            return true;

            // If the value is not larger, then ensure the overflow is clear
         } else {
            if (value.equals(varchar)) return false;
            varchar = value;
            data = new byte[0];
            return true;
         }

      } catch (IOException ex) {
         throw new RuntimeException("this should never happen.", ex);
      }
   }

   public void setBlobData(InputStream stream) {
      this.data = Streams.getByteArray(stream);
   }

   public byte[] getBlobData() {
      return data;
   }
}
