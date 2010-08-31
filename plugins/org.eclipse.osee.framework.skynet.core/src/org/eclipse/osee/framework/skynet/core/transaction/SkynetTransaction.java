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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.lifecycle.AbstractLifecyclePoint;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
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

/**
 * @author Robert A. Fisher
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public final class SkynetTransaction extends AbstractOperation {

   private static final String GET_EXISTING_ATTRIBUTE_IDS =
      "SELECT att1.attr_id FROM osee_attribute att1, osee_artifact art1, osee_txs txs1 WHERE att1.attr_type_id = ? AND att1.art_id = ? AND att1.art_id = art1.art_id AND art1.gamma_id = txs1.gamma_id AND txs1.branch_id <> ?";

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

   public SkynetTransaction(Branch branch, String comment) {
      super(comment, Activator.PLUGIN_ID);
      this.branch = branch;
      this.comment = comment;
      txMonitor.reportTxCreation(this, branch, comment);
   }

   public SkynetTransaction(IOseeBranch branch, String comment) throws OseeCoreException {
      this(BranchManager.getBranch(branch), comment);
   }

   private int getNewAttributeId(Artifact artifact, Attribute<?> attribute) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      AttributeType attributeType = attribute.getAttributeType();
      int attrId = -1;
      // reuse an existing attribute id when there should only be a max of one and it has already been created on another branch
      if (attributeType.getMaxOccurrences() == 1) {
         try {
            chStmt.runPreparedQuery(GET_EXISTING_ATTRIBUTE_IDS, attributeType.getId(), artifact.getArtId(),
               artifact.getBranch().getId());

            if (chStmt.next()) {
               attrId = chStmt.getInt("attr_id");
            }
         } finally {
            chStmt.close();
         }
      }
      if (attrId < 1) {
         attrId = ConnectionHandler.getSequence().getNextAttributeId();
      }
      return attrId;
   }

   private int getNewRelationId() throws OseeDataStoreException {
      return ConnectionHandler.getSequence().getNextRelationId();
   }

   private User getAuthor() throws OseeDataStoreException, OseeCoreException {
      if (user == null) {
         user = UserManager.getUser();
      }
      return user;
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
    * Performs branch validation checks
    */
   private void checkBranch(Artifact artifact) throws OseeStateException {
      ensureCorrectBranch(artifact);
      ensureBranchIsEditable(artifact);
   }

   /**
    * Performs branch validation checks
    */
   private void checkBranch(RelationLink link) throws OseeStateException {
      ensureCorrectBranch(link);
      ensureBranchIsEditable(link);
   }

   private void ensureCorrectBranch(RelationLink link) throws OseeStateException {
      if (!link.getBranch().equals(branch)) {
         String msg =
            String.format("The relation link [%s] is on branch [%s] but this transaction is for branch [%s]",
               link.getId(), link.getBranch(), branch);
         throw new OseeStateException(msg);
      }
   }

   private void ensureCorrectBranch(Artifact artifact) throws OseeStateException {
      if (!artifact.getBranch().equals(branch)) {
         String msg =
            String.format("The artifact [%s] is on branch [%s] but this transaction is for branch [%s]",
               artifact.getGuid(), artifact.getBranch(), branch);
         throw new OseeStateException(msg);
      }
   }

   private void ensureBranchIsEditable(RelationLink link) throws OseeStateException {
      if (!link.getBranch().isEditable()) {
         String msg =
            String.format("The relation link [%s] is on a non-editable branch [%s]", link.getId(), link.getBranch());
         throw new OseeStateException(msg);
      }
   }

   private void ensureBranchIsEditable(Artifact artifact) throws OseeStateException {
      if (!artifact.getBranch().isEditable()) {
         String msg =
            String.format("The artifact [%s] is on a non-editable branch [%s]", artifact.getGuid(),
               artifact.getBranch());
         throw new OseeStateException(msg);
      }
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

   public void addArtifactAndAttributes(Artifact artifact) throws OseeCoreException {
      checkBranch(artifact);

      if (artifact.isDeleted() && !artifact.isInDb()) {
         for (Attribute<?> attribute : artifact.internalGetAttributes()) {
            if (attribute.isDirty()) {
               attribute.setNotDirty();
            }
         }
         return;
      }
      madeChanges = true;

      if (!artifact.isInDb() || artifact.hasDirtyArtifactType() || artifact.getModType().isDeleted()) {
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

   public void addRelation(RelationLink link) throws OseeCoreException {
      checkBranch(link);
      madeChanges = true;
      link.setNotDirty();

      ModificationType modificationType;
      RelationEventType relationEventType; // needed until persist undeleted modtypes and modified == rational only change

      Artifact aArtifact = ArtifactCache.getActive(link.getAArtifactId(), link.getABranch());
      Artifact bArtifact = ArtifactCache.getActive(link.getBArtifactId(), link.getBBranch());
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
            modificationType = ModificationType.MODIFIED;
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

      persitRelatedArtifact(aArtifact);
      persitRelatedArtifact(bArtifact);

      BaseTransactionData txItem = transactionDataItems.get(RelationTransactionData.class, link.getId());
      if (txItem == null) {
         txItem = new RelationTransactionData(link, modificationType, relationEventType);
         transactionDataItems.put(RelationTransactionData.class, link.getId(), txItem);
      } else {
         updateTxItem(txItem, modificationType);
      }
   }

   /**
    * Always want to persist artifacts on other side of dirty relation. This is necessary for ordering attribute to be
    * persisted and desired for other cases.
    */
   private void persitRelatedArtifact(Artifact artifact) throws OseeCoreException {
      if (artifact != null) {
         if (!alreadyProcessedArtifacts.contains(artifact)) {
            alreadyProcessedArtifacts.add(artifact);
            artifact.persist(this);
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

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      int smallWork = calculateWork(0.10);
      try {
         txMonitor.reportTxStart(SkynetTransaction.this, getBranch());
         monitor.worked(smallWork);
         if (madeChanges) {
            IOperation subOp = createLifeCycleOp();
            doSubWork(subOp, monitor, 0.80);
         }
      } finally {
         reset();
         txMonitor.reportTxEnd(SkynetTransaction.this, getBranch());
         monitor.worked(smallWork);
      }
   }

   private IOperation createLifeCycleOp() throws OseeCoreException {
      ILifecycleService service = Activator.getInstance().getLifecycleServices();

      Set<IBasicArtifact<?>> objectsToCheck = new HashSet<IBasicArtifact<?>>();
      objectsToCheck.addAll(getArtifactReferences());
      objectsToCheck.addAll(alreadyProcessedArtifacts);
      AbstractLifecyclePoint<?> lifecyclePoint = new SkynetTransactionCheckPoint(getAuthor(), objectsToCheck);
      return new StoreSkynetTransactionOperation(getName(), service, lifecyclePoint, getBranch(),
         getTransactionRecord(), getTransactionData(), getArtifactReferences());
   }

   //TODO this method needs to be removed
   public void execute() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(this);
   }
}