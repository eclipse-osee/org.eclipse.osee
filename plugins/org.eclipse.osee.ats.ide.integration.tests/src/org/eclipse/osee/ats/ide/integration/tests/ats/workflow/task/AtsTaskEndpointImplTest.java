/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test unit for AtsTaskEndpointImpl
 *
 * @author Donald G. Dunne
 */
public class AtsTaskEndpointImplTest {

   private AtsTaskEndpointApi taskEp;
   private long taskId1, taskId2, taskId3, taskId4, codeTeamWfId;
   private AtsApiIde client;

   @Before
   public void setup() {
      client = AtsApiService.get();
      taskEp = AtsApiService.get().getServerEndpoints().getTaskEp();
      taskId1 = Lib.generateArtifactIdAsInt();
      taskId2 = Lib.generateArtifactIdAsInt();
      taskId3 = Lib.generateArtifactIdAsInt();
      taskId4 = Lib.generateArtifactIdAsInt();
      codeTeamWfId = DemoUtil.getSawCodeUnCommittedWf().getId();
   }

   @After
   public void cleanup() {
      taskEp.delete(taskId1);
      taskEp.delete(taskId2);
      taskEp.delete(taskId3);
      taskEp.delete(taskId4);
   }

   @Test
   public void testCreateTaskAndRelate() {

      String createdByUserId = DemoUsers.Joe_Smith.getUserId();
      Date createdDate = new Date();
      NewTaskSet newTaskSet = NewTaskSet.createWithData("Create Tasks via - " + getClass().getSimpleName(),
         codeTeamWfId, DemoUsers.Joe_Smith.getUserId());
      NewTaskData newTaskData = newTaskSet.getTaskData();

      // Test add relation where task on A side
      JaxAtsTask newTask = createJaxAtsTask(taskId1, "Task 4", "description", createdByUserId, createdDate, null);
      newTask.setWorkDef(AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString());
      newTaskData.getTasks().add(newTask);
      newTask.addRelation(CoreRelationTypes.SupportingInfo_IsSupportedBy, codeTeamWfId);

      newTaskSet = taskEp.create(newTaskSet);
      Assert.assertEquals(1, newTaskSet.getTaskData().getTasks().size());

      IAtsTask task = (IAtsTask) client.getQueryService().createQuery(WorkItemType.Task).andIds(
         newTask.getId()).getResults().getExactlyOne();
      Collection<IAtsWorkItem> workItems = client.getRelationResolver().getRelated(task,
         CoreRelationTypes.SupportingInfo_IsSupportedBy, IAtsWorkItem.class);
      Assert.assertEquals(1, workItems.size());
      Assert.assertEquals(codeTeamWfId, workItems.iterator().next().getId().longValue());

      IAtsChangeSet changes = client.getStoreService().createAtsChangeSet(getClass().getSimpleName() + " - cleanup",
         AtsCoreUsers.SYSTEM_USER);
      changes.deleteArtifact(task);
      changes.execute();

      workItems = client.getRelationResolver().getRelated(task, CoreRelationTypes.SupportingInfo_IsSupportedBy,
         IAtsWorkItem.class);
      Assert.assertTrue(workItems.isEmpty());

      // Test add relation where task on B side
      newTaskSet = NewTaskSet.createWithData("Create Tasks via - " + getClass().getSimpleName(), codeTeamWfId,
         DemoUsers.Joe_Smith.getUserId());
      newTaskData = newTaskSet.getTaskData();
      newTask = createJaxAtsTask(taskId4, "Task 4", "description", createdByUserId, createdDate, null);
      newTask.setWorkDef(AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString());
      newTaskData.getTasks().add(newTask);
      newTask.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, codeTeamWfId);

      newTaskSet = NewTaskSet.create(newTaskData, "testCreateTaskAndRelate 2", DemoUsers.Joe_Smith.getUserId());
      newTaskSet = taskEp.create(newTaskSet);
      Assert.assertTrue(newTaskSet.getResults().toString(), newTaskSet.isSuccess());
      Assert.assertEquals(1, newTaskSet.getTaskData().getTasks().size());

      task = (IAtsTask) client.getQueryService().createQuery(WorkItemType.Task).andIds(
         newTask.getId()).getResults().getExactlyOne();
      workItems = client.getRelationResolver().getRelated(task, CoreRelationTypes.SupportingInfo_SupportingInfo,
         IAtsWorkItem.class);
      Assert.assertEquals(1, workItems.size());
      Assert.assertEquals(codeTeamWfId, workItems.iterator().next().getId().longValue());

      changes = client.getStoreService().createAtsChangeSet(getClass().getSimpleName() + " - cleanup",
         AtsCoreUsers.SYSTEM_USER);
      changes.deleteArtifact(task);
      changes.execute();

      workItems = client.getRelationResolver().getRelated(task, CoreRelationTypes.SupportingInfo_SupportingInfo,
         IAtsWorkItem.class);
      Assert.assertTrue(workItems.isEmpty());

   }

   @Test
   public void testTaskCrd() {
      // Test Create
      String createdByUserId = DemoUsers.Joe_Smith.getUserId();
      Date createdDate = new Date();
      NewTaskSet newTaskSet = NewTaskSet.createWithData("Create Tasks via - " + getClass().getSimpleName(),
         codeTeamWfId, DemoUsers.Joe_Smith.getUserId());
      NewTaskData newTaskData = newTaskSet.getTaskData();

      JaxAtsTask task = createJaxAtsTask(taskId1, "Task 1", "description", createdByUserId, createdDate, null);
      task.setWorkDef(AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString());
      newTaskData.getTasks().add(task);

      JaxAtsTask task2 = createJaxAtsTask(taskId2, "Task 2", "description", createdByUserId, createdDate, null);
      newTaskData.getTasks().add(task2);

      JaxAtsTask task3 = createJaxAtsTask(taskId3, "Task 3", null, createdByUserId, createdDate,
         Arrays.asList(DemoUsers.Alex_Kay.getUserId()));
      task3.addAttribute(CoreAttributeTypes.StaticId, "my static id");
      newTaskData.getTasks().add(task3);

      newTaskSet = taskEp.create(NewTaskSet.create(newTaskData, "testTaskCrd", DemoUsers.Joe_Smith));
      Assert.assertEquals(3, newTaskSet.getTaskData().getTasks().size());

      // Test Get
      JaxAtsTask task1R = taskEp.get(taskId1);
      Assert.assertNotNull(task1R);
      Assert.assertEquals("Task 1", task1R.getName());
      Assert.assertEquals(taskId1, task1R.getId().longValue());
      Assert.assertEquals(createdByUserId, task1R.getCreatedByUserId());
      Assert.assertEquals("description", task1R.getDescription());
      Assert.assertEquals(true, task1R.isActive());
      String createdDateStr = DateUtil.getDateNow(createdDate, DateUtil.MMDDYYHHMM);
      String taskDateStr = DateUtil.getDateNow(task1R.getCreatedDate(), DateUtil.MMDDYYHHMM);
      Assert.assertEquals(createdDateStr, taskDateStr);
      Assert.assertEquals(1, task1R.getAssigneeUserIds().size());
      Assert.assertEquals(SystemUser.UnAssigned.getUserId(), task1R.getAssigneeUserIds().iterator().next());
      List<JaxAttribute> attributes = task1R.getAttributes();
      // Work Definition should be set
      boolean found = false;
      for (JaxAttribute attr : attributes) {
         if (attr.getAttrType().equals(AtsAttributeTypes.WorkflowDefinitionReference)) {
            found = true;
            Assert.assertEquals("Expected Attribute WorkDefintiion WorkDef_Task_Default",
               AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString(), attr.getValues().iterator().next());
         }
      }
      if (!found) {
         Assert.fail("Attribute WorkDefintiion wasn't found");
      }

      JaxAtsTask task2R = taskEp.get(taskId2);
      Assert.assertNotNull(task2R);
      Assert.assertEquals("Task 2", task2R.getName());
      Assert.assertEquals(taskId2, task2R.getId().longValue());
      Assert.assertEquals(createdByUserId, task2R.getCreatedByUserId());
      Assert.assertEquals("description", task2R.getDescription());
      Assert.assertEquals(true, task2R.isActive());
      Assert.assertEquals(1, task2R.getAssigneeUserIds().size());
      Assert.assertEquals(SystemUser.UnAssigned.getUserId(), task2R.getAssigneeUserIds().iterator().next());
      // Work Definition attribute should NOT be set
      attributes = task2R.getAttributes();
      boolean foundById = false;
      for (JaxAttribute attr : attributes) {
         if (attr.getAttrType().equals(AtsAttributeTypes.WorkflowDefinitionReference)) {
            Assert.assertEquals(AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString(),
               attr.getValues().iterator().next());
            foundById = true;
         }
      }
      Assert.assertTrue(foundById);

      JaxAtsTask task3R = taskEp.get(taskId3);
      Assert.assertNotNull(task3R);
      Assert.assertEquals("Task 3", task3R.getName());
      Assert.assertEquals(taskId3, task3R.getId().longValue());
      Assert.assertEquals(createdByUserId, task2R.getCreatedByUserId());
      Assert.assertEquals("", task3R.getDescription());
      Assert.assertEquals(true, task3R.isActive());
      Assert.assertEquals(1, task3R.getAssigneeUserIds().size());
      Assert.assertEquals(9, task3R.getAttributes().size());
      found = false;
      for (JaxAttribute attribute : task3R.getAttributes()) {
         if (attribute.getAttrType().equals(CoreAttributeTypes.StaticId)) {
            Assert.assertEquals("my static id", attribute.getValues().iterator().next());
            found = true;
            break;
         }
      }
      Assert.assertTrue("Static Id attribute not found", found);
      Assert.assertEquals(DemoUsers.Alex_Kay.getUserId(), task3R.getAssigneeUserIds().iterator().next());

      // Test Delete
      taskEp.delete(taskId1);
      Assert.assertNull(AtsApiService.get().getQueryService().getArtifact(taskId1));
      taskEp.delete(taskId2);
      Assert.assertNull(AtsApiService.get().getQueryService().getArtifact(taskId2));
      taskEp.delete(taskId3);
      Assert.assertNull(AtsApiService.get().getQueryService().getArtifact(taskId3));
   }

   private JaxAtsTask createJaxAtsTask(long taskId, String title, String description, String createdByUserId, Date createdDate, List<String> assigneeUserIds) {
      JaxAtsTask task = new JaxAtsTask();
      task.setId(taskId);
      task.setName(title);
      task.setDescription(description);
      task.setCreatedByUserId(createdByUserId);
      task.setCreatedDate(createdDate);
      if (assigneeUserIds == null || assigneeUserIds.isEmpty()) {
         assigneeUserIds = new ArrayList<>();
         assigneeUserIds.add(SystemUser.UnAssigned.getUserId());
      }
      task.setAssigneeUserIds(assigneeUserIds);
      return task;
   }

}
