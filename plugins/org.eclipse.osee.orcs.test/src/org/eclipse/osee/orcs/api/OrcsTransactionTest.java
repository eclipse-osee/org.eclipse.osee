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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
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
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

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
   private QueryFactory query;

   @Before
   public void setUp() throws Exception {
      txFactory = orcsApi.getTransactionFactory(context);
      orcsBranch = orcsApi.getBranchOps(context);
      query = orcsApi.getQueryFactory(context);
      userArtifact = getSystemUser();
   }

   @Test
   public void testWritingUriAttribute() throws OseeCoreException {
      final String requirementText = "The engine torque shall be directly controllable through the engine control unit";

      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, "Create plain text requirement");

      ArtifactWriteable torqueRequirement =
         transaction.createArtifact(CoreArtifactTypes.SoftwareRequirementPlainText, "Engine Torque Control");
      torqueRequirement.createAttribute(CoreAttributeTypes.PlainTextContent, requirementText);

      String artifactId = torqueRequirement.getGuid();
      transaction.commit();

      ResultSet<ArtifactReadable> results =
         query.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.SoftwareRequirementPlainText).getResults();

      Optional<ArtifactReadable> item = Iterables.tryFind(results, new Predicate<ArtifactReadable>() {
         @Override
         public boolean apply(ArtifactReadable artifact) {
            String data = "";
            try {
               data = artifact.getSoleAttributeAsString(CoreAttributeTypes.PlainTextContent, "");
            } catch (OseeCoreException ex) {
               Assert.fail(Lib.exceptionToString(ex));
            }
            return requirementText.equals(data);
         }
      });

      assertTrue(item.isPresent());
      assertEquals(artifactId, item.get().getGuid());
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
      assertEquals(expectedName, writeable.getName());
      assertEquals(expectedAnnotation, writeable.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());

      String id = writeable.getGuid();

      assertTrue(Proxy.isProxyClass(writeable.getClass()));

      TransactionRecord newTx = tx.commit();
      assertFalse(tx.isCommitInProgress());

      TransactionRecord newHeadTx = orcsApi.getTxsCache().getHeadTransaction(branch);

      assertEquals(newTx, newHeadTx);

      checkTransaction(previousTx, newTx, branch, comment, userArtifact);

      ResultSet<ArtifactReadable> result = query.fromBranch(CoreBranches.COMMON).andGuidsOrHrids(id).getResults();

      ArtifactReadable artifact = result.getExactlyOne();

      assertEquals(expectedName, artifact.getName());
      assertEquals(expectedAnnotation, artifact.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());
      assertEquals(writeable.getLocalId(), artifact.getLocalId());

      assertTrue(Proxy.isProxyClass(artifact.getClass()));
   }

   @Test
   public void testDuplicateAritfact() throws Exception {
      ArtifactReadable guestUser =
         query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      // duplicate on same branch
      OrcsTransaction transaction1 =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, "testDuplicateArtifactSameBranch");
      ArtifactWriteable duplicate = transaction1.duplicateArtifact(guestUser);
      transaction1.commit();
      ArtifactReadable guestUserDup =
         query.fromBranch(CoreBranches.COMMON).andGuidsOrHrids(duplicate.getGuid()).getResults().getExactlyOne();

      assertNotSame(SystemUser.Guest.getGuid(), guestUserDup.getGuid());
      assertEquals(SystemUser.Guest.getName(), guestUserDup.getName());

      // duplicate on different branch
      IOseeBranch branchToken = TokenFactory.createBranch(GUID.create(), "DuplicateArtifact tests");
      Callable<ReadableBranch> callableBranch = orcsBranch.createTopLevelBranch(branchToken, userArtifact);

      ReadableBranch topLevelBranch = callableBranch.call();

      OrcsTransaction transaction2 =
         txFactory.createTransaction(topLevelBranch, userArtifact, "testDuplicateArtifactDifferentBranch");
      duplicate = transaction2.duplicateArtifact(guestUser);
      transaction2.commit();
      guestUserDup = query.fromBranch(topLevelBranch).andGuidsOrHrids(duplicate.getGuid()).getResults().getExactlyOne();

      assertNotSame(SystemUser.Guest.getGuid(), guestUserDup.getGuid());
      assertEquals(SystemUser.Guest.getName(), guestUserDup.getName());
   }

   @Test
   public void testIntroduceArtifact() throws Exception {
      ArtifactReadable guestUser =
         query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      IOseeBranch branchToken = TokenFactory.createBranch(GUID.create(), "IntroduceArtifact tests");
      Callable<ReadableBranch> callableBranch = orcsBranch.createTopLevelBranch(branchToken, userArtifact);

      ReadableBranch topLevelBranch = callableBranch.call();
      OrcsTransaction transaction = txFactory.createTransaction(topLevelBranch, userArtifact, "testIntroduceArtifact");
      transaction.introduceArtifact(guestUser);
      transaction.commit();

      ArtifactReadable introduced =
         query.fromBranch(topLevelBranch).andGuidsOrHrids(SystemUser.Guest.getGuid()).getResults().getExactlyOne();
      assertEquals(guestUser.getLocalId(), introduced.getLocalId());
   }

   @Test
   public void testIntroduceOnSameBranch() throws OseeCoreException {
      ArtifactReadable guestUser =
         query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, "testIntroduceOnSameBranch");

      thrown.expect(OseeArgumentException.class);
      transaction.introduceArtifact(guestUser);
   }

   @Test
   public void testAsWritable() throws OseeCoreException {
      ArtifactReadable guestUser =
         query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();
      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());
      ArtifactWriteable writeable = transaction.asWriteable(guestUser);
      writeable.setName("Test");

      // make sure readables have not been updated
      assertEquals("Guest", guestUser.getName());

      transaction.commit();

      // make sure readables have not been updated
      assertEquals("Guest", guestUser.getName());

      guestUser = query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      assertEquals("Test", guestUser.getName());
   }

   @Test
   public void testAsWritableException() throws OseeCoreException {
      ArtifactReadable guestUser =
         query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();
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
         query.fromBranch(CoreBranches.COMMON).andGuidsOrHrids(artifact.getGuid()).getResults().getExactlyOne();
      ArtifactWriteable writeable = transaction.asWriteable(toDelete);
      writeable.delete();
      transaction.commit();

      toDelete =
         query.fromBranch(CoreBranches.COMMON).andGuidsOrHrids(writeable.getGuid()).includeDeleted().getResults().getOneOrNull();
      assertNotNull(toDelete);
      assertTrue(toDelete.isDeleted());
   }

   @Test
   public void testArtifactGetTransaction() throws OseeCoreException {
      OrcsTransaction transaction =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());

      String guid = transaction.createArtifact(CoreArtifactTypes.Component, "A component").getGuid();
      int startingTx = transaction.commit().getId();

      ArtifactReadable artifact =
         query.fromBranch(CoreBranches.COMMON).andGuidsOrHrids(guid).getResults().getExactlyOne();
      assertEquals(startingTx, artifact.getTransaction());

      OrcsTransaction transaction2 =
         txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());

      transaction2.asWriteable(artifact).setName("Modified - component");
      int lastTx = transaction2.commit().getId();

      assertTrue(startingTx != lastTx);

      ArtifactReadable currentArtifact =
         query.fromBranch(CoreBranches.COMMON).andGuidsOrHrids(guid).getResults().getExactlyOne();
      assertEquals(lastTx, currentArtifact.getTransaction());
   }

   private ArtifactReadable getSystemUser() throws OseeCoreException {
      return query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   private void checkTransaction(TransactionRecord previousTx, TransactionRecord newTx, Branch branch, String comment, ArtifactReadable user) throws OseeCoreException {
      assertTrue(previousTx.getId() < newTx.getId());
      assertEquals(comment, newTx.getComment());
      assertEquals(branch, newTx.getBranch());
      assertEquals(TransactionDetailsType.NonBaselined, newTx.getTxType());
      assertEquals(user.getLocalId(), newTx.getAuthor());
      assertEquals(-1, newTx.getCommit());
      assertTrue(previousTx.getTimeStamp().before(newTx.getTimeStamp()));
   }
}
