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
package org.eclipse.osee.framework.core.server;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeServerProperties {
   private static final String OSEE_APPLICATION_SERVER_DATA = "osee.application.server.data";
   private static final String OSGI_PORT_PROPERTY = "org.osgi.service.http.port";
   private static final String CHECK_TAG_QUEUE_ON_START_UP = "osee.check.tag.queue.on.startup";
   private static final String OSEE_VERSION = "osee.version";

   private static boolean wasBinaryDataChecked = false;

   private OseeServerProperties() {
      super();
   }

   /**
    * Get OSEE application server version settings
    * 
    * @return OSEE application server versions
    */
   public static String[] getOseeVersion() {
      String[] toReturn = new String[0];
      String versionString = System.getProperty(OSEE_VERSION, "");
      if (Strings.isValid(versionString)) {
         toReturn = versionString.split(";");
      }
      return toReturn;
   }

   private static String internalGetOseeApplicationServerData() {
      String toReturn = System.getProperty(OSEE_APPLICATION_SERVER_DATA);
      if (toReturn == null) {
         String userHome = System.getProperty("user.home");
         if (Strings.isValid(userHome)) {
            toReturn = userHome;
         }
      }
      return toReturn;
   }

   /**
    * Get location for OSEE application server binary data
    * 
    * @return OSEE application server binary data path
    */
   public static String getOseeApplicationServerData() {
      String toReturn = internalGetOseeApplicationServerData();
      if (!wasBinaryDataChecked) {
         File file = new File(toReturn);
         if (file.exists()) {
            OseeLog.log(CoreServerActivator.class, Level.INFO, String.format("Application Server Data: [%s]", toReturn));
         } else {
            OseeLog.log(CoreServerActivator.class, Level.WARNING, String.format(
                  "Application Server Data: [%s] does not exist and will be created", toReturn));
         }
         wasBinaryDataChecked = true;
      }
      return toReturn;
   }

   /**
    * Retrieve the application server port
    * 
    * @return the application server port
    */
   public static int getOseeApplicationServerPort() {
      return Integer.valueOf(System.getProperty(OSGI_PORT_PROPERTY, "-1"));
   }

   /**
    * Check Tag Queue on start up. Entries found in the tag queue are tagged by the server on start up.
    * 
    * @return whether tag queue should be checked upon server start-up.
    */
   public static boolean isCheckTagQueueOnStartupAllowed() {
      return Boolean.valueOf(System.getProperty(CHECK_TAG_QUEUE_ON_START_UP, "false"));
   }

   /**
    * Retrieve the connection info file location
    * 
    * @return connection info file URI
    */
   public static String getOseeConnectionInfoUri() {
      return OseeProperties.getOseeConnectionInfoUri();
   }
}
