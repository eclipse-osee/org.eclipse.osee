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
package org.eclipse.osee.framework.search.engine.internal;

import org.eclipse.osee.framework.search.engine.ISearchStatistics;
import org.eclipse.osee.framework.search.engine.SearchOptions;

/**
 * @author Roberto E. Escobar
 */
public class SearchStatistics implements Cloneable, ISearchStatistics {
   public static final SearchStatistics EMPTY_STATS = new SearchStatistics();
   private static final String EMPTY_STRING = "";
   private long averageProcessingTime;
   private int totalProcessed;
   private long totalProcessingTime;
   private long longestProcessingTime;
   private String longestQuery;

   public SearchStatistics() {
      clear();
   }

   public void clear() {
      this.averageProcessingTime = 0;
      this.totalProcessed = 0;
      this.totalProcessingTime = 0;
      this.longestProcessingTime = 0;
      this.longestQuery = EMPTY_STRING;
   }

   public long getAverageSearchTime() {
      return averageProcessingTime;
   }

   public int getTotalSearches() {
      return totalProcessed;
   }

   public long getLongestSearchTime() {
      return longestProcessingTime;
   }

   public String getLongestSearch() {
      return longestQuery;
   }

   public void addEntry(String queryString, int branchId, SearchOptions options, int found, long processingTime) {
      this.totalProcessed++;
      this.totalProcessingTime += processingTime;
      this.averageProcessingTime = totalProcessingTime / this.totalProcessed;

      if (processingTime > this.longestProcessingTime) {
         this.longestQuery =
               String.format("Query:[%s] BranchId:[%d] Options:[%s] Found:[%d in %d ms]", queryString, branchId,
                     options.toString(), found, processingTime);
      }
   }

   /* (non-Javadoc)
    * @see java.lang.Object#clone()
    */
   @Override
   protected SearchStatistics clone() throws CloneNotSupportedException {
      SearchStatistics other = (SearchStatistics) super.clone();
      other.averageProcessingTime = this.averageProcessingTime;
      other.totalProcessed = this.totalProcessed;
      other.totalProcessingTime = this.totalProcessingTime;
      other.longestProcessingTime = this.longestProcessingTime;
      other.longestQuery = this.longestQuery;
      return other;
   }
}
