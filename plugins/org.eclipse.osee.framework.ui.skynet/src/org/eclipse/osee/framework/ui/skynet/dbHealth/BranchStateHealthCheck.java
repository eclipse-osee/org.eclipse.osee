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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public class BranchStateHealthCheck extends DatabaseHealthOperation {

   public BranchStateHealthCheck() {
      super("Branch State Errors");
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      monitor.setTaskName("Loading Branch information");
      List<BranchData> itemsToFix = new ArrayList<>();

      Collection<BranchData> branchDatas = getAllBranchData();
      monitor.worked(calculateWork(0.25));
      checkForCancelledStatus(monitor);

      monitor.setTaskName("Checking Branch State Data");
      for (BranchData branchData : branchDatas) {
         check(branchData);
         if (branchData.hasBranchStateChanged()) {
            itemsToFix.add(branchData);
         }
      }
      monitor.worked(calculateWork(0.25));
      checkForCancelledStatus(monitor);

      appendToDetails(AHTML.beginMultiColumnTable(100, 1));
      appendToDetails(AHTML.addHeaderRowMultiColumnTable(new String[] {
         "Reason",
         "Was State",
         "Fixed State",
         "BranchType",
         "Archived",
         "Txs",
         "Commit Tx",
         "BranchId",
         "Branch Name"}));
      Collections.sort(itemsToFix, new Comparator<BranchData>() {

         @Override
         public int compare(BranchData o1, BranchData o2) {
            int result = 0;
            if (o1 != null && o2 != null) {
               result = o1.getBranchType().compareTo(o2.getBranchType());
               if (result == 0) {
                  result = ((Boolean) o1.isArchived()).compareTo(o2.isArchived());
               }
               if (result == 0) {
                  result = o1.getNumberOfTxs() - o2.getNumberOfTxs();
               }
            }
            return result;
         }

      });
      for (BranchData data : itemsToFix) {
         appendToDetails(AHTML.addRowMultiColumnTable(new String[] {
            data.getReason(),
            String.valueOf(data.getOriginalBranchState()),
            String.valueOf(data.getBranchState()),
            String.valueOf(data.getBranchType()),
            String.valueOf(data.isArchived()),
            String.valueOf(data.getNumberOfTxs()),
            String.valueOf(data.hasCommitTransactionId()),
            String.valueOf(data.getId()),
            data.getBranchName()}));
      }
      appendToDetails(AHTML.endMultiColumnTable());
      monitor.worked(calculateWork(0.25));
      checkForCancelledStatus(monitor);

      setItemsToFix(itemsToFix.size());
      if (isFixOperationEnabled() && getItemsToFixCount() > 0) {
         monitor.setTaskName("Fixing Branch State data");
         List<Object[]> data = new ArrayList<>();
         for (BranchData branchData : itemsToFix) {
            data.add(new Object[] {branchData.getBranchState().getValue(), branchData});
         }
         ConnectionHandler.runBatchUpdate("update osee_branch set branch_state = ? where branch_id = ?", data);
      }

      getSummary().append(String.format("Found [%s] branches with invalid branchState\n", getItemsToFixCount()));
      monitor.worked(calculateWork(0.25));
   }

   private boolean isRebaselined(BranchData branchData) {
      String name = branchData.getBranchName();
      return name.contains("- moved by update on -") || name.contains("- for update -");
   }

   private boolean hasBeenCommitted(BranchData branchData) {
      boolean result = false;
      if (BranchType.MERGE.equals(branchData.getBranchType())) {
         result = branchData.hasCommitTransactionId();
      } else {
         result = branchData.isArchived();
      }
      return result;
   }

   private void check(BranchData branchData) {
      BranchState state = branchData.getBranchState();
      BranchType type = branchData.getBranchType();

      if (BranchType.SYSTEM_ROOT.equals(type)) {
         if (!BranchState.CREATED.equals(state)) {
            branchData.setBranchState(BranchState.CREATED);
            branchData.setReason("System Root should always be set to created");
         }
      } else if (state == BranchState.CREATED || state == BranchState.MODIFIED || state == BranchState.COMMITTED || state == BranchState.REBASELINED || state == BranchState.CREATION_IN_PROGRESS) {
         if (BranchType.WORKING.equals(type)) {
            if (isRebaselined(branchData)) {
               branchData.setBranchState(BranchState.REBASELINED);
               branchData.setReason("Rebaselined case - detected from name");
            } else if (hasBeenCommitted(branchData)) {
               branchData.setBranchState(BranchState.COMMITTED);
               branchData.setReason("Committed case - detected");
            } else if (branchData.getNumberOfTxs() > 1) {
               branchData.setBranchState(BranchState.MODIFIED);
               branchData.setReason("Modified case - detected");
            } else {
               branchData.setBranchState(BranchState.CREATED);
               branchData.setReason("Was Created case - detected");
            }
         } else if (BranchType.MERGE.equals(type)) {
            if (hasBeenCommitted(branchData)) {
               branchData.setBranchState(BranchState.COMMITTED);
               branchData.setReason("Committed case - detected");
            } else if (branchData.getNumberOfTxs() > 1) {
               branchData.setBranchState(BranchState.MODIFIED);
               branchData.setReason("Modified case - detected");
            } else {
               branchData.setBranchState(BranchState.CREATED);
               branchData.setReason("Was Created case - detected");
            }
         }
      }
   }

   private Collection<BranchData> getAllBranchData() {
      Map<Long, BranchData> data = new HashMap<Long, BranchData>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery("select * from osee_branch");
         while (chStmt.next()) {
            long branchUuid = chStmt.getLong("branch_id");
            int branchType = chStmt.getInt("branch_type");
            int branchState = chStmt.getInt("branch_state");
            boolean isArchived = chStmt.getInt("archived") == 1 ? true : false;
            int numberOfTxs = ConnectionHandler.getJdbcClient().fetch(0,
               "select count(1) from osee_tx_details where branch_id = ?", branchUuid);
            data.put(branchUuid, new BranchData(branchUuid, chStmt.getString("branch_name"),
               BranchType.valueOf(branchType), BranchState.getBranchState(branchState), isArchived, numberOfTxs));
         }
      } finally {
         chStmt.close();
      }
      chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery("select * from osee_merge");
         while (chStmt.next()) {
            Long branchUuid = chStmt.getLong("merge_branch_id");
            BranchData branchData = data.get(branchUuid);
            branchData.setHasCommitTransactionId(chStmt.getInt("commit_transaction_id") != -1);
         }
      } finally {
         chStmt.close();
      }
      return data.values();
   }
   private final class BranchData {
      private final long branchUuid;
      private final String branchName;
      private final BranchType branchType;
      private final BranchState originalBranchState;
      private BranchState branchState;
      private final boolean isArchived;
      private final int numberOfTxs;
      private String reasonForChange;
      private boolean hasCommitTransactionId;

      private BranchData(long branchUuid, String branchName, BranchType branchType, BranchState branchState, boolean isArchived, int numberOfTxs) {
         super();
         this.branchUuid = branchUuid;
         this.branchName = branchName;
         this.branchType = branchType;
         this.branchState = branchState;
         this.originalBranchState = branchState;
         this.isArchived = isArchived;
         this.numberOfTxs = numberOfTxs;
         this.reasonForChange = Strings.emptyString();
         this.hasCommitTransactionId = false;
      }

      public long getId() {
         return branchUuid;
      }

      public String getBranchName() {
         return branchName;
      }

      public BranchType getBranchType() {
         return branchType;
      }

      public void setBranchState(BranchState branchState) {
         this.branchState = branchState;
      }

      public BranchState getBranchState() {
         return branchState;
      }

      public BranchState getOriginalBranchState() {
         return originalBranchState;
      }

      public boolean isArchived() {
         return isArchived;
      }

      public int getNumberOfTxs() {
         return numberOfTxs;
      }

      public boolean hasBranchStateChanged() {
         return branchState != originalBranchState;
      }

      public void setReason(String reasonForChange) {
         this.reasonForChange = reasonForChange;
      }

      public String getReason() {
         return reasonForChange;
      }

      public void setHasCommitTransactionId(boolean hasCommitTransactionId) {
         this.hasCommitTransactionId = hasCommitTransactionId;
      }

      public boolean hasCommitTransactionId() {
         return hasCommitTransactionId;
      }
   }

   @Override
   public String getCheckDescription() {
      return "Enter Check Description Here";
   }

   @Override
   public String getFixDescription() {
      return "Enter Fix Description Here";
   }

}
