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
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactChangeAcquirer extends ChangeAcquirer {

   public ArtifactChangeAcquirer(Branch sourceBranch, TransactionId transactionId, IProgressMonitor monitor, Artifact specificArtifact, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds) {
      super(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
   }

   @Override
   public ArrayList<ChangeBuilder> acquireChanges() throws OseeCoreException {
      Map<Integer, ArtifactChangeBuilder> artifactChangeBuilders = new HashMap<Integer, ArtifactChangeBuilder>();
      boolean hasBranch = getSourceBranch() != null;
      TransactionId fromTransactionId;
      TransactionId toTransactionId;

      getMonitor().subTask("Gathering New or Deleted Artifacts");
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {

         if (hasBranch) { //Changes per a branch
            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getStartEndPoint(getSourceBranch());

            fromTransactionId = branchStartEndTransaction.getFirst();
            toTransactionId = branchStartEndTransaction.getSecond();

            chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_BRANCH_ARTIFACT),
                  getSourceBranch().getBranchId());
         } else { //Changes per a transaction
            toTransactionId = getTransactionId();

            if (getSpecificArtifact() != null) {
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_TX_ARTIFACT_FOR_SPECIFIC_ARTIFACT),
                     toTransactionId.getTransactionNumber(), getSpecificArtifact().getArtId());
               fromTransactionId = toTransactionId;
            } else {
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_TX_ARTIFACT),
                     toTransactionId.getTransactionNumber());
               fromTransactionId = TransactionIdManager.getPriorTransaction(toTransactionId);
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
