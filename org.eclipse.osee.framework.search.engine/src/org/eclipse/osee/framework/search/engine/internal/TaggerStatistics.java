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

import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.search.engine.ITagItemStatistics;
import org.eclipse.osee.framework.search.engine.ITaggerStatistics;
import org.eclipse.osee.framework.search.engine.TagListenerAdapter;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;

/**
 * @author Roberto E. Escobar
 */
public class TaggerStatistics extends TagListenerAdapter implements Cloneable, ITaggerStatistics {
   public static final TaggerStatistics EMPTY_STATS = new TaggerStatistics();
   private static final TaskStatistics DEFAULT_TASK_STATS = new TaskStatistics(-1, -1, -1);

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
   private TaskStatistics longestTask;
   private TaskStatistics mostTags;

   public TaggerStatistics() {
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

   public long getLongestQueryIdWaitTime() {
      return longestQueryIdWaitTime;
   }

   public long getLongestQueryIdProcessingTime() {
      return longestQueryIdProcessingTime;
   }

   public long getAverageQueryIdWaitTime() {
      return averageQueryIdWaitTime;
   }

   public int getTotalQueryIdsProcessed() {
      return this.totalQueryIdsProcessed;
   }

   public long getAverageQueryIdProcessingTime() {
      return averageQueryIdProcessingTime;
   }

   public long getAverageAttributeProcessingTime() {
      return averageAttributeProcessingTime;
   }

   public long getTotalTags() {
      return totalTags;
   }

   public int getTotalAttributesProcessed() {
      return totalAttributesProcessed;
   }

   public long getLongestAttributeProcessingTime() {
      return longestTask.getProcessingTime();
   }

   public ITagItemStatistics getLongestTask() {
      return longestTask;
   }

   public ITagItemStatistics getMostTagsTask() {
      return mostTags;
   }

   public long getTagsInSystem() throws OseeDataStoreException {
      return SearchTagDataStore.getTotalTags();
   }

   public long getTotalQueryIdsInQueue() throws OseeDataStoreException {
      return SearchTagDataStore.getTotalQueryIdsInQueue();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#clone()
    */
   @Override
   protected ITaggerStatistics clone() throws CloneNotSupportedException {
      TaggerStatistics other = (TaggerStatistics) super.clone();
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
      return other;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onAttributeTagComplete(int, long, int, long)
    */
   @Override
   public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
      this.totalTags += totalTags;
      this.totalAttributesProcessed++;
      this.totalAttributeProcessingTime += processingTime;
      this.averageAttributeProcessingTime = this.totalAttributeProcessingTime / this.totalAttributesProcessed;

      TaskStatistics newTask = new TaskStatistics(gammaId, totalTags, processingTime);
      if (newTask.getProcessingTime() > this.longestTask.getProcessingTime()) {
         this.longestTask = newTask;
      }
      if (newTask.getTotalTags() > this.mostTags.getTotalTags()) {
         this.mostTags = newTask;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagQueryIdTagComplete(int, long)
    */
   @Override
   public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
      this.totalQueryIdsProcessed++;
      this.totalQueryIdWaitTime += waitTime;
      this.totalQueryIdProcessingTime += processingTime;

      this.averageQueryIdWaitTime = totalQueryIdWaitTime / this.totalQueryIdsProcessed;
      this.averageQueryIdProcessingTime = totalQueryIdProcessingTime / this.totalQueryIdsProcessed;

      this.longestQueryIdProcessingTime = Math.max(this.longestQueryIdProcessingTime, processingTime);
      this.longestQueryIdWaitTime = Math.max(this.longestQueryIdWaitTime, waitTime);
   }
}
