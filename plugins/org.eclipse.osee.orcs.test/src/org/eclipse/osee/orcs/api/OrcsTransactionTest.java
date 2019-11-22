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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Component;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralDocument;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Parent;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Dependency_Artifact;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Dependency_Dependency;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.mock.TestDatabase;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTransactionTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Rule
   public final TestName testName = new TestName();

   private static TestDatabase db;
   private static TransactionFactory txFactory;
   private static final UserId userArtifact = OseeSystem;
   private static OrcsBranch orcsBranch;
   private static QueryFactory query;

   private static ArtifactId artifact1;
   private static ArtifactId artifact2;
   private static TransactionId[] transactions;

   @BeforeClass
   public static void setUp() throws Exception {
      db = new TestDatabase(OrcsTransactionTest.class.getName(), "for whole class", "orcs.jdbc.service");
      db.initialize();
      OrcsApi orcsApi = OsgiUtil.getService(OrcsTransactionTest.class, OrcsApi.class);
      txFactory = orcsApi.getTransactionFactory();
      orcsBranch = orcsApi.getBranchOps();
      query = orcsApi.getQueryFactory();
      setupHistory();
   }

   @AfterClass
   public static void cleanup() throws Exception {
      db.cleanup();
   }

   @Test
   public void testWritingUriAttribute() {
      final String requirementText = "The engine torque shall be directly controllable through the engine control unit";

      TransactionBuilder tx = createTx();

      ArtifactId torqueRequirement =
         tx.createArtifact(CoreArtifactTypes.SoftwareRequirementPlainText, "Engine Torque Control");
      tx.createAttribute(torqueRequirement, CoreAttributeTypes.PlainTextContent, requirementText);

      tx.commit();

      ResultSet<ArtifactReadable> results =
         query.fromBranch(COMMON).andTypeEquals(CoreArtifactTypes.SoftwareRequirementPlainText).getResults();

      Optional<ArtifactReadable> item = Iterables.tryFind(results, new Predicate<ArtifactReadable>() {
         @Override
         public boolean apply(ArtifactReadable artifact) {
            String data = "";
            try {
               data = artifact.getSoleAttributeAsString(CoreAttributeTypes.PlainTextContent, "");
            } catch (OseeCoreException ex) {
               fail(Lib.exceptionToString(ex));
            }
            return requirementText.equals(data);
         }
      });

      assertTrue(item.isPresent());
      assertEquals(torqueRequirement, item.get());
   }

   @Test
   public void testCreateArtifact() {
      String comment = "Test Artifact Write";
      String expectedName = "Create A Folder";
      String expectedAnnotation = "Annotate It";

      query.branchQuery();

      TransactionQuery transactionQuery = query.transactionQuery();
      TransactionReadable previousTx = transactionQuery.andIsHead(COMMON).getResults().getExactlyOne();

      TransactionBuilder tx = txFactory.createTransaction(COMMON, userArtifact, comment);

      ArtifactToken artifact = tx.createArtifact(CoreArtifactTypes.Folder, expectedName);

      tx.setAttributesFromStrings(artifact, CoreAttributeTypes.Annotation, expectedAnnotation);
      assertEquals(expectedName, artifact.getName());

      TransactionToken newTx = tx.commit();
      assertFalse(tx.isCommitInProgress());

      TransactionReadable newHeadTx = transactionQuery.andIsHead(COMMON).getResults().getExactlyOne();

      assertEquals(newTx, newHeadTx);

      TransactionReadable newTxReadable = transactionQuery.andTxId(newTx).getResults().getExactlyOne();
      checkTransaction(previousTx, newTxReadable, COMMON, comment, userArtifact);

      ResultSet<ArtifactReadable> result = query.fromBranch(COMMON).andId(artifact).getResults();

      ArtifactReadable artifact1 = result.getExactlyOne();

      assertEquals(expectedAnnotation, artifact1.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());
      assertEquals(expectedName, artifact1.getName());
      assertEquals(expectedAnnotation, artifact1.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());
      assertEquals(artifact1.getGuid(), artifact1.getGuid());
   }

   @Test
   public void testCreateArtifactWithParent() {
      TransactionBuilder tx = txFactory.createTransaction(SAW_Bld_2, userArtifact, "testCreateArtifact");
      ArtifactId folder = tx.createArtifact(ArtifactId.SENTINEL, CoreArtifactTypes.Folder, "Just a Folder");
      ArtifactId subfolder = tx.createArtifact(folder, CoreArtifactTypes.Folder, "subfolder");
      tx.commit();

      ArtifactReadable folderArt = query.fromBranch(SAW_Bld_2).andId(folder).getResults().getExactlyOne();
      Assert.assertEquals(null, folderArt.getParent());
      Assert.assertEquals(subfolder, folderArt.getChild());
   }

   @Test
   public void testCreateArtifactWithoutTagger() {
      String comment = "Test Artifact with untagged attribute";
      String expectedName = "Create An Artifact";
      String expectedAnnotation = "Annotate It";
      String expectedQualifaction = "Test";

      query.branchQuery();

      TransactionQuery transactionQuery = query.transactionQuery();
      transactionQuery.andIsHead(COMMON).getResults().getExactlyOne();

      TransactionBuilder tx = txFactory.createTransaction(COMMON, userArtifact, comment);

      ArtifactToken artifact = tx.createArtifact(CoreArtifactTypes.SubsystemRequirementHtml, expectedName);

      tx.setAttributesFromStrings(artifact, CoreAttributeTypes.Annotation, expectedAnnotation);
      tx.setAttributesFromStrings(artifact, CoreAttributeTypes.QualificationMethod, expectedQualifaction);
      assertEquals(expectedName, artifact.getName());

      tx.commit();

      ArtifactReadable artifactReadable = query.fromBranch(COMMON).andId(artifact).getResults().getExactlyOne();
      assertEquals(expectedName, artifactReadable.getName());
      assertEquals(expectedQualifaction,
         artifactReadable.getSoleAttributeAsString(CoreAttributeTypes.QualificationMethod));

   }

   @Test
   public void testCopyArtifact() throws Exception {
      ArtifactReadable user = query.fromBranch(COMMON).andId(SystemUser.Anonymous).getResults().getExactlyOne();

      // duplicate on same branch
      TransactionBuilder transaction1 = createTx();
      ArtifactId duplicate = transaction1.copyArtifact(user);
      transaction1.commit();
      ArtifactReadable userDup = query.fromBranch(COMMON).andId(duplicate).getResults().getExactlyOne();

      assertTrue(SystemUser.Anonymous.notEqual(userDup));
      assertEquals(SystemUser.Anonymous.getName(), userDup.getName());

      // duplicate on different branch
      IOseeBranch branchToken = IOseeBranch.create("DuplicateArtifact tests");
      IOseeBranch topLevelBranch = orcsBranch.createTopLevelBranch(branchToken, userArtifact);

      TransactionBuilder transaction2 =
         txFactory.createTransaction(topLevelBranch, userArtifact, testName.getMethodName());
      duplicate = transaction2.copyArtifact(user);
      transaction2.commit();
      userDup = query.fromBranch(topLevelBranch).andId(duplicate).getResults().getExactlyOne();

      assertTrue(SystemUser.Anonymous.notEqual(userDup));
      assertEquals(SystemUser.Anonymous.getName(), userDup.getName());
   }

   @Test
   public void testIntroduceArtifact() throws Exception {
      ArtifactReadable user = query.fromBranch(COMMON).andId(SystemUser.Anonymous).getResults().getExactlyOne();

      IOseeBranch branchToken = IOseeBranch.create("IntroduceArtifact tests");
      IOseeBranch topLevelBranch = orcsBranch.createTopLevelBranch(branchToken, userArtifact);

      TransactionBuilder transaction =
         txFactory.createTransaction(topLevelBranch, userArtifact, testName.getMethodName());
      transaction.introduceArtifact(COMMON, user);
      transaction.commit();

      ArtifactReadable introduced =
         query.fromBranch(topLevelBranch).andId(SystemUser.Anonymous).getResults().getExactlyOne();
      assertEquals(user, introduced);
   }

   @Test
   public void testIntroduceOnSameBranch() {
      ArtifactReadable user = query.fromBranch(COMMON).andId(SystemUser.Anonymous).getResults().getExactlyOne();

      TransactionBuilder tx = createTx();

      thrown.expect(OseeArgumentException.class);
      tx.introduceArtifact(COMMON, user);
   }

   @Test
   public void testReadAfterWrite() {
      QueryBuilder queryBuilder = query.fromBranch(COMMON).andId(OseeSystem);

      ArtifactReadable oseeSystem = queryBuilder.getResults().getExactlyOne();

      TransactionBuilder tx = createTx();
      tx.setSoleAttributeFromString(oseeSystem, CoreAttributeTypes.Name, "Test");
      tx.commit();

      ArtifactReadable newAnonymous = queryBuilder.getResults().getExactlyOne();

      assertEquals(OseeSystem.getName(), oseeSystem.getName());
      assertEquals("Test", newAnonymous.getName());
   }

   @Test
   public void testDeleteArtifact() {
      TransactionBuilder tx = createTx();
      ArtifactId artifact = tx.createArtifact(CoreArtifactTypes.AccessControlModel, "deleteMe");
      tx.commit();

      ArtifactReadable toDelete = query.fromBranch(COMMON).andId(artifact).getResults().getExactlyOne();

      tx = txFactory.createTransaction(COMMON, userArtifact, testName.getMethodName());
      tx.deleteArtifact(toDelete);
      tx.commit();

      toDelete = query.fromBranch(COMMON).andId(artifact).includeDeletedArtifacts().getResults().getOneOrDefault(
         ArtifactReadable.SENTINEL);
      Conditions.assertNotSentinel(toDelete);

      assertTrue(toDelete.isDeleted());
   }

   @Test
   public void testDeleteAttribute() {
      TransactionBuilder tx = createTx();
      ArtifactId artifact = tx.createArtifact(CoreArtifactTypes.AccessControlModel, "deleteThis");
      tx.createAttribute(artifact, CoreAttributeTypes.GeneralStringData, "deleted Name");
      tx.createAttribute(artifact, CoreAttributeTypes.PublishInline, true);
      tx.commit();

      tx = createTx();
      tx.deleteAttributes(artifact, CoreAttributeTypes.GeneralStringData);
      tx.commit();

      QueryBuilder builder = query.fromBranch(COMMON);
      builder.andExists(CoreAttributeTypes.GeneralStringData);
      builder.andId(artifact);
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(0, artifacts.size());
      builder = query.fromBranch(COMMON);
      builder.andExists(CoreAttributeTypes.GeneralStringData);
      builder.andId(artifact);
      builder.includeDeletedAttributes();
      artifacts = builder.getResults();
      assertEquals(1, artifacts.size());
      builder = query.fromBranch(COMMON);
      builder.andExists(CoreAttributeTypes.Annotation);
      builder.andId(artifact);
      builder.includeDeletedAttributes();
      artifacts = builder.getResults();
      assertEquals(0, artifacts.size());

      ArtifactReadable toDelete = query.fromBranch(COMMON).andId(artifact).getResults().getExactlyOne();

      tx = createTx();
      tx.deleteArtifact(toDelete);
      tx.commit();

      toDelete = query.fromBranch(COMMON).andId(artifact).includeDeletedArtifacts().getResults().getOneOrDefault(
         ArtifactReadable.SENTINEL);
      Conditions.assertNotSentinel(toDelete);

      assertTrue(toDelete.isDeleted());

   }

   private static void setupHistory() {
      TransactionBuilder tx = txFactory.createTransaction(COMMON, userArtifact, "create art 1 and 2");
      artifact1 = tx.createArtifact(CoreArtifactTypes.AccessControlModel, "deleteThis");
      tx.createAttribute(artifact1, CoreAttributeTypes.GeneralStringData, "deleted Name");
      tx.createAttribute(artifact1, CoreAttributeTypes.PublishInline, true);
      artifact2 = tx.createArtifact(CoreArtifactTypes.Folder, "deleteThisFolder");
      tx.createAttribute(artifact2, CoreAttributeTypes.Annotation, "annotation");
      tx.relate(artifact2, DefaultHierarchical_Parent, artifact1);
      TransactionId tx1 = tx.commit();

      tx = txFactory.createTransaction(COMMON, userArtifact, "create art 1 attribute");
      tx.deleteAttributes(artifact1, CoreAttributeTypes.GeneralStringData);
      TransactionId tx2 = tx.commit();

      ArtifactReadable toDelete = query.fromBranch(COMMON).andId(artifact1).getResults().getExactlyOne();

      tx = txFactory.createTransaction(COMMON, userArtifact, "delete art 1");
      tx.deleteArtifact(toDelete);
      tx.deleteAttributes(artifact2, CoreAttributeTypes.Annotation);
      tx.unrelate(artifact2, DefaultHierarchical_Parent, artifact1);
      TransactionId tx3 = tx.commit();

      toDelete = query.fromBranch(COMMON).andId(artifact2).getResults().getExactlyOne();

      tx = txFactory.createTransaction(COMMON, userArtifact, "delete art 2");
      tx.deleteArtifact(toDelete);
      TransactionId tx4 = tx.commit();

      toDelete = query.fromBranch(COMMON).andId(artifact1).includeDeletedArtifacts().getResults().getOneOrDefault(
         ArtifactReadable.SENTINEL);
      Conditions.assertNotSentinel(toDelete);

      assertTrue(toDelete.isDeleted());
      transactions = new TransactionId[] {tx1, tx2, tx3, tx4};
   }

   @Test
   public void testHistoricalArtifactsCreated() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[0]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact1, artifact2);
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, artifact2);
   }

   @Test
   public void testHistoricalOneArtifactDeleted() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[2]);
      builder.andIds(artifact1, artifact2);
      verifyHistoricalArtifacts(builder.getResults(), null, artifact2);
   }

   @Test
   public void testHistoricalDeletedAttribute() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[1]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact1, artifact2);
      // test the historical count query
      int count = builder.getCount();
      assertEquals(1, count);
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, null, artifact2);
   }

   @Test
   public void testHistoricalOneArtifactDeletedAttribute() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[1]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andId(artifact1);
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, null, null);
   }

   @Test
   public void testHistoricalIncludeDeletedAttribute() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[1]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andId(artifact1);
      builder.includeDeletedAttributes();
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, null);
   }

   @Test
   public void testHistoricalTwoArtifactsIncludeDeletedAttribute() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[1]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact2, artifact1);
      builder.includeDeletedAttributes();
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, artifact2);
   }

   @Test
   public void testHistoricalAllowDeletedArtifactsDeletedAttributes() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[2]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact1, artifact2);
      builder.includeDeletedArtifacts();
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, null, null);
   }

   @Test
   public void testHistoricalAllowDeletedArtifactsNondeletedAttribute() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[2]);
      builder.andExists(CoreAttributeTypes.PublishInline, CoreAttributeTypes.Annotation);
      builder.andIds(artifact1, artifact2);
      builder.includeDeletedArtifacts();
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, null);
   }

   @Test
   public void testHistoricalRelation() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[1]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact2, artifact1);
      builder.includeDeletedAttributes();
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, artifact2);
      Iterator<ArtifactReadable> iter = artifacts.iterator();
      ArtifactReadable artifact1Actual = iter.next();
      if (artifact1Actual.notEqual(artifact2)) {
         artifact1Actual = iter.next();
      }
      builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[1]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact1, artifact2);
      builder.includeDeletedArtifacts();
      builder.includeDeletedAttributes();
      builder.andRelatedTo(DefaultHierarchical_Parent, artifact1Actual);
      artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, null);
   }

   @Test
   public void testHistoricalDeletedRelation() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[1]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact2, artifact1);
      builder.includeDeletedAttributes();
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, artifact2);
      Iterator<ArtifactReadable> iter = artifacts.iterator();
      ArtifactReadable artifact1Actual = iter.next();
      if (!artifact1Actual.equals(artifact2)) {
         artifact1Actual = iter.next();
      }
      builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[2]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact1, artifact2);
      builder.includeDeletedArtifacts();
      builder.includeDeletedAttributes();
      builder.andRelatedTo(DefaultHierarchical_Parent, artifact1Actual);
      artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, null, null);
   }

   @Test
   public void testHistoricalAllowDeletedRelation() {
      QueryBuilder builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[1]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact2, artifact1);
      builder.includeDeletedAttributes();
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, artifact2);
      Iterator<ArtifactReadable> iter = artifacts.iterator();
      ArtifactReadable artifact1Actual = iter.next();
      if (artifact1Actual.notEqual(artifact2)) {
         artifact1Actual = iter.next();
      }
      builder = query.fromBranch(COMMON);
      builder.fromTransaction(transactions[2]);
      builder.andExists(CoreAttributeTypes.GeneralStringData, CoreAttributeTypes.Annotation);
      builder.andIds(artifact1, artifact2);
      builder.includeDeletedArtifacts();
      builder.includeDeletedAttributes();
      builder.includeDeletedRelations();
      builder.andRelatedTo(DefaultHierarchical_Parent, artifact1Actual);
      artifacts = builder.getResults();
      verifyHistoricalArtifacts(artifacts, artifact1, null);
   }

   public void verifyHistoricalArtifacts(ResultSet<ArtifactReadable> artifacts, ArtifactId artifact, ArtifactId artifact1) {
      int size = artifacts.size();
      int expectedSize = 0;
      if (artifact != null) {
         expectedSize++;
      }
      if (artifact1 != null) {
         expectedSize++;
      }
      assertEquals(expectedSize, size);
      if (size > 0) {
         for (ArtifactReadable art : artifacts) {
            if (artifact != null && art.matches(artifact)) {
               assertEquals(1,
                  art.getAttributeCount(CoreAttributeTypes.GeneralStringData, DeletionFlag.INCLUDE_HARD_DELETED));
               assertEquals(1,
                  art.getAttributeCount(CoreAttributeTypes.PublishInline, DeletionFlag.INCLUDE_HARD_DELETED));
            } else if (artifact1 != null && art.matches(artifact1)) {
               assertEquals(1, art.getAttributeCount(CoreAttributeTypes.Annotation, DeletionFlag.INCLUDE_HARD_DELETED));
            } else {
               assertTrue("Unexpected artifact", false);
            }
         }
      }
   }

   @Test
   public void testArtifactGetTransaction() {
      TransactionBuilder tx = createTx();

      ArtifactId artId = tx.createArtifact(CoreArtifactTypes.Component, "A component");
      TransactionId startingTx = tx.commit();

      ArtifactReadable artifact = query.fromBranch(COMMON).andId(artId).getResults().getExactlyOne();
      assertEquals(startingTx, artifact.getTransaction());

      TransactionBuilder tx2 = createTx();
      tx2.setName(artifact, "Modified - component");
      TransactionId lastTx = tx2.commit();

      assertFalse(startingTx.equals(lastTx));

      ArtifactReadable currentArtifact = query.fromBranch(COMMON).andId(artId).getResults().getExactlyOne();
      assertEquals(lastTx, currentArtifact.getTransaction());
   }

   @Test
   public void testRelate() {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(CoreArtifactTypes.Component, "A component");
      ArtifactId art2 = tx1.createArtifact(CoreArtifactTypes.User, "User Artifact");
      tx1.relate(art1, CoreRelationTypes.Users_User, art2);
      tx1.commit();

      ArtifactReadable artifact = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      assertEquals("A component", artifact.getName());

      ResultSet<ArtifactReadable> related = artifact.getRelated(CoreRelationTypes.Users_User);
      assertEquals(1, related.size());
      assertEquals("User Artifact", related.getExactlyOne().getName());
   }

   @Test
   public void testRelateTypeCheckException() {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(CoreArtifactTypes.Component, "A component");
      ArtifactId art2 = tx1.createArtifact(CoreArtifactTypes.User, "User Artifact");

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Relation validity error for [artifact type[Component]");
      thrown.expectMessage("only items of type [User] are allowed");
      tx1.relate(art2, CoreRelationTypes.Users_User, art1);
   }

   @Test
   public void testRelateWithSortType() {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(CoreArtifactTypes.Component, "A component");
      tx1.createArtifact(art1, CoreArtifactTypes.Component, "B component");
      tx1.createArtifact(art1, CoreArtifactTypes.Component, "C component");
      TransactionId tx1Id = tx1.commit();

      QueryBuilder art1Query = query.fromBranch(COMMON).andId(art1);

      ArtifactReadable artifact = art1Query.getResults().getExactlyOne();
      assertEquals("A component", artifact.getName());
      assertEquals(tx1Id, artifact.getTransaction());

      List<ArtifactReadable> children = artifact.getChildren();
      assertEquals(2, children.size());

      Iterator<ArtifactReadable> iterator = children.iterator();
      assertEquals("B component", iterator.next().getName());
      assertEquals("C component", iterator.next().getName());

      TransactionBuilder tx2 = createTx();
      ArtifactId art4 = tx2.createArtifact(Component, "D component");
      tx2.relate(art1, DefaultHierarchical_Child, art4, LEXICOGRAPHICAL_DESC);
      TransactionId tx2Id = tx2.commit();

      ArtifactReadable artifact21 = art1Query.getResults().getExactlyOne();
      assertEquals("A component", artifact21.getName());
      assertEquals(tx2Id, artifact21.getTransaction());

      List<ArtifactReadable> children2 = artifact21.getChildren();
      assertEquals(3, children2.size());

      Iterator<ArtifactReadable> iterator2 = children2.iterator();
      assertEquals("D component", iterator2.next().getName());
      assertEquals("C component", iterator2.next().getName());
      assertEquals("B component", iterator2.next().getName());
   }

   @Test
   public void testSetRelations() {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(art1, Component, "B component");
      ArtifactId art3 = tx1.createArtifact(Component, "C component");
      ArtifactId art4 = tx1.createArtifact(Component, "D component");
      tx1.commit();

      ArtifactReadable artifact1 = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      assertEquals("A component", artifact1.getName());

      List<ArtifactReadable> children = artifact1.getChildren();
      assertEquals(1, children.size());

      Iterator<ArtifactReadable> iterator = children.iterator();
      ArtifactReadable artifact2 = children.iterator().next();

      assertEquals("B component", artifact2.getName());
      assertEquals(artifact1, artifact2.getParent());
      assertEquals(art2, artifact2);

      TransactionBuilder tx2 = createTx();
      tx2.setRelations(art1, DefaultHierarchical_Child, Arrays.asList(art3, art4));
      tx2.commit();

      artifact1 = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      assertEquals("A component", artifact1.getName());

      children = artifact1.getChildren();
      assertEquals(2, children.size());

      iterator = children.iterator();
      ArtifactReadable artifact3 = iterator.next();
      ArtifactReadable artifact4 = iterator.next();

      assertEquals("C component", artifact3.getName());
      assertEquals("D component", artifact4.getName());

      assertEquals(artifact1, artifact3.getParent());
      assertEquals(artifact1, artifact4.getParent());

      assertEquals(art3, artifact3);
      assertEquals(art4, artifact4);

      artifact2 = query.fromBranch(COMMON).andId(art2).getResults().getExactlyOne();
      assertEquals("B component", artifact2.getName());
      assertNull(artifact2.getParent());
      assertEquals(art2, artifact2);
   }

   @Test
   public void testAddChild() {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(Component, "C component");
      ArtifactId art3 = tx1.createArtifact(Component, "B component");
      tx1.commit();

      TransactionBuilder tx2 = createTx();
      tx2.addChild(art1, art2);
      tx2.addChild(art1, art3);
      tx2.commit();

      ArtifactReadable artifact1 = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      assertEquals("A component", artifact1.getName());

      List<ArtifactReadable> children = artifact1.getChildren();
      assertEquals(2, children.size());

      Iterator<ArtifactReadable> iterator = children.iterator();
      ArtifactReadable artifact3 = iterator.next();
      ArtifactReadable artifact2 = iterator.next();

      assertEquals(art3, artifact3);
      assertEquals(art2, artifact2);

      assertEquals("B component", artifact3.getName());
      assertEquals("C component", artifact2.getName());

      assertEquals(artifact1, artifact2.getParent());
      assertEquals(artifact1, artifact3.getParent());
   }

   @Test
   public void testSetRationale() {
      String rationale = "This is my rationale";

      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(Component, "B component");

      tx1.relate(art1, DefaultHierarchical_Child, art2);
      tx1.setRationale(art1, DefaultHierarchical_Child, art2, rationale);

      tx1.commit();

      ArtifactReadable artifact = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      assertEquals("A component", artifact.getName());

      ArtifactReadable otherArtifact = artifact.getChild();
      assertEquals("B component", otherArtifact.getName());

      String actual1 = artifact.getRationale(DefaultHierarchical_Child, otherArtifact);
      assertEquals(rationale, actual1);

      String actual2 = otherArtifact.getRationale(DefaultHierarchical_Parent, artifact);
      assertEquals(rationale, actual2);
   }

   @Test
   public void testUnrelate() {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(art1, Component, "C component");
      ArtifactId art3 = tx1.createArtifact(art1, Component, "B component");
      ArtifactId art4 = tx1.createArtifact(GeneralDocument, "Document");
      tx1.relate(art1, Dependency_Dependency, art4);
      tx1.commit();

      ArtifactReadable artifact4 = query.fromBranch(COMMON).andId(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      ArtifactReadable artifact1 = artifact4.getRelated(CoreRelationTypes.Dependency_Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      Iterator<ArtifactReadable> iterator = artifact1.getChildren().iterator();
      assertEquals(art3, iterator.next());
      assertEquals(art2, iterator.next());

      // Un-relate a child
      TransactionBuilder tx2 = createTx();
      tx2.unrelate(art1, DefaultHierarchical_Child, art2);
      tx2.commit();

      artifact4 = query.fromBranch(COMMON).andId(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      artifact1 = artifact4.getRelated(CoreRelationTypes.Dependency_Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      assertEquals(art3, artifact1.getChild());
   }

   @Test
   public void testUnrelateFromAllByType() {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(art1, Component, "C component");
      ArtifactId art3 = tx1.createArtifact(art1, Component, "B component");

      ArtifactId art4 = tx1.createArtifact(GeneralDocument, "Document");
      tx1.relate(art1, Dependency_Dependency, art4);
      tx1.commit();

      ArtifactReadable artifact4 = query.fromBranch(COMMON).andId(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      ArtifactReadable artifact1 = artifact4.getRelated(Dependency_Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      Iterator<ArtifactReadable> iterator = artifact1.getChildren().iterator();
      assertEquals(art3, iterator.next());
      assertEquals(art2, iterator.next());

      // Unrelate All children
      TransactionBuilder tx2 = createTx();
      tx2.unrelateFromAll(DefaultHierarchical_Parent, art1);
      tx2.commit();

      artifact4 = query.fromBranch(COMMON).andId(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      artifact1 = artifact4.getRelated(Dependency_Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      assertEquals(true, artifact1.getChildren().isEmpty());
   }

   @Test
   public void testAttributeCommitOnlyAffectNewStripe() {
      // create artifact and check exists and name set
      TransactionBuilder tx1 = createTx();
      ArtifactToken art1 = tx1.createArtifact(Component, "orig name");
      TransactionId rec1 = tx1.commit();
      assertNotNull(rec1);
      art1 = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      assertEquals("orig name", art1.getName());

      // change name
      ArtifactToken art2 = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      TransactionBuilder tx2 = createTx();
      tx2.setName(art2, "new name");
      TransactionId rec2 = tx2.commit();
      assertNotNull(rec2);

      // verify that change only exists on new stripe?
      ArtifactToken art3 = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      assertEquals("orig name", art1.getName());
      // this should be same name cause we didn't re-query it and the tx didn't change the model?
      assertEquals("orig name", art2.getName());
      // since art3 is a re-query, it should show the same name
      assertEquals("new name", art3.getName());
   }

   @Test
   public void testUnrelateFromAll() {
      ArtifactReadable artifact1 = null;
      ArtifactReadable artifact2 = null;
      ArtifactReadable artifact3 = null;
      ArtifactReadable artifact4 = null;

      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(art1, Component, "B component");
      ArtifactId art3 = tx1.createArtifact(art1, Component, "C component");

      ArtifactId art4 = tx1.createArtifact(GeneralDocument, "Document");
      tx1.relate(art1, Dependency_Dependency, art4);
      TransactionId rec1 = tx1.commit();
      assertNotNull(rec1);

      artifact4 = query.fromBranch(COMMON).andId(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      artifact1 = artifact4.getRelated(Dependency_Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      Iterator<ArtifactReadable> iterator = artifact1.getChildren().iterator();
      assertEquals(art2, iterator.next());
      assertEquals(art3, iterator.next());

      TransactionBuilder tx2 = createTx();
      tx2.unrelateFromAll(art1);
      TransactionId rec2 = tx2.commit();
      assertNotNull(rec2);

      artifact1 =
         query.fromBranch(COMMON).andUuid(art1.getUuid()).includeDeletedArtifacts().getResults().getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL);
      Conditions.assertNotSentinel(artifact1);
      artifact2 =
         query.fromBranch(COMMON).andUuid(art2.getUuid()).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      Conditions.assertNotSentinel(artifact2);
      artifact3 =
         query.fromBranch(COMMON).andUuid(art3.getUuid()).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      Conditions.assertNotSentinel(artifact3);
      artifact4 =
         query.fromBranch(COMMON).andUuid(art4.getUuid()).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      Conditions.assertNotSentinel(artifact4);

      assertEquals(true, artifact1.getChildren().isEmpty());
      assertEquals(true, artifact1.getRelated(Dependency_Dependency).isEmpty());

      assertNull(artifact2.getParent());
      assertNull(artifact3.getParent());

      assertEquals(true, artifact4.getRelated(Dependency_Artifact).isEmpty());
   }

   @Test
   public void testMultiAttriVersionsWriteAndLoading() {
      TransactionBuilder tx = createTx();
      ArtifactId art1 = tx.createArtifact(Component, "A component");
      tx.setSoleAttributeFromString(art1, CoreAttributeTypes.Annotation, "write1");
      tx.commit();

      tx = createTx();
      tx.setSoleAttributeFromString(art1, CoreAttributeTypes.Annotation, "write2");
      tx.commit();

      tx = createTx();
      tx.setSoleAttributeFromString(art1, CoreAttributeTypes.Annotation, "write3");
      tx.commit();

      tx = createTx();
      tx.setSoleAttributeFromString(art1, CoreAttributeTypes.Annotation, "write4");
      tx.commit();

      tx = createTx();
      tx.setSoleAttributeFromString(art1, CoreAttributeTypes.Annotation, "write5");
      TransactionId lastTx = tx.commit();

      ArtifactReadable art = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      ResultSet<? extends AttributeReadable<Object>> attributes = art.getAttributes(CoreAttributeTypes.Annotation);

      assertEquals(1, attributes.size());
      assertEquals("write5", attributes.getExactlyOne().getValue());

      QueryBuilder builder = query.fromBranch(COMMON).fromTransaction(lastTx).andId(art1);
      ResultSet<ArtifactReadable> results = builder.getResults();
      art = results.getExactlyOne();
      attributes = art.getAttributes(CoreAttributeTypes.Annotation);
      assertEquals(1, attributes.size());
      assertEquals("write5", attributes.getExactlyOne().getValue());
   }

   @Test
   public void testMultiRelationVersionsWriteAndLoading() {
      TransactionBuilder tx = createTx();
      ArtifactId art1 = tx.createArtifact(CoreArtifactTypes.Component, "A component");
      ArtifactId art2 = tx.createArtifact(CoreArtifactTypes.User, "User Artifact");
      tx.relate(art1, CoreRelationTypes.Users_User, art2, "rationale1");
      tx.commit();

      tx = createTx();
      tx.setRationale(art1, CoreRelationTypes.Users_User, art2, "rationale2");
      tx.commit();

      tx = createTx();
      tx.setRationale(art1, CoreRelationTypes.Users_User, art2, "rationale3");
      tx.commit();

      tx = createTx();
      tx.setRationale(art1, CoreRelationTypes.Users_User, art2, "rationale4");
      tx.commit();

      tx = createTx();
      tx.setRationale(art1, CoreRelationTypes.Users_User, art2, "rationale5");
      TransactionId lastTx = tx.commit();

      ArtifactReadable art = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      ResultSet<ArtifactReadable> related = art.getRelated(CoreRelationTypes.Users_User);
      assertEquals(1, related.size());
      ArtifactReadable other = related.getExactlyOne();
      String rationale = art.getRationale(CoreRelationTypes.Users_User, other);

      assertEquals("rationale5", rationale);

      art = query.fromBranch(COMMON).fromTransaction(lastTx).andId(art1).getResults().getExactlyOne();
      related = art.getRelated(CoreRelationTypes.Users_User);
      assertEquals(1, related.size());
      other = related.getExactlyOne();
      rationale = art.getRationale(CoreRelationTypes.Users_User, other);

      assertEquals("rationale5", rationale);
   }

   @Test
   public void testRelateUnrelateMultipleTimes() {
      TransactionBuilder tx = createTx();
      ArtifactId art1 = tx.createArtifact(CoreArtifactTypes.Component, "A component");
      ArtifactId art2 = tx.createArtifact(CoreArtifactTypes.User, "User Artifact");
      tx.relate(art1, CoreRelationTypes.Users_User, art2, "rationale1");
      tx.commit();

      ArtifactReadable art = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      ArtifactReadable otherSide = art.getRelated(CoreRelationTypes.Users_User).getExactlyOne();
      assertEquals(true, art.areRelated(CoreRelationTypes.Users_User, otherSide));

      tx = createTx();
      tx.unrelate(art1, CoreRelationTypes.Users_User, art2);
      tx.commit();

      art = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      ResultSet<ArtifactReadable> otherSideResults = art.getRelated(CoreRelationTypes.Users_User);
      assertEquals(true, otherSideResults.isEmpty());

      tx = createTx();
      tx.relate(art1, CoreRelationTypes.Users_User, art2);
      tx.commit();

      art = query.fromBranch(COMMON).andId(art1).getResults().getExactlyOne();
      otherSide = art.getRelated(CoreRelationTypes.Users_User).getExactlyOne();
      assertEquals(true, art.areRelated(CoreRelationTypes.Users_User, otherSide));
   }

   @Test
   public void testSetTransactionComment() throws Exception {
      TransactionBuilder tx = createTx();
      ArtifactId art1 = tx.createArtifact(CoreArtifactTypes.Component, "A component");
      ArtifactId art2 = tx.createArtifact(CoreArtifactTypes.User, "User Artifact");
      tx.relate(art1, CoreRelationTypes.Users_User, art2, "rationale1");
      TransactionToken txId = tx.commit();

      String expectedComment = "My new Comment";
      txFactory.setTransactionComment(txId, expectedComment).call();

      TransactionReadable actual = query.transactionQuery().andTxId(txId).getResults().getExactlyOne();
      assertEquals(txId, actual);
      assertEquals(expectedComment, actual.getComment());
   }

   @Test(expected = OseeStateException.class)
   public void testAttributeMultiplicity() {
      TransactionBuilder tx = createTx();
      ArtifactId art1 = tx.createArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, "SwReq");
      tx.createAttribute(art1, CoreAttributeTypes.ParagraphNumber, "1.1");
      tx.createAttribute(art1, CoreAttributeTypes.ParagraphNumber, "2.2");
   }

   @Test(expected = OseeStateException.class)
   public void testRelationMultiplicity() {
      TransactionBuilder tx = createTx();
      ArtifactId child = tx.createArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, "Child");
      ArtifactId parent1 = tx.createArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, "Parent1");
      ArtifactId parent2 = tx.createArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, "Parent2");
      tx.relate(parent1, DefaultHierarchical_Parent, child);
      tx.relate(parent2, DefaultHierarchical_Parent, child);
   }

   private TransactionBuilder createTx() {
      return txFactory.createTransaction(COMMON, userArtifact, testName.getMethodName());
   }

   private void checkTransaction(TransactionReadable previousTx, TransactionReadable newTx, BranchId branchId, String comment, ArtifactId user) {
      assertTrue(previousTx.isOlderThan(newTx));
      assertEquals(comment, newTx.getComment());
      assertEquals(branchId, newTx.getBranch());
      assertEquals(TransactionDetailsType.NonBaselined, newTx.getTxType());
      assertEquals(user, newTx.getAuthor());
      assertEquals(newTx.getCommitArt(), 0L);
      assertTrue(previousTx.getDate().before(newTx.getDate()));
   }
}