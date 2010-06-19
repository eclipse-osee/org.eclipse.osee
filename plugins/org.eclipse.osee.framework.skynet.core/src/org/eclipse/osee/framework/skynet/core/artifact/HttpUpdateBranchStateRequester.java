/*
 * Created on Jan 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ChangeBranchStateRequest;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.requester.HttpPurgeBranchRequester;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Megumi Telles
 */
public class HttpUpdateBranchStateRequester {

   public static void updateBranchState(IProgressMonitor monitor, int branchId, String branchGuid, BranchState branchState) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.UPDATE_BRANCH_STATE.name());

      ChangeBranchStateRequest requestData = new ChangeBranchStateRequest(branchId, branchState);
      AcquireResult response =
            HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslatorId.CHANGE_BRANCH_STATE,
                  requestData, null);

      if (response.wasSuccessful()) {
         BranchManager.refreshBranches();
         try {
            OseeEventManager.kickBranchEvent(HttpPurgeBranchRequester.class, new BranchEvent(
                  BranchEventType.StateUpdated, branchGuid), branchId);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
         }
      }
   }
}
