package org.eclipse.osee.framework.lifecycle.test.internal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { //
LifecycleServiceTest.class, //
      LifecycleOperationTest.class, //
      OnEditOperationTest.class, //
})
public class InternalLifecycleTestSuite {

}
