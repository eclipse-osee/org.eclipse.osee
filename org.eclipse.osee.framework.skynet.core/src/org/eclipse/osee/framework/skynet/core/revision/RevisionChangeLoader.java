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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.ArtifactChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.AttributeChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.RelationChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * Acquires changes for either branches or transactions.
 * 
 * @author Jeff C. Phillips
 */
public final class RevisionChangeLoader {

   protected RevisionChangeLoader() {
      super();
   }

   /**
    * @return Returns artifact, relation and attribute changes from a specific artifact
    * @throws OseeCoreException
    */
   public Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      ArrayList<Change> changes = new ArrayList<Change>();
      Branch branch = artifact.getBranch();
      ArrayList<TransactionRecord> transactionIds = new ArrayList<TransactionRecord>();
      recurseBranches(branch, artifact, transactionIds, TransactionManager.getLastTransaction(branch));

      for (TransactionRecord transactionId : transactionIds) {
         changes.addAll(getChanges(null, transactionId, monitor, artifact));
      }
      return changes;
   }

   private void recurseBranches(Branch branch, Artifact artifact, Collection<TransactionRecord> transactionIds, TransactionRecord transactionId) throws OseeCoreException {
      transactionIds.addAll(getTransactionsPerArtifact(branch, artifact, transactionId));

      if (branch.hasParentBranch() && !branch.getParentBranch().getBranchType().isSystemRootBranch()) {
         recurseBranches(branch.getParentBranch(), artifact, transactionIds, branch.getBaseTransaction());
      }
   }

   private Collection<TransactionRecord> getTransactionsPerArtifact(Branch branch, Artifact artifact, TransactionRecord transactionId) throws OseeCoreException {
      Set<TransactionRecord> transactionIds = new HashSet<TransactionRecord>();

      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(
               "SELECT /*+ ordered FIRST_ROWS */ td1.transaction_id from osee_tx_details td1, osee_txs tx1, osee_artifact_version av1 where td1.branch_id = ? and td1.transaction_id = tx1.transaction_id and td1.transaction_id <=? and tx1.gamma_id = av1.gamma_id and av1.art_id = ?",
               branch.getId(), transactionId.getId(), artifact.getArtId());

         while (chStmt.next()) {
            transactionIds.add(TransactionManager.getTransactionId(chStmt.getInt("transaction_id")));
         }

         chStmt.runPreparedQuery(
               "SELECT /*+ ordered FIRST_ROWS */ td1.transaction_id from osee_tx_details td1, osee_txs tx1, osee_relation_link rel where td1.branch_id = ? and td1.transaction_id = tx1.transaction_id and td1.transaction_id <=? and tx1.gamma_id = rel.gamma_id and (rel.a_art_id = ? or rel.b_art_id = ?)",
               branch.getId(), transactionId.getId(), artifact.getArtId(), artifact.getArtId());

         while (chStmt.next()) {
            transactionIds.add(TransactionManager.getTransactionId(chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }

      return transactionIds;
   }

   /**
    * Not Part of Change Report
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return
    * @throws OseeCoreException
    */
   private Collection<Change> getChanges(Branch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor, Artifact specificArtifact) throws OseeCoreException {
      @SuppressWarnings("unused")
      //This is so weak references do not get collected from bulk loading
      Collection<Artifact> bulkLoadedArtifacts;
      ArrayList<Change> changes = new ArrayList<Change>();
      ArrayList<ChangeBuilder> changeBuilders = new ArrayList<ChangeBuilder>();

      Set<Integer> artIds = new HashSet<Integer>();
      Set<Integer> newAndDeletedArtifactIds = new HashSet<Integer>();
      boolean historical = sourceBranch == null;

      monitor.beginTask("Find Changes", 100);

      ArtifactChangeAcquirer artifactChangeAcquirer =
            new ArtifactChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders,
                  newAndDeletedArtifactIds);
      changeBuilders = artifactChangeAcquirer.acquireChanges();

      AttributeChangeAcquirer attributeChangeAcquirer =
            new AttributeChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders,
                  newAndDeletedArtifactIds);
      changeBuilders = attributeChangeAcquirer.acquireChanges();

      RelationChangeAcquirer relationChangeAcquirer =
            new RelationChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders,
                  newAndDeletedArtifactIds);
      changeBuilders = relationChangeAcquirer.acquireChanges();

      monitor.subTask("Loading Artifacts from the Database");
      Branch branch = historical ? transactionId.getBranch() : sourceBranch;

      if (!artIds.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (int artId : artIds) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, branch.getId(),
                  historical ? transactionId.getId() : SQL3DataType.INTEGER});
         }
         bulkLoadedArtifacts =
               ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, true, historical, true);
      }

      //We build the changes after the artifact loader has been run so we can take advantage of bulk loading. 
      for (ChangeBuilder builder : changeBuilders) {
         changes.add(builder.build(branch));
      }

      monitor.done();
      return changes;
   }
}