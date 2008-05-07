/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.locator.snapshot.test;

import junit.framework.TestSuite;

/**
 * @author Roberto E. Escobar
 */
public class AllResourceLocatorSnapshotTests extends TestSuite {

   public AllResourceLocatorSnapshotTests() {
      addTestSuite(TestResourceLocatorSnapshot.class);
   }

}
