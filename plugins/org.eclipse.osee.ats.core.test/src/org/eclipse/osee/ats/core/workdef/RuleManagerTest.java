/*
 * Created on Mar 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.core.config.RuleManager;
import org.eclipse.osee.ats.workdef.api.RuleDefinitionOption;
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
      mgr.addRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      mgr.addRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      mgr.addRule("test");
   }

}
