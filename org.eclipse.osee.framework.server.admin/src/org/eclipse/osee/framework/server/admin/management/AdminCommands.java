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

import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class AdminCommands {

   private final ServerShutdownWorker shutdownWorker;
   private final RemoveServerVersionWorker removeServerVersion;
   private final AddServerVersionWorker addServerVersion;
   private final ReloadCachesWorker reloadCacheWorker;
   private final ClearCachesWorker clearCacheWorker;

   public AdminCommands() {
      this.shutdownWorker = new ServerShutdownWorker();
      this.shutdownWorker.setExecutionAllowed(true);

      this.addServerVersion = new AddServerVersionWorker();
      this.addServerVersion.setExecutionAllowed(true);

      this.removeServerVersion = new RemoveServerVersionWorker();
      this.removeServerVersion.setExecutionAllowed(true);

      this.reloadCacheWorker = new ReloadCachesWorker();
      this.reloadCacheWorker.setExecutionAllowed(true);

      this.clearCacheWorker = new ClearCachesWorker();
      this.clearCacheWorker.setExecutionAllowed(true);
   }

   public void getServerStatus(CommandInterpreter ci) {
      ServerStats stats = new ServerStats();
      stats.setCommandInterpreter(ci);
      stats.setExecutionAllowed(true);
      Operations.executeAsJob(stats, false);
      // Thread th = new Thread(stats);
      // th.setName("Server Statistics");
      // th.start();
   }

   public void getServerVersion(CommandInterpreter ci) {
      GetServerVersionWorker serverVersion = new GetServerVersionWorker();
      serverVersion.setCommandInterpreter(ci);
      serverVersion.setExecutionAllowed(true);
      Operations.executeAsJob(serverVersion, false);
   }

   public void reloadCache(CommandInterpreter ci) {
      if (!this.clearCacheWorker.isRunning() && !this.reloadCacheWorker.isRunning()) {
         this.reloadCacheWorker.setCommandInterpreter(ci);
         Operations.executeAsJob(reloadCacheWorker, false);
      } else {
         if (clearCacheWorker.isRunning()) {
            ci.println("Waiting for clear cache to complete");
         }
         if (reloadCacheWorker.isRunning()) {
            ci.println("Waiting for reload cache to complete");
         }
      }
   }

   public void clearCache(CommandInterpreter ci) {
      if (!this.clearCacheWorker.isRunning() && !this.reloadCacheWorker.isRunning()) {
         this.clearCacheWorker.setCommandInterpreter(ci);
         Operations.executeAsJob(clearCacheWorker, false);
      } else {
         if (clearCacheWorker.isRunning()) {
            ci.println("Waiting for clear cache to complete");
         }
         if (reloadCacheWorker.isRunning()) {
            ci.println("Waiting for reload cache to complete");
         }
      }
   }

   public void addServerVersion(CommandInterpreter ci) {
      if (!this.removeServerVersion.isRunning()) {
         this.addServerVersion.setCommandInterpreter(ci);
         Operations.executeAsJob(addServerVersion, false);
      } else {
         ci.println("Waiting for remove server version");
      }
   }

   public void removeServerVersion(CommandInterpreter ci) {
      if (!this.addServerVersion.isRunning()) {
         this.removeServerVersion.setCommandInterpreter(ci);
         Operations.executeAsJob(removeServerVersion, false);
      } else {
         ci.println("Waiting for add server version");
      }
   }

   public void startServerShutdown(CommandInterpreter ci) {
      if (!this.shutdownWorker.isRunning()) {
         this.shutdownWorker.setCommandInterpreter(ci);
         this.shutdownWorker.setExecutionAllowed(true);
         Operations.executeAsJob(shutdownWorker, false);
      } else {
         if (this.shutdownWorker.isRunning()) {
            ci.println("Waiting for shutdown");
         }
      }
   }

   public void stopServerShutdown(CommandInterpreter ci) {
      if (this.shutdownWorker.isRunning()) {
         this.shutdownWorker.setExecutionAllowed(false);
      } else {
         ci.println("Server shutdown is not running.");
      }
   }

   public void setServletRequestProcessing(CommandInterpreter ci) {
      ServerRequestsWorker worker = new ServerRequestsWorker();
      worker.setCommandInterpreter(ci);
      worker.setExecutionAllowed(true);
      Operations.executeAsJob(worker, false);
   }

}
