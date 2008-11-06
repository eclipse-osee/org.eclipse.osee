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

public class OseeProperties {

   public static final String OSEE_CONFIG_FACTORY = "OseeConfigFactory";
   public static final String OSEE_DB_CONNECTION_ID = "osee.db.connection.id";
   public static final String OSEE_IMPORT_FROM_DB_SERVICE = "OseeImportFromDbService";
   public static final String OSEE_TIMING_LOG = "OseeTimingLog";
   public static final String OSEE_BENCHMARK = "osee.benchmark";
   public static final String OSEE_JINI_SERVICE_GROUPS = "osee.jini.lookup.groups";
   public static final String OSEE_CMD_CONSOLE = "osee.cmd.console";
   public static final String OSEE_LOCAL_HTTP_WORKER_PORT = "osee.local.http.worker.port";
   public static final String OSEE_DEVELOPER = "osee.developer";

   public static final String OSEE_NO_PROMPT = "OseeNoPrompt";
   private static final String OSEE_USE_FILE_SPECIFIED_SCHEMAS = "OseeUseFileSpecifiedSchemas";
   private static final String DONT_LOG_USAGE = "DontLogUsage";
   private static final String OSEE_DB_CONFIG_INIT_CHOICE = "osee.db.config.init.choice";
   private static final String OSEE_DB_IMPORT_SKYNET_BRANCH = "osee.db.import.skynet.branch";
   private static final String OSEE_AUTHENTICATION_PROTOCOL = "osee.authentication.protocol";

   public static final String OSEE_APPLICATION_SERVER_OVERRIDE = "osee.application.server.override";
   private static final String OSEE_APPLICATION_SERVER_DATA = "osee.application.server.data";
   private static final String OSEE_LOCAL_APPLICATION_SERVER = "osee.local.application.server";
   private static final String DEFAULT_OSEE_ARIBTRATION_SERVER = "osee.default.arbitration.server";

   private OseeProperties() {
   }

   public static void setDeveloper(boolean developer) {
      System.setProperty(OSEE_DEVELOPER, Boolean.toString(developer));
   }

   public static boolean isDeveloper() {
      return getBooleanProperty(OSEE_DEVELOPER);
   }

   public static boolean isPromptEnabled() {
      return !getBooleanProperty(OSEE_NO_PROMPT);
   }

   public static boolean useSchemasSpecifiedInDbConfigFiles() {
      return getBooleanProperty(OSEE_USE_FILE_SPECIFIED_SCHEMAS);
   }

   public static boolean isUsageLoggingEnabled() {
      return System.getProperty(DONT_LOG_USAGE) == null;
   }

   private static boolean getBooleanProperty(String key) {
      String propertyValue = System.getProperty(key, "false");
      if (propertyValue != null && propertyValue.equalsIgnoreCase("true")) {
         return true;
      }
      return false;
   }

   public static String getDbConfigInitChoice() {
      return System.getProperty(OSEE_DB_CONFIG_INIT_CHOICE, "");
   }

   public static boolean getDbOseeSkynetBranchImport() {
      return getBooleanProperty(OSEE_DB_IMPORT_SKYNET_BRANCH);
   }

   public static void setDBConfigInitChoice(String value) {
      System.setProperty(OSEE_DB_CONFIG_INIT_CHOICE, value);
   }

   /**
    * Authentication Protocol to use
    * 
    * @return client/server authentication protocol.
    */
   public static String getAuthenticationProtocol() {
      return System.getProperty(OSEE_AUTHENTICATION_PROTOCOL, "TrustAll");
   }

   /**
    * Get location for OSEE application server binary data
    * 
    * @return OSEE application server binary data path
    */
   public static String getOseeApplicationServerData() {
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
    * Gets whether local application server launch is required
    * 
    * @return <b>true</b> if local application server launch is required. <b>false</b> if local application server
    *         launch is not required.
    */
   public static boolean isLocalApplicationServerRequired() {
      return getBooleanProperty(OSEE_LOCAL_APPLICATION_SERVER);
   }

   /**
    * Gets value entered for application server override. When specified, this system property sets the URL used to
    * reference the application server.
    * 
    * @return application server URL to use instead of server specified in database
    */
   public static String getOseeApplicationServerOverride() {
      return System.getProperty(OSEE_APPLICATION_SERVER_OVERRIDE, "");
   }

   /**
    * Gets value entered as the default arbitration server. When specified, this system property sets default address
    * and port values for the arbitration server preferences. If it is not specified, the user will be responsible for
    * setting the arbitration server address and port in the arbitration server preference page.
    * 
    * @return default arbitration server URL to set preferences.
    */
   public static String getDefaultArbitrationServer() {
      return System.getProperty(DEFAULT_OSEE_ARIBTRATION_SERVER, "");
   }

}
