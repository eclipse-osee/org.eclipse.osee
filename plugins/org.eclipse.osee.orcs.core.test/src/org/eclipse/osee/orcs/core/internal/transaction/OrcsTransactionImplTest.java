/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link OrcsTransactionImpl}
 * 
 * @author John Misinco
 */
public class OrcsTransactionImplTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private SessionContext sessionContext;
   @Mock private BranchDataStore dataStore;
   @Mock private ArtifactProxyFactory artifactFactory;
   @Mock private TxDataManager txManager;
   @Mock private IOseeBranch branch;
   @Mock private ArtifactWriteable expected;
   // @formatter:on

   private OrcsTransactionImpl tx;
   private String guid;
   private final IArtifactType artType = CoreArtifactTypes.Artifact;
   private String sessionId;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      tx = new OrcsTransactionImpl(logger, sessionContext, dataStore, artifactFactory, txManager, branch);

      sessionId = GUID.create();
      guid = GUID.create();
      when(expected.getGuid()).thenReturn(guid);
   }

   @Test
   public void testSetGetAuthor() {
      ArtifactReadable source = mock(ArtifactReadable.class);
      tx.setAuthor(source);

      Assert.assertEquals(source, tx.getAuthor());
   }

   @Test
   public void testSetGetComment() {
      String comment = "This is my comment";
      tx.setComment(comment);

      Assert.assertEquals(comment, tx.getComment());
   }

   @Test
   public void testGetTxData() throws OseeCoreException {
      tx.getTxData();
      verify(txManager).getChanges();
   }

   @Test
   public void testAsWriteable() throws OseeCoreException {
      ArtifactReadable source = mock(ArtifactReadable.class);
      when(txManager.getOrAddWrite(source)).thenReturn(expected);

      ArtifactWriteable actual = tx.asWritable(source);

      Assert.assertEquals(actual, expected);
      verify(txManager).getOrAddWrite(source);
   }

   @Test
   public void testAsWriteableList() throws OseeCoreException {
      ArtifactReadable sourceA = mock(ArtifactReadable.class);
      ArtifactReadable sourceB = mock(ArtifactReadable.class);

      ArtifactWriteable expectedA = mock(ArtifactWriteable.class);
      ArtifactWriteable expectedB = mock(ArtifactWriteable.class);
      when(txManager.getOrAddWrite(sourceA)).thenReturn(expectedA);
      when(txManager.getOrAddWrite(sourceB)).thenReturn(expectedB);

      List<ArtifactReadable> readables = Arrays.asList(sourceA, sourceB);

      List<ArtifactWriteable> actuals = tx.asWritable(readables);

      Assert.assertEquals(readables.size(), actuals.size());
      verify(txManager).getOrAddWrite(sourceA);
      verify(txManager).getOrAddWrite(sourceB);

      Iterator<ArtifactWriteable> iterator = actuals.iterator();
      Assert.assertEquals(expectedA, iterator.next());
      Assert.assertEquals(expectedB, iterator.next());
   }

   @Test
   public void testCreateArtifactFromToken() throws OseeCoreException {
      IArtifactToken token = mock(IArtifactToken.class);
      String name = "testCreateArtifactFromToken";

      when(token.getName()).thenReturn(name);
      when(token.getArtifactType()).thenReturn(artType);

      when(token.getGuid()).thenReturn(guid);
      when(artifactFactory.create(branch, artType, guid, name)).thenReturn(expected);

      ArtifactWriteable artifact = tx.createArtifact(token);

      assertNotNull(artifact);
      verify(artifactFactory).create(branch, artType, guid, name);
      verify(txManager).addWrite(artifact);
   }

   @Test
   public void testCreateArtifactTypeAndName() throws OseeCoreException {
      String name = "testCreateArtifact";
      when(artifactFactory.create(branch, artType, null, name)).thenReturn(expected);

      ArtifactWriteable artifact = tx.createArtifact(artType, name);

      assertNotNull(artifact);
      verify(artifactFactory).create(branch, artType, null, name);
      verify(txManager).addWrite(artifact);
   }

   @Test
   public void testCreateArtifactTypeNameGuid() throws OseeCoreException {
      String name = "testCreateArtifact";
      when(artifactFactory.create(branch, artType, guid, name)).thenReturn(expected);

      ArtifactWriteable artifact = tx.createArtifact(artType, name, guid);

      assertNotNull(artifact);
      verify(artifactFactory).create(branch, artType, guid, name);
      verify(txManager).addWrite(artifact);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDuplicateArtifact() throws OseeCoreException {
      ArtifactReadable source = mock(ArtifactReadable.class);
      final Collection<AttributeType> types = mock(Collection.class);
      when(source.getGuid()).thenReturn(guid);
      when(source.getExistingAttributeTypes()).thenAnswer(new Answer<Collection<? extends IAttributeType>>() {

         @Override
         public Collection<? extends IAttributeType> answer(InvocationOnMock invocation) throws Throwable {
            return types;
         }
      });
      when(artifactFactory.copy(source, types, branch)).thenReturn(expected);

      ArtifactWriteable actual = tx.duplicateArtifact(source);

      Assert.assertTrue(actual != source);
      verify(artifactFactory).copy(source, types, branch);
      verify(txManager).addWrite(actual);
   }

   @Test
   public void testIntroduceArtifact() throws OseeCoreException {
      ArtifactReadable source = mock(ArtifactReadable.class);
      when(artifactFactory.introduce(source, branch)).thenReturn(expected);

      ArtifactWriteable actual = tx.introduceArtifact(source);

      Assert.assertTrue(actual != source);
      verify(artifactFactory).introduce(source, branch);
      verify(txManager).addWrite(actual);
   }

   @Test
   public void testCommitTwiceWhileInProgress() throws Exception {
      OrcsTransactionImpl spy = Mockito.spy(tx);

      when(spy.isCommitInProgress()).thenReturn(true);

      thrown.expect(OseeCoreException.class);
      thrown.expectMessage("Commit is already in progress");
      spy.commit();
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCommitErrorDuringExecution() throws Exception {
      final Callable<TransactionResult> callable = mock(Callable.class);
      when(dataStore.commitTransaction(sessionId, tx)).thenAnswer(new Answer<Callable<TransactionResult>>() {

         @Override
         public Callable<TransactionResult> answer(InvocationOnMock invocation) throws Throwable {
            Assert.assertTrue(tx.isCommitInProgress());
            return callable;
         }
      });

      OseeCoreException exception = new OseeCoreException("Execution error");
      when(callable.call()).thenThrow(exception);

      Assert.assertFalse(tx.isCommitInProgress());

      thrown.expect(OseeCoreException.class);
      thrown.expectMessage(exception.getMessage());
      tx.commit();

      verify(txManager).onCommitStart();
      verify(txManager).onCommitRollback();
      verify(txManager).onCommitEnd();

      Assert.assertFalse(tx.isCommitInProgress());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCommitErrorDuringRollback() throws Exception {
      final Callable<TransactionResult> callable = mock(Callable.class);
      when(dataStore.commitTransaction(sessionId, tx)).thenAnswer(new Answer<Callable<TransactionResult>>() {

         @Override
         public Callable<TransactionResult> answer(InvocationOnMock invocation) throws Throwable {
            Assert.assertTrue(tx.isCommitInProgress());
            return callable;
         }
      });

      OseeCoreException exception = new OseeCoreException("Execution error");
      doThrow(exception).when(txManager).onCommitRollback();

      Assert.assertFalse(tx.isCommitInProgress());

      thrown.expect(OseeCoreException.class);
      thrown.expectMessage("Exception during rollback and commit");
      tx.commit();

      verify(txManager).onCommitStart();
      verify(txManager).onCommitRollback();
      verify(txManager).onCommitEnd();

      Assert.assertFalse(tx.isCommitInProgress());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCommit() throws Exception {
      final Callable<TransactionResult> callable = mock(Callable.class);
      when(dataStore.commitTransaction(sessionId, tx)).thenAnswer(new Answer<Callable<TransactionResult>>() {

         @Override
         public Callable<TransactionResult> answer(InvocationOnMock invocation) throws Throwable {
            Assert.assertTrue(tx.isCommitInProgress());
            return callable;
         }
      });
      TransactionResult txResult = mock(TransactionResult.class);
      TransactionRecord newTx = mock(TransactionRecord.class);

      when(callable.call()).thenReturn(txResult);
      when(txResult.getTransaction()).thenReturn(newTx);

      Assert.assertFalse(tx.isCommitInProgress());

      TransactionRecord actual = tx.commit();

      Assert.assertEquals(newTx, actual);

      verify(txManager).onCommitStart();
      verify(txManager).onCommitSuccess(txResult);
      verify(txManager).onCommitEnd();

      Assert.assertFalse(tx.isCommitInProgress());
   }
}
