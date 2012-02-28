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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.Conditions;
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
   private IOseeCachingService cachingService;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public void setCachingService(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   public IOseeCachingService getCachingService() {
      return cachingService;
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
      return "[branchGuids=<BRANCH_GUID,..>] [includeChildren=<TRUE|FALSE>] [deletedAndArchivedOnly=<TRUE|FALSE>]";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      List<String> branchGuids = Arrays.asList(params.getArray("branchGuids"));
      boolean includeChildren = params.getBoolean("includeChildren");
      boolean deletedAndArchivedOnly = params.getBoolean("deletedAndArchivedOnly");

      OrcsBranch orcsBranch = getOrcsApi().getBranchOps(null);
      return new PurgeBranchCallable(console, orcsBranch, getCachingService().getBranchCache(), branchGuids,
         includeChildren, deletedAndArchivedOnly);
   }

   private static class PurgeBranchCallable extends CancellableCallable<List<ReadableBranch>> {

      private final Console console;
      private final OrcsBranch orcsBranch;
      private final BranchCache branchCache;
      private final List<String> branchGuids;
      private final boolean includeChildren;
      private final boolean deletedAndArchivedOnly;

      public PurgeBranchCallable(Console console, OrcsBranch orcsBranch, BranchCache branchCache, List<String> branchGuids, boolean includeChildren, boolean deletedAndArchivedOnly) {
         this.console = console;
         this.orcsBranch = orcsBranch;
         this.branchCache = branchCache;
         this.branchGuids = branchGuids;
         this.includeChildren = includeChildren;
         this.deletedAndArchivedOnly = deletedAndArchivedOnly;
      }

      private Collection<Branch> getBranchesToPurge() throws OseeCoreException {
         Set<Branch> branches = new HashSet<Branch>();
         for (String guid : branchGuids) {
            branches.add(branchCache.getByGuid(guid));
         }
         BranchFilter branchFilter;
         if (deletedAndArchivedOnly) {
            branchFilter = new BranchFilter(BranchArchivedState.ARCHIVED);
            branchFilter.setBranchStates(BranchState.DELETED);
         } else {
            branchFilter = new BranchFilter();
         }
         branchFilter.setNegatedBranchTypes(BranchType.BASELINE);

         BranchProvider provider = new MultiBranchProvider(includeChildren, branches, branchFilter);
         return provider.getBranches();
      }

      @Override
      public List<ReadableBranch> call() throws Exception {
         Collection<Branch> branchesToPurge = getBranchesToPurge();
         Conditions.checkNotNull(branchesToPurge, "branchesToPurge");

         List<ReadableBranch> purged = new LinkedList<ReadableBranch>();
         List<Branch> orderedBranches = BranchUtil.orderByParent(branchesToPurge);
         int size = orderedBranches.size();
         int count = 0;
         for (Branch aBranch : orderedBranches) {
            console.writeln("Purging Branch [%s of %s]: [%s]", ++count, size, aBranch);
            Callable<List<ReadableBranch>> callable = orcsBranch.purgeBranch(aBranch, false);
            purged.addAll(callable.call());
         }
         return purged;
      }
   }

}
