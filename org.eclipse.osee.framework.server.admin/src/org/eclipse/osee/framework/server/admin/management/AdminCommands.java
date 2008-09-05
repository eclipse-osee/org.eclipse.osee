/*
 * Created on Sep 4, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.management;

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class AdminCommands {
   private static AdminCommands instance = null;

   private ServerShutdownWorker shutdownWorker;

   public static AdminCommands getInstance() {
      if (instance == null) {
         instance = new AdminCommands();
      }
      return instance;
   }

   private AdminCommands() {
      this.shutdownWorker = new ServerShutdownWorker();
      this.shutdownWorker.setExecutionAllowed(true);
   }

   public void getServerStatus(CommandInterpreter ci) {
      ServerStats stats = new ServerStats();
      stats.setCommandInterpreter(ci);
      stats.setExecutionAllowed(true);
      Thread th = new Thread(stats);
      th.setName("Server Statistics");
      th.start();
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
}
