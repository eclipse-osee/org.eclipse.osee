/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jdbc.internal.dbsupport.hsql;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.jdbc.JdbcLogger;
import org.hsqldb.Database;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;

/**
 * @author Mark Joy
 */
public final class HsqlServerDelegate {
   private static final String sc_key_remote_open_db = "server.remote_open";

   private final String dbHost;
   private final int dbPort;
   private final String dbPath;
   private final String dbName;
   private final Properties dbProperties;

   private final AtomicBoolean wasStarted = new AtomicBoolean();
   private Server server;
   private Thread hook;

   public HsqlServerDelegate(String dbHost, int dbPort, String dbPath, String dbName, Properties dbProperties) {
      super();
      this.dbHost = dbHost;
      this.dbPort = dbPort;
      this.dbPath = dbPath;
      this.dbName = dbName;
      this.dbProperties = dbProperties;
   }

   public void start(final JdbcLogger logger) throws Exception {
      if (!wasStarted.getAndSet(true)) {
         server = new Server();
         server.setErrWriter(newErrorWriter(logger));
         server.setLogWriter(newDebugWriter(logger));

         InetAddress address = InetAddress.getByName(dbHost);
         server.setAddress(address.getHostAddress());
         server.setPort(dbPort);

         StringBuilder dbCreationUrl = new StringBuilder();
         dbCreationUrl.append(dbPath);

         appendProperties(dbCreationUrl, dbProperties);

         server.setDatabaseName(0, dbName);
         server.setDatabasePath(0, dbCreationUrl.toString());

         HsqlProperties props = new HsqlProperties();
         props.setProperty(sc_key_remote_open_db, true);
         server.setProperties(props);

         server.start();
         hook = new Thread() {

            @Override
            public void run() {
               try {
                  shutdown(logger);
               } catch (Exception ex) {
                  // do nothing
               }
            }
         };
         Runtime.getRuntime().addShutdownHook(hook);
      }
   }

   private void appendProperties(StringBuilder builder, Properties props) {
      if (!props.isEmpty()) {
         for (Entry<Object, Object> entry : props.entrySet()) {
            builder.append(String.format(";%s=%s", entry.getKey(), entry.getValue()));
         }
      }
   }

   public void shutdown(JdbcLogger logger) {
      if (wasStarted.getAndSet(false)) {
         if (hook != null) {
            try {
               Runtime.getRuntime().removeShutdownHook(hook);
            } catch (Exception ex) {
               // do nothing;
            }
         }

         if (server != null) {
            try {
               server.shutdownWithCatalogs(Database.CLOSEMODE_NORMAL);
            } catch (Exception ex) {
               if (logger != null) {
                  logger.error(ex, "Error during shutdown");
               }
            }
         }
      }
   }

   public void printInfo(JdbcLogger logger) {
      if (logger != null) {
         if (server != null) {
            try {
               StringBuilder builder = new StringBuilder();
               builder.append("HyperSQL Database: ");
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
               logger.info(builder.toString());
            } catch (Exception ex) {
               logger.error(ex, "Error getting Server Information");
            }
         } else {
            logger.info("Server was not started");
         }
      }
   }

   public boolean isAlive(long waitTimeMillis) {
      boolean result = false;
      if (server != null) {
         try {
            if (waitTimeMillis > 0) {
               int totalWaitTime = 0;
               while (totalWaitTime < waitTimeMillis && !result) {
                  try {
                     server.checkRunning(true);
                     result = true;
                  } catch (Exception ex) {
                     try {
                        if (totalWaitTime < waitTimeMillis) {
                           Thread.sleep(100);
                           totalWaitTime += 100;
                        }
                     } catch (InterruptedException ex1) {
                        // do nothing;
                     }
                  }
               }
            }
         } catch (Exception ex) {
            // do nothing;
         }
      }
      return result;
   }

   private static PrintWriter newErrorWriter(JdbcLogger logger) {
      PrintWriter toReturn = null;
      if (logger != null) {
         toReturn = new LogPrintWriter(logger, true);
      }
      return toReturn;
   }

   private static PrintWriter newDebugWriter(JdbcLogger logger) {
      PrintWriter toReturn = null;
      if (logger != null) {
         toReturn = new LogPrintWriter(logger, false);
      }
      return toReturn;
   }

   private static class LogPrintWriter extends PrintWriter {

      private final StringBuilder builder = new StringBuilder();

      private final JdbcLogger log;
      private final boolean logToError;

      public LogPrintWriter(JdbcLogger log, boolean logToError) {
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