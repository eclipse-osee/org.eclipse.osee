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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTransactionData;
import org.eclipse.osee.framework.skynet.core.event.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTransactionData;

/**
 * @author Robert A. Fisher
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public class SkynetTransaction extends DbTransaction {
   private static final TransactionMonitor txMonitor = new TransactionMonitor();

   private static final String UPDATE_TXS_NOT_CURRENT =
         "UPDATE osee_txs txs1 SET tx_current = " + TxChange.NOT_CURRENT.getValue() + " WHERE txs1.transaction_id = ? AND txs1.gamma_id = ?";
   private static final String GET_EXISTING_ATTRIBUTE_IDS =
         "SELECT att1.attr_id FROM osee_attribute att1, osee_arts art1, osee_txs txs1 WHERE att1.attr_type_id = ? AND att1.art_id = ? AND att1.art_id = art1.art_id AND art1.gamma_id = txs1.gamma_id AND txs1.branch_id <> ?";

   private TransactionRecord transactionId;

   private final CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData> transactionDataItems =
         new CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData>();

   private final HashCollection<String, Object[]> dataItemInserts = new HashCollection<String, Object[]>();
   private final Map<Integer, String> dataInsertOrder = new HashMap<Integer, String>();

   // Used to avoid garbage collection of artifacts until the transaction has been committed;
   private final Set<Artifact> artifactReferences = new HashSet<Artifact>();

   private final Branch branch;
   private boolean madeChanges = false;
   private boolean executedWithException = false;
   private final String comment;

   public SkynetTransaction(Branch branch, String comment) {
      this.branch = branch;
      this.comment = comment;
      txMonitor.reportTxCreation(this, branch, comment);
   }

   public SkynetTransaction(IOseeBranch branch, String comment) throws OseeCoreException {
      this(BranchManager.getBranch(branch), comment);
   }

   /**
    * Reset state so transaction object can be re-used
    */
   private void reset() {
      madeChanges = false;
      executedWithException = false;
      dataInsertOrder.clear();
      transactionDataItems.clear();
      dataItemInserts.clear();
      artifactReferences.clear();
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

   private void fetchTxNotCurrent(OseeConnection connection, BaseTransactionData transactionData, List<Object[]> results) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         String query = ClientSessionManager.getSql(transactionData.getSelectTxNotCurrentSql());
         chStmt.runPreparedQuery(query, transactionData.getItemId(), branch.getId());
         while (chStmt.next()) {
            results.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }
   }

   private void executeTransactionDataItems(OseeConnection connection) throws OseeCoreException {
      if (transactionDataItems.isEmpty()) {
         return;
      }

      List<Object[]> txNotCurrentData = new ArrayList<Object[]>();
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         // Collect inserts for attribute, relation, artifact, and artifact version tables
         transactionData.addInsertToBatch(this);

         // Collect stale tx currents for batch update
         fetchTxNotCurrent(connection, transactionData, txNotCurrentData);
      }

      // Insert into data tables - i.e. attribute, relation and artifact version tables
      List<Integer> keys = new ArrayList<Integer>(dataInsertOrder.keySet());
      Collections.sort(keys);
      for (int priority : keys) {
         String sqlKey = dataInsertOrder.get(priority);
         ConnectionHandler.runBatchUpdate(connection, sqlKey, (List<Object[]>) dataItemInserts.getValues(sqlKey));
      }

      // Set stale tx currents in txs table
      ConnectionHandler.runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, txNotCurrentData);
   }

   void internalAddInsertToBatch(int insertPriority, String insertSql, Object... data) {
      dataItemInserts.put(insertSql, data);
      dataInsertOrder.put(insertPriority, insertSql);
   }

   @Override
   public void execute() throws OseeCoreException {
      try {
         txMonitor.reportTxStart(this, getBranch());
         if (madeChanges) {
            super.execute();
         }
      } finally {
         txMonitor.reportTxEnd(this, getBranch());
      }
   }

   public int getTransactionNumber() throws OseeCoreException {
      return internalGetTransactionRecord().getId();
   }

   TransactionRecord internalGetTransactionRecord() throws OseeCoreException {
      if (transactionId == null) {
         transactionId = TransactionManager.internalCreateTransactionRecord(branch, UserManager.getUser(), comment);
      }
      return transactionId;
   }

   public void addArtifactAndAttributes(Artifact artifact) throws OseeCoreException {
      checkBranch(artifact);

      if (artifact.isDeleted() && !artifact.isInDb()) {
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

   public void addRelation(RelationLink link) throws OseeCoreException {
      checkBranch(link);
      madeChanges = true;
      link.setNotDirty();

      ModificationType modificationType;

      Artifact aArtifact = ArtifactCache.getActive(link.getAArtifactId(), link.getABranch());
      Artifact bArtifact = ArtifactCache.getActive(link.getBArtifactId(), link.getBBranch());
      if (link.isInDb()) {
         if (link.isDeleted()) {

            if (aArtifact != null && aArtifact.isDeleted() || bArtifact != null && bArtifact.isDeleted()) {
               modificationType = ModificationType.ARTIFACT_DELETED;
            } else {
               modificationType = ModificationType.DELETED;
            }
         } else {
            modificationType = ModificationType.MODIFIED;
         }
      } else {
         if (link.isDeleted()) {
            return;
         }

         link.internalSetRelationId(ConnectionHandler.getSequence().getNextRelationId());
         modificationType = ModificationType.NEW;
      }
      /**
       * Always want to persist artifacts on other side of dirty relation. This is necessary for ordering attribute to
       * be persisted and desired for other cases.
       */
      if (aArtifact != null) aArtifact.persist(this);
      if (bArtifact != null) bArtifact.persist(this);

      BaseTransactionData txItem = transactionDataItems.get(RelationTransactionData.class, link.getId());
      if (txItem == null) {
         txItem = new RelationTransactionData(link, modificationType);
         transactionDataItems.put(RelationTransactionData.class, link.getId(), txItem);
      } else {
         updateTxItem(txItem, modificationType);
      }
   }

   boolean isInTransaction(Class<? extends BaseTransactionData> clazz, int id) {
      return transactionDataItems.containsKey(clazz, id);
   }

   private void updateTxItem(BaseTransactionData itemToCheck, ModificationType currentModType) {
      if (itemToCheck.getModificationType() == ModificationType.NEW && currentModType.isDeleted()) {
         transactionDataItems.remove(itemToCheck.getClass(), itemToCheck.getItemId());
      } else {
         itemToCheck.setModificationType(currentModType);
      }
   }

   @Override
   protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
      TransactionManager.internalPersist(connection, internalGetTransactionRecord());
      executeTransactionDataItems(connection);
      if (branch.getBranchState() == BranchState.CREATED) {
         branch.setBranchState(BranchState.MODIFIED);
         BranchManager.persist(branch);
      }
   }

   @Override
   protected void handleTxException(Exception ex) {
      executedWithException = true;
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         try {
            transactionData.internalOnRollBack();
         } catch (OseeCoreException ex1) {
            OseeLog.log(Activator.class, Level.SEVERE, ex1);
         }
      }
   }

   private void updateModifiedCachedObject() throws OseeCoreException {
      Collection<ArtifactTransactionModifiedEvent> xModifiedEvents = new ArrayList<ArtifactTransactionModifiedEvent>();
      Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();

      // Update all transaction items before collecting events
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         transactionData.internalUpdate(internalGetTransactionRecord());
      }

      // Collect events before clearing any dirty flags
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         transactionData.internalAddToEvents(xModifiedEvents, artifactChanges);
      }

      for (Artifact artifact : artifactReferences) {
         if (artifact.hasDirtyAttributes()) {
            xModifiedEvents.add(new ArtifactModifiedEvent(new Sender(this.getClass().getName()),
                  ArtifactModType.Changed, artifact, artifact.getTransactionNumber(),
                  artifact.getDirtySkynetAttributeChanges()));
            artifactChanges.add(new EventModifiedBasicGuidArtifact(artifact,
                  artifact.getDirtyFrameworkAttributeChanges()));
         }
      }
      // Clear all dirty flags
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         transactionData.internalClearDirtyState();
      }

      if (xModifiedEvents.size() > 0) {
         OseeEventManager.kickTransactionEvent(this, xModifiedEvents, artifactChanges);
         xModifiedEvents.clear();
      }
   }

   @Override
   protected void handleTxFinally() throws OseeCoreException {
      if (!executedWithException) {
         updateModifiedCachedObject();
      }
      reset();
   }
}