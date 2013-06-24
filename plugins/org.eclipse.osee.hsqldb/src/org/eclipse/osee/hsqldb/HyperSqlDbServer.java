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

import java.net.InetAddress;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class HyperSqlDbServer {
   private static final int CHECK_ALIVE_NUMBER_OF_TIMES = 5;

   private static final HyperSqlDbServer instance = new HyperSqlDbServer();

   private HyperSqlServerUtil nwServer;

   private HyperSqlDbServer() {
      this.nwServer = null;
   }

   public static void startServer(String host, int port, int webPort, IDatabaseInfo dbInfo) throws Exception {
      instance.startServerInternal(host, port, webPort, dbInfo);
   }

   public static void stopServer() {
      instance.shutdown();
   }

   public static boolean stopServerWithWait() {
      instance.shutdown();
      return instance.isShutdown();

   }

   private void startServerInternal(String host, int port, int webPort, IDatabaseInfo dbInfo) throws Exception {
      OseeLog.logf(HyperSqlDbServer.class, Level.INFO,
         "Starting HyperSQL Database Server on [%s:%s] with webserver on [%s,%s]....", host, port, host, webPort);
      try {
         nwServer = new HyperSqlServerUtil(InetAddress.getByName(host), port, webPort, dbInfo);
         nwServer.start();

         if (isConnectionAvailable()) {
            nwServer.printInfo();
            addShutdownHook();
         } else {
            OseeLog.log(HyperSqlDbServer.class, Level.INFO,
               "Exiting, since unable to connect to HyperSQL Network Server.");
            OseeLog.log(HyperSqlDbServer.class, Level.INFO,
               "Please try to increase the amount of time to keep trying to connect to the Server.");
         }
      } catch (Exception ex) {
         OseeLog.log(HyperSqlDbServer.class, Level.SEVERE, ex);
      }
   }

   private void shutdown() {
      OseeLog.log(HyperSqlDbServer.class, Level.INFO, "Shutting down HyperSQL Database server...");
      nwServer.shutdown();
      OseeLog.log(HyperSqlDbServer.class, Level.INFO, "Server down.");
   }

   private void addShutdownHook() {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            OseeLog.log(HyperSqlDbServer.class, Level.INFO, "Shutting down");
            shutdown();
         }
      });
   }

   private boolean isConnectionAvailable() throws InterruptedException {
      boolean knowIfServerUp = false;
      int numTimes = CHECK_ALIVE_NUMBER_OF_TIMES;

      while (!knowIfServerUp && numTimes > 0) {
         try {
            numTimes--;
            nwServer.testForConnection();
            knowIfServerUp = true;
         } catch (Exception e) {
            OseeLog.log(HyperSqlDbServer.class, Level.SEVERE,
               "Unable to obtain a connection to network server, trying again after 3000 ms.", e);
            Thread.sleep(3000);
         }
      }
      return knowIfServerUp;
   }

   private boolean isShutdown() {
      boolean isDead = false;
      int numTimes = CHECK_ALIVE_NUMBER_OF_TIMES;
      while (!isDead && numTimes > 0) {
         try {
            numTimes--;
            nwServer.testNotRunning();
            isDead = true;
         } catch (Exception e) {
            isDead = false;
            try {
               Thread.sleep(3000);
            } catch (InterruptedException ex) {
               // Do nothing
            }
         }
      }
      return isDead;
   }

}
