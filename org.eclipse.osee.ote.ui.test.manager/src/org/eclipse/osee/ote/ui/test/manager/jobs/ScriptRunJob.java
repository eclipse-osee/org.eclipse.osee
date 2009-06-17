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
package org.eclipse.osee.ote.ui.test.manager.jobs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class ScriptRunJob extends Job {
   private static final String JOB_NAME = "Script Run Job";

   private final TestManagerEditor testManagerEditor;
   private final List<ScriptTask> runTasks;

   public ScriptRunJob(TestManagerEditor testManagerEditor) {
      super(JOB_NAME);
      this.testManagerEditor = testManagerEditor;
      this.testManagerEditor.doSave();
      ScriptPage scriptPage = getScriptPage();
      scriptPage.getScriptTableViewer().refresh();
      this.runTasks = scriptPage.getScriptTableViewer().getRunTasks();
   }

   public IStatus verifyOutfileLocations() {
      final LinkedList<IStatus> failedLocations = new LinkedList<IStatus>();
      for (ScriptTask task : runTasks) {
         final String fileName = task.getScriptModel().getOutputModel().getRawFilename();
         final File file = new File(fileName);
         if (file.exists() && (!file.canWrite() || !file.canRead())) {
            failedLocations.add(new Status(Status.ERROR, TestManagerPlugin.PLUGIN_ID, "could not access " + fileName));
         } else if (!file.getParentFile().canWrite()) {
            failedLocations.add(new Status(Status.ERROR, TestManagerPlugin.PLUGIN_ID, "could not access " + fileName));
         }
      }
      if (failedLocations.isEmpty()) {
         return Status.OK_STATUS;
      } else {
         return new Status(Status.ERROR, TestManagerPlugin.PLUGIN_ID, "unable to access out files") {

            /* (non-Javadoc)
             * @see org.eclipse.core.runtime.Status#isMultiStatus()
             */
            @Override
            public boolean isMultiStatus() {
               return true;
            }

            /* (non-Javadoc)
             * @see org.eclipse.core.runtime.Status#getChildren()
             */
            @Override
            public IStatus[] getChildren() {
               return failedLocations.toArray(new IStatus[failedLocations.size()]);
            }

         };
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      final IStatus status = verifyOutfileLocations();

      if (status != Status.OK_STATUS) {
         Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
               ErrorDialog.openError(
                     Display.getDefault().getActiveShell(),
                     "Script Run Error",
                     "Could not access some out file locations. Check access permissions. Click Details to see a list of failed locations",
                     status, -1);
            }

         });

         return Status.OK_STATUS;
      }

      long time = System.currentTimeMillis();

      clearMarkers();
      getScriptPage().onScriptRunning(true);

      long elapsed = System.currentTimeMillis() - time;
      OseeLog.log(TestManagerPlugin.class, Level.FINE, String.format("%d milliseconds to initialize the running of scripts.", elapsed));
      OseeLog.log(TestManagerPlugin.class, Level.INFO, String.format("%d scripts have been batched.", runTasks.size()));

      Display.getDefault().syncExec(new Runnable() {

         @Override
         public void run() {
             getScriptPage().getScriptManager().addTestsToQueue(runTasks);
         }

      });
      toReturn = Status.OK_STATUS;
      return toReturn;
   }

   private ScriptPage getScriptPage() {
      return this.testManagerEditor.getPageManager().getScriptPage();
   }

   public boolean isRunAllowed() {

      return this.testManagerEditor.getPageManager().areSettingsValidForRun();

   }

   private void clearMarkers() {
      // TODO can we somehow wait until the script is actually run to remove
      // the markers? Otherwise if the run is aborted before the script
      // runs...

      // Remove markers from scripts to be run
      // for (ScriptTask task : runTasks) {
      // try {
      // MarkerSupport.deleteMarkersFromInputFile(task.getScriptModel().getIFile());
      // }
      // catch (Exception ex) {
      // OseeLog.log(Activator.class, Level.SEVERE, "Unable to clear the tests markers before
      // running the test.", ex);
      // }
      // }
   }

}
