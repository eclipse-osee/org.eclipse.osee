/*
 * Created on Nov 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.util;

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
