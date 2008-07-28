/*
 * Created on Jul 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.search;

import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagItemStatistics;
import org.eclipse.osee.framework.search.engine.ITaggerStatistics;
import org.eclipse.osee.framework.server.admin.Activator;

/**
 * @author Roberto E. Escobar
 */
class TaggerStats extends BaseCmdWorker {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#doWork(long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      ISearchEngineTagger tagger = Activator.getInstance().getSearchTagger();

      ITaggerStatistics stats = tagger.getStatistics();

      println("\n----------------------------------------------");
      println("                  Tagger Stats                ");
      println("----------------------------------------------");
      println(String.format("Query Id Processing Time  - avg: [%s] ms - longest: [%s] ms",
            stats.getAverageQueryIdProcessingTime(), stats.getLongestQueryIdProcessingTime()));
      println(String.format("Query Id Wait Time        - avg: [%s] ms - longest: [%s] ms",
            stats.getAverageQueryIdWaitTime(), stats.getLongestQueryIdWaitTime()));

      println(String.format("Attribute Processing Time - avg: [%s] ms - longest: [%s] ms",
            stats.getAverageAttributeProcessingTime(), stats.getLongestAttributeProcessingTime()));
      println(String.format("Attribute with longest processing time - %s", toString(stats.getLongestTask())));
      println(String.format("Attribute with most tags - %s", toString(stats.getMostTagsTask())));
      println(String.format("Total - QueryIds: [%d] Attributes: [%d] Tags: [%d]", stats.getTotalQueryIdsProcessed(),
            stats.getTotalAttributesProcessed(), stats.getTotalTags()));
      println(String.format("Total Query Ids Waiting to be Processing - [%d]", tagger.getWorkersInQueue()));
      println(String.format("Total Query Ids in Tag Queue Table - [%d]", stats.getTotalQueryIdsInQueue()));
      println(String.format("Total Tags in System - [%d]\n", stats.getTagsInSystem()));
   }

   private String toString(ITagItemStatistics task) {
      return String.format("id: [%d] - processed [%d] tags in [%d] ms", task.getGammaId(), task.getTotalTags(),
            task.getProcessingTime());
   }
}
