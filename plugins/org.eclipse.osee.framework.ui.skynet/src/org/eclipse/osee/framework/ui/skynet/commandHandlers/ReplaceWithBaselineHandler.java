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
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceArtifactWithBaselineOperation;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceAttributeWithBaselineOperation;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.replace.ReplaceWithBaselineVersionDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 * @author Karol Wilk
 */
public class ReplaceWithBaselineHandler extends AbstractHandler {
   private Collection<Change> changes;
   private IStructuredSelection structuredSelection;

   private final JobChangeAdapter adapter = new JobChangeAdapter() {
      @Override
      public void done(IJobChangeEvent event) {
         IStatus status = event.getResult();
         if (status != null && status.getSeverity() == IStatus.ERROR) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, status.getException());
         }
      }
   };

   @Override
   public boolean isEnabled() {
      boolean isEnabled = false;
      setSelectedData();

      LoadChangeType lastChangeType = null;
      changes = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);
      // Only use the item selected for accessControl
      if (structuredSelection != null) {
         for (Change change : changes) {
            if (lastChangeType == null) {
               lastChangeType = change.getChangeType();
            }
            isEnabled = lastChangeType == change.getChangeType();

            try {
               isEnabled =
                  isEnabled && AccessControlManager.hasPermission(change.getChangeArtifact(), PermissionEnum.WRITE);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error when checking permisstions", ex);
            }
            if (!isEnabled) {
               break;
            }
         }
      }
      return isEnabled;
   }

   private void setSelectedData() {
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (!workbench.isClosing() || !workbench.isStarting()) {
         try {
            ISelection selection =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
            structuredSelection = (IStructuredSelection) selection;
         } catch (Exception e) {
            structuredSelection = null;
            OseeLog.log(Activator.class, Level.WARNING, "Could not obtain replace selection from UI", e);
         }
      }
   }

   private boolean enableButtons(Collection<Change> changes, LoadChangeType changeType) {
      boolean attrEnabled = false;
      for (Change change : changes) {
         attrEnabled = change.getChangeType() == changeType;
         if (!attrEnabled) {
            break;
         }
      }
      return attrEnabled;
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (structuredSelection != null) {
         try {
            boolean attrEnabled = enableButtons(changes, LoadChangeType.attribute);
            boolean artEnabled = enableButtons(changes, LoadChangeType.artifact);

            ReplaceWithBaselineVersionDialog dialog = new ReplaceWithBaselineVersionDialog(artEnabled, attrEnabled);
            if (dialog.open() == Window.OK) {
               IOperation op =
                  dialog.isAttributeSelected() ? new ReplaceAttributeWithBaselineOperation(changes) : new ReplaceArtifactWithBaselineOperation(
                     changes, removeDuplicateArtifacts());

               Operations.executeAsJob(op, true, Job.LONG, adapter);
            }
         } catch (Exception ex) {
            throw new ExecutionException(ex.getMessage());
         }
      }
      return null;
   }

   private Set<Artifact> removeDuplicateArtifacts() {
      Set<Artifact> duplicateArtCheck = new HashSet<Artifact>();

      for (Change change : changes) {
         duplicateArtCheck.add(change.getChangeArtifact());
      }
      return duplicateArtCheck;
   }
}
