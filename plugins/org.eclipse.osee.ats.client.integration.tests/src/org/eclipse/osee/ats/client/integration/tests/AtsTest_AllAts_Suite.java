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
package org.eclipse.osee.ats.client.integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.integration.AtsClientIntegrationTestSuite;
import org.eclipse.osee.ats.client.integration.tests.ats.AtsTest_Ats_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.actions.AtsTest_Action_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsCoreClient_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.review.AtsCoreClient_Review_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.workflow.transition.TransitionManagerTest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({//
   AtsCoreClient_Suite.class, //
   AtsTest_Ats_Suite.class, //
   // Long running tests at bottom for ease of development/re-run
   TransitionManagerTest.class, //
   AtsTest_Action_Suite.class, //
   AtsCoreClient_Review_Suite.class, //
})
public class AtsTest_AllAts_Suite {

   @BeforeClass
   public static void setUp() throws Exception {
      DemoUtil.checkDbInitSuccess();
      System.out.println("\nBegin " + AtsClientIntegrationTestSuite.class.getSimpleName());
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      assertTrue("Should be run on demo database.", TestUtil.isDemoDb());

      IdeClientSession session = ClientSessionManager.getSession();
      assertEquals("Must run populate as Joe Smith (3333)", DemoUsers.Joe_Smith.getUserId(), session.getUserId());
      assertEquals("Must run populate as Joe Smith (3333)", DemoUsers.Joe_Smith.getUserId(),
         UserManager.getUser().getUserId());

      RenderingUtil.setPopupsAllowed(false);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsClientIntegrationTestSuite.class.getSimpleName() + "\n\n");
   }
}
