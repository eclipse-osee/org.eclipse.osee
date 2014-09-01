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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.hsqldb.Database;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerAcl.AclFormatException;

/**
 * @author Mark Joy
 */
public final class HyperSqlServerMgr {

   private static final String sc_key_remote_open_db = "server.remote_open";

   private final Map<String, Pair<Server, Thread>> serverControls =
      new ConcurrentHashMap<String, Pair<Server, Thread>>();

   public HyperSqlServerMgr() {
   }

   public String createServerInstance(InetAddress host, int dbPort, int webPort, IDatabaseInfo dbInfo) throws IOException, AclFormatException {
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

      OseeLog.logf(HyperSqlDbServer.class, Level.INFO, "HyperSQL Database Server created on [%s:%s]", host, dbPort);

      hsqlServer.setLogWriter(new PrintWriter(System.out, true));
      try {
         hsqlServer.start();
      } catch (Exception e) {
         OseeLog.log(HyperSqlDbServer.class, Level.SEVERE, e.getMessage(), e);
      }

      Thread shutdownHook = addShutdownHook(dbInfo.getDatabaseHome());
      Pair<Server, Thread> serverPair = new Pair<Server, Thread>(hsqlServer, shutdownHook);
      serverControls.put(dbInfo.getDatabaseHome(), serverPair);

      return dbInfo.getDatabaseHome();
   }

   public void testForConnection(String dbId) throws Exception {
      Pair<Server, Thread> entry = serverControls.get(dbId);
      if (entry != null) {
         try {
            Server server = entry.getFirst();
            server.checkRunning(true);
         } catch (Exception e) {
            OseeLog.log(HyperSqlDbServer.class, Level.SEVERE, e.getMessage(), e);
         }
      }
   }

   public void shutdown(String dbId) {
      Pair<Server, Thread> entry = serverControls.remove(dbId);
      if (entry != null) {
         try {
            Thread second = entry.getSecond();
            removeShutdownHook(second);

            Server first = entry.getFirst();
            first.shutdownWithCatalogs(Database.CLOSEMODE_NORMAL);
         } catch (Exception e) {
            OseeLog.log(HyperSqlDbServer.class, Level.SEVERE, e.getMessage(), e);
         }
      }
   }

   private Thread addShutdownHook(final String dbId) {
      Thread retThread = new Thread() {
         @Override
         public void run() {
            OseeLog.log(HyperSqlDbServer.class, Level.INFO, "Shutting down");
            shutdown(dbId);
         }
      };
      Runtime.getRuntime().addShutdownHook(retThread);
      return retThread;
   }

   private void removeShutdownHook(Thread shutdownHook) {
      if (shutdownHook != null) {
         Runtime.getRuntime().removeShutdownHook(shutdownHook);
      }
   }

   public void testNotRunning(String dbId) throws Exception {
      Pair<Server, Thread> entry = serverControls.get(dbId);
      if (entry != null) {
         try {
            Server server = entry.getFirst();
            server.checkRunning(false);
         } catch (Exception e) {
            OseeLog.log(HyperSqlDbServer.class, Level.SEVERE, e.getMessage(), e);
         }
      }
   }

   public void printInfo(String dbId) {
      try {
         StringBuilder builder = new StringBuilder();
         builder.append("HyperSQL Database: ");
         Pair<Server, Thread> entry = serverControls.get(dbId);
         if (entry != null) {
            Server server = entry.getFirst();
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
         } else {
            builder.append("\n\tDatabase not found for id: ");
            builder.append(dbId);
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