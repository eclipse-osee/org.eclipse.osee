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
package org.eclipse.osee.ote.ui.define.jobs;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.ui.define.internal.Activator;
import org.eclipse.osee.ote.ui.define.viewers.XViewerDataManager;

/**
 * @author Roberto E. Escobar
 */
public class AddArtifactsToViewerJob extends Job {
   private static final String JOB_NAME = "Adding Artifacts to Table";
   private final List<Artifact> artifacts;
   private final XViewerDataManager viewerDataManager;

   public AddArtifactsToViewerJob(XViewerDataManager viewerDataManager, final List<Artifact> artifacts) {
      super(JOB_NAME);
      this.artifacts = artifacts;
      this.viewerDataManager = viewerDataManager;
      setUser(true);
      setPriority(Job.LONG);
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      monitor.beginTask(getName(), artifacts.size());
      try {
         viewerDataManager.addArtifacts(monitor, artifacts);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
      }
      if (monitor.isCanceled() != true) {
         toReturn = Status.OK_STATUS;
      }
      monitor.done();
      return toReturn;
   }
}
