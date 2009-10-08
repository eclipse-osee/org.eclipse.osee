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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.OseeDbConnection;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTransactionData;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
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
   private static final String UPDATE_TXS_NOT_CURRENT =
         "UPDATE osee_txs txs1 SET tx_current = " + TxChange.NOT_CURRENT.getValue() + " WHERE txs1.transaction_id = ? AND txs1.gamma_id = ?";
   private static final String GET_EXISTING_ATTRIBUTE_IDS =
         "SELECT att1.attr_id FROM osee_attribute att1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE att1.attr_type_id = ? AND att1.art_id = ? AND att1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id <> ?";

   private TransactionId transactionId;

   private final CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData> transactionDataItems =
         new CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData>();

   private final HashCollection<String, Object[]> dataItemInserts = new HashCollection<String, Object[]>();
   private final Map<Integer, String> dataInsertOrder = new HashMap<Integer, String>();

   private final Branch branch;
   private boolean madeChanges = false;
   private boolean executedWithException = false;
   private final String comment;

   @Deprecated
   // please provide a more meaningful comment than empty quote
   public SkynetTransaction(Branch branch) throws OseeCoreException {
      this(branch, "");
   }

   public SkynetTransaction(Branch branch, String comment) throws OseeCoreException {
      this.branch = branch;
      this.comment = comment;
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
      transactionId = null;
   }

   /**
    * @return the branch
    */
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
                     link.getRelationId(), link.getBranch(), branch);
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
               String.format("The relation link [%s] is on a non-editable branch [%s]", link.getRelationId(),
                     link.getBranch());
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
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      try {
         String query = ClientSessionManager.getSql(transactionData.getSelectTxNotCurrentSql());
         chStmt.runPreparedQuery(query, transactionData.getItemId(), this.branch.getBranchId());
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
      if (madeChanges) {
         super.execute();
      } else {
         OseeDbConnection.reportTxStart(this);
         OseeDbConnection.reportTxEnd(this);
      }
   }

   /**
    * @return the transaction number.
    * @throws OseeDataStoreException
    */
   public int getTransactionNumber() throws OseeCoreException {
      return internalGetTransactionId().getTransactionNumber();
   }

   /**
    * @return Returns the transactionId.
    * @throws OseeDataStoreException
    */
   TransactionId internalGetTransactionId() throws OseeCoreException {
      if (transactionId == null) {
         transactionId = TransactionIdManager.createNextTransactionId(branch, UserManager.getUser(), comment);
      }
      return transactionId;
   }

   public void addArtifact(Artifact artifact) throws OseeCoreException {
      checkBranch(artifact);

      if (artifact.isDeleted() && !artifact.isInDb()) {
         return;
      }

      madeChanges = true;
      BaseTransactionData txItem = transactionDataItems.get(ArtifactTransactionData.class, artifact.getArtId());
      if (txItem == null) {
         txItem = new ArtifactTransactionData(artifact);
         transactionDataItems.put(ArtifactTransactionData.class, artifact.getArtId(), txItem);
      } else {
         updateTxItem(txItem, artifact.getModType());
      }

      for (Attribute<?> attribute : artifact.internalGetAttributes()) {
         if (attribute.isDirty()) {
            addAttribute(artifact, attribute);
         }
      }
   }

   private void addAttribute(Artifact artifact, Attribute<?> attribute) throws OseeCoreException {
      if (attribute.isDeleted() && !attribute.isInDb()) {
         return;
      }
      if (attribute.getAttrId() == 0) {
         attribute.internalSetAttributeId(getNewAttributeId(artifact, attribute));
      }

      BaseTransactionData txItem = transactionDataItems.get(AttributeTransactionData.class, attribute.getAttrId());
      if (txItem == null) {
         txItem = new AttributeTransactionData(attribute);
         transactionDataItems.put(AttributeTransactionData.class, attribute.getAttrId(), txItem);
      } else {
         updateTxItem(txItem, attribute.getModificationType());
      }
   }

   private int getNewAttributeId(Artifact artifact, Attribute<?> attribute) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      AttributeType attributeType = attribute.getAttributeType();
      int attrId = -1;
      // reuse an existing attribute id when there should only be a max of one and it has already been created on another branch 
      if (attributeType.getMaxOccurrences() == 1) {
         try {
            chStmt.runPreparedQuery(GET_EXISTING_ATTRIBUTE_IDS, attributeType.getId(), artifact.getArtId(),
                  artifact.getBranch().getBranchId());

            if (chStmt.next()) {
               attrId = chStmt.getInt("attr_id");
            }
         } finally {
            chStmt.close();
         }
      }
      if (attrId < 1) {
         attrId = SequenceManager.getNextAttributeId();
      }
      return attrId;
   }

   public void addRelation(RelationLink link) throws OseeCoreException {
      checkBranch(link);
      madeChanges = true;
      link.setNotDirty();

      ModificationType modificationType;

      if (link.isInDb()) {
         if (link.isDeleted()) {
            Artifact aArtifact = ArtifactCache.getActive(link.getAArtifactId(), link.getABranch());
            Artifact bArtifact = ArtifactCache.getActive(link.getBArtifactId(), link.getBBranch());

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

         Artifact aArtifact = link.getArtifact(RelationSide.SIDE_A);
         if (!aArtifact.isInDb()) {
            aArtifact.persist(this);
         }
         Artifact bArtifact = link.getArtifact(RelationSide.SIDE_B);
         if (!bArtifact.isInDb()) {
            bArtifact.persist(this);
         }

         link.internalSetRelationId(SequenceManager.getNextRelationId());
         modificationType = ModificationType.NEW;
      }

      BaseTransactionData txItem = transactionDataItems.get(RelationTransactionData.class, link.getRelationId());
      if (txItem == null) {
         txItem = new RelationTransactionData(link, modificationType);
         transactionDataItems.put(RelationTransactionData.class, link.getRelationId(), txItem);
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
      executeTransactionDataItems(connection);
      if (branch.getBranchState() == BranchState.CREATED) {
         branch.setBranchState(BranchState.MODIFIED);
         branch.persist();
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

      // Update all transaction items before collecting events
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         transactionData.internalUpdate(internalGetTransactionId());
      }

      // Collect events before clearing any dirty flags
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         transactionData.internalAddToEvents(xModifiedEvents);
      }

      // Clear all dirty flags
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         transactionData.internalClearDirtyState();
      }

      if (xModifiedEvents.size() > 0) {
         OseeEventManager.kickTransactionEvent(this, xModifiedEvents);
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