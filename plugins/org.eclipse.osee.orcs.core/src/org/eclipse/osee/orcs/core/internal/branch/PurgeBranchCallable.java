/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.branch;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.branch.provider.BranchProvider;
import org.eclipse.osee.orcs.core.internal.branch.provider.MultiBranchProvider;
import org.eclipse.osee.orcs.core.internal.branch.provider.SingleBranchProvider;

/**
 * @author Roberto E. Escobar
 */
public class PurgeBranchCallable extends AbstractBranchCallable<List<ReadableBranch>> {

   private final BranchCache branchCache;
   private final IOseeBranch branchToken;
   private final boolean isRecursive;

   public PurgeBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, BranchCache branchCache, IOseeBranch branchToken, boolean isRecursive) {
      super(logger, session, branchStore);
      this.branchCache = branchCache;
      this.branchToken = branchToken;
      this.isRecursive = isRecursive;
   }

   private BranchProvider createProvider(Branch branch, boolean isRecursive) {
      BranchProvider provider;
      if (isRecursive) {
         provider = new MultiBranchProvider(true, Collections.singleton(branch), new BranchFilter());
      } else {
         provider = new SingleBranchProvider(branch);
      }
      return provider;
   }

   @Override
   protected List<ReadableBranch> innerCall() throws Exception {
      Conditions.checkNotNull(branchCache, "branchCache");
      Conditions.checkNotNull(branchToken, "branchToPurge");

      Branch branch = branchCache.get(branchToken);

      Conditions.checkNotNull(branch, "branchToPurge");

      BranchProvider provider = createProvider(branch, isRecursive);

      Collection<Branch> branches = provider.getBranches();
      Conditions.checkNotNull(branches, "branchesToPurge");

      List<ReadableBranch> purged = new LinkedList<ReadableBranch>();
      List<Branch> orderedBranches = BranchUtil.orderByParent(branches);
      for (Branch aBranch : orderedBranches) {
         checkForCancelled();
         Callable<Branch> callable = getBranchStore().purgeBranch(getSession(), aBranch);
         purged.add(callAndCheckForCancel(callable));
      }
      return purged;
   }
}
