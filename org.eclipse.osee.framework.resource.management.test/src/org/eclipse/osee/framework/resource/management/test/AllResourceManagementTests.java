/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management.test;

import junit.framework.TestSuite;

/**
 * @author Andrew M. Finkbeiner
 */
public class AllResourceManagementTests extends TestSuite {

   public AllResourceManagementTests() {
      addTestSuite(TestResourceManager.class);
   }

}
