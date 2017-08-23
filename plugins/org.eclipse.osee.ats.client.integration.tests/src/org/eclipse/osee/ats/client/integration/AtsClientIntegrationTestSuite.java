/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration;

import org.eclipse.osee.ats.client.integration.tests.AtsTest_AllAts_Suite;
import org.eclipse.osee.ats.client.integration.tests.DemoDbPopulateSuite;
import org.eclipse.osee.ats.client.integration.tests.DirtyArtifactCacheTest;
import org.eclipse.osee.ats.client.integration.tests.framework.skynet.core.artifact.SkyentCoreArtifact_Suite;
import org.eclipse.osee.ats.client.integration.tests.framework.ui.skynet.FrameworkUiSkynetTest_Suite;
import org.eclipse.osee.ats.client.integration.tests.framework.ui.skynet.dialog.FrameworkUiSkynetTest_Dialog_Suite;
import org.eclipse.osee.ats.client.integration.tests.orcs.rest.ClientEndpointTest;
import org.eclipse.osee.ats.client.integration.tests.util.DbInitTest;
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
   AtsTest_AllAts_Suite.class,
   SkyentCoreArtifact_Suite.class,
   FrameworkUiSkynetTest_Suite.class,
   FrameworkUiSkynetTest_Dialog_Suite.class,
   ClientEndpointTest.class,
   DirtyArtifactCacheTest.class})
public class AtsClientIntegrationTestSuite {
   // Test Suite

   private static ElapsedTime time;

   @BeforeClass
   public static void setup() {
      time = new ElapsedTime("AtsClientIntegrationTestSuite", true);
      OseeProperties.setIsInTest(true);
   }

   @AfterClass
   public static void cleanup() {
      time.end(Units.MIN);
      OseeProperties.setIsInTest(false);
   }

}
