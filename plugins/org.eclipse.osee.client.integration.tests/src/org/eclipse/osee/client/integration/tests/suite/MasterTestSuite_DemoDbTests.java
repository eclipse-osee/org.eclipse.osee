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

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;
import org.eclipse.osee.ats.AtsTest_Demo_Config_Suite;
import org.eclipse.osee.ats.AtsTest_Demo_StateItem_Suite;
import org.eclipse.osee.ats.AtsTest_Demo_Suite;
import org.eclipse.osee.ats.Review_Demo_Suite;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.coverage.Coverage_Db_Suite;
import org.eclipse.osee.define.AllDefineTestSuite;
import org.eclipse.osee.framework.access.test.AllAccessTestSuite;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.messaging.event.res.test.AllEventResTestSuite;
import org.eclipse.osee.framework.skynet.core.FrameworkCore_Demo_Suite;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.DirtyArtifactCacheTest;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.test.FrameworkUi_Demo_Suite;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AllAccessTestSuite.class,
   Review_Demo_Suite.class,
   AtsTest_Demo_StateItem_Suite.class,
   CoreRuntimeFeatureTestsSuite.class,
   Coverage_Db_Suite.class,
   FrameworkCore_Demo_Suite.class,
   AllEventResTestSuite.class,
   AtsTest_Demo_Config_Suite.class,
   AtsTest_Demo_Suite.class,
   FrameworkUi_Demo_Suite.class,
   AllDefineTestSuite.class,
   // This should always be last test of master suite
   DirtyArtifactCacheTest.class})
/**
 * This suite should contain all cases and suites that can be run against a Demo Db Init and Demo Populated osee
 * database.
 *
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoDbTests {
   @BeforeClass
   public static void setUp() throws Exception {
      DemoDbUtil.checkDbInitAndPopulateSuccess();
      System.out.println("\nBegin " + MasterTestSuite_DemoDbTests.class.getSimpleName());
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      assertTrue("Should be run on demo database.", TestUtil.isDemoDb());
      Assert.assertEquals("Demo client should run as Joe Smith insead of " + UserManager.getUser().toStringWithId(),
         UserManager.getUser(DemoUsers.Joe_Smith), UserManager.getUser());

      RenderingUtil.setPopupsAllowed(false);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + MasterTestSuite_DemoDbTests.class.getSimpleName() + "\n\n");
   }
}
