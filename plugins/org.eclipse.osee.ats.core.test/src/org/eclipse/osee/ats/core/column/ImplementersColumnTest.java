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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.AbstractUserTest;
import org.eclipse.osee.ats.core.mock.MockWorkItem;
import org.eclipse.osee.ats.core.model.impl.AtsActionGroup;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;

/**
 * @tests ImplementersColumn
 * @author Donald G. Dunne
 */
public class ImplementersColumnTest extends AbstractUserTest {

   // @formatter:off
   @Mock private IAtsWorkItem workItem;
   @Mock private IAtsStateManager stateMgr;
   @Mock private IAtsWorkItem workItem2;
   @Mock private IAtsStateManager stateMgr2;
   @Mock private AtsActionGroup group;
   // @formatter:on

   @Override
   @Before
   public void setup() {
      super.setup();

      when(workItem.getStateMgr()).thenReturn(stateMgr);

      when(workItem2.getStateMgr()).thenReturn(stateMgr2);
   }

   @org.junit.Test
   public void testConstructor() {
      new ImplementersColumn();
   }

   @org.junit.Test
   public void testGetImplementersStrFromInWorkWorkflow_null() throws OseeCoreException {
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(null));
   }

   /**
    * Should be blank if in Working state
    */
   @org.junit.Test
   public void testGetImplementersStrFromInWorkWorkflow_workItem() throws OseeCoreException {
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Working);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   /**
    * Should be blank if in Working state and Assigned
    */
   @org.junit.Test
   public void testGetImplementersStrFromInWorkWorkflow_blankIfAssigned() throws OseeCoreException {
      List<IAtsUser> assigneesToReturn = new ArrayList<IAtsUser>();
      assigneesToReturn.addAll(Arrays.asList(steve, alice));
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   /**
    * Test no completedBy, no completedFromState and no workItem.getImplementers()
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_blankIfNothingToShow() throws OseeCoreException {
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Completed);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem));

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   /**
    * Test if CompletedBy set
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_completedBySet() throws OseeCoreException {
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      Assert.assertEquals("steve", ImplementersColumn.instance.getImplementersStr(workItem));

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      Assert.assertEquals("steve", ImplementersColumn.instance.getImplementersStr(workItem));

   }

   /**
    * Test one CompletedBy and assignees from completedFromState
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_completedByAndAssignee() throws OseeCoreException {
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));
   }

   /**
    * Test one CompletedBy and assignees from completedFromState with unassigned
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_completedByAndAssigneeWithUnassigned() throws OseeCoreException {
      List<IAtsUser> implementersToReturn = new ArrayList<IAtsUser>();
      implementersToReturn.add(alice);
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(AtsCoreUsers.UNASSIGNED_USER);

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));
   }

   /**
    * Test steve as completedBy and completedFrom only registers once in implementersStr
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_duplicatesHandled() throws OseeCoreException {
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);
      implementStateImplementers.add(steve);

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));
   }

   /**
    * Test one CompletedBy and assignees from completedFromState and workItem.getImplementers()
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_fromAll() throws OseeCoreException {

      List<IAtsUser> implementersToReturn = new ArrayList<IAtsUser>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      implementersToReturn.add(joe);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(ImplementersColumn.instance.getImplementersStr(workItem).contains("alice"));
      Assert.assertTrue(ImplementersColumn.instance.getImplementersStr(workItem).contains("joe"));
      Assert.assertTrue(ImplementersColumn.instance.getImplementersStr(workItem).contains("steve"));

      implementersToReturn = new ArrayList<IAtsUser>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      implementersToReturn.add(joe);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(ImplementersColumn.instance.getImplementersStr(workItem).contains("alice"));
      Assert.assertTrue(ImplementersColumn.instance.getImplementersStr(workItem).contains("joe"));
      Assert.assertTrue(ImplementersColumn.instance.getImplementersStr(workItem).contains("steve"));

   }

   @org.junit.Test
   public void testGetImplementersStrFromCompletedWorkflow_duplicates() throws OseeCoreException {
      List<IAtsUser> implementersToReturn = new ArrayList<IAtsUser>();
      implementersToReturn.add(alice);
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(alice);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice", ImplementersColumn.instance.getImplementersStr(workItem));

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(alice);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   @org.junit.Test
   public void testGetImplementers_fromCompletedCancelledBy_noDuplicatesIfInImplementersAndCompletedBy() throws OseeCoreException {
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      when(workItem.getCompletedBy()).thenReturn(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      List<IAtsUser> implementers = new ArrayList<IAtsUser>();
      ImplementersColumn.instance.getImplementers_fromCompletedCancelledBy(workItem, implementers);
      Assert.assertEquals(implementers.iterator().next(), alice);

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(workItem.getCancelledBy()).thenReturn(alice);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      implementers = new ArrayList<IAtsUser>();
      ImplementersColumn.instance.getImplementers_fromCompletedCancelledBy(workItem, implementers);
      Assert.assertEquals(implementers.iterator().next(), alice);
   }

   @org.junit.Test
   public void testGetImplementers_fromWorkItem_noDuplicates() throws OseeCoreException {
      MockWorkItem workItem = new MockWorkItem("this", "Completed", StateType.Completed);
      List<IAtsUser> implementers = new ArrayList<IAtsUser>();
      implementers.add(alice);
      workItem.addImplementer(alice);
      ImplementersColumn.instance.getImplementers_fromWorkItem(workItem, implementers);
      Assert.assertEquals(implementers.iterator().next(), alice);

   }

   @org.junit.Test
   public void testGetImplementersFromActionGroup() throws OseeCoreException {

      when(group.getActions()).thenReturn(Arrays.asList(workItem, workItem2));
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Working);
      when(workItem.getCancelledBy()).thenReturn(alice);
      when(workItem2.getStateMgr().getStateType()).thenReturn(StateType.Working);
      when(workItem2.getCancelledBy()).thenReturn(steve);

      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(group));

      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);

      Assert.assertEquals("alice; steve", ImplementersColumn.instance.getImplementersStr(group));
   }

   @org.junit.Test
   public void testGetImplementersFromActionGroup_noDuplicates() throws OseeCoreException {

      when(group.getActions()).thenReturn(Arrays.asList(workItem, workItem2));
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      when(workItem2.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getCancelledBy()).thenReturn(steve);

      List<IAtsUser> implementers = ImplementersColumn.instance.getActionGroupImplementers(group);
      Assert.assertEquals(1, implementers.size());
      Assert.assertEquals(steve, implementers.iterator().next());
   }

}
