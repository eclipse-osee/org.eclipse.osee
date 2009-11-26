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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;

/**
 * @author Theron Virgin
 * @author Ryan D. Brooks
 */
public class CleanUpBackingData extends DatabaseHealthOperation {
   private static final String NOT_ADDRESSESED_GAMMAS =
         "select gamma_id from %s t1 where not exists (select 1 from osee_txs txs1 where t1.gamma_id = txs1.gamma_id) and not exists (select 1 from osee_removed_txs txs2 where t1.gamma_id = txs2.rem_gamma_id) and not exists (select 1 from osee_txs_archived txs3 where t1.gamma_id = txs3.gamma_id)";
   private static final String EMPTY_TRANSACTIONS =
         "select transaction_id from osee_tx_details txd where tx_type = 0 and not exists (select 1 from osee_txs txs1 where txd.transaction_id = txs1.transaction_id) and not exists (select 1 from osee_removed_txs txs2 where txd.transaction_id = txs2.transaction_id or txd.transaction_id = txs2.rem_transaction_id) and not exists (select 1 from osee_txs_archived txs3 where txd.transaction_id = txs3.transaction_id)";
   private static final String DELETE_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_EMPTY_TRANSACTIONS = "DELETE FROM osee_tx_details WHERE transaction_id = ?";

   private IProgressMonitor monitor;

   public CleanUpBackingData() {
      super("Data with no TXS Addressing and empty transactions");
   }

   private void processNotAddressedGammas(String tableName) throws OseeDataStoreException, IOException {
      checkForCancelledStatus(monitor);

      List<Object[]> notAddressedGammas = new LinkedList<Object[]>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      String sql = String.format(NOT_ADDRESSESED_GAMMAS, tableName);

      ResultsEditorTableTab resultsTab = new ResultsEditorTableTab(tableName + " gammas");
      getResultsProvider().addResultsTab(resultsTab);
      resultsTab.addColumn(new XViewerColumn("1", tableName + " gammas", 80, SWT.LEFT, true, SortDataType.Integer,
            false, ""));

      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            notAddressedGammas.add(new Object[] {chStmt.getLong("gamma_id")});
            resultsTab.addRow(new ResultsXViewerRow(new String[] {String.valueOf(chStmt.getLong("gamma_id"))}));
         }
      } finally {
         chStmt.close();
      }

      if (isFixOperationEnabled()) {
         sql = String.format(DELETE_GAMMAS, tableName);
         ConnectionHandler.runBatchUpdate(sql, notAddressedGammas);
      }

      monitor.worked(calculateWork(0.10));
   }

   private void processEmptyTransactions() throws OseeDataStoreException, IOException {
      checkForCancelledStatus(monitor);

      List<Object[]> emptyTransactions = new LinkedList<Object[]>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();

      ResultsEditorTableTab resultsTab = new ResultsEditorTableTab("Empty transactions");
      getResultsProvider().addResultsTab(resultsTab);
      resultsTab.addColumn(new XViewerColumn("1", "Transaction Id", 80, SWT.LEFT, true, SortDataType.Integer, false, ""));

      try {
         chStmt.runPreparedQuery(EMPTY_TRANSACTIONS);
         while (chStmt.next()) {
            emptyTransactions.add(new Object[] {chStmt.getInt("transaction_id")});
            resultsTab.addRow(new ResultsXViewerRow(new String[] {String.valueOf(chStmt.getInt("transaction_id"))}));
         }
      } finally {
         chStmt.close();
      }

      if (isFixOperationEnabled()) {
         ConnectionHandler.runBatchUpdate(DELETE_EMPTY_TRANSACTIONS, emptyTransactions);
      }

      monitor.worked(calculateWork(0.10));
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      this.monitor = monitor;

      processNotAddressedGammas("osee_attribute");
      processNotAddressedGammas("osee_artifact_version");
      processNotAddressedGammas("osee_relation_link");
      processEmptyTransactions();

      monitor.worked(calculateWork(0.20));
   }

   @Override
   public String getCheckDescription() {
      return "Check for artifact, attribute, and relation versions that are not addressed and for empty transactions";
   }

   @Override
   public String getFixDescription() {
      return "Purge artifact, attribute, and relation versions that are not addressed and purge empty transactions";
   }
}