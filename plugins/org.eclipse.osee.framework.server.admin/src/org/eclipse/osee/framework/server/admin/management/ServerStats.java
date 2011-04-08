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
package org.eclipse.osee.framework.server.admin.management;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.database.core.DatabaseInfoManager;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ServerStats extends AbstractOperation {

   public ServerStats(OperationLogger logger) {
      super("Server Stats", Activator.PLUGIN_ID, logger);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      IApplicationServerManager manager = Activator.getApplicationServerManager();
      ISessionManager sessionManager = Activator.getSessionManager();

      log("\n----------------------------------------------");
      log("                  Server Stats");
      log("----------------------------------------------");

      logf("Server:[%s:%s]", manager.getServerAddress(), manager.getPort());
      logf("Id: [%s]", manager.getId());
      log(String.format("Running Since: [%s]\n",
         DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(manager.getDateStarted())));

      logf("Code Base Location: [%s]", System.getProperty("user.dir"));
      logf("Datastore: [%s]", DatabaseInfoManager.getDefault().toString());
      logf("Binary Data Path: [%s]\n", OseeServerProperties.getOseeApplicationServerData());

      logf("Supported Versions: %s", Arrays.deepToString(manager.getSupportedVersions()));
      logf("Accepting Requests: [%s]", manager.isAcceptingRequests());
      log(Lib.getMemoryInfo());

      logServlets(manager);

      logf("\nSessionsManaged: [%s]", sessionManager.getAllSessions(false).size());
      logf("\nServer State: [%s]", manager.isSystemIdle() ? "IDLE" : "BUSY");
      logf("Active Threads: [%s]", manager.getNumberOfActiveThreads());

      IJobManager jobManager = Job.getJobManager();
      logf("Job Manager: [%s]", jobManager.isIdle() ? "IDLE" : "BUSY");
      logf("Current Job: [%s]", jobManager.currentJob().getName());

      log("Current Tasks: ");
      List<String> entries = manager.getCurrentProcesses();
      if (entries.isEmpty()) {
         log("[NONE]");
      } else {
         log();
         for (int index = 0; index < entries.size(); index++) {
            logf("[%s] ", index);
            log(entries.get(index));
            if (index + 1 < entries.size()) {
               log();
            }
         }
      }

      log();

   }

   private void logServlets(IApplicationServerManager manager) {
      log("Servlets:");
      List<String> contexts = new ArrayList<String>(manager.getRegisteredServlets());
      Collections.sort(contexts);
      if (contexts.size() % 2 == 1) {
         contexts.add("");
      }
      int midPoint = contexts.size() / 2;
      for (int i = 0; i < midPoint; i++) {
         logf("%-40.40s%s", contexts.get(i), contexts.get(i + midPoint));
      }
   }
}
