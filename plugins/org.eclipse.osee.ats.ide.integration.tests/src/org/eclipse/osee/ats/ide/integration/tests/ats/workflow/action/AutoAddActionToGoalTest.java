/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.action;

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test for {@link ActionManager}
 *
 * @author Mark Joy
 */
public class AutoAddActionToGoalTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();

      SkynetTransaction transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         "AutoAddActionToGoalTest - cleanup");
      for (Artifact art : ArtifactQuery.getArtifactListFromName("AutoAddActionToGoalTest",
         AtsClientService.get().getAtsBranch(), DeletionFlag.EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS)) {
         art.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   // Test that no action is added to the Goal
   @org.junit.Test
   public void testNoActionAddedToGoal() {
      AtsTestUtil.cleanupAndReset("AutoAddActionToGoalTest - AutoAddActionToGoalTest - NoAdd");

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      GoalArtifact goalArt = GoalManager.createGoal("AutoAddActionToGoalTest - NoActionAddedGoal", changes);
      changes.execute();
      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should have no memebers", 0, memArt.size());
      ArtifactCache.deCache(goalArt);
   }

   // Create a goal with a relation to an AI and add an Action.  The
   // Team Workflow should be added to the Goal
   @org.junit.Test
   public void testAutoAddActionToGoal_AI() {
      AtsTestUtil.cleanupAndReset("AutoAddActionToGoalTest - AddActionWithAI");

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      GoalArtifact goalArt = GoalManager.createGoal("AutoAddActionToGoalTest - AddActionToGoalFromAI", changes);
      changes.execute();

      changes.clear();
      IAtsTeamDefinition teamDef = AtsTestUtil.getTestTeamDef();

      for (IAtsVersion version : teamDef.getVersions()) {
         changes.deleteArtifact(AtsClientService.get().getQueryService().getArtifact(version));
      }
      changes.execute();

      Artifact testAI2 = AtsClientService.get().getQueryServiceClient().getArtifact(AtsTestUtil.getTestAi2());

      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_ConfigObject, testAI2);

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();

      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should have one memeber from AI", 1, memArt.size());
      Assert.assertTrue("Team Workflow with AI not part of Goal",
         goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf2));

      AtsTestUtil.cleanup();
      testAI2.deleteAndPersist();
      ArtifactCache.deCache(goalArt);
   }

   // Create a goal with a relation to a Team Definition and add an Action.  The
   // Team Workflow should be added to the Goal
   @org.junit.Test
   public void testAutoAddActionToGoal_TeamWF() {
      AtsTestUtil.cleanupAndReset("AutoAddActionToGoalTest - AddActionWithTeamDef");

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      GoalArtifact goalArt = GoalManager.createGoal("AutoAddActionToGoalTest - AddActionToGoalFromTeamDef", changes);
      IAtsTeamDefinition teamDef = AtsTestUtil.getTestTeamDef();
      for (IAtsVersion version : teamDef.getVersions()) {
         changes.deleteArtifact(AtsClientService.get().getQueryService().getArtifact(version));
      }
      changes.execute();

      Artifact teamDefArtifact =
         AtsClientService.get().getQueryServiceClient().getArtifact(AtsTestUtil.getTestTeamDef());
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_ConfigObject, teamDefArtifact);
      goalArt.persist(getClass().getSimpleName() + " - testAutoAddActionToGoal_TeamWF");

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should have one memeber from Team Definition", 1, memArt.size());
      Assert.assertTrue("Team Workflow with TeamDef not part of Goal",
         goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf2));

      AtsTestUtil.cleanup();
      ArtifactCache.deCache(goalArt);
   }

   // Create a Goal with a relation to an AI and Team Definition.  The action should
   // only be added to the goal once.
   @org.junit.Test
   public void testAutoAddActionToGoal_AIandTeamWF() {
      AtsTestUtil.cleanupAndReset("AutoAddActionToGoalTest - AddActionWithAIandTeamDef");

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      IAtsTeamDefinition teamDef = AtsTestUtil.getTestTeamDef();
      for (IAtsVersion version : teamDef.getVersions()) {
         changes.deleteArtifact(AtsClientService.get().getQueryService().getArtifact(version));
      }

      Artifact testAI2Art = AtsClientService.get().getQueryServiceClient().getArtifact(AtsTestUtil.getTestAi2());
      Artifact teamDefArtifact =
         AtsClientService.get().getQueryServiceClient().getArtifact(AtsTestUtil.getTestTeamDef());

      GoalArtifact goalArt =
         GoalManager.createGoal("AutoAddActionToGoalTest - AddActionToGoalFromAIorTeamDef", changes);
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_ConfigObject, testAI2Art);
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_ConfigObject, teamDefArtifact);
      changes.execute();

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should only have one memeber", 1, memArt.size());
      Assert.assertTrue("Team Workflow with AI and TeamDef not part of Goal",
         goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf2));

      AtsTestUtil.cleanup();
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), getClass().getSimpleName());
      testAI2Art.deleteAndPersist(transaction);
      goalArt.deleteAndPersist(transaction);
      transaction.execute();
      ArtifactCache.deCache(goalArt);
   }

   // Create two Goals.  Goal one has relation to two different AIs.  Goal two has a relation to
   // one of those AIs.  Create two Actions and test Goal one has two members and Goal two has
   // only one member.
   @org.junit.Test
   public void testAutoAddActionToGoal_TwoAIsTwoGoals() {
      AtsTestUtil.cleanupAndReset("DecisionReviewManagerTest - UnAssigned");

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      GoalArtifact goalArt = GoalManager.createGoal("AutoAddActionToGoalTest - AddTwoActions", changes);
      GoalArtifact goalArt2 = GoalManager.createGoal("AutoAddActionToGoalTest - SecondGoal", changes);

      IAtsTeamDefinition teamDef = AtsTestUtil.getTestTeamDef();
      for (IAtsVersion version : teamDef.getVersions()) {
         changes.deleteArtifact(AtsClientService.get().getQueryService().getArtifact(version));
      }
      changes.execute();

      Artifact testAI2 = AtsClientService.get().getQueryServiceClient().getArtifact(AtsTestUtil.getTestAi2());
      Artifact testAI3 = AtsClientService.get().getQueryServiceClient().getArtifact(AtsTestUtil.getTestAi3());

      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_ConfigObject, testAI2);
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_ConfigObject, testAI3);
      goalArt2.addRelation(AtsRelationTypes.AutoAddActionToGoal_ConfigObject, testAI2);

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      TeamWorkFlowArtifact teamWf3 = AtsTestUtil.getTeamWf3();
      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should have two memebers", 2, memArt.size());
      Assert.assertTrue("Team Workflow with two memebers of Goal - TeamWf",
         goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf2));
      Assert.assertTrue("Team Workflow with two memebers of Goal - TeamWf2",
         goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf3));
      List<Artifact> memArt2 = goalArt2.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal2 should only have one memeber", 1, memArt2.size());
      Assert.assertTrue("Team Workflow with AI part of Goal2",
         goalArt2.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf2));

      AtsTestUtil.cleanup();

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), getClass().getSimpleName());
      testAI2.deleteAndPersist(transaction);
      testAI3.deleteAndPersist(transaction);
      goalArt.deleteAndPersist(transaction);
      goalArt2.deleteAndPersist(transaction);
      transaction.execute();
   }

}
