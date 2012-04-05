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

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

/**
 * @author Donald G. Dunne
 * @author Ryan D. Brooks
 */
public final class OseeCodeVersion {

   private OseeCodeVersion() {
      // private constructor
   }

   /**
    * @return version <b>V</b> from <code>-Dosee.version <b>V</b></code> or pull one from
    * <code>OseeCodeVersion.getBundleVersion()</code>
    */
   public static String getVersion() {
      String version = System.getProperty("osee.version", "");
      if (!Strings.isValid(version)) {
         version = getBundleVersion();
         if (isDevelopment(version)) {
            version = "Development";
         }
      }
      return version;
   }

   /**
    * @return bundle version or ""
    */
   public static String getBundleVersion() {
      Bundle bundle = FrameworkUtil.getBundle(OseeCodeVersion.class);
      Version version = null;
      if (bundle != null) {
         version = bundle.getVersion();
      }
      return version != null ? version.toString() : "";
   }

   public static boolean isDevelopment() {
      return isDevelopment(getBundleVersion());
   }

   private static boolean isDevelopment(String version) {
      // The version of this bundle ends with .qualifier until it is replaced by PDE build with a time stamp
      return !Strings.isValid(version) || version.endsWith("qualifier");
   }
}