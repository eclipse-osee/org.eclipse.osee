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
package org.eclipse.osee.ote.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

/**
 * @author Roberto E. Escobar
 */
public class XmlSupport {

   private static final Matcher xmlPatternMatcher =
         Pattern.compile("[^" + "a-zA-Z0-9" + "!@#$%\\^&*\\(\\)" + "+ _.-=" + "\'\"<>{}\\[\\]|:;,\n\r\t?/`~\\\\]+").matcher(
               "");
   private static final String CDATA_TEMPLATE = "<![CDATA[%s]]>";
   private static final String HEX_START = " 0x";
   private static final String CDATA_END = "]]>";

   // Prevent Instantiation
   private XmlSupport() {
   }

   public static String format(String value) {
      return XmlSupport.isValidCharaterData(value) ? value : asCDATA(value);
   }

   public static String asCDATA(String value) {
      if (!isValidCDATA(value)) {
         ChangeSet changeSet = null;
         xmlPatternMatcher.reset(value);
         while (xmlPatternMatcher.find()) {
            char[] charToConvert = xmlPatternMatcher.group().toCharArray();
            StringBuilder converted = new StringBuilder();
            for (int index = 0; index < charToConvert.length; index++) {
               converted.append(HEX_START);
               converted.append(Integer.toString((int) charToConvert[index], 16));
            }
            if (changeSet == null) {
               changeSet = new ChangeSet(value);
            }
            changeSet.replace(xmlPatternMatcher.start(), xmlPatternMatcher.end(), converted.toString());
         }
         if (changeSet != null) {
            value = changeSet.applyChangesToSelf().toString();
         }
      }
      return String.format(CDATA_TEMPLATE, value);
   }

   public static String sanitizeXMLContent(String str) {
      return str.replace((char) 0x1a, ' ');
   }

   private static boolean isValidCDATA(String text) {
      return isValidCharaterData(text) && !text.contains(CDATA_END);
   }

   public static boolean isValidCharaterData(String text) {
      if (text != null) {
         int size = text.length();
         for (int index = 0; index < size; index++) {
            int character = text.charAt(index);
            if (isSurrogatePair(character)) {
               index++;
               if (index < size) {
                  // Check the lower part of the surrogate pair
                  char lowerPart = text.charAt(index);
                  if (isValidLowerSurrogate(lowerPart)) {
                     character = toInt(character, lowerPart);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            }
            if (!isHTMLCharacter(character)) {
               return false;
            }
         }
         return true;
      }
      return false;
   }

   private static boolean isSurrogatePair(int character) {
      return 0xD800 <= character && character <= 0xDBFF;
   }

   private static boolean isValidLowerSurrogate(char toCheck) {
      return 0xDC00 <= toCheck && toCheck <= 0xDFFF;
   }

   private static int toInt(int higher, char lower) {
      return 0x10000 + (higher - 0xD800) * 0x400 + (lower - 0xDC00);
   }

   private static boolean isHTMLCharacter(int c) {
      if (c == '\n' || c == '\r' || c == '\t') return true;
      if (c >= 0x20 && c < 0x7F) return true;
      return false;
   }
}
