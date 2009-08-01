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
public class CharChange implements CharacterChanger {
   private int srcStartIndex;
   private int srcEndIndex;
   private char newChar;
   private CharacterChanger next;

   /**
    * 
    */
   public CharChange(int srcStartIndex, int srcEndIndex, char newChar) {
      super();
      this.srcStartIndex = srcStartIndex;
      this.srcEndIndex = srcEndIndex;
      this.newChar = newChar;
   }

   public int getStartIndex() {
      return srcStartIndex;
   }

   public int getEndIndex() {
      return srcEndIndex;
   }

   public int applyChange(char[] dest, int destPos) {
      dest[destPos] = newChar;
      return destPos + 1;
   }

   public void applyChange(Writer writer) throws IOException {
      writer.write(newChar);
   }

   public CharacterChanger next() {
      return next;
   }

   public void setNext(CharacterChanger next) {
      this.next = next;
   }

   public int getLengthDelta() {
      return 1 - srcEndIndex + srcStartIndex;
   }
}
