/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Updates commit transactions so new and then modified objects will be committed with a mod type of new. This BLAM
 * operation also removes attribute from deleted artifacts from committed transactions.
 *
 * @author Jeff C. Phillips
 */
public class CommitTransactions extends DatabaseHealthOperation {
   private static final String GET_COMMIT_TRANSACTIONS =
      "SELECT transaction_id FROM osee_tx_details WHERE osee_comment LIKE '%Commit%'";
   private static final String UPDATE_NEW_TRANSACTIONS_TO_CURRENT =
      "UPDATE osee_txs SET mod_type = 1 WHERE transaction_id = ? AND mod_type <> 1 AND gamma_id IN (SELECT tx1.gamma_id FROM osee_txs tx1, osee_artifact av1 WHERE tx1.transaction_id = ? AND tx1.gamma_id = av1.gamma_id AND av1.art_id NOT IN (SELECT av2.art_id FROM osee_tx_details td2, osee_txs tx2, osee_artifact av2 WHERE td2.osee_comment like '%Commit%' AND tx2.transaction_id < tx1.transaction_id AND tx2.transaction_id = tx2.transaction_id AND tx2.gamma_id = av2.gamma_id) AND av1.art_id NOT IN (SELECT av3.art_id FROM osee_txs tx3, osee_artifact av3 WHERE tx3.branch_id = tx1.branch_id AND tx3.transaction_id < tx1.transaction_id AND tx3.mod_type = 1 AND tx3.gamma_id = av3.gamma_id))";
   private static final String DELETE_ORPHAN_ATTRIBUTES =
      "DELETE FROM osee_attribute WHERE gamma_id IN (SELECT t3.gamma_id FROM osee_txs t2, osee_attribute t3 WHERE t2.transaction_id = ? AND t2.gamma_id = t3.gamma_id AND t3.art_id NOT IN (SELECT art_id FROM osee_txs t4, osee_artifact t5 WHERE t4.transaction_id = t2.transaction_id AND t4.gamma_id = t5.gamma_id))";

   public CommitTransactions() {
      super(
         "commit transactions by deleting orphan attributes and setting new artifacts that have been modified to a mod type of 1");
   }

   @Override
   public String getVerifyTaskName() {
      return Strings.emptyString();
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      if (isFixOperationEnabled()) {
         checkForCancelledStatus(monitor);

         JdbcStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(GET_COMMIT_TRANSACTIONS, new Object[0]);

            checkForCancelledStatus(monitor);
            monitor.worked(calculateWork(0.50));

            while (chStmt.next()) {
               Long transactionNumber = chStmt.getLong("transaction_id");
               int updateCount = ConnectionHandler.runPreparedUpdate(UPDATE_NEW_TRANSACTIONS_TO_CURRENT,
                  transactionNumber, transactionNumber);
               int deleteAttrCount = ConnectionHandler.runPreparedUpdate(DELETE_ORPHAN_ATTRIBUTES, transactionNumber);

               getSummary().append(
                  "For transaction: " + transactionNumber + " Number of update modTypes to 1:" + updateCount + " Number of deleted attrs: " + deleteAttrCount);
            }
         } finally {
            chStmt.close();
         }
      } else {
         monitor.worked(calculateWork(0.50));
      }
      monitor.worked(calculateWork(0.50));
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
