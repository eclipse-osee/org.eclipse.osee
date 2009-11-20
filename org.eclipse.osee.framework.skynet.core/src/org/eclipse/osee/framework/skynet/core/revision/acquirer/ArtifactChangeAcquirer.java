/*
 * Created on Sep 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.revision.acquirer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactChangeAcquirer extends ChangeAcquirer {

   public ArtifactChangeAcquirer(Branch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor, Artifact specificArtifact, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds) {
      super(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
   }

   @Override
   public ArrayList<ChangeBuilder> acquireChanges() throws OseeCoreException {
      Map<Integer, ArtifactChangeBuilder> artifactChangeBuilders = new HashMap<Integer, ArtifactChangeBuilder>();
      boolean hasBranch = getSourceBranch() != null;
      TransactionRecord fromTransactionId;
      TransactionRecord toTransactionId;

      getMonitor().subTask("Gathering New or Deleted Artifacts");
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {

         if (hasBranch) { //Changes per a branch
            Pair<TransactionRecord, TransactionRecord> branchStartEndTransaction =
                  TransactionManager.getStartEndPoint(getSourceBranch());

            fromTransactionId = branchStartEndTransaction.getFirst();
            toTransactionId = branchStartEndTransaction.getSecond();

            chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_BRANCH_ARTIFACT),
                  getSourceBranch().getId());
         } else { //Changes per a transaction
            toTransactionId = getTransaction();

            if (getSpecificArtifact() != null) {
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_TX_ARTIFACT_FOR_SPECIFIC_ARTIFACT),
                     toTransactionId.getId(), getSpecificArtifact().getArtId());
               fromTransactionId = toTransactionId;
            } else {
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_TX_ARTIFACT), toTransactionId.getId());
               fromTransactionId = TransactionManager.getPriorTransaction(toTransactionId);
            }
         }
         int count = 0;
         while (chStmt.next()) {
            count++;
            int artId = chStmt.getInt("art_id");
            ModificationType modificationType = ModificationType.getMod(chStmt.getInt("mod_type"));

            ArtifactChangeBuilder artifactChangeBuilder =
                  new ArtifactChangeBuilder(getSourceBranch(),
                        ArtifactTypeManager.getType(chStmt.getInt("art_type_id")), chStmt.getInt("gamma_id"), artId,
                        toTransactionId, fromTransactionId, modificationType, ChangeType.OUTGOING, !hasBranch);

            getArtIds().add(artId);
            getChangeBuilders().add(artifactChangeBuilder);
            artifactChangeBuilders.put(artId, artifactChangeBuilder);
         }

         getMonitor().worked(25);
      } finally {
         chStmt.close();
      }

      return getChangeBuilders();
   }

}
