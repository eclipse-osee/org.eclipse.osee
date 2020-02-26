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
package org.eclipse.osee.ats.core.column;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.users.AbstractUserTest;
import org.eclipse.osee.ats.core.workflow.Action;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @tests AssigneeColumn
 * @author Donald G. Dunne
 */
public class AssigneeColumnTest extends AbstractUserTest {

   // @formatter:off
   @Mock private IAtsTeamWorkflow workItem;
   @Mock private IAtsStateManager stateMgr;
   @Mock private IAtsTeamWorkflow workItem2;
   @Mock private IAtsStateManager stateMgr2;
   @Mock private Action action;
   @Mock private AtsApi atsApi;
   @Mock private IAtsUserService userService;
   // @formatter:on

   AssigneeColumn assigneeColumn;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(workItem.getStateMgr()).thenReturn(stateMgr);
      when(workItem2.getStateMgr()).thenReturn(stateMgr2);
      when(atsApi.getUserService()).thenReturn(userService);
      when(action.getTeamWorkflows()).thenReturn(Arrays.asList(workItem, workItem2));
      assigneeColumn = new AssigneeColumn(atsApi);
   }

   @org.junit.Test
   public void testGetAssigneeStr_null() {
      Assert.assertEquals("", assigneeColumn.getAssigneeStr(null));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromInWorkWorkflow() {
      List<AtsUser> assigneesToReturn = new ArrayList<>();
      assigneesToReturn.addAll(Arrays.asList(joe, steve, alice));
      when(stateMgr.getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);

      Assert.assertEquals("joe; steve; alice", assigneeColumn.getAssigneeStr(workItem));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromCompletedWorkflow() {
      List<AtsUser> assigneesToReturn = new ArrayList<>();
      when(stateMgr.getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", assigneeColumn.getAssigneeStr(workItem));

      List<AtsUser> implementersToReturn = new ArrayList<>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(stateMgr.getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      Assert.assertEquals("(steve)", assigneeColumn.getAssigneeStr(workItem));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));

      // add alice as completedFromState assignee
      implementersToReturn.add(steve);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));

      // add duplicate steve as state assigne and ensure doesn't duplicate in string
      implementStateImplementers.add(steve);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromCancelledWorkflow() {
      List<AtsUser> assigneesToReturn = new ArrayList<>();
      when(stateMgr.getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", assigneeColumn.getAssigneeStr(workItem));

      List<AtsUser> implementersToReturn = new ArrayList<>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(stateMgr.getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));

      // add alice as completedFromState assignee
      implementersToReturn.add(steve);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));

      // add duplicate steve as state assigne and ensure doesn't duplicate in string
      implementStateImplementers.add(steve);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_oneWorkingOneCancelled() {

      List<AtsUser> assigneesToReturn = new ArrayList<>();
      when(stateMgr.getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);

      when(stateMgr2.getStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getCancelledFromState()).thenReturn("Implement");
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(joe);
      when(workItem2.getImplementers()).thenReturn(implementStateImplementers);
      Assert.assertEquals("(joe)", assigneeColumn.getAssigneeStr(action));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_duplicateImplementers() {
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getImplementers()).thenReturn(Arrays.asList(joe));
      when(workItem2.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getImplementers()).thenReturn(Arrays.asList(joe));

      Assert.assertEquals("(joe)", assigneeColumn.getAssigneeStr(action));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_twoCancelled() {

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getImplementers()).thenReturn(Arrays.asList(steve));
      when(workItem2.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getImplementers()).thenReturn(Arrays.asList(joe));

      Assert.assertTrue(assigneeColumn.getAssigneeStr(action).contains("joe"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(action).contains("steve"));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_twoWorkingDuplicates() {

      List<AtsUser> assigneesToReturn = new ArrayList<>();
      assigneesToReturn.add(steve);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      when(stateMgr.getStateType()).thenReturn(StateType.Working);

      List<AtsUser> assigneesToReturn2 = new ArrayList<>();
      assigneesToReturn2.add(steve);
      when(workItem2.getAssignees()).thenReturn(assigneesToReturn2);
      when(stateMgr2.getStateType()).thenReturn(StateType.Working);
      Assert.assertEquals("steve", assigneeColumn.getAssigneeStr(action));
   }

   @org.junit.Test
   public void testGetAssigneesStr_invalidImplementersString() {
      AssigneeColumn column = new AssigneeColumn((AtsApi) null);
      when(stateMgr.getStateType()).thenReturn(StateType.Cancelled);
      Assert.assertEquals("", column.getAssigneeStr(workItem));
   }
}