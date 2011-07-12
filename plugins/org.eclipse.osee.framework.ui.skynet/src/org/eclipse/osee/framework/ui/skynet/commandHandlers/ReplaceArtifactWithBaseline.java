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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceArtifactWithBaseline extends AbstractHandler {
   private List<Artifact> artifacts;

   @Override
   public Object execute(ExecutionEvent event) {
      if (MessageDialog.openConfirm(Displays.getActiveShell(),
         "Confirm Replace with baseline version of " + artifacts.size() + " attributes.",
         "All attribute changes selected will be replaced with thier baseline version.")) {
         for (Artifact artifact : artifacts) {
            try {
               TransactionRecord baselineTransactionRecord = artifact.getBranch().getBaseTransaction();
               for (Change change : ChangeManager.getChangesPerArtifact(artifact, new NullProgressMonitor())) {
                  if (change.getTxDelta().getEndTx().getId() == baselineTransactionRecord.getId()) {
                     //These should be the baseline items only
                     if (change.getItemKind().equals("Attribute")) {
                        artifact.getAttributeById(change.getItemId(), true).replaceWithVersion((int) change.getGamma());
                     } else if (change.getItemKind().equals("Relation")) {
                        RelationChange relationChange = (RelationChange) change;
                        RelationManager.getLoadedRelationById(relationChange.getItemId(), relationChange.getArtId(),
                           relationChange.getBArtId(), artifact.getBranch()).repplaceWithVersion(
                           (int) change.getGamma());
                     }
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      boolean isEnabled = false;
      try {
         ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            this.artifacts = Handlers.processSelectionObjects(Artifact.class, structuredSelection);

            if (artifacts.isEmpty()) {
               return false;
            }

            for (Artifact artifact : artifacts) {
               isEnabled = AccessControlManager.hasPermission(artifact, PermissionEnum.WRITE);
               if (!isEnabled) {
                  break;
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
      return isEnabled;
   }
}
