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

package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class DeleteBranchOperation extends AbstractOperation {

   private final BranchId branch;

   public DeleteBranchOperation(BranchId branch) {
      super("Delete Branch: " + branch, Activator.PLUGIN_ID);
      this.branch = branch;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Branch branch = BranchManager.getBranch(this.branch);
      BranchState originalState = branch.getBranchState();
      boolean originalArchivedState = BranchManager.isArchived(branch);

      ArtifactCache.deCache(this.branch);
      OseeClient client = ServiceUtil.getOseeClient();
      BranchEndpoint proxy = client.getBranchEndpoint();

      try {
         branch.setBranchState(BranchState.DELETE_IN_PROGRESS);
         branch.setArchived(true);
         OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Deleting, branch));
         BranchManager.persist(branch);

         branch.setBranchState(BranchState.DELETED);
         BranchManager.persist(branch);

         proxy.logBranchActivity(
            String.format("Branch Operation Branch State Changed {branchUUID: %s prevState: %s newState: %s ",
               branch.getUuid(), originalState, BranchState.DELETED));
      } catch (Exception ex) {
         try {
            branch.setBranchState(originalState);
            branch.setArchived(originalArchivedState);
            OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.StateUpdated, branch));
            BranchManager.persist(branch);
         } catch (Exception ex2) {
            log(ex2);
         }
         throw ex;
      }
   }
}