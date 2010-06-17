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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleVisitor;
import org.eclipse.osee.framework.lifecycle.LifecycleService;
import org.eclipse.osee.framework.lifecycle.LifecycleServiceImpl;
import org.eclipse.osee.framework.lifecycle.test.mock.MockHandler;
import org.eclipse.osee.framework.lifecycle.test.mock.MockLifecycePoint;
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
   public void testAccess() throws OseeCoreException {
      LifecycleService lifecycleServices = new LifecycleServiceImpl();
      Assert.assertTrue(lifecycleServices.getHandlerTypes().isEmpty());

      MockHandler handler = new MockHandler();

      lifecycleServices.addHandler(MockLifecycePoint.TYPE, handler);

      Assert.assertEquals(1, lifecycleServices.getHandlerCount(MockLifecycePoint.TYPE));
      Assert.assertFalse(lifecycleServices.getHandlerTypes().isEmpty());

      AbstractLifecycleVisitor<?> accessPoint = new MockLifecycePoint("one", "two");
      IStatus status = lifecycleServices.dispatch(new NullProgressMonitor(), accessPoint, "");
      Assert.assertTrue(status.isOK());

      lifecycleServices.removeHandler(MockLifecycePoint.TYPE, handler);
      Assert.assertTrue(lifecycleServices.getHandlerTypes().isEmpty());
      Assert.assertEquals(0, lifecycleServices.getHandlerCount(MockLifecycePoint.TYPE));

   }
}
