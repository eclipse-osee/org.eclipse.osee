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
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class PurgeArtifactHandler extends CommandHandler {
   private List<Artifact> artifacts;

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
         "Confirm Artifact Purge ",
         " Are you sure you want to purge this artifact, all of " + "its children and all history associated with these artifacts from the database ?")) {
         Job job = new Job("Purge artifact") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
               IStatus toReturn = Status.CANCEL_STATUS;
               monitor.beginTask("Purge artifact", artifacts.size());
               try {
                  Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
                  toReturn = Status.OK_STATUS;
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
                  toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
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
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
      return AccessControlManager.isOseeAdmin() && AccessControlManager.hasPermission(artifacts, PermissionEnum.WRITE);
   }
}
