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
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
 * @author Megumi Telles
 * @author Ryan D. Brooks
 */
public class UpdateBranchStateHttpRequestOperation extends AbstractOperation {
   private final long branchUuid;
   private final BranchState branchState;

   public UpdateBranchStateHttpRequestOperation(long branchUuid, BranchState branchState) {
      super("Update branch state " + branchUuid, Activator.PLUGIN_ID);
      this.branchUuid = branchUuid;
      this.branchState = branchState;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      OseeClient client = ServiceUtil.getOseeClient();
      BranchEndpoint proxy = client.getBranchEndpoint();
      try {
         Response response = proxy.setBranchState(branchUuid, branchState);
         if (Status.OK.getStatusCode() == response.getStatus()) {
            Branch branch = BranchManager.getBranch(branchUuid);
            branch.setBranchState(branchState);
            OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.StateUpdated, branch));
         }
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}
