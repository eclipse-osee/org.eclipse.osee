/*******************************************************************************
 * Copyright (c) 2016 Boeing.
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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;

/**
 * @author Morgan E. Cook
 */
public class UpdateBranchNameHttpRequestOperation extends AbstractOperation {

   private long branchUuid;
   private String newBranchName;

   public UpdateBranchNameHttpRequestOperation(long branchUuid, String newBranchName) {
      super("Update branch name " + branchUuid, Activator.PLUGIN_ID);
      this.branchUuid = branchUuid;
      this.newBranchName = newBranchName;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      OseeClient client = ServiceUtil.getOseeClient();
      BranchEndpoint proxy = client.getBranchEndpoint();

      Response response = proxy.setBranchName(branchUuid, newBranchName);
      if (Status.OK.getStatusCode() == response.getStatus()) {
         Branch branch = BranchManager.getBranch(branchUuid);
         branch.setName(newBranchName);
         OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.Renamed, branchUuid));
      }
   }

}
