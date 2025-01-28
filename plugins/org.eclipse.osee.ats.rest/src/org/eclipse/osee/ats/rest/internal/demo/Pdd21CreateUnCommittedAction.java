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
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoCscis;
import org.eclipse.osee.ats.api.demo.DemoWorkflowTitles;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.demo.DemoUtil.SoftwareRequirementStrs;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoSubsystems;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class Pdd21CreateUnCommittedAction extends AbstractPopulateDemoDatabase {

   private ActionResult actionResult;

   public Pdd21CreateUnCommittedAction(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   public TeamWorkFlowManager getTeamWfMgr(IAtsTeamWorkflow teamWf) {
      return new TeamWorkFlowManager(teamWf, atsApi, TransitionOption.OverrideAssigneeCheck,
         TransitionOption.OverrideWorkingBranchCheck);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      String title = DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW;
      Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(DemoArtifactToken.SAW_Code_AI,
         DemoArtifactToken.SAW_SW_Design_AI, DemoArtifactToken.SAW_Requirements_AI, DemoArtifactToken.SAW_Test_AI);
      Date createdDate = new Date();
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();
      String priority = "3";

      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      actionResult = atsApi.getActionService().createAction(null, title, "Problem with the Diagram View",
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

      Result result = atsApi.getBranchService().createWorkingBranchValidate(reqTeamWf);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }

      BranchData bData = atsApi.getBranchService().createWorkingBranch(reqTeamWf);
      if (bData.getResults().isErrors()) {
         throw new OseeStateException("Error creating working branch %s\n", bData.getResults().toString());
      }
      if (bData.getNewBranch().isInvalid()) {
         throw new OseeStateException("New Branch is invalid\n");
      }
      BranchToken workingBranch = atsApi.getBranchService().getBranch(bData.getNewBranch());
      if (workingBranch.isInvalid()) {
         throw new OseeStateException("Working Branch is invalid\n");
      }

      changes = atsApi.createChangeSet(getClass().getSimpleName() + " 2.5", workingBranch);
      for (ArtifactToken art : DemoUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.Functional,
         workingBranch)) {
         changes.setSoleAttributeValue(art, CoreAttributeTypes.CSCI, DemoCscis.Interface.name());
         changes.setSoleAttributeValue(art, CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         ArtifactToken robotArt = atsApi.getQueryService().getArtifactFromName(CoreArtifactTypes.Component,
            DemoSubsystems.Robot_API.name(), workingBranch);
         changes.relate(art, CoreRelationTypes.Allocation_Component, robotArt);
      }
      changes.execute();

      changes = atsApi.createChangeSet(getClass().getSimpleName() + " - 3", workingBranch);
      // Delete one artifacts
      for (ArtifactToken art : DemoUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.daVinci,
         workingBranch)) {
         changes.deleteArtifact(art);
      }

      // Add three new artifacts
      ArtifactToken parentArt = DemoUtil.getInterfaceInitializationSoftwareRequirement(false, workingBranch);
      for (int x = 1; x < 4; x++) {
         String name = "Claw Interface Init " + x;
         ArtifactToken newArt = changes.createArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, name);
         changes.setSoleAttributeValue(newArt, CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         changes.addChild(parentArt, newArt);
      }
      changes.execute();

   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         if (applicableAis.iterator().next().getArtifactToken().equals(DemoArtifactToken.SAW_Test_AI)) {
            return DemoArtifactToken.SAW_UnCommited_Test_TeamWf;
         } else if (applicableAis.iterator().next().getArtifactToken().equals(DemoArtifactToken.SAW_Code_AI)) {
            return DemoArtifactToken.SAW_UnCommited_Code_TeamWf;
         } else if (applicableAis.iterator().next().getArtifactToken().equals(DemoArtifactToken.SAW_Requirements_AI)) {
            return DemoArtifactToken.SAW_UnCommited_Req_TeamWf;
         } else if (applicableAis.iterator().next().getArtifactToken().equals(DemoArtifactToken.SAW_SW_Design_AI)) {
            return DemoArtifactToken.SAW_UnCommited_SWDesign_TeamWf;
         }
         throw new UnsupportedOperationException();
      }
   }

}
