/*
 * Created on May 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb.review;

import junit.framework.TestCase;
import org.eclipse.osee.ats.test.testDb.DemoTestUtil;

/**
 * This test is intended to be run against a demo database. It tests the ability to create and set rules on a workflow
 * page that causes decision and peerToPeer reviews to be auto-created during transition, createBranch and commitBranch
 * 
 * @author Donald G. Dunne
 */
public class AtsReviewRuleTest extends TestCase {

   public void testDemoDatabase() throws Exception {
      DemoTestUtil.setUpTest();
   }

   public void testReviewCreation() throws Exception {

   }
}
