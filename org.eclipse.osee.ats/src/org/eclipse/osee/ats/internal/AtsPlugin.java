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
import org.eclipse.osee.framework.plugin.core.IActionReportingService;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.skynet.ats.IOseeAtsService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Donald G. Dunne
 */
public class AtsPlugin extends OseeUiActivator {
   private static AtsPlugin pluginInstance;
   public static final String PLUGIN_ID = "org.eclipse.osee.ats";

   private ServiceRegistration service1;
   private ServiceRegistration service2;

   public AtsPlugin() {
      super();
      pluginInstance = this;
      AtsPreSaveCacheRemoteEventHandler.getInstance();
      AtsCacheManager.start();
      AtsNotifyUsers.getInstance();
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      service1 =
            context.registerService(IActionReportingService.class.getName(), new AtsActionReportingServiceImpl(), null);
      service2 = context.registerService(IOseeAtsService.class.getName(), new OseeAtsServiceImpl(), null);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (service1 != null) {
         service1.unregister();
      }
      if (service2 != null) {
         service2.unregister();
      }
      super.stop(context);
   }

   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   public static AtsPlugin getInstance() {
      return pluginInstance;
   }

}
