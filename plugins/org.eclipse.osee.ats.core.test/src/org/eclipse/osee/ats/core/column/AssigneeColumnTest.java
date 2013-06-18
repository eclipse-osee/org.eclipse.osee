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

import java.util.Arrays;
import org.junit.Assert;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.HasWorkData;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.core.mock.MockActionGroup;
import org.eclipse.osee.ats.core.mock.MockAtsUser;
import org.eclipse.osee.ats.core.mock.MockWorkData;
import org.eclipse.osee.ats.core.mock.MockWorkItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @tests AssigneeColumn
 * @author Donald G. Dunne
 */
public class AssigneeColumnTest {

   private final MockAtsUser joe = new MockAtsUser("joe");
   private final MockAtsUser steve = new MockAtsUser("steve");
   private final MockAtsUser alice = new MockAtsUser("alice");

   @org.junit.Test
   public void testGetAssigneeStr_null() throws OseeCoreException {
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(null));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromInWorkWorkflow() throws OseeCoreException {
      MockWorkItem workItem = new MockWorkItem("this", "Working", StateType.Working);
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(workItem));

      workItem.getStateData().setAssignees("Working", Arrays.asList(joe, steve, alice));
      Assert.assertEquals("joe; steve; alice", AssigneeColumn.instance.getAssigneeStr(workItem));

   }

   @org.junit.Test
   public void testGetAssigneeStrFromCompletedWorkflow() throws OseeCoreException {
      MockWorkItem workItem = new MockWorkItem("this", "Working", StateType.Working);
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(workItem));

      workItem = new MockWorkItem("this", "Completed", StateType.Completed);
      workItem.getWorkData().setCompletedBy(steve);
      Assert.assertEquals("(steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

      // add alice as completedFromState assignee
      workItem.getWorkData().setCompletedFromState("Implement");
      workItem.getStateData().addState("Implement", Arrays.asList(alice));
      Assert.assertEquals("(alice; steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

      // add duplicate steve as state assigne and ensure doesn't duplicate in string
      workItem.getStateData().addAssignee("Implement", steve);
      Assert.assertEquals("(alice; steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

   }

   @org.junit.Test
   public void testGetAssigneeStrFromCancelledWorkflow() throws OseeCoreException {
      MockWorkItem workItem = new MockWorkItem("this", "Working", StateType.Working);
      Assert.assertEquals("", AssigneeColumn.instance.getAssigneeStr(workItem));

      workItem = new MockWorkItem("this", "Cancelled", StateType.Cancelled);
      workItem.getWorkData().setCancelledBy(steve);
      Assert.assertEquals("(steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

      // add alice as completedFromState assignee
      workItem.getWorkData().setCancelledFromState("Implement");
      workItem.getStateData().addState("Implement", Arrays.asList(alice));
      Assert.assertEquals("(alice; steve)", AssigneeColumn.instance.getAssigneeStr(workItem));

      // add duplicate steve as state assigne and ensure doesn't duplicate in string
      workItem.getStateData().addAssignee("Implement", steve);
      Assert.assertEquals("(alice; steve)", AssigneeColumn.instance.getAssigneeStr(workItem));
   }

   @org.junit.Test
   public void testGetAssigneesStr_hasActions_oneWorkingOneCancelled() throws OseeCoreException {

      MockWorkItem workItem1 = new MockWorkItem("this", "Working", StateType.Working);

      MockWorkItem workItem2 = new MockWorkItem("that", "Cancelled", StateType.Cancelled);
      workItem2.addImplementer(joe);

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem1);
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

      MockWorkItem workItem1 = new MockWorkItem("this", "Working", StateType.Working);
      workItem1.getStateData().setCurrentStateName("Implement");
      workItem1.getStateData().addState("Implement", Arrays.asList(steve));

      MockWorkItem workItem2 = new MockWorkItem("that", "Working", StateType.Working);
      workItem2.getStateData().setCurrentStateName("Implement");
      workItem2.getStateData().addState("Implement", Arrays.asList(steve));

      MockActionGroup group = new MockActionGroup("group");
      group.addAction(workItem1);
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

      MockWorkItem workItem1 = new MockWorkItem("this", "Completed", StateType.Completed);
      workItem1.addImplementer(joe);

      Assert.assertEquals("", column.getAssigneeStr(workItem1));
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
