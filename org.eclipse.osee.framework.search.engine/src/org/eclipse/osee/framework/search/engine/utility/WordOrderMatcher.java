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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.search.engine.MatchLocation;
import org.eclipse.osee.framework.search.engine.Options;

/**
 * @author Roberto E. Escobar
 */
public class WordOrderMatcher {

   private WordOrderMatcher() {
   }

   public static List<MatchLocation> findInStream(InputStream inputStream, String toSearch, Options options) throws IOException {
      List<MatchLocation> matchLocations = new ArrayList<MatchLocation>();
      Reader reader = null;
      try {
         reader = new InputStreamReader(inputStream, "UTF-8");

         char[] charsToSearch = removeExtraSpacesAndSpecialCharacters(toSearch);
         int charCount = 0;
         int index = 0;
         int value = 0;
         boolean lastCharacterAddedWasWhiteSpace = false;
         boolean currCharValid = false;
         MatchLocation matchLocation = new MatchLocation();
         while (value != -1) {
            value = reader.read();
            charCount++;
            char currChar = Character.toLowerCase((char) value);
            if (currChar != '\r' && currChar != '\n') {
               if (WordsUtil.isPunctuationOrApostrophe(currChar)) {
                  currChar = ' ';
               }

               if (Character.isWhitespace(currChar)) {
                  if (!lastCharacterAddedWasWhiteSpace) {
                     currCharValid = true;
                     lastCharacterAddedWasWhiteSpace = true;
                  } else {
                     currCharValid = false;
                  }
               } else {
                  currCharValid = true;
                  lastCharacterAddedWasWhiteSpace = false;
               }
            }

            if (currCharValid) {
               if (charsToSearch[index] == currChar) {
                  if (index == 0) {
                     matchLocation.setStartPosition(charCount);
                  }

                  if (index + 1 < charsToSearch.length) {
                     index++;
                  } else {
                     matchLocation.setEndPosition(charCount);
                     matchLocations.add(matchLocation.clone());
                     index = 0;
                     if (!options.getBoolean("find all locations")) {
                        break;
                     }
                  }
               } else {
                  index = 0;
                  matchLocation.reset();
               }
            }
         }
      } finally {
         if (reader != null) {
            reader.close();
         }
      }
      return matchLocations;
   }

   private static char[] removeExtraSpacesAndSpecialCharacters(String toSearch) {
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
