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
package org.eclipse.osee.framework.branch.management.purge;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;

public class PurgeDeletedBranches extends AbstractOperation {
   private final IOseeCachingService cachingService;
   private final IOseeDatabaseService databaseService;

   public PurgeDeletedBranches(OperationLogger logger, IOseeCachingService cachingService, IOseeDatabaseService databaseService) {
      super("Purge Deleted Branches", Activator.PLUGIN_ID, logger);
      this.cachingService = cachingService;
      this.databaseService = databaseService;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      BranchFilter branchFilter = new BranchFilter(BranchArchivedState.ARCHIVED, BranchType.WORKING);
      branchFilter.setBranchStates(BranchState.DELETED);
      BranchCache branchCache = cachingService.getBranchCache();

      List<Branch> branches = branchCache.getBranches(branchFilter);
      logf("Found %s branches to purge.", branches.size());
      for (Branch branch : branches) {
         logf("Purging %s.", branch);
         try {
            doSubWork(new PurgeBranchOperation(getLogger(), branch, cachingService, databaseService), monitor, 0);
         } catch (OseeCoreException ex) {
            log(ex);
         }
      }
   }
}