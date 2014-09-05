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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.hsqldb.HsqlServerManager;
import org.eclipse.osee.logger.Log;
import org.hsqldb.Database;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerAcl.AclFormatException;

/**
 * @author Mark Joy
 */
public final class HsqlServerManagerImpl implements HsqlServerManager {

   private static final int CHECK_ALIVE_NUMBER_OF_TIMES = 5;
   private static final String sc_key_remote_open_db = "server.remote_open";

   private Map<String, Pair<Server, Thread>> serverControls;

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start() {
      serverControls = new ConcurrentHashMap<String, Pair<Server, Thread>>();
   }

   public void stop() {
      if (serverControls != null && !serverControls.isEmpty()) {
         for (String dbId : serverControls.keySet()) {
            try {
               shutdown(dbId);
            } catch (Exception ex) {
               // do nothing
            }
         }
         serverControls.clear();
         serverControls = null;
      }
   }

   @Override
   public Iterable<String> getIds() {
      return serverControls.keySet();
   }

   @Override
   public String startServer(String host, int port, int webPort, IDatabaseInfo dbInfo) throws Exception {
      logger.info("Starting HyperSQL Database Server on [%s:%s] with webserver on [%s,%s]....", host, port, host,
         webPort);
      String dbId = "";
      try {
         dbId = createServerInstance(InetAddress.getByName(host), port, webPort, dbInfo);

         if (isConnectionAvailable(dbId)) {
            printInfo(dbId);
         } else {
            logger.info("Exiting, since unable to connect to HyperSQL Network Server.");
            logger.info("Please try to increase the amount of time to keep trying to connect to the Server.");
         }
      } catch (Exception ex) {
         logger.error(ex, "Error starting Hsql server");
      }
      return dbId;
   }

   @Override
   public boolean stopServerWithWait(String dbId) {
      logger.info("Shutting down HyperSQL Database server [%s]", dbId);
      shutdown(dbId);
      boolean dead = isShutdown(dbId);
      logger.info("Server [%s] - state[%s]", dbId, dead ? "STOPPED" : "RUNNING");
      return dead;
   }

   private String createServerInstance(InetAddress host, int dbPort, int webPort, IDatabaseInfo dbInfo) throws IOException, AclFormatException {
      Server hsqlServer = new Server();
      hsqlServer.setErrWriter(newErrorWriter(logger));
      hsqlServer.setLogWriter(newDebugWriter(logger));

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

      logger.info("HyperSQL Database Server created on [%s:%s]", host, dbPort);

      String dbId = dbInfo.getDatabaseHome();
      try {
         hsqlServer.start();
      } catch (Exception ex) {
         logger.error(ex, "Error starting hsql server");
      } finally {
         ShutdownHook hook = new ShutdownHook(dbId);
         Runtime.getRuntime().addShutdownHook(hook);
         serverControls.put(dbId, new Pair<Server, Thread>(hsqlServer, hook));
      }
      return dbId;
   }

   private synchronized void shutdown(String dbId) {
      if (serverControls != null) {
         Pair<Server, Thread> entry = serverControls.remove(dbId);
         if (entry != null) {
            Thread hook = entry.getSecond();
            if (hook != null) {
               try {
                  Runtime.getRuntime().removeShutdownHook(hook);
               } catch (Exception ex) {
                  // do nothing;
               }
            }

            try {
               Server first = entry.getFirst();
               first.shutdownWithCatalogs(Database.CLOSEMODE_NORMAL);
            } catch (Exception ex) {
               logger.error(ex, "Error during shutdown");
            }
         }
      }
   }

   @Override
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
         logger.info(builder.toString());
      } catch (Exception ex) {
         logger.error(ex, "Error getting Server Information");
      }
   }

   @Override
   public boolean isRunning(String dbId) {
      boolean result = false;
      if (serverControls != null) {
         Pair<Server, Thread> entry = serverControls.get(dbId);
         if (entry != null) {
            try {
               Server server = entry.getFirst();
               server.checkRunning(true);
               result = true;
            } catch (Exception ex) {
               // do nothing;
            }
         }
      }
      return result;
   }

   private boolean isConnectionAvailable(String dbId) throws InterruptedException {
      boolean knowIfServerUp = false;
      int numTimes = CHECK_ALIVE_NUMBER_OF_TIMES;

      while (!knowIfServerUp && numTimes > 0) {
         try {
            numTimes--;
            knowIfServerUp = isRunning(dbId);
         } catch (Exception e) {
            Thread.sleep(3000);
         }
      }
      return knowIfServerUp;
   }

   private boolean isShutdown(String dbId) {
      boolean isDead = false;
      int numTimes = CHECK_ALIVE_NUMBER_OF_TIMES;
      while (!isDead && numTimes > 0) {
         try {
            numTimes--;
            isDead = !isRunning(dbId);
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

   @Override
   public String asConnectionUrl(String url, Properties props) {
      StringBuilder builder = new StringBuilder();
      builder.append(url);
      appendProperties(builder, props);
      return builder.toString();
   }

   private static void appendProperties(StringBuilder builder, Properties props) {
      if (!props.isEmpty()) {
         for (Entry<Object, Object> entry : props.entrySet()) {
            builder.append(String.format(";%s=%s", entry.getKey(), entry.getValue()));
         }
      }
   }

   private static PrintWriter newErrorWriter(Log logger) {
      PrintWriter toReturn = null;
      if (logger != null) {
         toReturn = new LogPrintWriter(logger, true);
      }
      return toReturn;
   }

   private static PrintWriter newDebugWriter(Log logger) {
      PrintWriter toReturn = null;
      if (logger != null) {
         toReturn = new LogPrintWriter(logger, false);
      }
      return toReturn;
   }

   private class ShutdownHook extends Thread {

      private final String dbId;

      public ShutdownHook(String dbId) {
         this.dbId = dbId;
      }

      @Override
      public void run() {
         try {
            shutdown(dbId);
         } catch (Exception ex) {
            // do nothing
         }
      }

   }

   private static class LogPrintWriter extends PrintWriter {

      private final StringBuilder builder = new StringBuilder();

      private final Log log;
      private final boolean logToError;

      public LogPrintWriter(Log log, boolean logToError) {
         super(new NoOpWriter());
         this.log = log;
         this.logToError = logToError;
      }

      @Override
      public void close() {
         flushBuffer();
         super.close();
      }

      @Override
      public void flush() {
         flushBuffer();
         super.flush();
      }

      @Override
      public void write(int c) {
         builder.append(c);
      }

      @Override
      public void write(char cbuf[], int off, int len) {
         builder.append(cbuf, off, len);
      }

      @Override
      public void write(String str, int off, int len) {
         builder.append(str.substring(off, off + len));
      }

      @Override
      public void println() {
         if (log == null) {
            builder.append('\n');
         }
         flushBuffer();
      }

      private void flushBuffer() {
         if (builder.length() == 0) {
            return;
         }
         if (log != null) {
            if (logToError) {
               log.error(builder.toString());
            } else {
               log.debug(builder.toString());
            }
         } else {
            try {
               out.write(builder.toString());
            } catch (IOException e) {
               this.setError();
            }
         }
         builder.setLength(0);
      }

      private static class NoOpWriter extends Writer {

         @Override
         public void close() {
            // do nothing
         }

         @Override
         public void flush() {
            // do nothing
         }

         @Override
         public void write(char cbuf[], int off, int len) {
            // do nothing
         }
      }

   }
}