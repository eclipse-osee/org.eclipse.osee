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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.PurgeBranchRequest;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 * @author Ryan D. Brooks
 */
public final class PurgeBranchHttpRequestOperation extends AbstractOperation {
   private final Branch branch;

   public PurgeBranchHttpRequestOperation(Branch branch) {
      super("Purge " + branch, Activator.PLUGIN_ID);
      this.branch = branch;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      PurgeBranchRequest requestData = new PurgeBranchRequest(branch.getId());
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.PURGE_BRANCH.name());

      AcquireResult response =
         HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslatorId.PURGE_BRANCH_REQUEST,
            requestData, null);

      if (response.wasSuccessful()) {
         branch.setStorageState(StorageState.PURGED);
         BranchManager.decache(branch);
         OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.Purged, branch.getGuid()),
            branch.getId());
      }
   }
}