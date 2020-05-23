/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.db.internal.search.SearchAsserts;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author John Misinco
 */
@RunWith(Parameterized.class)
public class SecondPassScannerTest {

   private final String input;
   private final List<Pair<String, MatchLocation>> expectedMatches;
   private final QueryOption delimiter;

   public SecondPassScannerTest(String input, List<Pair<String, MatchLocation>> expectedMatches, QueryOption delimiter) {
      this.input = input;
      this.expectedMatches = expectedMatches;
      this.delimiter = delimiter;
   }

   private void testResult(Pair<String, MatchLocation> expected, String next, MatchLocation match) {
      Assert.assertEquals(expected.getFirst(), next);
      SearchAsserts.assertEquals(expected.getSecond(), match);
   }

   @Test
   public void testScanner() {
      SecondPassScanner scanner = new SecondPassScanner(input, delimiter);
      int i = 0;
      while (scanner.hasNext()) {
         String next = scanner.next();
         MatchLocation match = scanner.match();
         testResult(expectedMatches.get(i++), next, match);
      }
      Assert.assertEquals(expectedMatches.size(), i);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<>();

      data.add(new Object[] {
         "What->does] .this. mean",
         getExpected(Arrays.asList("What", "does", "this", "mean"), 0, 4, 6, 10, 13, 17, 19, 23),
         QueryOption.TOKEN_DELIMITER__ANY});
      data.add(new Object[] {
         "Wh\ndo\rth. r",
         getExpected(Arrays.asList("Wh", "do", "th", "r"), 0, 2, 3, 5, 6, 8, 10, 11),
         QueryOption.TOKEN_DELIMITER__ANY});
      data.add(new Object[] {
         "Â ® …tags… the",
         getExpected(Arrays.asList("tags", "the"), 5, 9, 11, 14),
         QueryOption.TOKEN_DELIMITER__ANY});
      data.add(new Object[] {
         "What   does   this mean",
         getExpected(Arrays.asList("What", "does", "this", "mean"), 0, 4, 7, 11, 14, 18, 19, 23),
         QueryOption.TOKEN_DELIMITER__WHITESPACE});
      data.add(new Object[] {
         "at->do]",
         getExpected(Arrays.asList("a", "t", "-", ">", "d", "o", "]"), 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7),
         QueryOption.TOKEN_DELIMITER__EXACT});
      data.add(new Object[] {
         "Â ® …ta]",
         getExpected(Arrays.asList(" ", " ", "t", "a", "]"), 1, 2, 3, 4, 5, 6, 6, 7, 7, 8),
         QueryOption.TOKEN_DELIMITER__EXACT});

      return data;
   }

   private static List<Pair<String, MatchLocation>> getExpected(List<String> tokens, int... locations) {
      List<Pair<String, MatchLocation>> toReturn = new LinkedList<>();
      List<MatchLocation> matchLocs = SearchAsserts.asLocations(locations);
      for (int i = 0; i < tokens.size(); i++) {
         Pair<String, MatchLocation> pair = new Pair<>(tokens.get(i), matchLocs.get(i));
         toReturn.add(pair);
      }
      return toReturn;
   }

}
