/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.mock.internal;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Roberto E. Escobar
 */
public final class OsgiUtil {

   private OsgiUtil() {
      // Utility class
   }

   public static <T> T getService(Class<T> clazz) {
      return org.eclipse.osee.framework.core.util.OsgiUtil.getService(OsgiUtil.class, clazz);
   }

   public static <T> T getService(Class<T> clazz, String filter, long waitTimeMillis) {
      Bundle bundle = FrameworkUtil.getBundle(OsgiUtil.class);
      checkStarted(bundle);

      BundleContext context = bundle.getBundleContext();
      Assert.assertNotNull(context);

      ServiceReference<T> reference = null;
      int totalWaitTime = 0;
      do {
         try {
            Collection<ServiceReference<T>> references = context.getServiceReferences(clazz, filter);
            if (references != null && !references.isEmpty()) {
               reference = references.iterator().next();
            }
         } catch (Exception ex) {
            // do nothing;
         } finally {
            try {
               if (totalWaitTime < waitTimeMillis && reference == null) {
                  Thread.sleep(100);
                  totalWaitTime += 100;
               }
            } catch (InterruptedException ex1) {
               // do nothing;
            }
         }
      } while (totalWaitTime < waitTimeMillis && reference == null);
      Assert.assertNotNull(String.format("Unable to find service [%s]", clazz), reference);

      T service = context.getService(reference);
      Assert.assertNotNull(String.format("Unable to find service instance for [%s]", clazz), service);
      return service;
   }

   public static Bundle getBundleByName(String bundleName) {
      Bundle bundle = FrameworkUtil.getBundle(OsgiUtil.class);
      checkStarted(bundle);

      BundleContext context = bundle.getBundleContext();
      Bundle toReturn = null;
      for (Bundle item : context.getBundles()) {
         if (bundleName.equals(item.getSymbolicName())) {
            toReturn = item;
            break;
         }
      }
      return toReturn;
   }

   public static ConfigurationAdmin getConfigAdmin() {
      Bundle cmBundle = getBundleByName("org.eclipse.equinox.cm");
      checkStarted(cmBundle);
      try {
         return getService(ConfigurationAdmin.class);
      } catch (Exception ex) {
         throw new OseeCoreException(ex.getCause(), "Error acquiring configuration admin");
      }
   }

   private static void checkStarted(Bundle bundle) {
      Assert.assertNotNull("Bundle cannot be null", bundle);
      int bundleState = bundle.getState();
      if (bundleState != Bundle.STARTING && bundleState != Bundle.ACTIVE) {
         try {
            bundle.start();
         } catch (BundleException ex) {
            throw new OseeCoreException(ex.getCause(), "Error starting bundle [%s]", bundle.getSymbolicName());
         }
      }
   }

}
