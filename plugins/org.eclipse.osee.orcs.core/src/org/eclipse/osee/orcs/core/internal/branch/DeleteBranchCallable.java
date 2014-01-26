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
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchReadable;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;

public class DeleteBranchCallable extends AbstractBranchCallable<BranchReadable> {

   private final BranchCache cache;
   private final IOseeBranch toDelete;

   public DeleteBranchCallable(Log logger, OrcsSession session, BranchDataStore branchStore, BranchCache cache, IOseeBranch toDelete) {
      super(logger, session, branchStore);
      this.cache = cache;
      this.toDelete = toDelete;
   }

   @Override
   protected BranchReadable innerCall() throws Exception {
      Conditions.checkNotNull(cache, "branchCache");
      Conditions.checkNotNull(toDelete, "toDelete");
      Branch branch = cache.get(toDelete);

      BranchState originalState = branch.getBranchState();
      BranchArchivedState originalArchivedState = branch.getArchiveState();
      try {
         branch.setBranchState(BranchState.DELETED);
         branch.setArchived(true);
         cache.storeItems(branch);
      } catch (Exception ex) {
         branch.setBranchState(originalState);
         branch.setArchived(originalArchivedState.isArchived());
         throw ex;
      } finally {
         //         OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Deleting, branch.getGuid()),
         //            branch.getId());
         // TODO Event ?
      }
      return branch;
   }
}
