/*
 * Created on Jul 19, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.client.integration.tests.suite;

import org.eclipse.osee.framework.core.message.test.AllCoreMessageTestSuite;
import org.eclipse.osee.framework.core.model.test.AllCoreModelTestSuite;
import org.eclipse.osee.framework.core.test.FrameworkCoreTestSuite;
import org.eclipse.osee.framework.jdk.core.test.JdkCoreTestSuite;
import org.eclipse.osee.framework.lifecycle.test.AllLifecycleTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({//
JdkCoreTestSuite.class, //
			FrameworkCoreTestSuite.class, //
			AllCoreModelTestSuite.class, //
			AllCoreMessageTestSuite.class, //
			AllLifecycleTestSuite.class,//
})
public class CoreRuntimeFeatureTestsSuite {

}
