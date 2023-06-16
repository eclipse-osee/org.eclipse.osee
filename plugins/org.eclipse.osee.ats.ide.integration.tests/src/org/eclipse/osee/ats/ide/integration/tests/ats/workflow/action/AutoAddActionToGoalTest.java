/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.action;

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.junit.Assert;

/**
 * @author Mark Joy
 */
public class AutoAddActionToGoalTest {

   private static String NAME = AutoAddActionToGoalTest.class.getSimpleName();

   // Test that no action is added to the Goal
   @org.junit.Test
   public void testNoActionAddedToGoal() {
      AtsTestUtil.cleanupAndReset(NAME + " - NoAdd", true);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      GoalArtifact goalArt =
         (GoalArtifact) AtsApiService.get().getActionService().createGoal(NAME + " - NoActionAddedGoal", changes);
      changes.execute();
      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should have no memebers", 0, memArt.size());

      ArtifactCache.deCache(goalArt);
      goalArt.deleteAndPersist(NAME);
      AtsTestUtil.cleanup();
   }

   /**
    * Create a goal with a relation to an AI and add an Action. The Team Workflow should be added to the Goal
    */
   @org.junit.Test
   public void testAutoAddActionToGoal_AI() {
      AtsTestUtil.cleanupAndReset(NAME + " - AddActionWithAI", true);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      GoalArtifact goalArt =
         (GoalArtifact) AtsApiService.get().getActionService().createGoal(NAME + " - AddActionToGoalFromAI", changes);
      Artifact testAI2 = AtsApiService.get().getQueryServiceIde().getArtifact(AtsTestUtil.getTestAi2());
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_AtsConfigObject, testAI2);
      changes.execute();

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();

      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should have one memeber from AI", 1, memArt.size());
      Assert.assertTrue("Team Workflow with AI not part of Goal",
         goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf2));

      ArtifactCache.deCache(goalArt);
      goalArt.deleteAndPersist(NAME);
      AtsTestUtil.cleanup();
   }

   /**
    * Create a goal with a relation to a Team Definition and add an Action. The Team Workflow should be added to the
    * Goal
    */
   @org.junit.Test
   public void testAutoAddActionToGoal_TeamWF() {
      AtsTestUtil.cleanupAndReset(NAME + " - AddActionWithTeamDef", true);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      GoalArtifact goalArt =
         (GoalArtifact) AtsApiService.get().getActionService().createGoal(NAME + " - AddActionToGoalFromTeamDef",
            changes);
      changes.execute();

      Artifact teamDefArtifact = AtsApiService.get().getQueryServiceIde().getArtifact(AtsTestUtil.getTestTeamDef());
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_AtsConfigObject, teamDefArtifact);
      goalArt.persist(getClass().getSimpleName() + " - testAutoAddActionToGoal_TeamWF");

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should have one memeber from Team Definition", 1, memArt.size());
      Assert.assertTrue("Team Workflow with TeamDef not part of Goal",
         goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf2));

      ArtifactCache.deCache(goalArt);
      goalArt.deleteAndPersist(NAME);
      AtsTestUtil.cleanup();
   }

   /** Create a Goal with a relation to an AI and Team Definition. The action should only be added to the goal once. */
   @org.junit.Test
   public void testAutoAddActionToGoal_AIandTeamWF() {
      AtsTestUtil.cleanupAndReset(NAME + " - AddActionWithAIandTeamDef", true);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());

      Artifact testAI2Art = AtsApiService.get().getQueryServiceIde().getArtifact(AtsTestUtil.getTestAi2());
      Artifact teamDefArtifact = AtsApiService.get().getQueryServiceIde().getArtifact(AtsTestUtil.getTestTeamDef());

      GoalArtifact goalArt =
         (GoalArtifact) AtsApiService.get().getActionService().createGoal(NAME + " - AddActionToGoalFromAIorTeamDef",
            changes);
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_AtsConfigObject, testAI2Art);
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_AtsConfigObject, teamDefArtifact);
      changes.execute();

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      List<Artifact> memArt = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Goal should only have one memeber", 1, memArt.size());
      Assert.assertTrue("Team Workflow with AI and TeamDef not part of Goal",
         goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member).contains(teamWf2));

      ArtifactCache.deCache(goalArt);
      goalArt.deleteAndPersist(NAME);
      AtsTestUtil.cleanup();
   }

   /**
    * Create two Goals. Goal one has relation to two different AIs. Goal two has a relation to one of those AIs. Create
    * two Actions and test Goal one has two members and Goal two has only one member.
    */
   @org.junit.Test
   public void testAutoAddActionToGoal_TwoAIsTwoGoals() {
      AtsTestUtil.cleanupAndReset(NAME + " - UnAssigned", true);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      GoalArtifact goalArt =
         (GoalArtifact) AtsApiService.get().getActionService().createGoal(NAME + " - AddTwoActions", changes);
      GoalArtifact goalArt2 =
         (GoalArtifact) AtsApiService.get().getActionService().createGoal(NAME + " - SecondGoal", changes);
      Artifact testAI2 = AtsApiService.get().getQueryServiceIde().getArtifact(AtsTestUtil.getTestAi2());
      Artifact testAI3 = AtsApiService.get().getQueryServiceIde().getArtifact(AtsTestUtil.getTestAi3());
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_AtsConfigObject, testAI2);
      goalArt.addRelation(AtsRelationTypes.AutoAddActionToGoal_AtsConfigObject, testAI3);
      goalArt2.addRelation(AtsRelationTypes.AutoAddActionToGoal_AtsConfigObject, testAI2);
      changes.execute();

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

      ArtifactCache.deCache(goalArt);
      ArtifactCache.deCache(goalArt2);
      goalArt.deleteAndPersist(NAME);
      goalArt2.deleteAndPersist(NAME);
      AtsTestUtil.cleanup();
   }

}
