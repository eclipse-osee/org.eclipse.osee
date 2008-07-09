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

import java.sql.SQLException;
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
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class PurgeArtifactHandler extends AbstractHandler {
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   private List<Artifact> artifacts;

   /**
    * 
    */
   public PurgeArtifactHandler() {
      super();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (MessageDialog.openConfirm(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Confirm Artifact Purge ",
            " Are you sure you want to purge this artifact, all of " + "its children and all history associated with these artifacts from the database ?")) {
         Job job = new Job("Purge artifact") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               IStatus toReturn = Status.CANCEL_STATUS;
               monitor.beginTask("Purge artifact", artifacts.size());
               final IProgressMonitor fMonitor = monitor;

               AbstractSkynetTxTemplate purgeTx =
                     new AbstractSkynetTxTemplate(artifacts.iterator().next().getBranch()) {
                        @Override
                        protected void handleTxWork() throws OseeCoreException, SQLException {
                           for (Artifact artifactToPurge : artifacts) {
                              if (!artifactToPurge.isDeleted()) {
                                 fMonitor.setTaskName("Purge: " + artifactToPurge.getDescriptiveName());
                                 artifactToPurge.purgeFromBranch();
                              }
                              fMonitor.worked(1);
                           }
                           fMonitor.done();
                        }
                     };

               // Perform the purge transaction
               try {
                  purgeTx.execute();
                  toReturn = Status.OK_STATUS;
               } catch (Exception ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, false);
                  toReturn = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, ex.getMessage(), ex);
               } finally {
                  monitor.done();
               }
               return toReturn;
            }
         };
         Jobs.startJob(job);
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }
      boolean isEnabled = false;

      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
         isEnabled =
               OseeProperties.isDeveloper() && accessControlManager.checkObjectListPermission(artifacts,
                     PermissionEnum.WRITE);
      }
      return isEnabled;
   }
}
