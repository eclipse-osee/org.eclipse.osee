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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.CoreActivator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class OseeCodeVersion {

   // Until release, version returned from getOseeVersion will be DEFAULT_DEVELOPMENT_VERSION
   private static final String DEFAULT_DEVELOPMENT_VERSION = "Development";
   private static final String VERSION_FILE_PATH = "/support/OseeCodeVersion.txt";

   private static OseeCodeVersion instance = new OseeCodeVersion();

   private String oseeVersion;

   private OseeCodeVersion() {
      oseeVersion = null;
   }

   private static OseeCodeVersion getInstance() {
      return instance;
   }

   /**
    * Gets version
    * 
    * @return the version
    */
   public static String getVersion() {
      return getInstance().get();
   }

   public static boolean isDevelopment() {
      return getVersion().equalsIgnoreCase(DEFAULT_DEVELOPMENT_VERSION);
   }

   private String get() {
      if (oseeVersion == null) {
         try {
            oseeVersion = loadVersionInfo();
            if (oseeVersion != null) {
               oseeVersion = oseeVersion.replace("^0=", "");
               oseeVersion = oseeVersion.trim();
            }
         } catch (Exception ex) {
            OseeLog.log(CoreActivator.class, Level.SEVERE, "Can't access OseeVersion.txt\n" + Lib.exceptionToString(ex));
         }
         if (oseeVersion == null) {
            oseeVersion = DEFAULT_DEVELOPMENT_VERSION;
         }
      }
      return oseeVersion;
   }

   private String loadVersionInfo() throws IOException {
      String toReturn = null;
      Bundle bundle = CoreActivator.getInstance().getBundleContext().getBundle();
      URL url = bundle.getEntry(VERSION_FILE_PATH);
      if (url != null) {
         InputStream inputStream = null;
         try {
            inputStream = new BufferedInputStream(url.openStream());
            toReturn = Lib.inputStreamToString(inputStream);
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }
      }
      return toReturn;
   }
}
