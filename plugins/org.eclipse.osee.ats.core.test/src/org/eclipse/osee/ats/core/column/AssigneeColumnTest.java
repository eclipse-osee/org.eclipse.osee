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
import org.eclipse.osee.ats.api.workflow.HasWorkData;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.mock.MockActionGroup;
import org.eclipse.osee.ats.core.mock.MockAtsUser;
import org.eclipse.osee.ats.core.mock.MockWorkData;
import org.eclipse.osee.ats.core.mock.MockWorkItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @tests AssigneeColumn
 * @author Donald G. Dunne
 */
public class AssigneeColumnTest {

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
   public void testGetAssigneeStr_null() throws OseeCoreException {
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(null));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromInWorkWorkflow() throws OseeCoreException {
      List<IAtsUser> assigneesToReturn = new ArrayList<IAtsUser>();
      assigneesToReturn.addAll(Arrays.asList(joe, steve, alice));
      when(workData.isCompletedOrCancelled()).thenReturn(false);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);

      Assert.assertEquals("joe; steve; alice", AssigneeColumn.instance.getAssigneeStr(workItem));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromCompletedWorkflow() throws OseeCoreException {
      List<IAtsUser> assigneesToReturn = new ArrayList<IAtsUser>();
      when(workData.isCompletedOrCancelled()).thenReturn(false);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(workItem));

      List<IAtsUser> implementersToReturn = new ArrayList<IAtsUser>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCompleted()).thenReturn(true);
      when(workData.getCompletedBy()).thenReturn(steve);
      Assert.assertEquals("(steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

      // add alice as completedFromState assignee
      implementersToReturn.add(steve);
      when(workData.getCompletedFromState()).thenReturn("Implement");
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("(alice; steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

      // add duplicate steve as state assigne and ensure doesn't duplicate in string
      implementStateImplementers.add(steve);
      Assert.assertEquals("(alice; steve)", AssigneeColumn.instance.getAssigneeStr(workItem));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromCancelledWorkflow() throws OseeCoreException {
      List<IAtsUser> assigneesToReturn = new ArrayList<IAtsUser>();
      when(workData.isCompletedOrCancelled()).thenReturn(false);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(workItem));

      List<IAtsUser> implementersToReturn = new ArrayList<IAtsUser>();
      when(workItem.getImplementers()).thenReturn(implementersToReturn);
      when(workData.isCompletedOrCancelled()).thenReturn(true);
      when(workData.isCancelled()).thenReturn(true);
      when(workData.getCancelledBy()).thenReturn(steve);
      Assert.assertEquals("(steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

      // add alice as completedFromState assignee
      implementersToReturn.add(steve);
      when(workData.getCancelledFromState()).thenReturn("Implement");
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(alice);
      when(stateMgr.getAssigneesForState("Implement")).thenReturn(implementStateImplementers);
      Assert.assertEquals("(alice; steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

      // add duplicate steve as state assigne and ensure doesn't duplicate in string
      implementStateImplementers.add(steve);
      Assert.assertEquals("(alice; steve)", AssigneeColumn.instance.getAssigneeStr(workItem));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_oneWorkingOneCancelled() throws OseeCoreException {

      List<IAtsUser> assigneesToReturn = new ArrayList<IAtsUser>();
      when(workData.isCompletedOrCancelled()).thenReturn(false);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);

      when(workData2.isCompletedOrCancelled()).thenReturn(true);
      when(workData2.isCancelled()).thenReturn(true);
      when(workData2.getCancelledFromState()).thenReturn("Implement");
      List<IAtsUser> implementStateImplementers = new ArrayList<IAtsUser>();
      implementStateImplementers.add(joe);
      when(workItem2.getImplementers()).thenReturn(implementStateImplementers);

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem);
      group.addAction(workItem2);

      Assert.assertEquals("(joe)", AssigneeColumn.instance.getAssigneeStr(group));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_duplicateImplementers() throws OseeCoreException {

      MockWorkItem workItem1 = new MockWorkItem("this", "Cancelled", StateType.Cancelled);
      workItem1.addImplementer(joe);

      MockWorkItem workItem2 = new MockWorkItem("that", "Cancelled", StateType.Cancelled);
      workItem2.addImplementer(joe);

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem1);
      group.addAction(workItem2);

      Assert.assertEquals("(joe)", AssigneeColumn.instance.getAssigneeStr(group));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_twoCancelled() throws OseeCoreException {

      MockWorkItem workItem1 = new MockWorkItem("this", "Cancelled", StateType.Cancelled);
      workItem1.addImplementer(steve);

      MockWorkItem workItem2 = new MockWorkItem("that", "Cancelled", StateType.Cancelled);
      workItem2.addImplementer(joe);

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem1);
      group.addAction(workItem2);

      Assert.assertEquals("(joe; steve)", AssigneeColumn.instance.getAssigneeStr(group));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_twoWorkingDuplicates() throws OseeCoreException {

      List<IAtsUser> assigneesToReturn = new ArrayList<IAtsUser>();
      assigneesToReturn.add(steve);
      when(workItem.getAssignees()).thenReturn(assigneesToReturn);
      when(workData.isCompletedOrCancelled()).thenReturn(false);

      List<IAtsUser> assigneesToReturn2 = new ArrayList<IAtsUser>();
      assigneesToReturn2.add(steve);
      when(workItem2.getAssignees()).thenReturn(assigneesToReturn2);
      when(workData2.isCompletedOrCancelled()).thenReturn(false);

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem);
      group.addAction(workItem2);

      Assert.assertEquals("steve", AssigneeColumn.instance.getAssigneeStr(group));
   }

   @org.junit.Test
   public void testGetAssigneesStr_invalidImplementersString() throws OseeCoreException {
      AssigneeColumn column = new AssigneeColumn(new ImplementersStringProvider() {

         @Override
         public String getImplementersStr(Object object) {
            return null;
         }
      });

      when(workData.isCompletedOrCancelled()).thenReturn(true);
      Assert.assertEquals("", column.getAssigneeStr(workItem));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasWorkDataNoAssignees() throws OseeCoreException {
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(new HasWorkDataNoAssignees()));
   }

   private class HasWorkDataNoAssignees implements HasWorkData {

      @Override
      public IAtsWorkData getWorkData() {
         return new MockWorkData(StateType.Working);
      }

   }
}
