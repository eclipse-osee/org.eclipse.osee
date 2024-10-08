/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.mocks;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class MockBundleContext implements BundleContext {

   @Override
   public String getProperty(String key) {
      return null;
   }

   @Override
   public Bundle getBundle() {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public Bundle installBundle(String location, InputStream input) throws BundleException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public Bundle installBundle(String location) throws BundleException {
      return null;
   }

   @Override
   public Bundle getBundle(long id) {
      return null;
   }

   @Override
   public Bundle[] getBundles() {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
      //
   }

   @Override
   public void addServiceListener(ServiceListener listener) {
      //
   }

   @Override
   public void removeServiceListener(ServiceListener listener) {
      //
   }

   @Override
   public void addBundleListener(BundleListener listener) {
      //
   }

   @Override
   public void removeBundleListener(BundleListener listener) {
      //
   }

   @Override
   public void addFrameworkListener(FrameworkListener listener) {
      //
   }

   @Override
   public void removeFrameworkListener(FrameworkListener listener) {
      //
   }

   @Override
   public ServiceRegistration<?> registerService(String[] clazzes, Object service, Dictionary<String, ?> properties) {
      return null;
   }

   @Override
   public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
      return null;
   }

   @Override
   public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String, ?> properties) {
      return null;
   }

   @Override
   public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
      return null;
   }

   @Override
   public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter) {
      return null;
   }

   @Override
   public <S> S getService(ServiceReference<S> reference) {
      return null;
   }

   @Override
   public boolean ungetService(ServiceReference<?> reference) {
      return false;
   }

   @Override
   public Bundle getBundle(String location) {
      return null;
   }

   @Override
   public ServiceReference<?>[] getServiceReferences(String clazz, String filter) {
      return null;
   }

   @Override
   public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) {
      return null;
   }

   @Override
   public ServiceReference<?> getServiceReference(String clazz) {
      return null;
   }

   @Override
   public File getDataFile(String filename) {
      return null;
   }

   @Override
   public Filter createFilter(String filter) {
      return null;
   }

   @Override
   public <S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory,
      Dictionary<String, ?> properties) {
      return null;
   }

   @Override
   public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
      return null;
   }

}