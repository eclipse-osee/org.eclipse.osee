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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTransactionData;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.event.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationTransactionData;

/**
 * @author Robert A. Fisher
 */
public final class SkynetTransaction extends DbTransaction {
   private static final String UPDATE_TXS_NOT_CURRENT =
         "UPDATE osee_txs txs1 SET tx_current = " + TxChange.NOT_CURRENT.getValue() + " WHERE txs1.transaction_id = ? AND txs1.gamma_id = ?";
   private static final String GET_EXISTING_ATTRIBUTE_IDS =
         "SELECT att1.attr_id FROM osee_attribute att1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE att1.attr_type_id = ? AND att1.art_id = ? AND att1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id <> ?";

   private TransactionId transactionId;

   private final List<ArtifactTransactionModifiedEvent> xModifiedEvents =
         new ArrayList<ArtifactTransactionModifiedEvent>();

   private final CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData> transactionDataItems =
         new CompositeKeyHashMap<Class<? extends BaseTransactionData>, Integer, BaseTransactionData>();

   private final HashCollection<String, Object[]> dataItemInserts = new HashCollection<String, Object[]>();
   private final Map<Integer, String> dataInsertOrder = new HashMap<Integer, String>();

   private final Branch branch;
   private boolean madeChanges = false;

   public SkynetTransaction(Branch branch) throws OseeCoreException {
      this.branch = branch;
   }

   /**
    * Reset state so transaction object can be re-used
    */
   private void clear() {
      madeChanges = false;
      dataInsertOrder.clear();
      transactionDataItems.clear();
      dataItemInserts.clear();
      xModifiedEvents.clear();
      transactionId = null;
   }

   /**
    * @return the branch
    */
   public Branch getBranch() {
      return branch;
   }

   private void ensureCorrectBranch(Artifact artifact) throws OseeStateException {
      if (!artifact.getBranch().equals(branch)) {
         String msg =
               String.format("The artifact [%s] is on branch [%s] but this transaction is for branch [%s]",
                     artifact.getHumanReadableId(), artifact.getBranch(), branch);
         throw new OseeStateException(msg);
      }
   }

   private void fetchTxNotCurrent(Connection connection, BaseTransactionData transactionData, List<Object[]> results) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      try {
         String query = ClientSessionManager.getSQL(transactionData.getSelectTxNotCurrentSql());
         chStmt.runPreparedQuery(query, transactionData.getItemId(), this.branch.getBranchId());
         while (chStmt.next()) {
            results.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }
   }

   private void executeTransactionDataItems(Connection connection) throws OseeCoreException {
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

   private void addArtifactModifiedEvent(Object sourceObject, ModificationType modificationType, Artifact artifact) throws OseeCoreException {
      madeChanges = true;
      ArtifactModType artifactModType;
      switch (modificationType) {
         case CHANGE:
            artifactModType = ArtifactModType.Changed;
            break;
         case DELETED:
            artifactModType = ArtifactModType.Deleted;
            break;
         default:
            artifactModType = ArtifactModType.Added;
            break;
      }
      xModifiedEvents.add(new ArtifactModifiedEvent(new Sender(sourceObject), artifactModType, artifact,
            internalGetTransactionId().getTransactionNumber(), artifact.getDirtySkynetAttributeChanges()));
   }

   public void execute() throws OseeCoreException {
      if (madeChanges) {
         super.execute();
      } else {
         OseeDbConnection.reportTxStart(this);
         OseeDbConnection.reportTxEnd(this);
      }
   }

   /**
    * @return Returns the transactionId.
    * @throws OseeDataStoreException
    */
   TransactionId internalGetTransactionId() throws OseeCoreException {
      if (transactionId == null) {
         transactionId = TransactionIdManager.createNextTransactionId(branch, UserManager.getUser(), "");
      }
      return transactionId;
   }

   public void deleteArtifact(Artifact artifact, boolean reorderRelations) throws OseeCoreException {
      if (!artifact.isInDb()) return;
      ensureCorrectBranch(artifact);
      madeChanges = true;

      addArtifactHelper(artifact, ModificationType.DELETED);
      artifact.deleteAttributes();
      RelationManager.deleteRelationsAll(artifact, reorderRelations);

      artifact.persistAttributesAndRelations(this);

      // Kick Local Event
      addArtifactModifiedEvent("persistArtifact()", ModificationType.DELETED, artifact);
   }

   public void addArtifact(Artifact artifact) throws OseeCoreException {
      ensureCorrectBranch(artifact);
      madeChanges = true;

      ModificationType modificationType;

      if (!artifact.isInDb()) {
         modificationType = ModificationType.NEW;
      } else {
         if (artifact.isDeleted()) {
            modificationType = ModificationType.DELETED;
         } else {
            modificationType = ModificationType.CHANGE;
         }
      }

      addArtifactHelper(artifact, modificationType);

      // Add Attributes to Transaction
      for (Attribute<?> attribute : artifact.internalGetAttributes()) {
         if (attribute != null && attribute.isDirty()) {
            addAttribute(artifact, attribute);
         }
      }

      // Kick Local Event
      addArtifactModifiedEvent("persistArtifact()", modificationType, artifact);
   }

   private void addArtifactHelper(Artifact artifact, ModificationType modificationType) throws OseeCoreException {
      BaseTransactionData txItem = transactionDataItems.get(ArtifactTransactionData.class, artifact.getArtId());
      if (txItem == null) {
         txItem = new ArtifactTransactionData(artifact, modificationType);
         transactionDataItems.put(ArtifactTransactionData.class, artifact.getArtId(), txItem);
      } else {
         updateTxItem(txItem, modificationType);
      }

   }

   private void addAttribute(Artifact artifact, Attribute<?> attribute) throws OseeCoreException {
      ModificationType modificationType;
      if (attribute.isDeleted()) {
         if (artifact.isDeleted()) {
            modificationType = ModificationType.ARTIFACT_DELETED;
         } else {
            modificationType = ModificationType.DELETED;
         }
      } else {
         if (attribute.isInDb()) {
            modificationType = ModificationType.CHANGE;
         } else {
            modificationType = ModificationType.NEW;
         }
      }

      if (!attribute.isInDb()) {
         attribute.internalSetAttributeId(getNewAttributeId(artifact, attribute));
      }

      BaseTransactionData txItem = transactionDataItems.get(AttributeTransactionData.class, attribute.getAttrId());
      if (txItem == null) {
         txItem = new AttributeTransactionData(attribute, modificationType);
         transactionDataItems.put(AttributeTransactionData.class, attribute.getAttrId(), txItem);
      } else {
         updateTxItem(txItem, modificationType);
      }
   }

   private int getNewAttributeId(Artifact artifact, Attribute<?> attribute) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      AttributeType attributeType = attribute.getAttributeType();
      int attrId = -1;
      // reuse an existing attribute id when there should only be a max of one and it has already been created on another branch 
      if (attributeType.getMaxOccurrences() == 1) {
         try {
            chStmt.runPreparedQuery(GET_EXISTING_ATTRIBUTE_IDS, attributeType.getAttrTypeId(), artifact.getArtId(),
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
      madeChanges = true;

      link.setNotDirty();

      ModificationType modificationType;

      if (link.isInDb()) {
         if (link.isDeleted()) {
            Artifact aArtifact = ArtifactCache.getActive(link.getAArtifactId(), link.getABranch());
            Artifact bArtifact = ArtifactCache.getActive(link.getBArtifactId(), link.getBBranch());

            if ((aArtifact != null && aArtifact.isDeleted()) || (bArtifact != null && bArtifact.isDeleted())) {
               modificationType = ModificationType.ARTIFACT_DELETED;
            } else {
               modificationType = ModificationType.DELETED;
            }
         } else {
            modificationType = ModificationType.CHANGE;
         }
      } else {
         if (link.isDeleted()) return;

         Artifact aArtifact = link.getArtifact(RelationSide.SIDE_A);
         if (!aArtifact.isInDb()) {
            aArtifact.persistAttributesAndRelations(this);
         }
         Artifact bArtifact = link.getArtifact(RelationSide.SIDE_B);
         if (!bArtifact.isInDb()) {
            bArtifact.persistAttributesAndRelations(this);
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

      RelationModType relationModType = modificationType.isDeleted() ? RelationModType.Deleted : RelationModType.Added;

      xModifiedEvents.add(new RelationModifiedEvent(new Sender("RelationManager"), relationModType, link,
            link.getBranch(), link.getRelationType().getTypeName()));
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(Connection connection) throws OseeCoreException {
      executeTransactionDataItems(connection);

      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         transactionData.internalClearDirtyState();
         transactionData.internalUpdate(internalGetTransactionId());
      }

      if (xModifiedEvents.size() > 0) {
         OseeEventManager.kickTransactionEvent(this, xModifiedEvents);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxException(java.lang.Exception)
    */
   @Override
   protected void handleTxException(Exception ex) {
      for (BaseTransactionData transactionData : transactionDataItems.values()) {
         try {
            transactionData.internalOnRollBack();
         } catch (OseeCoreException ex1) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex1);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.DbTransaction#handleTxFinally()
    */
   @Override
   protected void handleTxFinally() throws OseeCoreException {
      clear();
   }
}