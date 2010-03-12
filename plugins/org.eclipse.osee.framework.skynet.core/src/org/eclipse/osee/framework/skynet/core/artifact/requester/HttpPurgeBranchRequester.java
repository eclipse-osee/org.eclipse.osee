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
package org.eclipse.osee.framework.skynet.core.artifact.requester;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.PurgeBranchRequest;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 */
public class HttpPurgeBranchRequester {

   public static void purge(final Branch branch) throws OseeCoreException {
      PurgeBranchRequest requestData = new PurgeBranchRequest(branch.getId());
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.PURGE_BRANCH.name());

      AcquireResult response =
            HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslatorId.PURGE_BRANCH_REQUEST,
                  requestData, null);

      if (response.wasSuccessful()) {
         BranchManager.decache(branch);
         OseeEventManager.kickBranchEvent(HttpPurgeBranchRequester.class, BranchEventType.Purged, branch.getId());
      }
   }
}
