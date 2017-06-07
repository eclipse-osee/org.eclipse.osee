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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.core.internal.branch.BranchUtil;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public final class BranchPurgeCommand implements ConsoleCommand {

   private OrcsApi orcsApi;
   private static final String ERROR_STRING =
      "Branch [%s] is a %s branch and that option was not specified!  It will not be purged!\n";

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
      sb.append("Usage: branch_purge [-R] [-D] [-A] [-B] [-P] branchUuids=<BRANCH_UUID,..>\n");
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
      List<Long> branchUuids = new ArrayList<>();
      for (String uuid : params.getArray("branchUuids")) {
         if (Strings.isNumeric(uuid)) {
            branchUuids.add(Long.parseLong(uuid));
         } else {
            console.writeln("UUID listed %s is not a valid UUID", uuid);
         }
      }

      if (branchUuids.isEmpty()) {
         console.writeln("No branch uuids where specified");
      }

      Collection<String> options = params.getOptions();
      boolean recurse = options.contains("R");
      boolean unArchived = options.contains("A");
      boolean unDeleted = options.contains("D");
      boolean baseline = options.contains("B");
      boolean runPurge = options.contains("P");

      OrcsBranch orcsBranch = getOrcsApi().getBranchOps();
      return new PurgeBranchCallable(console, orcsBranch, getOrcsApi().getQueryFactory(), branchUuids, recurse,
         unArchived, unDeleted, baseline, runPurge);
   }

   private static class PurgeBranchCallable extends CancellableCallable<List<BranchId>> {

      private final Console console;
      private final OrcsBranch orcsBranch;
      private final List<Long> branchUuids;
      private final boolean recurse;
      private final boolean includeUnarchived;
      private final boolean includeUndeleted;
      private final boolean includeBaseline;
      private final boolean runPurge;
      private final QueryFactory queryFactory;

      public PurgeBranchCallable(Console console, OrcsBranch orcsBranch, QueryFactory queryFactory, List<Long> branchUuids, boolean recurse, boolean unArchived, boolean unDeleted, boolean baseline, boolean runPurge) {
         this.console = console;
         this.orcsBranch = orcsBranch;
         this.queryFactory = queryFactory;
         this.branchUuids = branchUuids;
         this.recurse = recurse;
         this.includeUnarchived = unArchived;
         this.includeUndeleted = unDeleted;
         this.includeBaseline = baseline;
         this.runPurge = runPurge;
      }

      private boolean filterBranch(BranchReadable branch) {
         if (!includeBaseline && branch.getBranchType().isBaselineBranch()) {
            console.writeln(ERROR_STRING, branch, branch.getBranchType());
            return true;
         } else if (!includeUnarchived && branch.getArchiveState() == BranchArchivedState.UNARCHIVED) {
            console.writeln(ERROR_STRING, branch, branch.getArchiveState());
            return true;
         } else if (!includeUndeleted && branch.getBranchState() != BranchState.DELETED) {
            console.writeln(ERROR_STRING, branch, branch.getBranchState());
            return true;
         }
         return false;
      }

      private Collection<BranchReadable> getBranchesToPurge() throws OseeCoreException {
         Set<BranchReadable> specifiedBranches = new HashSet<>();
         for (Long uuid : branchUuids) {
            if (uuid <= 0) {
               console.writeln("UUID listed %s is not a valid UUID", uuid);
            } else {
               BranchReadable cached =
                  queryFactory.branchQuery().andId(BranchId.valueOf(uuid)).getResults().getExactlyOne();
               if (cached != null) {
                  specifiedBranches.add(cached);
               }
            }
         }

         Collection<BranchReadable> branchesToPurge =
            recurse ? getChildBranchesToPurge(specifiedBranches) : specifiedBranches;

         Iterator<BranchReadable> iter = branchesToPurge.iterator();
         while (iter.hasNext()) {
            if (filterBranch(iter.next())) {
               iter.remove();
            }
         }
         return branchesToPurge;
      }

      private Collection<BranchReadable> getChildBranchesToPurge(Iterable<BranchReadable> branches) throws OseeCoreException {

         BranchQuery branchQuery = queryFactory.branchQuery();
         branchQuery.includeArchived();
         branchQuery.includeDeleted();

         if (includeBaseline) {
            branchQuery.andIsOfType(BranchType.WORKING, BranchType.MERGE, BranchType.PORT, BranchType.BASELINE);
         } else {
            branchQuery.andIsOfType(BranchType.WORKING, BranchType.MERGE, BranchType.PORT);
         }

         if (includeUndeleted) {
            branchQuery.andStateIs(BranchState.CREATED, BranchState.MODIFIED, BranchState.COMMITTED,
               BranchState.REBASELINED, BranchState.DELETED, BranchState.REBASELINE_IN_PROGRESS,
               BranchState.COMMIT_IN_PROGRESS, BranchState.CREATION_IN_PROGRESS, BranchState.DELETE_IN_PROGRESS,
               BranchState.PURGE_IN_PROGRESS, BranchState.PURGED);
         } else {
            branchQuery.andStateIs(BranchState.DELETED);
         }

         Set<BranchReadable> results = new HashSet<>();
         for (BranchReadable parent : branches) {
            for (BranchReadable branch : branchQuery.andIsChildOf(parent).getResults()) {
               if (includeUnarchived || branch.getArchiveState() == BranchArchivedState.ARCHIVED) {
                  results.add(branch);
               }
            }
         }

         if (recurse) {
            results.addAll(getChildBranchesToPurge(new ArrayList<BranchReadable>(results)));
         }
         return results;
      }

      @Override
      public List<BranchId> call() throws Exception {
         Collection<BranchReadable> branchesToPurge = getBranchesToPurge();

         Conditions.checkNotNull(branchesToPurge, "branchesToPurge");
         if (branchesToPurge.isEmpty()) {
            console.writeln("no branches matched specified criteria");
         } else {
            List<? extends IOseeBranch> orderedBranches = BranchUtil.orderByParentReadable(branchesToPurge);

            for (IOseeBranch toPurge : orderedBranches) {
               console.writeln("Branch [%s] will be purged!", toPurge);
            }

            List<BranchId> purged = new LinkedList<>();
            if (runPurge) {
               int size = orderedBranches.size();
               int count = 0;
               for (BranchId aBranch : orderedBranches) {
                  console.writeln("Purging Branch [%s of %s]: [%s]", ++count, size, aBranch);
                  Callable<List<BranchId>> callable = orcsBranch.purgeBranch(aBranch, false);
                  purged.addAll(callable.call());
               }
            }
            return purged;
         }
         return null;
      }

   }
}