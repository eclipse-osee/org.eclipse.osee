/*
 * Created on Sep 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import java.util.List;

public interface IWorkProductTaskProvider {

   public List<WorkProductAction> getWorkProductRelatedActions();

   public void removeWorkProductAction(WorkProductAction action);

   public void addWorkProductAction(Collection<WorkProductAction> actions);

   public List<WorkProductTask> getWorkProductTasks();

   public WorkProductTask getWorkProductTask(String guid);

   public void setCoveragePackage(CoveragePackage coveragePackage);

   public CoveragePackage getCoveragePackage();

   public void reload();
}
