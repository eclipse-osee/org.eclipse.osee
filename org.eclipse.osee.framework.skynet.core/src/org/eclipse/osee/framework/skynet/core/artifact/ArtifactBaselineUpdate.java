/*
 * Created on Mar 26, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class ArtifactBaselineUpdate {
   private static final String POPULATE_ARTIFACT_VERSION_GAMMAS_FOR_UPDATES =
         "INSERT INTO osee_join_transaction (insert_time, transaction_id, gamma_id, query_id) (Select ?, tx1.transaction_id, tx1.gamma_id, ? FROM osee_txs tx1, osee_tx_details td1, osee_artifact_version av1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = av1.gamma_id AND av1.art_id = ja1.art_id AND ja1.branch_id = ? AND ja1.query_id = ?)";
   private static final String POPULATE_ATTRIBUTE_GAMMAS_FOR_UPDATES =
         "INSERT INTO osee_join_transaction (insert_time, transaction_id, gamma_id, query_id) (Select ?, tx2.transaction_id, tx2.gamma_id, ? FROM osee_txs tx2, osee_tx_details td2, osee_attribute at2, osee_join_artifact ja2 WHERE td2.branch_id = ? AND td2.transaction_id = tx2.transaction_id AND tx2.gamma_id = at2.gamma_id AND at2.art_id = ja2.art_id AND ja2.branch_id = ? AND ja2.query_id = ?)";
   private static final String POPULATE_RELATION_GAMMAS_FOR_UPDATES =
         "INSERT INTO osee_join_transaction (insert_time, transaction_id, gamma_id, query_id) (Select ?, tx3.transaction_id, tx3.gamma_id, ? FROM osee_txs tx3, osee_tx_details td3, osee_relation_link rl3, osee_join_artifact ja3 WHERE td3.branch_id = ? AND td3.transaction_id = tx3.transaction_id AND tx3.gamma_id = rl3.gamma_id AND (rl3.a_art_id = ja3.art_id OR rl3.b_art_id = ja3.art_id) AND ja3.branch_id = ? AND ja3.query_id = ?)";

   private static final String UPDATE_REVERT_TABLE =
         "INSERT INTO osee_removed_txs (transaction_id, rem_mod_type, rem_tx_current, rem_transaction_id, rem_gamma_id) (SELECT ?, txs.mod_type, txs.tx_current, txs.transaction_id, txs.gamma_id FROM osee_txs txs, osee_join_transaction trn1 WHERE txs.gamma_id = trn1.gamma_id AND txs.transaction_id = trn1.transaction_id AND trn1.query_id = ?)";
   private static final String DELETE_FROM_TXS_TABLE =
         "DELETE FROM osee_txs where (transaction_id, gamma_id) in (SELECT transaction_id, gamma_id FROM osee_join_transaction WHERE query_id = ?)";

   private static final String INSERT_UPDATED_ARTIFACTS =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_txs tx1, osee_tx_details td1, osee_artifact_version av1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = " + TxChange.CURRENT.getValue() + " AND tx1.gamma_id = av1.gamma_id AND td1.branch_id = ja1.branch_id AND av1.art_id = ja1.art_id AND ja1.query_id = ?";
   private static final String INSERT_UPDATED_ATTRIBUTES_GAMMAS =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_txs tx1, osee_tx_details td1, osee_attribute at1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = " + TxChange.CURRENT.getValue() + " AND tx1.gamma_id = at1.gamma_id AND td1.branch_id = ja1.branch_id AND at1.art_id = ja1.art_id AND ja1.query_id = ?";
   private static final String INSERT_UPDATED_LINKS_GAMMAS =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT DISTINCT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_txs tx1, osee_tx_details td1, osee_relation_link rl1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = " + TxChange.CURRENT.getValue() + " AND tx1.gamma_id = rl1.gamma_id AND td1.branch_id = ja1.branch_id AND (rl1.a_art_id = ja1.art_id OR rl1.b_art_id = ja1.art_id) AND ja1.query_id = ?";
   private static final String DELETE_FROM_JOIN_TRANSACTION = "DELETE FROM osee_join_transaction WHERE query_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public static void replaceArtifactsWithArtifactsFromAnotherBranch(Branch branchToUpdate, List<Artifact> artifactVersions, Branch updatingSourceBranch) throws OseeCoreException {
      if (updatingSourceBranch == null) {
         if (!artifactVersions.isEmpty()) {
            updatingSourceBranch = artifactVersions.get(0).getBranch();
         } else {
            throw new OseeCoreException("Source Branch could not be identified for Artifact Update");
         }

      }
      int baselineTransactionNumber =
            TransactionIdManager.getStartEndPoint(branchToUpdate).getKey().getTransactionNumber();
      int queryIdForBranchInfo = ArtifactLoader.getNewQueryId();
      int queryIdForTransGammaIds = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
      List<Object[]> insertParameters = new LinkedList<Object[]>();
      TransactionId transId = TransactionIdManager.createNextTransactionId(branchToUpdate, UserManager.getUser(), "");

      OseeConnection connection = OseeDbConnection.getConnection();
      try {
         // insert into the artifact_join_table
         for (Artifact artifact : artifactVersions) {
            insertParameters.add(new Object[] {queryIdForBranchInfo, insertTime, artifact.getArtId(),
                  updatingSourceBranch.getBranchId(), SQL3DataType.INTEGER});
            try {
               Artifact sourceArtifact = ArtifactQuery.getArtifactFromId(artifact.getArtId(), branchToUpdate);
               sourceArtifact.revert();
            } catch (ArtifactDoesNotExist ex) {
               //Artifact does not need to be reverted if it does not exist.
            }
         }

         ArtifactLoader.insertIntoArtifactJoin(connection, insertParameters);

         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         int count =
               ConnectionHandler.runPreparedUpdate(connection, POPULATE_ARTIFACT_VERSION_GAMMAS_FOR_UPDATES, timestamp,
                     queryIdForTransGammaIds, branchToUpdate.getBranchId(), updatingSourceBranch.getBranchId(),
                     queryIdForBranchInfo);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO,"populated " + count + " Artifact Version gammas into the join table");
         count =
               ConnectionHandler.runPreparedUpdate(connection, POPULATE_ATTRIBUTE_GAMMAS_FOR_UPDATES, timestamp,
                     queryIdForTransGammaIds, branchToUpdate.getBranchId(), updatingSourceBranch.getBranchId(),
                     queryIdForBranchInfo);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO,"populated " + count + " Attribute gammas into the join table");
         count =
               ConnectionHandler.runPreparedUpdate(connection, POPULATE_RELATION_GAMMAS_FOR_UPDATES, timestamp,
                     queryIdForTransGammaIds, branchToUpdate.getBranchId(), updatingSourceBranch.getBranchId(),
                     queryIdForBranchInfo);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO,"populated " + count + " Relation gammas into the join table");

         count =
               ConnectionHandler.runPreparedUpdate(connection, UPDATE_REVERT_TABLE, transId.getTransactionNumber(),
                     queryIdForTransGammaIds);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO, "inserted " + count + " old gammas into the Revert Table");

         count = ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_TXS_TABLE, queryIdForTransGammaIds);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO, "Deleted " + count + " old gammas from the TXS Table");

         count =
               ConnectionHandler.runPreparedUpdate(connection, INSERT_UPDATED_ARTIFACTS, baselineTransactionNumber,
                     updatingSourceBranch.getBranchId(), queryIdForBranchInfo);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO, "inserted " + count + " artifacts");

         count =
               ConnectionHandler.runPreparedUpdate(connection, INSERT_UPDATED_ATTRIBUTES_GAMMAS,
                     baselineTransactionNumber, updatingSourceBranch.getBranchId(), queryIdForBranchInfo);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO, "inserted " + count + " attributes");

         count =
               ConnectionHandler.runPreparedUpdate(connection, INSERT_UPDATED_LINKS_GAMMAS, baselineTransactionNumber,
                     updatingSourceBranch.getBranchId(), queryIdForBranchInfo);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO, "inserted " + count + " relations");

      } finally {
         ArtifactLoader.clearQuery(connection, queryIdForBranchInfo);
         ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_JOIN_TRANSACTION, queryIdForTransGammaIds);
         connection.close();
      }
   }
}
