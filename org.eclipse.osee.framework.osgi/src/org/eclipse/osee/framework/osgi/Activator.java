/*******************************************************************************
 * Copyright (c) 2004, 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class is a BundleActivator and is responsible for starting all installed bundles. This is an automated way to
 * get an osgi framework launched without having to maintain a config ini file as new bundles are developed.
 * 
 * @author Ryan D. Brooks
 */
public class Activator implements BundleActivator {
   private LogService logservice;

   public void start(BundleContext context) throws Exception {
      ServiceTracker logServiceTracker = new ServiceTracker(context, LogService.class.getName(), null);
      logServiceTracker.open();
      logservice = (LogService) logServiceTracker.getService();

      for (Bundle bundle : context.getBundles()) {
         start(bundle);
      }

      context.addBundleListener(new BundleListener() {
         @Override
         public void bundleChanged(BundleEvent event) {
            if (event.getType() == BundleEvent.INSTALLED) {
               start(event.getBundle());
            }
         }
      });
   }

   private void start(Bundle bundle) {
      try {
         if (bundle.getState() == Bundle.RESOLVED) {
            bundle.start(Bundle.START_TRANSIENT);
         }
      } catch (BundleException ex) {
         if (logservice != null) {
            logservice.log(LogService.LOG_ERROR, ex.getMessage(), ex);
         }
      }
   }

   public void stop(BundleContext context) throws Exception {
   }
}