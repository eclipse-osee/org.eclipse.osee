/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.plugin.core.internal;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.EclipseErrorLogLogger;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("deprecation")
public class Activator extends OseeActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.plugin.core";

   private static Activator instance;
   @SuppressWarnings("rawtypes")
   private ServiceTracker packageAdminTracker;

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      instance = this;

      OseeLog.registerLoggerListener(new EclipseErrorLogLogger());

      packageAdminTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
      packageAdminTracker.open();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      packageAdminTracker.close();
   }

   public static Activator getInstance() {
      return instance;
   }

   public PackageAdmin getPackageAdmin() {
      return (PackageAdmin) packageAdminTracker.getService();
   }

}
