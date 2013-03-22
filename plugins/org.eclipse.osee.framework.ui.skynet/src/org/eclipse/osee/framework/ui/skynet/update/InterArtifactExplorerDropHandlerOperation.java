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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
   private final Collection<Artifact> sourceArtifacts;
   private final boolean prompt, recurseChildren;
   private final boolean parentToDestinationArtifact;

   public InterArtifactExplorerDropHandlerOperation(Artifact destinationParentArtifact, Artifact[] sourceArtifacts, boolean prompt, boolean recurseChildren, boolean parentToDestinationArtifact) {
      super("Introduce Artifact(s)", Activator.PLUGIN_ID);
      this.destinationParentArtifact = destinationParentArtifact;
      this.sourceArtifacts = Arrays.asList(sourceArtifacts);
      this.prompt = prompt;
      this.recurseChildren = recurseChildren;
      this.parentToDestinationArtifact = parentToDestinationArtifact;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (destinationParentArtifact == null || sourceArtifacts == null || sourceArtifacts.isEmpty()) {
         throw new OseeArgumentException("Invalid arguments");
      }
      Branch sourceBranch = sourceArtifacts.iterator().next().getFullBranch();
      Branch destinationBranch = destinationParentArtifact.getFullBranch();

      if (isUpdateFromParent(sourceBranch, destinationBranch)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openError(Displays.getActiveShell(), ACCESS_ERROR_MSG_TITLE, UPDATE_FROM_PARENT_ERROR_MSG);
            }
         });
      } else if (isAccessAllowed(sourceBranch, destinationBranch)) {
         Set<Artifact> transferArtifacts = getArtifactSetToTransfer();
         monitor.beginTask("Processing Artifact(s)", 2 + (transferArtifacts.size() * 4));
         final List<TransferObject> convertedArtifacts =
            convertToTransferObjects(transferArtifacts, destinationParentArtifact.getBranch());
         if (prompt) {
            final MutableBoolean userConfirmed = new MutableBoolean(false);
            //convert to TransferObject so that the user can see which are introduced and which are updated
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
         addArtifactsToNewTransaction(destinationParentArtifact, convertedArtifacts, sourceBranch, monitor);
      } else {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openError(Displays.getActiveShell(), ACCESS_ERROR_MSG_TITLE, ACCESS_ERROR_MSG);
            }
         });
      }
      monitor.done();
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

   private void addArtifactsToNewTransaction(Artifact destinationArtifact, List<TransferObject> convertedArtifacts, Branch sourceBranch, IProgressMonitor monitor) throws OseeCoreException {
      loadArtifactsIntoCache(convertedArtifacts, sourceBranch, monitor);
      handleTransfers(convertedArtifacts, destinationArtifact, monitor);
   }

   private void reloadCachedArtifacts(Set<Artifact> reloadArtifacts) throws OseeCoreException {
      for (Artifact reloadArtifact : reloadArtifacts) {
         reloadArtifact.reloadAttributesAndRelations();
      }
   }

   private void handleTransfers(List<TransferObject> convertedArtifacts, Artifact destinationArtifact, IProgressMonitor monitor) throws OseeCoreException {
      IOseeBranch destinationBranch = destinationArtifact.getBranch();
      IOseeBranch sourceBranch = convertedArtifacts.iterator().next().getArtifact().getBranch();

      SkynetTransaction transaction = TransactionManager.createTransaction(destinationBranch, Strings.EMPTY_STRING);

      Set<Artifact> updated = new LinkedHashSet<Artifact>();
      Set<Artifact> introduced = new LinkedHashSet<Artifact>();

      //make sure all transfer artifacts are on the destination branch
      for (TransferObject sourceArtifact : convertedArtifacts) {
         if (sourceArtifact.getStatus() == TransferStatus.INTRODUCE) {
            handleIntroduceCase(sourceArtifact.getArtifact(), destinationBranch, introduced, transaction);
            monitor.worked(1);
         }
      }

      for (TransferObject sourceArtifact : convertedArtifacts) {
         if (sourceArtifact.getStatus() == TransferStatus.UPDATE) {
            handleUpdateCase(sourceArtifact.getArtifact(), destinationBranch, updated, transaction);
            monitor.worked(1);
         }
      }

      Set<Integer> sourceArtIds = getSourceArtIds();

      // updated arts should already have a parent on destination, leave them as is
      for (Artifact art : introduced) {
         Artifact onSourceBranch = ArtifactQuery.getArtifactFromId(art.getArtId(), sourceBranch);
         Artifact parentOnDestination = null;
         if (onSourceBranch.getParent() != null) {
            int parentId = onSourceBranch.getParent().getArtId();
            parentOnDestination =
               ArtifactQuery.checkArtifactFromId(parentId, destinationBranch, DeletionFlag.EXCLUDE_DELETED);
         }
         if ((sourceArtIds.contains(art.getArtId()) && parentToDestinationArtifact) || parentOnDestination == null) {
            setParent(art, destinationArtifact);
         } else {
            setParent(art, parentOnDestination);
         }
         art.persist(transaction);
         monitor.worked(1);
      }

      String resultComment =
         Strings.buildStatment(Arrays.asList(String.format("Introduced %s", introduced.size()),
            String.format("Updated %s", updated.size() - introduced.size())));

      transaction.setComment(String.format("%s artifact(s)", resultComment));
      transaction.execute();
      monitor.worked(1);
      reloadCachedArtifacts(updated);
      monitor.worked(1);
   }

   private Set<Integer> getSourceArtIds() {
      Set<Integer> toReturn = new HashSet<Integer>(sourceArtifacts.size());
      for (Artifact art : sourceArtifacts) {
         toReturn.add(art.getArtId());
      }
      return toReturn;
   }

   private void setParent(Artifact child, Artifact parent) throws OseeCoreException {
      if (child.getParent() != null && !child.getParent().equals(parent)) {
         child.deleteRelations(CoreRelationTypes.Default_Hierarchical__Parent);
      }
      parent.addChild(child);
   }

   private void loadArtifactsIntoCache(List<TransferObject> convertedArtifacts, Branch sourceBranch, IProgressMonitor monitor) throws OseeCoreException {
      ArrayList<Integer> sourceArtIds = new ArrayList<Integer>(convertedArtifacts.size());

      for (TransferObject transferObject : convertedArtifacts) {
         monitor.worked(1);
         sourceArtIds.add(transferObject.getArtifact().getArtId());
      }
      ArtifactQuery.getArtifactListFromIds(sourceArtIds, sourceBranch);
   }

   private void handleIntroduceCase(Artifact sourceArtifact, IOseeBranch destinationBranch, Set<Artifact> reloadArtifacts, SkynetTransaction transaction) throws OseeCoreException {
      Artifact updatedArtifact = sourceArtifact.reflect(destinationBranch);
      updatedArtifact.persist(transaction);
      reloadArtifacts.add(updatedArtifact);
   }

   private void handleUpdateCase(Artifact sourceArtifact, IOseeBranch destinationBranch, Set<Artifact> updated, SkynetTransaction transaction) throws OseeCoreException {
      Artifact destinationArtifact = sourceArtifact.reflect(destinationBranch);
      updated.add(destinationArtifact);
      destinationArtifact.persist(transaction);
   }

}
