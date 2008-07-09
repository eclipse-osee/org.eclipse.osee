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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.database.core.DatabaseNotSupportedException;
import org.eclipse.osee.framework.database.core.DbClientThread;
import org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask;
import org.eclipse.osee.framework.database.utility.GroupSelection;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbDetailData.ConfigField;
import org.eclipse.osee.framework.db.connection.info.DbSetupData.ServerInfoFields;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class LaunchOseeDbConfigClient extends DbClientThread {

   private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

   private LaunchOseeDbConfigClient(DbInformation databaseService) {
      super(ConfigUtil.getConfigFactory().getLogger(LaunchOseeDbConfigClient.class), "Config Client Thread",
            databaseService);
   }

   @Override
   public void processTask() throws SQLException, DatabaseNotSupportedException, Exception {
      logger.log(Level.INFO, "Begin Database Initialization...");
      run(connection, GroupSelection.getInstance().getDbInitTasks());
      logger.log(Level.INFO, "Database Initialization Complete.");
   }

   private static final String dbInitExtensionPointId = "org.eclipse.osee.framework.database.IDbInitializationTask";
   private Logger logger = ConfigUtil.getConfigFactory().getLogger(LaunchOseeDbConfigClient.class);

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#run(java.sql.Connection)
    */
   public void run(Connection connection, List<String> extensionIds) throws Exception {
      for (String pointId : extensionIds) {
         IExtension extension = Platform.getExtensionRegistry().getExtension(pointId);
         if (extension == null) {
            logger.log(Level.SEVERE, "Unable to locate extension [" + pointId + "]");
         } else {
            String extsionPointId = extension.getExtensionPointUniqueIdentifier();
            if (dbInitExtensionPointId.equals(extsionPointId)) {
               boolean success = runDbInitTasks(extension, connection);
               if (!success) {
                  logger.log(Level.SEVERE, "Aborting due to errors.");
                  break;
               }
            } else {
               logger.log(Level.SEVERE,
                     "Unknown extension id [" + extsionPointId + "] from extension [" + pointId + "]");
            }
         }
      }
   }

   /**
    * @param skynetDbTypesExtensions
    * @param extensionIds
    */
   private boolean runDbInitTasks(IExtension extension, Connection connection) {
      IConfigurationElement[] elements = extension.getConfigurationElements();
      String classname = null;
      String bundleName = null;
      for (IConfigurationElement el : elements) {
         if (el.getName().equals("DatabaseTask")) {
            classname = el.getAttribute("classname");
            bundleName = el.getContributor().getName();
         }
      }
      if (classname != null && bundleName != null) {
         Bundle bundle = Platform.getBundle(bundleName);
         try {
            logger.log(Level.INFO, "Starting [" + extension.getUniqueIdentifier() + "]");
            Class<?> taskClass = bundle.loadClass(classname);
            Object obj = taskClass.newInstance();
            IDbInitializationTask task = (IDbInitializationTask) obj;
            task.run(connection);
            // logger.log(Level.INFO, "Completed [" + extension.getUniqueIdentifier() + "]");
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return false;
         } catch (NoClassDefFoundError er) {
            logger.log(Level.SEVERE, er.getLocalizedMessage(), er);
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
            ex.printStackTrace();
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

   private static boolean checkPreconditions() {
      DbInformation dbInfo = OseeDb.getDefaultDatabaseService();

      if (DatabaseActivator.getInstance().isProductionDb()) {
         System.err.println("You are not allowed to run config client against production servers.\nExiting.");
         return false;
      }
      String serverUrl = dbInfo.getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.applicationServer);
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

   public static void main(String[] args) {
      boolean isConfigured = false;
      System.setProperty(OSEE_CONFIG_FACTORY,
            "org.eclipse.osee.framework.plugin.core.config.HeadlessEclipseConfigurationFactory");

      Logger.getLogger("org.eclipse.osee.framework.jdk.core.util.db.ConnectionHandler").setLevel(Level.SEVERE);
      Logger.getLogger("org.eclipse.osee.framework.jdk.core.util.db.DBConnection").setLevel(Level.SEVERE);
      Logger.getLogger("org.eclipse.osee.framework.jdk.core.sql.manager.OracleSqlManager").setLevel(Level.SEVERE);
      Logger.getLogger("org.eclipse.osee.framework.jdk.core.sql.manager.OracleSqlManager").setLevel(Level.SEVERE);

      if (checkPreconditions()) {
         DbInformation dbInfo = OseeDb.getDefaultDatabaseService();
         String dbName = dbInfo.getDatabaseDetails().getFieldValue(ConfigField.DatabaseName);
         String userName = dbInfo.getDatabaseDetails().getFieldValue(ConfigField.UserName);

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
            System.out.println("Configuring Database...");
            LaunchOseeDbConfigClient configClient = new LaunchOseeDbConfigClient(dbInfo);
            configClient.start();
            try {
               configClient.join();
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
         }
      }

      if (isConfigured != true) {
         System.out.println("Database will not be configured. ");
         Runtime.getRuntime().exit(0);
      }
   }
}
