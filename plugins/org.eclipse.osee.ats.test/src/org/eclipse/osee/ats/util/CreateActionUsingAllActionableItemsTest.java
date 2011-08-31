/*
 * Created on Aug 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.action.ActionArtifact;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Test for {@link CreateActionUsingAllActionableItems}
 * 
 * @author Donald G. Dunne
 */
public class CreateActionUsingAllActionableItemsTest {

   @org.junit.Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      ActionArtifact action = CreateActionUsingAllActionableItems.createActionWithAllAis();
      if (TestUtil.isDemoDb()) {
         Assert.assertEquals("Should be 14 workflows created", 14, action.getTeams().size());
      } else {
         Assert.assertEquals("Should be 33 workflows created", 33, action.getTeams().size());
      }
      TestUtil.severeLoggingEnd(monitor);
   }

}
