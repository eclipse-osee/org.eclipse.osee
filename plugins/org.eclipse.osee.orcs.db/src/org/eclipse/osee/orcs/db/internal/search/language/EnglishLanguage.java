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

package org.eclipse.osee.orcs.db.internal.search.language;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.search.tagger.Language;

/**
 * @author Roberto E. Escobar
 */
public class EnglishLanguage implements Language {

   private static final String VOWELS = "aeiou";
   private static final String IES_ENDING = "ies";
   private static final String OES_ENDING = "oes";
   private static final String ES_ENDING = "es";
   private static final String S_ENDING = "s";
   private static final String VES_ENDING = "ves";
   private static final String[] SPECIAL_ES_ENDING_CASES = new String[] {"ss", "sh", "ch", "x"};
   private static final Collection<String> wordsToSkip = Arrays.asList("ies", "s", "oes", "es");

   private final Properties dictionary = new Properties();
   private boolean isInitialized = false;
   private final Log logger;

   public EnglishLanguage(Log logger) {
      super();
      this.logger = logger;
   }

   private boolean hasConstantBeforeEnding(String word, String ending) {
      if (!word.equals(ending)) {
         String remainder = word.substring(word.length() - (ending.length() + 1));
         return VOWELS.indexOf(remainder, 1) < 0;
      } else {
         return false;
      }
   }

   private boolean hasEitherSequenceBeforeEnding(String word, String ending, String... sequences) {
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

   private String stripPossesive(String original) {
      String toReturn = original;
      if (original != null && original.length() > 0) {
         if (original.lastIndexOf('\'') == original.length() - 1) {
            toReturn = replaceEndingWith(original, "'", "");
         } else if (original.endsWith("'s")) {
            toReturn = replaceEndingWith(original, "'s", "");
         }
      }
      return toReturn;
   }

   @Override
   public boolean isWord(String rawText) {
      return !wordsToSkip.contains(rawText);
   }

   @Override
   public String toSingular(String rawText) {
      String word = stripPossesive(rawText);
      word = word.toLowerCase();
      String toReturn = getSingular(word);
      if (toReturn == null) {
         if (word.endsWith(IES_ENDING) && hasConstantBeforeEnding(word, IES_ENDING)) {
            toReturn = replaceEndingWith(word, IES_ENDING, "y");
         } else if (word.endsWith(OES_ENDING) && hasConstantBeforeEnding(word, OES_ENDING)) {
            toReturn = replaceEndingWith(word, OES_ENDING, "o");
         } else if (word.endsWith(ES_ENDING) && hasConstantBeforeEnding(word, ES_ENDING)) {
            String replaceWith = "e";
            String ending = ES_ENDING;
            if (hasEitherSequenceBeforeEnding(word, ES_ENDING, SPECIAL_ES_ENDING_CASES)) {
               replaceWith = Strings.emptyString();
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

   public String getSingular(String word) {
      ensureIntialized();
      return dictionary.getProperty(word);
   }

   private synchronized void ensureIntialized() {
      if (!isInitialized) {
         isInitialized = true;
         InputStream inputStream = null;
         try {
            URL url = this.getClass().getResource("pluralToSingularExceptions.xml");
            inputStream = new BufferedInputStream(url.openStream());
            dictionary.loadFromXML(inputStream);
         } catch (Exception ex) {
            logger.error(ex, "Unable to process plural to singular exceptions file.");
         } finally {
            Lib.close(inputStream);
         }
      }
   }
}
