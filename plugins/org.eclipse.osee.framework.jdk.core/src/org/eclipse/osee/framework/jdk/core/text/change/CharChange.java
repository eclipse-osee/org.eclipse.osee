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
   private final int srcStartIndex;
   private final int srcEndIndex;
   private final char newChar;
   private CharacterChanger next;

   public CharChange(int srcStartIndex, int srcEndIndex, char newChar) {
      super();
      this.srcStartIndex = srcStartIndex;
      this.srcEndIndex = srcEndIndex;
      this.newChar = newChar;
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
      dest[destPos] = newChar;
      return destPos + 1;
   }

   @Override
   public void applyChange(Writer writer) throws IOException {
      writer.write(newChar);
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
      return 1 - srcEndIndex + srcStartIndex;
   }
}
