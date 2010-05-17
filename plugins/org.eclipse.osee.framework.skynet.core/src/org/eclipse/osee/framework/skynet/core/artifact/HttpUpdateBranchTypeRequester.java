/*
 * Created on Jan 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ChangeBranchTypeRequest;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.artifact.requester.HttpPurgeBranchRequester;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;

/**
 * @author Megumi Telles
 */
public class HttpUpdateBranchTypeRequester {

   public static void updateBranchType(IProgressMonitor monitor, int branchId, BranchType type) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.UPDATE_BRANCH_TYPE.name());

      ChangeBranchTypeRequest requestData = new ChangeBranchTypeRequest(branchId, type);
      AcquireResult response =
            HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslatorId.CHANGE_BRANCH_TYPE,
                  requestData, null);

      if (response.wasSuccessful()) {
         BranchManager.refreshBranches();
         OseeEventManager.kickBranchEvent(HttpPurgeBranchRequester.class, BranchEventType.TypeUpdated, branchId);
      }
   }
}
