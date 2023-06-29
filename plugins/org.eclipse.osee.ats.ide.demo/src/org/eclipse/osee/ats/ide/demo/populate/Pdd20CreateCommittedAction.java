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
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoCscis;
import org.eclipse.osee.ats.api.demo.DemoWorkflowTitles;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil.SoftwareRequirementStrs;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoSubsystems;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class Pdd20CreateCommittedAction implements IPopulateDemoDatabase {

   @Override
   public void run() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.SAW_Requirements_AI,
         DemoArtifactToken.SAW_Code_AI, DemoArtifactToken.SAW_Test_AI);
      Date createdDate = new Date();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
      String priority = "1";

      ActionResult actionResult = AtsApiService.get().getActionService().createAction(null,
         DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "Problem with the Diagram View",
         ChangeTypes.Problem, priority, false, null, aias, createdDate, createdBy,
         Arrays.asList(new ArtifactTokenActionListener()), changes);
      for (IAtsTeamWorkflow teamWf : actionResult.getTeamWfs()) {

         if (teamWf.getTeamDefinition().getName().contains(
            "Req") && !teamWf.getWorkDefinition().getName().equals("WorkDef_Team_Demo_Req")) {
            throw new OseeCoreException("Req workflow expected work def [WorkDef_Team_Demo_Req] actual [%s]",
               teamWf.getWorkDefinition().getName());
         } else if (teamWf.getTeamDefinition().getName().contains(
            "Code") && !teamWf.getWorkDefinition().getName().equals("WorkDef_Team_Demo_Code")) {
            throw new OseeCoreException("Code workflow expected work def [WorkDef_Team_Demo_Code] actual [%s]",
               teamWf.getWorkDefinition().getName());
         } else if (teamWf.getTeamDefinition().getName().contains(
            "Test") && !teamWf.getWorkDefinition().getName().equals("WorkDef_Team_Demo_Test")) {
            throw new OseeCoreException("Test workflow expected work def [WorkDef_Team_Demo_Test] actual [%s]",
               teamWf.getWorkDefinition().getName());
         }

         TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsApiService.get(),
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);

         // Transition to desired state
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
         throw new OseeArgumentException("Error creating working branch: " + result);
      }
      AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(reqTeamArt, true);

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.Robot,
         reqTeamArt.getWorkingBranch())) {
         art.setSoleAttributeValue(CoreAttributeTypes.CSCI, DemoCscis.Navigation.name());
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Navigation.name());
         Artifact navArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
            DemoSubsystems.Navigation.name(), reqTeamArt.getWorkingBranch());
         art.addRelation(CoreRelationTypes.Allocation_Component, navArt);
         art.persist(getClass().getSimpleName());
      }
      Artifact testArtifact = null;
      Artifact testRelArtifact = null;
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.Event,
         reqTeamArt.getWorkingBranch())) {
         art.setSoleAttributeValue(CoreAttributeTypes.CSCI, DemoCscis.Interface.name());
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         Artifact robotArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
            DemoSubsystems.Robot_API.name(), reqTeamArt.getWorkingBranch());
         art.addRelation(CoreRelationTypes.Allocation_Component, robotArt);
         art.persist(getClass().getSimpleName());
         testArtifact = art;
         testRelArtifact = robotArt;

      }

      // Delete two artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.daVinci,
         reqTeamArt.getWorkingBranch())) {
         art.deleteAndPersist(getClass().getSimpleName());
      }

      // Add three new artifacts
      Artifact parentArt =
         DemoDbUtil.getInterfaceInitializationSoftwareRequirement(false, reqTeamArt.getWorkingBranch());
      for (int x = 1; x < 4; x++) {
         String name = "Robot Interface Init " + x;
         Artifact newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         newArt.persist(getClass().getSimpleName());
         parentArt.addChild(newArt);
         parentArt.persist(getClass().getSimpleName());
      }

      Artifact parentArtifact = testCommitBranchHttpRequestOperationSetup(reqTeamArt, testArtifact, testRelArtifact);

      XResultData rd = AtsApiService.get().getBranchServiceIde().commitWorkingBranch(reqTeamArt, false, true,
         AtsApiService.get().getBranchService().getBranch(
            AtsApiService.get().getVersionService().getTargetedVersion(reqTeamArt)),
         true, new XResultData());
      if (rd.isErrors()) {
         throw new OseeCoreException(rd.toString());
      }

      testCommitBranchHttpRequestOperation(testRelArtifact, parentArtifact);
   }

   private void testCommitBranchHttpRequestOperation(Artifact testRelArtifact, Artifact parentArtifact) {
      // Try up to 10 times to wait for update to happen since update event runs in background thread.
      int loops = 1;
      for (int x = 0; x <= loops; x++) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
         /**
          * This tests that the CommitBranchHttpRequestOperation updates the locally cached parent branch artifacts that
          * were changed due to the commit.
          */
         String subsystemStrAfter = parentArtifact.getSoleAttributeValue(CoreAttributeTypes.Subsystem).toString();
         if (!subsystemStrAfter.equals(DemoSubsystems.Communications.name())) {
            if (x < loops) {
               continue;
            }
            throw new OseeArgumentException("Artifact Attribute did not update in Parent Branch after commit");
         }
         ArtifactToken testRelOnSameBranch = ArtifactToken.valueOf(testRelArtifact, parentArtifact.getBranch());
         if (!parentArtifact.isRelated(CoreRelationTypes.Allocation_Component, testRelOnSameBranch)) {
            if (x < loops) {
               continue;
            }
            throw new OseeArgumentException("Artifact Relation does NOT exist in Parent branch after commit.");
         }
      }
   }

   private Artifact testCommitBranchHttpRequestOperationSetup(TeamWorkFlowArtifact reqTeamArt, Artifact testArtifact,
      Artifact testRelArtifact) {
      /**
       * Setup for testing the CommitBranchHttpRequestOperation cache update code after commit. Load artifact from
       * parent branch which is being changed on the working branch.
       */
      BranchId parentBranch = AtsApiService.get().getBranchService().getBranch(
         AtsApiService.get().getVersionService().getTargetedVersion(reqTeamArt));
      Artifact parentArtifact = ArtifactQuery.getArtifactFromId(testArtifact.getId(), parentBranch);
      String subsystemStrBefore = parentArtifact.getSoleAttributeValue(CoreAttributeTypes.Subsystem).toString();
      if (subsystemStrBefore.equals(DemoSubsystems.Communications.name())) {
         throw new OseeArgumentException(
            "Artifact Attribute matches between Working and Parent branch before commit.  Invalid Test. ");
      }

      for (Artifact art : parentArtifact.getRelatedArtifacts(CoreRelationTypes.Allocation_Component)) {
         if (art.getId() == testRelArtifact.getId()) {
            throw new OseeArgumentException(
               "Artifact Relation exists in Working and Parent branch before commit.  Invalid Test. ");
         }
      }
      return parentArtifact;
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Test_AI)) {
            return DemoArtifactToken.SAW_Commited_Test_TeamWf;
         } else if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Code_AI)) {
            return DemoArtifactToken.SAW_Commited_Code_TeamWf;
         } else if (applicableAis.iterator().next().equals(DemoArtifactToken.SAW_Requirements_AI)) {
            return DemoArtifactToken.SAW_Commited_Req_TeamWf;
         }
         throw new UnsupportedOperationException();
      }
   }

}
