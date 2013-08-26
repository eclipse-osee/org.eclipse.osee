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

import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author David W. Miller
 */
public class OrcsBranchTest {

   private static final String ARTIFACT_NAME = "Joe Smith";

   @Rule
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   @OsgiService
   private OrcsApi orcsApi;

   private OrcsBranch branchInterface;
   private QueryFactory query;
   private TransactionFactory txFactory;

   @Before
   public void setUp() throws Exception {
      ApplicationContext context = null;
      branchInterface = orcsApi.getBranchOps(context);
      query = orcsApi.getQueryFactory(context);
      txFactory = orcsApi.getTransactionFactory(context);
   }

   @Test
   public void testCreateBranch() throws Exception {
      int SOURCE_TX_ID = 7; // Chosen starting transaction on Common Branch
      int CHANGED_TX_ID = 8; // Transaction containing tested change

      // set up the initial branch
      IOseeBranch branch = TokenFactory.createBranch(GUID.create(), "PriorBranch");

      ArtifactReadable author = getSystemUser();

      ITransaction tx = TokenFactory.createTransaction(SOURCE_TX_ID);
      Callable<ReadableBranch> callable = branchInterface.createCopyTxBranch(branch, author, tx, null);

      assertNotNull(callable);
      ReadableBranch priorBranch = callable.call();

      // in the database, on the common branch, the users are all created in transaction 8
      // the common branch will have one user named Joe Smith

      int coreResult = query.fromBranch(CoreBranches.COMMON).andNameEquals(ARTIFACT_NAME).getResults().size();
      assertEquals(1, coreResult);

      // we copied the branch at transaction 7, so, on the copied branch there will not be any
      // user Joe Smith

      int priorResult = query.fromBranch(priorBranch).andNameEquals(ARTIFACT_NAME).getResults().size();
      assertEquals(0, priorResult);

      // finally, we copy another branch at transaction id 8, this is the transaction that added the 
      // user Joe Smith, so if the code is correct, and the copy includes the final 
      // transaction, then this will produce the same result as the query of the common branch
      // create the branch with the copied transaction
      IOseeBranch postbranch = TokenFactory.createBranch(GUID.create(), "PostBranch");

      ITransaction tx1 = TokenFactory.createTransaction(CHANGED_TX_ID);
      Callable<ReadableBranch> postCallable = branchInterface.createCopyTxBranch(postbranch, author, tx1, null);

      assertNotNull(postCallable);
      ReadableBranch postBranch = postCallable.call();

      int postResult = query.fromBranch(postBranch).andNameEquals(ARTIFACT_NAME).getResults().size();
      assertEquals(1, postResult);
   }

   @Test
   public void testCreateBranchCopyFromTx() throws Exception {
      // this test shows that the change report for a transaction for the newly copied branch is 
      // the same as the change report on the branch the transaction is copied from
      int PRIOR_TX_ID = 15;
      int SOURCE_TX_ID = 16;

      // get the list of changes from the original branch
      Callable<List<ChangeItem>> callable =
         branchInterface.compareBranch(TokenFactory.createTransaction(PRIOR_TX_ID),
            TokenFactory.createTransaction(SOURCE_TX_ID));
      List<ChangeItem> priorItems = callable.call();

      // create the branch with the copied transaction
      IOseeBranch branch = TokenFactory.createBranch(GUID.create(), "CopiedBranch");

      ArtifactReadable author = getSystemUser();

      ITransaction tx = TokenFactory.createTransaction(SOURCE_TX_ID);
      Callable<ReadableBranch> callableBranch = branchInterface.createCopyTxBranch(branch, author, tx, null);

      // the new branch will contain two transactions - these should have the same change report as the original branch
      ReadableBranch postBranch = callableBranch.call();

      callable = branchInterface.compareBranch(postBranch);
      List<ChangeItem> newItems = callable.call();
      compareBranchChanges(priorItems, newItems);
   }

   @Test
   public void testCommitBranchMissingArtifactsOnDestination() throws Exception {
      ArtifactReadable author =
         query.fromBranch(CoreBranches.COMMON).andNameEquals("OSEE System").getResults().getExactlyOne();
      // set up the initial branch
      IOseeBranch branch = TokenFactory.createBranch(GUID.create(), "BaseBranch");

      Callable<ReadableBranch> callableBranch = branchInterface.createTopLevelBranch(branch, author);
      ReadableBranch base = callableBranch.call();
      // put some changes on the base branch
      OrcsTransaction tx = txFactory.createTransaction(base, author, "add some changes");
      ArtifactWriteable folder = tx.createArtifact(CoreArtifactTypes.Folder, "BaseFolder");
      tx.commit();

      // create working branch off of base to make some changes
      // set up the child branch
      IOseeBranch branchName = TokenFactory.createBranch(GUID.create(), "ChildBranch");
      Callable<ReadableBranch> callableChildBranch =
         branchInterface.createWorkingBranch(branchName, author, base, null);

      ReadableBranch childBranch = callableChildBranch.call();

      OrcsTransaction tx2 = txFactory.createTransaction(childBranch, author, "modify and make new arts");
      ArtifactReadable readableFolder =
         query.fromBranch(childBranch).andGuidsOrHrids(folder.getGuid()).getResults().getExactlyOne();

      // modifying this artifact should cause it to get introduced
      ArtifactWriteable writeableFolder = tx2.asWriteable(readableFolder);
      writeableFolder.setName("New Folder Name");
      writeableFolder.setSoleAttributeFromString(CoreAttributeTypes.StaticId, "test id");

      // new artifacts should come across as new
      tx2.createArtifact(CoreArtifactTypes.Folder, "childBranch folder");
      tx2.commit();

      List<ChangeItem> expectedChanges = branchInterface.compareBranch(childBranch).call();

      // create a disjoint working branch from common

      IOseeBranch commonName = TokenFactory.createBranch(GUID.create(), "ChildFromCommonBranch");
      Callable<ReadableBranch> callableBranchFromCommon =
         branchInterface.createWorkingBranch(commonName, author, CoreBranches.COMMON, null);
      ReadableBranch commonChildBranch = callableBranchFromCommon.call();

      branchInterface.commitBranch(author, childBranch, commonChildBranch).call();

      List<ChangeItem> actualChanges = branchInterface.compareBranch(commonChildBranch).call();
      ensureExpectedAreInActual(expectedChanges, actualChanges);
   }

   private void ensureExpectedAreInActual(List<ChangeItem> expected, List<ChangeItem> actual) {
      for (ChangeItem expect : expected) {
         boolean contains = actual.contains(expect);
         if (!contains) {
            for (ChangeItem act : actual) {
               if (act.getItemId() == expect.getItemId() && act.getArtId() == expect.getArtId() && act.getCurrentVersion().getModType().matches(
                  ModificationType.INTRODUCED)) {
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

   private ArtifactReadable getSystemUser() throws OseeCoreException {
      return query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

}
