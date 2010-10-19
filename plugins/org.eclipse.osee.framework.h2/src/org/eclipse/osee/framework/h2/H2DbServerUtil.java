/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.h2;

import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.h2.tools.Server;

public final class H2DbServerUtil {

   private final Collection<Server> serverControls = new ArrayList<Server>();

   public H2DbServerUtil(InetAddress host, int port) {
      try {
         String[] webArgs = new String[] {"-webAllowOthers", "true", "-webPort", String.valueOf(port - 1)};
         serverControls.add(Server.createWebServer(webArgs));

         String[] dbArgs = new String[] {"-tcp", "-tcpAllowOthers", "true", "-tcpPort", String.valueOf(port)};
         serverControls.add(Server.createTcpServer(dbArgs));

         OseeLog.log(H2DbServer.class, Level.INFO, "H2 Database Server created");
      } catch (Exception e) {
         OseeLog.log(H2DbServer.class, Level.SEVERE, "Error Initializing Server Control.", e);
      }

      for (Server server : serverControls) {
         server.setOut(new PrintStream(System.out, true));
      }
   }

   public void testForConnection() throws Exception {
      for (Server server : serverControls) {
         server.isRunning(true);
      }
   }

   public void shutdown() {
      for (Server server : serverControls) {
         try {
            server.shutdown();
         } catch (Exception e) {
            OseeLog.log(H2DbServer.class, Level.SEVERE, e.getMessage(), e);
         }
      }
   }

   public void start() {
      for (Server server : serverControls) {
         try {
            server.start();
         } catch (Exception e) {
            OseeLog.log(H2DbServer.class, Level.SEVERE, e.getMessage(), e);
         }
      }
   }

   public void printInfo() {
      try {
         StringBuilder builder = new StringBuilder();
         builder.append("H2 Database: ");
         for (Server server : serverControls) {
            builder.append(server.getStatus());
         }
         OseeLog.log(H2DbServer.class, Level.INFO, builder.toString());
      } catch (Exception ex) {
         OseeLog.log(H2DbServer.class, Level.SEVERE, "Error getting Server Information", ex);
      }
   }
}