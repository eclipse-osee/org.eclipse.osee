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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AccessControlTest.class,
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
   AttributeResourceTest.class,
   BranchEventLoopbackTest.class,
   BranchEventTest.class,
   BranchFavoriteTest.class,
   BranchManagerTest.class,
   // BranchPurgeTest moved to LongRunningTestSuite
   // BranchStateTest moved to LongRunningTestSuite
   ChangeManagerTest.class,
   ConflictDeletionTest.class,
   ConflictIntroduceTest.class,
   ConflictTest.class,
   CreateBranchOperationTest.class,
   CrossBranchRelationLinkTest.class,
   CsvArtifactTest.class,
   DoorsArtifactExtractorTest.class,
   IntegerAttributeTest.class,
   FrameworkEventToRemoteEventListenerTest.class,
   LoadDeletedRelationTest.class,
   MergeManagerTest.class,
   NativeArtifactTest.class,
   OseeEnumerationValidationTest.class,
   OseeInfoTest.class,
   PurgeArtifactsTest.class,
   // PurgeTransactionTest moved to LongRunningTestSuite
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
   WordOutlineTest.class,
   ChangeDataTest.class})
/**
 * @author Roberto E. Escobar
 */
public final class XSkynetCoreIntegrationTestSuite {
   // Test Suite
}
