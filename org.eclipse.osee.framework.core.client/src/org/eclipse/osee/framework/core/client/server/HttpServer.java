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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.CorePreferences;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;

public class HttpServer implements Runnable {
   private static final int DEFAULT_HTTP_PRODUCTION_PORT = 8010;
   private static final int DEFAULT_HTTP_DEVELOPMENT_PORT = 8011;
   protected static final String DEFAULT_SERVICE_NAME = "osee.http.server";

   private static String serverAddress;
   private static boolean isRemoteServer = false;
   private static boolean neverRun = true;
   private final int port;
   private boolean listenFlag;
   private ServerSocket listenSocket;
   private String serviceName;
   private ExecutorService executorService;

   private static final Map<String, HttpServer> availableServers = new HashMap<String, HttpServer>();

   public static void remoteServerStartup() {
      startServers(10);
      isRemoteServer = true;
   }

   public static String getLocalServerAddress() {
      if (serverAddress == null) {
         if (isRemoteServer) {
            try {
               InetAddress address = InetAddress.getLocalHost();
               serverAddress = address.getHostAddress();
            } catch (UnknownHostException ex) {
               try {
                  serverAddress = CorePreferences.getDefaultInetAddress().getHostAddress();
               } catch (UnknownHostException ex1) {
                  OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex1.toString(), ex1);
               }
            }
         } else {
            try {
               serverAddress = CorePreferences.getDefaultInetAddress().getHostAddress();
            } catch (UnknownHostException ex) {
               OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
            }
         }
      }
      return serverAddress;
   }

   public static int getPortByServiceName(String serviceName) {
      int toReturn = -1;
      HttpServer server = availableServers.get(serviceName);
      if (server != null) {
         toReturn = server.getPort();
      }
      return toReturn;
   }

   public static int getDefaultServicePort() {
      return getPortByServiceName(HttpServer.DEFAULT_SERVICE_NAME);
   }

   /**
    * Starts HTTP Servers listening on a port for particularly formmated requests. A server will be started for each
    * org.eclipse.osee.framework.skynet.core.HttpServerPort extension point defined. If no such extension point is
    * defined, then skynet will use the osee.http.port system property. Finally if a port has still not been specified,
    * the DEFAULT_HTTP_SERVER_PORT will be used.
    * 
    * @param maxThreads TODO
    */
   public static void startServers(int maxThreads) {
      if (neverRun) {
         neverRun = false;
         Map<String, Integer> serversToRun = determineRequestedPorts();
         for (String key : serversToRun.keySet()) {
            int port = serversToRun.get(key);
            HttpServer server = new HttpServer(key, port, maxThreads);
            availableServers.put(key, server);
            Thread thread = new Thread(server);
            thread.setName(String.format("%s:%s", key, port));
            thread.start();
         }
      }
   }

   public HttpServer(String serviceName, int port, int poolSize) {
      this.listenFlag = true;
      this.serviceName = serviceName;
      this.port = port;
      this.executorService = Executors.newFixedThreadPool(poolSize);
      // new ServerKiller(this);
   }

   /**
    * Shuts down the HTTP Server
    */
   public void haltServer() {
      listenFlag = false;
   }

   public static void stopServers() {
      for (HttpServer server : HttpServer.availableServers.values()) {
         server.haltServer();
      }
   }

   private String getServiceName() {
      return serviceName;
   }

   private int getPort() {
      return port;
   }

   private String createNameForConnection(Socket incoming) {
      return String.format("%s:%s - Worker - %s:%s", getServiceName(), getPort(), incoming.getInetAddress(),
            incoming.getPort());
   }

   /**
    * Not to be called directly. Use the constructor.
    */
   public void run() {
      try {
         // Establish the listen socket.
         listenSocket = new ServerSocket(getPort());
         OseeLog.log(CoreClientActivator.class, Level.INFO, String.format("Starting HttpServer on port: [%s]",
               getPort()));

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
                  this.executorService.execute(new Runnable() {
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
                  OseeLog.log(CoreClientActivator.class, Level.SEVERE, "Error processing request.", ex);
               }
            } catch (SocketTimeoutException ex) {
               /*
                * this catch statement is hit every 10 seconds since that is the timeout value that
                * has been set which allows the loop to check if the server should keep running (so
                * don't log the exception)
                */
            }
         }
      } catch (IOException ex) {
         OseeLog.log(CoreClientActivator.class, Level.WARNING, "Unable to start HttpServer, socket may be busy", ex);
      }
   }

   private static Map<String, Integer> determineRequestedPorts() {
      Map<String, Integer> ports = new HashMap<String, Integer>();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements("org.eclipse.osee.framework.skynet.core.HttpServerPort",
                  "HttpServerPort");
      for (IConfigurationElement element : elements) {
         String serviceName = element.getAttribute("serviceName");
         try {
            String portAttribute = "productionPort";
            if (OseeCodeVersion.isDevelopment()) {
               portAttribute = "developmentPort";
            }
            Integer port = new Integer(element.getAttribute(portAttribute));

            // If a serviceName is not provided, we assume extension point is
            // attempting to override the default service.
            if (true != Strings.isValid(serviceName)) {
               OseeLog.log(CoreClientActivator.class, Level.INFO, String.format(
                     "Contributor [%s] overriding [%s]. Using port [%s]", element.getContributor().getName(),
                     DEFAULT_SERVICE_NAME, port));
               serviceName = DEFAULT_SERVICE_NAME;
            }
            ports.put(serviceName, port);
         } catch (NumberFormatException ex) {
            OseeLog.log(CoreClientActivator.class, Level.WARNING, String.format("Invalid Port for: [%s][%s]",
                  element.getName(), serviceName), ex);
         }
      }
      String value = OseeClientProperties.getLocalHttpWorkerPort();
      int portToUse = 0;
      if (Strings.isValid(value)) {
         try {
            portToUse = Integer.valueOf(value);
         } catch (Exception ex) {
            portToUse = 0;
         }
      }

      if (ports.containsKey(DEFAULT_SERVICE_NAME) == false || portToUse != 0) {
         // User Specified Port take Precedence
         if (portToUse == 0) {
            portToUse =
                  false != OseeCodeVersion.isDevelopment() ? DEFAULT_HTTP_DEVELOPMENT_PORT : DEFAULT_HTTP_PRODUCTION_PORT;
         }
         ports.put(DEFAULT_SERVICE_NAME, portToUse);

         if (DEFAULT_HTTP_PRODUCTION_PORT != portToUse) {
            OseeLog.log(CoreClientActivator.class, Level.INFO, String.format(
                  "HttpServer port override. Using port [%s]", portToUse));
         }
      }
      return ports;
   }
}
