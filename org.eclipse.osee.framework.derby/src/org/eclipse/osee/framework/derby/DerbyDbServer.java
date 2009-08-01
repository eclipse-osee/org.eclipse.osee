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

package org.eclipse.osee.framework.derby;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.logging.Level;
import org.apache.derby.drda.NetworkServerControl;
import org.eclipse.osee.framework.derby.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DerbyDbServer {
   protected static Object keepAlive;
   private NetworkServerUtil nwServer;
   private static final DerbyDbServer instance = new DerbyDbServer();

   private DerbyDbServer() {
      this.nwServer = null;
   }

   public static void startServer(String host, int port) throws Exception {
      instance.startServerInternal(host, port);
   }

   private void startServerInternal(String host, int port) throws Exception {
      try {
         OseeLog.log(Activator.class, Level.INFO, "Starting Derby Network Server ....");
         nwServer = new NetworkServerUtil(InetAddress.getByName(host), port);
         nwServer.start();

         if (isConnectionAvailable()) {
            nwServer.printInfo();
            addShutdownHook();
            //            stayAlive();
         } else {
            OseeLog.log(Activator.class, Level.INFO, "Exiting, since unable to connect to Derby Network Server.");
            OseeLog.log(Activator.class, Level.INFO,
                  "Please try to increase the amount of time to keep trying to connect to the Server.");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void stayAlive() {
      keepAlive = new Object();
      synchronized (keepAlive) {
         try {
            keepAlive.wait();
         } catch (InterruptedException e) {
            OseeLog.log(Activator.class, Level.SEVERE, e.getMessage(), e);
         }
      }
   }

   private void commitSuicide() {
      synchronized (keepAlive) {
         keepAlive.notify();
      }
   }

   private void shutdown() {
      OseeLog.log(Activator.class, Level.INFO, "Shutting down network server...");
      nwServer.shutdown();
      OseeLog.log(Activator.class, Level.INFO, "Server down.");
      commitSuicide();
   }

   private void addShutdownHook() {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            OseeLog.log(Activator.class, Level.INFO, "Shutting down");
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
            OseeLog.log(Activator.class, Level.SEVERE,
                  "Unable to obtain a connection to network server, trying again after 3000 ms.", e);
            Thread.sleep(3000);
         }
      }
      return knowIfServerUp;
   }

   // public static void main(String[] args) throws Exception {
   // (new DBServer( new PrintWriter(System.out, true)).startServer(args);
   // }

   private final class NetworkServerUtil {

      private NetworkServerControl serverControl;

      public NetworkServerUtil(InetAddress host, int port) {
         try {
            serverControl = new NetworkServerControl(host, port);
            OseeLog.log(Activator.class, Level.INFO, "Derby Network Server created");
         } catch (Exception e) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error Initializing Server Control.", e);
         }
      }

      /**
       * trace utility of server
       */
      public void trace(boolean onoff) {
         try {
            serverControl.trace(onoff);
         } catch (Exception e) {
            OseeLog.log(Activator.class, Level.SEVERE, e.getMessage(), e);
         }
      }

      /**
       * Try to test for a connection Throws exception if unable to get a connection
       */
      public void testForConnection() throws Exception {
         serverControl.ping();
      }

      /**
       * Shutdown the NetworkServer
       */
      public void shutdown() {
         try {
            serverControl.shutdown();
         } catch (Exception e) {
            OseeLog.log(Activator.class, Level.SEVERE, e.getMessage(), e);
         }
      }

      /**
       * Start Derby Network server
       */
      public void start() {
         try {
            serverControl.start(new PrintWriter(System.out, true));
         } catch (Exception e) {
            OseeLog.log(Activator.class, Level.SEVERE, e.getMessage(), e);
         }
      }

      public void printInfo() {
         try {
            String sysinfo = serverControl.getSysinfo();
            int start = sysinfo.indexOf("Java classpath:");
            int stop = sysinfo.indexOf("OS name:");
            String cpSubString = sysinfo.substring(start, stop);
            cpSubString = cpSubString.replaceAll(";", ";\n\t");
            String temp = sysinfo.substring(0, start) + cpSubString + sysinfo.substring(stop, sysinfo.length());
            OseeLog.log(Activator.class, Level.INFO, temp);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error getting Server Information", ex);
         }
      }
   }
}
