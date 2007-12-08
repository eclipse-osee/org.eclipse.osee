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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.swt.IContentProviderRunnable;

/**
 * @author Robert A. Fisher
 */
public class GetChildrenJob extends Job {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(GetChildrenJob.class);

   private final Viewer viewer;
   private final JobbedNode parent;
   private final IContentProviderRunnable runnable;

   /**
    * @param name
    * @param viewer
    * @param parent
    * @param runnable
    */
   public GetChildrenJob(String name, Viewer viewer, JobbedNode parent, IContentProviderRunnable runnable) {
      super("Fetching children" + (name == null ? "" : " for " + name));
      if (viewer == null) throw new IllegalArgumentException("view can not be null");
      if (parent == null) throw new IllegalArgumentException("parent can not be null.");
      if (runnable == null) throw new IllegalArgumentException("runnable can not be null.");

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
               public void run() {
                  // TODO figure out why this is having refresh troubles
                  //            if (viewer instanceof StructuredViewer && parent.getParent() != null)
                  //               ((StructuredViewer)viewer).refresh(parent.getParent());
                  //            else
                  viewer.refresh();
               }
            });
         } else {
            parent.cancelled();
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
         parent.cancelled(ex);
      } finally {
         watcher.done();
         monitor.done();
      }

      return Status.OK_STATUS;
   }
}
