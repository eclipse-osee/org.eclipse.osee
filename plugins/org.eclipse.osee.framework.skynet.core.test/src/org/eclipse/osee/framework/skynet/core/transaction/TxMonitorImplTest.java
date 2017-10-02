/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.transaction;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.MonitoredTx;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.TxState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link TxMonitorImpl}
 * 
 * @author Roberto E. Escobar
 */
public class TxMonitorImplTest {

   private Object key1;
   private Object key2;

   private MockMonitoredTx tx1;
   private MockMonitoredTx tx2;

   private TxMonitorImpl<Object> txMonitor;
   private TxMonitorCache<Object> txCache;

   @Before
   public void setUp() {
      key1 = new Object();
      key2 = new Object();

      tx1 = createTx();
      tx2 = createTx();

      txCache = new TxMonitorCache<>();
      txMonitor = new TxMonitorImpl<>(txCache);
   }

   @Test(expected = OseeStateException.class)
   public void testCreatedButNotInCreatedState()  {
      tx1.setTxState(TxState.ENDED);
      txMonitor.createTx(key1, tx1);
   }

   @Test(expected = OseeStateException.class)
   public void testCreatedButWasFoundInCache()  {
      txCache.putTx(key1, tx1);
      txMonitor.createTx(key1, tx1);
   }

   @Test
   public void testCreateAddsItem()  {
      Assert.assertFalse(txCache.contains(key1, tx1.getUuid()));
      txMonitor.createTx(key1, tx1);
      Assert.assertTrue(txCache.contains(key1, tx1.getUuid()));
   }

   @Test(expected = OseeStateException.class)
   public void testBeginButNotInCache()  {
      Assert.assertFalse(txCache.contains(key1, tx1.getUuid()));
      txMonitor.beginTx(key1, tx1);
   }

   @Test(expected = OseeStateException.class)
   public void testBeginButInEndedState()  {
      txCache.putTx(key1, tx1);
      tx1.setTxState(TxState.ENDED);
      txMonitor.beginTx(key1, tx1);
   }

   @Test(expected = OseeStateException.class)
   public void testBeginButInRunningState()  {
      txCache.putTx(key1, tx1);
      tx1.setTxState(TxState.RUNNING);
      txMonitor.beginTx(key1, tx1);
   }

   @Test
   public void testBeginCreated()  {
      txCache.putTx(key1, tx1);
      tx1.setTxState(TxState.CREATED);
      txMonitor.beginTx(key1, tx1);
   }

   @Test
   public void testBeginModified()  {
      txCache.putTx(key1, tx1);
      tx1.setTxState(TxState.MODIFIED);
      txMonitor.beginTx(key1, tx1);
   }

   @Test(expected = OseeStateException.class)
   public void testEndButNotInCache()  {
      Assert.assertFalse(txCache.contains(key1, tx1.getUuid()));
      txMonitor.endTx(key1, tx1);
   }

   @Test(expected = OseeStateException.class)
   public void testEndButNotInEndedState()  {
      txCache.putTx(key1, tx1);
      tx1.setTxState(TxState.RUNNING);
      txMonitor.endTx(key1, tx1);
   }

   @Test
   public void testEndInEndedState()  {
      txCache.putTx(key1, tx1);
      tx1.setTxState(TxState.ENDED);
      Assert.assertTrue(txCache.contains(key1, tx1.getUuid()));
      txMonitor.endTx(key1, tx1);
      Assert.assertFalse(txCache.contains(key1, tx1.getUuid()));
   }

   @Test
   public void testEndInEndedWithExceptionState()  {
      txCache.putTx(key1, tx1);
      tx1.setTxState(TxState.ENDED_WITH_EXCEPTION);
      Assert.assertTrue(txCache.contains(key1, tx1.getUuid()));
      txMonitor.endTx(key1, tx1);
      Assert.assertFalse(txCache.contains(key1, tx1.getUuid()));
   }

   @Test(expected = OseeStateException.class)
   public void testRollbackButNotInCache()  {
      Assert.assertFalse(txCache.contains(key1, tx1.getUuid()));
      txMonitor.rollbackTx(key1, tx1);
   }

   @Test
   public void testRollback()  {
      txCache.putTx(key1, tx1);
      Assert.assertFalse(tx1.wasRollbackCalled());
      txMonitor.rollbackTx(key1, tx1);
      Assert.assertTrue(tx1.wasRollbackCalled());
   }

   @Test
   public void testNormalPath()  {
      tx1.setTxState(TxState.CREATED);
      txMonitor.createTx(key1, tx1);
      txMonitor.beginTx(key1, tx1);
      tx1.setTxState(TxState.ENDED);
      txMonitor.endTx(key1, tx1);
   }

   @Test(expected = OseeStateException.class)
   public void testCoModificationFail()  {
      Object toModify = new Object();
      tx1.addItem(toModify);
      tx2.addItem(toModify);
      txCache.putTx(key1, tx1);
      txCache.putTx(key1, tx2);
      txMonitor.checkForComodification(key1, tx1, toModify);
   }

   @Test
   public void testCoModificationOk()  {
      Object toModify = new Object();
      tx1.addItem(toModify);
      tx2.addItem(toModify);
      txCache.putTx(key1, tx1);
      txCache.putTx(key2, tx2);
      txMonitor.checkForComodification(key1, tx1, toModify);
   }

   private static MockMonitoredTx createTx() {
      return new MockMonitoredTx(GUID.create(), TxState.CREATED);
   }

   private static final class MockMonitoredTx implements MonitoredTx {

      private final String uuid;
      private TxState txState;
      private boolean rolledBack;
      private final Set<Object> data = new HashSet<>();

      public MockMonitoredTx(String uuid, TxState txState) {
         this.uuid = uuid;
         this.txState = txState;
      }

      public void addItem(Object object) {
         data.add(object);
      }

      public boolean wasRollbackCalled() {
         return rolledBack;
      }

      @Override
      public String getUuid() {
         return uuid;
      }

      public void setTxState(TxState txState) {
         this.txState = txState;
      }

      @Override
      public TxState getTxState() {
         return txState;
      }

      @Override
      public boolean containsItem(Object object) {
         return data.contains(object);
      }

      @Override
      public void rollback() {
         rolledBack = true;
      }
   }
}
