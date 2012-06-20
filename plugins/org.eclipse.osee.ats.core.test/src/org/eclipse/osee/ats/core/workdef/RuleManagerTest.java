/*
 * Created on Mar 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.junit.Test;

/**
 * Test case for {@link RuleManager}
 * 
 * @author Donald G. Dunne
 */
public class RuleManagerTest {

   @Test
   public void testGetOrCreateRule() {
      RuleManager mgr = new RuleManager();
      mgr.getOrCreateRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      mgr.getOrCreateRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      mgr.getOrCreateRule("test");
   }

}
