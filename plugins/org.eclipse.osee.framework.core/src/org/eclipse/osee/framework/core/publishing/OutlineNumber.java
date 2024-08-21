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

import java.util.Optional;
import java.util.regex.Pattern;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Maintains the outlining number for the document being published.
 *
 * @author Loren K. Ashley
 */

public class OutlineNumber {

   /**
    * The maximum number of outlining levels allowed for any document format.
    */

   private static int maximumMaximumOutlineLevel = 16;

   /**
    * The {@link Pattern} used to split a outline number string into a string array of the outline levels.
    */

   private static Pattern splitPattern = Pattern.compile("\\.");

   /**
    * Tracks the index for the current outlining level up to the {@link #maximumIndex}. When the maximum outlining level
    * is reached, this index will stop incrementing. It will only be decremented when ending an outlining level and the
    * current outlining level is at or below the maximum outlining level.
    */

   private int currentIndex;

   /**
    * Tracks the index for the current outlining level. This index tracker may exceed the maximum outlining level.
    */

   private int currentIndexNoMaximum;

   /**
    * Saves the outlining number for the current section as a string.
    */

   private String currentOutlineNumber;

   /**
    * Saves the maximum index. This is one less than the {@link maximumOutlineLevel}.
    */

   private final int maximumIndex;

   /**
    * Saves the maximum number of outlining levels for the document.
    */

   private final int maximumOutlineLevel;

   /**
    * Saves the initial value to be used for a new outlining level.
    */

   private final int outlineLevelInitialValue;

   /**
    * An array of the section counts at each outline level. Array entries beyond the index for the current outline level
    * are maintained with a value of -1.
    */

   private final @NonNull int[] outlineNumber;

   /**
    * Saves the {@link Pattern} used to determine if an outline number string is valid.
    */

   private final @NonNull Pattern outlineNumberCheckPattern;

   /**
    * An array of the section counts as a string for each outline level. Array entries beyond the index for the current
    * outline level are maintained as <code>null</code>.
    */

   private final @NonNull String[] outlineStrings;

   /**
    * Saves the strategy for outlining numbers when the maximum outlining level has been exceeded.
    */

   private final @NonNull SectionNumberWhenMaximumOutlineLevelExceeded sectionNumberWhenMaximumOutlineLevelExceeded;

   /**
    * Saves whether to include a training separator (dot) on the outlining number.
    */

   private final @NonNull TrailingDot trailingDot;

   /**
    * Creates a new {@link OutlineNumber} for maintaining the document's outlining numbers.
    *
    * @param maximumOutlineLevel the maximum number of outlining levels allowed for the document.
    * @param outlineLevelInitialValue the initial value for each new outline level. Use {@link #setOutlineNumber} to
    * specify the initial outlining number.
    * @param trailingDot specifies whether the outline number string should end with a trailing separator (dot). See
    * {@link TrailingDot}.
    * @param sectionNumberWhenMaximumOutlineLevelExceeded specifies how to handle the outline number when the maximum
    * number of outline levels has been exceeded. See {@link SectionNumberWhenMaximumOutlineLevelExceeded}.
    * @throws NullPointerException when either of the parameters <code>trailingDot</code> or
    * <code>sectionNumberWhenMaximumOutlineLevelExceeded</code> are <code>null</code>.
    */

   public OutlineNumber(int maximumOutlineLevel, int outlineLevelInitialValue, @NonNull TrailingDot trailingDot, @NonNull SectionNumberWhenMaximumOutlineLevelExceeded sectionNumberWhenMaximumOutlineLevelExceeded) {

      this.trailingDot = Conditions.requireNonNull(trailingDot, "trailingDot");

      this.sectionNumberWhenMaximumOutlineLevelExceeded = //
         Conditions.requireNonNull(sectionNumberWhenMaximumOutlineLevelExceeded,
            "sectionNumberWhenMaximumOutlineLevelExceeded");

      //@formatter:off
      maximumOutlineLevel =
         ( maximumOutlineLevel < 1 )
            ? 1
            : ( maximumOutlineLevel > OutlineNumber.maximumMaximumOutlineLevel )
               ? OutlineNumber.maximumMaximumOutlineLevel
               : maximumOutlineLevel;
      //@formatter:on

      this.maximumOutlineLevel = maximumOutlineLevel;
      this.outlineLevelInitialValue = outlineLevelInitialValue;
      this.maximumIndex = maximumOutlineLevel - 1;
      this.currentIndex = 0;
      this.currentIndexNoMaximum = 0;

      this.outlineNumber = new int[maximumOutlineLevel];
      this.outlineStrings = new String[maximumOutlineLevel];

      this.outlineNumber[0] = this.outlineLevelInitialValue;
      this.outlineStrings[0] = Integer.toString(this.outlineLevelInitialValue);

      for (int i = 1; i < maximumOutlineLevel; i++) {
         this.outlineNumber[i] = -1;
         this.outlineStrings[i] = null;
      }

      //@formatter:off
      this.outlineNumberCheckPattern =
         maximumOutlineLevel == 1
            ? Pattern.compile("[0-9]+\\.?")
            : Pattern.compile( "(?:[0-9]+\\.){0," + this.maximumIndex + "}[0-9]+\\.?");
      //@formatter:on

      this.generateOutlineNumberString();
   }

   /**
    * Ends an outlining level and calculates the next outlining number as follows:
    * <dl>
    * <dt>Current Outlining Level Is At Or Below The Maximum Outlining Level</dt>
    * <dd>
    * <ul>
    * <li>The tracking outline level ({@link #currentIndexNoMaximum}) is decremented.</li>
    * <li>The effective outline level ({@link #currentIndex}) is decremented.</li>
    * <li>The last segment of the outlining number is removed.</li>
    * </ul>
    * </dd>
    * <dt>Current Outlining Level Is Above The Maximum Outlining Level</dt>
    * <dd>
    * <ul>
    * <li>The tracking outline level ({@link #currentIndexNoMaximum}) is decremented.</li>
    * <li>The effective outline level ({@link #currentIndex}) is left at the maximum outlining level.</li>
    * </ul>
    * </dd>
    * </dl>
    */

   public void endLevel() {

      final var lastIndex = this.currentIndex;

      if (this.currentIndexNoMaximum <= 0) {

         /*
          * Prevent indices from becoming negative
          */

         this.currentIndexNoMaximum = 0;
         this.currentIndex = 0;

      } else {

         this.currentIndexNoMaximum--;

         /*
          * Don't allow the currentIndex to decrement unless currentIndexNoMaximum is back in range
          */

         //@formatter:off
         this.currentIndex = (this.currentIndexNoMaximum <= this.maximumIndex)
                                ? this.currentIndexNoMaximum
                                : this.maximumIndex;
         //@formatter:on

      }

      if (lastIndex != this.currentIndex) {

         /*
          * Level has decreased below the maximum, reset the bottom level
          */

         this.outlineNumber[lastIndex] = -1;
         this.outlineStrings[lastIndex] = null;

      } else {

         this.outlineNumber[lastIndex]--;
         final var sectionNumber = this.outlineNumber[lastIndex];
         this.outlineStrings[lastIndex] = Integer.toString(sectionNumber);
      }

      this.generateOutlineNumberString();
   }

   /**
    * Creates the outline number string from the {@link #outlineStrings} array. Whether a trailing separator is included
    * is determined by the member {@link trailingDot}.
    */

   private void generateOutlineNumberString() {

      final var stringBuilder = new StringBuilder(128);

      for (int i = 0; (i < this.maximumOutlineLevel) && (this.outlineNumber[i] >= 0); i++) {
         stringBuilder.append(this.outlineStrings[i]).append(".");
      }

      if (this.trailingDot.isNo()) {
         stringBuilder.setLength(stringBuilder.length() - 1);
      }

      this.currentOutlineNumber = stringBuilder.toString();
   }

   /**
    * Gets the current outlining level up to the maximum outline level for the document.
    *
    * @return the current outlining level.
    */

   public int getOutlineLevel() {
      return this.currentIndex + 1;
   }

   /**
    * Gets the current outline number string according to the strategy specified by the member
    * {@link #sectionNumberWhenMaximumOutlineLevelExceeded}.
    *
    * @return an {@link Optional} containing the current outline number.
    */

   public Optional<String> getOutlineNumberString() {

      if (this.currentIndexNoMaximum <= this.maximumIndex) {
         return Optional.of(this.currentOutlineNumber);
      }

      switch (this.sectionNumberWhenMaximumOutlineLevelExceeded) {
         case INCREMENT_CURRENT_LEVEL:
            return Optional.of(this.currentOutlineNumber);
         case NO_SECTION_NUMBER:
            return Optional.empty();
         default:
            throw Conditions.invalidCase(this.sectionNumberWhenMaximumOutlineLevelExceeded,
               "sectionNumberWhenMaximumOutlineLevelExceeded", OseeCoreException::new);
      }

   }

   /**
    * Predicate to determine if the tracking outline level is at or above the maximum outlining level.
    *
    * @return <code>true</code> when the tacking outline level is above the maximum outlining level; otherwise
    * <code>false</code>.
    */

   public boolean isAtOrAboveMaximumOutlingLevel() {
      return (this.currentIndexNoMaximum >= this.maximumIndex);
   }

   /**
    * Predicate to determine if the tracking outline level is above the maximum outlining level.
    *
    * @return <code>true</code> when the tacking outline level is above the maximum outlining level; otherwise
    * <code>false</code>.
    */

   public boolean isAboveMaximumOutlingLevel() {
      return (this.currentIndexNoMaximum > this.maximumIndex);
   }

   /**
    * Predicate to determine if the <code>outlineNumber</code> matches the {@link #outlineNumberCheckPattern} and each
    * outline level is greater than or equal to {@link #outlineLevelInitialValue}.
    *
    * @param outlineNumber the outline number to check.
    * @return <code>true</code> when the outline number is a valid outline number string; otherwise, <code>false</code>.
    */

   public boolean isValidOutlineNumber(@Nullable CharSequence outlineNumber) {

      if (Strings.isInvalidOrBlank(outlineNumber)) {
         return false;
      }

      final var matcher = this.outlineNumberCheckPattern.matcher(outlineNumber);

      final var patternMatches = matcher.matches();

      if (!patternMatches) {
         return false;
      }

      final var outlineStrings = OutlineNumber.splitPattern.split(outlineNumber);

      for (final var levelString : outlineStrings) {
         final var level = Integer.valueOf(levelString);
         if (level < this.outlineLevelInitialValue) {
            return false;
         }
      }

      return true;
   }

   /**
    * Increments the section number for the current outlining level and regenerates the {@link #currentOutlineNumber}
    * string.
    */

   public void nextSection() {

      final var sectionNumber = ++this.outlineNumber[this.currentIndex];
      this.outlineStrings[this.currentIndex] = Integer.toString(sectionNumber);
      this.generateOutlineNumberString();
   }

   /**
    * Parses the provided <code>outlineNumber</code> and sets the current outlining number accordingly.
    *
    * @param outlineNumber the outline number to be set.
    * @return the newly set outline number.
    * @throws IllegalArgumentException when the <code>outlineNumber<code> is not a valid outline number according to the
    * predicate {@link #isValidOutlineNumber}.
    */

   public String setOutlineNumber(CharSequence outlineNumber) {

      final var matcher = this.outlineNumberCheckPattern.matcher(outlineNumber);

      if (!matcher.matches()) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "OutlineNumber::setOutlineNumber, outline line number does not match outline number pattern.")
                             .indentInc()
                             .segment( "Outline Number", outlineNumber )
                             .segment( "Outline Number Check Pattern", this.outlineNumberCheckPattern )
                             .toString()
                   );
         //@formatter:on
      }

      final var outlineStrings = OutlineNumber.splitPattern.split(outlineNumber, this.maximumOutlineLevel);

      for (int i = 0; i < this.maximumOutlineLevel; i++) {

         if (i < outlineStrings.length) {
            final var outlineSegment = outlineStrings[i];
            this.outlineStrings[i] = outlineSegment;
            this.outlineNumber[i] = Integer.valueOf(outlineSegment);
         } else {
            this.outlineStrings[i] = null;
            this.outlineNumber[i] = -1;
         }
      }

      this.currentIndexNoMaximum = outlineStrings.length - 1;
      this.currentIndex =
         this.currentIndexNoMaximum <= this.maximumIndex ? this.currentIndexNoMaximum : this.maximumIndex;
      this.generateOutlineNumberString();

      return this.currentOutlineNumber;
   }

   /**
    * Starts an outlining level and calculates the next outlining number as follows:
    * <dl>
    * <dt>Current Outlining Level Is Below The Maximum Outlining Level</dt>
    * <dd>
    * <ul>
    * <li>The tracking outline level ({@link #currentIndexNoMaximum}) is incremented.</li>
    * <li>The effective outline level ({@link #currentIndex}) is incremented.</li>
    * <li>A new segment with the initial segment value is added to the outlining number.</li>
    * </ul>
    * </dd>
    * <dt>Current Outlining Level Is At Or Above The Maximum Outlining Level</dt>
    * <dd>
    * <ul>
    * <li>The tracking outline level ({@link #currentIndexNoMaximum}) is incremented.</li>
    * <li>The effective outline level ({@link #currentIndex}) is left at the maximum outlining level.</li>
    * <li>The last segment of the outlining number is incremented.</li>
    * </ul>
    * </dd>
    * </dl>
    */

   public void startLevel() {

      this.currentIndexNoMaximum++;

      if (this.currentIndexNoMaximum <= this.maximumIndex) {

         /*
          * The new level is at or below the maximum, and another segment to the outlining number
          */

         this.currentIndex = this.currentIndexNoMaximum;
         this.outlineNumber[this.currentIndex] = this.outlineLevelInitialValue;

      } else {

         /*
          * The new level is above the maximum, increment the bottom level
          */

         this.outlineNumber[this.currentIndex]++;

      }

      final var sectionNumber = this.outlineNumber[this.currentIndex];
      this.outlineStrings[this.currentIndex] = Integer.toString(sectionNumber);
      this.generateOutlineNumberString();

   }

}

/* EOF */
