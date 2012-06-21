/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.impl.internal.model;

import java.util.Arrays;
import junit.framework.Assert;
import org.eclipse.osee.ats.workdef.api.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.workdef.impl.internal.model.DecisionReviewOption;
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

      Assert.assertEquals("opt2 - Followup Required", option.toString());

      Assert.assertTrue(option.getUserNames().isEmpty());
      option.setUserNames(Arrays.asList("joe", "alice"));
      Assert.assertTrue(option.getUserNames().contains("joe"));
      Assert.assertTrue(option.getUserNames().contains("joe"));

      option = new DecisionReviewOption("opt", false, null);
      Assert.assertTrue(option.getUserIds().isEmpty());

      option.setFollowupRequired(false);
      Assert.assertEquals("opt - No Followup Required", option.toString());
   }
}
