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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public final class OsgiUtil {

   private OsgiUtil() {
      // Utility Class;
   }

   public static void close(ServiceTracker<?, ?> tracker) {
      if (tracker != null) {
         try {
            tracker.close();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, ex);
         }
      }
   }

   public static <T> T getService(Class<?> classFromBundle, Class<T> clazz) {

      BundleContext context = FrameworkUtil.getBundle(classFromBundle).getBundleContext();
      if (context == null) {
         throw new OseeCoreException("BundleContext is null for " + classFromBundle);
      }
      ServiceReference<T> serviceReference = context.getServiceReference(clazz);
      if (serviceReference == null) {
         throw new OseeCoreException("ServiceReference is null for class" + clazz);
      }
      T service = context.getService(serviceReference);
      if (service == null) {
         throw new OseeCoreException("getService is null for class" + clazz);
      }
      return service;
   }

   public static InputStream getResourceAsStream(Class<?> classFromBundle, String resourcePath) {
      try {
         return new BufferedInputStream(getResourceAsUrl(classFromBundle, resourcePath).openStream());
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static URL getResourceAsUrl(Class<?> classFromBundle, String resourcePath) {
      try {
         Bundle bundle = FrameworkUtil.getBundle(classFromBundle);
         URL resourceUrl = bundle.getResource(resourcePath);
         return FileLocator.toFileURL(resourceUrl);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static String getResourceAsString(Class<?> classFromBundle, String resourcePath) {
      try {
         return Lib.inputStreamToString(getResourceAsStream(classFromBundle, resourcePath));
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}
