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
package org.eclipse.osee.framework.search.engine.utility;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.HtmlReservedCharacters;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class WordsUtil {

   private static final String VOWELS = "aeiou";
   private static final String IES_ENDING = "ies";
   private static final String OES_ENDING = "oes";
   private static final String ES_ENDING = "es";
   private static final String S_ENDING = "s";
   private static final String VES_ENDING = "ves";
   public static final String EMPTY_STRING = "";
   private static final String[] SPECIAL_ES_ENDING_CASES = new String[] {"ss", "sh", "ch", "x"};

   private static Character[] DEFAULT_PUNCTUACTION =
         new Character[] {'\n', '\r', ' ', '!', '"', '#', '$', '%', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';',
               '<', '>', '?', '@', '[', '\\', ']', '^', '{', '|', '}', '~', '_', '`', '\\', '=', '&'};

   private static char[] PUNCTUATION = null;

   private static final Properties dictionary;
   static {
      Set<Character> combined = new HashSet<Character>();
      combined.addAll(Arrays.asList(DEFAULT_PUNCTUACTION));
      combined.addAll(HtmlReservedCharacters.getChars());
      combined.remove('\'');
      PUNCTUATION = new char[combined.size()];
      int index = 0;
      for (Character character : combined) {
         PUNCTUATION[index] = character;
         index++;
      }
      Arrays.sort(PUNCTUATION);
      dictionary = new Properties();
      try {
         URL url = Activator.getResource("/support/pluralToSingularExceptions.xml");
         dictionary.loadFromXML(url.openStream());
      } catch (Exception ex) {
         OseeLog.log(TagProcessor.class, Level.SEVERE, "Unable to process plural to singular exceptions file.", ex);
      }
   }

   private static boolean hasConstantBeforeEnding(String word, String ending) {
      if (!word.equals(ending)) {
         String remainder = word.substring(word.length() - (ending.length() + 1));
         return VOWELS.indexOf(remainder, 1) < 0;
      } else {
         return false;
      }
   }

   private static boolean hasEitherSequenceBeforeEnding(String word, String ending, String... sequences) {
      boolean toReturn = false;
      String remainder = word.substring(0, word.length() - ending.length());
      for (String sequence : sequences) {
         toReturn |= remainder.endsWith(sequence);
      }
      return toReturn;
   }

   private static String replaceEndingWith(String word, String ending, String replaceWith) {
      return word.substring(0, word.length() - ending.length()) + replaceWith;
   }

   public static String stripPossesive(String original) {
      String toReturn = original;
      if (original != null && original.length() > 0) {
         if (original.lastIndexOf('\'') == (original.length() - 1)) {
            toReturn = replaceEndingWith(original, "'", "");
         } else if (original.endsWith("'s")) {
            toReturn = replaceEndingWith(original, "'s", "");
         }
      }
      return toReturn;
   }

   public static String[] splitOnPunctuation(String original) {
      List<String> toReturn = new ArrayList<String>();
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
         toAdd = WordsUtil.toRemoveSingleQuotes(toAdd);
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

   public static String toSingular(String word) {
      word = word.toLowerCase();
      String toReturn = dictionary.getProperty(word);
      if (toReturn == null) {
         if (word.endsWith(IES_ENDING) && hasConstantBeforeEnding(word, IES_ENDING)) {
            toReturn = replaceEndingWith(word, IES_ENDING, "y");
         } else if (word.endsWith(OES_ENDING) && hasConstantBeforeEnding(word, OES_ENDING)) {
            toReturn = replaceEndingWith(word, OES_ENDING, "o");
         } else if (word.endsWith(ES_ENDING) && hasConstantBeforeEnding(word, ES_ENDING)) {
            String replaceWith = "e";
            String ending = ES_ENDING;
            if (hasEitherSequenceBeforeEnding(word, ES_ENDING, SPECIAL_ES_ENDING_CASES)) {
               replaceWith = EMPTY_STRING;
            } else if (hasEitherSequenceBeforeEnding(word, ES_ENDING, "v")) {
               ending = VES_ENDING;
               replaceWith = "f";
            }
            toReturn = replaceEndingWith(word, ending, replaceWith);
         } else if (word.endsWith(S_ENDING)) {
            toReturn = word.substring(0, word.length() - 1);
         } else {
            toReturn = word;
         }
      }
      return toReturn;
   }

   public static String toRemoveSingleQuotes(String original) {
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
}
