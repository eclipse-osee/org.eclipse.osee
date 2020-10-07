/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.mail.notifier;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ajay Chandrahasan
 */
public class ICTeamExportClassLoader extends ClassLoader {

   private static ICTeamExportClassLoader exportClassloaderInstance;
   private static ServiceTracker packageAdminTracker;
   private final PackageAdmin packageAdmin;

   public static ICTeamExportClassLoader getInstance() {
      if (exportClassloaderInstance == null) {
         exportClassloaderInstance = new ICTeamExportClassLoader();
      }
      return exportClassloaderInstance;
   }

   public ICTeamExportClassLoader(PackageAdmin packageAdmin) {
      super(ICTeamExportClassLoader.class.getClassLoader());
      this.packageAdmin = packageAdmin;
   }

   public ICTeamExportClassLoader() {
      this(getPackageAdmin());
   }

   public static PackageAdmin getPackageAdmin() {
      packageAdminTracker =
         new ServiceTracker(Platform.getBundle("org.eclipse.osee.icteam.job.scheduler").getBundleContext(),
            PackageAdmin.class.getName(), null);
      packageAdminTracker.open();
      return (PackageAdmin) packageAdminTracker.getService();
   }

   @Override
   protected Class<?> findClass(String name) throws ClassNotFoundException {
      try {
         Bundle bundle = getExportingBundle(name);
         if (bundle != null) {
            return bundle.loadClass(name);
         }
         throw new ClassNotFoundException("could not locate a class for " + name);
      } catch (Exception e) {
         throw new ClassNotFoundException("could not locate a class for " + name, e);
      }
   }

   public Bundle getExportingBundle(String name) {
      final String pkg = name.substring(0, name.lastIndexOf('.'));
      ExportedPackage[] list = packageAdmin.getExportedPackages(pkg);
      if (list != null) {
         for (ExportedPackage ep : list) {
            final Bundle bundle = ep.getExportingBundle();
            final int state = bundle.getState();
            if (state == Bundle.RESOLVED || state == Bundle.STARTING || state == Bundle.ACTIVE || state == Bundle.STOPPING) {
               return bundle;
            }
         }
      }
      return null;
   }

}
