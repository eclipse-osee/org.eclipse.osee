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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.INTRODUCED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MODIFIED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.eclipse.osee.framework.core.enums.ModificationType.REPLACED_WITH_VERSION;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
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
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.TxState;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * @author Robert A. Fisher
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public final class SkynetTransaction extends TransactionOperation<BranchId> {
   private static final String ATTR_ID_SEQ = "SKYNET_ATTR_ID_SEQ";
   private static final String REL_LINK_ID_SEQ = "SKYNET_REL_LINK_ID_SEQ";

   private final CompositeKeyHashMap<Class<? extends BaseTransactionData>, Id, BaseTransactionData> transactionDataItems =
      new CompositeKeyHashMap<>();

   // Used to avoid garbage collection of artifacts until the transaction has been committed and determine attribute events;
   private final Set<Artifact> modifiedArtifacts = new HashSet<>();
   private final Set<Artifact> alreadyProcessedArtifacts = new HashSet<>();

   private String comment;
   private User user;

   private AccessPolicy access;
   private TransactionRecord transaction;

   protected SkynetTransaction(TxMonitor<BranchId> txMonitor, BranchId branch, String comment) {
      super(txMonitor, branch, comment);
      this.comment = comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   private AttributeId getNewAttributeId(Artifact artifact, Attribute<?> attribute) throws OseeCoreException {
      return AttributeId.valueOf(ConnectionHandler.getNextSequence(ATTR_ID_SEQ, true));
   }

   private int getNewRelationId() throws OseeCoreException {
      return (int) ConnectionHandler.getNextSequence(REL_LINK_ID_SEQ, true);
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
      BranchId txBranch = getBranch();
      if (!artifact.isOnBranch(txBranch)) {
         String msg = String.format("The artifact [%s] is on branch [%s] but this transaction is for branch [%s]",
            artifact.getGuid(), artifact.getBranch(), txBranch);
         throw new OseeStateException(msg);
      }

      checkBranch(artifact);
      checkNotHistorical(artifact);
      getAccess().hasArtifactPermission(Collections.singleton(artifact), PermissionEnum.WRITE, Level.FINE);
   }

   private void checkBranch(ArtifactToken artifact) throws OseeCoreException {
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

   private boolean isBranchWritable(BranchId branch) throws OseeCoreException {
      boolean toReturn = true;
      if (!UserManager.duringMainUserCreation()) {
         toReturn = getAccess().hasBranchPermission(branch, PermissionEnum.WRITE,
            Level.FINE).matched() && BranchManager.isEditable(branch);
      }
      return toReturn;
   }

   private void checkAccess(Artifact artifact, RelationLink link) throws OseeCoreException {
      if (UserManager.duringMainUserCreation()) {
         return;
      }
      checkBranch(link);
      BranchId txBranch = getBranch();
      if (!link.isOnBranch(txBranch)) {
         String msg = String.format("The relation link [%s] is on branch [%s] but this transaction is for branch [%s]",
            link.getId(), link.getBranch(), txBranch);
         throw new OseeStateException(msg);
      }

      RelationSide sideToCheck = link.getSide(artifact).oppositeSide();
      PermissionStatus status = getAccess().canRelationBeModified(artifact, null,
         new RelationTypeSide(link.getRelationType(), sideToCheck), Level.FINE);

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

   public Collection<Artifact> getArtifactReferences() {
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

   public BranchId getBranch() {
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
      if (artifact.hasDirtyAttributes() || artifact.hasDirtyArtifactType() || artifact.getModType() == REPLACED_WITH_VERSION || artifact.isUseBackingdata()) {
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

         if (!artifact.isInDb() || artifact.hasDirtyArtifactType() || artifact.getModType().isDeleted() || artifact.getModType() == REPLACED_WITH_VERSION || artifact.isUseBackingdata()) {
            BaseTransactionData txItem = transactionDataItems.get(ArtifactTransactionData.class, artifact);
            if (txItem == null) {
               modifiedArtifacts.add(artifact);
               txItem = new ArtifactTransactionData(artifact);
               transactionDataItems.put(ArtifactTransactionData.class, artifact, txItem);
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

   private void checkMultiplicity(Artifact artifact, Attribute<?> attr) {
      if (attr.getAttributeType().getMaxOccurrences() == 1 && artifact.getAttributeCount(attr.getAttributeType()) > 1) {
         throw new OseeStateException("Artifact %s can only have 1 [%s] attribute but has %d",
            artifact.toStringWithId(), attr.getAttributeType().getName(),
            artifact.getAttributeCount(attr.getAttributeType()));
      }
   }

   private void checkMultiplicity(Artifact art, RelationLink link) {
      RelationType relationType = RelationTypeManager.getType(link.getRelationType());
      RelationTypeMultiplicity multiplicity = relationType.getMultiplicity();

      RelationSide sideToCheck = link.getOppositeSide(art);
      int limitToCheck = sideToCheck.isSideA() ? multiplicity.getSideALimit() : multiplicity.getSideBLimit();
      if (limitToCheck == 1) {
         int count = art.getRelatedArtifactsCount(RelationTypeSide.create(relationType, sideToCheck));
         if (count > 1) {
            throw new OseeStateException("Artifact %s can only have 1 [%s] on [%s] but has %d", art.toStringWithId(),
               relationType.getSideName(sideToCheck), sideToCheck.name(), count);
         }
      }
   }

   private void addAttribute(Artifact artifact, Attribute<?> attribute) throws OseeCoreException {
      if (attribute.isDeleted() && !attribute.isInDb()) {
         return;
      }

      checkMultiplicity(artifact, attribute);

      if (attribute.isInvalid()) {
         attribute.internalSetAttributeId(getNewAttributeId(artifact, attribute));
      }

      BaseTransactionData txItem = transactionDataItems.get(AttributeTransactionData.class, attribute);
      if (txItem == null) {
         txItem = new AttributeTransactionData(attribute);
         transactionDataItems.put(AttributeTransactionData.class, attribute, txItem);
      } else {
         updateTxItem(txItem, attribute.getModificationType());
      }
   }

   private void addRelations(Artifact artifact) throws OseeCoreException {
      List<RelationLink> links = artifact.getRelationsAll(INCLUDE_DELETED);

      for (RelationLink relation : links) {
         if (relation.isDirty()) {
            addRelation(artifact, relation);
         }
      }
   }

   private void addRelation(Artifact artifact, RelationLink link) throws OseeCoreException {
      synchronized (getTxMonitor()) {
         checkAccess(artifact, link);
         setTxState(TxState.MODIFIED);
         link.setNotDirty();

         ModificationType modificationType;
         RelationEventType relationEventType; // needed until persist undeleted modtypes and modified == rational only change

         BranchId branch = link.getBranch();
         Artifact aArtifact = ArtifactQuery.checkArtifactFromId(link.getAArtifactId(), branch, INCLUDE_DELETED);
         Artifact bArtifact = ArtifactQuery.checkArtifactFromId(link.getBArtifactId(), branch, INCLUDE_DELETED);

         if (link.isInDb()) {
            if (link.isUnDeleted()) {
               modificationType = MODIFIED; // Temporary until UNDELETED persisted to DB
               relationEventType = RelationEventType.Undeleted;
            } else if (link.isDeleted()) {
               if (aArtifact != null && aArtifact.isDeleted() || bArtifact != null && bArtifact.isDeleted()) {
                  modificationType = ARTIFACT_DELETED;
                  relationEventType = RelationEventType.Deleted;
               } else {
                  modificationType = DELETED;
                  relationEventType = RelationEventType.Deleted;
               }
            } else {
               if (link.isUseBackingData() || link.getModificationType().matches(REPLACED_WITH_VERSION, INTRODUCED)) {
                  modificationType = link.getModificationType();
               } else {
                  modificationType = MODIFIED;
               }
               relationEventType = RelationEventType.ModifiedRationale;
            }
         } else {
            if (link.isDeleted()) {
               return;
            }
            checkMultiplicity(artifact, link);
            link.internalSetRelationId(getNewRelationId());
            modificationType = NEW;
            relationEventType = RelationEventType.Added;
         }

         /**
          * Always want to persist artifacts on other side of dirty relation. This is necessary for ordering attribute
          * to be persisted and desired for other cases.
          */
         addArtifact(aArtifact, false);
         addArtifact(bArtifact, false);

         Id relId = Id.valueOf(link.getId());
         BaseTransactionData txItem = transactionDataItems.get(RelationTransactionData.class, relId);
         if (txItem == null) {
            txItem = new RelationTransactionData(link, modificationType, relationEventType);
            transactionDataItems.put(RelationTransactionData.class, relId, txItem);

            if (aArtifact != null) {
               modifiedArtifacts.add(aArtifact);
            }
            if (bArtifact != null) {
               modifiedArtifacts.add(bArtifact);
            }

         } else {
            updateTxItem(txItem, modificationType);
         }
      }
   }

   private void updateTxItem(BaseTransactionData itemToCheck, ModificationType currentModType) {
      if (itemToCheck.getModificationType() == NEW && currentModType.isDeleted()) {
         transactionDataItems.removeAndGet(itemToCheck.getClass(), itemToCheck.getItemId());
      } else {
         itemToCheck.setModificationType(currentModType);
      }
   }

   private IOperation createStorageOp() throws OseeCoreException {
      transaction = internalCreateTransaction(getBranch(), getAuthor(), comment);
      return new StoreSkynetTransactionOperation(getName(), getBranch(), transaction, getTransactionData(),
         getArtifactReferences());
   }

   public static synchronized TransactionRecord internalCreateTransaction(BranchId branch, User userToBlame, String comment) throws OseeCoreException {
      if (comment == null) {
         comment = "";
      }
      Integer authorArtId = userToBlame.getArtId();
      TransactionDetailsType txType = TransactionDetailsType.NonBaselined;
      Date timestamp = GlobalTime.GreenwichMeanTimestamp();
      //keep transaction id's sequential in the face of concurrent transaction by multiple users
      Long txId = ConnectionHandler.getNextSequence("SKYNET_TRANSACTION_ID_SEQ", false);

      return new TransactionRecord(txId, branch, comment, timestamp, authorArtId, 0, txType);
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
   public TransactionId execute() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(this);
      return transaction;
   }

   public void cancel() {
      getTxMonitor().cancel(getBranch(), this);
   }

}
