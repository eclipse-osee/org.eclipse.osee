/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
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
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.core.AtsCore_JT_Suite;
import org.eclipse.osee.ats.core.AtsCore_PT_Suite;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientSession;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AtsCore_JT_Suite.class, AtsCore_PT_Suite.class})
/**
 * Core tests that are necessary before populate demo database is run
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoCoreTests {
   @BeforeClass
   public static void setUp() throws Exception {
      DemoDbUtil.checkDbInitSuccess();
      System.out.println("\nBegin " + MasterTestSuite_DemoCoreTests.class.getSimpleName());
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      assertTrue("Should be run on demo database.", TestUtil.isDemoDb());
      // Re-authenticate so we can continue
      OseeClientSession session = ClientSessionManager.getSession();
      UserManager.releaseUser();

      Assert.assertEquals("Must run populate as Joe Smith", DemoUsers.Joe_Smith.getUserId(), session.getUserId());
      Assert.assertEquals("Must run populate as Joe Smith", DemoUsers.Joe_Smith.getUserId(),
         UserManager.getUser().getUserId());

      RenderingUtil.setPopupsAllowed(false);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + MasterTestSuite_DemoCoreTests.class.getSimpleName() + "\n\n");
   }

}
