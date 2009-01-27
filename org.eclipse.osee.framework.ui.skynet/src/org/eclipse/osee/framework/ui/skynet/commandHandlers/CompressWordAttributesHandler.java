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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 */
public class CompressWordAttributesHandler extends AbstractHandler {
   private List<Artifact> artifacts;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      Jobs.startJob(new Job("Compress Word Attributes") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               int count = 0;
               final int total = artifacts.size();

               monitor.beginTask("Analyzing attributes", total);

               for (Artifact artifact : artifacts) {
                  if (WordUtil.revertNonusefulWordChanges(artifact.getArtId(), artifact.getBranch(),
                        "osee_compression_gammas")) count++;
                  monitor.worked(1);
                  if (monitor.isCanceled()) {
                     monitor.done();
                     return Status.CANCEL_STATUS;
                  }
               }

               final int finalCount = count;
               Displays.ensureInDisplayThread(new Runnable() {
                  public void run() {
                     MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                           "Compression Data", finalCount + " of the " + total + " artifacts need compression");
                  }
               });

               monitor.done();
               return Status.OK_STATUS;
            } catch (Exception ex) {
               return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
            }
         }

      });
      return null;
   }

   @Override
   public boolean isEnabled() {
      boolean enabled = false;
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }
      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

            if (!artifacts.isEmpty()) {
               boolean writePermission =
                     AccessControlManager.checkObjectPermission(artifacts.get(0), PermissionEnum.WRITE);
               enabled = writePermission && AccessControlManager.isOseeAdmin();
            }
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         enabled = false;
      }
      return enabled;
   }
}
