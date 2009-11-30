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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrderingTest {

   private static final String ARTIFACT_TYPE = "Folder";

   private Branch branch;
   private Artifact parent;
   private Artifact child1;
   private Artifact child2;
   private Artifact child3;

   @Before
   public void setupArtifacts() throws Exception {
      branch = BranchManager.getCommonBranch();
      parent = createArtifact(ARTIFACT_TYPE, branch);

      child1 = createArtifact(ARTIFACT_TYPE, branch);
      child1.setSoleAttributeFromString("Name", "a_child");

      child2 = createArtifact(ARTIFACT_TYPE, branch);
      child2.setSoleAttributeFromString("Name", "b_child");

      child3 = createArtifact(ARTIFACT_TYPE, branch);
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

      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child,
            RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);
      Attribute<Object> attribute = parent.getSoleAttribute(CoreAttributeTypes.RELATION_ORDER.getName());
      assertTrue("Setting the attribute back to the default type did not cause an attribute to be deleted",
            (attribute == null || attribute.isDeleted()));

      checkDesc();

      Artifact child4 = createArtifact("User", branch);
      child1.setSoleAttributeFromString("Name", "a_child");
      Artifact child5 = createArtifact("User", branch);
      child2.setSoleAttributeFromString("Name", "b_child");
      Artifact child6 = createArtifact("User", branch);

      parent.addRelation(CoreRelationTypes.Users_User, child4);
      parent.addRelation(CoreRelationTypes.Users_User, child5);
      parent.addRelation(CoreRelationTypes.Users_User, child6);
      parent.persist();

      parent.setRelationOrder(CoreRelationTypes.Users_User, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationTypes.Users_Artifact, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child,
            RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);

      attribute = parent.getSoleAttribute(CoreAttributeTypes.RELATION_ORDER.getName());
      assertTrue("The attribute was deleted even though there was a still a non default sort order on the artifact.",
            (attribute != null));

   }

   private void checkAsc() throws OseeCoreException {
      parent.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child,
            RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);
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

      String orderString = parent.getSoleAttributeValue(CoreAttributeTypes.RELATION_ORDER.getName());

      assertFalse(orderString.contains(artifactGuid));

      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child);
      Assert.assertEquals(2, children.size());
      Assert.assertEquals(children.get(0).getName(), "b_child");
      Assert.assertEquals(children.get(1).getName(), "a_child");
   }

   private Artifact createArtifact(String type, Branch branch) throws OseeCoreException {
      return ArtifactTypeManager.makeNewArtifact(ArtifactTypeManager.getType(type), branch);
   }
}
