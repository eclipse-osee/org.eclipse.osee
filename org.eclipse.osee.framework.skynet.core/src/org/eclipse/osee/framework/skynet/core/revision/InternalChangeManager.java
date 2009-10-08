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
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ErrorChange;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.commit.ArtifactChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.AttributeChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.LoadChangeDataOperation;
import org.eclipse.osee.framework.skynet.core.commit.RelationChangeItem;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
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

   protected InternalChangeManager() {
      super();
   }

   /**
    * @return Returns artifact, relation and attribute changes from a specific artifact
    * @throws OseeCoreException
    */
   public Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
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

      if (branch.hasParentBranch() && !branch.getParentBranch().getBranchType().isSystemRootBranch()) {
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

         chStmt.runPreparedQuery(
               "SELECT td1.transaction_id from osee_tx_details td1, osee_txs tx1, osee_relation_link rel where td1.branch_id = ? and td1.transaction_id = tx1.transaction_id and tx1.gamma_id = rel.gamma_id and (rel.a_art_id = ? or rel.b_art_id = ?)",
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
            insertParameters.add(new Object[] {queryId, insertTime, artId, branch.getBranchId(),
                  historical ? transactionId.getTransactionNumber() : SQL3DataType.INTEGER});
         }

         ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, true, historical, true);
      }

      //We build the changes after the artifact loader has been run so we can take advantage of bulk loading. 
      for (ChangeBuilder builder : changeBuilders) {
         changes.add(builder.build(branch));
      }

      monitor.done();
      return changes;
   }

   public Collection<Change> getChangesPerBranch(Branch sourceBranch, IProgressMonitor monitor) throws OseeCoreException {
      return getChangeReportChanges(sourceBranch, null, monitor);
   }

   public Collection<Change> getChangesPerTransaction(TransactionId transactionId, IProgressMonitor monitor) throws OseeCoreException {
      return getChangeReportChanges(null, transactionId, monitor);
   }

   private Collection<Change> getChangeReportChanges(Branch sourceBranch, TransactionId transactionId, IProgressMonitor monitor) throws OseeCoreException {
      boolean isHistorical = sourceBranch == null;
      ArrayList<Change> changes = new ArrayList<Change>();
      List<ChangeItem> changeItems = loadChangeItems(sourceBranch, transactionId, monitor, isHistorical);
      preloadArtifacts(changeItems, sourceBranch, transactionId, isHistorical, monitor);

      for (ChangeItem item : changeItems) {
         Change change = null;
         Branch branch = null;
         Artifact artifact = null;
         
         try {
            TransactionId toTransactionId =
                  TransactionIdManager.getTransactionId(item.getCurrent().getTransactionNumber().intValue());
            TransactionId fromTransactionId;
            String wasValue = "";
            branch = isHistorical ? transactionId.getBranch() : sourceBranch;

            if (isHistorical) {
               fromTransactionId = TransactionIdManager.getPriorTransaction(toTransactionId);
               artifact =  ArtifactQuery.getHistoricalArtifactFromId(item.getArtId(), transactionId, true);
               branch = transactionId.getBranch();
            } else {
               branch = sourceBranch;
               artifact = ArtifactQuery.getArtifactFromId(item.getArtId(), branch, true);

               if (item.getCurrent().getModType() == ModificationType.NEW || item.getNet().getModType() == ModificationType.NEW || item.getNet().getModType() == ModificationType.INTRODUCED) {
                  fromTransactionId = toTransactionId;
               } else {
                  if (item.getBase().isValid()) {
                     fromTransactionId = TransactionIdManager.getTransactionId(item.getBase().getTransactionNumber());
                     wasValue = item.getBase().getValue();
                  } else {
                     fromTransactionId = TransactionIdManager.getTransactionId(item.getFirst().getTransactionNumber());
                     wasValue = item.getFirst().getValue();
                  }
               }
            }

            monitor.subTask("Build Change Display Objects");
            //The artifacts have been previously bulk loaded for performance      
            change = asChange(item, artifact, branch, fromTransactionId, toTransactionId, wasValue, isHistorical);
         } catch (Exception ex) {
            change = new ErrorChange(branch, item.getArtId(), ex.toString());
         }
         changes.add(change);
      }
      monitor.done();
      return changes;
   }

   private Change asChange(ChangeItem item, Artifact artifact, Branch branch, TransactionId fromTransactionId, TransactionId toTransactionId, String wasValue, boolean isHistorical) throws OseeCoreException {
      Change change = null;
      if (item instanceof ArtifactChangeItem) {
         change =
               new ArtifactChange(branch, artifact.getArtifactType(), (int) item.getCurrent().getGammaId().longValue(),
                     item.getItemId(), toTransactionId, fromTransactionId, item.getNet().getModType(),
                     ChangeType.OUTGOING, isHistorical, artifact);
      } else if (item instanceof AttributeChangeItem) {
         change =
               new AttributeChange(branch, artifact.getArtifactType(),
                     (int) item.getCurrent().getGammaId().longValue(), item.getArtId(), toTransactionId,
                     fromTransactionId, item.getNet().getModType(), ChangeType.OUTGOING, item.getCurrent().getValue(),
                     wasValue, item.getItemId(),
                     artifact.getAttributeById(item.getItemId(), true).getAttributeType().getId(),
                     item.getNet().getModType(), isHistorical, artifact);
      } else if (item instanceof RelationChangeItem) {
         RelationChangeItem relationChangeItem = (RelationChangeItem) item;
         Artifact bArtifact = ArtifactQuery.getArtifactFromId(relationChangeItem.getBArtId(), branch, true);
         change =
               new RelationChange(branch, artifact.getArtifactType(),
                     (int) relationChangeItem.getCurrent().getGammaId().longValue(), item.getArtId(), toTransactionId,
                     fromTransactionId, relationChangeItem.getNet().getModType(), ChangeType.OUTGOING,
                     bArtifact.getArtId(), relationChangeItem.getItemId(), relationChangeItem.getRationale(),
                     RelationTypeManager.getType(relationChangeItem.getRelTypeId()), isHistorical, artifact, bArtifact);
      } else {
         throw new OseeCoreException("The change item must map to either a artifact, attribute or relation change");
      }
      return change;
   }

   private void preloadArtifacts(List<ChangeItem> changeItems, Branch sourceBranch, TransactionId transactionId, boolean isHistorical, IProgressMonitor monitor) throws OseeCoreException {
      if (!changeItems.isEmpty()) {
         monitor.subTask("Preload artifacts");
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
         Branch branch = isHistorical ? transactionId.getBranch() : sourceBranch;

         Set<Integer> artIds = new HashSet<Integer>();
         for (ChangeItem item : changeItems) {
            artIds.add(item.getArtId());

            if (item instanceof RelationChangeItem) {
               artIds.add(((RelationChangeItem) item).getBArtId());
            }
         }

         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (Integer artId : artIds) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, branch.getBranchId(),
                  isHistorical ? transactionId.getTransactionNumber() : SQL3DataType.INTEGER});
         }

         ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.ALL_CURRENT, null, insertParameters, true, isHistorical,
               true);
      }
   }

   /**
    * @param sourceBranch
    * @param monitor
    * @return
    * @throws OseeCoreException
    * @throws OseeWrappedException
    */
   private List<ChangeItem> loadChangeItems(Branch sourceBranch, TransactionId transactionId, IProgressMonitor monitor, boolean isHistorical) throws OseeCoreException, OseeWrappedException {
      List<ChangeItem> changeItems = new ArrayList<ChangeItem>();
      List<IOperation> ops = new ArrayList<IOperation>();

      if (isHistorical) {
         ops.add(new LoadChangeDataOperation(Integer.valueOf(transactionId.getTransactionNumber()), changeItems));
      } else {
         ops.add(new LoadChangeDataOperation(sourceBranch, changeItems));
      }
      ops.add(new ComputeNetChangeOperation(changeItems, false, isHistorical));

      String opName =
            String.format("Gathering changes for %s",
                  sourceBranch != null ? sourceBranch.getShortName() : transactionId);
      IOperation op = new CompositeOperation(opName, Activator.PLUGIN_ID, ops);
      Operations.executeWork(op, monitor, -1);
      try {
         Operations.checkForErrorStatus(op.getStatus());
      } catch (Exception ex) {
         if (ex instanceof OseeCoreException) {
            throw (OseeCoreException) ex;
         } else {
            throw new OseeWrappedException(ex);
         }
      }
      return changeItems;
   }

}