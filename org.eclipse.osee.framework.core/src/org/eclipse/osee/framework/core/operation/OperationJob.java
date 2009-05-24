/*
 * Created on May 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Ryan D. Brooks
 */
public class OperationJob extends Job {
   private final AbstractOperation operation;

   /**
    * @param operation the operation that will be executed in this Job
    */
   public OperationJob(AbstractOperation operation) {
      super(operation.getName());
      this.operation = operation;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      return operation.run(monitor).getStatus();
   }

}
