/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.client.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.client.demo.config.DemoDbUtil.SoftwareRequirementStrs;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.branch.AtsBranchUtil;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoCscis;
import org.eclipse.osee.ats.demo.api.DemoDbAIs;
import org.eclipse.osee.ats.demo.api.DemoSubsystems;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class PopulateSawBuild2Actions {

   private static IAtsVersion version = null;
   private static TeamState toState = TeamState.Implement;
   private static boolean DEBUG = false;

   public static void run() throws OseeCoreException {

      version = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_2);
      Conditions.checkNotNull(version, "SAW_Bld_2");

      // Create SAW_Bld_2 Actions
      AtsChangeSet changes = new AtsChangeSet("Populate Demo DB - PopulateSawBuild2Actions");

      // SawBuild2Action1
      ActionArtifact committedAction = sawBuild2Action1_createCommittedAction(changes);

      // SawBuild2Action2
      ActionArtifact unCommittedAction = sawBuild2Action2_createUnCommittedAction(changes);

      // SawBuild2Action3
      sawBuild2Action3_createNoBranchAction(changes);

      // SawBuild2Action4
      ActionArtifact conflictedAction = sawBuild2Action4_createUnCommittedConflictedAction(changes);

      changes.execute();

      // Sleep to wait for the persist of the actions
      DemoDbUtil.sleep(3000);

      // Working Branch off SAW_Bld_2, Make Changes, Commit
      makeCommittedActionChanges(committedAction);

      // Working Branch off SAW_Bld_2, Make Changes, DON'T Commit
      makeUnCommittedActionChanges(unCommittedAction);

      // Working Branch off SAW_Bld_2, Make Conflicted Changes, DON'T Commit
      makeConflictedActionChanges(conflictedAction);

   }

   private static ActionArtifact sawBuild2Action4_createUnCommittedConflictedAction(IAtsChangeSet changes) throws OseeCoreException {
      String title = "SAW (uncommitted-conflicted) More Requirement Changes for Diagram View";
      Collection<IAtsActionableItem> aias =
         DemoDbUtil.getActionableItems(new String[] {DemoDbAIs.SAW_Requirements.getAIName()});
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      String priority = "3";

      ActionArtifact actionArt = ActionManager.createAction(null, title, "Problem with the Diagram View",
         ChangeType.Problem, priority, false, null, aias, createdDate, createdBy, null, changes);
      for (TeamWorkFlowArtifact teamWf : ActionManager.getTeams(actionArt)) {

         TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices(),
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);

         // Transition to desired state
         Result result = dtwm.transitionTo(toState, teamWf.getAssignees().iterator().next(), false, changes);
         if (result.isFalse()) {
            throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
               toState.getName(), result.getText());
         }

         if (!teamWf.isCompletedOrCancelled()) {
            // Reset assignees that may have been overwritten during transition
            teamWf.getStateMgr().setAssignees(teamWf.getTeamDefinition().getLeads());
         }

         AtsClientService.get().getVersionService().setTargetedVersionAndStore(teamWf, version);
         changes.add(teamWf);
      }
      return actionArt;
   }

   private static ActionArtifact sawBuild2Action3_createNoBranchAction(IAtsChangeSet changes) throws OseeCoreException {
      String title = "SAW (no-branch) Even More Requirement Changes for Diagram View";
      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(new String[] {
         DemoDbAIs.SAW_Code.getAIName(),
         DemoDbAIs.SAW_SW_Design.getAIName(),
         DemoDbAIs.SAW_Requirements.getAIName(),
         DemoDbAIs.SAW_Test.getAIName()});
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      String priority = "3";

      ActionArtifact actionArt = ActionManager.createAction(null, title, "Problem with the Diagram View",
         ChangeType.Problem, priority, false, null, aias, createdDate, createdBy, null, changes);
      for (TeamWorkFlowArtifact teamWf : ActionManager.getTeams(actionArt)) {

         boolean isSwDesign = teamWf.getTeamDefinition().getName().contains("SW Design");

         TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices(),
            TransitionOption.OverrideAssigneeCheck);

         if (isSwDesign) {
            // transition to analyze
            Result result =
               dtwm.transitionTo(TeamState.Analyze, teamWf.getAssignees().iterator().next(), false, changes);
            if (result.isFalse()) {
               throw new OseeCoreException("Error transitioning [%s] to Analyze state: [%s]", teamWf.toStringWithId(),
                  toState.getName(), result.getText());
            }
            if (ReviewManager.getReviews(teamWf).size() != 1) {
               throw new OseeCoreException(
                  "Error, 1 review should have been created instead of " + ReviewManager.getReviews(teamWf).size());
            }
            // set reviews to non-blocking
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(teamWf)) {
               reviewArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
            }

            // transition to authorize
            result = dtwm.transitionTo(TeamState.Authorize, teamWf.getAssignees().iterator().next(), false, changes);
            if (result.isFalse()) {
               throw new OseeCoreException("Error transitioning [%s] to Authorize state: [%s]", teamWf.toStringWithId(),
                  toState.getName(), result.getText());
            }
            if (ReviewManager.getReviews(teamWf).size() != 2) {
               throw new OseeCoreException(
                  "Error, 2 reviews should exist instead of " + ReviewManager.getReviews(teamWf).size());
            }

            // set reviews to non-blocking
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(teamWf)) {
               reviewArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
            }
         }
         // Transition to final state
         Result result = dtwm.transitionTo(toState, teamWf.getAssignees().iterator().next(), false, changes);
         if (result.isFalse()) {
            throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
               toState.getName(), result.getText());
         }

         if (!teamWf.isCompletedOrCancelled()) {
            // Reset assignees that may have been overwritten during transition
            teamWf.getStateMgr().setAssignees(teamWf.getTeamDefinition().getLeads());
         }

         AtsClientService.get().getVersionService().setTargetedVersionAndStore(teamWf, version);
         changes.add(teamWf);
      }
      return actionArt;
   }

   private static ActionArtifact sawBuild2Action2_createUnCommittedAction(IAtsChangeSet changes) throws OseeCoreException {
      String title = "SAW (uncommitted) More Reqt Changes for Diagram View";
      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(new String[] {
         DemoDbAIs.SAW_Code.getAIName(),
         DemoDbAIs.SAW_SW_Design.getAIName(),
         DemoDbAIs.SAW_Requirements.getAIName(),
         DemoDbAIs.SAW_Test.getAIName()});
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      String priority = "3";

      ActionArtifact actionArt = ActionManager.createAction(null, title, "Problem with the Diagram View",
         ChangeType.Problem, priority, false, null, aias, createdDate, createdBy, null, changes);
      for (TeamWorkFlowArtifact teamWf : ActionManager.getTeams(actionArt)) {

         boolean isSwDesign = teamWf.getTeamDefinition().getName().contains("SW Design");

         TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices(),
            TransitionOption.OverrideAssigneeCheck, TransitionOption.None);

         if (isSwDesign) {
            // transition to analyze
            Result result =
               dtwm.transitionTo(TeamState.Analyze, teamWf.getAssignees().iterator().next(), false, changes);
            if (result.isFalse()) {
               throw new OseeCoreException("Error transitioning [%s] to Analyze state [%s] error [%s]",
                  teamWf.toStringWithId(), toState.getName(), result.getText());
            }
            if (ReviewManager.getReviews(teamWf).size() != 1) {
               throw new OseeCoreException(
                  "Error, 1 review should have been created instead of " + ReviewManager.getReviews(teamWf).size());
            }
            // set reviews to non-blocking
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(teamWf)) {
               reviewArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
            }

            // transition to authorize
            result = dtwm.transitionTo(TeamState.Authorize, teamWf.getAssignees().iterator().next(), false, changes);
            if (result.isFalse()) {
               throw new OseeCoreException("Error transitioning [%s] to Authorize state: [%s]", teamWf.toStringWithId(),
                  toState.getName(), result.getText());
            }
            if (ReviewManager.getReviews(teamWf).size() != 2) {
               throw new OseeCoreException(
                  "Error, 2 reviews should exist instead of " + ReviewManager.getReviews(teamWf).size());
            }

            // set reviews to non-blocking
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(teamWf)) {
               reviewArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
            }
         }

         // Transition to final state
         Result result = dtwm.transitionTo(toState, teamWf.getAssignees().iterator().next(), false, changes);
         if (result.isFalse()) {
            throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
               toState.getName(), result.getText());
         }

         if (!teamWf.isCompletedOrCancelled()) {
            // Reset assignees that may have been overwritten during transition
            teamWf.getStateMgr().setAssignees(teamWf.getTeamDefinition().getLeads());
         }

         AtsClientService.get().getVersionService().setTargetedVersionAndStore(teamWf, version);
         changes.add(teamWf);
      }
      return actionArt;
   }

   private static ActionArtifact sawBuild2Action1_createCommittedAction(IAtsChangeSet changes) throws OseeCoreException {
      String title = "SAW (committed) Reqt Changes for Diagram View";
      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(new String[] {
         DemoDbAIs.SAW_Requirements.getAIName(),
         DemoDbAIs.SAW_Code.getAIName(),
         DemoDbAIs.SAW_Test.getAIName()});
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      String priority = "1";

      ActionArtifact actionArt = ActionManager.createAction(null, title, "Problem with the Diagram View",
         ChangeType.Problem, priority, false, null, aias, createdDate, createdBy, null, changes);
      for (TeamWorkFlowArtifact teamWf : ActionManager.getTeams(actionArt)) {

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
         } else if (teamWf.getTeamDefinition().getName().contains(
            "Design") && !teamWf.getWorkDefinition().getName().equals("WorkDef_Team_Demo_SwDesign")) {
            throw new OseeCoreException("SwDesign workflow expected work def [WorkDef_Team_Demo_SwDesign] actual [%s]",
               teamWf.getWorkDefinition().getName());
         }

         TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices(),
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);

         // Transition to desired state
         Result result = dtwm.transitionTo(toState, teamWf.getAssignees().iterator().next(), false, changes);
         if (result.isFalse()) {
            throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
               toState.getName(), result.getText());
         }

         if (!teamWf.isCompletedOrCancelled()) {
            // Reset assignees that may have been overwritten during transition
            teamWf.getStateMgr().setAssignees(teamWf.getTeamDefinition().getLeads());
         }

         changes.add(teamWf);
         AtsClientService.get().getVersionService().setTargetedVersionAndStore(teamWf, version);
      }
      return actionArt;
   }

   private static void makeCommittedActionChanges(Artifact actionArt) throws OseeCoreException {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Making Action 1 Requirement Changes");
      }
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : ActionManager.getTeams(actionArt)) {
         if (team.getTeamDefinition().getName().contains("Req")) {
            reqTeam = team;
         }
      }

      if (reqTeam == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Creating working branch");
      }
      Result result = AtsBranchUtil.createWorkingBranch_Validate(reqTeam);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }
      AtsBranchUtil.createWorkingBranch_Create(reqTeam, true);

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(DEBUG, SoftwareRequirementStrs.Robot,
         reqTeam.getWorkingBranch())) {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO,
               new StringBuilder("Modifying artifact => ").append(art).toString());
         }
         art.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Navigation.name());
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Navigation.name());
         Artifact navArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
            DemoSubsystems.Navigation.name(), reqTeam.getWorkingBranch());
         art.addRelation(CoreRelationTypes.Allocation__Component, navArt);
         art.persist(PopulateSawBuild2Actions.class.getSimpleName());
      }

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(DEBUG, SoftwareRequirementStrs.Event,
         reqTeam.getWorkingBranch())) {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO,
               new StringBuilder("Modifying artifact => ").append(art).toString());
         }
         art.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Interface.name());
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         Artifact robotArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
            DemoSubsystems.Robot_API.name(), reqTeam.getWorkingBranch());
         art.addRelation(CoreRelationTypes.Allocation__Component, robotArt);
         art.persist(PopulateSawBuild2Actions.class.getSimpleName());
      }

      // Delete two artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(DEBUG, SoftwareRequirementStrs.daVinci,
         reqTeam.getWorkingBranch())) {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, new StringBuilder("Deleting artifact => ").append(art).toString());
         }
         art.deleteAndPersist();
      }

      // Add three new artifacts
      Artifact parentArt = DemoDbUtil.getInterfaceInitializationSoftwareRequirement(DEBUG, reqTeam.getWorkingBranch());
      for (int x = 1; x < 4; x++) {
         String name = "Robot Interface Init " + x;
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Adding artifact => " + name);
         }
         Artifact newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         newArt.persist(PopulateSawBuild2Actions.class.getSimpleName());
         parentArt.addChild(newArt);
         parentArt.persist(PopulateSawBuild2Actions.class.getSimpleName());
      }

      DemoDbUtil.sleep(2000L);
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Committing branch");
      }
      IOperation op =
         AtsBranchManager.commitWorkingBranch(reqTeam, false, true, AtsClientService.get().getBranchService().getBranch(
            (IAtsConfigObject) AtsClientService.get().getVersionService().getTargetedVersion(reqTeam)), true);
      Operations.executeWorkAndCheckStatus(op);
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Completing Action");
      }
   }

   private static void makeConflictedActionChanges(Artifact actionArt) throws OseeCoreException {
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : ActionManager.getTeams(actionArt)) {
         if (team.getTeamDefinition().getName().contains("Req")) {
            reqTeam = team;
         }
      }

      if (reqTeam == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Creating working branch");
      }
      Result result = AtsBranchUtil.createWorkingBranch_Validate(reqTeam);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }
      AtsBranchUtil.createWorkingBranch_Create(reqTeam);

      DemoDbUtil.sleep(5000);

      Artifact branchArtifact = DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement,
         DemoDbUtil.HAPTIC_CONSTRAINTS_REQ, reqTeam.getWorkingBranch()).iterator().next();
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO,
            new StringBuilder("Modifying branch artifact => ").append(branchArtifact).toString());
      }
      branchArtifact.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Interface.name());
      branchArtifact.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
      Artifact comArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
         DemoSubsystems.Robot_API.name(), reqTeam.getWorkingBranch());
      branchArtifact.addRelation(CoreRelationTypes.Allocation__Component, comArt);
      branchArtifact.persist(PopulateSawBuild2Actions.class.getSimpleName());

      Artifact parentArtifact = DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement,
         DemoDbUtil.HAPTIC_CONSTRAINTS_REQ, reqTeam.getWorkingBranch()).iterator().next();
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO,
            new StringBuilder("Modifying parent artifact => ").append(parentArtifact).toString());
      }
      parentArtifact.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Navigation.name());
      parentArtifact.setSoleAttributeValue(CoreAttributeTypes.Subsystem,
         DemoSubsystems.Cognitive_Decision_Aiding.name());
      parentArtifact.persist(PopulateSawBuild2Actions.class.getSimpleName());

   }

   private static void makeUnCommittedActionChanges(Artifact actionArt) throws OseeCoreException {
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : ActionManager.getTeams(actionArt)) {
         if (team.getTeamDefinition().getName().contains("Req")) {
            reqTeam = team;
         }
      }

      if (reqTeam == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Creating working branch");
      }
      Result result = AtsBranchUtil.createWorkingBranch_Validate(reqTeam);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }
      AtsBranchUtil.createWorkingBranch_Create(reqTeam);

      DemoDbUtil.sleep(5000);

      for (Artifact art : DemoDbUtil.getSoftwareRequirements(DEBUG, SoftwareRequirementStrs.Functional,
         reqTeam.getWorkingBranch())) {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO,
               new StringBuilder("Modifying artifact => ").append(art).toString());
         }
         art.setSoleAttributeValue(CoreAttributeTypes.Csci, DemoCscis.Interface.name());
         art.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         Artifact comArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
            DemoSubsystems.Robot_API.name(), reqTeam.getWorkingBranch());

         art.addRelation(CoreRelationTypes.Allocation__Component, comArt);
         art.persist(PopulateSawBuild2Actions.class.getSimpleName());
      }

      // Delete one artifacts
      for (Artifact art : DemoDbUtil.getSoftwareRequirements(DEBUG, SoftwareRequirementStrs.CISST,
         reqTeam.getWorkingBranch())) {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, new StringBuilder("Deleting artifact => ").append(art).toString());
         }
         art.deleteAndPersist();
      }

      // Add two new artifacts
      Artifact parentArt = DemoDbUtil.getInterfaceInitializationSoftwareRequirement(DEBUG, reqTeam.getWorkingBranch());
      for (int x = 15; x < 17; x++) {
         String name = "Claw Interface Init " + x;
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Adding artifact => " + name);
         }
         Artifact newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, parentArt.getBranch(), name);
         newArt.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         parentArt.addChild(newArt);

         newArt.persist(PopulateSawBuild2Actions.class.getSimpleName());
      }

   }

}
