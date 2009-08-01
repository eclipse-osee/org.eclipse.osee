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
package org.eclipse.osee.framework.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.eclipse.osee.framework.database.core.IConnection;

public class PostgresqlConnection implements IConnection {

   private static final String driverName = "org.postgresql.Driver";

   public PostgresqlConnection() {
   }

   public Connection getConnection(Properties properties, String connectionURL) throws Exception {
      Class.forName(driverName);
      Connection connection = DriverManager.getConnection(connectionURL, properties);
      return connection;
   }

   @Override
   public String getDriver() {
      return driverName;
   }
}
