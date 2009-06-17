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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.ui.TestCoreGuiPlugin;
import org.eclipse.osee.ote.ui.markers.MarkerPlugin;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.connection.ScriptManager;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.models.OutputModel;
import org.eclipse.osee.ote.ui.test.manager.pages.contributions.TestManagerStorageKeys;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;

/**
 * @author Roberto E. Escobar
 */
public class StoreOutfileJob extends Job {

   

   private final ScriptManager userEnvironment;
   private final ScriptTask scriptTask;
   private boolean isValidRun;
   private final TestManagerEditor testManagerEditor;
   private final ITestEnvironment env;

   private String clientOutfilePath;

   private String serverOutfilePath;

   public StoreOutfileJob(ITestEnvironment env, TestManagerEditor testManagerEditor, ScriptManager userEnvironment, ScriptTask scriptTask, String clientOutfilePath, String serverOutfilePath, boolean isValidRun) {
      super("Store: " + scriptTask.getName());
      this.env = env;
      this.scriptTask = scriptTask;
      this.testManagerEditor = testManagerEditor;
      this.userEnvironment = userEnvironment;
      this.isValidRun = isValidRun;
      this.clientOutfilePath = clientOutfilePath;
      this.serverOutfilePath = serverOutfilePath;
   }

   public static void scheduleJob(Job job) {
      job.setUser(false);
      job.setPriority(Job.SHORT);
      job.schedule();
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         if (isValidRun == true) {
            try {
               storeOutfile(scriptTask);
            } catch (Exception e) {
               return new Status(Status.ERROR, TestManagerPlugin.PLUGIN_ID, "Failed to write out file to workspace", e);
            }
         }
//         scriptTask.computeExists();
         userEnvironment.updateScriptTableViewer(scriptTask);
         try {
            Thread.sleep(2000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
//         Display.getDefault().asyncExec(new Runnable() {
//            public void run() {
               processOutFile(scriptTask);
//            }
//         });
      } catch (Exception ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
      }
      return Status.OK_STATUS;
   }

   public void processOutFile(ScriptTask task) {
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "Processing Outfile: " + task.getName());
//      task.computeExists();
      File xmlSourceFile = task.getScriptModel().getOutputModel().getFile();
      IFile javaSourceIFile = task.getScriptModel().getIFile();

      if (!xmlSourceFile.exists()) {
         TestCoreGuiPlugin.getDefault().getConsole().writeError("Output File Not Created");
      } else {
         // Refresh the parent so the workspace knows the new tmo file exists
         AWorkspace.refreshResource(javaSourceIFile);
         task.getScriptModel().getOutputModel().updateTestPointsFromOutfile();
         int failedPoints = task.getScriptModel().getOutputModel().getFailedTestPoints();
         userEnvironment.updateScriptTableViewer(scriptTask);
         if (failedPoints > 0) {
            // Print fails in red, but don't force the console to popup
            TestCoreGuiPlugin.getDefault().getConsole().write(
                  String.format("Test Point Failures => %s[%d]", task.getName(), failedPoints), OseeConsole.CONSOLE_ERROR, false);
         }
      }
   }

   private boolean isKeepSavedOutfileEnabled() {
      return testManagerEditor.getPropertyStore().getBoolean(TestManagerStorageKeys.KEEP_OLD_OUTFILE_COPIES_ENABLED_KEY);
   }

   private void storeOutfile(ScriptTask scriptTask) throws Exception {
      if (clientOutfilePath.equals(serverOutfilePath) != true) {
         // the paths are different so we need to copy the file
         byte[] outBytes = env.getScriptOutfile(serverOutfilePath);
         if (outBytes != null && outBytes.length > 0) {

            if (isKeepSavedOutfileEnabled()) {
               moveOutputToNextAvailableSpot(scriptTask);
            }
            // else {
            // task.getScriptModel().getOutputModel().getIFile().delete(true, null);
            // }
            IFile file = AIFile.constructIFile(clientOutfilePath);
            if (file != null) {
               AIFile.writeToFile(file, new ByteArrayInputStream(outBytes));
               MarkerPlugin.getDefault().addMarkers(file);
            } else {
               Lib.writeBytesToFile(outBytes, new File(clientOutfilePath));
            }
         }
      }
   }

   private void moveOutputToNextAvailableSpot(ScriptTask task) {
      OutputModel outputModel = task.getScriptModel().getOutputModel();
      File oldFile = outputModel.getFile();
      if (oldFile != null && oldFile.exists() && oldFile.isFile() && oldFile.canRead()) {
         String outputExtension = "." + outputModel.getFileExtension();
         int fileNum = 1;
         File destFile =
               new File(oldFile.getAbsoluteFile().toString().replaceFirst(outputExtension,
                     "." + fileNum + outputExtension));
         if (destFile.exists()) {
            while (destFile.exists()) {
               fileNum++;
               destFile =
                     new File(oldFile.getAbsoluteFile().toString().replaceFirst(outputExtension,
                           "." + fileNum + outputExtension));
            }
         }
         try {
            Lib.copyFile(oldFile, destFile);
         } catch (IOException e2) {
            OseeLog.log(TestManagerPlugin.class, Level.SEVERE, "Failed to move output file to next available spot", e2);
         }
      }
   }
}