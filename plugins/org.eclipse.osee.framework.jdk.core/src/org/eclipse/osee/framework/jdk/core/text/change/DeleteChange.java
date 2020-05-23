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

import java.io.Writer;

/**
 * @author Ryan D. Brooks
 */
public class DeleteChange implements CharacterChanger {
   private int srcStartIndex;
   private int srcEndIndex;
   private CharacterChanger next;

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
      return destPos;
   }

   @Override
   public void applyChange(Writer writer) {
      return;
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
      return srcStartIndex - srcEndIndex;
   }
}
