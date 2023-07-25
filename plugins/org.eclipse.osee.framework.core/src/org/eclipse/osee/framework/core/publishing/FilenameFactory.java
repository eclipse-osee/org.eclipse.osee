/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * A factory class for creating publishing filenames.
 *
 * @implSpec All publishing filenames should be created with this class.
 * @implSpec The methods in this class should only work with {@link String} and {@link CharSequence} parameters to
 * remain compatible with both OSEE Client and OSEE Server code.
 * @author Loren K. Ashley
 */

public class FilenameFactory {

   /**
    * The character unsafe characters in a filename are replaced with.
    */

   private static String FILENAME_CLEAN_CHAR_REPLACEMENT = " ";

   /**
    * The unsafe characters to be replaced in a filename.
    */

   private static final Pattern FILENAME_CLEANER_PATTERN = Pattern.compile("[/<>(){}\\[\\].:;\\\"'\\\\|?*+]+");

   /**
    * Filename date/time format
    */

   private static final String FILENAME_DATE_TIME_FORMAT = "yyyyMMdd-HHmmss-SSS";

   /**
    * The character unsafe characters in a filename are replaced with.
    */

   private static String FILENAME_SAFE_CHAR_REPLACEMENT = "-";

   /**
    * The maximum number of characters to be included in a safe name.
    */

   private static final int FILENAME_SAFE_LENGTH = 20;

   /**
    * The character used to separate segments of the filename.
    */

   private static char FILENAME_EXTENSION_SEPARATOR = '.';

   /**
    * The character used to separate segments of the filename.
    */

   private static String FILENAME_SEGMENT_SEPARATOR = "-";

   /**
    * Random number generator to randomize filenames to help prevent collisions between multiple servers.
    */

   private static final Random generator = new Random();

   /**
    * The unsafe characters to be replaced in a filename.
    */

   private static final Pattern WHITESPACE_CLEANER_PATTERN = Pattern.compile("\\s+");

   /**
    * The maximum number of characters allowed in a filename.
    */

   private static final int WINDOWS_FILENAME_LIMIT = 215;

   /**
    * Converts each segment in the <code>segments</code> array into a safe name segment using {@link #makeNameSafer}. A
    * random segment is created using {@link #getRandomSegment} and a date time segment is created using
    * {@link #getDateSegment}. The segments that remain non-<code>null</code> and not empty are joined together as
    * follows:
    *
    * <pre>
    *    { &lt;segmentN&gt; "-" }{0,N-1} &lt;date-segment&gt; "-" &lt;random-segment&gt; { &lt;extension&gt; }
    * </pre>
    *
    * If the first character of the <code>extension</code> is not a '.', a '.' will be appended before the
    * <code>extension</code>. The returned {@link String} will always be non-<code>null</code> and non-blank.
    *
    * @param extension the extension for the filename.
    * @param segments an array of {@link CharSequence}s to be used as filename segments.
    * @return the built filename.
    */

   public static String create(CharSequence extension, CharSequence... segments) {

      //@formatter:off
      var dateSegment = FilenameFactory.getDateSegment();
      var randomSegment = FilenameFactory.getRandomSegment();
      var safeExtension = FilenameFactory.makeNameSafer(extension);

      var name = new StringBuilder( 2 * FilenameFactory.WINDOWS_FILENAME_LIMIT );

      if( Objects.nonNull( segments) && segments.length > 0 ) {

         Arrays.stream( segments )
            .map( FilenameFactory::makeNameSafer )
            .filter( Strings::isValidAndNonBlank )
            .forEach( (segment) -> { name.append( segment ); name.append(FilenameFactory.FILENAME_SEGMENT_SEPARATOR); } );
            ;
      }

      if (Objects.nonNull(dateSegment)) {
         name.append(dateSegment);
         name.append(FilenameFactory.FILENAME_SEGMENT_SEPARATOR);
      }

      if (Objects.nonNull(randomSegment)) {
         name.append(randomSegment);
      }

      if (Strings.isValidAndNonBlank(safeExtension)) {

         if( safeExtension.charAt(0) != FilenameFactory.FILENAME_EXTENSION_SEPARATOR )
         {
            name.append( FilenameFactory.FILENAME_EXTENSION_SEPARATOR );
         }

         name.append( safeExtension );
      }

      return name.toString();
   }

   /**
    * Generates a time date string in the format {@link FilenameFactory#FILENAME_DATE_TIME_FORMAT}. This always returns
    * a non-<code>null</code> and non-blank {@link String}.
    *
    * @return time date string.
    */

   private static String getDateSegment() {
      DateFormat dateFormat = new SimpleDateFormat(FilenameFactory.FILENAME_DATE_TIME_FORMAT);
      var dateSegment = dateFormat.format(new Date());
      return dateSegment;
   }

   /**
    * Generates a random 5 digit 0 padded decimal string. A random segment is added to filename to reduce the changes of
    * two OSEE servers colliding and attempting to write the same file. This always returns a non-<code>null</code> and
    * non-blank {@link String}.
    *
    * @return a 5 digit 0 padded decimal string.
    */

   private static String getRandomSegment() {
      var randomValue = FilenameFactory.generator.nextInt(99999) + 1;
      var randomSegment = String.format("%05d", randomValue);
      return randomSegment;
   }

   /**
    * Predicate to determine if a filename is a <code>null</code> {@link CharSequence} or a non-<code>null</code>
    * {@link CharSequence} with a length less than or equal to the {@link FilenameFactory#WINDOWS_FILENAME_LIMIT}.
    *
    * @param filename the filename to check.
    * @return <code>true</code>, when the <code>filename</code> is <code>null</code> or has a length less than or equal
    * to {@link FilenameFactory#WINDOWS_FILENAME_LIMIT}; otherwise, <code>false</code>.
    */

   public static boolean isInLimit(CharSequence filename) {
      //@formatter:off
      return
         Lib.isWindows()
            ? ( ( Objects.isNull( filename ) ) || ( filename.length() <= FilenameFactory.WINDOWS_FILENAME_LIMIT ) )
            : true;
      //@formatter:on
   }

   /**
    * Replaces the following characters in the name with a " ":
    *
    * <pre>
    *    '/', '<', '>', '(', ')', '{', '}', '[', ']', '.', ':', ';',
    *
    *    '"', ''', '\', '|', '?', '*', '+'
    * </pre>
    *
    * The the string is trimmed by removing all leading and trailing white space.
    * <p>
    * The remaining white space sequences are consolidated and replaced with a "-".
    * <p>
    * The string is truncated to 20 characters in length.
    * <p>
    * The returned {@link String} will always be non-<code>null</code> but might be empty.
    *
    * @param filename the file name to clean and URL encode.
    * @return a clean filename.
    */

   public static String makeNameSafer(CharSequence name) {

      //@formatter:off
      var characterCleanName =
         Strings.totallySaferReplace
            (
               name,
               FilenameFactory.FILENAME_CLEANER_PATTERN,
               FilenameFactory.FILENAME_CLEAN_CHAR_REPLACEMENT
            );

      if (Strings.isInvalidOrBlank(characterCleanName)) {
         return Strings.emptyString();
      }

      var trimCharacterCleanName = characterCleanName.trim();

      var whiteSpaceCleanTrimCharacterCleanName =
            Strings.totallySaferReplace
               (
                  trimCharacterCleanName,
                  FilenameFactory.WHITESPACE_CLEANER_PATTERN,
                  FilenameFactory.FILENAME_SAFE_CHAR_REPLACEMENT
               );

      var shortWhiteSpaceCleanTrimCharacterCleanName =
         (whiteSpaceCleanTrimCharacterCleanName.length() > FilenameFactory.FILENAME_SAFE_LENGTH )
            ? whiteSpaceCleanTrimCharacterCleanName.substring(0, FilenameFactory.FILENAME_SAFE_LENGTH )
            : whiteSpaceCleanTrimCharacterCleanName;
         //@formatter:on

      return shortWhiteSpaceCleanTrimCharacterCleanName;
   }

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private FilenameFactory() {
   }

}

/* EOF */
