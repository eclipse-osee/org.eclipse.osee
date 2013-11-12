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
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.mock.MockActionGroup;
import org.eclipse.osee.ats.core.mock.MockAtsUser;
import org.eclipse.osee.ats.core.mock.MockWorkItem;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @tests ImplementersColumn
 * @author Donald G. Dunne
 */
public class ImplementersColumnTest {

   private final MockAtsUser joe = new MockAtsUser("joe");
   private final MockAtsUser steve = new MockAtsUser("steve");
   private final MockAtsUser alice = new MockAtsUser("alice");

   // @formatter:off
   @Mock private IAtsWorkItem workItem;
   @Mock private IAtsWorkData workData;
   @Mock private IAtsStateManager stateMgr;
   @Mock private IAtsWorkItem workItem2;
   @Mock private IAtsWorkData workData2;
   @Mock private IAtsStateManager stateMgr2;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(workItem.getWorkData()).thenReturn(workData);
      when(workItem.getStateMgr()).thenReturn(stateMgr);

      when(workItem2.getWorkData()).thenReturn(workData2);
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

   @org.junit.Test
   public void testGetImplementersStrFromInWorkWorkflow_blank() throws OseeCoreException {
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr("this"));
   }

   /**
    * Should be blank if in Working state
    */
   @org.junit.Test
   public void testGetImplementersStrFromInWorkWorkflow_workItem() throws OseeCoreException {
      MockWorkItem workItem = new MockWorkItem("this", "InWork", StateType.Working);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   /**
    * Should be blank if in Working state and Assigned
    */
   @org.junit.Test
   public void testGetImplementersStrFromInWorkWorkflow_blankIfAssigned() throws OseeCoreException {
      List<IAtsUser> assigneesToReturn = new ArrayList<IAtsUser>();
      assigneesToReturn.addAll(Arrays.asList(steve, alice));
      when(workData.isCompletedOrCancelled()).thenReturn(false);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   /**
    * Test no completedBy, no completedFromState and no workItem.getImplementers()
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_blankIfNothingToShow() throws OseeCoreException {
      MockWorkItem workItem = new MockWorkItem("this", "Completed", StateType.Completed);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem));

      workItem = new MockWorkItem("this", "Cancelled", StateType.Cancelled);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   /**
    * Test if CompletedBy set
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_completedBySet() throws OseeCoreException {
      MockWorkItem workItem = new MockWorkItem("this", "Completed", StateType.Completed);
      workItem.getWorkData().setCompletedBy(steve);
      Assert.assertEquals("steve", ImplementersColumn.instance.getImplementersStr(workItem));

      workItem = new MockWorkItem("this", "Cancelled", StateType.Cancelled);
      workItem.getWorkData().setCancelledBy(steve);
      Assert.assertEquals("steve", ImplementersColumn.instance.getImplementersStr(workItem));

   }

   /**
    * Test one CompletedBy and assignees from completedFromState
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_completedByAndAssignee() throws OseeCoreException {
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);

      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCompleted()).thenReturn(true);
      when(workData.getCompletedBy()).thenReturn(steve);
      when(workData.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice; steve", ImplementersColumn.instance.getImplementersStr(workItem));

      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCancelled()).thenReturn(true);
      when(workData.getCancelledBy()).thenReturn(steve);
      when(workData.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice; steve", ImplementersColumn.instance.getImplementersStr(workItem));
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
      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCompleted()).thenReturn(true);
      when(workData.getCompletedBy()).thenReturn(steve);
      when(workData.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice; steve", ImplementersColumn.instance.getImplementersStr(workItem));

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCancelled()).thenReturn(true);
      when(workData.getCancelledBy()).thenReturn(steve);
      when(workData.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice; steve", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   /**
    * Test steve as completedBy and completedFrom only registers once in implementersStr
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_duplicatesHandled() throws OseeCoreException {
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);
      implementStateImplementers.add(steve);

      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCompleted()).thenReturn(true);
      when(workData.getCompletedBy()).thenReturn(steve);
      when(workData.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice; steve", ImplementersColumn.instance.getImplementersStr(workItem));

      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCancelled()).thenReturn(true);
      when(workData.getCancelledBy()).thenReturn(steve);
      when(workData.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice; steve", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   /**
    * Test one CompletedBy and assignees from completedFromState and workItem.getImplementers()
    */
   @org.junit.Test
   public void testGetImplementersStrFromCompletedCancelledWorkflow_fromAll() throws OseeCoreException {

      List<IAtsUser> implementersToReturn = new ArrayList<IAtsUser>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCompleted()).thenReturn(true);
      when(workData.getCompletedBy()).thenReturn(steve);
      implementersToReturn.add(joe);
      when(workData.getCompletedFromState()).thenReturn("Implement");
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice; joe; steve", ImplementersColumn.instance.getImplementersStr(workItem));

      implementersToReturn = new ArrayList<IAtsUser>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCancelled()).thenReturn(true);
      when(workData.getCancelledBy()).thenReturn(steve);
      implementersToReturn.add(joe);
      when(workData.getCancelledFromState()).thenReturn("Implement");
      implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice; joe; steve", ImplementersColumn.instance.getImplementersStr(workItem));

   }

   @org.junit.Test
   public void testGetImplementersStrFromCompletedWorkflow_duplicates() throws OseeCoreException {
      List<IAtsUser> implementersToReturn = new ArrayList<IAtsUser>();
      implementersToReturn.add(alice);
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCompleted()).thenReturn(true);
      when(workData.getCompletedBy()).thenReturn(alice);
      when(workData.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice", ImplementersColumn.instance.getImplementersStr(workItem));

      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCancelled()).thenReturn(true);
      when(workData.getCancelledBy()).thenReturn(alice);
      when(workData.getCancelledFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("alice", ImplementersColumn.instance.getImplementersStr(workItem));
   }

   @org.junit.Test
   public void testGetImplementers_fromCompletedCancelledBy_noDuplicatesIfInImplementersAndCompletedBy() throws OseeCoreException {
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);

      MockWorkItem workItem = new MockWorkItem("this", "Completed", StateType.Completed);
      workItem.getWorkData().setCompletedFromState("Implement");
      workItem.getWorkData().setCompletedBy(alice);
      when(workData.getCompletedFromState()).thenReturn("Implement");
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      List<IAtsUser> implementers = new ArrayList<IAtsUser>();
      ImplementersColumn.instance.getImplementers_fromCompletedCancelledBy(workItem, implementers);
      Assert.assertEquals(implementers.iterator().next(), alice);

      workItem = new MockWorkItem("this", "Cancelled", StateType.Cancelled);
      workItem.getWorkData().setCancelledFromState("Implement");
      workItem.getWorkData().setCancelledBy(alice);
      when(workData.getCancelledFromState()).thenReturn("Implement");
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

      MockWorkItem workItem1 = new MockWorkItem("this", "Working", StateType.Working);
      workItem1.getWorkData().setCancelledBy(alice);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem1));

      MockWorkItem workItem2 = new MockWorkItem("that", "Working", StateType.Working);
      workItem2.getWorkData().setCancelledBy(steve);
      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(workItem2));

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem1);
      group.addAction(workItem2);

      Assert.assertEquals("", ImplementersColumn.instance.getImplementersStr(group));

      workItem1.getWorkData().setStateType(StateType.Cancelled);
      workItem1.getWorkData().setCancelledBy(alice);

      workItem2.getWorkData().setStateType(StateType.Cancelled);
      workItem2.getWorkData().setCancelledBy(steve);

      Assert.assertEquals("alice; steve", ImplementersColumn.instance.getImplementersStr(group));
   }

   @org.junit.Test
   public void testGetImplementersFromActionGroup_noDuplicates() throws OseeCoreException {

      MockWorkItem workItem1 = new MockWorkItem("this", "Cancelled", StateType.Cancelled);
      workItem1.getWorkData().setCancelledBy(steve);

      MockWorkItem workItem2 = new MockWorkItem("that", "Cancelled", StateType.Cancelled);
      workItem2.getWorkData().setCancelledBy(steve);

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem1);
      group.addAction(workItem2);

      List<IAtsUser> implementers = ImplementersColumn.instance.getActionGroupImplementers(group);
      Assert.assertEquals(1, implementers.size());
      Assert.assertEquals(steve, implementers.iterator().next());
   }

}
