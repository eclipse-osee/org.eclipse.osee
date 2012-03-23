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

package org.eclipse.osee.ats.internal;

import java.util.logging.Level;
import org.eclipse.osee.ats.core.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.util.AtsCacheManager;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionReportingService;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Donald G. Dunne
 */
public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.ats";

   private ServiceRegistration service1;
   private ServiceRegistration service2;
   private ServiceDependencyTracker tracker;

   private volatile boolean needsInitialization = true;

   private Thread thread;

   @Override
   public void start(BundleContext context) throws Exception {
      service1 =
         context.registerService(IActionReportingService.class.getName(), new AtsActionReportingServiceImpl(), null);
      service2 = context.registerService(IOseeCmService.class.getName(), new OseeAtsServiceImpl(), null);

      tracker = new ServiceDependencyTracker(context, new AtsCmAccessControlRegHandler());
      tracker.open();

      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            if (needsInitialization) {
               needsInitialization = false;
               AtsCacheManager.start();
               AtsBranchManager.start();
               try {
                  AtsNotificationManager.start(OseeNotificationManager.getInstance(),
                     ClientSessionManager.isProductionDataStore());
               } catch (OseeAuthenticationRequiredException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      };

      thread = new Thread(runnable);
      thread.start();

   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (thread != null) {
         thread.interrupt();
         thread = null;
      }
      AtsBranchManager.stop();
      OsgiUtil.close(tracker);
      OsgiUtil.close(service1);
      OsgiUtil.close(service2);
   }

}
