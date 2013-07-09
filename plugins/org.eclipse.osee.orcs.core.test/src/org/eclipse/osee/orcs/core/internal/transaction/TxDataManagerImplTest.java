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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyWriteable;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManagerImpl.TxDataHandlerFactory;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link TxDataManagerImpl}
 * 
 * @author Roberto E. Escobar
 */
@SuppressWarnings("rawtypes")
public class TxDataManagerImplTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock ArtifactProxyFactory proxyFactory;
   @Mock TxDataHandlerFactory dataFactory;
   @Mock ArtifactWriteable expected;
   @Mock ProxyWriteable proxy;
   // @formatter:on

   private TxDataManagerImpl txManager;
   private String guid;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      txManager = new TxDataManagerImpl(proxyFactory, dataFactory);

      guid = GUID.create();
      when(expected.getGuid()).thenReturn(guid);
   }

   @Test
   public void testAddAndGetOrWrite() throws OseeCoreException {
      Assert.assertEquals(0, txManager.size());
      txManager.addWrite(expected);
      Assert.assertEquals(1, txManager.size());

      ArtifactWriteable actual = txManager.getOrAddWrite(expected);
      Assert.assertTrue(expected == actual);
      Assert.assertEquals(1, txManager.size());
   }

   @Test
   public void testAddingAnotherInstanceOfWriteable() throws OseeCoreException {
      Assert.assertEquals(0, txManager.size());
      txManager.addWrite(expected);
      Assert.assertEquals(1, txManager.size());

      // Check no exception
      txManager.addWrite(expected);
      Assert.assertEquals(1, txManager.size());

      ArtifactWriteable other = mock(ArtifactWriteable.class);
      when(other.getGuid()).thenReturn(guid);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Another instance of writeable detected - writeable tracking would be inconsistent");
      txManager.addWrite(other);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testOnStart() throws OseeCoreException {
      txManager.addWrite(expected);
      when(proxyFactory.asProxyWriteable(expected)).thenReturn(proxy);

      txManager.onCommitStart();
      verify(proxy).setWritesAllowed(false);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testOnRollback() throws OseeCoreException {
      txManager.addWrite(expected);
      when(proxyFactory.asProxyWriteable(expected)).thenReturn(proxy);

      txManager.onCommitRollback();
      verify(proxy).setWritesAllowed(true);
   }

   @Test
   public void testGetZeroChanges() throws OseeCoreException {
      Artifact impl = mock(Artifact.class);

      txManager.addWrite(expected);
      when(expected.isDirty()).thenReturn(false);

      txManager.getChanges();
      verify(proxyFactory, times(0)).getProxiedObject(expected);
      verify(impl, times(0)).accept(null);
   }

   @Test
   public void testGetOneDirty() throws OseeCoreException {
      Artifact impl = mock(Artifact.class);

      txManager.addWrite(expected);
      when(expected.isDirty()).thenReturn(true);
      when(proxyFactory.getProxiedObject(expected)).thenReturn(impl);

      List<ArtifactTransactionData> data = txManager.getChanges();

      verify(proxyFactory).getProxiedObject(expected);
      verify(dataFactory).createOnDirtyHandler(data);
      verify(impl).accept(null);
   }

   @Test
   public void testOnCommitSuccess() throws OseeCoreException {
      final List<ArtifactTransactionData> list = new ArrayList<ArtifactTransactionData>();
      Map<String, ArtifactWriteable> writeableArtifacts = new HashMap<String, ArtifactWriteable>();
      writeableArtifacts.put(expected.getGuid(), expected);

      TransactionResult result = mock(TransactionResult.class);
      final ArtifactTransactionData txData = mock(ArtifactTransactionData.class);
      OrcsVisitor vistor = mock(OrcsVisitor.class);
      when(dataFactory.createOnSuccessHandler(writeableArtifacts)).thenReturn(vistor);
      when(result.getData()).thenAnswer(new Answer<List<ArtifactTransactionData>>() {

         @Override
         public List<ArtifactTransactionData> answer(InvocationOnMock invocation) throws Throwable {
            return list;
         }

      });
      list.add(txData);
      txManager.addWrite(expected);

      txManager.onCommitSuccess(result);

      verify(dataFactory).createOnSuccessHandler(writeableArtifacts);
      verify(txData).accept(vistor);
   }
}
