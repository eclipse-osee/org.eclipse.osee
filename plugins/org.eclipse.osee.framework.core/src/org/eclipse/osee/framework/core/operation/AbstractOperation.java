/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * This class is the basic unit of work for OSEE. All operations should be designed such that they can be chained and/or
 * composed into composite operations.
 * 
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public abstract class AbstractOperation implements IOperation {

   private IStatus status;
   private final String pluginId;
   private final String name;
   private boolean wasExecuted = false;

   public AbstractOperation(String operationName, String pluginId) {
      this.pluginId = pluginId;
      this.name = operationName;
   }

   @Override
   public String getName() {
      return name;
   }

   /**
    * Subclasses should only call this if they need special control over the status returned from the run method. If the
    * doWork method terminates normally then this status will be returned; however, if doWork throws an uncaught
    * exception, then a status will be constructed based on that exception and returned.
    */
   protected void setStatus(IStatus status) {
      this.status = status;
   }

   public final boolean wasExecuted() {
      return wasExecuted;
   }

   @Override
   public final IStatus run(SubMonitor subMonitor) {
      wasExecuted = true;
      try {
         doWork(subMonitor);
      } catch (Throwable throwable) {
         setStatusFromThrowable(throwable);
      } finally {
         doFinally(subMonitor);
      }
      return status == null ? Status.OK_STATUS : status;
   }

   /**
    * life-cycle method to allow clients to hook into the operation's finally block
    */
   protected void doFinally(IProgressMonitor monitor) {
      //
   }

   /**
    * All the operations work should be executed directly or indirectly by this method. The operation runs until its
    * doWork() method terminates normally or by throwing an exception (including OperationCanceledException)
    * 
    * @param subMonitor the progress monitor to use for reporting progress to the user. It is the caller's
    * responsibility to call done() on the given monitor. Accepts null, indicating that no progress should be reported
    * and that the operation cannot be cancelled.
    * @throws Exception the exception will be caught by the calling method and turned into a status
    */
   protected abstract void doWork(IProgressMonitor monitor) throws Exception;

   private void setStatusFromThrowable(Throwable throwable) {
      if (throwable instanceof OperationCanceledException) {
         setStatus(Status.CANCEL_STATUS);
      } else {
         setStatus(new Status(IStatus.ERROR, pluginId, throwable.toString(), throwable));
      }
   }

   protected final int calculateWork(double workPercentage) {
      return Operations.calculateWork(Operations.TASK_WORK_RESOLUTION, workPercentage);
   }

   /**
    * Executes a nested operation and calls monitor begin and done. The parentMonitor will be wrapped into a
    * SubProgressMonitor and set to the appropriate number of ticks to consume from the main monitor.
    * 
    * @throws OseeCoreException
    * @throws Exception
    */
   public final IStatus doSubWork(IOperation operation, IProgressMonitor parentMonitor, double workPercentage) throws OseeCoreException {
      IStatus status = Operations.executeWork(operation, parentMonitor);
      checkForCancelledStatus(parentMonitor);
      Operations.checkForErrorStatus(status);
      return status;
   }

   /**
    * throws OperationCanceledException if the user cancelled the operation via the monitor , otherwise it simply
    * returns
    */
   protected static final void checkForCancelledStatus(IProgressMonitor monitor) throws OperationCanceledException {
      if (monitor.isCanceled()) {
         throw new OperationCanceledException();
      }
   }

   @Override
   public String toString() {
      return getName();
   }

   /**
    * simply returns the pluginId that was provided to the constructor
    */
   protected final String getPluginId() {
      return pluginId;
   }
}