/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.rest.internal.notify;

import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * @author Ken J. Aguilar
 */
@SuppressWarnings("deprecation")
public class ExportClassLoader extends ClassLoader {

   private static ExportClassLoader exportClassloaderInstance;
   private final PackageAdmin packageAdmin;

   public static ExportClassLoader getInstance() {
      if (exportClassloaderInstance == null) {
         exportClassloaderInstance = new ExportClassLoader();
      }
      return exportClassloaderInstance;
   }

   public ExportClassLoader(PackageAdmin packageAdmin) {
      super(ExportClassLoader.class.getClassLoader());
      this.packageAdmin = packageAdmin;
   }

   public ExportClassLoader() {
      this(ServiceUtil.getPackageAdmin());
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
