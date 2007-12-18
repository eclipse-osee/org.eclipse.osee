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
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Ryan D. Brooks
 */
public final class Jobs {

   private Jobs() {
   };

   public static Job startJob(Job job) {
      return startJob(job, true);
   }

   public static Job startJob(Job job, boolean user) {
      job.setUser(user);
      job.setPriority(Job.LONG);
      job.schedule();
      return job;
   }

   public static void run(String name, IExceptionableRunnable runnable, Logger logger, String pluginId) {
      run(name, runnable, logger, pluginId, true);
   }

   public static void run(String name, IExceptionableRunnable runnable, Logger logger, String pluginId, boolean user) {
      startJob(new CatchAndReleaseJob(name, runnable, logger, pluginId), user);
   }

   public static class CatchAndReleaseJob extends Job {
      private final IExceptionableRunnable runnable;
      private final Logger logger;
      private final String pluginId;

      /**
       * @param name
       * @param runnable
       * @param logger
       * @param pluginId
       */
      public CatchAndReleaseJob(String name, IExceptionableRunnable runnable, Logger logger, String pluginId) {
         super(name);
         this.runnable = runnable;
         this.logger = logger;
         this.pluginId = pluginId;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {

         try {
            runnable.run(monitor);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            String message = ex.getLocalizedMessage() == null ? ex.toString() : ex.getLocalizedMessage();
            return new Status(Status.ERROR, pluginId, Status.OK, message, ex);
         }

         return Status.OK_STATUS;
      }
   }
}