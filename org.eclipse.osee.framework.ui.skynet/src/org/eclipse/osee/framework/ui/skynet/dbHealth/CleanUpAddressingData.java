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

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Identifies and removes addressing from the transaction table that no longer addresses other tables.
 * 
 * @author Theron Virgin
 */
public class CleanUpAddressingData extends DatabaseHealthOperation {

   private static final String NOT_BACKED_GAMMAS =
         "SELECT gamma_id from osee_txs Union Select rem_gamma_id as gamma_id FROM osee_removed_txs %s " + HealthHelper.ALL_BACKING_GAMMAS;
   private static final String NOT_BACKED_TRANSACTIONS =
         "SELECT transaction_id from osee_txs UNION SELECT rem_transaction_id as transaction_id FROM osee_removed_txs UNION SELECT DISTINCT transaction_id FROM osee_removed_txs %s SELECT transaction_id from osee_tx_details";
   private static final String REMOVE_NOT_ADDRESSED_GAMMAS = "DELETE FROM osee_txs WHERE gamma_id = ?";
   private static final String REMOVE_NOT_ADDRESSED_TRANSACTIONS = "DELETE FROM osee_txs WHERE transaction_id = ?";

   private List<Object[]> gammas = null;
   private List<Object[]> transactions = null;

   public CleanUpAddressingData() {
      super("TXS Entries with no Backing Data");
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      boolean fix = isFixOperationEnabled();
      boolean verify = !fix;
      if (verify || gammas == null) {
         gammas =
               HealthHelper.runSingleResultQuery(
                     String.format(NOT_BACKED_GAMMAS, SupportedDatabase.getComplementSql()), "gamma_id");
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.25));

      if (verify || transactions == null) {
         transactions =
               HealthHelper.runSingleResultQuery(String.format(NOT_BACKED_TRANSACTIONS,
                     SupportedDatabase.getComplementSql()), "transaction_id");
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.25));

      appendToDetails(AHTML.beginMultiColumnTable(100, 1));
      HealthHelper.displayForCleanUp("Gamma Id", getDetailedReport(), getSummary(), verify, gammas,
            "'s with no backing data\n");
      HealthHelper.displayForCleanUp("Transaction Id", getDetailedReport(), getSummary(), verify, transactions,
            "'s with no backing data\n");

      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.25));

      int gammaCount = gammas != null ? gammas.size() : 0;
      int txCount = transactions != null ? transactions.size() : 0;
      setItemsToFix(txCount + gammaCount);

      if (fix) {
         if (gammas.size() > 0) {
            ConnectionHandler.runBatchUpdate(REMOVE_NOT_ADDRESSED_GAMMAS, gammas);
         }
         monitor.worked(calculateWork(0.10));
         if (transactions.size() > 0) {
            ConnectionHandler.runBatchUpdate(REMOVE_NOT_ADDRESSED_TRANSACTIONS, transactions);
         }
         monitor.worked(calculateWork(0.10));
         gammas = null;
         transactions = null;
      } else {
         monitor.worked(calculateWork(0.20));
      }

      appendToDetails(AHTML.endMultiColumnTable());
      monitor.worked(calculateWork(0.05));
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
