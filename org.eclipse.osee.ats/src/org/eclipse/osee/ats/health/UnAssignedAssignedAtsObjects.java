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
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
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
public class UnAssignedAssignedAtsObjects extends XNavigateItemAutoRunAction implements IAutoRunTask {

   boolean fixIt = false;

   /**
    * @param parent
    */
   public UnAssignedAssignedAtsObjects(XNavigateItem parent) {
      super(parent, "Report UnAssigned AND Assigned / NoOne Assigned (partial fix available)");
   }

   public UnAssignedAssignedAtsObjects() {
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

   public class Report extends Job {

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
         try {
            final XResultData rd = new XResultData(AtsPlugin.getLogger());
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
               getUnassignedAtsObjectHelper(rd);
            }
         };
         txWrapper.execute();

      } else {
         getUnassignedAtsObjectHelper(rd);
      }
   }

   private void getUnassignedAtsObjectHelper(final XResultData rd)throws OseeCoreException, SQLException{
      User unAssignedUser = SkynetAuthentication.getUser(UserEnum.UnAssigned);
      User noOneUser = SkynetAuthentication.getUser(UserEnum.NoOne);

      Collection<Artifact> arts = StateMachineArtifact.getAllSMATypeArtifacts();
      for (Artifact art : arts) {
         StateMachineArtifact sma = (StateMachineArtifact) art;
         SMAManager smaMgr = new SMAManager(sma);
         if (smaMgr.getStateMgr().getAssignees().size() > 1) {
            if (smaMgr.getStateMgr().getAssignees().contains(unAssignedUser)) {
               rd.logError(art.getHumanReadableId() + " is unassigned and assigned => " + Artifacts.commaArts(smaMgr.getStateMgr().getAssignees()));
               if (fixIt) {
                  smaMgr.getStateMgr().removeAssignee(unAssignedUser);
               }
            }
         }
         for (String stateName : smaMgr.getStateMgr().getVisitedStateNames()) {
            Collection<User> assignees = smaMgr.getStateMgr().getAssignees(stateName);
            if (assignees.size() > 1 && assignees.contains(unAssignedUser)) {
               rd.logError(art.getHumanReadableId() + " state " + stateName + " is unassigned and assigned => " + Artifacts.commaArts(assignees));
               if (fixIt) {
                  smaMgr.getStateMgr().removeAssignee(stateName, unAssignedUser);
               }
            }
         }
         if (smaMgr.getStateMgr().getAssignees().contains(noOneUser)) {
            rd.logError(art.getHumanReadableId() + " is assigned to NoOne; invalid assignment - MANUAL FIX REQUIRED");
         }
         sma.persistAttributes();
      }
      rd.log("Completed processing " + arts.size() + " artifacts.");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#get24HourStartTime()
    */
   public String get24HourStartTime() {
      return "23:35";
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
      return getName();
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
