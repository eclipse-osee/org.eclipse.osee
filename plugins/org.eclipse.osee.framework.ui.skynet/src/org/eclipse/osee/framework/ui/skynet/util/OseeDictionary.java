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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.swt.styledText.IDictionary;

/**
 * Dictionary provided by OSEE that includes all dictionarys through the OseeDictionary extension point.
 * 
 * @author Donald G. Dunne
 */
public final class OseeDictionary {

   private static final IDictionary instance = new DictionaryImpl();

   public static IDictionary getInstance() {
      return instance;
   }

   private OseeDictionary() {
      // Singleton - should not be instantiated outside of this class
   }

   private static final class DictionaryImpl implements IDictionary {

      /**
       * Remove any junky characters and check for acronyms and other known non-word type stuff. Return valid word to
       * check in dictionary OR "" if there is no word in this string eg now) = now
       * 
       * <pre>
       * a..b = ""
       * SQA = ""
       * NEon = ""
       * </pre>
       */
      private static final Pattern pattern = Pattern.compile("^[a-zA-Z]{1}[a-z]+$");

      private final ExtensionDefinedObjects<IOseeDictionary> contributions =
         new ExtensionDefinedObjects<>("org.eclipse.osee.framework.ui.skynet.OseeDictionary",
            "OseeDictionary", "classname");

      public Iterable<IOseeDictionary> getDictionaries() {
         return contributions.getObjects();
      }

      @Override
      public boolean isWord(String word) {
         String cleanWord = getCleanWord(word);
         if (cleanWord.equals("") || cleanWord.length() == 1) {
            return true;
         }

         for (IOseeDictionary dict : getDictionaries()) {
            if (dict.isWord(cleanWord)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public String getCleanWord(String word) {
         String cleanWord = word;
         // Single character is a valid word
         if (cleanWord.length() == 1) {
            return cleanWord;
         }

         // First, remove any non-word characters before and after string
         // eg. end. (now) it!
         cleanWord = cleanWord.replaceAll("^\\W+", "");
         cleanWord = cleanWord.replaceAll("\\W+$", "");
         cleanWord = cleanWord.replaceAll("'s$", ""); // Get rid of 's at end of word

         // If any non-alphabetic characters still in string, not a word
         // If string not either all lowercase or first letter capitalized, not a
         // word
         Matcher matcher = pattern.matcher(cleanWord);
         if (!matcher.find()) {
            return "";
         }
         return cleanWord.toLowerCase();
      }
   }
}
