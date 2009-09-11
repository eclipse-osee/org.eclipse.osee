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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.ArtifactChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.AttributeChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.RelationChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Acquires changes for either branches or transactions.
 * 
 * @author Jeff C. Phillips
 */
public final class InternalChangeManager {
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Change"));

   private static InternalChangeManager instance = new InternalChangeManager();

   private InternalChangeManager() {
      super();
   }

   public static InternalChangeManager getInstance() {
      return instance;
   }

   /**
    * @return Returns artifact, relation and attribute changes from a specific artifact
    * @throws OseeCoreException
    */
   Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      ArrayList<Change> changes = new ArrayList<Change>();
      Branch branch = artifact.getBranch();
      ArrayList<TransactionId> transactionIds = new ArrayList<TransactionId>();
      recurseBranches(branch, artifact, transactionIds);

      for (TransactionId transactionId : transactionIds) {
         changes.addAll(getChanges(null, transactionId, monitor, artifact));
      }
      return changes;
   }

   private void recurseBranches(Branch branch, Artifact artifact, Collection<TransactionId> transactionIds) throws OseeCoreException {
      transactionIds.addAll(getTransactionsPerArtifact(branch, artifact));

      if (branch.getParentBranch() != null && branch.hasTopLevelBranch()) {
         recurseBranches(branch.getParentBranch(), artifact, transactionIds);
      }
   }

   private Collection<TransactionId> getTransactionsPerArtifact(Branch branch, Artifact artifact) throws OseeCoreException {
      Set<TransactionId> transactionIds = new HashSet<TransactionId>();

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CHANGE_GET_TRANSACTIONS_PER_ARTIFACT),
               branch.getBranchId(), artifact.getArtId());

         while (chStmt.next()) {
            transactionIds.add(TransactionIdManager.getTransactionId(chStmt.getInt("transaction_id")));
         }

         chStmt.runPreparedQuery("SELECT td1.transaction_id from osee_tx_details td1, osee_txs tx1, osee_relation_link rel where td1.branch_id = ? and td1.transaction_id = tx1.transaction_id and tx1.gamma_id = rel.gamma_id and (rel.a_art_id = ? or rel.b_art_id = ?)",
               branch.getBranchId(), artifact.getArtId(), artifact.getArtId());

         while (chStmt.next()) {
            transactionIds.add(TransactionIdManager.getTransactionId(chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }

      return transactionIds;
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    */
   Collection<Change> getChanges(Branch sourceBranch, TransactionId transactionId, IProgressMonitor monitor) throws OseeCoreException {
      return getChanges(sourceBranch, transactionId, monitor, null);
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return
    * @throws OseeCoreException
    */
   private Collection<Change> getChanges(Branch sourceBranch, TransactionId transactionId, IProgressMonitor monitor, Artifact specificArtifact) throws OseeCoreException {
      ArrayList<Change> changes = new ArrayList<Change>();
      ArrayList<ChangeBuilder> changeBuilders = new ArrayList<ChangeBuilder>();
      
      Set<Integer> artIds = new HashSet<Integer>();
      Set<Integer> newAndDeletedArtifactIds = new HashSet<Integer>();
      boolean historical = sourceBranch == null;
      long totalTime = System.currentTimeMillis();

      monitor.beginTask("Find Changes", 100);
      if (DEBUG) {
         System.out.println(String.format("\nChange Manager: getChanges(%s, %s)", sourceBranch, transactionId));
      }

      ArtifactChangeAcquirer artifactChangeAcquirer = new ArtifactChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
      changeBuilders = artifactChangeAcquirer.acquireChanges();
      
      AttributeChangeAcquirer attributeChangeAcquirer = new AttributeChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
      changeBuilders = attributeChangeAcquirer.acquireChanges();
      
      RelationChangeAcquirer relationChangeAcquirer = new RelationChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
      changeBuilders = relationChangeAcquirer.acquireChanges();
      
      monitor.subTask("Loading Artifacts from the Database");
      Branch branch = historical ? transactionId.getBranch() : sourceBranch;
      
      long time = System.currentTimeMillis();
      if (!artIds.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (int artId : artIds) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, branch.getBranchId(),
                  historical ? transactionId.getTransactionNumber() : SQL3DataType.INTEGER});
         }
         
         ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, true, historical, true);
      }
      
      for(ChangeBuilder builder : changeBuilders){
         changes.add(builder.build(branch));
      }

      if (DEBUG) {
         System.out.println(String.format("     Loaded %d Artifacts in %s", artIds.size(), Lib.getElapseString(time)));
         System.out.println(String.format("Change Manager: Found all of the Changes in %s\n",
               Lib.getElapseString(totalTime)));
      }
      monitor.done();
      return changes;
   }
   
   boolean isChangesOnWorkingBranch(Branch workingBranch) throws OseeCoreException {
      Pair<TransactionId, TransactionId> transactionToFrom = TransactionIdManager.getStartEndPoint(workingBranch);
      if (transactionToFrom.getFirst().equals(transactionToFrom.getSecond())) {
         return false;
      }
      return true;
   }
}