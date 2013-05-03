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
package org.eclipse.osee.framework.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;

/**
 * @author Roberto E. Escobar
 */
public class H2ClientConnection implements IConnectionFactory {

   private static final String driver = "org.h2.Driver";
   private boolean firstTime = true;

   @Override
   public Connection getConnection(IDatabaseInfo dbInfo) throws Exception {
      Class.forName(driver);

      if (firstTime) {
         firstTime = false;
         Pair<String, Integer> addressAndPort = OseeProperties.getOseeDbEmbeddedServerAddress();
         if (addressAndPort != null) {
            int webPort = OseeProperties.getOseeDbEmbeddedWebServerPort();
            if (webPort < 0) {
               webPort = PortUtil.getInstance().getValidPort();
            }
            H2DbServer.startServer(addressAndPort.getFirst(), addressAndPort.getSecond(), webPort);
         }
      }
      Connection connection = DriverManager.getConnection(dbInfo.getConnectionUrl(), dbInfo.getConnectionProperties());
      return connection;
   }

   @Override
   public String getDriver() {
      return driver;
   }

}
