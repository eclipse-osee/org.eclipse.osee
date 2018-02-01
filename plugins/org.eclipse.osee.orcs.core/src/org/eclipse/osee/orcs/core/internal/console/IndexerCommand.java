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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.statistics.IndexerStatistics;

/**
 * @author Roberto E. Escobar
 */
public class IndexerCommand implements ConsoleCommand {

   private static enum OpType {
      STATS,
      DROP,
      ALL,
      MISSING_ITEMS_ONLY,
      ITEM_IDS,
      ATTR_TYPE_IDS,
      CANCEL
   }

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "indexer";
   }

   @Override
   public String getDescription() {
      return "Interacts with the data indexer";
   }

   @Override
   public String getUsage() {
      StringBuilder builder = new StringBuilder();
      builder.append("op=<");
      OpType[] types = OpType.values();
      int cnt = 0;
      for (OpType type : types) {
         if (OpType.ITEM_IDS != type) {
            builder.append(type);
            if (cnt + 1 < types.length) {
               builder.append("|");
            }
         }
         cnt++;
      }
      builder.append("> [branchUuids=<BRANCH_UUID,..>]\n");
      builder.append("op=ITEM_IDS ids=<GAMMA_IDS,..> debug=<TRUE|FALSE>\n");
      builder.append("op=ATTR_TYPE_IDS ids=<ATTR_TYPE_IDS,..>");
      return builder.toString();
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new IndexerCommandCallable(console, params);
   }

   private final class IndexerCommandCallable extends CancellableCallable<Boolean> {

      private final Console console;
      private final ConsoleParameters params;

      public IndexerCommandCallable(Console console, ConsoleParameters params) {
         this.console = console;
         this.params = params;
      }

      private OpType toOpType(String value) {
         OpType opType = OpType.STATS;
         if (Strings.isValid(value)) {
            opType = OpType.valueOf(value.toUpperCase());
         }
         return opType;
      }

      @Override
      public Boolean call() throws Exception {
         long startTime = System.currentTimeMillis();
         boolean indexOnlyMissingitems = false;

         Set<Branch> branches = new HashSet<>();
         String[] uuids = params.getArray("branchUuids");
         if (uuids != null && uuids.length > 0) {
            for (String uuid : uuids) {
               branches.add(getOrcsApi().getQueryFactory().branchQuery().andId(
                  BranchId.valueOf(uuid)).getResults().getExactlyOne());
            }
         }

         QueryIndexer indexer = getOrcsApi().getQueryIndexer();

         OpType opType = toOpType(params.get("op"));
         switch (opType) {
            case CANCEL:
               throw new UnsupportedOperationException("Can't cancel index op");
            case DROP:
               if (branches.isEmpty()) {
                  indexer.purgeAllIndexes().call();
               } else {
                  throw new UnsupportedOperationException("Can't selectively drop indexed branches");
               }
               break;
            case MISSING_ITEMS_ONLY:
               indexOnlyMissingitems = true;
            case ALL:
               IndexStatusDisplayCollector collector = new IndexStatusDisplayCollector(console, startTime, false);
               Callable<?> callable = indexer.indexBranches(branches, indexOnlyMissingitems, collector);
               callable.call();
               break;
            case ITEM_IDS:
            case ATTR_TYPE_IDS:
               boolean printTags = params.getBoolean("debug");
               Set<Long> ids = new LinkedHashSet<>();
               for (String value : params.getArray("ids")) {
                  ids.add(Long.parseLong(value));
               }
               if (opType == OpType.ATTR_TYPE_IDS) {
                  indexer.indexAttrTypeIds(ids);
               } else {
                  IndexStatusDisplayCollector collector2 =
                     new IndexStatusDisplayCollector(console, startTime, printTags);
                  Callable<?> callable2 = indexer.indexResources(ids, collector2);
                  callable2.call();
               }
               break;
            case STATS:
            default:
               IndexerStatistics indexerStats = getOrcsApi().getOrcsPerformance().getIndexerStatistics();
               IndexerUtil.writeStatistics(console, indexerStats);
               break;
         }
         return Boolean.TRUE;
      }

   }
}