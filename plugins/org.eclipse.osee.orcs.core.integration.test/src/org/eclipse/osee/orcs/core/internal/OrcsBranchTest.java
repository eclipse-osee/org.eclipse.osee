/*
 * Created on Apr 30, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
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
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   private OrcsApi orcsApi = null;
   private OrcsBranch branchInterface = null;
   private final ApplicationContext context = null; // TODO use real application context

   private final static String ARTIFACT_NAME = "Joe Smith";

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {

      orcsApi = OsgiUtil.getService(OrcsApi.class);

      branchInterface = orcsApi.getBranchOps(context);
   }

   @Test
   public void testCreateBranch() throws Exception {
      Integer SOURCE_TX_ID = 13; // Chosen starting transaction on Common Branch
      Integer CHANGED_TX_ID = 14; // Transaction containing tested change
      Integer MERGE_DESTINATION_ID = -1; // only used on merge branches
      Integer MERGE_POPULATE_BASE_TX_ID = -1; // only used on merge branches

      // set up the query factory for the test
      QueryFactory qf = orcsApi.getQueryFactory(context);

      CreateBranchData createData = new CreateBranchData();
      createData.setGuid(GUID.create());
      createData.setName("Prior Branch");
      createData.setBranchType(BranchType.WORKING);
      createData.setCreationComment("Creation of initial working branch for test");
      createData.setFromTransaction(TokenFactory.createTransaction(SOURCE_TX_ID));

      ReadableArtifact createdBy =
         qf.fromBranch(CoreBranches.COMMON).andNameEquals(ARTIFACT_NAME).getResults().getExactlyOne();
      createData.setUserArtifact(createdBy);

      createData.setAssociatedArtifact(createdBy);
      createData.setDestinationBranchId(MERGE_DESTINATION_ID);

      createData.setPopulateBaseTxFromAddressingQueryId(MERGE_POPULATE_BASE_TX_ID);
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

      CreateBranchData createDataNew = new CreateBranchData();
      createDataNew.setGuid(GUID.create());
      createDataNew.setName("PostBranch");
      createDataNew.setBranchType(BranchType.WORKING);
      createDataNew.setCreationComment("Creation of branch with transaction ID equal to the change test");
      createDataNew.setFromTransaction(TokenFactory.createTransaction(CHANGED_TX_ID));

      ReadableArtifact createdByNew =
         qf.fromBranch(CoreBranches.COMMON).andNameEquals(ARTIFACT_NAME).getResults().getExactlyOne();
      createData.setUserArtifact(createdByNew);

      createData.setAssociatedArtifact(createdByNew);
      createData.setDestinationBranchId(MERGE_DESTINATION_ID);

      createData.setPopulateBaseTxFromAddressingQueryId(MERGE_POPULATE_BASE_TX_ID);
      Callable<ReadableBranch> postCallable = branchInterface.createBranch(createDataNew);
      Assert.assertNotNull(postCallable);
      ReadableBranch postBranch = postCallable.call();

      int postResult = qf.fromBranch(postBranch).andNameEquals(ARTIFACT_NAME).getResults().getList().size();
      Assert.assertEquals(1, postResult);

   }

}
