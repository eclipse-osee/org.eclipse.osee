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

package org.eclipse.osee.framework.core.server;

import java.io.File;
import java.util.Optional;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

public class OseeServerProperties {

   /**
    * System property name for the servers root URL.
    */

   private static final String OSEE_APPLICATION_SERVER = "OseeApplicationServer";

   /**
    * System property name for the server's data directory.
    */

   private static final String OSEE_APPLICATION_SERVER_DATA = "osee.application.server.data";

   private static final String OSEE_AUTHENTICATION_PROTOCOL = "osee.authentication.protocol";

   private static final String OSGI_PORT_PROPERTY = "org.osgi.service.http.port";

   private static final String OSGI_SECURE_PORT_PROPERTY = "org.osgi.service.http.port.secure";

   private static boolean wasBinaryDataChecked = false;

   /**
    * Authentication Protocol to use
    *
    * @return client/server authentication protocol.
    */

   public static String getAuthenticationProtocol() {
      return System.getProperty(OSEE_AUTHENTICATION_PROTOCOL, "");
   }

   /**
    * Get location for OSEE application server binary data
    *
    * @return OSEE application server binary data path
    */

   public static String getOseeApplicationServerData(Log logger) {
      String toReturn = internalGetOseeApplicationServerData();
      if (toReturn != null) {
         if (!wasBinaryDataChecked) {
            File file = new File(toReturn);
            if (logger != null) {
               if (file.exists()) {
                  logger.info("Application Server Data: [%s]", toReturn);
               } else {
                  logger.warn("Application Server Data: [%s] does not exist and will be created", toReturn);
               }
            }
            wasBinaryDataChecked = true;
         }
      }
      return toReturn;
   }

   /**
    * Gets the system property for the OSEE application server URL.
    *
    * @return when the property is set an {@link Optional} with the system property value; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<String> getOseeApplicationServer() {
      return Optional.ofNullable(System.getProperty(OseeServerProperties.OSEE_APPLICATION_SERVER));
   }

   /**
    * Gets the system property for the OSEE application server data directory.
    *
    * @return when the property is set an {@link Optional} with the system property value; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<String> getOseeApplicationServerData() {
      return Optional.ofNullable(System.getProperty(OseeServerProperties.OSEE_APPLICATION_SERVER_DATA));
   }

   /**
    * Retrieve the application server port
    *
    * @return the application server port
    */

   public static int getOseeApplicationServerPort() {
      int toReturn = Integer.valueOf(System.getProperty(OSGI_SECURE_PORT_PROPERTY, "-1"));
      if (toReturn == -1) {
         toReturn = Integer.valueOf(System.getProperty(OSGI_PORT_PROPERTY, "-1"));
      }
      return toReturn;
   }

   /**
    * Retrieve the application server scheme
    *
    * @return the application server scheme
    */

   public static String getOseeApplicationServerScheme() {
      String toReturn = "https";
      if (Integer.valueOf(System.getProperty(OSGI_SECURE_PORT_PROPERTY, "-1")) == -1) {
         toReturn = "http";
      }
      return toReturn;
   }

   /**
    * Retrieve the connection info file location
    *
    * @return connection info file URI
    */

   public static String getOseeConnectionInfoUri() {
      return OseeProperties.getOseeConnectionInfoUri();
   }

   private static String internalGetOseeApplicationServerData() {
      String toReturn = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (toReturn == null) {
         String userHome = System.getProperty("user.home");
         if (Strings.isValid(userHome)) {
            toReturn = userHome;
         }
      }
      return toReturn;
   }

   /**
    * Authentication Protocol to use
    *
    * @param client/server authentication protocol.
    */

   public static void setAuthenticationProtocol(String protocol) {
      System.setProperty(OSEE_AUTHENTICATION_PROTOCOL, protocol);
   }

   private OseeServerProperties() {
      super();
   }

}
