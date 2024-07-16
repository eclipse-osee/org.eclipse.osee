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

package org.eclipse.osee.ats.core.column;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.users.AbstractUserTest;
import org.eclipse.osee.ats.core.workflow.AtsImplementersService;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @tests ImplementersColumn
 * @author Donald G. Dunne
 */
public class ImplementersColumnTest extends AbstractUserTest {

   // @formatter:off
   @Mock private IAtsTeamWorkflow workItem;
   @Mock private IAtsStateManager stateMgr;
   @Mock private IAtsTeamWorkflow workItem2;
   @Mock private IAtsStateManager stateMgr2;
   @Mock private IAtsAction group;
   @Mock private AtsApi atsApi;
   @Mock private IAtsUserService userService;
   @Mock private IAttributeResolver attributeResolver;
   // @formatter:on

   AtsImplementersService impService;
   AssigneeColumn assigneeColumn;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(atsApi.getUserService()).thenReturn(userService);
      when(workItem.getStateMgr()).thenReturn(stateMgr);
      when(workItem2.getStateMgr()).thenReturn(stateMgr2);
      when(atsApi.getUserService()).thenReturn(userService);
      when(atsApi.getAttributeResolver()).thenReturn(attributeResolver);
      when(group.getTeamWorkflows()).thenReturn(Arrays.asList(workItem, workItem2));

      impService = new AtsImplementersService();
      assigneeColumn = new AssigneeColumn(atsApi);
   }

   public void testGetImplementersStrFromInWorkWorkflow_null() {
      Assert.assertEquals("", impService.getImplementersStr(null));
   }

   /**
    * Should be blank if in Working state
    */
   @org.junit.Test
   public void testGetImplementersStrFromInWorkWorkflow_workItem() {
      when(workItem.getCurrentStateType()).thenReturn(StateType.Working);
      Assert.assertEquals("", impService.getImplementersStr(workItem));
   }

   /**
    * Should be blank if in Working state and Assigned
    */
   @org.junit.Test
   public void testGetImplementersStrFromInWorkWorkflow_blankIfAssigned() {
      List<AtsUser> assigneesToReturn = new ArrayList<>();
      assigneesToReturn.addAll(Arrays.asList(steve, alice));
      when(workItem.getCurrentStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", impService.getImplementersStr(workItem));
   }

   /**
    * Test no completedBy, no completedFromState and no workItem.getImplementers()
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_blankIfNothingToShow() {
      when(workItem.getCurrentStateType()).thenReturn(StateType.Completed);
      Assert.assertEquals("", impService.getImplementersStr(workItem));

      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      Assert.assertEquals("", impService.getImplementersStr(workItem));
   }

   /**
    * Test if CompletedBy set
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_completedBySet() {
      when(workItem.getCurrentStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      Assert.assertEquals("steve", impService.getImplementersStr(workItem));

      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      Assert.assertEquals("steve", impService.getImplementersStr(workItem));

   }

   /**
    * Test one CompletedBy and assignees from completedFromState
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_completedByAndAssignee() {
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);

      when(workItem.getCurrentStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));

      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));
   }

   /**
    * Test one CompletedBy (steve) and assignees (alice, unassigned) from completedFromState
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_completedByAndAssigneeWithUnassigned() {
      when(workItem.isCompleted()).thenReturn(true);
      when(workItem.getCurrentStateType()).thenReturn(StateType.Completed);

      // Add alice and unassigned as Implement assignees
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);
      implementStateImplementers.add(AtsCoreUsers.UNASSIGNED_USER);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);

      // Add steve as completed by
      when(workItem.getCompletedBy()).thenReturn(steve);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      String assigneeStr = assigneeColumn.getAssigneeStr(workItem);

      Assert.assertTrue(assigneeStr.contains("alice"));
      Assert.assertTrue(assigneeStr.contains("steve"));

      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      assigneeStr = assigneeColumn.getAssigneeStr(workItem);
      Assert.assertTrue(assigneeStr.contains("alice"));
      Assert.assertTrue(assigneeStr.contains("steve"));
   }

   /**
    * Test steve as completedBy and completedFrom only registers once in implementersStr
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_duplicatesHandled() {
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);
      implementStateImplementers.add(steve);

      when(workItem.getCurrentStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));

      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(assigneeColumn.getAssigneeStr(workItem).contains("steve"));
   }

   /**
    * Test one CompletedBy and assignees from completedFromState
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_fromAll() {

      // completed by is steve
      when(workItem.getCurrentStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);

      // "completed from state" had assignees alice and joe
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);
      implementStateImplementers.add(joe);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);

      String implementersStr = impService.getImplementersStr(workItem);
      Assert.assertTrue(implementersStr.contains("alice"));
      Assert.assertTrue(implementersStr.contains("joe"));
      Assert.assertTrue(implementersStr.contains("steve"));

      // cancelled by is steve
      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);

      // "completed from state" has assignees joe and alice
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);
      implementStateImplementers.add(joe);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);

      implementersStr = impService.getImplementersStr(workItem);
      Assert.assertTrue(implementersStr.contains("alice"));
      Assert.assertTrue(implementersStr.contains("joe"));
      Assert.assertTrue(implementersStr.contains("steve"));

   }

   @org.junit.Test
   public void testGetImplementersStrFromCompletedWorkflow_duplicates() {
      List<AtsUser> implementersToReturn = new ArrayList<>();
      implementersToReturn.add(alice);
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workItem.getCurrentStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(alice);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice", impService.getImplementersStr(workItem));

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(alice);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice", impService.getImplementersStr(workItem));
   }

   @org.junit.Test
   public void testGetImplementers_fromCompletedCancelledBy_noDuplicatesIfInImplementersAndCompletedBy() {
      List<AtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);

      when(workItem.getCurrentStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(workItem.getCompletedBy()).thenReturn(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      List<AtsUser> implementers = new ArrayList<>();
      impService.getImplementers_fromCompletedCancelledBy(workItem, implementers);
      Assert.assertEquals(implementers.iterator().next(), alice);

      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(workItem.getCancelledBy()).thenReturn(alice);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      implementers = new ArrayList<>();
      impService.getImplementers_fromCompletedCancelledBy(workItem, implementers);
      Assert.assertEquals(implementers.iterator().next(), alice);
   }

   @org.junit.Test
   public void testGetImplementersFromActionGroup() {
      when(workItem.getCurrentStateType()).thenReturn(StateType.Working);
      when(workItem.getCancelledBy()).thenReturn(alice);
      when(workItem2.getCurrentStateType()).thenReturn(StateType.Working);
      when(workItem2.getCancelledBy()).thenReturn(steve);

      Assert.assertEquals("", impService.getImplementersStr(group));

      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getCurrentStateType()).thenReturn(StateType.Cancelled);

      Assert.assertEquals("alice; steve", impService.getImplementersStr(group));
   }

   @org.junit.Test
   public void testGetImplementersFromActionGroup_noDuplicates() {
      when(workItem.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      when(workItem2.getCurrentStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getCancelledBy()).thenReturn(steve);

      List<AtsUser> implementers = impService.getActionGroupImplementers(group);
      Assert.assertEquals(1, implementers.size());
      Assert.assertEquals(steve, implementers.iterator().next());
   }
}