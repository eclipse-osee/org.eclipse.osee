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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Theron Virgin
 */
public class RevertAction {
   private static final String DELETE_TXS_GAMMAS_REVERT =
      "DELETE from osee_txs txs1 WHERE txs1.gamma_id = ? and txs1.transaction_id = ?";
   private static final String UPDATE_DETAILS_TABLE =
      "UPDATE osee_tx_details set osee_comment = 'Reverted Transaction', tx_type = 2 WHERE transaction_id = ?";
   private static final String UPDATE_REVERT_TABLE =
      "INSERT INTO osee_removed_txs (transaction_id, rem_mod_type, rem_tx_current, rem_transaction_id, rem_gamma_id) (SELECT ?, txs.mod_type, txs.tx_current, txs.transaction_id, txs.gamma_id FROM osee_txs txs WHERE txs.gamma_id = ? AND txs.transaction_id = ?)";
   private static final String SET_TX_CURRENT_REVERT =
      "UPDATE osee_txs txs1 SET tx_current = " + TxChange.CURRENT.getValue() + " WHERE txs1.gamma_id = ? and txs1.transaction_id = ?";
   private static final String REVERT_ARTIFACT_VERSION_CURRENT_SELECT =
      "(SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_artifact_version art1 WHERE art1.art_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id = (SELECT max(txs.transaction_id) FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND art.art_id = ?))";
   private static final String UPDATE_TRANSACTION =
      "Update osee_txs set tx_current = CASE WHEN mod_type = 3 THEN 2 WHEN mod_type = 5 THEN 3 ELSE 1 END where (gamma_id, transaction_id) in ";
   private static final String REVERT_ARTIFACT_VERSION_SET_CURRENT =
      UPDATE_TRANSACTION + REVERT_ARTIFACT_VERSION_CURRENT_SELECT;
   private static final String REVERT_ARTIFACT_VERSION_SELECT =
      "SELECT det.tx_type, txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE txs.transaction_id in (%s) AND det.tx_type = 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND NOT EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs2.transaction_id = txs.transaction_id AND txs2.gamma_id != txs.gamma_id)";


   
   private static final boolean DEBUG =
      "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Revert"));

   List<Object[]> gammaIdsModifications = new ArrayList<Object[]>();
   List<Object[]> gammaIdsToInsert = new ArrayList<Object[]>();
   List<Object[]> gammaIdsBaseline = new ArrayList<Object[]>();
   List<Integer> transactionIds = new ArrayList<Integer>();
   
   private OseeConnection connection;
   private ConnectionHandlerStatement chStmt;
   private TransactionId transId;
   private String objectReverted;

   public RevertAction(OseeConnection connection, ConnectionHandlerStatement chStmt, TransactionId transId){
      this.connection = connection;
      this.chStmt = chStmt;
      this.transId = transId;
   }
   
   public void revertObject(long totalTime, int id, String objectReverted) throws OseeCoreException {
      this.objectReverted = objectReverted;
      
      processChStmtSortGammas();
      
      
      if (!gammaIdsModifications.isEmpty()) {
         updateTransactionTables();
         if (!gammaIdsBaseline.isEmpty()) {
            setTxCurrentForRevertedObjects();
         }
      }
      if (DEBUG) {
         System.out.println(String.format(" Reverted the %s %d in %s", objectReverted, id,
                  Lib.getElapseString(totalTime)));
      }
   }

   public void fixArtifactVersionForAttributeRevert(int branchId, int artId) throws OseeCoreException {
      if (!transactionIds.isEmpty()) {
         chStmt = new ConnectionHandlerStatement(connection);
         chStmt.runPreparedQuery(String.format(REVERT_ARTIFACT_VERSION_SELECT,
               Collections.toString(",", transactionIds)));
         objectReverted = "Atrtribute";
         processChStmtSortGammas();
         updateTransactionTables();
         updateArtifactVersionTxCurrents(branchId, artId);
      }
   }
   
   private void processChStmtSortGammas() throws OseeDataStoreException {
      gammaIdsModifications.clear();
      gammaIdsToInsert.clear();
      gammaIdsBaseline.clear();
      transactionIds.clear();
      long time = System.currentTimeMillis();
      try {
         while (chStmt.next()) {
            if (chStmt.getInt("tx_type") == TransactionDetailsType.NonBaselined.getId()) {
               Integer gammaId = chStmt.getInt("gamma_id");
               gammaIdsModifications.add(new Object[] {gammaId, chStmt.getInt("transaction_id")});
               gammaIdsToInsert.add(new Object[] {transId.getTransactionNumber(), gammaId,
                     chStmt.getInt("transaction_id")});
               transactionIds.add(chStmt.getInt("transaction_id"));
               if (DEBUG) {
                  System.out.println(String.format("  Revert%s: Delete Gamma ID = %d , Transaction ID = %d",
                        objectReverted, chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            } else {
               gammaIdsBaseline.add(new Object[] {chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")});
            }
         }
      } finally {
         chStmt.close();
      }
      if (DEBUG) {
         System.out.println(String.format("  Revert%s: Ran the Select Query in %s", objectReverted,
               Lib.getElapseString(time)));
      }
   }

   private void updateTransactionTables() throws OseeDataStoreException, OseeCoreException {
      long time = System.currentTimeMillis();
      ConnectionHandler.runPreparedUpdate(connection, UPDATE_DETAILS_TABLE, transId.getTransactionNumber());
      int count1 = ConnectionHandler.runBatchUpdate(connection, UPDATE_REVERT_TABLE, gammaIdsToInsert);
      int count2 = ConnectionHandler.runBatchUpdate(connection, DELETE_TXS_GAMMAS_REVERT, gammaIdsModifications);

      if (count1 != count2) {
         throw new OseeCoreException(String.format(
               "Revert Transaction moved %d transaction but should have moved %d", count1, count2));
      }
      if (DEBUG) {
         displayRevertResults(time, objectReverted, gammaIdsModifications, count2);
      }
   }

   private void displayRevertResults(long time, String objectReverted, List<Object[]> gammaIdsModifications, int count2) {
      System.out.println(String.format("Deleted %d txs for gamma revert in %s", count2, Lib.getElapseString(time)));
      time = System.currentTimeMillis();
      for (Object[] items : gammaIdsModifications) {
         System.out.println(String.format(" Revert %s: [gammaId, transactionId] = %s ", objectReverted,
               Arrays.deepToString(items)));
      }
      System.out.println(String.format("     Displayed all the data in %s", Lib.getElapseString(time)));
   }

   
   private void setTxCurrentForRevertedObjects() throws OseeDataStoreException {
      int count2;
      long time = System.currentTimeMillis();
      count2 = ConnectionHandler.runBatchUpdate(connection, SET_TX_CURRENT_REVERT, gammaIdsBaseline);
      if (DEBUG) {
         System.out.println(String.format("   Set %d tx currents for revert in %s", count2, Lib.getElapseString(time)));
         for (Object[] items : gammaIdsBaseline) {
            System.out.println(String.format(" Revert %s: Baseline [gammaId, transactionId] = %s ", objectReverted,
                  Arrays.deepToString(items)));
         }
      }
   }
   
   private void updateArtifactVersionTxCurrents(int branchId, int artId) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      if (DEBUG) {
         try {
            chStmt.runPreparedQuery(REVERT_ARTIFACT_VERSION_CURRENT_SELECT, artId, branchId, artId);
            while (chStmt.next()) {
               System.out.println(String.format(
                     "  Revert Artifact Current Version: Set Current Gamma ID = %d , Transaction ID = %d for art ID = %d branch ID = %d",
                     chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id"), artId, branchId));
            }
         } finally {
            chStmt.close();
         }
      }
      ConnectionHandler.runPreparedUpdate(connection, REVERT_ARTIFACT_VERSION_SET_CURRENT, artId, branchId, artId);
   }
}
