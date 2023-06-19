/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

/**
 * Utility class for working with Streams.
 * 
 * @author Jeff C. Phillips
 */
public class Streams {

   private final static int ORACLE_BUFF_LENGTH = 4 * 8192;

   /**
    * Converts a String into a InputStream using specifiec charSet
    * 
    * @return Return input stream reference
    */
   public static InputStream convertStringToInputStream(String string, String charSet)
      throws UnsupportedEncodingException {
      return new ByteArrayInputStream(string.getBytes(charSet));
   }

   public static byte[] getByteArray(InputStream stream) {

      if (stream == null) {
         throw new IllegalStateException("stream can not be null");
      }

      int length = -1;
      long read = 0;
      byte[] data = new byte[ORACLE_BUFF_LENGTH];
      LinkedList<byte[]> linkedByteArrays = new LinkedList<>();

      try {
         while ((length = stream.read(data)) != -1) {
            linkedByteArrays.add(data);
            read += length;

            // Get another buffer for the next go around
            data = new byte[ORACLE_BUFF_LENGTH];

            if (read > Integer.MAX_VALUE) {
               throw new UnsupportedOperationException("The data is too large.");
            }
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      // Get a contiguous buffer for all of the pieces to go into
      data = new byte[(int) read];
      int writeSize;
      int index = 0;

      for (byte[] chunk : linkedByteArrays) {
         writeSize = Math.min((int) read, ORACLE_BUFF_LENGTH);
         System.arraycopy(chunk, 0, data, index, writeSize);

         read -= writeSize;
         index += writeSize;
      }
      return data;
   }
}
