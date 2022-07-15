/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.artifact;

import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
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
      BranchState originalState = BranchManager.getState(branch);
      boolean originalArchivedState = BranchManager.isArchived(branch);

      ArtifactCache.deCache(branch);
      BranchEndpoint proxy = ServiceUtil.getOseeClient().getBranchEndpoint();

      try {
         BranchManager.setState(branch, BranchState.DELETE_IN_PROGRESS);
         BranchManager.archiveUnArchiveBranch(branch, BranchArchivedState.ARCHIVED);
         OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Deleting, branch));

         BranchManager.setState(branch, BranchState.DELETED);
         OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Deleted, branch));

         Response res = proxy.logBranchActivity(
            String.format("Branch Operation Branch State Changed {branchId: %s prevState: %s newState: %s ",
               branch.getId(), originalState, BranchState.DELETED));
         res.close();
      } catch (Exception ex) {
         try {
            BranchManager.setState(branch, originalState);
            BranchManager.archiveUnArchiveBranch(branch, BranchArchivedState.fromBoolean(originalArchivedState));
         } catch (Exception ex2) {
            log(ex2);
         }
         throw ex;
      }
   }
}