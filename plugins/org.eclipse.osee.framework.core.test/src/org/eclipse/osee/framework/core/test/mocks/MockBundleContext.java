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

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
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

   @SuppressWarnings("rawtypes")
   @Override
   public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
      return null;
   }

   @SuppressWarnings("rawtypes")
   @Override
   public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
      return null;
   }

   @Override
   public ServiceReference getServiceReference(String clazz) {
      return null;
   }

   @Override
   public Object getService(ServiceReference reference) {
      return null;
   }

   @Override
   public boolean ungetService(ServiceReference reference) {
      return false;
   }

   @Override
   public File getDataFile(String filename) {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public Filter createFilter(String filter) throws InvalidSyntaxException {
      return null;
   }

}