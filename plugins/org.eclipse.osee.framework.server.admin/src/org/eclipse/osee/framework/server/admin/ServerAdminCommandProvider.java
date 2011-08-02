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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.branch.management.TxCurrentsAndModTypesCommand;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.operation.CommandInterpreterLogger;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.MutexSchedulingRule;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.operation.ConsolidateArtifactVersionTxOperation;
import org.eclipse.osee.framework.database.operation.ParseWindowsDirectoryListingOperation;
import org.eclipse.osee.framework.database.operation.ConsolidateRelationsTxOperation;
import org.eclipse.osee.framework.database.operation.PurgeUnusedBackingDataAndTransactions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osee.framework.server.admin.management.SchedulingCommand;
import org.eclipse.osee.framework.server.admin.management.ServerShutdownOperation;
import org.eclipse.osee.framework.server.admin.management.ServerStats;
import org.eclipse.osee.framework.server.admin.management.UpdateCachesOperation;
import org.eclipse.osee.framework.server.admin.management.UpdateServerVersionsOperation;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerAdminCommandProvider implements CommandProvider {

   private final ISchedulingRule versionMutex = new MutexSchedulingRule();
   private final ISchedulingRule cacheMutex = new MutexSchedulingRule();
   private final ISchedulingRule shutdownMutex = new MutexSchedulingRule();

   public Job _server_status(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new ServerStats(logger), false);
   }

   public void _server_process_requests(CommandInterpreter ci) throws OseeCoreException {
      String value = ci.nextArgument();
      Activator.getApplicationServerManager().setServletRequestsAllowed(new Boolean(value));
   }

   public Job _add_osee_version(CommandInterpreter ci) {
      return updateServerVersions(ci, true);
   }

   public Job _remove_osee_version(CommandInterpreter ci) {
      return updateServerVersions(ci, false);
   }

   private Job updateServerVersions(CommandInterpreter ci, boolean add) {
      String version = ci.nextArgument();
      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new UpdateServerVersionsOperation(logger, version, add), false, versionMutex);
   }

   public void _osee_version(CommandInterpreter ci) {
      ci.print("Osee Application Server: ");
      ci.println(Arrays.deepToString(Activator.getApplicationServerManager().getSupportedVersions()));
   }

   public Job _reload_cache(CommandInterpreter ci) {
      return updateCaches(ci, false);
   }

   public Job _clear_cache(CommandInterpreter ci) {
      return updateCaches(ci, false);
   }

   private Job updateCaches(CommandInterpreter ci, boolean reload) {
      Set<OseeCacheEnum> cacheIds = new HashSet<OseeCacheEnum>();

      for (String arg = ci.nextArgument(); Strings.isValid(arg); arg = ci.nextArgument()) {
         cacheIds.add(OseeCacheEnum.valueOf(arg));
      }

      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new UpdateCachesOperation(logger, cacheIds, reload), false, cacheMutex);
   }

   public Job _consolidate_artifact_versions(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      IOperation operation = new ConsolidateArtifactVersionTxOperation(Activator.getOseeDatabaseService(), logger);
      return Operations.executeAsJob(operation, false);
   }

   public Job _schedule(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new SchedulingCommand(logger, ci), false);
   }

   public Job _tx_currents(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      boolean archived = Boolean.parseBoolean(ci.nextArgument());
      return Operations.executeAsJob(new TxCurrentsAndModTypesCommand(logger, archived), false);
   }

   public Job _purge_relation_type(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);

      //to be purged
      final Collection<String> relationTypes = new ArrayList<String>();

      boolean force = false;
      for (String arg = ci.nextArgument(); Strings.isValid(arg); arg = ci.nextArgument()) {
         if (arg.equals("-force")) {
            force = true;
         } else {
            relationTypes.add(arg);
         }
      }

      IOperation operation =
         new PurgeRelationType(Activator.getOseeDatabaseService(), Activator.getOseeCachingService(), logger, force,
            relationTypes.toArray(new String[relationTypes.size()]));

      return Operations.executeAsJob(operation, false);
   }

   public Job _tx_prune(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new PurgeUnusedBackingDataAndTransactions(logger), false);
   }

   public Job _duplicate_attr(CommandInterpreter ci) throws OseeDataStoreException {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new DuplicateAttributesOperation(logger, Activator.getOseeDatabaseService()),
         false);
   }

   public Job _osee_shutdown(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new ServerShutdownOperation(logger, ci), true, shutdownMutex);
   }

   public Job _parse_dir(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      String listingFile = ci.nextArgument();
      IOperation operation =
         new ParseWindowsDirectoryListingOperation(Activator.getOseeDatabaseService(),
            Activator.getOseeCachingService(), logger, listingFile);

      return Operations.executeAsJob(operation, false);
   }
   
   public Job _consolidate_relations(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);

      IOperation operation = new ConsolidateRelationsTxOperation(Activator.getOseeDatabaseService(), logger);

      return Operations.executeAsJob(operation, false);
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
      sb.append("        tx_prune - Purge artifact, attribute, and relation versions that are not addressed or nonexistent and purge empty transactions\n");
      sb.append("        duplicate_attr - detect and fix duplicate attributes\n");
      sb.append("        osee_shutdown [-oseeOnly] - immediately release the listening port then waits for all existing operations to finish. \n");
      sb.append("        schedule <delay seconds> <iterations> <command> - runs the command after the specified delay and repeat given number of times\n");
      sb.append("        purge_relation_type -force excute the operation, relationType1 ...\n");
      sb.append("        parse_dir - converts the given file into a formatted CSV file\n");
      sb.append("        consolidate_relations - consolidate rows of relations\n");
      sb.append(String.format("        reload_cache %s? - reloads server caches\n",
         Arrays.deepToString(OseeCacheEnum.values()).replaceAll(",", " | ")));
      sb.append(String.format("        clear_cache %s? - decaches all objects from the specified caches\n",
         Arrays.deepToString(OseeCacheEnum.values()).replaceAll(",", " |")));
      return sb.toString();
   }
}
