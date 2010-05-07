/*
 * Created on May 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Operation to return if no work is to be done.
 * 
 * @author Donald G. Dunne
 */
public class EmptyOperation extends AbstractOperation {

   public EmptyOperation(String operationName, String pluginId) {
      super(operationName, pluginId);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.worked(calculateWork(1.0));
   }

}
