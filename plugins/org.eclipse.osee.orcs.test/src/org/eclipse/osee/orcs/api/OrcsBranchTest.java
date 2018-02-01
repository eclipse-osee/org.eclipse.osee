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
package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.framework.core.data.ArtifactId.SENTINEL;
import static org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem;
import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchReadable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * @author David W. Miller
 */
public class OrcsBranchTest {

   private static final String ARTIFACT_NAME = "Joe Smith";

   @Rule
   public TestRule osgi = integrationRule(this);

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private OrcsApi orcsApi;

   private OrcsBranch branchOps;
   private QueryFactory query;
   private TransactionFactory txFactory;

   @Before
   public void setUp() throws Exception {
      branchOps = orcsApi.getBranchOps();
      query = orcsApi.getQueryFactory();
      txFactory = orcsApi.getTransactionFactory();
   }

   @Test
   public void testCreateBranch() throws Exception {
      int SOURCE_TX_ID = 8; // Chosen starting transaction on Common Branch
      int CHANGED_TX_ID = 9; // Transaction containing tested change

      // set up the initial branch
      IOseeBranch branch = IOseeBranch.create("PriorBranch");

      UserId author = OseeSystem;

      TransactionId tx = TransactionId.valueOf(SOURCE_TX_ID);
      Callable<BranchReadable> callable = branchOps.createCopyTxBranch(branch, author, tx, ArtifactId.SENTINEL);

      assertNotNull(callable);
      BranchReadable priorBranch = callable.call();

      // in the database, on the common branch, the users are all created in transaction 9
      // the common branch will have one user named Joe Smith

      int coreResult = query.fromBranch(CoreBranches.COMMON).andNameEquals(ARTIFACT_NAME).getResults().size();
      assertEquals(1, coreResult);

      // we copied the branch at transaction 8, so, on the copied branch there will not be any
      // user Joe Smith

      int priorResult = query.fromBranch(priorBranch).andNameEquals(ARTIFACT_NAME).getResults().size();
      assertEquals(0, priorResult);

      // finally, we copy another branch at transaction id 9, this is the transaction that added the
      // user Joe Smith, so if the code is correct, and the copy includes the final
      // transaction, then this will produce the same result as the query of the common branch
      // create the branch with the copied transaction
      IOseeBranch postbranch = IOseeBranch.create("PostBranch");

      TransactionId tx1 = TransactionId.valueOf(CHANGED_TX_ID);
      Callable<BranchReadable> postCallable =
         branchOps.createCopyTxBranch(postbranch, author, tx1, ArtifactId.SENTINEL);

      assertNotNull(postCallable);
      BranchReadable postBranch = postCallable.call();

      int postResult = query.fromBranch(postBranch).andNameEquals(ARTIFACT_NAME).getResults().size();
      assertEquals(1, postResult);
   }

   @Test
   public void testCreateBranchCopyFromTx() throws Exception {
      // this test shows that the change report for a transaction for the newly copied branch is
      // the same as the change report on the branch the transaction is copied from
      TransactionId PRIOR_TX_ID = TransactionId.valueOf(15);
      TransactionId SOURCE_TX_ID = TransactionId.valueOf(16);

      // get the list of changes from the original branch
      TransactionToken priorTx = query.transactionQuery().andTxId(PRIOR_TX_ID).getResults().getExactlyOne();
      TransactionToken sourceTx = query.transactionQuery().andTxId(SOURCE_TX_ID).getResults().getExactlyOne();
      Callable<List<ChangeItem>> callable = branchOps.compareBranch(priorTx, sourceTx);
      List<ChangeItem> priorItems = callable.call();

      // create the branch with the copied transaction
      IOseeBranch branch = IOseeBranch.create("CopiedBranch");

      Callable<BranchReadable> callableBranch =
         branchOps.createCopyTxBranch(branch, OseeSystem, SOURCE_TX_ID, ArtifactId.SENTINEL);

      // the new branch will contain two transactions - these should have the same change report as the original branch
      BranchReadable postBranch = callableBranch.call();

      callable = branchOps.compareBranch(postBranch);
      List<ChangeItem> newItems = callable.call();
      compareBranchChanges(priorItems, newItems);
   }

   @Test
   public void testCommitBranchMissingArtifactsOnDestination() throws Exception {
      UserId author = OseeSystem;
      // set up the initial branch
      IOseeBranch branch = IOseeBranch.create("BaseBranch");

      Callable<BranchReadable> callableBranch = branchOps.createTopLevelBranch(branch, author);
      BranchReadable base = callableBranch.call();
      // put some changes on the base branch
      TransactionBuilder tx = txFactory.createTransaction(base, author, "add some changes");
      ArtifactId folder = tx.createArtifact(CoreArtifactTypes.Folder, "BaseFolder");
      tx.commit();

      // create working branch off of base to make some changes
      // set up the child branch
      IOseeBranch branchName = IOseeBranch.create("ChildBranch");
      Callable<BranchReadable> callableChildBranch =
         branchOps.createWorkingBranch(branchName, author, base, ArtifactId.SENTINEL);

      BranchReadable childBranch = callableChildBranch.call();

      TransactionBuilder tx2 = txFactory.createTransaction(childBranch, author, "modify and make new arts");
      ArtifactReadable readableFolder = query.fromBranch(childBranch).andId(folder).getResults().getExactlyOne();

      tx2.setName(readableFolder, "New Folder Name");
      tx2.setSoleAttributeFromString(readableFolder, CoreAttributeTypes.StaticId, "test id");

      // new artifacts should come across as new
      tx2.createArtifact(CoreArtifactTypes.Folder, "childBranch folder");
      tx2.commit();

      List<ChangeItem> expectedChanges = branchOps.compareBranch(childBranch).call();

      // create a disjoint working branch from common

      IOseeBranch commonName = IOseeBranch.create("ChildFromCommonBranch");
      Callable<BranchReadable> callableBranchFromCommon =
         branchOps.createWorkingBranch(commonName, author, CoreBranches.COMMON, ArtifactId.SENTINEL);
      BranchReadable commonChildBranch = callableBranchFromCommon.call();

      branchOps.commitBranch(author, childBranch, commonChildBranch).call();

      List<ChangeItem> actualChanges = branchOps.compareBranch(commonChildBranch).call();
      ensureExpectedAreInActual(expectedChanges, actualChanges);
   }

   @Test
   public void testBranchUpdateFields() throws Exception {
      String branchName = testName.getMethodName();

      IOseeBranch branch = IOseeBranch.create(branchName);

      branchOps.createBaselineBranch(branch, OseeSystem, CoreBranches.SYSTEM_ROOT, SENTINEL).call();

      BranchReadable actual = getBranch(branch);
      assertBranch(actual, branchName, BranchState.CREATED, BranchType.BASELINE, SENTINEL);

      branchName = "another-name";
      branchOps.changeBranchName(branch, branchName).call();

      actual = getBranch(branch);
      assertBranch(actual, branchName, BranchState.CREATED, BranchType.BASELINE, SENTINEL);

      BranchState branchState = BranchState.DELETED;
      branchOps.changeBranchState(branch, branchState).call();

      actual = getBranch(branch);
      assertBranch(actual, branchName, branchState, BranchType.BASELINE, SENTINEL);

      BranchType branchType = BranchType.WORKING;
      branchOps.changeBranchType(branch, branchType).call();

      actual = getBranch(branch);
      assertBranch(actual, branchName, branchState, branchType, SENTINEL);

      UserId assocArtifact = OseeSystem;
      branchOps.associateBranchToArtifact(branch, assocArtifact).call();

      actual = getBranch(branch);
      assertBranch(actual, branchName, branchState, branchType, assocArtifact);

      branchOps.unassociateBranch(branch).call();

      actual = getBranch(branch);
      assertBranch(actual, branchName, branchState, branchType, SENTINEL);
   }

   private void assertBranch(BranchReadable branch, String name, BranchState state, BranchType type, ArtifactId assocArtId) {
      assertEquals(name, branch.getName());
      assertEquals(state, branch.getBranchState());
      assertEquals(type, branch.getBranchType());
      assertEquals(assocArtId, branch.getAssociatedArtifact());
   }

   private BranchReadable getBranch(BranchId branch) {
      return query.branchQuery().andId(branch).getResults().getExactlyOne();
   }

   private void ensureExpectedAreInActual(List<ChangeItem> expected, List<ChangeItem> actual) {
      for (ChangeItem expect : expected) {
         boolean contains = actual.contains(expect);
         if (!contains) {
            for (ChangeItem act : actual) {
               if (act.getItemId().equals(expect.getItemId()) && act.getArtId().equals(
                  expect.getArtId()) && act.getCurrentVersion().getModType().matches(ModificationType.INTRODUCED)) {
                  contains = true;
                  break;
               }
            }
            assertTrue(contains);
         }
      }
   }

   private void compareBranchChanges(List<ChangeItem> priorItems, List<ChangeItem> newItems) {
      Collections.sort(priorItems);
      Collections.sort(newItems);
      assertEquals(priorItems, newItems);
   }
}