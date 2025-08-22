/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Code_AI;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_NoBranch_Code_TeamWf;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_NoBranch_Req_TeamWf;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_NoBranch_SWDesign_TeamWf;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_NoBranch_Test_TeamWf;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Requirements_AI;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_SW_Design_AI;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Test_AI;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class Pdd23CreateNoBranchAction extends AbstractPopulateDemoDatabase {

   public Pdd23CreateNoBranchAction(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      String title = DemoArtifactToken.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW;
      NewActionData data = atsApi.getActionService() //
         .createActionData(getClass().getSimpleName(), title, "Problem with the Diagram View") //
         .andAiAndToken(SAW_Test_AI, SAW_NoBranch_Test_TeamWf) //
         .andAiAndToken(SAW_Code_AI, SAW_NoBranch_Code_TeamWf) //
         .andAiAndToken(SAW_Requirements_AI, SAW_NoBranch_Req_TeamWf) //
         .andAiAndToken(SAW_SW_Design_AI, SAW_NoBranch_SWDesign_TeamWf) //
         .andChangeType(ChangeTypes.Problem).andPriority("3");
      NewActionData newData = atsApi.getActionService().createAction(data);
      if (dataErrored(newData)) {
         return;
      }

      IAtsTeamWorkflow reqTeamWf = null;
      for (IAtsTeamWorkflow teamWf : newData.getActResult().getAtsTeamWfs()) {

         boolean isSwDesign = teamWf.getTeamDefinition().getName().contains("SW Design");

         if (isSwDesign) {

            // transition to analyze
            teamWf = transitionAndReload(teamWf, TeamState.Analyze);

            if (atsApi.getReviewService().getReviews(teamWf).size() != 1) {
               throw new OseeCoreException(
                  "Error, 1 review should have been created instead of " + atsApi.getReviewService().getReviews(
                     teamWf).size());
            }

            // set reviews to non-blocking
            IAtsChangeSet changes = atsApi.createChangeSet("Transition Workflows");
            for (IAtsAbstractReview review : atsApi.getReviewService().getReviews(teamWf)) {
               changes.setSoleAttributeValue(review, AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
            }
            changes.execute();

            teamWf = reload(teamWf);

            // transition to authorize
            teamWf = transitionAndReload(teamWf, TeamState.Authorize);

            teamWf = reload(teamWf);

            if (atsApi.getReviewService().getReviews(teamWf).size() != 2) {
               throw new OseeCoreException(
                  "Error, 2 atsApi.getReviewService().getReviews(teamWf) should exist instead of " + atsApi.getReviewService().getReviews(
                     teamWf).size());
            }

            // set reviews to non-blocking
            changes = atsApi.createChangeSet("Transition Workflows");
            for (IAtsAbstractReview review : atsApi.getReviewService().getReviews(teamWf)) {
               changes.setSoleAttributeValue(review, AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
            }
            changes.execute();

            // reload to see latest
            teamWf = reload(teamWf);

         }

         // Transition to final state
         Pair<IAtsTeamWorkflow, Result> result = transitionToWithPersist(teamWf, TeamState.Implement,
            teamWf.getAssignees().iterator().next(), teamWf.getAssignees(), atsApi);
         if (result.getSecond().isFalse()) {
            rd.errorf("Transition Failed: " + result.getSecond().getText());
            return;
         }
         teamWf = result.getFirst();

         teamWf = setVersionAndReload(teamWf, DemoArtifactToken.SAW_Bld_2);

         if (teamWf.getTeamDefinition().getName().contains("Requirements")) {
            reqTeamWf = teamWf;
         }
      }

      if (reqTeamWf == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
   }

}
