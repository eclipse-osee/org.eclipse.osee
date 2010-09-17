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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.message.SearchOptions;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public final class WordOrderMatcher {

   private WordOrderMatcher() {
      // Utility Class
   }

   public static List<MatchLocation> findInStream(InputStream inputStream, String toSearch, SearchOptions options) throws OseeCoreException {
      List<MatchLocation> matchLocations = new ArrayList<MatchLocation>();
      Reader reader = null;
      try {
         reader = new InputStreamReader(inputStream, "UTF-8");
         boolean isCaseInsensitive = !options.isCaseSensitive();
         char[] charsToSearch = WordsUtil.removeExtraSpacesAndSpecialCharacters(toSearch, isCaseInsensitive);
         int charCount = 0;
         int index = 0;
         int value = 0;
         boolean lastCharacterAddedWasWhiteSpace = false;
         boolean currCharValid = false;
         MatchLocation matchLocation = new MatchLocation();
         while (value != -1) {
            value = reader.read();
            charCount++;
            char currChar = (char) value;
            if (isCaseInsensitive) {
               currChar = Character.toLowerCase(currChar);
            }

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
               if (charsToSearch[index] != currChar) {
                  index = 0;
                  matchLocation.reset();
               }

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
                     if (!options.isFindAllLocationsEnabled()) {
                        break;
                     }
                  }
               }
            }
         }
      } catch (UnsupportedEncodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(reader);
      }
      return matchLocations;
   }
}
