/*
 * Created on Sep 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

public interface IWorkProductRelatable {

   public String getWorkProductTaskGuid();

   public void setWorkProductTaskGuid(String workProductGuid);

   public WorkProductTask getWorkProductTask();

   public void setWorkProductTask(WorkProductTask workProductTask);

}
