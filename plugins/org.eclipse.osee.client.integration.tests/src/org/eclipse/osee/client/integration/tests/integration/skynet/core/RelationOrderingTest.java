/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrderingTest {

   private static final String STATIC_ID_TO_DELETE = "testOrderPersist";

   private Branch branch;
   private Artifact parent;
   private Artifact child1;
   private Artifact child2;
   private Artifact child3;

   @BeforeClass
   @AfterClass
   public static void setupTeardown() throws Exception {
      Set<Artifact> artsToDel = new HashSet<Artifact>();
      artsToDel.addAll(ArtifactQuery.getArtifactListFromTypeAndAttribute(CoreArtifactTypes.Folder,
         CoreAttributeTypes.StaticId, STATIC_ID_TO_DELETE, BranchManager.getCommonBranch()));
      if (artsToDel.size() > 0) {
         Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artsToDel));
         Thread.sleep(5000);
      }
   }

   @Before
   public void setupArtifacts() throws Exception {
      branch = BranchManager.getCommonBranch();
      parent = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, "parent");
      addStaticIdToDelete(parent);

      child1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, "a_child");
      addStaticIdToDelete(child1);
      child2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, "b_child");
      addStaticIdToDelete(child2);
      child3 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, "c_child");
      addStaticIdToDelete(child3);

      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child1);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child2);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child3);

   }

   @After
   public void cleanupArtifacts() throws Exception {
      for (Artifact artifact : new Artifact[] {parent, child1, child2, child3}) {
         ArtifactCache.deCache(artifact);
         artifact.deleteAndPersist();
      }
   }

   @org.junit.Test
   public void testSetOrderAndSort() throws Exception {

      checkDesc();

      checkAsc();

      checkUserDefined();

      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);
      Attribute<Object> attribute = parent.getSoleAttribute(CoreAttributeTypes.RelationOrder);
      assertTrue("Setting the attribute back to the default type did not cause an attribute to be deleted",
         (attribute == null || attribute.isDeleted()));

      checkDesc();

      /**
       * set userId on Users so doesn't break user management
       */
      SkynetTransaction transaction = TransactionManager.createTransaction(branch, getClass().getSimpleName());
      Artifact child4 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, branch, "a_child");
      child4.setSoleAttributeValue(CoreAttributeTypes.UserId, "a_child");
      child4.persist(transaction);
      addStaticIdToDelete(child4);

      Artifact child5 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, branch, "b_child");
      child5.setSoleAttributeValue(CoreAttributeTypes.UserId, "b_child");
      child5.persist(transaction);
      addStaticIdToDelete(child5);

      Artifact child6 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, branch, "c_child");
      child6.setSoleAttributeValue(CoreAttributeTypes.UserId, "c_child");
      child6.persist(transaction);
      addStaticIdToDelete(child6);

      parent.addRelation(CoreRelationTypes.Users_User, child4);
      parent.addRelation(CoreRelationTypes.Users_User, child5);
      parent.addRelation(CoreRelationTypes.Users_User, child6);
      parent.persist(transaction);

      parent.setRelationOrder(CoreRelationTypes.Users_User, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationTypes.Users_Artifact, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);
      transaction.execute();

      attribute = parent.getSoleAttribute(CoreAttributeTypes.RelationOrder);
      assertTrue("The attribute was deleted even though there was a still a non default sort order on the artifact.",
         (attribute != null));

   }

   private void checkAsc() throws OseeCoreException {
      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);
      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);
      Assert.assertEquals(3, children.size());
      Assert.assertEquals(children.get(0).getName(), "a_child");
      Assert.assertEquals(children.get(1).getName(), "b_child");
      Assert.assertEquals(children.get(2).getName(), "c_child");
   }

   private void checkDesc() throws OseeCoreException {
      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child,
         RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);
      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);
      Assert.assertEquals(3, children.size());
      Assert.assertEquals(children.get(0).getName(), "c_child");
      Assert.assertEquals(children.get(1).getName(), "b_child");
      Assert.assertEquals(children.get(2).getName(), "a_child");
   }

   private void checkUserDefined() throws OseeCoreException {
      List<Artifact> children = new ArrayList<Artifact>();
      children.add(child2);
      children.add(child3);
      children.add(child1);
      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, children);
      children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);
      Assert.assertEquals(3, children.size());
      Assert.assertEquals(children.get(0).getName(), "b_child");
      Assert.assertEquals(children.get(1).getName(), "c_child");
      Assert.assertEquals(children.get(2).getName(), "a_child");
   }

   @org.junit.Test
   public void testUserDefinedOrderUpdatesListWhenRelationDeleted() throws OseeCoreException {
      checkUserDefined();

      String artifactGuid = child3.getGuid();

      parent.deleteRelation(CoreRelationTypes.Default_Hierarchical__Child, child3);

      String orderString = parent.getSoleAttributeValue(CoreAttributeTypes.RelationOrder);

      assertFalse(orderString.contains(artifactGuid));

      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);
      Assert.assertEquals(2, children.size());
      Assert.assertEquals(children.get(0).getName(), "b_child");
      Assert.assertEquals(children.get(1).getName(), "a_child");
   }

   /**
    * This tests the case where a parent artifact already exists and is persisted in the database with ordered children.
    * Then a new artifact is created and added with a persist() call on the artifact. This persists the new artifact and
    * the new relation, but does not persist the relation order attribute stored on the parent.<br>
    */
   @Test
   public void testOrderPersist() throws OseeCoreException {
      String guid = GUID.create();
      SkynetTransaction transaction = TransactionManager.createTransaction(BranchManager.getCommonBranch(), "Test");
      Artifact mainFolder =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
            "Main Folder - " + guid);
      mainFolder.persist(transaction);
      mainFolder.setSingletonAttributeValue(CoreAttributeTypes.StaticId, STATIC_ID_TO_DELETE);
      OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch()).addChild(mainFolder);
      List<Artifact> children = new ArrayList<Artifact>();
      for (int x = 0; x < 3; x++) {
         Artifact childArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
               "New Child " + x + " - " + guid);
         children.add(childArt);
         addStaticIdToDelete(childArt);
         mainFolder.addChild(childArt);
         childArt.persist(transaction);
      }
      mainFolder.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, children);
      transaction.execute();

      Artifact newArtifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
            "New Artifact " + guid);
      mainFolder.addChild(newArtifact);
      addStaticIdToDelete(newArtifact);
      newArtifact.setSingletonAttributeValue(CoreAttributeTypes.StaticId, STATIC_ID_TO_DELETE);
      newArtifact.persist(getClass().getSimpleName());

      for (Artifact child : children) {
         Assert.assertFalse(child.isDirty());
      }
      Assert.assertFalse(newArtifact.isDirty());

      Assert.assertFalse("Artifact should not be dirty.", mainFolder.isDirty());
   }

   private void addStaticIdToDelete(Artifact artifact) throws OseeCoreException {
      artifact.setSingletonAttributeValue(CoreAttributeTypes.StaticId, STATIC_ID_TO_DELETE);
   }
}
