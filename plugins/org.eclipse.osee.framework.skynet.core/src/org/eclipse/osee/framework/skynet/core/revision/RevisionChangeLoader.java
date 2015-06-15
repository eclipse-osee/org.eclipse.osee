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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.change.RelationChangeBuilder;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.ArtifactChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.AttributeChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.revision.acquirer.RelationChangeAcquirer;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

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
    */
   public Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      return getChangesPerArtifact(artifact, monitor, LoadChangeType.artifact, LoadChangeType.attribute,
         LoadChangeType.relation);
   }

   /**
    * @return Returns artifact, relation and attribute changes from a specific artifact
    */
   private Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor, LoadChangeType... loadChangeTypes) throws OseeCoreException {
      Branch branch = artifact.getFullBranch();
      Set<TransactionRecord> transactionIds = new LinkedHashSet<TransactionRecord>();
      boolean recurseThroughBranchHierarchy = true;
      loadBranchTransactions(branch, artifact, transactionIds, TransactionManager.getHeadTransaction(branch),
         recurseThroughBranchHierarchy);

      Collection<Change> changes = new ArrayList<Change>();

      for (TransactionRecord transactionId : transactionIds) {
         loadChanges(null, transactionId, monitor, artifact, changes, loadChangeTypes);
      }
      return changes;
   }

   private void loadBranchTransactions(Branch branch, Artifact artifact, Set<TransactionRecord> transactionIds, TransactionRecord transactionId, boolean recurseThroughBranchHierarchy) throws OseeCoreException {
      loadTransactions(branch, artifact, transactionId, transactionIds);

      if (recurseThroughBranchHierarchy) {
         if (branch.hasParentBranch() && !branch.getParentBranch().getBranchType().isSystemRootBranch()) {
            loadBranchTransactions(branch.getParentBranch(), artifact, transactionIds, branch.getBaseTransaction(),
               recurseThroughBranchHierarchy);
         }
      }
   }

   private void loadTransactions(Branch branch, Artifact artifact, TransactionRecord transactionId, Set<TransactionRecord> transactionIds) throws OseeCoreException {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.LOAD_REVISION_HISTORY_TRANSACTION_ATTR),
            artifact.getArtId(), branch.getUuid(), transactionId.getId());

         while (chStmt.next()) {
            transactionIds.add(TransactionManager.getTransactionId(chStmt.getInt("transaction_id")));
         }

         chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.LOAD_REVISION_HISTORY_TRANSACTION_REL),
            artifact.getArtId(), artifact.getArtId(), branch.getUuid(), transactionId.getId());

         while (chStmt.next()) {
            transactionIds.add(TransactionManager.getTransactionId(chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
   }

   /**
    * Not Part of Change Report Acquires artifact, relation and attribute changes from a source branch since its
    * creation.
    */
   private void loadChanges(Branch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor, Artifact specificArtifact, Collection<Change> changes, LoadChangeType... loadChangeTypes) throws OseeCoreException {
      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      monitor.beginTask("Find Changes", 100);

      Set<Integer> artIds = new HashSet<Integer>();
      Set<Integer> newAndDeletedArtifactIds = new HashSet<Integer>();
      boolean isHistorical = sourceBranch == null;

      ArrayList<ChangeBuilder> changeBuilders = new ArrayList<ChangeBuilder>();
      for (LoadChangeType changeType : loadChangeTypes) {
         switch (changeType) {
            case artifact:
               ArtifactChangeAcquirer artifactChangeAcquirer =
               new ArtifactChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds,
                  changeBuilders, newAndDeletedArtifactIds);
               changeBuilders = artifactChangeAcquirer.acquireChanges();
               break;
            case attribute:
               AttributeChangeAcquirer attributeChangeAcquirer =
               new AttributeChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds,
                  changeBuilders, newAndDeletedArtifactIds);
               changeBuilders = attributeChangeAcquirer.acquireChanges();
               break;
            case relation:
               RelationChangeAcquirer relationChangeAcquirer =
               new RelationChangeAcquirer(sourceBranch, transactionId, monitor, specificArtifact, artIds,
                  changeBuilders, newAndDeletedArtifactIds);

               changeBuilders = relationChangeAcquirer.acquireChanges();
               break;
            default:
               break;
         }
      }
      monitor.subTask("Loading Artifacts from the Database");

      IOseeBranch branch = isHistorical ? transactionId.getBranch() : sourceBranch;

      Collection<Change> changesLoaded = getChanges(branch, isHistorical, changeBuilders);
      changes.addAll(changesLoaded);

      monitor.done();
   }

   private CompositeKeyHashMap<TransactionRecord, Integer, Artifact> getBulkLoadedArtifacts(IOseeBranch branch, boolean isHistorical, List<ChangeBuilder> changeBuilders) throws OseeCoreException {
      HashCollection<TransactionRecord, Integer> loadMap =
         new HashCollection<TransactionRecord, Integer>(false, HashSet.class);
      for (ChangeBuilder builder : changeBuilders) {
         TransactionRecord endTx = builder.getTxDelta().getEndTx();
         loadMap.put(endTx, builder.getArtId());
         if (builder instanceof RelationChangeBuilder) {
            RelationChangeBuilder relBuilder = (RelationChangeBuilder) builder;
            loadMap.put(endTx, relBuilder.getbArtId());
         }
      }

      CompositeKeyHashMap<TransactionRecord, Integer, Artifact> loadedMap =
         new CompositeKeyHashMap<TransactionRecord, Integer, Artifact>();

      for (Entry<TransactionRecord, Collection<Integer>> entry : loadMap.entrySet()) {
         Collection<Artifact> artifacts;
         if (isHistorical) {
            artifacts =
               ArtifactQuery.getHistoricalArtifactListFromIds(entry.getValue(), entry.getKey(),
                  DeletionFlag.INCLUDE_DELETED);
         } else {
            artifacts = ArtifactQuery.getArtifactListFromIds(entry.getValue(), branch, DeletionFlag.INCLUDE_DELETED);
         }
         for (Artifact artifact : artifacts) {
            loadedMap.put(entry.getKey(), artifact.getArtId(), artifact);
         }
      }
      return loadedMap;
   }

   private Collection<Change> getChanges(IOseeBranch branch, boolean isHistorical, List<ChangeBuilder> changeBuilders) throws OseeCoreException {
      CompositeKeyHashMap<TransactionRecord, Integer, Artifact> loadedMap =
         getBulkLoadedArtifacts(branch, isHistorical, changeBuilders);

      Collection<Change> changes = new ArrayList<Change>();
      for (ChangeBuilder builder : changeBuilders) {
         Change toReturn = null;
         Artifact changeArtifact = loadedMap.get(builder.getTxDelta().getEndTx(), builder.getArtId());

         if (changeArtifact != null) {
            ArtifactDelta delta = new ArtifactDelta(builder.getTxDelta(), changeArtifact, null);
            if (builder instanceof ArtifactChangeBuilder) {
               toReturn =
                  new ArtifactChange(branch, builder.getSourceGamma(), builder.getArtId(), builder.getTxDelta(),
                     builder.getModType(), isHistorical, changeArtifact, delta);
            } else if (builder instanceof AttributeChangeBuilder) {
               AttributeChangeBuilder attrBuilder = (AttributeChangeBuilder) builder;
               toReturn =
                  new AttributeChange(branch, attrBuilder.getSourceGamma(), attrBuilder.getArtId(),
                     attrBuilder.getTxDelta(), attrBuilder.getModType(), attrBuilder.getIsValue(),
                     attrBuilder.getWasValue(), attrBuilder.getAttrId(), attrBuilder.getAttributeType(),
                     attrBuilder.getArtModType(), isHistorical, changeArtifact, delta);
            } else if (builder instanceof RelationChangeBuilder) {
               RelationChangeBuilder relBuilder = (RelationChangeBuilder) builder;
               Artifact bArtifact = loadedMap.get(builder.getTxDelta().getEndTx(), relBuilder.getbArtId());
               toReturn =
                  new RelationChange(branch, builder.getSourceGamma(), builder.getArtId(), builder.getTxDelta(),
                     builder.getModType(), relBuilder.getbArtId(), relBuilder.getRelLinkId(),
                     relBuilder.getRationale(), relBuilder.getRelationType(), isHistorical, changeArtifact, delta,
                     bArtifact);
            }
         } else {
            toReturn =
               new ArtifactChange(branch, builder.getSourceGamma(), builder.getArtId(), builder.getTxDelta(),
                  builder.getModType(), isHistorical, null, null);
         }
         changes.add(toReturn);
      }
      return changes;
   }
}