/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.task.TaskStates;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.junit.After;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class TransitionCompletedCancelledTest {

   private static final String CANCELLATION_REASON = "Cancellation Reason";
   private AtsApi atsApi;
   private IAtsTask task;
   private String title;
   private AtsUser joe;
   private TeamWorkFlowArtifact teamWf;
   private Date createdDate;

   @After
   public void cleanup() {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testHandleTransitionCompletedAndUnCompleted() {

      createTask("Complete-UnComplete Task");

      // Transition to Completed
      transitionTo(TaskStates.Completed);

      // Validate Completed
      validateAttrValue(CoreAttributeTypes.Name, title);
      validateAttrValue(AtsAttributeTypes.WorkflowDefinitionReference,
         AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString());
      validateAttrValue(AtsAttributeTypes.CurrentStateType, StateType.Completed.name());
      validateAttrValue(AtsAttributeTypes.CreatedDate,
         String.valueOf(DateUtil.get(createdDate, DateUtil.YYYY_MM_DD_WITH_DASHES)));
      validateAttrValue(AtsAttributeTypes.CreatedBy, joe.getUserId());
      validateAttrValue(AtsAttributeTypes.State, "InWork;<3333><4444>;;");
      validateAttrValue(AtsAttributeTypes.CurrentState, "Completed;;;");
      validateAttrValue(AtsAttributeTypes.RelatedToState, TeamState.Analyze.getName());
      validateAttrValue(AtsAttributeTypes.CurrentStateName, TeamState.Completed.getName());
      validateAttrValue(AtsAttributeTypes.CurrentStateAssignee, "");
      validateAttrValue(AtsAttributeTypes.CompletedBy, joe.getUserId());
      validateAttrValue(AtsAttributeTypes.CompletedFromState, TaskStates.InWork.getName());
      Date compDate = atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.CompletedDate, null);
      Assert.assertTrue("Completed Date should not be null", compDate != null);

      // Transition back to InWork
      transitionTo(TaskStates.InWork);

      // Validate InWork
      validateAttrValue(CoreAttributeTypes.Name, title);
      validateAttrValue(AtsAttributeTypes.WorkflowDefinitionReference,
         AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString());
      validateAttrValue(AtsAttributeTypes.CurrentStateType, StateType.Working.name());
      validateAttrValue(AtsAttributeTypes.CreatedDate,
         String.valueOf(DateUtil.get(createdDate, DateUtil.YYYY_MM_DD_WITH_DASHES)));
      validateAttrValue(AtsAttributeTypes.CreatedBy, joe.getUserId());
      validateAttrValue(AtsAttributeTypes.State, "Completed;;;");
      validateAttrValue(AtsAttributeTypes.CurrentState, "InWork;<3333>;;");
      validateAttrValue(AtsAttributeTypes.RelatedToState, TeamState.Analyze.getName());
      validateAttrValue(AtsAttributeTypes.CurrentStateName, TaskStates.InWork.getName());
      validateAttrValue(AtsAttributeTypes.CurrentStateAssignee, joe.getIdString());
      validateAttrValue(AtsAttributeTypes.CompletedBy, "");
      validateAttrValue(AtsAttributeTypes.CompletedFromState, "");
      validateAttrValue(AtsAttributeTypes.CompletedDate, "");
   }

   @org.junit.Test
   public void testHandleTransitionCancelledAndUnCancelled() {

      createTask("Cancelled-UnCancelled Task");

      // Transition to Completed
      transitionTo(TaskStates.Cancelled);

      // Validate Completed
      validateAttrValue(CoreAttributeTypes.Name, title);
      validateAttrValue(AtsAttributeTypes.WorkflowDefinitionReference,
         AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString());
      validateAttrValue(AtsAttributeTypes.CurrentStateType, StateType.Cancelled.name());
      validateAttrValue(AtsAttributeTypes.CreatedDate,
         String.valueOf(DateUtil.get(createdDate, DateUtil.YYYY_MM_DD_WITH_DASHES)));
      validateAttrValue(AtsAttributeTypes.CreatedBy, joe.getUserId());
      validateAttrValue(AtsAttributeTypes.State, "InWork;<3333><4444>;;");
      validateAttrValue(AtsAttributeTypes.CurrentState, "Cancelled;;;");
      validateAttrValue(AtsAttributeTypes.RelatedToState, TeamState.Analyze.getName());
      validateAttrValue(AtsAttributeTypes.CurrentStateName, TeamState.Cancelled.getName());
      validateAttrValue(AtsAttributeTypes.CurrentStateAssignee, "");
      validateAttrValue(AtsAttributeTypes.CancelledBy, joe.getUserId());
      validateAttrValue(AtsAttributeTypes.CancelledFromState, TaskStates.InWork.getName());
      validateAttrValue(AtsAttributeTypes.CancelledReason, CANCELLATION_REASON);
      Date compDate = atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.CancelledDate, null);
      Assert.assertTrue("Cancelled Date should not be null", compDate != null);

      // Transition back to InWork
      transitionTo(TaskStates.InWork);

      // Validate InWork
      validateAttrValue(CoreAttributeTypes.Name, title);
      validateAttrValue(AtsAttributeTypes.WorkflowDefinitionReference,
         AtsWorkDefinitionTokens.WorkDef_Task_Default.getIdString());
      validateAttrValue(AtsAttributeTypes.CurrentStateType, StateType.Working.name());
      validateAttrValue(AtsAttributeTypes.CreatedDate,
         String.valueOf(DateUtil.get(createdDate, DateUtil.YYYY_MM_DD_WITH_DASHES)));
      validateAttrValue(AtsAttributeTypes.CreatedBy, joe.getUserId());
      validateAttrValue(AtsAttributeTypes.State, "Cancelled;;;");
      validateAttrValue(AtsAttributeTypes.CurrentState, "InWork;<3333>;;");
      validateAttrValue(AtsAttributeTypes.RelatedToState, TeamState.Analyze.getName());
      validateAttrValue(AtsAttributeTypes.CurrentStateName, TaskStates.InWork.getName());
      validateAttrValue(AtsAttributeTypes.CurrentStateAssignee, joe.getIdString());
      validateAttrValue(AtsAttributeTypes.CancelledBy, "");
      validateAttrValue(AtsAttributeTypes.CancelledFromState, "");
      validateAttrValue(AtsAttributeTypes.CancelledReason, "");
      validateAttrValue(AtsAttributeTypes.CancelledDate, "");
   }

   private void transitionTo(TaskStates toState) {
      TestTransitionData transData = new TestTransitionData(getClass().getSimpleName(), Arrays.asList(task),
         toState.getName(), Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()), null,
         atsApi.createChangeSet(getClass().getSimpleName()), TransitionOption.None);
      if (toState.isCancelled()) {
         transData.setCancellationReason(CANCELLATION_REASON);
      }
      TransitionResults tResult = atsApi.getWorkItemService().transition(transData);
      Assert.assertTrue(tResult.toString(), tResult.isSuccess());
   }

   private void createTask(String title) {
      this.title = title;
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      atsApi = AtsApiService.get();
      teamWf = AtsTestUtil.getTeamWf();
      joe = atsApi.getUserService().getCurrentUser();

      NewTaskSet newTaskSet = NewTaskSet.create(getClass().getSimpleName(), joe);
      NewTaskData newTaskData = NewTaskData.create(newTaskSet, teamWf);
      List<String> assigneeUserIds = new ArrayList<>();
      assigneeUserIds.add(DemoUsers.Joe_Smith.getUserId());
      assigneeUserIds.add(DemoUsers.Kay_Jones.getUserId());
      createdDate = new Date();
      JaxAtsTask jTask = JaxAtsTask.create(newTaskData, title, joe, createdDate);
      jTask.setRelatedToState(teamWf.getCurrentStateName());
      jTask.setAssigneeUserIds(assigneeUserIds);
      newTaskSet = atsApi.getTaskService().createTasks(newTaskSet);
      if (newTaskSet.isErrors()) {
         Assert.fail(newTaskSet.toString());
      }

      task = atsApi.getTaskService().getTask(atsApi.getQueryService().getArtifact(
         newTaskSet.getNewTaskDatas().iterator().next().getTasks().iterator().next().getId()));
   }

   private void validateAttrValue(AttributeTypeToken attrType, String expected) {
      String actual = atsApi.getAttributeResolver().getSoleAttributeValueAsString(task, attrType, "");
      Assert.assertEquals(attrType.toStringWithId(), expected, actual);
   }

}
