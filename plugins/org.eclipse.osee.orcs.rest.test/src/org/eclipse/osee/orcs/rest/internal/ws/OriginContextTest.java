/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.core.event.OriginContext;
import org.junit.After;
import org.junit.Test;

/**
 * Test Case for {@link OriginContext}. Covers the set/get/clear facade, the empty/null-clears
 * behavior, and the thread-local isolation invariant (a value set on one thread must not be visible
 * on another). {@link #clearThreadLocal()} clears after every test so a value cannot leak onto the
 * shared JUnit thread.
 *
 * @author Boeing
 */
public class OriginContextTest {

   @After
   public void clearThreadLocal() {
      OriginContext.clear();
   }

   @Test
   public void testGetReturnsNullWhenUnset() {
      assertNull(OriginContext.get());
   }

   @Test
   public void testSetThenGet() {
      OriginContext.set("tab-123");
      assertEquals("tab-123", OriginContext.get());
   }

   @Test
   public void testSetNullClears() {
      OriginContext.set("tab-123");
      OriginContext.set(null);
      assertNull(OriginContext.get());
   }

   @Test
   public void testSetEmptyClears() {
      OriginContext.set("tab-123");
      OriginContext.set("");
      assertNull(OriginContext.get());
   }

   @Test
   public void testClear() {
      OriginContext.set("tab-123");
      OriginContext.clear();
      assertNull(OriginContext.get());
   }

   @Test
   public void testValueIsIsolatedPerThread() throws InterruptedException {
      OriginContext.set("main-thread-tab");

      AtomicReference<String> seenOnOtherThread = new AtomicReference<>("sentinel");
      Thread worker = new Thread(() -> seenOnOtherThread.set(OriginContext.get()));
      worker.start();
      worker.join();

      // The other thread has its own thread-local slot -- it must not see this thread's value.
      assertNull(seenOnOtherThread.get());
      // And this thread's value is unaffected by the other thread.
      assertEquals("main-thread-tab", OriginContext.get());
   }

   @Test
   public void testClearOnOneThreadDoesNotAffectAnother() throws InterruptedException {
      OriginContext.set("main-thread-tab");

      Thread worker = new Thread(() -> {
         OriginContext.set("worker-tab");
         OriginContext.clear();
      });
      worker.start();
      worker.join();

      assertEquals("main-thread-tab", OriginContext.get());
   }
}
