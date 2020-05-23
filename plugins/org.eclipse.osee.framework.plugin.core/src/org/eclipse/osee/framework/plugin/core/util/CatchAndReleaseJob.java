/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
         return new Status(IStatus.ERROR, pluginId, IStatus.OK, message, ex);
      }
      return Status.OK_STATUS;
   }
}
