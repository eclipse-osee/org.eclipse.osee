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

package org.eclipse.osee.ats.core.internal.state;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.core.mock.MockWorkItem;
import org.eclipse.osee.ats.core.users.AbstractUserTest;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class StateManagerTest extends AbstractUserTest {

   private StateManager stateMgr;

   // @formatter:off
   @Mock private WorkDefinition workDef;
   @Mock private StateDefinition endorseStateDef;
   @Mock private StateDefinition analyzeStateDef;
   @Mock private IAtsLogFactory logFactory;
   @Mock private AtsApi atsApi;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      MockWorkItem workItem = new MockWorkItem("mock work item", "Endorse", workDef, StateType.Working);
      stateMgr = new StateManager(workItem, atsApi);

      when(workDef.getStateByName("endorse")).thenReturn(endorseStateDef);
      when(endorseStateDef.getStateType()).thenReturn(StateType.Working);

      when(workDef.getStateByName("analyze")).thenReturn(analyzeStateDef);
      when(analyzeStateDef.getStateType()).thenReturn(StateType.Working);
   }

   @Test
   public void testSetNotificationListener() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignee(steve);

      stateMgr.removeAssignee(steve);

      stateMgr.setAssignee(steve);
      Assert.assertEquals(1, stateMgr.getAssigneesAdded().size());
      Assert.assertEquals(steve, stateMgr.getAssigneesAdded().iterator().next());

   }

   @Test
   public void testAddAssignee() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");

      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
      stateMgr.addAssignees("endorse", null);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());

      List<AtsUser> users = new ArrayList<>();
      stateMgr.addAssignees("endorse", users);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());

      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
      stateMgr.addAssignee(joe);
      Assert.assertEquals(1, stateMgr.getAssignees().size());

      stateMgr.addAssignee(joe);
      Assert.assertEquals(1, stateMgr.getAssignees().size());

   }

   @Test(expected = OseeArgumentException.class)
   public void testAddAssignee_exception() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");

      stateMgr.addAssignee(AtsCoreUsers.SYSTEM_USER);
   }

   @Test
   public void testSetAssignee() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");

      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
      stateMgr.setAssignee("endorse", null);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
      stateMgr.setAssignee(null);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());

      List<AtsUser> users = new ArrayList<>();
      stateMgr.setAssignees("endorse", users);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());

      stateMgr.setAssignees("endorse", null);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());

      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
      stateMgr.setAssignee(joe);
      Assert.assertEquals(1, stateMgr.getAssignees().size());

      stateMgr.setAssignee(joe);
      Assert.assertEquals(1, stateMgr.getAssignees().size());
   }

   @Test
   public void testSetAssignee_removeUnassigned() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");

      stateMgr.setAssignee(AtsCoreUsers.UNASSIGNED_USER);
      Assert.assertEquals(1, stateMgr.getAssignees().size());
      Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER, stateMgr.getAssignees().iterator().next());

      stateMgr.setAssignees(Arrays.asList(joe, AtsCoreUsers.UNASSIGNED_USER));
      Assert.assertEquals(1, stateMgr.getAssignees().size());
      Assert.assertEquals(joe, stateMgr.getAssignees().iterator().next());
   }

   @Test
   public void testSetAssignees_nextStateNotification() {
      // create state with two assignees
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      List<AtsUser> currentAssignees = Arrays.asList(joe, steve);
      stateMgr.setAssignees(currentAssignees);
      stateMgr.getInitialAssignees().addAll(currentAssignees);

      // create next state with no assignees
      stateMgr.addState(WorkState.create("analyze"));

      stateMgr.setAssignees("analyze", currentAssignees);
      Assert.assertTrue(stateMgr.getAssignees().contains(joe));
      Assert.assertTrue(stateMgr.getAssignees().contains(steve));
      Assert.assertEquals("shouldn't notify anyone previously assigned", 0, stateMgr.getAssigneesAdded().size());
   }

   @Test
   public void testSetAssignees_sameStateNotification() {
      // create state with two assignees
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      List<AtsUser> currentAssignees = Arrays.asList(joe);
      stateMgr.setAssignees(currentAssignees);
      stateMgr.getInitialAssignees().addAll(currentAssignees);

      List<AtsUser> newAssignees = Arrays.asList(joe, steve);
      stateMgr.setAssignees("endorse", newAssignees);
      Assert.assertTrue(stateMgr.getAssignees().contains(joe));
      Assert.assertTrue(stateMgr.getAssignees().contains(steve));
      Assert.assertEquals("should notify new assignee steve", 1, stateMgr.getAssigneesAdded().size());
   }

   @Test
   public void testAddState_exception() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.addState(WorkState.create("endorse"), false);
   }

   @Test
   public void testAddState_exception2() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.addState("endorse", new LinkedList<AtsUser>(), false);
   }

   @Test(expected = OseeArgumentException.class)
   public void testSetAssignee_exception() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");

      stateMgr.setAssignee(AtsCoreUsers.SYSTEM_USER);
   }

   @Test
   public void testIsUnAssigned() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      Assert.assertFalse(stateMgr.isUnAssigned());
      stateMgr.addAssignee(AtsCoreUsers.UNASSIGNED_USER);
      Assert.assertTrue(stateMgr.isUnAssigned());
   }

   @Test
   public void testIsUnAssignedSolely() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      Assert.assertFalse(stateMgr.isUnAssignedSolely());
      stateMgr.addAssignee(AtsCoreUsers.UNASSIGNED_USER);
      Assert.assertTrue(stateMgr.isUnAssignedSolely());
      stateMgr.addAssignee(joe);
      Assert.assertFalse(stateMgr.isUnAssignedSolely());
      stateMgr.removeAssignee(AtsCoreUsers.UNASSIGNED_USER);
      Assert.assertFalse(stateMgr.isUnAssignedSolely());
   }

   @Test
   public void testGetAssigneesStrString() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignee(steve);
      stateMgr.addAssignee(joe);
      Assert.assertEquals("steve; joe", stateMgr.getAssigneesStr());
      Assert.assertEquals("steve; joe", stateMgr.getAssigneesStr("endorse"));
      Assert.assertEquals("", stateMgr.getAssigneesStr("analyze"));
      Assert.assertEquals("stev...", stateMgr.getAssigneesStr("endorse", 5));
      Assert.assertEquals("steve; joe", stateMgr.getAssigneesStr("endorse", 50));
   }

   @Test
   public void testAddAssignees() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignees(Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testAddAssignees_state() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesAtsUser() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignee(joe);
      Assert.assertEquals("joe", stateMgr.getAssigneesStr());
      stateMgr.setAssignee(alice);
      Assert.assertEquals("alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testSetAssigneeStringAtsUser() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignee("endorse", joe);
      Assert.assertEquals("joe", stateMgr.getAssigneesStr());
      stateMgr.setAssignee("endorse", alice);
      Assert.assertEquals("alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesListOfQextendsAtsUser() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignees(Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
      stateMgr.setAssignees(Arrays.asList(steve));
      Assert.assertEquals("steve", stateMgr.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesStringListOfQextendsAtsUser() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignee("endorse", joe);
      Assert.assertEquals("joe", stateMgr.getAssigneesStr());
      stateMgr.setAssignee("endorse", alice);
      Assert.assertEquals("alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testRemoveAssigneeAtsUser() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
      stateMgr.removeAssignee(alice);
      Assert.assertEquals("joe", stateMgr.getAssigneesStr());
   }

   @Test
   public void testClearAssignees() {
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
      stateMgr.clearAssignees();
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
   }

   @Test
   public void testIsStateVisited() {
      Assert.assertFalse(stateMgr.isStateVisited("endorse"));
      stateMgr.addState(WorkState.create("endorse"));
      stateMgr.setCurrentStateName("endorse");
      Assert.assertTrue(stateMgr.isStateVisited("endorse"));
      stateMgr.addState(WorkState.create("analyze"));
      Assert.assertTrue(stateMgr.isStateVisited("analyze"));

   }

   @Test
   public void testCreateState() {
      stateMgr.createState("endorse");
      Assert.assertTrue(stateMgr.isStateVisited("endorse"));
      stateMgr.createState("endorse");
      Assert.assertEquals(1, stateMgr.getVisitedStateNames().size());
   }

   @Test
   public void testAddStateStringListOfQextendsAtsUserDoubleInt() {
      stateMgr.addState("endorse", Arrays.asList(joe));
      Assert.assertTrue(stateMgr.isStateVisited("endorse"));
      Assert.assertEquals(1, stateMgr.getVisitedStateNames().size());
      Assert.assertEquals(1, stateMgr.getAssignees("endorse").size());
   }

   @Test(expected = OseeStateException.class)
   public void testGetAssigneesForState() {
      stateMgr.getAssignees();
   }

   @Test
   public void testAddStateWorkState() {
      stateMgr.addState("endorse", Arrays.asList(joe));
      Assert.assertTrue(stateMgr.isStateVisited("endorse"));
      Assert.assertEquals(1, stateMgr.getVisitedStateNames().size());
      Assert.assertEquals(1, stateMgr.getAssignees("endorse").size());
   }

   @Test(expected = OseeStateException.class)
   public void removeAssignee_exception() {
      stateMgr.removeAssignee(joe);
   }

}
