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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A {@link TokenMatcher} is a three part {@link Pattern} for finding tokens in a {@link CharSequence} with complex
 * prefix and suffix {@link Pattern}s. This provides better performance than a single complex regular expression. A
 * simpler core pattern is searched for in the {@link CharSequence}. Once the core pattern is found an anchored match
 * test can be made for the suffix pattern followed by a backwards search for the prefix pattern.
 * <p>
 * The {@link TokenMatcher} is a compiled ({@link Pattern}) representation of the prefix, core, and suffix regular
 * expressions.
 *
 * @author Loren K. Ashley
 */

public class TokenPattern {

   /**
    * The regular expression {@link Pattern} options to be used.
    */

   private static final int regexOptions = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE;

   /**
    * When <code>true</code> the prefix and suffix are required to match for a successful match.
    */

   private final Boolean prefixSuffixRequired;

   /**
    * A regular expression than can verify the prefix start token and everything between the prefix start token and the
    * core-token composes a valid prefix.
    */

   private final Pattern prefixPattern;

   /**
    * A regular expression for finding occurrences of the core-token in the publishing template.
    */

   private final Pattern coreTokenPattern;

   /**
    * A regular expression used to determine if a suffix is present after the core-token.
    */

   private final Pattern suffixPattern;

   /**
    * Private constructor is used to create all {@link TokenMatcherPattern} objects with compiled regular expressions
    * ({@link Pattern}s).
    *
    * @param prefixSuffixRequired when <code>true</code>, the prefix and suffix patterns must match for the match to be
    * successful.
    * @param prefixBackupSafety the maximum number of characters to search backwards for the prefix. A value less than
    * or equal to zero indicates no backup limit.
    * @param prefixStartChar the first character of the prefix.
    * @param prefixStartTokenPattern pattern for the initial portion of the prefix.
    * @param prefixPattern pattern to verify the entire prefix.
    * @param coreTokenPattern pattern for the core-token.
    * @param suffixPattern pattern used to determine the presence of a suffix.
    */

   //@formatter:off
      private
         TokenPattern
            (
               Boolean prefixSuffixRequired,
               Pattern prefixPattern,
               Pattern coreTokenPattern,
               Pattern suffixPattern
            )
      {
         this.prefixSuffixRequired    = prefixSuffixRequired;
         this.prefixPattern           = prefixPattern;
         this.coreTokenPattern        = coreTokenPattern;
         this.suffixPattern           = suffixPattern;
      }
      //@formatter:on

   /**
    * Creates a new {@link TokenMatcherPattern} with the search criteria.
    *
    * @param prefixSuffixRequired when <code>true</code>, the prefix and suffix patterns must match for the match to be
    * successful.
    * @param prefixBackupSafety the maximum number of characters to search backwards for the prefix. A value less than
    * or equal to zero indicates no backup limit.
    * @param prefixStartChar the first character of the prefix.
    * @param prefixStartTokenRegexp a regular expression to identify the starting portion of the prefix.
    * @param prefixRegexp a regular expression to verify the entire prefix.
    * @param coreTokenRegexp a regular expression to find the core-token.
    * @param suffixRegexp a regular expression to determine if a suffix is present after the core-token.
    * @throws NullPointerException if any of the provided regular expressions are <code>null</code>.
    * @throws PatternSyntaxException if any of the provided regular expression fail to compile.
    */

   //@formatter:off
   public static TokenPattern
      compile
         (
            Boolean prefixSuffixRequired,
            String  prefixRegexp,
            String  coreTokenRegexp,
            String  suffixRegexp
         )
   {
      Objects.requireNonNull( prefixSuffixRequired,   "TokenMatcher::new, parameter \"prefixSuffixRequired\" cannot be null."   );
      Objects.requireNonNull( prefixRegexp,           "TokenMatcher::new, parameter \"prefixRegexp\" cannot be null."           );
      Objects.requireNonNull( coreTokenRegexp,        "TokenMatcher::new, parameter \"coreTokenRegexp\" cannot be null."        );
      Objects.requireNonNull( suffixRegexp,           "TokenMatcher::new, parameter \"suffixRegexp\" cannot be null."           );

      var prefixPattern    = TokenPattern.compilePattern( prefixRegexp           );
      var coreTokenPattern = TokenPattern.compilePattern( coreTokenRegexp        );
      var suffixPattern    = TokenPattern.compilePattern( suffixRegexp           );

      return
         new TokenPattern
                (
                   prefixSuffixRequired,
                   prefixPattern,
                   coreTokenPattern,
                   suffixPattern
                );
   }
   //@formatter:on

   /**
    * Compiles the provided regular expression with the required options.
    *
    * @param regexp the regular expression to compile
    * @return the compiled regular expression as a {@link Pattern}.
    * @throws PatternSyntaxException if the regular expression fails to compile.
    */

   private static Pattern compilePattern(String regexp) {
      return Pattern.compile(regexp, TokenPattern.regexOptions);
   }

   /**
    * Creates a {@link TokenMatcher} that will match the token patterns against the given input {@link CharSequence}.
    *
    * @param input the {@link CharSequence} to be searched.
    * @return a {@link TokenMatcher} for these patterns and the given input.
    */

   public TokenMatcher tokenMatcher(CharSequence input) {
      //@formatter:off
         return
            new TokenMatcher
                   (
                     input,
                     this.prefixSuffixRequired,
                     new Function<CharSequence,Matcher>() {
                        @Override
                        public Matcher apply(CharSequence charSequence) {
                           return TokenPattern.this.prefixPattern.matcher( charSequence );
                        }
                     },
                     new Function<CharSequence,Matcher>() {
                        @Override
                        public Matcher apply(CharSequence charSequence) {
                           return TokenPattern.this.coreTokenPattern.matcher( charSequence );
                        }
                     },
                     new Function<CharSequence,Matcher>() {
                        @Override
                        public Matcher apply(CharSequence charSequence) {
                           return TokenPattern.this.suffixPattern.matcher( charSequence );
                        }
                     }
                   );
         //@formatter:on
   }
}

/* EOF */