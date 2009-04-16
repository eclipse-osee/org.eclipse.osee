/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.update.UpdateArtifactDbTransaction;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class InterArtifactExplorerDropHandler {
   private static final String IS_ARTIFACT_ON_BASELINE =
         "select txd1.tx_type from osee_artifact_version av1, osee_txs txs1, osee_tx_details txd1 where av1.art_id = ? and av1.gamma_id = txs1.gamma_id and txs1.transaction_id = txd1.transaction_id and txd1.branch_id = ? AND txd1.tx_type = 1";
   private static final String ACCESS_ERROR_MSG_TITLE = "Drag and Drop Error";
   private static final String ACCESS_ERROR_MSG =
         "Access control has restricted this action. The current user does not have sufficient permission to drag and drop artifacts on this branch from the selected source branch.";

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

            if (artifactOnBaseline(destinationParentArtifact.getBranch(), sourceArtifact)) {
               if (artifactIds.contains(sourceArtifact.getArtId())) {
                  transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.UPDATE_DROP));
               } else {
                  transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.UPDATE_SOMEWHERE_ON_BRANCH));
               }
            } else {
               if (artifactOnParentBranch(destinationParentArtifact.getBranch(), sourceArtifact)) {
                  transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.ADD_TO_BASELINE));
               } else {
                  transferObjects.add(new TransferObject(sourceArtifact, TransferStatus.ADD_NOT_TO_BASELINE));
               }
            }
         }
         confirmUsersRequestAndProcess(destinationParentArtifact, sourceBranch, transferObjects);
      } else {
         MessageDialog.openError(Display.getCurrent().getActiveShell(), ACCESS_ERROR_MSG_TITLE, ACCESS_ERROR_MSG);
      }
   }

   private void confirmUsersRequestAndProcess(Artifact destinationArtifact, Branch sourceBranch, List<TransferObject> transferObjects) throws OseeCoreException {
      UpdateArtifactStatusDialog updateArtifactStatusDialog = new UpdateArtifactStatusDialog(transferObjects);

      if (updateArtifactStatusDialog.open() == Window.OK) {
         Branch destinationBranch = destinationArtifact.getBranch();
         addArtifactsToBaseline(transferObjects, destinationBranch, sourceBranch);
         addArtifactsToNewTransaction(destinationArtifact, transferObjects, sourceBranch);
      }
   }

   private void addArtifactsToNewTransaction(Artifact destinationArtifact, List<TransferObject> transferObjects, Branch sourceBranch) throws OseeCoreException {
      List<Artifact> artifacts = new LinkedList<Artifact>();

      for (TransferObject transferObject : transferObjects) {
         if (transferObject.getStatus().equals(TransferStatus.ADD_NOT_TO_BASELINE)) {
            artifacts.add(transferObject.getArtifact());
         }
      }

      if (!artifacts.isEmpty()) {
         SkynetTransaction transaction = new SkynetTransaction(destinationArtifact.getBranch());
         for (Artifact sourceArtifact : artifacts) {
            Artifact parentArtifact = removeReflectedArtifact(sourceArtifact, destinationArtifact);
            Artifact reflectedArtifact = sourceArtifact.reflect(destinationArtifact.getBranch());
            reflectedArtifact.setSoleRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__PARENT, parentArtifact);
            reflectedArtifact.persistAttributesAndRelations(transaction);
         }
         transaction.execute();
      }
   }

   private Artifact removeReflectedArtifact(Artifact sourceArtifact, Artifact destinationArtifact) throws OseeCoreException {
      Artifact reflectedArtifact =
            ArtifactQuery.checkArtifactFromId(sourceArtifact.getArtId(), destinationArtifact.getBranch(), true);
      Artifact newDestinationArtifact = destinationArtifact;

      if (reflectedArtifact != null) {
         newDestinationArtifact = reflectedArtifact.getParent();
         reflectedArtifact.revert();

         if (!reflectedArtifact.equals(newDestinationArtifact)) {
            newDestinationArtifact.reloadAttributesAndRelations();
         }
      }
      return newDestinationArtifact;
   }

   private void addArtifactsToBaseline(List<TransferObject> transferObjects, Branch destinationBranch, Branch sourceBranch) throws OseeCoreException {
      List<Artifact> artifacts = new LinkedList<Artifact>();

      for (TransferObject transferObject : transferObjects) {
         if (transferObject.getStatus().equals(TransferStatus.UPDATE_DROP) || transferObject.getStatus().equals(
               TransferStatus.UPDATE_SOMEWHERE_ON_BRANCH) || transferObject.getStatus().equals(
               TransferStatus.ADD_TO_BASELINE)) {
            artifacts.add(transferObject.getArtifact());
         }
      }

      if (artifacts.isEmpty()) {
         return;
      }

      UpdateArtifactDbTransaction artifactDbTransaction =
            new UpdateArtifactDbTransaction(destinationBranch, sourceBranch, artifacts);
      artifactDbTransaction.execute();
   }

   private boolean artifactOnParentBranch(Branch branch, Artifact artifact) throws OseeCoreException {
      return branch.getParentBranch().equals(artifact.getBranch());
   }

   private boolean artifactOnBaseline(Branch sourceBranch, Artifact sourceArtifact) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      boolean found = false;

      try {
         chStmt.runPreparedQuery(IS_ARTIFACT_ON_BASELINE, sourceArtifact.getArtId(), sourceBranch.getBranchId());
         found = chStmt.next();
      } finally {
         chStmt.close();
      }
      return found;
   }
}
