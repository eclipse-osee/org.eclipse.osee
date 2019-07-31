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
package org.eclipse.osee.ats.ide.integration.tests;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.demo.AtsTest_DemoPopulateAndTest_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtilTest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({//
   AtsTestUtilTest.class,
   AtsTest_DemoPopulateAndTest_Suite.class,
   DemoDbPopulateValidateAtsDatabaseTest.class //
})
/**
 * @author Donald G. Dunne
 */
public class DemoDbPopulateSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("Begin Database Populate");
      DemoUtil.checkDbInitSuccess();
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      assertTrue("Should be run on demo database.", TestUtil.isDemoDb());

      RenderingUtil.setPopupsAllowed(false);
   }

   @AfterClass
   public static void cleanup() throws Exception {
      System.out.println("End Database Populate\n");
   }
}
