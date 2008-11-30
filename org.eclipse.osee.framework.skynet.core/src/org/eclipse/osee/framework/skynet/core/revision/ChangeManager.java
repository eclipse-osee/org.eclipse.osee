package org.eclipse.osee.framework.skynet.core.revision;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.status.IStatusMonitor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Public API class for access to change data from branches and transactionIds
 * 
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public class ChangeManager {

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return changes
    * @throws OseeCoreException
    */
   public static Collection<Change> getChangesPerTransaction(TransactionId transactionId, IStatusMonitor monitor) throws OseeCoreException {
      return InternalChangeManager.getInstance().getChanges(null, transactionId,
            monitor == null ? new EmptyMonitor() : monitor);
   }

   public static ChangeData getChangeDataPerTransaction(TransactionId transactionId, IStatusMonitor monitor) throws OseeCoreException {
      return new ChangeData(InternalChangeManager.getInstance().getChanges(null, transactionId,
            monitor == null ? new EmptyMonitor() : monitor));
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return changes
    * @throws OseeCoreException
    */
   public static Collection<Change> getChangesPerBranch(Branch sourceBranch, IStatusMonitor monitor) throws OseeCoreException {
      return InternalChangeManager.getInstance().getChanges(sourceBranch, null,
            monitor == null ? new EmptyMonitor() : monitor);
   }

   public static ChangeData getChangeDataPerBranch(Branch sourceBranch, IStatusMonitor monitor) throws OseeCoreException {
      return new ChangeData(InternalChangeManager.getInstance().getChanges(sourceBranch, null,
            monitor == null ? new EmptyMonitor() : monitor));
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return changes
    * @throws OseeCoreException
    */
   public static Collection<Change> getChanges(Branch sourceBranch, TransactionId transactionId, IStatusMonitor monitor) throws OseeCoreException {
      return InternalChangeManager.getInstance().getChanges(sourceBranch, transactionId,
            monitor == null ? new EmptyMonitor() : monitor);
   }

   public static ChangeData getChangeData(Branch sourceBranch, TransactionId transactionId, IStatusMonitor monitor) throws OseeCoreException {
      return new ChangeData(getChanges(sourceBranch, transactionId, monitor == null ? new EmptyMonitor() : monitor));
   }

   /**
    * @return true changes exist
    * @throws OseeCoreException
    */
   public static boolean isChangesOnWorkingBranch(Branch workingBranch) throws OseeCoreException {
      return InternalChangeManager.getInstance().isChangesOnWorkingBranch(workingBranch);
   }

   /**
    * For the given list of artifacts determine which transactions (on that artifact's branch) affected that artifact.
    * The branch's baseline transaction is excluded.
    * 
    * @param artifacts
    * @return a map of artifact to collection of TransactionIds which affected the given artifact
    * @throws OseeCoreException
    */
   public static HashCollection<Artifact, TransactionId> getModifingTransactions(Collection<Artifact> artifacts) throws OseeCoreException {
      List<Object[]> insertParameters = new ArrayList<Object[]>(artifacts.size() * 5);

      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      for (Artifact artifact : artifacts) {
         Branch branch = artifact.getBranch();
         int transactionNumber = Integer.MAX_VALUE;
         insertParameters.add(new Object[] {queryId, insertTime, artifact.getArtId(), branch.getBranchId(),
               transactionNumber});

         // for each combination of artifact and its branch hierarchy
         while (branch.hasParentBranch()) {
            transactionNumber = branch.getParentTransactionId();
            branch = branch.getParentBranch();
            insertParameters.add(new Object[] {queryId, insertTime, artifact.getArtId(), branch.getBranchId(),
                  transactionNumber});
         }
      }

      HashCollection<Artifact, TransactionId> transactionMap = new HashCollection<Artifact, TransactionId>();
      try {
         ArtifactLoader.selectArtifacts(insertParameters);
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(insertParameters.size() * 2,
                  ClientSessionManager.getSQL(OseeSql.Changes.SELECT_MODIFYING_TRANSACTION), queryId);
            while (chStmt.next()) {
               int artifactId = chStmt.getInt("art_id");
               int branchId = chStmt.getInt("branch_id");
               int transactionNumber = chStmt.getInt("transaction_id");
               Artifact artifact = ArtifactCache.getActive(artifactId, BranchManager.getBranch(branchId));
               transactionMap.put(artifact, TransactionIdManager.getTransactionId(transactionNumber));
            }
         } finally {
            chStmt.close();
         }
      } finally {
         ArtifactLoader.clearQuery(queryId);
      }

      return transactionMap;
   }

   /**
    * For the given list of artifacts determine which branches (in the branch hierarchy for that artifact) affected that
    * artifact.
    * 
    * @param artifacts
    * @return a map of artifact to collection of branches which affected the given artifact
    */
   public HashCollection<Artifact, Branch> getModifingBranches(Collection<Artifact> artifacts) {
      return null;
   }
}