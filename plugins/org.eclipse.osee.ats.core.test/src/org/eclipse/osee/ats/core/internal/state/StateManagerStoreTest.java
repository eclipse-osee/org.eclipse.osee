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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.internal.state.StateManager;
import org.eclipse.osee.ats.core.internal.state.StateManagerStore;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.workflow.TestState;
import org.eclipse.osee.ats.core.workflow.state.StateManagerUtility;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   @Mock IAtsUser Joe, Kay;
   @Mock IAtsChangeSet changes;
   @Mock IAtsWorkDefinition workDef;
   @Mock IAtsStateDefinition analyzeState, completedState;
   @Mock IAttributeResolver attrResolver;
   @Mock IAtsStateManager stateMgr;
   @Mock IAtsWorkStateFactory workStateFactory;
   @Mock IAtsUserService userService;
   @Mock IAtsNotificationService notifyService;
   // @formatter:on

   @Before
   public void setup() throws OseeCoreException {
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
      new StateManagerStore();
   }

   @Test
   public void testWriteToStore() {
      StateManager stateMgr = new StateManager(workItem);
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());
      when(workStateFactory.toStoreStr(stateMgr, "Analyze")).thenReturn("Analyze;<Joe><Kay>;;");
      StateManagerStore.writeToStore(workItem, stateMgr, attrResolver, changes, workStateFactory, notifyService);

      verify(attrResolver).setSoleAttributeValue(eq(workItem), eq(AtsAttributeTypes.CurrentState),
         eq("Analyze;<Joe><Kay>;;"), eq(changes));
   }

   @Test
   public void testLoad() {
      StateManager stateMgr = new StateManager(workItem);
      stateMgr.setCurrentStateName("Analyze");
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "")).thenReturn(
         "Analyze;<Joe><Kay>;;");
      WorkState currentState = new WorkStateImpl("Analyze", Arrays.asList(Joe, Kay), 0, 0);
      when(workStateFactory.fromStoreStr(eq("Analyze;<Joe><Kay>;;"))).thenReturn(currentState);

      StateManagerStore.load(workItem, stateMgr, attrResolver, workStateFactory);

      verify(attrResolver).getSoleAttributeValue(eq(workItem), eq(AtsAttributeTypes.CurrentState), eq(""));
   }

   @Test
   public void testPostPersistNotifyReset() {
      StateManager stateMgr = new StateManager(workItem);
      stateMgr.setCurrentStateName("Analyze");
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "")).thenReturn(
         "Analyze;<Joe><Kay>;;");
      WorkState currentState = new WorkStateImpl("Analyze", Arrays.asList(Joe, Kay), 0, 0);
      when(workStateFactory.fromStoreStr(eq("Analyze;<Joe><Kay>;;"))).thenReturn(currentState);

      StateManagerStore.postPersistNotifyReset(workItem, stateMgr, stateMgr.getAssigneesAdded(), attrResolver,
         workStateFactory, notifyService);

      List<IAtsUser> assigneesAdded = Arrays.asList(Joe, Kay);
      verify(notifyService).notify(workItem, assigneesAdded, AtsNotifyType.Assigned);
   }

   @Test
   public void testGetPostPersistExecutionListener() {
      StateManager stateMgr = new StateManager(workItem);
      stateMgr.setCurrentStateName("Analyze");
      TestState state = new TestState("Analyze", StateType.Working);
      StateManagerUtility.initializeStateMachine(stateMgr, state, Arrays.asList(Joe, Kay), Joe, changes);
      Assert.assertEquals("Analyze", stateMgr.getCurrentStateName());
      Assert.assertEquals(2, stateMgr.getAssignees().size());

      when(workItem.getStateMgr()).thenReturn(stateMgr);
      List<Object> objects = new ArrayList<Object>();
      when(changes.getObjects()).thenReturn(objects);
      IExecuteListener listener =
         StateManagerStore.getPostPersistExecutionListener(workItem, stateMgr, stateMgr.getAssigneesAdded(),
            attrResolver, workStateFactory, notifyService);

      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "")).thenReturn(
         "Analyze;<Joe><Kay>;;");
      WorkState currentState = new WorkStateImpl("Analyze", Arrays.asList(Joe, Kay), 0, 0);
      when(workStateFactory.fromStoreStr(eq("Analyze;<Joe><Kay>;;"))).thenReturn(currentState);

      objects.add(workItem);
      objects.add("now");

      listener =
         StateManagerStore.getPostPersistExecutionListener(workItem, stateMgr, stateMgr.getAssigneesAdded(),
            attrResolver, workStateFactory, notifyService);
      listener.changesStored(changes);

      List<IAtsUser> assigneesAdded = Arrays.asList(Joe, Kay);
      verify(notifyService).notify(workItem, assigneesAdded, AtsNotifyType.Assigned);
   }

}
