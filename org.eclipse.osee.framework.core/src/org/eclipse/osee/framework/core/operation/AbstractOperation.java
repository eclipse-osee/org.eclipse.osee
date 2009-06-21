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
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOperation implements IOperation {

   private final InternalMultiStatus status;
   private boolean wasExecuted;
   private String name;

   /**
    * @param operationName
    * @param pluginId
    */
   public AbstractOperation(String operationName, String pluginId) {
      this.status = new InternalMultiStatus(pluginId, IStatus.OK, operationName);
      this.wasExecuted = false;
      setName(operationName);
   }

   /**
    * Sets the operation's name
    * 
    * @param name
    */
   public void setName(String name) {
      this.name = name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.IOperation#getName()
    */
   public String getName() {
      return name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.IOperation#getStatus()
    */
   public IStatus getStatus() {
      return status;
   }

   /**
    * Updates the operation's status
    * 
    * @param status
    */
   protected void setStatus(IStatus status) {
      if (status.getSeverity() != IStatus.OK) {
         this.status.merge(status);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.IOperation#wasExecuted()
    */
   public boolean wasExecuted() {
      return wasExecuted;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.IOperation#getSchedulingRule()
    */
   public ISchedulingRule getSchedulingRule() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.IOperation#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   public final IOperation run(IProgressMonitor monitor) {
      wasExecuted = true;
      try {
         doWork(monitor);
         checkForCancelledStatus(monitor);
      } catch (Throwable error) {
         setStatus(createErrorStatus(error));
      } finally {
         doFinally(monitor);
      }
      return this;
   }

   /**
    * Convenience method to allow clients to hook into the operation's finally block
    * 
    * @param monitor
    */
   protected void doFinally(IProgressMonitor monitor) {

   }

   /**
    * All work should be performed here
    * 
    * @param monitor
    * @throws Exception
    */
   protected abstract void doWork(IProgressMonitor monitor) throws Exception;

   /**
    * Updates the operation's status message
    * 
    * @param message
    */
   protected void setStatusMessage(String message) {
      status.setMessage(message);
   }

   /**
    * Creates a new error status object
    * 
    * @param error
    * @return
    */
   protected IStatus createErrorStatus(Throwable error) {
      if (error instanceof OperationCanceledException) {
         return Status.CANCEL_STATUS;
      } else {
         return new Status(IStatus.ERROR, status.getPlugin(), String.format("%s: %s", status.getMessage(),
               error.getLocalizedMessage()), error);
      }
   }

   /**
    * @param workPercentage
    * @return
    */
   protected int calculateWork(double workPercentage) {
      return Operations.calculateWork(getTotalWorkUnits(), workPercentage);
   }

   /**
    * Executes a nested operation setting monitor begin and done. If workPercentage is set greater than 0, monitor will
    * be wrapped into a SubProgressMonitor set to the appropriate number of ticks to consume from the main monitor.
    * Checks for status after work is complete to detect for execution errors or canceled.
    * 
    * @param operation
    * @param monitor
    * @param workPercentage
    * @throws Exception
    */
   public void doSubWork(IOperation operation, IProgressMonitor monitor, double workPercentage) throws Exception {
      doSubWorkNoChecks(operation, monitor, workPercentage);
      checkForErrorsOrCanceled(monitor);
   }

   /**
    * Executes a nested operation setting monitor begin and done. If workPercentage is set greater than 0, monitor will
    * be wrapped into a SubProgressMonitor set to the appropriate number of ticks to consume from the main monitor.
    * Clients should use {@link #doSubWork(IOperation, IProgressMonitor, double)} when required to throw exceptions for
    * status errors or canceled. Alternatively, clients can perform the appropriate checks after calling this method.
    * The operation's status contains the result of having executed the sub-operation.
    * 
    * @param operation
    * @param monitor
    * @param workPercentage
    */
   public void doSubWorkNoChecks(IOperation operation, IProgressMonitor monitor, double workPercentage) {
      Operations.executeWork(operation, monitor, workPercentage);
      setStatus(operation.getStatus());
   }

   /**
    * Throws an exception if the severity mask is detected.
    * 
    * @param monitor
    * @throws Exception
    */
   protected void checkForStatusSeverityMask(int severityMask) throws Exception {
      Operations.checkForStatusSeverityMask(getStatus(), severityMask);
   }

   /**
    * Checks that the user has not canceled the operation and that the operation's status is still OK. If the status has
    * changed to ERROR, WARNING or CANCEL - an Exception will be thrown.
    * 
    * @param monitor
    * @throws Exception
    */
   protected void checkForErrorsOrCanceled(IProgressMonitor monitor) throws Exception {
      checkForCancelledStatus(monitor);
      Operations.checkForErrorStatus(getStatus());
   }

   /**
    * Checks to see if the user cancelled the operation. If the operation was cancelled, the method will throw an
    * OperationCanceledException
    * 
    * @param monitor
    * @throws OperationCanceledException
    */
   protected void checkForCancelledStatus(IProgressMonitor monitor) throws OperationCanceledException {
      if (monitor.isCanceled()) {
         boolean wasCancelled = false;
         IStatus[] children = this.status.getChildren();
         for (int i = 0; i < children.length; i++) {
            Throwable exception = children[i].getException();
            if (exception instanceof OperationCanceledException) {
               wasCancelled = true;
               break;
            }
         }
         if (!wasCancelled) {
            throw new OperationCanceledException();
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.IOperation#getTotalWorkUnits()
    */
   @Override
   public int getTotalWorkUnits() {
      return IOperation.TOTAL_WORK;
   }

   private final static class InternalMultiStatus extends org.eclipse.core.runtime.MultiStatus {

      public InternalMultiStatus(String pluginId, int code, String message) {
         super(pluginId, code, message, null);
      }

      @Override
      public void setMessage(String message) {
         super.setMessage(message);
      }
   }
}
