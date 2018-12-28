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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.mocks.MockBundleContext;
import org.eclipse.osee.framework.core.mocks.MockServiceReference;
import org.eclipse.osee.framework.core.mocks.MockTrackingHandler;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Test Case for {@link MultiServiceBinder}
 * 
 * @author Roberto E. Escobar
 */
public class MultiServiceBinderTest {

   private static ServiceReference serviceReference1;
   private static ServiceReference serviceReference2;
   private static Object serviceObject1;
   private static Object serviceObject2;
   private static BundleContext context;

   private static MockTrackingHandler handler;

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

      handler = new MockTrackingHandler(context);
   }

   private Map<Class<?>, Collection<Object>> serviceMap;
   private AbstractServiceBinder serviceBinder;

   @Before
   public void setupTest() {
      serviceMap = new ConcurrentHashMap<>();
      serviceBinder = new MultiServiceBinder(serviceMap, context, handler);
      handler.reset();
   }

   @Test
   public void testCreateTracker() {
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      ServiceTracker intTracker = serviceBinder.createTracker(Integer.class);
      Assert.assertNotNull(intTracker);
      intTracker.open();

      Assert.assertEquals(2, serviceMap.size());
      Assert.assertTrue(serviceMap.containsKey(String.class));
      Assert.assertTrue(serviceMap.containsKey(Integer.class));
   }

   @Test
   public void testAddServices() {
      // Register Two dependencies
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      ServiceTracker intTracker = serviceBinder.createTracker(Integer.class);
      Assert.assertNotNull(intTracker);
      intTracker.open();

      handler.reset();
      stringTracker.addingService(serviceReference1);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      intTracker.addingService(serviceReference2);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      Map<Class<?>, Object> activated = handler.getOnActivateServices();
      Assert.assertEquals(2, activated.size());
      Assert.assertEquals(serviceObject1, activated.get(String.class));
      Assert.assertEquals(serviceObject2, activated.get(Integer.class));
   }

   @Test
   public void testAddAnotherService() {
      // Register One Dependency
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      stringTracker.addingService(serviceReference1);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      handler.reset();
      stringTracker.addingService(serviceReference2);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertTrue(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      Pair<Class<?>, Object> addedService = handler.getServiceAdded();
      Assert.assertNotNull(addedService);
      Assert.assertEquals(String.class, addedService.getFirst());
      Assert.assertEquals(serviceObject2, addedService.getSecond());
   }

   @Test
   public void testAddSameService() {
      // Register One Dependency
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      stringTracker.addingService(serviceReference1);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      handler.reset();
      stringTracker.addingService(serviceReference1);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertTrue(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      Pair<Class<?>, Object> addedService = handler.getServiceAdded();
      Assert.assertNotNull(addedService);
      Assert.assertEquals(String.class, addedService.getFirst());
      Assert.assertEquals(serviceObject1, addedService.getSecond());
   }

   @Test
   public void testRemoveService() {
      // Register One Dependency
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      stringTracker.addingService(serviceReference1);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      handler.reset();
      stringTracker.addingService(serviceReference2);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertTrue(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      handler.reset();
      stringTracker.removedService(serviceReference2, serviceObject2);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertTrue(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      Pair<Class<?>, Object> removedService = handler.getServiceRemoved();
      Assert.assertNotNull(removedService);
      Assert.assertEquals(String.class, removedService.getFirst());
      Assert.assertEquals(serviceObject2, removedService.getSecond());
   }

   @Test(expected = IllegalStateException.class)
   public void testRemoveSameService() {
      // Register One Dependency
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      stringTracker.addingService(serviceReference2);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      handler.reset();
      stringTracker.removedService(serviceReference2, serviceObject2);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertTrue(handler.wasOnServiceRemovedCalled());
      Assert.assertTrue(handler.wasOnDeactivateCalled());

      // Remove Again
      handler.reset();
      stringTracker.removedService(serviceReference2, serviceObject2);
   }

   @Test
   public void testDeactivateServiceBinder() {
      // Register One Dependency
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      stringTracker.addingService(serviceReference1);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      handler.reset();
      stringTracker.addingService(serviceReference2);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertTrue(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      handler.reset();
      stringTracker.removedService(serviceReference2, serviceObject2);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertTrue(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      Pair<Class<?>, Object> removedService = handler.getServiceRemoved();
      Assert.assertNotNull(removedService);
      Assert.assertEquals(String.class, removedService.getFirst());
      Assert.assertEquals(serviceObject2, removedService.getSecond());

      handler.reset();
      stringTracker.removedService(serviceReference1, serviceObject1);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertTrue(handler.wasOnServiceRemovedCalled());
      Assert.assertTrue(handler.wasOnDeactivateCalled());

      removedService = handler.getServiceRemoved();
      Assert.assertNotNull(removedService);
      Assert.assertEquals(String.class, removedService.getFirst());
      Assert.assertEquals(serviceObject1, removedService.getSecond());
   }

   @Test
   public void testReactivateServiceBinder() {
      // Register One Dependency
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      stringTracker.addingService(serviceReference1);
      handler.reset();
      stringTracker.removedService(serviceReference1, serviceObject1);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertTrue(handler.wasOnServiceRemovedCalled());
      Assert.assertTrue(handler.wasOnDeactivateCalled());

      handler.reset();

      // Add another dependency to check that onActivate is
      // not called when dependencies that might be managed by other binders are missing
      serviceMap.put(Boolean.class, new HashSet<>());

      stringTracker.addingService(serviceReference1);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      serviceMap.remove(Boolean.class);
      stringTracker.addingService(serviceReference1);
      Assert.assertTrue(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertFalse(handler.wasOnServiceRemovedCalled());
      Assert.assertFalse(handler.wasOnDeactivateCalled());

      Map<Class<?>, Object> activated = handler.getOnActivateServices();
      Assert.assertEquals(1, activated.size());
      Assert.assertEquals(serviceObject1, activated.get(String.class));
   }

   @Test
   public void testDeactivateFollowedByRemoveServiceBinder() {
      // Register 2 Dependencies
      ServiceTracker stringTracker = serviceBinder.createTracker(String.class);
      Assert.assertNotNull(stringTracker);
      stringTracker.open();

      ServiceTracker intTracker = serviceBinder.createTracker(Integer.class);
      Assert.assertNotNull(intTracker);
      intTracker.open();

      stringTracker.addingService(serviceReference1);
      intTracker.addingService(serviceReference2);

      handler.reset();
      stringTracker.removedService(serviceReference1, serviceObject1);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertTrue(handler.wasOnServiceRemovedCalled());
      Assert.assertTrue(handler.wasOnDeactivateCalled());

      Pair<Class<?>, Object> removedService = handler.getServiceRemoved();
      Assert.assertNotNull(removedService);
      Assert.assertEquals(String.class, removedService.getFirst());
      Assert.assertEquals(serviceObject1, removedService.getSecond());

      handler.reset();
      intTracker.removedService(serviceReference2, serviceObject2);
      Assert.assertFalse(handler.wasOnActivateCalled());
      Assert.assertFalse(handler.wasOnServiceAddedCalled());
      Assert.assertTrue(handler.wasOnServiceRemovedCalled());
      Assert.assertTrue(handler.wasOnDeactivateCalled());

      removedService = handler.getServiceRemoved();
      Assert.assertNotNull(removedService);
      Assert.assertEquals(Integer.class, removedService.getFirst());
      Assert.assertEquals(serviceObject2, removedService.getSecond());
   }
}
