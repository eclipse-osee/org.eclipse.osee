package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.MultiPageResultsProvider;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;

public class InvalidTxCurrentsAndModTypes extends AbstractOperation {
   private static final String SELECT_ADDRESSES =
         "select %s, txd.branch_id, txd.transaction_id, tx_type, txs.gamma_id, mod_type, tx_current from %s t1, osee_txs txs, osee_tx_details txd where t1.gamma_id = txs.gamma_id and txs.transaction_id = txd.transaction_id order by txd.branch_id, %s, txd.transaction_id desc, txs.gamma_id desc";

   private static final String DELETE_ADDRESS = "delete from osee_txs where transaction_id = ? and gamma_id = ?";
   private static final String UPDATE_ADDRESS =
         "update osee_txs set tx_current = ? where transaction_id = ? and gamma_id = ?";

   private final List<Address> addresses = new ArrayList<Address>();
   private ResultsEditorTableTab resultsTab;

   private final List<Object[]> purgeData = new ArrayList<Object[]>();
   private final List<Object[]> currentData = new ArrayList<Object[]>();
   private final String tableName;
   private final String columnName;
   private final MultiPageResultsProvider resultsProvider;
   private final boolean isFixOperationEnabled;

   public InvalidTxCurrentsAndModTypes(String tableName, String columnName, MultiPageResultsProvider resultsProvider, boolean isFixOperationEnabled) {
      super("InvalidTxCurrentsAndModTypes " + tableName, SkynetGuiPlugin.PLUGIN_ID);
      this.tableName = tableName;
      this.columnName = columnName;
      this.resultsProvider = resultsProvider;
      this.isFixOperationEnabled = isFixOperationEnabled;
   }

   private void fixIssues(IProgressMonitor monitor) throws OseeDataStoreException {
      if (isFixOperationEnabled) {
         checkForCancelledStatus(monitor);
         ConnectionHandler.runBatchUpdate(DELETE_ADDRESS, purgeData);
         ConnectionHandler.runBatchUpdate(UPDATE_ADDRESS, currentData);
      }
      monitor.worked(calculateWork(0.1));
   }

   private void logIssue(String issue, Address address) {
      resultsTab.addRow(new ResultsXViewerRow(new String[] {issue, String.valueOf(address.branchId),
            String.valueOf(address.itemId), String.valueOf(address.transactionId), String.valueOf(address.gammaId),
            address.modType.toString(), address.txCurrent.toString()}));
   }

   private void consolidateAddressing() {
      checkForMultipleVersionsInOneTransaction();
      checkForIdenticalAddressingInDifferentTransactions();
      checkForMultipleCurrents();

      if (issueDetected()) {
         for (Address address : addresses) {
            if (address.purge) {
               logIssue("purge", address);
               purgeData.add(new Object[] {address.transactionId, address.gammaId});
            } else if (address.correctedTxCurrent != null) {
               logIssue("corrected txCurrent: " + address.correctedTxCurrent, address);
               currentData.add(new Object[] {address.correctedTxCurrent.getValue(), address.transactionId,
                     address.gammaId});
            }
         }
      }
   }

   private void checkForIdenticalAddressingInDifferentTransactions() {
      Address previousAddress = null;

      for (Address address : addresses) {
         if (address.hasSameGamma(previousAddress) && address.hasSameModType(previousAddress)) {
            previousAddress.purge = true;
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
            if (address.hasSameModType(previousAddress) || !address.modType.isDeleted() && previousAddress.modType.isEdited()) {
               address.purge = true;
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
         if (!address.purge) {
            if (mostRecentTx) {
               address.ensureCorrectCurrent();
               mostRecentTx = false;
            } else {
               address.ensureNotCurrent();
            }
         }
      }
   }

   private static class Address {
      final int branchId;
      final int itemId;
      final int transactionId;
      final long gammaId;
      final boolean isBaselineTx;
      final ModificationType modType;
      final TxChange txCurrent;

      TxChange correctedTxCurrent;
      boolean purge;

      public Address(int branchId, int itemId, int transactionId, int txType, long gammaId, int modType, int txCurrent) throws OseeArgumentException {
         super();
         this.branchId = branchId;
         this.itemId = itemId;
         this.transactionId = transactionId;
         this.gammaId = gammaId;
         this.modType = ModificationType.getMod(modType);
         this.txCurrent = TxChange.getChangeType(txCurrent);
         this.isBaselineTx = TransactionDetailsType.toEnum(txType) == TransactionDetailsType.Baselined;
      }

      public boolean isSimilar(Address other) {
         return other != null && other.itemId == itemId && other.branchId == branchId;
      }

      public boolean isSameTransaction(Address other) {
         return other != null && transactionId == other.transactionId;
      }

      public boolean hasSameGamma(Address other) {
         return other != null && gammaId == other.gammaId;
      }

      public boolean hasSameModType(Address other) {
         return modType == other.modType;
      }

      public void ensureCorrectCurrent() {
         TxChange correctCurrent = TxChange.getCurrent(modType);
         if (txCurrent != correctCurrent) {
            correctedTxCurrent = correctCurrent;
         }
      }

      public void ensureNotCurrent() {
         if (txCurrent != TxChange.NOT_CURRENT) {
            correctedTxCurrent = TxChange.NOT_CURRENT;
         }
      }

      public boolean hasIssue() {
         return purge || correctedTxCurrent != null;
      }

      @Override
      public String toString() {
         return "Address [branchId=" + branchId + ", gammaId=" + gammaId + ", itemId=" + itemId + ", modType=" + modType + ", transactionId=" + transactionId + ", txCurrent=" + txCurrent + "]";
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      checkForCancelledStatus(monitor);

      resultsTab = new ResultsEditorTableTab(tableName + " currents");
      resultsProvider.addResultsTab(resultsTab);
      resultsTab.addColumn(new XViewerColumn("1", "Issue", 220, SWT.LEFT, true, SortDataType.String, false, ""));
      resultsTab.addColumn(new XViewerColumn("2", "Branch Id", 80, SWT.LEFT, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(new XViewerColumn("3", columnName, 80, SWT.LEFT, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(new XViewerColumn("4", "Transaction Id", 80, SWT.LEFT, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(new XViewerColumn("5", "Gamma Id", 80, SWT.LEFT, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(new XViewerColumn("6", "Mod Type", 80, SWT.LEFT, true, SortDataType.String, false, ""));
      resultsTab.addColumn(new XViewerColumn("7", "TX Current", 80, SWT.LEFT, true, SortDataType.String, false, ""));

      IOseeStatement chStmt = ConnectionHandler.getStatement();
      String sql = String.format(SELECT_ADDRESSES, columnName, tableName, columnName);
      try {
         chStmt.runPreparedQuery(10000, sql);
         monitor.worked(calculateWork(0.40));

         Address previousAddress = null;
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            Address address =
                  new Address(chStmt.getInt("branch_id"), chStmt.getInt(columnName), chStmt.getInt("transaction_id"),
                  chStmt.getInt("tx_type"), chStmt.getLong("gamma_id"), chStmt.getInt("mod_type"),
                  chStmt.getInt("tx_current"));

            if (!address.isSimilar(previousAddress)) {
               consolidateAddressing();
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
   }
}