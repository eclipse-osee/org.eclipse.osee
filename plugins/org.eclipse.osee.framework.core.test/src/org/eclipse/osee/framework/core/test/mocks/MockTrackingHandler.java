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
package org.eclipse.osee.framework.core.test.mocks;

import java.util.Map;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class MockTrackingHandler extends AbstractTrackingHandler {

   private boolean wasOnActivateCalled;
   private boolean wasOnDeactivateCalled;
   private boolean wasOnServiceAddedCalled;
   private boolean wasOnServiceRemovedCalled;
   private Map<Class<?>, Object> onActivateServices;
   private Pair<Class<?>, Object> serviceRemoved;
   private Pair<Class<?>, Object> serviceAdded;

   private final BundleContext expContext;
   private final Class<?>[] singletonDependencies;

   public MockTrackingHandler(BundleContext expContext, Class<?>... singletonDependencies) {
      this.expContext = expContext;
      this.singletonDependencies = singletonDependencies;
   }

   public void reset() {
      wasOnActivateCalled = false;
      wasOnDeactivateCalled = false;
      wasOnServiceAddedCalled = false;
      wasOnServiceRemovedCalled = false;
      onActivateServices = null;
      serviceRemoved = null;
      serviceAdded = null;
   }

   @Override
   public Class<?>[] getDependencies() {
      return singletonDependencies;
   }

   @Override
   public Map<Class<?>, ServiceBindType> getConfiguredDependencies() {
      return super.getConfiguredDependencies();
   }

   @Override
   public void onServiceAdded(BundleContext context, Class<?> clazz, Object services) {
      wasOnServiceAddedCalled = true;
      Assert.assertEquals(expContext, context);
      serviceAdded = new Pair<Class<?>, Object>(clazz, services);
   }

   @Override
   public void onServiceRemoved(BundleContext context, Class<?> clazz, Object services) {
      wasOnServiceRemovedCalled = true;
      Assert.assertEquals(expContext, context);
      serviceRemoved = new Pair<Class<?>, Object>(clazz, services);
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      wasOnActivateCalled = true;
      Assert.assertEquals(expContext, context);
      this.onActivateServices = services;
   }

   @Override
   public void onDeActivate() {
      wasOnDeactivateCalled = true;
   }

   public Pair<Class<?>, Object> getServiceRemoved() {
      return serviceRemoved;
   }

   public Pair<Class<?>, Object> getServiceAdded() {
      return serviceAdded;
   }

   public Map<Class<?>, Object> getOnActivateServices() {
      return onActivateServices;
   }

   public boolean wasOnActivateCalled() {
      return wasOnActivateCalled;
   }

   public boolean wasOnDeactivateCalled() {
      return wasOnDeactivateCalled;
   }

   public boolean wasOnServiceAddedCalled() {
      return wasOnServiceAddedCalled;
   }

   public boolean wasOnServiceRemovedCalled() {
      return wasOnServiceRemovedCalled;
   }

}
