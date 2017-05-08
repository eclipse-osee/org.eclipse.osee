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
package org.eclipse.osee.orcs.core.internal.console;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.ExportOptions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public final class BranchExportCommand implements ConsoleCommand {

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "branch_export";
   }

   @Override
   public String getDescription() {
      return "Export a specific set of branches into an exchange zip file.";
   }

   @Override
   public String getUsage() {
      return "uri=<EXCHANGE_FILE_LOCATION> [compress=<TRUE|FALSE>] [minTx=<TX_ID>] [maxTx=<TX_ID>] [includeArchivedBranches=<TRUE|FALSE>] " + //
         "[excludeBranchIds=<BRANCH_IDS,...>] [includeBranchIds=<BRANCH_IDS>]";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      String exportFileName = params.get("uri");
      boolean includeArchivedBranches = params.getBoolean("includeArchivedBranches");

      PropertyStore options = new PropertyStore();
      if (params.exists("minTx")) {
         options.put(ExportOptions.MIN_TXS.name(), params.getLong("minTx"));
      }
      if (params.exists("maxTx")) {
         options.put(ExportOptions.MAX_TXS.name(), params.getLong("maxTx"));
      }

      List<String> excludeBranchIds = Arrays.asList(params.getArray("excludeBranchIds"));
      List<String> includeBranchIds = Arrays.asList(params.getArray("includeBranchIds"));

      OrcsBranch orcsBranch = getOrcsApi().getBranchOps();
      return new ExportBranchCallable(console, orcsBranch, exportFileName, options, includeArchivedBranches,
         includeBranchIds, excludeBranchIds, orcsApi);
   }

   private static class ExportBranchCallable extends CancellableCallable<URI> {

      private final Console console;
      private final OrcsBranch orcsBranch;
      private final PropertyStore options;
      private final String exportFileName;
      private final boolean includeArchivedBranches;

      private final List<String> includeBranchIds;
      private final List<String> excludeBranchIds;
      private final OrcsApi orcsApi;

      public ExportBranchCallable(Console console, OrcsBranch orcsBranch, String exportFileName, PropertyStore options, boolean includeArchivedBranches, List<String> includeBranchIds, List<String> excludeBranchIds, OrcsApi orcsApi) {
         this.console = console;
         this.orcsBranch = orcsBranch;
         this.options = options;
         this.exportFileName = exportFileName;
         this.includeArchivedBranches = includeArchivedBranches;
         this.includeBranchIds = includeBranchIds;
         this.excludeBranchIds = excludeBranchIds;
         this.orcsApi = orcsApi;
      }

      private List<BranchId> getBranchesToExport() throws OseeCoreException {
         List<BranchId> branches = new LinkedList<>();
         BranchQuery branchQuery = orcsApi.getQueryFactory().branchQuery();
         if (includeBranchIds.isEmpty()) {
            ResultSet<BranchReadable> branchReadables = null;
            if (includeArchivedBranches) {
               branchQuery.includeArchived();
            } else {
               branchQuery.excludeArchived();
            }
            branchReadables = branchQuery.andIsOfType(BranchType.WORKING, BranchType.BASELINE, BranchType.MERGE,
               BranchType.PORT, BranchType.SYSTEM_ROOT).getResults();

            for (BranchReadable branch : branchReadables) {
               branches.add(branch);
            }
         } else {
            for (String branchUuidString : includeBranchIds) {
               branches.add(branchQuery.andId(BranchId.valueOf(branchUuidString)).getResults().getExactlyOne());
            }
         }

         branchQuery = orcsApi.getQueryFactory().branchQuery();
         if (!excludeBranchIds.isEmpty()) {
            for (String branchUuidString : excludeBranchIds) {
               BranchReadable toExclude =
                  branchQuery.andId(BranchId.valueOf(branchUuidString)).getResults().getExactlyOne();
               branches.remove(toExclude);
            }
         }
         return branches;
      }

      @Override
      public URI call() throws Exception {
         Conditions.checkNotNullOrEmpty(exportFileName, "exportFileName");
         List<BranchId> branches = getBranchesToExport();
         console.writeln("Exporting: [%s] branches", branches.size());
         Callable<URI> callable = orcsBranch.exportBranch(branches, options, exportFileName);
         return callable.call();
      }
   }
}