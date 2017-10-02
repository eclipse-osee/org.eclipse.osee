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
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.FileChangeEvent;
import org.eclipse.osee.framework.jdk.core.util.io.FileChangeType;
import org.eclipse.osee.framework.jdk.core.util.io.FileWatcher;
import org.eclipse.osee.framework.jdk.core.util.io.IFileWatcherListener;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

final class ArtifactFileMonitor implements IFileWatcherListener {
   private final ResourceAttributes readonlyfileAttributes;
   private final FileWatcher watcher;
   private final boolean firstTime;
   private final Map<File, IOperation> fileMap = new ConcurrentHashMap<>(128);

   public ArtifactFileMonitor() {
      firstTime = true;
      readonlyfileAttributes = new ResourceAttributes();
      readonlyfileAttributes.setReadOnly(true);

      watcher = new FileWatcher(3, TimeUnit.SECONDS);
      watcher.addListener(this);
      watcher.start();
   }

   public void addFile(File file, IOperation updateOperation) {
      watcher.addFile(file);
      fileMap.put(file, updateOperation);
   }

   public void markAsReadOnly(IFile file) {
      try {
         file.setResourceAttributes(readonlyfileAttributes);
      } catch (CoreException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   @Override
   public void filesModified(Collection<FileChangeEvent> fileChangeEvents) {
      for (FileChangeEvent event : fileChangeEvents) {
         if (event.getChangeType() == FileChangeType.MODIFIED) {
            processFileUpdate(event.getFile());
         }
      }
   }

   private void processFileUpdate(final File file) {
      IOperation op = fileMap.get(file);
      Operations.executeAsJob(op, false, Job.LONG, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            if (event.getResult().isOK()) {
               OseeLog.log(Activator.class, Level.INFO, "Updated artifact linked to: " + file.getAbsolutePath());
            }
         }
      });
   }

   @Override
   public void handleException(Exception ex) {
      OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
   }
}