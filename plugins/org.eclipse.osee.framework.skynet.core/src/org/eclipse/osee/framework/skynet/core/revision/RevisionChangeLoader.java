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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
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
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdComparator;
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
   public Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor) {
      return getChangesPerArtifact(artifact, monitor, LoadChangeType.artifact, LoadChangeType.attribute,
         LoadChangeType.relation);
   }

   /**
    * @return Returns artifact, relation and attribute changes from a specific artifact
    */
   public Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor, LoadChangeType... loadChangeTypes) {
      BranchId branch = artifact.getBranch();
      Set<TransactionToken> transactionIds = new LinkedHashSet<>();
      boolean recurseThroughBranchHierarchy = true;
      loadBranchTransactions(branch, artifact, transactionIds, TransactionManager.getHeadTransaction(branch),
         recurseThroughBranchHierarchy);

      Collection<Change> changes = new ArrayList<>();

      for (TransactionToken transactionId : transactionIds) {
         loadChanges(null, transactionId, monitor, artifact, changes, loadChangeTypes);
      }
      return changes;
   }

   public Collection<? extends Change> getChangesPerArtifact(Artifact artifact, int numberTransactionsToShow, IProgressMonitor monitor) {
      BranchId branch = artifact.getBranch();
      Set<TransactionToken> transactionIds = new LinkedHashSet<>();
      boolean recurseThroughBranchHierarchy = true;
      loadBranchTransactions(branch, artifact, transactionIds, TransactionManager.getHeadTransaction(branch),
         recurseThroughBranchHierarchy);

      Collection<Change> changes = new ArrayList<>();
      List<TransactionToken> sortedTransIds = new ArrayList<>();
      sortedTransIds.addAll(transactionIds);
      Collections.sort(sortedTransIds, new TransactionIdComparator());
      Collections.reverse(sortedTransIds);

      int count = 0;
      for (TransactionToken transactionId : sortedTransIds) {
         loadChanges(null, transactionId, monitor, artifact, changes, LoadChangeType.artifact, LoadChangeType.attribute,
            LoadChangeType.relation);
         count++;
         if (count >= numberTransactionsToShow) {
            break;
         }
      }
      return changes;
   }

   private void loadBranchTransactions(BranchId branch, Artifact artifact, Set<TransactionToken> transactionIds, TransactionId transactionId, boolean recurseThroughBranchHierarchy) {
      loadTransactions(branch, artifact, transactionId, transactionIds);

      if (recurseThroughBranchHierarchy) {
         BranchId parentBranch = BranchManager.getParentBranch(branch);
         TransactionRecord baseTx = BranchManager.getBaseTransaction(branch);

         if (!BranchManager.isParentSystemRoot(branch)) {
            loadBranchTransactions(parentBranch, artifact, transactionIds, baseTx, recurseThroughBranchHierarchy);
         }
      }
   }

   private void loadTransactions(BranchId branch, Artifact artifact, TransactionId transactionId, Set<TransactionToken> transactionIds) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.LOAD_REVISION_HISTORY_TRANSACTION_ATTR), artifact, branch,
            transactionId);

         while (chStmt.next()) {
            transactionIds.add(TransactionToken.valueOf(chStmt.getLong("transaction_id"), branch));
         }

         chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.LOAD_REVISION_HISTORY_TRANSACTION_REL), artifact, artifact,
            branch, transactionId);

         while (chStmt.next()) {
            transactionIds.add(TransactionToken.valueOf(chStmt.getLong("transaction_id"), branch));
         }
      } finally {
         chStmt.close();
      }
   }

   /**
    * Not Part of Change Report Acquires artifact, relation and attribute changes from a source branch since its
    * creation.
    */
   private void loadChanges(BranchId sourceBranch, TransactionToken transactionId, IProgressMonitor monitor, Artifact specificArtifact, Collection<Change> changes, LoadChangeType... loadChangeTypes) {
      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      monitor.beginTask("Find Changes", 100);

      Set<Integer> artIds = new HashSet<>();
      Set<Integer> newAndDeletedArtifactIds = new HashSet<>();
      boolean isHistorical = sourceBranch == null;

      ArrayList<ChangeBuilder> changeBuilders = new ArrayList<>();
      for (LoadChangeType changeType : loadChangeTypes) {
         switch (changeType) {
            case artifact:
               ArtifactChangeAcquirer artifactChangeAcquirer = new ArtifactChangeAcquirer(sourceBranch, transactionId,
                  monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
               changeBuilders = artifactChangeAcquirer.acquireChanges();
               break;
            case attribute:
               AttributeChangeAcquirer attributeChangeAcquirer = new AttributeChangeAcquirer(sourceBranch,
                  transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
               changeBuilders = attributeChangeAcquirer.acquireChanges();
               break;
            case relation:
               RelationChangeAcquirer relationChangeAcquirer = new RelationChangeAcquirer(sourceBranch, transactionId,
                  monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);

               changeBuilders = relationChangeAcquirer.acquireChanges();
               break;
            default:
               break;
         }
      }
      monitor.subTask("Loading Artifacts from the Database");

      BranchId branch = isHistorical ? transactionId.getBranch() : sourceBranch;

      Collection<Change> changesLoaded = getChanges(branch, isHistorical, changeBuilders);
      changes.addAll(changesLoaded);

      monitor.done();
   }

   private CompositeKeyHashMap<TransactionToken, ArtifactId, Artifact> getBulkLoadedArtifacts(BranchId branch, boolean isHistorical, List<ChangeBuilder> changeBuilders) {
      HashCollectionSet<TransactionToken, ArtifactId> loadMap = new HashCollectionSet<>(HashSet::new);
      for (ChangeBuilder builder : changeBuilders) {
         TransactionToken endTx = builder.getTxDelta().getEndTx();
         loadMap.put(endTx, builder.getArtId());
         if (builder instanceof RelationChangeBuilder) {
            RelationChangeBuilder relBuilder = (RelationChangeBuilder) builder;
            loadMap.put(endTx, relBuilder.getbArtId());
         }
      }

      CompositeKeyHashMap<TransactionToken, ArtifactId, Artifact> loadedMap = new CompositeKeyHashMap<>();

      for (Entry<TransactionToken, Set<ArtifactId>> entry : loadMap.entrySet()) {
         Collection<Artifact> artifacts;
         if (isHistorical) {
            artifacts = ArtifactQuery.getHistoricalArtifactListFromIds(entry.getValue(), entry.getKey(),
               DeletionFlag.INCLUDE_DELETED);
         } else {
            artifacts = ArtifactQuery.getArtifactListFrom(entry.getValue(), branch);
         }
         for (Artifact artifact : artifacts) {
            loadedMap.put(entry.getKey(), artifact, artifact);
         }
      }
      return loadedMap;
   }

   private Collection<Change> getChanges(BranchId branch, boolean isHistorical, List<ChangeBuilder> changeBuilders) {
      CompositeKeyHashMap<TransactionToken, ArtifactId, Artifact> loadedMap =
         getBulkLoadedArtifacts(branch, isHistorical, changeBuilders);

      Collection<Change> changes = new ArrayList<>();
      for (ChangeBuilder builder : changeBuilders) {
         Change toReturn = null;
         Artifact changeArtifact = loadedMap.get(builder.getTxDelta().getEndTx(), builder.getArtId());

         if (changeArtifact != null) {
            ArtifactDelta delta = new ArtifactDelta(builder.getTxDelta(), changeArtifact, null);
            if (builder instanceof ArtifactChangeBuilder) {
               toReturn = new ArtifactChange(branch, builder.getSourceGamma(), builder.getArtId(), builder.getTxDelta(),
                  builder.getModType(), "", "", isHistorical, changeArtifact, delta);
            } else if (builder instanceof AttributeChangeBuilder) {
               AttributeChangeBuilder attrBuilder = (AttributeChangeBuilder) builder;
               toReturn = new AttributeChange(branch, attrBuilder.getSourceGamma(), attrBuilder.getArtId(),
                  attrBuilder.getTxDelta(), attrBuilder.getModType(), attrBuilder.getIsValue(), attrBuilder.getIsUri(),
                  attrBuilder.getWasValue(), attrBuilder.getWasUri(), AttributeId.valueOf(attrBuilder.getAttrId()),
                  attrBuilder.getAttributeType(), attrBuilder.getArtModType(), isHistorical, changeArtifact, delta);
            } else if (builder instanceof RelationChangeBuilder) {
               RelationChangeBuilder relBuilder = (RelationChangeBuilder) builder;
               Artifact bArtifact = loadedMap.get(builder.getTxDelta().getEndTx(), relBuilder.getbArtId());
               toReturn = new RelationChange(branch, builder.getSourceGamma(), builder.getArtId(), builder.getTxDelta(),
                  builder.getModType(), relBuilder.getbArtId(),
                  RelationId.valueOf(Long.valueOf(relBuilder.getRelLinkId())), relBuilder.getRationale(), "",
                  relBuilder.getRelationType(), isHistorical, changeArtifact, delta, bArtifact);
            }
         } else {
            toReturn = new ArtifactChange(branch, builder.getSourceGamma(), builder.getArtId(), builder.getTxDelta(),
               builder.getModType(), "", "", isHistorical, Artifact.SENTINEL, null);
         }
         changes.add(toReturn);
      }
      return changes;
   }

}