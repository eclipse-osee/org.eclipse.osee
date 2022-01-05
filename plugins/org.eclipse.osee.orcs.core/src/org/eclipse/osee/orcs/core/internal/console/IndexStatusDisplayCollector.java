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

package org.eclipse.osee.orcs.core.internal.console;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.search.IndexerCollectorAdapter;

/**
 * @author Roberto E. Escobar
 */
public final class IndexStatusDisplayCollector extends IndexerCollectorAdapter {
   private final int DEFAULT_STATS_PRINT_FREQUENCY = 1000;

   private final List<Long> taskIds = new CopyOnWriteArrayList<>();
   private int attributesProcessed;
   private int queriesProcessed;
   private final long startTime;
   private long totalAttributes;
   private int totalQueries;
   private final boolean printTags;
   private final int statsPrintFrequency;

   private final Console console;

   public IndexStatusDisplayCollector(Console console, long startTime, boolean printTags) {
      this(console, startTime, -1, printTags);
   }

   public IndexStatusDisplayCollector(Console console, long startTime, int statsPrintFrequency, boolean printTags) {
      this.console = console;
      this.startTime = startTime;
      this.attributesProcessed = 0;
      this.queriesProcessed = 0;
      this.statsPrintFrequency = statsPrintFrequency <= 0 ? DEFAULT_STATS_PRINT_FREQUENCY : statsPrintFrequency;
      this.printTags = printTags;
   }

   public boolean isProcessingDone() {
      return queriesProcessed == totalQueries();
   }

   private int totalQueries() {
      int toReturn = totalQueries;
      if (toReturn == 0) {
         int remainder = (int) totalAttributes % statsPrintFrequency;
         toReturn = (int) totalAttributes / statsPrintFrequency + (remainder > 0 ? 1 : 0);
      }
      return toReturn;
   }

   @Override
   public void onIndexTaskSubmit(Long indexerId) {
      taskIds.add(indexerId);
   }

   public void printStats() {
      console.writeln("QueryIds: [ %d of %d] Attributes: [%d of %d] - Elapsed Time = %s.", queriesProcessed,
         totalQueries(), attributesProcessed, totalAttributes, Lib.getElapseString(startTime));
   }

   @Override
   public void onIndexTaskTotalToProcess(int totalQueries) {
      this.totalQueries = totalQueries;
   }

   @Override
   public void onIndexTotalTaskItems(long totalItems) {
      totalAttributes = totalItems;
   }

   @Override
   public void onIndexItemComplete(Long queryId, long gammaId, int totalTags, long processingTime) {
      if (taskIds.contains(queryId)) {
         attributesProcessed++;
         if (attributesProcessed % statsPrintFrequency == 0) {
            printStats();
         }
      }
   }

   @Override
   synchronized public void onIndexTaskComplete(Long queryId, long waitTime, long processingTime) {
      taskIds.remove(queryId);
      queriesProcessed++;
      if (taskIds.isEmpty()) {
         console.writeln("QueryIds: [ %d of %d] Attributes: [%d of %d] - Elapsed Time = %s.", queriesProcessed,
            totalQueries(), attributesProcessed, totalAttributes, Lib.getElapseString(startTime));
      }
      if (isProcessingDone()) {
         this.notify();
      }
   }

   @Override
   public void onIndexItemAdded(Long indexerId, long itemId, String word, long codedTag) {
      if (printTags) {
         console.writeln("indexerId:[%s] itemId:[%s] word:[%s] tag:[%s]", indexerId, itemId, word, codedTag);
      }
   }
}