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

   public ArtifactFileMonitor() {
      firstTime = true;
      readonlyfileAttributes = new ResourceAttributes();
      readonlyfileAttributes.setReadOnly(true);

      watcher = new ArtifactEditFileWatcher(3, TimeUnit.SECONDS);
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