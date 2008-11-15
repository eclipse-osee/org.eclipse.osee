/*
 * Created on May 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.database.initialize.DatabaseInitializationOperation;

/**
 * @author Donald G. Dunne
 */
public class AtsDbInitTest extends TestCase {

   /**
    * @throws java.lang.Exception
    */
   protected void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(AtsPlugin.isProductionDb());
   }

   public void testDemoDbInit() throws Exception {
      System.out.println("Begin Database Initialization...");
      DatabaseInitializationOperation.executeWithoutPrompting("ATS - Developer");
      System.out.println("Database Initialization Complete.");
   }

}
