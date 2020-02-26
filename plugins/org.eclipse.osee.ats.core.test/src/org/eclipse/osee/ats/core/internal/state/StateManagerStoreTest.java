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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.workflow.TestState;
import org.eclipse.osee.ats.core.workflow.state.StateManagerUtility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link StateManagerStore}
 *
 * @author Donald G. Dunne
 */
public class StateManagerStoreTest {

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   @Mock AtsUser Joe, Kay, asUser;
   @Mock IAtsChangeSet changes;
   @Mock IAtsWorkDefinition workDef;
   @Mock IAtsStateDefinition analyzeState, completedState;
   @Mock IAttributeResolver attrResolver;
   @Mock IAtsStateManager stateMgr;
   @Mock IAtsWorkStateFactory workStateFactory;
   @Mock IAtsUserService userService;
   @Mock AtsNotificationCollector notifications;
   @Mock IAtsLogFactory logFactory;
   @Mock AtsApi atsApi;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(workItem.getWorkDefinition()).thenReturn(workDef);
      when(workItem.getStateMgr()).thenReturn(stateMgr);

      when(workDef.getStateByName("Analyze")).thenReturn(analyzeState);
      when(analyzeState.getStateType()).thenReturn(StateType.Working);
      when(workDef.getStateByName("Completed")).thenReturn(completedState);
      when(completedState.getStateType()).thenReturn(StateType.Completed);

      when(atsApi.getAttributeResolver()).thenReturn(attrResolver);
      when(atsApi.getUserService()).thenReturn(userService);

   }

   @Test
   public void testConstructor() {
      new StateManagerStore();
   }

   @Test
   public void testWriteToStore() {
      StateManager stateMgr = new StateManager(workItem, logFactory, atsApi);
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());
      when(workStateFactory.toStoreStr(stateMgr, "Analyze")).thenReturn("Analyze;<Joe><Kay>;;");
      StateManagerStore.writeToStore(asUser, workItem, stateMgr, attrResolver, changes, workStateFactory);

      verify(attrResolver).setSoleAttributeValue(eq(workItem), eq(AtsAttributeTypes.CurrentState),
         eq("Analyze;<Joe><Kay>;;"), eq(changes));
   }

   @Test
   public void testLoad() {
      StateManager stateMgr = new StateManager(workItem, logFactory, atsApi);
      stateMgr.setCurrentStateName("Analyze");
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "")).thenReturn(
         "Analyze;<Joe><Kay>;;");
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType, null)).thenReturn(
         StateType.Working.name());
      WorkState currentState = new WorkStateImpl("Analyze", Arrays.asList(Joe, Kay), 0, 0);
      when(workStateFactory.fromStoreStr(eq("Analyze;<Joe><Kay>;;"))).thenReturn(currentState);

      StateManagerStore.load(workItem, stateMgr, attrResolver, workStateFactory);

      verify(attrResolver).getSoleAttributeValue(eq(workItem), eq(AtsAttributeTypes.CurrentState), eq(""));
   }

   @Test
   public void testPostPersistNotifyReset() {
      StateManager stateMgr = new StateManager(workItem, logFactory, atsApi);
      stateMgr.setCurrentStateName("Analyze");
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "")).thenReturn(
         "Analyze;<Joe><Kay>;;");
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType, null)).thenReturn(
         StateType.Working.name());
      WorkState currentState = new WorkStateImpl("Analyze", Arrays.asList(Joe, Kay), 0, 0);
      when(workStateFactory.fromStoreStr(eq("Analyze;<Joe><Kay>;;"))).thenReturn(currentState);
      when(changes.getNotifications()).thenReturn(notifications);
      when(workItem.getAtsId()).thenReturn("ATS1234");

      StateManagerStore.postPersistNotifyReset(asUser, workItem, stateMgr, stateMgr.getAssigneesAdded(), attrResolver,
         workStateFactory, changes);

      verify(changes).addWorkItemNotificationEvent(any(AtsWorkItemNotificationEvent.class));
   }

   @Test
   public void testGetPostPersistExecutionListener() {
      StateManager stateMgr = new StateManager(workItem, logFactory, atsApi);
      stateMgr.setCurrentStateName("Analyze");
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());

      when(workItem.getStateMgr()).thenReturn(stateMgr);
      IExecuteListener listener = StateManagerStore.getPostPersistExecutionListener(asUser, workItem, stateMgr,
         stateMgr.getAssigneesAdded(), attrResolver, workStateFactory, changes);

      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "")).thenReturn(
         "Analyze;<Joe><Kay>;;");
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType, null)).thenReturn(
         StateType.Working.name());
      WorkState currentState = new WorkStateImpl("Analyze", Arrays.asList(Joe, Kay), 0, 0);
      when(workStateFactory.fromStoreStr(eq("Analyze;<Joe><Kay>;;"))).thenReturn(currentState);
      when(changes.getNotifications()).thenReturn(notifications);
      when(workItem.getAtsId()).thenReturn("ATS1234");

      listener = StateManagerStore.getPostPersistExecutionListener(asUser, workItem, stateMgr,
         stateMgr.getAssigneesAdded(), attrResolver, workStateFactory, changes);
      listener.changesStored(changes);

      verify(changes).addWorkItemNotificationEvent(any(AtsWorkItemNotificationEvent.class));
   }
}
