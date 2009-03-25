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
package org.eclipse.osee.framework.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.db.connection.IConnection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DerbyClientConnection implements IConnection {

   private static final String driver = "org.apache.derby.jdbc.ClientDriver";
   private boolean firstTime = true;

   public Connection getConnection(Properties properties, String connectionURL) throws ClassNotFoundException, SQLException {
      Class.forName(driver);

      if (firstTime) {
         firstTime = false;
         try {
            String derbyAddress = System.getProperty(OseeServerProperties.OSEE_DERBY_SERVER);
            if (Strings.isValid(derbyAddress)) {
               String[] hostPort = derbyAddress.split(":");
               DerbyDbServer.startServer(hostPort[0], Integer.parseInt(hostPort[1]));
            }
         } catch (Exception ex) {
            OseeLog.log(getClass(), Level.SEVERE, ex);
         }
      }

      Connection connection = DriverManager.getConnection(connectionURL, properties);
      return connection;
   }

   public String getDriver() {
      return driver;
   }

}
