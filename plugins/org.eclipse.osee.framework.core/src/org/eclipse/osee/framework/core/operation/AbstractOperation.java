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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

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
   private final OperationLogger logger;

   public AbstractOperation(String operationName, String pluginId) {
      this(operationName, pluginId, NullOperationLogger.getSingleton());
   }

   public AbstractOperation(String operationName, String pluginId, OperationLogger logger) {
      this.name = operationName;
      this.logger = logger;
      if (Strings.isValid(pluginId)) {
         this.pluginId = pluginId;
      } else {
         this.pluginId = getClass().getPackage().getName();
      }
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
      setStatus(null);
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
    * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
    * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and that the
    * operation cannot be cancelled.
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
    * Executes a nested operation and calls monitor begin and done. The parentMonitor will be converted using
    * SubMonitor.convert and set to the appropriate number of ticks to consume from the main monitor.
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
   public static final void checkForCancelledStatus(IProgressMonitor monitor) throws OperationCanceledException {
      if (monitor != null && monitor.isCanceled()) {
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

   protected final void log(String... row) {
      if (logger != null) {
         logger.log(row);
      }
   }

   protected final void log(Throwable th) {
      if (logger != null) {
         logger.log(th);
      }
   }

   protected final void log(IStatus status) {
      if (logger != null) {
         logger.log(status);
      }
   }

   protected final void logf(String format, Object... args) {
      if (logger != null) {
         logger.logf(format, args);
      }
   }

   @Override
   public OperationLogger getLogger() {
      return logger;
   }
}
