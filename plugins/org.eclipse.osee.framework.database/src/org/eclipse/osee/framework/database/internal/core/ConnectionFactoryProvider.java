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

package org.eclipse.osee.framework.database.internal.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public class ConnectionFactoryProvider {

   private final BundleContext bundleContext;
   private final Map<String, IConnectionFactory> factoryMap = new HashMap<String, IConnectionFactory>();

   public ConnectionFactoryProvider(BundleContext bundleContext) {
      this.bundleContext = bundleContext;
   }

   public IConnectionFactory get(String driver) throws OseeCoreException {
      Conditions.checkNotNull(driver, "database driver to find");
      ServiceTracker serviceTracker = new ServiceTracker(bundleContext, IConnectionFactory.class.getName(), null);
      serviceTracker.open(true);
      try {
         Object[] services = serviceTracker.getServices();
         if (services != null) {
            for (Object object : services) {
               if (object instanceof IConnectionFactory) {
                  IConnectionFactory factory = (IConnectionFactory) object;
                  if (driver.equals(factory.getDriver())) {
                     return factory;
                  }
               }
            }
         }

         IConnectionFactory factory = factoryMap.get(driver);
         if (factory == null) {
            factory = new DefaultConnectionFactory(driver);
            factoryMap.put(driver, factory);
         }
         return factory;

      } finally {
         serviceTracker.close();
      }
   }
   private static final class DefaultConnectionFactory implements IConnectionFactory {

      private final String driver;

      public DefaultConnectionFactory(String driver) {
         this.driver = driver;
      }

      @Override
      public Connection getConnection(Properties properties, String connectionURL) throws Exception {
         try {
            Class.forName(driver);
         } catch (Exception ex) {
            throw new OseeNotFoundException("Unable to find connection factory with driver [%s]", driver);
         }
         return DriverManager.getConnection(connectionURL, properties);
      }

      @Override
      public String getDriver() {
         return driver;
      }
   }
}
