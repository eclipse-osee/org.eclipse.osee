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
package org.eclipse.osee.framework.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.mocks.MockBundleContext;
import org.eclipse.osee.framework.core.mocks.MockTrackingHandler;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Test Case for {@link ServiceDependencyTracker}
 * 
 * @author Roberto E. Escobar
 */
public class ServiceDependencyTrackerTest {

   private static MockBundleContext context;
   private static MockTrackingHandler handler;

   @BeforeClass
   public static void setup() {
      context = new MockBundleContext() {
         @Override
         public Object getService(ServiceReference reference) {
            Assert.assertNotNull(reference);
            return null;
         }
      };

      handler = new MockTrackingHandler(context, String.class) {

         @Override
         public Map<Class<?>, ServiceBindType> getConfiguredDependencies() {
            Map<Class<?>, ServiceBindType> map = new LinkedHashMap<>();
            map.putAll(super.getConfiguredDependencies());
            map.put(Integer.class, ServiceBindType.MANY);
            return map;
         }

      };
   }

   @Test
   public void testOpen()  {
      MockServiceBinderFactory factory = new MockServiceBinderFactory();
      ServiceDependencyTracker depTracker = new ServiceDependencyTracker(factory, context, handler);

      depTracker.open();

      Collection<MockServiceTracker> trackers = factory.getTracker();
      Assert.assertEquals(2, trackers.size());

      Iterator<MockServiceTracker> iterator = trackers.iterator();
      MockServiceTracker tracker = iterator.next();
      Assert.assertTrue(tracker.wasOpenCalledWithTrackAll());
      Assert.assertFalse(tracker.wasOpenCalled());
      Assert.assertFalse(tracker.wasCloseCalled());

      Assert.assertEquals(String.class, tracker.getClazz());
      Assert.assertEquals(ServiceBindType.SINGLETON, tracker.getBindType());

      tracker = iterator.next();
      Assert.assertTrue(tracker.wasOpenCalledWithTrackAll());
      Assert.assertFalse(tracker.wasOpenCalled());
      Assert.assertFalse(tracker.wasCloseCalled());

      Assert.assertEquals(Integer.class, tracker.getClazz());
      Assert.assertEquals(ServiceBindType.MANY, tracker.getBindType());
   }

   @Test
   public void testClose()  {
      MockServiceBinderFactory factory = new MockServiceBinderFactory();
      ServiceDependencyTracker depTracker = new ServiceDependencyTracker(factory, context, handler);

      depTracker.open();

      Collection<MockServiceTracker> trackers = factory.getTracker();
      Assert.assertEquals(2, trackers.size());
      for (MockServiceTracker tracker : trackers) {
         tracker.reset();
      }
      depTracker.close();
      for (MockServiceTracker tracker : trackers) {
         Assert.assertFalse(tracker.wasOpenCalledWithTrackAll());
         Assert.assertFalse(tracker.wasOpenCalled());
         Assert.assertTrue(tracker.wasCloseCalled());
      }
   }

   private final static class MockServiceBinderFactory implements ServiceBinderFactory {
      private final Collection<MockServiceTracker> trackers = new ArrayList<>();

      @Override
      public ServiceTracker createTracker(ServiceBindType bindType, Class<?> clazz) {
         MockServiceTracker tracker = new MockServiceTracker(context, bindType, clazz);
         trackers.add(tracker);
         return tracker;
      }

      public Collection<MockServiceTracker> getTracker() {
         return trackers;
      }
   }

   private static final class MockServiceTracker extends ServiceTracker {

      private boolean wasOpenCalled;
      private boolean wasOpenCalledWithTrackAll;
      private boolean wasCloseCalled;

      private final Class<?> clazz;
      private final ServiceBindType bindType;

      public MockServiceTracker(BundleContext context, ServiceBindType bindType, Class<?> clazz) {
         super(context, clazz.getName(), null);
         this.clazz = clazz;
         this.bindType = bindType;
      }

      public void reset() {
         wasOpenCalled = false;
         wasOpenCalledWithTrackAll = false;
         wasCloseCalled = false;
      }

      public Class<?> getClazz() {
         return clazz;
      }

      public ServiceBindType getBindType() {
         return bindType;
      }

      @Override
      public void open() {
         super.open();
         wasOpenCalled = true;
      }

      @Override
      public void open(boolean trackAllServices) {
         super.open(trackAllServices);
         wasOpenCalledWithTrackAll = true;
      }

      @Override
      public void close() {
         super.close();
         wasCloseCalled = true;
      }

      public boolean wasOpenCalled() {
         return wasOpenCalled;
      }

      public boolean wasOpenCalledWithTrackAll() {
         return wasOpenCalledWithTrackAll;
      }

      public boolean wasCloseCalled() {
         return wasCloseCalled;
      }

   }
}
