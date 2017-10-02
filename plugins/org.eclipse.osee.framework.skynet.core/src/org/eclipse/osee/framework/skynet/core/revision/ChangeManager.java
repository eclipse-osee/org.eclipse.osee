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
package org.eclipse.osee.framework.skynet.core.revision;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.Id4JoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Public API class for access to change data from branches and transactionIds
 *
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public final class ChangeManager {

   private final static RevisionChangeLoader revsionChangeLoader = new RevisionChangeLoader();

   private ChangeManager() {
      // this private empty constructor exists to prevent the default constructor from allowing public construction
   }

   public static Collection<ArtifactDelta> getCompareArtifacts(Collection<Change> changes) {
      Collection<ArtifactDelta> toReturn = new ArrayList<>(changes.size());
      for (Change change : changes) {
         toReturn.add(change.getDelta());
      }
      return toReturn;
   }

   /**
    * Acquires changes for a particular artifact
    */
   public static Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor) {
      return revsionChangeLoader.getChangesPerArtifact(artifact, monitor);
   }

   public static Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor, LoadChangeType... loadChangeTypes) {
      return revsionChangeLoader.getChangesPerArtifact(artifact, monitor, loadChangeTypes);
   }

   public static Collection<? extends Change> getChangesPerArtifact(Artifact artifact, int numberTransactionsToShow, IProgressMonitor monitor) {
      return revsionChangeLoader.getChangesPerArtifact(artifact, numberTransactionsToShow, monitor);
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    */
   public static IOperation comparedToPreviousTx(TransactionToken transactionId, Collection<Change> changes) {
      TransactionToken startTx = TransactionManager.getPriorTransaction(transactionId);
      TransactionToken endTx = transactionId;

      TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
      return new ChangeDataLoader(changes, txDelta);
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    */
   public static IOperation comparedToParent(BranchId branch, Collection<Change> changes) {
      TransactionToken startTx = TransactionManager.getHeadTransaction(branch);
      TransactionToken endTx = TransactionManager.getHeadTransaction(BranchManager.getParentBranch(branch));

      TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
      return new ChangeDataLoader(changes, txDelta);
   }

   public static IOperation compareTwoBranchesHead(BranchId sourceBranch, BranchId destinationBranch, Collection<Change> changes) {
      TransactionToken startTx = TransactionManager.getHeadTransaction(sourceBranch);
      TransactionToken endTx = TransactionManager.getHeadTransaction(destinationBranch);
      return new ChangeDataLoader(changes, new TransactionDelta(startTx, endTx));
   }

   /**
    * For the given list of artifacts determine which transactions (on that artifact's branch) affected that artifact.
    * The branch's baseline transaction is excluded.
    *
    * @return a map of artifact to collection of TransactionIds which affected the given artifact
    */
   public static HashCollection<Artifact, TransactionId> getModifingTransactions(Collection<Artifact> artifacts) {
      HashCollection<Artifact, TransactionId> transactionMap = new HashCollection<>();
      try (Id4JoinQuery joinQuery = JoinUtility.createId4JoinQuery()) {
         CompositeKeyHashMap<Integer, BranchId, Artifact> artifactMap = new CompositeKeyHashMap<>();
         for (Artifact artifact : artifacts) {
            BranchId branch = artifact.getBranch();
            artifactMap.put(artifact.getArtId(), branch, artifact);
            TransactionId transaction = TransactionManager.getHeadTransaction(branch);
            joinQuery.add(branch, artifact, transaction, branch.getViewId());

            // for each combination of artifact and its branch hierarchy
            while (branch.notEqual(CoreBranches.SYSTEM_ROOT)) {
               transaction = BranchManager.getSourceTransaction(branch);
               branch = BranchManager.getParentBranch(branch);
               joinQuery.add(branch, artifact, transaction);
            }
         }

         joinQuery.store();
         JdbcStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(joinQuery.size() * 2, ServiceUtil.getSql(OseeSql.CHANGE_TX_MODIFYING),
               joinQuery.getQueryId());
            while (chStmt.next()) {
               BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
               Artifact artifact = artifactMap.get(chStmt.getInt("art_id"), branch);
               transactionMap.put(artifact, TransactionId.valueOf(chStmt.getLong("transaction_id")));
            }
         } finally {
            chStmt.close();
         }
      }
      return transactionMap;
   }

   /**
    * For the given list of artifacts determine which branches (in the branch hierarchy for that artifact) affected that
    * artifact.
    *
    * @return a map of artifact to collection of branches which affected the given artifact
    */
   public static HashCollection<Artifact, BranchId> getModifingBranches(Collection<Artifact> artifacts) {
      HashCollection<Artifact, BranchId> branchMap = new HashCollection<>();
      try (Id4JoinQuery joinQuery = JoinUtility.createId4JoinQuery()) {
         CompositeKeyHashMap<Integer, BranchId, Artifact> artifactMap =
            new CompositeKeyHashMap<Integer, BranchId, Artifact>();
         for (Artifact artifact : artifacts) {
            artifactMap.put(artifact.getArtId(), artifact.getBranch(), artifact);
            // for each combination of artifact and all working branches in its hierarchy
            for (BranchId workingBranch : BranchManager.getBranches(BranchArchivedState.UNARCHIVED,
               BranchType.WORKING)) {
               if (artifact.isOnBranch(BranchManager.getParentBranch(workingBranch))) {
                  joinQuery.add(workingBranch, artifact, TransactionId.SENTINEL, workingBranch.getViewId());
               }
            }
         }

         joinQuery.store();
         JdbcStatement chStmt = ConnectionHandler.getStatement();

         try {
            chStmt.runPreparedQuery(joinQuery.size() * 2, ServiceUtil.getSql(OseeSql.CHANGE_BRANCH_MODIFYING),
               joinQuery.getQueryId());
            while (chStmt.next()) {
               if (chStmt.getInt("tx_count") > 0) {
                  BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
                  Artifact artifact = artifactMap.get(chStmt.getInt("art_id"), BranchManager.getParentBranch(branch));
                  branchMap.put(artifact, branch);
               }
            }
         } finally {
            chStmt.close();
         }
      }
      return branchMap;
   }

}