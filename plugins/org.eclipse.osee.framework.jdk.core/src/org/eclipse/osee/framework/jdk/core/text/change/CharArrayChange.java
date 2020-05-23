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

package org.eclipse.osee.framework.jdk.core.text.change;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Ryan D. Brooks
 */
public class CharArrayChange implements CharacterChanger {
   private final int srcStartIndex;
   private final int srcEndIndex;
   private char[] newChars;
   private final int offset;
   private final int length;
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

   public CharArrayChange(int srcStartIndex, int srcEndIndex, char[] newChars, int offset, int length) {
      this(srcStartIndex, srcEndIndex, newChars, offset, length, false);
   }

   public CharArrayChange(int srcStartIndex, int srcEndIndex, char[] newChars) {
      this(srcStartIndex, srcEndIndex, newChars, 0, newChars.length);
   }

   @Override
   public int getStartIndex() {
      return srcStartIndex;
   }

   @Override
   public int getEndIndex() {
      return srcEndIndex;
   }

   @Override
   public int applyChange(char[] dest, int destPos) {
      System.arraycopy(newChars, offset, dest, destPos, length);
      return destPos + length;
   }

   @Override
   public void applyChange(Writer writer) throws IOException {
      writer.write(newChars, offset, length);
   }

   @Override
   public CharacterChanger next() {
      return next;
   }

   @Override
   public void setNext(CharacterChanger next) {
      this.next = next;
   }

   @Override
   public int getLengthDelta() {
      return length - srcEndIndex + srcStartIndex;
   }
}
