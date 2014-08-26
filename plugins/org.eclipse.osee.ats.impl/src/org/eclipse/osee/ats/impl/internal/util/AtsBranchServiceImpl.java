/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchServiceImpl extends AbstractAtsBranchService {

   private final String SELECT_MERGE_BRANCH =
      "select branch_id from osee_merge where source_branch_id = ? and dest_branch_id = ?";
   private final String SELECT_WORKING_BRANCH =
      "select branch_id from osee_branch where associated_art_id = %d and branch_type = 0 and archived = 0 %s";
   private final String SELECT_COMMITTED_WORKING_BRANCH =
      "select branch_id from osee_branch where associated_art_id = %d and branch_type = 0 and archived = 0 and branch_state = 2";
   private final String SELECT_BRANCH_STATE = "select branch_state from osee_branch where branch_id = ?";
   private final String SELECT_BRANCH_PARENT_ID = "select parent_branch_id from osee_branch where branch_id = ?";
   private final String SELECT_BRANCH_BASE_TRANSACTION_ID =
      "select base_transaction_id from osee_branch where branch_id = ?";
   private final String SELECT_BRANCH_TYPE = "select branch_type from osee_branch where branch_id = ?";
   private final String SELECT_BRANCH_ARCHIVE_STATE = "select archived from osee_branch where branch_id = ?";
   private final String SELECT_COMMIT_TRANSACTIONS =
      "SELECT transaction_id FROM osee_tx_details WHERE commit_art_id = ?";
   private final String SELECT_TRANSACTION = "SELECT * FROM osee_tx_details WHERE transaction_id = ?";
   private final IOseeDatabaseService dbService;
   private final OrcsApi orcsApi;
   private static final HashMap<Integer, List<ITransaction>> commitArtifactIdMap =
      new HashMap<Integer, List<ITransaction>>();

   public AtsBranchServiceImpl(IAtsServices atsServices, OrcsApi orcsApi, IOseeDatabaseService dbService) {
      super(atsServices);
      this.orcsApi = orcsApi;
      this.dbService = dbService;
   }

   @Override
   public IOseeBranch getCommittedWorkingBranch(IAtsTeamWorkflow teamWf) {
      Long longId =
         dbService.runPreparedQueryFetchObject(0L,
            String.format(SELECT_COMMITTED_WORKING_BRANCH, ((ArtifactReadable) teamWf.getStoreObject()).getLocalId()));
      if (longId == null) {
         return null;
      }
      return orcsApi.getQueryFactory(null).branchQuery().andUuids(longId).getResults().getExactlyOne();
   }

   @Override
   public IOseeBranch getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates) {
      String negatedStr = "";
      if (negatedBranchStates.length > 0) {
         negatedStr = "and branch_state not in (";
         for (BranchState state : negatedBranchStates) {
            negatedStr += state.getValue() + ",";
         }
         negatedStr = negatedStr.replaceFirst(",$", ")");
      }
      Long longId =
         dbService.runPreparedQueryFetchObject(0L,
            String.format(SELECT_WORKING_BRANCH, ((ArtifactReadable) teamWf.getStoreObject()).getLocalId(), negatedStr));
      if (longId == null) {
         return null;
      }
      return orcsApi.getQueryFactory(null).branchQuery().andUuids(longId).getResults().getExactlyOne();
   }

   @Override
   public BranchType getBranchType(IOseeBranch branch) {
      String branchType = dbService.runPreparedQueryFetchObject("", SELECT_BRANCH_TYPE, branch.getUuid());
      if (!Strings.isValid(branchType)) {
         return null;
      }
      return BranchType.valueOf(branchType);
   }

   @Override
   public BranchState getBranchState(IOseeBranch branch) {
      String branchState = dbService.runPreparedQueryFetchObject("", SELECT_BRANCH_STATE, branch.getUuid());
      if (!Strings.isValid(branchState)) {
         return null;
      }
      return BranchState.valueOf(branchState);
   }

   /**
    * Return true if merge branch exists in DB (whether archived or not)
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, IOseeBranch destinationBranch) throws OseeCoreException {
      return isMergeBranchExists(teamWf, getWorkingBranch(teamWf), destinationBranch);
   }

   /**
    * Method available for optimized checking of merge branches so don't have to re-acquire working branch if already
    * have
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, IOseeBranch workingBranch, IOseeBranch destinationBranch) throws OseeCoreException {
      if (workingBranch == null) {
         return false;
      }
      Long longId =
         dbService.runPreparedQueryFetchObject(0L, SELECT_MERGE_BRANCH, workingBranch.getUuid(),
            destinationBranch.getUuid());
      return longId != null && longId > 0;
   }

   @Override
   public IOseeBranch getBranchByUuid(long branchUuid) {
      return orcsApi.getQueryFactory(null).branchQuery().andUuids(branchUuid).getResults().getExactlyOne();
   }

   @Override
   public boolean branchExists(long branchUuid) {
      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      return query.andUuids(branchUuid).getCount() > 0;
   }

   @Override
   public BranchArchivedState getArchiveState(IOseeBranch branch) {
      String archived = dbService.runPreparedQueryFetchObject("0", SELECT_BRANCH_ARCHIVE_STATE, branch.getUuid());
      if (!Strings.isValid(archived)) {
         return null;
      }
      return BranchArchivedState.valueOf(archived);
   }

   @Override
   public IOseeBranch getBranch(ITransaction transaction) {
      return getBranchByUuid(((TransactionRecord) transaction).getBranchId());
   }

   @Override
   public Date getTimeStamp(ITransaction transaction) {
      return ((TransactionRecord) transaction).getTimeStamp();
   }

   @Override
   public Collection<ITransaction> getCommittedArtifactTransactionIds(IAtsTeamWorkflow teamWf) {
      ArtifactReadable artifactReadable = (ArtifactReadable) teamWf;
      List<ITransaction> transactionIds = commitArtifactIdMap.get(artifactReadable.getLocalId());
      // Cache the transactionIds first time through.  Other commits will be added to cache as they
      // happen in this client or as remote commit events come through
      if (transactionIds == null) {
         transactionIds = new ArrayList<ITransaction>(5);
         IOseeStatement chStmt = dbService.getStatement();
         try {
            chStmt.runPreparedQuery(SELECT_COMMIT_TRANSACTIONS, artifactReadable.getLocalId());
            while (chStmt.next()) {
               transactionIds.add(getTransactionId(chStmt.getInt("transaction_id")));
            }
            commitArtifactIdMap.put(artifactReadable.getLocalId(), transactionIds);
         } finally {
            chStmt.close();
         }
      }
      return transactionIds;
   }

   private TransactionRecord getTransactionId(int int1) {
      IOseeStatement chStmt = dbService.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_TRANSACTION, int1);
         while (chStmt.next()) {
            TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));
            return new TransactionRecord(int1, chStmt.getLong("branch_id"), chStmt.getString("osee_comment"),
               chStmt.getTimestamp("time"), chStmt.getInt("author"), chStmt.getInt("commit_art_id"), txType, null);
         }
      } finally {
         chStmt.close();
      }
      return null;
   }

   @Override
   public IOseeBranch getParentBranch(IOseeBranch branch) {
      long parentId = dbService.runPreparedQueryFetchObject(0L, SELECT_BRANCH_PARENT_ID, branch.getUuid());
      return getBranchByUuid(parentId);
   }

   @Override
   public ITransaction getBaseTransaction(IOseeBranch branch) {
      int baseTransactionId =
         dbService.runPreparedQueryFetchObject(0, SELECT_BRANCH_BASE_TRANSACTION_ID, branch.getUuid());
      return getTransactionId(baseTransactionId);
   }

}
