/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.hsqldb.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.hsqldb.HsqlServerManager;

/**
 * @author Roberto E. Escobar
 */
public class HsqlClientConnection implements IConnectionFactory {

   private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

   private final AtomicBoolean firstTime = new AtomicBoolean(true);

   private HsqlServerManager dbServer;

   public void setHsqlServerManager(HsqlServerManager dbServer) {
      this.dbServer = dbServer;
   }

   @Override
   public Connection getConnection(IDatabaseInfo dbInfo) throws Exception {
      Class.forName(JDBC_DRIVER);

      if (firstTime.compareAndSet(true, false)) {
         Pair<String, Integer> addressAndPort = OseeProperties.getOseeDbEmbeddedServerAddress();
         if (addressAndPort != null) {
            int webPort = OseeProperties.getOseeDbEmbeddedWebServerPort();
            if (webPort < 0) {
               webPort = PortUtil.getInstance().getValidPort();
            }
            dbServer.startServer(addressAndPort.getFirst(), addressAndPort.getSecond(), webPort, dbInfo);
         }
      }
      Properties props = dbInfo.getConnectionProperties();
      String url = dbServer.asConnectionUrl(dbInfo.getConnectionUrl(), dbInfo.getConnectionProperties());
      return DriverManager.getConnection(url, props);
   }

   @Override
   public String getDriver() {
      return JDBC_DRIVER;
   }

}
