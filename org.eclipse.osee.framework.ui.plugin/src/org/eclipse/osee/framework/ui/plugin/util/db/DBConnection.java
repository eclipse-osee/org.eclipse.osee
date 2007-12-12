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
package org.eclipse.osee.framework.ui.plugin.util.db;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.OSEE_INFO_TABLE;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.data.DbInformation;
import org.eclipse.osee.framework.plugin.core.config.data.DbDetailData.ConfigField;
import org.eclipse.osee.framework.plugin.core.db.IConnection;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.osgi.framework.Bundle;

/**
 * Connect to a Database Service
 */
public class DBConnection {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(DBConnection.class);
   public static final String codeVersion = "20060727";

   private static Map<String, IConnection> connections = new HashMap<String, IConnection>();

   static {
      populateConnections();
   }

   public static Connection getNewConnection(DbInformation databaseService) throws SQLException {
      return getNewConnection(databaseService, true);
   }

   public static Connection getNewConnection(DbInformation databaseService, boolean validityCheck) throws SQLException {
      try {

         IConnection connectionFactory = connections.get(databaseService.getConnectionData().getDBDriver());
         if (connectionFactory == null) {
            throw new IllegalStateException(String.format(
                  "Unable to locate a Connection Factory for the driver type [%s].",
                  databaseService.getConnectionData().getDBDriver()));
         }

         String userName = databaseService.getDatabaseDetails().getFieldValue(ConfigField.UserName);
         String password = databaseService.getDatabaseDetails().getFieldValue(ConfigField.Password);

         // Connection properties and attributes are added in the
         // Connection Description portion of the Database Config XML file.
         Properties properties = databaseService.getConnectionData().getProperties();
         properties.setProperty("user", userName);
         properties.setProperty("password", password);

         String dbUrl = databaseService.getFormattedURL() + databaseService.getConnectionData().getAttributes();

         logger.log(Level.INFO, "Getting new connection: " + dbUrl);
         Connection connection = connectionFactory.getConnection(properties, dbUrl);
         connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

         if (validityCheck && !userName.equals("peer")) {
            ensureDatabaseCompatability(connection);
         }

         return connection;
      } catch (Throwable th) {
         logger.log(Level.SEVERE, "Unable to get database connection.", th);
         throw new SQLException("Unable to get a connection: " + th.getMessage());
      }
   }

   private static void populateConnections() {
      Bundle bundle = PluginCoreActivator.getInstance().getBundle();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(bundle.getSymbolicName() + ".JdbcConnection", "Connection");

      for (IConfigurationElement element : elements) {
         String connectionClass = element.getAttribute("ConnectionFactory");
         String driver = element.getAttribute("Driver");
         try {
            IConnection connection =
                  (IConnection) Platform.getBundle(element.getContributor().getName()).loadClass(connectionClass).newInstance();
            connections.put(driver, connection);
         } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   private static void ensureDatabaseCompatability(Connection connection) throws SQLException {
      String query = "SELECT OSEE_VALUE FROM " + OSEE_INFO_TABLE + " WHERE OSEE_KEY = \'version\'";

      Statement statement = connection.createStatement();
      statement.setFetchSize(1);
      ResultSet rset = statement.executeQuery(query);
      String dbVersion = null;
      if (rset.next()) {
         dbVersion = rset.getString("OSEE_VALUE");
      }
      statement.close();

      if (dbVersion == null || !dbVersion.equals(codeVersion)) {
         throw new IllegalArgumentException(String.format("Code verion %s does not match database version %s",
               codeVersion, dbVersion));
      }
   }

   public static void insertDbVersion() throws SQLException {
      String query =
            String.format("INSERT INTO %s (OSEE_KEY,OSEE_VALUE) VALUES ('version', '%s')", OSEE_INFO_TABLE, codeVersion);
      Statement statement =
            getNewConnection(ConfigUtil.getConfigFactory().getOseeConfig().getDefaultClientData(), false).createStatement();
      statement.executeUpdate(query);
      statement.close();
   }
}