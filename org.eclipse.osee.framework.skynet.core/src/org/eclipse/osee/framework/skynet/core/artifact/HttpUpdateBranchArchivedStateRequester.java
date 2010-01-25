/*
 * Created on Jan 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ChangeBranchArchiveStateRequest;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.artifact.requester.HttpPurgeBranchRequester;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;

/**
 * @author Megumi Telles
 */
public class HttpUpdateBranchArchivedStateRequester {

   public static void updateBranchArchivedState(IProgressMonitor monitor, int branchId, BranchArchivedState branchState) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.UPDATE_ARCHIVE_STATE.name());

      ChangeBranchArchiveStateRequest requestData = new ChangeBranchArchiveStateRequest(branchId, branchState);
      AcquireResult response =
            HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters,
                  CoreTranslatorId.CHANGE_BRANCH_ARCHIVE_STATE, requestData, null);

      if (response.wasSuccessful()) {
         BranchManager.refreshBranches();
         OseeEventManager.kickBranchEvent(HttpPurgeBranchRequester.class, BranchEventType.ArchiveStateUpdated, branchId);
      }

   }
}
