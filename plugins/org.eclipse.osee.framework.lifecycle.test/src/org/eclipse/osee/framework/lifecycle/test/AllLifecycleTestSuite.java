package org.eclipse.osee.framework.lifecycle.test;

import org.eclipse.osee.framework.lifecycle.test.internal.InternalLifecycleTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { //
InternalLifecycleTestSuite.class, //
})
public class AllLifecycleTestSuite {

}
