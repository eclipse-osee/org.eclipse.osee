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

   public ConnectionFactoryProvider(BundleContext bundleContext) {
      this.bundleContext = bundleContext;
   }

   public IConnectionFactory get(String driver) throws OseeCoreException {
      Conditions.checkNotNull(driver, "database driver to find");
      ServiceTracker serviceTracker = new ServiceTracker(bundleContext, IConnectionFactory.class.getName(), null);
      serviceTracker.open(true);
      try {
         Object[] services = serviceTracker.getServices();
         for (Object object : services) {
            if (object instanceof IConnectionFactory) {
               IConnectionFactory factory = (IConnectionFactory) object;
               if (driver.equals(factory.getDriver())) {
                  return factory;
               }
            }
         }
      } finally {
         serviceTracker.close();
      }
      throw new OseeNotFoundException("Unable to find connection factory with driver [%s]", driver);
   }
}
