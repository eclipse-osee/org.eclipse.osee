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
package org.eclipse.osee.ote.ui.test.manager.batches.navigate;

import java.io.File;
import java.net.URI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.configuration.LoadConfigurationOperation;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.util.PluginUtil;
import org.eclipse.osee.ote.ui.test.manager.util.TestManagerSelectDialog;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
final class TestBatchSetupItem extends XNavigateItem implements Runnable {
   private URI testBatchFile;
   private String jobName;

   public TestBatchSetupItem(XNavigateItem parent, String name, OseeImage oseeImage, URI testBatchFile) {
      super(parent, name, oseeImage);
      this.jobName = String.format("Test Manager Configuration: [%s]", name);
      this.testBatchFile = testBatchFile;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Runnable#run()
    */
   public void run() {
      Job job = new UIJob(jobName) {
         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status = Status.CANCEL_STATUS;
            if (PluginUtil.areTestManagersAvailable() != true) {
               Exception exception =
                     new IllegalStateException("Test Manager was not opened before this operation was selected.");
               status =
                     new Status(Status.ERROR, TestManagerPlugin.PLUGIN_ID, Status.ERROR,
                           "A Test Manager must be opened for this operation to work.", exception);
            } else {
               TestManagerEditor[] itemsToOpen = PluginUtil.getTestManagers();
               if (itemsToOpen.length > 1) {
                  itemsToOpen = TestManagerSelectDialog.getTestManagerFromUser();
               }

               if (itemsToOpen.length > 0) {
                  boolean result = configureSelectedItems(itemsToOpen);
                  if (result != false) {
                     status = Status.OK_STATUS;
                  } else {
                     Exception exception = new IllegalStateException("Test Manager setup failed.");
                     status =
                           new Status(Status.ERROR, TestManagerPlugin.PLUGIN_ID,
                                 "Test Manager configuration failed for some unknown reason. Please try again.",
                                 exception);
                  }
               } else {
                  Exception exception = new IllegalStateException("No Test Manager was selected.");
                  status =
                        new Status(Status.ERROR, TestManagerPlugin.PLUGIN_ID,
                              "A Test Manager must be selected for this operation to work.", exception);
               }
            }
            return status;
         }

         private boolean configureSelectedItems(TestManagerEditor[] items) {
            boolean result = true;
            for (TestManagerEditor testManager : items) {
               result &= LoadConfigurationOperation.load(testManager, new File(testBatchFile));
            }
            return result;
         }
      };
      job.setUser(true);
      job.schedule();
   }
}
