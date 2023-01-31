/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * @author Megumi Telles
 * @author Loren K. Ashley
 */

public class WordCoreUtil {

   /**
    * Factory for creating {@link TokenMatcher} objects for finding "insert thing here" tokens in a publishing template.
    */

   private static class TokenMatcherPattern {

      /**
       * The regular expression {@link Pattern} options to be used.
       */

      private static final int regexOptions = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE;

      /**
       * The first character of the optional "insert thing here" token prefix.
       */

      private final char prefixStartChar;

      /**
       * A regular expression that can identify the starting character sequence of an "insert thing here" token prefix.
       */

      private final Pattern prefixStartTokenPattern;

      /**
       * A regular expression than can verify the prefix start token and everything between the prefix start token and
       * the core-token composes a valid prefix.
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
       * @param prefixStartChar the first character of the prefix.
       * @param prefixStartTokenPattern pattern for the initial portion of the prefix.
       * @param prefixPattern pattern to verify the entire prefix.
       * @param coreTokenPattern pattern for the core-token.
       * @param suffixPattern pattern used to determine the presence of a suffix.
       */

      //@formatter:off
      private
         TokenMatcherPattern
            (
               char prefixStartChar,
               Pattern prefixStartTokenPattern,
               Pattern prefixPattern,
               Pattern coreTokenPattern,
               Pattern suffixPattern
            )
      {
         this.prefixStartChar         = prefixStartChar;
         this.prefixStartTokenPattern = prefixStartTokenPattern;
         this.prefixPattern           = prefixPattern;
         this.coreTokenPattern        = coreTokenPattern;
         this.suffixPattern           = suffixPattern;
      }
      //@formatter:on

      /**
       * Creates a new {@link TokenMatcherPattern} with the search criteria.
       *
       * @param prefixStartChar the first character of the prefix.
       * @param prefixStartTokenRegexp a regular expression to identify the starting portion of the prefix.
       * @param prefixRegexp a regular expression to verify the entire prefix.
       * @param coreTokenRegexp a regular expression to find the core-token.
       * @param suffixRegexp a regular expression to determine if a suffix is present after the core-token.
       * @throws NullPointerException if any of the provided regular expressions are <code>null</code>.
       * @throws PatternSyntaxException if any of the provided regular expression fail to compile.
       */

      //@formatter:off
      static TokenMatcherPattern
         compile
         (
            char prefixStartChar,
            String prefixStartTokenRegexp,
            String prefixRegexp,
            String coreTokenRegexp,
            String suffixRegexp
         )
      {
         Objects.requireNonNull( prefixStartTokenRegexp, "TokenMatcher::new, parameter \"prefixStartTokenRegexp\" cannot be null." );
         Objects.requireNonNull( prefixRegexp,           "TokenMatcher::new, parameter \"prefixRegexp\" cannot be null."           );
         Objects.requireNonNull( coreTokenRegexp,        "TokenMatcher::new, parameter \"coreTokenRegexp\" cannot be null."        );
         Objects.requireNonNull( suffixRegexp,           "TokenMatcher::new, parameter \"suffixRegexp\" cannot be null."           );

         var prefixStartTokenPattern = TokenMatcherPattern.compilePattern( prefixStartTokenRegexp );
         var prefixPattern           = TokenMatcherPattern.compilePattern( prefixRegexp           );
         var coreTokenPattern        = TokenMatcherPattern.compilePattern( coreTokenRegexp        );
         var suffixPattern           = TokenMatcherPattern.compilePattern( suffixRegexp           );

         return
            new TokenMatcherPattern
                   (
                      prefixStartChar,
                      prefixStartTokenPattern,
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
         return Pattern.compile(regexp, TokenMatcherPattern.regexOptions);
      }

      /**
       * Creates a {@link TokenMatcher} that will match the "insert thing here" token patterns against the given input
       * {@link String}.
       *
       * @param string the input to be matched.
       * @return a {@link TokenMatcher} for these patterns and the given input.
       */

      TokenMatcher tokenMatcher(String string) {
         //@formatter:off
         return
            new TokenMatcher
                   (
                     string,
                     this.prefixStartChar,
                     new Function<CharSequence,Matcher>() {
                        @Override
                        public Matcher apply(CharSequence CharSequence) {
                           return TokenMatcherPattern.this.prefixStartTokenPattern.matcher( CharSequence );
                        }
                     },
                     new Function<CharSequence,Matcher>() {
                        @Override
                        public Matcher apply(CharSequence CharSequence) {
                           return TokenMatcherPattern.this.prefixPattern.matcher( CharSequence );
                        }
                     },
                     new Function<CharSequence,Matcher>() {
                        @Override
                        public Matcher apply(CharSequence CharSequence) {
                           return TokenMatcherPattern.this.coreTokenPattern.matcher( CharSequence );
                        }
                     },
                     new Function<CharSequence,Matcher>() {
                        @Override
                        public Matcher apply(CharSequence CharSequence) {
                           return TokenMatcherPattern.this.suffixPattern.matcher( CharSequence );
                        }
                     }
                   );
         //@formatter:on
      }
   }

   /**
    * A matcher to find occurrences of the "insert thing here" token in a publishing template string. The "insert thing
    * here" tokens consist of the following parts:
    *
    * <pre>
    *    [ &lt;prefix&gt; ] &lt;core-token&gt; [ &lt;suffix&gt; ]
    * </pre>
    * <dl>
    * <dt>core-token:
    * <dt>
    * <dd>The core-token is the "INSERT_ARTIFACT_HERE" or "INSERT_LINK_HERE" marker in a publishing template. The
    * core-token and its prefix and suffix are replaced in the publishing template with the content to be
    * published.</dd>
    * <dt>prefix:</dt>
    * <dd>When the core-token is placed into a publishing template without a prefix and suffix, the core-token is not
    * visible when the publishing template is opened in Word. Saving such a template from Word will cause the core-token
    * to be lost. The prefix is the Word ML paragraph tag, optional paragraph presentation, run tag, and text tag at the
    * start of a Word ML paragraph.</dd>
    * <dt>suffix:</dt>
    * <dd>The suffix is the Word ML closing text tag, closing run tag, and closing paragraph tag at the end of a Word ML
    * paragraph.</dd>
    * </dl>
    * The {@link TokenMatcher} will indicate the starting and ending indices of the "insert thing here" token as
    * follows:
    * <dl>
    * <dt>The prefix, core-token, and suffix are found:
    * <dt>
    * <dd>The starting index will be the starting index of the prefix. The ending index will be the index of the first
    * character after the end of the suffix.</dd>
    * <dt>The core-token is found, but either or both of the prefix and suffix are not found:
    * <dt>
    * <dd>The starting index will be the starting index of the core-token. The ending index will be the index of the
    * first character after the end of the core-token.</dd>
    * </dl>
    *
    * @implNote The use of a single regular expression to find the optional prefix, core-token, and optional suffix
    * performs very poorly. The following sequence is used to find the next token:
    * <ul>
    * <li>The next core-token is found in the publishing template using a much simpler regular expression with good
    * performance.</li>
    * <li>The suffix regular expression is then used in an anchored search just after the core-token to determine if the
    * suffix is present.</li>
    * <li>When the suffix is present, the publishing template string is searched backwards to find the prefix.</li>
    * </ul>
    */

   private static class TokenMatcher {

      String string;
      char prefixStartChar;
      Function<CharSequence, Matcher> prefixStartTokenMatcherFunction;
      Function<CharSequence, Matcher> prefixMatcherFunction;
      Function<CharSequence, Matcher> coreTokenMatcherFunction;
      Function<CharSequence, Matcher> suffixMatcherFunction;
      Matcher tokenMatcher;
      int start;
      int end;
      boolean started = false;
      boolean done = false;

      //@formatter:off
      TokenMatcher
         (
            String                          string,
            char                            preambleStartChar,
            Function<CharSequence, Matcher> prefixStartTokenMatcherFunction,
            Function<CharSequence, Matcher> prefixMatcherFunction,
            Function<CharSequence, Matcher> coreTokenMatcherFunction,
            Function<CharSequence, Matcher> suffixMatcherFunction
         )
      {
         //@formatter:on
         this.string = string;
         this.prefixStartChar = preambleStartChar;
         this.prefixStartTokenMatcherFunction = prefixStartTokenMatcherFunction;
         this.prefixMatcherFunction = prefixMatcherFunction;
         this.coreTokenMatcherFunction = coreTokenMatcherFunction;
         this.suffixMatcherFunction = suffixMatcherFunction;

         this.tokenMatcher = this.coreTokenMatcherFunction.apply(string);

         this.start = 0;
         this.end = -1;
         this.started = false;
         this.done = false;
      }

      /**
       * Returns the string index of the first character in the last found "insert here token".
       *
       * @return the string index of the first character in the last found "insert here token".
       * @throws IllegalStateException when a {@link #find} operation has not yet been performed or a prior
       * {@link #find} operation returned <code>false</code>/
       */

      int start() {
         if (!this.started || this.done) {
            throw new IllegalStateException();
         }
         return this.start;
      }

      /**
       * Returns the string index of the first character after the last found "insert here token".
       *
       * @return the string index of the first character after the last found "insert here token".
       * @throws IllegalStateException when a {@link #find} operation has not yet been performed or a prior
       * {@link #find} operation returned <code>false</code>.
       */

      int end() {
         if (!this.started || this.done) {
            throw new IllegalStateException();
         }
         return this.end;
      }

      /**
       * Searches the string backwards for the "insert thing here" token prefix.
       *
       * @param index the position in the input to start searching backwards for the prefix from.
       * @return when prefix is found, the character index in the search string where the prefix was found; otherwise,
       * -1.
       */

      private int backMatch(int index) {

         var s = index;
         var safety = index - 2048;
         safety = safety > 0 ? safety : 0;
         char c;
         do {

            //back up looking for the prefix start char
            do {
               s--;
               c = this.string.charAt(s);
            } while ((c != this.prefixStartChar) && (s >= safety));

            if (c != this.prefixStartChar) {
               //safety expired, the prefix start char was not found
               return -1;
            }

            //the prefix start char was found, check for paragraph tag
            var checkStringWindow = new StringWindow(this.string, s);
            var matcher = this.prefixStartTokenMatcherFunction.apply(checkStringWindow);

            if (matcher.lookingAt()) {
               //paragraph tag found, check for full pattern
               var bigMatcher = this.prefixMatcherFunction.apply(checkStringWindow);
               if (bigMatcher.lookingAt()) {
                  //leading paragraph tags verified
                  return s;
               } else {
                  //tag found but leading paragraph is not correct
                  return -1;
               }
            }
            //was not paragraph tag, keep looking
         } while (s > safety);

         //safety expired
         return -1;
      }

      /**
       * Attempts to find the next occurrence of the "insert thing here" token within the provided string. This method
       * starts at the beginning of the provided string, or, if a previous invocation of the method was successful, at
       * the first character after the last found "insert thing here" token. When a match is found, the location of the
       * matched "insert thing here" token can be obtained via the {@link #start} and {@link #end} methods.
       *
       * @return <code>true</code>, when an "insert thing here" token is found; otherwise, <code>false</code>.
       */

      boolean find() {

         if (this.done) {
            //a prior invocation determined there are none or no-more "insert here tokens".
            throw new IllegalStateException();
         }

         if (!this.tokenMatcher.find()) {
            //there are no-more "insert here tokens".
            this.start = -1;
            this.end = -1;
            this.started = true;
            this.done = true;
            return false;
         }

         //the next "insert here token" was found
         var tokenStart = this.tokenMatcher.start();
         var tokenEnd = this.tokenMatcher.end();

         //check for trailing paragraph tags at end of the token
         var tailStringWindow = new StringWindow(this.string, tokenEnd);
         var tailMatcher = this.suffixMatcherFunction.apply(tailStringWindow);

         if (!tailMatcher.lookingAt()) {
            //trailing paragraph tags are NOT present
            this.start = tokenStart;
            this.end = tokenEnd;
            this.started = true;
            this.done = false;
            return true;
         }

         //trailing paragraph tags are present
         var tailEnd = tailMatcher.end();

         //look backwards for the opening paragraph tags
         var backMatch = this.backMatch(tokenStart);

         if (backMatch == -1) {
            //opening paragraph tags were NOT found, never mind the trailing tags
            this.start = tokenStart;
            this.end = tokenEnd;
            this.started = true;
            this.done = false;
            return true;
         }

         //opening paragraph tags were found
         this.start = backMatch;
         this.end = tokenEnd + tailEnd;
         this.started = true;
         this.done = false;
         return true;
      }
   }

   /**
    * Provides a {@link CharSequence} implementation for a portion of a {@link String}.
    */

   private static class StringWindow implements CharSequence {

      /**
       * The {@link String} to have a portion be windowed as a {@link CharSequence}.
       */

      private final String string;

      /**
       * The starting index of the portion to be windowed.
       */

      private final int start;

      /**
       * The ending index plus one of the portion to be windowed.
       */

      private final int end;

      /**
       * Creates a {@link CharSequence} {@link StringWindow} implementation for a portion of the provided
       * {@link String}.
       *
       * @param string the {@link String} to create a window for.
       * @param start the starting character position of the {@link CharSequence} {@link StringWindow} within the
       * provided {@link String}.
       */

      StringWindow(String string, int start) {
         this.string = string;
         this.start = start;
         this.end = string.length();
      }

      /**
       * Creates a {@link CharSequence} {@link StringWindow} implementation for a portion of the provided
       * {@link String}.
       *
       * @param string the {@link String} to create a window for.
       * @param start the starting character position of the {@link CharSequence} {@link StringWindow} within the
       * provided {@link String}.
       * @param end the character position plus one of the end of the {@link CharSequence} {@link StringWindow} within
       * the provided {@link String}.
       */

      StringWindow(String string, int start, int end) {
         this.string = string;
         this.start = start;
         this.end = end;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public int length() {
         return this.end - this.start;
      }

      /**
       * {@inheritDoc}
       *
       * @throws IndexOutOfBoundsException {@inheritDoc}
       */

      @Override
      public char charAt(int index) {
         //@formatter:off
         if(    ( index < 0 )
             || ( start + index >= end ) ) {
            throw new IndexOutOfBoundsException();
         }
         return this.string.charAt(start + index);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public CharSequence subSequence(int start, int end) {
         return new StringWindow(this.string, this.start + start, this.start + end);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public String toString() {
         return this.string.substring( this.start, this.end );
      }
   }

   public static String FEATUREAPP = "feature";
   public static String CONFIGAPP = "configuration";
   public static String CONFIGGRPAPP = "configurationgroup";

   public static String MAX_TAG_OCCURENCE = "30";
   public static String WORD_ML_TAGS = "(\\<[^>]*?>){0," + MAX_TAG_OCCURENCE + "}";

   public static String TABLE_CELL = "<w:tc>";
   public static String TABLE = "<w:tbl>";
   public static String START_TABLE_ROW = "<w:tr wsp:rsidR=";
   public static String END_TABLE_ROW = "</w:tr>";
   public static String LIST = "<w:listPr>";
   public static String START_PARAGRAPH = "<w:p wsp:rsid";
   public static String WHOLE_END_PARAGRAPH = "</w:t></w:r></w:p>";
   public static String END_PARAGRAPH = "</w:p>";
   public static String END_DOCUMENT = "</w:wordDocument>";

   public static String END = "E" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "d ?" + WORD_ML_TAGS + " ?";
   public static String ELSE = "E" + WORD_ML_TAGS + "l" + WORD_ML_TAGS + "s" + WORD_ML_TAGS + "e ?";
   public static String FEATURE =
      "F" + WORD_ML_TAGS + "e" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "e";
   public static String CONFIG =
      "C" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "f" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "g" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n";
   public static String CONFIGGRP =
      "C" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "f" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "g" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "G" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "p";

   public static String NOT = "N" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "t";

   public static String ENDBRACKETS = WORD_ML_TAGS + " ?(\\[(.*?)\\]) ?";
   public static String OPTIONAL_ENDBRACKETS = " ?(" + WORD_ML_TAGS + "(\\[.*?\\]))?";
   public static String BEGINFEATURE = FEATURE + WORD_ML_TAGS + " ?" + ENDBRACKETS;
   public static String ENDFEATURE = END + WORD_ML_TAGS + FEATURE + OPTIONAL_ENDBRACKETS;
   public static String BEGINCONFIG =
      CONFIG + WORD_ML_TAGS + "( " + WORD_ML_TAGS + NOT + WORD_ML_TAGS + ")? ?" + ENDBRACKETS;
   public static String ENDCONFIG = END + WORD_ML_TAGS + CONFIG + OPTIONAL_ENDBRACKETS;

   public static String BEGINCONFIGGRP =
      CONFIGGRP + WORD_ML_TAGS + "( " + WORD_ML_TAGS + NOT + WORD_ML_TAGS + ")? ?" + ENDBRACKETS;
   public static String ENDCONFIGGRP = END + WORD_ML_TAGS + CONFIGGRP + OPTIONAL_ENDBRACKETS;
   public static String ELSE_EXP =
      "(" + FEATURE + "|" + CONFIGGRP + "|" + CONFIG + ")" + WORD_ML_TAGS + " " + WORD_ML_TAGS + ELSE;

   public static Pattern ELSE_PATTERN = Pattern.compile(ELSE_EXP, Pattern.DOTALL | Pattern.MULTILINE);

   public static String BIN_DATA_STRING = "<w:binData.*?w:name=\"(.*?)\".*?</w:binData>";
   public static Pattern BIN_DATA_PATTERN =
      Pattern.compile(BIN_DATA_STRING, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   public static Pattern IMG_SRC_PATTERN =
      Pattern.compile("<v:imagedata.*?src=\"([^\"]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

   public static Pattern FULL_PATTERN = Pattern.compile(
      "(" + BEGINFEATURE + ")|(" + ENDFEATURE + ")|(" + BEGINCONFIGGRP + ")|(" + ENDCONFIGGRP + ")|(" + BEGINCONFIG + ")|(" + ENDCONFIG + ")",
      Pattern.DOTALL | Pattern.MULTILINE);

   public static Pattern LIST_PATTERN = Pattern.compile(
      "<w:listPr>(<w:ilvl([^>]*?)/?>(</w:ilvl>)?)?(<w:ilfo([^>]*?)/?>(</w:ilfo>)?)?<wx:t wx:val=\"([^>]*?)\"/?>(</wx:t>)?<wx:font wx:val=\"[^\"]*?\"/?>(</wx:font>)?</w:listPr>");

   public static String EMPTY_LIST_REGEX =
      "<w:p wsp:rsidP=\"[^\"]*?\" wsp:rsidR=\"[^\"]*?\" wsp:rsidRDefault=\"[^\"]*?\"><w:pPr><w:pStyle w:val=\"[^\"]*?\"></w:pStyle><w:listPr><wx:t wx:val=\"([^>]*?)\"></wx:t><wx:font wx:val=\"[^\"]*?\"></wx:font></w:listPr></w:pPr><w:r><w:t></w:t></w:r></w:p>";

   public static String OSEE_BOOKMARK_REGEX =
      "^<aml:annotation[^<>]+w:name=\"OSEE\\.([^\"]*)\"[^<>]+w:type=\"Word\\.Bookmark\\.Start\\\"/><aml:annotation[^<>]+Word.Bookmark.End\\\"/>";

   public static String OSEE_HYPERLINK_REGEX = "<w:instrText>\\s+HYPERLINK[^<>]+\"OSEE\\.([^\"]*)\"\\s+</w:instrText>";

   private static final String OSEE_LINK_MARKER = "OSEE_LINK(%s)";

   private static final String WORDML_BOOKMARK_FORMAT =
      "<aml:annotation aml:id=\"%s\" w:type=\"Word.Bookmark.Start\" w:name=\"OSEE.%s\"/><aml:annotation aml:id=\"%s\" w:type=\"Word.Bookmark.End\"/>";

   private static final String WORDML_INTERNAL_DOC_LINK_FORMAT =
      "<w:r><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:instrText> HYPERLINK \\l \"OSEE.%s\" </w:instrText></w:r><w:r><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>%s</w:t></w:r><w:r><w:fldChar w:fldCharType=\"end\"/></w:r>";

   private static final String WORDML_INTERNAL_DOC_REFERENCE_FORMAT =
      "<w:r><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:instrText> REF OSEE.%s \\h %s</w:instrText></w:r><w:r><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Sec#</w:t></w:r><w:r><w:fldChar w:fldCharType=\"end\"/></w:r><w:r><w:t> %s</w:t></w:r>";

   private static final String WORDML_LINK_FORMAT =
      "<w:hlink w:dest=\"%s\"><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>%s</w:t></w:r></w:hlink>";

   private static final Pattern tagKiller =
      Pattern.compile("<.*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern =
      Pattern.compile("<w:p( .*?)?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final String AML_ANNOTATION = "<.??aml:annotation.*?>";
   private static final String AML_CONTENT = "<.??aml:content.*?>";
   private static final String DELETIONS = "<w:delText>.*?</w:delText>";

   //@formatter:off
   public static final String END_BIN_DATA =
      "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a" +
      "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy" +
      "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIA" +
      "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA" +
      "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3" +
      "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm" +
      "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA" +
      "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx" +
      "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK" +
      "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3" +
      "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD1jUPE" +
      "2oR+NF8NabpdtcTf2f8Ab2mubxoVC+YY9uFjck5wf886s2t6fYIE1XUNPsrpIFnmie6UCNSQu7Lb" +
      "SV3naGIGTjoeK5HxB4emufiUmsXXhn+29KGji1CYt32zecWztldei55H9761V17wjfal4hvL210V" +
      "I7U+EpbCzjYxAwXLFgsYAbCkIxXI+XBIzivYWGwk1BOSj7t3qt+2r0+5fMzvJXO11/XrTQNMubma" +
      "SFrmO2muILV5gj3HlIXYLnk8DkgHGc1Pouo/2xoOnan5Xk/bLaO48vdu2b1DYzgZxnrivMdQ8E68" +
      "1ra7dJhvDJ4QXSHiedB5FyhVwWzweR8pXPzKMlR8w9H8MWc+neE9GsbqPy7m2sYIZUyDtdY1BGRw" +
      "eQelY4rD4elh04TUpX79PS44tt6mUnifVb3xVrWiaZpFlL/ZXkeZNc37Rb/NTeMKsT9MEdfSp/GH" +
      "i+08J6Je3mIbq8tokm+w/aAkjRtKse/oSFy3XGMjFc3J4cdPH/iXU9T8Hf21Z332X7HJttZNuyLa" +
      "/EsilcnHbnb9KyvG/gnXtSuvGH2HSYb4av8AYJrSUzovktCNjgbuQ+CfQbWb5s/KeylhcFOvTUpJ" +
      "RtBvXdvk5k3zabyb0VrNIlykkzstT8c2OhTa62rrDBaaZ5IieO6SWW5eRC2zyh8yNxxu6jLcAEjp" +
      "oJ4rmCOeCVJYZVDxyRsGV1IyCCOCCO9eZeJfBeuas/xCW2tkA1ZbBrFnlUCYwgF165U5GBuwMkc4" +
      "5r02CRpYI5HheF3UM0UhBZCR907SRkdOCR7muHF0sPClCVJ+87X1/uQe3q5J+a6bFRbvqSUUUV55" +
      "YUUUUAFZV54Y8P6jdPdX2h6Zc3MmN809pG7tgYGSRk8AD8KKKuFScHeDt6Ba5owQRW0EcEESRQxK" +
      "EjjjUKqKBgAAcAAdqkooqG76sAooooAKKKKAP//Z";
   // @formatter:on

   //@formatter:off
   private static final String PIC_TAG_DATA =
      "<w:r><w:pict>" +
      "<v:shapetype id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\" o:preferrelative=\"t\"" +
      " path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\">" + "<v:stroke joinstyle=\"miter\"/>" +
      "<v:formulas><v:f eqn=\"if lineDrawn pixelLineWidth 0\"/><v:f eqn=\"sum @0 1 0\"/>" +
      "<v:f eqn=\"sum 0 0 @1\"/><v:f eqn=\"prod @2 1 2\"/><v:f eqn=\"prod @3 21600 pixelWidth\"/>" +
      "<v:f eqn=\"prod @3 21600 pixelHeight\"/><v:f eqn=\"sum @0 0 1\"/><v:f eqn=\"prod @6 1 2\"/>" +
      "<v:f eqn=\"prod @7 21600 pixelWidth\"/><v:f eqn=\"sum @8 21600 0\"/>" +
      "<v:f eqn=\"prod @7 21600 pixelHeight\"/><v:f eqn=\"sum @10 21600 0\"/></v:formulas>" +
      "<v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/>" +
      "<o:lock v:ext=\"edit\" aspectratio=\"t\"/></v:shapetype>" +
      "<w:binData w:name=\"wordml://%s\">%s</w:binData>" +
      "<v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\">" +
      "<v:imagedata src=\"wordml://%s\" o:title=\"%s\"/></v:shape></w:pict></w:r>";
   // @formatter:on

   // @formatter:off
   public static String START_BIN_DATA =
      "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a" +
      "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy" +
      "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIA" +
      "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA" +
      "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3" +
      "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm" +
      "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA" +
      "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx" +
      "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK" +
      "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3" +
      "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDuI/HV" +
      "3L4l1XTJvEnhmwtrKK1aC5uYSRd+bEHZkzcKNoPTBbhhz3Ol478fWvhjR9TFhcW0us2awt9nmRmQ" +
      "eY4AViMDcU3sF3bsKWxgGix0bxLpfi3Xtahs9JnTVltSYnv5IzC0UW1hkQncMk4PHAHHOBleLPh/" +
      "rWrz+Kv7Nu9PWHXls2b7TvDRtAcbPlBGCAG3c9Nu3ncPfhHBSxEPaWUEovdav3OZOyv/ADPXe2lj" +
      "J81nY0ta8fjw7N4nluxDdw6V9mWC2tYpRKryoSBM5GxVJxhlzgcHLEA9pBMtxBHMgcJIodRIhRgC" +
      "M8qwBB9iARXAa98P9Q1l/G4W7tok11bI2pO4lWgAyHGOASAMjOAc44wen/4S/wAPwfub/XtGtryP" +
      "5Z4P7QjPlyDhlycHg5HIB9hXHiKNGdOH1dXlpe3+GHS383N+vQpN31ObtPGOsap4k1fSrW40azvr" +
      "G+8iHS7+OVJbqEYPmLKG/iUOwxG2BtzkHJ2bfxxp0/iHW9KeG5hGkrF5k0kD4kdyRtUbckk7Ao6y" +
      "FjtBAycbxb4O1jxRJIj2+jRTx3Mb2OswySxXdrErBsbAp3sMvj94qkkHCkVY1Hwjrp1Xxbd6RqcN" +
      "pJrltbiG4ywkt5Yl2bcAH5WXPzghlJ4U4zXTKGCqRV2otpddneN3dJ3VuZ6rmVnvoT7yNh/G3h+L" +
      "TdQv5r14YdOZEu1mtpY5YS+Nm6NlD4bcMHGDz6GoLTxvY33i+Hw9BZ6gsslm90ZZ7OWELhgoG10B" +
      "wfm+Y4XIABJOBx0vww1N9D8WWNpBpOnJqy2KWlvDcSSRwiAjducxgktjOcEkk59a7WXw/dj4jW/i" +
      "SGSF7ZtMbT54nYq6fvPMV1wCGyeCDtx1yelZ1KGAgpcsm3Z21W/LFrZd3Jb20GnNnR0UUV45oFFF" +
      "FABRRRQAUUUUAFFFFABRRRQB/9k=";
   // @formatter:on

   /**
    * The following static numbers are derived from the FULL_PATTERN compiled above
    */
   public final static int beginFeatureMatcherGroup = 1;
   public final static int beginConfigGroupMatcherGroup = 26;
   public final static int beginConfigMatcherGroup = 78;
   public final static int endFeatureMatcherGroup = 12;
   public final static int endConfigGroupMatcherGroup = 53;
   public final static int endConfigMatcherGroup = 100;
   public final static int endFeatureBracketMatcherGroup = 23;
   public final static int endConfigGroupBracketMatcherGroup = 75;
   public final static int endConfigBracketMatcherGroup = 117;

   /**
    * {@link TokenMatcherPattern} used to produce {@link TokenMatcher} objects for finding the "insert thing here"
    * tokens in a publishing template.
    */

   //@formatter:off
   private static final TokenMatcherPattern tokenMatcherPattern =
      TokenMatcherPattern.compile
             (
               '<',                                                                                  /* prefix start char  */
               "<w:p(?:>|\\s)",                                                                      /* prefix start token */
               "<w:p[^>]*>\\s*(?:<w:pPr[^>]*>.*?</w:pPr>\\s*)?<w:r[^>]*>\\s*<w:t[^>]*>\\s*INSERT_",  /* prefix             */
               "INSERT_(ARTIFACT|LINK)_HERE",                                                        /* core token         */
               "\\s*</w:t>\\s*</w:r>\\s*</w:p>"                                                      /* suffix             */
             );
   //@formatter:on

   /**
    * Shorter regular expression used to determine if a publishing template's replacement token is for artifacts or for
    * links.
    */

   //@formatter:off
   private static final Pattern whichInsertHerePattern =
      Pattern.compile
         (
            "INSERT_(ARTIFACT|LINK)_HERE",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE
         );
   //@formatter:off




   public static boolean areApplicabilityTagsInvalid(String wordml, BranchId branch, HashCollection<String, String> validFeatureValues, Set<String> allValidConfigurations, Set<String> allValidConfigurationGroups) {

      Matcher matcher = FULL_PATTERN.matcher(wordml);
      Stack<ApplicabilityBlock> applicabilityBlocks = new Stack<>();
      int applicBlockCount = 0;

      while (matcher.find()) {
         String beginFeature = matcher.group(beginFeatureMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginFeatureMatcherGroup)) : null;
         String beginConfiguration = matcher.group(beginConfigMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginConfigMatcherGroup)) : null;
         String beginConfigurationGroup = matcher.group(beginConfigGroupMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginConfigGroupMatcherGroup)) : null;

         String endFeature = matcher.group(endFeatureMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endFeatureMatcherGroup)) : null;
         String endConfiguration = matcher.group(endConfigMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endConfigMatcherGroup)) : null;
         String endConfigurationGroup = matcher.group(endConfigGroupMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endConfigGroupMatcherGroup)) : null;

         if (beginFeature != null && beginFeature.toLowerCase().contains(FEATUREAPP)) {
            applicBlockCount += 1;
            applicabilityBlocks.add(createApplicabilityBlock(ApplicabilityType.Feature, beginFeature));
         } else if (beginConfiguration != null && beginConfiguration.toLowerCase().contains(CONFIGAPP)) {
            if (isValidConfigurationBracket(beginConfiguration, allValidConfigurations)) {
               applicBlockCount += 1;
               applicabilityBlocks.add(createApplicabilityBlock(ApplicabilityType.Configuration, beginConfiguration));
            }
         } else if (beginConfigurationGroup != null && beginConfigurationGroup.toLowerCase().contains(CONFIGGRPAPP)) {
            if (isValidConfigurationGroupBracket(beginConfigurationGroup, allValidConfigurationGroups)) {
               applicBlockCount += 1;
               applicabilityBlocks.add(
                  createApplicabilityBlock(ApplicabilityType.ConfigurationGroup, beginConfigurationGroup));
            }
         } else if (endFeature != null && endFeature.toLowerCase().contains(FEATUREAPP)) {
            applicBlockCount -= 1;

            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidFeatureBlock(applicabilityBlocks.pop(), matcher, branch, validFeatureValues)) {
               return true;
            }

         } else if (endConfiguration != null && endConfiguration.toLowerCase().contains(CONFIGAPP)) {
            applicBlockCount -= 1;
            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidConfigurationBlock(applicabilityBlocks.pop(), matcher)) {
               return true;
            }
         } else if (endConfigurationGroup != null && endConfigurationGroup.toLowerCase().contains(CONFIGGRPAPP)) {
            applicBlockCount -= 1;
            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidConfigurationGroupBlock(applicabilityBlocks.pop(), matcher)) {
               return true;
            }
         }
      }

      if (applicBlockCount != 0) {
         return true;
      }

      return false;
   }

   private static boolean containsIgnoreCase(Collection<String> validValues, String val) {
      for (String validValue : validValues) {
         if (validValue.equalsIgnoreCase(val)) {
            return true;
         }
      }
      return false;
   }

   public static boolean containsLists(String wordMl) {
      return LIST_PATTERN.matcher(wordMl).find();
   }

   public static boolean containsWordAnnotations(String wordml) {
      return wordml.contains("<w:delText>") || wordml.contains("w:type=\"Word.Insertion\"") || wordml.contains(
         "w:type=\"Word.Formatting\"") || wordml.contains("w:type=\"Word.Deletion\"");
   }

   private static ApplicabilityBlock createApplicabilityBlock(ApplicabilityType applicType, String beginExpression) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock(applicType);
      beginExpression = beginExpression.replace(" [", "[");
      beginApplic.setApplicabilityExpression(beginExpression);
      return beginApplic;
   }

   public static int endIndexOf(String str, String regex) {
      int toReturn = -1;

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(str);
      if (matcher.find()) {
         toReturn = matcher.end();
      }

      return toReturn;
   }


   private static String getEditImage(boolean isStart, String guid) {
      String imageId = String.format("%s_%s", guid, isStart ? "START.jpg" : "END.jpg");
      String imageData = isStart ? START_BIN_DATA : END_BIN_DATA;
      return String.format(PIC_TAG_DATA, imageId, imageData, imageId, guid);
   }

   public static String getEndEditImage(String guid) {
      return WordCoreUtil.getEditImage(false, guid);
   }

   /**
    * Gets the {@link PublishingTemplateInsertTokenType} of the first "insert here token" in the publishing template content.
    *
    * @param templateContent the publishing template to be searched.
    * @return
    * <ul>
    * <li>{@link PublishingTemplateInsertTokenType#ARTIFACT} when the first "insert here token" is for an artifact.</li>
    * <li>{@link PublishingTemplateInsertTokenType#LINK} when the first "insert here token" is for a link.</li>
    * <li>{@link PublishingTemplateInsertTokenType#NONE} when a valid "insert here token" is not found.</li>
    * </ul>
    */

   public static PublishingTemplateInsertTokenType getInsertHereTokenType(String templateContent) {

      var matcher = WordCoreUtil.whichInsertHerePattern.matcher( templateContent );

      if( matcher.find() ) {

         return PublishingTemplateInsertTokenType.parse( matcher.group(1) );
      }

      return PublishingTemplateInsertTokenType.NONE;
   }

   public static String getLinkFormat(LinkType destLinkType) {
      //@formatter:off
      return LinkType.OSEE_SERVER_LINK.equals(destLinkType)
                ? WORDML_LINK_FORMAT
                : WORDML_INTERNAL_DOC_LINK_FORMAT;
      //@formatter:on
   }

   public static String getOseeLinkMarker(String guid) {
      return String.format(OSEE_LINK_MARKER, guid);
   }

   public static String getUnknownArtifactLink(String guid, BranchId branch) {
      //@formatter:off
      var message      = String.format( "Invalid Link: artifact with guid:[%s] on branchUuid:[%s] does not exist", guid, branch );
      var internalLink = String.format( "http://none/%s?guid=%s&amp;branchUuid=%s", "unknown", guid, branch);
      var artifactLink = String.format( WORDML_LINK_FORMAT, internalLink, message );
      return artifactLink;
      //@formatter:on
   }

   public static String getStartEditImage(String guid) {
      return WordCoreUtil.getEditImage(true, guid);
   }

   public static String getWordMlBookmark(Long uuid) {
      return String.format(WORDML_BOOKMARK_FORMAT, 0, uuid, 0);
   }

   public static String getWordMlReference(String linkId, boolean addParagraphNumber, String linkText) {
      String paragraphNumSwitch = addParagraphNumber ? "\\n " : "";
      return String.format(WORDML_INTERNAL_DOC_REFERENCE_FORMAT, linkId, paragraphNumSwitch, linkText);
   }

   public static int indexOf(String str, String regex) {
      int toReturn = -1;

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(str);
      if (matcher.find()) {
         toReturn = matcher.start();
      }

      return toReturn;
   }

   public static boolean isExpressionInvalid(String expression, BranchId branch, HashCollection<String, String> validFeatureValues) {
      ApplicabilityGrammarLexer lex = new ApplicabilityGrammarLexer(new ANTLRStringStream(expression.toUpperCase()));
      ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));

      try {
         parser.start();
      } catch (RecognitionException ex) {
         return true;
      }

      HashMap<String, List<String>> featureIdValuesMap = parser.getIdValuesMap();

      if (featureIdValuesMap.isEmpty()) {
         return true;
      }

      for (String featureId : featureIdValuesMap.keySet()) {
         featureId = featureId.trim();
         if (validFeatureValues.containsKey(featureId.toUpperCase())) {
            List<String> values = featureIdValuesMap.get(featureId);
            if (values.contains("Default")) {
               continue;
            }
            Collection<String> validValues = validFeatureValues.getValues(featureId.toUpperCase());
            for (String val : values) {
               val = val.trim();
               if (val.equals("(") || val.equals(")") || val.equals("|") || val.equals("&")) {
                  continue;
               }
               if (!containsIgnoreCase(validValues, val)) {
                  return true;
               }
            }
         } else {
            return true;
         }
      }

      return false;
   }

   private static boolean isInvalidConfigurationBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher) {
      if (applicabilityBlock.getType() != ApplicabilityType.Configuration) {
         return true;
      }

      return false;
   }

   private static boolean isInvalidConfigurationGroupBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher) {
      if (applicabilityBlock.getType() != ApplicabilityType.ConfigurationGroup) {
         return true;
      }

      return false;
   }

   private static boolean isInvalidFeatureBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher, BranchId branch, HashCollection<String, String> validFeatureValues) {

      if (applicabilityBlock.getType() != ApplicabilityType.Feature) {
         return true;
      }
      String applicabilityExpression = applicabilityBlock.getApplicabilityExpression();

      if (isExpressionInvalid(applicabilityExpression, branch, validFeatureValues)) {
         return true;
      }

      return false;
   }

   private static boolean isValidConfigurationBracket(String beginConfig, Set<String> allValidConfigurations) {
      beginConfig = WordCoreUtil.textOnly(beginConfig);
      int start = beginConfig.indexOf("[") + 1;
      int end = beginConfig.indexOf("]");
      String applicExpText = beginConfig.substring(start, end);

      String[] configs = applicExpText.split("&|\\|");

      for (String config : configs) {
         String configKey = config.split("=")[0].trim().toUpperCase();
         if (!allValidConfigurations.contains(configKey)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isValidConfigurationGroupBracket(String beginConfigGroup, Set<String> allValidConfigurationGroups) {
      beginConfigGroup = WordCoreUtil.textOnly(beginConfigGroup);
      int start = beginConfigGroup.indexOf("[") + 1;
      int end = beginConfigGroup.indexOf("]");
      String applicExpText = beginConfigGroup.substring(start, end);

      String[] configs = applicExpText.split("&|\\|");

      for (String config : configs) {
         String configKey = config.split("=")[0].trim().toUpperCase();
         if (!allValidConfigurationGroups.contains(configKey)) {
            return false;
         }
      }

      return true;
   }

   public static int lastIndexOf(String str, String regex) {
      int toReturn = -1;

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(str);
      while (matcher.find()) {
         toReturn = matcher.start();
      }

      return toReturn;
   }

   /**
    * Applies the <code>segmentProcessor</code> to each section of the <code>templateContent</code> from the start of
    * <code>templateContent</code> or the end of the last section. The <code>tailProcessor</code> is applied to the last
    * section of the <code>templateContent</code>. The <code>templateContent</code> is split into sections using a
    * {@link WordCoreUtil#TokenMatcher} produced by the {@link WordCoreUtil#tokenMatcherPattern}.
    *
    * @param templateContent the publishing template to be processed.
    * @param segmentProcessor a {@link Consumer} used to process sections of the <code>templateContent</code> leading up
    * to an "insert here token".
    * @param tailProcessor a {@link Consumer} used to process the final section of the <code>templateContent</code>.
    */

   public static void processPublishingTemplate(String templateContent, Consumer<String> segmentProcessor, Consumer<String> tailProcessor) {

      var tokenMatcher = WordCoreUtil.tokenMatcherPattern.tokenMatcher(templateContent);
      int lastEndIndex = 0;

      while (tokenMatcher.find()) {

         var tokenStart = tokenMatcher.start();
         var tokenEnd = tokenMatcher.end();

         var segment = templateContent.substring(lastEndIndex, tokenStart);

         lastEndIndex = tokenEnd;
         segmentProcessor.accept(segment);
      }

      var tail = templateContent.substring(lastEndIndex);
      tailProcessor.accept(tail);
   }

   public static String removeAnnotations(String wordml) {
      String response = wordml;
      if (Strings.isValid(response)) {
         response = response.replaceAll(AML_ANNOTATION, "");
         response = response.replaceAll(AML_CONTENT, "");
         response = response.replaceAll(DELETIONS, "");
      }
      return response;
   }

   public static String textOnly(String str) {
      str = paragraphPattern.matcher(str).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return Xml.unescape(str).toString();
   }
}
