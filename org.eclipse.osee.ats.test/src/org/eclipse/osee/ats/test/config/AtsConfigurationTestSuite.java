/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.config;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigurationTestSuite {

   public static Test suite() {
      TestSuite suite =
            new TestSuite(
                  "Test for org.eclipse.osee.ats.test.config - All ATS Tests - Can be run on either production or test databases");
      //$JUnit-BEGIN$
      suite.addTestSuite(AtsWorkItemDefinitionTest.class);
      suite.addTestSuite(AtsActionableItemToTeamDefinitionTest.class);
      suite.addTestSuite(AtsTeamDefintionToWorkflowTest.class);
      //$JUnit-END$
      return suite;
   }

}
