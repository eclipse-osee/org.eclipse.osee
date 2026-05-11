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

import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Code_AI;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Commited_Code_TeamWf;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Commited_Req_TeamWf;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Commited_Test_TeamWf;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Requirements_AI;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.SAW_Test_AI;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoCscis;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.demo.DemoUtil.SoftwareRequirementStrs;
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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class Pdd20CreateCommittedAction extends AbstractPopulateDemoDatabase {

   public Pdd20CreateCommittedAction(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      boolean found = false;
      for (WorkDefinition workDef : atsApi.getWorkDefinitionService().getAllWorkDefinitions()) {
         if (workDef.getId().equals(DemoWorkDefinitions.WorkDef_Team_Demo_Req.getId())) {
            found = true;
         }
      }
      if (!found) {
         rd.errorf("Demo Work Definitions not Loaded");
         return;
      }

      NewActionData data = atsApi.getActionService() //
         .createActionData(getClass().getSimpleName(), SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "Description") //
         .andAiAndToken(SAW_Test_AI, SAW_Commited_Test_TeamWf) //
         .andAiAndToken(SAW_Code_AI, SAW_Commited_Code_TeamWf) //
         .andAiAndToken(SAW_Requirements_AI, SAW_Commited_Req_TeamWf) //
         .andChangeType(ChangeTypes.Improvement).andPriority("1");
      NewActionData newData = atsApi.getActionService().createAction(data);
      if (dataErrored(newData)) {
         return;
      }

      IAtsTeamWorkflow reqTeamWf = null;
      for (IAtsTeamWorkflow teamWf : newData.getActResult().getAtsTeamWfs()) {

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

         // Transition to desired state
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
      Result result = atsApi.getBranchService().createWorkingBranchValidate(reqTeamWf);
      if (result.isFalse()) {
         throw new OseeArgumentException("Error creating working branch: " + result);
      }

      BranchData bData = atsApi.getBranchService().createWorkingBranch(reqTeamWf);
      if (bData.getResults().isErrors()) {
         throw new OseeStateException("Error creating working branch %s\n", bData.getResults().toString());
      }
      if (bData.getNewBranch().isInvalid()) {
         throw new OseeStateException("New Branch is invalid\n");
      }

      BranchToken workingBranch = atsApi.getBranchService().getWorkingBranchPend(reqTeamWf);
      if (workingBranch.isInvalid()) {
         throw new OseeStateException("Working Branch is invalid\n");
      }

      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName() + " - 2", workingBranch);
      for (ArtifactToken art : DemoUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.Robot, workingBranch)) {
         changes.setSoleAttributeValue(art, CoreAttributeTypes.CSCI, DemoCscis.Navigation.name());
         changes.setSoleAttributeValue(art, CoreAttributeTypes.Subsystem, DemoSubsystems.Navigation.name());
         ArtifactToken navArt = atsApi.getQueryService().getArtifactFromName(CoreArtifactTypes.Component,
            DemoSubsystems.Navigation.name(), workingBranch);
         changes.relate(art, CoreRelationTypes.Allocation_Component, navArt);
      }

      ArtifactToken testArtifactToken = null;
      ArtifactToken testRelArtifactToken = null;
      for (ArtifactToken art : DemoUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.Event, workingBranch)) {
         changes.setSoleAttributeValue(art, CoreAttributeTypes.CSCI, DemoCscis.Interface.name());
         changes.setSoleAttributeValue(art, CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         ArtifactToken robotArt = atsApi.getQueryService().getArtifactFromName(CoreArtifactTypes.Component,
            DemoSubsystems.Robot_API.name(), workingBranch);
         changes.relate(art, CoreRelationTypes.Allocation_Component, robotArt);
         testArtifactToken = art;
         testRelArtifactToken = robotArt;
      }
      changes.execute();
      Conditions.requireNonNull(testArtifactToken);
      Conditions.requireNonNull(testRelArtifactToken);

      changes = atsApi.createChangeSet(getClass().getSimpleName() + " - 3", workingBranch);
      // Delete two artifacts
      for (ArtifactToken art : DemoUtil.getSoftwareRequirements(false, SoftwareRequirementStrs.daVinci,
         workingBranch)) {
         changes.deleteArtifact(art);
      }

      // Add three new artifacts
      ArtifactToken parentArt = DemoUtil.getInterfaceInitializationSoftwareRequirement(false, workingBranch);
      for (int x = 1; x < 4; x++) {
         String name = "Robot Interface Init " + x;
         ArtifactToken newArt = changes.createArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, name);
         changes.setSoleAttributeValue(newArt, CoreAttributeTypes.Subsystem, DemoSubsystems.Communications.name());
         changes.addChild(parentArt, newArt);
      }
      changes.execute();

      ArtifactToken parentArtifactToken =
         testCommitBranchHttpRequestOperationSetup(reqTeamWf, testArtifactToken, testRelArtifactToken);

      XResultData rd = atsApi.getBranchService().commitWorkingBranch(reqTeamWf, false, true,
         atsApi.getBranchService().getBranch(atsApi.getVersionService().getTargetedVersion(reqTeamWf)), true,
         new XResultData());
      if (rd.isErrors()) {
         throw new OseeCoreException(rd.toString());
      }

      BranchToken parentBranch = atsApi.getBranchService().getBranch(parentArtifactToken.getBranch());
      parentArtifactToken = atsApi.getQueryService().getArtifact(parentArtifactToken.getToken(), parentBranch);
      @SuppressWarnings("null")
      ArtifactToken testRelArtifact =
         atsApi.getQueryService().getArtifact(testRelArtifactToken.getToken(), parentBranch);

      testCommitBranchHttpRequestOperation(testRelArtifact, parentArtifactToken, parentBranch);
   }

   private void testCommitBranchHttpRequestOperation(ArtifactToken testRelArtifact, ArtifactToken parentArtifact,
      BranchToken parentBranch) {
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
         String subsystemStrAfter =
            atsApi.getAttributeResolver().getSoleAttributeValue(parentArtifact, CoreAttributeTypes.Subsystem, "");
         if (!subsystemStrAfter.equals(DemoSubsystems.Communications.name())) {
            if (x < loops) {
               continue;
            }
            throw new OseeArgumentException("ArtifactToken Attribute did not update in Parent Branch after commit");
         }
         ArtifactToken testRelOnSameBranch = ArtifactToken.valueOf(testRelArtifact, parentBranch);
         if (!atsApi.getRelationResolver().getRelated(parentArtifact, CoreRelationTypes.Allocation_Component).contains(
            testRelOnSameBranch)) {
            if (x < loops) {
               continue;
            }
            throw new OseeArgumentException("ArtifactToken Relation does NOT exist in Parent branch after commit.");
         }
      }
   }

   private ArtifactToken testCommitBranchHttpRequestOperationSetup(IAtsTeamWorkflow reqTeamArt,
      ArtifactToken testArtifact, ArtifactToken testRelArtifact) {
      /**
       * Setup for testing the CommitBranchHttpRequestOperation cache update code after commit. Load ArtifactToken from
       * parent branch which is being changed on the working branch.
       */
      BranchToken parentBranch =
         atsApi.getBranchService().getBranch(atsApi.getVersionService().getTargetedVersion(reqTeamArt));
      ArtifactToken parentArtifactToken = atsApi.getQueryService().getArtifact(testArtifact.getToken(), parentBranch);
      String subsystemStrBefore =
         atsApi.getAttributeResolver().getSoleAttributeValue(parentArtifactToken, CoreAttributeTypes.Subsystem, "");
      if (subsystemStrBefore.equals(DemoSubsystems.Communications.name())) {
         throw new OseeArgumentException(
            "ArtifactToken Attribute matches between Working and Parent branch before commit.  Invalid Test. ");
      }

      for (ArtifactToken art : atsApi.getRelationResolver().getRelatedArtifacts(parentArtifactToken,
         CoreRelationTypes.Allocation_Component)) {
         if (art.getId().equals(testRelArtifact.getId())) {
            throw new OseeArgumentException(
               "ArtifactToken Relation exists in Working and Parent branch before commit.  Invalid Test. ");
         }
      }
      return parentArtifactToken;
   }

}
