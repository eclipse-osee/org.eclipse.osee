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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.FileWatcher;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

final class ArtifactEditFileWatcher extends FileWatcher {

   private final IArtifactUpdateOperationFactory opFactory;

   public ArtifactEditFileWatcher(IArtifactUpdateOperationFactory opFactory, long time, TimeUnit unit) {
      super(time, unit);
      this.opFactory = opFactory;
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
               IOperation op = opFactory.createUpdateOp(file);
               Operations.executeAsJob(op, false, Job.LONG, new JobChangeAdapter() {

                  @Override
                  public void done(IJobChangeEvent event) {
                     if (event.getResult().isOK()) {
                        OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
                           "Updated artifact linked to: " + file.getAbsolutePath());
                     }
                  }
               });
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }
}