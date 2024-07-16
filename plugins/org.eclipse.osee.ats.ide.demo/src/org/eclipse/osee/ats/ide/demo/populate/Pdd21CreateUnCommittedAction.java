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

package org.eclipse.osee.ats.ide.demo.populate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
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
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil.SoftwareRequirementStrs;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoSubsystems;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class Pdd21CreateUnCommittedAction implements IPopulateDemoDatabase {

   private ActionResult actionResult;

   @Override
   public void run() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      String title = DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW;
      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.SAW_Code_AI,
         DemoArtifactToken.SAW_SW_Design_AI, DemoArtifactToken.SAW_Requirements_AI, DemoArtifactToken.SAW_Test_AI);
      Date createdDate = new Date();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
      String priority = "3";

      actionResult = AtsApiService.get().getActionService().createAction(null, title, "Problem with the Diagram View",
         ChangeTypes.Problem, priority, false, null, aias, createdDate, createdBy,
         Arrays.asList(new ArtifactTokenActionListener()), changes);
      for (IAtsTeamWorkflow teamWf : actionResult.getTeamWfs()) {

         boolean isSwDesign = teamWf.getTeamDefinition().getName().contains("SW Design");

         TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsApiService.get(),
            TransitionOption.OverrideAssigneeCheck, TransitionOption.None);

         if (isSwDesign) {
            // transition to analyze
            Result result =
               dtwm.transitionTo(TeamState.Analyze, teamWf.getAssignees().iterator().next(), false, changes);
            if (result.isFalse()) {
               throw new OseeCoreException("Error transitioning [%s] to Analyze state [%s] error [%s]",
                  teamWf.toStringWithId(), toState.getName(), result.getText());
            }
            if (AtsApiService.get().getReviewService().getReviews(teamWf).size() != 1) {
               throw new OseeCoreException(
                  "Error, 1 review should have been created instead of " + AtsApiService.get().getReviewService().getReviews(
                     teamWf).size());
            }
            // set reviews to non-blocking
            for (IAtsAbstractReview review : AtsApiService.get().getReviewService().getReviews(teamWf)) {
               changes.setSoleAttributeValue(review, AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
            }

            // transition to authorize
            result = dtwm.transitionTo(TeamState.Authorize, teamWf.getAssignees().iterator().next(), false, changes);
            if (result.isFalse()) {
               throw new OseeCoreException("Error transitioning [%s] to Authorize state: [%s]", teamWf.toStringWithId(),
                  toState.getName(), result.getText());
            }
            if (AtsApiService.get().getReviewService().getReviews(teamWf).size() != 2) {
               throw new OseeCoreException(
                  "Error, 2 AtsApiService.get().getReviewService().getReviews(teamWf) should exist instead of " + AtsApiService.get().getReviewService().getReviews(
                     teamWf).size());
            }

            // set reviews to non-blocking
            for (IAtsAbstractReview review : AtsApiService.get().getReviewService().getReviews(teamWf)) {
               changes.setSoleAttributeValue(review, AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
            }
         }

         if (isSwDesign) {
            // Need to persist so CreateTasksDefinitionBuilder can find workflow to create tasks
            changes.execute();
            changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + "- 2");
         }

         // Transition to final state
         Result result = dtwm.transitionTo(toState, teamWf.getAssignees().iterator().next(), false, changes);
         if (result.isFalse()) {
            throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
               toState.getName(), result.getText());
         }

         if (!teamWf.isCompletedOrCancelled()) {
            // Reset assignees that may have been overwritten during transition
            changes.setAssignees(teamWf,
               AtsApiService.get().getTeamDefinitionService().getLeads(teamWf.getTeamDefinition()));
         }

         setVersion(teamWf, DemoArtifactToken.SAW_Bld_2, changes);
         changes.add(teamWf);
      }
      changes.execute();

      TeamWorkFlowArtifact reqTeamArt = null;
      for (IAtsTeamWorkflow teamWf : actionResult.getTeamWfs()) {
         if (teamWf.getTeamDefinition().getName().contains("Req")) {
            reqTeamArt = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         }
      }

      if (reqTeamArt == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
      Result result = AtsApiService.get().getBranchServiceIde().createWorkingBranch_Validate(reqTeamArt);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }
      AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(reqTeamArt, true);

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.Functional,
         reqTeamArt.getWorkingBranch())) {
         art.setSoleAttributeValue(CoreAttributeTypes.CSCI, DemoCscis.Interface.name());
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         Artifact comArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
            DemoSubsystems.Robot_API.name(), reqTeamArt.getWorkingBranch());

         art.addRelation(CoreRelationTypes.Allocation_Component, comArt);
         art.persist(getClass().getSimpleName());
      }

      // Delete one artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.CISST,
         reqTeamArt.getWorkingBranch())) {
         art.deleteAndPersist(getClass().getSimpleName());
      }

      // Add two new artifacts
      Artifact parentArt =
         DemoDbUtil.getInterfaceInitializationSoftwareRequirement(false, reqTeamArt.getWorkingBranch());
      for (int x = 15; x < 17; x++) {
         String name = "Claw Interface Init " + x;
         Artifact newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         parentArt.addChild(newArt);

         newArt.persist(getClass().getSimpleName());
      }

   }
   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Test_AI)) {
            return DemoArtifactToken.SAW_UnCommited_Test_TeamWf;
         } else if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Code_AI)) {
            return DemoArtifactToken.SAW_UnCommited_Code_TeamWf;
         } else if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Requirements_AI)) {
            return DemoArtifactToken.SAW_UnCommited_Req_TeamWf;
         } else if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_SW_Design_AI)) {
            return DemoArtifactToken.SAW_UnCommited_SWDesign_TeamWf;
         }
         throw new UnsupportedOperationException();
      }
   }

}
