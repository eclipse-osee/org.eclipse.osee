/*
 * Created on May 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public abstract class OseeOperation extends Job {
   private final int totalWorkUnits;

   /**
    * @param name
    */
   public OseeOperation(String name, int totalWorkUnits) {
      super(name);
      this.totalWorkUnits = totalWorkUnits;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      return null;
   }

   protected abstract IStatus doOperation(IProgressMonitor monitor) throws Exception;

   protected void checkCancelled(IProgressMonitor monitor) throws OperationCanceledException {
      if (monitor.isCanceled()) {
         throw new OperationCanceledException();
      }
   }

   public IStatus runNestedOperation(OseeOperation nestedOperation, IProgressMonitor monitor, double workPercentage) {
      return nestedOperation.run(new SubProgressMonitor(monitor, (int) (totalWorkUnits * workPercentage)));
   }
}
