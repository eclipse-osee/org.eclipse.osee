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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
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

   private static char[] PUNCTUATION =
         new char[] {'\n', '\r', ' ', '!', '"', '#', '$', '%', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<',
               '>', '?', '@', '[', '\\', ']', '^', '{', '|', '}', '~', '_'};

   private static final Properties dictionary;
   static {
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
            String value = buffer.toString().trim();
            if (value.length() > 0) {
               toReturn.add(value);
            }
            buffer.setLength(0);
         }
      }
      if (buffer.length() > 0) {
         toReturn.add(buffer.toString());
         buffer.setLength(0);
      }
      return toReturn.toArray(new String[toReturn.size()]);
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

   public static boolean isWordML(InputStream inputStream) {
      boolean toReturn = false;
      try {
         inputStream.mark(250);
         byte[] buffer = new byte[200];
         int index = 0;
         for (; index < buffer.length; index++) {
            if (inputStream.available() > 0) {
               buffer[index] = (byte) inputStream.read();
            } else {
               break;
            }
         }
         if (index > 0) {
            String header = new String(buffer).toLowerCase();
            if (header.contains("word.document") || header.contains("worddocument") || header.contains("<w:")) {
               toReturn = true;
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         try {
            inputStream.reset();
         } catch (IOException ex) {
            // Do Nothing
         }
      }
      return toReturn;
   }
}
