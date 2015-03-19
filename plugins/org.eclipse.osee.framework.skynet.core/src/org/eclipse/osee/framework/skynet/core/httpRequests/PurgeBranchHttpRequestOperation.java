/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.httpRequests;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 * @author Ryan D. Brooks
 */
public final class PurgeBranchHttpRequestOperation extends AbstractOperation {
   private final Branch branch;
   private final boolean recursive;

   public PurgeBranchHttpRequestOperation(IOseeBranch branch, boolean recursive) throws OseeCoreException {
      super("Purge " + branch, Activator.PLUGIN_ID);
      this.branch = BranchManager.getBranch(branch);
      this.recursive = recursive;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      long branchUuid = branch.getUuid();
      BranchState currentState = branch.getBranchState();
      BranchArchivedState archivedState = branch.getArchiveState();

      ArtifactCache.deCache(branch);

      branch.setBranchState(BranchState.PURGE_IN_PROGRESS);
      branch.setArchived(true);
      OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.Purging, branchUuid));

      OseeClient client = ServiceUtil.getOseeClient();
      BranchEndpoint proxy = client.getBranchEndpoint();
      try {
         Response response = proxy.purgeBranch(branchUuid, recursive);
         if (Status.OK.getStatusCode() == response.getStatus()) {
            branch.setStorageState(StorageState.PURGED);
            branch.setBranchState(BranchState.PURGED);
            branch.setArchived(true);
            BranchManager.decache(branch);
            OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.Purged, branchUuid));
         }
      } catch (Exception ex) {
         try {
            branch.setBranchState(currentState);
            branch.setArchived(archivedState.isArchived());
            OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.StateUpdated, branchUuid));
         } catch (Exception ex2) {
            log(ex2);
         }
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}
