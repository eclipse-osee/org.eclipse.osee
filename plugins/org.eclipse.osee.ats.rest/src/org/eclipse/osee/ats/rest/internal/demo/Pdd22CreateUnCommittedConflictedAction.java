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
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoCscis;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoSubsystems;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class Pdd22CreateUnCommittedConflictedAction extends AbstractPopulateDemoDatabase {

   private ActionResult actionResult;

   public Pdd22CreateUnCommittedConflictedAction(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(DemoArtifactToken.SAW_Requirements_AI);
      Date createdDate = new Date();
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();
      String priority = "3";

      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getName());
      actionResult =
         atsApi.getActionService().createAction(null, DemoArtifactToken.SAW_UnCommitedConflicted_Req_TeamWf.getName(),
            "Problem with the Diagram View", ChangeTypes.Problem, priority, false, null, aias, createdDate, createdBy,
            Arrays.asList(new ArtifactTokenActionListener()), changes);
      changes.execute();

      IAtsTeamWorkflow reqTeamWf = null;
      for (IAtsTeamWorkflow teamWf : actionResult.getTeamWfs()) {
         changes = atsApi.createChangeSet(getClass().getName() + " - 2");

         transitionToWithPersist(teamWf, TeamState.Implement, teamWf.getAssignees().iterator().next(),
            teamWf.getAssignees(), atsApi);

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

      changes = atsApi.createChangeSet(getClass().getName(), workingBranch);
      ArtifactToken branchArtifact = DemoUtil.getArtTypeRequirements(isDebug(),
         CoreArtifactTypes.SoftwareRequirementMsWord, DemoUtil.HAPTIC_CONSTRAINTS_REQ, workingBranch).iterator().next();
      changes.setSoleAttributeValue(branchArtifact, CoreAttributeTypes.CSCI, DemoCscis.Interface.name());
      changes.setSoleAttributeValue(branchArtifact, CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
      ArtifactToken comArt = atsApi.getQueryService().getArtifactFromName(CoreArtifactTypes.Component,
         DemoSubsystems.Robot_API.name(), workingBranch);
      changes.relate(branchArtifact, CoreRelationTypes.Allocation_Component, comArt);

      ArtifactToken parentArtifact = DemoUtil.getArtTypeRequirements(isDebug(),
         CoreArtifactTypes.SoftwareRequirementMsWord, DemoUtil.HAPTIC_CONSTRAINTS_REQ, workingBranch).iterator().next();
      changes.setSoleAttributeValue(parentArtifact, CoreAttributeTypes.CSCI, DemoCscis.Navigation.name());
      changes.setSoleAttributeValue(parentArtifact, CoreAttributeTypes.Subsystem,
         DemoSubsystems.Cognitive_Decision_Aiding.name());

      changes.execute();
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         return DemoArtifactToken.SAW_UnCommitedConflicted_Req_TeamWf;
      }
   }

}
