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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.core.util.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link TransitionHelperAdapter}
 * 
 * @author Donald G. Dunne
 */
public class TransitionHelperAdapterTest {

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   @Mock IAtsStateManager stateMgr;
   @Mock IAtsUser Joe, Kay;
   @Mock IAtsUserService userService;
   @Mock IAtsBranchService branchService;
   @Mock IAttributeResolver attrResolver;
   // @formatter:on

   TestTransitionHelper helper = null;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      helper = new TestTransitionHelper();
   }

   @Test
   public void testIsPrivilegedEditEnabled() {
      Assert.assertFalse(helper.isPrivilegedEditEnabled());
   }

   @Test
   public void testIsOverrideTransitionValidityCheck() {
      Assert.assertFalse(helper.isOverrideTransitionValidityCheck());
   }

   @Test
   public void testIsOverrideAssigneeCheck() {
      Assert.assertFalse(helper.isOverrideAssigneeCheck());
   }

   @Test
   public void testIsExecuteChanges() {
      Assert.assertFalse(helper.isExecuteChanges());
   }

   @Test
   public void testIsSystemUserAssignee() {
      when(workItem.getStateMgr()).thenReturn(stateMgr);
      List<IAtsUser> assignees = new ArrayList<>();
      when(stateMgr.getAssignees()).thenReturn(assignees);

      Assert.assertFalse(helper.isSystemUserAssingee(workItem));

      assignees.add(Joe);
      Assert.assertFalse(helper.isSystemUserAssingee(workItem));

      assignees.add(AtsCoreUsers.SYSTEM_USER);
      Assert.assertTrue(helper.isSystemUserAssingee(workItem));

      assignees.clear();
      assignees.add(AtsCoreUsers.ANONYMOUS_USER);
      Assert.assertTrue(helper.isSystemUserAssingee(workItem));
   }

   private class TestTransitionHelper extends TransitionHelperAdapter {

      public TestTransitionHelper() {
         super(null);
      }

      @Override
      public String getName() {
         return null;
      }

      @Override
      public Collection<? extends IAtsWorkItem> getWorkItems() {
         return null;
      }

      @Override
      public Result getCompleteOrCancellationReason() {
         return null;
      }

      @Override
      public Result handleExtraHoursSpent(IAtsChangeSet changes) {
         return null;
      }

      @Override
      public Collection<? extends IAtsUser> getToAssignees(IAtsWorkItem workItem) {
         return null;
      }

      @Override
      public String getToStateName() {
         return null;
      }

      @Override
      public IAtsChangeSet getChangeSet() {
         return null;
      }

      @Override
      public Collection<ITransitionListener> getTransitionListeners() {
         return null;
      }

      @Override
      public AtsApi getServices() {
         return null;
      }

   }
}
