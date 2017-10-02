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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.util.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransitionStatusDataTest {

   // @formatter:off
   @Mock IAtsWorkItem workItem, workItem2;
   @Mock IAtsTeamWorkflow teamWf;
   @Mock IAtsUser Joe, Kay;
   @Mock IAtsUserService userService;
   @Mock IAtsBranchService branchService;
   // @formatter:on

   TransitionStatusData data;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      List<IAtsWorkItem> workItems = new ArrayList<>();
      workItems.add(workItem);
      workItems.add(workItem2);
      data = new TransitionStatusData(workItems, false);

      new TransitionStatusDataResult();
   }

   @Test
   public void testGetSetPercent() {
      Assert.assertEquals(null, data.getPercent());

      data.setPercent(34);
      Assert.assertEquals(34, data.getPercent().intValue());

      data.setPercent(0);
      Assert.assertEquals(0, data.getPercent().intValue());
   }

   @Test
   public void testGetSetAdditionalHours() {
      Assert.assertEquals(null, data.getAdditionalHours());

      data.setAdditionalHours(34.0);
      Assert.assertEquals(34.0, data.getAdditionalHours().doubleValue(), 0.1);

      data.setAdditionalHours(0.0);
      Assert.assertEquals(0.0, data.getAdditionalHours().doubleValue(), 0.1);
   }

   @Test
   public void testSetIsSplitHoursBetweenItems() {
      Assert.assertTrue(data.isSplitHoursBetweenItems());

      data.setSplitHoursBetweenItems(false);
      Assert.assertFalse(data.isSplitHoursBetweenItems());
   }

   @Test
   public void testGetSetWorkItems() {
      Assert.assertEquals(2, data.getWorkItems().size());
      Assert.assertTrue(data.getWorkItems().contains(workItem));
      Assert.assertTrue(data.getWorkItems().contains(workItem2));

      data.setAwas(Arrays.asList(workItem));
      Assert.assertEquals(1, data.getWorkItems().size());
      Assert.assertTrue(data.getWorkItems().contains(workItem));
   }

   @Test
   public void testSetIsPercentRequired() {
      Assert.assertFalse(data.isPercentRequired());

      data.setPercentRequired(true);
      Assert.assertTrue(data.isPercentRequired());
   }

   @Test
   public void testGetSetDefaultPercent() {
      Assert.assertEquals(null, data.getDefaultPercent());

      data.setDefaultPercent(34);
      Assert.assertEquals(34, data.getDefaultPercent().intValue());
   }

   @Test
   public void testGetSetApplyHoursToEachItem() {
      Assert.assertFalse(data.isApplyHoursToEachItem());

      data.setApplyHoursToEachItem(true);
      Assert.assertTrue(data.isApplyHoursToEachItem());
   }

   @Test
   public void testIsValid_percent() {
      data.setPercentRequired(false);
      data.setAdditionalHours(1.0);
      Assert.assertEquals(Result.TrueResult, data.isValid());

      data.setPercentRequired(true);
      Assert.assertEquals(TransitionStatusDataResult.INVALID__PERCENT_MUST_BE_ENTERED, data.isValid());
      data.setPercent(-1);
      Assert.assertEquals(TransitionStatusDataResult.INVALID__UNCOMPLETE_PERCENT, data.isValid());
      data.setPercent(0);
      Assert.assertEquals(Result.TrueResult, data.isValid());
      data.setPercent(30);
      Assert.assertEquals(Result.TrueResult, data.isValid());
      data.setPercent(100);
      Assert.assertEquals(TransitionStatusDataResult.INVALID__UNCOMPLETE_PERCENT, data.isValid());
      data.setPercent(101);
      Assert.assertEquals(TransitionStatusDataResult.INVALID__UNCOMPLETE_PERCENT, data.isValid());
   }

   @Test
   public void testIsValid_hours() {
      data.setPercentRequired(false);
      Assert.assertEquals(TransitionStatusDataResult.INVALID__HOURS_MUST_BE_SET, data.isValid());

      data.setAdditionalHours(1.0);
      Assert.assertEquals(Result.TrueResult, data.isValid());
   }

   @Test
   public void testIsValid_workItems() {
      data.setPercentRequired(false);
      data.setAdditionalHours(1.0);
      Assert.assertEquals(Result.TrueResult, data.isValid());

      data.setSplitHoursBetweenItems(true);
      data.setApplyHoursToEachItem(true);
      Assert.assertEquals(TransitionStatusDataResult.INVALID__SELECT_ONLY_ONE_SPLIT_OR_APPLY, data.isValid());

      data.setSplitHoursBetweenItems(false);
      data.setApplyHoursToEachItem(false);
      Assert.assertEquals(TransitionStatusDataResult.INVALID__SELECT_EITHER_SPLIT_OR_APPLY, data.isValid());

      data.setSplitHoursBetweenItems(false);
      data.setApplyHoursToEachItem(true);
      Assert.assertEquals(Result.TrueResult, data.isValid());

      data.setSplitHoursBetweenItems(true);
      data.setApplyHoursToEachItem(false);
      Assert.assertEquals(Result.TrueResult, data.isValid());

      data.getWorkItems().clear();
      Assert.assertEquals(Result.TrueResult, data.isValid());

   }

}
