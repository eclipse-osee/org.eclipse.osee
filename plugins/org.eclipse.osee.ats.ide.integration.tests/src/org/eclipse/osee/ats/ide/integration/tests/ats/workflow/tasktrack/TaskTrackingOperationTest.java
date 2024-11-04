/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.tasktrack;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.track.TaskTrackItems;
import org.eclipse.osee.ats.api.task.track.TaskTrackingData;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for TaskTrackingOperation
 *
 * @author Donald G. Dunne
 */
public class TaskTrackingOperationTest {

   public static ArtifactToken TaskTrackItemsArt =
      ArtifactToken.valueOf(485234857L, "Task Tracking Items - Demo", COMMON, CoreArtifactTypes.GeneralData);

   @Test
   public void testJson() {

      AtsApiIde atsApi = AtsApiService.get();

      String trackItemsJson =
         OseeInf.getResourceContents("taskTrack/TaskTrackStaticArtExample.json", TaskTrackingOperationTest.class);
      TaskTrackItems trackItems = JsonUtil.readValue(trackItemsJson, TaskTrackItems.class);
      Assert.assertNotNull(trackItems);
      Assert.assertEquals(3, trackItems.getTasks().size());

      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      ArtifactToken artifact = changes.createArtifact(TaskTrackItemsArt);
      changes.addAttribute(artifact, CoreAttributeTypes.GeneralStringData, trackItemsJson);
      TransactionToken tx = changes.execute();
      Assert.assertTrue(tx.isValid());

      String taskTrackingDataJson =
         OseeInf.getResourceContents("taskTrack/TaskTrackingDataExampleCreate.json", TaskTrackingOperationTest.class);
      TaskTrackingData taskTrackingData = JsonUtil.readValue(taskTrackingDataJson, TaskTrackingData.class);
      Assert.assertNotNull(taskTrackingData);
      Assert.assertEquals(2, taskTrackingData.getTrackItems().getTasks().size());

      TaskTrackingData taskTrackingDataResults =
         atsApi.getServerEndpoints().getActionEndpoint().createUpdateTaskTrack(taskTrackingData);
      Assert.assertTrue(taskTrackingDataResults.getResults().toString(),
         taskTrackingDataResults.getResults().isSuccess());

      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(taskTrackingDataResults.getTeamWf());
      Assert.assertEquals("Readiness Reviews for SAW Release", teamWf.getName());
      Assert.assertEquals("Support",
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ChangeType, ""));
      Assert.assertEquals("3",
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""));
      Assert.assertEquals(TeamState.Implement.getName(), teamWf.getCurrentStateName());
      Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks(teamWf);
      Assert.assertEquals(5, tasks.size());
      for (IAtsTask task : tasks) {
         // If one works, they all work.  Just test the first
         if (task.getName().startsWith("01")) {
            Assert.assertEquals(DemoUsers.Joe_Smith, task.getAssignees().iterator().next());
            Assert.assertTrue(task.getDescription().startsWith("Load MIM"));
            Collection<ArtifactToken> supported =
               atsApi.getRelationResolver().getRelated(task, CoreRelationTypes.SupportingInfo_IsSupportedBy);
            ArtifactToken suppArt = supported.iterator().next();
            String atsId =
               atsApi.getAttributeResolver().getSoleAttributeValueAsString(suppArt, AtsAttributeTypes.AtsId, "");
            Assert.assertEquals("TW7", atsId);
         }
      }

      // Prove that a single added task with the 2 existing only creates 1 new one (match by name)
      String taskTrackingDataUpdateJson =
         OseeInf.getResourceContents("taskTrack/TaskTrackingDataExampleUpdate.json", TaskTrackingOperationTest.class);
      TaskTrackingData taskTrackingUpdateData = JsonUtil.readValue(taskTrackingDataUpdateJson, TaskTrackingData.class);
      Assert.assertNotNull(taskTrackingUpdateData);
      Assert.assertEquals(3, taskTrackingUpdateData.getTrackItems().getTasks().size());

      TaskTrackingData taskTrackingDataUpdateResults =
         atsApi.getServerEndpoints().getActionEndpoint().createUpdateTaskTrack(taskTrackingUpdateData);
      Assert.assertTrue(taskTrackingDataUpdateResults.getResults().toString(),
         taskTrackingDataUpdateResults.getResults().isSuccess());

      ((Artifact) teamWf.getStoreObject()).reloadAttributesAndRelations();
      teamWf = atsApi.getWorkItemService().getTeamWf(taskTrackingDataResults.getTeamWf());

      // Check that additional task was added, while others were not duplicated
      tasks = atsApi.getTaskService().getTasks(teamWf);
      Assert.assertEquals(6, tasks.size());
      for (IAtsTask task : tasks) {
         // Test all assignees once and new added task 06
         if (task.getName().startsWith("01")) {
            // Assignee from json REST payload
            Assert.assertEquals(DemoUsers.Joe_Smith, task.getAssignees().iterator().next());
         } else if (task.getName().startsWith("02")) {
            // Assignee from json REST payload
            Assert.assertEquals(DemoUsers.Jason_Michael, task.getAssignees().iterator().next());
         } else if (task.getName().startsWith("03")) {
            // Assignee from json art payload
            Assert.assertEquals(DemoUsers.Joe_Smith, task.getAssignees().iterator().next());
         } else if (task.getName().startsWith("04")) {
            // Assignee from json art payload
            Assert.assertEquals(DemoUsers.Kay_Jones, task.getAssignees().iterator().next());
         } else if (task.getName().startsWith("05")) {
            // No assignee from json art payload, so unassigned
            Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER, task.getAssignees().iterator().next());
         } else if (task.getName().startsWith("06")) {
            // No assignee from json REST payload, so use TW8 assignee
            Assert.assertEquals(DemoUsers.Kay_Jones, task.getAssignees().iterator().next());
            Assert.assertTrue(task.getDescription().equals("Improve report"));
            Collection<ArtifactToken> supported =
               atsApi.getRelationResolver().getRelated(task, CoreRelationTypes.SupportingInfo_IsSupportedBy);
            ArtifactToken suppArt = supported.iterator().next();
            String atsId =
               atsApi.getAttributeResolver().getSoleAttributeValueAsString(suppArt, AtsAttributeTypes.AtsId, "");
            Assert.assertEquals("TW8", atsId);
         }
      }
   }
}
