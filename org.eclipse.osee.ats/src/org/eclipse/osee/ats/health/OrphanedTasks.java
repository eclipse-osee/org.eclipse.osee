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
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAutoRunAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class OrphanedTasks extends XNavigateItemAutoRunAction implements IAutoRunTask {

   /**
    * @param parent
    */
   public OrphanedTasks(XNavigateItem parent) {
      super(parent, "Report Orphaned Tasks");
   }

   public OrphanedTasks() {
      this(null);
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
         try {
            final List<TaskArtifact> orphanedTasks = runIt(monitor, rd);
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  rd.report(name);
                  WorldView.loadIt("Orphaned Tasks", orphanedTasks);
               }
            });
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            rd.logError(ex.getLocalizedMessage());
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private List<TaskArtifact> runIt(IProgressMonitor monitor, XResultData rd) throws Exception {
      final List<TaskArtifact> orphanedTasks = new ArrayList<TaskArtifact>();
      Collection<Artifact> arts =
            ArtifactPersistenceManager.getInstance().getArtifactsFromSubtypeName(TaskArtifact.ARTIFACT_NAME,
                  BranchPersistenceManager.getInstance().getAtsBranch());
      int x = 0;
      for (Artifact art : arts) {
         TaskArtifact taskArt = (TaskArtifact) art;
         if (monitor != null) monitor.subTask("Checking task " + x++ + "/" + arts.size() + " - " + art.getHumanReadableId());
         if (taskArt.getParentSMA() == null) {
            orphanedTasks.add(taskArt);
            rd.logError("Orphaned => " + taskArt.getHumanReadableId());
         }
      }
      rd.log("Completed processing " + arts.size() + " artifacts.");
      return orphanedTasks;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#get24HourStartTime()
    */
   public String get24HourStartTime() {
      return "23:25";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#getCategory()
    */
   public String getCategory() {
      return "OSEE ATS";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#getDescription()
    */
   public String getDescription() {
      return "Ensure all Task artifacts have parents.";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#getRunDb()
    */
   public RunDb getRunDb() {
      return IAutoRunTask.RunDb.Production_Db;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#getTaskType()
    */
   public TaskType getTaskType() {
      return IAutoRunTask.TaskType.Db_Health;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#startTasks(org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData)
    */
   public void startTasks(XResultData resultData) throws Exception {
      runIt(null, resultData);
   }
}
