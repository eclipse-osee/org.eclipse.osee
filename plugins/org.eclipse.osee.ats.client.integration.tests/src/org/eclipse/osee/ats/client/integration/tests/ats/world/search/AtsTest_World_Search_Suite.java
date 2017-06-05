/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.world.search;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   WorldSearchItemTest.class,
   AtsQueryServiceImplTest.class,
   AtsConfigQueryImplTest.class,
   AtsQueryImplTest.class,
   MyFavoritesSearchItemTest.class,
   MyWorldSearchItemTest.class,
   MySubscribedSearchItemTest.class,
   NextVersionSearchItemTest.class,
   VersionTargetedForTeamSearchItemTest.class,
   ShowOpenWorkflowsByArtifactTypeTest.class,
   LegacyPcrIdQuickSearchTest.class,
   TeamDefinitionQuickSearchTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_World_Search_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      System.out.println("\n\nBegin " + AtsTest_World_Search_Suite.class.getSimpleName());
      DemoTestUtil.setUpTest();
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_World_Search_Suite.class.getSimpleName());
   }
}
