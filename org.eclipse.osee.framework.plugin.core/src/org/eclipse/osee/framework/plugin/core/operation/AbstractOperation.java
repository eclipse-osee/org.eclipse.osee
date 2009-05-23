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
package org.eclipse.osee.framework.plugin.core.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOperation implements IOperation {
   private final MultiStatus status;
   private boolean wasExecuted;

   protected String name;

   public AbstractOperation(String operationName) {
      status = new MultiStatus(PluginCoreActivator.PLUGIN_ID, IStatus.OK, operationName, null);
      wasExecuted = false;
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
      monitor.beginTask(getName(), IOperation.TOTAL_WORK);
      try {
         doWork(monitor);
         checkForCancelledStatus(monitor);
      } catch (Throwable error) {
         setStatus(createErrorStatus(error));
      } finally {
         monitor.done();
      }
      return this;
   }

   private void checkForCancelledStatus(IProgressMonitor monitor) throws OperationCancelledException {
      if (monitor.isCanceled()) {
         boolean wasCancelled = false;
         IStatus[] children = this.status.getChildren();
         for (int i = 0; i < children.length; i++) {
            Throwable exception = children[i].getException();
            if (exception instanceof OperationCancelledException) {
               wasCancelled = true;
               break;
            }
         }
         if (!wasCancelled) {
            throw new OperationCancelledException();
         }
      }
   }

   protected abstract void doWork(IProgressMonitor monitor) throws Exception;

   protected IStatus createErrorStatus(Throwable error) {
      return new Status(IStatus.ERROR, PluginCoreActivator.PLUGIN_ID, IStatus.OK, String.format("%s: %s",
            status.getMessage(), error.getMessage()), error);
   }

   protected int calculateWork(int totalWeight, int currentWeight) {
      return (int) ((double) IOperation.TOTAL_WORK) * currentWeight / totalWeight;
   }

   protected void doSubWork(IOperation operation, IProgressMonitor monitor, int totalWeight, int currentWeight) throws Exception {
      if (totalWeight > 0) {
         monitor = new SubProgressMonitor(monitor, calculateWork(totalWeight, currentWeight));
      }
      monitor.beginTask(operation.getName(), IOperation.TOTAL_WORK);
      try {
         operation.run(monitor);
      } finally {
         monitor.done();
      }
   }
}
