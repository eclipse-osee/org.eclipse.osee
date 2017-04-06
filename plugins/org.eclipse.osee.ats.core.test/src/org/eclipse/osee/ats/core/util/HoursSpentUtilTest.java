/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsReviewServiceProvider;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionDslService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsBranchServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.workflow.state.SimpleTeamState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Megumi Telles
 */
public class HoursSpentUtilTest {

   // @formatter:off
   @Mock IAtsTeamWorkflow teamWf;
   @Mock IAtsTask task1;
   @Mock IAtsTask task2;
   @Mock IAtsTask task3;
   @Mock IAtsAbstractReview pr1;
   @Mock IAtsAbstractReview pr2;
   @Mock IAtsStateManager wfStateMgr;
   @Mock IAtsStateManager taskStateMgr;
   @Mock IAtsStateManager prStateMgr;
   @Mock IAtsWorkItemService workItemService;
   @Mock IAtsTaskService taskService;
   @Mock IAtsUserService userService;
   @Mock IAtsWorkDefinitionDslService workDefService;
   @Mock IAtsWorkItemServiceProvider workItemServiceProvider;
   @Mock IAttributeResolver attrResolver;
   @Mock IAtsBranchServiceProvider branchServiceProvider;
   @Mock IAtsReviewServiceProvider reviewServiceProvider;
   @Mock IAtsServices services;

   // @formatter:on

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      when(teamWf.getStateMgr()).thenReturn(wfStateMgr);
      when(task1.getStateMgr()).thenReturn(taskStateMgr);
      when(task2.getStateMgr()).thenReturn(taskStateMgr);
      when(task3.getStateMgr()).thenReturn(taskStateMgr);
      when(pr1.getStateMgr()).thenReturn(prStateMgr);

      when(wfStateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("Endorse", "Analyze", "Authorize", "Implement"));

      when(wfStateMgr.getHoursSpent("Endorse")).thenReturn(0.0);
      when(wfStateMgr.getHoursSpent("Analyze")).thenReturn(2.0);
      when(wfStateMgr.getHoursSpent("Authorize")).thenReturn(0.0);
      when(wfStateMgr.getHoursSpent("Implement")).thenReturn(18.0);

      when(services.getAttributeResolver()).thenReturn(attrResolver);
      when(services.getUserService()).thenReturn(userService);
      when(services.getTaskService()).thenReturn(taskService);
      when(services.getWorkItemService()).thenReturn(workItemService);

   }

   @Test
   public void testGetHoursSpentTotalWithState() {
      double hours =
         HoursSpentUtil.getHoursSpentTotal(teamWf, new SimpleTeamState("Implement", StateType.Working), services);
      Assert.assertEquals(20.0, hours, 0);
   }

   @Test
   public void testGetHoursSpentTotal() {
      when(taskService.getTasks(teamWf)).thenReturn(Arrays.asList(task1));
      when(workItemService.getReviews(teamWf)).thenReturn(Arrays.asList(pr1));

      when(workItemService.getCurrentState(task1)).thenReturn(new SimpleTeamState("In-Work", StateType.Working));
      when(workItemService.getCurrentState(pr1)).thenReturn(new SimpleTeamState("Complete", StateType.Completed));

      when(taskStateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("In-Work"));
      when(prStateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("In-Work", "Complete"));

      when(taskStateMgr.getHoursSpent("In-Work")).thenReturn(6.0);
      when(prStateMgr.getHoursSpent("In-Work")).thenReturn(5.0);
      when(prStateMgr.getHoursSpent("Complete")).thenReturn(4.0);

      double hours = HoursSpentUtil.getHoursSpentTotal(teamWf, services);
      Assert.assertEquals(35.0, hours, 0);
   }

   @Test
   public void testGetHoursSpentFromTasks() {
      when(taskService.getTasks(teamWf)).thenReturn(Arrays.asList(task1));
      when(workItemService.getCurrentState(task1)).thenReturn(new SimpleTeamState("In-Work", StateType.Working));

      when(taskStateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("In-Work", "Complete"));
      when(taskStateMgr.getHoursSpent("In-Work")).thenReturn(6.0);
      when(taskStateMgr.getHoursSpent("Complete")).thenReturn(4.0);

      double hours = HoursSpentUtil.getHoursSpentFromTasks(teamWf, services);
      Assert.assertEquals(10.0, hours, 0);
   }

   @Test
   public void testGetHoursSpentFromStateTasks() {

      when(taskStateMgr.getHoursSpent("In-Work")).thenReturn(5.0);
      when(taskStateMgr.getHoursSpent("Complete")).thenReturn(1.0);

      when(workItemService.getCurrentState(task2)).thenReturn(new SimpleTeamState("In-Work", StateType.Working));
      when(workItemService.getCurrentState(task3)).thenReturn(new SimpleTeamState("Complete", StateType.Working));

      when(taskStateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("In-Work", "Complete"));

      SimpleTeamState relatedToState = new SimpleTeamState("In-Work", StateType.Working);
      when(taskService.getTasks(teamWf, relatedToState)).thenReturn(Arrays.asList(task2));
      double hours = HoursSpentUtil.getHoursSpentFromStateTasks(teamWf, relatedToState, services);
      Assert.assertEquals(6.0, hours, 0);

      relatedToState = new SimpleTeamState("Complete", StateType.Working);
      when(taskService.getTasks(teamWf, relatedToState)).thenReturn(Arrays.asList(task2, task3));
      hours = HoursSpentUtil.getHoursSpentFromStateTasks(teamWf, relatedToState, services);
      Assert.assertEquals(12.0, hours, 0);

      relatedToState = new SimpleTeamState("Implement", StateType.Working);
      when(taskService.getTasks(teamWf, relatedToState)).thenReturn(new ArrayList<IAtsTask>());
      hours = HoursSpentUtil.getHoursSpentFromStateTasks(teamWf, relatedToState, services);
      Assert.assertEquals(0.0, hours, 0);
   }

   @Test
   public void testGetHoursSpentStateReview() {
      when(prStateMgr.getHoursSpent("In-Work")).thenReturn(2.0);

      SimpleTeamState relatedToState = new SimpleTeamState("In-Work", StateType.Working);
      when(workItemService.getReviews(teamWf, relatedToState)).thenReturn(Arrays.asList(pr1));
      double hours = HoursSpentUtil.getHoursSpentStateReview(teamWf, relatedToState, services);
      Assert.assertEquals(2.0, hours, 0);

      relatedToState = new SimpleTeamState("Implement", StateType.Working);
      when(workItemService.getReviews(teamWf, relatedToState)).thenReturn(new ArrayList<IAtsAbstractReview>());
      hours = HoursSpentUtil.getHoursSpentStateReview(teamWf, relatedToState, services);
      Assert.assertEquals(0.0, hours, 0);

   }

   @Test
   public void testGetHoursSpentReview() {
      when(workItemService.getReviews(teamWf)).thenReturn(Arrays.asList(pr1));
      when(workItemService.getCurrentState(pr1)).thenReturn(new SimpleTeamState("Complete", StateType.Completed));
      when(prStateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("In-Work", "Complete"));
      when(prStateMgr.getHoursSpent("In-Work")).thenReturn(0.0);
      when(prStateMgr.getHoursSpent("Complete")).thenReturn(15.0);

      double hours = HoursSpentUtil.getHoursSpentReview(teamWf, services);
      Assert.assertEquals(15.0, hours, 0);
   }

}
