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

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.jdbc.JdbcLogger;
import org.eclipse.osee.jdbc.JdbcServer;
import org.eclipse.osee.jdbc.JdbcServerConfig;

/**
 * @author Mark Joy
 */
public final class HsqlJdbcServer implements JdbcServer {

   private final JdbcLogger logger;
   private final JdbcServerConfig config;
   private final AtomicReference<HsqlServerDelegate> serverRef = new AtomicReference<>();
   private final AtomicBoolean wasStarted = new AtomicBoolean(false);

   public HsqlJdbcServer(JdbcLogger logger, JdbcServerConfig config) {
      super();
      this.logger = logger;
      this.config = config;
   }

   @Override
   public void start() {
      if (!wasStarted.getAndSet(true)) {
         String host = config.getDbHost();
         int port = config.getDbPort();
         String path = config.getDbPath();
         String dbName = config.getDbName();
         Properties properties = config.getProperties();

         HsqlServerDelegate oldInfo = serverRef.getAndSet(new HsqlServerDelegate(host, port, path, dbName, properties));
         if (oldInfo != null) {
            oldInfo.shutdown(logger);
         }

         HsqlServerDelegate server = serverRef.get();
         logInfo("Starting Hsql Database Server on [%s:%s]....", host, port, host);
         try {
            server.start(logger);
            if (isAlive(config.getStartUpWaitTimeout())) {
               server.printInfo(logger);
            } else {
               logInfo("Exiting, since unable to connect to Hsql Database Server.");
               logInfo("Please try to increase the amount of time to keep trying to connect to the Server.");
            }
         } catch (Exception ex) {
            logError(ex, "Error starting Hsql server");
         }
      }
   }

   @Override
   public void stop() {
      if (wasStarted.getAndSet(false)) {
         HsqlServerDelegate server = serverRef.getAndSet(null);
         if (server != null) {
            server.shutdown(logger);
         }
      }
   }

   @Override
   public boolean isAlive() {
      return isAlive(config.getAliveWaitTimeout());
   }

   @Override
   public boolean isAlive(long waitTime) {
      boolean result = false;
      HsqlServerDelegate server = serverRef.get();
      if (server != null) {
         result = server.isAlive(waitTime);
      }
      return result;
   }

   @Override
   public JdbcServerConfig getConfig() {
      return config;
   }

   private void logInfo(String msg, Object... data) {
      if (logger != null) {
         logger.info(msg, data);
      }
   }

   private void logError(Exception ex, String msg, Object... data) {
      if (logger != null) {
         logger.error(ex, msg, data);
      }
   }

}