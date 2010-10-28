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

import java.net.InetAddress;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class H2DbServer {
   protected static Object keepAlive;
   private H2DbServerUtil nwServer;
   private static final H2DbServer instance = new H2DbServer();

   private H2DbServer() {
      this.nwServer = null;
   }

   public static void startServer(String host, int port) throws Exception {
      instance.startServerInternal(host, port);
   }

   private void startServerInternal(String host, int port) throws Exception {
      OseeLog.format(H2DbServer.class, Level.INFO, "Starting H2 Database Server on [%s:%s]....", host, port);
      try {
         nwServer = new H2DbServerUtil(InetAddress.getByName(host), port);
         nwServer.start();

         if (isConnectionAvailable()) {
            nwServer.printInfo();
            addShutdownHook();
            //            stayAlive();
         } else {
            OseeLog.log(H2DbServer.class, Level.INFO, "Exiting, since unable to connect to Derby Network Server.");
            OseeLog.log(H2DbServer.class, Level.INFO,
               "Please try to increase the amount of time to keep trying to connect to the Server.");
         }
      } catch (Exception ex) {
         OseeLog.log(H2DbServer.class, Level.SEVERE, ex);
      }
   }

   private void shutdown() {
      OseeLog.log(H2DbServer.class, Level.INFO, "Shutting down H2 Database server...");
      nwServer.shutdown();
      OseeLog.log(H2DbServer.class, Level.INFO, "Server down.");
   }

   private void addShutdownHook() {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            OseeLog.log(H2DbServer.class, Level.INFO, "Shutting down");
            shutdown();
         }
      });
   }

   private boolean isConnectionAvailable() throws InterruptedException {
      boolean knowIfServerUp = false;
      int numTimes = 5;

      while (!knowIfServerUp && numTimes > 0) {
         try {
            numTimes--;
            nwServer.testForConnection();
            knowIfServerUp = true;
         } catch (Exception e) {
            OseeLog.log(H2DbServer.class, Level.SEVERE,
               "Unable to obtain a connection to network server, trying again after 3000 ms.", e);
            Thread.sleep(3000);
         }
      }
      return knowIfServerUp;
   }

}
