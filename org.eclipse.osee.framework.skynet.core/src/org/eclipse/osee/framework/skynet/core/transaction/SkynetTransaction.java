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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TXD_COMMENT;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.CHANGE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.NEW;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.remoteEvent.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.data.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.data.ITransactionData;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Robert A. Fisher
 */
public class SkynetTransaction {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetTransaction.class);
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private static final RemoteEventManager remoteEventManager = RemoteEventManager.getInstance();
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final ArtifactPersistenceManager artifactPersistenceManager =
         ArtifactPersistenceManager.getInstance();
   private static final String INSERT_INTO_TRANSACTION_DETAIL_TABLE =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE.columnsForInsert("transaction_id", TXD_COMMENT, "time", "author",
               "branch_id");
   private static final String INSERT_INTO_TRANSACTION_TABLE =
         " INSERT INTO " + TRANSACTIONS_TABLE + " (transaction_id, gamma_id, tx_type) VALUES (?, ?, ?)";

   private static final String DELETE_TRANSACTION_DETAIL =
         "DELETE FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id =?";

   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private static final String SELECT_MAX_TRANSACTION =
         "SELECT art_id, " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "transaction_id") + " FROM " + ARTIFACT_VERSION_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_TABLE.join(
               TRANSACTIONS_TABLE, "gamma_id") + " AND " + TRANSACTIONS_TABLE.join(TRANSACTION_DETAIL_TABLE,
               "transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? AND " + ARTIFACT_VERSION_TABLE.column("art_id") + " in (";
   private final List<Object[]> batchToTransactionTable;
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

   public SkynetTransaction(Branch branch) throws SQLException {
      this(branch, skynetAuth.getAuthenticatedUser());
   }

   public SkynetTransaction(Branch branch, String comment) throws SQLException {
      this(branch, skynetAuth.getAuthenticatedUser(), comment);
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
            blameArtId = skynetAuth.getNoOneArtifactId();
         }
      } else {
         blameArtId = userToBlame.getArtId();
      }

      datas.add(new Object[] {SQL3DataType.INTEGER, transactionNumber, SQL3DataType.VARCHAR, getComment(),
            SQL3DataType.TIMESTAMP, GlobalTime.GreenwichMeanTimestamp(), SQL3DataType.INTEGER, blameArtId,
            SQL3DataType.INTEGER, branch.getBranchId()});
      ConnectionHandler.runPreparedUpdate(INSERT_INTO_TRANSACTION_DETAIL_TABLE, datas);

      batchToTransactionTable = new LinkedList<Object[]>();
      preparedBatch = new HashMap<String, List<Object[]>>();
      transactionItems = new HashMap<ITransactionData, ITransactionData>();
   }

   public void addToTransactionTableBatch(int gammaId) {
      if (batchToTransactionTable == null) throw new IllegalArgumentException("batchToTransactionTable can not be null");

      batchToTransactionTable.add(new Object[] {SQL3DataType.INTEGER, transactionNumber, SQL3DataType.BIGINT, gammaId,
            SQL3DataType.BIGINT, 0});
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
    	  //Verify that the stripechecker is working correclty
         performStripeCheckAndSetArtifactsNotDirty();

         boolean insertBatchToTransactions = executeBatchToTransactions(monitor);
         boolean insertTransactionDataItems = executeTransactionDataItems();

         if (!insertBatchToTransactions && !insertTransactionDataItems) {
            deleteTransactionDetail = true;
         }
      } catch (SQLException ex) {
         deleteTransactionDetail = true;
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

   public boolean executeTransactionDataItems() throws SQLException {
      return executeTransactionDataItemsMap(transactionItems);
   }

   private boolean executeTransactionDataItemsMap(Map<ITransactionData, ITransactionData> map) throws SQLException {
      boolean insertTransactionDataItems = map.size() > 0;

      for (ITransactionData transactionData : map.keySet()) {
         ConnectionHandler.runPreparedUpdate(INSERT_INTO_TRANSACTION_TABLE, SQL3DataType.INTEGER,
               transactionData.getTransactionId(), SQL3DataType.INTEGER, transactionData.getGammaId(),
               SQL3DataType.INTEGER, TransactionType.convertModificationTypeToTransactionType(
                     transactionData.getModificationType()).getId());
         ConnectionHandler.runPreparedUpdate(transactionData.getTransactionChangeSql(),
               transactionData.getTransactionChangeData().toArray());
      }
      return insertTransactionDataItems;
   }

   private void performStripeCheckAndSetArtifactsNotDirty() throws SQLException {
      Set<Artifact> allArtifacts = new HashSet<Artifact>();
      Map<Integer, Artifact> potentialConflictedArtifacts = new HashMap<Integer, Artifact>();
      Map<Integer, Integer> artIdToNewGamma = new HashMap<Integer, Integer>();

      for (ITransactionData transactionData : transactionItems.keySet()) {
         if (transactionData instanceof ArtifactTransactionData) {
            Artifact artifact = ((ArtifactTransactionData) transactionData).getArtifact();
            ModificationType modificationType = ((ArtifactTransactionData) transactionData).getModificationType();

            allArtifacts.add(artifact);
            if (!remoteEventManager.isConnected() && modificationType != ModificationType.NEW) {
               potentialConflictedArtifacts.put(artifact.getArtId(), artifact);
            }
            artIdToNewGamma.put(artifact.getArtId(), ((ArtifactTransactionData) transactionData).getGammaId());
         }
      }

      ArrayList<Artifact> tempArts = new ArrayList<Artifact>(potentialConflictedArtifacts.values());
      ConnectionHandlerStatement chStmt = null;
      int local_transaction_id;
      int db_transaction_id;
      int art_id;

      while (!tempArts.isEmpty()) {
         Branch branch = tempArts.get(0).getBranch();
         String artIdList = artifactPersistenceManager.getArtIdList(tempArts);
         chStmt =
               ConnectionHandler.runPreparedQuery(SELECT_MAX_TRANSACTION + artIdList + ") group by art_id",
                     SQL3DataType.INTEGER, branch.getBranchId());
         local_transaction_id = transactionId.getLastSavedTransactionNumber();

         while (chStmt.next()) {
            db_transaction_id = chStmt.getRset().getInt("transaction_id");

            if (local_transaction_id < db_transaction_id) {
               for (Artifact artifact : allArtifacts) {
                  artifact.setInTransaction(false);
               }
               art_id = chStmt.getRset().getInt("art_id");
               logger.log(
                     Level.WARNING,
                     "A collision has occurred.  Artifact " + art_id + " with local transaction_id " + local_transaction_id + " has database transaction_id " + db_transaction_id);
            }
         }
         DbUtil.close(chStmt);
      }

      for (Artifact artifact : allArtifacts) {
         artifact.setInTransaction(false);
         if (!artifact.isDeleted()) {
            artifact.setNotDirty();
            artifact.getPersistenceMemo().setGammaId(artIdToNewGamma.get(artifact.getArtId()));
         }
      }
   }

   // Supports adding configuration information, adding new artifacts to the artifact table and
   // updating attributes that are not versioned.
   public boolean executeBatchToTransactions(IProgressMonitor monitor) throws SQLException {
      boolean insertIntoTxsTable = batchToTransactionTable.size() > 0;
      boolean insertChangeInformation = preparedBatch.size() > 0;

      if (insertIntoTxsTable) {
         ConnectionHandler.runPreparedUpdate(INSERT_INTO_TRANSACTION_TABLE, batchToTransactionTable);
      }

      Collection<String> sqls = preparedBatch.keySet();
      int size = sqls.size();
      int count = 0;
      Iterator<String> iter = sqls.iterator();
      while (iter.hasNext()) {
         monitor.subTask("Processing Prepared SQL set " + (++count) + "/" + size);
         String sql = iter.next();
         ConnectionHandler.runPreparedUpdate(sql, preparedBatch.get(sql));
         monitor.worked(1);
      }
      return insertIntoTxsTable |= insertChangeInformation;
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
         remoteEventManager.kick(remoteEvents.toArray(ISkynetEvent.EMPTY_ARRAY));
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
         } else if (!(oldDataItem.getModificationType() == NEW && dataItem.getModificationType() == DELETE)) {
            transactionItems.put(dataItem, dataItem);
         }
      } else {
         transactionItems.put(dataItem, dataItem);
      }
   }
}
