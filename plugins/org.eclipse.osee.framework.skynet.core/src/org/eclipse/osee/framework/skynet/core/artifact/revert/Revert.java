/*
 * Created on Apr 3, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact.revert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Theron Virgin
 */
public abstract class Revert {
   private static final String REVERT_COMMENT = "Reverted Transaction";

   private static final String DELETE_TXS_GAMMAS_REVERT =
      "DELETE FROM osee_txs WHERE gamma_id = ? AND transaction_id = ?";

   private static final String UPDATE_DETAILS_TABLE =
      "UPDATE osee_tx_details SET osee_comment = ?, tx_type = ? WHERE transaction_id = ?";

   private static final String SET_TX_CURRENT_REVERT =
      "UPDATE osee_txs SET tx_current = " + TxChange.CURRENT.getValue() + " WHERE gamma_id = ? and transaction_id = ?";

   private static final String REVERT_ARTIFACT_VERSION_CURRENT_SELECT =
      "(SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_artifact art1 WHERE art1.art_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id = (SELECT max(txs.transaction_id) FROM osee_txs txs, osee_artifact art WHERE txs.branch_id = ? AND txs.gamma_id = art.gamma_id AND art.art_id = ?))";

   private static final String REVERT_ARTIFACT_VERSION_SET_CURRENT =
      "UPDATE osee_txs SET tx_current = CASE WHEN mod_type = 3 THEN 2 WHEN mod_type = 5 THEN 3 ELSE 1 END WHERE (gamma_id, transaction_id) IN " + REVERT_ARTIFACT_VERSION_CURRENT_SELECT;

   private static final String REVERT_ARTIFACT_VERSION_SELECT =
      "SELECT txs.gamma_id, txs.transaction_id FROM osee_txs txs, osee_artifact art WHERE txs.transaction_id in (%s) AND txs.gamma_id = art.gamma_id AND NOT EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs2.transaction_id = txs.transaction_id AND txs2.gamma_id != txs.gamma_id)";

   private final List<Object[]> gammaIdsModifications = new ArrayList<Object[]>();
   private final List<Object[]> gammaIdsToInsert = new ArrayList<Object[]>();
   private final List<Object[]> gammaIdsBaseline = new ArrayList<Object[]>();
   private final List<Integer> transactionIds = new ArrayList<Integer>();

   private String objectReverted;

   private static final boolean DEBUG =
      "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Revert"));

   public abstract void revert(OseeConnection connection) throws OseeCoreException;

   protected void revertObject(long totalTime, int id, String objectReverted, IOseeStatement chStmt, TransactionRecord transId, OseeConnection connection) throws OseeCoreException {
      this.objectReverted = objectReverted;

      processChStmtSortGammas(chStmt, transId);

      if (!gammaIdsModifications.isEmpty()) {
         updateTransactionTables(connection, transId);
         if (!gammaIdsBaseline.isEmpty()) {
            setTxCurrentForRevertedObjects(connection);
         }
      }
      if (DEBUG) {
         System.out.println(String.format(" Reverted the %s %d in %s", objectReverted, id,
            Lib.getElapseString(totalTime)));
      }
   }

   private void processChStmtSortGammas(IOseeStatement chStmt, TransactionRecord transId) throws OseeCoreException {
      gammaIdsModifications.clear();
      gammaIdsToInsert.clear();
      gammaIdsBaseline.clear();
      transactionIds.clear();
      long time = System.currentTimeMillis();
      try {
         Integer baselineTransactionId = transId.getBranch().getBaseTransaction().getId();
         while (chStmt.next()) {
            Integer transactionId = chStmt.getInt("transaction_id");
            Long gammaId = chStmt.getLong("gamma_id");

            if (baselineTransactionId.equals(transactionId)) {
               gammaIdsBaseline.add(new Object[] {gammaId, transactionId});
            } else {
               gammaIdsModifications.add(new Object[] {gammaId, transactionId});
               gammaIdsToInsert.add(new Object[] {transId.getId(), gammaId, transactionId});
               transactionIds.add(transactionId);
               if (DEBUG) {
                  System.out.println(String.format("  Revert%s: Delete Gamma ID = %s , Transaction ID = %s",
                     objectReverted, gammaId, transactionId));
               }
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

   public void fixArtifactVersionForAttributeRevert(int branchId, int artId, OseeConnection connection, TransactionRecord transId) throws OseeCoreException {
      if (!transactionIds.isEmpty()) {
         IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
         try {
            chStmt.runPreparedQuery(String.format(REVERT_ARTIFACT_VERSION_SELECT,
               Collections.toString(",", transactionIds)));
            objectReverted = "Atrtribute";
            processChStmtSortGammas(chStmt, transId);
            updateTransactionTables(connection, transId);
            updateArtifactVersionTxCurrents(branchId, artId, connection);
         } finally {
            chStmt.close();
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void updateTransactionTables(OseeConnection connection, TransactionRecord transId) throws OseeDataStoreException, OseeCoreException {
      ConnectionHandler.runPreparedUpdate(connection, UPDATE_DETAILS_TABLE, REVERT_COMMENT,
         TxChange.DELETED.getValue(), transId.getId());
      ConnectionHandler.runBatchUpdate(connection, DELETE_TXS_GAMMAS_REVERT, gammaIdsModifications);
   }

   private void setTxCurrentForRevertedObjects(OseeConnection connection) throws OseeCoreException {
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

   private void updateArtifactVersionTxCurrents(int branchId, int artId, OseeConnection connection) throws OseeCoreException {
      if (DEBUG) {
         IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
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
