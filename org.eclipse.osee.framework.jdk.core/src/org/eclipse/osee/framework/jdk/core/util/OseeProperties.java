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

import java.util.logging.Logger;

public class OseeProperties {

   public static final String OSEE_CONFIG_FACTORY = "OseeConfigFactory";
   private static final String OSEE_AUTHENTICATION_PROVIDER_ID = "osee.authentication.provider.id";
   public static final String OSEE_CONFIG_FILE = "OseeConfig";
   public static final String OSEE_DB_CONNECTION_ID = "osee.db.connection.id";
   public static final String OSEE_IMPORT_FROM_DB_SERVICE = "OseeImportFromDbService";
   public static final String OSEE_TIMING_LOG = "OseeTimingLog";
   public static final String OSEE_BENCHMARK = "osee.benchmark";
   public static final String OSEE_JINI_SERVICE_GROUPS = "osee.jini.lookup.groups";
   public static final String OSEE_CMD_CONSOLE = "osee.cmd.console";
   public static final String OSEE_LOCAL_HTTP_WORKER_PORT = "osee.local.http.worker.port";
   public static final String OSEE_DEVELOPER = "osee.developer";
   public static final String OSEE_OVERRIDE_VERSION_CHECK = "osee.override.version.check";

   public static final String OSEE_NO_PROMPT = "OseeNoPrompt";
   private static final String OSEE_USE_FILE_SPECIFIED_SCHEMAS = "OseeUseFileSpecifiedSchemas";
   private static final String PRINT_SQL = "PrintSql";
   private static final String DONT_LOG_USAGE = "DontLogUsage";
   private static final String OSEE_AUTORUN = "osee.autoRun";
   private static final String OSEE_AUTORUN_NOTIFY = "osee.autoRunNotify";
   private static final String OSEE_DB_CONFIG_INIT_CHOICE = "osee.db.config.init.choice";
   private static final String OSEE_DB_IMPORT_SKYNET_BRANCH = "osee.db.import.skynet.branch";
   @Deprecated
   private static final String OSEE_REMOTE_HTTP_SERVER = "osee.remote.http.server";
   @Deprecated
   private static final String OSEE_REMOTE_HTTP_UPLOAD_PATH = "osee.remote.http.upload.path";

   private static final String OSEE_APPLICATION_SERVER_OVERRIDE = "osee.application.server.override";
   private static final String OSEE_APPLICATION_SERVER_DATA = "osee.application.server.data";
   private static final String OSEE_LOCAL_APPLICATION_SERVER = "osee.local.application.server";

   private static OseeProperties instance = null;
   private static Logger logger = null;

   private OseeProperties() {
      if (logger == null) {
         logger = Logger.getLogger("org.eclipse.osee.framework.jdk.core.util.OseeProperties");
      }
   }

   public static OseeProperties getInstance() {
      if (instance == null) {
         instance = new OseeProperties();
      }
      return instance;
   }
   
   public void setDeveloper(boolean developer) {
      System.setProperty(OSEE_DEVELOPER, Boolean.toString(developer));
   }

   public static boolean isDeveloper() {
      return getBooleanProperty(OSEE_DEVELOPER);
   }

   public String getAutoRun() {
      return System.getProperty(OSEE_AUTORUN);
   }

   public String getAutoRunNotify() {
      return System.getProperty(OSEE_AUTORUN_NOTIFY);
   }

   public boolean isOverrideVersionCheck() {
      return System.getProperty(OSEE_OVERRIDE_VERSION_CHECK) != null;
   }

   public void setOverrideVersionCheck(String value) {
      System.setProperty(OSEE_OVERRIDE_VERSION_CHECK, value);
   }

   public String getAuthenticationProviderId() {
      return System.getProperty(OSEE_AUTHENTICATION_PROVIDER_ID);
   }

   public boolean isPromptEnabled() {
      return !getBooleanProperty(OSEE_NO_PROMPT);
   }

   public boolean useSchemasSpecifiedInDbConfigFiles() {
      return getBooleanProperty(OSEE_USE_FILE_SPECIFIED_SCHEMAS);
   }

   public boolean isPrintSqlEnabled() {
      return System.getProperty(PRINT_SQL) != null;
   }

   public boolean isUsageLoggingEnabled() {
      return System.getProperty(DONT_LOG_USAGE) == null;
   }

   private static boolean getBooleanProperty(String key) {
      String propertyValue = System.getProperty(key, "false");
      if (propertyValue != null && propertyValue.equalsIgnoreCase("true")) {
         return true;
      }
      return false;
   }

   public String getDbConfigInitChoice() {
      return System.getProperty(OSEE_DB_CONFIG_INIT_CHOICE, "");
   }

   public boolean getDbOseeSkynetBranchImport() {
      return getBooleanProperty(OSEE_DB_IMPORT_SKYNET_BRANCH);
   }

   public void setDBConfigInitChoice(String value) {
      System.setProperty(OSEE_DB_CONFIG_INIT_CHOICE, value);
   }

   @Deprecated
   public String getRemoteHttpServer() {
      return System.getProperty(OSEE_REMOTE_HTTP_SERVER, "");
   }

   @Deprecated
   public String getRemoteHttpServerUploadPath() {
      return System.getProperty(OSEE_REMOTE_HTTP_UPLOAD_PATH, "");
   }

   /**
    * Get location for OSEE application server binary data
    * 
    * @return OSEE application server binary data path
    */
   public String getOseeApplicationServerData() {
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
   public boolean isLocalApplicationServerRequired() {
      return getBooleanProperty(OSEE_LOCAL_APPLICATION_SERVER);
   }

   /**
    * Gets value entered for application server override. When specified, this system property sets the URL used to
    * reference the application server.
    * 
    * @return application server URL to use instead of server specified in database
    */
   public String getOseeApplicationServerOverride() {
      return System.getProperty(OSEE_APPLICATION_SERVER_OVERRIDE, "");
   }

}
