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
package org.eclipse.osee.ats.core.workdef;

import java.util.Arrays;
import org.eclipse.osee.ats.api.review.DecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link DecisionReviewOption}
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewOptionTest {

   @Test
   public void test() {
      IAtsDecisionReviewOption option = new DecisionReviewOption("opt", false, Arrays.asList("123", "345"));
      Assert.assertEquals("opt", option.getName());
      option.setName("opt2");
      Assert.assertEquals("opt2", option.getName());

      Assert.assertTrue(option.getUserIds().contains("123"));
      Assert.assertTrue(option.getUserIds().contains("345"));

      option.setUserIds(Arrays.asList("333", "444"));
      Assert.assertFalse(option.getUserIds().contains("123"));
      Assert.assertFalse(option.getUserIds().contains("345"));
      Assert.assertTrue(option.getUserIds().contains("333"));
      Assert.assertTrue(option.getUserIds().contains("444"));

      Assert.assertFalse(option.isFollowupRequired());
      option.setFollowupRequired(true);
      Assert.assertTrue(option.isFollowupRequired());

      Assert.assertEquals("opt2", option.toString());

      Assert.assertTrue(option.getUserNames().isEmpty());
      option.setUserNames(Arrays.asList("joe", "alice"));
      Assert.assertTrue(option.getUserNames().contains("joe"));
      Assert.assertTrue(option.getUserNames().contains("joe"));

      option = new DecisionReviewOption("opt", false, null);
      Assert.assertTrue(option.getUserIds().isEmpty());

      option.setFollowupRequired(false);
      Assert.assertEquals("opt", option.toString());
   }
}
