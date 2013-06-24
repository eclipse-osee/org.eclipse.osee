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
package org.eclipse.osee.hsqldb;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.logging.OseeLog;
import org.hsqldb.Database;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;

public final class HyperSqlServerUtil {

   private static final String sc_key_remote_open_db = "server.remote_open";

   private final Collection<Server> serverControls = new ArrayList<Server>();

   public HyperSqlServerUtil(InetAddress host, int dbPort, int webPort, IDatabaseInfo dbInfo) throws Exception {
      Server hsqlServer = new Server();
      hsqlServer.setAddress(host.getHostAddress());
      hsqlServer.setPort(dbPort);

      StringBuilder dbCreationUrl = new StringBuilder();
      dbCreationUrl.append(dbInfo.getDatabaseHome());

      Properties connProps = dbInfo.getConnectionProperties();
      appendProperties(dbCreationUrl, connProps);

      hsqlServer.setDatabaseName(0, dbInfo.getDatabaseName());
      hsqlServer.setDatabasePath(0, dbCreationUrl.toString());

      HsqlProperties props = new HsqlProperties();
      props.setProperty(sc_key_remote_open_db, true);
      hsqlServer.setProperties(props);

      serverControls.add(hsqlServer);
      OseeLog.logf(HyperSqlDbServer.class, Level.INFO, "HyperSQL Database Server created on [%s:%s]", host, dbPort);

      for (Server server : serverControls) {
         server.setLogWriter(new PrintWriter(System.out, true));
      }
   }

   public void testForConnection() throws Exception {
      for (Server server : serverControls) {
         server.checkRunning(true);
      }
   }

   public void shutdown() {
      for (Server server : serverControls) {
         try {
            server.shutdownWithCatalogs(Database.CLOSEMODE_COMPACT);
         } catch (Exception e) {
            OseeLog.log(HyperSqlDbServer.class, Level.SEVERE, e.getMessage(), e);
         }
      }
   }

   public void testNotRunning() throws Exception {
      for (Server server : serverControls) {
         server.checkRunning(false);
      }
   }

   public void start() {
      for (Server server : serverControls) {
         try {
            server.start();
         } catch (Exception e) {
            OseeLog.log(HyperSqlDbServer.class, Level.SEVERE, e.getMessage(), e);
         }
      }
   }

   public void printInfo() {
      try {
         StringBuilder builder = new StringBuilder();
         builder.append("HyperSQL Database: ");
         for (Server server : serverControls) {
            builder.append("\n\tAddress : ");
            builder.append(server.getAddress());
            builder.append("\n\tDatabase Name : ");
            builder.append(server.getDatabaseName(0, true));
            builder.append("\n\tDatabasePath : ");
            builder.append(server.getDatabasePath(0, true));
            builder.append("\n\tPort : ");
            builder.append(server.getPort());
            builder.append("\n\tDefaultWebPage : ");
            builder.append(server.getDefaultWebPage());
            builder.append("\n\tProductName : ");
            builder.append(server.getProductName());
            builder.append("\n\tProductVersion : ");
            builder.append(server.getProductVersion());
            builder.append("\n\tProtocol : ");
            builder.append(server.getProtocol());
            builder.append("\n\tServerId : ");
            builder.append(server.getServerId());
            builder.append("\n\tState : ");
            builder.append(server.getState());
            builder.append("\n\tStateDescriptor : ");
            builder.append(server.getStateDescriptor());
            builder.append("\n\tWebRoot : ");
            builder.append(server.getWebRoot());
         }
         OseeLog.log(HyperSqlDbServer.class, Level.INFO, builder.toString());
      } catch (Exception ex) {
         OseeLog.log(HyperSqlDbServer.class, Level.SEVERE, "Error getting Server Information", ex);
      }
   }

   public static void appendProperties(StringBuilder builder, Properties props) {
      if (!props.isEmpty()) {
         for (Entry<Object, Object> entry : props.entrySet()) {
            builder.append(String.format(";%s=%s", entry.getKey(), entry.getValue()));
         }
      }
   }

}