/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactUpdateHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class InterArtifactExplorerDropHandler {
   private static final String IS_ARTIFACT_ON_BRANCH =
         "select txd1.tx_type from osee_artifact_version av1, osee_txs txs1, osee_tx_details txd1 where av1.art_id = ? and av1.gamma_id = txs1.gamma_id and txs1.transaction_id = txd1.transaction_id and txd1.branch_id = ?";

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

         List<TransferObject> transferObjects = new LinkedList<TransferObject>();
         Branch sourceBranch = null;

         for (Artifact sourceArtifact : sourceArtifacts) {
            if (sourceBranch == null) {
               sourceBranch = sourceArtifact.getBranch();
            }

            TransactionDetailsType artifactTransactionDetailsType =
                  getTransactionTypeOfArtifactsFoundOnDestination(destinationParentArtifact.getBranch(), sourceArtifact);

            if (artifactTransactionDetailsType != null) {
               if (artifactTransactionDetailsType.equals(TransactionDetailsType.Baselined)) {
                  if (artifactIds.contains(sourceArtifact.getArtId())) {
                     transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.UPDATE_DROP));
                  } else {
                     transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.UPDATE_SOMEWHERE_ON_BRANCH));
                  }
               } else {
                  transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.ERROR));
               }
            } else if (artifactOnParentBranch(destinationParentArtifact.getBranch(), sourceArtifact)) {
               transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.ADD_TO_BASELINE));
            } else {
               transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.ADD_NOT_TO_BASELINE));
            }
         }

         confirmUsersRequestAndProcess(destinationParentArtifact, sourceBranch, transferObjects);
      } else {
         MessageDialog.openError(
               Display.getCurrent().getActiveShell(),
               "Drag and Drop Error",
               "Access control has restricted this action. The current user does not have sufficient permission to drag and drop artifacts on this branch from the selected source branch.");
      }
   }

   private void confirmUsersRequestAndProcess(Artifact destinationArtifact, Branch sourceBranch, List<TransferObject> transferObjects) throws OseeCoreException {
      UpdateArtifactStatusDialog updateArtifactStatusDialog = new UpdateArtifactStatusDialog(transferObjects);
      
      if (!transferObjects.isEmpty() && updateArtifactStatusDialog.open() == Window.OK) {
         Branch destinationBranch = destinationArtifact.getBranch();
         updateArtifacts(transferObjects, destinationBranch, sourceBranch);
         addNewArtifact(destinationArtifact, transferObjects, sourceBranch);
      }
   }

   private void addNewArtifact(Artifact destinationArtifact, List<TransferObject> transferObjects, Branch sourceBranch) throws OseeCoreException {
      List<Artifact> artifacts = new LinkedList<Artifact>();

      for (TransferObject transferObject : transferObjects) {
         if (transferObject.getStatus().equals(TransferStatus.ADD_NOT_TO_BASELINE)) {
            artifacts.add(transferObject.getArtifact());
         }
      }

      if (artifacts.isEmpty()) {
         return;
      }
      
      ArtifactUpdateHandler.updateArtifacts(destinationArtifact.getBranch(), artifacts, sourceBranch,
            destinationArtifact, true);
   }

  

   private void updateArtifacts(List<TransferObject> transferObjects, Branch destinationBranch, Branch sourceBranch) throws OseeCoreException {
      List<Artifact> artifacts = new LinkedList<Artifact>();

      for (TransferObject transferObject : transferObjects) {
         if (transferObject.getStatus().equals(TransferStatus.UPDATE_DROP) || transferObject.getStatus().equals(TransferStatus.UPDATE_SOMEWHERE_ON_BRANCH) || transferObject.getStatus().equals(TransferStatus.ADD_TO_BASELINE)) {
            artifacts.add(transferObject.getArtifact());
         }
      }
      
      if (artifacts.isEmpty()) {
         return;
      }

      ArtifactUpdateHandler.updateArtifacts(destinationBranch, artifacts, sourceBranch);
   }

   private boolean artifactOnParentBranch(Branch branch, Artifact artifact) throws OseeCoreException {
      return branch.getParentBranch().equals(artifact.getBranch());
   }

   private TransactionDetailsType getTransactionTypeOfArtifactsFoundOnDestination(Branch branch, Artifact artifact) throws OseeCoreException {
      return artifactOnBranch(branch, artifact);
   }

   private TransactionDetailsType artifactOnBranch(Branch sourceBranch, Artifact sourceArtifact) throws OseeDataStoreException {
      TransactionDetailsType type = null;
      ConnectionHandlerStatement chtStmt = new ConnectionHandlerStatement();

      try {
         chtStmt.runPreparedQuery(IS_ARTIFACT_ON_BRANCH, sourceArtifact.getArtId(), sourceBranch.getBranchId());
         if (chtStmt.next()) {
            type = TransactionDetailsType.toEnum(chtStmt.getInt("tx_type"));

         }
      } finally {
         chtStmt.close();
      }

      return type;
   }
}
