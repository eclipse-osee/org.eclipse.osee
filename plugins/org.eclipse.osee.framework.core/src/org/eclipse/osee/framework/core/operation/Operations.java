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

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public final class Operations {
   public static final int TASK_WORK_RESOLUTION = Integer.MAX_VALUE;

   private Operations() {
      // this private empty constructor exists to prevent the default constructor from allowing public construction
   }

   /**
    * @param workPercentage
    * @return amount from total work
    */
   public static int calculateWork(int totalWork, double workPercentage) {
      return (int) (totalWork * workPercentage);
   }

   /**
    * Checks to see if the status has errors. If the status contains errors, an OseeCoreException will be thrown.
    */
   public static void checkForErrorStatus(IStatus status) throws OseeCoreException {
      if (status.getSeverity() == IStatus.ERROR) {
         OseeExceptions.wrapAndThrow(status.getException());
      }
   }

   public static IStatus executeWork(IOperation operation) {
      return executeWork(operation, null);
   }

   public static void executeWorkAndCheckStatus(IOperation operation) throws OseeCoreException {
      executeWorkAndCheckStatus(operation, null);
   }

   /**
    * Executes an operation by calling {@link #executeWork(IOperation, IProgressMonitor)} and checks for error status
    * {@link #checkForErrorStatus(IStatus)}. An OseeCoreException is thrown is an error is detected
    * 
    * @param operation
    * @param monitor
    * @param workPercentage
    */
   public static IStatus executeWorkAndCheckStatus(IOperation operation, IProgressMonitor monitor) throws OseeCoreException {
      IStatus status = executeWork(operation, monitor);
      checkForErrorStatus(status);
      return status;
   }

   /**
    * Executes an operation calling the monitor begin and done methods. If workPercentage is set greater than 0, monitor
    * will be wrapped into a SubProgressMonitor set to the appropriate number of ticks to consume from the main monitor.
    * 
    * @param operation
    * @param monitor
    */
   public static IStatus executeWork(IOperation operation, IProgressMonitor monitor) {
      SubMonitor subMonitor = SubMonitor.convert(monitor, operation.getName(), TASK_WORK_RESOLUTION);
      return operation.run(subMonitor);
   }

   public static Job executeAsJob(IOperation operation, boolean user) {
      return scheduleJob(new OperationJob(operation), user, Job.LONG, null);
   }

   public static Job executeAsJob(IOperation operation, OperationReporter reporter, boolean user) {
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
