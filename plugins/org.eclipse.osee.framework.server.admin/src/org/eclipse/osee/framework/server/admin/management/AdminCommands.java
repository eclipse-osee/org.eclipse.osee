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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class AdminCommands {

   private ServerShutdownWorker shutdownWorker;
   private final RemoveServerVersionWorker removeServerVersion;
   private final AddServerVersionWorker addServerVersion;
   private final ReloadCachesWorker reloadCacheWorker;
   private final ClearCachesWorker clearCacheWorker;

   public AdminCommands() {
      this.addServerVersion = new AddServerVersionWorker();
      this.addServerVersion.setExecutionAllowed(true);

      this.removeServerVersion = new RemoveServerVersionWorker();
      this.removeServerVersion.setExecutionAllowed(true);

      this.reloadCacheWorker = new ReloadCachesWorker();
      this.reloadCacheWorker.setExecutionAllowed(true);

      this.clearCacheWorker = new ClearCachesWorker();
      this.clearCacheWorker.setExecutionAllowed(true);
   }

   public Job getServerStatus(CommandInterpreter ci) {
      return Operations.executeAsJob(new ServerStats(ci), false);
   }

   public Job getServerVersion(CommandInterpreter ci) {
      GetServerVersionWorker serverVersion = new GetServerVersionWorker();
      serverVersion.setCommandInterpreter(ci);
      serverVersion.setExecutionAllowed(true);
      return Operations.executeAsJob(serverVersion, false);
   }

   public Job reloadCache(CommandInterpreter ci) {
      if (!this.clearCacheWorker.isRunning() && !this.reloadCacheWorker.isRunning()) {
         this.reloadCacheWorker.setCommandInterpreter(ci);
         return Operations.executeAsJob(reloadCacheWorker, false);
      } else {
         if (clearCacheWorker.isRunning()) {
            ci.println("Waiting for clear cache to complete");
         }
         if (reloadCacheWorker.isRunning()) {
            ci.println("Waiting for reload cache to complete");
         }
         return null;
      }
   }

   public Job clearCache(CommandInterpreter ci) {
      if (!this.clearCacheWorker.isRunning() && !this.reloadCacheWorker.isRunning()) {
         this.clearCacheWorker.setCommandInterpreter(ci);
         return Operations.executeAsJob(clearCacheWorker, false);
      } else {
         if (clearCacheWorker.isRunning()) {
            ci.println("Waiting for clear cache to complete");
         }
         if (reloadCacheWorker.isRunning()) {
            ci.println("Waiting for reload cache to complete");
         }
         return null;
      }
   }

   public Job addServerVersion(CommandInterpreter ci) {
      if (!this.removeServerVersion.isRunning()) {
         this.addServerVersion.setCommandInterpreter(ci);
         return Operations.executeAsJob(addServerVersion, false);
      } else {
         ci.println("Waiting for remove server version");
         return null;
      }
   }

   public Job removeServerVersion(CommandInterpreter ci) {
      if (!this.addServerVersion.isRunning()) {
         this.removeServerVersion.setCommandInterpreter(ci);
         return Operations.executeAsJob(removeServerVersion, false);
      } else {
         ci.println("Waiting for add server version");
         return null;
      }
   }

   public void oseeShutdown(CommandInterpreter ci) {
      if (shutdownWorker == null) {
         this.shutdownWorker = new ServerShutdownWorker(ci);
         Operations.executeAsJob(shutdownWorker, false);
      } else {
         ci.println("Waiting for shutdown");
      }
   }

   public Job setServletRequestProcessing(CommandInterpreter ci) {
      ServerRequestsWorker worker = new ServerRequestsWorker();
      worker.setCommandInterpreter(ci);
      worker.setExecutionAllowed(true);
      return Operations.executeAsJob(worker, false);
   }
}