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

import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsPreSaveCacheRemoteEventHandler;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.plugin.core.IActionReportingService;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Donald G. Dunne
 */
public class AtsPlugin implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.ats";

   private static AtsPlugin pluginInstance;
   private ServiceRegistration service1;
   private ServiceRegistration service2;
   private ServiceDependencyTracker tracker;
   private AtsCmAccessControlRegHandler cmAccessHandler;

   public AtsPlugin() {
      super();
      AtsPlugin.pluginInstance = this;
      AtsPreSaveCacheRemoteEventHandler.start();
      AtsCacheManager.start();
      AtsNotifyUsers.start();
   }

   @Override
   public void start(BundleContext context) throws Exception {
      service1 =
         context.registerService(IActionReportingService.class.getName(), new AtsActionReportingServiceImpl(), null);
      service2 = context.registerService(IOseeCmService.class.getName(), new OseeAtsServiceImpl(), null);

      // TODO Uncomment to re-enable ATS CM Access providing
      //      if (OseeInfo.isEnabled("atsAccessEnabled")) {
      cmAccessHandler = new AtsCmAccessControlRegHandler();
      //         tracker = new ServiceDependencyTracker(context, cmAccessHandler);
      //         tracker.open();
      //      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      OsgiUtil.close(tracker);
      OsgiUtil.close(service1);
      OsgiUtil.close(service2);
   }

   public static AtsPlugin getInstance() {
      return pluginInstance;
   }

   public CmAccessControl getCmService() {
      return cmAccessHandler.getCmService();
   }

}
