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
package org.eclipse.osee.framework.jdk.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Roberto E. Escobar
 */
public class ChecksumUtil {

   public static final String MD5 = "MD5";
   public static final String SHA = "SHA";

   private static final byte[] HEX_CHAR_TABLE = {
      (byte) '0',
      (byte) '1',
      (byte) '2',
      (byte) '3',
      (byte) '4',
      (byte) '5',
      (byte) '6',
      (byte) '7',
      (byte) '8',
      (byte) '9',
      (byte) 'a',
      (byte) 'b',
      (byte) 'c',
      (byte) 'd',
      (byte) 'e',
      (byte) 'f'};

   private ChecksumUtil() {
   }

   private static String getHexString(byte[] rawData) throws UnsupportedEncodingException {
      byte[] hex = new byte[2 * rawData.length];
      int index = 0;

      for (byte b : rawData) {
         int v = b & 0xFF;
         hex[index++] = HEX_CHAR_TABLE[v >>> 4];
         hex[index++] = HEX_CHAR_TABLE[v & 0xF];
      }
      return new String(hex, "ASCII");
   }

   public static String createChecksumAsString(InputStream inputStream, String algorithm) throws Exception {
      return getHexString(createChecksum(inputStream, algorithm));
   }

   public static byte[] createChecksum(InputStream inputStream, String algorithm) throws IOException, NoSuchAlgorithmException {
      MessageDigest checksum = MessageDigest.getInstance(algorithm);
      byte[] buffer = new byte[1024];
      int numRead = -1;
      while ((numRead = inputStream.read(buffer)) != -1) {
         checksum.update(buffer, 0, numRead);
      }
      return checksum.digest();
   }
}
