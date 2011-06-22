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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceRelationsWithBaselineOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceRelationsWithBaselineHandler extends CommandHandler {
   private List<Change> changes;

   @Override
   public Object executeWithException(ExecutionEvent event) {
      Set<Artifact> duplicateCheck = new HashSet<Artifact>();

      for (Change change : changes) {
         Artifact changeArtifact = change.getChangeArtifact();
         duplicateCheck.add(changeArtifact);
      }

      Shell shell = HandlerUtil.getActiveShell(event);
      boolean confirmed =
         MessageDialog.openConfirm(shell,
            "Confirm Replace with baseline version of " + duplicateCheck.size() + " attributes.",
            "All attribute and relation changes will be replaced with thier baseline version.");
      //select the correct artifact from a relation

      if (confirmed) {
         scheduelReplaceWithBaseline(duplicateCheck);
      }
      return null;
   }

   private void scheduelReplaceWithBaseline(final Collection<Artifact> artifacts) {
      IOperation operation = new ReplaceRelationsWithBaselineOperation(artifacts);
      Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            super.done(event);
            IStatus status = event.getResult();
            if (status.getSeverity() == IStatus.ERROR) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, status.getException());
            }
         }
      });
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection selection) throws OseeCoreException {
      boolean isEnabled = false;
      changes = Handlers.getArtifactChangesFromStructuredSelection(selection);
      for (Change change : changes) {
         isEnabled = AccessControlManager.hasPermission(change.getChangeArtifact(), PermissionEnum.WRITE);
         if (!isEnabled) {
            break;
         }
      }
      return isEnabled;
   }
}
