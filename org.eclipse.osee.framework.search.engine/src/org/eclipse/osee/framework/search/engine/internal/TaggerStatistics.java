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

import org.eclipse.osee.framework.search.engine.ITaggerStatistics;
import org.eclipse.osee.framework.search.engine.ITagItemStatistics;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;

/**
 * @author Roberto E. Escobar
 */
public class TaggerStatistics implements Cloneable, ITaggerStatistics {
   public static final TaggerStatistics EMPTY_STATS = new TaggerStatistics();
   private static final TaskStatistics DEFAULT_TASK_STATS = new TaskStatistics(-1, 0, 0, 0);

   private long averageWaitTime;
   private long averageProcessingTime;
   private long totalTags;
   private int totalProcessed;
   private long totalWaitTime;
   private long totalProcessingTime;
   private long longestWaitTime;
   private TaskStatistics longestTask;
   private TaskStatistics mostTags;

   public TaggerStatistics() {
      clear();
   }

   public void clear() {
      this.averageWaitTime = 0;
      this.totalTags = 0;
      this.averageProcessingTime = 0;
      this.totalProcessed = 0;
      this.totalWaitTime = 0;
      this.totalProcessingTime = 0;
      this.longestWaitTime = 0;
      this.longestTask = DEFAULT_TASK_STATS;
      this.mostTags = DEFAULT_TASK_STATS;
   }

   public long getAverageWaitTime() {
      return averageWaitTime;
   }

   public long getAverageProcessingTime() {
      return averageProcessingTime;
   }

   public long getTotalTags() {
      return totalTags;
   }

   public int getTotalProcessed() {
      return totalProcessed;
   }

   public long getLongestProcessingTime() {
      return longestTask.getProcessingTime();
   }

   public long getLongestWaitTime() {
      return longestWaitTime;
   }

   public ITagItemStatistics getLongestTask() {
      return longestTask;
   }

   public ITagItemStatistics getMostTagsTask() {
      return mostTags;
   }

   public long getTagsInSystem() {
      return SearchTagDataStore.getTotalTags();
   }

   public void addEntry(long gammaId, int totalTags, long waitTime, long processingTime) {
      this.totalTags += totalTags;
      this.totalProcessed++;
      this.totalWaitTime += waitTime;
      this.totalProcessingTime += processingTime;
      this.averageWaitTime = totalWaitTime / this.totalProcessed;
      this.averageProcessingTime = totalProcessingTime / this.totalProcessed;

      TaskStatistics newTask = new TaskStatistics(gammaId, totalTags, processingTime, waitTime);
      if (newTask.getProcessingTime() > this.longestTask.getProcessingTime()) {
         this.longestTask = newTask;
      }
      if (newTask.getTotalTags() > this.mostTags.getTotalTags()) {
         this.mostTags = newTask;
      }
      this.longestWaitTime = Math.max(this.longestWaitTime, waitTime);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#clone()
    */
   @Override
   protected ITaggerStatistics clone() throws CloneNotSupportedException {
      TaggerStatistics other = (TaggerStatistics) super.clone();
      other.averageProcessingTime = this.averageProcessingTime;
      other.averageWaitTime = this.averageWaitTime;
      other.totalTags = this.totalTags;
      other.totalProcessed = this.totalProcessed;
      other.totalWaitTime = this.totalWaitTime;
      other.totalProcessingTime = this.totalProcessingTime;
      other.longestWaitTime = this.longestWaitTime;
      other.longestTask = this.longestTask.clone();
      other.mostTags = this.mostTags.clone();
      return other;
   }
}
