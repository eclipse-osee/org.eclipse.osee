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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.ds.TupleDataFactory;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link TxCallableFactory}
 *
 * @author Megumi Telles
 */
public class TxCallableFactoryTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private OrcsSession session;
   @Mock private Log logger;
   @Mock private ExternalArtifactManager proxyManager;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private RelationManager relationManager;
   @Mock private TxDataLoader loader;
   @Mock private TupleDataFactory tupleFactory;
   @Mock private GraphData graph;
   @Mock private TxDataStore txDataStore;
   @Mock private ArtifactReadable groupArtifact;
   @Captor private ArgumentCaptor<TransactionData> txData;
   // @formatter:on

   private TxCallableFactory txFactory;
   private TxData data;
   private TxDataManager txManager;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      txManager = new TxDataManager(proxyManager, artifactFactory, relationManager, tupleFactory, loader);
      txFactory = new TxCallableFactory(logger, txDataStore, txManager);
      data = new TxData(session, graph);
      when(graph.getBranch()).thenReturn(COMMON);

   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testpurgeTransactions() throws Exception {
      Callable callable = mock(Callable.class);
      Collection<? extends TransactionId> transactions = Collections.emptyList();
      when(txDataStore.purgeTransactions(session, transactions)).thenReturn(callable);

      txFactory.purgeTransactions(session, transactions).call();
      verify(txDataStore).purgeTransactions(session, transactions);
   }

   @Test
   public void testCommit() throws Exception {
      final TransactionResult txResult = mock(TransactionResult.class);

      txManager.setAuthor(data, SystemUser.OseeSystem);
      txManager.setComment(data, "My Comment");

      when(txDataStore.commitTransaction(eq(session), txData.capture())).thenReturn(new Callable<TransactionResult>() {

         @Override
         public TransactionResult call() throws Exception {
            assertTrue(data.isCommitInProgress());
            return txResult;
         }
      });

      TransactionReadable newTx = mock(TransactionReadable.class);

      when(txResult.getTransaction()).thenReturn(newTx);

      Assert.assertFalse(data.isCommitInProgress());

      TransactionToken actual = txFactory.createTx(data).call();

      assertEquals(newTx, actual);
      assertFalse(data.isCommitInProgress());

      TransactionData data = txData.getValue();
      assertEquals(COMMON, data.getBranch());
      assertEquals(SystemUser.OseeSystem, data.getAuthor());
      assertEquals("My Comment", data.getComment());
   }

   @Test
   public void testCommitErrorDuringRollback() throws Exception {
      TxDataManager manager = mock(TxDataManager.class);

      TxCallableFactory factory = new TxCallableFactory(logger, txDataStore, manager);

      Exception exception = new IllegalStateException("onCommit Exception");

      doThrow(exception).when(manager).txCommitSuccess(data);
      doThrow(OseeCoreException.class).when(manager).rollbackTx(data);

      thrown.expect(OseeCoreException.class);
      thrown.expectMessage("Exception during rollback and commit");
      factory.createTx(data).call();
   }
}
