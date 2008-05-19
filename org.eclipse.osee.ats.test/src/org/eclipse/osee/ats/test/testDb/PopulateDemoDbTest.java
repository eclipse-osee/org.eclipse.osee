/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;

/**
 * @author Donald G. Dunne
 */
public class PopulateDemoDbTest extends TestCase {

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      // This test should only be run on test db
      assertFalse(AtsPlugin.isProductionDb());
      System.out.println("Validating OSEE Application Server...");
      if (!OseeApplicationServer.isApplicationServerAlive()) {
         System.err.println("No OSEE Application Server running.\nExiting.");
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
