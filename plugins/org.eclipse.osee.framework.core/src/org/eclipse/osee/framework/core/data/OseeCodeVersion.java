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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Donald G. Dunne
 * @author Ryan D. Brooks
 */
public final class OseeCodeVersion {

   private static final String DEVELOPMENT = "Development";
   private static String version, bundleVersion;
   public static Pattern BuildStrPattern =
      Pattern.compile("([\\d]{1,2})\\.([\\d]{1,2})\\.([\\d]{1,2})\\.v([\\d]{12})-.*$");
   public static Pattern DevStrPattern = Pattern.compile("([\\d]{1,2})\\.([\\d]{1,2})\\.([\\d]{1,2})\\.qualifier");
   private static Long versionId;

   private OseeCodeVersion() {
      // private constructor
   }

   /**
    * @return version <b>V</b> from <code>-Dosee.version <b>V</b></code> or pull one from
    * <code>OseeCodeVersion.getBundleVersion()</code>
    */
   public static String getVersion() {
      String ver = version;
      if (!Strings.isValid(ver)) {
         ver = System.getProperty("osee.version", "");
         if (!Strings.isValid(ver)) {
            ver = getBundleVersion();
            if (isDevelopment(ver)) {
               ver = DEVELOPMENT;
            }
         }
      }
      return ver;
   }

   /**
    * Since the build is mostly a long anyway (eg: 0.25.2.v201708012108-NR), </br>
    * construct a long that can be stored in the database that represents build as</br>
    * </br>
    * nn rr yyyy mm dd hh mm b</br>
    * </br>
    * where "nn rr" is 2502 representing 0.25.2 build</br>
    * </br>
    * and b is </br>
    * </br>
    * 0 - unknown</br>
    * 1 - rel</br>
    * 2 - nr beta</br>
    * 3 - nr alpha</br>
    * 4 - dev beta</br>
    * 5 - dev alpha</br>
    * </br>
    * Constructed as a string and turn into long.</br>
    * Should be 19 digits which is max for long.</br>
    * For runtime transactions, only "nn rr" will show cause there is no build id
    *
    * @return long representation (as above) or hashcode for string "Developement" if dev or 0 if build not correctly
    * formatted or exception happens
    */
   public static Long getVersionId() {
      if (versionId == null) {
         versionId = computeVersionId();
      }
      return versionId;
   }

   static Long computeVersionId() {
      Long versionId = 0L;
      try {
         String devVersion = getVersion();
         boolean isDev = devVersion.toLowerCase().endsWith("development");
         String bundleVersion = getBundleVersion();
         Matcher m = BuildStrPattern.matcher(bundleVersion);
         if (m.find()) {
            String major = m.group(1);
            if (major.length() == 1) {
               major = "0" + major;
            }
            String minor = m.group(2);
            if (minor.length() == 1) {
               minor = "0" + minor;
            }
            String inc = m.group(3);
            if (inc.length() == 1) {
               inc = "0" + inc;
            }
            String dayTimeStamp = m.group(4);
            int buildId = 0;
            if (bundleVersion.endsWith("-REL")) {
               buildId = 1;
            } else if (bundleVersion.endsWith("-NR")) {
               if (isDev) {
                  buildId = 3;
               } else {
                  buildId = 2;
               }
            } else if (bundleVersion.endsWith("-DEV")) {
               if (isDev) {
                  buildId = 5;
               } else {
                  buildId = 4;
               }
            }
            String buildStr = String.format("%s%s%s%s%s", major, minor, inc, dayTimeStamp, buildId);
            versionId = Long.valueOf(buildStr);
         } else {
            m = DevStrPattern.matcher(bundleVersion);
            if (m.find()) {
               String major = m.group(1);
               if (major.length() == 1) {
                  major = "0" + major;
               }
               String minor = m.group(2);
               if (minor.length() == 1) {
                  minor = "0" + minor;
               }
               String inc = m.group(3);
               if (inc.length() == 1) {
                  inc = "0" + inc;
               }
               String buildStr = String.format("%s%s%s", major, minor, inc);
               versionId = Long.valueOf(buildStr);
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return versionId;
   }

   /**
    * @return bundle version or ""
    */
   public static String getBundleVersion() {
      String version = bundleVersion;
      if (!Strings.isValid(version)) {
         Bundle bundle = FrameworkUtil.getBundle(OseeCodeVersion.class);
         if (bundle != null && bundle.getVersion() != null) {
            version = bundle.getVersion().toString();
         }
      }
      return version;
   }

   public static boolean isDevelopment() {
      return isDevelopment(getBundleVersion());
   }

   private static boolean isDevelopment(String version) {
      // The version of this bundle ends with .qualifier until it is replaced by PDE build with a time stamp
      return !Strings.isValid(version) || version.endsWith("qualifier");
   }

   public static void setVersion(String version) {
      OseeCodeVersion.version = version;
   }

   public static void setBundleVersion(String bundleVersion) {
      OseeCodeVersion.bundleVersion = bundleVersion;
   }
}