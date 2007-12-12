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
package org.eclipse.osee.ats.health;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class OrphanedTasks extends XNavigateItemAction {

   /**
    * @param parent
    */
   public OrphanedTasks(XNavigateItem parent) {
      super(parent, "Report Orphaned Tasks");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new LoadArtifactsJob(getName()), true);
   }

   public class LoadArtifactsJob extends Job {

      private final String name;

      public LoadArtifactsJob(String name) {
         super(name);
         this.name = name;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         final XResultData rd = new XResultData(AtsPlugin.getLogger());
         final List<TaskArtifact> orphanedTasks = new ArrayList<TaskArtifact>();
         try {
            Collection<Artifact> arts =
                  ArtifactPersistenceManager.getInstance().getArtifactsFromSubtypeName(TaskArtifact.ARTIFACT_NAME,
                        BranchPersistenceManager.getInstance().getAtsBranch());
            int x = 0;
            for (Artifact art : arts) {
               TaskArtifact taskArt = (TaskArtifact) art;
               monitor.subTask("Checking task " + x++ + "/" + arts.size() + " - " + art.getHumanReadableId());
               if (taskArt.getParentSMA() == null) {
                  orphanedTasks.add(taskArt);
                  rd.logError("Orphaned => " + taskArt.getHumanReadableId());
               }
            }
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            rd.logError(ex.getLocalizedMessage());
         }
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               rd.report(name);
               WorldView.loadIt("Orphaned Tasks", orphanedTasks);
            }
         });
         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
