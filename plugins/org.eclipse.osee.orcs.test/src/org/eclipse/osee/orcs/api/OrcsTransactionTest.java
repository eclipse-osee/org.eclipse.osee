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
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Child;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Parent;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Dependency__Artifact;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Dependency__Dependency;
import static org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Iterator;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
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
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
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

      TransactionBuilder tx = createTx();

      ArtifactId torqueRequirement =
         tx.createArtifact(CoreArtifactTypes.SoftwareRequirementPlainText, "Engine Torque Control");
      tx.createAttribute(torqueRequirement, CoreAttributeTypes.PlainTextContent, requirementText);

      tx.commit();

      ResultSet<ArtifactReadable> results =
         query.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.SoftwareRequirementPlainText).getResults();

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
      assertEquals(torqueRequirement.getGuid(), item.get().getGuid());
   }

   @Test
   public void testCreateArtifact() throws OseeCoreException {
      String comment = "Test Artifact Write";
      String expectedName = "Create A Folder";
      String expectedAnnotation = "Annotate It";

      Branch branch = orcsApi.getBranchCache().get(CoreBranches.COMMON);
      TransactionRecord previousTx = orcsApi.getTxsCache().getHeadTransaction(branch);

      TransactionBuilder tx = txFactory.createTransaction(branch, userArtifact, comment);

      ArtifactId artifactId = tx.createArtifact(CoreArtifactTypes.Folder, expectedName);

      tx.setAttributesFromStrings(artifactId, CoreAttributeTypes.Annotation, expectedAnnotation);
      assertEquals(expectedName, artifactId.getName());

      TransactionRecord newTx = tx.commit();
      assertFalse(tx.isCommitInProgress());

      TransactionRecord newHeadTx = orcsApi.getTxsCache().getHeadTransaction(branch);

      assertEquals(newTx, newHeadTx);

      checkTransaction(previousTx, newTx, branch, comment, userArtifact);

      ResultSet<ArtifactReadable> result = query.fromBranch(CoreBranches.COMMON).andIds(artifactId).getResults();

      ArtifactReadable artifact = result.getExactlyOne();

      assertEquals(expectedAnnotation, artifact.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());
      assertEquals(expectedName, artifact.getName());
      assertEquals(expectedAnnotation, artifact.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());
      assertEquals(artifactId.getGuid(), artifact.getGuid());
   }

   @Test
   public void testCopyArtifact() throws Exception {
      ArtifactReadable guestUser =
         query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      // duplicate on same branch
      TransactionBuilder transaction1 = createTx();
      ArtifactId duplicate = transaction1.copyArtifact(guestUser);
      transaction1.commit();
      ArtifactReadable guestUserDup =
         query.fromBranch(CoreBranches.COMMON).andIds(duplicate).getResults().getExactlyOne();

      assertNotSame(SystemUser.Guest.getGuid(), guestUserDup.getGuid());
      assertEquals(SystemUser.Guest.getName(), guestUserDup.getName());

      // duplicate on different branch
      IOseeBranch branchToken = TokenFactory.createBranch(GUID.create(), "DuplicateArtifact tests");
      Callable<ReadableBranch> callableBranch = orcsBranch.createTopLevelBranch(branchToken, userArtifact);

      ReadableBranch topLevelBranch = callableBranch.call();

      TransactionBuilder transaction2 =
         txFactory.createTransaction(topLevelBranch, userArtifact, testName.getMethodName());
      duplicate = transaction2.copyArtifact(guestUser);
      transaction2.commit();
      guestUserDup = query.fromBranch(topLevelBranch).andIds(duplicate).getResults().getExactlyOne();

      assertNotSame(SystemUser.Guest.getGuid(), guestUserDup.getGuid());
      assertEquals(SystemUser.Guest.getName(), guestUserDup.getName());
   }

   @Test
   public void testIntroduceArtifact() throws Exception {
      ArtifactReadable guestUser =
         query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      IOseeBranch branchToken = TokenFactory.createBranch(GUID.create(), "IntroduceArtifact tests");
      ReadableBranch topLevelBranch = orcsBranch.createTopLevelBranch(branchToken, userArtifact).call();

      TransactionBuilder transaction =
         txFactory.createTransaction(topLevelBranch, userArtifact, testName.getMethodName());
      transaction.introduceArtifact(guestUser);
      transaction.commit();

      ArtifactReadable introduced =
         query.fromBranch(topLevelBranch).andIds(SystemUser.Guest).getResults().getExactlyOne();
      assertEquals(guestUser.getLocalId(), introduced.getLocalId());
   }

   @Test
   public void testIntroduceOnSameBranch() throws OseeCoreException {
      ArtifactReadable guestUser =
         query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest).getResults().getExactlyOne();

      TransactionBuilder tx = createTx();

      thrown.expect(OseeArgumentException.class);
      tx.introduceArtifact(guestUser);
   }

   @Test
   public void testReadAfterWrite() throws OseeCoreException {
      QueryBuilder queryBuilder = query.fromBranch(CoreBranches.COMMON).andIds(SystemUser.Guest);

      ArtifactReadable originalGuest = queryBuilder.getResults().getExactlyOne();

      TransactionBuilder tx = createTx();
      tx.setSoleAttributeFromString(originalGuest, CoreAttributeTypes.Name, "Test");
      tx.commit();

      ArtifactReadable newGuest = queryBuilder.getResults().getExactlyOne();

      assertEquals("Guest", originalGuest.getName());
      assertEquals("Test", newGuest.getName());
   }

   @Test
   public void testDeleteArtifact() throws OseeCoreException {
      TransactionBuilder tx = createTx();
      ArtifactId artifact = tx.createArtifact(CoreArtifactTypes.AccessControlModel, "deleteMe");
      tx.commit();

      ArtifactReadable toDelete = query.fromBranch(CoreBranches.COMMON).andIds(artifact).getResults().getExactlyOne();

      tx = txFactory.createTransaction(CoreBranches.COMMON, userArtifact, testName.getMethodName());
      tx.deleteArtifact(toDelete);
      tx.commit();

      toDelete = query.fromBranch(CoreBranches.COMMON).andIds(artifact).includeDeleted().getResults().getOneOrNull();
      assertNotNull(toDelete);
      assertTrue(toDelete.isDeleted());
   }

   @Test
   public void testArtifactGetTransaction() throws OseeCoreException {
      TransactionBuilder tx = createTx();

      String guid = tx.createArtifact(CoreArtifactTypes.Component, "A component").getGuid();
      int startingTx = tx.commit().getId();

      ArtifactReadable artifact =
         query.fromBranch(CoreBranches.COMMON).andGuidsOrHrids(guid).getResults().getExactlyOne();
      assertEquals(startingTx, artifact.getTransaction());

      TransactionBuilder tx2 = createTx();
      tx2.setName(artifact, "Modified - component");
      int lastTx = tx2.commit().getId();

      assertTrue(startingTx != lastTx);

      ArtifactReadable currentArtifact =
         query.fromBranch(CoreBranches.COMMON).andGuidsOrHrids(guid).getResults().getExactlyOne();
      assertEquals(lastTx, currentArtifact.getTransaction());
   }

   @Test
   public void testRelate() throws OseeCoreException {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(CoreArtifactTypes.Component, "A component");
      ArtifactId art2 = tx1.createArtifact(CoreArtifactTypes.Component, "B component");
      tx1.relate(art1, CoreRelationTypes.Default_Hierarchical__Child, art2);
      tx1.commit();

      ArtifactReadable artifact = query.fromBranch(CoreBranches.COMMON).andIds(art1).getResults().getExactlyOne();
      assertEquals("A component", artifact.getName());

      ResultSet<ArtifactReadable> children = artifact.getChildren();
      assertEquals(1, children.size());
      assertEquals("B component", children.getExactlyOne().getName());
   }

   @Test
   public void testRelateWithSortType() throws OseeCoreException {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(CoreArtifactTypes.Component, "A component");
      ArtifactId art2 = tx1.createArtifact(CoreArtifactTypes.Component, "B component");
      ArtifactId art3 = tx1.createArtifact(CoreArtifactTypes.Component, "C component");
      tx1.addChildren(art1, art2, art3);
      int tx1Id = tx1.commit().getId();

      QueryBuilder art1Query = query.fromBranch(CoreBranches.COMMON).andIds(art1);

      ArtifactReadable artifact = art1Query.getResults().getExactlyOne();
      assertEquals("A component", artifact.getName());
      assertEquals(tx1Id, artifact.getTransaction());

      ResultSet<ArtifactReadable> children = artifact.getChildren();
      assertEquals(2, children.size());

      Iterator<ArtifactReadable> iterator = children.iterator();
      assertEquals("B component", iterator.next().getName());
      assertEquals("C component", iterator.next().getName());

      TransactionBuilder tx2 = createTx();
      ArtifactId art4 = tx2.createArtifact(Component, "D component");
      tx2.relate(art1, Default_Hierarchical__Child, art4, LEXICOGRAPHICAL_DESC);
      int tx2Id = tx2.commit().getId();

      ArtifactReadable artifact21 = art1Query.getResults().getExactlyOne();
      assertEquals("A component", artifact21.getName());
      assertEquals(tx2Id, artifact21.getTransaction());

      ResultSet<ArtifactReadable> children2 = artifact21.getChildren();
      assertEquals(3, children2.size());

      Iterator<ArtifactReadable> iterator2 = children2.iterator();
      assertEquals("D component", iterator2.next().getName());
      assertEquals("C component", iterator2.next().getName());
      assertEquals("B component", iterator2.next().getName());
   }

   @Test
   public void testAddChildren() throws OseeCoreException {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(Component, "C component");
      ArtifactId art3 = tx1.createArtifact(Component, "B component");
      tx1.commit();

      TransactionBuilder tx2 = createTx();
      tx2.addChildren(art1, art2, art3);
      tx2.commit();

      ArtifactReadable artifact1 = query.fromBranch(CoreBranches.COMMON).andIds(art1).getResults().getExactlyOne();
      assertEquals("A component", artifact1.getName());

      ResultSet<ArtifactReadable> children = artifact1.getChildren();
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
   public void testSetRationale() throws OseeCoreException {
      String rationale = "This is my rationale";

      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(Component, "B component");

      tx1.relate(art1, Default_Hierarchical__Child, art2);
      tx1.setRationale(art1, Default_Hierarchical__Child, art2, rationale);

      tx1.commit();

      ArtifactReadable artifact = query.fromBranch(CoreBranches.COMMON).andIds(art1).getResults().getExactlyOne();
      assertEquals("A component", artifact.getName());

      ResultSet<ArtifactReadable> children = artifact.getChildren();
      assertEquals(1, children.size());

      ArtifactReadable otherArtifact = children.getExactlyOne();
      assertEquals("B component", otherArtifact.getName());

      String actual1 = artifact.getRationale(Default_Hierarchical__Child, otherArtifact);
      assertEquals(rationale, actual1);

      String actual2 = otherArtifact.getRationale(Default_Hierarchical__Parent, artifact);
      assertEquals(rationale, actual2);
   }

   @Test
   public void testUnrelate() throws OseeCoreException {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(Component, "C component");
      ArtifactId art3 = tx1.createArtifact(Component, "B component");
      tx1.addChildren(art1, art2, art3);
      ArtifactId art4 = tx1.createArtifact(GeneralDocument, "Document");
      tx1.relate(art1, Dependency__Dependency, art4);
      int txId = tx1.commit().getId();

      ArtifactReadable artifact4 = query.fromBranch(CoreBranches.COMMON).andIds(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      ArtifactReadable artifact1 = artifact4.getRelated(CoreRelationTypes.Dependency__Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      Iterator<ArtifactReadable> iterator = artifact1.getChildren().iterator();
      assertEquals(art3, iterator.next());
      assertEquals(art2, iterator.next());

      // Un-relate a child
      TransactionBuilder tx2 = createTx();
      tx2.unrelate(art1, Default_Hierarchical__Child, art2);
      int txId2 = tx2.commit().getId();

      artifact4 = query.fromBranch(CoreBranches.COMMON).andIds(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      artifact1 = artifact4.getRelated(CoreRelationTypes.Dependency__Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      assertEquals(art3, artifact1.getChildren().getExactlyOne());
   }

   @Test
   public void testUnrelateFromAllByType() throws OseeCoreException {
      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(Component, "C component");
      ArtifactId art3 = tx1.createArtifact(Component, "B component");
      tx1.addChildren(art1, art2, art3);

      ArtifactId art4 = tx1.createArtifact(GeneralDocument, "Document");
      tx1.relate(art1, Dependency__Dependency, art4);
      tx1.commit();

      ArtifactReadable artifact4 = query.fromBranch(COMMON).andIds(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      ArtifactReadable artifact1 = artifact4.getRelated(Dependency__Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      Iterator<ArtifactReadable> iterator = artifact1.getChildren().iterator();
      assertEquals(art3, iterator.next());
      assertEquals(art2, iterator.next());

      // Unrelate All children
      TransactionBuilder tx2 = createTx();
      tx2.unrelateFromAll(Default_Hierarchical__Parent, art1);
      tx2.commit();

      artifact4 = query.fromBranch(COMMON).andIds(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      artifact1 = artifact4.getRelated(Dependency__Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      assertEquals(true, artifact1.getChildren().isEmpty());
   }

   @Test
   public void testUnrelateFromAll() throws OseeCoreException {
      ArtifactReadable artifact1;
      ArtifactReadable artifact2;
      ArtifactReadable artifact3;
      ArtifactReadable artifact4;

      TransactionBuilder tx1 = createTx();
      ArtifactId art1 = tx1.createArtifact(Component, "A component");
      ArtifactId art2 = tx1.createArtifact(Component, "C component");
      ArtifactId art3 = tx1.createArtifact(Component, "B component");
      tx1.addChildren(art1, art2, art3);

      ArtifactId art4 = tx1.createArtifact(GeneralDocument, "Document");
      tx1.relate(art1, Dependency__Dependency, art4);
      tx1.commit();

      artifact4 = query.fromBranch(COMMON).andIds(art4).getResults().getExactlyOne();
      assertEquals(art4, artifact4);

      artifact1 = artifact4.getRelated(Dependency__Artifact).getExactlyOne();
      assertEquals(art1, artifact1);

      Iterator<ArtifactReadable> iterator = artifact1.getChildren().iterator();
      assertEquals(art3, iterator.next());
      assertEquals(art2, iterator.next());

      TransactionBuilder tx2 = createTx();
      tx2.unrelateFromAll(art1);
      tx2.commit();

      ResultSet<ArtifactReadable> arts =
         query.fromBranch(COMMON).andIds(art1, art2, art3, art4).includeDeleted().getResults();
      Iterator<ArtifactReadable> iterator2 = arts.iterator();
      artifact1 = iterator2.next();
      artifact2 = iterator2.next();
      artifact3 = iterator2.next();
      artifact4 = iterator2.next();

      assertEquals(art1, artifact1);
      assertEquals(art2, artifact2);
      assertEquals(art3, artifact3);
      assertEquals(art4, artifact4);

      assertEquals(true, artifact1.getChildren().isEmpty());
      assertEquals(true, artifact1.getRelated(Dependency__Dependency).isEmpty());

      assertNull(artifact2.getParent());
      assertNull(artifact3.getParent());

      assertEquals(true, artifact4.getRelated(Dependency__Artifact).isEmpty());
   }

   private TransactionBuilder createTx() throws OseeCoreException {
      return txFactory.createTransaction(COMMON, userArtifact, testName.getMethodName());
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