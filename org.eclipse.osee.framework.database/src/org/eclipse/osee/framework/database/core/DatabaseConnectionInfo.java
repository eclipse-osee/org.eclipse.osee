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
package org.eclipse.osee.framework.database.core;

import java.util.Properties;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;

/**
 * @author Ryan D. Brooks
 */
public class DatabaseConnectionInfo implements IDatabaseInfo {

   private static final long serialVersionUID = -6513818831248393100L;
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

   @Override
   public Properties getConnectionProperties() {
      return properties;
   }

   @Override
   public String getConnectionUrl() {
      return connectionPrefix + ":" + databaseName;
   }

   @Override
   public String getDatabaseLoginName() {
      return databaseLoginName;
   }

   @Override
   public String getDatabaseName() {
      return databaseName;
   }

   @Override
   public String getDriver() {
      return driver;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public boolean isProduction() {
      return isProduction;
   }
}