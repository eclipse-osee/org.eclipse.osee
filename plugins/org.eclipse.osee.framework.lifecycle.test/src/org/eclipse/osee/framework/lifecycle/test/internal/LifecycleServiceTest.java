/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.lifecycle.test.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleVisitor;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.lifecycle.LifecycleServiceImpl;
import org.eclipse.osee.framework.lifecycle.test.mock.AnotherMockHandler;
import org.eclipse.osee.framework.lifecycle.test.mock.AnotherMockLifecycePoint;
import org.eclipse.osee.framework.lifecycle.test.mock.MockHandler;
import org.eclipse.osee.framework.lifecycle.test.mock.MockLifecycePoint;
import org.eclipse.osee.framework.lifecycle.test.mock.NonRunHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link LifecycleServiceImpl}
 * 
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class LifecycleServiceTest {

   @Test
   public void testAccess()  {
      ILifecycleService lifecycleServices = new LifecycleServiceImpl();
      Assert.assertTrue(lifecycleServices.getHandlerTypes().isEmpty());

      MockHandler mockHandler = new MockHandler();
      AnotherMockHandler anotherMockHandler = new AnotherMockHandler();
      NonRunHandler nonRunHandler = new NonRunHandler();

      lifecycleServices.addHandler(MockLifecycePoint.TYPE, mockHandler);
      lifecycleServices.addHandler(MockLifecycePoint.TYPE, anotherMockHandler);
      lifecycleServices.addHandler(AnotherMockLifecycePoint.TYPE, nonRunHandler);

      Assert.assertEquals(2, lifecycleServices.getHandlerCount(MockLifecycePoint.TYPE));
      Assert.assertFalse(lifecycleServices.getHandlerTypes().isEmpty());

      Assert.assertFalse(mockHandler.hasRan());
      Assert.assertFalse(anotherMockHandler.hasRan());
      Assert.assertFalse(nonRunHandler.hasRan());

      //Execute the handlers
      AbstractLifecycleVisitor<?> accessPoint = new MockLifecycePoint("one", "two");
      IStatus status = lifecycleServices.dispatch(new NullProgressMonitor(), accessPoint, "");
      Assert.assertTrue(status.isOK());

      Assert.assertTrue(mockHandler.hasRan());
      Assert.assertTrue(anotherMockHandler.hasRan());
      Assert.assertFalse(nonRunHandler.hasRan());

      lifecycleServices.removeHandler(MockLifecycePoint.TYPE, mockHandler);
      lifecycleServices.removeHandler(MockLifecycePoint.TYPE, anotherMockHandler);
      lifecycleServices.removeHandler(AnotherMockLifecycePoint.TYPE, nonRunHandler);
      //Check that everything cleaned up
      Assert.assertTrue(lifecycleServices.getHandlerTypes().isEmpty());
      Assert.assertEquals(0, lifecycleServices.getHandlerCount(MockLifecycePoint.TYPE));
   }
}
