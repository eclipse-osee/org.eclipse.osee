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
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class InterArtifactExplorerDropHandler {
   private static final String IS_ARTIFACT_ON_BRANCH =
         "select count(1) from osee_artifact_version av1, osee_txs txs1, osee_tx_details txd1 where av1.art_id = ? and av1.gamma_id = txs1.gamma_id and txs1.transaction_id = txd1.transaction_id and txd1.branch_id = ?";
   private static final String ACCESS_ERROR_MSG_TITLE = "Drag and Drop Error";
   private static final String UPDATE_FROM_PARENT_ERROR_MSG =
         "Attempting to update child branch from parent branch. Use 'Update Branch' instead.";
   private static final String ACCESS_ERROR_MSG =
         "Access control has restricted this action. The current user does not have sufficient permission to drag and drop artifacts on this branch from the selected source branch.";

   public boolean isUpdateFromParent(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      boolean result = false;
      if (destinationBranch.hasParentBranch()) {
         result = destinationBranch.getParentBranch().equals(sourceBranch);
      }
      return result;
   }

   public boolean isAccessAllowed(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      return AccessControlManager.hasPermission(destinationBranch, PermissionEnum.WRITE) && AccessControlManager.hasPermission(
            sourceBranch, PermissionEnum.READ);
   }

   public void dropArtifactIntoDifferentBranch(Artifact destinationParentArtifact, Artifact[] sourceArtifacts, boolean prompt) throws OseeCoreException {
      if (destinationParentArtifact == null || sourceArtifacts == null || sourceArtifacts.length < 1) {
         throw new OseeArgumentException("");
      }

      Branch sourceBranch = sourceArtifacts[0].getBranch();
      Branch destinationBranch = destinationParentArtifact.getBranch();

      if (isUpdateFromParent(sourceBranch, destinationBranch)) {
         MessageDialog.openError(Display.getCurrent().getActiveShell(), ACCESS_ERROR_MSG_TITLE,
               UPDATE_FROM_PARENT_ERROR_MSG);
      } else if (isAccessAllowed(sourceBranch, destinationBranch)) {
         List<Integer> artifactIds = new ArrayList<Integer>();
         artifactIds.add(destinationParentArtifact.getArtId());

         for (Artifact artifact : destinationParentArtifact.getDescendants()) {
            artifactIds.add(artifact.getArtId());
         }

         List<TransferObject> transferObjects = new LinkedList<TransferObject>();

         for (Artifact sourceArtifact : sourceArtifacts) {
            TransferStatus transferStatus = null;

            if (artifactOnBranch(destinationParentArtifact.getBranch(), sourceArtifact)) {
               transferStatus = TransferStatus.UPDATE;
            } else {
               transferStatus = TransferStatus.INTRODUCE;
            }
            transferObjects.add(new TransferObject(sourceArtifact, transferStatus));

         }
         if (prompt) {
            boolean userConfirmed =
                  confirmUsersRequestAndProcess(transferObjects);
            if (!userConfirmed) {
               return;
            }
         }
         addArtifactsToNewTransaction(destinationParentArtifact, transferObjects, sourceBranch);
      } else {
         MessageDialog.openError(Display.getCurrent().getActiveShell(), ACCESS_ERROR_MSG_TITLE, ACCESS_ERROR_MSG);
      }

   }

   private boolean confirmUsersRequestAndProcess(List<TransferObject> transferObjects) throws OseeCoreException {
      ReflectArtifactStatusDialog updateArtifactStatusDialog = new ReflectArtifactStatusDialog(transferObjects);
      return updateArtifactStatusDialog.open() == Window.OK;
   }

   private void addArtifactsToNewTransaction(Artifact destinationArtifact, List<TransferObject> transferObjects, Branch sourceBranch) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(destinationArtifact.getBranch(), "Introduced " + transferObjects.size() + " artifact(s)");
      ArrayList<Artifact> reloadArtifacts = new ArrayList<Artifact>();

      ArrayList<Integer> sourceArtIds = new ArrayList<Integer>(transferObjects.size());
      for (TransferObject transferObject : transferObjects) {
         sourceArtIds.add(transferObject.getArtifact().getArtId());
      }

      ArtifactLoader.loadArtifacts(sourceArtIds, sourceBranch, ArtifactLoad.ALL_CURRENT, true);

      for (TransferObject transferObject : transferObjects) {
         TransferStatus status = transferObject.getStatus();
         Artifact sourceArtifact = transferObject.getArtifact();

         if (status == TransferStatus.INTRODUCE || status == TransferStatus.UPDATE) {
            Artifact parentArtifact = getParent(sourceArtifact, destinationArtifact, status);

            Artifact reflectedArtifact = sourceArtifact.reflect(destinationArtifact.getBranch());
            if (status == TransferStatus.INTRODUCE) {
               reflectedArtifact.setRelations(RelationOrderBaseTypes.USER_DEFINED,
                     CoreRelationTypes.Default_Hierarchical__Parent, Collections.singleton(parentArtifact));
            } else {
               reloadArtifacts.add(reflectedArtifact);
            }
            reflectedArtifact.persist(transaction);
         }
      }
      transaction.execute();

      for (Artifact reloadArtifact : reloadArtifacts) {
         reloadArtifact.reloadAttributesAndRelations();
      }
   }

   private Artifact getParent(Artifact sourceArtifact, Artifact destinationArtifact, TransferStatus status) throws OseeCoreException {
      Artifact reflectedArtifact =
            ArtifactQuery.checkArtifactFromId(sourceArtifact.getArtId(), destinationArtifact.getBranch(), true);
      Artifact newDestinationArtifact = destinationArtifact;

      if (reflectedArtifact != null) {
         newDestinationArtifact = reflectedArtifact.getParent();
         //    Causes transaction errors so we can only introduce the same artifact once.
         //         if (status == TransferStatus.INTRODUCE) {
         //            reflectedArtifact.revert();
         //            ArtifactPersistenceManager.revertArtifact(null, reflectedArtifact);
         //         }
         //
         //         if (!reflectedArtifact.equals(newDestinationArtifact)) {
         //            newDestinationArtifact.reloadAttributesAndRelations();
         //         }
      }
      return newDestinationArtifact;
   }

   //   private void addArtifactsToBaseline(List<TransferObject> transferObjects, Branch destinationBranch, Branch sourceBranch) throws OseeCoreException {
   //      List<Artifact> artifacts = new LinkedList<Artifact>();
   //
   //      for (TransferObject transferObject : transferObjects) {
   //         TransferStatus status = transferObject.getStatus();
   //         if (status == TransferStatus.REBASELINE || status == TransferStatus.REBASELINE_SOMEWHERE_ON_BRANCH || status == TransferStatus.ADD_TO_BASELINE) {
   //            artifacts.add(transferObject.getArtifact());
   //         }
   //      }
   //
   //      if (artifacts.isEmpty()) {
   //         return;
   //      }
   //
   //      RebaselineDbTransaction artifactDbTransaction =
   //            new RebaselineDbTransaction(destinationBranch, sourceBranch, artifacts);
   //      artifactDbTransaction.execute();
   //   }

   private boolean artifactOnBranch(Branch sourceBranch, Artifact sourceArtifact) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(0, IS_ARTIFACT_ON_BRANCH, sourceArtifact.getArtId(),
            sourceBranch.getId()) > 0;
   }
}
