/*
 * Created on May 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import junit.framework.TestCase;
import org.eclipse.osee.framework.database.initialize.DatabaseInitializationOperation;

/**
 * @author Donald G. Dunne
 */
public class DemoDbInitTest extends TestCase {

   public void testDemoDbInit() throws Exception {
      System.out.println("Begin Database Initialization...");
      DatabaseInitializationOperation.executeWithoutPrompting("OSEE Demo Database");
      System.out.println("Database Initialization Complete.");
   }

}
