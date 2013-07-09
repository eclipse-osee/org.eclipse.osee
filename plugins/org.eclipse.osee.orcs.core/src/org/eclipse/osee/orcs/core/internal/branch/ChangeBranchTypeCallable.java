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

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;

public class ChangeBranchTypeCallable extends AbstractBranchCallable<ReadableBranch> {

   private final BranchCache cache;
   private final IOseeBranch branchToken;
   private final BranchType branchType;

   public ChangeBranchTypeCallable(Log logger, OrcsSession session, BranchDataStore branchStore, BranchCache cache, IOseeBranch branch, BranchType branchType) {
      super(logger, session, branchStore);
      this.cache = cache;
      this.branchToken = branch;
      this.branchType = branchType;
   }

   @Override
   protected ReadableBranch innerCall() throws Exception {
      Conditions.checkNotNull(branchToken, "branch");
      Conditions.checkNotNull(branchType, "branchType");

      Branch branch = cache.get(branchToken);
      Conditions.checkNotNull(branch, "branch");

      BranchType original = branch.getBranchType();

      try {
         branch.setBranchType(branchType);
         cache.storeItems(branch);
      } catch (Exception ex) {
         branch.setBranchType(original);
         throw ex;
      } finally {
         // TODO Event ?
      }
      return branch;
   }
}
