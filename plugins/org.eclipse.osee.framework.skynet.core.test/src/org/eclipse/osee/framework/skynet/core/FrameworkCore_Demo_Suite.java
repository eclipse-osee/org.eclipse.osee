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
package org.eclipse.osee.framework.skynet.core;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.event.systems.FrameworkEventToRemoteEventListenerTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({FrameworkEventToRemoteEventListenerTest.class,
//   BranchSuite.class,
//   UserManagerTest.class,
//   ReplaceAttributeWithTest.class,
//   AttributePurgeTest.class,
//   CrossBranchLinkTest.class,
//   UpdateBookmarkIdTest.class,
//   ArtifactEventFiltersTest.class,
//   BranchEventFiltersTest.class,
//   BranchEventTest.class,
//   BranchEventLoopbackTest.class,
//   ArtifactLoaderTest.class,
//   ArtifactEventTest.class,
//   ArtifactEventLoopbackTest.class,
//   TransactionEventLocalTest.class,
//   TransactionEventRemoteTest.class,
//   EventBasicGuidArtifactTest.class,
//   EventBasicGuidRelationTest.class,
//   EventChangeTypeBasicGuidArtifactTest.class,
//   ArtifactQueryTestDemo.class,
//   TransactionTestSuite.class,
//   ArtifactTestSuite.class,
//   RelationTestSuite.class,
//   ArtifactTypeInheritanceTest.class,
//   ArtifactPurgeTest.class,
//   BranchPurgeTest.class,
//   PurgeTransactionTest.class,
//   Artifact_setAttributeValues.class,
//   Artifact_getLastModified.class,
//   CsvArtifactTest.class,
//   NativeArtifactTest.class,
//   ConflictTest.class,
//   ChangeManagerTest.class,
//   RelationDeletionTest.class,
//   ArtifactCacheQueryTest.class,
//   BranchStateTest.class,
//   DuplicateHridTest.class,
//   RelationOrderingTest.class,
//   ImportingSuite.class,
//   ParsersSuite.class
})
/**
 * @author Donald G. Dunne
 */
public final class FrameworkCore_Demo_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + FrameworkCore_Demo_Suite.class.getSimpleName());
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + FrameworkCore_Demo_Suite.class.getSimpleName());
   }

}
