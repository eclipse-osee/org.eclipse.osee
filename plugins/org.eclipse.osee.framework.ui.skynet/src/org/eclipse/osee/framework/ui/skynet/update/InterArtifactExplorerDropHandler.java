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

import static org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag.INCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class InterArtifactExplorerDropHandler {

   private static final String ACCESS_ERROR_MSG_TITLE = "Drag and Drop Error";
   private static final String UPDATE_FROM_PARENT_ERROR_MSG =
         "Attempting to update child branch from parent branch. Use 'Update Branch' instead.";
   private static final String ACCESS_ERROR_MSG =
         "Access control has restricted this action. The current user does not have sufficient permission to drag and drop artifacts on this branch from the selected source branch.";

   public void dropArtifactIntoDifferentBranch(Artifact destinationParentArtifact, Artifact[] sourceArtifacts, boolean prompt) throws OseeCoreException {
      if (destinationParentArtifact == null || sourceArtifacts == null || sourceArtifacts.length < 1) {
         throw new OseeArgumentException("Invalid arguments");
      }
      Branch sourceBranch = sourceArtifacts[0].getBranch();
      Branch destinationBranch = destinationParentArtifact.getBranch();

      if (isUpdateFromParent(sourceBranch, destinationBranch)) {
         MessageDialog.openError(Displays.getActiveShell(), ACCESS_ERROR_MSG_TITLE,
               UPDATE_FROM_PARENT_ERROR_MSG);
      } else if (isAccessAllowed(sourceBranch, destinationBranch)) {
         List<TransferObject> transferObjects = createTransferObjects(destinationParentArtifact, sourceArtifacts);
         if (prompt) {
            boolean userConfirmed = confirmUsersRequestAndProcess(transferObjects);
            if (!userConfirmed) {
               return;
            }
         }
         addArtifactsToNewTransaction(destinationParentArtifact, transferObjects, sourceBranch);
      } else {
         MessageDialog.openError(Displays.getActiveShell(), ACCESS_ERROR_MSG_TITLE, ACCESS_ERROR_MSG);
      }
   }

   private List<TransferObject> createTransferObjects(Artifact destinationParentArtifact, Artifact[] sourceArtifacts) throws OseeCoreException {
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
      return transferObjects;
   }

   private boolean isUpdateFromParent(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      boolean result = false;
      if (destinationBranch.hasParentBranch()) {
         result = destinationBranch.getParentBranch().equals(sourceBranch);
      }
      return result;
   }

   private boolean isAccessAllowed(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      return AccessControlManager.hasPermission(destinationBranch, PermissionEnum.WRITE) && AccessControlManager.hasPermission(
            sourceBranch, PermissionEnum.READ);
   }

   private boolean confirmUsersRequestAndProcess(List<TransferObject> transferObjects) {
      ReflectArtifactStatusDialog updateArtifactStatusDialog = new ReflectArtifactStatusDialog(transferObjects);
      return updateArtifactStatusDialog.open() == Window.OK;
   }

   private void addArtifactsToNewTransaction(Artifact destinationArtifact, List<TransferObject> transferObjects, Branch sourceBranch) throws OseeCoreException {
      loadArtifactsIntoCache(transferObjects, sourceBranch);
      handleTransfers(transferObjects, destinationArtifact);
   }

   private void reloadCachedArtifacts(ArrayList<Artifact> reloadArtifacts) throws OseeCoreException {
      for (Artifact reloadArtifact : reloadArtifacts) {
         reloadArtifact.reloadAttributesAndRelations();
      }
   }

   private void handleTransfers(List<TransferObject> transferObjects, Artifact destinationArtifact) throws OseeCoreException {
      SkynetTransaction transaction =
            new SkynetTransaction(destinationArtifact.getBranch(),
                  "Introduced " + transferObjects.size() + " artifact(s)");
      ArrayList<Artifact> reloadArtifacts = new ArrayList<Artifact>();

      for (TransferObject transferObject : transferObjects) {
         TransferStatus status = transferObject.getStatus();
         Artifact updatedArtifact = null;
         if (status == TransferStatus.INTRODUCE) {
            updatedArtifact = handleIntroduceCase(transferObject, destinationArtifact);
         } else if (status == TransferStatus.UPDATE) {
            updatedArtifact = handleUpdateCase(transferObject, destinationArtifact, reloadArtifacts);
         }
         updatedArtifact.persist(transaction);
      }
      transaction.execute();
      reloadCachedArtifacts(reloadArtifacts);
   }

   private void loadArtifactsIntoCache(List<TransferObject> transferObjects, Branch sourceBranch) throws OseeCoreException {
      ArrayList<Integer> sourceArtIds = new ArrayList<Integer>(transferObjects.size());

      for (TransferObject transferObject : transferObjects) {
         sourceArtIds.add(transferObject.getArtifact().getArtId());
      }
      ArtifactQuery.getArtifactListFromIds(sourceArtIds, sourceBranch);
   }

   private Artifact handleIntroduceCase(TransferObject transferObject, Artifact destinationArtifact) throws OseeCoreException {
      Artifact updatedArtifact = null;
      Artifact sourceArtifact = transferObject.getArtifact();
      Artifact parentArtifact = getParent(sourceArtifact, destinationArtifact);
      updatedArtifact = sourceArtifact.reflect(destinationArtifact.getBranch());
      updatedArtifact.setRelations(RelationOrderBaseTypes.USER_DEFINED, CoreRelationTypes.Default_Hierarchical__Parent,
            Collections.singleton(parentArtifact));
      return updatedArtifact;
   }

   private Artifact handleUpdateCase(TransferObject transferObject, Artifact destinationArtifact, ArrayList<Artifact> reloadArtifacts) throws OseeCoreException {
      Artifact sourceArtifact = transferObject.getArtifact();
      destinationArtifact.updateArtifactFromBranch(sourceArtifact.getBranch());
      reloadArtifacts.add(destinationArtifact);
      return destinationArtifact;
   }

   private Artifact getParent(Artifact sourceArtifact, Artifact destinationArtifact) throws OseeCoreException {
      Artifact reflectedArtifact =
            ArtifactQuery.checkArtifactFromId(sourceArtifact.getArtId(), destinationArtifact.getBranch(),
                  INCLUDE_DELETED);
      Artifact newDestinationArtifact = destinationArtifact;

      if (reflectedArtifact != null) {
         newDestinationArtifact = reflectedArtifact.getParent();
      }
      return newDestinationArtifact;
   }

   private boolean artifactOnBranch(Branch sourceBranch, Artifact sourceArtifact) throws OseeCoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(0, ClientSessionManager.getSql(OseeSql.IS_ARTIFACT_ON_BRANCH),
            sourceArtifact.getArtId(), sourceBranch.getId()) > 0;
   }
}
