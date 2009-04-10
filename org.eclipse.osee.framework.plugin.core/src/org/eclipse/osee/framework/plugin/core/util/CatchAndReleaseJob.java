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
package org.eclipse.osee.framework.plugin.core.util;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Ryan D. Brooks
 */
public class CatchAndReleaseJob extends Job {
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

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
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
