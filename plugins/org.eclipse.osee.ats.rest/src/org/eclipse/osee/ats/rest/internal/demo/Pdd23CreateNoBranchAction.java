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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkflowTitles;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
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

      String title = DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW;
      Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(DemoArtifactToken.SAW_Code_AI,
         DemoArtifactToken.SAW_SW_Design_AI, DemoArtifactToken.SAW_Requirements_AI, DemoArtifactToken.SAW_Test_AI);
      Date createdDate = new Date();
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();
      String priority = "3";

      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      ActionResult actionResult = atsApi.getActionService().createAction(null, title, "Problem with the Diagram View",
         ChangeTypes.Problem, priority, false, null, aias, createdDate, createdBy,
         Arrays.asList(new ArtifactTokenActionListener()), changes);
      changes.execute();

      IAtsTeamWorkflow reqTeamWf = null;
      for (IAtsTeamWorkflow teamWf : actionResult.getTeamWfs()) {

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
            changes = atsApi.createChangeSet("Transition Workflows");
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
            throw new OseeStateException("Error transitioning " + result.getSecond().toString());
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

   private static class ArtifactTokenActionListener implements INewActionListener {
      @SuppressWarnings("unlikely-arg-type")
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         if (applicableAis.iterator().next().getArtifactToken().equals(DemoArtifactToken.SAW_Test_AI)) {
            return DemoArtifactToken.SAW_NoBranch_Test_TeamWf;
         } else if (applicableAis.iterator().next().getArtifactToken().equals(DemoArtifactToken.SAW_Code_AI)) {
            return DemoArtifactToken.SAW_NoBranch_Code_TeamWf;
         } else if (applicableAis.iterator().next().getArtifactToken().equals(DemoArtifactToken.SAW_Requirements_AI)) {
            return DemoArtifactToken.SAW_NoBranch_Req_TeamWf;
         } else if (applicableAis.iterator().next().getArtifactToken().equals(DemoArtifactToken.SAW_SW_Design_AI)) {
            return DemoArtifactToken.SAW_NoBranch_SWDesign_TeamWf;
         }
         throw new UnsupportedOperationException();
      }
   }

}
