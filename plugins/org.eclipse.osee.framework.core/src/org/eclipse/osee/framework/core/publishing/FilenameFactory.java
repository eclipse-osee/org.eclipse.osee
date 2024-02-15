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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
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
    * The character used to separate segments of the filename.
    */

   private static char FILENAME_EXTENSION_SEPARATOR = '.';

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

   private static String FILENAME_SEGMENT_SEPARATOR = "-";

   /**
    * Random number generator to randomize filenames to help prevent collisions between multiple servers.
    */

   private static final Random generator = new Random();

   /**
    * The unsafe characters to be replaced in a filename.
    */

   private static final Pattern WHITESPACE_CLEANER_PATTERN = Pattern.compile("\\s+");

   private static final Pattern FILENNAME_SAFE_CHAR_CONSOLIDATION_PATTERN =
      Pattern.compile(FILENAME_SAFE_CHAR_REPLACEMENT + FILENAME_SAFE_CHAR_REPLACEMENT + "+");
   /**
    * The maximum number of characters allowed in a filename.
    */

   private static final int WINDOWS_FILENAME_LIMIT = 215;

   /**
    * Cleans the <code>name</code> using {@link #makeNameCleaner} and the forms the file name by combining the
    * <code>name</code> and <code>extension</code>.
    * <p>
    * If the first character of the <code>extension</code> is not a '.', a '.' will be appended before the
    * <code>extension</code>. The returned {@link String} will always be non-<code>null</code> and non-blank.
    *
    * @param extension the optional file extension.
    * @param name the name portion of the filename.
    * @return the build filename.
    */

   public static @NonNull String create(@Nullable CharSequence extension, @NonNull CharSequence name) {

      //@formatter:off
      final var safeName =
         Conditions.require
            (
               name,
               Conditions.ValueType.PARAMETER,
               "name",
               "cannot be null or blank",
               Strings::isInvalidOrBlank,
               IllegalArgumentException::new
            );

      final var cleanName = FilenameFactory.makeNameCleaner(safeName);

      final var extensionValid =
            Strings.isValidAndNonBlank( extension )
         && (    ( extension.length()    != 1                                            )
              || ( extension.charAt( 0 ) != FilenameFactory.FILENAME_EXTENSION_SEPARATOR ) );

      final var length =
           cleanName.length()
         + ( extensionValid ? extension.length() : 0 )
         + 1;

      var filename = new StringBuilder( length ).append( cleanName );

      if ( extensionValid ) {

         if( extension.charAt(0) != FilenameFactory.FILENAME_EXTENSION_SEPARATOR )
         {
            filename.append( FilenameFactory.FILENAME_EXTENSION_SEPARATOR );
         }

         filename.append( extension );
      }

      return filename.toString();
      //@formatter:on
   }

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

   public static @NonNull String create(CharSequence extension, CharSequence... segments) {

      //@formatter:off
      var dateSegment = FilenameFactory.getDateSegment();
      var randomSegment = FilenameFactory.getRandomSegment();

      return
         FilenameFactory.create
            (
               dateSegment,
               randomSegment,
               extension,
               segments
            );
      //@formatter:on
   }

   /**
    * Builds a filename as described by {@link #create(CharSequence, CharSequence...)}.
    *
    * @param dateSegment the {@link CharSequence} to be used as the filename date segment.
    * @param randomSegment the {@link CharSequence} to be used as the filename random segment.
    * @param extension the extension for the filename.
    * @param segments an array of {@link CharSequence}s to be used as filename segments.
    * @return the built filename.
    */

   static @NonNull String create(CharSequence dateSegment, CharSequence randomSegment, CharSequence extension,
      CharSequence... segments) {

      //@formatter:off
      var safeDateSegment = FilenameFactory.makeNameSafer(dateSegment);
      safeDateSegment = safeDateSegment.isEmpty() ? FilenameFactory.getDateSegment() : safeDateSegment;
      var safeRandomSegment = FilenameFactory.makeNameSafer(randomSegment);
      safeRandomSegment = safeRandomSegment.isEmpty() ? FilenameFactory.getRandomSegment() : safeRandomSegment;
      var safeExtension = FilenameFactory.makeNameSafer(extension);

      var safeSegments =
         ( Objects.nonNull( segments) && segments.length > 0 )
            ? Arrays.stream( segments )
                 .map( FilenameFactory::makeNameSafer )
                 .filter( Strings::isValidAndNonBlank )
                 .collect( Collectors.joining( FilenameFactory.FILENAME_SEGMENT_SEPARATOR ) )
            : Strings.EMPTY_STRING;

      var name =
         Arrays.stream( new String[] { safeSegments, safeDateSegment, safeRandomSegment } )
            .filter( Strings::isValidAndNonBlank )
            .collect( Collectors.joining( FilenameFactory.FILENAME_SEGMENT_SEPARATOR ) );

      var filename =
         new StringBuilder( name.length() + safeExtension.length() + 1 )
                .append( name );

      if (Strings.isValidAndNonBlank(safeExtension)) {

         if( safeExtension.charAt(0) != FilenameFactory.FILENAME_EXTENSION_SEPARATOR )
         {
            filename.append( FilenameFactory.FILENAME_EXTENSION_SEPARATOR );
         }

         filename.append( safeExtension );
      }

      return filename.toString();
      //@formatter:on
   }

   /**
    * Generates a {@link Map} of filenames from a {@link List} of {@link FilenameSpecification}s. Builds the filenames
    * as described by {@link #create(CharSequence, CharSequence...)}.
    *
    * @param filenameSpecifications
    * @return a {@link Map} of the generated filenames keyed with the keys specified in each
    * <code>filenameSpecifications</code>.
    * @throws NullPointerException when <code>filenameSpecifications</code> is <code>null</code> or contains a
    * <code>null</code> element.
    */

   public static Map<String, String> create(@NonNull List<@NonNull FilenameSpecification> filenameSpecifications) {

      //@formatter:off
      Conditions.require
         (
            filenameSpecifications,
            ValueType.PARAMETER,
            "list of filename specifications",
            "non-null and no null elements",
            Conditions.collectionContainsNull,
            NullPointerException::new
         );
      //@formatter:on

      //@formatter:off
      return
         filenameSpecifications
            .stream()
            .collect
               (
                  Collectors.toMap
                     (
                        FilenameSpecification::getKey,
                        FilenameSpecification::build
                     )
               );
      //@formatter:on
   }

   /**
    * Generates a time date string in the format {@link FilenameFactory#FILENAME_DATE_TIME_FORMAT}. This always returns
    * a non-<code>null</code> and non-blank {@link String}.
    *
    * @return time date string.
    */

   static String getDateSegment() {
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

   static String getRandomSegment() {
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
    * Cleans the file name using {@link @makeNameCleaner} and truncates the length to {@link #FILENAME_SAFE_LENGTH}.
    *
    * @param name the filename to make safe.
    * @return a clean short filename.
    */

   public static @NonNull String makeNameSafer(CharSequence name) {

      //@formatter:off
      final var cleanName = FilenameFactory.makeNameCleaner( name );

      final var shortCleanName =
         (cleanName.length() > FilenameFactory.FILENAME_SAFE_LENGTH )
            ? cleanName.substring(0, FilenameFactory.FILENAME_SAFE_LENGTH )
            : cleanName;
      //@formatter:on

      return shortCleanName;
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
    * Multiple safe characters "-" in a sequence are consolidated into a single safe character.
    * <p>
    * The returned {@link String} will always be non-<code>null</code> but might be empty.
    *
    * @param filename the file name to clean.
    * @return a clean filename.
    */

   public static @NonNull String makeNameCleaner(CharSequence name) {

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

      var consolidatedSafeWhiteSpaceCleanTrimCharacterCleanName =
             Strings.totallySaferReplace
                (
                   whiteSpaceCleanTrimCharacterCleanName,
                   FilenameFactory.FILENNAME_SAFE_CHAR_CONSOLIDATION_PATTERN,
                   FilenameFactory.FILENAME_SAFE_CHAR_REPLACEMENT
                );
      //@formatter:on

      return consolidatedSafeWhiteSpaceCleanTrimCharacterCleanName;
   }

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private FilenameFactory() {
   }

}

/* EOF */
