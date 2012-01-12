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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class InterArtifactExplorerDropHandlerOperation extends AbstractOperation {

   private static final String ACCESS_ERROR_MSG_TITLE = "Drag and Drop Error";
   private static final String UPDATE_FROM_PARENT_ERROR_MSG =
      "Attempting to update child branch from parent branch. Use 'Update Branch' instead.";
   private static final String ACCESS_ERROR_MSG =
      "Access control has restricted this action. The current user does not have sufficient permission to drag and drop artifacts on this branch from the selected source branch.";
   private final Artifact destinationParentArtifact;
   private final Artifact[] sourceArtifacts;
   private final boolean prompt, recurseChildren;

   public InterArtifactExplorerDropHandlerOperation(Artifact destinationParentArtifact, Artifact[] sourceArtifacts, boolean prompt, boolean recurseChildren) {
      super("Introduce Artifact(s)", Activator.PLUGIN_ID);
      this.destinationParentArtifact = destinationParentArtifact;
      this.sourceArtifacts = sourceArtifacts;
      this.prompt = prompt;
      this.recurseChildren = recurseChildren;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (destinationParentArtifact == null || sourceArtifacts == null || sourceArtifacts.length < 1) {
         throw new OseeArgumentException("Invalid arguments");
      }
      Branch sourceBranch = sourceArtifacts[0].getFullBranch();
      Branch destinationBranch = destinationParentArtifact.getFullBranch();

      if (isUpdateFromParent(sourceBranch, destinationBranch)) {
         MessageDialog.openError(Displays.getActiveShell(), ACCESS_ERROR_MSG_TITLE, UPDATE_FROM_PARENT_ERROR_MSG);
      } else if (isAccessAllowed(sourceBranch, destinationBranch)) {
         Set<Artifact> transferArtifacts = getArtifactSetToTransfer();
         monitor.beginTask("Processing Artifact(s)", 2 + (transferArtifacts.size() * 4));
         if (prompt) {
            final MutableBoolean userConfirmed = new MutableBoolean(false);
            //convert to TransferObject so that the user can see which are introduced and which are updated
            final List<TransferObject> convertedArtifacts =
               convertToTransferObjects(transferArtifacts, destinationParentArtifact.getBranch());
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  userConfirmed.setValue(confirmUsersRequestAndProcess(convertedArtifacts));
               }
            }, true);
            if (!userConfirmed.getValue()) {
               return;
            }
         }
         addArtifactsToNewTransaction(destinationParentArtifact, transferArtifacts, sourceBranch, monitor);
      } else {
         MessageDialog.openError(Displays.getActiveShell(), ACCESS_ERROR_MSG_TITLE, ACCESS_ERROR_MSG);
      }
   }

   private Set<Artifact> getArtifactSetToTransfer() throws OseeCoreException {
      Set<Artifact> transferObjects = new LinkedHashSet<Artifact>();

      for (Artifact sourceArtifact : sourceArtifacts) {
         transferObjects.add(sourceArtifact);
         if (recurseChildren) {
            transferObjects.addAll(sourceArtifact.getDescendants());
         }
      }
      return transferObjects;
   }

   private List<TransferObject> convertToTransferObjects(Set<Artifact> sourceArtifacts, IOseeBranch desitnationBranch) throws OseeCoreException {
      List<TransferObject> transferObjects = new LinkedList<TransferObject>();
      for (Artifact sourceArtifact : sourceArtifacts) {
         TransferStatus transferStatus = null;
         if (artifactOnBranch(desitnationBranch, sourceArtifact)) {
            transferStatus = TransferStatus.UPDATE;
         } else {
            transferStatus = TransferStatus.INTRODUCE;
         }
         transferObjects.add(new TransferObject(sourceArtifact, transferStatus));
      }
      return transferObjects;
   }

   private boolean artifactOnBranch(IOseeBranch sourceBranch, Artifact sourceArtifact) throws OseeCoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(0, ClientSessionManager.getSql(OseeSql.IS_ARTIFACT_ON_BRANCH),
         sourceArtifact.getArtId(), BranchManager.getBranchId(sourceBranch)) > 0;
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

   private void addArtifactsToNewTransaction(Artifact destinationArtifact, Set<Artifact> transferObjects, Branch sourceBranch, IProgressMonitor monitor) throws OseeCoreException {
      loadArtifactsIntoCache(transferObjects, sourceBranch, monitor);
      handleTransfers(transferObjects, destinationArtifact, monitor);
   }

   private void reloadCachedArtifacts(Set<Artifact> reloadArtifacts) throws OseeCoreException {
      for (Artifact reloadArtifact : reloadArtifacts) {
         reloadArtifact.reloadAttributesAndRelations();
      }
   }

   private void handleTransfers(Set<Artifact> transferArtifacts, Artifact destinationArtifact, IProgressMonitor monitor) throws OseeCoreException {
      IOseeBranch destinationBranch = destinationArtifact.getBranch();

      SkynetTransaction transaction =
         TransactionManager.createTransaction(destinationBranch, "Introduced " + transferArtifacts.size() + " artifact(s)");
      Set<Artifact> reloadArtifacts = new LinkedHashSet<Artifact>();

      //make sure all transfer artifacts are on the destination branch
      for (Artifact sourceArtifact : transferArtifacts) {
         Artifact onDestinationBranch =
            ArtifactQuery.checkArtifactFromId(sourceArtifact.getArtId(), destinationBranch,
               DeletionFlag.EXCLUDE_DELETED);
         if (onDestinationBranch == null) {
            handleIntroduceCase(sourceArtifact, destinationArtifact, reloadArtifacts, transaction);
         }
         monitor.worked(1);
      }

      //all artifacts should be on the destination branch with matching attributes now
      for (Artifact sourceArtifact : transferArtifacts) {
         Artifact onDestinationBranch =
            ArtifactQuery.checkArtifactFromId(sourceArtifact.getArtId(), destinationBranch,
               DeletionFlag.EXCLUDE_DELETED);
         handleUpdateCase(sourceArtifact, onDestinationBranch, reloadArtifacts, transaction);
         monitor.worked(1);
      }

      //now check to make sure all the transfer artifacts have a parent, if not, set it to destination artifact
      for (Artifact sourceArtifact : transferArtifacts) {
         Artifact onDestinationBranch =
            ArtifactQuery.checkArtifactFromId(sourceArtifact.getArtId(), destinationBranch,
               DeletionFlag.EXCLUDE_DELETED);
         Artifact parent = onDestinationBranch.getParent();
         if ((parent == null || parent.equals(CoreArtifactTokens.DefaultHierarchyRoot)) && !destinationArtifact.equals(onDestinationBranch)) {
            destinationArtifact.addChild(onDestinationBranch);
            destinationArtifact.persist(transaction);
         }
         monitor.worked(1);
      }

      transaction.execute();
      monitor.worked(1);
      reloadCachedArtifacts(reloadArtifacts);
      monitor.worked(1);
   }

   private void loadArtifactsIntoCache(Set<Artifact> transferObjects, Branch sourceBranch, IProgressMonitor monitor) throws OseeCoreException {
      ArrayList<Integer> sourceArtIds = new ArrayList<Integer>(transferObjects.size());

      for (Artifact transferObject : transferObjects) {
         monitor.worked(1);
         sourceArtIds.add(transferObject.getArtId());
      }
      ArtifactQuery.getArtifactListFromIds(sourceArtIds, sourceBranch);
   }

   private void handleIntroduceCase(Artifact sourceArtifact, Artifact destinationParent, Set<Artifact> reloadArtifacts, SkynetTransaction transaction) throws OseeCoreException {
      Artifact updatedArtifact = sourceArtifact.reflect(destinationParent.getBranch());
      updatedArtifact.persist(transaction);
      reloadArtifacts.add(updatedArtifact);
   }

   private void handleUpdateCase(Artifact sourceArtifact, Artifact destinationArtifact, Set<Artifact> reloadArtifacts, SkynetTransaction transaction) throws OseeCoreException {
      destinationArtifact.updateArtifactFromBranch(sourceArtifact.getBranch());
      reloadArtifacts.add(destinationArtifact);
      destinationArtifact.persist(transaction);
   }

}
