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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.cache.BranchMoveOperation;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.server.admin.Activator;
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
   }

   public void finishPartialArchives(CommandInterpreter ci) {
      String sql =
            "select branch_id from osee_branch br where exists (select 1 from osee_txs txs where txs.branch_id = br.branch_id and br.archived = " + BranchArchivedState.ARCHIVED.getValue() + ")";
      IOseeStatement chStmt = null;
      try {
         IOseeDatabaseService databaseService = Activator.getInstance().getOseeDatabaseService();
         chStmt = databaseService.getStatement();

         List<Branch> branches = new ArrayList<Branch>(100);
         chStmt.runPreparedQuery(100, sql);
         BranchCache branchCache = Activator.getInstance().getOseeCachingService().getBranchCache();
         while (chStmt.next()) {
            branches.add(branchCache.getById(chStmt.getInt("branch_id")));
         }
         for (Branch branch : branches) {
            Operations.executeAsJob(new BranchMoveOperation(Activator.getInstance(), true, branch), false);
         }
      } catch (OseeCoreException ex) {
         ci.printStackTrace(ex);
      } finally {
         if (chStmt != null) {
            chStmt.close();
         }
      }
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

   @Deprecated
   //this public method is never called
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

   @Deprecated
   //this public method is never called
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
