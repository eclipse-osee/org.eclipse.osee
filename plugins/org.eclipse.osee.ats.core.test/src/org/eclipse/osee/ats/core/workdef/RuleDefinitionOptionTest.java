/*
 * Created on Mar 19, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import junit.framework.Assert;
import org.eclipse.osee.ats.workdef.api.RuleDefinitionOption;
import org.junit.Test;

/**
 * Test case for {@link RuleDefinitionOption}
 *
 * @author Donald G. Dunne
 */
public class RuleDefinitionOptionTest {

   @Test
   public void testGetDescription() {
      Assert.assertEquals(
         "StateDefinition Option: Will auto-create a blocking decision review for this state requesting validation for this workflow.",
         RuleDefinitionOption.AddDecisionValidateBlockingReview.getDescription());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(12, RuleDefinitionOption.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(RuleDefinitionOption.AddDecisionValidateBlockingReview,
         RuleDefinitionOption.valueOf(RuleDefinitionOption.AddDecisionValidateBlockingReview.name()));
   }

}
