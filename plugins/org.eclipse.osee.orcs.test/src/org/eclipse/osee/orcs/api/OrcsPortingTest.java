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

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsIntegrationRule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author David Miller
 */

public class OrcsPortingTest {

   @Rule
   public OrcsIntegrationRule osgi = new OrcsIntegrationRule(this);

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   private OrcsBranch branchInterface = null;

   private final ApplicationContext context = null; // TODO use real application context

   ReadableBranch mainFromBranch = null;
   ReadableBranch indepToBranch = null;
   ReadableBranch childFromBranch = null;
   TransactionRecord transactionFrom = null;

   @OsgiService
   private OrcsApi orcsApi;

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
      branchInterface = orcsApi.getBranchOps(context);
   }

   @Test
   public void testCreateBranch() throws Exception {
      CreateInitialSetup();
      ReadableBranch copyTxBranch = doCopyFromTransaction(transactionFrom.getId());
      doCommitIntoDestination(copyTxBranch);
   }

   private void CreateInitialSetup() throws Exception {
      // set up the query factory for the test
      QueryFactory qf = orcsApi.getQueryFactory(context);
      // set up the author for the branches. 
      ArtifactReadable author = getSystemUser();

      // set up the main branch
      IOseeBranch branch = TokenFactory.createBranch(GUID.create(), "MainFromBranch");

      Callable<ReadableBranch> callable = branchInterface.createTopLevelBranch(branch, author);

      Assert.assertNotNull(callable);
      mainFromBranch = callable.call();

      // set up the independent branch to copy to
      IOseeBranch toBranch = TokenFactory.createBranch(GUID.create(), "IndepToBranch");

      Callable<ReadableBranch> toCallable = branchInterface.createTopLevelBranch(toBranch, author);

      Assert.assertNotNull(callable);
      indepToBranch = toCallable.call();

      // baseline branch - set up artifacts on the main branch, and on the child branch
      // first, add some transaction on the main branch, then create the child branch

      OrcsTransaction tx =
         orcsApi.getTransactionFactory(context).createTransaction(mainFromBranch, author, "add base requirement");
      ArtifactWriteable req1 = tx.createArtifact(CoreArtifactTypes.SoftwareRequirement, "BaseRequirement");
      req1.setSoleAttributeFromString(CoreAttributeTypes.Subsystem, "Test");

      tx.commit();

      OrcsTransaction tx2 =
         orcsApi.getTransactionFactory(context).createTransaction(mainFromBranch, author, "add another requirement");
      ArtifactWriteable req2 = tx2.createArtifact(CoreArtifactTypes.SoftwareRequirement, "SecondRequirement");
      req2.setSoleAttributeFromString(CoreAttributeTypes.Subsystem, "Test2");

      tx2.commit();

      // set up the child branch to copy to
      IOseeBranch childBranch = TokenFactory.createBranch(GUID.create(), "childBranch");

      Callable<ReadableBranch> childCallable =
         branchInterface.createBaselineBranch(childBranch, author, mainFromBranch, null);

      Assert.assertNotNull(callable);
      childFromBranch = childCallable.call();

      OrcsTransaction tx3 =
         orcsApi.getTransactionFactory(context).createTransaction(childFromBranch, author, "update second requirement");
      ArtifactReadable readableReq2 =
         qf.fromBranch(childFromBranch).andGuidsOrHrids(req2.getGuid()).getResults().getExactlyOne();

      // modifying this artifact should cause it to get introduced
      ArtifactWriteable writeableReq2 = tx3.asWriteable(readableReq2);
      writeableReq2.setSoleAttributeFromString(CoreAttributeTypes.Subsystem, "test changed");

      // new artifacts should come across as new
      tx3.createArtifact(CoreArtifactTypes.Folder, "childBranch folder");

      // set this aside to use in the copy from transaction for the branch
      transactionFrom = tx3.commit();

      // make an additional transaction to make sure it doesn't get copied also
      OrcsTransaction tx4 =
         orcsApi.getTransactionFactory(context).createTransaction(childFromBranch, author, "after second requirement");
      ArtifactReadable readableReq2verA =
         qf.fromBranch(childFromBranch).andGuidsOrHrids(req2.getGuid()).getResults().getExactlyOne();

      // modifying this artifact should cause it to get introduced
      ArtifactWriteable writeableReq2verA = tx4.asWriteable(readableReq2verA);
      writeableReq2verA.setSoleAttributeFromString(CoreAttributeTypes.Subsystem, "test changed again");

      // additional artifacts should not come across
      tx4.createArtifact(CoreArtifactTypes.Folder, "folder after transaction");

      tx4.commit();

   }

   private ReadableBranch doCopyFromTransaction(int fromTransaction) throws Exception {

      // create the branch with the copied transaction
      IOseeBranch branch = TokenFactory.createBranch(GUID.create(), "CopiedTxBranch");

      ArtifactReadable author = getSystemUser();

      Callable<ReadableBranch> callableBranch =
         branchInterface.createCopyTxBranch(branch, author, fromTransaction, null);

      // the new branch will contain two transactions -
      return callableBranch.call();
   }

   private void doCommitIntoDestination(ReadableBranch fromBranch) throws Exception {
      // set up the query factory for the test
      QueryFactory qf = orcsApi.getQueryFactory(context);
      // set up the author for the branches. 
      // ArtifactReadable author = getSystemUser();

      TransactionRecord finalTx = branchInterface.commitBranch(getSystemUser(), fromBranch, indepToBranch).call();
      // now check to make sure everything is as expected
      // we should have a SoftwareRequirement named "SecondRequirement" with an attribute named "test changed" (changed on child branch to this)
      // the attribute for the SecondRequirement should not be named "test changed again" (on the branch after the copy from)
      // we should have a folder named "childBranch folder", but no folder named "folder after transaction"

      for (ArtifactReadable art : qf.fromBranch(indepToBranch).andIsOfType(CoreArtifactTypes.Artifact).getResults()) {
         if (art.getArtifactType().equals(CoreArtifactTypes.SoftwareRequirement)) {
            Assert.assertEquals("SecondRequirement", art.getName());
            List<AttributeReadable<Object>> attrs = art.getAttributes();
            AttributeReadable<Object> attr = attrs.get(0);
            Assert.assertEquals("test changed", attr.toString());
            // if there is a requirement with an attribute other than "test changed" then the above should fail
         } else if (art.getArtifactType().equals(CoreArtifactTypes.Folder)) {
            Assert.assertEquals("childBranch folder", art.getName());
            // if there is any other folder like "folder after transaction" then the above should fail
         } else {
            Assert.assertTrue(false);
         }
      }
   }

   private ArtifactReadable getSystemUser() throws OseeCoreException {
      return orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }
}
