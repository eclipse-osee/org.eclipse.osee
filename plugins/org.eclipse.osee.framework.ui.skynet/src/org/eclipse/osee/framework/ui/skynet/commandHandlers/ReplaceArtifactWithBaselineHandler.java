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
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceArtifactWithBaselineOperation;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class ReplaceArtifactWithBaselineHandler extends AbstractHandler {
   private List<Change> changes;

   @Override
   public Object execute(ExecutionEvent event) {
      Set<Artifact> duplicateCheck = new HashSet<Artifact>();

      for (Change change : changes) {
         Artifact changeArtifact = change.getChangeArtifact();
         if (!duplicateCheck.contains(changeArtifact)) {
            duplicateCheck.add(changeArtifact);
         }

      }
      replaceWithBaseline(duplicateCheck);
      return null;
   }

   private void replaceWithBaseline(Collection<Artifact> artifacts) {
      if (MessageDialog.openConfirm(Displays.getActiveShell(),
         "Confirm Replace with baseline version of " + artifacts.size() + " attributes.",
         "All attribute and relation changes will be replaced with thier baseline version.")) {

         Operations.executeAsJob(new ReplaceArtifactWithBaselineOperation(artifacts), true);
      }
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
            changes = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

            if (changes.isEmpty()) {
               return false;
            }

            for (Change change : changes) {
               isEnabled = AccessControlManager.hasPermission(change.getChangeArtifact(), PermissionEnum.WRITE);
               if (!isEnabled) {
                  break;
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
         return false;
      }
      return isEnabled;
   }
}
