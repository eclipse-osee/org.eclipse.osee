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

import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.statistics.QueryStatistics;

/**
 * @author Roberto E. Escobar
 */
public class QueryStatisticsImpl implements Cloneable, QueryStatistics {
   public static final QueryStatisticsImpl EMPTY_STATS = new EmptyStats();
   private static final String EMPTY_STRING = "";
   private long averageProcessingTime;
   private int totalProcessed;
   private long totalProcessingTime;
   private long longestProcessingTime;
   private String longestQuery;

   public QueryStatisticsImpl() {
      clear();
   }

   public void clear() {
      this.averageProcessingTime = 0;
      this.totalProcessed = 0;
      this.totalProcessingTime = 0;
      this.longestProcessingTime = 0;
      this.longestQuery = EMPTY_STRING;
   }

   @Override
   public long getAverageSearchTime() {
      return averageProcessingTime;
   }

   @Override
   public int getTotalSearches() {
      return totalProcessed;
   }

   @Override
   public long getLongestSearchTime() {
      return longestProcessingTime;
   }

   @Override
   public String getLongestSearch() {
      return longestQuery;
   }

   public void addEntry(QueryData query, int found, long processingTime)  {
      Conditions.checkNotNull(query, "query");
      this.totalProcessed++;
      this.totalProcessingTime += processingTime;

      this.averageProcessingTime = totalProcessingTime / this.totalProcessed;

      if (processingTime > this.longestProcessingTime) {
         this.longestProcessingTime = processingTime;
         this.longestQuery = toString(found, processingTime, query);
      }
   }

   private String toString(int found, long processingTime, QueryData data) {
      StringBuilder builder = new StringBuilder();
      builder.append("Query:\n");
      builder.append("\tFound: [");
      builder.append(found);
      builder.append(" item(s) in ");
      builder.append(Lib.asTimeString(processingTime));
      builder.append("]\n");
      builder.append("\tDetails:\n\t\t");
      builder.append(data.getOptions());
      builder.append("\n\t\t");
      builder.append(data.getCriteriaSets());
      builder.append("\n");
      return builder.toString();
   }

   @Override
   public QueryStatisticsImpl clone() {
      QueryStatisticsImpl other = new QueryStatisticsImpl();
      other.averageProcessingTime = this.averageProcessingTime;
      other.totalProcessed = this.totalProcessed;
      other.totalProcessingTime = this.totalProcessingTime;
      other.longestProcessingTime = this.longestProcessingTime;
      other.longestQuery = this.longestQuery;
      return other;
   }

   private static final class EmptyStats extends QueryStatisticsImpl {
      @Override
      public void addEntry(QueryData query, int found, long processingTime) {
         throw new UnsupportedOperationException();
      }
   }
}
