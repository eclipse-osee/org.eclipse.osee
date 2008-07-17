/*
 * Created on Jul 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.search.engine;

/**
 * @author Roberto E. Escobar
 */
public interface ITaskStatistics {

   public long getGammaId();

   public int getTotalTags();

   public long getProcessingTime();

   public long getWaitTime();

}
