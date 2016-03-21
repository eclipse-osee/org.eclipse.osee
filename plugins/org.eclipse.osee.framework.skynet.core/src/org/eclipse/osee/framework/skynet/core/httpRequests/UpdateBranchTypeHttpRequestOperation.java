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
import org.eclipse.osee.framework.core.enums.BranchType;
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
public final class UpdateBranchTypeHttpRequestOperation extends AbstractOperation {
   private final long branchUuid;
   private final BranchType type;

   public UpdateBranchTypeHttpRequestOperation(long branchUuid, BranchType type) {
      super("Update branch type" + branchUuid, Activator.PLUGIN_ID);
      this.branchUuid = branchUuid;
      this.type = type;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      OseeClient client = ServiceUtil.getOseeClient();
      BranchEndpoint proxy = client.getBranchEndpoint();
      try {
         Response response = proxy.setBranchType(branchUuid, type);
         if (Status.OK.getStatusCode() == response.getStatus()) {
            Branch branch = BranchManager.getBranch(branchUuid);
            branch.setBranchType(type);
            OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.TypeUpdated, branch));
         }
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}
