/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.core.branch;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Audrey Denk
 */
public class BranchOperationsUtil {

   public static XResultData validateBranchCommit(IAtsTeamWorkflow teamWf, BranchToken commitToBranch, boolean overrideStateValidation, XResultData rd, AtsApi atsApi) {
      if (rd == null) {
         rd = new XResultData();
      }

      rd.logf("Committing %s to destination %s\n\n", teamWf.toStringWithId(), commitToBranch.toStringWithId());
      BranchToken workingBranch = atsApi.getBranchService().getBranch(teamWf);
      try {
         if (workingBranch.isInvalid()) {
            rd.errorf("Commit Branch Failed: Can not locate branch for workflow [%s]", teamWf.getAtsId());
            return rd;
         }

         /**
          * Confirm that all blocking reviews are completed. Loop through this state's blocking reviews to confirm
          * complete
          */
         for (IAtsAbstractReview review : atsApi.getReviewService().getReviews(teamWf)) {
            ReviewBlockType blockType =
               ReviewBlockType.valueOf(atsApi.getAttributeResolver().getSoleAttributeValue(review.getArtifactId(),
                  AtsAttributeTypes.ReviewBlocks, "None"));
            if (blockType.isCommit() && !review.isCompletedOrCancelled()) {
               rd.error(
                  "All blocking reviews must be completed before committing the working branch.  Please complete all blocking reviews in order to continue.");
               return rd;
            }
         }
      } catch (Exception ex) {
         rd.errorf("Exception committing branch %s", Lib.exceptionToString(ex));
      }
      return rd;

   }
}
