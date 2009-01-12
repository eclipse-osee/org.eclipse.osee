/*
 * Created on May 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Donald G. Dunne
 */
public class DemoTestUtil {

   public static void setUpTest() throws Exception {
      try {
         // This test should only be run on test db
         TestCase.assertFalse(AtsPlugin.isProductionDb());
         // Confirm test setup with demo data
         TestCase.assertTrue(DemoDbUtil.isDbPopulatedWithDemoData().isTrue());
         // Confirm user is Joe Smith
         TestCase.assertTrue("User \"Joe Smith\" does not exist in DB.  Run Demo DBInit prior to this test.",
               UserManager.getUserByUserId("Joe Smith") != null);
         // Confirm user is Joe Smith
         TestCase.assertTrue(
               "Authenticated user should be \"Joe Smith\" and is not.  Check that Demo Application Server is being run.",
               UserManager.getUser().getUserId().equals("Joe Smith"));
      } catch (OseeAuthenticationException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         TestCase.fail("Can't authenticate, either Demo Application Server is not running or Demo DbInit has not been performed");
      }

   }

}
