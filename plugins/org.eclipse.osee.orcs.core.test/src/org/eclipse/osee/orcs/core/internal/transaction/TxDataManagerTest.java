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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
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
   @Mock private TxDataLoader loader;
   
   @Mock private TxData txData;
   @Mock private ArtifactReadable readable1;
   @Mock private ArtifactReadable readable2;
   @Mock private Artifact artifact;
   @Mock private Artifact artifact2;
   @Mock private Artifact child;
   @Mock private ArtifactId artifactId;
   @Captor private ArgumentCaptor<Collection<ArtifactId>> idCaptor;
   // @formatter:on

   private TxDataManager txDataManager;
   private String guid;
   private final Collection<? extends IAttributeType> types = Collections.emptyList();

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
      txDataManager = new TxDataManager(proxyManager, artifactFactory, loader);

      guid = GUID.create();

      when(artifact.getExistingAttributeTypes()).thenAnswer(answerValue(types));

      when(proxyManager.asInternalArtifact(readable1)).thenReturn(artifact);
      when(proxyManager.asExternalArtifact(session, artifact)).thenReturn(readable1);

      when(txData.getSession()).thenReturn(session);
      when(txData.getBranch()).thenReturn(branch);
   }

   @Test
   public void testCreateTxData() {
      TxData newData = txDataManager.createTxData(session, branch);
      assertNotNull(newData);
   }

   @Test
   public void testTxCommitSuccess() {
      Iterable<Artifact> writeables = Arrays.asList(artifact);
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
   public void testGetForWrite() throws OseeCoreException {
      ResultSet<Artifact> loaded = new ResultSetIterable<Artifact>(Collections.<Artifact> singleton(artifact));
      when(loader.loadArtifacts(eq(session), eq(branch), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);

      Artifact actual = txDataManager.getForWrite(txData, readable1);

      verify(loader).loadArtifacts(eq(session), eq(branch), idCaptor.capture());
      assertEquals(artifact, actual);
   }

   @Test
   public void testGetForWriteDuringWrite() throws OseeCoreException {
      when(txData.add(artifact)).thenReturn(child);
      when(artifactFactory.clone(artifact)).thenReturn(artifact);

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
      when(artifactFactory.createArtifact(branch, CoreArtifactTypes.DirectSoftwareRequirement, guid)).thenReturn(
         artifact);
      when(proxyManager.asExternalArtifact(session, artifact)).thenReturn(readable1);

      ArtifactReadable actual =
         txDataManager.createArtifact(txData, CoreArtifactTypes.DirectSoftwareRequirement, "Direct SW requirement",
            guid);

      verify(artifactFactory).createArtifact(branch, CoreArtifactTypes.DirectSoftwareRequirement, guid);
      assertEquals(readable1, actual);
   }

   @Test
   public void testCopyExisitingArtifact() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(txData.getWriteable(readable1)).thenReturn(artifact);
      when(artifactFactory.copyArtifact(artifact, types, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txData, branch, readable1);

      verify(txData).getWriteable(readable1);
      verify(artifactFactory).copyArtifact(artifact, types, branch);
      verify(proxyManager).asExternalArtifact(session, artifact2);

      assertEquals(readable2, actual);
   }

   @Test
   public void testCopyReadableArtifact() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(readable1.getBranch()).thenReturn(branch);
      when(txData.getWriteable(readable1)).thenReturn(null);
      when(proxyManager.asInternalArtifact(readable1)).thenReturn(artifact);

      when(artifactFactory.copyArtifact(artifact, types, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txData, branch, readable1);

      verify(txData).getWriteable(readable1);
      verify(proxyManager).asInternalArtifact(readable1);
      verify(artifactFactory).copyArtifact(artifact, Collections.<IAttributeType> emptyList(), branch);
      verify(proxyManager).asExternalArtifact(session, artifact2);

      assertEquals(readable2, actual);
   }

   @Test
   public void testCopyArtifact() throws OseeCoreException {
      String guid = GUID.create();

      ArtifactData data = Mockito.mock(ArtifactData.class);

      @SuppressWarnings("unchecked")
      ValueProvider<IOseeBranch, OrcsData> provider = Mockito.mock(ValueProvider.class);

      Artifact sourceArtifact = Mockito.spy(new ArtifactImpl(null, data, null, null, provider));

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
      when(txData.getWriteable(artifactId)).thenReturn(artifact);
      when(artifact.getExistingAttributeTypes()).thenAnswer(answerValue(types));
      when(artifactFactory.copyArtifact(artifact, types, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.copyArtifact(txData, branch, artifactId);

      verify(txData).getWriteable(artifactId);
      verify(artifactFactory).copyArtifact(artifact, types, branch);
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
      txDataManager.introduceArtifact(txData, branch, artifactId);
   }

   @Test
   public void testIntroduceArtifact() throws OseeCoreException {
      when(txData.isCommitInProgress()).thenReturn(false);
      when(txData.getTxState()).thenReturn(TxState.NEW_TX);
      when(txData.getBranch()).thenReturn(branch);

      ResultSet<Artifact> loaded = new ResultSetIterable<Artifact>(Collections.<Artifact> singleton(artifact));
      when(loader.loadArtifacts(eq(session), eq(COMMON), anyCollectionOf(ArtifactId.class))).thenReturn(loaded);
      when(artifactFactory.introduceArtifact(artifact, branch)).thenReturn(artifact2);
      when(proxyManager.asExternalArtifact(session, artifact2)).thenReturn(readable2);

      ArtifactReadable actual = txDataManager.introduceArtifact(txData, COMMON, artifactId);

      verify(loader).loadArtifacts(eq(session), eq(COMMON), idCaptor.capture());
      assertEquals(artifactId, idCaptor.getValue().iterator().next());
      verify(artifactFactory).introduceArtifact(artifact, branch);
      verify(proxyManager).asExternalArtifact(session, artifact2);
      assertEquals(readable2, actual);
   }

   @Test
   public void testDeleteArtifact() throws OseeCoreException {
      when(artifactFactory.clone(artifact)).thenReturn(artifact2);

      txDataManager.deleteArtifact(txData, artifact);

      verify(artifact2).delete();
   }

   @Test
   public void testCreateChangeData() throws OseeCoreException {
      Iterable<Artifact> writeables = Arrays.asList(artifact);
      when(txData.getAllWriteables()).thenReturn(writeables);
      when(artifact.isDirty()).thenReturn(true);

      TransactionData changeData = txDataManager.createChangeData(txData);
      assertNotNull(changeData);
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
