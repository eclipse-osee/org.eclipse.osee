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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.event.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;

/**
 * @author Robert A. Fisher
 */
public class SkynetTransaction {
   private static final String UPDATE_TXS_NOT_CURRENT =
         "UPDATE osee_txs txs1 SET tx_current = 0 WHERE txs1.transaction_id = ? AND txs1.gamma_id = ?";

   private static final String DELETE_TRANSACTION_DETAIL = "DELETE FROM osee_tx_details WHERE transaction_id =?";
   private static final String INSERT_INTO_TRANSACTION_TABLE =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) VALUES (?, ?, ?, ?)";
   private final Map<String, List<Object[]>> preparedBatch = new HashMap<String, List<Object[]>>();
   private String transactionName;
   private String comment;
   private final TransactionId transactionId;
   private final Branch branch;
   private final List<ArtifactTransactionModifiedEvent> xModifiedEvents =
         new ArrayList<ArtifactTransactionModifiedEvent>();
   private final Map<BaseTransactionData, BaseTransactionData> transactionItems =
         new HashMap<BaseTransactionData, BaseTransactionData>();

   public SkynetTransaction(Branch branch) throws OseeDataStoreException {
      this(branch, SkynetAuthentication.getUser());
   }

   public SkynetTransaction(Branch branch, String comment) throws OseeDataStoreException {
      this(branch, SkynetAuthentication.getUser(), comment);
   }

   public SkynetTransaction(Branch branch, User userToBlame) throws OseeDataStoreException {
      this(branch, userToBlame, "");
   }

   public SkynetTransaction(Branch branch, User userToBlame, String comment) throws OseeDataStoreException {
      this.branch = branch;
      this.comment = comment;
      transactionId = TransactionIdManager.createNextTransactionId(branch, userToBlame, comment);
   }

   public void addToBatch(String sql, Object... data) {
      if (sql == null) throw new IllegalArgumentException("SQL can not be null.");

      List<Object[]> statementData;

      if (preparedBatch.containsKey(sql)) {
         statementData = preparedBatch.get(sql);
      } else {
         statementData = new LinkedList<Object[]>();
         preparedBatch.put(sql, statementData);
      }

      statementData.add(data);
   }

   public synchronized void execute(IProgressMonitor monitor) throws OseeDataStoreException {
      boolean deleteTransactionDetail = false;

      try {
         Connection connection = ConnectionHandler.getConnection();
         boolean insertBatchToTransactions = executeBatchToTransactions(monitor);
         boolean insertTransactionDataItems = executeTransactionDataItems(connection);

         deleteTransactionDetail = !insertBatchToTransactions && !insertTransactionDataItems;

         for (BaseTransactionData transactionData : transactionItems.keySet()) {
            transactionData.internalClearDirtyState();
         }
      } catch (OseeDataStoreException ex) {
         deleteTransactionDetail = true;
         for (BaseTransactionData transactionData : transactionItems.keySet()) {
            try {
               transactionData.internalOnRollBack();
            } catch (OseeCoreException ex1) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex1);
            }
         }
         ConnectionHandler.requestRollback();
         OseeLog.log(SkynetActivator.class, Level.SEVERE,
               "Rollback occured for transaction: " + getTransactionId().getTransactionNumber(), ex);
         throw ex;
      } finally {
         if (deleteTransactionDetail) {
            xModifiedEvents.clear();
            ConnectionHandler.runPreparedUpdate(DELETE_TRANSACTION_DETAIL, transactionId.getTransactionNumber());
         } else {
            for (BaseTransactionData transactionData : transactionItems.keySet()) {
               transactionData.internalUpdate();
            }
         }
      }
   }

   private void fetchTxNotCurrent(Connection connection, BaseTransactionData transactionData, List<Object[]> results) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(connection, transactionData.getSelectTxNotCurrentSql(),
                     transactionData.getSelectData());
         while (chStmt.next()) {
            results.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         ConnectionHandler.close(chStmt);
      }
   }

   public boolean executeTransactionDataItems(Connection connection) throws OseeDataStoreException {
      if (transactionItems.isEmpty()) {
         return false;
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
         ConnectionHandler.runPreparedUpdate(connection, itemInsertSql,
               (List<Object[]>) dataItemInserts.getValues(itemInsertSql));
      }
      // Insert addressing data for changes into the txs table
      ConnectionHandler.runPreparedUpdate(connection, INSERT_INTO_TRANSACTION_TABLE, addressingInsertData);

      // Set stale tx currents in txs table
      ConnectionHandler.runPreparedUpdate(connection, UPDATE_TXS_NOT_CURRENT, txNotCurrentData);
      return true;
   }

   // Supports adding new artifacts to the artifact table and
   // updating attributes that are not versioned.
   public boolean executeBatchToTransactions(IProgressMonitor monitor) throws OseeDataStoreException {
      Collection<String> sqls = preparedBatch.keySet();
      int size = sqls.size();
      int count = 0;
      Iterator<String> iter = sqls.iterator();
      while (iter.hasNext()) {
         monitor.subTask("Processing Prepared SQL set " + (++count) + "/" + size);
         String sql = iter.next();
         ConnectionHandler.runPreparedUpdateBatch(sql, preparedBatch.get(sql));
         monitor.worked(1);
      }
      return preparedBatch.size() > 0;
   }

   public void addArtifactModifiedEvent(Object sourceObject, ArtifactModType artifactModType, Artifact artifact) throws OseeDataStoreException {
      xModifiedEvents.add(new ArtifactModifiedEvent(new Sender(sourceObject), artifactModType, artifact,
            getTransactionNumber(), artifact.getDirtySkynetAttributeChanges()));
   }

   public void addRelationModifiedEvent(Object sourceObject, RelationModType relationModType, RelationLink link, Branch branch, String relationType) {
      xModifiedEvents.add(new RelationModifiedEvent(new Sender(sourceObject), relationModType, link, branch,
            relationType));
   }

   /**
    * Kicks local and remote events
    */
   public void kickEvents() {
      if (xModifiedEvents.size() > 0) {
         OseeEventManager.kickTransactionEvent(this, xModifiedEvents);
         xModifiedEvents.clear();
      }
   }

   /**
    * @return Returns the transactionName.
    */
   public String getTransactionName() {
      return transactionName;
   }

   /**
    * @param transactionName The transactionName to set.
    */
   public void setTransactionName(String transactionName) {
      this.transactionName = transactionName;
   }

   /**
    * @return Returns the transactionId.
    */
   public Integer getTransactionNumber() {
      return transactionId.getTransactionNumber();
   }

   /**
    * @return Returns the comment.
    */
   public String getComment() {
      return comment;
   }

   /**
    * @param comment The comment to set.
    */
   public void setComment(String comment) {
      this.comment = comment;
   }

   /**
    * Returns the amount of work that this Transaction has to do. This value is suitable for using to compute total work
    * for status to an IProgressMonitor.
    */
   protected int getWork() {
      return preparedBatch.keySet().size() + transactionItems.size();
   }

   /**
    * @return Returns the branch.
    */
   public Branch getBranch() {
      return branch;
   }

   /**
    * @return Returns the transactionId.
    */
   public TransactionId getTransactionId() {
      return transactionId;
   }

   public void addTransactionDataItem(BaseTransactionData dataItem) {
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
}
