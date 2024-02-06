/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * A base class that implements the {@link PublishingAppender} interface methods that are common to all publishing
 * formats.
 *
 * @author fi390f
 */

public abstract class PublishingAppenderBase implements PublishingAppender {

   /**
    * Generated Word ML is appended to this {@link Appendable}.
    */

   protected final Appendable appendable;

   /**
    * Counts the number of outline levels the maximum ({@link WordCoreUtil#OUTLINE_LEVEL_MAXIMUM}) has been exceeded by.
    * This member is incremented in {@link #startOutlineSubSection}. It is decremented in {@link #endOutlineSubSection}.
    * <p>
    * TODO: This member is not accounted for in {@link #setNextParagraphNumberTo}.
    */

   protected int flattenedLevelCount;

   /**
    * The maximum number of outline levels to use. This value cannot exceed {@link WordCoreUtil#OUTLINE_LEVEL_MAXIMUM}.
    */

   protected int maxOutlineLevel;

   /**
    * Tracks the current outlining depth. This member is incremented by {@link #startOutlineSubSection}. It is
    * decremented by {@link #endOutlineSubSection}. It is set in {@link #setNextParagraphNumberTo}.
    */

   protected int outlineLevel;

   /**
    * Word supports outline levels 1 to 9. This array is indexed using the Word outline level, index 0 is not used.
    */

   protected final int[] outlineNumber;

   protected PublishingAppenderBase(Appendable appendable, int maxOutlineLevel) {
      this.appendable = Objects.requireNonNull(appendable);
      this.flattenedLevelCount = 0;
      this.maxOutlineLevel = maxOutlineLevel;
      this.outlineLevel = 0;
      this.outlineNumber = new int[WordCoreUtil.OUTLINE_LEVEL_MAXIMUM + 1];
   }

   /**
    * Appends the provided text as it is to the {@link #appendable}.
    *
    * @param value the text to be appended.
    */

   @Override
   public PublishingAppender append(CharSequence value) {
      try {
         this.appendable.append(value);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return this;
   }

   @Override
   public void setMaxOutlineLevel(int maxOutlineLevel) {
      this.maxOutlineLevel = Math.min(maxOutlineLevel, FormatIndicator.WORD_ML.getMaximumOutlineDepth());
   }

   @Override
   public void setNextParagraphNumberTo(String outlineNumber) {

      var nextOutlineNumbers = outlineNumber.split("\\.");

      if (nextOutlineNumbers.length > WordCoreUtil.OUTLINE_LEVEL_MAXIMUM) {
         nextOutlineNumbers = Arrays.copyOf(nextOutlineNumbers, WordCoreUtil.OUTLINE_LEVEL_MAXIMUM);
      }

      Arrays.fill(this.outlineNumber, 0);

      try {
         for (int i = 0; i < nextOutlineNumbers.length; i++) {

            this.outlineNumber[i + 1] = Integer.parseInt(nextOutlineNumbers[i]);
         }
         this.outlineNumber[nextOutlineNumbers.length]--;
         this.outlineLevel = nextOutlineNumbers.length - 1;
      } catch (NumberFormatException ex) {
         //Do nothing
      }
   }

}
