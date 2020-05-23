/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.MonitoredTx;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.TxState;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link TxMonitorCache}
 * 
 * @author Roberto E. Escobar
 */
public class TxMonitorCacheTest {

   @Test
   public void testCache() {
      Object key1 = new Object();
      Object key2 = new Object();

      TxMonitorCache<Object> cache = new TxMonitorCache<>();

      MonitoredTx tx1 = createTx();
      MonitoredTx tx2 = createTx();
      MonitoredTx tx3 = createTx();
      MonitoredTx tx4 = createTx();

      cache.putTx(key1, tx1);
      cache.putTx(key1, tx2);

      cache.putTx(key2, tx3);
      cache.putTx(key2, tx4);

      Assert.assertTrue(cache.contains(key1));
      Assert.assertTrue(cache.contains(key2));
      Assert.assertTrue(cache.contains(key1, tx1.getUuid()));
      Assert.assertTrue(cache.contains(key1, tx2.getUuid()));

      Assert.assertTrue(cache.contains(key2, tx3.getUuid()));
      Assert.assertTrue(cache.contains(key2, tx4.getUuid()));

      Assert.assertFalse(cache.contains(key1, tx3.getUuid()));

      Assert.assertEquals(2, cache.getTxs(key1).size());
      Assert.assertEquals(2, cache.getTxs(key2).size());

      Assert.assertEquals(tx1, cache.getTx(key1, tx1.getUuid()));

      String tx2Uuid = tx2.getUuid();
      Assert.assertEquals(tx2, cache.getTx(key1, tx2Uuid));
      tx2 = null;
      System.gc();
      Assert.assertNull(cache.getTx(key1, tx2Uuid));

      Assert.assertEquals(1, cache.getTxs(key1).size());
      Assert.assertEquals(2, cache.getTxs(key2).size());

      cache.removeTx(key1, tx1.getUuid());
      Assert.assertNull(cache.getTx(key1, tx1.getUuid()));
      Assert.assertEquals(0, cache.getTxs(key1).size());
      Assert.assertEquals(2, cache.getTxs(key2).size());

      tx3 = null;
      tx4 = null;
      System.gc();
      Assert.assertEquals(0, cache.getTxs(key1).size());
      Assert.assertEquals(0, cache.getTxs(key2).size());
   }

   private static MonitoredTx createTx() {
      return new MockMonitoredTx(GUID.create());
   }

   private static final class MockMonitoredTx implements MonitoredTx {

      private final String uuid;

      public MockMonitoredTx(String uuid) {
         this.uuid = uuid;
      }

      @Override
      public String getUuid() {
         return uuid;
      }

      @Override
      public TxState getTxState() {
         return null;
      }

      @Override
      public boolean containsItem(Object object) {
         return false;
      }

      @Override
      public void rollback() {
         // Do nothing
      }
   }
}
