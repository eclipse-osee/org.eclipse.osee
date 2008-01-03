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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 * @author Paul K. Waldfogel
 */
public class OpenBranchAssociatedArtifactHandler extends AbstractSelectionChangedHandler {
   // private static final Logger logger =
   // ConfigUtil.getConfigFactory().getLogger(CreateSelectiveBranchHandler.class);
   // private static final AccessControlManager accessManager = AccessControlManager.getInstance();
   // private static final BranchPersistenceManager branchManager =
   // BranchPersistenceManager.getInstance();
   // private static final TransactionIdManager transactionIdManager =
   // TransactionIdManager.getInstance();
   // private TreeViewer branchTable;
   // private boolean selective;
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();

   /**
    * @param branchTable
    */
   public OpenBranchAssociatedArtifactHandler() {
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IStructuredSelection myIStructuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      List<Branch> mySelectedBranchList = Handlers.getBranchListFromStructuredSelection(myIStructuredSelection);
      Branch selectedBranch = (Branch) ((JobbedNode) myIStructuredSelection.getFirstElement()).getBackingData();
      if (selectedBranch != mySelectedBranchList.get(0)) {
         System.out.println("selectedBranch != mySelectedBranchList.get(0)");
      }
      try {
         if (selectedBranch.getAssociatedArtifact() == null) {
            AWorkbench.popup("Open Associated Artifact", "No artifact associated with branch " + selectedBranch);
            return null;
         }
         if (AccessControlManager.getInstance().checkObjectPermission(skynetAuth.getAuthenticatedUser(),
               selectedBranch.getAssociatedArtifact(), PermissionEnum.READ)) {
            if (selectedBranch.getAssociatedArtifact() instanceof IATSArtifact)
               OseeAts.openATSArtifact(selectedBranch.getAssociatedArtifact());
            else
               ArtifactEditor.editArtifact(selectedBranch.getAssociatedArtifact());
         } else {
            OSEELog.logInfo(
                  SkynetGuiPlugin.class,
                  "The user " + skynetAuth.getAuthenticatedUser() + " does not have read access to " + selectedBranch.getAssociatedArtifact(),
                  true);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      IStructuredSelection myIStructuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      return SkynetSelections.oneBranchSelected(myIStructuredSelection);
   }

}