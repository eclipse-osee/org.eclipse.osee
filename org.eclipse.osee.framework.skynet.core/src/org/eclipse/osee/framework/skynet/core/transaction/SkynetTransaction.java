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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.CHANGE;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETED;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.NEW;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeURL;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.transaction.data.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.data.AttributeTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.data.ITransactionData;
import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Robert A. Fisher
 */
public class SkynetTransaction {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetTransaction.class);
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final String INSERT_INTO_TRANSACTION_DETAIL_TABLE =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE.columnsForInsert("transaction_id", TXD_COMMENT, "time", "author",
               "branch_id", "tx_type");
   private static final String INSERT_INTO_TRANSACTION_TABLE =
         "INSERT INTO osee_define_txs (transaction_id, gamma_id, mod_type, tx_current) VALUES (?, ?, ?, ?)";

   private static final String DELETE_TRANSACTION_DETAIL =
         "DELETE FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id =?";

   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private final Map<String, List<Object[]>> preparedBatch;
   private String transactionName;
   private String comment;
   private TransactionId transactionId;
   private final Branch branch;
   private List<ISkynetEvent> remoteEvents;
   private List<Event> localEvents;
   private Map<ITransactionData, ITransactionData> transactionItems;

   // IMPORTANT: The transactionNumber for this transaction is kept in addition to the TransactionId reference
   //            since the number in the TransactionId may be updated from events, and the number of this
   //            exact transaction must be known over the life of this transaction despite the head transaction
   //            potentially moving on from other transactions occurring in parallel.
   private Integer transactionNumber;
   private Date transactionDate;

   public SkynetTransaction(Branch branch) throws SQLException {
      this(branch, SkynetAuthentication.getUser());
   }

   public SkynetTransaction(Branch branch, String comment) throws SQLException {
      this(branch, SkynetAuthentication.getUser(), comment);
   }

   /**
    * 
    */
   @SuppressWarnings("unchecked")
   public SkynetTransaction(Branch branch, User userToBlame) throws SQLException {
      this(branch, userToBlame, "");
   }

   /**
    * 
    */
   @SuppressWarnings("unchecked")
   public SkynetTransaction(Branch branch, User userToBlame, String comment) throws SQLException {
      super();

      this.branch = branch;
      this.comment = "";
      this.remoteEvents = new LinkedList<ISkynetEvent>();
      this.localEvents = new LinkedList<Event>();
      this.comment = comment;

      Pair<Integer, TransactionId> nextTranData = transactionIdManager.createNextTransactionId(branch);
      this.transactionNumber = nextTranData.getKey();
      this.transactionId = nextTranData.getValue();
      List datas = new LinkedList();

      int blameArtId = -1;
      if (userToBlame == null || !userToBlame.isInDb()) {
         if (!SkynetDbInit.isDbInit()) {
            blameArtId = SkynetAuthentication.getNoOneArtifactId();
         }
      } else {
         blameArtId = userToBlame.getArtId();
      }

      transactionDate = GlobalTime.GreenwichMeanTimestamp();
      datas.add(new Object[] {SQL3DataType.INTEGER, transactionNumber, SQL3DataType.VARCHAR, getComment(),
            SQL3DataType.TIMESTAMP, transactionDate, SQL3DataType.INTEGER, blameArtId, SQL3DataType.INTEGER,
            branch.getBranchId(), SQL3DataType.INTEGER, TransactionDetailsType.NonBaselined.getId()});
      ConnectionHandler.runPreparedUpdateBatch(INSERT_INTO_TRANSACTION_DETAIL_TABLE, datas);

      preparedBatch = new HashMap<String, List<Object[]>>();
      transactionItems = new HashMap<ITransactionData, ITransactionData>();
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
         updateLastModified();
      } catch (SQLException ex) {
         deleteTransactionDetail = true;
         transactionCleanUp();
         ConnectionHandler.requestRollback();
         logger.log(Level.SEVERE, "Rollback occured for transaction: " + getTransactionId().getTransactionNumber(), ex);
         throw ex;
      } finally {
         if (deleteTransactionDetail) {
            localEvents = null;
            remoteEvents = null;
            ConnectionHandler.runPreparedUpdate(DELETE_TRANSACTION_DETAIL, SQL3DataType.INTEGER, transactionNumber);
         } else {
            transactionId.setLastSavedTransactionNumber(transactionNumber);
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
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      }

   }

   public boolean executeTransactionDataItems() throws SQLException {
      boolean insertTransactionDataItems = transactionItems.size() > 0;

      for (ITransactionData transactionData : transactionItems.keySet()) {
         //This must be called before adding the new transaction information, because it
         //will update the current transaction to 0.
         ConnectionHandler.runPreparedUpdate(transactionData.setPreviousTxNotCurrentSql(),
               transactionData.getPreviousTxNotCurrentData().toArray());

         //Add current transaction information
         ModificationType modType = transactionData.getModificationType();

         ConnectionHandler.runPreparedUpdate(INSERT_INTO_TRANSACTION_TABLE, SQL3DataType.INTEGER,
               transactionData.getTransactionId(), SQL3DataType.INTEGER, transactionData.getGammaId(),
               SQL3DataType.INTEGER, modType.getValue(), SQL3DataType.INTEGER, modType.getCurrentValue());

         //Add specific object values to the their tables
         ConnectionHandler.runPreparedUpdate(transactionData.getTransactionChangeSql(),
               transactionData.getTransactionChangeData().toArray());
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

   private void updateLastModified() throws SQLException {
      for (ITransactionData transactionData : transactionItems.keySet()) {
         if (transactionData instanceof ArtifactTransactionData) {
            Artifact artifact = ((ArtifactTransactionData) transactionData).getArtifact();
            artifact.setLastModified(transactionDate);
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

   public void addRemoteEvent(ISkynetEvent event) {
      if (event == null) throw new IllegalArgumentException("Remote event can not be null");

      if (!remoteEvents.contains(event)) {
         remoteEvents.add(event);
      }
   }

   public void addLocalEvent(Event event) {
      if (event == null) throw new IllegalArgumentException("local event can not be null");

      if (!localEvents.contains(event)) localEvents.add(event);
   }

   /**
    * Kicks local and remote events
    */
   public void kickEvents() {
      if (localEvents != null) {
         eventManager.kick(new LocalTransactionEvent(localEvents, this));
         localEvents.clear();
      }

      if (remoteEvents != null) {
         RemoteEventManager.kick(remoteEvents.toArray(ISkynetEvent.EMPTY_ARRAY));
         remoteEvents.clear();
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
      return transactionNumber;
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
