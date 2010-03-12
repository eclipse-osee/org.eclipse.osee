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
package org.eclipse.osee.framework.jdk.core.text.change;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Ryan D. Brooks
 */
public class CharArrayChange implements CharacterChanger {
   private int srcStartIndex;
   private int srcEndIndex;
   private char[] newChars;
   private int offset;
   private int length;
   private CharacterChanger next;

   public CharArrayChange(int srcStartIndex, int srcEndIndex, char[] newChars, int offset, int length, boolean copy) {
      super();
      this.srcStartIndex = srcStartIndex;
      this.srcEndIndex = srcEndIndex;
      if (copy) {
         this.newChars = new char[newChars.length];
         System.arraycopy(newChars, 0, this.newChars, 0, newChars.length);
      } else {
         this.newChars = newChars;
      }
      this.offset = offset;
      this.length = length;
   }

   /**
    * 
    */
   public CharArrayChange(int srcStartIndex, int srcEndIndex, char[] newChars, int offset, int length) {
      this(srcStartIndex, srcEndIndex, newChars, offset, length, false);
   }

   public CharArrayChange(int srcStartIndex, int srcEndIndex, char[] newChars) {
      this(srcStartIndex, srcEndIndex, newChars, 0, newChars.length);
   }

   public int getStartIndex() {
      return srcStartIndex;
   }

   public int getEndIndex() {
      return srcEndIndex;
   }

   public int applyChange(char[] dest, int destPos) {
      System.arraycopy(newChars, offset, dest, destPos, length);
      return destPos + length;
   }

   public void applyChange(Writer writer) throws IOException {
      writer.write(newChars, offset, length);
   }

   public CharacterChanger next() {
      return next;
   }

   public void setNext(CharacterChanger next) {
      this.next = next;
   }

   public int getLengthDelta() {
      return length - srcEndIndex + srcStartIndex;
   }
}
