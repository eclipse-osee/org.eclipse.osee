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
package org.eclipse.osee.framework.skynet.core.test.integration;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ArtifactCacheQueryTest.class,
   ArtifactEventFiltersTest.class,
   ArtifactEventLoopbackTest.class,
   ArtifactEventTest.class,
   ArtifactGetLastModifiedTest.class,
   ArtifactLoaderTest.class,
   ArtifactPurgeTest.class,
   ArtifactQueryTest.class,
   ArtifactSetAttributeValuesTest.class,
   ArtifactTest.class,
   ArtifactTypeInheritanceTest.class,
   AttributePurgeTest.class,
   BranchEventFiltersTest.class,
   BranchEventLoopbackTest.class,
   BranchEventTest.class,
   BranchManagerTest.class,
   BranchPurgeTest.class,
   BranchStateTest.class,
   ChangeManagerTest.class,
   ConflictDeletionTest.class,
   ConflictTest.class,
   CreateBranchOperationTest.class,
   CrossBranchRelationLinkTest.class,
   CsvArtifactTest.class,
   DuplicateHridTest.class,
   FrameworkEventToRemoteEventListenerTest.class,
   LoadDeletedRelationTest.class,
   NativeArtifactTest.class,
   OseeEnumerationValidationTest.class,
   PurgeTransactionTest.class,
   RelationCacheTest.class,
   RelationDeletionTest.class,
   RelationFilterUtilTest.class,
   RelationOrderingTest.class,
   RelationOrderMergeUtilityTest.class,
   RoughArtifactTest.class,
   SkynetTransactionTest.class,
   TransactionEventLocalTest.class,
   TransactionEventRemoteTest.class,
   TransactionManagerTest.class,
   UserManagerTest.class,
   WordMlLinkHandlerTest.class,
   WordOutlineTest.class})
public final class XSkynetCoreIntegrationTestSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + XSkynetCoreIntegrationTestSuite.class.getSimpleName());
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + XSkynetCoreIntegrationTestSuite.class.getSimpleName());
   }

}
