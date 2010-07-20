/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.utility.FileWatcher;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

final class ArtifactEditFileWatcher extends FileWatcher {

   public ArtifactEditFileWatcher(long time, TimeUnit unit) {
      super(time, unit);
   }

   @Override
   public synchronized void run() {
      try {
         for (Map.Entry<File, Long> entry : filesToWatch.entrySet()) {
            final File file = entry.getKey();
            final Long storedLastModified = entry.getValue();

            Long latestLastModified = file.lastModified();
            boolean requiresUpdate = false;
            if (!storedLastModified.equals(latestLastModified)) {
               entry.setValue(latestLastModified);
               if (file.exists()) {
                  requiresUpdate = true;
               }
            }

            if (requiresUpdate) {
               UpdateArtifactJob updateJob = new UpdateArtifactJob();
               updateJob.setWorkingFile(file);
               updateJob.addJobChangeListener(new JobChangeAdapter() {

                  @Override
                  public void done(IJobChangeEvent event) {
                     if (event.getResult().isOK()) {
                        OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
                              "Updated artifact linked to: " + file.getAbsolutePath());
                     }
                  }
               });
               Jobs.startJob(updateJob);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }
}