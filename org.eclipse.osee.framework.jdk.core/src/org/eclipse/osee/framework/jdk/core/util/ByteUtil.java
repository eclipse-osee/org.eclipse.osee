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

import java.io.PrintStream;
import java.nio.ByteBuffer;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class ByteUtil {

   public static void printBinary(byte[] data, int bytesPerGroup, int groupPerLine, PrintStream out) {
      int groups = 0;
      for (int i = 0; i < data.length; i++) {
         out.print(ByteUtil.toBinaryString(data[i]));
         if ((i + 1) % bytesPerGroup == 0) {
            out.print(" ");
            groups++;
            if ((groups) % groupPerLine == 0) {
               out.println();
            }
         }
      }
   }

   public static void printHex(byte[] data, int bytesPerGroup, int groupPerLine, PrintStream out) {
      int groups = 0;
      for (int i = 0; i < data.length; i++) {
         out.print(ByteUtil.toHexString(data[i]));
         if ((i + 1) % bytesPerGroup == 0) {
            out.print(" ");
            groups++;
            if ((groups) % groupPerLine == 0) {
               out.println();
            }
         }
      }
   }

   public static void printHex(byte[] data, int bytesPerGroup, int groupPerLine, StringBuilder strBuilder) {
      int groups = 0;
      for (int i = 0; i < data.length; i++) {
         strBuilder.append(ByteUtil.toHexString(data[i]));
         if ((i + 1) % bytesPerGroup == 0) {
            strBuilder.append(" ");
            groups++;
         }
      }
   }

   public static void printHex(byte[] data, int bytesPerGroup, int groupPerLine, boolean isSpaced, StringBuilder strBuilder) {
      int groups = 0;
      for (int i = 0; i < data.length; i++) {
         strBuilder.append(ByteUtil.toHexString(data[i]));
         if ((i + 1) % bytesPerGroup == 0 && isSpaced) {
            strBuilder.append(" ");
            groups++;
         }
      }
   }

   /**
    * NOTE the SDK supplies a Integer.toBinaryString but it is not formatted to a standard number of chars so it was not
    * a good option.
    * 
    * @param b
    * @return String
    */
   public static String toBinaryString(byte b) {
      StringBuffer sb = new StringBuffer();
      sb.append(((b >> 7) & 0x01));
      sb.append(((b >> 6) & 0x01));
      sb.append(((b >> 5) & 0x01));
      sb.append(((b >> 4) & 0x01));
      sb.append(((b >> 3) & 0x01));
      sb.append(((b >> 2) & 0x01));
      sb.append(((b >> 1) & 0x01));
      sb.append((b & 0x01));
      return sb.toString();
   }

   public static byte[] toBytes(long n) {
      byte[] bytes = new byte[8];
      toBytes(bytes, 0, n);
      return bytes;
   }

   public static void toBytes(byte[] bytes, int startPos, long n) {
      for (int i = startPos + 7; i >= startPos; i--) {
         bytes[i] = (byte) (n);
         n >>>= 8;
      }
   }

   public static void toBytes(byte[] bytes, int startPos, int n) {
      for (int i = startPos + 3; i >= startPos; i--) {
         bytes[i] = (byte) (n);
         n >>>= 8;
      }
   }

   public static String toHexString(byte b) {
      String temp = Integer.toHexString(b);
      if (temp.length() >= 2)
         return temp.substring(temp.length() - 2).toUpperCase();
      else
         return "0" + temp.substring(0).toUpperCase();
   }

   /**
    * Build a long from first 8 bytes of the array.
    * 
    * @param b The byte[] to convert.
    * @return A long.
    */
   public static long toLong(byte[] b) {
      if (b.length != 8) {
         throw new IllegalArgumentException();
      }

      return ((((long) b[7]) & 0xFF) + ((((long) b[6]) & 0xFF) << 8) + ((((long) b[5]) & 0xFF) << 16) + ((((long) b[4]) & 0xFF) << 24) + ((((long) b[3]) & 0xFF) << 32) + ((((long) b[2]) & 0xFF) << 40) + ((((long) b[1]) & 0xFF) << 48) + ((((long) b[0]) & 0xFF) << 56));
   }

   /**
    *  
    */
   public ByteUtil() {
      super();
   }

   /**
    * writes message data to a buffer in hex format
    * 
    * @param data
    * @param offset
    */
   public static void printByteDump(StringBuilder strBuilder, byte[] data, int offset, int length, int columnNum) {
      printByteDump(strBuilder, data, offset, length, columnNum, true);
   }

   /**
    * writes message data to a buffer in hex format
    * 
    * @param data
    * @param offset
    */
   public static void printByteDump(StringBuilder strBuilder, byte[] data, int offset, int length, int columnNum, boolean hex) {
      int columnCount = 0;
      final int endIndex = offset + length;
      for (int i = offset; i < endIndex; i++) {
         if (columnCount == columnNum) {
            strBuilder.append('\n');
            columnCount = 0;
         }
         if (hex) {
            strBuilder.append(String.format("%02x ", data[i]));
         } else {
        	 
            strBuilder.append(data[i]).append(' ');
         }
         columnCount++;
      }
      strBuilder.append('\n');
   }

   public static void printByteDump(StringBuilder strBuilder, ByteBuffer data, int offset, int length, int columnNum) {
      int currentPosition = data.position();
      //	   data.position(offset);
      int columnCount = 0;
      final int endIndex = offset + length;
      for (int i = offset; i < endIndex; i++) {
         if (columnCount == columnNum) {
            strBuilder.append('\n');
            columnCount = 0;
         }
         strBuilder.append(String.format("%02x ", data.get(i)));
         columnCount++;
      }
      strBuilder.append('\n');

      data.position(currentPosition);
   }

   public static void main(String[] args) {
      System.out.println(ByteUtil.toHexString((byte) 128));
   }
}