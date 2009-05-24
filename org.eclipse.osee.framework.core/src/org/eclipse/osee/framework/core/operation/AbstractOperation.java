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
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOperation implements IOperation {
   private final int totalWorkUnits;

   private final MultiStatus status;
   private boolean wasExecuted;

   protected String name;

   public AbstractOperation(String operationName, int totalWorkUnits, String pluginId) {
      status = new MultiStatus(pluginId, IStatus.OK, operationName, null);
      wasExecuted = false;
      this.totalWorkUnits = totalWorkUnits;
      setName(operationName);
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public IStatus getStatus() {
      return status;
   }

   protected void setStatus(IStatus status) {
      if (status.getSeverity() != IStatus.OK) {
         this.status.merge(status);
      }
   }

   public boolean wasExecuted() {
      return wasExecuted;
   }

   public ISchedulingRule getSchedulingRule() {
      return null;
   }

   public final IOperation run(IProgressMonitor monitor) {
      wasExecuted = true;
      try {
         if (returnStatusFromDoWork()) {
            setStatus(doWorkWithStatus(monitor));
         } else {
            doWork(monitor);
         }
         checkForCancelledStatus(monitor);
      } catch (Throwable error) {
         setStatus(createErrorStatus(error));
      }
      return this;
   }

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

   protected void doWork(IProgressMonitor monitor) throws Exception {

   }

   protected IStatus doWorkWithStatus(IProgressMonitor monitor) throws Exception {
      return Status.OK_STATUS;
   }

   protected boolean returnStatusFromDoWork() {
      return false;
   }

   protected IStatus createErrorStatus(Throwable error) {

      return new Status(IStatus.ERROR, status.getPlugin(), IStatus.OK, String.format("%s: %s", status.getMessage(),
            error.getLocalizedMessage()), error);
   }

   protected int calculateWork(int totalWeight, int currentWeight) {
      return (int) ((double) IOperation.TOTAL_WORK) * currentWeight / totalWeight;
   }

   protected void doSubWork(IOperation operation, IProgressMonitor monitor, double workPercentage) throws Exception {
      monitor = new SubProgressMonitor(monitor, (int) (totalWorkUnits * workPercentage));
      monitor.beginTask(operation.getName(), operation.getTotalWorkUnits());
      try {
         operation.run(monitor);
      } finally {
         monitor.done();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.IOperation#getTotalWorkUnits()
    */
   @Override
   public int getTotalWorkUnits() {
      return totalWorkUnits;
   }

}
