/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.search.engine.test;

import junit.framework.TestSuite;

/**
 * @author Roberto E. Escobar
 */
public class AllSearchEngineTests extends TestSuite {

   public AllSearchEngineTests() {
      addTestSuite(TestSearchEngine.class);
   }

}
