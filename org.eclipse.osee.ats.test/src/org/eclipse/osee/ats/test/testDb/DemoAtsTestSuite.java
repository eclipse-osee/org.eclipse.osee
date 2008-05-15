/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Donald G. Dunne
 */
public class DemoAtsTestSuite {

   public static Test suite() {
      TestSuite suite = new TestSuite("Test for org.eclipse.osee.ats.test.testDb - All ATS Tests");
      //$JUnit-BEGIN$
      suite.addTestSuite(AtsNavigateItemsToWorldViewTest.class);
      suite.addTestSuite(AtsNavigateItemsToTaskEditorTest.class);
      suite.addTestSuite(AtsNavigateItemsToMassEditorTest.class);
      //$JUnit-END$
      return suite;
   }

}
