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

import static org.eclipse.osee.framework.skynet.core.change.ModificationType.CHANGE;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETED;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.NEW;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.event.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;

/**
 * @author Robert A. Fisher
 */
public final class SkynetTransaction extends DbTransaction {
   private static final String UPDATE_TXS_NOT_CURRENT =
         "UPDATE osee_txs txs1 SET tx_current = 0 WHERE txs1.transaction_id = ? AND txs1.gamma_id = ?";
   private static final String INSERT_ARTIFACT =
         "INSERT INTO osee_artifact (art_id, art_type_id, guid, human_readable_id) VALUES (?, ?, ?, ?)";
   private static final String INSERT_INTO_TRANSACTION_TABLE =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) VALUES (?, ?, ?, ?)";
   private final Map<String, List<Object[]>> preparedBatch = new HashMap<String, List<Object[]>>();
   private TransactionId transactionId;
   private final List<ArtifactTransactionModifiedEvent> xModifiedEvents =
         new ArrayList<ArtifactTransactionModifiedEvent>();
   private final Map<BaseTransactionData, BaseTransactionData> transactionItems =
         new HashMap<BaseTransactionData, BaseTransactionData>();
   private final Branch branch;
   private boolean madeChanges = false;

   /**
    * @return the branch
    */
   public Branch getBranch() {
      return branch;
   }

   public SkynetTransaction(Branch branch) throws OseeCoreException {
      this.branch = branch;
   }

   public void addArtifactToPersist(Artifact artifact) throws OseeCoreException {
      madeChanges = true;
      ModificationType modType;
      ArtifactModType artifactModType;

      if (artifact.isInDb()) {
         if (artifact.isDeleted()) {
            modType = ModificationType.DELETED;
            artifactModType = ArtifactModType.Deleted;
         } else {
            modType = ModificationType.CHANGE;
            artifactModType = ArtifactModType.Changed;
         }
      } else {
         modType = ModificationType.NEW;
         artifactModType = ArtifactModType.Added;
         addToBatch(INSERT_ARTIFACT, artifact.getArtId(), artifact.getArtTypeId(), artifact.getGuid(),
               artifact.getHumanReadableId());
      }

      int artGamma = SequenceManager.getNextGammaId();
      artifact.setGammaId(artGamma);
      processTransactionForArtifact(artifact, modType, artGamma);

      // Add Attributes to Transaction
      AttributeToTransactionOperation operation = new AttributeToTransactionOperation(artifact, this);
      operation.execute();

      // Kick Local Event
      addArtifactModifiedEvent("persistArtifact()", artifactModType, artifact);
   }

   private void processTransactionForArtifact(Artifact artifact, ModificationType modType, int artGamma) throws OseeDataStoreException {
      addTransactionDataItem(new ArtifactTransactionData(artifact, artGamma, getTransactionId(), modType));
   }

   private void addToBatch(String sql, Object... data) throws OseeArgumentException {
      if (sql == null) throw new OseeArgumentException("SQL can not be null.");

      List<Object[]> statementData;

      if (preparedBatch.containsKey(sql)) {
         statementData = preparedBatch.get(sql);
      } else {
         statementData = new LinkedList<Object[]>();
         preparedBatch.put(sql, statementData);
      }

      statementData.add(data);
   }

   private void fetchTxNotCurrent(Connection connection, BaseTransactionData transactionData, List<Object[]> results) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      try {
         chStmt.runPreparedQuery(transactionData.getSelectTxNotCurrentSql(), transactionData.getSelectData());
         while (chStmt.next()) {
            results.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }
   }

   private void executeTransactionDataItems(Connection connection) throws OseeDataStoreException {
      if (transactionItems.isEmpty()) {
         return;
      }

      List<Object[]> txNotCurrentData = new ArrayList<Object[]>();
      List<Object[]> addressingInsertData = new ArrayList<Object[]>();
      HashCollection<String, Object[]> dataItemInserts = new HashCollection<String, Object[]>();
      for (BaseTransactionData transactionData : transactionItems.keySet()) {
         // Collect stale tx currents for batch update
         fetchTxNotCurrent(connection, transactionData, txNotCurrentData);

         // Collect addressing data for batch update into the txs table
         ModificationType modType = transactionData.getModificationType();
         addressingInsertData.add(new Object[] {transactionData.getTransactionId().getTransactionNumber(),
               transactionData.getGammaId(), modType.getValue(), TxChange.getCurrent(modType).getValue()});

         // Collect specific object values for their tables i.e. attribute, relation and artifact version tables
         if (transactionData.getModificationType() != ModificationType.ARTIFACT_DELETED) {
            dataItemInserts.put(transactionData.getInsertSql(), transactionData.getInsertData());
         }
      }

      // Insert into data tables - i.e. attribute, relation and artifact version tables
      for (String itemInsertSql : dataItemInserts.keySet()) {
         ConnectionHandler.runBatchUpdate(connection, itemInsertSql,
               (List<Object[]>) dataItemInserts.getValues(itemInsertSql));
      }
      // Insert addressing data for changes into the txs table
      ConnectionHandler.runBatchUpdate(connection, INSERT_INTO_TRANSACTION_TABLE, addressingInsertData);

      // Set stale tx currents in txs table
      ConnectionHandler.runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, txNotCurrentData);
   }

   // Supports adding new artifacts to the artifact table
   private void executeBatchToTransactions(Connection connection) throws OseeDataStoreException {
      Collection<String> sqls = preparedBatch.keySet();
      Iterator<String> iter = sqls.iterator();
      while (iter.hasNext()) {
         String sql = iter.next();
         ConnectionHandler.runBatchUpdate(connection, sql, preparedBatch.get(sql));
      }
   }

   public void addArtifactModifiedEvent(Object sourceObject, ArtifactModType artifactModType, Artifact artifact) throws OseeDataStoreException, OseeAuthenticationRequiredException {
      madeChanges = true;
      xModifiedEvents.add(new ArtifactModifiedEvent(new Sender(sourceObject), artifactModType, artifact,
            getTransactionId().getTransactionNumber(), artifact.getDirtySkynetAttributeChanges()));
   }

   public void addRelationModifiedEvent(Object sourceObject, RelationModType relationModType, RelationLink link, Branch branch, String relationType) throws OseeAuthenticationRequiredException {
      madeChanges = true;
      xModifiedEvents.add(new RelationModifiedEvent(new Sender(sourceObject), relationModType, link, branch,
            relationType));
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
   public TransactionId getTransactionId() throws OseeDataStoreException {
      if (transactionId == null) {
         transactionId = TransactionIdManager.createNextTransactionId(branch, UserManager.getUser(), "");
      }
      return transactionId;
   }

   public void addTransactionDataItem(BaseTransactionData dataItem) {
      madeChanges = true;
      BaseTransactionData oldDataItem = transactionItems.remove(dataItem);

      if (oldDataItem != null) {
         if (oldDataItem.getModificationType() == NEW && dataItem.getModificationType() == CHANGE) {
            dataItem.setModificationType(NEW);
            transactionItems.put(dataItem, dataItem);
         } else if (!(oldDataItem.getModificationType() == NEW && dataItem.getModificationType() == DELETED)) {
            transactionItems.put(dataItem, dataItem);
         }
      } else {
         transactionItems.put(dataItem, dataItem);
      }
   }

   public void deleteArtifact(Artifact artifact, boolean reorderRelations) throws OseeCoreException {
      if (!artifact.isInDb()) return;
      madeChanges = true;
      processTransactionForArtifact(artifact, ModificationType.DELETED, SequenceManager.getNextGammaId());

      // Kick Local Event
      addArtifactModifiedEvent(this, ArtifactModType.Deleted, artifact);

      RelationManager.deleteRelationsAll(artifact, reorderRelations);
      artifact.deleteAttributes();

      artifact.persistAttributesAndRelations();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(Connection connection) throws OseeCoreException {
      executeBatchToTransactions(connection);
      executeTransactionDataItems(connection);

      for (BaseTransactionData transactionData : transactionItems.keySet()) {
         transactionData.internalClearDirtyState();
         transactionData.internalUpdate();
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
      xModifiedEvents.clear();
      for (BaseTransactionData transactionData : transactionItems.keySet()) {
         try {
            transactionData.internalOnRollBack();
         } catch (OseeCoreException ex1) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex1);
         }
      }
   }
}