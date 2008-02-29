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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
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
public class DuplicateUsersItem extends XNavigateItemAutoRunAction implements IAutoRunTask {

   /**
    * @param parent
    */
   public DuplicateUsersItem(XNavigateItem parent) {
      super(parent, "Report Duplicate Users in DB");
   }

   public DuplicateUsersItem() {
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
         try {
            XResultData rd = new XResultData(AtsPlugin.getLogger());
            runIt(monitor, rd);
            rd.report(getName());
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private void runIt(IProgressMonitor monitor, XResultData rd) throws Exception {
      Set<User> users = new HashSet<User>();
      Collection<Artifact> userArts =
            ArtifactPersistenceManager.getInstance().getArtifactsFromSubtypeName("User",
                  BranchPersistenceManager.getInstance().getAtsBranch());
      for (Artifact user : userArts) {
         if (!users.add((User) user)) {
            User otherUser = null;
            for (User userArt : users)
               if (userArt.equals(user)) {
                  otherUser = userArt;
                  break;
               }
            rd.logError("Duplicate User => " + ((User) user).getUserId() + " " + user.getHumanReadableId() + " and " + otherUser.getUserId() + " " + otherUser.getHumanReadableId());
         }
      }
      rd.log("Completed processing " + userArts.size() + " artifacts.");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#get24HourStartTime()
    */
   public String get24HourStartTime() {
      return "23:15";
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
      return "Ensure there are no duplicpate users in the DB.";
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getNotificationEmailAddresses()
    */
   public String[] getNotificationEmailAddresses() {
      return new String[] {"donald.g.dunne@boeing.com, ryan.d.brooks@boeing.com"};
   }

}
