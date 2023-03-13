/*********************************************************************
 * Copyright (c) 2023
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

package org.eclipse.osee.framework.jdk.core.util.xml;

import java.io.StringWriter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.CharSequenceWindow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Class of static utility methods for encoding and decoding XML text.
 *
 * @author Loren K. Ashley
 */

public class XmlEncoderDecoder {

   /**
    * Prevents the ampersand in XML predefined entities and XML numeric escapes from being double escaped.
    */

   public static int DONT_DOUBLE_QUOTE = 0x01;

   /**
    * Enables XML numeric escape processing.
    * <p>
    * When converting from XML to Text this flag will enable the conversion of decimal and hexadecimal XML numeric
    * escape sequence to the Unicode character. When converting from Text to XML this flag will enable the conversion of
    * the Unicode characters with a code point greater than 127 into an XML hexadecimal numeric escape.
    */

   public static int ESCAPE_NUMERIC = 0x02;

   /**
    * Enables the removal of characters that are not part of a valid unicode character.
    * <p>
    * When converting text to XML set this flag to enable the removal of characters that are not valid for XML.
    */

   public static int REMOVE_INVALID_CHARS = 0x04;

   /**
    * Enables the removal of XML tags.
    * <p>
    * When converting from XML to Text this flag will enable the removal of XML tags. When an XML tag is removed
    * everything between and including the starting '<' character and the ending '>' character is removed.
    */

   public static int REMOVE_TAGS = 0x08;

   /**
    * {@link Pattern} used to find characters that will be escaped with XML predefined entities.
    */

   public final static Pattern textToXmlPattern = Pattern.compile("[&\'<>\"]");

   /**
    * {@link Pattern} used to find characters that will be escaped for XML with XML predefined entities and numeric
    * escapes.
    */

   public final static Pattern textToXmlEscapeNumericPattern = Pattern.compile("[&\'<>\"\u0000-\u001A\u0080-\u9fff]");

   /**
    * {@link Pattern} used to find character sequences to not be double escaped.
    */

   public final static Pattern textToXmlSkipPattern =
      Pattern.compile("&amp;|&apos;|&lt;|&gt;|&quot;|&#x[A-Fa-f0-9]+;|&#[0-9]+;");

   /**
    * Char array containing the sequence of characters for an XML ampersand predefined entity.
    */

   private static char[] xmlAmp = new char[] {'&', 'a', 'm', 'p', ';'};

   /**
    * Char array containing the sequence of characters for an XML apostrophe predefined entitiy.
    */

   private static char[] xmlApos = new char[] {'&', 'a', 'p', 'o', 's', ';'};

   /**
    * Char array containing the sequence of characters for an XML greater than predefined entity.
    */

   private static char[] xmlGt = new char[] {'&', 'g', 't', ';'};

   /**
    * Char array containing the sequence of characters for an XML less than predefined entity.
    */

   private static char[] xmlLt = new char[] {'&', 'l', 't', ';'};

   /**
    * Char array containing the sequence of characters for an XML quotation mark predefined entity.
    */

   private static char[] xmlQuot = new char[] {'&', 'q', 'u', 'o', 't', ';'};

   /**
    * {@link Pattern} used to find XML predefined entities.
    */

   private static Pattern xmlToTextPattern = Pattern.compile("&amp;|&apos;|&lt;|&gt;|&quot;");

   /**
    * {@link Pattern} used to find XML predefined entities and XML numeric escapes.
    */

   private static Pattern xmlToTextEscapeNumericPattern =
      Pattern.compile("&amp;|&apos;|&lt;|&gt;|&quot;|&#([0-9]+|x[0-9A-Fa-f]+);");

   /**
    * {@link Pattern} used to find XML predefined entities and XML tags.
    */

   private static Pattern xmlToTextRemoveTagsPattern = Pattern.compile("<[^>]+>|&amp;|&apos;|&lt;|&gt;|&quot;");

   /**
    * {@link Pattern} used to find XML predefined entities, XML numeric escapes, and XML tags.
    */

   private static Pattern xmlToTextEscapeNumericRemoveTagsPattern =
      Pattern.compile("<[^>]+>|&amp;|&apos;|&lt;|&gt;|&quot;|&#([0-9]+|x[0-9A-Fa-f]+);");

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private XmlEncoderDecoder() {
   }

   /**
    * Predicate to determine if a code point is valid for XML.
    *
    * @param codePoint the code point to test.
    * @return <code>true</code>, when the code point is valid; otherwise, <code>false</code>.
    */

   public static final boolean isValidCodePoint(int codePoint) {
      //@formatter:off
      return
            ( codePoint == 0x9 )
         || ( codePoint == 0xA )
         || ( codePoint == 0xD )
         || ( ( codePoint >= 0x000020 ) && ( codePoint <= 0x00D7FF ) )
         || ( ( codePoint >= 0x00E000 ) && ( codePoint <= 0x00FFFD ) )
         || ( ( codePoint >= 0x010000 ) && ( codePoint <= 0x10FFFF ) );
      //@formatter:on
   }

   /**
    * This method is functionally equivalent to:
    *
    * <pre>
    *    {@link XmlEncoderDecoder#textToXml(CharSequence, int) XmlEncoderDecoder.textToXml(input,0);}
    * </pre>
    *
    * @param input the {@link CharSequence} to be processed.
    * @return if changes are made, a new {@link CharSequence}; otherwise, the original {@link CharSequence}.
    */

   public static CharSequence textToXml(CharSequence input) {
      return XmlEncoderDecoder.textToXml(input, 0);
   }

   /**
    * Escapes the XML characters '&', ''', '<', '>', and '"' in a {@link CharSequence} using the predefined XML
    * entities. This implementation does not interpret CDATA sequences. The following options are supported:
    * <dl>
    * <dt>{@link XmlEncoderDecoder#DONT_DOUBLE_QUOTE}</dt>
    * <dd>Ampersand characters that start a predefined XML entity character sequence ( "&amp;", "&apos;", "&lt;",
    * "&gt;", "&quot;") or a numerical escape will not be escaped.</dd>
    * <dt>{@link XmlEncoderDecoder#ESCAPE_NUMERIC}</dt>
    * <dd>Characters with a code point value greater than 127 will be encoded as a hexadecimal XML numeric escape
    * sequence.</dd>
    * </dl>
    *
    * @param input the {@link CharSequence} to be processed.
    * @param options a bit mask that may contain any of the supported options.
    * @return if changes are made, a new {@link CharSequence}; otherwise, the original {@link CharSequence}.
    */

   @SuppressWarnings("null")
   public static CharSequence textToXml(CharSequence input, int options) {
      var dontDoubleQuote = (options & XmlEncoderDecoder.DONT_DOUBLE_QUOTE) > 0;
      var escapeNumeric = (options & XmlEncoderDecoder.ESCAPE_NUMERIC) > 0;
      var removeInvalidChars = (options & XmlEncoderDecoder.REMOVE_INVALID_CHARS) > 0;
      var changeSet = new ChangeSet(input);
      int sizePlus = 0;

      for (int i = 0, e = input.length(); i < e; i++) {

         var c = input.charAt(i);

         //@formatter:off
         if(    (    escapeNumeric
                  || removeInvalidChars )
             && (    ( c <  0x20 )
                  || ( c >= 0x80 ) ) )
         //@formatter:on
         {
            var codePoint = Character.codePointAt(input, i);
            var end = i + Character.charCount(codePoint);

            if (XmlEncoderDecoder.isValidCodePoint(codePoint)) {
               if (escapeNumeric) {
                  var hexString = Integer.toHexString(codePoint);
                  changeSet.replace(i, end, "&#x");
                  changeSet.insertBefore(end, hexString);
                  changeSet.insertBefore(end, ";");
                  sizePlus += 4 + hexString.length();
               }
            } else {
               if (removeInvalidChars) {
                  changeSet.delete(i, end);
               }
            }

            i = end - 1;

            continue;
         }

         switch (c) {
            case '&':
               if (dontDoubleQuote) {
                  var tail = new CharSequenceWindow(input, i);
                  var skipMatcher = XmlEncoderDecoder.textToXmlSkipPattern.matcher(tail);
                  if (skipMatcher.lookingAt()) {
                     continue;
                  }
               }
               changeSet.replace(i, i + 1, xmlAmp);
               sizePlus += 4;
               break;

            case '\'':
               changeSet.replace(i, i + 1, xmlApos);
               sizePlus += 5;
               break;

            case '>':
               changeSet.replace(i, i + 1, xmlGt);
               sizePlus += 3;
               break;

            case '<':
               changeSet.replace(i, i + 1, xmlLt);
               sizePlus += 3;
               break;

            case '\"':
               changeSet.replace(i, i + 1, xmlQuot);
               sizePlus += 5;
               break;
         }
      }

      if (!changeSet.hasChanges()) {
         return input;
      }

      try (var stringWriter = new StringWriter(input.length() + sizePlus)) {
         changeSet.applyChanges(stringWriter);
         var result = stringWriter.getBuffer();
         return result;
      } catch (Exception e) {
         return input;
      }

   }

   /**
    * This method is functionally equivalent to:
    *
    * <pre>
    *    {@link XmlEncoderDecoder#xmlToText(CharSequence, int) XmlEncoderDecoder.xmlToText(input,0);}
    * </pre>
    *
    * @param input the {@link CharSequence} to be processed.
    * @return if changes are made, a new {@link CharSequence}; otherwise, the original {@link CharSequence}.
    */

   public static CharSequence xmlToText(CharSequence input) {
      return XmlEncoderDecoder.xmlToText(input, 0);
   }

   /**
    * Removes all XML tags and un-escapes the predefined entity references, and numerically escaped characters. Declared
    * entity references are not un-escaped. This implementation does not interpret CDATA sequences. The following
    * options are supported:
    * <dl>
    * <dt>{@link XmlEncoderDecoder#ESCAPE_NUMERIC}</dt>
    * <dd>Characters with a code point value greater than 127 will be encoded as a hexadecimal XML numeric escape
    * sequence.</dd>
    * <dt>{@link XmlEncoderDecoder#REMOVE_TAGS}</dt>
    * <dd>When this option is specified, any XML tags (character sequences starting with '&lt;' and ending with '&gt;'
    * will be removed from the output.</dd>
    * </dl>
    *
    * @param input the {@link CharSequence} to be processed.
    * @return if changes are made, a new {@link CharSequence}; otherwise, the original {@link CharSequence}.
    */

   @SuppressWarnings("null")
   public static CharSequence xmlToText(CharSequence input, int options) {

      //@formatter:off
      var patternOption =   ( ( options & XmlEncoderDecoder.ESCAPE_NUMERIC ) > 0 ? 1 : 0 )
                          + ( ( options & XmlEncoderDecoder.REMOVE_TAGS    ) > 0 ? 2 : 0 );

      ChangeSet changeSet = null;
      Matcher matcher = null;

      switch( patternOption ) {
         case 0:
            matcher = XmlEncoderDecoder.xmlToTextPattern.matcher(input);
            break;
         case 1:
            matcher = XmlEncoderDecoder.xmlToTextEscapeNumericPattern.matcher(input);
            break;
         case 2:
            matcher = XmlEncoderDecoder.xmlToTextRemoveTagsPattern.matcher(input);
            break;
         case 3:
            matcher = XmlEncoderDecoder.xmlToTextEscapeNumericRemoveTagsPattern.matcher(input);
            break;
      }

      while (matcher.find()) {
         //changeSet is also used as a flag to determine if any changes were made
         changeSet = Objects.isNull(changeSet) ? new ChangeSet(input) : changeSet;

         var group = matcher.group();

         if (group.charAt(0) == '<') {
            //delete the XML tag
            changeSet.delete(matcher.start(), matcher.end());
         } else {
            //replace the escaped XML character with just the character
            switch (group.charAt(1)) {
               case 'a': //case: &amp; or &apos;
                  //formatter:off
                  changeSet.replace(matcher.start(), matcher.end(), group.charAt(2) == 'm' ? '&' : '\'');
                  //@formatter:on
                  break;

               case 'l': //case: &lt;
                  changeSet.replace(matcher.start(), matcher.end(), '<');
                  break;

               case 'g': //case &gt;
                  changeSet.replace(matcher.start(), matcher.end(), '>');
                  break;

               case 'q': //case &quot;
                  changeSet.replace(matcher.start(), matcher.end(), '"');
                  break;

               case '#': //case &#NN;
                  var numberCode = new CharSequenceWindow(input, matcher.start(1), matcher.end(1));
                  var firstChar = numberCode.charAt(0);
                  //@formatter:off
                  var codePoint = ( firstChar == 'x' )
                              ? Integer.parseUnsignedInt( numberCode, 1, numberCode.length(), 16 )
                              : Integer.parseUnsignedInt( numberCode, 0, numberCode.length(), 10 );
                  //@formatter:on
                  var chars = Character.toChars(codePoint);
                  changeSet.replace(matcher.start(), matcher.end(), chars);
                  break;
            }
         }
      }

      if (Objects.isNull(changeSet)) {
         //no changes were made
         return input;
      }

      try (var stringWriter = new StringWriter(input.length())) {
         //build the new text into a CharSequence backed by a StringBuilder
         changeSet.applyChanges(stringWriter);
         var result = stringWriter.getBuffer();
         return result;
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                       "Xml::xmlToText, failed to write changes to StringWriter."
                   );
         //@formatter:on
      }

   }

}

/* EOF */