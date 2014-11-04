/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.internal;

import java.util.Properties;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("serial")
public class SqliteDbInfo implements IDatabaseInfo {

   private final String connectionId;
   private final String dbPath;
   private final Properties properties;

   public SqliteDbInfo(String connectionId, String dbPath, Properties properties) {
      super();
      this.connectionId = connectionId;
      this.dbPath = dbPath;
      this.properties = properties;
   }

   @Override
   public String getId() {
      return connectionId;
   }

   @Override
   public String getDatabaseName() {
      return "cover.db";
   }

   @Override
   public String getDatabaseLoginName() {
      return "";
   }

   @Override
   public String getDriver() {
      return "org.sqlite.JDBC";
   }

   @Override
   public String getConnectionUrl() {
      return String.format("jdbc:sqlite:%s", dbPath);
   }

   @Override
   public Properties getConnectionProperties() {
      return properties;
   }

   @Override
   public boolean isProduction() {
      return false;
   }

   @Override
   public String getDatabaseHome() {
      return dbPath;
   }
}