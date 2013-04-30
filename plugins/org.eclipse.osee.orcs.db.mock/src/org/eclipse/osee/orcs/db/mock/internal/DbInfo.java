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
   private final Properties properties;

   public DbInfo(String connectionId, int port, String dbPath) {
      super();
      this.port = port;
      this.connectionId = connectionId;
      this.dbPath = dbPath;
      properties = new Properties();
      properties.setProperty("user", getDatabaseLoginName());
      properties.put("password", "");
      properties.put("hsqldb.tx", "MVCC");
   }

   @Override
   public String getId() {
      return connectionId;
   }

   @Override
   public String getDatabaseName() {
      return "osee.hsql.db";
   }

   @Override
   public String getDatabaseLoginName() {
      return "public";
   }

   @Override
   public String getDriver() {
      return "org.hsqldb.jdbc.JDBCDriver";
   }

   @Override
   public String getConnectionUrl() {
      return String.format("jdbc:hsqldb:hsql://127.0.0.1:%s/osee.hsql.db", port);
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