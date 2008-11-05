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
package org.eclipse.osee.framework.database.initialize;

import static org.eclipse.osee.framework.jdk.core.util.OseeProperties.OSEE_CONFIG_FACTORY;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.database.IDbInitializationRule;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.database.utility.GroupSelection;
import org.eclipse.osee.framework.db.connection.DatabaseInfoManager;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class LaunchOseeDbConfigClient {
   private static final String dbInitExtensionPointId = "org.eclipse.osee.framework.database.IDbInitializationTask";
   private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

   private LaunchOseeDbConfigClient() {
   }

   private static void processTask(OseeConnection connection) throws InvalidRegistryObjectException {
      OseeLog.log(DatabaseActivator.class, Level.INFO, "Begin Database Initialization...");

      for (String pointId : GroupSelection.getInstance().getDbInitTasks()) {
         IExtension extension = Platform.getExtensionRegistry().getExtension(pointId);
         if (extension == null) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, "Unable to locate extension [" + pointId + "]");
         } else {
            String extsionPointId = extension.getExtensionPointUniqueIdentifier();
            if (dbInitExtensionPointId.equals(extsionPointId)) {
               boolean success = runDbInitTasks(extension, connection);
               if (!success) {
                  OseeLog.log(DatabaseActivator.class, Level.SEVERE, "Aborting due to errors.");
                  break;
               }
            } else {
               OseeLog.log(DatabaseActivator.class, Level.SEVERE,
                     "Unknown extension id [" + extsionPointId + "] from extension [" + pointId + "]");
            }
         }
      }
      OseeLog.log(DatabaseActivator.class, Level.INFO, "Database Initialization Complete.");
   }

   /**
    * @param skynetDbTypesExtensions
    * @param extensionIds
    */
   private static boolean runDbInitTasks(IExtension extension, OseeConnection connection) {
      IConfigurationElement[] elements = extension.getConfigurationElements();
      String classname = null;
      String bundleName = null;
      String initRuleClassName = null;
      for (IConfigurationElement el : elements) {
         if (el.getName().equals("DatabaseTask")) {
            classname = el.getAttribute("classname");
            bundleName = el.getContributor().getName();
            initRuleClassName = el.getAttribute("DbInitRule");
         }
      }
      if (classname != null && bundleName != null) {
         Bundle bundle = Platform.getBundle(bundleName);
         try {
            boolean isExecutionAllowed = true;
            if (Strings.isValid(initRuleClassName)) {
               isExecutionAllowed = false;
               try {
                  Class<?> taskClass = bundle.loadClass(initRuleClassName);
                  IDbInitializationRule rule = (IDbInitializationRule) taskClass.newInstance();
                  isExecutionAllowed = rule.isAllowed();
               } catch (Exception ex) {
                  OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
                  return false;
               }
            }

            OseeLog.log(DatabaseActivator.class, isExecutionAllowed ? Level.INFO : Level.WARNING, String.format(
                  "%s [%s] execution rule [%s]", isExecutionAllowed ? "Starting" : "Skipping",
                  extension.getUniqueIdentifier(), Strings.isValid(initRuleClassName) ? initRuleClassName : "Default"));
            if (isExecutionAllowed) {
               IDbInitializationTask task = (IDbInitializationTask) bundle.loadClass(classname).newInstance();
               task.run(connection);
            }
         } catch (Exception ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
            return false;
         } catch (NoClassDefFoundError er) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, er);
            return false;
         }
      }

      return true;
   }

   private static String waitForUserResponse() {
      System.out.println("Enter: [Y|N]\n");
      String line = "N";

      if (!OseeProperties.getInstance().isPromptEnabled()) {
         line = "Y";
      } else {
         try {
            line = stdin.readLine();
         } catch (IOException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
         }
      }
      return line;
   }

   private static boolean isApplicationServerAlive(String applicationServerUrl) {
      boolean canConnection = false;
      try {
         URL url = new URL(applicationServerUrl);
         URLConnection connection = url.openConnection();
         connection.connect();
         canConnection = true;
      } catch (Exception ex) {

      }
      return canConnection;
   }

   private static boolean checkPreconditions() throws OseeCoreException {
      IDatabaseInfo dbInfo = DatabaseInfoManager.getDefault();
      String serverUrl = dbInfo.getDefaultArbitrationServer();
      System.setProperty(OseeProperties.OSEE_APPLICATION_SERVER_OVERRIDE, serverUrl);

      if (dbInfo.isProduction()) {
         System.err.println(String.format(
               "You are not allowed to run config client against production: [%s].\nExiting.", dbInfo.getDatabaseName()));
         return false;
      }

      boolean serverOk = isApplicationServerAlive(serverUrl);
      System.out.println(String.format("OSEE Application Server Validation [%s]", serverOk ? "PASSED" : "FAILED"));
      if (serverOk != true) {
         System.err.println(String.format(
               "Error connecting to application server [%s].\n" + "Please ensure server is running and try again.",
               serverUrl));
         return false;
      }

      return true;
   }

   public static void main(String[] args) throws OseeCoreException {
      boolean isConfigured = false;
      System.setProperty(OSEE_CONFIG_FACTORY,
            "org.eclipse.osee.framework.plugin.core.config.HeadlessEclipseConfigurationFactory");

      Logger.getLogger("org.eclipse.osee.framework.jdk.core.util.db.ConnectionHandler").setLevel(Level.SEVERE);
      Logger.getLogger("org.eclipse.osee.framework.jdk.core.util.db.DBConnection").setLevel(Level.SEVERE);
      Logger.getLogger("org.eclipse.osee.framework.jdk.core.sql.manager.OracleSqlManager").setLevel(Level.SEVERE);
      Logger.getLogger("org.eclipse.osee.framework.jdk.core.sql.manager.OracleSqlManager").setLevel(Level.SEVERE);

      if (checkPreconditions()) {
         IDatabaseInfo dbInfo = DatabaseInfoManager.getDefault();
         String dbName = dbInfo.getDatabaseName();
         String userName = dbInfo.getDatabaseLoginName();

         boolean isPromptEnabled = OseeProperties.getInstance().isPromptEnabled();
         String line = null;
         if (isPromptEnabled) {
            System.out.println("\nAre you sure you want to configure: " + dbName + ":" + userName);
            line = waitForUserResponse();
         } else {
            line = "Y";
         }
         if (line.equalsIgnoreCase("Y")) {
            isConfigured = true;
            OseeLog.log(DatabaseActivator.class, Level.INFO, "Configuring Database...");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            OseeConnection connection = OseeDbConnection.getConnection(dbInfo);
            try {
               processTask(connection);
            } catch (Throwable ex) {
               OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
            } finally {
               connection.close();
               stopWatch.stop();
               System.out.println(String.format("Database Configurationg completed in [%s] ms", stopWatch));
            }
         }
      }

      if (isConfigured != true) {
         System.out.println("Database will not be configured. ");
      }
   }
}
