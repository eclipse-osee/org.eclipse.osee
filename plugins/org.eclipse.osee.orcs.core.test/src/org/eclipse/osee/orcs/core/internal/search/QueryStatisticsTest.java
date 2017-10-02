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
package org.eclipse.osee.orcs.core.internal.search;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.statistics.QueryStatistics;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link QueryStatisticsImpl}
 *
 * @author Roberto E. Escobar
 */
public class QueryStatisticsTest {

   private static QueryData searchRequest1;
   private static QueryData searchRequest2;
   private static QueryData searchRequest3;

   @BeforeClass
   public static void setup() {
      searchRequest1 = createQueryData("[hello]");
      searchRequest2 = createQueryData("short search");
      searchRequest3 = createQueryData("long search");
   }

   private static QueryData createQueryData(String value) {
      QueryData queryData = new QueryData(new CriteriaSet(), OptionsUtil.createOptions());
      Collection<AttributeTypeId> types = Collections.singleton(CoreAttributeTypes.Name);
      queryData.addCriteria(new CriteriaAttributeKeywords(false, types, null, value, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, QueryOption.CASE__MATCH));
      return queryData;
   }

   @Test
   public void testEmptyStats() {
      checkStat(QueryStatisticsImpl.EMPTY_STATS, 0, 0, 0, "");
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEmptyStatsAdd()  {
      QueryStatisticsImpl.EMPTY_STATS.addEntry(null, -1, -1);
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddNullSearchRequest()  {
      QueryStatisticsImpl actual = new QueryStatisticsImpl();
      actual.addEntry(null, 0, 0);
   }

   @Test
   public void testStatistics()  {
      QueryStatisticsImpl actual = new QueryStatisticsImpl();
      checkStat(actual, 0, 0, 0, "");

      actual.addEntry(searchRequest1, 3, 500);
      checkStat(actual, 500, 500, 1, asData(3, 500, searchRequest1));

      actual.addEntry(searchRequest2, 10, 499);
      long expectedAverage = (500 + 499) / 2;
      checkStat(actual, expectedAverage, 500, 2, asData(3, 500, searchRequest1));

      actual.addEntry(searchRequest3, 20000, 501);
      expectedAverage = (500 + 499 + 501) / 3;
      checkStat(actual, expectedAverage, 501, 3, asData(20000, 501, searchRequest3));

      actual.clear();
      checkStat(actual, 0, 0, 0, "");
   }

   @Test
   public void testClear()  {
      QueryStatisticsImpl actual = new QueryStatisticsImpl();
      checkStat(actual, 0, 0, 0, "");
      actual.addEntry(searchRequest1, 7, 111111);

      checkStat(actual, 111111, 111111, 1, asData(7, 111111, searchRequest1));
      actual.clear();
      checkStat(actual, 0, 0, 0, "");
   }

   @Test
   public void testClone()  {
      QueryStatisticsImpl actual = new QueryStatisticsImpl();
      checkStat(actual, 0, 0, 0, "");
      actual.addEntry(searchRequest1, 7, 111111);
      checkStat(actual, 111111, 111111, 1, asData(7, 111111, searchRequest1));

      QueryStatistics copy = actual.clone();
      Assert.assertTrue(!actual.equals(copy));

      actual.clear();
      checkStat(actual, 0, 0, 0, "");
      checkStat(copy, 111111, 111111, 1, asData(7, 111111, searchRequest1));
   }

   private static String asData(int found, long time, QueryData data) {
      StringBuilder builder = new StringBuilder();
      builder.append("Query:\n");
      builder.append("\tFound: [");
      builder.append(found);
      builder.append(" item(s) in ");
      builder.append(Lib.asTimeString(time));
      builder.append("]\n");
      builder.append("\tDetails:\n\t\t");
      builder.append(data.getOptions());
      builder.append("\n\t\t");
      builder.append(data.getCriteriaSets());
      builder.append("\n");
      return builder.toString();
   }

   private static void checkStat(QueryStatistics actual, long average, long longest, int total, String longestData) {
      Assert.assertEquals(average, actual.getAverageSearchTime());
      Assert.assertEquals(longest, actual.getLongestSearchTime());
      Assert.assertEquals(total, actual.getTotalSearches());
      Assert.assertEquals(longestData, actual.getLongestSearch());
   }
}
