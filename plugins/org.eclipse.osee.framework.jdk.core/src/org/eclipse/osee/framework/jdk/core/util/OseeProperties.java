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
package org.eclipse.osee.framework.jdk.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Roberto E. Escobar
 */
public class OseeProperties {
   private static final String OSEE_LOG_DEFAULT = "osee.log.default";
   private static final String OSEE_JINI_SERVICE_GROUPS = "osee.jini.lookup.groups";
   private static final String OSEE_JINI_FORCED_REGGIE_SEARCH = "osee.jini.forced.reggie.search";
   private static final String OSEE_PORT_SCAN_START_PORT = "osee.port.scanner.start.port";

   public static final String OSEE_DB_CONNECTION_ID = "osee.db.connection.id";
   private static final String OSEE_CONNECTION_INFO_URI = "osee.connection.info.uri";
   private static final String OSEE_EMBEDDED_DB_SERVER = "osee.db.embedded.server";
   private static final String OSEE_EMBEDDED_DB_WEB_SERVER_PORT = "osee.db.embedded.web.server.port";
   private static final String OSEE_DEFAULT_BROKER_URI = "osee.default.broker.uri";
   private static final String OSEE_PROXY_BYPASS_ENABLED = "osee.proxy.bypass.enabled";
   private static final String OSEE_DB_CONNECTION_POOL_SIZE = "osee.db.connection.pool.size";
   private static final String OSEE_DB_CONNECTION_POOL_CONFIG_URI = "osee.db.connection.pool.config.uri";
   public static final String OSEE_SHOW_TOKEN_FOR_CHANGE_NAME = "osee.show.token.for.change.name";
   public static final String OSEE_DB = "osee.db";

   protected OseeProperties() {
      // Utility Class
   }

   public static String getOseeDefaultBrokerUri() {
      return System.getProperty(OSEE_DEFAULT_BROKER_URI);
   }

   public static int getOseeDbEmbeddedWebServerPort() {
      int port = -1;
      String portStr = System.getProperty(OSEE_EMBEDDED_DB_WEB_SERVER_PORT, "");
      if (Strings.isValid(portStr)) {
         port = Integer.parseInt(portStr);
      }
      return port;
   }

   public static Pair<String, Integer> getOseeDbEmbeddedServerAddress() {
      Pair<String, Integer> addressAndPort = null;
      String serverAddress = System.getProperty(OSEE_EMBEDDED_DB_SERVER, "");
      if (Strings.isValid(serverAddress)) {
         String[] hostPort = serverAddress.split(":");
         addressAndPort = new Pair<>(hostPort[0], Integer.parseInt(hostPort[1]));
      }
      return addressAndPort;
   }

   public static int getOseePortScannerStartPort() {
      int toReturn = 18000;
      String startPort = System.getProperty(OSEE_PORT_SCAN_START_PORT, "18000");
      try {
         toReturn = Integer.parseInt(startPort);
      } catch (Exception ex) {
         toReturn = 18000;
      }
      return toReturn;
   }

   /**
    * Get the default OSEE logging level. The default level is WARNING.
    *
    * @return default logging level
    */
   public static Level getOseeLogDefault() {
      Level toReturn = Level.WARNING;
      String level = System.getProperty(OSEE_LOG_DEFAULT, "WARNING");
      try {
         toReturn = Level.parse(level);
      } catch (Exception ex) {
         toReturn = Level.WARNING;
      }
      return toReturn;
   }

   /**
    * OSEE database information id to use for default database connections.
    *
    * @return the default database information id to use for database connections.
    */
   public static String getOseeDbConnectionId() {
      return System.getProperty(OSEE_DB_CONNECTION_ID);
   }

   /**
    * Retrieve the connection info file location
    *
    * @return connection info file URI
    */
   public static String getOseeConnectionInfoUri() {
      return System.getProperty(OSEE_CONNECTION_INFO_URI, "");
   }

   /**
    * Retrieve the number of max active connections
    *
    * @return number of max active connections
    */
   public static int getOseeDbConnectionCount() {
      int toReturn;
      String connections = System.getProperty(OSEE_DB_CONNECTION_POOL_SIZE, "10");
      try {
         toReturn = Integer.parseInt(connections);
      } catch (Exception ex) {
         toReturn = 10;
      }
      return toReturn;
   }

   /**
    * Retrieve the connection pool configuration file location
    *
    * @return connection pool configuration file URI
    */
   public static String getOseeDbConnectionPoolConfigUri() {
      return System.getProperty(OSEE_DB_CONNECTION_POOL_CONFIG_URI, "");
   }

   /**
    * Retrieves the JINI Groups this system is a part of.
    *
    * @return JINI service groups
    */
   public static String getOseeJiniServiceGroups() {
      return System.getProperty(OSEE_JINI_SERVICE_GROUPS);
   }

   /**
    * Sets the JINI Groups this system is a part of.
    *
    * @param JINI service groups
    */
   public static void setOseeJiniServiceGroups(String toStore) {
      System.setProperty(OSEE_JINI_SERVICE_GROUPS, toStore);
   }

   /**
    * @return whether forced reggie search is enabled
    */
   public static boolean isOseeJiniForcedReggieSearchEnabled() {
      return Boolean.valueOf(System.getProperty(OSEE_JINI_FORCED_REGGIE_SEARCH));
   }

   /**
    * Need to match methods in TestUtil
    */
   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   /**
    * Need to match methods in TestUtil
    */
   public static void setIsInTest(boolean isInTest) {
      System.setProperty("osee.isInTest", String.valueOf(isInTest));
   }

   private void toStringHelper(List<String> list, Class<?> clazz) {
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
         int mod = field.getModifiers();
         if (Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
            boolean wasModified = false;
            try {
               if (!field.isAccessible()) {
                  field.setAccessible(true);
                  wasModified = true;
               }
               Object object = field.get(this);
               if (object instanceof String) {
                  String value = (String) object;
                  list.add(String.format("%s: %s", value, System.getProperty(value)));
               }
            } catch (IllegalAccessException ex) {
               // DO NOTHING
            } finally {
               if (wasModified) {
                  field.setAccessible(false);
               }
            }
         }
      }
      Class<?> superClazz = clazz.getSuperclass();
      if (superClazz != null) {
         toStringHelper(list, superClazz);
      }
   }

   /**
    * @return whether proxy settings should be ignored for HttpRequests
    */
   public static boolean getOseeProxyBypassEnabled() {
      return Boolean.valueOf(System.getProperty(OSEE_PROXY_BYPASS_ENABLED, "false"));
   }

   @Override
   public String toString() {
      List<String> list = new ArrayList<>();
      toStringHelper(list, getClass());
      Collections.sort(list);
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", list);
   }

   public static boolean isOseeDb(String db) {
      return db.equals(System.getProperty(OSEE_DB));
   }

   public static boolean isOseeDbDefined() {
      return Strings.isValid(System.getProperty(OSEE_DB));
   }

}
