/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
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
public final class ReservedCharacters {

   private static Map<String, Character> reservedCharacters = new HashMap<>();
   private static Map<Character, String> charsToEncoding = new HashMap<>();
   private static Map<Character, String> xmlEntitiesMap = new HashMap<>();

   static {
      try {
         loadReservedChars();
      } catch (Exception ex) {
         throw new IllegalStateException(ex);
      }
      xmlEntitiesMap.put('"', "&quot;");
      xmlEntitiesMap.put('\'', "&apos;");
      xmlEntitiesMap.put('&', "&amp;");
      xmlEntitiesMap.put('<', "&lt;");
      xmlEntitiesMap.put('>', "&gt;");

      for (Entry<String, Character> entry : reservedCharacters.entrySet()) {
         charsToEncoding.put(entry.getValue(), entry.getKey());
      }
      for (Entry<Character, String> entry : xmlEntitiesMap.entrySet()) {
         charsToEncoding.put(entry.getKey(), entry.getValue());
         reservedCharacters.put(entry.getValue(), entry.getKey());
      }
   }

   private static void loadReservedChars() throws UnsupportedEncodingException, IOException {
      BufferedReader reader = null;
      try {
         URL url = ReservedCharacters.class.getResource("ReservedCharacters.txt");
         reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
         String line = null;
         while ((line = reader.readLine()) != null) {
            String[] items = line.split(",\\s*");
            String key = items[0];
            String value = items[1];
            Character charValue = value.charAt(1);
            reservedCharacters.put(key, charValue);
         }
      } finally {
         Lib.close(reader);
      }
   }

   private ReservedCharacters() {
      // Utility class
   }

   public static String encode(String original) {
      return encode(original, charsToEncoding);
   }

   public static String encodeXmlEntities(String original) {
      return encode(original, xmlEntitiesMap);
   }

   private static String encode(String original, Map<Character, String> charactersToEntityMap) {
      StringBuilder encodedItem = new StringBuilder();
      for (int index = 0; index < original.length(); index++) {
         char item = original.charAt(index);
         String encode = charactersToEntityMap.get(item);
         if (encode != null) {
            encodedItem.append(encode);
         } else {
            encodedItem.append(item);
         }
      }
      return encodedItem.toString();
   }

   public static Character toCharacter(String entity) {
      Character character = null;
      if (Strings.isValid(entity)) {
         entity = entity.trim();
         character = reservedCharacters.get(entity);
      }
      return character;
   }

   public static Collection<Character> getChars() {
      return reservedCharacters.values();
   }
}
