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

package org.eclipse.osee.framework.db.connection.core;

import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.Activator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class OseeCodeVersion {

   // Until release, version returned from getOseeVersion will be DEFAULT_DEVELOPMENT_VERSION
   public static String DEFAULT_DEVELOPMENT_VERSION = "Development";
   private String oseeVersion = DEFAULT_DEVELOPMENT_VERSION;
   private static OseeCodeVersion instance = new OseeCodeVersion();

   private OseeCodeVersion() {
   }

   public static OseeCodeVersion getInstance() {
      return instance;
   }

   public String get() {
      if (oseeVersion == null) {
         try {
            if (Activator.getInstance().getBundleContext().getBundle().getEntry("support/OseeCodeVersion.txt") != null) {
               InputStream is =
                     Activator.getInstance().getBundleContext().getBundle().getEntry("support/OseeCodeVersion.txt").openStream();
               if (is != null) {
                  oseeVersion = Lib.inputStreamToString(is);
                  oseeVersion = oseeVersion.replace("0=", "");
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class.getName(), Level.SEVERE,
                  "Can't access OseeVersion.txt\n" + Lib.exceptionToString(ex));
         }
      }
      return oseeVersion;
   }

   public boolean isDevelopmentVersion() {
      return get().equals(DEFAULT_DEVELOPMENT_VERSION);
   }

   /**
    * This method public for testing purposes only
    * 
    * @param oseeVersion the oseeVersion to set
    */
   public void set(String oseeVersion) {
      this.oseeVersion = oseeVersion;
   }

}
