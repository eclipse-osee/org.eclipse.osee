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

import java.util.Properties;

/**
 * @author Ryan D. Brooks
 */
public class DatabaseConnectionInfo implements IDatabaseInfo {
   private final String databaseLoginName;
   private final String connectionPrefix;
   private final String databaseName;
   private final String driver;
   private final String id;
   private final boolean isProduction;
   private final Properties properties;

   /**
    * @param databaseLoginName
    * @param databaseName
    * @param driver
    * @param id
    * @param isProduction
    */
   public DatabaseConnectionInfo(String databaseLoginName, String connectionPrefix, String databaseName, String driver, String id, Properties properties, boolean isProduction) {
      super();
      this.databaseLoginName = databaseLoginName;
      this.connectionPrefix = connectionPrefix;
      this.databaseName = databaseName;
      this.driver = driver;
      this.id = id;
      this.isProduction = isProduction;
      this.properties = properties;
      properties.setProperty("user", databaseLoginName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getConnectionProperties()
    */
   @Override
   public Properties getConnectionProperties() {
      return properties;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getConnectionUrl()
    */
   @Override
   public String getConnectionUrl() {
      return connectionPrefix + ":" + databaseName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDatabaseLoginName()
    */
   @Override
   public String getDatabaseLoginName() {
      return databaseLoginName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDatabaseName()
    */
   @Override
   public String getDatabaseName() {
      return databaseName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDriver()
    */
   @Override
   public String getDriver() {
      return driver;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getId()
    */
   @Override
   public String getId() {
      return id;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#isProduction()
    */
   @Override
   public boolean isProduction() {
      return isProduction;
   }
}