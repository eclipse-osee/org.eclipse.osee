/*********************************************************************
 * Copyright (c) 2012 Boeing
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

import org.eclipse.osee.ats.ide.integration.tests.define.DefineIntegrationTestSuite;
import org.eclipse.osee.ats.ide.integration.tests.framework.access.FrameworkAccess_Suite;
import org.eclipse.osee.ats.ide.integration.tests.framework.core.FrameworkCoreSuite;
import org.eclipse.osee.ats.ide.integration.tests.framework.skynet.core.artifact.SkyentCoreArtifact_Suite;
import org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet.FrameworkUiSkynetTest_Suite;
import org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet.dialog.FrameworkUiSkynetTest_Dialog_Suite;
import org.eclipse.osee.ats.ide.integration.tests.orcs.rest.ClientEndpointTest;
import org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic.OrcsRestTestSuite;
import org.eclipse.osee.ats.ide.integration.tests.publishing.PublishingTestSuite;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.SkynetCoreIntegrationTestSuite;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.SynchronizationTestSuite;
import org.eclipse.osee.ats.ide.integration.tests.ui.skynet.SkynetUiCoreIntegrationTestSuite;
import org.eclipse.osee.ats.ide.integration.tests.util.DbInitTest;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   DbInitTest.class,
   DemoDbPopulateSuite.class,
   /**
    * AtsTest_AllAts_Suite suite needs to be right after populate because<br/>
    * search counts are used from populate and need to remain the same.
    */
   AtsTest_AllAts_Suite.class,
   DefineIntegrationTestSuite.class,
   FrameworkAccess_Suite.class,
   FrameworkCoreSuite.class,
   OrcsRestTestSuite.class,
   PublishingTestSuite.class,
   SkyentCoreArtifact_Suite.class,
   SkynetCoreIntegrationTestSuite.class,
   SkynetUiCoreIntegrationTestSuite.class,
   SynchronizationTestSuite.class,
   FrameworkUiSkynetTest_Dialog_Suite.class,
   ClientEndpointTest.class,
   DirtyArtifactCacheTest.class,
   FrameworkUiSkynetTest_Suite.class,
   LongRunningTestSuite.class //
})
public class AtsIdeIntegrationTestSuite {
   // Test Suite

   private static ElapsedTime time;

   @BeforeClass
   public static void setup() {
      time = new ElapsedTime("AtsIdeIntegrationTestSuite", true);
      OseeProperties.setIsInTest(true);
   }

   @AfterClass
   public static void cleanup() {
      time.end(Units.MIN);
      OseeProperties.setIsInTest(false);
   }

}
