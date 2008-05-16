/*
 * Created on May 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;

/**
 * @author Donald G. Dunne
 */
public class DemoTestUtil {

   public static void setUpTest() throws Exception {
      // This test should only be run on test db
      TestCase.assertFalse(AtsPlugin.isProductionDb());
      // Confirm test setup with demo data
      TestCase.assertTrue(DemoDbUtil.isDbPopulatedWithDemoData().isTrue());
      // Confirm user is Joe Smith
      TestCase.assertTrue(SkynetAuthentication.getUser().getUserId().equals("Joe Smith"));
   }

}
