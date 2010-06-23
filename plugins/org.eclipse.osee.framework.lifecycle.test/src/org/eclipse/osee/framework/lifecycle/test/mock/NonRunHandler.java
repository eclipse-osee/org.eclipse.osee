/*
 * Created on Jun 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.lifecycle.test.mock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.lifecycle.LifecycleOpHandler;

public class NonRunHandler implements LifecycleOpHandler {
   private boolean hasRun;
   IStatus status;

   public NonRunHandler() {
      super();
      this.status = Status.OK_STATUS;
      hasRun = false;
   }

   @Override
   public IStatus onCheck(IProgressMonitor monitor) {
      hasRun = true;
      return status;
   }

   @Override
   public IStatus onPostCondition(IProgressMonitor monitor) {
      hasRun = true;
      return status;
   }

   @Override
   public IStatus onPreCondition(IProgressMonitor monitor) {
      hasRun = true;
      return status;
   }

   public boolean hasRan() {
      return hasRun;
   }
}
