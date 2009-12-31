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
package org.eclipse.osee.framework.branch.management.creation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.BranchCreationRequest;
import org.eclipse.osee.framework.core.data.BranchCreationResponse;
import org.eclipse.osee.framework.core.data.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchOperation extends AbstractDbTxOperation {

   private static final String INSERT_TX_DETAILS =
         "INSERT INTO osee_tx_details ( branch_id, transaction_id, OSEE_COMMENT, time, author, tx_type ) VALUES ( ?, ?, ?, ?, ?, ?)";

   // descending order is used so that the most recent entry will be used if there are multiple rows with the same gamma (an error case)
   private static final String SELECT_ADDRESSING =
         "SELECT gamma_id, mod_type FROM osee_txs txs WHERE txs.tx_current <> ? AND txs.branch_id = ? order by txs.transaction_id desc";
   private static final String INSERT_ADDRESSING =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id) VALUES (?,?,?,?,?)";
   private static final String USER_ID_QUERY =
         "select oa.art_id from osee_attribute_type oat, osee_attribute oa, osee_txs txs where oat.name = 'User Id' and oat.attr_type_id = oa.attr_type_id and oa.gamma_id = txs.gamma_id and txs.tx_current = 1 and oa.value = ?";

   private static final String MERGE_BRANCH_INSERT =
         "INSERT INTO osee_merge (source_branch_id, dest_branch_id, merge_branch_id, commit_transaction_id) VALUES(?,?,?,?)";

   private final static String SELECT_ATTRIBUTE_ADDRESSING_FROM_JOIN =
         "SELECT item.gamma_id, txs.mod_type FROM osee_attribute item, osee_txs txs, osee_join_artifact artjoin WHERE txs.branch_id = ? AND txs.tx_current <> ? AND txs.gamma_id = item.gamma_id AND item.art_id = artjoin.art_id and artjoin.query_id = ? order by txs.transaction_id desc";
   private final static String SELECT_ARTIFACT_ADDRESSING_FROM_JOIN =
         "SELECT item.gamma_id, txs.mod_type FROM osee_artifact_version item, osee_txs txs, osee_join_artifact artjoin WHERE txs.branch_id = ? AND txs.tx_current <> ? AND txs.gamma_id = item.gamma_id AND item.art_id = artjoin.art_id and artjoin.query_id = ? order by txs.transaction_id desc";

   private boolean passedPreConditions;
   private boolean wasSuccessful;
   private int systemUserId;

   private final IOseeCachingServiceProvider cachingService;
   private final IOseeModelFactoryServiceProvider factoryService;
   private final BranchCreationRequest request;
   private final BranchCreationResponse response;
   private Branch branch;

   public CreateBranchOperation(IOseeDatabaseServiceProvider provider, IOseeModelFactoryServiceProvider factoryService, IOseeCachingServiceProvider cachingService, BranchCreationRequest request, BranchCreationResponse response) {
      super(provider,
            String.format("Create Branch: [%s from %s]", request.getBranchName(), request.getParentBranchId()),
            Activator.PLUGIN_ID);
      this.cachingService = cachingService;
      this.factoryService = factoryService;
      this.request = request;
      this.response = response;
      this.wasSuccessful = false;
      this.systemUserId = -1;
   }

   private int getSystemUserId() {
      if (systemUserId == -1) {
         try {
            systemUserId =
                  getDatabaseService().runPreparedQueryFetchObject(-1, USER_ID_QUERY, SystemUser.OseeSystem.getUserID());
         } catch (OseeDataStoreException ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Unable to retrieve the system user");
         }
      }
      return systemUserId;
   }

   public void checkPreconditions(IProgressMonitor monitor) throws OseeCoreException {
      if (!request.getBranchType().isMergeBranch() && !request.getBranchType().isSystemRootBranch()) {
         int associatedArtifactId = request.getAssociatedArtifactId();
         int systemUserId = getSystemUserId();
         if (associatedArtifactId > -1 && associatedArtifactId != systemUserId) {
            int count =
                  getDatabaseService().runPreparedQueryFetchObject(0,
                        "select (1) from osee_branch where associated_art_id=? and branch_state <> ?",
                        request.getAssociatedArtifactId(), BranchState.DELETED.getValue());
            if (count > 0) {
               throw new OseeStateException(String.format("Existing branch creation detected for [%s]",
                     request.getBranchName()));
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      passedPreConditions = false;
      checkPreconditions(monitor);
      passedPreConditions = true;

      BranchCache branchCache = cachingService.getOseeCachingService().getBranchCache();
      TransactionCache txCache = cachingService.getOseeCachingService().getTransactionCache();

      String guid = request.getBranchGuid();
      if (!GUID.isValid(guid)) {
         guid = GUID.create();
      }
      branch =
            factoryService.getOseeFactoryService().getBranchFactory().create(guid, request.getBranchName(),
                  request.getBranchType(), BranchState.CREATION_IN_PROGRESS, false);

      branch.setParentBranch(branchCache.getById(request.getParentBranchId()));
      branch.setAssociatedArtifact(new DefaultBasicArtifact(request.getAssociatedArtifactId(), "", ""));

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      int nextTransactionId = getDatabaseService().getSequence().getNextTransactionId();

      if (branch.getBranchType().isSystemRootBranch()) {
         TransactionRecord systemTx =
               factoryService.getOseeFactoryService().getTransactionFactory().create(nextTransactionId, branch.getId(),
                     request.getCreationComment(), timestamp, request.getAuthorId(), -1,
                     TransactionDetailsType.Baselined);
         systemTx.setBranchCache(branchCache);
         branch.setSourceTransaction(systemTx);
      } else {
         branch.setSourceTransaction(txCache.getOrLoad(request.getSourceTransactionId()));
      }

      if (branch.getBranchType().isMergeBranch()) {
         ((MergeBranch) branch).setSourceBranch(branchCache.getById(request.getParentBranchId()));
         ((MergeBranch) branch).setDestinationBranch(branchCache.getById(request.getDestinationBranchId()));
      }

      branchCache.cache(branch);
      branchCache.storeItems(branch);

      getDatabaseService().runPreparedUpdate(connection, INSERT_TX_DETAILS, branch.getId(), nextTransactionId,
            request.getCreationComment(), timestamp, request.getAuthorId(), TransactionDetailsType.Baselined.getId());

      TransactionRecord record =
            factoryService.getOseeFactoryService().getTransactionFactory().create(nextTransactionId, branch.getId(),
                  request.getCreationComment(), timestamp, request.getAuthorId(), -1, TransactionDetailsType.Baselined);

      record.setBranchCache(branchCache);
      if (branch.getBranchType().isSystemRootBranch()) {
         branch.setSourceTransaction(record);
      }
      branch.setBaseTransaction(record);

      txCache.cache(record);

      populateBaseTransaction(monitor, 0.30, connection, branch, request.getPopulateBaseTxFromAddressingQueryId());

      addMergeBranchEntry(monitor, 0.20, connection, branch, request.getDestinationBranchId());
      wasSuccessful = true;
   }

   @Override
   protected void handleTxException(IProgressMonitor monitor, Exception ex) {
      if (passedPreConditions) {
         try {
            BranchCache branchCache = cachingService.getOseeCachingService().getBranchCache();
            branch.setModificationType(ModificationType.DELETED);
            branchCache.storeItems(branch);
            branchCache.decache(branch);
         } catch (OseeCoreException ex1) {
            OseeLog.log(Activator.class, Level.SEVERE, ex1);
         }
      }
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      if (wasSuccessful) {
         BranchCache branchCache = cachingService.getOseeCachingService().getBranchCache();
         branch.setBranchState(BranchState.CREATED);
         branchCache.storeItems(branch);
         response.setBranchId(branch.getId());
      }
      monitor.worked(calculateWork(0.10));
   }

   private void addMergeBranchEntry(IProgressMonitor monitor, double workAmount, OseeConnection connection, Branch branch, int destinationBranchId) throws OseeCoreException {
      if (branch.getBranchType().isMergeBranch()) {
         int parentBranchId = branch.hasParentBranch() ? branch.getParentBranch().getId() : -1;
         getDatabaseService().runPreparedUpdate(connection, MERGE_BRANCH_INSERT, parentBranchId,
               request.getDestinationBranchId(), branch.getId(), -1);
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(workAmount));
   }

   private void populateBaseTransaction(IProgressMonitor monitor, double workAmount, OseeConnection connection, Branch branch, int populateBaseTxFromAddressingQueryId) throws OseeCoreException {
      if (branch.getBranchType() != BranchType.SYSTEM_ROOT) {
         List<Object[]> data = new ArrayList<Object[]>();
         HashSet<Integer> gammas = new HashSet<Integer>(100000);
         int parentBranchId = -1;
         if (branch.hasParentBranch()) {
            parentBranchId = branch.getParentBranch().getId();
         }
         int baseTxId = branch.getBaseTransaction().getId();
         String extraMessage = "";
         if (populateBaseTxFromAddressingQueryId > 0) {
            populateAddressingToCopy(monitor, connection, data, baseTxId, gammas,
                  SELECT_ATTRIBUTE_ADDRESSING_FROM_JOIN, parentBranchId, TxChange.NOT_CURRENT.getValue(),
                  populateBaseTxFromAddressingQueryId);
            populateAddressingToCopy(monitor, connection, data, baseTxId, gammas, SELECT_ARTIFACT_ADDRESSING_FROM_JOIN,
                  parentBranchId, TxChange.NOT_CURRENT.getValue(), populateBaseTxFromAddressingQueryId);

            extraMessage = " by joining against query id";
         } else {
            populateAddressingToCopy(monitor, connection, data, baseTxId, gammas, SELECT_ADDRESSING,
                  TxChange.NOT_CURRENT.getValue(), parentBranchId);
         }
         if (!data.isEmpty()) {
            getDatabaseService().runBatchUpdate(connection, INSERT_ADDRESSING, data);
         }
         monitor.setTaskName(String.format("Created branch [%s] with [%d] transactions%s", branch.getName(),
               data.size(), extraMessage));
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(workAmount));
   }

   private void populateAddressingToCopy(IProgressMonitor monitor, OseeConnection connection, List<Object[]> data, int baseTxId, HashSet<Integer> gammas, String query, Object... parameters) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(10000, query, parameters);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            Integer gamma = chStmt.getInt("gamma_id");
            if (!gammas.contains(gamma)) {
               ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
               TxChange txCurrent = TxChange.getCurrent(modType);
               data.add(new Object[] {baseTxId, gamma, modType.getValue(), txCurrent.getValue(), branch.getId()});
               gammas.add(gamma);
            }
         }
      } finally {
         chStmt.close();
      }
   }
}
