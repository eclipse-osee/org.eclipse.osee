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

package org.eclipse.osee.orcs.db.internal.search.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.ReservedCharacters;

/**
 * @author Roberto E. Escobar
 */
public class WordsUtil {

   private static Character[] DEFAULT_PUNCTUACTION = new Character[] {
      '\n',
      '\r',
      ' ',
      '!',
      '"',
      '#',
      '$',
      '%',
      '(',
      ')',
      '*',
      '+',
      ',',
      '-',
      '.',
      '/',
      ':',
      ';',
      '<',
      '>',
      '?',
      '@',
      '[',
      '\\',
      ']',
      '^',
      '{',
      '|',
      '}',
      '~',
      '_',
      '`',
      '\\',
      '=',
      '&'};

   private static char[] PUNCTUATION = null;

   static {
      Set<Character> combined = new HashSet<>();
      combined.addAll(Arrays.asList(DEFAULT_PUNCTUACTION));
      combined.addAll(ReservedCharacters.getChars());
      combined.remove('\'');
      PUNCTUATION = new char[combined.size()];
      int index = 0;
      for (Character character : combined) {
         PUNCTUATION[index] = character;
         index++;
      }
      Arrays.sort(PUNCTUATION);
   }

   public static String[] splitOnPunctuation(String original) {
      List<String> toReturn = new ArrayList<>();
      StringBuffer buffer = new StringBuffer();
      for (int index = 0; index < original.length(); index++) {
         char c = original.charAt(index);
         int pos = Arrays.binarySearch(PUNCTUATION, c);
         if (pos < 0) {
            buffer.append(c);
         } else {
            addToList(buffer.toString(), toReturn);
            buffer.setLength(0);
         }
      }
      addToList(buffer.toString(), toReturn);
      buffer.setLength(0);
      return toReturn.toArray(new String[toReturn.size()]);
   }

   private static void addToList(String toAdd, List<String> list) {
      toAdd = toAdd.trim();
      if (toAdd.length() > 0) {
         toAdd = WordsUtil.removeSingleQuotesFromBeginningAndEnd(toAdd);
         if (toAdd.length() > 0) {
            list.add(toAdd);
         }
      }
   }

   public static boolean isPunctuationOrApostrophe(char character) {
      return Arrays.binarySearch(PUNCTUATION, character) > 0 || character == '\'';
   }

   public static int countPuntuation(String original) {
      int toReturn = 0;
      for (int index = 0; index < original.length(); index++) {
         int pos = Arrays.binarySearch(PUNCTUATION, original.charAt(index));
         if (pos > 0) {
            toReturn++;
         }
      }
      return toReturn;
   }

   public static boolean endsWithPunctuation(String original) {
      boolean toReturn = false;
      int size = original.length();
      if (size > 0) {
         char c = original.charAt(size - 1);
         int pos = Arrays.binarySearch(PUNCTUATION, c);
         if (pos > 0) {
            toReturn = true;
         }
      }
      return toReturn;
   }

   public static String removeSingleQuotesFromBeginningAndEnd(String original) {
      int startAt = 0;
      int stopAt = original.length();
      boolean process = false;
      if (original.startsWith("'")) {
         startAt = 1;
         process = true;
      }
      if (original.endsWith("'")) {
         stopAt = original.length() - 1;
         process = true;
      }
      if (process) {
         if (startAt > stopAt) {
            original = "";
         } else {
            original = original.substring(startAt, stopAt);
         }
      }
      return original;
   }

   public static char[] removeExtraSpacesAndSpecialCharacters(String toSearch, boolean setAllToLowerCase) {
      boolean lastCharacterAddedWasWhiteSpace = false;
      StringBuilder searchString = new StringBuilder();
      for (int index = 0; index < toSearch.length(); index++) {
         char currChar = toSearch.charAt(index);
         if (setAllToLowerCase) {
            currChar = Character.toLowerCase(currChar);
         }
         if (currChar != '\r' && currChar != '\n') {
            if (isPunctuationOrApostrophe(currChar)) {
               currChar = ' ';
            }
            if (Character.isWhitespace(currChar)) {
               if (!lastCharacterAddedWasWhiteSpace) {
                  searchString.append(currChar);
                  lastCharacterAddedWasWhiteSpace = true;
               }
            } else {
               searchString.append(currChar);
               lastCharacterAddedWasWhiteSpace = false;
            }
         }
      }
      return searchString.toString().trim().toCharArray();
   }
}
