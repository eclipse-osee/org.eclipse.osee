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
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.data.ArchiveOperation;

public class ArchiveUnarchiveBranchCallable extends AbstractBranchCallable<ReadableBranch> {

   private final BranchCache cache;
   private final IOseeBranch branchToken;
   private final ArchiveOperation archiveOp;

   public ArchiveUnarchiveBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, BranchCache cache, IOseeBranch branch, ArchiveOperation archiveOp) {
      super(logger, session, branchStore);
      this.cache = cache;
      this.branchToken = branch;
      this.archiveOp = archiveOp;
   }

   @Override
   protected ReadableBranch innerCall() throws Exception {
      Conditions.checkNotNull(branchToken, "branch");
      Conditions.checkNotNull(archiveOp, "archiveOp");

      Branch branch = cache.get(branchToken);
      Conditions.checkNotNull(branch, "branch");

      BranchArchivedState original = branch.getArchiveState();

      try {
         branch.setArchived(ArchiveOperation.ARCHIVE == archiveOp);
         cache.storeItems(branch);
      } catch (Exception ex) {
         branch.setArchived(original.isArchived());
         throw ex;
      } finally {
         // Event ?
      }
      return branch;
   }
}
