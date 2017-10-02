/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.internal;

import org.eclipse.osee.framework.core.mocks.MockBundleContext;
import org.eclipse.osee.framework.core.mocks.MockServiceReference;
import org.eclipse.osee.framework.core.mocks.MockTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.eclipse.osee.framework.core.util.ServiceBinderFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Test Case for {@link ServiceBinderFactoryImpl}{@link ServiceBinderFactory}
 * 
 * @author Roberto E. Escobar
 */
public class ServiceBinderFactoryTest {

   private static ServiceReference serviceReference1;
   private static ServiceReference serviceReference2;
   private static Object serviceObject1;
   private static Object serviceObject2;
   private static BundleContext context;

   private static MockTrackingHandler handler;
   private static ServiceBinderFactory factory;

   @BeforeClass
   public static void setup() {
      serviceReference1 = new MockServiceReference();
      serviceReference2 = new MockServiceReference();
      serviceObject1 = new Object();
      serviceObject2 = new Object();
      context = new MockBundleContext() {
         @Override
         public Object getService(ServiceReference reference) {
            Assert.assertNotNull(reference);
            return reference.equals(serviceReference1) ? serviceObject1 : serviceObject2;
         }
      };

      handler = new MockTrackingHandler(context, String.class);
      factory = new ServiceBinderFactoryImpl(context, handler);
   }

   @Test(expected = OseeStateException.class)
   public void testNullBindType()  {
      factory.createTracker(null, String.class);
   }

   @Test(expected = IllegalStateException.class)
   public void testCreateSingletonBinderTracker()  {
      ServiceTracker tracker = factory.createTracker(ServiceBindType.SINGLETON, String.class);
      Assert.assertNotNull(tracker);
      tracker.open();

      handler.reset();
      tracker.addingService(serviceReference1);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      tracker.addingService(serviceReference2);
   }

   @Test
   public void testCreateMultiBinderTracker()  {
      ServiceTracker tracker = factory.createTracker(ServiceBindType.MANY, String.class);
      Assert.assertNotNull(tracker);
      tracker.open();

      handler.reset();
      tracker.addingService(serviceReference1);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      handler.reset();
      tracker.addingService(serviceReference2);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertTrue(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());
   }
}
