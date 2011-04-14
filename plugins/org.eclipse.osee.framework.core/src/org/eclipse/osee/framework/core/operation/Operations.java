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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;

/**
 * @author Roberto E. Escobar
 */
public final class Operations {
   public static final int TASK_WORK_RESOLUTION = Integer.MAX_VALUE;

   private Operations() {
      // this private empty constructor exists to prevent the default constructor from allowing public construction
   }

   /**
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
    */
   public static IStatus executeWorkAndCheckStatus(IOperation operation, IProgressMonitor monitor) throws OseeCoreException {
      IStatus status = executeWork(operation, monitor);
      checkForErrorStatus(status);
      return status;
   }

   /**
    * Executes an operation calling the monitor begin and done methods.
    */
   public static IStatus executeWork(IOperation operation, IProgressMonitor monitor) {
      SubMonitor subMonitor = SubMonitor.convert(monitor, operation.getName(), TASK_WORK_RESOLUTION);
      return operation.run(subMonitor);
   }

   public static Job executeAsJob(IOperation operation, boolean user) {
      return executeAsJob(operation, user, Job.LONG, null, null);
   }

   public static Job executeAsJob(IOperation operation, boolean user, ISchedulingRule rule) {
      return executeAsJob(operation, user, Job.LONG, null, rule);
   }

   public static Job executeAsJob(IOperation operation, boolean user, int priority, IJobChangeListener jobChangeListener, ISchedulingRule rule) {
      Job job = new OperationJob(operation);
      job.addJobChangeListener(new JobChangeLogger(operation.getLogger()));
      return scheduleJob(job, user, priority, jobChangeListener, rule);
   }

   public static Job executeAsJob(IOperation operation, boolean user, int priority, IJobChangeListener jobChangeListener) {
      return executeAsJob(operation, user, priority, jobChangeListener, null);
   }

   public static Job scheduleJob(Job job, boolean user, int priority, IJobChangeListener jobChangeListener) {
      return scheduleJob(job, user, priority, jobChangeListener, null);
   }

   public static Job scheduleJob(Job job, boolean user, int priority, IJobChangeListener jobChangeListener, ISchedulingRule rule) {
      job.setUser(user);
      job.setPriority(priority);
      if (jobChangeListener != null) {
         job.addJobChangeListener(jobChangeListener);
      }
      job.setRule(rule);
      job.schedule();
      return job;
   }
}