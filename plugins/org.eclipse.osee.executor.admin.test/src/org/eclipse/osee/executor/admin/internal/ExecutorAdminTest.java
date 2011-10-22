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
package org.eclipse.osee.executor.admin.internal;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import org.eclipse.osee.executor.admin.mock.MockEventService;
import org.eclipse.osee.executor.admin.mock.MockLog;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorAdminTest {

   @Test(expected = IllegalStateException.class)
   public void testInitializationGuard() throws Exception {
      ExecutorAdminImpl admin = new ExecutorAdminImpl();
      admin.getDefaultExecutor();
   }

   @Test
   public void testGetService() throws Exception {
      ExecutorAdminImpl admin = new ExecutorAdminImpl();
      admin.setLogger(new MockLog());
      admin.setEventService(new MockEventService());
      admin.start(new HashMap<String, Object>());

      ExecutorService defaultService = admin.getDefaultExecutor();
      Assert.assertNotNull(defaultService);

      ExecutorService serviceByName = admin.getExecutor("default.executor");
      Assert.assertNotNull(serviceByName);
      Assert.assertEquals(serviceByName, defaultService);

      ExecutorService anotherExecutor = admin.getExecutor("hello");
      Assert.assertNotNull(anotherExecutor);
      Assert.assertTrue(!anotherExecutor.equals(defaultService));
   }

   @Test
   public void testTerminateExecutorService() throws Exception {
      ExecutorAdminImpl admin = new ExecutorAdminImpl();
      admin.setLogger(new MockLog());
      admin.setEventService(new MockEventService());
      admin.start(new HashMap<String, Object>());

      ExecutorService anotherExecutor = admin.getExecutor("hello");
      Assert.assertNotNull(anotherExecutor);

      ExecutorService second = admin.getExecutor("hello");
      Assert.assertEquals(anotherExecutor, second);

      second.shutdown();
      ExecutorService third = admin.getExecutor("hello");
      Assert.assertFalse(third.equals(second));
   }
}
