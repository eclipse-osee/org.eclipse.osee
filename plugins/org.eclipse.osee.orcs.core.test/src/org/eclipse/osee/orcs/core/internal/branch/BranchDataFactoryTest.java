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
package org.eclipse.osee.orcs.core.internal.branch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**  */
public class BranchDataFactoryTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private BranchQuery branchQuery;
   @Mock private TransactionQuery txQuery;
   @Mock private QueryFactory queryFactory;
   
   @Mock private IOseeBranch branch;
   @Mock private IOseeBranch parentToken;
   @Mock private BranchReadable parent;
   @Mock private ArtifactReadable author;
   @Mock private ArtifactReadable associatedArtifact;
   @Mock private TransactionReadable txRecord;
   @Mock private ResultSet<TransactionReadable> results;
   @Mock private ResultSet<BranchReadable> branchResults;
   // @formatter:on

   private BranchDataFactory factory;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      when(author.getLocalId()).thenReturn(55);
      when(associatedArtifact.getLocalId()).thenReturn(66);
      when(queryFactory.transactionQuery()).thenReturn(txQuery);
      when(queryFactory.branchQuery()).thenReturn(branchQuery);
      factory = new BranchDataFactory(queryFactory);
   }

   @Test
   public void testDataForTopLevelBranch() throws OseeCoreException {
      String branchName = "testDataForTopLevelBranch";
      Long branchUuid = Lib.generateUuid();
      when(branch.getName()).thenReturn(branchName);
      when(branch.getUuid()).thenReturn(branchUuid);

      when(txQuery.andIsHead(CoreBranches.SYSTEM_ROOT)).thenReturn(txQuery);
      when(txQuery.getResults()).thenReturn(results);
      when(results.getExactlyOne()).thenReturn(txRecord);

      CreateBranchData result = factory.createTopLevelBranchData(branch, author);

      verify(txQuery).andIsHead(CoreBranches.SYSTEM_ROOT);

      String comment = "Branch Creation for " + branchName;
      assertData(result, branchName, branchUuid, BranchType.BASELINE, comment, txRecord, author, null, false);
   }

   @Test
   public void testDataForBaselineBranch() throws OseeCoreException {
      String branchName = "testDataForBaselineBranch";
      Long branchUuid = Lib.generateUuid();
      when(branch.getName()).thenReturn(branchName);
      when(branch.getUuid()).thenReturn(branchUuid);

      when(txQuery.andIsHead(parentToken)).thenReturn(txQuery);
      when(txQuery.getResults()).thenReturn(results);
      when(results.getExactlyOne()).thenReturn(txRecord);

      CreateBranchData result = factory.createBaselineBranchData(branch, author, parentToken, associatedArtifact);

      verify(txQuery).andIsHead(parentToken);

      String comment = "Branch Creation for " + branchName;
      assertData(result, branchName, branchUuid, BranchType.BASELINE, comment, txRecord, author, associatedArtifact,
         false);
   }

   @Test
   public void testDataForWorkingBranch() throws OseeCoreException {
      String branchName = "testDataForWorkingBranch";
      String parentName = "testParentBranchName";
      Long branchUuid = Lib.generateUuid();
      when(branch.getName()).thenReturn(branchName);
      when(branch.getUuid()).thenReturn(branchUuid);
      when(parentToken.getName()).thenReturn(parentName);

      when(txQuery.andIsHead(parentToken)).thenReturn(txQuery);
      when(txQuery.getResults()).thenReturn(results);
      when(results.getExactlyOne()).thenReturn(txRecord);

      CreateBranchData result = factory.createWorkingBranchData(branch, author, parentToken, associatedArtifact);

      verify(txQuery).andIsHead(parentToken);

      String comment = String.format("New Branch from %s (%s)", parentName, txRecord.getGuid());
      assertData(result, branchName, branchUuid, BranchType.WORKING, comment, txRecord, author, associatedArtifact,
         false);
   }

   @Test
   public void testDataForCopyTxBranch() throws OseeCoreException {
      String branchName = "testDataForCopyTxBranch";
      String parentName = "testParentBranchName";
      Long branchUuid = Lib.generateUuid();
      when(branch.getName()).thenReturn(branchName);
      when(branch.getUuid()).thenReturn(branchUuid);
      when(parent.getName()).thenReturn(parentName);

      ITransaction tx = TokenFactory.createTransaction(99);

      when(txQuery.andTxId(tx.getGuid())).thenReturn(txQuery);
      when(txQuery.getResults()).thenReturn(results);
      when(results.getExactlyOne()).thenReturn(txRecord);
      when(txRecord.getBranchId()).thenReturn(44L);
      when(txRecord.getGuid()).thenReturn(99);

      when(branchQuery.andUuids(txRecord.getBranchId())).thenReturn(branchQuery);
      when(branchQuery.getResults()).thenReturn(branchResults);
      when(branchResults.getExactlyOne()).thenReturn(parent);

      CreateBranchData result = factory.createCopyTxBranchData(branch, author, tx, null);

      verify(txQuery).andTxId(99);
      verify(branchQuery).andUuids(txRecord.getBranchId());

      String comment = String.format("Transaction %d copied from %s to create Branch %s", 99, parentName, branchName);
      assertData(result, branchName, branchUuid, BranchType.WORKING, comment, txRecord, author, null, true);
   }

   @Test
   public void testDataForPortBranch() throws OseeCoreException {
      String branchName = "testDataForPortBranch";
      String parentName = "testParentBranchName";
      Long branchUuid = Lib.generateUuid();
      when(branch.getName()).thenReturn(branchName);
      when(branch.getUuid()).thenReturn(branchUuid);
      when(parent.getName()).thenReturn(parentName);

      ITransaction tx = TokenFactory.createTransaction(99);

      when(txQuery.andTxId(tx.getGuid())).thenReturn(txQuery);
      when(txQuery.getResults()).thenReturn(results);
      when(results.getExactlyOne()).thenReturn(txRecord);
      when(txRecord.getGuid()).thenReturn(99);
      when(txRecord.getBranchId()).thenReturn(44L);

      when(branchQuery.andUuids(txRecord.getBranchId())).thenReturn(branchQuery);
      when(branchQuery.getResults()).thenReturn(branchResults);
      when(branchResults.getExactlyOne()).thenReturn(parent);

      CreateBranchData result = factory.createPortBranchData(branch, author, tx, null);

      verify(txQuery).andTxId(99);
      verify(branchQuery).andUuids(44L);

      String comment = String.format("Transaction %d ported from %s to create Branch %s", 99, parentName, branchName);
      assertData(result, branchName, branchUuid, BranchType.PORT, comment, txRecord, author, null, true);
   }

   private static void assertData(CreateBranchData actual, String branchName, Long branchUuid, BranchType type, String comment, TransactionReadable fromTx, ArtifactReadable author, ArtifactReadable associatedArtifact, boolean isCopyFromTx) {
      assertEquals(branchName, actual.getName());
      assertEquals(branchUuid, actual.getGuid());

      assertEquals(type, actual.getBranchType());
      assertEquals(comment, actual.getCreationComment());
      assertEquals(fromTx, actual.getFromTransaction());

      assertEquals(-1, actual.getMergeAddressingQueryId());
      assertEquals(-1L, actual.getMergeDestinationBranchId());

      assertEquals(author, actual.getUserArtifact());
      assertEquals(author.getLocalId(), actual.getUserArtifactId());

      assertEquals(associatedArtifact, actual.getAssociatedArtifact());

      int assocArtId = associatedArtifact == null ? -1 : associatedArtifact.getLocalId();
      assertEquals(assocArtId, actual.getAssociatedArtifactId());

      assertEquals(isCopyFromTx, actual.isTxCopyBranchType());
   }
}
