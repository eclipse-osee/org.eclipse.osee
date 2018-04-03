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

package org.eclipse.osee.framework.core.client.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.CorePreferences;

public class HttpServer {
   protected static final String DEFAULT_SERVICE_NAME = "osee.http.server";
   private static final String LOCALHOST = "localhost";

   private static InternalHttpServer internalHttpServer = null;

   private HttpServer() {
   }

   public static String getServerAddressForExternalCommunication() {
      String serverAddress = "";
      try {
         serverAddress = CorePreferences.getDefaultInetAddress().getHostAddress();
      } catch (UnknownHostException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return serverAddress;
   }

   public static String getLocalServerAddress() {
      return LOCALHOST;
   }

   public static int getDefaultServicePort() {
      return internalHttpServer != null ? internalHttpServer.getPort() : -1;
   }

   public synchronized static void startServer(int maxThreads) {
      if (internalHttpServer == null) {
         int portToUse = -1;
         String value = OseeClientProperties.getLocalHttpWorkerPort();
         if (Strings.isValid(value)) {
            try {
               portToUse = Integer.valueOf(value);
            } catch (Exception ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, ex, "Unable to parse port property - [%s]", value);
            }
         }

         if (portToUse <= 0) {
            try {
               portToUse = PortUtil.getInstance().getValidPort();
            } catch (IOException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Unable to get a valid port.");
            }
         }

         if (portToUse > 0) {
            internalHttpServer = new InternalHttpServer(DEFAULT_SERVICE_NAME, portToUse, maxThreads);
            Thread thread = new Thread(internalHttpServer);
            thread.setName(String.format("%s:%s", DEFAULT_SERVICE_NAME, portToUse));
            thread.start();
         } else {
            OseeLog.log(Activator.class, Level.SEVERE,
               "Unable to get a valid port for osee http local server.");
         }
      }
   }

   public static void stopServer() {
      if (internalHttpServer != null) {
         internalHttpServer.haltServer();
         if (!internalHttpServer.executorService.isShutdown()) {
            internalHttpServer.executorService.shutdownNow();
         }
      }
   }

   private static final class InternalHttpServer implements Runnable {
      private final int port;
      private boolean listenFlag;
      private ServerSocket listenSocket;
      private final String serviceName;
      private final ExecutorService executorService;

      private InternalHttpServer(String serviceName, int port, int poolSize) {
         this.listenFlag = true;
         this.serviceName = serviceName;
         this.port = port;
         this.executorService = Executors.newFixedThreadPool(poolSize);
      }

      public void haltServer() {
         listenFlag = false;
      }

      public String getServiceName() {
         return serviceName;
      }

      public int getPort() {
         return port;
      }

      private String createNameForConnection(Socket incoming) {
         return String.format("%s:%s - Worker - %s:%s", getServiceName(), getPort(), incoming.getInetAddress(),
            incoming.getPort());
      }

      @Override
      public void run() {
         try {
            listenSocket = new ServerSocket(getPort());
            OseeLog.logf(Activator.class, Level.INFO, "Starting HttpServer on port: [%s]", getPort());

            // Process HTTP service requests in an infinite loop.
            while (listenFlag) {
               // Listen for a TCP connection request.
               listenSocket.setSoTimeout(10000);
               try {
                  Socket incoming = listenSocket.accept();
                  try {
                     final HttpRequestHandler handler = new HttpRequestHandler(incoming);
                     final String threadName = createNameForConnection(incoming);

                     // Process the request in a new thread
                     executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                           Thread thread = Thread.currentThread();
                           String oldName = thread.getName();
                           thread.setName(threadName);
                           try {
                              handler.run();
                           } finally {
                              thread.setName(oldName);
                           }
                        }
                     });
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, "Error processing request.", ex);
                  }
               } catch (SocketTimeoutException ex) {
                  /*
                   * this catch statement is hit every 10 seconds since that is the timeout value that has been set
                   * which allows the loop to check if the server should keep running (so don't log the exception)
                   */
               }
            }
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Unable to start HttpServer, socket may be busy", ex);
         } finally {
            executorService.shutdownNow();
         }
      }
   }
}
