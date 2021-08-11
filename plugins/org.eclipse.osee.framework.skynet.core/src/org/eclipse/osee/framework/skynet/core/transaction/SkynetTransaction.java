/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
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
   private TransactionRecord transaction;
   private static boolean overrideAccess;

   protected SkynetTransaction(TxMonitor<BranchId> txMonitor, BranchId branch, String comment) {
      super(txMonitor, branch, comment);
      this.comment = comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   private AttributeId getNewAttributeId(Artifact artifact, Attribute<?> attribute) {
      return AttributeId.valueOf(ConnectionHandler.getNextSequence(ATTR_ID_SEQ, true));
   }

   private int getNewRelationId() {
      return (int) ConnectionHandler.getNextSequence(REL_LINK_ID_SEQ, true);
   }

   private User getAuthor() {
      if (user == null) {
         user = UserManager.getUser();
      }
      return user;
   }

   private void checkAccess(Artifact artifact) {
      if (UserManager.duringMainUserCreation()) {
         return;
      }
      BranchId txBranch = getBranch();
      if (!artifact.isOnBranch(txBranch)) {
         Branch branch = BranchManager.getBranch(artifact.getBranch());
         String msg = getCheckAccessError(artifact, txBranch, branch);
         throw new OseeStateException(msg);
      }
      if (!SkynetTransaction.isOverrideAccess()) {
         for (Attribute<?> attr : artifact.getAttributes()) {
            if (attr.isDirty()) {
               XResultData rd = ServiceUtil.getOseeClient().getAccessControlService().hasAttributeTypePermission(
                  Collections.singleton(artifact), attr.getAttributeType(), PermissionEnum.WRITE,
                  AccessControlArtifactUtil.getXResultAccessHeader("Skynet Transaction: " + comment, artifact));
               if (rd.isErrors()) {
                  throw new OseeCoreException(rd.toString());
               }
            }
         }
         for (RelationLink rel : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
            if (rel.isDirty()) {
               XResultData rd = ServiceUtil.getOseeClient().getAccessControlService().hasRelationTypePermission(artifact,
                  rel.getRelationType(), Collections.emptyList(), PermissionEnum.WRITE,
                  AccessControlArtifactUtil.getXResultAccessHeader("Skynet Transaction: " + comment, artifact));
               if (rd.isErrors()) {
                  throw new OseeCoreException(rd.toString());
               }
            }
         }
      }
      checkNotHistorical(artifact);
   }

   public String getCheckAccessError(ArtifactToken artifact, BranchId txBranch, BranchToken branch) {
      String msg =
         String.format("The artifact\n\n%s\n\nis on branch\n\n%s\n\nbut this transaction is for branch\n\n%s\n\n",
            artifact.getGuid(), branch.toStringWithId(), txBranch);
      return msg;
   }

   private void checkNotHistorical(Artifact artifact) {
      if (artifact.isHistorical()) {
         String msg = getCheckNotHistoricalError(artifact);
         throw new OseeStateException(msg);
      }
   }

   public String getCheckNotHistoricalError(Artifact artifact) {
      String msg =
         String.format("The artifact\n\n%s\n\nmust be at the head of the branch to be edited.", artifact.getGuid());
      return msg;
   }

   private void checkAccess(Artifact artifact, RelationLink link) {
      if (UserManager.duringMainUserCreation()) {
         return;
      }
      if (!SkynetTransaction.isOverrideAccess()) {
         BranchId txBranch = getBranch();
         if (!link.isOnBranch(txBranch)) {
            RelationSide sideToCheck = link.getSide(artifact).oppositeSide();
            RelationTypeSide relTypeSide = new RelationTypeSide(link.getRelationType(), sideToCheck);
            XResultData rd =
               ServiceUtil.getOseeClient().getAccessControlService().hasRelationTypePermission(artifact, relTypeSide, null,
                  PermissionEnum.WRITE, AccessControlArtifactUtil.getXResultAccessHeader("Relation Access Denied",
                     Collections.singleton(artifact), relTypeSide));
            if (rd.isErrors()) {
               throw new OseeCoreException(rd.toString());
            }
         }
      }
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

   public void addArtifact(Artifact artifact) {
      synchronized (getTxMonitor()) {
         addArtifact(artifact, true);
      }
   }

   private void addArtifact(Artifact artifact, boolean force) {
      if (artifact != null) {
         ensureCanBeAdded(artifact);
         boolean wasAdded = alreadyProcessedArtifacts.add(artifact);
         if (wasAdded || force) {
            addArtifactAndAttributes(artifact);
            addRelations(artifact);
         }
      }
   }

   private void addArtifactAndAttributes(Artifact artifact) {
      if (artifact.hasDirtyAttributes() || artifact.hasDirtyArtifactType() || artifact.getModType() == REPLACED_WITH_VERSION || artifact.isUseBackingdata()) {
         if (artifact.isDeleted() && !artifact.isInDb()) {
            for (Attribute<?> attribute : artifact.internalGetAttributes()) {
               if (attribute.isDirty()) {
                  attribute.setNotDirty();
               }
            }
            return;
         }
         if (!SkynetTransaction.isOverrideAccess()) {
            checkAccess(artifact);
         }
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
      AttributeTypeToken attributeType = attr.getAttributeType();
      if (attr.getArtifact().getArtifactType().getMax(attributeType) == 1 && artifact.getAttributeCount(
         attributeType) > 1) {
         throw new OseeStateException("Artifact %s can only have 1 [%s] attribute but has %d",
            artifact.toStringWithId(), attr.getAttributeType().getName(),
            artifact.getAttributeCount(attr.getAttributeType()));
      }
   }

   private void checkMultiplicity(Artifact art, RelationLink link) {
      RelationTypeToken relationType = link.getRelationType();
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

   private void addAttribute(Artifact artifact, Attribute<?> attribute) {
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

   private void addRelations(Artifact artifact) {
      List<RelationLink> links = artifact.getRelationsAll(INCLUDE_DELETED);

      for (RelationLink relation : links) {
         if (relation.isDirty()) {
            addRelation(artifact, relation);
         }
      }
   }

   private void addRelation(Artifact artifact, RelationLink link) {
      synchronized (getTxMonitor()) {
         if (!SkynetTransaction.isOverrideAccess()) {
            checkAccess(artifact, link);
         }
         setTxState(TxState.MODIFIED);
         link.setNotDirty();

         ModificationType modificationType;
         RelationEventType relationEventType; // needed until persist undeleted modtypes and modified == rational only change

         Artifact aArtifact = ArtifactQuery.getArtifactFromToken(link.getArtifactIdA(), INCLUDE_DELETED);
         Artifact bArtifact = ArtifactQuery.getArtifactFromToken(link.getArtifactIdB(), INCLUDE_DELETED);

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

   private IOperation createStorageOp() {
      transaction = internalCreateTransaction(getBranch(), getAuthor(), comment);
      return new StoreSkynetTransactionOperation(getName(), getBranch(), transaction, getTransactionData(),
         getArtifactReferences());
   }

   public static synchronized TransactionRecord internalCreateTransaction(BranchId branch, User userToBlame, String comment) {
      if (comment == null) {
         comment = "";
      }
      TransactionDetailsType txType = TransactionDetailsType.NonBaselined;
      Date timestamp = GlobalTime.GreenwichMeanTimestamp();
      //keep transaction id's sequential in the face of concurrent transaction by multiple users
      Long txId = ConnectionHandler.getNextSequence("SKYNET_TRANSACTION_ID_SEQ", false);

      return new TransactionRecord(txId, branch, comment, timestamp, userToBlame, ArtifactId.SENTINEL, txType,
         OseeCodeVersion.getVersionId());
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
   public TransactionToken execute() {
      Operations.executeWorkAndCheckStatus(this);
      return transaction;
   }

   public void cancel() {
      getTxMonitor().cancel(getBranch(), this);
   }

   public static boolean isOverrideAccess() {
      return overrideAccess;
   }

   public static void setOverrideAccess(boolean overrideAccess) {
      if (OseeProperties.isInTest()) {
         SkynetTransaction.overrideAccess = overrideAccess;
      } else {
         throw new OseeArgumentException("Access Control can not be overridden in production");
      }
   }

}
