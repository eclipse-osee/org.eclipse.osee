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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.ExportOptions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;

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

      OrcsBranch orcsBranch = getOrcsApi().getBranchOps(null);
      return new ExportBranchCallable(console, orcsBranch, getOrcsApi().getBranchCache(), exportFileName, options,
         includeArchivedBranches, includeBranchIds, excludeBranchIds);
   }

   private static class ExportBranchCallable extends CancellableCallable<URI> {

      private final Console console;
      private final OrcsBranch orcsBranch;
      private final BranchCache branchCache;
      private final PropertyStore options;
      private final String exportFileName;
      private final boolean includeArchivedBranches;

      private final List<String> includeBranchIds;
      private final List<String> excludeBranchIds;

      public ExportBranchCallable(Console console, OrcsBranch orcsBranch, BranchCache branchCache, String exportFileName, PropertyStore options, boolean includeArchivedBranches, List<String> includeBranchIds, List<String> excludeBranchIds) {
         this.console = console;
         this.orcsBranch = orcsBranch;
         this.branchCache = branchCache;
         this.options = options;
         this.exportFileName = exportFileName;
         this.includeArchivedBranches = includeArchivedBranches;
         this.includeBranchIds = includeBranchIds;
         this.excludeBranchIds = excludeBranchIds;
      }

      private List<IOseeBranch> getBranchesToExport() throws OseeCoreException {
         List<IOseeBranch> branches = new LinkedList<IOseeBranch>();
         if (includeBranchIds.isEmpty()) {
            BranchFilter filter;
            if (includeArchivedBranches) {
               filter = new BranchFilter(BranchArchivedState.ALL, BranchType.values());
            } else {
               filter = new BranchFilter(BranchArchivedState.UNARCHIVED, BranchType.values());
            }
            for (Branch branch : branchCache.getBranches(filter)) {
               branches.add(branch);
            }
         } else {
            for (String branchIdString : includeBranchIds) {
               int branchId = Integer.parseInt(branchIdString);
               branches.add(branchCache.getById(branchId));
            }
         }

         if (!excludeBranchIds.isEmpty()) {
            for (String branchIdString : excludeBranchIds) {
               int branchId = Integer.parseInt(branchIdString);
               Branch toExclude = branchCache.getById(branchId);
               branches.remove(toExclude);
            }
         }
         return branches;
      }

      @Override
      public URI call() throws Exception {
         Conditions.checkNotNullOrEmpty(exportFileName, "exportFileName");
         List<IOseeBranch> branches = getBranchesToExport();
         console.writeln("Exporting: [%s] branches", branches.size());
         Callable<URI> callable = orcsBranch.exportBranch(branches, options, exportFileName);
         return callable.call();
      }
   }
}