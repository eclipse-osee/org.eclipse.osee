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
package org.eclipse.osee.framework.plugin.core.internal;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.EclipseErrorLogLogger;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("deprecation")
public class PluginCoreActivator extends OseeActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.plugin.core";

   private static PluginCoreActivator pluginInstance;
   @SuppressWarnings("rawtypes")
   private ServiceTracker packageAdminTracker;

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      pluginInstance = this;

      OseeLog.registerLoggerListener(new EclipseErrorLogLogger());

      packageAdminTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
      packageAdminTracker.open();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      packageAdminTracker.close();
   }

   public static PluginCoreActivator getInstance() {
      return pluginInstance;
   }

   public PackageAdmin getPackageAdmin() {
      return (PackageAdmin) packageAdminTracker.getService();
   }

}