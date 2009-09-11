/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.test.cases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsDeleteManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.TaskManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.support.test.util.AtsUserCommunity;
import org.eclipse.osee.support.test.util.DemoActionableItems;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * This test must be run against a demo database. It tests the case where a team workflow or action is deleted or purged
 * and makes sure that all expected related ats objects are deleted also.
 * 
 * @author Donald G. Dunne
 */
public class AtsDeleteManagerTest {

   private enum TestNames {
      TeamArtDeleteOneWorkflow, TeamArtDeleteWithTwoWorkflows, TeamArtPurge, ActionDelete, ActionPurge
   };

   @BeforeClass
   public static void testCleanupPre() throws Exception {
      cleanup();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.ats.util.AtsDeleteManager#handleDeletePurgeAtsObject(java.util.Collection, org.eclipse.osee.ats.util.AtsDeleteManager.DeleteOption[])}
    * .
    */
   @org.junit.Test
   public void testTeamArtDeleteOneWorkflow() throws Exception {
      if (AtsUtil.isProductionDb()) fail("Test not intended for production Db");
      if (DemoTestUtil.isDbPopulatedWithDemoData().isFalse()) fail("Test should be run on Demo Db");

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      // Create Action
      TeamWorkFlowArtifact teamArt =
            createAction(TestNames.TeamArtDeleteOneWorkflow,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())),
                  transaction);
      transaction.execute();

      // Verify exists
      verifyExists(TestNames.TeamArtDeleteOneWorkflow, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), DeleteOption.Delete);

      // Verify doesn't exist
      verifyExists(TestNames.TeamArtDeleteOneWorkflow, 0, 0, 0, 0, 0);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.ats.util.AtsDeleteManager#handleDeletePurgeAtsObject(java.util.Collection, org.eclipse.osee.ats.util.AtsDeleteManager.DeleteOption[])}
    * .
    */
   @org.junit.Test
   public void testTeamArtDeleteWithTwoWorkflows() throws Exception {
      if (AtsUtil.isProductionDb()) fail("Test not intended for production Db");
      if (DemoTestUtil.isDbPopulatedWithDemoData().isFalse()) fail("Test should be run on Demo Db");

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      // Create Action
      TeamWorkFlowArtifact teamArt =
            createAction(TestNames.TeamArtDeleteWithTwoWorkflows,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName(),
                        DemoActionableItems.SAW_Requirements.getName())), transaction);
      transaction.execute();

      // Verify exists
      verifyExists(TestNames.TeamArtDeleteWithTwoWorkflows, 1, 1, 1, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), DeleteOption.Delete);

      // Verify Action and Req Workflow still exist
      verifyExists(TestNames.TeamArtDeleteWithTwoWorkflows, 1, 0, 1, 0, 0);
   }

   @org.junit.Test
   public void testTeamArtPurge() throws Exception {
      if (AtsUtil.isProductionDb()) fail("Test not intended for production Db");
      if (DemoTestUtil.isDbPopulatedWithDemoData().isFalse()) fail("Test should be run on Demo Db");

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      // Create Action
      TeamWorkFlowArtifact teamArt =
            createAction(TestNames.TeamArtPurge,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())),
                  transaction);
      transaction.execute();

      // Verify exists
      verifyExists(TestNames.TeamArtPurge, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), DeleteOption.Purge);

      // Verify doesn't exist
      verifyExists(TestNames.TeamArtPurge, 0, 0, 0, 0, 0);
   }

   @org.junit.Test
   public void testActionDelete() throws Exception {
      if (AtsUtil.isProductionDb()) fail("Test not intended for production Db");
      if (DemoTestUtil.isDbPopulatedWithDemoData().isFalse()) fail("Test should be run on Demo Db");

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      // Create Action
      TeamWorkFlowArtifact teamArt =
            createAction(TestNames.ActionDelete,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())),
                  transaction);
      transaction.execute();

      // Verify exists
      verifyExists(TestNames.ActionDelete, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), DeleteOption.Delete);

      // Verify doesn't exist
      verifyExists(TestNames.ActionDelete, 0, 0, 0, 0, 0);
   }

   @org.junit.Test
   public void testActionPurge() throws Exception {
      if (AtsUtil.isProductionDb()) fail("Test not intended for production Db");
      if (DemoTestUtil.isDbPopulatedWithDemoData().isFalse()) fail("Test should be run on Demo Db");

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
      // Create Action
      TeamWorkFlowArtifact teamArt =
            createAction(TestNames.ActionPurge,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())),
                  transaction);
      transaction.execute();

      // Verify exists
      verifyExists(TestNames.ActionPurge, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), DeleteOption.Purge);

      // Verify doesn't exist
      verifyExists(TestNames.ActionPurge, 0, 0, 0, 0, 0);
   }

   private void verifyExists(TestNames testName, int numActions, int numCodeWorkflows, int numReqWorkflows, int numTasks, int numReviews) throws OseeCoreException {
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName(testName + "%", AtsUtil.getAtsBranch(), false);

      CountingMap<String> numArts = new CountingMap<String>();
      for (Artifact artifact : artifacts) {
         numArts.put(artifact.getArtifactTypeName());
      }
      assertTrue("Should be " + numActions + " ActionArtifact, found " + numArts.get(ActionArtifact.ARTIFACT_NAME),
            numArts.get(ActionArtifact.ARTIFACT_NAME) == numActions);
      assertTrue(
            "Should be " + numCodeWorkflows + " Demo Code Workflow, found " + numArts.get(TestUtil.DEMO_CODE_TEAM_WORKFLOW_ARTIFACT),
            numArts.get(TestUtil.DEMO_CODE_TEAM_WORKFLOW_ARTIFACT) == numCodeWorkflows);
      assertTrue(
            "Should be " + numReqWorkflows + " Demo Req Workflow, found " + numArts.get(TestUtil.DEMO_REQ_TEAM_WORKFLOW_ARTIFACT),
            numArts.get(TestUtil.DEMO_REQ_TEAM_WORKFLOW_ARTIFACT) == numReqWorkflows);
      assertTrue("Should be " + numTasks + " TaskArtifacts, found " + numArts.get(TaskArtifact.ARTIFACT_NAME),
            numArts.get(TaskArtifact.ARTIFACT_NAME) == numTasks);
      assertTrue(
            "Should be " + numReviews + " DecisionReviewArtifact, found " + numArts.get(DecisionReviewArtifact.ARTIFACT_NAME),
            numArts.get(DecisionReviewArtifact.ARTIFACT_NAME) == numReviews);
   }

   private TeamWorkFlowArtifact createAction(TestNames testName, Collection<ActionableItemArtifact> actionableItems, SkynetTransaction transaction) throws OseeCoreException {
      ActionArtifact actionArt =
            ActionManager.createAction(null, testName.name(), "Description", ChangeType.Improvement,
                  PriorityType.Priority_2, Arrays.asList(AtsUserCommunity.Other.name()), false, null, actionableItems,
                  transaction);

      TeamWorkFlowArtifact teamArt = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getName().contains("Code")) {
            teamArt = team;
         }
      }

      TaskManager.createTasks(teamArt, Arrays.asList(testName.name() + "Task 1", testName.name() + "Task 2"), null,
            transaction);

      DecisionReviewArtifact decRev =
            ReviewManager.createNewDecisionReview(teamArt, ReviewBlockType.None, testName.name(),
                  DefaultTeamState.Endorse.name(), "Description", ReviewManager.getDefaultDecisionReviewOptions(),
                  Arrays.asList(UserManager.getUser()));
      decRev.persist(transaction);

      return teamArt;

   }

   @AfterClass
   public static void testCleanupPost() throws Exception {
      cleanup();
   }

   public static void cleanup() throws Exception {
      List<String> names = new ArrayList<String>();
      for (TestNames testName : TestNames.values()) {
         names.add(testName.name());
      }
      DemoTestUtil.cleanupSimpleTest(names);
   }
}
