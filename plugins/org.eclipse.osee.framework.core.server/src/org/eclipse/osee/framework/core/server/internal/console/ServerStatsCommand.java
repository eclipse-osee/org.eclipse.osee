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
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.database.DatabaseInfoRegistry;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class ServerStatsCommand implements ConsoleCommand {

   private IApplicationServerManager appManager;
   private DatabaseInfoRegistry registry;
   private IAuthenticationManager authenticationManager;
   private IOseeDatabaseService dbService;

   public void setDbInfoRegistry(DatabaseInfoRegistry registry) {
      this.registry = registry;
   }

   public void setApplicationServerManager(IApplicationServerManager appManager) {
      this.appManager = appManager;
   }

   public void setAuthenticationManager(IAuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   private DatabaseInfoRegistry getDbInfoRegistry() {
      return registry;
   }

   private IApplicationServerManager getApplicationServerManager() {
      return appManager;
   }

   private IAuthenticationManager getAuthenticationManager() {
      return authenticationManager;
   }

   private IOseeDatabaseService getDbService() {
      return dbService;
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
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new ServerStatsCallable(getDbInfoRegistry(), getApplicationServerManager(), getAuthenticationManager(),
         getDbService(), console);
   }

   private static final class ServerStatsCallable implements Callable<Boolean> {
      private final DatabaseInfoRegistry registry;
      private final IApplicationServerManager manager;
      private final IAuthenticationManager authManager;
      private final IOseeDatabaseService dbService;
      private final Console console;

      public ServerStatsCallable(DatabaseInfoRegistry registry, IApplicationServerManager manager, IAuthenticationManager authenticationManager, IOseeDatabaseService dbService, Console console) {
         super();
         this.registry = registry;
         this.manager = manager;
         this.authManager = authenticationManager;
         this.dbService = dbService;
         this.console = console;
      }

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
         console.writeln("Datastore: [%s]", registry.getSelectedDatabaseInfo().toString());
         console.writeln("Binary Data Path: [%s]", OseeServerProperties.getOseeApplicationServerData(null));
         console.writeln();

         console.writeln("Authentication Scheme: [%s]", authManager.getProtocol());
         console.writeln("Supported Authentication Schemes: %s", Arrays.deepToString(authManager.getProtocols()));
         console.writeln();

         console.writeln("Supported Versions: %s", Arrays.deepToString(manager.getSupportedVersions()));
         console.writeln("Accepting Requests: [%s]", manager.isAcceptingRequests());
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
         logDatabaseStats(dbService);
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

      private void logDatabaseStats(IOseeDatabaseService dbService) {
         console.writeln("Database Stats:");
         try {
            Map<String, String> store = dbService.getStatistics();
            for (String key : store.keySet()) {
               String value = store.get(key);

               console.writeln("\t%s = %s", key, value);
            }

         } catch (Exception ex) {
            console.write(ex);
         }
      }

   }

}
