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
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.TupleDataFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;
import org.eclipse.osee.orcs.core.internal.transaction.TxData.TxState;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class TxDataManager {

   public interface TxDataLoader {

      GraphData createGraph(OrcsSession session, BranchId branch);

      ResultSet<Artifact> loadArtifacts(OrcsSession session, BranchId branch, Collection<ArtifactId> artifactIds);

      ResultSet<Artifact> loadArtifacts(OrcsSession session, GraphData graph, Collection<ArtifactId> singleton);

   }

   private final ExternalArtifactManager proxyManager;
   private final ArtifactFactory artifactFactory;
   private final RelationManager relationManager;
   private final TupleDataFactory tupleFactory;
   private final TxDataLoader loader;

   public TxDataManager(ExternalArtifactManager proxyManager, ArtifactFactory artifactFactory, RelationManager relationManager, TupleDataFactory tupleFactory, TxDataLoader loader) {
      this.proxyManager = proxyManager;
      this.artifactFactory = artifactFactory;
      this.relationManager = relationManager;
      this.loader = loader;
      this.tupleFactory = tupleFactory;
   }

   public TxData createTxData(OrcsSession session, BranchId branch) {
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

   public void startTx(TxData txData) {
      Conditions.checkExpressionFailOnTrue(txData.isCommitInProgress(), "Commit is already in progress");
      txData.setCommitInProgress(true);
      txData.setTxState(TxState.COMMIT_STARTED);
   }

   public void endTx(TxData txData) {
      txData.setCommitInProgress(false);
   }

   public Iterable<Artifact> getForWrite(TxData txData, Iterable<? extends ArtifactId> ids) {
      checkChangesAllowed(txData);
      Set<ArtifactId> toLoad = new LinkedHashSet<>();

      LinkedHashMap<Long, Artifact> items = new LinkedHashMap<>();
      for (ArtifactId artifactId : ids) {
         Artifact node = findArtifactLocallyForWrite(txData, artifactId);
         if (node == null) {
            toLoad.add(artifactId);
         } else {
            checkAndAdd(txData, node);
         }
         items.put(artifactId.getId(), node);
      }
      if (!toLoad.isEmpty()) {
         Iterable<Artifact> result = loader.loadArtifacts(txData.getSession(), txData.getGraph(), toLoad);
         for (Artifact node : result) {
            items.put(node.getId(), node);
            checkAndAdd(txData, node);
         }
      }
      return items.values();
   }

   public Artifact getForWrite(TxData txData, ArtifactId artifactId) {
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

   private Artifact findArtifactLocallyForWrite(TxData txData, ArtifactId artifactId) {
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
      if (artifact == null) {
         return false;
      }
      return artifact.getTransaction().equals(txData.getGraph().getTransaction());
   }

   private boolean includesDeletedData(Artifact artifact) {
      boolean result = false;
      if (artifact != null) {
         result = true;
         // Check load options
      }
      return result;
   }

   private Artifact copyArtifactForWrite(TxData txData, Artifact source) {
      Artifact artifact = artifactFactory.clone(txData.getSession(), source);
      txData.getGraph().addNode(artifact, artifact.getOrcsData().isExistingVersionUsed());
      relationManager.cloneRelations(txData.getSession(), source, artifact);
      return artifact;
   }

   private Artifact getSourceArtifact(TxData txData, BranchId fromBranch, ArtifactId artifactId) {
      Artifact source = null;
      if (txData.isOnBranch(fromBranch)) {
         source = txData.getWriteable(artifactId);
      }

      if (source == null) {
         Artifact artifactSrc = null;
         if (artifactId instanceof Artifact) {
            Artifact external = (Artifact) artifactId;
            if (fromBranch.equals(external.getBranch())) {
               artifactSrc = external;
            }
         } else if (artifactId instanceof ArtifactReadable) {
            ArtifactReadable external = (ArtifactReadable) artifactId;
            if (fromBranch.equals(external.getBranch())) {
               artifactSrc = proxyManager.asInternalArtifact(external);
            }
         }

         if (includesDeletedData(artifactSrc)) {
            source = artifactSrc;
         }
      }
      if (source == null) {
         ResultSet<Artifact> loadArtifacts =
            loader.loadArtifacts(txData.getSession(), fromBranch, singleton(artifactId));
         source = loadArtifacts.getExactlyOne();
      }
      return source;
   }

   private void checkChangesAllowed(TxData txData) {
      String errorMessage = "";
      if (txData.isCommitInProgress() || TxState.COMMIT_STARTED == txData.getTxState()) {
         errorMessage = "Changes are not allowed - [COMMIT_IN_PROGRESS]";
      }
      if (Strings.isValid(errorMessage)) {
         throw new OseeStateException(errorMessage);
      }
   }

   public void setComment(TxData txData, String comment) {
      checkChangesAllowed(txData);
      txData.setComment(comment);
   }

   public void setAuthor(TxData txData, UserId author) {
      checkChangesAllowed(txData);
      txData.setAuthor(author);
   }

   public GammaId createTuple2(TxData txData, Tuple2Type<?, ?> tupleType, Long e1, Long e2) {
      TupleData tuple = tupleFactory.createTuple2Data(tupleType, txData.getBranch(), e1, e2);
      txData.add(tuple);
      return tuple.getVersion().getGammaId();
   }

   public GammaId createTuple3(TxData txData, Tuple3Type<?, ?, ?> tupleType, Long e1, Long e2, Long e3) {
      TupleData tuple = tupleFactory.createTuple3Data(tupleType, txData.getBranch(), e1, e2, e3);
      txData.add(tuple);
      return tuple.getVersion().getGammaId();
   }

   public GammaId createTuple4(TxData txData, Tuple4Type<?, ?, ?, ?> tupleType, Long e1, Long e2, Long e3, Long e4) {
      TupleData tuple = tupleFactory.createTuple4Data(tupleType, txData.getBranch(), e1, e2, e3, e4);
      txData.add(tuple);
      return tuple.getVersion().getGammaId();
   }

   public ArtifactReadable createArtifact(TxData txData, IArtifactType artifactType, String name, String guid) {
      checkChangesAllowed(txData);
      Artifact artifact = artifactFactory.createArtifact(txData.getSession(), txData.getBranch(), artifactType, guid);
      artifact.setName(name);
      return asExternalArtifact(txData, artifact);
   }

   public ArtifactReadable createArtifact(TxData txData, IArtifactType artifactType, String name, Long artifactId, String guid) {
      checkChangesAllowed(txData);
      Artifact artifact =
         artifactFactory.createArtifact(txData.getSession(), txData.getBranch(), artifactType, guid, artifactId);
      artifact.setName(name);
      return asExternalArtifact(txData, artifact);
   }

   public ArtifactReadable createArtifact(TxData txData, IArtifactType artifactType, String name, long uuid) {
      checkChangesAllowed(txData);
      Artifact artifact = artifactFactory.createArtifact(txData.getSession(), txData.getBranch(), artifactType, uuid);
      artifact.setName(name);
      return asExternalArtifact(txData, artifact);
   }

   public ArtifactReadable copyArtifact(TxData txData, BranchId fromBranch, ArtifactId artifactId) {
      checkChangesAllowed(txData);
      Artifact source = getSourceArtifact(txData, fromBranch, artifactId);
      return copyArtifactHelper(txData, source, source.getExistingAttributeTypes());
   }

   public ArtifactReadable copyArtifact(TxData txData, BranchId fromBranch, ArtifactId artifactId, Collection<AttributeTypeId> attributesToDuplicate) {
      checkChangesAllowed(txData);
      Artifact source = getSourceArtifact(txData, fromBranch, artifactId);
      return copyArtifactHelper(txData, source, attributesToDuplicate);
   }

   private ArtifactReadable copyArtifactHelper(TxData txData, Artifact source, Collection<? extends AttributeTypeId> attributesToDuplicate) {
      Artifact copy =
         artifactFactory.copyArtifact(txData.getSession(), source, attributesToDuplicate, txData.getBranch());
      return asExternalArtifact(txData, copy);
   }

   public ArtifactReadable introduceArtifact(TxData txData, BranchId fromBranch, ArtifactReadable source, ArtifactReadable destination) {
      checkChangesAllowed(txData);
      Artifact src = getSourceArtifact(txData, fromBranch, source);
      Artifact dest = null;
      if (destination == null) {
         dest = artifactFactory.createArtifact(txData.getSession(), txData.getBranch(), src.getArtifactTypeId(),
            src.getGuid());
         dest.setGraph(loader.createGraph(txData.getSession(), txData.getBranch()));
      } else {
         dest = getSourceArtifact(txData, fromBranch, destination);
      }
      artifactFactory.introduceArtifact(txData.getSession(), src, dest, txData.getBranch());
      relationManager.introduce(txData.getSession(), txData.getBranch(), src, dest);
      addAdjacencies(txData, dest);
      return asExternalArtifact(txData, dest);
   }

   public ArtifactReadable replaceWithVersion(TxData txData, BranchId fromBranch, ArtifactReadable readable, ArtifactReadable destination) {
      return introduceArtifact(txData, fromBranch, readable, destination);
   }

   private void addAdjacencies(TxData txData, Artifact dest) {
      GraphData destGraph = dest.getGraph();
      RelationNodeAdjacencies adjacencies = destGraph.getAdjacencies(dest);
      GraphData graph = txData.getGraph();
      if (adjacencies != null) {
         for (Relation rel : adjacencies.getAll()) {
            graph.addAdjacencies(rel.getIdForSide(RelationSide.SIDE_A), adjacencies);
         }
      }
   }

   private ArtifactReadable asExternalArtifact(TxData txData, Artifact artifact) {
      checkAndAdd(txData, artifact);
      ArtifactReadable readable = txData.getReadable(artifact);
      if (readable == null) {
         readable = proxyManager.asExternalArtifact(txData.getSession(), artifact);
         txData.add(readable);
      }
      return readable;
   }

   private void checkAndAdd(TxData txData, Artifact artifact) {
      checkChangesAllowed(txData);
      Artifact oldArtifact = txData.add(artifact);
      boolean isDifferent = oldArtifact != null && oldArtifact.notEqual(artifact);
      Conditions.checkExpressionFailOnTrue(isDifferent,
         "Another instance of writeable detected - writeable tracking would be inconsistent");

      txData.getGraph().addNode(artifact, artifact.getOrcsData().isExistingVersionUsed());
   }

   public void deleteArtifact(TxData txData, ArtifactId sourceArtifact) {
      Artifact asArtifact = getForWrite(txData, sourceArtifact);
      relationManager.unrelateFromAll(txData.getSession(), asArtifact);
      asArtifact.delete();
   }

   public void addChild(TxData txData, ArtifactId parent, ArtifactId child) {
      OrcsSession session = txData.getSession();
      Artifact asArtifact = getForWrite(txData, parent);
      relationManager.addChild(session, asArtifact, getForWrite(txData, child));
   }

   public void relate(TxData txData, ArtifactId artA, RelationTypeToken type, ArtifactId artB) {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.relate(txData.getSession(), asArtifactA, type, asArtifactB);
   }

   public void relate(TxData txData, ArtifactId artA, RelationTypeToken type, ArtifactId artB, String rationale) {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.relate(txData.getSession(), asArtifactA, type, asArtifactB, rationale);
   }

   public void relate(TxData txData, ArtifactId artA, RelationTypeToken type, ArtifactId artB, RelationSorter sortType) {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.relate(txData.getSession(), asArtifactA, type, asArtifactB, sortType);
   }

   public void relate(TxData txData, ArtifactId artA, RelationTypeToken type, ArtifactId artB, String rationale, RelationSorter sortType) {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.relate(txData.getSession(), asArtifactA, type, asArtifactB, rationale, sortType);
   }

   public void setRelations(TxData txData, ArtifactId artA, RelationTypeToken type, Iterable<? extends ArtifactId> artBs) {
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

   public void setRelationsAndOrder(TxData txData, ArtifactId artA, RelationTypeSide relationSide, List<? extends ArtifactId> artBs) {
      setRelations(txData, artA, relationSide, artBs);
      Artifact asArtifactA = getForWrite(txData, artA);
      List<Artifact> asArtifactBs = new LinkedList<>();
      for (ArtifactId artB : artBs) {
         asArtifactBs.add(getForWrite(txData, artB));
      }

      relationManager.order(txData.getSession(), asArtifactA, relationSide, relationSide.getSide(), asArtifactBs);
   }

   public void setRationale(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB, String rationale) {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.setRationale(txData.getSession(), asArtifactA, type, asArtifactB, rationale);
   }

   public void unrelate(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB) {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.unrelate(txData.getSession(), asArtifactA, type, asArtifactB);
   }

   public void unrelateFromAll(TxData txData, ArtifactId artA) {
      Artifact asArtifactA = getForWrite(txData, artA);
      relationManager.unrelateFromAll(txData.getSession(), asArtifactA);
   }

   public void unrelateFromAll(TxData txData, IRelationType type, ArtifactId artA, RelationSide side) {
      Artifact asArtifactA = getForWrite(txData, artA);
      relationManager.unrelateFromAll(txData.getSession(), type, asArtifactA, side);
   }

   public void setRelationApplicabilityId(TxData txData, ArtifactId artA, IRelationType type, ArtifactId artB, ApplicabilityId applicId) {
      Artifact asArtifactA = getForWrite(txData, artA);
      Artifact asArtifactB = getForWrite(txData, artB);
      relationManager.setApplicabilityId(txData.getSession(), asArtifactA, type, asArtifactB, applicId);
   }

   public TransactionData createChangeData(TxData txData) {
      OrcsSession session = txData.getSession();
      GraphData graph = txData.getGraph();

      ChangeSetBuilder builder = new ChangeSetBuilder();
      for (Artifact artifact : txData.getAllWriteables()) {
         artifact.accept(builder);
         relationManager.accept(session, graph, artifact, builder);
      }
      for (TupleData tuple : txData.getAllTuples()) {
         tuple.accept(builder);
      }

      OrcsChangeSet changeSet = builder.getChangeSet();
      return new TransactionDataImpl(txData.getBranch(), txData.getAuthor(), txData.getComment(), changeSet);
   }

   public void setApplicabilityId(TxData txData, ArtifactId artId, ApplicabilityId applicId) {
      Artifact asArtifactA = getForWrite(txData, artId);
      asArtifactA.getOrcsData().setApplicabilityId(applicId);
   }

   private static final class TransactionDataImpl implements TransactionData {

      private final BranchId branch;
      private final UserId author;
      private final String comment;
      private final OrcsChangeSet changeSet;

      public TransactionDataImpl(BranchId branch, UserId author, String comment, OrcsChangeSet changeSet) {
         this.branch = branch;
         this.author = author;
         this.comment = comment;
         this.changeSet = changeSet;
      }

      @Override
      public BranchId getBranch() {
         return branch;
      }

      @Override
      public UserId getAuthor() {
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
         return "TransactionDataImpl [branch=" + branch + ", author=" + author + ", comment=" + comment + ", changeSet=" + changeSet + "]";
      }

   }

}
