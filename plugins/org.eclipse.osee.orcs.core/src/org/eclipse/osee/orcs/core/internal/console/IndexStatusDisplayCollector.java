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

   private final List<Integer> taskIds = new CopyOnWriteArrayList<Integer>();
   private int attributesProcessed;
   private int queriesProcessed;
   private final long startTime;
   private long totalAttributes;
   private int totalQueries;

   private final Console console;

   public IndexStatusDisplayCollector(Console console, long startTime) {
      this.console = console;
      this.startTime = startTime;
      this.attributesProcessed = 0;
      this.queriesProcessed = 0;
   }

   public boolean isProcessingDone() {
      return queriesProcessed == totalQueries();
   }

   private int totalQueries() {
      int toReturn = totalQueries;
      if (toReturn == 0) {
         int remainder = (int) totalAttributes % 1000;
         toReturn = (int) totalAttributes / 1000 + (remainder > 0 ? 1 : 0);
      }
      return toReturn;
   }

   @Override
   public void onIndexTaskSubmit(int indexerId) {
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
   public void onIndexItemComplete(int queryId, long gammaId, int totalTags, long processingTime) {
      if (taskIds.contains(queryId)) {
         attributesProcessed++;
         if (attributesProcessed % 1000 == 0) {
            printStats();
         }
      }
   }

   @Override
   synchronized public void onIndexTaskComplete(int queryId, long waitTime, long processingTime) {
      taskIds.remove((Integer) queryId);
      queriesProcessed++;
      if (taskIds.isEmpty()) {
         console.writeln("QueryIds: [ %d of %d] Attributes: [%d of %d] - Elapsed Time = %s.", queriesProcessed,
            totalQueries(), attributesProcessed, totalAttributes, Lib.getElapseString(startTime));
      }
      if (isProcessingDone()) {
         this.notify();
      }
   }
}