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
package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.swt.graphics.Color;

public class HexTableRow {
   public final int offset;
   public int length;
   public byte[] array;
   private Color[] backgroundColor;
   private boolean[] displayAsAscii;

   private static final String[] hexTbl = new String[256];
   static {
		for (int i = 0; i < 256; i++) {
			hexTbl[i] = String.format("%02X", i);
		}
	}
   
   public HexTableRow(int offset, int length, byte[] array) {
      this.offset = offset;
      this.array = array;
      this.length = length;
      backgroundColor = new Color[length];
      displayAsAscii = new boolean[length];
   }

   public int getOffset() {
      return offset;
   }

   public byte[] getArray() {
      return array;
   }

   public void setArray(byte[] array) {
      this.array = array;
   }

   public String getHex(int column) {
      byte b = array[offset + column];
      return hexTbl[b & 0xFF];
   }

   public String getAscii(int column) {
      byte b = array[offset + column];
      if (b >= 32 && b < 127) {
         return new String(new byte[] {b});
      } else {
         return ".";
      }
   }

   public String getText(int column) {
      if (displayAsAscii[column]) {
         return getAscii(column);
      } else {
         return getHex(column);
      }
   }

   public String getBinary(int column) {
      byte b = array[offset + column];
      return Integer.toBinaryString(b & 0xFF);
   }

   public String getToolTip(int column) {
      return getBinary(column);
   }

   @Override
   public String toString() {
      return super.toString();
   }

   public Color getBackgroundColor(int column) {
      return backgroundColor[column];
   }

   public void setBackgroundColor(int columnm, Color backgroundColor) {
      this.backgroundColor[columnm] = backgroundColor;
   }

   /**
    * @param asAscii the asAscii to set
    */
   public void setDisplayAsAscii(int column, boolean displayAsAscii) {
      this.displayAsAscii[column] = displayAsAscii;
   }

}
