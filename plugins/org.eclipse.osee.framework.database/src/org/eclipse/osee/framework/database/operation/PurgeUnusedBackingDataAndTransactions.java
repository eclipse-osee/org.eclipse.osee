/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.operation;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.internal.Activator;

/**
 * Purge artifact, attribute, and relation versions that are not addressed or nonexistent and purge empty transactions
 * 
 * @author Ryan D. Brooks
 */
public class PurgeUnusedBackingDataAndTransactions extends AbstractOperation {
   private static final String NOT_ADDRESSESED_GAMMAS =
      "select gamma_id from %s t1 where not exists (select 1 from osee_txs txs1 where t1.gamma_id = txs1.gamma_id) and not exists (select 1 from osee_txs_archived txs3 where t1.gamma_id = txs3.gamma_id)";
   private static final String EMPTY_TRANSACTIONS =
      "select transaction_id from osee_tx_details txd where tx_type = 0 and not exists (select 1 from osee_txs txs1 where txd.transaction_id = txs1.transaction_id) and not exists (select 1 from osee_txs_archived txs3 where txd.transaction_id = txs3.transaction_id)";
   
   private static final String NONEXISTENT_GAMMAS =
      "SELECT gamma_id FROM osee_txs txs WHERE NOT EXISTS " +
      "(SELECT 1 FROM osee_attribute att WHERE txs.gamma_id = att.gamma_id) AND NOT EXISTS " +
      "(SELECT 1 FROM osee_artifact art WHERE txs.gamma_id = art.gamma_id) AND NOT EXISTS " +
      "(SELECT 1 FROM osee_relation_link rel WHERE txs.gamma_id = rel.gamma_id)";
   private static final String DELETE_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_EMPTY_TRANSACTIONS = "DELETE FROM osee_tx_details WHERE transaction_id = ?";

   public PurgeUnusedBackingDataAndTransactions(OperationLogger logger) {
      super("Data with no TXS Addressing and empty transactions", Activator.PLUGIN_ID, logger);
   }

   private void processNotAddressedGammas(String tableName) throws OseeCoreException {
      List<Object[]> notAddressedGammas = new LinkedList<Object[]>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      String sql = String.format(NOT_ADDRESSESED_GAMMAS, tableName);

      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            notAddressedGammas.add(new Object[] {chStmt.getLong("gamma_id")});
            log(String.valueOf(chStmt.getLong("gamma_id")));
         }
      } finally {
         chStmt.close();
      }

      sql = String.format(DELETE_GAMMAS, tableName);
      ConnectionHandler.runBatchUpdate(sql, notAddressedGammas);
   }

   private void processAddressedButNonexistentGammas() throws OseeCoreException {
      List<Object[]> nonexistentGammas = new LinkedList<Object[]>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();

      try {
         chStmt.runPreparedQuery(NONEXISTENT_GAMMAS);
         while (chStmt.next()) {
            nonexistentGammas.add(new Object[] {chStmt.getInt("gamma_id")});
            log(String.valueOf(chStmt.getInt("gamma_id")));
         }
      } finally {
         chStmt.close();
      }

      ConnectionHandler.runBatchUpdate(String.format(DELETE_GAMMAS, "osee_txs"), nonexistentGammas);
   }

   private void processEmptyTransactions() throws OseeCoreException {
      List<Object[]> emptyTransactions = new LinkedList<Object[]>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();

      try {
         chStmt.runPreparedQuery(EMPTY_TRANSACTIONS);
         while (chStmt.next()) {
            emptyTransactions.add(new Object[] {chStmt.getInt("transaction_id")});
            log(String.valueOf(chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }

      ConnectionHandler.runBatchUpdate(DELETE_EMPTY_TRANSACTIONS, emptyTransactions);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      processNotAddressedGammas("osee_attribute");
      processNotAddressedGammas("osee_artifact");
      processNotAddressedGammas("osee_relation_link");
      processAddressedButNonexistentGammas();
      processEmptyTransactions();
   }
}