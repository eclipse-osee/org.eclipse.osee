/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.executor.internal;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.osee.logger.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorAdminTest {

   //@formatter:off
   @Mock private Log logger;
   //@formatter:on

   private ExecutorAdminImpl admin;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      admin = new ExecutorAdminImpl();
      admin.setLogger(logger);

      admin.start(new HashMap<String, Object>());
   }

   @After
   public void tearDown() {
      if (admin != null) {
         admin.stop(new HashMap<String, Object>());
      }
   }

   @Test
   public void testTerminateExecutorService() throws Exception {
      ExecutorService anotherExecutor = admin.getExecutor("hello");
      Assert.assertNotNull(anotherExecutor);

      ExecutorService second = admin.getExecutor("hello");
      Assert.assertEquals(anotherExecutor, second);

      second.shutdown();
      ExecutorService third = admin.getExecutor("hello");
      Assert.assertFalse(third.equals(second));
   }

   @Test
   public void testScheduleExecutor() throws InterruptedException {
      final AtomicInteger executed = new AtomicInteger();
      admin.scheduleAtFixedRate("schedule.test", () -> executed.incrementAndGet(), -1, 250, TimeUnit.MILLISECONDS);
      synchronized (this) {
         this.wait(1000L);
      }
      admin.shutdown("schedule.test");
      int numOfExecutions = executed.get();
      int limit = 3;
      Assert.assertTrue("Number of executions was [" + numOfExecutions + "] expected > " + limit,
         numOfExecutions > limit);
   }
}
