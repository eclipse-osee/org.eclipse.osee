/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management.servlet.test;

import junit.framework.TestSuite;

/**
 * @author Roberto E. Escobar
 */
public class AllResourceManagementServletTests extends TestSuite {

   public AllResourceManagementServletTests() {
      addTestSuite(TestResourceManagerServlet.class);
   }

}
