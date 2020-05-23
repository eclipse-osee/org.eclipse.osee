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

package org.eclipse.osee.framework.ui.plugin.util;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IContentProviderRunnable;

/**
 * @author Robert A. Fisher
 */
public class GetChildrenJob extends Job {
   private final Viewer viewer;
   private final JobbedNode parent;
   private final IContentProviderRunnable runnable;

   public GetChildrenJob(String name, Viewer viewer, JobbedNode parent, IContentProviderRunnable runnable) {
      super("Fetching children" + (name == null ? "" : " for " + name));
      if (viewer == null) {
         throw new IllegalArgumentException("view can not be null");
      }
      if (parent == null) {
         throw new IllegalArgumentException("parent can not be null.");
      }
      if (runnable == null) {
         throw new IllegalArgumentException("runnable can not be null.");
      }

      this.viewer = viewer;
      this.parent = parent;
      this.runnable = runnable;
   }

   @Override
   protected final IStatus run(IProgressMonitor monitor) {
      CancelWatcher watcher = new CancelWatcher(getThread(), monitor);
      watcher.start();
      try {
         Object[] children = runnable.run(parent.getBackingData());

         if (!monitor.isCanceled()) {
            parent.setChildren(children);

            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  viewer.refresh();
               }
            });
         } else {
            parent.cancelled();
         }
      } catch (Exception ex) {
         OseeLog.log(UiPluginConstants.class, Level.SEVERE, ex);
         parent.cancelled(ex);
      } finally {
         watcher.done();
         monitor.done();
      }

      return Status.OK_STATUS;
   }
}
