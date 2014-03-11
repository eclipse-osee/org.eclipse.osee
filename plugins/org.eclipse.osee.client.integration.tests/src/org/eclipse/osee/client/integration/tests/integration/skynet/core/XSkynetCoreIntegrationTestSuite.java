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
   ArtifactCacheQueryTest.class,
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
   DoorsArtifactExtractorTest.class,
   FrameworkEventToRemoteEventListenerTest.class,
   LoadDeletedRelationTest.class,
   MergeManagerTest.class,
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
   WordOutlineTest.class,
   ChangeDataTest.class})
public final class XSkynetCoreIntegrationTestSuite {
   // Test Suite
}
