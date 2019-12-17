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
package org.eclipse.osee.ats.core.workflow.transition;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.framework.core.util.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link TransitionHelper}
 *
 * @author Donald G. Dunne
 */
public class TransitionHelperTest {

   // @formatter:off
   @Mock IAtsWorkItem workItem, workItem2;
   @Mock IAtsTeamWorkflow teamWf;
   @Mock IAtsUser Joe, Kay;
   @Mock IAtsChangeSet changes;
   @Mock IAtsTransitionHook transListener1, transListener2;
   @Mock IAtsWorkItemService workItemService;
   @Mock IAtsUserService userService;
   @Mock IAtsBranchService branchService;
   @Mock AtsApi atsApi;
   @Mock IAttributeResolver attrResolver;
   // @formatter:on

   TransitionHelper helper = null;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(atsApi.getAttributeResolver()).thenReturn(attrResolver);
      when(atsApi.getBranchService()).thenReturn(branchService);
      when(atsApi.getUserService()).thenReturn(userService);
      when(atsApi.getWorkItemService()).thenReturn(workItemService);

      helper = new TransitionHelper("test", Arrays.asList(workItem, workItem2), "Completed", Arrays.asList(Joe, Kay),
         "cancel reason", changes, atsApi, TransitionOption.None);
   }

   @Test
   public void testIsOverrideTransitionValidityCheck() {
      Assert.assertFalse(helper.isOverrideTransitionValidityCheck());

      TransitionHelper helper = new TransitionHelper("test", Arrays.asList(workItem, workItem2), "Completed",
         Arrays.asList(Joe, Kay), "cancel reason", changes, atsApi, TransitionOption.OverrideTransitionValidityCheck);

      Assert.assertTrue(helper.isOverrideTransitionValidityCheck());
   }

   @Test
   public void testIsOverrideAssigneeCheck() {
      Assert.assertFalse(helper.isOverrideAssigneeCheck());

      TransitionHelper helper = new TransitionHelper("test", Arrays.asList(workItem, workItem2), "Completed",
         Arrays.asList(Joe, Kay), "cancel reason", changes, atsApi, TransitionOption.OverrideAssigneeCheck);

      Assert.assertTrue(helper.isOverrideAssigneeCheck());
   }

   @Test
   public void testIsExecuteChanges() {
      Assert.assertFalse(helper.isExecuteChanges());

      helper.setExecuteChanges(true);

      Assert.assertTrue(helper.isExecuteChanges());
   }

   @Test
   public void testGetWorkItems() {
      Assert.assertEquals(2, helper.getWorkItems().size());
      Assert.assertTrue(helper.getWorkItems().contains(workItem));
      Assert.assertTrue(helper.getWorkItems().contains(workItem2));
   }

   @Test
   public void testGetCompleteOrCancellationReason() {
      Result reason = helper.getCompleteOrCancellationReason();
      Assert.assertEquals("cancel reason", reason.getText());
      Assert.assertTrue(reason.isTrue());

      TransitionHelper helper2 = new TransitionHelper("test", Arrays.asList(workItem, workItem2), "Completed",
         Arrays.asList(Joe, Kay), null, changes, atsApi, TransitionOption.OverrideAssigneeCheck);

      reason = helper2.getCompleteOrCancellationReason();
      Assert.assertEquals("", reason.getText());
      Assert.assertTrue(reason.isFalse());
   }

   @Test
   public void testGetName() {
      Assert.assertEquals("test", helper.getName());
   }

   @Test
   public void testGetToAssignees() {
      Collection<? extends IAtsUser> toAssignees = helper.getToAssignees(workItem);
      Assert.assertEquals(2, toAssignees.size());
      Assert.assertTrue(toAssignees.contains(Kay));
      Assert.assertTrue(toAssignees.contains(Joe));
   }

   @Test
   public void testHandleExtraHoursSpent() {
      Assert.assertEquals(Result.TrueResult, helper.handleExtraHoursSpent(changes));
   }

   @Test
   public void testGetToStateName() {
      Assert.assertEquals("Completed", helper.getToStateName());
   }

   @Test
   public void testAddTransitionOption() {
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      helper.addTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertTrue(helper.isOverrideAssigneeCheck());
      helper.addTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertTrue(helper.isOverrideAssigneeCheck());
   }

   @Test
   public void testRemoveTransitionOption() {
      helper.addTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertTrue(helper.isOverrideAssigneeCheck());
      helper.removeTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
      helper.removeTransitionOption(TransitionOption.OverrideAssigneeCheck);
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
   }

   @Test
   public void testSetToStateName() {
      helper.setToStateName("Analyze");
      Assert.assertEquals("Analyze", helper.getToStateName());
   }

   @Test
   public void testGetChangeSet() {
      Assert.assertEquals(changes, helper.getChangeSet());
   }

   @Test
   public void testGetTransitionListeners() {
      when(workItemService.getTransitionHooks()).thenReturn(Arrays.asList(transListener1));

      Assert.assertEquals(transListener1, helper.getTransitionListeners().iterator().next());
   }

   @Test
   public void testIsWorkingBranchInWork() {
      when(branchService.isWorkingBranchInWork(teamWf)).thenReturn(true);

      Assert.assertTrue(helper.isWorkingBranchInWork(teamWf));

      when(branchService.isWorkingBranchInWork(teamWf)).thenReturn(false);

      Assert.assertFalse(helper.isWorkingBranchInWork(teamWf));
   }

   @Test
   public void testIsBranchInCommit() {
      when(branchService.isBranchInCommit(teamWf)).thenReturn(true);

      Assert.assertTrue(helper.isBranchInCommit(teamWf));

      when(branchService.isBranchInCommit(teamWf)).thenReturn(false);

      Assert.assertFalse(helper.isBranchInCommit(teamWf));
   }

   @Test
   public void testIsSystemUser() {
      helper.setTransitionUser(Joe);
      when(userService.getCurrentUser()).thenReturn(Joe);

      Assert.assertFalse(helper.isSystemUser());

      helper.setTransitionUser(AtsCoreUsers.SYSTEM_USER);
      Assert.assertTrue(helper.isSystemUser());
   }

   @Test
   public void testGetSetTransitionUser() {
      Assert.assertNull(helper.getTransitionUser());

      helper.setTransitionUser(Joe);
      Assert.assertEquals(Joe, helper.getTransitionUser());
   }
}
