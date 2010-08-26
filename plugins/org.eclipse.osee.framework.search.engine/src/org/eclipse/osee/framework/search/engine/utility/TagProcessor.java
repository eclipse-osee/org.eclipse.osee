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

import java.io.InputStream;
import java.util.Scanner;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.ILanguage;

/**
 * @author Roberto E. Escobar
 */
public class TagProcessor {

   private final ILanguage language;

   public TagProcessor(ILanguage language) {
      this.language = language;
   }

   public void collectFromString(String value, ITagCollector tagCollector) {
      if (Strings.isValid(value)) {
         Scanner scanner = new Scanner(value);
         while (scanner.hasNext()) {
            processWord(scanner.next(), tagCollector);
         }
      }
   }

   public void collectFromInputStream(InputStream inputStream, ITagCollector tagCollector) {
      if (inputStream != null) {
         Scanner scanner = new Scanner(inputStream, "UTF-8");
         while (scanner.hasNext()) {
            processWord(scanner.next(), tagCollector);
         }
      }
   }

   public void collectFromScanner(Scanner sourceScanner, ITagCollector tagCollector) {
      try {
         while (sourceScanner.hasNext()) {
            String entry = sourceScanner.next();
            if (entry.length() > 0) {
               Scanner innerScanner = new Scanner(entry);
               while (innerScanner.hasNext()) {
                  String entry1 = innerScanner.next();
                  processWord(entry1, tagCollector);
               }
            }
         }
      } catch (Exception ex) {
         // Do nothing
      }
   }

   private void processWord(String original, ITagCollector tagCollector) {
      if (Strings.isValid(original) && (original.length() >= 2 || 0 == WordsUtil.countPuntuation(original))) {
         original = original.toLowerCase();
         for (String toEncode : WordsUtil.splitOnPunctuation(original)) {
            if (language.isWord(toEncode)) {
               String target = language.toSingular(toEncode);
               TagEncoder.encode(target, tagCollector);
            }
         }
      }
   }

   public static char[] normalizeWord(String toSearch) {
      boolean lastCharacterAddedWasWhiteSpace = false;
      StringBuilder searchString = new StringBuilder();
      for (int index = 0; index < toSearch.length(); index++) {
         char currChar = toSearch.charAt(index);
         currChar = Character.toLowerCase(currChar);

         if (currChar != '\r' && currChar != '\n') {
            if (WordsUtil.isPunctuationOrApostrophe(currChar)) {
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
