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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AccessControlTest.class,
   ArtifactEventLoopbackTest.class,
   ArtifactEventTest.class,
   ArtifactTopicEventLoopbackTest.class,
   ArtifactTopicEventTest.class,
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
   ChangeDataTest.class,
   ChangeManagerTest.class,
   ComputedCharacteristicTest.class,
   ConflictIntroduceTest.class,
   ConflictTest.class,
   CreateBranchOperationTest.class,
   CrossBranchRelationLinkTest.class,
   CsvArtifactTest.class,
   EnumAttributeMultiplicityTest.class,
   IntegerAttributeTest.class,
   FrameworkEventToRemoteEventListenerTest.class,
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
   RevisionChangeTest.class,
   SkynetTransactionTest.class,
   TransactionEventLocalTest.class,
   TransactionEventRemoteTest.class,
   TransactionManagerTest.class,
   WordMlLinkHandlerTest.class,
   WordOutlineTest.class})

/**
 * @author Roberto E. Escobar
 */
public final class SkynetCoreIntegrationTestSuite {
   // Test Suite
}
