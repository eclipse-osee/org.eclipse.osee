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

import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.orcs.statistics.IndexerItemStatistics;
import org.eclipse.osee.orcs.statistics.IndexerStatistics;

/**
 * @author Roberto E. Escobar
 */
public final class IndexerUtil {

   private IndexerUtil() {
      // Utility class
   }

   public static void writeStatistics(Console console, IndexerStatistics stats) {
      console.writeln("\n----------------------------------------------");
      console.writeln("                  Indexer Stats");
      console.writeln("----------------------------------------------");
      console.writeln("Query Id Processing Time  - avg: [%s] ms - longest: [%s] ms",
         stats.getAverageQueryIdProcessingTime(), stats.getLongestQueryIdProcessingTime());
      console.writeln("Query Id Wait Time        - avg: [%s] ms - longest: [%s] ms", stats.getAverageQueryIdWaitTime(),
         stats.getLongestQueryIdWaitTime());

      console.writeln("Attribute Processing Time - avg: [%s] ms - longest: [%s] ms",
         stats.getAverageAttributeProcessingTime(), stats.getLongestAttributeProcessingTime());
      console.writeln("Attribute with longest processing time - %s", toString(stats.getLongestTask()));
      console.writeln("Attribute with most tags - %s", toString(stats.getMostTagsTask()));
      console.writeln("Total - QueryIds: [%d] Attributes: [%d] Tags: [%d]", stats.getTotalQueryIdsProcessed(),
         stats.getTotalAttributesProcessed(), stats.getTotalTags());
      console.writeln("Total Query Ids Waiting to be Processed - [%d]", stats.getWorkersInQueue());
      console.writeln("Total Query Ids in Tag Queue Table - [%d]", stats.getTotalQueryIdsInQueue());
      console.writeln("Total Tags in System - [%d]", stats.getTagsInSystem());
   }

   private static String toString(IndexerItemStatistics item) {
      return String.format("id: [%d] - processed [%d] tags in [%d] ms", item.getId(), item.getTotalTags(),
         item.getProcessingTime());
   }
}
