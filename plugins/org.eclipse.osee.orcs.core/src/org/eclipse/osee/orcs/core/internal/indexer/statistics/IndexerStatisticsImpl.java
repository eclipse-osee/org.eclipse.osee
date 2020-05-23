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

package org.eclipse.osee.orcs.core.internal.indexer.statistics;

import org.eclipse.osee.orcs.core.ds.IndexerData;
import org.eclipse.osee.orcs.statistics.IndexerItemStatistics;
import org.eclipse.osee.orcs.statistics.IndexerStatistics;

/**
 * @author Roberto E. Escobar
 */
public class IndexerStatisticsImpl implements Cloneable, IndexerStatistics {
   public static final IndexerStatisticsImpl EMPTY_STATS = new IndexerStatisticsImpl();
   private static final IndexerItemStatisticsImpl DEFAULT_TASK_STATS = new IndexerItemStatisticsImpl(-1, -1, -1);

   private long averageQueryIdWaitTime;
   private long averageAttributeProcessingTime;
   private long averageQueryIdProcessingTime;
   private long totalTags;
   private int totalAttributesProcessed;
   private int totalQueryIdsProcessed;
   private long totalQueryIdWaitTime;
   private long totalQueryIdProcessingTime;
   private long totalAttributeProcessingTime;
   private long longestQueryIdWaitTime;
   private long longestQueryIdProcessingTime;
   private IndexerItemStatisticsImpl longestTask;
   private IndexerItemStatisticsImpl mostTags;
   private IndexerData indexerData;

   public IndexerStatisticsImpl() {
      clear();
   }

   public void clear() {
      this.averageQueryIdWaitTime = 0;
      this.totalTags = 0;
      this.averageAttributeProcessingTime = 0;
      this.averageQueryIdProcessingTime = 0;
      this.totalAttributesProcessed = 0;
      this.totalQueryIdsProcessed = 0;
      this.totalQueryIdWaitTime = 0;
      this.totalAttributeProcessingTime = 0;
      this.totalQueryIdProcessingTime = 0;
      this.longestQueryIdWaitTime = 0;
      this.longestQueryIdProcessingTime = 0;
      this.longestTask = DEFAULT_TASK_STATS;
      this.mostTags = DEFAULT_TASK_STATS;
   }

   @Override
   public long getLongestQueryIdWaitTime() {
      return longestQueryIdWaitTime;
   }

   @Override
   public long getLongestQueryIdProcessingTime() {
      return longestQueryIdProcessingTime;
   }

   @Override
   public long getAverageQueryIdWaitTime() {
      return averageQueryIdWaitTime;
   }

   @Override
   public int getTotalQueryIdsProcessed() {
      return this.totalQueryIdsProcessed;
   }

   @Override
   public long getAverageQueryIdProcessingTime() {
      return averageQueryIdProcessingTime;
   }

   @Override
   public long getAverageAttributeProcessingTime() {
      return averageAttributeProcessingTime;
   }

   @Override
   public long getTotalTags() {
      return totalTags;
   }

   @Override
   public int getTotalAttributesProcessed() {
      return totalAttributesProcessed;
   }

   @Override
   public long getLongestAttributeProcessingTime() {
      return longestTask.getProcessingTime();
   }

   @Override
   public IndexerItemStatistics getLongestTask() {
      return longestTask;
   }

   @Override
   public IndexerItemStatistics getMostTagsTask() {
      return mostTags;
   }

   @Override
   public int getWorkersInQueue() {
      return indexerData != null ? indexerData.getWorkersInQueue() : -1;
   }

   @Override
   public long getTagsInSystem() {
      return indexerData != null ? indexerData.getTotalTags() : -1;
   }

   @Override
   public long getTotalQueryIdsInQueue() {
      return indexerData != null ? indexerData.getTotalItemsInQueue() : -1;
   }

   @Override
   protected IndexerStatistics clone() {
      IndexerStatisticsImpl other = new IndexerStatisticsImpl();
      other.averageAttributeProcessingTime = this.averageAttributeProcessingTime;
      other.averageQueryIdProcessingTime = this.averageQueryIdProcessingTime;
      other.averageQueryIdWaitTime = this.averageQueryIdWaitTime;
      other.totalTags = this.totalTags;
      other.totalAttributesProcessed = this.totalAttributesProcessed;
      other.totalQueryIdsProcessed = this.totalQueryIdsProcessed;
      other.totalAttributeProcessingTime = this.totalAttributeProcessingTime;
      other.totalQueryIdProcessingTime = this.totalQueryIdProcessingTime;
      other.totalQueryIdWaitTime = this.totalQueryIdWaitTime;
      other.longestQueryIdWaitTime = this.longestQueryIdWaitTime;
      other.longestQueryIdProcessingTime = this.longestQueryIdProcessingTime;
      other.longestTask = this.longestTask.clone();
      other.mostTags = this.mostTags.clone();
      other.setIndexerData(this.indexerData);
      return other;
   }

   public void setIndexerData(IndexerData indexerData) {
      this.indexerData = indexerData;
   }

   public void addIndexerItem(int queryId, long gammaId, int totalTags, long processingTime) {
      this.totalTags += totalTags;
      this.totalAttributesProcessed++;
      this.totalAttributeProcessingTime += processingTime;
      this.averageAttributeProcessingTime = this.totalAttributeProcessingTime / this.totalAttributesProcessed;

      IndexerItemStatisticsImpl newTask = new IndexerItemStatisticsImpl(gammaId, totalTags, processingTime);
      if (newTask.getProcessingTime() > this.longestTask.getProcessingTime()) {
         this.longestTask = newTask;
      }
      if (newTask.getTotalTags() > this.mostTags.getTotalTags()) {
         this.mostTags = newTask;
      }
   }

   public void addIndexerTask(int queryId, long waitTime, long processingTime) {
      this.totalQueryIdsProcessed++;
      this.totalQueryIdWaitTime += waitTime;
      this.totalQueryIdProcessingTime += processingTime;

      this.averageQueryIdWaitTime = totalQueryIdWaitTime / this.totalQueryIdsProcessed;
      this.averageQueryIdProcessingTime = totalQueryIdProcessingTime / this.totalQueryIdsProcessed;

      this.longestQueryIdProcessingTime = Math.max(this.longestQueryIdProcessingTime, processingTime);
      this.longestQueryIdWaitTime = Math.max(this.longestQueryIdWaitTime, waitTime);
   }

}
