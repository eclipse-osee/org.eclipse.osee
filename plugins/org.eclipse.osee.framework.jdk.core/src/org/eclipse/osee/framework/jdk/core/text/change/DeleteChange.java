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

import java.io.Writer;

/**
 * @author Ryan D. Brooks
 */
public class DeleteChange implements CharacterChanger {
   private int srcStartIndex;
   private int srcEndIndex;
   private CharacterChanger next;

   /**
    * 
    */
   public DeleteChange(int srcStartIndex, int srcEndIndex) {
      super();
      this.srcStartIndex = srcStartIndex;
      this.srcEndIndex = srcEndIndex;
   }

   public void coalesce(DeleteChange overlapping) {
      DeleteChange changeA = null;
      DeleteChange changeB = null;

      // make changeA be the one with the smaller srcStartIndex
      if (overlapping.srcStartIndex < srcStartIndex) {
         changeA = overlapping;
         changeB = this;
      } else {
         changeA = this;
         changeB = overlapping;
      }

      if (changeB.srcStartIndex > changeA.srcEndIndex) { // Note: delete 2,5 is not adjacent to 6,7 (char 5 is not deleted)
         throw new IllegalArgumentException("Tried to coalesce non-adjacent, non-overlapping DeleteChanges");
      }
      this.srcStartIndex = changeA.srcStartIndex;
      this.srcEndIndex = changeB.srcEndIndex;
   }

   public int getStartIndex() {
      return srcStartIndex;
   }

   public int getEndIndex() {
      return srcEndIndex;
   }

   public int applyChange(char[] dest, int destPos) {
      return destPos;
   }

   public void applyChange(Writer writer) {
      return;
   }

   public CharacterChanger next() {
      return next;
   }

   public void setNext(CharacterChanger next) {
      this.next = next;
   }

   public int getLengthDelta() {
      return srcStartIndex - srcEndIndex;
   }
}
