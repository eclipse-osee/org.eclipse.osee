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

package org.eclipse.osee.define.health;

import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class BranchCommitRegressionTest extends XNavigateItemAction implements IAutoRunTask {

   private String autoRunUniqueId;
   public static String NAME = "CommitRegressionTest - Run on Test";

   /**
    * @param parent
    */
   public BranchCommitRegressionTest(XNavigateItem parent) {
      super(parent, NAME);
   }

   public BranchCommitRegressionTest() {
      this(null);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new TraxInterfaceRegressionTestJob(getName()), true);
   }

   public class TraxInterfaceRegressionTestJob extends Job {
      XResultData rd = new XResultData(DefinePlugin.getLogger());
      private final String jobName;

      public TraxInterfaceRegressionTestJob(String jobName) {
         super(jobName);
         this.jobName = jobName;
      }

      public IStatus run(IProgressMonitor monitor) {
         try {
            runTest(rd, monitor);
            rd.report(getName());
            monitor.done();
            return Status.OK_STATUS;
         } catch (Exception ex) {
            OSEELog.logException(DefinePlugin.class, ex, false);
            return new Status(Status.ERROR, DefinePlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
      }

   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    * 
    * @param monitor
    * @throws Exception
    */
   public static void runTest(XResultData rd, IProgressMonitor monitor) throws Exception {
      if (monitor != null) monitor.subTask(NAME);
      rd.logError("This is an error");
      rd.logWarning("This is a warning");
      if (monitor != null) monitor.done();
      rd.log("Complete");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#startTasks()
    */
   public void startTasks(XResultData resultData) throws Exception {
      BranchCommitRegressionTest.runTest(resultData, null);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#get24HourStartTime()
    */
   public String get24HourStartTime() {
      return "00:08";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getNotificationEmailAddresses()
    */
   public String[] getNotificationEmailAddresses() {
      return new String[] {"jeff.c.phillips@boeing.com"};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getAutoRunUniqueId()
    */
   public String getAutoRunUniqueId() {
      return autoRunUniqueId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#setAutoRunUniqueId(java.lang.String)
    */
   public void setAutoRunUniqueId(String autoRunUniqueId) {
      this.autoRunUniqueId = autoRunUniqueId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getDescription()
    */
   public String getDescription() {
      return "Test Branch Commit Logic";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getRunDb()
    */
   public RunDb getRunDb() {
      return RunDb.Test_Db;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getTaskType()
    */
   public TaskType getTaskType() {
      return TaskType.Regression;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getCategory()
    */
   public String getCategory() {
      return "OSEE Define";
   }

}
