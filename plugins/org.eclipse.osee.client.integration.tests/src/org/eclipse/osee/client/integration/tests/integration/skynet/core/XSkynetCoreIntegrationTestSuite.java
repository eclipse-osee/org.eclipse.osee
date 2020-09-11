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
   ArtifactTypeEventFilterTest.class,
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
   RelationDeletionTest.class,
   RelationFilterUtilTest.class,
   RelationOrderingTest.class,
   RelationOrderMergeUtilityTest.class,
   SkynetTransactionTest.class,
   TransactionEventLocalTest.class,
   TransactionEventRemoteTest.class,
   TransactionManagerTest.class,
   UserManagerTest.class,
   WordMlLinkHandlerTest.class,
   WordOutlineTest.class,
   ChangeDataTest.class,
   EnumAttributeMultiplicityTest.class})
/**
 * @author Roberto E. Escobar
 */
public final class XSkynetCoreIntegrationTestSuite {
   // Test Suite
}
