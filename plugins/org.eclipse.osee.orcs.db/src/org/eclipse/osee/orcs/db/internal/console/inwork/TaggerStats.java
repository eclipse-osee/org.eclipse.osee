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
package org.eclipse.osee.orcs.db.internal.console.inwork;


/**
 * @author Roberto E. Escobar
 */
public final class TaggerStats {

   //   public TaggerStats(OperationLogger logger) {
   //      super("Tag Engine Stats", Activator.PLUGIN_ID, logger);
   //   }
   //
   //   private String toString(ITagItemStatistics task) {
   //      return String.format("id: [%d] - processed [%d] tags in [%d] ms", task.getGammaId(), task.getTotalTags(),
   //         task.getProcessingTime());
   //   }
   //
   //   @Override
   //   protected void doWork(IProgressMonitor monitor) throws Exception {
   //      ISearchEngineTagger tagger = Activator.getSearchTagger();
   //
   //      ITaggerStatistics stats = tagger.getStatistics();
   //
   //      log("\n----------------------------------------------");
   //      log("                  Tagger Stats");
   //      log("----------------------------------------------");
   //      logf("Query Id Processing Time  - avg: [%s] ms - longest: [%s] ms", stats.getAverageQueryIdProcessingTime(),
   //         stats.getLongestQueryIdProcessingTime());
   //      logf("Query Id Wait Time        - avg: [%s] ms - longest: [%s] ms", stats.getAverageQueryIdWaitTime(),
   //         stats.getLongestQueryIdWaitTime());
   //
   //      logf("Attribute Processing Time - avg: [%s] ms - longest: [%s] ms", stats.getAverageAttributeProcessingTime(),
   //         stats.getLongestAttributeProcessingTime());
   //      logf("Attribute with longest processing time - %s", toString(stats.getLongestTask()));
   //      logf("Attribute with most tags - %s", toString(stats.getMostTagsTask()));
   //      logf("Total - QueryIds: [%d] Attributes: [%d] Tags: [%d]", stats.getTotalQueryIdsProcessed(),
   //         stats.getTotalAttributesProcessed(), stats.getTotalTags());
   //      logf("Total Query Ids Waiting to be Processed - [%d]", tagger.getWorkersInQueue());
   //      logf("Total Query Ids in Tag Queue Table - [%d]", stats.getTotalQueryIdsInQueue());
   //      logf("Total Tags in System - [%d]\n", stats.getTagsInSystem());
   //   }
}
