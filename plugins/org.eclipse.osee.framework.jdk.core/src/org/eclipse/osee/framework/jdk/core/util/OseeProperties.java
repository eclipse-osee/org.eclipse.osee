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
   private static final String OSEE_IS_IN_TEST = "osee.isInTest";
   private static final String OSEE_PORT_SCAN_START_PORT = "osee.port.scanner.start.port";

   public static final String OSEE_DB_CONNECTION_ID = "osee.db.connection.id";
   private static final String OSEE_CONNECTION_INFO_URI = "osee.connection.info.uri";
   private static final String OSEE_DERBY_SERVER = "osee.derby.server";

   protected OseeProperties() {
   }

   public static Pair<String, Integer> getDerbyServerAddress() {
      Pair<String, Integer> addressAndPort = null;
      String serverAddress = System.getProperty(OSEE_DERBY_SERVER, "");
      if (Strings.isValid(serverAddress)) {
         String[] hostPort = serverAddress.split(":");
         addressAndPort = new Pair<String, Integer>(hostPort[0], Integer.parseInt(hostPort[1]));
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

   @Override
   public String toString() {
      List<String> list = new ArrayList<String>();
      toStringHelper(list, getClass());
      Collections.sort(list);
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", list);
   }
}
