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

package org.eclipse.osee.orcs.core.internal.branch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.CreateBranchData;
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
   @Mock private ResultSet<TransactionToken> results;
   @Mock private ResultSet<Branch> branchResults;
   @Mock private Branch parentBranch;
   // @formatter:on

   private final ArtifactId associatedArtifact = ArtifactId.valueOf(66);
   private final TransactionToken txRecord = TransactionToken.valueOf(99, parentBranch);
   private BranchDataFactory factory;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      when(queryFactory.transactionQuery()).thenReturn(txQuery);
      when(queryFactory.branchQuery()).thenReturn(branchQuery);
      factory = new BranchDataFactory(queryFactory);

      when(branchQuery.andId(txRecord.getBranch())).thenReturn(branchQuery);
      when(branchQuery.getResults()).thenReturn(branchResults);
      when(branchResults.getExactlyOne()).thenReturn(parentBranch);

      when(txQuery.andIsHead(any(BranchId.class))).thenReturn(txQuery);
      when(txQuery.getTokens()).thenReturn(results);
      when(results.getExactlyOne()).thenReturn(txRecord);

      when(txQuery.andTxId(txRecord)).thenReturn(txQuery);

      when(parentBranch.getName()).thenReturn("testParentBranchName");
      when(parentBranch.getId()).thenReturn(44L);
   }

   @Test
   public void testDataForBaselineBranch() {
      BranchToken branch = BranchToken.create("testDataForBaselineBranch");
      CreateBranchData result = factory.createBaselineBranchData(branch, parentBranch, associatedArtifact);

      verify(txQuery).andIsHead(parentBranch);

      String comment = String.format("New Branch from %s (%s)", parentBranch.getName(), txRecord.getId());
      assertData(result, branch.getName(), branch, BranchType.BASELINE, comment, txRecord, associatedArtifact, false);
   }

   @Test
   public void testDataForWorkingBranch() {
      BranchToken branch = BranchToken.create("testDataForWorkingBranch");

      CreateBranchData result = factory.createWorkingBranchData(branch, parentBranch, associatedArtifact);
      verify(txQuery).andIsHead(parentBranch);

      String comment = String.format("New Branch from %s (%s)", parentBranch.getName(), txRecord.getId());
      assertData(result, branch.getName(), branch, BranchType.WORKING, comment, txRecord, associatedArtifact, false);
   }

   @Test
   public void testDataForCopyTxBranch() {
      BranchToken branch = BranchToken.create("testDataForCopyTxBranch");

      CreateBranchData result = factory.createCopyTxBranchData(branch, txRecord, ArtifactId.SENTINEL);

      verify(txQuery).andTxId(txRecord);
      verify(branchQuery).andId(txRecord.getBranch());

      String comment = String.format("Transaction %d copied from %s to create Branch %s", txRecord.getId(),
         parentBranch.getName(), branch.getName());
      assertData(result, branch.getName(), branch, BranchType.WORKING, comment, txRecord, ArtifactId.SENTINEL, true);
   }

   @Test
   public void testDataForPortBranch() {
      BranchToken branch = BranchToken.create("testDataForPortBranch");

      CreateBranchData result = factory.createPortBranchData(branch, txRecord, ArtifactId.SENTINEL);

      verify(txQuery).andTxId(txRecord);
      verify(branchQuery).andId(txRecord.getBranch());

      String comment = String.format("Transaction %d ported from %s to create Branch %s", txRecord.getId(),
         parentBranch.getName(), branch.getName());
      assertData(result, branch.getName(), branch, BranchType.PORT, comment, txRecord, ArtifactId.SENTINEL, true);
   }

   private static void assertData(CreateBranchData actual, String branchName, BranchId branch, BranchType type, String comment, TransactionId fromTx, ArtifactId associatedArtifact, boolean isCopyFromTx) {
      assertEquals(branchName, actual.getName());
      assertEquals(branch, actual.getBranch());

      assertEquals(type, actual.getBranchType());
      assertEquals(comment, actual.getCreationComment());
      assertEquals(fromTx, actual.getFromTransaction());

      assertEquals(Id.SENTINEL, actual.getMergeAddressingQueryId());
      assertEquals(BranchId.SENTINEL, actual.getMergeDestinationBranchId());

      assertEquals(associatedArtifact, actual.getAssociatedArtifact());

      assertEquals(isCopyFromTx, actual.isTxCopyBranchType());
   }
}
