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
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.FileChangeEvent;
import org.eclipse.osee.framework.skynet.core.utility.IFileWatcherListener;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEditFileWatcher implements IFileWatcherListener {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.IFileWatcherListener#filesModified(java.util.Collection)
    */
   @Override
   public void filesModified(Collection<FileChangeEvent> fileChangeEvents) {
      for (FileChangeEvent event : fileChangeEvents) {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
               event.getChangeType().name() + ": " + event.getFile().getAbsolutePath());

         File file = event.getFile();
         if (file.exists()) {
            UpdateArtifactJob updateJob = new UpdateArtifactJob();
            updateJob.setWorkingFile(file);
            Jobs.startJob(updateJob);
         }
      }
   }
}