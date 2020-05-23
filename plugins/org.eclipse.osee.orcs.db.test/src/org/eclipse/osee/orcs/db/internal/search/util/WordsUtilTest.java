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
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link WordsUtil}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class WordsUtilTest {

   private final String toAnalyze;
   private final int numberOfPunctuations;
   private final boolean endsWithPunctuation;
   private final String[] splitOnPunctuations;

   public WordsUtilTest(String toAnalyze, int numberOfPunctuations, boolean endsWithPunctuation, String[] splitOnPunctuations) {
      super();
      this.toAnalyze = toAnalyze;
      this.numberOfPunctuations = numberOfPunctuations;
      this.endsWithPunctuation = endsWithPunctuation;
      this.splitOnPunctuations = splitOnPunctuations;
   }

   @Test
   public void testCountPunctuation() {
      Assert.assertEquals(String.format("testing:[%s]", toAnalyze), numberOfPunctuations,
         WordsUtil.countPuntuation(toAnalyze));
   }

   @Test
   public void testEndsWithPunctuation() {
      Assert.assertEquals(endsWithPunctuation, WordsUtil.endsWithPunctuation(toAnalyze));
   }

   @Test
   public void testSplitOnPunctuation() {
      String[] actual = WordsUtil.splitOnPunctuation(toAnalyze);
      Assert.assertEquals(String.format("Original: [%s] ", toAnalyze), Arrays.deepToString(splitOnPunctuations),
         Arrays.deepToString(actual));
   }

   @Test
   public void testIsPunctuationOrApostrophe() {
      for (char character : toAnalyze.toCharArray()) {
         boolean actual = WordsUtil.isPunctuationOrApostrophe(character);
         boolean expected = !Character.isLetter(character);
         Assert.assertEquals(expected, actual);
      }
   }

   @Test
   public void testRemoveSingleQuotes() {
      String actual = WordsUtil.removeSingleQuotesFromBeginningAndEnd(toAnalyze);
      StringBuilder expected = new StringBuilder();
      int index = 0;
      for (char character : toAnalyze.toCharArray()) {
         boolean addAllowed = true;
         if (character == '\'') {
            if (index == 0 || index == toAnalyze.length() - 1) {
               addAllowed = false;
            }
         }
         if (addAllowed) {
            expected.append(character);
         }
         index++;
      }

      Assert.assertEquals(expected.toString(), actual);
   }

   @Test
   public void testRemoveExtraSpacesAndSpecialCharacters() {
      char[] actualLower = WordsUtil.removeExtraSpacesAndSpecialCharacters(toAnalyze, true);
      char[] actual = WordsUtil.removeExtraSpacesAndSpecialCharacters(toAnalyze, false);

      StringBuilder builder = new StringBuilder();
      boolean wasLastASpace = true;
      for (char character : toAnalyze.toCharArray()) {
         if (Character.isLetterOrDigit(character)) {
            wasLastASpace = false;
            builder.append(character);
         } else {
            if (!wasLastASpace) {
               wasLastASpace = true;
               builder.append(' ');
            }
         }
      }
      String expected = builder.toString().trim();
      Assert.assertEquals(expected, new String(actual));
      Assert.assertEquals(expected.toLowerCase(), new String(actualLower));
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<>();
      //***WORD***
      String dataWithSpecial = getSpecial(169, 174, 87, 79, 82, 68, 96, 8220, 34);
      data.add(new Object[] {dataWithSpecial, 5, true, new String[] {"WORD"}});

      data.add(new Object[] {"test.db.preset", 2, false, new String[] {"test", "db", "preset"}});
      data.add(new Object[] {"{what is this}", 4, true, new String[] {"what", "is", "this"}});
      data.add(new Object[] {"What is your name?", 4, true, new String[] {"What", "is", "your", "name"}});
      data.add(new Object[] {"Run!!", 2, true, new String[] {"Run"}});
      data.add(new Object[] {"don't", 0, false, new String[] {"don't"}});
      data.add(new Object[] {"hel@lo", 1, false, new String[] {"hel", "lo"}});
      data.add(new Object[] {"'why'", 0, false, new String[] {"why"}});
      data.add(new Object[] {"'start", 0, false, new String[] {"start"}});
      data.add(new Object[] {"end'", 0, false, new String[] {"end"}});
      return data;
   }

   private static String getSpecial(int... charValues) {
      StringBuilder toReturn = new StringBuilder();
      for (int charValue : charValues) {
         toReturn.append((char) charValue);
      }
      return toReturn.toString();//
   }
}
