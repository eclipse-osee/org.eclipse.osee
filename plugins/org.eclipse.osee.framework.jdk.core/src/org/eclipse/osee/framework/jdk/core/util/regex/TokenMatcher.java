/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.jdk.core.util.regex;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import org.eclipse.osee.framework.jdk.core.type.CharSequenceWindow;

/**
 * A matcher to find occurrences of a {@link Pattern} that is optionally surrounded by a prefix {@link Pattern} and
 * suffix {@link Pattern}.
 * <p>
 * When a regular expression is large and complex it can perform poorly. If the complex regular expression can be split
 * into a simple core token regular expression with a prefix and suffix regular expression this, {@link TokenMatcher}
 * implementation can improve performance. The following sequence is used to find the next match:
 * <ul>
 * <li>The next match of the core token {@link Pattern} is found in the input sequence.</li>
 * <li>The suffix regular expression is then used in an anchored search just after the core token {@link Pattern} match
 * to determine if the suffix is present.</li>
 * <li>When the suffix is present, the input sequence is searched backwards to find the prefix.</li>
 * </ul>
 */

public class TokenMatcher {

   /**
    * Save the core token {@link Matcher}. Used to continue the search for more matches in the {@link CharSequence} and
    * for the core token group match methods.
    */

   private final Matcher coreTokenMatcher;

   /**
    * A {@link Function} to obtain a {@link Matcher} to search a {@link CharSequence} for the core token
    * {@link Pattern}.
    */

   private final Function<CharSequence, Matcher> coreTokenMatcherFunction;

   /**
    * State flag to indicate the {@link TokenMatcher} search of the {@link CharSequence} has been completed.
    */

   private boolean done;

   /**
    * Saves the exclusive ending character position of the last match.
    */

   private int endExclusive;

   /**
    * The {@link CharSequence} to search for pattern matches.
    */

   private final CharSequence input;

   /**
    * Saves the prefix {@link Matcher} for the last full prefix match attempt. This member is used for the prefix match
    * and group match methods.
    */

   private Matcher prefixMatcher;

   /**
    * A {@link Function} to obtain a {@link Matcher} to search a {@link CharSequence} for the prefix {@link Pattern}.
    */

   private final Function<CharSequence, Matcher> prefixMatcherFunction;

   /**
    * When <code>true</code>, the suffix and prefix patterns must be found after and before the core token match for a
    * successful match. When <code>false</code>, a core token match is sufficient for a successful match.
    */

   private final boolean prefixSuffixRequired;

   /**
    * Saves the inclusive starting position of the prefix.
    */

   private int prefixStartInclusive;

   /**
    * Saves the inclusive starting character position of the last match.
    */

   private int startInclusive;

   /**
    * State flag to indicate the {@link TokenMatcher} search of the {@link CharSequence} has started.
    */

   private boolean started;

   /**
    * Saves the suffix {@link Matcher} for the last suffix match attempt. This member is used for the suffix match and
    * group match methods.
    */

   private Matcher suffixMatcher;

   /**
    * A {@link Function} to obtain a {@link Matcher} to search a {@link CharSequence} for the suffix {@link Pattern}.
    */

   private final Function<CharSequence, Matcher> suffixMatcherFunction;

   /**
    * Saves the suffix search start position in the {@link CharSequence} after the last core token match.
    */

   private int suffixStartInclusive;

   /**
    * Package private constructor used by {@link TokenPattern#tokenMatcher} method for creating new
    * {@link TokenMatcher}s.
    *
    * @param input the {@link CharSequence} to be searched for matches.
    * @param prefixSuffixRequired when <code>true</code>, the prefix and suffix patterns must match for the match to be
    * successful.
    * @param prefixBackupSafety the maximum number of characters to search backwards for the prefix. A value less than
    * or equal to zero indicates no backup limit.
    * @param prefixStartChar the first character of the prefix.
    * @param prefixStartMatcherFunction a {@link Function} to obtain a {@link Matcher} to search a {@link CharSequence}
    * for the prefix start {@link Pattern}.
    * @param prefixMatcherFunction a {@link Function} to obtain a {@link Matcher} to search a {@link CharSequence} for
    * the prefix {@link Pattern}.
    * @param coreTokenMatcherFunction a {@link Function} to obtain a {@link Matcher} to search a {@link CharSequence}
    * for the core token {@link Pattern}.
    * @param suffixMatcherFunction a {@link Function} to obtain a {@link Matcher} to search a {@link CharSequence} for
    * the suffix {@link Pattern}.
    */
   //@formatter:off
   TokenMatcher
      (
         CharSequence                    input,
         Boolean                         prefixSuffixRequired,
         Function<CharSequence, Matcher> prefixMatcherFunction,
         Function<CharSequence, Matcher> coreTokenMatcherFunction,
         Function<CharSequence, Matcher> suffixMatcherFunction
      )
   {
      //@formatter:on
      this.input = input;
      this.prefixSuffixRequired = prefixSuffixRequired;
      this.prefixMatcherFunction = prefixMatcherFunction;
      this.coreTokenMatcherFunction = coreTokenMatcherFunction;
      this.suffixMatcherFunction = suffixMatcherFunction;

      this.coreTokenMatcher = this.coreTokenMatcherFunction.apply(input);
      this.suffixMatcher = null;
      this.prefixMatcher = null;

      this.startInclusive = 0;
      this.endExclusive = -1;
      this.prefixStartInclusive = -1;
      this.suffixStartInclusive = -1;
      this.started = false;
      this.done = false;
   }

   /**
    * Gets the offset in the {@link CharSequence} after the last character of the subsequence captured by the given
    * group in the core token pattern during the previous match operation.
    *
    * @param group the index of a capturing group in the core token pattern.
    * @return When the last core token match was successful, the offset after the last character captured by the core
    * token pattern group; otherwise -1.
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the core token pattern with the specified
    * index.
    */

   public int coreTokenEnd(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      return this.coreTokenMatcher.end(group);
   }

   /**
    * Returns a the input subsequence matched by the specified core token pattern group as a {@link CharSequenceWindow}.
    *
    * @param group the index of a capturing group in the core token pattern.
    * @return the input subsequence matched by the core token pattern group as a {@link CharSequence} backed by the
    * input {@link CharSequence}. during the previous match, or <code>null</code> if the group failed to match part of
    * the input {@link CharSequence}.
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the core token pattern with the specified
    * index.
    */

   public CharSequence coreTokenGroupCharSequence(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      var start = this.coreTokenStart(group);
      var end = this.coreTokenEnd(group);
      var charSequenceWindow = new CharSequenceWindow(this.input, start, end);
      return charSequenceWindow;
   }

   /**
    * Returns a copy of the input subsequence matched by the specified core token pattern group as a {@link String}.
    *
    * @param group the index of a capturing group in the core token pattern.
    * @return a copy of the input subsequence matched by the core token pattern group as a {@link String} during the
    * previous match, or <code>null</code> if the group failed to match part of the input {@link CharSequence}.
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the core token pattern with the specified
    * index.
    */

   public String coreTokenGroupString(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      return this.coreTokenMatcher.group(group);
   }

   /**
    * Gets the offset in the {@link CharSequence} of the first character of the subsequence captured by the given group
    * in the core token pattern during the previous match operation.
    *
    * @param group the index of a capturing group in the core token pattern.
    * @return When the last core token match was successful, the offset of the first character captured by the core
    * token pattern group; otherwise -1.
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the core token pattern with the specified
    * index.
    */

   public int coreTokenStart(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      return this.coreTokenMatcher.start(group);
   }

   /**
    * Gets the offset in the {@link CharSequence} after the last character of the subsequence captured by the previous
    * match operation. When the prefix and suffix are not required and the prefix and suffix were not matched the
    * captured subsequence will be the subsequence matched by the core token pattern.
    *
    * @return the index of the first character after the matched subsequence.
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    */

   public int end() {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      return this.endExclusive;
   }

   /**
    * Attempts to find the next occurrence of the "insert thing here" token within the provided string. This method
    * starts at the beginning of the provided string, or, if a previous invocation of the method was successful, at the
    * first character after the last found "insert thing here" token. When a match is found, the location of the matched
    * "insert thing here" token can be obtained via the {@link #startInclusive} and {@link #endExclusive} methods.
    *
    * @return <code>true</code>, when an "insert thing here" token is found; otherwise, <code>false</code>.
    */

   public boolean find() {

      if (this.done) {
         //a prior invocation determined there are none or no more Core Tokens.
         throw new IllegalStateException();
      }

      while (this.coreTokenMatcher.find()) {

         //The next Core Token was found
         var coreTokenStartInclusive = this.coreTokenMatcher.start();
         var coreTokenEndExclusive = this.coreTokenMatcher.end();

         //Check for the Suffix after the Core Token
         var suffixStringWindow = new CharSequenceWindow(this.input, coreTokenEndExclusive);
         var suffixMatcher = this.suffixMatcherFunction.apply(suffixStringWindow);

         if (!suffixMatcher.lookingAt()) {
            //The Suffix is NOT present

            if (this.prefixSuffixRequired) {
               //Need to keep looking for Core Tokens
               continue;
            } else {
               //Prefix and Suffix are not required, return the Core Token match
               this.startInclusive = coreTokenStartInclusive;
               this.endExclusive = coreTokenEndExclusive;
               this.prefixStartInclusive = -1;
               this.suffixStartInclusive = -1;
               this.prefixMatcher = null;
               this.suffixMatcher = null;
               this.started = true;
               this.done = false;
               return true;
            }
         }

         //The Suffix is present
         var suffixLength = suffixMatcher.end();

         //Check for the Prefix before the Core Token
         var prefixStringWindow = new CharSequenceWindow(this.input, coreTokenStartInclusive, 0);
         var prefixMatcher = this.prefixMatcherFunction.apply(prefixStringWindow);

         if (!prefixMatcher.lookingAt()) {
            //The Prefix is NOT present

            if (this.prefixSuffixRequired) {
               //Need to keep looking for core tokens
               continue;
            } else {
               //Prefix and Suffix are not required, return the core token match
               this.startInclusive = coreTokenStartInclusive;
               this.endExclusive = coreTokenEndExclusive;
               this.prefixStartInclusive = -1;
               this.suffixStartInclusive = -1;
               this.prefixMatcher = null;
               this.suffixMatcher = null;
               this.started = true;
               this.done = false;
               return true;
            }
         }

         //The Prefix is present
         var prefixLength = prefixMatcher.end();

         //The Prefix and Suffix were found, return the entire match
         this.startInclusive = coreTokenStartInclusive - prefixLength;
         this.endExclusive = coreTokenEndExclusive + suffixLength;
         this.prefixStartInclusive = coreTokenStartInclusive - 1;
         this.suffixStartInclusive = coreTokenEndExclusive;
         this.prefixMatcher = prefixMatcher;
         this.suffixMatcher = suffixMatcher;
         this.started = true;
         this.done = false;
         return true;
      }

      //There are no more core tokens

      this.startInclusive = -1;
      this.endExclusive = -1;
      this.prefixStartInclusive = -1;
      this.suffixStartInclusive = -1;
      this.prefixMatcher = null;
      this.suffixMatcher = null;
      this.started = true;
      this.done = true;
      return false;
   }

   /**
    * Gets the offset in the {@link CharSequence} being matched by the {@link TokenMatcher} after the last character of
    * the subsequence captured by the given group in the prefix pattern during the previous match operation.
    *
    * @param group the index of a capturing group in the prefix pattern.
    * @return
    * <dl>
    * <dt>When the last match operation was successful and the prefix pattern matched:</dt>
    * <dd>the offset in the {@link CharSequence} being matched by the {@link TokenMatcher} after the last character of
    * the subsequence captured by the given group in the prefix pattern.</dd>
    * <dt>When the last match operation was successful and the prefix pattern did not match:</dt>
    * <dd>-1</dd>
    * </dl>
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the prefix pattern with the specified index.
    */

   public int prefixEnd(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      //add 1 to make the end exclusive
      return Objects.nonNull(this.prefixMatcher) ? this.prefixStartInclusive - this.prefixMatcher.start(group) + 1 : -1;
   }

   /**
    * Returns the input subsequence matched by the specified prefix pattern group as a {@link CharSequenceWindow}. The
    * returned {@link CharSequenceWindow} will order the characters in the same direction as the {@link CharSequence}
    * being searched by the {@link TokenMatcher}.
    *
    * @param group the index of a capturing group in the prefix pattern.
    * @return the input subsequence matched by the prefix token pattern group as a {@link CharSequence} backed by the
    * input {@link CharSequence} during the previous match, or <code>null</code> if the group failed to match part of
    * the input {@link CharSequence}.
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the prefix pattern with the specified index.
    */

   public CharSequence prefixGroupCharSequence(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      var startInclusive = this.prefixStart(group);
      var endExclusive = this.prefixEnd(group);
      var charSequenceWindow = new CharSequenceWindow(this.input, startInclusive, endExclusive);
      return charSequenceWindow;
   }

   /**
    * Gets the offset in the {@link CharSequence} being matched by the {@link TokenMatcher} of the first character of
    * the subsequence captured by the given group in the prefix pattern during the previous match operation.
    *
    * @param group the index of a capturing group in the prefix pattern.
    * @return
    * <dl>
    * <dt>When the last match operation was successful and the prefix pattern matched:</dt>
    * <dd>the offset in the {@link CharSequence} being matched by the {@link TokenMatcher} of the first character of the
    * subsequence captured by the given group in the prefix pattern.</dd>
    * <dt>When the last match operation was successful and the prefix pattern did not match:</dt>
    * <dd>-1</dd>
    * </dl>
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the prefix pattern with the specified index.
    */

   public int prefixStart(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      //add 1 because this.prefixMatcher.end(group) is exclusive which causes the start position (negative range) to overshoot by 1.
      return Objects.nonNull(this.prefixMatcher) ? this.prefixStartInclusive - this.prefixMatcher.end(group) + 1 : -1;
   }

   /**
    * Gets the offset in the {@link CharSequence} of the first character of the subsequence captured by the previous
    * match operation. When the prefix and suffix are not required and the prefix and suffix were not matched the
    * captured subsequence will be the subsequence matched by the core token pattern.
    *
    * @return the index of the first character in the matched subsequence.
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    */

   public int start() {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      return this.startInclusive;
   }

   /**
    * Gets the offset in the {@link CharSequence} after the last character of the subsequence captured by the given
    * group in the suffix pattern during the previous match operation.
    *
    * @param group the index of a capturing group in the suffix pattern.
    * @return
    * <dl>
    * <dt>When the last match operation was successful and the suffix pattern matched:</dt>
    * <dd>the offset in the {@link CharSequence} after the last character of the subsequence captured by the given group
    * in the suffix pattern.</dd>
    * <dt>When the last match operation was successful and the suffix pattern did not match:</dt>
    * <dd>-1</dd>
    * </dl>
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the suffix pattern with the specified index.
    */

   public int suffixEnd(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      return Objects.nonNull(this.suffixMatcher) ? this.suffixMatcher.end(group) + this.suffixStartInclusive : -1;
   }

   /**
    * Returns the input sequence matched by the specified suffix pattern group as a {@link CharSequence}.
    *
    * @param group the index of a capturing group in the suffix pattern.
    * @return
    * <dl>
    * <dt>When the last match operation was successful and the suffix pattern matched:</dt>
    * <dd>the input subsequence matched by the suffix pattern group as a {@link CharSequence} backed by the input
    * {@link CharSequence}.</dd>
    * <dt>When the last match operation was successful and the suffix pattern did not match:</dt>
    * <dd><code>null</code></dd>
    * </dl>
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the suffix pattern with the specified index.
    */

   public CharSequence suffixGroupCharSequence(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }

      if (Objects.isNull(this.suffixMatcher)) {
         return null;
      }

      var start = this.suffixStart(group);
      var end = this.suffixEnd(group);
      var charSequenceWindow = new CharSequenceWindow(this.input, start, end);
      return charSequenceWindow;
   }

   /**
    * Returns a copy of the input sequence matched by the specified suffix pattern group as a {@link String}.
    *
    * @param group the index of a capturing group in the suffix pattern.
    * @return
    * <dl>
    * <dt>When the last match operation was successful and the suffix pattern matched:</dt>
    * <dd>a copy of the input subsequence matched by the suffix pattern group as a {@link String}.</dd>
    * <dt>When the last match operation was successful and the suffix pattern did not match:</dt>
    * <dd><code>null</code></dd>
    * </dl>
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the suffix pattern with the specified index.
    */

   public String suffixGroupString(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      return Objects.nonNull(this.suffixMatcher) ? this.suffixMatcher.group(group) : null;
   }

   /**
    * Gets the offset in the {@link CharSequence} of the first character of the subsequence captured by the given group
    * in the suffix pattern during the previous match operation.
    *
    * @param group the index of a capturing group in the suffix pattern.
    * @return
    * <dl>
    * <dt>When the last match operation was successful and the suffix pattern matched:</dt>
    * <dd>the offset in the {@link CharSequence} of the first character of the subsequence captured by the given group
    * in the suffix pattern.</dd>
    * <dt>When the last match operation was successful and the suffix pattern did not match:</dt>
    * <dd>-1</dd>
    * </dl>
    * @throws IllegalStateException when no match has yet been attempted or the previous match attempt failed.
    * @throws IndexOutOfBoundsException when there is no capturing group in the suffix pattern with the specified index.
    */

   public int suffixStart(int group) {
      if (!this.started || this.done) {
         throw new IllegalStateException();
      }
      return Objects.nonNull(this.suffixMatcher) ? this.suffixMatcher.start(group) + this.suffixStartInclusive : -1;
   }

}

/* EOF */