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
package org.eclipse.osee.ats.client.integration.tests;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.PopulateDemoActions;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.TaskStates;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.demo.api.DemoTeam;
import org.eclipse.osee.ats.demo.api.DemoWorkflowTitles;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Test unit for {@link PopulateDemoActions}
 *
 * @author Donald G. Dunne
 */
public class PopulateDemoActionsTest {

   @BeforeClass
   public static void validateDbInit() throws OseeCoreException {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

   @AfterClass
   public static void cleanup() throws OseeCoreException {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Cleanup PopulateDemoActionsTest");
      for (Artifact art : ArtifactQuery.getArtifactListFromName("Auto-created", AtsClientService.get().getAtsBranch(),
         DeletionFlag.EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS)) {
         changes.deleteArtifact(art);
      }
      changes.execute();
   }

   @Before
   public void setup() throws OseeCoreException {
      AtsBulkLoad.reloadConfig(true);
   }

   @org.junit.Test
   public void testAtsAdmin() throws OseeCoreException {
      Assert.assertEquals(DemoUsers.Joe_Smith.getUserId(),
         AtsClientService.get().getUserService().getCurrentUser().getUserId());
      Assert.assertFalse(AtsClientService.get().getUserService().isAtsAdmin());
      Assert.assertTrue(AtsClientService.get().getUserService().isAtsAdmin(
         AtsClientService.get().getUserServiceClient().getUserById(DemoUsers.Jason_Michael.getUserId())));
   }

   @org.junit.Test
   public void testSawUnCommittedTeamWfs() throws OseeCoreException {
      Collection<TeamWorkFlowArtifact> sawUnCommittedTeamWfs = DemoUtil.getSawUnCommittedTeamWfs();
      Assert.assertEquals(4, sawUnCommittedTeamWfs.size());

      TeamWorkFlowArtifact codeTeamArt = DemoUtil.getSawCodeUnCommittedWf();
      Assert.assertNotNull(codeTeamArt);
      TeamWorkFlowArtifact testTeamArt = DemoUtil.getSawTestUnCommittedWf();
      Assert.assertNotNull(testTeamArt);
      TeamWorkFlowArtifact reqTeamArt = DemoUtil.getSawReqUnCommittedWf();
      Assert.assertNotNull(reqTeamArt);
      TeamWorkFlowArtifact designTeamArt = DemoUtil.getSawSWDesignUnCommittedWf();
      Assert.assertNotNull(designTeamArt);

      testTeamContents(codeTeamArt, DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "3",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Code", "Joe Smith",
         DemoArtifactTypes.DemoCodeTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_Code));
      testTeamContents(testTeamArt, DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "3",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Test", "Kay Jones",
         DemoArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_Test));
      testTeamContents(reqTeamArt, DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "3",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements", "Joe Smith",
         DemoArtifactTypes.DemoReqTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_Requirements));
      testTeamContents(designTeamArt, DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "3",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW SW Design", "Kay Jones",
         AtsArtifactTypes.TeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_SW_Design));

      // test code team 1 review and 6 tasks
      //  - test review
      Collection<AbstractReviewArtifact> reviews = ReviewManager.getReviews(codeTeamArt);
      Assert.assertEquals(1, reviews.size());
      PeerToPeerReviewArtifact revArt = (PeerToPeerReviewArtifact) reviews.iterator().next();
      testReviewContents(revArt, "Review new logic", PeerToPeerReviewState.Completed.getName());

      //  - test tasks
      List<String> taskNames = new ArrayList<>();
      taskNames.addAll(DemoTestUtil.getTaskTitles(false));
      for (TaskArtifact task : codeTeamArt.getTaskArtifacts()) {
         testTaskContents(task, TaskStates.InWork.getName(), TeamState.Implement.getName());
         taskNames.remove(task.getName());
         Assert.assertEquals("Joe Smith", task.getStateMgr().getAssigneesStr());
      }
      Assert.assertEquals(
         String.format("Not all tasks exist for [%s]; [%s] remain", codeTeamArt.toStringWithId(), taskNames), 0,
         taskNames.size());

      // test sw_design 1 peer and 1 decision review
      testSwDesign1PeerAnd1DecisionReview(designTeamArt);

   }

   @org.junit.Test
   public void testSawNoBranchCommittedTeamWfs() throws OseeCoreException {
      // {@link DemoDbActionData.getReqSawActionsData()} - 3
      String title = DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW;

      TeamWorkFlowArtifact codeTeamArt = DemoUtil.getSawCodeNoBranchWf();
      Assert.assertNotNull(codeTeamArt);
      TeamWorkFlowArtifact testTeamArt = DemoUtil.getSawTestNoBranchWf();
      Assert.assertNotNull(testTeamArt);
      TeamWorkFlowArtifact reqTeamArt = DemoUtil.getSawReqNoBranchWf();
      Assert.assertNotNull(reqTeamArt);
      TeamWorkFlowArtifact designTeamArt = DemoUtil.getSawSWDesignNoBranchWf();
      Assert.assertNotNull(designTeamArt);

      testTeamContents(codeTeamArt, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Code",
         "Joe Smith", DemoArtifactTypes.DemoCodeTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_Code));
      testTeamContents(testTeamArt, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Test",
         "Kay Jones", DemoArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_Test));
      testTeamContents(reqTeamArt, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements",
         "Joe Smith", DemoArtifactTypes.DemoReqTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_Requirements));
      testTeamContents(designTeamArt, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW SW Design",
         "Kay Jones", AtsArtifactTypes.TeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_SW_Design));

      // test sw_design 1 peer and 1 decision review
      testSwDesign1PeerAnd1DecisionReview(designTeamArt);
   }

   @org.junit.Test
   public void testSawUnCommittedForDiagramViewTeamWfs() throws OseeCoreException {
      // {@link DemoDbActionData.getReqSawActionsData()} - 4
      String title = DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW;
      IAtsTeamWorkflow teamWf = AtsClientService.get().getTeamWf(DemoArtifactToken.SAW_UnCommitedConflicted_Req_TeamWf);
      Assert.assertNotNull(teamWf);

      testTeamContents((TeamWorkFlowArtifact) teamWf.getStoreObject(), title, "3", SAW_Bld_2.getName(),
         TeamState.Implement.getName(), "SAW Requirements", "Joe Smith", DemoArtifactTypes.DemoReqTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoTeam.SAW_Requirements));
   }

   @org.junit.Test
   public void testWorkaroundForGraphViewBld1Action() throws OseeCoreException {
      String title = "Workaround for Graph View for SAW_Bld_1";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "1", SAW_Bld_1.getName(), TeamState.Completed.getName(), "Adapter", "",
         DemoArtifactTypes.DemoReqTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_HW));
   }

   @org.junit.Test
   public void testWorkaroundForGraphViewBld2Action() throws OseeCoreException {
      String title = "Workaround for Graph View for SAW_Bld_2";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "1", SAW_Bld_2.getName(), TeamState.Implement.getName(), "Adapter",
         DemoUsers.Jason_Michael.getName(), DemoArtifactTypes.DemoReqTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoTeam.SAW_HW));
   }

   @org.junit.Test
   public void testWorkaroundForGraphViewBld3Action() throws OseeCoreException {
      String title = "Workaround for Graph View for SAW_Bld_3";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "1", SAW_Bld_3.getName(), TeamState.Implement.getName(), "Adapter",
         DemoUsers.Jason_Michael.getName(), DemoArtifactTypes.DemoReqTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoTeam.SAW_HW));
   }

   @org.junit.Test
   public void testWorkingWithDiagramTreeBld1Action() throws OseeCoreException {
      String title = "Working with Diagram Tree for SAW_Bld_1";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "3", SAW_Bld_1.getName(), TeamState.Completed.getName(), "SAW SW Design", "",
         AtsArtifactTypes.TeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_SW_Design));
   }

   @org.junit.Test
   public void testWorkingWithDiagramTreeBld2Action() throws OseeCoreException {
      String title = "Working with Diagram Tree for SAW_Bld_2";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "3", SAW_Bld_2.getName(), TeamState.Endorse.getName(), "SAW SW Design",
         "Kay Jones", AtsArtifactTypes.TeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_SW_Design));
   }

   @org.junit.Test
   public void testWorkingWithDiagramTreeBld3Action() throws OseeCoreException {
      String title = "Working with Diagram Tree for SAW_Bld_3";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "3", SAW_Bld_3.getName(), TeamState.Endorse.getName(), "SAW SW Design",
         "Kay Jones", AtsArtifactTypes.TeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_SW_Design));
   }

   @org.junit.Test
   public void testButton2DoesntWorkOnHelpAction() throws OseeCoreException {
      String title = "Button S doesn't work on help";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "3", "", TeamState.Completed.getName(), "Reader", "",
         AtsArtifactTypes.TeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.Tools_Team));

      // test decision review
      Collection<AbstractReviewArtifact> reviews = ReviewManager.getReviews(teamArt);
      Assert.assertEquals(1, reviews.size());
      DecisionReviewArtifact revArt = (DecisionReviewArtifact) reviews.iterator().next();
      testReviewContents(revArt, "Is the resolution of this Action valid?", DecisionReviewState.Decision.getName(),
         "Joe Smith");

   }

   @org.junit.Test
   public void testButtonWDoesntWorkOnSituationPageAction() throws OseeCoreException {
      String title = "Button W doesn't work on Situation Page";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "3", "", TeamState.Analyze.getName(), "CIS Test", "Kay Jones",
         DemoArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.CIS_Test));

      // test decision review
      Collection<AbstractReviewArtifact> reviews = ReviewManager.getReviews(teamArt);
      Assert.assertEquals(1, reviews.size());
      DecisionReviewArtifact revArt = (DecisionReviewArtifact) reviews.iterator().next();
      testReviewContents(revArt, "Is the resolution of this Action valid?", DecisionReviewState.Followup.getName(),
         "Joe Smith");

   }

   @org.junit.Test
   public void testCantLoadDiagramTreeAction() throws OseeCoreException {
      String title = "Can't load Diagram Tree";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "3", "", TeamState.Endorse.getName(), "CIS Test", "Kay Jones",
         DemoArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.CIS_Test));
   }

   @org.junit.Test
   public void testCantSeeTheGraphViewAction() throws OseeCoreException {
      String title = "Can't see the Graph View";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "1", "", TeamState.Implement.getName(), "Adapter",
         DemoUsers.Jason_Michael.getName(), DemoArtifactTypes.DemoReqTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoTeam.SAW_HW));

   }

   @org.junit.Test
   public void testProblemInDiagramTreeAction() throws OseeCoreException {
      String title = "Problem in Diagram Tree";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "3", "", TeamState.Endorse.getName(), "CIS Test", "Kay Jones",
         DemoArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.CIS_Test));

   }

   @org.junit.Test
   public void testProblemWithTheGraphViewAction() throws OseeCoreException {
      String title = "Problem with the Graph View";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "1", "", TeamState.Implement.getName(), "Adapter",
         DemoUsers.Jason_Michael.getName(), DemoArtifactTypes.DemoReqTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoTeam.SAW_HW));

   }

   @org.junit.Test
   public void testProblemWithTheUserWindowAction() throws OseeCoreException {
      String title = "Problem with the user window";
      ActionArtifact action = (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, title,
         AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(action);
      Assert.assertEquals(1, action.getTeams().size());
      TeamWorkFlowArtifact teamArt = action.getTeams().iterator().next();

      testTeamContents(teamArt, title, "4", "", TeamState.Implement.getName(), "Timesheet", "Jeffery Kay",
         AtsArtifactTypes.TeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.Tools_Team));

   }

   private static void testReviewContents(AbstractReviewArtifact revArt, String title, String currentStateName, String... assigneeStrs) throws OseeCoreException {
      Assert.assertEquals(title, revArt.getName());
      Assert.assertEquals(currentStateName, revArt.getCurrentStateName());

      Collection<String> assigneeNames = AtsObjects.getNames(revArt.getStateMgr().getAssignees());

      Assert.assertEquals(assigneeNames.size(), assigneeStrs.length);
      for (String assignee : assigneeStrs) {
         if (!assigneeNames.contains(assignee)) {
            Assert.fail(String.format("revArt.getStateMgr().getAssignees(), does not contain user: %s", assignee));
         }
      }
   }

   private static void testTeamContents(TeamWorkFlowArtifact teamArt, String title, String priority, String versionName, String currentStateName, String actionableItemStr, String assigneeStr, IArtifactType artifactType, IAtsTeamDefinition teamDef) throws OseeCoreException {
      Assert.assertEquals(currentStateName, teamArt.getCurrentStateName());
      Assert.assertEquals(priority, teamArt.getSoleAttributeValue(AtsAttributeTypes.PriorityType, ""));
      // want targeted version, not error/exception
      String targetedVerStr = "";
      IAtsVersion version = AtsClientService.get().getVersionService().getTargetedVersion(teamArt);
      if (version != null) {
         targetedVerStr = version.getName();
      }
      Assert.assertEquals(versionName, targetedVerStr);
      Assert.assertEquals(artifactType, teamArt.getArtifactType());
      Assert.assertEquals(teamDef, teamArt.getTeamDefinition());
      Assert.assertEquals(assigneeStr, teamArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(actionableItemStr,
         AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItemsStr(teamArt));
   }

   private void testTaskContents(TaskArtifact task, String currentStateName, String relatedToState) throws OseeCoreException {
      Assert.assertEquals(currentStateName, task.getCurrentStateName());
      Assert.assertEquals(relatedToState, task.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, ""));
   }

   private void testSwDesign1PeerAnd1DecisionReview(TeamWorkFlowArtifact designTeam) throws OseeCoreException {
      Assert.assertNotNull(designTeam);
      PeerToPeerReviewArtifact peerArt = null;
      DecisionReviewArtifact decArt = null;
      for (AbstractReviewArtifact revArt1 : ReviewManager.getReviews(designTeam)) {
         if (revArt1.getName().contains("PeerToPeer")) {
            peerArt = (PeerToPeerReviewArtifact) revArt1;
         } else {
            decArt = (DecisionReviewArtifact) revArt1;
         }
      }
      Assert.assertNotNull(peerArt);
      Assert.assertNotNull(decArt);
      testReviewContents(peerArt,
         "Auto-created Peer Review from ruleId atsAddPeerToPeerReview.test.addPeerToPeerReview.Authorize.None.TransitionTo",
         PeerToPeerReviewState.Prepare.getName(), "UnAssigned");
      testReviewContents(decArt,
         "Auto-created Decision Review from ruleId: atsAddDecisionReview.test.addDecisionReview.Analyze.None.TransitionTo",
         DecisionReviewState.Decision.getName(), "UnAssigned");

   }

}
