/*
 * Created on Sep 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.revision.acquirer;

import java.util.ArrayList;
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
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.RelationChangeBuilder;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Jeff C. Phillips
 */
public class RelationChangeAcquirer extends ChangeAcquirer {

   public RelationChangeAcquirer(Branch sourceBranch, TransactionId transactionId, IProgressMonitor monitor, Artifact specificArtifact, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds) {
      super(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
   }

   @Override
   public ArrayList<ChangeBuilder> acquireChanges() throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      TransactionId fromTransactionId;
      TransactionId toTransactionId;

      getMonitor().subTask("Gathering Relation Changes");
      try {
         boolean hasBranch = getSourceBranch() != null;

         //Changes per a branch
         if (hasBranch) {
            chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_BRANCH_RELATION),
                  getSourceBranch().getBranchId());

            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getStartEndPoint(getSourceBranch());

            fromTransactionId = branchStartEndTransaction.getFirst();
            toTransactionId = branchStartEndTransaction.getSecond();
         }//Changes per a transaction
         else {
            toTransactionId = getTransactionId();

            if (getSpecificArtifact() != null) {
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_TX_RELATION_FOR_SPECIFIC_ARTIFACT),
                     getTransactionId().getTransactionNumber(), getSpecificArtifact().getArtId(),
                     getSpecificArtifact().getArtId());
               fromTransactionId = getTransactionId();
            } else {
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_TX_RELATION),
                     getTransactionId().getTransactionNumber());
               fromTransactionId = TransactionIdManager.getPriorTransaction(toTransactionId);
            }
         }

         while (chStmt.next()) {
            int aArtId = chStmt.getInt("a_art_id");
            int bArtId = chStmt.getInt("b_art_id");
            int relLinkId = chStmt.getInt("rel_link_id");

            if (!getNewAndDeletedArtifactIds().contains(aArtId) && !getNewAndDeletedArtifactIds().contains(bArtId)) {
               ModificationType modificationType = ModificationType.getMod(chStmt.getInt("mod_type"));
               String rationale = modificationType != ModificationType.DELETED ? chStmt.getString("rationale") : "";
               getArtIds().add(aArtId);
               getArtIds().add(bArtId);

               getChangeBuilders().add(
                     new RelationChangeBuilder(getSourceBranch(),
                           ArtifactTypeManager.getType(chStmt.getInt("art_type_id")), chStmt.getInt("gamma_id"),
                           aArtId, toTransactionId, fromTransactionId, modificationType, ChangeType.OUTGOING, bArtId,
                           relLinkId, rationale,RelationTypeManager.getType(chStmt.getInt("rel_link_type_id")), !hasBranch));
            }
         }
         getMonitor().worked(25);
      } finally {
         chStmt.close();
      }
      return getChangeBuilders();
   }

}
