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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.DirectSoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Category;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_CHILD;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.TupleDataFactory;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;
import org.eclipse.osee.orcs.core.internal.transaction.TxData.TxState;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link TxCallableFactory}
 *
 * @author Megumi Telles
 */
public class TxDataManagerTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private OrcsSession session;

   @Mock private ExternalArtifactManager proxyManager;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private QueryFactory queryFactory;
   @Mock private RelationManager relationManager;
   @Mock private TxDataLoader loader;
   @Mock private TupleDataFactory tupleFactory;

   @Mock private TxData txData;

   @Mock private GraphData graph;
   @Mock private ArtifactReadable readable1;
   @Mock private ArtifactReadable readable2;
   @Mock private ArtifactReadable readable3;
   @Mock private Artifact artifact1;
   @Mock private Artifact artifact2;
   @Mock private Artifact artifact3;
   @Mock private RelationNodeAdjacencies adjacencies;
   @Captor private ArgumentCaptor<Collection<ArtifactId>> idCaptor;
   @Captor private ArgumentCaptor<List<? extends RelationNode>> nodeCaptor;
   // @formatter:on

   private TxDataManager txDataManager;
   private TxData txDataReal;
   private final Collection<AttributeTypeId> types = Arrays.asList(Name, Category);
   private String r1Guid;
   private String r2Guid;
   private String r3Guid;
   private ArtifactId artifactId1;
   private ArtifactId artifactId2;
   private ArtifactId artifactId3;

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
      txDataManager = new TxDataManager(proxyManager, artifactFactory, relationManager, tupleFactory, loader);

      when(artifact1.getExistingAttributeTypes()).thenAnswer(answerValue(types));

      when(proxyManager.asInternalArtifact(readable1)).thenReturn(artifact1);
      when(proxyManager.asExternalArtifact(session, artifact1)).thenReturn(readable1);

      when(txData.getSession()).thenReturn(session);
      when(txData.getGraph()).thenReturn(graph);

      when(graph.getBranch()).thenReturn(COMMON);
      txDataReal = new TxData(session, graph);

      r1Guid = GUID.create();
      r2Guid = GUID.create();
      r3Guid = GUID.create();

      Long id1 = Lib.generateUuid();
      Long id2 = Lib.generateUuid();
      Long id3 = Lib.generateUuid();

      artifactId1 = ArtifactToken.valueOf(id1, r1Guid, "", COMMON, null);
      artifactId2 = ArtifactToken.valueOf(id2, r2Guid, "", COMMON, null);
      artifactId3 = ArtifactToken.valueOf(id3, r3Guid, "", COMMON, null);

      when(readable1.getGuid()).thenReturn(r1Guid);
      when(readable2.getGuid()).thenReturn(r2Guid);
      when(readable3.getGuid()).thenReturn(r3Guid);

      when(readable1.getBranch()).thenReturn(COMMON);

      when(artifact1.getGuid()).thenReturn(r1Guid);
      when(artifact2.getGuid()).thenReturn(r2Guid);
      when(artifact3.getGuid()).thenReturn(r3Guid);

      when(artifact1.getTransaction()).thenReturn(TransactionId.SENTINEL);
      when(graph.getTransaction()).thenReturn(TransactionId.SENTINEL);
   }

   @Test
   public void testCreateTxData() throws OseeCoreException {
      TxData newData = txDataManager.createTxData(session, COMMON);
      assertNotNull(newData);
   }

   @Test
   public void testTxCommitSuccess() {
      List<Relation> empty = Collections.emptyList();

      txDataReal.add(artifact1);
      when(graph.getAdjacencies(artifact1)).thenReturn(adjacencies);
      when(adjacencies.getDirties()).thenReturn(empty);

      txDataManager.txCommitSuccess(txDataReal);
      assertEquals(TxState.COMMITTED, txDataReal.getTxState());
   }

   @Test
   public void testRollbackTx() {
      txDataManager.rollbackTx(txData);

      verify(txData).setTxState(TxState.COMMIT_FAILED);
   }

   @Test
   public void testStartTx() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);

      txDataManager.startTx(txData);

      verify(txData).setCommitInProgress(true);
      verify(txData).setTxState(TxState.COMMIT_STARTED);
   }

   @Test
   public void testStartTxCommitInProgress() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(true);

      thrown.expect(OseeCoreException.class);
      thrown.expectMessage("Commit is already in progress");

      txDataManager.startTx(txData);
   }

   @Test
   public void testEndTx() {
      txDataManager.endTx(txData);

      verify(txData).setCommitInProgress(false);
   }

   @Test
   public void testGetForWriteId() throws OseeCoreException {
      when(txData.getWriteable(artifactId1)).thenReturn(null);

      ResultSet<Artifact> loaded = ResultSets.singleton(artifact1);
      when(loader.loadArtifacts(eq(session), eq(graph), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      Artifact actual = txDataManager.getForWrite(txData, artifactId1);

      verify(txData).getWriteable(artifactId1);
      verify(loader).loadArtifacts(eq(session), eq(graph), idCaptor.capture());

      assertEquals(artifactId1, idCaptor.getValue().iterator().next());
      assertEquals(artifact1, actual);
   }

   @Test
   public void testGetForWriteReadable() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(null);
      when(artifactFactory.clone(session, artifact1)).thenReturn(artifact2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);
      when(txData.isOnSameBranch(artifact1)).thenReturn(true);

      Artifact actual = txDataManager.getForWrite(txData, readable1);

      verify(txData).getWriteable(readable1);
      verify(proxyManager).asInternalArtifact(readable1);
      verify(artifactFactory).clone(session, artifact1);

      assertEquals(artifact2, actual);
   }

   @Test
   public void testGetForWriteReadableButIsFromDifferentBranch() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(null);
      when(artifactFactory.clone(session, artifact1)).thenReturn(artifact2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);
      when(txData.isOnSameBranch(artifact1)).thenReturn(true);

      Artifact actual = txDataManager.getForWrite(txData, readable1);

      verify(txData).getWriteable(readable1);
      verify(proxyManager).asInternalArtifact(readable1);

      assertEquals(artifact2, actual);
   }

   @Test
   public void testGetForWriteArtifact() throws OseeCoreException {
      when(txData.getWriteable(artifact1)).thenReturn(null);
      when(artifactFactory.clone(session, artifact1)).thenReturn(artifact2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);
      when(txData.isOnSameBranch(artifact1)).thenReturn(true);

      Artifact actual = txDataManager.getForWrite(txData, artifact1);

      verify(txData).getWriteable(artifact1);
      verify(artifactFactory).clone(session, artifact1);

      assertEquals(artifact2, actual);
   }

   @Test
   public void testGetForWriteArtifactButIsFromDifferentBranch() throws OseeCoreException {
      ResultSet<Artifact> loaded = ResultSets.singleton(artifact2);
      when(loader.loadArtifacts(eq(session), eq(graph), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);
      when(artifact1.getTransaction()).thenReturn(TransactionId.valueOf(3));

      Artifact actual = txDataManager.getForWrite(txDataReal, artifact1);

      verify(loader).loadArtifacts(eq(session), eq(graph), idCaptor.capture());

      assertEquals(artifact1, idCaptor.getValue().iterator().next());
      assertEquals(artifact2, actual);
   }

   @Test
   public void testGetForWriteMultiples() throws OseeCoreException {
      List<? extends ArtifactId> ids = Arrays.asList(artifactId1, artifactId2, artifactId3);

      when(txData.getWriteable(artifactId2)).thenReturn(artifact2);

      List<Artifact> artifacts = Arrays.asList(artifact1, artifact3);
      ResultSet<Artifact> loaded = ResultSets.newResultSet(artifacts);
      when(loader.loadArtifacts(eq(session), eq(graph), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(artifact3.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      Iterable<Artifact> actual = txDataManager.getForWrite(txData, ids);

      verify(loader).loadArtifacts(eq(session), eq(graph), idCaptor.capture());
      Iterator<ArtifactId> iterator1 = idCaptor.getValue().iterator();
      assertEquals(artifactId1, iterator1.next());
      assertEquals(artifactId3, iterator1.next());

      Iterator<Artifact> iterator2 = actual.iterator();
      assertEquals(artifact1, iterator2.next());
      assertEquals(artifact2, iterator2.next());
      assertEquals(artifact3, iterator2.next());
   }

   @Test
   public void testGetForWriteDuringWrite() throws OseeCoreException {
      when(txData.add(artifact1)).thenReturn(artifact3);
      when(artifact3.notEqual(artifact1)).thenReturn(true);
      when(artifactFactory.clone(session, artifact1)).thenReturn(artifact1);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);
      when(txData.isOnSameBranch(artifact1)).thenReturn(true);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Another instance of writeable detected - writeable tracking would be inconsistent");
      txDataManager.getForWrite(txData, readable1);
   }

   @Test
   public void testSetComment() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);

      txDataManager.setComment(txData, "this is a comment");
      verify(txData).setComment("this is a comment");
   }

   @Test
   public void testSetCommentChangesNotAllowed() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(true);

      thrown.expect(OseeStateException.class);
      thrown.expectMessage("Changes are not allowed - [COMMIT_IN_PROGRESS]");
      txDataManager.setComment(txData, "trying to commit");
   }

   @Test
   public void testSetAuthor() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);

      txDataManager.setAuthor(txData, readable1);

      verify(txData).setAuthor(readable1);
   }

   @Test
   public void testCreateArtifact() throws OseeCoreException {
      when(artifactFactory.createArtifact(session, COMMON, DirectSoftwareRequirement, null)).thenReturn(artifact1);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      ArtifactReadable actual =
         txDataManager.createArtifact(txDataReal, DirectSoftwareRequirement, "Direct SW requirement", null);

      verify(artifactFactory).createArtifact(session, COMMON, DirectSoftwareRequirement, null);
      assertEquals(readable1, actual);
   }

   @Test
   public void testCopyExisitingArtifact() throws OseeCoreException {
      txDataReal.add(artifact1);

      when(artifactFactory.copyArtifact(session, artifact1, types, COMMON)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      ArtifactReadable actual = txDataManager.copyArtifact(txDataReal, COMMON, readable1);

      verify(artifactFactory).copyArtifact(session, artifact1, types, COMMON);
      verify(proxyManager).asExternalArtifact(session, artifact2);

      assertEquals(readable2, actual);
   }

   @Test
   public void testCopyReadableArtifact() throws OseeCoreException {
      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      when(artifactFactory.copyArtifact(session, artifact1, types, COMMON)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txDataReal, COMMON, readable1);

      verify(proxyManager).asInternalArtifact(readable1);
      verify(artifactFactory).copyArtifact(session, artifact1, types, COMMON);
      verify(proxyManager).asExternalArtifact(session, artifact2);

      assertEquals(readable2, actual);
   }

   @Test
   public void testCopyArtifact() throws OseeCoreException {
      String guid = GUID.create();

      ArtifactData data = Mockito.mock(ArtifactData.class);
      VersionData version = Mockito.mock(VersionData.class);
      when(data.getVersion()).thenReturn(version);
      when(version.getBranch()).thenReturn(COMMON);

      Artifact sourceArtifact = Mockito.spy(new ArtifactImpl(null, data, null));

      when(data.getGuid()).thenReturn(guid);

      List<AttributeTypeId> copyTypes = Arrays.asList(CoreAttributeTypes.Active, CoreAttributeTypes.Name);
      when(sourceArtifact.getExistingAttributeTypes()).thenAnswer(answerValue(copyTypes));

      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      when(artifactFactory.copyArtifact(session, sourceArtifact, copyTypes, COMMON)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txDataReal, COMMON, sourceArtifact);

      verify(artifactFactory).copyArtifact(session, sourceArtifact, copyTypes, COMMON);

      assertEquals(readable2, actual);
   }

   @Test
   public void testCopyArtifactId() throws OseeCoreException {
      txDataReal.add(artifact1);

      when(artifactFactory.copyArtifact(session, artifact1, types, COMMON)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      ArtifactReadable actual = txDataManager.copyArtifact(txDataReal, COMMON, artifactId1);

      verify(artifactFactory).copyArtifact(session, artifact1, types, COMMON);
      verify(proxyManager).asExternalArtifact(session, artifact2);

      assertEquals(readable2, actual);
   }

   @Test
   public void testIntroduceArtifact() throws OseeCoreException {
      ResultSet<Artifact> loaded = ResultSets.singleton(artifact1);
      when(loader.loadArtifacts(eq(session), eq(COMMON), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);
      when(artifactFactory.introduceArtifact(session, artifact1, artifact1, COMMON)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);
      when(artifact1.getGraph()).thenReturn(graph);
      when(graph.getAdjacencies(artifact1)).thenReturn(adjacencies);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      ArtifactReadable actual = txDataManager.introduceArtifact(txDataReal, COMMON, readable1, readable2);

      verify(artifactFactory).introduceArtifact(session, artifact1, artifact1, COMMON);
      verify(proxyManager).asExternalArtifact(session, artifact1);
      assertEquals(readable1, actual);
   }

   @Test
   public void testDeleteArtifact() throws OseeCoreException {
      when(artifactFactory.clone(session, artifact1)).thenReturn(artifact2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);
      when(txData.isOnSameBranch(artifact1)).thenReturn(true);

      txDataManager.deleteArtifact(txData, artifact1);

      verify(artifact2).delete();
      verify(relationManager).unrelateFromAll(session, artifact2);
   }

   @Test
   public void testCreateChangeData() throws OseeCoreException {
      Iterable<Artifact> writeables = Arrays.asList(artifact1);
      Iterable<TupleData> tuples = Collections.emptySet();
      when(txData.getAllWriteables()).thenReturn(writeables);
      when(txData.getAllTuples()).thenReturn(tuples);
      when(artifact1.isDirty()).thenReturn(true);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      TransactionData changeData = txDataManager.createChangeData(txData);
      assertNotNull(changeData);
   }

   @Test
   public void testSetRationale() throws OseeCoreException {
      String rationale = "i have no rationale";

      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact3);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact3.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      txDataManager.setRationale(txData, readable1, DEFAULT_HIERARCHY, readable2, rationale);

      verify(relationManager).setRationale(session, artifact1, DEFAULT_HIERARCHY, artifact3, rationale);
   }

   @Test
   public void testRelate() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      txDataManager.relate(txData, readable1, DEFAULT_HIERARCHY, readable2);

      verify(relationManager).relate(session, artifact1, DEFAULT_HIERARCHY, artifact2);
   }

   @Test
   public void testRelateWithOrder() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      txDataManager.relate(txData, readable1, DEFAULT_HIERARCHY, readable2, LEXICOGRAPHICAL_DESC);

      verify(relationManager).relate(session, artifact1, DEFAULT_HIERARCHY, artifact2, LEXICOGRAPHICAL_DESC);
   }

   @Test
   public void testAddChildren() throws OseeCoreException {
      List<? extends ArtifactReadable> children = Arrays.asList(readable2, readable3);

      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);
      when(txData.getWriteable(readable3)).thenReturn(artifact3);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(artifact3.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      txDataManager.addChildren(txData, readable1, children);

      verify(relationManager).addChildren(eq(session), eq(artifact1), nodeCaptor.capture());

      Iterator<? extends RelationNode> iterator = nodeCaptor.getValue().iterator();
      assertEquals(artifact2, iterator.next());
      assertEquals(artifact3, iterator.next());
   }

   @Test
   public void testSetRelations() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);
      when(txData.getWriteable(readable3)).thenReturn(artifact3);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(artifact3.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      ResultSet<Artifact> related = ResultSets.singleton(artifact3);
      when(relationManager.<Artifact> getRelated(session, DEFAULT_HIERARCHY, artifact1, SIDE_A)).thenReturn(related);
      when(txData.getWriteable(artifact3)).thenReturn(artifact3);

      txDataManager.setRelations(txData, readable1, DEFAULT_HIERARCHY, Arrays.asList(readable2));

      verify(relationManager).relate(session, artifact1, DEFAULT_HIERARCHY, artifact2);
      verify(relationManager).unrelate(session, artifact1, DEFAULT_HIERARCHY, artifact3);
   }

   @Test
   public void testUnrelate() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(artifact2.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      txDataManager.unrelate(txData, readable1, DEFAULT_HIERARCHY, readable2);

      verify(relationManager).unrelate(session, artifact1, DEFAULT_HIERARCHY, artifact2);
   }

   @Test
   public void testUnrelateTypeFromAll() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      txDataManager.unrelateFromAll(txData, DEFAULT_HIERARCHY, readable1, IS_CHILD);

      verify(relationManager).unrelateFromAll(session, DEFAULT_HIERARCHY, artifact1, IS_CHILD);
   }

   @Test
   public void testUnrelateFromAll() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);

      ArtifactData data = Mockito.mock(ArtifactData.class);
      when(artifact1.getOrcsData()).thenReturn(data);
      when(data.isExistingVersionUsed()).thenReturn(false);

      txDataManager.unrelateFromAll(txData, readable1);

      verify(relationManager).unrelateFromAll(session, artifact1);
   }

   private <T> Answer<T> answerValue(final T value) {
      return new Answer<T>() {

         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return value;
         }
      };
   }
}
