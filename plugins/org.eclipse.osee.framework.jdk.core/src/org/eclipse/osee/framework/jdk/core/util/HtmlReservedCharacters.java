/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <pre>
 *    Character Entity Name   Description
 *       &quot;   &amp;quot;    quotation mark
 *       '        &amp;apos; apostrophe 
 *       &amp;    &amp;amp;     ampersand
 *       &lt;     &amp;lt;      less-than
 *       &gt;     &amp;gt;      greater-than
 * 
 *       ISO 8859-1 Symbols
 *       Character  Entity Name    Description
 *            &amp;nbsp;   non-breaking space
 *       ¡    &amp;iexcl   inverted exclamation mark
 *       ¢    &amp;cent    cent
 *       £    &amp;pound   pound
 *       ¤    &amp;curren  currency
 *       ¥    &amp;yen     yen
 *       ¦    &amp;brvbar  broken vertical bar
 *       §    &amp;sect    section
 *       ¨    &amp;uml     spacing diaeresis
 *       ©    &amp;copy    copyright
 *       ª    &amp;ordf    feminine ordinal indicator
 *       «    &amp;laquo   angle quotation mark (left)
 *       ¬    &amp;not     negation
 *       ­    &amp;shy     soft hyphen
 *       ®    &amp;reg     registered trademark
 *       ¯    &amp;macr    spacing macron
 *       °    &amp;deg     degree
 *       ±    &amp;plusmn  plus-or-minus 
 *       ²    &amp;sup2    superscript 2
 *       ³    &amp;sup3    superscript 3
 *       ´    &amp;acute   spacing acute
 *       µ    &amp;micro   micro
 *       ¶    &amp;para    paragraph
 *       ·    &amp;middot  middle dot
 *       ¸    &amp;cedil   spacing cedilla
 *       ¹    &amp;sup1    superscript 1
 *       º    &amp;ordm    masculine ordinal indicator
 *       »    &amp;raquo   angle quotation mark (right)
 *       ¼    &amp;frac14  fraction 1/4
 *       ½    &amp;frac12  fraction 1/2
 *       ¾    &amp;frac34  fraction 3/4
 *       ¿    &amp;iquest  inverted question mark
 *       ×    &amp;times   multiplication
 *       ÷    &amp;divide  division
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
public class HtmlReservedCharacters {

   private static Map<String, Character> reservedCharacters = new HashMap<String, Character>();
   private static Map<Character, String> charsToEncoding = new HashMap<Character, String>();
   static {
      reservedCharacters.put("&quot;", '"');
      reservedCharacters.put("&apos;", '\'');
      reservedCharacters.put("&amp;", '&');
      reservedCharacters.put("&lt;", '<');
      reservedCharacters.put("&gt;", '>');
      reservedCharacters.put("&nbsp;", ' ');
      reservedCharacters.put("&iexcl;", '¡');
      reservedCharacters.put("&cent;", '¢');
      reservedCharacters.put("&pound;", '£');
      reservedCharacters.put("&curren;", '¤');
      reservedCharacters.put("&yen;", '¥');
      reservedCharacters.put("&brvbar;", '¦');
      reservedCharacters.put("&sect;", '§');
      reservedCharacters.put("&uml;", '¨');
      reservedCharacters.put("&copy;", '©');
      reservedCharacters.put("&ordf;", 'ª');
      reservedCharacters.put("&laquo;", '«');
      reservedCharacters.put("&not;", '¬');
      reservedCharacters.put("&shy;", '­');
      reservedCharacters.put("&reg;", '®');
      reservedCharacters.put("&macr;", '¯');
      reservedCharacters.put("&deg;", '°');
      reservedCharacters.put("&plusmn;", '±');
      reservedCharacters.put("&sup2;", '²');
      reservedCharacters.put("&sup3;", '³');
      reservedCharacters.put("&acute;", '´');
      reservedCharacters.put("&micro;", 'µ');
      reservedCharacters.put("&para;", '¶');
      reservedCharacters.put("&middot;", '·');
      reservedCharacters.put("&cedil;", '¸');
      reservedCharacters.put("&sup1;", '¹');
      reservedCharacters.put("&ordm;", 'º');
      reservedCharacters.put("&raquo;", '»');
      reservedCharacters.put("&frac14;", '¼');
      reservedCharacters.put("&frac12;", '½');
      reservedCharacters.put("&frac34;", '¾');
      reservedCharacters.put("&iquest;", '¿');
      reservedCharacters.put("&times;", '×');
      reservedCharacters.put("&divide;", '÷');

      for (Entry<String, Character> entry : reservedCharacters.entrySet()) {
         charsToEncoding.put(entry.getValue(), entry.getKey());
      }
   }

   private HtmlReservedCharacters() {

   }

   public static String encode(String original) {
      StringBuilder encodedItem = new StringBuilder();
      for (int index = 0; index < original.length(); index++) {
         char item = original.charAt(index);
         String encode = charsToEncoding.get(item);
         if (encode != null) {
            encodedItem.append(encode);
         } else {
            encodedItem.append(item);
         }
      }
      return encodedItem.toString();
   }

   public static Character toCharacter(String word) {
      Character toReturn = null;
      if (Strings.isValid(word)) {
         word = word.trim();
         toReturn = reservedCharacters.get(word);
      }
      return toReturn;
   }

   public static Collection<Character> getChars() {
      return reservedCharacters.values();
   }
}
