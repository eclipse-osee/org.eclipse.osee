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
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

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

      StringBuffer buffer = new StringBuffer();
      buffer.append("\n----------------------------------------------\n");
      buffer.append("                  Tagger Stats                \n");
      buffer.append("----------------------------------------------\n");
      buffer.append(String.format("Query Id Processing Time  - avg: [%s] ms - longest: [%s] ms\n",
            stats.getAverageQueryIdProcessingTime(), stats.getLongestQueryIdProcessingTime()));
      buffer.append(String.format("Query Id Wait Time        - avg: [%s] ms - longest: [%s] ms\n",
            stats.getAverageQueryIdWaitTime(), stats.getLongestQueryIdWaitTime()));

      buffer.append(String.format("Attribute Processing Time - avg: [%s] ms - longest: [%s] ms\n",
            stats.getAverageAttributeProcessingTime(), stats.getLongestAttributeProcessingTime()));
      buffer.append(String.format("Attribute with longest processing time - %s\n", toString(stats.getLongestTask())));
      buffer.append(String.format("Attribute with most tags - %s\n", toString(stats.getMostTagsTask())));
      buffer.append(String.format("Total - QueryIds: [%d] Attributes: [%d] Tags: [%d]\n",
            stats.getTotalQueryIdsProcessed(), stats.getTotalAttributesProcessed(), stats.getTotalTags()));
      buffer.append(String.format("Total Query Ids Waiting to be Processing - [%d]\n", tagger.getWorkersInQueue()));
      buffer.append(String.format("Total Query Ids in Tag Queue Table - [%d]\n", stats.getTotalQueryIdsInQueue()));
      buffer.append(String.format("Total Tags in System - [%d]\n\n", stats.getTagsInSystem()));

      println(buffer.toString());
   }

   private String toString(ITagItemStatistics task) {
      return String.format("id: [%d] - processed [%d] tags in [%d] ms", task.getGammaId(), task.getTotalTags(),
            task.getProcessingTime());
   }
}
