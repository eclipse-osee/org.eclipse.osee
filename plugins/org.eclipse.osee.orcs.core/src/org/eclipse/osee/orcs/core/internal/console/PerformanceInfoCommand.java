/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.console;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsPerformance;
import org.eclipse.osee.orcs.statistics.IndexerStatistics;
import org.eclipse.osee.orcs.statistics.QueryStatistics;

/**
 * @author Roberto E. Escobar
 */
public class PerformanceInfoCommand implements ConsoleCommand {

   private OrcsApi orcsApi;

   private enum StatsType {
      QUERY,
      INDEXER,
      ALL
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "performance";
   }

   @Override
   public String getDescription() {
      return "Displays performance information";
   }

   @Override
   public String getUsage() {
      return "[statusId=<[QUERY|INDEXER|ALL],..>] [reset=<TRUE|FALSE>]";
   }

   private Collection<StatsType> toStatusTypes(String[] stats) {
      Collection<StatsType> statsType = new HashSet<>();
      boolean addAllStatTypes = false;

      if (stats != null && stats.length > 0) {
         for (String stat : stats) {
            StatsType type = StatsType.valueOf(stat.toUpperCase());
            if (StatsType.ALL == type) {
               addAllStatTypes = true;
               break;
            } else {
               statsType.add(type);
            }
         }
      } else {
         addAllStatTypes = true;
      }

      if (addAllStatTypes) {
         statsType.addAll(Arrays.asList(StatsType.values()));
      }
      return statsType;
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      boolean isResetAllowed = params.getBoolean("reset");
      Collection<StatsType> statsType = toStatusTypes(params.getArray("statusId"));

      OrcsPerformance performance = getOrcsApi().getOrcsPerformance();
      return new PerformanceCallable(console, performance, statsType, isResetAllowed);
   }

   private static final class PerformanceCallable extends CancellableCallable<Boolean> {

      private final Console console;
      private final OrcsPerformance performance;
      private final Collection<StatsType> statsType;
      private final boolean isResetAllowed;

      public PerformanceCallable(Console console, OrcsPerformance performance, Collection<StatsType> statsType, boolean isResetAllowed) {
         super();
         this.console = console;
         this.performance = performance;
         this.statsType = statsType;
         this.isResetAllowed = isResetAllowed;
      }

      private boolean isResetAllowed(StatsType type) {
         return isResetAllowed && isWriteAllowed(type);
      }

      private boolean isWriteAllowed(StatsType type) {
         return statsType.contains(type);
      }

      @Override
      public Boolean call() throws Exception {
         if (isResetAllowed(StatsType.QUERY)) {
            performance.clearQueryStatistics();
         }

         if (isWriteAllowed(StatsType.QUERY)) {
            QueryStatistics queryStats = performance.getQueryStatistics();
            writeStatistics(queryStats);
         }

         if (isResetAllowed(StatsType.INDEXER)) {
            performance.clearIndexerStatistics();
         }

         if (isWriteAllowed(StatsType.INDEXER)) {
            IndexerStatistics indexerStats = performance.getIndexerStatistics();
            IndexerUtil.writeStatistics(console, indexerStats);
         }
         return Boolean.TRUE;
      }

      private void writeStatistics(QueryStatistics stats) {
         console.writeln("\n----------------------------------------------");
         console.writeln("                  Search Stats");
         console.writeln("----------------------------------------------");
         console.writeln("Total Searches - [%d]", stats.getTotalSearches());
         console.writeln("Search Time    - avg: [%s] ms - longest: [%s] ms", stats.getAverageSearchTime(),
            stats.getLongestSearchTime());
         console.writeln("Longest Search  - %s", stats.getLongestSearch());
      }
   }
}
