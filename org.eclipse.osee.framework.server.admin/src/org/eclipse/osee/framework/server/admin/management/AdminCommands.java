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
   private final AttributeFileNameToGuidOperation changeFileNamesToGuid;

   public AdminCommands() {
      this.shutdownWorker = new ServerShutdownWorker();
      this.shutdownWorker.setExecutionAllowed(true);

      this.addServerVersion = new AddServerVersionWorker();
      this.addServerVersion.setExecutionAllowed(true);

      this.removeServerVersion = new RemoveServerVersionWorker();
      this.removeServerVersion.setExecutionAllowed(true);

      this.changeFileNamesToGuid = new AttributeFileNameToGuidOperation();
      this.changeFileNamesToGuid.setExecutionAllowed(true);

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

   public void startAttributeURItoGuidChange(CommandInterpreter ci) {
      if (!this.changeFileNamesToGuid.isRunning()) {
         this.changeFileNamesToGuid.setCommandInterpreter(ci);
         this.changeFileNamesToGuid.setExecutionAllowed(true);
         Operations.executeAsJob(changeFileNamesToGuid, false);
      } else {
         if (this.changeFileNamesToGuid.isRunning()) {
            ci.println("Waiting for Attribute URI To Guid");
         }
      }
   }

   public void stopAttributeURItoGuidChange(CommandInterpreter ci) {
      if (this.changeFileNamesToGuid.isRunning()) {
         this.changeFileNamesToGuid.setExecutionAllowed(false);
      } else {
         ci.println("Attribute URI To Guid is not running.");
      }
   }

   public void setServletRequestProcessing(CommandInterpreter ci) {
      ServerRequestsWorker worker = new ServerRequestsWorker();
      worker.setCommandInterpreter(ci);
      worker.setExecutionAllowed(true);
      Operations.executeAsJob(worker, false);
   }

}
