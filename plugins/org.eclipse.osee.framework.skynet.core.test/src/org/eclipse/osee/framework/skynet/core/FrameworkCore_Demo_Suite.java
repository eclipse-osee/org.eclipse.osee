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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCacheQueryTest;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPurgeTest;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTestSuite;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeInheritanceTest;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact_getLastModified;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact_setAttributeValues;
import org.eclipse.osee.framework.skynet.core.artifact.AttributePurgeTest;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPurgeTest;
import org.eclipse.osee.framework.skynet.core.artifact.BranchStateTest;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeManagerTest;
import org.eclipse.osee.framework.skynet.core.artifact.DuplicateHridTest;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifactTest;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeTransactionTest;
import org.eclipse.osee.framework.skynet.core.artifact.RelationDeletionTest;
import org.eclipse.osee.framework.skynet.core.artifact.RelationOrderingTest;
import org.eclipse.osee.framework.skynet.core.artifact.ReplaceAttributeWithTest;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactLoaderTest;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryTestDemo;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactEventFiltersTest;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchEventFiltersTest;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEventLoopbackTest;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEventTest;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventLoopbackTest;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventTest;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifactTest;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelationTest;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifactTest;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventLocalTest;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventRemoteTest;
import org.eclipse.osee.framework.skynet.core.importing.ImportingSuite;
import org.eclipse.osee.framework.skynet.core.importing.parsers.ParsersSuite;
import org.eclipse.osee.framework.skynet.core.relation.CrossBranchLinkTest;
import org.eclipse.osee.framework.skynet.core.relation.RelationTestSuite;
import org.eclipse.osee.framework.skynet.core.revision.ConflictTest;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionTestSuite;
import org.eclipse.osee.framework.skynet.core.utility.CsvArtifactTest;
import org.eclipse.osee.framework.skynet.core.word.UpdateBookmarkIdTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   UserManagerTest.class,
   ReplaceAttributeWithTest.class,
   AttributePurgeTest.class,
   CrossBranchLinkTest.class,
   UpdateBookmarkIdTest.class,
   ArtifactEventFiltersTest.class,
   BranchEventFiltersTest.class,
   BranchEventTest.class,
   BranchEventLoopbackTest.class,
   ArtifactLoaderTest.class,
   ArtifactEventTest.class,
   ArtifactEventLoopbackTest.class,
   TransactionEventLocalTest.class,
   TransactionEventRemoteTest.class,
   EventBasicGuidArtifactTest.class,
   EventBasicGuidRelationTest.class,
   EventChangeTypeBasicGuidArtifactTest.class,
   ArtifactQueryTestDemo.class,
   TransactionTestSuite.class,
   ArtifactTestSuite.class,
   RelationTestSuite.class,
   ArtifactTypeInheritanceTest.class,
   ArtifactPurgeTest.class,
   BranchPurgeTest.class,
   PurgeTransactionTest.class,
   Artifact_setAttributeValues.class,
   Artifact_getLastModified.class,
   CsvArtifactTest.class,
   NativeArtifactTest.class,
   ConflictTest.class,
   ChangeManagerTest.class,
   RelationDeletionTest.class,
   ArtifactCacheQueryTest.class,
   BranchStateTest.class,
   DuplicateHridTest.class,
   RelationOrderingTest.class,
   ImportingSuite.class,
   ParsersSuite.class})
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
