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

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class AdminCommands {
   private static AdminCommands instance = null;

   private ServerShutdownWorker shutdownWorker;
   private RemoveServerVersionWorker removeServerVersion;
   private AddServerVersionWorker addServerVersion;

   public static AdminCommands getInstance() {
      if (instance == null) {
         instance = new AdminCommands();
      }
      return instance;
   }

   private AdminCommands() {
      this.shutdownWorker = new ServerShutdownWorker();
      this.shutdownWorker.setExecutionAllowed(true);

      this.addServerVersion = new AddServerVersionWorker();
      this.addServerVersion.setExecutionAllowed(true);

      this.removeServerVersion = new RemoveServerVersionWorker();
      this.removeServerVersion.setExecutionAllowed(true);
   }

   public void getServerStatus(CommandInterpreter ci) {
      ServerStats stats = new ServerStats();
      stats.setCommandInterpreter(ci);
      stats.setExecutionAllowed(true);
      Thread th = new Thread(stats);
      th.setName("Server Statistics");
      th.start();
   }

   public void getServerVersion(CommandInterpreter ci) {
      GetServerVersionWorker serverVersion = new GetServerVersionWorker();
      serverVersion.setCommandInterpreter(ci);
      serverVersion.setExecutionAllowed(true);
      Thread th = new Thread(serverVersion);
      th.setName("Server Version");
      th.start();
   }

   public void addServerVersion(CommandInterpreter ci) {
      if (!this.removeServerVersion.isRunning()) {
         this.addServerVersion.setCommandInterpreter(ci);
         Thread th = new Thread(addServerVersion);
         th.setName("Add Server Version");
         th.start();
      } else {
         ci.println("Waiting for remove server version");
      }
   }

   public void removeServerVersion(CommandInterpreter ci) {
      if (!this.addServerVersion.isRunning()) {
         this.removeServerVersion.setCommandInterpreter(ci);
         Thread th = new Thread(removeServerVersion);
         th.setName("Remove Server Version");
         th.start();
      } else {
         ci.println("Waiting for add server version");
      }
   }

   public void startServerShutdown(CommandInterpreter ci) {
      if (!this.shutdownWorker.isRunning()) {
         this.shutdownWorker.setCommandInterpreter(ci);
         this.shutdownWorker.setExecutionAllowed(true);
         Thread th = new Thread(shutdownWorker);
         th.setName("Server Shutdown requested.... please wait");
         th.start();
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
      Thread th = new Thread(worker);
      th.setName("Server Requests");
      th.start();
   }
}
