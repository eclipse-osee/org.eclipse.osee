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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.core.internal.branch.BranchUtil;
import org.eclipse.osee.orcs.core.internal.branch.provider.BranchProvider;
import org.eclipse.osee.orcs.core.internal.branch.provider.MultiBranchProvider;

/**
 * @author Roberto E. Escobar
 */
public final class BranchPurgeCommand implements ConsoleCommand {

   private OrcsApi orcsApi;
   private static final String ERROR_STRING =
      "Branch %s[%s] is a %s branch and that option was not specified!  It will not be purged!\n";

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "branch_purge";
   }

   @Override
   public String getDescription() {
      return "Permenatly remove all branches matching the passed in criteria";
   }

   @Override
   public String getUsage() {
      // includeChildren excludes baseline branches
      StringBuilder sb = new StringBuilder();
      sb.append("Usage: branch_purge [-R] [-D] [-A] [-B] [-P] branchGuids=<BRANCH_GUID,..>\n");
      sb.append("Synopsis:\n");
      sb.append("\tCAUTION: This command will permanently remove branches from the datastore!\n");
      sb.append("\tThis command has no effect unless the [P] option is specified, otherwise it\n");
      sb.append("\tprints what would be purged had the [P] option been specified.  Each option turns\n");
      sb.append("\tfunctionality on.  Thus, the default behavior with no options is to list the \n");
      sb.append("\tarchived, deleted, non-baseline branches and not recurse branch children.\n");
      sb.append("Options:\n");
      sb.append("\tR: Recurse branch children\n");
      sb.append("\tA: Allow Un-Archived branches\n");
      sb.append("\tD: Allow Un-Deleted branches\n");
      sb.append("\tB: Allow Baseline branches\n");
      sb.append("\tP: Purge the branches\n");

      return sb.toString();
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      List<String> branchGuids = Arrays.asList(params.getArray("branchGuids"));

      if (branchGuids.isEmpty()) {
         console.writeln("No branch guids where specified");
      }

      Collection<String> options = params.getOptions();
      boolean recurse = options.contains("R");
      boolean unArchived = options.contains("A");
      boolean unDeleted = options.contains("D");
      boolean baseline = options.contains("B");
      boolean runPurge = options.contains("P");

      OrcsBranch orcsBranch = getOrcsApi().getBranchOps(null);
      return new PurgeBranchCallable(console, orcsBranch, getOrcsApi().getBranchCache(), branchGuids, recurse,
         unArchived, unDeleted, baseline, runPurge);
   }

   private static class PurgeBranchCallable extends CancellableCallable<List<ReadableBranch>> {

      private final Console console;
      private final OrcsBranch orcsBranch;
      private final BranchCache branchCache;
      private final List<String> branchGuids;
      private final boolean recurse;
      private final boolean includeUnarchived;
      private final boolean includeUndeleted;
      private final boolean includeBaseline;
      private final boolean runPurge;

      public PurgeBranchCallable(Console console, OrcsBranch orcsBranch, BranchCache branchCache, List<String> branchGuids, boolean recurse, boolean unArchived, boolean unDeleted, boolean baseline, boolean runPurge) {
         this.console = console;
         this.orcsBranch = orcsBranch;
         this.branchCache = branchCache;
         this.branchGuids = branchGuids;
         this.recurse = recurse;
         this.includeUnarchived = unArchived;
         this.includeUndeleted = unDeleted;
         this.includeBaseline = baseline;
         this.runPurge = runPurge;
      }

      private boolean filterBranch(Branch branch) {
         if (!includeBaseline && branch.getBranchType() == BranchType.BASELINE) {
            console.writeln(ERROR_STRING, branch, branch.getGuid(), branch.getBranchType());
            return true;
         } else if (!includeUnarchived && branch.getArchiveState() == BranchArchivedState.UNARCHIVED) {
            console.writeln(ERROR_STRING, branch, branch.getGuid(), branch.getArchiveState());
            return true;
         } else if (!includeUndeleted && !branch.isDeleted()) {
            console.writeln(ERROR_STRING, branch, branch.getGuid(), branch.getBranchState());
            return true;
         }
         return false;
      }

      private Collection<Branch> getBranchesToPurge() throws OseeCoreException {
         Set<Branch> specifiedBranches = new HashSet<Branch>();
         for (String guid : branchGuids) {
            if (!GUID.isValid(guid)) {
               console.write("GUID listed %s is not a valid GUID", guid);
            } else {
               Branch cached = branchCache.getByGuid(guid);
               if (cached != null) {
                  specifiedBranches.add(cached);
               }
            }
         }

         Collection<Branch> branchesToPurge = recurse ? getChildBranchesToPurge(specifiedBranches) : specifiedBranches;

         Iterator<Branch> iter = branchesToPurge.iterator();
         while (iter.hasNext()) {
            if (filterBranch(iter.next())) {
               iter.remove();
            }
         }
         return branchesToPurge;
      }

      private Collection<Branch> getChildBranchesToPurge(Set<Branch> branches) throws OseeCoreException {
         BranchFilter branchFilter;
         if (includeUnarchived) {
            branchFilter = new BranchFilter();
         } else {
            branchFilter = new BranchFilter(BranchArchivedState.ARCHIVED);
         }

         if (includeBaseline) {
            branchFilter.setNegatedBranchTypes(BranchType.SYSTEM_ROOT);
         } else {
            branchFilter.setNegatedBranchTypes(BranchType.SYSTEM_ROOT, BranchType.BASELINE);
         }

         if (!includeUndeleted) {
            branchFilter.setBranchStates(BranchState.DELETED);
         }

         BranchProvider provider = new MultiBranchProvider(recurse, branches, branchFilter);
         return provider.getBranches();
      }

      @Override
      public List<ReadableBranch> call() throws Exception {
         Collection<Branch> branchesToPurge = getBranchesToPurge();
         branchesToPurge.addAll(getMergeBranches(branchesToPurge));

         Conditions.checkNotNull(branchesToPurge, "branchesToPurge");
         if (branchesToPurge.isEmpty()) {
            console.writeln("no branches matched specified criteria");
         } else {
            List<Branch> orderedBranches = BranchUtil.orderByParent(branchesToPurge);

            for (Branch toPurge : orderedBranches) {
               console.writeln("Branch [%s] guid [%s] will be purged!", toPurge.getName(), toPurge.getGuid());
            }

            List<ReadableBranch> purged = new LinkedList<ReadableBranch>();
            if (runPurge) {
               int size = orderedBranches.size();
               int count = 0;
               for (Branch aBranch : orderedBranches) {
                  console.writeln("Purging Branch [%s of %s]: [%s]", ++count, size, aBranch);
                  Callable<List<ReadableBranch>> callable = orcsBranch.purgeBranch(aBranch, false);
                  purged.addAll(callable.call());
               }
            }
            return purged;
         }
         return null;
      }

      private List<Branch> getMergeBranches(Collection<Branch> branches) throws OseeCoreException {
         List<Branch> mergeBranches = new ArrayList<Branch>();
         for (Branch branch : branches) {
            branch.getChildBranches(mergeBranches, false, new BranchFilter(BranchType.MERGE));
         }
         return mergeBranches;
      }
   }
}