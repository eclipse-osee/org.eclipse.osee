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
package org.eclipse.osee.ats.core.workflow.state;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.internal.state.AtsWorkStateFactory;
import org.eclipse.osee.ats.core.internal.state.StateManager;
import org.eclipse.osee.ats.core.workflow.TestState;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link StateManagerUtility}
 *
 * @author Donald G. Dunne
 */
public class StateManagerUtilityTest {

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   @Mock IAtsChangeSet changes;
   @Mock IAtsWorkDefinition workDef;
   @Mock IAtsStateDefinition analyzeState, completedState;
   @Mock IAttributeResolver attrResolver;
   @Mock IAtsStateManager stateMgr;
   @Mock IAtsWorkStateFactory workStateFactory;
   @Mock IAtsUserService userService;
   @Mock IAtsLogFactory logFactory;
   @Mock IAtsServices services;
   // @formatter:on
   private static final IAtsUser Joe = new AtsUser(DemoUsers.Joe_Smith);
   private static final IAtsUser Kay = new AtsUser(DemoUsers.Kay_Jones);

   @Before
   public void setup()  {
      MockitoAnnotations.initMocks(this);
      when(workItem.getWorkDefinition()).thenReturn(workDef);
      when(workItem.getStateMgr()).thenReturn(stateMgr);

      when(workDef.getStateByName("Analyze")).thenReturn(analyzeState);
      when(analyzeState.getStateType()).thenReturn(StateType.Working);
      when(workDef.getStateByName("Completed")).thenReturn(completedState);
      when(completedState.getStateType()).thenReturn(StateType.Completed);

   }

   @Test
   public void testConstructor() {
      new StateManagerUtility();
   }

   @Test
   public void testInitializeStateMachine() {
      StateManager stateMgr = new StateManager(workItem, logFactory, services);
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());

      state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, null, Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(1, stateMgr.getAssignees().size());
      Assert.assertEquals(Joe, stateMgr.getAssignees().iterator().next());

      state = new TestState("Completed", StateType.Completed);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Completed", stateMgr.getCurrentStateName());
      Assert.assertEquals(0, stateMgr.getAssignees().size());
   }

   @Test
   public void testIsDirtyResult() {
      AtsWorkStateFactory workStateFactory = new AtsWorkStateFactory(userService);

      // test current state new
      when(attrResolver.getAttributeCount(workItem, AtsAttributeTypes.CurrentState)).thenReturn(0);
      Result result = StateManagerUtility.isDirtyResult(workItem, stateMgr, attrResolver, workStateFactory);
      Assert.assertTrue(result.getText().contains("Current State new"));

      // test current state modified
      when(attrResolver.getAttributeCount(workItem, AtsAttributeTypes.CurrentState)).thenReturn(1);
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, null)).thenReturn("this");
      when(stateMgr.getCurrentStateName()).thenReturn("Analyze");
      result = StateManagerUtility.isDirtyResult(workItem, stateMgr, attrResolver, workStateFactory);
      Assert.assertTrue(result.getText().contains("Current State modified"));

      // test no visited states
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, null)).thenReturn("Analyze;;;");
      List<String> emptyVisitedNames = new ArrayList<>();
      when(stateMgr.getVisitedStateNames()).thenReturn(emptyVisitedNames);
      result = StateManagerUtility.isDirtyResult(workItem, stateMgr, attrResolver, workStateFactory);
      Assert.assertFalse(result.isTrue());

      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, null)).thenReturn("Analyze;;;");
      when(stateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("Implement"));
      when(attrResolver.getAttributesToStringList(workItem, AtsAttributeTypes.State)).thenReturn(
         new ArrayList<String>());
      result = StateManagerUtility.isDirtyResult(workItem, stateMgr, attrResolver, workStateFactory);
      Assert.assertTrue(result.getText().contains("State [Implement] added"));

      // test state added
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, null)).thenReturn("Analyze;;;");
      when(stateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("Implement"));
      when(attrResolver.getAttributesToStringList(workItem, AtsAttributeTypes.State)).thenReturn(
         new ArrayList<String>());
      result = StateManagerUtility.isDirtyResult(workItem, stateMgr, attrResolver, workStateFactory);
      Assert.assertTrue(result.getText().contains("State [Implement] added"));

      // test state unmodified
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, null)).thenReturn("Analyze;;;");
      when(stateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("Implement"));
      when(attrResolver.getAttributesToStringList(workItem, AtsAttributeTypes.State)).thenReturn(
         Arrays.asList("Implement;;;"));
      result = StateManagerUtility.isDirtyResult(workItem, stateMgr, attrResolver, workStateFactory);
      Assert.assertFalse(result.isTrue());

      // test state modified
      when(stateMgr.getVisitedStateNames()).thenReturn(Arrays.asList("Analyze", "Implement"));
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, null)).thenReturn("Analyze;;;");
      when(attrResolver.getAttributesToStringList(workItem, AtsAttributeTypes.State)).thenReturn(
         Arrays.asList("Endorse;;;", "Implement;;;"));
      WorkState workState = Mockito.mock(WorkState.class);
      when(stateMgr.getHoursSpent("Implement")).thenReturn(1.3);
      when(stateMgr.getHoursSpentStr("Implement")).thenReturn("1.3");
      when(stateMgr.getState("Implement")).thenReturn(workState);

      result = StateManagerUtility.isDirtyResult(workItem, stateMgr, attrResolver, workStateFactory);
      Assert.assertTrue(
         result.getText().contains("StateManager: State [Implement] modified was [Implement;;;] is [Implement;;1.3;]"));

   }

}
