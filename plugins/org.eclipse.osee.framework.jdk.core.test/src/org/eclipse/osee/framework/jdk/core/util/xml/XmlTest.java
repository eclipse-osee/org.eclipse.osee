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

package org.eclipse.osee.framework.jdk.core.util.xml;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * JUnit tests for the {@link Xml} utilities class.
 *
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class XmlTest {

   @Parameters
   public static Collection<Object[]> data() {
      //@formatter:off
      return
         List.of
            (
               (Object[]) new Function[] { ( input ) -> new String( (String) input ) },
               (Object[]) new Function[] { ( input ) -> new StringBuilder().append( (String) input ) },
               (Object[]) new Function[] { ( input ) -> new StringBuffer().append( (String) input ) }
            );
      //@formatter:on
   }

   Function<String, CharSequence> testCharSequenceFunction;

   public XmlTest(Function<String, CharSequence> testCharSequenceFunction) {
      this.testCharSequenceFunction = testCharSequenceFunction;
   }

   private static void assertEquals(CharSequence e, CharSequence r) {
      if (CharSequence.compare(e, r) != 0) {
         //@formatter:off
         throw
            new AssertionError
                   (
                      new Message()
                             .title( "CharSequences are not lexicographically equals." )
                             .indentInc()
                             .segment( "Expected", e )
                             .segment( "Actual", r)
                             .toString()
                   );
      }
   }

   @Test
   public void xmlToText1() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc &amp; &#x25F8;def <taga b=\"sam\">&lt;ghi&gt; &jkl;</taga> &quot;mno&apos;"
/*                  ^     ^           ^               ^      ^    ^    ^       ^        ^
 *                  |     |           |               |      |    |    |       |        |
 *                  |     |           |               |      |    |    |       |        +- Decode to '''
 *                  |     |           +- Pass through |      |    |    |       +- Decode to '"'
 *                  |     |                           |      |    |    +- Pass through
 *                  |     +- Pass through             |      |    +- Pass through
 *                  |                                 |      +- Decode to '>'
 *                  +- Decode to '&"                  +- Decode to '<'
 */
            );
      //@formatter:on

      var expected = "abc & &#x25F8;def <taga b=\"sam\"><ghi> &jkl;</taga> \"mno\'";
      var result = XmlEncoderDecoder.xmlToText(input);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToText1RemoveTags() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc &amp; &#x25F8;def <taga b=\"sam\">&lt;ghi&gt; &jkl;</taga> &quot;mno&apos;"
/*                  ^     ^           ^               ^      ^    ^    ^       ^        ^
 *                  |     |           |               |      |    |    |       |        |
 *                  |     |           |               |      |    |    |       |        +- Decode to '''
 *                  |     |           +- Remove tag   |      |    |    |       +- Decode to '"'
 *                  |     |                           |      |    |    +- Remove tag
 *                  |     +- Pass through             |      |    +- Pass through
 *                  |                                 |      +- Decode to '>'
 *                  +- Decode to '&"                  +- Decode to '<'
 */
            );
      //@formatter:on

      var expected = "abc & &#x25F8;def <ghi> &jkl; \"mno\'";
      var result = XmlEncoderDecoder.xmlToText(input, XmlEncoderDecoder.REMOVE_TAGS);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToText1EscapeNumericRemoveTags() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc &amp; &#x25F8;def <taga b=\"sam\">&lt;ghi&gt; &jkl;</taga> &quot;mno&apos;"
/*                  ^     ^           ^               ^      ^    ^    ^       ^        ^
 *                  |     |           |               |      |    |    |       |        |
 *                  |     |           |               |      |    |    |       |        +- Decode to '''
 *                  |     |           +- Remove tag   |      |    |    |       +- Decode to '"'
 *                  |     |                           |      |    |    +- Remove tag
 *                  |     +- Decode to unicode        |      |    +- Pass through
 *                  |                                 |      +- Decode to '>'
 *                  +- Decode to '&"                  +- Decode to '<'
 */
            );
      //@formatter:on

      var expected = "abc & \u25F8def <ghi> &jkl; \"mno\'";
      var result = XmlEncoderDecoder.xmlToText(input, XmlEncoderDecoder.ESCAPE_NUMERIC | XmlEncoderDecoder.REMOVE_TAGS);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToText2() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc&#32;def"
/*                 ^
 *                 |
 *                 +- Pass through
 */
            );
      //@formatter:off

      var expected = "abc&#32;def";
      var result = XmlEncoderDecoder.xmlToText(input);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToText2EscapeNumeric() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc&#32;def"
/*                 ^
 *                 |
 *                 +- Decode to unicode space
 */
            );
      //@formatter:on

      var expected = "abc def";
      var result = XmlEncoderDecoder.xmlToText(input, XmlEncoderDecoder.ESCAPE_NUMERIC);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToText3() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc&#x20;def"
/*                 ^
 *                 |
 *                 +- Pass through
 */
            );
      //@formatter:on

      var expected = "abc&#x20;def";
      var result = XmlEncoderDecoder.xmlToText(input);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToText3EscapeNumeric() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc&#x20;def"
/*                 ^
 *                 |
 *                 +- Decode to unicode space
 */
            );
      //@formatter:on

      var expected = "abc def";
      var result = XmlEncoderDecoder.xmlToText(input, XmlEncoderDecoder.ESCAPE_NUMERIC);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToText4() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc&#X20;def"
/*                 ^
 *                 |
 *                 +- Pass through
 */
            );
      //@formatter:on

      var expected = "abc&#X20;def";
      var result = XmlEncoderDecoder.xmlToText(input);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToText4EscapeNumeric() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc&#X20;def"
/*                 ^
 *                 |
 *                 +- Pass through, cap 'X' not valid sequence
 */
            );
      //@formatter:on

      var expected = "abc&#X20;def";
      var result = XmlEncoderDecoder.xmlToText(input, XmlEncoderDecoder.ESCAPE_NUMERIC);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToTextUnicodeUpperLeftTriangle() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc&#x25F8;def"
/*                 ^
 *                 |
 *                 +- Decode to unicode
 */
            );
      //@formatter:on

      var expected = "abc\u25F8def";
      var result = XmlEncoderDecoder.xmlToText(input, XmlEncoderDecoder.ESCAPE_NUMERIC);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToTextChars() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "&amp;&apos;&lt;&gt;&quot;"
/*              ^    ^     ^   ^   ^
 *              |    |     |   |   |
 *              |    |     |   |   +- Decode to '"'
 *              |    |     |   +- Decode to '>'
 *              |    |     +- Decode to '<'
 *              |    +- Decode to '''
 *              +- Decode to '&'
 */
            );
      //@formatter:on

      var expected = "&\'<>\"";
      var result = XmlEncoderDecoder.xmlToText(input);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToTextTagAtStart() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "<Z:r s=\"&amp;abc\" \n\r>abc"
/*              ^
 *              |
 *              +- Remove tag with attribute
 */
            );
      //@formatter:on

      var expected = "abc";
      var result = XmlEncoderDecoder.xmlToText(input, XmlEncoderDecoder.REMOVE_TAGS);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToTextTagAtEnd() {

      //@formatter:on
      var input = this.testCharSequenceFunction.apply("abc</Z:r s=\"&amp;abc\" \n\r>"
      /*
       * ^ | +- Remove tag with attribute
       */
      );
      //@formatter:on

      var expected = "abc";
      var result = XmlEncoderDecoder.xmlToText(input, XmlEncoderDecoder.REMOVE_TAGS);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void xmlToTextNoChange() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc & def ghi &jkl; \"mno\' <pqr>"
/*                  ^         ^      ^    ^ ^   ^
 *                  |         |      |    | |   |
 *                  +---------+------+----+-+---+- Pass through
 */
            );
      //@formatter:on

      var result = XmlEncoderDecoder.xmlToText(input);
      Assert.assertTrue(result == input);
   }

   @Test
   public void textToXml1() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc & def < ghi > \"jkl\'"
/*                  ^     ^     ^  ^    ^
 *                  |     |     |  |    |
 *                  |     |     |  |    +- Encode to &apos;
 *                  |     |     |  +- Encode to &quot;
 *                  |     |     +- Encode to &gt;
 *                  |     +- Encode to &lt;
 *                  +- Encode &amp;
 */
            );
      //@formatter:on

      var expected = "abc &amp; def &lt; ghi &gt; &quot;jkl&apos;";
      var result = XmlEncoderDecoder.textToXml(input);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void textToXmlDontDoubleQuote() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc &quot;&&quot; def <&lt; ghi &gt;> \"jkl &amp; mno\'"
/*                  ^     ^^          ^^        ^   ^  ^    ^         ^
 *                  |     ||          ||        |   |  |    |         |
 *                  |     ||          ||        |   |  |    |         +- Encode to &apos;
 *                  |     ||          ||        |   |  |    +- Don't double quote
 *                  |     ||          ||        |   |  +- Encode to &quot;
 *                  |     ||          ||        |   +- Encode to &gt;
 *                  |     ||          ||        +- Don't double quote
 *                  |     ||          |+- Don't double quote
 *                  |     ||          +- Encode to &lt;
 *                  |     |+- Don't double quote
 *                  |     +- Encode to &amp;
 *                  +- Don't double quote
 */
            );
      //@formatter:on

      var expected = "abc &quot;&amp;&quot; def &lt;&lt; ghi &gt;&gt; &quot;jkl &amp; mno&apos;";
      var result = XmlEncoderDecoder.textToXml(input, XmlEncoderDecoder.DONT_DOUBLE_QUOTE);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void textToXmlDoubleQuote() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc &quot;&&quot; def <&lt; ghi &gt;> \"jkl &amp; mno\'"
/*                  ^     ^^          ^^        ^   ^  ^    ^         ^
 *                  |     ||          ||        |   |  |    |         |
 *                  |     ||          ||        |   |  |    |         +- Encode to &apos;
 *                  |     ||          ||        |   |  |    +- Encode to &amp;
 *                  |     ||          ||        |   |  +- Encode to &quot;
 *                  |     ||          ||        |   +- Encode to &gt;
 *                  |     ||          ||        +- Encode to &amp;
 *                  |     ||          |+- Encode to &amp;
 *                  |     ||          +- Encode to &lt;
 *                  |     |+- Encode to &amp;
 *                  |     +- Encode to &amp;
 *                  +- Encode to &amp;
 */
         );
      //@formatter:on

      var expected = "abc &amp;quot;&amp;&amp;quot; def &lt;&amp;lt; ghi &amp;gt;&gt; &quot;jkl &amp;amp; mno&apos;";
      var result = XmlEncoderDecoder.textToXml(input);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void textToXmlUpperLeftTriangleEscapeNumeric() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc◸def"
/*                 ^
 *                 |
 *                 +- Encode to &#x25f8;
 */
            );
      //@formatter:on

      var expected = "abc&#x25f8;def";
      var result = XmlEncoderDecoder.textToXml(input, XmlEncoderDecoder.ESCAPE_NUMERIC);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void textToXmlUpperLeftTriangle() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc◸def"
/*                 ^
 *                 |
 *                 +- Pass through
 */
         );
      //@formatter:on

      var expected = "abc◸def";
      var result = XmlEncoderDecoder.textToXml(input);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void textToXmlRemoveInvalidChars() {
      //can't encode an invalid 2 char codepoint

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "A\u0007B\ud800C\udfffD\ufffeE"
/*               ^      ^      ^      ^
 *               |      |      |      |
 *               +------+------+------+- drop invalid characters
 */
            );
      //@formatter:on

      var expected = "ABCDE";
      var result = XmlEncoderDecoder.textToXml(input, XmlEncoderDecoder.REMOVE_INVALID_CHARS);
      XmlTest.assertEquals(expected, result);
   }

   @Test
   public void textToXmlNewLines() {

      //@formatter:off
      var input =
         this.testCharSequenceFunction.apply
            (
               "abc\n\ndef\nghi"
            );
      //@formatter:on

      var expected = "abc\n\ndef\nghi";
      var result = XmlEncoderDecoder.textToXml(input);
      XmlTest.assertEquals(expected, result);
   }

}

/* EOF */
