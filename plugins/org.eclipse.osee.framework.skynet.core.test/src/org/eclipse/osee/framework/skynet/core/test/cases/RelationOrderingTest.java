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
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
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
      Set<Artifact> artsToDel =
         StaticIdManager.getArtifactsFromArtifactQuery(CoreArtifactTypes.Folder, STATIC_ID_TO_DELETE,
            BranchManager.getCommonBranch());
      if (artsToDel.size() > 0) {
         new PurgeArtifacts(artsToDel).execute();
         Thread.sleep(5000);
      }
   }

   @Before
   public void setupArtifacts() throws Exception {
      branch = BranchManager.getCommonBranch();
      parent = createArtifact(CoreArtifactTypes.Folder, branch);

      child1 = createArtifact(CoreArtifactTypes.Folder, branch);
      child1.setSoleAttributeFromString("Name", "a_child");

      child2 = createArtifact(CoreArtifactTypes.Folder, branch);
      child2.setSoleAttributeFromString("Name", "b_child");

      child3 = createArtifact(CoreArtifactTypes.Folder, branch);
      child3.setSoleAttributeFromString("Name", "c_child");

      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child1);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child2);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child3);

   }

   @After
   public void cleanupArtifacts() throws Exception {
      //      parent.deleteAndPersist();
      //      child1.deleteAndPersist();
      //      child2.deleteAndPersist();
      //      child3.deleteAndPersist();
   }

   @org.junit.Test
   public void testSetOrderAndSort() throws Exception {

      checkDesc();

      checkAsc();

      checkUserDefined();

      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);
      Attribute<Object> attribute = parent.getSoleAttribute(CoreAttributeTypes.RELATION_ORDER);
      assertTrue("Setting the attribute back to the default type did not cause an attribute to be deleted",
         (attribute == null || attribute.isDeleted()));

      checkDesc();

      Artifact child4 = createArtifact(CoreArtifactTypes.User, branch);
      child1.setSoleAttributeFromString("Name", "a_child");
      Artifact child5 = createArtifact(CoreArtifactTypes.User, branch);
      child2.setSoleAttributeFromString("Name", "b_child");
      Artifact child6 = createArtifact(CoreArtifactTypes.User, branch);

      parent.addRelation(CoreRelationTypes.Users_User, child4);
      parent.addRelation(CoreRelationTypes.Users_User, child5);
      parent.addRelation(CoreRelationTypes.Users_User, child6);
      parent.persist();

      parent.setRelationOrder(CoreRelationTypes.Users_User, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationTypes.Users_Artifact, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);

      attribute = parent.getSoleAttribute(CoreAttributeTypes.RELATION_ORDER);
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

      String orderString = parent.getSoleAttributeValue(CoreAttributeTypes.RELATION_ORDER);

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
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch(), "Test");
      Artifact mainFolder =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
            "Main Folder - " + guid);
      mainFolder.persist(transaction);
      StaticIdManager.setSingletonAttributeValue(mainFolder, STATIC_ID_TO_DELETE);
      OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch()).addChild(mainFolder);
      List<Artifact> children = new ArrayList<Artifact>();
      for (int x = 0; x < 3; x++) {
         Artifact childArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
               "New Child " + x + " - " + guid);
         children.add(childArt);
         StaticIdManager.setSingletonAttributeValue(childArt, STATIC_ID_TO_DELETE);
         mainFolder.addChild(childArt);
         childArt.persist(transaction);
      }
      mainFolder.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, children);
      transaction.execute();

      Artifact newArtifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
            "New Artifact " + guid);
      mainFolder.addChild(newArtifact);
      StaticIdManager.setSingletonAttributeValue(newArtifact, STATIC_ID_TO_DELETE);
      newArtifact.persist();

      for (Artifact child : children) {
         Assert.assertFalse(child.isDirty());
      }
      Assert.assertFalse(newArtifact.isDirty());

      Assert.assertFalse("Artifact should not be dirty.", mainFolder.isDirty());
   }

   private Artifact createArtifact(IArtifactType artifactType, Branch branch) throws OseeCoreException {
      return ArtifactTypeManager.makeNewArtifact(artifactType, branch);
   }
}
