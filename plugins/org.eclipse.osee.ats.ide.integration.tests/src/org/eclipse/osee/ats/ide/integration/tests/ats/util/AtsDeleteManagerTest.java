/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoActionableItems;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
      cleanup();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.ats.ide.util.AtsDeleteManager#handleDeletePurgeAtsObject(java.util.Collection, org.eclipse.osee.ats.ide.util.AtsDeleteManager.DeleteOption[])}
    * .
    */
   @org.junit.Test
   public void testTeamArtDeleteOneWorkflow() throws Exception {
      // Create Action
      IAtsTeamWorkflow teamWf = createAction(TestNames.TeamArtDeleteOneWorkflow,
         AtsApiService.get().getActionableItemService().getActionableItems(
            Arrays.asList(DemoActionableItems.SAW_Code.getName())));

      // Verify exists
      verifyExists(TestNames.TeamArtDeleteOneWorkflow, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(
         Arrays.asList(AtsApiService.get().getQueryServiceIde().getArtifact(teamWf)), true, DeleteOption.Delete);

      // Verify doesn't exist
      verifyExists(TestNames.TeamArtDeleteOneWorkflow, 0, 0, 0, 0, 0);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.ats.ide.util.AtsDeleteManager#handleDeletePurgeAtsObject(java.util.Collection, org.eclipse.osee.ats.ide.util.AtsDeleteManager.DeleteOption[])}
    * .
    */
   @org.junit.Test
   public void testTeamArtDeleteWithTwoWorkflows() throws Exception {
      IAtsTeamWorkflow teamWf = createAction(TestNames.TeamArtDeleteWithTwoWorkflows,
         AtsApiService.get().getActionableItemService().getActionableItems(
            Arrays.asList(DemoActionableItems.SAW_Code.getName(), DemoActionableItems.SAW_Requirements.getName())));

      // Verify exists
      verifyExists(TestNames.TeamArtDeleteWithTwoWorkflows, 1, 1, 1, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(
         Arrays.asList(AtsApiService.get().getQueryServiceIde().getArtifact(teamWf)), true, DeleteOption.Delete);

      // Verify Action and Req Workflow still exist
      verifyExists(TestNames.TeamArtDeleteWithTwoWorkflows, 1, 0, 1, 0, 0);
   }

   @org.junit.Test
   public void testTeamArtPurge() throws Exception {
      // Create Action
      IAtsTeamWorkflow teamWf =
         createAction(TestNames.TeamArtPurge, AtsApiService.get().getActionableItemService().getActionableItems(
            Arrays.asList(DemoActionableItems.SAW_Code.getName())));

      // Verify exists
      verifyExists(TestNames.TeamArtPurge, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(
         Arrays.asList(AtsApiService.get().getQueryServiceIde().getArtifact(teamWf)), true, DeleteOption.Purge);

      // Verify doesn't exist
      verifyExists(TestNames.TeamArtPurge, 0, 0, 0, 0, 0);
   }

   @org.junit.Test
   public void testActionDelete() throws Exception {
      // Create Action
      IAtsTeamWorkflow teamWf =
         createAction(TestNames.ActionDelete, AtsApiService.get().getActionableItemService().getActionableItems(
            Arrays.asList(DemoActionableItems.SAW_Code.getName())));

      // Verify exists
      verifyExists(TestNames.ActionDelete, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(
         Arrays.asList(AtsApiService.get().getQueryServiceIde().getArtifact(teamWf)), true, DeleteOption.Delete);

      // Verify doesn't exist
      verifyExists(TestNames.ActionDelete, 0, 0, 0, 0, 0);
   }

   @org.junit.Test
   public void testActionPurge() throws Exception {
      // Create Action
      IAtsTeamWorkflow teamWf =
         createAction(TestNames.ActionPurge, AtsApiService.get().getActionableItemService().getActionableItems(
            Arrays.asList(DemoActionableItems.SAW_Code.getName())));

      // Verify exists
      verifyExists(TestNames.ActionPurge, 1, 1, 0, 2, 1);

      // Delete
      AtsDeleteManager.handleDeletePurgeAtsObject(
         Arrays.asList(AtsApiService.get().getQueryServiceIde().getArtifact(teamWf)), true, DeleteOption.Purge);

      // Verify doesn't exist
      verifyExists(TestNames.ActionPurge, 0, 0, 0, 0, 0);
   }

   private void verifyExists(TestNames testName, int expectedNumActions, int expectedNumCodeWorkflows,
      int expectedNumReqWorkflows, int expectedNumTasks, int expectedNumReviews) {
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName(testName.toString(),
         AtsApiService.get().getAtsBranch(), EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS);
      CountingMap<ArtifactTypeToken> countMap = new CountingMap<>();
      for (Artifact artifact : artifacts) {
         countMap.put(artifact.getArtifactType());
      }
      checkExpectedCount(countMap, AtsArtifactTypes.Action, expectedNumActions);
      checkExpectedCount(countMap, AtsArtifactTypes.DemoCodeTeamWorkflow, expectedNumCodeWorkflows);
      checkExpectedCount(countMap, AtsArtifactTypes.DemoReqTeamWorkflow, expectedNumReqWorkflows);
      checkExpectedCount(countMap, AtsArtifactTypes.Task, expectedNumTasks);
      checkExpectedCount(countMap, AtsArtifactTypes.DecisionReview, expectedNumReviews);
   }

   private <T extends Named> void checkExpectedCount(CountingMap<T> map, T key, int expectedCount) {
      int actualCount = map.get(key);
      String message = String.format("%s expected[%s] actual[%s]", key.getName(), expectedCount, actualCount);
      Assert.assertEquals(message, expectedCount, actualCount);
   }

   private IAtsTeamWorkflow createAction(TestNames testName, Collection<IAtsActionableItem> actionableItems) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Delete Manager Test - testActionPurge");

      Date createdDate = new Date();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
      ActionResult result = AtsApiService.get().getActionService().createAction(null, testName.name(), "Description",
         ChangeTypes.Improvement, "2", false, null, actionableItems, createdDate, createdBy, null, changes);

      IAtsTeamWorkflow teamWf = null;
      for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(result)) {
         if (team.getTeamDefinition().getName().contains("Code")) {
            teamWf = team;
         }
      }

      DecisionReviewArtifact decRev =
         (DecisionReviewArtifact) AtsApiService.get().getReviewService().createNewDecisionReview(teamWf,
            ReviewBlockType.None, testName.name(), TeamState.Endorse.getName(), "Description",
            AtsApiService.get().getReviewService().getDefaultDecisionReviewOptions(),
            Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()), createdDate, createdBy,
            changes).getStoreObject();
      changes.add(decRev);

      changes.execute();

      NewTaskData newTaskData =
         NewTaskData.create(teamWf, Arrays.asList(testName.name() + " Task 1", testName.name() + " Task 2"),
            (List<AtsUser>) null, createdDate, createdBy, null, null, null);
      NewTaskSet newTaskSet = NewTaskSet.create(newTaskData, getClass().getSimpleName(), DemoUsers.Joe_Smith);
      newTaskSet = AtsApiService.get().getTaskService().createTasks(newTaskSet);
      if (newTaskSet.isErrors()) {
         throw new OseeStateException("Exception creating tasks [%s]", newTaskSet.getResults().toString());
      }

      return teamWf;

   }

   @AfterClass
   public static void testCleanupPost() throws Exception {
      cleanup();
   }

   private static void cleanup() throws Exception {
      List<String> names = new ArrayList<>();
      for (TestNames testName : TestNames.values()) {
         names.add(testName.name());
      }
      AtsTestUtil.cleanupSimpleTest(names);
   }
}
