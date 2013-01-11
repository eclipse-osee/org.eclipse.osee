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
package org.eclipse.osee.coverage.integration.tests;

import org.eclipse.osee.coverage.integration.tests.integration.ArtifactTestUnitStoreTest;
import org.eclipse.osee.coverage.integration.tests.integration.CoverageItemPersistTest;
import org.eclipse.osee.coverage.integration.tests.integration.CoverageManagerTest;
import org.eclipse.osee.coverage.integration.tests.integration.CoverageOptionManagerStoreTest;
import org.eclipse.osee.coverage.integration.tests.integration.CoveragePackageImportTest;
import org.eclipse.osee.coverage.integration.tests.integration.CoverageParametersTest;
import org.eclipse.osee.coverage.integration.tests.integration.CoveragePreferencesTest;
import org.eclipse.osee.coverage.integration.tests.integration.CoverageUnitPersistTest;
import org.eclipse.osee.coverage.integration.tests.integration.VCastAdaCoverage_V5_3_ImportOperationTest;
import org.eclipse.osee.coverage.integration.tests.integration.VCastAdaCoverage_V6_0_ImportOperationTest;
import org.eclipse.osee.coverage.integration.tests.integration.VCastDataStoreTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ArtifactTestUnitStoreTest.class,
   CoverageItemPersistTest.class,
   CoverageManagerTest.class,
   CoverageOptionManagerStoreTest.class,
   CoveragePackageImportTest.class,
   CoverageParametersTest.class,
   CoveragePreferencesTest.class,
   CoverageUnitPersistTest.class,
   VCastAdaCoverage_V5_3_ImportOperationTest.class,
   VCastAdaCoverage_V6_0_ImportOperationTest.class,
   VCastDataStoreTest.class})
/**
 * @author Donald G. Dunne
 */
public class CoverageIntegrationTestSuite {
   //   @BeforeClass
   //   public static void setUp() throws Exception {
   //      OseeProperties.setIsInTest(true);
   //      assertTrue("Should be run on test database.", TestUtil.isTestDb());
   //      assertTrue(
   //         "Application Server must be running.",
   //         ClientSessionManager.getAuthenticationProtocols().contains("lba") || ClientSessionManager.getAuthenticationProtocols().contains(
   //            "demo"));
   //      assertTrue(
   //         "Client must authenticate using lba protocol",
   //         ClientSessionManager.getSession().getAuthenticationProtocol().equals("lba") || ClientSessionManager.getSession().getAuthenticationProtocol().equals(
   //            "demo"));
   //      RenderingUtil.setPopupsAllowed(false);
   //      System.out.println("\n\nBegin " + Coverage_Db_Suite.class.getSimpleName());
   //   }
   //
   //   @AfterClass
   //   public static void tearDown() throws Exception {
   //      System.out.println("End " + Coverage_Db_Suite.class.getSimpleName());
   //   }

}
