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

   /**
    * 
    */
   public CharArrayChange(int srcStartIndex, int srcEndIndex, char[] newChars, int offset, int length) {
      super();
      this.srcStartIndex = srcStartIndex;
      this.srcEndIndex = srcEndIndex;
      this.newChars = newChars;
      this.offset = offset;
      this.length = length;
   }

   public CharArrayChange(int srcStartIndex, int srcEndIndex, char[] newChars) {
      this(srcStartIndex, srcEndIndex, newChars, 0, newChars.length);
   }

   /* (non-Javadoc)
    * @see text.change.CharacterChanger#getStartIndex()
    */
   public int getStartIndex() {
      return srcStartIndex;
   }

   /* (non-Javadoc)
    * @see text.change.CharacterChanger#getEndIndex()
    */
   public int getEndIndex() {
      return srcEndIndex;
   }

   /* (non-Javadoc)
    * @see text.change.CharacterChanger#applyChange(char[], int)
    */
   public int applyChange(char[] dest, int destPos) {
      System.arraycopy(newChars, offset, dest, destPos, length);
      return destPos + length;
   }

   public void applyChange(Writer writer) throws IOException {
      writer.write(newChars, offset, length);
   }

   /* (non-Javadoc)
    * @see text.change.CharacterChanger#next()
    */
   public CharacterChanger next() {
      return next;
   }

   /* (non-Javadoc)
    * @see text.change.CharacterChanger#setNext(text.change.CharacterChanger)
    */
   public void setNext(CharacterChanger next) {
      this.next = next;
   }

   public int getLengthDelta() {
      return length - srcEndIndex + srcStartIndex;
   }
}
