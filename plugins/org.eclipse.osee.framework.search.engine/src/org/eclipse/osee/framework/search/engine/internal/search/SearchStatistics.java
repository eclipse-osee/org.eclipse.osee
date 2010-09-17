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
package org.eclipse.osee.framework.search.engine.internal.search;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.search.engine.ISearchStatistics;

/**
 * @author Roberto E. Escobar
 */
public class SearchStatistics implements Cloneable, ISearchStatistics {
   public static final SearchStatistics EMPTY_STATS = new EmptyStats();
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

   public void addEntry(SearchRequest searchRequest, int found, long processingTime) throws OseeCoreException {
      Conditions.checkNotNull(searchRequest, "searchRequest");
      this.totalProcessed++;
      this.totalProcessingTime += processingTime;

      this.averageProcessingTime = totalProcessingTime / this.totalProcessed;

      if (processingTime > this.longestProcessingTime) {
         this.longestProcessingTime = processingTime;
         this.longestQuery = String.format("%s - [%d in %d ms]", searchRequest.toString(), found, processingTime);
      }
   }

   @Override
   public SearchStatistics clone() throws CloneNotSupportedException {
      SearchStatistics other = (SearchStatistics) super.clone();
      other.averageProcessingTime = this.averageProcessingTime;
      other.totalProcessed = this.totalProcessed;
      other.totalProcessingTime = this.totalProcessingTime;
      other.longestProcessingTime = this.longestProcessingTime;
      other.longestQuery = this.longestQuery;
      return other;
   }

   private static final class EmptyStats extends SearchStatistics {
      @Override
      public void addEntry(SearchRequest searchRequest, int found, long processingTime) {
         throw new UnsupportedOperationException();
      }
   }
}
