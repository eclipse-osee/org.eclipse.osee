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
package org.eclipse.osee.framework.search.engine.test.internal.search;

import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.search.engine.internal.search.SearchStatistics;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link SearchStatistics}
 * 
 * @author Roberto E. Escobar
 */
public class SearchStatisticsTest {

   private static SearchRequest searchRequest1;
   private static SearchRequest searchRequest2;
   private static SearchRequest searchRequest3;

   @BeforeClass
   public static void setup() {
      searchRequest1 = new SearchRequest(CoreBranches.COMMON, "[hello]");
      searchRequest2 = new SearchRequest(CoreBranches.SYSTEM_ROOT, "short search");
      searchRequest3 = new SearchRequest(CoreBranches.SYSTEM_ROOT, "long search");
   }

   @Test
   public void testEmptyStats() {
      checkStat(SearchStatistics.EMPTY_STATS, 0, 0, 0, "");
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEmptyStatsAdd() throws OseeCoreException {
      SearchStatistics.EMPTY_STATS.addEntry(null, -1, -1);
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddNullSearchRequest() throws OseeCoreException {
      SearchStatistics actual = new SearchStatistics();
      actual.addEntry(null, 0, 0);
   }

   @Test
   public void testStatistics() throws OseeCoreException {
      SearchStatistics actual = new SearchStatistics();
      checkStat(actual, 0, 0, 0, "");

      actual.addEntry(searchRequest1, 3, 500);
      checkStat(actual, 500, 500, 1, searchRequest1.toString() + " - [3 in 500 ms]");

      actual.addEntry(searchRequest2, 10, 499);
      long expectedAverage = (500 + 499) / 2;
      checkStat(actual, expectedAverage, 500, 2, searchRequest1.toString() + " - [3 in 500 ms]");

      actual.addEntry(searchRequest3, 20000, 501);
      expectedAverage = (500 + 499 + 501) / 3;
      checkStat(actual, expectedAverage, 501, 3, searchRequest3.toString() + " - [20000 in 501 ms]");

      actual.clear();
      checkStat(actual, 0, 0, 0, "");
   }

   @Test
   public void testClear() throws OseeCoreException {
      SearchStatistics actual = new SearchStatistics();
      checkStat(actual, 0, 0, 0, "");
      actual.addEntry(searchRequest1, 7, 111111);

      checkStat(actual, 111111, 111111, 1, searchRequest1.toString() + " - [7 in 111111 ms]");
      actual.clear();
      checkStat(actual, 0, 0, 0, "");
   }

   @Test
   public void testClone() throws OseeCoreException, CloneNotSupportedException {
      SearchStatistics actual = new SearchStatistics();
      checkStat(actual, 0, 0, 0, "");
      actual.addEntry(searchRequest1, 7, 111111);
      checkStat(actual, 111111, 111111, 1, searchRequest1.toString() + " - [7 in 111111 ms]");

      SearchStatistics copy = actual.clone();
      Assert.assertTrue(!actual.equals(copy));

      actual.clear();
      checkStat(actual, 0, 0, 0, "");
      checkStat(copy, 111111, 111111, 1, searchRequest1.toString() + " - [7 in 111111 ms]");

   }

   private static void checkStat(SearchStatistics actual, long average, long longest, int total, String longestData) {
      Assert.assertEquals(average, actual.getAverageSearchTime());
      Assert.assertEquals(longest, actual.getLongestSearchTime());
      Assert.assertEquals(total, actual.getTotalSearches());
      Assert.assertEquals(longestData, actual.getLongestSearch());
   }
}
