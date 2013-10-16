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

import java.util.Dictionary;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public final class OsgiUtil {

   private OsgiUtil() {
      // Utility class
   }

   public static <T> T getService(Class<T> clazz) throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(OsgiUtil.class);
      Assert.assertNotNull(bundle);

      int bundleState = bundle.getState();
      if (bundleState != Bundle.STARTING && bundleState != Bundle.ACTIVE) {
         try {
            bundle.start();
         } catch (BundleException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }

      BundleContext context = bundle.getBundleContext();
      Assert.assertNotNull(context);

      ServiceReference<T> reference = context.getServiceReference(clazz);
      Assert.assertNotNull(String.format("Unable to find service [%s]", clazz), reference);

      T service = context.getService(reference);
      Assert.assertNotNull(String.format("Unable to find service instance for [%s]", clazz), service);

      return service;
   }

   public static <S> ServiceRegistration<S> registerService(Class<S> class1, S service, Dictionary<String, ?> properties) throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(OsgiUtil.class);
      Assert.assertNotNull(bundle);

      int bundleState = bundle.getState();
      if (bundleState != Bundle.STARTING && bundleState != Bundle.ACTIVE) {
         try {
            bundle.start();
         } catch (BundleException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      BundleContext context = bundle.getBundleContext();
      Assert.assertNotNull(context);

      return context.registerService(class1, service, properties);
   }
}
