/*********************************************************************
 * Copyright (c) 2012 Boeing
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
         if (isResetAllowed(StatsType.INDEXER)) {
            performance.clearIndexerStatistics();
         }

         if (isWriteAllowed(StatsType.INDEXER)) {
            IndexerStatistics indexerStats = performance.getIndexerStatistics();
            IndexerUtil.writeStatistics(console, indexerStats);
         }
         return Boolean.TRUE;
      }
   }
}