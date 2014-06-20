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
package org.eclipse.osee.ats.client.integration.tests.ats.util;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.client.demo.DemoActionableItems;
import org.eclipse.osee.ats.client.demo.DemoArtifactTypes;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.util.AtsDeleteManager;
import org.eclipse.osee.ats.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryOptions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * This test must be run against a demo database. It tests the case where a team workflow or action is deleted or purged
 * and makes sure that all expected related ats objects are deleted also.
 * 
 * @author Donald G. Dunne
 */
public class AtsDeleteManagerTest {

   private enum TestNames {
      TeamArtDeleteOneWorkflow,
      TeamArtDeleteWithTwoWorkflows,
      TeamArtPurge,
      ActionDelete,
      ActionPurge
   };

   @BeforeClass
   public static void testCleanupPre() throws Exception {
      DemoTestUtil.setUpTest();
      cleanup();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.ats.util.AtsDeleteManager#handleDeletePurgeAtsObject(java.util.Collection, org.eclipse.osee.ats.util.AtsDeleteManager.DeleteOption[])}
    * .
    */
   @org.junit.Test
   public void testTeamArtDeleteOneWorkflow() throws Exception {
      AtsChangeSet changes = new AtsChangeSet("Delete Manager Test");
      // Create Action
      TeamWorkFlowArtifact teamArt =
         createAction(TestNames.TeamArtDeleteOneWorkflow,
            ActionableItems.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())), changes);
      changes.execute();

      // Verify exists
      verifyExists(TestNames.TeamArtDeleteOneWorkflow, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), true, DeleteOption.Delete);

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
      AtsChangeSet changes = new AtsChangeSet("Delete Manager Test");
      // Create Action
      TeamWorkFlowArtifact teamArt =
         createAction(TestNames.TeamArtDeleteWithTwoWorkflows, ActionableItems.getActionableItems(Arrays.asList(
            DemoActionableItems.SAW_Code.getName(), DemoActionableItems.SAW_Requirements.getName())), changes);
      changes.execute();

      // Verify exists
      verifyExists(TestNames.TeamArtDeleteWithTwoWorkflows, 1, 1, 1, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), true, DeleteOption.Delete);

      // Verify Action and Req Workflow still exist
      verifyExists(TestNames.TeamArtDeleteWithTwoWorkflows, 1, 0, 1, 0, 0);
   }

   @org.junit.Test
   public void testTeamArtPurge() throws Exception {
      AtsChangeSet changes = new AtsChangeSet("Delete Manager Test");
      // Create Action
      TeamWorkFlowArtifact teamArt =
         createAction(TestNames.TeamArtPurge,
            ActionableItems.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())), changes);
      changes.execute();

      // Verify exists
      verifyExists(TestNames.TeamArtPurge, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), true, DeleteOption.Purge);

      // Verify doesn't exist
      verifyExists(TestNames.TeamArtPurge, 0, 0, 0, 0, 0);
   }

   @org.junit.Test
   public void testActionDelete() throws Exception {
      AtsChangeSet changes = new AtsChangeSet("Delete Manager Test");
      // Create Action
      TeamWorkFlowArtifact teamArt =
         createAction(TestNames.ActionDelete,
            ActionableItems.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())), changes);
      changes.execute();

      // Verify exists
      verifyExists(TestNames.ActionDelete, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), true, DeleteOption.Delete);

      // Verify doesn't exist
      verifyExists(TestNames.ActionDelete, 0, 0, 0, 0, 0);
   }

   @org.junit.Test
   public void testActionPurge() throws Exception {
      AtsChangeSet changes = new AtsChangeSet("Delete Manager Test");
      // Create Action
      TeamWorkFlowArtifact teamArt =
         createAction(TestNames.ActionPurge,
            ActionableItems.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())), changes);
      changes.execute();

      // Verify exists
      verifyExists(TestNames.ActionPurge, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(teamArt), true, DeleteOption.Purge);

      // Verify doesn't exist
      verifyExists(TestNames.ActionPurge, 0, 0, 0, 0, 0);
   }

   private void verifyExists(TestNames testName, int expectedNumActions, int expectedNumCodeWorkflows, int expectedNumReqWorkflows, int expectedNumTasks, int expectedNumReviews) throws OseeCoreException {
      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromName(testName.toString(), AtsUtilCore.getAtsBranch(), EXCLUDE_DELETED,
            QueryOptions.CONTAINS_MATCH_OPTIONS);
      CountingMap<IArtifactType> countMap = new CountingMap<IArtifactType>();
      for (Artifact artifact : artifacts) {
         countMap.put(artifact.getArtifactType());
      }
      checkExpectedCount(countMap, AtsArtifactTypes.Action, expectedNumActions);
      checkExpectedCount(countMap, DemoArtifactTypes.DemoCodeTeamWorkflow, expectedNumCodeWorkflows);
      checkExpectedCount(countMap, DemoArtifactTypes.DemoReqTeamWorkflow, expectedNumReqWorkflows);
      checkExpectedCount(countMap, AtsArtifactTypes.Task, expectedNumTasks);
      checkExpectedCount(countMap, AtsArtifactTypes.DecisionReview, expectedNumReviews);
   }

   private <T extends Named> void checkExpectedCount(CountingMap<T> map, T key, int expectedCount) {
      int actualCount = map.get(key);
      String message = String.format("%s expected[%s] actual[%s]", key.getName(), expectedCount, actualCount);
      Assert.assertEquals(message, expectedCount, actualCount);
   }

   private TeamWorkFlowArtifact createAction(TestNames testName, Collection<IAtsActionableItem> actionableItems, IAtsChangeSet changes) throws OseeCoreException {
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      Artifact actionArt =
         ActionManager.createAction(null, testName.name(), "Description", ChangeType.Improvement, "2", false, null,
            actionableItems, createdDate, createdBy, null, changes);

      TeamWorkFlowArtifact teamArt = null;
      for (TeamWorkFlowArtifact team : ActionManager.getTeams(actionArt)) {
         if (team.getTeamDefinition().getName().contains("Code")) {
            teamArt = team;
         }
      }

      teamArt.createTasks(Arrays.asList(testName.name() + " Task 1", testName.name() + " Task 2"),
         (List<IAtsUser>) null, createdDate, createdBy, changes);

      DecisionReviewArtifact decRev =
         DecisionReviewManager.createNewDecisionReview(teamArt, ReviewBlockType.None, testName.name(),
            TeamState.Endorse.getName(), "Description", DecisionReviewManager.getDefaultDecisionReviewOptions(),
            Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), createdDate, createdBy, changes);
      changes.add(decRev);

      return teamArt;

   }

   @AfterClass
   public static void testCleanupPost() throws Exception {
      cleanup();
   }

   private static void cleanup() throws Exception {
      List<String> names = new ArrayList<String>();
      for (TestNames testName : TestNames.values()) {
         names.add(testName.name());
      }
      AtsTestUtil.cleanupSimpleTest(names);
   }
}
