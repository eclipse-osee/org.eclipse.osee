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
package org.eclipse.osee.framework.ui.plugin.util;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Ryan D. Brooks
 */
public final class Jobs {

   private Jobs() {
   };

   public static Job startJob(Job job, IJobChangeListener jobChangeListener) {
      return startJob(job, true, jobChangeListener);
   }

   public static Job startJob(Job job) {
      return startJob(job, true, null);
   }

   public static Job startJob(Job job, boolean user) {
      return startJob(job, user, null);
   }

   public static Job startJob(Job job, boolean user, IJobChangeListener jobChangeListener) {
      job.setUser(user);
      job.setPriority(Job.LONG);
      if (jobChangeListener != null) {
         job.addJobChangeListener(jobChangeListener);
      }
      job.schedule();
      return job;
   }

   public static void run(String name, IExceptionableRunnable runnable, Class<?> clazz, String pluginId) {
      run(name, runnable, clazz, pluginId, true);
   }

   public static void run(String name, IExceptionableRunnable runnable, Class<?> clazz, String pluginId, boolean user) {
      startJob(new CatchAndReleaseJob(name, runnable, clazz, pluginId), user);
   }

   public static class CatchAndReleaseJob extends Job {
      private final IExceptionableRunnable runnable;
      private final Class<?> clazz;
      private final String pluginId;

      /**
       * @param name
       * @param runnable
       * @param logger
       * @param pluginId
       */
      public CatchAndReleaseJob(String name, IExceptionableRunnable runnable, Class<?> clazz, String pluginId) {
         super(name);
         this.runnable = runnable;
         this.clazz = clazz;
         this.pluginId = pluginId;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            runnable.run(monitor);
         } catch (Exception ex) {
            String message = ex.getLocalizedMessage() == null ? ex.toString() : ex.getLocalizedMessage();
            OseeLog.log(clazz, Level.SEVERE, ex);
            return new Status(Status.ERROR, pluginId, Status.OK, message, ex);
         }
         return Status.OK_STATUS;
      }
   }
}