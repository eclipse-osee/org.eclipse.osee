/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.accessor.UpdatePreviousTxCurrent;
import org.eclipse.osee.orcs.db.internal.loader.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.util.IdUtil;

/**
 * the behavior of this class - it needs to: have a branch
 * 
 * @author David Miller
 */
public final class BranchCopyTxCallable extends AbstractDatastoreTxCallable<Branch> {

   private final BranchCache branchCache;
   private final TransactionCache txCache;
   private final BranchFactory branchFactory;
   private final TransactionRecordFactory txFactory;
   private final CreateBranchData branchData;
   private Branch internalBranch;

   private static final String INSERT_TX_DETAILS =
      "INSERT INTO osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type) VALUES (?,?,?,?,?,?)";

   private static final String INSERT_ADDRESSING =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id) VALUES (?,?,?,?,?)";

   private static final String SELECT_ADDRESSING =
      "SELECT gamma_id, mod_type FROM osee_txs txs WHERE txs.branch_id = ? AND txs.transaction_id = ?";

   public BranchCopyTxCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, BranchCache branchCache, TransactionCache txCache, BranchFactory branchFactory, TransactionRecordFactory txFactory, CreateBranchData branchData) {
      super(logger, session, databaseService, String.format("Create Branch %s", branchData.getName()));
      this.branchCache = branchCache;
      this.txCache = txCache;
      this.branchFactory = branchFactory;
      this.txFactory = txFactory;
      this.branchData = branchData;
      //this.systemUserId = -1;
   }

   private TransactionCache getTxCache() {
      return txCache;
   }

   private BranchCache getBranchCache() {
      return branchCache;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Branch handleTxWork(OseeConnection connection) throws OseeCoreException {
      // get the previous transaction, if there is one
      // TODO figure out what happens when there isn't one
      int sourceTx = IdUtil.getSourceTxId(branchData, txCache);
      TransactionRecord savedTx = txCache.getOrLoad(sourceTx);

      TransactionRecord priorTx = txCache.getPriorTransaction(savedTx);
      // copy the branch up to the prior transaction - the goal is to have the provided
      // transaction available on the new branch for merging or comparison purposes
      // first set aside the transaction

      branchData.setFromTransaction(priorTx);

      Callable<Branch> callable =
         new CreateBranchDatabaseTxCallable(getLogger(), getSession(), getDatabaseService(), getBranchCache(),
            getTxCache(), branchFactory, txFactory, branchData);

      try {
         internalBranch = callable.call();
         // TODO figure out if this call is "stackable", is the data passed in above
         // still valid after the branch creation, or do I need to get it all from the new branch???

         String guid = branchData.getGuid();
         if (!GUID.isValid(guid)) {
            guid = GUID.create();
         }

         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         int nextTransactionId = getDatabaseService().getSequence().getNextTransactionId();

         String creationComment = branchData.getCreationComment() + " and copied transaction " + savedTx.getId();

         getDatabaseService().runPreparedUpdate(connection, INSERT_TX_DETAILS, internalBranch.getId(),
            nextTransactionId, creationComment, timestamp, branchData.getUserArtifactId(),
            TransactionDetailsType.NonBaselined.getId());

         TransactionRecord record =
            txFactory.create(nextTransactionId, internalBranch.getId(), creationComment, timestamp,
               branchData.getUserArtifactId(), RelationalConstants.ART_ID_SENTINEL, TransactionDetailsType.Baselined,
               branchCache);

         txCache.cache(record);

         populateTransaction(0.30, connection, record.getId(), internalBranch, savedTx);

         UpdatePreviousTxCurrent updater =
            new UpdatePreviousTxCurrent(getDatabaseService(), connection, internalBranch.getId());
         updater.updateTxNotCurrentsFromTx(record.getId());

      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return internalBranch;
   }

   private void populateTransaction(double workAmount, OseeConnection connection, int intoTx, Branch branch, TransactionRecord copyTx) throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      HashSet<Integer> gammas = new HashSet<Integer>(100000);
      int parentBranchId = RelationalConstants.BRANCH_SENTINEL;
      if (branch.hasParentBranch()) {
         parentBranchId = branch.getParentBranch().getId();
      }
      int copyTxId = copyTx.getId();

      populateAddressingToCopy(connection, data, intoTx, gammas, SELECT_ADDRESSING, parentBranchId, copyTxId);

      if (!data.isEmpty()) {
         getDatabaseService().runBatchUpdate(connection, INSERT_ADDRESSING, data);
      }

      checkForCancelled();
   }

   private void populateAddressingToCopy(OseeConnection connection, List<Object[]> data, int baseTxId, HashSet<Integer> gammas, String query, Object... parameters) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(10000, query, parameters);
         while (chStmt.next()) {
            checkForCancelled();
            Integer gamma = chStmt.getInt("gamma_id");
            if (!gammas.contains(gamma)) {
               ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
               TxChange txCurrent = TxChange.getCurrent(modType);
               data.add(new Object[] {baseTxId, gamma, modType.getValue(), txCurrent.getValue(), internalBranch.getId()});
               gammas.add(gamma);
            }
         }
      } finally {
         chStmt.close();
      }
   }
}
