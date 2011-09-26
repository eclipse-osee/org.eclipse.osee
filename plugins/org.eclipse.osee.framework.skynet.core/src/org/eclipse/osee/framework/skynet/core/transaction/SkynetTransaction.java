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
package org.eclipse.osee.framework.skynet.core.transaction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTransactionData;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTransactionData;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Robert A. Fisher
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public final class SkynetTransaction extends AbstractOperation {

   private TransactionRecord transactionId;

   private final CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData> transactionDataItems =
      new CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData>();

   // Used to avoid garbage collection of artifacts until the transaction has been committed;
   private final Set<Artifact> artifactReferences = new HashSet<Artifact>();
   private final Set<Artifact> alreadyProcessedArtifacts = new HashSet<Artifact>();

   private final Branch branch;
   private boolean madeChanges = false;

   private final String comment;
   private User user;
   private final TransactionMonitor txMonitor = new TransactionMonitor();
   private AccessPolicy access;

   public SkynetTransaction(Branch branch, String comment) {
      super(comment, Activator.PLUGIN_ID);
      this.branch = branch;
      this.comment = comment;
      txMonitor.reportTxCreation(this, branch, comment);
   }

   public SkynetTransaction(IOseeBranch branch, String comment) throws OseeCoreException {
      this(BranchManager.getBranch(branch), comment);
   }

   private int getNewAttributeId(Artifact artifact, Attribute<?> attribute) throws OseeCoreException {
      return StoreSkynetTransactionOperation.getNewAttributeId(artifact, attribute);
   }

   private int getNewRelationId() throws OseeCoreException {
      return ConnectionHandler.getSequence().getNextRelationId();
   }

   private User getAuthor() throws OseeCoreException {
      if (user == null) {
         user = UserManager.getUser();
      }
      return user;
   }

   private void checkAccess(Artifact artifact) throws OseeCoreException {
      if (UserManager.duringMainUserCreation()) {
         return;
      }
      Branch txBranch = getBranch();
      if (!artifact.getBranch().equals(txBranch)) {
         String msg =
            String.format("The artifact [%s] is on branch [%s] but this transaction is for branch [%s]",
               artifact.getGuid(), artifact.getBranch(), txBranch);
         throw new OseeStateException(msg);
      }

      checkBranch(artifact);
      checkNotHistorical(artifact);
      getAccess().hasArtifactPermission(Collections.singleton(artifact), PermissionEnum.WRITE, Level.FINE);
   }

   private void checkBranch(IArtifact artifact) throws OseeCoreException {
      if (!isBranchWritable(artifact.getBranch())) {
         throw new OseeStateException("The artifact [%s] is on a non-editable branch [%s] ", artifact,
            artifact.getBranch());
      }
   }

   private void checkBranch(RelationLink link) throws OseeCoreException {
      if (!isBranchWritable(link.getBranch())) {
         throw new OseeStateException("The relation link [%s] is on a non-editable branch [%s] ", link,
            link.getBranch());
      }
   }

   private void checkNotHistorical(Artifact artifact) throws OseeCoreException {
      if (artifact.isHistorical()) {
         throw new OseeStateException("The artifact [%s] must be at the head of the branch to be edited.",
            artifact.getGuid());
      }
   }

   private boolean isBranchWritable(Branch branch) throws OseeCoreException {
      boolean toReturn = true;
      if (!UserManager.duringMainUserCreation()) {
         toReturn =
            getAccess().hasBranchPermission(branch, PermissionEnum.WRITE, Level.FINE).matched() && branch.isEditable();
      }
      return toReturn;
   }

   private void checkAccess(Artifact artifact, RelationLink link) throws OseeCoreException {
      if (UserManager.duringMainUserCreation()) {
         return;
      }
      checkBranch(link);
      Branch txBranch = getBranch();
      if (!link.getBranch().equals(txBranch)) {
         String msg =
            String.format("The relation link [%s] is on branch [%s] but this transaction is for branch [%s]",
               link.getId(), link.getBranch(), txBranch);
         throw new OseeStateException(msg);
      }

      RelationSide sideToCheck = link.getSide(artifact).oppositeSide();
      PermissionStatus status =
         getAccess().canRelationBeModified(artifact, null, new RelationTypeSide(link.getRelationType(), sideToCheck),
            Level.FINE);

      if (!status.matched()) {
         throw new OseeCoreException(
            "Access Denied - [%s] does not have valid permission to edit this relation\n itemsToPersist:[%s]\n reason:[%s]",
            getAuthor(), link, status.getReason());
      }
   }

   private AccessPolicy getAccess() {
      if (access == null) {
         access = Activator.getInstance().getAccessPolicy();
      }
      return access;
   }

   private Collection<BaseTransactionData> getTransactionData() {
      return transactionDataItems.values();
   }

   private Collection<Artifact> getArtifactReferences() {
      return artifactReferences;
   }

   private TransactionRecord getTransactionRecord() throws OseeCoreException {
      if (transactionId == null) {
         transactionId = TransactionManager.internalCreateTransactionRecord(branch, getAuthor(), comment);
      }
      return transactionId;
   }

   /**
    * Reset state so transaction object can be re-used
    */
   private void reset() {
      madeChanges = false;
      transactionDataItems.clear();
      artifactReferences.clear();
      alreadyProcessedArtifacts.clear();
      transactionId = null;
   }

   public Branch getBranch() {
      return branch;
   }

   /**
    * Returns the next transaction to be used by the system<br>
    * <br>
    * IF transaction has not been executed, this is the transaction that will be used.<br>
    * ELSE this is next transaction to be used upon execute
    */
   public int getTransactionNumber() throws OseeCoreException {
      return getTransactionRecord().getId();
   }

   public void addArtifact(Artifact artifact) throws OseeCoreException {
      addArtifact(artifact, true);
   }

   private void addArtifact(Artifact artifact, boolean force) throws OseeCoreException {
      boolean wasAdded = alreadyProcessedArtifacts.add(artifact);
      if (wasAdded || force) {
         addArtifactAndAttributes(artifact);
         addRelations(artifact);
      }
   }

   private void addArtifactAndAttributes(Artifact artifact) throws OseeCoreException {
      if (artifact.hasDirtyAttributes() || artifact.hasDirtyArtifactType() || artifact.getModType() == ModificationType.REPLACED_WITH_VERSION) {
         if (artifact.isDeleted() && !artifact.isInDb()) {
            for (Attribute<?> attribute : artifact.internalGetAttributes()) {
               if (attribute.isDirty()) {
                  attribute.setNotDirty();
               }
            }
            return;
         }
         checkAccess(artifact);
         madeChanges = true;

         if (!artifact.isInDb() || artifact.hasDirtyArtifactType() || artifact.getModType().isDeleted() || artifact.getModType() == ModificationType.REPLACED_WITH_VERSION) {
            BaseTransactionData txItem = transactionDataItems.get(ArtifactTransactionData.class, artifact.getArtId());
            if (txItem == null) {
               artifactReferences.add(artifact);
               txItem = new ArtifactTransactionData(artifact);
               transactionDataItems.put(ArtifactTransactionData.class, artifact.getArtId(), txItem);
            } else {
               updateTxItem(txItem, artifact.getModType());
            }
         }

         for (Attribute<?> attribute : artifact.internalGetAttributes()) {
            if (attribute.isDirty()) {
               artifactReferences.add(artifact);
               addAttribute(artifact, attribute);
            }
         }
      }
   }

   private void addAttribute(Artifact artifact, Attribute<?> attribute) throws OseeCoreException {
      if (attribute.isDeleted() && !attribute.isInDb()) {
         return;
      }
      if (attribute.getId() == 0) {
         attribute.internalSetAttributeId(getNewAttributeId(artifact, attribute));
      }

      BaseTransactionData txItem = transactionDataItems.get(AttributeTransactionData.class, attribute.getId());
      if (txItem == null) {
         txItem = new AttributeTransactionData(attribute);
         transactionDataItems.put(AttributeTransactionData.class, attribute.getId(), txItem);
      } else {
         updateTxItem(txItem, attribute.getModificationType());
      }
   }

   private void addRelations(Artifact artifact) throws OseeCoreException {
      List<RelationLink> links = artifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED);

      for (RelationLink relation : links) {
         if (relation.isDirty()) {
            addRelation(artifact, relation);
         }
      }
   }

   public void addRelation(Artifact artifact, RelationLink link) throws OseeCoreException {
      checkAccess(artifact, link);
      madeChanges = true;
      link.setNotDirty();

      ModificationType modificationType;
      RelationEventType relationEventType; // needed until persist undeleted modtypes and modified == rational only change

      Branch branch = link.getBranch();
      Artifact aArtifact = ArtifactCache.getActive(link.getAArtifactId(), branch);
      Artifact bArtifact = ArtifactCache.getActive(link.getBArtifactId(), branch);
      if (link.isInDb()) {
         if (link.isUnDeleted()) {
            modificationType = ModificationType.MODIFIED; // Temporary until UNDELETED persisted to DB
            relationEventType = RelationEventType.Undeleted;
         } else if (link.isDeleted()) {
            if (aArtifact != null && aArtifact.isDeleted() || bArtifact != null && bArtifact.isDeleted()) {
               modificationType = ModificationType.ARTIFACT_DELETED;
               relationEventType = RelationEventType.Deleted;
            } else {
               modificationType = ModificationType.DELETED;
               relationEventType = RelationEventType.Deleted;
            }
         } else {
            if (link.getModificationType() == ModificationType.REPLACED_WITH_VERSION) {
               modificationType = link.getModificationType();
            } else {
               modificationType = ModificationType.MODIFIED;
            }
            relationEventType = RelationEventType.ModifiedRationale;
         }
      } else {
         if (link.isDeleted()) {
            return;
         }
         link.internalSetRelationId(getNewRelationId());
         modificationType = ModificationType.NEW;
         relationEventType = RelationEventType.Added;
      }

      /**
       * Always want to persist artifacts on other side of dirty relation. This is necessary for ordering attribute to
       * be persisted and desired for other cases.
       */
      addArtifact(aArtifact, false);
      addArtifact(bArtifact, false);

      BaseTransactionData txItem = transactionDataItems.get(RelationTransactionData.class, link.getId());
      if (txItem == null) {
         txItem = new RelationTransactionData(link, modificationType, relationEventType);
         transactionDataItems.put(RelationTransactionData.class, link.getId(), txItem);
      } else {
         updateTxItem(txItem, modificationType);
      }
   }

   private void updateTxItem(BaseTransactionData itemToCheck, ModificationType currentModType) {
      if (itemToCheck.getModificationType() == ModificationType.NEW && currentModType.isDeleted()) {
         transactionDataItems.remove(itemToCheck.getClass(), itemToCheck.getItemId());
      } else {
         itemToCheck.setModificationType(currentModType);
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      int smallWork = calculateWork(0.10);
      try {
         txMonitor.reportTxStart(SkynetTransaction.this, getBranch());
         monitor.worked(smallWork);
         if (madeChanges) {
            IOperation subOp = createStorageOp();
            doSubWork(subOp, monitor, 0.80);
         }
      } finally {
         reset();
         txMonitor.reportTxEnd(SkynetTransaction.this, getBranch());
         monitor.worked(smallWork);
      }
   }

   private IOperation createStorageOp() throws OseeCoreException {
      return new StoreSkynetTransactionOperation(getName(), getBranch(), getTransactionRecord(), getTransactionData(),
         getArtifactReferences());
   }

   //TODO this method needs to be removed
   public void execute() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(this);
   }
}