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
package org.eclipse.osee.ats.core.internal.state;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.core.mock.MockWorkItem;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.users.AbstractUserTest;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Donald G. Dunne
 */
public class StateManagerTest extends AbstractUserTest {

   private StateManager stateMgr;

   // @formatter:off
   @Mock private IAtsWorkDefinition workDef;
   @Mock private IAtsStateDefinition endorseStateDef;
   @Mock private IAtsStateDefinition analyzeStateDef;
   @Mock private IAtsLogFactory logFactory;
   @Mock private AtsApi atsApi;
   // @formatter:on

   @Override
   @Before
   public void setup() {
      super.setup();

      MockWorkItem workItem = Mockito.spy(new MockWorkItem("mock work item", "Endorse", StateType.Working));
      stateMgr = Mockito.spy(new StateManager(workItem, logFactory, atsApi));
      when(workItem.getWorkDefinition()).thenReturn(workDef);
      when(workDef.getStateByName("endorse")).thenReturn(endorseStateDef);
      when(endorseStateDef.getStateType()).thenReturn(StateType.Working);

      when(workDef.getStateByName("analyze")).thenReturn(analyzeStateDef);
      when(analyzeStateDef.getStateType()).thenReturn(StateType.Working);

   }

   @Test
   public void testSetNotificationListener() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignee(steve);

      stateMgr.removeAssignee(steve);

      stateMgr.setAssignee(steve);
      Assert.assertEquals(1, stateMgr.getAssigneesAdded().size());
      Assert.assertEquals(steve, stateMgr.getAssigneesAdded().iterator().next());

   }

   @Test
   public void testAddAssignee() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");

      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
      stateMgr.addAssignees("endorse", null);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());

      List<IAtsUser> users = new ArrayList<>();
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
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");

      stateMgr.addAssignee(AtsCoreUsers.ANONYMOUS_USER);
   }

   @Test
   public void testSetAssignee() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");

      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
      stateMgr.setAssignee("endorse", null);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
      stateMgr.setAssignee(null);
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());

      List<IAtsUser> users = new ArrayList<>();
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
      stateMgr.addState(new WorkStateImpl("endorse"));
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
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      List<IAtsUser> currentAssignees = Arrays.asList(joe, steve);
      stateMgr.setAssignees(currentAssignees);
      stateMgr.getInitialAssignees().addAll(currentAssignees);

      // create next state with no assignees
      stateMgr.addState(new WorkStateImpl("analyze"));

      stateMgr.setAssignees("analyze", currentAssignees);
      Assert.assertTrue(stateMgr.getAssignees().contains(joe));
      Assert.assertTrue(stateMgr.getAssignees().contains(steve));
      Assert.assertEquals("shouldn't notify anyone previously assigned", 0, stateMgr.getAssigneesAdded().size());
   }

   @Test
   public void testSetAssignees_sameStateNotification() {
      // create state with two assignees
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      List<IAtsUser> currentAssignees = Arrays.asList(joe);
      stateMgr.setAssignees(currentAssignees);
      stateMgr.getInitialAssignees().addAll(currentAssignees);

      List<IAtsUser> newAssignees = Arrays.asList(joe, steve);
      stateMgr.setAssignees("endorse", newAssignees);
      Assert.assertTrue(stateMgr.getAssignees().contains(joe));
      Assert.assertTrue(stateMgr.getAssignees().contains(steve));
      Assert.assertEquals("should notify new assignee steve", 1, stateMgr.getAssigneesAdded().size());
   }

   @Test
   public void testAddState_exception() {
      stateMgr.addState(new WorkStateImpl("endorse"));

      stateMgr.addState(new WorkStateImpl("endorse"), false);
   }

   @Test
   public void testAddState_exception2() {
      stateMgr.addState(new WorkStateImpl("endorse"));

      stateMgr.addState("endorse", new LinkedList<IAtsUser>(), 34, 23, false);
   }

   @Test(expected = OseeArgumentException.class)
   public void testSetAssignee_exception() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");

      stateMgr.setAssignee(AtsCoreUsers.ANONYMOUS_USER);
   }

   @Test
   public void testIsUnAssigned() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      Assert.assertFalse(stateMgr.isUnAssigned());
      stateMgr.addAssignee(AtsCoreUsers.UNASSIGNED_USER);
      Assert.assertTrue(stateMgr.isUnAssigned());
   }

   @Test
   public void testIsUnAssignedSolely() {
      stateMgr.addState(new WorkStateImpl("endorse"));
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
      stateMgr.addState(new WorkStateImpl("endorse"));
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
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignees(Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testAddAssignees_state() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesIAtsUser() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignee(joe);
      Assert.assertEquals("joe", stateMgr.getAssigneesStr());
      stateMgr.setAssignee(alice);
      Assert.assertEquals("alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testSetAssigneeStringIAtsUser() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignee("endorse", joe);
      Assert.assertEquals("joe", stateMgr.getAssigneesStr());
      stateMgr.setAssignee("endorse", alice);
      Assert.assertEquals("alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesListOfQextendsIAtsUser() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.addAssignees(Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
      stateMgr.setAssignees(Arrays.asList(steve));
      Assert.assertEquals("steve", stateMgr.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesStringListOfQextendsIAtsUser() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignee("endorse", joe);
      Assert.assertEquals("joe", stateMgr.getAssigneesStr());
      stateMgr.setAssignee("endorse", alice);
      Assert.assertEquals("alice", stateMgr.getAssigneesStr());
   }

   @Test
   public void testRemoveAssigneeIAtsUser() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
      stateMgr.removeAssignee(alice);
      Assert.assertEquals("joe", stateMgr.getAssigneesStr());
   }

   @Test
   public void testClearAssignees() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", stateMgr.getAssigneesStr());
      stateMgr.clearAssignees();
      Assert.assertTrue(stateMgr.getAssignees().isEmpty());
   }

   @Test
   public void testIsStateVisited() {
      Assert.assertFalse(stateMgr.isStateVisited("endorse"));
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      Assert.assertTrue(stateMgr.isStateVisited("endorse"));
      stateMgr.addState(new WorkStateImpl("analyze"));
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
   public void testAddStateStringListOfQextendsIAtsUserDoubleInt() {
      stateMgr.addState("endorse", Arrays.asList(joe), 4.2, 4);
      Assert.assertTrue(stateMgr.isStateVisited("endorse"));
      Assert.assertEquals(1, stateMgr.getVisitedStateNames().size());
      Assert.assertEquals(1, stateMgr.getAssignees("endorse").size());
      Assert.assertEquals(4.2, stateMgr.getHoursSpent("endorse"), 0.0);
      Assert.assertEquals(4, stateMgr.getPercentComplete("endorse"));
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
      Assert.assertEquals(0.0, stateMgr.getHoursSpent("endorse"), 0.0);
      Assert.assertEquals(0, stateMgr.getPercentComplete("endorse"));
   }

   @Test
   public void getPercentComplete() {
      Assert.assertEquals(0, stateMgr.getPercentComplete("endorse"));
   }

   @Test(expected = OseeStateException.class)
   public void setPercentComplete_exception() {
      stateMgr.setPercentComplete("endorse", 34);
   }

   @Test
   public void setPercentComplete() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setPercentComplete("endorse", 34);

      Assert.assertEquals(34, stateMgr.getPercentComplete("endorse"));
   }

   @Test
   public void getHoursSpent() {
      Assert.assertEquals(0.0, stateMgr.getHoursSpent("endorse"), 0.0);
   }

   @Test(expected = OseeStateException.class)
   public void setHoursSpent_exception() {
      stateMgr.setHoursSpent("endorse", 8.0);
   }

   @Test
   public void setHoursSpent() {
      stateMgr.addState(new WorkStateImpl("endorse"));
      stateMgr.setCurrentStateName("endorse");
      stateMgr.setHoursSpent("endorse", 8.0);

      Assert.assertEquals(8.0, stateMgr.getHoursSpent("endorse"), 0.0);
   }

   @Test(expected = OseeStateException.class)
   public void removeAssignee_exception() {
      stateMgr.removeAssignee(joe);
   }

   @Test
   public void testIsHoursEqual() {
      IAtsWorkItem awa = Mockito.mock(IAtsWorkItem.class);
      StateManager mgr = new StateManager(awa, logFactory, atsApi);

      assertTrue(mgr.isHoursEqual(1.0, 1.0));
      assertTrue(mgr.isHoursEqual(01.0, 1.0));
      assertTrue(mgr.isHoursEqual(01.0, 1.000));
      assertTrue(mgr.isHoursEqual(1.0, 1.001));

      assertFalse(mgr.isHoursEqual(1.0, 1.01));
      assertFalse(mgr.isHoursEqual(1.0, -1.001));
      assertFalse(mgr.isHoursEqual(-1.0, 1.01));
      assertFalse(mgr.isHoursEqual(2, 4));
   }

   @Test
   public void testSetMetrics() {
      IAtsWorkItem awa = mock(IAtsWorkItem.class);
      StateManager mgr = new StateManager(awa, logFactory, atsApi);

      IAtsStateDefinition state = mock(IAtsStateDefinition.class);
      when(state.getName()).thenReturn("Endorse");

      List<IAtsUser> assignees = Collections.emptyList();
      mgr.addState("Endorse", assignees);
      mgr.setHoursSpent("Endorse", 1.0);
      mgr.setPercentComplete("Endorse", 46);

      assertFalse(mgr.setMetricsIfChanged(state, 1.0, 46));
      assertFalse(mgr.setMetricsIfChanged(state, 1.001, 46));

      assertTrue(mgr.setMetricsIfChanged(state, 1.1, 46));
      assertTrue(mgr.setMetricsIfChanged(state, 1.0, 47));
   }

}
