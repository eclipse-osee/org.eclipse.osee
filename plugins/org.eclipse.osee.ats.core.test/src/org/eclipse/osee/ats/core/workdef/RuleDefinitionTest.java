/*
 * Created on Mar 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link RuleDefinition}
 *
 * @author Donald G. Dunne
 */
public class RuleDefinitionTest {

   @Test
   public void testToString() {
      RuleDefinition def = new RuleDefinition("test");
      Assert.assertEquals("[test]", def.toString());
   }

   @Test
   public void testRuleDefinitionString() {
      RuleDefinition def = new RuleDefinition("test");
      Assert.assertEquals("test", def.getName());
   }

   @Test
   public void testRuleDefinitionRuleDefinitionOption() {
      RuleDefinition def = new RuleDefinition(RuleDefinitionOption.AddDecisionValidateBlockingReview);
      Assert.assertEquals(RuleDefinitionOption.AddDecisionValidateBlockingReview.name(), def.getName());
   }

}
