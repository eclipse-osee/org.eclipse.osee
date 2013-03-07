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
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTransactionData;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.TxState;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Robert A. Fisher
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public final class SkynetTransaction extends TransactionOperation<Branch> {

   private final CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData> transactionDataItems =
      new CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData>();

   // Used to avoid garbage collection of artifacts until the transaction has been committed and determine attribute events;
   private final Set<Artifact> modifiedArtifacts = new HashSet<Artifact>();
   private final Set<Artifact> alreadyProcessedArtifacts = new HashSet<Artifact>();

   private String comment;
   private User user;

   private AccessPolicy access;
   private int transactionId = -1;

   protected SkynetTransaction(TxMonitor<Branch> txMonitor, Branch branch, String uuid, String comment) {
      super(txMonitor, branch, uuid, comment);
      this.comment = comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
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

   private boolean isBranchWritable(IOseeBranch branch) throws OseeCoreException {
      boolean toReturn = true;
      if (!UserManager.duringMainUserCreation()) {
         Branch fullBranch = BranchManager.getBranch(branch);
         toReturn =
            getAccess().hasBranchPermission(branch, PermissionEnum.WRITE, Level.FINE).matched() && fullBranch.isEditable();
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

   private AccessPolicy getAccess() throws OseeCoreException {
      if (access == null) {
         access = ServiceUtil.getAccessPolicy();
      }
      return access;
   }

   private Collection<BaseTransactionData> getTransactionData() {
      return transactionDataItems.values();
   }

   private Collection<Artifact> getArtifactReferences() {
      return modifiedArtifacts;
   }

   /**
    * Reset state so transaction object can be re-used
    */
   @Override
   protected void clear() {
      transactionDataItems.clear();
      modifiedArtifacts.clear();
      alreadyProcessedArtifacts.clear();
   }

   public Branch getBranch() {
      return getKey();
   }

   public void addArtifact(Artifact artifact) throws OseeCoreException {
      synchronized (getTxMonitor()) {
         addArtifact(artifact, true);
      }
   }

   private void addArtifact(Artifact artifact, boolean force) throws OseeCoreException {
      if (artifact != null) {
         ensureCanBeAdded(artifact);
         boolean wasAdded = alreadyProcessedArtifacts.add(artifact);
         if (wasAdded || force) {
            addArtifactAndAttributes(artifact);
            addRelations(artifact);
         }
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
         setTxState(TxState.MODIFIED);

         if (!artifact.isInDb() || artifact.hasDirtyArtifactType() || artifact.getModType().isDeleted() || artifact.getModType() == ModificationType.REPLACED_WITH_VERSION) {
            BaseTransactionData txItem = transactionDataItems.get(ArtifactTransactionData.class, artifact.getArtId());
            if (txItem == null) {
               modifiedArtifacts.add(artifact);
               txItem = new ArtifactTransactionData(artifact);
               transactionDataItems.put(ArtifactTransactionData.class, artifact.getArtId(), txItem);
            } else {
               updateTxItem(txItem, artifact.getModType());
            }
         }

         for (Attribute<?> attribute : artifact.internalGetAttributes()) {
            if (attribute.isDirty()) {
               modifiedArtifacts.add(artifact);
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

   private Artifact getArtifact(int artId, IOseeBranch branch) throws OseeCoreException {
      try {
         return ArtifactQuery.getArtifactFromId(artId, branch, DeletionFlag.INCLUDE_DELETED);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   public void addRelation(Artifact artifact, RelationLink link) throws OseeCoreException {
      synchronized (getTxMonitor()) {
         checkAccess(artifact, link);
         setTxState(TxState.MODIFIED);
         link.setNotDirty();

         ModificationType modificationType;
         RelationEventType relationEventType; // needed until persist undeleted modtypes and modified == rational only change

         IOseeBranch branch = link.getBranch();
         Artifact aArtifact = getArtifact(link.getAArtifactId(), branch);
         Artifact bArtifact = getArtifact(link.getBArtifactId(), branch);

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
          * Always want to persist artifacts on other side of dirty relation. This is necessary for ordering attribute
          * to be persisted and desired for other cases.
          */
         addArtifact(aArtifact, false);
         addArtifact(bArtifact, false);

         BaseTransactionData txItem = transactionDataItems.get(RelationTransactionData.class, link.getId());
         if (txItem == null) {
            txItem = new RelationTransactionData(link, modificationType, relationEventType);
            transactionDataItems.put(RelationTransactionData.class, link.getId(), txItem);

            modifiedArtifacts.add(aArtifact);
            modifiedArtifacts.add(bArtifact);

         } else {
            updateTxItem(txItem, modificationType);
         }
      }
   }

   private void updateTxItem(BaseTransactionData itemToCheck, ModificationType currentModType) {
      if (itemToCheck.getModificationType() == ModificationType.NEW && currentModType.isDeleted()) {
         transactionDataItems.remove(itemToCheck.getClass(), itemToCheck.getItemId());
      } else {
         itemToCheck.setModificationType(currentModType);
      }
   }

   private IOperation createStorageOp() throws OseeCoreException {
      TransactionRecord transaction =
         TransactionManager.internalCreateTransactionRecord(getBranch(), getAuthor(), comment);
      transactionId = transaction.getId();
      return new StoreSkynetTransactionOperation(getName(), getBranch(), transaction, getTransactionData(),
         getArtifactReferences());
   }

   public int getTransactionId() {
      return transactionId;
   }

   @Override
   public boolean containsItem(Object object) {
      synchronized (getTxMonitor()) {
         return modifiedArtifacts.contains(object);
      }
   }

   @Override
   protected void txWork(IProgressMonitor monitor) throws Exception {
      IOperation subOp = createStorageOp();
      doSubWork(subOp, monitor, 1.00);
   }

   @Override
   public String toString() {
      return String.format("uuid:[%s] branch[%s] comment[%s]", getUuid(), getBranch(), comment);
   }

   //TODO this method needs to be removed
   public void execute() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(this);
   }
}
