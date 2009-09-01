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

import java.util.regex.Pattern;
import sun.misc.CharacterEncoder;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class GUID {
   private final static Pattern pattern = Pattern.compile("[0-9A-Za-z\\+_=]{20,22}");

   private static final ThreadLocal<byte[]> threadLocalBytes = new ThreadLocal<byte[]>() {
      @Override
      protected synchronized byte[] initialValue() {
         return new byte[15];
      }
   };

   private static final ThreadLocal<CharacterEncoder> threadLocalEncoder = new ThreadLocal<CharacterEncoder>() {
      @Override
      protected synchronized CharacterEncoder initialValue() {
         return new sun.misc.BASE64Encoder();
      }
   };

   private GUID() {
   }

   public static String create() {
      long time = System.nanoTime();
      long rand = (long) (Math.random() * Long.MAX_VALUE);

      // 120-bit value
      byte[] rawValue = chopMostSignificantByte(time, rand);
      CharacterEncoder base64Encoder = threadLocalEncoder.get();
      return base64Encoder.encode(rawValue).replaceAll("/", "_");
   }

   public static boolean isValid(String guid) {
      return Strings.isValid(guid) && pattern.matcher(guid).matches();
   }

   private static byte[] chopMostSignificantByte(long high, long low) {
      byte[] writeBuffer = threadLocalBytes.get();
      // Omit the first byte (high >>> 56);
      writeBuffer[0] = (byte) (high >>> 48);
      writeBuffer[1] = (byte) (high >>> 40);
      writeBuffer[2] = (byte) (high >>> 32);
      writeBuffer[3] = (byte) (high >>> 24);
      writeBuffer[4] = (byte) (high >>> 16);
      writeBuffer[5] = (byte) (high >>> 8);
      writeBuffer[6] = (byte) (high >>> 0);

      writeBuffer[7] = (byte) (low >>> 56);
      writeBuffer[8] = (byte) (low >>> 48);
      writeBuffer[9] = (byte) (low >>> 40);
      writeBuffer[10] = (byte) (low >>> 32);
      writeBuffer[11] = (byte) (low >>> 24);
      writeBuffer[12] = (byte) (low >>> 16);
      writeBuffer[13] = (byte) (low >>> 8);
      writeBuffer[14] = (byte) (low >>> 0);
      return writeBuffer;
   }

   public static void main(String[] args) {
      System.out.println(GUID.create());
   }
}