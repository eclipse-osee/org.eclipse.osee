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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.AbstractUserTest;
import org.eclipse.osee.ats.core.mock.MockActionGroup;
import org.eclipse.osee.ats.core.model.impl.AtsActionGroup;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;

/**
 * @tests AssigneeColumn
 * @author Donald G. Dunne
 */
public class AssigneeColumnTest extends AbstractUserTest {

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
   public void testGetAssigneeStr_null() throws OseeCoreException {
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(null));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromInWorkWorkflow() throws OseeCoreException {
      List<IAtsUser> assigneesToReturn = new ArrayList<>();
      assigneesToReturn.addAll(Arrays.asList(joe, steve, alice));
      when(stateMgr.getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);

      Assert.assertEquals("joe; steve; alice", AssigneeColumn.instance.getAssigneeStr(workItem));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromCompletedWorkflow() throws OseeCoreException {
      List<IAtsUser> assigneesToReturn = new ArrayList<>();
      when(stateMgr.getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(workItem));

      List<IAtsUser> implementersToReturn = new ArrayList<>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(stateMgr.getStateType()).thenReturn(StateType.Completed);
      when(workItem.getCompletedBy()).thenReturn(steve);
      Assert.assertEquals("(steve)", AssigneeColumn.instance.getAssigneeStr(workItem));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));

      // add alice as completedFromState assignee
      implementersToReturn.add(steve);
      when(workItem.getCompletedFromState()).thenReturn("Implement");
      List<IAtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));

      // add duplicate steve as state assigne and ensure doesn't duplicate in string
      implementStateImplementers.add(steve);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromCancelledWorkflow() throws OseeCoreException {
      List<IAtsUser> assigneesToReturn = new ArrayList<>();
      when(stateMgr.getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(workItem));

      List<IAtsUser> implementersToReturn = new ArrayList<>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(stateMgr.getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getCancelledBy()).thenReturn(steve);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));

      // add alice as completedFromState assignee
      implementersToReturn.add(steve);
      when(workItem.getCancelledFromState()).thenReturn("Implement");
      List<IAtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));

      // add duplicate steve as state assigne and ensure doesn't duplicate in string
      implementStateImplementers.add(steve);
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("alice"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(workItem).contains("steve"));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_oneWorkingOneCancelled() throws OseeCoreException {

      List<IAtsUser> assigneesToReturn = new ArrayList<>();
      when(stateMgr.getStateType()).thenReturn(StateType.Working);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);

      when(stateMgr2.getStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getCancelledFromState()).thenReturn("Implement");
      List<IAtsUser> implementStateImplementers = new ArrayList<>();
      implementStateImplementers.add(joe);
      when(workItem2.getImplementers()).thenReturn(implementStateImplementers);

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem);
      group.addAction(workItem2);

      Assert.assertEquals("(joe)", AssigneeColumn.instance.getAssigneeStr(group));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_duplicateImplementers() throws OseeCoreException {

      when(group.getActions()).thenReturn(Arrays.asList(workItem, workItem2));
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getImplementers()).thenReturn(Arrays.asList(joe));
      when(workItem2.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getImplementers()).thenReturn(Arrays.asList(joe));

      Assert.assertEquals("(joe)", AssigneeColumn.instance.getAssigneeStr(group));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_twoCancelled() throws OseeCoreException {

      when(group.getActions()).thenReturn(Arrays.asList(workItem, workItem2));
      when(workItem.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem.getImplementers()).thenReturn(Arrays.asList(steve));
      when(workItem2.getStateMgr().getStateType()).thenReturn(StateType.Cancelled);
      when(workItem2.getImplementers()).thenReturn(Arrays.asList(joe));

      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(group).contains("joe"));
      Assert.assertTrue(AssigneeColumn.instance.getAssigneeStr(group).contains("steve"));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_twoWorkingDuplicates() throws OseeCoreException {

      List<IAtsUser> assigneesToReturn = new ArrayList<>();
      assigneesToReturn.add(steve);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      when(stateMgr.getStateType()).thenReturn(StateType.Working);

      List<IAtsUser> assigneesToReturn2 = new ArrayList<>();
      assigneesToReturn2.add(steve);
      when(workItem2.getAssignees()).thenReturn(assigneesToReturn2);
      when(stateMgr2.getStateType()).thenReturn(StateType.Working);

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem);
      group.addAction(workItem2);

      Assert.assertEquals("steve", AssigneeColumn.instance.getAssigneeStr(group));
   }

   @org.junit.Test
   public void testGetAssigneesStr_invalidImplementersString() throws OseeCoreException {
      AssigneeColumn column = new AssigneeColumn(new ImplementersStringProvider() {

         @Override
         public String getImplementersStr(IAtsObject atsObject) {
            return null;
         }
      });

      when(stateMgr.getStateType()).thenReturn(StateType.Cancelled);
      Assert.assertEquals("", column.getAssigneeStr(workItem));
   }

}
