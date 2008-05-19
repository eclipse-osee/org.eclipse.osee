/*
 * Created on May 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.database.initialize.LaunchOseeDbConfigClient;
import org.eclipse.osee.framework.database.utility.GroupSelection;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;

/**
 * @author Donald G. Dunne
 */
public class DemoDbInitTest extends TestCase {

   /**
    * @throws java.lang.Exception
    */
   protected void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(AtsPlugin.isProductionDb());
   }

   public void testDemoDbInit() throws Exception {
      System.out.println("Validating OSEE Application Server...");
      if (!OseeApplicationServer.isApplicationServerAlive()) {
         System.err.println("No OSEE Application Server running.\nExiting.");
         return;
      }
      System.out.println("Begin Database Initialization...");
      LaunchOseeDbConfigClient configClient = new LaunchOseeDbConfigClient(OseeDb.getDefaultDatabaseService());
      configClient.run(OseeDbConnection.getConnection(), GroupSelection.getInstance().getDbInitTasks(
            "OSEE Demo Database"));
      System.out.println("Database Initialization Complete.");
   }

}
