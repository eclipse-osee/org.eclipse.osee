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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.framework.core.internal.Activator;

/**
 * This class is the basic unit of work for OSEE. All operations should be designed such that they can be chained and/or
 * composed into composite operations.
 * 
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public abstract class AbstractOperation implements IOperation {
   private static final IStatus NOT_RUN = new Status(IStatus.ERROR, Activator.class.getPackage().toString(),
      "It is Invalid to call getStatus() prior to executing the operation");
   private final List<IStatus> statuses = new ArrayList<IStatus>();
   private final String pluginId;
   private final String name;
   private boolean wasExecuted;

   public AbstractOperation(String operationName, String pluginId) {
      this.pluginId = pluginId;
      this.wasExecuted = false;
      this.name = operationName;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public final IStatus getStatus() {
      if (!wasExecuted) {
         return NOT_RUN;
      }
      if (statuses.isEmpty()) {
         return Status.OK_STATUS;
      }
      if (statuses.size() == 1) {
         return statuses.get(0);
      }

      StringBuilder strB = new StringBuilder();
      for (IStatus status : statuses) {
         strB.append(status.getMessage());
         strB.append("\n");
      }
      IStatus[] statusArray = statuses.toArray(new IStatus[statuses.size()]);
      return new MultiStatus(pluginId, IStatus.OK, statusArray, strB.toString(), null);
   }

   @Deprecated
   // this method should be private and non-private usages need to be replaced with composite operations
   protected void mergeStatus(IStatus status) {
      internalMergeStatus(status);
   }

   private void internalMergeStatus(IStatus status) {
      if (status.getSeverity() != IStatus.OK) {
         statuses.add(status);
      }
   }

   @Override
   public final boolean wasExecuted() {
      return wasExecuted;
   }

   @Override
   public final IOperation run(IProgressMonitor monitor) {
      wasExecuted = true;
      try {
         doWork(monitor);
         checkForCancelledStatus(monitor);
      } catch (Throwable error) {
         internalMergeStatus(createErrorStatus(error));
      } finally {
         doFinally(monitor);
      }
      return this;
   }

   /**
    * life-cycle method to allow clients to hook into the operation's finally block
    * 
    * @param monitor
    */
   protected void doFinally(IProgressMonitor monitor) {
      //
   }

   /**
    * All work should be performed here
    * 
    * @param monitor
    * @throws Exception
    */
   protected abstract void doWork(IProgressMonitor monitor) throws Exception;

   private IStatus createErrorStatus(Throwable error) {
      if (error instanceof OperationCanceledException) {
         return Status.CANCEL_STATUS;
      } else {
         return new Status(IStatus.ERROR, pluginId, error.toString(), error);
      }
   }

   protected final int calculateWork(double workPercentage) {
      return Operations.calculateWork(getTotalWorkUnits(), workPercentage);
   }

   /**
    * Executes a nested operation calling monitor begin and done. The parentMonitor will be wrapped into a
    * SubProgressMonitor and set to the appropriate number of ticks to consume from the main monitor. Checks for status
    * after work is complete to detect for execution errors or canceled.
    * 
    * @param operation
    * @param monitor
    * @param workPercentage
    * @throws Exception
    */
   public final void doSubWork(IOperation operation, IProgressMonitor monitor, double workPercentage) throws Exception {
      doSubWorkNoChecks(operation, monitor, workPercentage);
      checkForErrorsOrCanceled(monitor);
   }

   /**
    * Executes a nested operation calling monitor begin and done. The parentMonitor will be wrapped into a
    * SubProgressMonitor and set to the appropriate number of ticks to consume from the main monitor. Clients should use
    * {@link #doSubWork(IOperation, IProgressMonitor, double)} when required to throw exceptions for status errors or
    * canceled. Alternatively, clients can perform the appropriate checks after calling this method. The operation's
    * status contains the result of having executed the sub-operation.
    * 
    * @param operation
    * @param monitor
    * @param workPercentage
    */
   public final void doSubWorkNoChecks(IOperation operation, IProgressMonitor parentMonitor, double workPercentage) {
      IProgressMonitor monitor =
         new SubProgressMonitor(parentMonitor, Operations.calculateWork(operation.getTotalWorkUnits(), workPercentage));
      Operations.executeWork(operation, monitor);
      internalMergeStatus(operation.getStatus());
   }

   /**
    * Throws an exception if the severity mask is detected.
    * 
    * @param monitor
    * @throws Exception
    */
   protected final void checkForStatusSeverityMask(int severityMask) throws Exception {
      Operations.checkForStatusSeverityMask(getStatus(), severityMask);
   }

   /**
    * Checks that the user has not canceled the operation and that the operation's status is still OK. If the status has
    * changed to ERROR, WARNING or CANCEL - an Exception will be thrown.
    * 
    * @param monitor
    * @throws Exception
    */
   protected final void checkForErrorsOrCanceled(IProgressMonitor monitor) throws Exception {
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
   protected final void checkForCancelledStatus(IProgressMonitor monitor) throws OperationCanceledException {
      Operations.checkForCancelledStatus(monitor, getStatus());
   }

   @Override
   public int getTotalWorkUnits() {
      return IOperation.TOTAL_WORK;
   }

   @Override
   public String toString() {
      return getName();
   }

   protected String getPluginId() {
      return pluginId;
   }
}
