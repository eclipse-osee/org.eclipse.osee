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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsIntegrationRule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author David Miller
 */

public class OrcsBranchTest {

   @Rule
   public OrcsIntegrationRule osgi = new OrcsIntegrationRule(this);

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   private OrcsBranch branchInterface = null;
   private final ApplicationContext context = null; // TODO use real application context

   private final static String ARTIFACT_NAME = "Joe Smith";

   @OsgiService
   OrcsApi orcsApi;

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
      branchInterface = orcsApi.getBranchOps(context);
   }

   @Test
   public void testCreateBranch() throws Exception {
      int SOURCE_TX_ID = 13; // Chosen starting transaction on Common Branch
      int CHANGED_TX_ID = 14; // Transaction containing tested change

      // set up the query factory for the test
      QueryFactory qf = orcsApi.getQueryFactory(context);
      //      ReadableArtifact createdBy =
      //         qf.fromBranch(CoreBranches.COMMON).andNameEquals(ARTIFACT_NAME).getResults().getExactlyOne();
      ArtifactReadable createdBy =
         qf.fromBranch(CoreBranches.COMMON).andNameEquals("OSEE System").getResults().getExactlyOne();

      CreateBranchData createData =
         makeBranchData(qf, "PriorBranch", "Creation of initial working branch for test", SOURCE_TX_ID, createdBy, true);
      Callable<ReadableBranch> callable = branchInterface.createBranch(createData);
      Assert.assertNotNull(callable);
      ReadableBranch priorBranch = callable.call();

      // in the database, on the common branch, the users are all created in transaction 14
      // the common branch will have one user named Joe Smith

      int coreResult = qf.fromBranch(CoreBranches.COMMON).andNameEquals(ARTIFACT_NAME).getResults().getList().size();
      Assert.assertEquals(1, coreResult);

      // we copied the branch at transaction 13, so, on the copied branch there will not be any
      // user Joe Smith

      int priorResult = qf.fromBranch(priorBranch).andNameEquals(ARTIFACT_NAME).getResults().getList().size();
      Assert.assertEquals(0, priorResult);

      // finally, we copy another branch at transaction id 14, this is the transaction that added the 
      // user Joe Smith, so if the code is correct, and the copy includes the final 
      // transaction, then this will produce the same result as the query of the common branch

      CreateBranchData createDataNew =
         makeBranchData(qf, "PostBranch", "Creation of branch with transaction ID equal to the change test",
            CHANGED_TX_ID, createdBy, true);
      Callable<ReadableBranch> postCallable = branchInterface.createBranch(createDataNew);
      Assert.assertNotNull(postCallable);
      ReadableBranch postBranch = postCallable.call();

      int postResult = qf.fromBranch(postBranch).andNameEquals(ARTIFACT_NAME).getResults().getList().size();
      Assert.assertEquals(1, postResult);

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
      QueryFactory qf = orcsApi.getQueryFactory(context);
      ArtifactReadable createdBy =
         qf.fromBranch(CoreBranches.COMMON).andNameEquals("OSEE System").getResults().getExactlyOne();

      CreateBranchData createData =
         makeBranchData(qf, "CopiedBranch", "Creation of branch with copied tx for test", SOURCE_TX_ID, createdBy, true);
      Callable<ReadableBranch> callableBranch = branchInterface.createBranch(createData);

      // the new branch will contain two transactions - these should have the same change report as the original branch
      ReadableBranch postBranch = callableBranch.call();

      callable = branchInterface.compareBranch(postBranch);
      List<ChangeItem> newItems = callable.call();
      compareBranchChanges(priorItems, newItems);
   }

   private CreateBranchData makeBranchData(QueryFactory qf, String name, String creationComment, int fromTransaction, ArtifactReadable userArtifact, boolean copyTx) {
      int MERGE_DESTINATION_BRANCH_ID = -1; // only used on merge branches
      int MERGE_ADDRESSING_QUERY_ID = -1; // only used on merge branches
      CreateBranchData createData = new CreateBranchData();
      createData.setGuid(GUID.create());
      createData.setName(name);
      createData.setBranchType(BranchType.WORKING);
      createData.setCreationComment(creationComment);
      createData.setFromTransaction(TokenFactory.createTransaction(fromTransaction));
      createData.setUserArtifact(userArtifact);
      createData.setAssociatedArtifact(userArtifact);
      createData.setMergeDestinationBranchId(MERGE_DESTINATION_BRANCH_ID);
      createData.setMergeAddressingQueryId(MERGE_ADDRESSING_QUERY_ID);
      createData.setTxCopyBranchType(copyTx);
      return createData;
   }

   private void compareBranchChanges(List<ChangeItem> priorItems, List<ChangeItem> newItems) {
      Collections.sort(priorItems);
      Collections.sort(newItems);
      Assert.assertEquals(priorItems, newItems);
   }
}
