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

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.CharacterEncoder;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class GUID implements Serializable {
   private final long time;
   private final int hash;
   private final int rand;
   private final String guidString;
   private static final long serialVersionUID = -3849714490764637010L;

   private static final ThreadLocal<byte[]> threadLocalBytes = new ThreadLocal<byte[]>() {
      protected synchronized byte[] initialValue() {
         return new byte[16];
      }
   };

   private static final ThreadLocal<CharacterEncoder> threadLocalEncoder = new ThreadLocal<CharacterEncoder>() {
      protected synchronized CharacterEncoder initialValue() {
         return new sun.misc.BASE64Encoder();
      }
   };

   public static boolean isValid(String guid) {
      if (guid.length() != 22) {
         return false;
      }
      Matcher m = Pattern.compile("^[0-9A-Za-z\\+_]+$").matcher(guid);
      if (!m.find()) {
         return false;
      }
      return true;
   }

   public GUID() {
      this(GUIDType.ARTIFACT);
   }

   public GUID(GUIDType type) {
      this.time = System.currentTimeMillis();
      this.hash = Thread.currentThread().hashCode();
      this.rand = (int) (Math.random() * Integer.MAX_VALUE);

      this.guidString = toGuidString(time, hash, rand, type);
   }

   public static String generateGuidStr() {
      return generateGuidStr(GUIDType.ARTIFACT);
   }

   public static String generateGuidStr(GUIDType type) {
      long time = System.currentTimeMillis();
      int hash = Thread.currentThread().hashCode();
      int rand = (int) (Math.random() * Integer.MAX_VALUE);
      return toGuidString(time, hash, rand, type);
   }

   private static String toGuidString(long time, int hash, int rand, GUIDType type) {
      byte[] rawBytes = (byte[]) threadLocalBytes.get();
      ByteUtil.toBytes(rawBytes, 0, time);
      ByteUtil.toBytes(rawBytes, 8, hash);
      ByteUtil.toBytes(rawBytes, 12, rand);

      CharacterEncoder base64Encoder = (CharacterEncoder) threadLocalEncoder.get();
      /*
       * 64 = 2^6 64^22 > 2^128 (2^6)^22 > 2^128 2^132 > 2^128 thus a 22 digit base64 number is
       * needed to represent a 16 byte number
       */
      return type + base64Encoder.encode(rawBytes).replace('/', '_').substring(1, 22);
   }

   public boolean equals(Object other) {

      if (other instanceof GUID) {
         return this.time == ((GUID) other).time && this.hash == ((GUID) other).hash && this.rand == ((GUID) other).rand;
      }

      return false;
   }

   public int hashCode() {
      int result = 17;
      result = result + (int) (time ^ (time >>> 32));
      result = 37 * result + hash;
      result = 37 * result + rand;
      return result;
   }

   public String toString() {
      return guidString;
   }
}