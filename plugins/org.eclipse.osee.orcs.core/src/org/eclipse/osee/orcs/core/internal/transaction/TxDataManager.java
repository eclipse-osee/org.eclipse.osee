/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import static java.util.Collections.singleton;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;
import org.eclipse.osee.orcs.core.internal.transaction.TxData.TxState;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class TxDataManager {

   public interface TxDataLoader {

      GraphData createGraph(OrcsSession session, Long branch) throws OseeCoreException;

      ResultSet<Artifact> loadArtifacts(OrcsSession session, Long branchId, Collection<ArtifactId> artifactIds) throws OseeCoreException;

      ResultSet<Artifact> loadArtifacts(OrcsSession session, GraphData graph, Collection<ArtifactId> singleton) throws OseeCoreException;

   }

   private final ExternalArtifactManager proxyManager;
   private final ArtifactFactory artifactFactory;
   private final RelationManager relationManager;
   private final TxDataLoader loader;

   public TxDataManager(ExternalArtifactManager proxyManager, ArtifactFactory artifactFactory, RelationManager relationManager, TxDataLoader loader) {
      this.proxyManager = proxyManager;
      this.artifactFactory = artifactFactory;
      this.relationManager = relationManager;
      this.loader = loader;
   }

   public TxData createTxData(OrcsSession session, Long branch) throws OseeCoreException {
      GraphData graphData = loader.createGraph(session, branch);
      return new TxData(session, graphData);
   }

   public void txCommitSuccess(TxData txData) {
      GraphData graph = txData.getGraph();
      for (Artifact writeable : txData.getAllWriteables()) {
         writeable.setNotDirty();
         RelationNodeAdjacencies adjacencies = graph.getAdjacencies(writeable);
         for (Relation relation : adjacencies.getDirties()) {
            relation.clearDirty();
         }
      }
      txData.setTxState(TxState.COMMITTED);
   }

   public void rollbackTx(TxData txData) {
      txData.setTxState(TxState.COMMIT_FAILED);
   }

   public void startTx(TxData txData) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(txData.isCommitInProgress(), "Commit is already in progress");
      txData.setCommitInProgress(true);
      txData.setTxState(TxState.COMMIT_STARTED);
   }

   public void endTx(TxData txData) {
      txData.setCommitInProgress(false);
   }

   public Iterable<Artifact> getForWrite(TxData txData, Iterable<? extends ArtifactId> ids) throws OseeCoreException {
      checkChangesAllowed(txData);
      Set<ArtifactId> toLoad = new LinkedHashSet<>();

      LinkedHashMap<String, Artifact> items = new LinkedHashMap<>();
      for (ArtifactId artifactId : ids) {
         Artifact node = findArtifactLocallyForWrite(txData, artifactId);
         if (node == null) {
            toLoad.add(artifactId);
         } else {
            checkAndAdd(txData, node);
         }
         items.put(artifactId.getGuid(), node);
      }
      if (!toLoad.isEmpty()) {
         Iterable<Artifact> result = loader.loadArtifacts(txData.getSession(), txData.getGraph(), toLoad);
         for (Artifact node : result) {
            items.put(node.getGuid(), node);
            checkAndAdd(txData, node);
         }
      }
      return items.values();
   }

   public Artifact getForWrite(TxData txData, ArtifactId artifactId) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact node = findArtifactLocallyForWrite(txData, artifactId);
      if (node == null) {
         ResultSet<Artifact> result =
            loader.loadArtifacts(txData.getSession(), txData.getGraph(), singleton(artifactId));
         node = result.getExactlyOne();
      }
      checkAndAdd(txData, node);
      return node;
   }

   private Artifact findArtifactLocallyForWrite(TxData txData, ArtifactId artifactId) throws OseeCoreException {
      Artifact node = txData.getWriteable(artifactId);
      if (node == null) {
         Artifact source = null;
         if (artifactId instanceof Artifact) {
            source = (Artifact) artifactId;
         } else if (artifactId instanceof ArtifactReadable) {
            ArtifactReadable readable = (ArtifactReadable) artifactId;
            source = proxyManager.asInternalArtifact(readable);
         }
         if (isFromSameStripe(txData, source) && includesDeletedData(source)) {
            node = copyArtifactForWrite(txData, source);
         }
      }
      return node;
   }

   private boolean isFromSameStripe(TxData txData, Artifact artifact) {
      boolean result = false;
      if (txData.isOnSameBranch(artifact)) {
         int txId = txData.getGraph().getTransaction();
         if (txId == artifact.getTransaction()) {
            result = true;
         }
      }
      return result;
   }

   private boolean includesDeletedData(Artifact artifact) {
      boolean result = false;
      if (artifact != null) {
         result = true;
         // Check load options
      }
      return result;
   }

   private Artifact copyArtifactForWrite(TxData txData, Artifact source) throws OseeCoreException {
      Artifact artifact = artifactFactory.clone(txData.getSession(), source);
      txData.getGraph().addNode(artifact, artifact.getOrcsData().isUseBackingData());
      relationManager.cloneRelations(txData.getSession(), source, artifact);
      return artifact;
   }

   private Artifact getSourceArtifact(TxData txData, Long fromBranchId, ArtifactId artifactId) throws OseeCoreException {
      Artifact source = null;
      if (txData.isOnBranch(fromBranchId)) {
         source = txData.getWriteable(artifactId);
      }

      if (source == null) {
         Artifact artifactSrc = null;
         if (artifactId instanceof Artifact) {
            Artifact external = (Artifact) artifactId;
            if (fromBranchId.equals(external.getBranchId())) {
               artifactSrc = external;
            }
         } else if (artifactId instanceof ArtifactReadable) {
            ArtifactReadable external = (ArtifactReadable) artifactId;
            if (fromBranchId.equals(external.getBranchId())) {
               artifactSrc = proxyManager.asInternalArtifact(external);
            }
         }

         if (includesDeletedData(artifactSrc)) {
            source = artifactSrc;
         }
      }
      if (source == null) {
         ResultSet<Artifact> loadArtifacts =
            loader.loadArtifacts(txData.getSession(), fromBranchId, singleton(artifactId));
         source = loadArtifacts.getExactlyOne();
      }
      return source;
   }

   private void checkChangesAllowed(TxData txData) throws OseeCoreException {
      String errorMessage = "";
      if (txData.isCommitInProgress() || TxState.COMMIT_STARTED == txData.getTxState()) {
         errorMessage = "Changes are not allowed - [COMMIT_IN_PROGRESS]";
      }
      if (Strings.isValid(errorMessage)) {
         throw new OseeStateException(errorMessage);
      }
   }

   public void setComment(TxData txData, String comment) throws OseeCoreException {
      checkChangesAllowed(txData);
      txData.setComment(comment);
   }

   public void setAuthor(TxData txData, ArtifactReadable author) throws OseeCoreException {
      checkChangesAllowed(txData);
      txData.setAuthor(author);
   }

   public ArtifactReadable createArtifact(TxData txData, IArtifactType artifactType, String name, String guid) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact artifact = artifactFactory.createArtifact(txData.getSession(), txData.getBranchId(), artifactType, guid);
      artifact.setName(name);
      return asExternalArtifact(txData, artifact);
   }

   public ArtifactReadable createArtifact(TxData txData, IArtifactType artifactType, String name, String guid, long uuid) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact artifact =
         artifactFactory.createArtifact(txData.getSession(), txData.getBranchId(), artifactType, guid, uuid);
      artifact.setName(name);
      return asExternalArtifact(txData, artifact);
   }

   public ArtifactReadable copyArtifact(TxData txData, Long fromBranchId, ArtifactId artifactId) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact source = getSourceArtifact(txData, fromBranchId, artifactId);
      return copyArtifactHelper(txData, source, source.getExistingAttributeTypes());
   }

   public ArtifactReadable copyArtifact(TxData txData, Long fromBranchId, ArtifactId artifactId, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact source = getSourceArtifact(txData, fromBranchId, artifactId);
      return copyArtifactHelper(txData, source, attributesToDuplicate);
   }

   private ArtifactReadable copyArtifactHelper(TxData txData, Artifact source, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException {
      Artifact copy =
         artifactFactory.copyArtifact(txData.getSession(), source, attributesToDuplicate, txData.getBranchId());
      return asExternalArtifact(txData, copy);
   }

   public ArtifactReadable introduceArtifact(TxData txData, Long fromBranchId, ArtifactReadable source, ArtifactReadable destination) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact src = getSourceArtifact(txData, fromBranchId, source);
      Artifact dest = null;
      if (destination == null) {
         dest = artifactFactory.createArtifact(txData.getSession(), txData.getBranchId(), src.getArtifactType(),
            src.getGuid());
         dest.setGraph(loader.createGraph(txData.getSession(), txData.getBranchId()));
      } else {
         dest = getSourceArtifact(txData, fromBranchId, destination);
      }
      artifactFactory.introduceArtifact(txData.getSession(), src, dest, txData.getBranchId());
      relationManager.introduce(txData.getSession(), txData.getBranchId(), src, dest);
      addAdjacencies(txData, dest);
      return asExternalArtifact(txData, dest);
   }

   public ArtifactReadable replaceWithVersion(TxData txData, Long fromBranchId, ArtifactReadable readable, ArtifactReadable destination) throws OseeCoreException {
      return introduceArtifact(txData, fromBranchId, readable, destination);
   }

   private void addAdjacencies(TxData txData, Artifact dest) {
      GraphData destGraph = ((RelationNode) dest).getGraph();
      RelationNodeAdjacencies adjacencies = destGraph.getAdjacencies(dest);
      GraphData graph = txData.getGraph();
      if (adjacencies != null) {
         for (Relation rel : adjacencies.getAll()) {
            graph.addAdjacencies(rel.getLocalIdForSide(RelationSide.SIDE_A), adjacencies);
         }
      }
   }

   private ArtifactReadable asExternalArtifact(TxData txData, Artifact artifact) throws OseeCoreException {
      checkAndAdd(txData, artifact);
      ArtifactReadable readable = txData.getReadable(artifact);
      if (readable == null) {
         readable = proxyManager.asExternalArtifact(txData.getSession(), artifact);
         txData.add(readable);
      }
      return readable;
   }

   private void checkAndAdd(TxData txData, Artifact artifact) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact oldArtifact = txData.add(artifact);
      boolean isDifferent = oldArtifact != null && oldArtifact != artifact;
      Conditions.checkExpressionFailOnTrue(isDifferent,
         "Another instance of writeable detected - writeable tracking would be inconsistent");

      txData.getGraph().addNode(artifact, artifact.getOrcsData().isUseBackingData());
   }

   public void deleteArtifact(TxData txData, ArtifactId sourceArtifact) throws OseeCoreException {
      Artifact asArtifact = getForWrite(txData, sourceArtifact);
      relationManager.unrelateFromAll(txData.getSession(), asArtifact);
      asArtifact.delete();
   }

   public void addChildren(TxData txData, ArtifactId artA, Iterable<? extends ArtifactId> children) throws OseeCoreException {
      OrcsSession session = txData.getSession();
      Artifact asArtifact = getForWrite(txData, artA);
      Iterable<? extends RelationNode> artifacts = getForWrite(txData, children);
      List<RelationNode> nodes = Lists.newLinkedList(artifacts);
      relationManager.addChildren(session, asArtifact, nodes);
   }

   public void relate(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.relate(txData.getSession(), asArtifactA, type, asArtifactB);
   }

   public void relate(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB, String rationale) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.relate(txData.getSession(), asArtifactA, type, asArtifactB, rationale);
   }

   public void relate(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB, IRelationSorterId sortType) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.relate(txData.getSession(), asArtifactA, type, asArtifactB, sortType);
   }

   public void relate(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB, String rationale, IRelationSorterId sortType) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.relate(txData.getSession(), asArtifactA, type, asArtifactB, rationale, sortType);
   }

   public void setRelations(TxData txData, ArtifactId artA, IRelationType type, Iterable<? extends ArtifactId> artBs) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      Set<Artifact> asArtifactBs = Sets.newLinkedHashSet(getForWrite(txData, artBs));

      OrcsSession session = txData.getSession();
      ResultSet<Artifact> related = relationManager.getRelated(session, type, asArtifactA, RelationSide.SIDE_A);
      Set<Artifact> relatedArtBs = Sets.newLinkedHashSet(getForWrite(txData, related));

      // Add relations if they are not related
      for (Artifact asArtifactB : Sets.difference(asArtifactBs, relatedArtBs)) {
         relationManager.relate(session, asArtifactA, type, asArtifactB);
      }

      // Remove relations that are not in items to relate
      for (Artifact asArtifactB : Sets.difference(relatedArtBs, asArtifactBs)) {
         relationManager.unrelate(session, asArtifactA, type, asArtifactB);
      }
   }

   public void setRationale(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB, String rationale) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.setRationale(txData.getSession(), asArtifactA, type, asArtifactB, rationale);
   }

   public void unrelate(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.unrelate(txData.getSession(), asArtifactA, type, asArtifactB);
   }

   public void unrelateFromAll(TxData txData, ArtifactId artA) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      relationManager.unrelateFromAll(txData.getSession(), asArtifactA);
   }

   public void unrelateFromAll(TxData txData, IRelationType type, ArtifactId artA, RelationSide side) throws OseeCoreException {
      Artifact asArtifactA = getForWrite(txData, artA);
      relationManager.unrelateFromAll(txData.getSession(), type, asArtifactA, side);
   }

   public TransactionData createChangeData(TxData txData) throws OseeCoreException {
      OrcsSession session = txData.getSession();
      GraphData graph = txData.getGraph();

      ChangeSetBuilder builder = new ChangeSetBuilder();
      for (Artifact artifact : txData.getAllWriteables()) {
         artifact.accept(builder);
         relationManager.accept(session, graph, artifact, builder);
      }
      OrcsChangeSet changeSet = builder.getChangeSet();
      return new TransactionDataImpl(txData.getBranchId(), txData.getAuthor(), txData.getComment(), changeSet);
   }

   private static final class TransactionDataImpl implements TransactionData {

      private final Long branchId;
      private final ArtifactReadable author;
      private final String comment;
      private final OrcsChangeSet changeSet;

      public TransactionDataImpl(Long branchId, ArtifactReadable author, String comment, OrcsChangeSet changeSet) {
         super();
         this.branchId = branchId;
         this.author = author;
         this.comment = comment;
         this.changeSet = changeSet;
      }

      @Override
      public Long getBranchId() {
         return branchId;
      }

      @Override
      public ArtifactReadable getAuthor() {
         return author;
      }

      @Override
      public String getComment() {
         return comment;
      }

      @Override
      public OrcsChangeSet getChangeSet() {
         return changeSet;
      }

      @Override
      public String toString() {
         return "TransactionDataImpl [branch=" + branchId + ", author=" + author + ", comment=" + comment + ", changeSet=" + changeSet + "]";
      }

   }

}
