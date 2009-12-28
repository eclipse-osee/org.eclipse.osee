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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public final class Operations {

   private Operations() {
   }

   /**
    * @param workPercentage
    * @return amount from total work
    */
   public static int calculateWork(int totalWork, double workPercentage) {
      return (int) (totalWork * workPercentage);
   }

   /**
    * Checks to see if the user canceled the operation. If the operation was canceled, the method will throw an
    * OperationCanceledException
    * 
    * @param monitor
    * @throws OperationCanceledException
    */
   protected void checkForCancelledStatus(IProgressMonitor monitor, IStatus status) throws OperationCanceledException {
      if (monitor.isCanceled()) {
         boolean wasCancelled = false;
         IStatus[] children = status.getChildren();
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

   /**
    * Checks to see if the status has errors. If the status contains errors, an exception will be thrown.
    * 
    * @param monitor
    * @throws Exception
    * @see {@link IStatus#matches(int)}
    */
   public static void checkForStatusSeverityMask(IStatus status, int severityMask) throws Exception {
      if ((severityMask & IStatus.CANCEL) != 0 && status.getSeverity() == IStatus.CANCEL) {
         throw new OperationCanceledException();
      } else if (status.matches(severityMask)) {
         List<StackTraceElement> traceElements = new ArrayList<StackTraceElement>();
         String message = status.getMessage();
         for (IStatus childStatus : status.getChildren()) {
            Throwable exception = childStatus.getException();
            String childMessage = childStatus.getMessage();
            if (Strings.isValid(childMessage)) {
               message = childMessage;
            }
            if (exception != null) {
               traceElements.addAll(Arrays.asList(exception.getStackTrace()));
            }
         }

         Exception ex = new Exception(message);
         if (!traceElements.isEmpty()) {
            ex.setStackTrace(traceElements.toArray(new StackTraceElement[traceElements.size()]));
         }
         throw ex;
      }
   }

   /**
    * Checks to see if the status has errors. If the status contains errors, an exception will be thrown.
    * 
    * @param monitor
    * @throws Exception
    * @see {@link IStatus#matches(int)}
    */
   public static void checkForErrorStatus(IStatus status) throws Exception {
      checkForStatusSeverityMask(status, IStatus.CANCEL | IStatus.ERROR | IStatus.WARNING);
   }

   /**
    * Executes an operation calling the monitor begin and done methods. If workPercentage is set greater than 0, monitor
    * will be wrapped into a SubProgressMonitor set to the appropriate number of ticks to consume from the main monitor.
    * 
    * @param operation
    * @param monitor
    * @param workPercentage
    */
   public static void executeWork(IOperation operation, IProgressMonitor monitor, double workPercentage) {
      if (workPercentage > 0) {
         monitor = new SubProgressMonitor(monitor, calculateWork(operation.getTotalWorkUnits(), workPercentage));
      }
      monitor.beginTask(operation.getName(), operation.getTotalWorkUnits());
      try {
         operation.run(monitor);
      } finally {
         monitor.subTask("");
         monitor.setTaskName("");
         monitor.done();
      }
   }

   /**
    * Executes an operation by calling {@link #executeWork(IOperation, IProgressMonitor, double)} and checks for error
    * status {@link #checkForErrorStatus(IStatus)}. An OseeCoreException is thrown is an error is detected
    * 
    * @param operation
    * @param monitor
    * @param workPercentage
    */
   public static void executeWorkAndCheckStatus(IOperation operation, IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      executeWork(operation, monitor, workPercentage);
      try {
         Operations.checkForErrorStatus(operation.getStatus());
      } catch (Exception ex) {
         if (ex instanceof OseeCoreException) {
            throw (OseeCoreException) ex;
         } else {
            throw new OseeWrappedException(ex);
         }
      }
   }

   public static Job executeAsJob(IOperation operation, boolean user) {
      return scheduleJob(new OperationJob(operation), user, Job.LONG, null);
   }

   public static Job executeAndPend(IOperation operation, boolean user) {
      return scheduleJob(new OperationJob(operation), user, Job.LONG, null, true);
   }

   public static Job executeAsJob(IOperation operation, boolean user, int priority, IJobChangeListener jobChangeListener) {
      return scheduleJob(new OperationJob(operation), user, priority, jobChangeListener);
   }

   public static Job scheduleJob(Job job, boolean user, int priority, IJobChangeListener jobChangeListener) {
      return scheduleJob(job, user, priority, jobChangeListener, false);
   }

   public static Job scheduleJob(Job job, boolean user, int priority, IJobChangeListener jobChangeListener, boolean forcePend) {
      job.setUser(user);
      job.setPriority(priority);
      if (jobChangeListener != null) {
         job.addJobChangeListener(jobChangeListener);
      }
      job.schedule();
      if (forcePend) {
         try {
            job.join();
         } catch (InterruptedException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return job;
   }
}
