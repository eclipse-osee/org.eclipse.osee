/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.demo.AtsTest_DemoPopulateAndTest_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workdef.AtsIntegrationTestsWorkDefinitionProvider;
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
   DemoDbPopulateAtsHealthTest.class,
   AtsConfigurationsTest.class})
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
         ClientSessionManager.getAuthenticationProtocols().contains("orgdemo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("orgdemo"));
      assertTrue("Should be run on demo database.", TestUtil.isDemoDb());

      RenderingUtil.setPopupsAllowed(false);

      for (IAtsWorkDefinitionBuilder workDefBldr : (new AtsIntegrationTestsWorkDefinitionProvider()).getWorkDefinitionBuilders()) {
         AtsApiService.get().getWorkDefinitionService().addWorkDefinition(workDefBldr);
      }

   }

   @AfterClass
   public static void cleanup() throws Exception {
      System.out.println("End Database Populate\n");
   }
}
