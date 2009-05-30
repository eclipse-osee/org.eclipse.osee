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
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Identifies and removes addressing from the transaction table that no longer addresses other tables.
 * 
 * @author Theron Virgin
 */
public class CleanUpBackingData extends DatabaseHealthOperation {

   private static final String NOT_ADDRESSESED_GAMMAS =
         HealthHelper.ALL_BACKING_GAMMAS + " %s (SELECT gamma_id FROM osee_txs Union Select rem_gamma_id as gamma_id FROM osee_removed_txs)";
   private static final String NOT_ADDRESSESED_TRANSACTIONS =
         "SELECT transaction_id FROM osee_tx_details WHERE tx_type != 1 %s (SELECT transaction_id FROM osee_txs UNION SELECT rem_transaction_id as transaction_id FROM osee_removed_txs UNION SELECT DISTINCT transaction_id FROM osee_removed_txs)";
   private static final String REMOVE_GAMMAS_ARTIFACT = "DELETE FROM osee_artifact_version WHERE gamma_id = ?";
   private static final String REMOVE_GAMMAS_ATTRIBUTE = "DELETE FROM osee_attribute WHERE gamma_id = ?";
   private static final String REMOVE_GAMMAS_RELATIONS = "DELETE FROM osee_relation_link WHERE gamma_id = ?";
   private static final String REMOVE_NOT_ADDRESSED_TRANSACTIONS =
         "DELETE FROM osee_tx_details WHERE transaction_id = ?";

   private List<Object[]> gammas = null;
   private List<Object[]> transactions = null;

   public CleanUpBackingData() {
      super("Data with no TXS Addressing");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#doHealthCheck(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      boolean fix = isFixOperationEnabled();
      boolean verify = !fix;
      monitor.beginTask(fix ? "Deleting Data with no TXS addressing" : "Checking For Data with no TXS addressing", 100);
      monitor.worked(5);

      if (verify || gammas == null) {
         gammas =
               HealthHelper.runSingleResultQuery(String.format(NOT_ADDRESSESED_GAMMAS,
                     SupportedDatabase.getComplementSql()), "gamma_id");
         monitor.worked(25);
         checkForCancelledStatus(monitor);
      }
      if (verify || transactions == null) {
         transactions =
               HealthHelper.runSingleResultQuery(String.format(NOT_ADDRESSESED_TRANSACTIONS,
                     SupportedDatabase.getComplementSql()), "transaction_id");
         monitor.worked(25);
         checkForCancelledStatus(monitor);
      }

      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      HealthHelper.displayForCleanUp("Gamma Id", sbFull, getAppendable(), verify, gammas, "'s with no TXS addressing\n");
      monitor.worked(20);
      HealthHelper.displayForCleanUp("Transaction Id", sbFull, getAppendable(), verify, transactions,
            "'s with no TXS addressing\n");
      monitor.worked(20);

      checkForCancelledStatus(monitor);

      if (fix) {
         ConnectionHandler.runBatchUpdate(REMOVE_GAMMAS_ARTIFACT, gammas);
         monitor.worked(5);
         ConnectionHandler.runBatchUpdate(REMOVE_GAMMAS_ATTRIBUTE, gammas);
         monitor.worked(5);
         ConnectionHandler.runBatchUpdate(REMOVE_GAMMAS_RELATIONS, gammas);
         monitor.worked(5);
         ConnectionHandler.runBatchUpdate(REMOVE_NOT_ADDRESSED_TRANSACTIONS, transactions);
         monitor.worked(5);
         gammas = null;
         transactions = null;

      }

      if (isShowDetailsEnabled()) {
         HealthHelper.endTable(sbFull, getVerifyTaskName());
      }
   }
}
