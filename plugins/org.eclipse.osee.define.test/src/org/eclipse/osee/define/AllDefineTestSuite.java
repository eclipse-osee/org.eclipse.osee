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
package org.eclipse.osee.define;

import junit.framework.Assert;
import org.eclipse.osee.define.blam.operation.BlamTestSuite;
import org.eclipse.osee.define.jobs.JobsTestSuite;
import org.eclipse.osee.define.traceability.TestUnitAnnotationUtilityTest;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({BlamTestSuite.class, JobsTestSuite.class, TestUnitAnnotationUtilityTest.class})
public final class AllDefineTestSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AllDefineTestSuite.class.getSimpleName());
      Assert.assertTrue("osee.data project should be open", OseeData.isProjectOpen());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      Assert.assertTrue("osee.data project should be open", OseeData.isProjectOpen());
      System.out.println("End " + AllDefineTestSuite.class.getSimpleName());
   }
}
