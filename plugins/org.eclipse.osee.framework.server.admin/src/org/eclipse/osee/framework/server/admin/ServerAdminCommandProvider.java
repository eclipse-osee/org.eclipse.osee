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
package org.eclipse.osee.framework.server.admin;

import java.util.Arrays;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.branch.management.TxCurrentsAndModTypesCommand;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.operation.CommandInterpreterReporter;
import org.eclipse.osee.framework.core.operation.OperationReporter;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.server.admin.management.AdminCommands;
import org.eclipse.osee.framework.server.admin.management.ConsolidateArtifactVersionsCommand;
import org.eclipse.osee.framework.server.admin.management.FinishPartiallyArchivedBranchesCommand;
import org.eclipse.osee.framework.server.admin.management.GarbageCollectionCommand;
import org.eclipse.osee.framework.server.admin.management.SchedulingCommand;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerAdminCommandProvider implements CommandProvider {

   private final AdminCommands adminCommands;

   public ServerAdminCommandProvider() {
      this.adminCommands = new AdminCommands();
   }

   public Job _server_status(CommandInterpreter ci) {
      return adminCommands.getServerStatus(ci);
   }

   public Job _server_process_requests(CommandInterpreter ci) {
      return adminCommands.setServletRequestProcessing(ci);
   }

   public Job _add_osee_version(CommandInterpreter ci) {
      return adminCommands.addServerVersion(ci);
   }

   public Job _remove_osee_version(CommandInterpreter ci) {
      return adminCommands.removeServerVersion(ci);
   }

   public Job _osee_version(CommandInterpreter ci) {
      return adminCommands.getServerVersion(ci);
   }

   public Job _reload_cache(CommandInterpreter ci) {
      return adminCommands.reloadCache(ci);
   }

   public Job _clear_cache(CommandInterpreter ci) {
      return adminCommands.clearCache(ci);
   }

   public Job _finish_partial_archives(CommandInterpreter ci) {
      return Operations.executeAsJob(new FinishPartiallyArchivedBranchesCommand(ci), false);
   }

   public Job _consolidate_artifact_versions(CommandInterpreter ci) {
      return Operations.executeAsJob(new ConsolidateArtifactVersionsCommand(ci), false);
   }

   public Job _gc(CommandInterpreter ci) {
      return Operations.executeAsJob(new GarbageCollectionCommand(ci), false);
   }

   public Job _schedule(CommandInterpreter ci) {
      return Operations.executeAsJob(new SchedulingCommand(ci), false);
   }

   public Job _tx_currents(CommandInterpreter ci) {
      OperationReporter reporter = new CommandInterpreterReporter(ci);
      boolean archived = Boolean.parseBoolean(ci.nextArgument());
      return Operations.executeAsJob(new TxCurrentsAndModTypesCommand(reporter, archived), false);
   }

   public void _osee_shutdown(CommandInterpreter ci) {
      adminCommands.oseeShutdown(ci);
   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Server Admin Commands---\n");
      sb.append("        server_status - displays server status\n");
      sb.append("        server_process_requests [true | false]- command servlets to accept/reject requests\n");
      sb.append("        osee_version - displays the supported osee versions\n");
      sb.append("        add_osee_version [version string]- add the version string to the list of supported osee versions\n");
      sb.append("        remove_osee_version [version string]- removes the version string from the list of supported osee versions\n");
      sb.append("        finish_partial_archives - move txs addressing to osee_txs_archived for archived branches\n");
      sb.append("        consolidate_artifact_versions - migrate to 0.9.2 database schema\n");
      sb.append("        tx_currents [true | false] - detect and fix tx current and mod types inconsistencies on archive txs or txs\n");
      sb.append("        osee_shutdown [-oseeOnly] - immediately release the listening port then waits for all existing operations to finish. \n");
      sb.append("        gc - run java garbage collecction\n");
      sb.append("        schedule <delay seconds> <iterations> <command> - runs the command after the specified delay and repeat given number of times\n");
      sb.append(String.format("        reload_cache %s? - reloads server caches\n",
         Arrays.deepToString(OseeCacheEnum.values()).replaceAll(",", " | ")));
      sb.append(String.format("        clear_cache %s? - decaches all objects from the specified caches\n",
         Arrays.deepToString(OseeCacheEnum.values()).replaceAll(",", " |")));
      return sb.toString();
   }
}
