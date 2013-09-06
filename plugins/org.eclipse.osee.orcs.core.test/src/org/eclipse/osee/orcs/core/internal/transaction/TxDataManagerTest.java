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
import static org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.orcs.core.internal.relation.RelationUtil.DEFAULT_HIERARCHY;
import static org.eclipse.osee.orcs.core.internal.relation.RelationUtil.IS_CHILD;
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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.transaction.TxData.TxState;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;
import org.eclipse.osee.orcs.core.internal.util.ResultSetIterable;
import org.eclipse.osee.orcs.core.internal.util.ValueProvider;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
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
   @Mock private IOseeBranch branch;
   
   @Mock private ExternalArtifactManager proxyManager;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private RelationManager relationManager;
   @Mock private TxDataLoader loader;
   
   @Mock private TxData txData;
   @Mock private GraphData graph;
   @Mock private ArtifactId artifactId1;
   @Mock private ArtifactId artifactId2;
   @Mock private ArtifactId artifactId3;
   @Mock private ArtifactReadable readable1;
   @Mock private ArtifactReadable readable2;
   @Mock private ArtifactReadable readable3;
   @Mock private Artifact artifact1;
   @Mock private Artifact artifact2;
   @Mock private Artifact artifact3;
   @Captor private ArgumentCaptor<Collection<ArtifactId>> idCaptor;
   @Captor private ArgumentCaptor<List<? extends RelationNode>> nodeCaptor;
   // @formatter:on

   private TxDataManager txDataManager;
   private String guid;
   private final Collection<? extends IAttributeType> types = Arrays.asList(Name, Category);
   private String r1Guid;
   private String r2Guid;
   private String r3Guid;

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
      txDataManager = new TxDataManager(proxyManager, artifactFactory, relationManager, loader);

      guid = GUID.create();

      when(artifact1.getExistingAttributeTypes()).thenAnswer(answerValue(types));

      when(proxyManager.asInternalArtifact(readable1)).thenReturn(artifact1);
      when(proxyManager.asExternalArtifact(session, artifact1)).thenReturn(readable1);

      when(txData.getSession()).thenReturn(session);
      when(txData.getBranch()).thenReturn(branch);
      when(txData.getGraph()).thenReturn(graph);

      r1Guid = GUID.create();
      r2Guid = GUID.create();
      r3Guid = GUID.create();

      when(artifactId1.getGuid()).thenReturn(r1Guid);
      when(artifactId2.getGuid()).thenReturn(r2Guid);
      when(artifactId3.getGuid()).thenReturn(r3Guid);

      when(readable1.getGuid()).thenReturn(r1Guid);
      when(readable2.getGuid()).thenReturn(r2Guid);
      when(readable3.getGuid()).thenReturn(r3Guid);

      when(readable1.getBranch()).thenReturn(branch);
      when(readable2.getBranch()).thenReturn(branch);
      when(readable3.getBranch()).thenReturn(branch);

      when(artifact1.getGuid()).thenReturn(r1Guid);
      when(artifact2.getGuid()).thenReturn(r2Guid);
      when(artifact3.getGuid()).thenReturn(r3Guid);

      when(artifact1.getBranch()).thenReturn(branch);
      when(artifact2.getBranch()).thenReturn(branch);
      when(artifact3.getBranch()).thenReturn(branch);
   }

   @Test
   public void testCreateTxData() throws OseeCoreException {
      TxData newData = txDataManager.createTxData(session, branch);
      assertNotNull(newData);
   }

   @Test
   public void testTxCommitSuccess() {
      Iterable<Artifact> writeables = Arrays.asList(artifact1);
      when(txData.getAllWriteables()).thenReturn(writeables);

      txDataManager.txCommitSuccess(txData);
      verify(txData).setTxState(TxState.COMMITTED);
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

      ResultSet<Artifact> loaded = new ResultSetIterable<Artifact>(Collections.<Artifact> singleton(artifact1));
      when(loader.loadArtifacts(eq(session), eq(graph), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);

      Artifact actual = txDataManager.getForWrite(txData, artifactId1);

      verify(txData).getWriteable(artifactId1);
      verify(loader).loadArtifacts(eq(session), eq(graph), idCaptor.capture());

      assertEquals(artifactId1, idCaptor.getValue().iterator().next());
      assertEquals(artifact1, actual);
   }

   @Test
   public void testGetForWriteReadable() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(null);
      when(proxyManager.asInternalArtifact(readable1)).thenReturn(artifact1);
      when(artifactFactory.clone(artifact1)).thenReturn(artifact2);

      Artifact actual = txDataManager.getForWrite(txData, readable1);

      verify(txData).getWriteable(readable1);
      verify(proxyManager).asInternalArtifact(readable1);
      verify(artifactFactory).clone(artifact1);

      assertEquals(artifact2, actual);
   }

   @Test
   public void testGetForWriteReadableButIsFromDifferentBranch() throws OseeCoreException {
      when(readable1.getBranch()).thenReturn(CoreBranches.COMMON);
      when(txData.getWriteable(readable1)).thenReturn(null);

      ResultSet<Artifact> loaded = new ResultSetIterable<Artifact>(Collections.<Artifact> singleton(artifact1));
      when(loader.loadArtifacts(eq(session), eq(graph), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);

      Artifact actual = txDataManager.getForWrite(txData, readable1);

      verify(txData).getWriteable(readable1);
      verify(readable1).getBranch();
      verify(loader).loadArtifacts(eq(session), eq(graph), idCaptor.capture());

      assertEquals(readable1, idCaptor.getValue().iterator().next());
      assertEquals(artifact1, actual);
   }

   @Test
   public void testGetForWriteArtifact() throws OseeCoreException {
      when(txData.getWriteable(artifact1)).thenReturn(null);
      when(artifactFactory.clone(artifact1)).thenReturn(artifact2);

      Artifact actual = txDataManager.getForWrite(txData, artifact1);

      verify(txData).getWriteable(artifact1);
      verify(artifactFactory).clone(artifact1);

      assertEquals(artifact2, actual);
   }

   @Test
   public void testGetForWriteArtifactButIsFromDifferentBranch() throws OseeCoreException {
      when(artifact1.getBranch()).thenReturn(CoreBranches.COMMON);
      when(txData.getWriteable(readable1)).thenReturn(null);

      ResultSet<Artifact> loaded = new ResultSetIterable<Artifact>(Collections.<Artifact> singleton(artifact2));
      when(loader.loadArtifacts(eq(session), eq(graph), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);

      Artifact actual = txDataManager.getForWrite(txData, artifact1);

      verify(txData).getWriteable(artifact1);
      verify(artifact1).getBranch();
      verify(loader).loadArtifacts(eq(session), eq(graph), idCaptor.capture());

      assertEquals(artifact1, idCaptor.getValue().iterator().next());
      assertEquals(artifact2, actual);
   }

   @Test
   public void testGetForWriteMultiples() throws OseeCoreException {
      List<? extends ArtifactId> ids = Arrays.asList(artifactId1, artifactId2, artifactId3);

      when(txData.getWriteable(artifactId2)).thenReturn(artifact2);

      List<Artifact> artifacts = Arrays.asList(artifact1, artifact3);
      ResultSet<Artifact> loaded = new ResultSetList<Artifact>(artifacts);
      when(loader.loadArtifacts(eq(session), eq(graph), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);

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
      when(artifactFactory.clone(artifact1)).thenReturn(artifact1);

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
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(artifactFactory.createArtifact(branch, DirectSoftwareRequirement, guid)).thenReturn(artifact1);
      when(proxyManager.asExternalArtifact(session, artifact1)).thenReturn(readable1);

      ArtifactReadable actual =
         txDataManager.createArtifact(txData, DirectSoftwareRequirement, "Direct SW requirement", guid);

      verify(artifactFactory).createArtifact(branch, DirectSoftwareRequirement, guid);
      assertEquals(readable1, actual);
   }

   @Test
   public void testCopyExisitingArtifact() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(artifactFactory.copyArtifact(artifact1, types, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txData, branch, readable1);

      verify(txData).getWriteable(readable1);
      verify(artifactFactory).copyArtifact(artifact1, types, branch);
      verify(proxyManager).asExternalArtifact(session, artifact2);

      assertEquals(readable2, actual);
   }

   @Test
   public void testCopyReadableArtifact() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(readable1.getBranch()).thenReturn(branch);
      when(txData.getWriteable(readable1)).thenReturn(null);
      when(proxyManager.asInternalArtifact(readable1)).thenReturn(artifact1);

      when(artifactFactory.copyArtifact(artifact1, types, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txData, branch, readable1);

      verify(txData).getWriteable(readable1);
      verify(proxyManager).asInternalArtifact(readable1);
      verify(artifactFactory).copyArtifact(artifact1, types, branch);
      verify(proxyManager).asExternalArtifact(session, artifact2);

      assertEquals(readable2, actual);
   }

   @Test
   public void testCopyArtifact() throws OseeCoreException {
      String guid = GUID.create();

      ArtifactData data = Mockito.mock(ArtifactData.class);

      @SuppressWarnings("unchecked")
      ValueProvider<IOseeBranch, OrcsData> provider = Mockito.mock(ValueProvider.class);

      Artifact sourceArtifact = Mockito.spy(new ArtifactImpl(null, data, null, provider));

      when(data.getGuid()).thenReturn(guid);
      when(provider.get()).thenReturn(branch);

      List<? extends IAttributeType> copyTypes = Arrays.asList(CoreAttributeTypes.Active, CoreAttributeTypes.Name);
      when(sourceArtifact.getExistingAttributeTypes()).thenAnswer(answerValue(copyTypes));

      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(txData.getWriteable(sourceArtifact)).thenReturn(null);
      when(artifactFactory.copyArtifact(sourceArtifact, copyTypes, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txData, branch, sourceArtifact);

      verify(txData).getWriteable(sourceArtifact);
      verify(artifactFactory).copyArtifact(sourceArtifact, copyTypes, branch);

      assertEquals(readable2, actual);
   }

   @Test
   public void testCopyArtifactId() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(txData.getWriteable(artifactId1)).thenReturn(artifact1);
      when(artifact1.getExistingAttributeTypes()).thenAnswer(answerValue(types));
      when(artifactFactory.copyArtifact(artifact1, types, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txData, branch, artifactId1);

      verify(txData).getWriteable(artifactId1);
      verify(artifactFactory).copyArtifact(artifact1, types, branch);
      verify(proxyManager).asExternalArtifact(session, artifact2);

      assertEquals(readable2, actual);
   }

   @Test
   public void testIntroduceArtifactExceptionSameBranch() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(txData.getBranch()).thenReturn(branch);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(String.format("Source branch is same branch as transaction branch[%s]", branch));
      txDataManager.introduceArtifact(txData, branch, artifactId1);
   }

   @Test
   public void testIntroduceArtifact() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(txData.getBranch()).thenReturn(branch);

      ResultSet<Artifact> loaded = new ResultSetIterable<Artifact>(Collections.<Artifact> singleton(artifact1));
      when(loader.loadArtifacts(eq(session), eq(COMMON), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);
      when(artifactFactory.introduceArtifact(artifact1, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.introduceArtifact(txData, COMMON, artifactId1);

      verify(loader).loadArtifacts(eq(session), eq(COMMON), idCaptor.capture());
      assertEquals(artifactId1, idCaptor.getValue().iterator().next());
      verify(artifactFactory).introduceArtifact(artifact1, branch);
      verify(proxyManager).asExternalArtifact(session, artifact2);
      assertEquals(readable2, actual);
   }

   @Test
   public void testDeleteArtifact() throws OseeCoreException {
      when(artifactFactory.clone(artifact1)).thenReturn(artifact2);

      txDataManager.deleteArtifact(txData, artifact1);

      verify(artifact2).delete();
      verify(relationManager).unrelateFromAll(session, graph, artifact2);
   }

   @Test
   public void testCreateChangeData() throws OseeCoreException {
      Iterable<Artifact> writeables = Arrays.asList(artifact1);
      when(txData.getAllWriteables()).thenReturn(writeables);
      when(artifact1.isDirty()).thenReturn(true);

      TransactionData changeData = txDataManager.createChangeData(txData);
      assertNotNull(changeData);
   }

   @Test
   public void testSetRationale() throws OseeCoreException {
      String rationale = "i have no rationale";

      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact3);

      txDataManager.setRationale(txData, readable1, DEFAULT_HIERARCHY, readable2, rationale);

      verify(relationManager).setRationale(session, graph, artifact1, DEFAULT_HIERARCHY, artifact3, rationale);
   }

   @Test
   public void testRelate() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);
      when(txData.getGraph()).thenReturn(graph);

      txDataManager.relate(txData, readable1, DEFAULT_HIERARCHY, readable2);

      verify(relationManager).relate(session, graph, artifact1, DEFAULT_HIERARCHY, artifact2);
   }

   @Test
   public void testRelateWithOrder() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);
      when(txData.getGraph()).thenReturn(graph);

      txDataManager.relate(txData, readable1, DEFAULT_HIERARCHY, readable2, LEXICOGRAPHICAL_DESC);

      verify(relationManager).relate(session, graph, artifact1, DEFAULT_HIERARCHY, artifact2, LEXICOGRAPHICAL_DESC);
   }

   @Test
   public void testAddChildren() throws OseeCoreException {
      List<? extends ArtifactReadable> children = Arrays.asList(readable2, readable3);

      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);
      when(txData.getWriteable(readable3)).thenReturn(artifact3);
      when(txData.getGraph()).thenReturn(graph);

      txDataManager.addChildren(txData, readable1, children);

      verify(relationManager).addChildren(eq(session), eq(graph), eq(artifact1), nodeCaptor.capture());

      Iterator<? extends RelationNode> iterator = nodeCaptor.getValue().iterator();
      assertEquals(artifact2, iterator.next());
      assertEquals(artifact3, iterator.next());
   }

   @Test
   public void testUnrelate() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getWriteable(readable2)).thenReturn(artifact2);
      when(txData.getGraph()).thenReturn(graph);

      txDataManager.unrelate(txData, readable1, DEFAULT_HIERARCHY, readable2);

      verify(relationManager).unrelate(session, graph, artifact1, DEFAULT_HIERARCHY, artifact2);
   }

   @Test
   public void testUnrelateTypeFromAll() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getGraph()).thenReturn(graph);

      txDataManager.unrelateFromAll(txData, DEFAULT_HIERARCHY, readable1, IS_CHILD);

      verify(relationManager).unrelateFromAll(session, graph, DEFAULT_HIERARCHY, artifact1, IS_CHILD);
   }

   @Test
   public void testUnrelateFromAll() throws OseeCoreException {
      when(txData.getWriteable(readable1)).thenReturn(artifact1);
      when(txData.getGraph()).thenReturn(graph);

      txDataManager.unrelateFromAll(txData, readable1);

      verify(relationManager).unrelateFromAll(session, graph, artifact1);
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
