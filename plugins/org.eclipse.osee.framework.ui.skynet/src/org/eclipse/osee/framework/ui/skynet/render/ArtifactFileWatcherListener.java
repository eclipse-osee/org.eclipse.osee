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
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.io.FileChangeEvent;
import org.eclipse.osee.framework.jdk.core.util.io.FileChangeType;
import org.eclipse.osee.framework.jdk.core.util.io.IFileWatcherListener;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactFileWatcherListener implements IFileWatcherListener {
   private final IArtifactUpdateOperationFactory opFactory;

   public ArtifactFileWatcherListener(IArtifactUpdateOperationFactory opFactory) {
      this.opFactory = opFactory;
   }

   @Override
   public void filesModified(Collection<FileChangeEvent> fileChangeEvents) {
      for (FileChangeEvent event : fileChangeEvents) {
         if (event.getChangeType() == FileChangeType.MODIFIED) {
            try {
               processFileUpdate(event.getFile());
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }

   private void processFileUpdate(final File file) throws OseeCoreException {
      IOperation op = opFactory.createUpdateOp(file);
      Operations.executeAsJob(op, false, Job.LONG, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            if (event.getResult().isOK()) {
               OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Updated artifact linked to: " + file.getAbsolutePath());
            }
         }
      });
   }

   @Override
   public void handleException(Exception ex) {
      OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
   }
}
