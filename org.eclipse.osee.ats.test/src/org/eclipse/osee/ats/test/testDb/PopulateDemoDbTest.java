/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Donald G. Dunne
 */
public class PopulateDemoDbTest extends TestCase {

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception {
      super.setUp();
      try {
         // Confirm user is Joe Smith
         assertTrue("User \"Joe Smith\" does not exist in DB.  Run Demo DBInit prior to this test.",
               UserManager.getUserByUserId("Joe Smith") != null);
         // Confirm user is Joe Smith
         assertTrue(
               "Authenticated user should be \"Joe Smith\" and is not.  Check that Demo Application Server is being run.",
               UserManager.getUser().getUserId().equals("Joe Smith"));
      } catch (OseeAuthenticationException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         fail("Can't authenticate, either Demo Application Server is not running or Demo DbInit has not been performed");
      }
      // This test should only be run on test db
      assertFalse(AtsPlugin.isProductionDb());
      System.out.println("Validating OSEE Application Server...");
      if (!OseeLog.isStatusOk()) {
         System.err.println(OseeLog.getStatusReport() + ". \nExiting.");
         return;
      }
   }

   /**
    * Test method for {@link org.eclipse.osee.ats.config.demo.config.PopulateDemoActions#run()}.
    */
   public void testPopulateDemoDb() throws Exception {
      PopulateDemoActions populateDemoActions = new PopulateDemoActions(null);
      populateDemoActions.run(false);
   }

}
