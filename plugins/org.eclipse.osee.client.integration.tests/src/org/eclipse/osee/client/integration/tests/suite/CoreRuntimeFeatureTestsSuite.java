/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
   // Test Suite class
}
