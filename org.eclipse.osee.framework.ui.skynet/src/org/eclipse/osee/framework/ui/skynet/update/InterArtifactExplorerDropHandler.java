/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactUpdateHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class InterArtifactExplorerDropHandler {
   private static final String IS_ARTIFACT_ON_BRANCH =
         "select 'x' from osee_artifact_version av1, osee_txs txs1, osee_tx_details txd1 where av1.art_id = ? and av1.gamma_id = txs1.gamma_id and txs1.transaction_id = txd1.transaction_id and txd1.branch_id = ?";

   public void dropArtifactIntoDifferentBranch(Artifact destinationParentArtifact, Artifact[] sourceArtifacts) throws OseeCoreException {
      if (destinationParentArtifact == null || sourceArtifacts == null || sourceArtifacts.length < 1) {
         throw new OseeCoreException("");
      }

      if (AccessControlManager.checkObjectPermission(destinationParentArtifact.getBranch(), PermissionEnum.WRITE) && AccessControlManager.checkObjectPermission(
            sourceArtifacts[0].getBranch(), PermissionEnum.READ)) {

         List<Artifact> descendents = destinationParentArtifact.getDescendants();
         List<Integer> artifactIds = new ArrayList<Integer>();
         artifactIds.add(destinationParentArtifact.getArtId());

         for (Artifact artifact : descendents) {
            artifactIds.add(artifact.getArtId());
         }

         List<Artifact> updateSelectedArtifacts = new LinkedList<Artifact>();
         List<Artifact> updateArtifactsFoundOnBranch = new LinkedList<Artifact>();
         List<Artifact> newBaselineArtifacts = new LinkedList<Artifact>();
         List<Artifact> newNonBaselineArtifacts = new LinkedList<Artifact>();
         Branch sourceBranch = null;

         for (Artifact sourceArtifact : sourceArtifacts) {
            if (sourceBranch == null) {
               sourceBranch = sourceArtifact.getBranch();
            }

            if (artifactIds.contains(sourceArtifact.getArtId())) {
               updateSelectedArtifacts.add(sourceArtifact);
            } else if (artifactOnDestinaltionBranch(destinationParentArtifact.getBranch(), sourceArtifact)) {
               updateArtifactsFoundOnBranch.add(sourceArtifact);
            } else if (artifactOnParentBranch(destinationParentArtifact.getBranch(), sourceArtifact)) {
               newBaselineArtifacts.add(sourceArtifact);
            } else {
               newNonBaselineArtifacts.add(sourceArtifact);
            }
         }

         confirmUsersRequestAndProcess(destinationParentArtifact, sourceBranch, updateSelectedArtifacts,
               updateArtifactsFoundOnBranch, newBaselineArtifacts, newNonBaselineArtifacts);
      } else {
         MessageDialog.openError(
               Display.getCurrent().getActiveShell(),
               "Drag and Drop Error",
               "Access control has restricted this action. The current user does not have sufficient permission to drag and drop artifacts on this branch from the selected source branch.");
      }
   }

   private void confirmUsersRequestAndProcess(Artifact destinationArtifact, Branch sourceBranch, List<Artifact> updateSelectedArtifacts, List<Artifact> updateArtifactsFoundOnBranch, List<Artifact> newBaselineArtifacts, List<Artifact> newNonBaselineArtifacts) throws OseeCoreException {
      StringBuilder message = new StringBuilder();

      if (!updateSelectedArtifacts.isEmpty()) {
         message.append(" 1 Are you sure you want to update " + updateSelectedArtifacts.size() + " artifacts from thier parent branch? \n");
      }
      if (!updateArtifactsFoundOnBranch.isEmpty()) {
         message.append(" 2 These artifacts were already found on your branch. Are you sure you want to update " + updateArtifactsFoundOnBranch.size() + " artifacts from thier parent branch? \n");
      }
      if (!newBaselineArtifacts.isEmpty()) {
         message.append(" 3 Are you sure you want to add " + newBaselineArtifacts.size() + " artifacts from thier parent branch? \n");
      }
      if (!newNonBaselineArtifacts.isEmpty()) {
         message.append(" 4 Are you sure you want to add " + newNonBaselineArtifacts.size() + " new artifacts from this branch? \n");
      }

      if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Confirm Action",
            message.toString())) {

         Branch destinationBranch = destinationArtifact.getBranch();
         updateArtifacts(updateSelectedArtifacts, destinationBranch, sourceBranch);
         updateArtifacts(updateArtifactsFoundOnBranch, destinationBranch, sourceBranch);
         addNewArtifactToBaseline(newBaselineArtifacts, destinationBranch, sourceBranch);
         addNewArtifact(destinationArtifact, newNonBaselineArtifacts, sourceBranch);
      }
   }

   private void addNewArtifact(Artifact destinationArtifact, List<Artifact> updateArtifacts, Branch sourceBranch) throws OseeCoreException {
      ArtifactUpdateHandler.updateArtifacts(destinationArtifact.getBranch(), updateArtifacts, sourceBranch, destinationArtifact, true);
   }

   private void addNewArtifactToBaseline(List<Artifact> updateArtifacts, Branch destinationBranch, Branch sourceBranch) throws OseeCoreException {
      if (updateArtifacts.isEmpty()) {
         return;
      }

      ArtifactUpdateHandler.updateArtifacts(destinationBranch, updateArtifacts, sourceBranch);
   }

   private void updateArtifacts(List<Artifact> updateArtifacts, Branch destinationBranch, Branch sourceBranch) throws OseeCoreException {
      if (updateArtifacts.isEmpty()) {
         return;
      }

      ArtifactUpdateHandler.updateArtifacts(destinationBranch, updateArtifacts, sourceBranch);
   }

   private boolean artifactOnParentBranch(Branch branch, Artifact artifact) throws OseeCoreException {
      return branch.getParentBranch().equals(artifact.getBranch());
   }

   private boolean artifactOnDestinaltionBranch(Branch branch, Artifact artifact) throws OseeCoreException {
      return artifactOnBranch(branch, artifact);
   }

   private boolean artifactOnBranch(Branch sourceBranch, Artifact sourceArtifact) throws OseeDataStoreException {
      boolean found = false;
      ConnectionHandlerStatement chtStmt = new ConnectionHandlerStatement();

      try {
         chtStmt.runPreparedQuery(IS_ARTIFACT_ON_BRANCH, sourceArtifact.getArtId(), sourceBranch.getBranchId());
         found = chtStmt.next();
      } finally {
         chtStmt.close();
      }

      return found;
   }

}
