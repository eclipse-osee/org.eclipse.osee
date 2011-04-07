/*
 * Created on Apr 4, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact.revert;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Theron Virgin
 */
public class ArtifactRevert extends Revert {

   private static final String GET_GAMMAS_ARTIFACT_REVERT =
      "SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_attribute attr1 WHERE txs1.gamma_id = attr1.gamma_id and txs1.branch_id = ? and attr1.art_id = ? " + //
      "UNION ALL SELECT txs2.gamma_id, txs2.transaction_id FROM osee_txs txs2, osee_relation_link rel2 WHERE txs2.gamma_id = rel2.gamma_id AND txs2.branch_id = ? AND (rel2.a_art_id = ? or rel2.b_art_id = ?) " + //
      "UNION ALL SELECT txs3.gamma_id, txs3.transaction_id FROM osee_txs txs3, osee_artifact art3 WHERE txs3.gamma_id = art3.gamma_id AND txs3.branch_id = ? AND art3.art_id = ?";

   private final Integer[] artifactIds;
   private final Branch branch;

   public ArtifactRevert(Branch branch, Integer... artifactIds) {
      super();
      this.artifactIds = artifactIds;
      this.branch = branch;
   }

   @Override
   public void revert(OseeConnection connection) throws OseeCoreException {
      long totalTime = System.currentTimeMillis();
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         for (Integer artId : artifactIds) {
            int branchId = branch.getId();
            chStmt.runPreparedQuery(GET_GAMMAS_ARTIFACT_REVERT, branchId, artId, branchId, artId, artId, branchId,
               artId);
            TransactionRecord transId =
               TransactionManager.createNextTransactionId(connection, branch, UserManager.getUser(), "");
            revertObject(totalTime, artId, "Artifact", chStmt, transId, connection);
         }
      } finally {
         chStmt.close();
      }
   }
}
