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
import java.util.concurrent.TimeUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.skynet.core.utility.FileWatcher;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

final class ArtifactFileMonitor {
   private final ResourceAttributes readonlyfileAttributes;
   private final FileWatcher watcher;
   private boolean firstTime;
   private boolean workbenchSavePopUpDisabled;

   public ArtifactFileMonitor(IArtifactUpdateOperationFactory jobFactory) {
      firstTime = true;
      readonlyfileAttributes = new ResourceAttributes();
      readonlyfileAttributes.setReadOnly(true);

      watcher = new ArtifactEditFileWatcher(jobFactory, 3, TimeUnit.SECONDS);
      watcher.start();
   }

   public void addFile(IFile file) {
      monitorFile(file.getLocation().toFile());
   }

   public void markAsReadOnly(IFile file) throws OseeCoreException {
      try {
         file.setResourceAttributes(readonlyfileAttributes);
      } catch (CoreException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public boolean isWorkbenchSavePopUpDisabled() {
      return workbenchSavePopUpDisabled;
   }

   public void setWorkbenchSavePopUpDisabled(boolean workbenchSavePopUpDisabled) {
      this.workbenchSavePopUpDisabled = workbenchSavePopUpDisabled;
   }

   private void monitorFile(File file) {
      watcher.addFile(file);
      if (firstTime) {
         firstTime = false;

         if (!workbenchSavePopUpDisabled) {
            PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {

               @Override
               public void postShutdown(IWorkbench workbench) {
                  // do nothing
               }

               @Override
               public boolean preShutdown(IWorkbench workbench, boolean forced) {
                  boolean wasConfirmed =
                     MessageDialog.openConfirm(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "OSEE Edit",
                        "OSEE artifacts were opened for edit. Please save all external work before continuing. Click OK to continue shutdown process or Cancel to abort.");
                  return forced || wasConfirmed;
               }
            });
         }
      }
   }
}