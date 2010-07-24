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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ChangeBranchTypeRequest;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.requester.HttpPurgeBranchRequester;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Megumi Telles
 */
public class HttpUpdateBranchTypeRequester {

   public static void updateBranchType(IProgressMonitor monitor, int branchId, String branchGuid, BranchType type) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.UPDATE_BRANCH_TYPE.name());

      ChangeBranchTypeRequest requestData = new ChangeBranchTypeRequest(branchId, type);
      AcquireResult response =
         HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslatorId.CHANGE_BRANCH_TYPE,
            requestData, null);

      if (response.wasSuccessful()) {
         BranchManager.refreshBranches();
         try {
            OseeEventManager.kickBranchEvent(HttpPurgeBranchRequester.class, new BranchEvent(
               BranchEventType.TypeUpdated, branchGuid), branchId);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }
}
