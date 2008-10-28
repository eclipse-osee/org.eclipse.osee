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
package org.eclipse.osee.framework.svn;

import java.util.logging.Level;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class CheckoutJob extends Job {
   private String[] fileToCheckout;

   public CheckoutJob(String jobName, String[] fileToCheckout) {
      super(jobName);
      this.fileToCheckout = fileToCheckout;
   }

   @Override
   protected IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn = Status.OK_STATUS;
      try {
         ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) throws CoreException {
               VersionControl.getInstance().checkOut(fileToCheckout, monitor);
            }
         }, this.getRule(), IWorkspace.AVOID_UPDATE, monitor);
      } catch (Exception ex) {
         OseeLog.log(SvnActivator.class, Level.SEVERE, ex);
         toReturn = new Status(Status.ERROR, SvnActivator.PLUGIN_ID, "Unable to Checkout Files", ex);
      }
      return toReturn;
   }
}
