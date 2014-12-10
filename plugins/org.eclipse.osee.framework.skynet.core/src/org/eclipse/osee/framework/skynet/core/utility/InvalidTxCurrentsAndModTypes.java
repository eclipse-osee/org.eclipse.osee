/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan D. Brooks
 */
public class InvalidTxCurrentsAndModTypes extends AbstractOperation {
   private static final String SELECT_ADDRESSES =
      "select %s, txs.branch_id, txs.transaction_id, txs.gamma_id, txs.mod_type, txs.tx_current, txd.tx_type from %s t1, osee_txs%s txs, osee_tx_details txd where t1.gamma_id = txs.gamma_id and txd.transaction_id = txs.transaction_id and txs.branch_id = txd.branch_id order by txs.branch_id, %s, txs.transaction_id desc, txs.gamma_id desc";

   private static final String DELETE_ADDRESS =
      "delete from osee_txs%s where transaction_id = ? and gamma_id = ? and branch_id = ?";
   private static final String UPDATE_ADDRESS =
      "update osee_txs%s set tx_current = ? where transaction_id = ? and gamma_id = ? and branch_id = ?";

   private final List<Address> addresses = new ArrayList<Address>();

   private final List<Object[]> purgeData = new ArrayList<Object[]>();
   private final List<Object[]> currentData = new ArrayList<Object[]>();
   private final String tableName;
   private final String columnName;
   private final boolean isFixOperationEnabled;
   private final String txsTableName;
   private final JdbcClient jdbcClient;

   public InvalidTxCurrentsAndModTypes(String operationName, String tableName, String columnName, OperationLogger logger, boolean isFixOperationEnabled, boolean archived) throws OseeDataStoreException {
      this(ConnectionHandler.getJdbcClient(), operationName, tableName, columnName, logger, isFixOperationEnabled,
         archived);
   }

   private InvalidTxCurrentsAndModTypes(JdbcClient jdbcClient, String operationName, String tableName, String columnName, OperationLogger logger, boolean isFixOperationEnabled, boolean archived) {
      super(
         "InvalidTxCurrentsAndModTypes " + operationName + tableName + " fix:" + isFixOperationEnabled + " archived:" + archived,
         Activator.PLUGIN_ID, logger);
      this.jdbcClient = jdbcClient;
      this.tableName = tableName;
      this.columnName = columnName;
      this.isFixOperationEnabled = isFixOperationEnabled;
      txsTableName = archived ? "_archived" : "";
   }

   private JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   private void fixIssues(IProgressMonitor monitor) throws OseeCoreException {
      if (isFixOperationEnabled) {
         checkForCancelledStatus(monitor);
         getJdbcClient().runBatchUpdate(String.format(DELETE_ADDRESS, txsTableName), purgeData);
         getJdbcClient().runBatchUpdate(String.format(UPDATE_ADDRESS, txsTableName), currentData);
      }
      monitor.worked(calculateWork(0.1));
   }

   private void logIssue(String issue, Address address) {
      log(issue, String.valueOf(address.getBranchId()), String.valueOf(address.getItemId()),
         String.valueOf(address.getTransactionId()), String.valueOf(address.getGammaId()),
         address.getModType().toString(), address.getTxCurrent().toString());
   }

   private void consolidateAddressing() {
      checkForMultipleVersionsInOneTransaction();
      checkForIdenticalAddressingInDifferentTransactions();
      checkForMultipleCurrents();
      checkForInvalidMergedModType();

      if (issueDetected()) {
         for (Address address : addresses) {
            if (address.isPurge()) {
               logIssue("purge", address);
               purgeData.add(new Object[] {address.getTransactionId(), address.getGammaId(), address.getBranchId()});
            } else if (address.getCorrectedTxCurrent() != null) {
               logIssue("corrected txCurrent: " + address.getCorrectedTxCurrent(), address);
               currentData.add(new Object[] {
                  address.getCorrectedTxCurrent().getValue(),
                  address.getTransactionId(),
                  address.getGammaId(),
                  address.getBranchId()});
            } else {
               System.out.println("would have fixed merge here");
            }
         }
      }
   }

   private void checkForInvalidMergedModType() {
      int index = addresses.size() - 1;
      Address lastAddress = addresses.get(index);
      if (!lastAddress.isBaselineTx()) {
         for (; index > -1; index--) {
            if (!addresses.get(index).isPurge()) {
               if (addresses.get(index).getModType() == ModificationType.MERGED) {
                  //                  logIssue("found merged mod type for item not in baseline: ", addresses.get(index));
               }
               return;
            }
         }
      }
   }

   private void checkForIdenticalAddressingInDifferentTransactions() {
      Address previousAddress = null;

      for (Address address : addresses) {
         if (address.hasSameGamma(previousAddress) && address.hasSameModType(previousAddress)) {
            previousAddress.setPurge(true);
         }
         previousAddress = address;
      }
   }

   private boolean issueDetected() {
      for (Address address : addresses) {
         if (address.hasIssue()) {
            return true;
         }
      }
      return false;
   }

   private void checkForMultipleVersionsInOneTransaction() {
      Address previousAddress = null;

      for (Address address : addresses) {
         if (address.isSameTransaction(previousAddress)) {
            if (address.hasSameModType(previousAddress) || !address.getModType().isDeleted() && previousAddress.getModType().isEdited()) {
               address.setPurge(true);
            } else {
               logIssue("multiple versions in one transaction - unknown case", address);
            }
         }
         previousAddress = address;
      }
   }

   private void checkForMultipleCurrents() {
      boolean mostRecentTx = true;
      for (Address address : addresses) {
         if (!address.isPurge()) {
            if (mostRecentTx) {
               address.ensureCorrectCurrent();
               mostRecentTx = false;
            } else {
               address.ensureNotCurrent();
            }
         }
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      log("Starting " + getName());

      checkForCancelledStatus(monitor);

      JdbcStatement chStmt = getJdbcClient().getStatement();
      String sql = String.format(SELECT_ADDRESSES, columnName, tableName, txsTableName, columnName);
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, sql);
         monitor.worked(calculateWork(0.40));

         Address previousAddress = null;
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
            TxChange txCurrent = TxChange.getChangeType(chStmt.getInt("tx_current"));
            TransactionDetailsType type = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));
            Address address =
               new Address(type.isBaseline(), chStmt.getLong("branch_id"), chStmt.getInt(columnName),
                  chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id"), modType, txCurrent);

            if (!address.isSimilar(previousAddress)) {
               if (!addresses.isEmpty()) {
                  consolidateAddressing();
               }
               addresses.clear();
            }

            addresses.add(address);
            previousAddress = address;
         }
         monitor.worked(calculateWork(0.5));
      } finally {
         chStmt.close();
      }

      fixIssues(monitor);

      log("Completed " + getName());
   }
}