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
package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.BroadcastEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.OseeFormActivator;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.access.OseeSecurityManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class SkynetGuiPlugin extends OseeFormActivator implements IEventReceiver {
   private static SkynetGuiPlugin pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.skynet";
   public static final String CHANGE_REPORT_ATTRIBUTES_PREF =
         "org.eclipse.osee.framework.ui.skynet.changeReportAttributes";
   public static final String ARTIFACT_EXPLORER_ATTRIBUTES_PREF =
         "org.eclipse.osee.framework.ui.skynet.artifactExplorerAttributes";
   public static OseeSecurityManager securityManager;
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetGuiPlugin.class);
   private ServiceTracker packageAdminTracker;

   public SkynetGuiPlugin() {
      super();
      pluginInstance = this;
      securityManager = OseeSecurityManager.getInstance();
      SkynetEventManager.getInstance().register(BroadcastEvent.class, this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.OseeFormActivator#stop(org.osgi.framework.BundleContext)
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      packageAdminTracker.close();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.OseeUiActivator#start(org.osgi.framework.BundleContext)
    */
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      packageAdminTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
      packageAdminTracker.open();
   }

   /**
    * Returns the shared instance.
    */
   public static SkynetGuiPlugin getInstance() {
      return pluginInstance;
   }

   public static Logger getLogger() {
      return logger;
   }

   public void onEvent(Event event) {
      if (event instanceof BroadcastEvent) AWorkbench.popup("Broadcast Message", ((BroadcastEvent) event).getMessage());
   }

   public boolean runOnEventInDisplayThread() {
      return false;
   }

   /* (non-Javadoc)
    * @see osee.plugin.core.util.plugin.OseePlugin#getPluginName()
    */
   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   /**
    * @return
    */
   public PackageAdmin getPackageAdmin() {
      return (PackageAdmin) this.packageAdminTracker.getService();
   }
}