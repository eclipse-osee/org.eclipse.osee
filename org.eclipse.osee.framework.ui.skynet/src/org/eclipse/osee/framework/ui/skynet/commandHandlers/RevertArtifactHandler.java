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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.revert.RevertWizard;
import org.eclipse.osee.framework.ui.swt.NonmodalWizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class RevertArtifactHandler extends AbstractHandler {
   private List<ArtifactChange> artifactChanges;

   public RevertArtifactHandler() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      // This is serious stuff, make sure the user understands the impact.

      List<List<Artifact>> artifacts = new LinkedList<List<Artifact>>();
      Set<Artifact> duplicateCheck = new HashSet<Artifact>();

      for (ArtifactChange artifactChange : artifactChanges) {
         List<Artifact> artifactList = new LinkedList<Artifact>();
         if (!duplicateCheck.contains(artifactChange.getArtifact())) {
            artifactList.add(artifactChange.getArtifact());
            artifacts.add(artifactList);
            duplicateCheck.add(artifactChange.getArtifact());
         }
      }
      RevertWizard wizard = new RevertWizard(artifacts);
      NonmodalWizardDialog dialog = new NonmodalWizardDialog(Display.getCurrent().getActiveShell(), wizard);
      dialog.create();
      dialog.open();
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
            List<ArtifactChange> artifactChanges =
                  Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

            if (artifactChanges.isEmpty()) {
               return false;
            }

            this.artifactChanges = artifactChanges;

            for (ArtifactChange artifactChange : artifactChanges) {
               isEnabled =
                     AccessControlManager.checkObjectPermission(artifactChange.getArtifact(), PermissionEnum.WRITE);
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
