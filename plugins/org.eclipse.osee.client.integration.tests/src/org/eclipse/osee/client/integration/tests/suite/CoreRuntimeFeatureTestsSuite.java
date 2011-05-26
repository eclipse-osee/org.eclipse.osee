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
import org.eclipse.osee.framework.core.model.AllCoreModelTestSuite;
import org.eclipse.osee.framework.core.test.FrameworkCoreTestSuite;
import org.eclipse.osee.framework.database.test.DatabaseTestSuite;
import org.eclipse.osee.framework.jdk.core.JdkCoreTestSuite;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.lifecycle.test.AllLifecycleTestSuite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   JdkCoreTestSuite.class,
   DatabaseTestSuite.class,
   FrameworkCoreTestSuite.class,
   AllCoreModelTestSuite.class,
   AllCoreMessageTestSuite.class,
   AllLifecycleTestSuite.class,})
public class CoreRuntimeFeatureTestsSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + CoreRuntimeFeatureTestsSuite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + CoreRuntimeFeatureTestsSuite.class.getSimpleName());
   }
}
