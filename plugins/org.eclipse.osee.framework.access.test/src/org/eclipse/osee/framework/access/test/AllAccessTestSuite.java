/*
 * Created on Jun 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.test;

import org.eclipse.osee.framework.access.test.internal.InternalAccessTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { //
InternalAccessTestSuite.class, //
})
public class AllAccessTestSuite {

}
