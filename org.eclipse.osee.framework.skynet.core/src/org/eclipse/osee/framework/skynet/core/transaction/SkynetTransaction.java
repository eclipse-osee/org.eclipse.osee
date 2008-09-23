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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeURL;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.event.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;

/**
 * @author Robert A. Fisher
 */
public class SkynetTransaction {
   private static final String UPDATE_TXS_NOT_CURRENT =
         "UPDATE osee_define_txs txs1 SET tx_current = 0 WHERE EXISTS (SELECT 1 FROM osee_join_transaction jt1 WHERE jt1.query_id = ? AND txs1.transaction_id = jt1.transaction_id AND txs1.gamma_id = jt1.gamma_id)";
   private static final String DELETE_TRANSACTION_DETAIL = "DELETE FROM osee_define_tx_details WHERE transaction_id =?";
   private static final String INSERT_INTO_TRANSACTION_TABLE =
         "INSERT INTO osee_define_txs (transaction_id, gamma_id, mod_type, tx_current) VALUES (?, ?, ?, ?)";
   private final Map<String, List<Object[]>> preparedBatch = new HashMap<String, List<Object[]>>();
   private String transactionName;
   private String comment;
   private final TransactionId transactionId;
   private final Branch branch;
   private final List<ArtifactTransactionModifiedEvent> xModifiedEvents =
         new ArrayList<ArtifactTransactionModifiedEvent>();
   private final Map<ITransactionData, ITransactionData> transactionItems =
         new HashMap<ITransactionData, ITransactionData>();

   public SkynetTransaction(Branch branch) throws SQLException {
      this(branch, SkynetAuthentication.getUser());
   }

   public SkynetTransaction(Branch branch, String comment) throws SQLException {
      this(branch, SkynetAuthentication.getUser(), comment);
   }

   public SkynetTransaction(Branch branch, User userToBlame) throws SQLException {
      this(branch, userToBlame, "");
   }

   public SkynetTransaction(Branch branch, User userToBlame, String comment) throws SQLException {
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

   public void execute() throws SQLException {
      execute(new NullProgressMonitor());
   }

   public synchronized void execute(IProgressMonitor monitor) throws SQLException {
      boolean deleteTransactionDetail = false;

      try {
         boolean insertBatchToTransactions = executeBatchToTransactions(monitor);
         boolean insertTransactionDataItems = executeTransactionDataItems();

         if (!insertBatchToTransactions && !insertTransactionDataItems) {
            deleteTransactionDetail = true;
         }

         setArtifactsNotDirty();
      } catch (SQLException ex) {
         deleteTransactionDetail = true;
         transactionCleanUp();
         ConnectionHandler.requestRollback();
         OseeLog.log(SkynetActivator.class, Level.SEVERE,
               "Rollback occured for transaction: " + getTransactionId().getTransactionNumber(), ex);
         throw ex;
      } finally {
         if (deleteTransactionDetail) {
            xModifiedEvents.clear();
            ConnectionHandler.runPreparedUpdate(DELETE_TRANSACTION_DETAIL, transactionId.getTransactionNumber());
         } else {
            for (ITransactionData transactionData : transactionItems.keySet()) {
               if (transactionData instanceof ArtifactTransactionData) {
                  ((ArtifactTransactionData) transactionData).updateArtifact();
               }
            }
         }
      }
   }

   private void transactionCleanUp() {
      for (ITransactionData transactionData : transactionItems.keySet()) {
         if (transactionData instanceof AttributeTransactionData) {
            String uri = ((AttributeTransactionData) transactionData).getUri();
            if (Strings.isValid(uri)) {
               try {
                  HttpProcessor.delete(AttributeURL.getDeleteURL(uri));
               } catch (Exception ex) {
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
               }
            }
         }
      }

   }

   public boolean executeTransactionDataItems() throws SQLException {
      boolean insertTransactionDataItems = transactionItems.size() > 0;

      TransactionJoinQuery transactionJoin = JoinUtility.createTransactionJoinQuery();
      Timestamp insertTime = transactionJoin.getInsertTime();
      int queryId = transactionJoin.getQueryId();
      try {
         for (ITransactionData transactionData : transactionItems.keySet()) {
            //This must be called before adding the new transaction information, because it
            //will update the current transaction to 0.
            transactionData.setPreviousTxNotCurrent(insertTime, queryId);

            //Add current transaction information
            ModificationType modType = transactionData.getModificationType();

            ConnectionHandler.runPreparedUpdate(INSERT_INTO_TRANSACTION_TABLE,
                  transactionData.getTransactionId().getTransactionNumber(), transactionData.getGammaId(),
                  modType.getValue(), TxChange.getCurrent(modType).getValue());

            if (transactionData.getModificationType() != ModificationType.ARTIFACT_DELETED) {
               //Add specific object values to the their tables
               transactionData.insertTransactionChange();
            }
         }

         ConnectionHandler.runPreparedUpdate(UPDATE_TXS_NOT_CURRENT, queryId);
      } finally {
         transactionJoin.delete();
      }
      return insertTransactionDataItems;
   }

   private void setArtifactsNotDirty() throws SQLException {
      for (ITransactionData transactionData : transactionItems.keySet()) {
         if (transactionData instanceof ArtifactTransactionData) {
            Artifact artifact = ((ArtifactTransactionData) transactionData).getArtifact();
            artifact.setNotDirty();
         }
      }
   }

   // Supports adding new artifacts to the artifact table and
   // updating attributes that are not versioned.
   public boolean executeBatchToTransactions(IProgressMonitor monitor) throws SQLException {
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

   public void addArtifactModifiedEvent(Object sourceObject, ArtifactModType artifactModType, Artifact artifact) throws OseeCoreException {
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

   public void addTransactionDataItem(ITransactionData dataItem) {
      ITransactionData oldDataItem = transactionItems.remove(dataItem);

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
