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

   public static final String OSEE_NO_PROMPT = "OseeNoPrompt";
   private static final String OSEE_USE_FILE_SPECIFIED_SCHEMAS = "OseeUseFileSpecifiedSchemas";
   private static final String PRINT_SQL = "PrintSql";
   private static final String DONT_LOG_USAGE = "DontLogUsage";
   private static final String OSEE_DB_CONFIG_INIT_CHOICE = "osee.db.config.init.choice";

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

      // if (filterGroups == null) {
      // filterGroups = ConfigUtil.getConfigFactory().getOseeConfig().getJiniServiceGroups();
      // }
      // if (filterGroups == null || filterGroups.length > 0) {
      // logger.log(Level.SEVERE, "[-D" + OseeProperties.OSEE_JINI_SERVICE_GROUPS + "] was not
      // set.\n"
      // + "Please enter the Jini Group this service register with.");
      // System.exit(1);
      // }
      return null;
   }

   public boolean isAtsUseWorkflowFiles() {
      return System.getProperty("AtsUseWorkflowFiles") != null;
   }

   public boolean isAtsAdmin() {
      return System.getProperty("AtsAdmin") != null;
   }

   public boolean isDeveloper() {
      return System.getProperty(OSEE_DEVELOPER) != null || isAtsAdmin();
   }

   public String getAuthenticationProviderId() {
      return System.getProperty(OSEE_AUTHENTICATION_PROVIDER_ID);
   }

   public boolean isAtsShowUser() {
      return System.getProperty("AtsShowUser") != null;
   }

   public boolean isAtsIgnoreConfigUpgrades() {
      return System.getProperty("AtsIgnoreConfigUpgrades") != null;
   }

   public boolean isAtsDisableEmail() {
      return System.getProperty("AtsDisableEmail") != null;
   }

   public boolean isAtsAlwaysEmailMe() {
      return System.getProperty("AtsAlwaysEmailMe") != null;
   }

   public boolean isEmailMe() {
      return System.getProperty("EmailMe") != null;
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
}
