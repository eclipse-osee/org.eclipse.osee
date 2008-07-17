/*
 * Created on Jul 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.search;

import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITaggerStatistics;
import org.eclipse.osee.framework.search.engine.ITaskStatistics;
import org.eclipse.osee.framework.server.admin.Activator;

/**
 * @author Roberto E. Escobar
 */
public class TaggerStats extends BaseCmdWorker {

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
      println(String.format("Processing Time - avg: [%s] ms - longest: [%s] ms", stats.getAverageProcessingTime(),
            stats.getLongestProcessingTime()));
      println(String.format("Wait Time       - avg: [%s] ms - longest: [%s] ms", stats.getAverageWaitTime(),
            stats.getLongestWaitTime()));
      println(String.format("Max to process  - %s", toString(stats.getLongestTask())));
      println(String.format("Max tags        - %s", toString(stats.getMostTagsTask())));
      println(String.format("Processed       - items: [%d] - generated [%d] tags", stats.getTotalProcessed(),
            stats.getTotalTags()));
      println(String.format("Pending Items   - [%d]", tagger.getWorkersInQueue()));
      println(String.format("Total Tags in System - [%d]\n", stats.getTagsInSystem()));
   }

   private String toString(ITaskStatistics task) {
      return String.format("id: [%d] - [%d] ms waited to process [%d] tags in [%d] ms", task.getGammaId(),
            task.getWaitTime(), task.getTotalTags(), task.getProcessingTime());
   }
}
