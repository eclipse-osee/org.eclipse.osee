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
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAutoRunAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class AssignedActiveActions extends XNavigateItemAutoRunAction implements IAutoRunTask {

   boolean fixIt = true;

   /**
    * @param parent
    */
   public AssignedActiveActions(XNavigateItem parent) {
      super(parent, "Report Assigned Active Actions (fix available)");
   }

   public AssignedActiveActions() {
      this(null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   private final class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         IStatus toReturn = Status.CANCEL_STATUS;
         final XResultData rd = new XResultData(AtsPlugin.getLogger());
         try {
            runIt(monitor, rd);
            rd.report(getName());
            toReturn = Status.OK_STATUS;
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            toReturn = new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         } finally {
            monitor.done();
         }
         return toReturn;
      }
   }

   private void runIt(IProgressMonitor monitor, final XResultData rd)throws OseeCoreException, SQLException{
      if (fixIt) {
         AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
            @Override
            protected void handleTxWork()throws OseeCoreException, SQLException{
               assignedActiveActionsHelper(rd);
            }
         };
         txWrapper.execute();
      } else {
         assignedActiveActionsHelper(rd);
      }
   }

   private void assignedActiveActionsHelper(XResultData rd)throws OseeCoreException, SQLException{
      Set<String> artTypeNames = TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames();
      artTypeNames.add(TaskArtifact.ARTIFACT_NAME);
      Collection<Artifact> artifacts = new ArrayList<Artifact>();
      for (String artifactTypeName : artTypeNames) {
         artifacts.addAll(ArtifactQuery.getArtifactsFromType(artifactTypeName, BranchPersistenceManager.getAtsBranch()));
      }
      for (Artifact art : artifacts) {
         StateMachineArtifact sma = (StateMachineArtifact) art;
         SMAManager smaMgr = new SMAManager(sma);
         if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && smaMgr.getStateMgr().getAssignees().size() > 0) {
            rd.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " cancel/complete with assignees");
            if (fixIt) {
               smaMgr.getStateMgr().clearAssignees();
               smaMgr.getSma().persistAttributes();
               rd.log("Fixed");
            }
         } else if ((!smaMgr.isCompleted() && !smaMgr.isCancelled()) && smaMgr.getStateMgr().getAssignees().size() == 0) {
            rd.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " In Work without assignees");
         }
      }
      rd.log("Completed processing " + artifacts.size() + " artifacts.");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#get24HourStartTime()
    */
   public String get24HourStartTime() {
      return "23:05";
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
      return "Ensure active Actions have at least one assignee";
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
   public void startTasks(XResultData resultData)throws OseeCoreException, SQLException{
      runIt(null, resultData);
   }

}
