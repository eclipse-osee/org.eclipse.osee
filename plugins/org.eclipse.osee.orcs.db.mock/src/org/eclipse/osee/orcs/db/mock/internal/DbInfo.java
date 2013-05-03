/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.mock.internal;

import java.util.Properties;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("serial")
public class DbInfo implements IDatabaseInfo {

   private final int port;
   private final String connectionId;
   private final String dbPath;

   public DbInfo(String connectionId, int port, String dbPath) {
      super();
      this.port = port;
      this.connectionId = connectionId;
      this.dbPath = dbPath;
   }

   @Override
   public String getId() {
      return connectionId;
   }

   @Override
   public String getDatabaseName() {
      return "osee.h2.db";
   }

   @Override
   public String getDatabaseLoginName() {
      return "osee";
   }

   @Override
   public String getDriver() {
      return "org.h2.Driver";
   }

   @Override
   public String getConnectionUrl() {
      return String.format(
         "jdbc:h2:tcp://127.0.0.1:%s/%s/osee.h2.db;IGNORECASE=TRUE;SCHEMA_SEARCH_PATH=OSEE,PUBLIC;MVCC=TRUE;LOG=2",
         port, dbPath);
   }

   @Override
   public Properties getConnectionProperties() {
      Properties properties = new Properties();
      properties.setProperty("user", getDatabaseLoginName());
      properties.put("password", "osee");
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