/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.demo.populate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoCscis;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.branch.AtsBranchUtil;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.demo.internal.AtsClientService;
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
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class Pdd22CreateUnCommittedConflictedAction implements IPopulateDemoDatabase {

   private ActionResult actionResult;

   @Override
   public void run() {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getName());

      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.SAW_Requirements_AI);
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      String priority = "3";

      actionResult = AtsClientService.get().getActionFactory().createAction(null,
         DemoArtifactToken.SAW_UnCommitedConflicted_Req_TeamWf.getName(), "Problem with the Diagram View",
         ChangeType.Problem, priority, false, null, aias, createdDate, createdBy, new ArtifactTokenActionListener(),
         changes);
      for (IAtsTeamWorkflow teamWf : actionResult.getTeams()) {

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

         setVersion(teamWf, DemoArtifactToken.SAW_Bld_2, changes);
         changes.add(teamWf);
      }
      changes.execute();

      TeamWorkFlowArtifact reqTeamArt = null;
      for (IAtsTeamWorkflow teamWf : actionResult.getTeams()) {
         if (teamWf.getTeamDefinition().getName().contains("Req")) {
            reqTeamArt = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         }
      }

      if (reqTeamArt == null) {
         throw new OseeArgumentException("Can't locate Req team.");
      }
      Result result = AtsBranchUtil.createWorkingBranch_Validate(reqTeamArt);
      if (result.isFalse()) {
         throw new OseeArgumentException(
            new StringBuilder("Error creating working branch: ").append(result.getText()).toString());
      }
      AtsBranchUtil.createWorkingBranch_Create(reqTeamArt, true);

      Artifact branchArtifact = DemoDbUtil.getArtTypeRequirements(isDebug(), CoreArtifactTypes.SoftwareRequirement,
         DemoDbUtil.HAPTIC_CONSTRAINTS_REQ, reqTeamArt.getWorkingBranch()).iterator().next();
      branchArtifact.setSoleAttributeValue(CoreAttributeTypes.CSCI, DemoCscis.Interface.name());
      branchArtifact.setSoleAttributeValue(CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
      Artifact comArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Component,
         DemoSubsystems.Robot_API.name(), reqTeamArt.getWorkingBranch());
      branchArtifact.addRelation(CoreRelationTypes.Allocation__Component, comArt);
      branchArtifact.persist(getClass().getSimpleName());

      Artifact parentArtifact = DemoDbUtil.getArtTypeRequirements(isDebug(), CoreArtifactTypes.SoftwareRequirement,
         DemoDbUtil.HAPTIC_CONSTRAINTS_REQ, reqTeamArt.getWorkingBranch()).iterator().next();
      parentArtifact.setSoleAttributeValue(CoreAttributeTypes.CSCI, DemoCscis.Navigation.name());
      parentArtifact.setSoleAttributeValue(CoreAttributeTypes.Subsystem,
         DemoSubsystems.Cognitive_Decision_Aiding.name());
      parentArtifact.persist(getClass().getSimpleName());
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         return DemoArtifactToken.SAW_UnCommitedConflicted_Req_TeamWf;
      }
   }

}
