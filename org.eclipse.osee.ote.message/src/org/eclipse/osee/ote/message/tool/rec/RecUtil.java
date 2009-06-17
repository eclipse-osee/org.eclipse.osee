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
package org.eclipse.osee.ote.message.tool.rec;

import java.nio.ByteBuffer;

/**
 * @author Ken J. Aguilar
 *
 */
public class RecUtil {
   private static final int ASCII_A_ADDITIVE = 'A' - 10;
   private static final int ASCII_0_ADDITIVE = '0';

   /**
    * a very fast way of converting a byte into a two digit, zero padded hex value that is written directly into a byte
    * buffer
    * 
    * @param byteValue
    * @param buffer
    */
   public static void byteToAsciiHex(byte byteValue, ByteBuffer buffer) {
      int value = byteValue & 0xFF;
      int code = (value >>> 4);
      code += code >= 10 ? ASCII_A_ADDITIVE : ASCII_0_ADDITIVE;
      buffer.put((byte) code);
      code = value & 0x0F;
      code += code >= 10 ? ASCII_A_ADDITIVE : ASCII_0_ADDITIVE;
      buffer.put((byte) code);
   }
}
