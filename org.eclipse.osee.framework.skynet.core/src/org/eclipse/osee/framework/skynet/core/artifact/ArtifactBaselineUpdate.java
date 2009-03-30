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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class ArtifactBaselineUpdate {
   private static final String INSERT_UPDATED_ARTIFACTS =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_txs tx1, osee_tx_details td1, osee_artifact_version av1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = " + TxChange.CURRENT.getValue() + " AND tx1.gamma_id = av1.gamma_id AND td1.branch_id = ja1.branch_id AND av1.art_id = ja1.art_id AND ja1.query_id = ?";
   private static final String INSERT_UPDATED_ATTRIBUTES_GAMMAS =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_txs tx1, osee_tx_details td1, osee_attribute at1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = " + TxChange.CURRENT.getValue() + " AND tx1.gamma_id = at1.gamma_id AND td1.branch_id = ja1.branch_id AND at1.art_id = ja1.art_id AND ja1.query_id = ?";
   private static final String INSERT_UPDATED_LINKS_GAMMAS =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT DISTINCT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_txs tx1, osee_tx_details td1, osee_relation_link rl1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = " + TxChange.CURRENT.getValue() + " AND tx1.gamma_id = rl1.gamma_id AND td1.branch_id = ja1.branch_id AND (rl1.a_art_id = ja1.art_id OR rl1.b_art_id = ja1.art_id) AND ja1.query_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public static void overrideAndUpdateArtifacts(Branch branchToUpdate, List<Artifact> artifactVersions, Branch sourceBranch) throws OseeCoreException {
      if (sourceBranch == null){
         if (!artifactVersions.isEmpty()){
         sourceBranch = artifactVersions.get(0).getBranch();
         } else {
            throw new OseeCoreException("Source Branch could not be identified for Artifact Update");
         }
         
      } 
      int baselineTransactionNumber =
            TransactionIdManager.getStartEndPoint(branchToUpdate).getKey().getTransactionNumber();
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
      List<Object[]> insertParameters = new LinkedList<Object[]>();

      try {
         // insert into the artifact_join_table
         for (Artifact artifact : artifactVersions) {
            insertParameters.add(new Object[] {queryId, insertTime, artifact.getArtId(), sourceBranch.getBranchId(),
                  SQL3DataType.INTEGER});
            artifact.revert();
         }
         ArtifactLoader.insertIntoArtifactJoin(insertParameters);
         
         int count =
               ConnectionHandler.runPreparedUpdate(INSERT_UPDATED_ARTIFACTS, baselineTransactionNumber,
                     sourceBranch.getBranchId(), queryId);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO, "inserted " + count + " artifacts");

         count =
               ConnectionHandler.runPreparedUpdate(INSERT_UPDATED_ATTRIBUTES_GAMMAS, baselineTransactionNumber,
                     sourceBranch.getBranchId(), queryId);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO, "inserted " + count + " attributes");

         count =
               ConnectionHandler.runPreparedUpdate(INSERT_UPDATED_LINKS_GAMMAS, baselineTransactionNumber,
                     sourceBranch.getBranchId(), queryId);
         OseeLog.log(ArtifactBaselineUpdate.class, Level.INFO, "inserted " + count + " relations");

      } finally {
         ArtifactLoader.clearQuery(queryId);
      }
   }
}
