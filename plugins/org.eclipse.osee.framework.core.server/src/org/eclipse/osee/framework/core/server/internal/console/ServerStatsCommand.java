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
package org.eclipse.osee.framework.core.server.internal.console;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcServerConfig;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Roberto E. Escobar
 */
public class ServerStatsCommand implements ConsoleCommand {

   private final Map<String, JdbcService> jdbcServices = new ConcurrentHashMap<String, JdbcService>();
   private IApplicationServerManager manager;
   private IAuthenticationManager authManager;

   public void setApplicationServerManager(IApplicationServerManager manager) {
      this.manager = manager;
   }

   public void setAuthenticationManager(IAuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public void addJdbcService(JdbcService jdbcService) {
      jdbcServices.put(jdbcService.getId(), jdbcService);
   }

   public void removeJdbcService(JdbcService jdbcService) {
      jdbcServices.remove(jdbcService.getId());
   }

   @Override
   public String getName() {
      return "server_status";
   }

   @Override
   public String getDescription() {
      return "Displays server status information";
   }

   @Override
   public String getUsage() {
      return "";
   }

   @Override
   public Callable<?> createCallable(final Console console, final ConsoleParameters params) {
      return new Callable<Boolean>() {

         @Override
         public Boolean call() throws Exception {
            console.writeln("\n----------------------------------------------");
            console.writeln("                  Server Stats");
            console.writeln("----------------------------------------------");

            console.writeln("Server:[%s]", manager.getServerUri());
            console.writeln("Id: [%s]", manager.getId());
            console.writeln("Running Since: [%s]\n",
               DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(manager.getDateStarted()));

            console.writeln("Code Base Location: [%s]", System.getProperty("user.dir"));
            console.writeln("Binary Data Path: [%s]", OseeServerProperties.getOseeApplicationServerData(null));
            JdbcService jdbcService = getJdbcService("orcs.jdbc.service");
            writeOrcsJdbcServiceInfo(jdbcService);
            console.writeln();

            console.writeln("Authentication Scheme: [%s]", authManager.getProtocol());
            console.writeln("Supported Authentication Schemes: %s", Arrays.deepToString(authManager.getProtocols()));
            console.writeln();

            console.writeln("Supported Versions: %s", Arrays.deepToString(manager.getVersions()));
            console.writeln(Lib.getMemoryInfo());

            logServlets(manager);

            console.writeln("\nServer State: [%s]", manager.isSystemIdle() ? "IDLE" : "BUSY");
            console.writeln("Active Threads: [%s]", manager.getNumberOfActiveThreads());

            IJobManager jobManager = Job.getJobManager();
            console.writeln("Job Manager: [%s]", jobManager.isIdle() ? "IDLE" : "BUSY");

            Job current = jobManager.currentJob();

            console.writeln("Current Job: [%s]", current != null ? current.getName() : "NONE");

            console.write("Current Tasks: ");
            List<String> entries = manager.getCurrentProcesses();
            if (entries.isEmpty()) {
               console.writeln("[NONE]");
            } else {
               console.writeln();
               for (int index = 0; index < entries.size(); index++) {
                  console.writeln("\t[%s] - %s", index, entries.get(index));
               }
            }
            console.writeln();
            writeJdbcStats();
            console.writeln();
            return Boolean.TRUE;
         }

         private void logServlets(IApplicationServerManager manager) {
            console.writeln("Servlets:");
            List<String> contexts = new ArrayList<String>(manager.getRegisteredServlets());
            Collections.sort(contexts);
            if (contexts.size() % 2 == 1) {
               contexts.add("");
            }
            int midPoint = contexts.size() / 2;
            for (int i = 0; i < midPoint; i++) {
               console.writeln("%-40.40s%s", contexts.get(i), contexts.get(i + midPoint));
            }
         }

         private void writeOrcsJdbcServiceInfo(JdbcService jdbcService) {
            if (jdbcService != null) {
               JdbcClientConfig config = jdbcService.getClient().getConfig();
               console.writeln("Datastore Uri: [%s]", config.getDbUri());
               if (jdbcService.hasServer()) {
                  JdbcServerConfig serverConfig = jdbcService.getServerConfig();
                  console.writeln("Datastore Db Path: [%s]", serverConfig.getDbPath());
               }
            } else {
               console.writeln("Datastore: [N/A]");
            }
         }

         private JdbcService getJdbcService(String binding) {
            JdbcService toReturn = null;
            for (JdbcService jdbcService : jdbcServices.values()) {
               if (jdbcService.getBindings().contains(binding)) {
                  toReturn = jdbcService;
                  break;
               }
            }
            return toReturn;
         }

         private void writeJdbcStats() {
            console.writeln("Jdbc Services:");
            boolean isFirst = true;
            for (JdbcService jdbcService : jdbcServices.values()) {
               try {
                  if (!isFirst) {
                     console.writeln("");
                  }
                  console.writeln("\tid: %s", jdbcService.getId());
                  console.writeln("\tbindings: %s", jdbcService.getBindings());
                  console.writeln("\turi: %s", jdbcService.getClient().getConfig().getDbUri());
                  if (jdbcService.hasServer()) {
                     console.writeln("\tdb.file: %s", jdbcService.getServerConfig().getDbPath());
                  }
                  Map<String, String> store = jdbcService.getClient().getStatistics();
                  for (String key : store.keySet()) {
                     String value = store.get(key);
                     console.writeln("\t%s: %s", key, value);
                  }
                  isFirst = false;
               } catch (Exception ex) {
                  console.write(ex);
               }
            }
         }
      };
   }

}
