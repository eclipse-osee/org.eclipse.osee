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
package org.eclipse.osee.framework.db.connection;

import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Connect to a Database Service
 */
public class DBConnection {

   public static Connection getNewConnection(DbInformation databaseService) throws OseeDataStoreException {
      try {

         IConnection connectionFactory =
               org.eclipse.osee.framework.db.connection.Activator.getInstance().getDbConnectionFactory().get(
                     databaseService.getConnectionData().getDBDriver());

         // Connection properties and attributes are added in the
         // Connection Description portion of the Database Config XML file.
         Properties properties = databaseService.getProperties();
         String dbUrl = databaseService.getConnectionUrl();

         OseeLog.log(Activator.class, Level.INFO, "Getting new connection: " + dbUrl);
         Connection connection = connectionFactory.getConnection(properties, dbUrl);
         connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

         return connection;
      } catch (Exception ex) {
         throw new OseeDataStoreException("Unable to get a database connection:", ex);
      }
   }
}