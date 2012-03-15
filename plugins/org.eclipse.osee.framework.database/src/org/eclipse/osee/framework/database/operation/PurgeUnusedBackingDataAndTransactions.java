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
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.internal.DatabaseHelper;

/**
 * Purge artifact, attribute, and relation versions that are not addressed or nonexistent and purge empty transactions
 * 
 * @author Ryan D. Brooks
 */
public class PurgeUnusedBackingDataAndTransactions extends AbstractOperation {

   private static final String NOT_ADDRESSESED_GAMMAS =
      "select gamma_id from %s t1 where not exists (select 1 from osee_txs txs1 where t1.gamma_id = txs1.gamma_id) and not exists (select 1 from osee_txs_archived txs3 where t1.gamma_id = txs3.gamma_id)";

   private static final String EMPTY_TRANSACTIONS =
      "select branch_id, transaction_id from osee_tx_details txd where not exists (select 1 from osee_txs txs1 where txs1.branch_id = txd.branch_id and txs1.transaction_id = txd.transaction_id) and not exists (select 1 from osee_txs_archived txs2 where txs2.branch_id = txd.branch_id and txs2.transaction_id = txd.transaction_id)";

   private static final String NONEXISTENT_GAMMAS = "SELECT gamma_id FROM %s txs WHERE " + //
   "NOT EXISTS (SELECT 1 FROM osee_attribute att WHERE txs.gamma_id = att.gamma_id) " + //
   "AND NOT EXISTS (SELECT 1 FROM osee_artifact art WHERE txs.gamma_id = art.gamma_id) " + //
   "AND NOT EXISTS (SELECT 1 FROM osee_relation_link rel WHERE txs.gamma_id = rel.gamma_id)";

   private static final String DELETE_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_EMPTY_TRANSACTIONS =
      "DELETE FROM osee_tx_details WHERE branch_id = ? and transaction_id = ?";

   private final IOseeDatabaseService dbService;

   public PurgeUnusedBackingDataAndTransactions(OperationLogger logger) throws OseeDataStoreException {
      this(DatabaseHelper.getOseeDatabaseService(), logger);
   }

   public PurgeUnusedBackingDataAndTransactions(IOseeDatabaseService dbService, OperationLogger logger) {
      super("Data with no TXS Addressing and empty transactions", DatabaseHelper.PLUGIN_ID, logger);
      this.dbService = dbService;
   }

   private IOseeDatabaseService getDatabaseService() {
      return dbService;
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
      getDatabaseService().runBatchUpdate(sql, notAddressedGammas);
   }

   private void processAddressedButNonexistentGammas(String tableName) throws OseeCoreException {
      List<Object[]> nonexistentGammas = new LinkedList<Object[]>();
      IOseeStatement chStmt = getDatabaseService().getStatement();

      try {
         String sql = String.format(NONEXISTENT_GAMMAS, tableName);

         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            nonexistentGammas.add(new Object[] {chStmt.getInt("gamma_id")});
            log(String.valueOf(chStmt.getInt("gamma_id")));
         }
      } finally {
         chStmt.close();
      }

      getDatabaseService().runBatchUpdate(String.format(DELETE_GAMMAS, tableName), nonexistentGammas);
   }

   private void processEmptyTransactions() throws OseeCoreException {
      List<Object[]> emptyTransactions = new LinkedList<Object[]>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();

      String sql = EMPTY_TRANSACTIONS;
      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            emptyTransactions.add(new Object[] {chStmt.getInt("branch_id"), chStmt.getInt("transaction_id")});
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
      processAddressedButNonexistentGammas("osee_txs");
      processAddressedButNonexistentGammas("osee_txs_archived");
      processEmptyTransactions();
   }
}