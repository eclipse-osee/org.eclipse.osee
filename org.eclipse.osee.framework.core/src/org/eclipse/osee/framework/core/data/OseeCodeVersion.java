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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.CoreActivator;

/**
 * @author Donald G. Dunne
 * @author Ryan D. Brooks
 */
public final class OseeCodeVersion {

   private OseeCodeVersion() {
   }

   /**
    * Gets version
    * 
    * @return the version
    */
   public static String getVersion() {
      String bundleVersion = getBundleVersion();
      if (isDevelopment(bundleVersion)) {
         return "Development";
      }
      return bundleVersion;
   }

   private static String getBundleVersion() {
      return (String) CoreActivator.getBundleContext().getBundle().getHeaders().get(
            org.osgi.framework.Constants.BUNDLE_VERSION);
   }

   public static boolean isDevelopment() {
      return isDevelopment(getBundleVersion());
   }

   private static boolean isDevelopment(String version) {
      // The version of this bundle ends with .qualifier until it is replaced by PDE build with a time stamp
      return version.endsWith("qualifier");
   }
}