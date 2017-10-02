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

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.UserArtifactCheck;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrderingTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static final BranchId branch = CoreBranches.COMMON;

   private final Set<Artifact> itemsToDelete = new HashSet<>();
   private Artifact parent;
   private Artifact child1;
   private Artifact child2;
   private Artifact child3;

   @Before
   public void setupArtifacts() throws Exception {
      parent = ArtifactTypeManager.addArtifact(Folder, branch, "parent");
      addToCleanup(parent);

      child1 = ArtifactTypeManager.addArtifact(Folder, branch, "a_child");
      addToCleanup(child1);
      child2 = ArtifactTypeManager.addArtifact(Folder, branch, "b_child");
      addToCleanup(child2);
      child3 = ArtifactTypeManager.addArtifact(Folder, branch, "c_child");
      addToCleanup(child3);

      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child1);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child2);
      parent.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child3);

      parent.persist(method.getQualifiedTestName());
   }

   @After
   public void cleanupArtifacts() throws Exception {
      try {
         UserArtifactCheck.setEnabled(false);
         for (Artifact artifact : itemsToDelete) {
            if (artifact != null) {
               ArtifactCache.deCache(artifact);
               artifact.deleteAndPersist();
            }
         }
      } finally {
         UserArtifactCheck.setEnabled(true);
      }
   }

   @Test
   public void testSetOrderAndSort() throws Exception {

      checkDesc();

      checkAsc();

      checkUserDefined();

      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, LEXICOGRAPHICAL_ASC);
      Attribute<Object> attribute = parent.getSoleAttribute(CoreAttributeTypes.RelationOrder);
      assertTrue("Setting the attribute back to the default type did not cause an attribute to be deleted",
         attribute == null || attribute.isDeleted());

      checkDesc();

      /**
       * set userId on Users so doesn't break user management
       */
      SkynetTransaction transaction = TransactionManager.createTransaction(branch, method.getQualifiedTestName());
      Artifact child4 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, branch, "a_child");
      child4.setSoleAttributeValue(CoreAttributeTypes.UserId, "a_child");
      child4.persist(transaction);
      addToCleanup(child4);

      Artifact child5 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, branch, "b_child");
      child5.setSoleAttributeValue(CoreAttributeTypes.UserId, "b_child");
      child5.persist(transaction);
      addToCleanup(child5);

      Artifact child6 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, branch, "c_child");
      child6.setSoleAttributeValue(CoreAttributeTypes.UserId, "c_child");
      child6.persist(transaction);
      addToCleanup(child6);

      parent.addRelation(CoreRelationTypes.Users_User, child4);
      parent.addRelation(CoreRelationTypes.Users_User, child5);
      parent.addRelation(CoreRelationTypes.Users_User, child6);
      parent.persist(transaction);

      parent.setRelationOrder(CoreRelationTypes.Users_User, LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationTypes.Users_Artifact, LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, LEXICOGRAPHICAL_ASC);
      transaction.execute();

      attribute = parent.getSoleAttribute(CoreAttributeTypes.RelationOrder);
      assertTrue("The attribute was deleted even though there was a still a non default sort order on the artifact.",
         attribute != null);

   }

   private void checkAsc() {
      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, LEXICOGRAPHICAL_ASC);
      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);
      Assert.assertEquals(3, children.size());
      Assert.assertEquals(children.get(0).getName(), "a_child");
      Assert.assertEquals(children.get(1).getName(), "b_child");
      Assert.assertEquals(children.get(2).getName(), "c_child");
   }

   private void checkDesc() {
      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, LEXICOGRAPHICAL_DESC);
      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);
      Assert.assertEquals(3, children.size());
      Assert.assertEquals(children.get(0).getName(), "c_child");
      Assert.assertEquals(children.get(1).getName(), "b_child");
      Assert.assertEquals(children.get(2).getName(), "a_child");
   }

   private void checkUserDefined() {
      List<Artifact> children = new ArrayList<>();
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

   @Test
   public void testUserDefinedOrderUpdatesListWhenRelationDeleted() {
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
   public void testOrderPersist() {
      String guid = GUID.create();
      SkynetTransaction transaction = TransactionManager.createTransaction(branch, method.getQualifiedTestName());
      Artifact mainFolder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, "Main Folder - " + guid);
      mainFolder.persist(transaction);

      mainFolder.setSingletonAttributeValue(CoreAttributeTypes.StaticId, method.getQualifiedTestName());
      OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch).addChild(mainFolder);
      List<Artifact> children = new ArrayList<>();
      for (int x = 0; x < 3; x++) {
         Artifact childArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, "New Child " + x + " - " + guid);
         children.add(childArt);
         addToCleanup(childArt);
         mainFolder.addChild(childArt);
         childArt.persist(transaction);
      }
      mainFolder.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, children);
      transaction.execute();

      Artifact newArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, "New Artifact " + guid);
      mainFolder.addChild(newArtifact);
      addToCleanup(newArtifact);
      newArtifact.setSingletonAttributeValue(CoreAttributeTypes.StaticId, method.getQualifiedTestName());
      newArtifact.persist(method.getQualifiedTestName());

      for (Artifact child : children) {
         Assert.assertFalse(child.isDirty());
      }
      Assert.assertFalse(newArtifact.isDirty());

      Assert.assertFalse("Artifact should not be dirty.", mainFolder.isDirty());
   }

   private void addToCleanup(Artifact artifact) {
      artifact.setSingletonAttributeValue(CoreAttributeTypes.StaticId, method.getQualifiedTestName());
      itemsToDelete.add(artifact);
   }
}
