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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.RelationChangeBuilder;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Jeff C. Phillips
 */
public class RelationChangeAcquirer extends ChangeAcquirer {

   public RelationChangeAcquirer(Branch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor, Artifact specificArtifact, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds) {
      super(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
   }

   @Override
   public ArrayList<ChangeBuilder> acquireChanges() throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      TransactionRecord fromTransactionId;
      TransactionRecord toTransactionId;

      getMonitor().subTask("Gathering Relation Changes");
      try {
         boolean hasBranch = getSourceBranch() != null;

         //Changes per a branch
         if (hasBranch) {
            chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_BRANCH_RELATION),
                  getSourceBranch().getId());

            Pair<TransactionRecord, TransactionRecord> branchStartEndTransaction =
                  TransactionManager.getStartEndPoint(getSourceBranch());

            fromTransactionId = branchStartEndTransaction.getFirst();
            toTransactionId = branchStartEndTransaction.getSecond();
         }//Changes per a transaction
         else {
            toTransactionId = getTransaction();

            if (getSpecificArtifact() != null) {
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_TX_RELATION_FOR_SPECIFIC_ARTIFACT),
                     getTransaction().getId(), getSpecificArtifact().getArtId(), getSpecificArtifact().getArtId());
               fromTransactionId = getTransaction();
            } else {
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_TX_RELATION),
                     getTransaction().getId());
               fromTransactionId = TransactionManager.getPriorTransaction(toTransactionId);
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
                           relLinkId, rationale, RelationTypeManager.getType(chStmt.getInt("rel_link_type_id")),
                           !hasBranch));
            }
         }
         getMonitor().worked(25);
      } finally {
         chStmt.close();
      }
      return getChangeBuilders();
   }

}
