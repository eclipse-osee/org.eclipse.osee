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
import java.util.logging.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class ShowArtifactInResourceHandler extends AbstractSelectionChangedHandler {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ShowArtifactInResourceHandler.class);
   private static final BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   private static final ArtifactPersistenceManager artifactPersistenceManager =
         ArtifactPersistenceManager.getInstance();
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   private List<Artifact> artifacts;

   public ShowArtifactInResourceHandler() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      for (Artifact artifact : artifacts) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         try {
            RevisionHistoryView revisionHistoryView =
                  (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, artifact.getGuid(),
                        IWorkbenchPage.VIEW_ACTIVATE);
            revisionHistoryView.explore(artifactPersistenceManager.getArtifact(artifact.getGuid(), artifact.getBranch()));
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
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

      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

         if (artifacts.isEmpty()) {
            return false;
         }

         boolean readPermission = true;
         boolean reportBranch = true;

         for (Artifact artifact : artifacts) {
            readPermission &= accessControlManager.checkObjectPermission(artifact, PermissionEnum.READ);
            reportBranch &= (artifact.getBranch() == branchPersistenceManager.getDefaultBranch());
         }
         isEnabled = readPermission && reportBranch;
      }
      return isEnabled;
   }
}
