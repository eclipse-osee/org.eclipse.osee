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
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTransactionTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Rule
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private OrcsApi orcsApi;

   private final ApplicationContext context = null; // TODO use real application context
   private TransactionFactory txFactory;
   private ArtifactReadable userArtifact;
   private OrcsBranch orcsBranch;

   @Before
   public void setUp() throws Exception {
      txFactory = orcsApi.getTransactionFactory(context);
      orcsBranch = orcsApi.getBranchOps(context);
      userArtifact = getSystemUser();
   }

   @Test
   public void testCreateArtifact() throws OseeCoreException {
      String comment = "Test Artifact Write";
      String expectedName = "Create A Folder";
      String expectedAnnotation = "Annotate It";

      Branch branch = orcsApi.getBranchCache().get(CoreBranches.COMMON);
      TransactionRecord previousTx = orcsApi.getTxsCache().getHeadTransaction(branch);

      OrcsTransaction tx = txFactory.createTransaction(branch, userArtifact, comment);

      ArtifactWriteable writeable = tx.createArtifact(CoreArtifactTypes.Folder, expectedName);

      writeable.setAttributesFromStrings(CoreAttributeTypes.Annotation, expectedAnnotation);
      Assert.assertEquals(expectedName, writeable.getName());
      Assert.assertEquals(expectedAnnotation,
         writeable.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());

      String id = writeable.getGuid();

      Assert.assertTrue(Proxy.isProxyClass(writeable.getClass()));

      TransactionRecord newTx = tx.commit();
      Assert.assertFalse(tx.isCommitInProgress());

      TransactionRecord newHeadTx = orcsApi.getTxsCache().getHeadTransaction(branch);

      Assert.assertEquals(newTx, newHeadTx);

      checkTransaction(previousTx, newTx, branch, comment, userArtifact);

      ResultSet<ArtifactReadable> result =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andGuidsOrHrids(id).getResults();

      ArtifactReadable artifact = result.getExactlyOne();

      Assert.assertEquals(expectedName, artifact.getName());
      Assert.assertEquals(expectedAnnotation,
         artifact.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());
      Assert.assertEquals(writeable.getLocalId(), artifact.getLocalId());

      Assert.assertTrue(Proxy.isProxyClass(artifact.getClass()));
   }

   @Test
   public void testDuplicateAritfact() throws Exception {
      ArtifactReadable guestUser =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      // duplicate on same branch
      OrcsTransaction transaction1 =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, "testDuplicateArtifactSameBranch");
      ArtifactWriteable duplicate = transaction1.duplicateArtifact(guestUser);
      transaction1.commit();
      ArtifactReadable guestUserDup =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andGuidsOrHrids(duplicate.getGuid()).getResults().getExactlyOne();

      Assert.assertNotSame(SystemUser.Guest.getGuid(), guestUserDup.getGuid());
      Assert.assertEquals(SystemUser.Guest.getName(), guestUserDup.getName());

      // duplicate on different branch
      IOseeBranch branchToken = TokenFactory.createBranch(GUID.create(), "DuplicateArtifact tests");
      Callable<ReadableBranch> callableBranch = orcsBranch.createTopLevelBranch(branchToken, userArtifact);

      ReadableBranch topLevelBranch = callableBranch.call();

      OrcsTransaction transaction2 =
         txFactory.createTransaction(topLevelBranch, userArtifact, "testDuplicateArtifactDifferentBranch");
      duplicate = transaction2.duplicateArtifact(guestUser);
      transaction2.commit();
      guestUserDup =
         orcsApi.getQueryFactory(context).fromBranch(topLevelBranch).andGuidsOrHrids(duplicate.getGuid()).getResults().getExactlyOne();

      Assert.assertNotSame(SystemUser.Guest.getGuid(), guestUserDup.getGuid());
      Assert.assertEquals(SystemUser.Guest.getName(), guestUserDup.getName());
   }

   @Test
   public void testIntroduceArtifact() throws Exception {
      ArtifactReadable guestUser =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      IOseeBranch branchToken = TokenFactory.createBranch(GUID.create(), "IntroduceArtifact tests");
      Callable<ReadableBranch> callableBranch = orcsBranch.createTopLevelBranch(branchToken, userArtifact);

      ReadableBranch topLevelBranch = callableBranch.call();
      OrcsTransaction transaction = txFactory.createTransaction(topLevelBranch, userArtifact, "testIntroduceArtifact");
      transaction.introduceArtifact(guestUser);
      transaction.commit();

      ArtifactReadable introduced =
         orcsApi.getQueryFactory(context).fromBranch(topLevelBranch).andGuidsOrHrids(SystemUser.Guest.getGuid()).getResults().getExactlyOne();
      Assert.assertEquals(guestUser.getLocalId(), introduced.getLocalId());
   }

   @Test
   public void testIntroduceOnSameBranch() throws OseeCoreException {
      ArtifactReadable guestUser =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, "testIntroduceOnSameBranch");

      thrown.expect(OseeArgumentException.class);
      transaction.introduceArtifact(guestUser);
   }

   @Test
   public void testAsWritable() throws OseeCoreException {
      ArtifactReadable guestUser =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();
      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());
      ArtifactWriteable writeable = transaction.asWriteable(guestUser);
      writeable.setName("Test");

      // make sure readables have not been updated
      Assert.assertEquals("Guest", guestUser.getName());

      transaction.commit();

      // make sure readables have not been updated
      Assert.assertEquals("Guest", guestUser.getName());

      guestUser =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      Assert.assertEquals("Test", guestUser.getName());
   }

   @Test
   public void testAsWritableException() throws OseeCoreException {
      ArtifactReadable guestUser =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();
      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());
      ArtifactWriteable writeable = transaction.asWriteable(guestUser);
      writeable.setName("Test2");
      transaction.commit();

      // calls to sets should exception out after commit
      thrown.expect(OseeAccessDeniedException.class);
      writeable.setName("exception");
   }

   @Test
   public void testDeleteArtifact() throws OseeCoreException {
      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());
      ArtifactWriteable artifact = transaction.createArtifact(CoreArtifactTypes.AccessControlModel, "deleteMe");
      transaction.commit();

      transaction = txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());
      ArtifactReadable toDelete =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andGuidsOrHrids(artifact.getGuid()).getResults().getExactlyOne();
      ArtifactWriteable writeable = transaction.asWriteable(toDelete);
      writeable.delete();
      transaction.commit();

      toDelete =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andGuidsOrHrids(writeable.getGuid()).includeDeleted().getResults().getOneOrNull();
      Assert.assertNotNull(toDelete);
      Assert.assertTrue(toDelete.isDeleted());

   }

   @Test
   public void testArtifactGetTransaction() throws OseeCoreException {
      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());

      String guid = transaction.createArtifact(CoreArtifactTypes.Component, "A component").getGuid();
      int startingTx = transaction.commit().getId();

      ArtifactReadable artifact =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andGuidsOrHrids(guid).getResults().getExactlyOne();
      Assert.assertEquals(startingTx, artifact.getTransaction());

      OrcsTransaction transaction2 =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());

      transaction2.asWriteable(artifact).setName("Modified - component");
      int lastTx = transaction2.commit().getId();

      Assert.assertTrue(startingTx != lastTx);

      ArtifactReadable currentArtifact =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andGuidsOrHrids(guid).getResults().getExactlyOne();
      Assert.assertEquals(lastTx, currentArtifact.getTransaction());
   }

   private ArtifactReadable getSystemUser() throws OseeCoreException {
      return orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   private void checkTransaction(TransactionRecord previousTx, TransactionRecord newTx, Branch branch, String comment, ArtifactReadable user) throws OseeCoreException {
      Assert.assertTrue(previousTx.getId() < newTx.getId());
      Assert.assertEquals(comment, newTx.getComment());
      Assert.assertEquals(branch, newTx.getBranch());
      Assert.assertEquals(TransactionDetailsType.NonBaselined, newTx.getTxType());
      Assert.assertEquals(user.getLocalId(), newTx.getAuthor());
      Assert.assertEquals(-1, newTx.getCommit());
      Assert.assertTrue(previousTx.getTimeStamp().before(newTx.getTimeStamp()));
   }

   //   public static void main(String[] args) throws Exception {
   //      Tester x = new Tester();
   //
   //      ArtifactReadable artifact1 = null;
   //      ArtifactReadable artifact2 = null;
   //
   //      x.modifyOneArtifact(artifact1);
   //
   //      GraphReadable readableGraph = x.getApi().getGraph(null);
   //
   //      OrcsTransaction tx = x.getTransaction(); // branch and user and comment
   //
   //      GraphWriteable wGraph = tx.asWriteableGraph(readableGraph);
   //
   //      ArtifactWriteable wArt1 = tx.asWritable(artifact1);
   //      ArtifactWriteable wArt2 = tx.asWritable(artifact2);
   //
   //      for (ArtifactWriteable child : wGraph.getWriteableChildren(wArt1)) {
   //         child.setName("George");
   //      }
   //
   //      List<AttributeWriteable<String>> attributes = wArt1.getWriteableAttributes();
   //      for (AttributeWriteable<String> attribute : attributes) {
   //         attribute.setValue("Hello");
   //      }
   //
   //      wArt1.setName("Name");
   //      wArt1.setSoleAttributeValue(CoreAttributeTypes.Annotation, "hello");
   //
   //      wArt2.setName("Shawn");
   //
   //      tx.commit();
   //   }
}
