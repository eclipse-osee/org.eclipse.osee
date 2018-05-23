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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTransactionData;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData.InsertDataCollector;
import org.eclipse.osee.framework.skynet.core.utility.AbstractDbTxOperation;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public final class StoreSkynetTransactionOperation extends AbstractDbTxOperation implements InsertDataCollector {

   private static final String UPDATE_TXS_NOT_CURRENT =
      "UPDATE osee_txs SET tx_current = " + TxChange.NOT_CURRENT + " WHERE branch_id = ? AND transaction_id = ? AND gamma_id = ?";

   private final HashCollection<String, Object[]> dataItemInserts = new HashCollection<>();
   private final Map<Integer, String> dataInsertOrder = new HashMap<>();

   private final BranchId branch;
   private final TransactionRecord transactionRecord;
   private final Collection<BaseTransactionData> txDatas;
   private final Collection<Artifact> artifactReferences;

   private boolean executedWithException;

   public StoreSkynetTransactionOperation(String name, BranchId branch, TransactionRecord transactionRecord, Collection<BaseTransactionData> txDatas, Collection<Artifact> artifactReferences) {
      super(ConnectionHandler.getJdbcClient(), name, Activator.PLUGIN_ID);
      this.branch = branch;
      this.transactionRecord = transactionRecord;
      this.txDatas = txDatas;
      this.artifactReferences = artifactReferences;
   }

   @Override
   public TransactionId getTransaction() {
      return transactionRecord;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   @Override
   public void internalAddInsertToBatch(int insertPriority, String insertSql, Object... data) {
      dataItemInserts.put(insertSql, data);
      dataInsertOrder.put(insertPriority, insertSql);
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, JdbcConnection connection) {
      executedWithException = false;
      TransactionManager.internalPersist(connection, transactionRecord);
      if (!txDatas.isEmpty()) {
         executeTransactionDataItems(connection);
         if (BranchManager.getState(branch).isCreated()) {
            BranchManager.setState(branch, BranchState.MODIFIED);
         }
      }
   }

   @Override
   public void handleTxException(IProgressMonitor monitor, Exception ex) {
      executedWithException = true;
      for (BaseTransactionData transactionData : txDatas) {
         try {
            transactionData.internalOnRollBack();
         } catch (OseeCoreException ex1) {
            OseeLog.log(Activator.class, Level.SEVERE, ex1);
         }
      }
   }

   @Override
   public void handleTxFinally(IProgressMonitor monitor) {
      if (!executedWithException) {
         updateModifiedCachedObject();
         tagGammas();
      }
   }

   private void tagGammas() {
      Set<Long> gammasToTag = new LinkedHashSet<>();
      for (BaseTransactionData transactionData : txDatas) {
         if (!transactionData.getModificationType().isExistingVersionUsed() && transactionData instanceof AttributeTransactionData) {
            AttributeTransactionData attrData = (AttributeTransactionData) transactionData;
            if (!attrData.getAttribute().isUseBackingData()) {
               Attribute<?> attr = ((AttributeTransactionData) transactionData).getAttribute();
               if (attr.getAttributeType().isTaggable()) {
                  gammasToTag.add(transactionData.getGammaId().getId());
               }
            }
         }
      }
      if (!gammasToTag.isEmpty()) {
         AttributeTaggingOperation op = new AttributeTaggingOperation(gammasToTag);
         Operations.executeWorkAndCheckStatus(op, new NullProgressMonitor());
      }
   }

   private void executeTransactionDataItems(JdbcConnection connection) {
      List<Object[]> txNotCurrentData = new ArrayList<>();
      for (BaseTransactionData transactionData : txDatas) {
         // Collect inserts for attribute, relation, artifact, and artifact version tables
         transactionData.addInsertToBatch(this);

         // Collect stale tx currents for batch update
         fetchTxNotCurrent(connection, branch, transactionData, txNotCurrentData);
      }

      // Insert into data tables - i.e. attribute, relation and artifact version tables
      List<Integer> keys = new ArrayList<>(dataInsertOrder.keySet());
      Collections.sort(keys);
      for (int priority : keys) {
         String sqlKey = dataInsertOrder.get(priority);
         getJdbcClient().runBatchUpdate(connection, sqlKey, dataItemInserts.getValues(sqlKey));
      }

      // Set stale tx currents in txs table
      getJdbcClient().runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, txNotCurrentData);
   }

   private void fetchTxNotCurrent(JdbcConnection connection, BranchId branchId, BaseTransactionData transactionData, List<Object[]> results) {
      try (JdbcStatement chStmt = getJdbcClient().getStatement(connection)) {
         String query = ServiceUtil.getSql(transactionData.getSelectTxNotCurrentSql());

         chStmt.runPreparedQuery(query, transactionData.getItemId(), branchId);
         while (chStmt.next()) {
            results.add(new Object[] {branchId, chStmt.getLong("transaction_id"), chStmt.getLong("gamma_id")});
         }
      }
   }

   private void updateModifiedCachedObject() {
      ArtifactEvent artifactEvent = new ArtifactEvent(transactionRecord);

      // Update all transaction items before collecting events
      for (BaseTransactionData transactionData : txDatas) {
         transactionData.internalUpdate(transactionRecord);
      }

      // Collect events before clearing any dirty flags
      for (BaseTransactionData transactionData : txDatas) {
         transactionData.internalAddToEvents(artifactEvent);
      }

      // Collect attribute events
      for (Artifact artifact : artifactReferences) {
         if (artifact.hasDirtyAttributes()) {
            artifactEvent.addArtifact(artifact);
         }
      }

      // Clear all dirty flags
      for (BaseTransactionData transactionData : txDatas) {
         transactionData.internalClearDirtyState();
      }

      // Clear all relation order records
      for (Artifact artifact : artifactReferences) {
         artifact.getRelationOrderRecords().clear();
      }

      if (!artifactEvent.getArtifacts().isEmpty() || !artifactEvent.getRelations().isEmpty()) {
         OseeEventManager.kickPersistEvent(this, artifactEvent);
      }
   }

}
