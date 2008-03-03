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
   public static final String OSEE_AUTHENTICATION_PROVIDER_ID = "OseeAuthenticationProviderId";
   public static final String OSEE_CONFIG_FILE = "OseeConfig";
   public static final String DEFAULT_DB_CONNECTION = "DefaultDbConnection";
   public static final String OSEE_IMPORT_FROM_DB_SERVICE = "OseeImportFromDbService";
   public static final String OSEE_TIMING_LOG = "OseeTimingLog";
   public static final String OSEE_BENCHMARK = "osee.benchmark";
   public static final String OSEE_JINI_SERVICE_GROUPS = "osee.jini.lookup.groups";
   public static final String OSEE_CMD_CONSOLE = "osee.cmd.console";
   public static final String OSEE_HTTP_PORT = "osee.http.port";
   public static final String OSEE_DEVELOPER = "Developer";
   public static final String OSEE_OVERRIDE_VERSION_CHECK = "OseeOverrideVersionCheck";

   private static final String OSEE_NO_PROMPT = "OseeNoPrompt";
   private static final String OSEE_USE_FILE_SPECIFIED_SCHEMAS = "OseeUseFileSpecifiedSchemas";
   private static final String PRINT_SQL = "PrintSql";
   private static final String DONT_LOG_USAGE = "DontLogUsage";
   private static final String OSEE_AUTORUN = "osee.autoRun";
   private static final String OSEE_AUTORUN_NOTIFY = "osee.autoRunNotify";
   private static final String OSEE_DB_CONFIG_INIT_CHOICE = "osee.db.config.init.choice";
   private static final String OSEE_REMOTE_HTTP_SERVER = "osee.remote.http.server";
   private static final String OSEE_REMOTE_HTTP_UPLOAD_PATH = "osee.remote.http.upload.path";

   private static boolean developer = false;

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

   public String[] getOseeJiniServiceGroups() {
      String serviceGroup = System.getProperty(OSEE_JINI_SERVICE_GROUPS);
      // String[] filterGroups = null;
      if (serviceGroup != null && serviceGroup.length() > 0) {
         String[] values = serviceGroup.split(",");
         for (int index = 0; index < values.length; index++) {
            values[index] = values[index].trim();
         }
         return values;
      }
      return null;
   }

   public void setDeveloper(boolean developer) {
      OseeProperties.developer = developer;
   }

   public boolean isDeveloper() {
      return System.getProperty(OSEE_DEVELOPER) != null || developer;
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

   public String getAuthenticationProviderId() {
      return System.getProperty(OSEE_AUTHENTICATION_PROVIDER_ID);
   }

   public boolean isEmailMe() {
      return System.getProperty("EmailMe") != null;
   }

   public boolean isDisableOseeUpdateCheck() {
      return System.getProperty("DisableOseeUpdateCheck") != null;
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

   private boolean getBooleanProperty(String key) {
      String propertyValue = System.getProperty(key, "false");
      if (propertyValue != null && propertyValue.equalsIgnoreCase("true")) {
         return true;
      }
      return false;
   }

   public String getDBConfigInitChoice() {
      return System.getProperty(OSEE_DB_CONFIG_INIT_CHOICE, "");
   }

   public void setDBConfigInitChoice(String value) {
      System.setProperty(OSEE_DB_CONFIG_INIT_CHOICE, value);
   }

   public String getRemoteHttpServer() {
      return System.getProperty(OSEE_REMOTE_HTTP_SERVER, "");
   }

   public String getRemoteHttpServerUploadPath() {
      return System.getProperty(OSEE_REMOTE_HTTP_UPLOAD_PATH, "");
   }
}
