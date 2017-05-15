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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.BranchReadable;
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
   @Mock private ResultSet<BranchReadable> branchResults;
   @Mock private BranchReadable parentBranch;
   // @formatter:on

   private final ArtifactId associatedArtifact = ArtifactId.valueOf(66);
   private final ArtifactId author = ArtifactId.valueOf(55);
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
   public void testDataForTopLevelBranch() throws OseeCoreException {
      IOseeBranch branch = IOseeBranch.create("testDataForTopLevelBranch");
      CreateBranchData result = factory.createTopLevelBranchData(branch, author);

      verify(txQuery).andIsHead(CoreBranches.SYSTEM_ROOT);

      String comment = String.format("New Branch from %s (%s)", CoreBranches.SYSTEM_ROOT.getName(), txRecord);
      assertData(result, branch.getName(), branch, BranchType.BASELINE, comment, txRecord, author, ArtifactId.SENTINEL,
         false);
   }

   @Test
   public void testDataForBaselineBranch() throws OseeCoreException {
      IOseeBranch branch = IOseeBranch.create("testDataForBaselineBranch");
      CreateBranchData result = factory.createBaselineBranchData(branch, author, parentBranch, associatedArtifact);

      verify(txQuery).andIsHead(parentBranch);

      String comment = String.format("New Branch from %s (%s)", parentBranch.getName(), txRecord.getId());
      assertData(result, branch.getName(), branch, BranchType.BASELINE, comment, txRecord, author, associatedArtifact,
         false);
   }

   @Test
   public void testDataForWorkingBranch() throws OseeCoreException {
      IOseeBranch branch = IOseeBranch.create("testDataForWorkingBranch");

      CreateBranchData result = factory.createWorkingBranchData(branch, author, parentBranch, associatedArtifact);
      verify(txQuery).andIsHead(parentBranch);

      String comment = String.format("New Branch from %s (%s)", parentBranch.getName(), txRecord.getId());
      assertData(result, branch.getName(), branch, BranchType.WORKING, comment, txRecord, author, associatedArtifact,
         false);
   }

   @Test
   public void testDataForCopyTxBranch() throws OseeCoreException {
      IOseeBranch branch = IOseeBranch.create("testDataForCopyTxBranch");

      CreateBranchData result = factory.createCopyTxBranchData(branch, author, txRecord, ArtifactId.SENTINEL);

      verify(txQuery).andTxId(txRecord);
      verify(branchQuery).andId(txRecord.getBranch());

      String comment = String.format("Transaction %d copied from %s to create Branch %s", txRecord.getId(),
         parentBranch.getName(), branch.getName());
      assertData(result, branch.getName(), branch, BranchType.WORKING, comment, txRecord, author, ArtifactId.SENTINEL,
         true);
   }

   @Test
   public void testDataForPortBranch() throws OseeCoreException {
      IOseeBranch branch = IOseeBranch.create("testDataForPortBranch");

      CreateBranchData result = factory.createPortBranchData(branch, author, txRecord, ArtifactId.SENTINEL);

      verify(txQuery).andTxId(txRecord);
      verify(branchQuery).andId(txRecord.getBranch());

      String comment = String.format("Transaction %d ported from %s to create Branch %s", txRecord.getId(),
         parentBranch.getName(), branch.getName());
      assertData(result, branch.getName(), branch, BranchType.PORT, comment, txRecord, author, ArtifactId.SENTINEL,
         true);
   }

   private static void assertData(CreateBranchData actual, String branchName, BranchId branch, BranchType type, String comment, TransactionId fromTx, ArtifactId author, ArtifactId associatedArtifact, boolean isCopyFromTx) {
      assertEquals(branchName, actual.getName());
      assertEquals(branch, actual.getBranch());

      assertEquals(type, actual.getBranchType());
      assertEquals(comment, actual.getCreationComment());
      assertEquals(fromTx, actual.getFromTransaction());

      assertEquals(-1, actual.getMergeAddressingQueryId());
      assertEquals(BranchId.SENTINEL, actual.getMergeDestinationBranchId());

      assertEquals(author, actual.getAuthor());
      assertEquals(associatedArtifact, actual.getAssociatedArtifact());

      assertEquals(isCopyFromTx, actual.isTxCopyBranchType());
   }
}