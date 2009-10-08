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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
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
      parent = ArtifactTypeManager.getType(ARTIFACT_TYPE).makeNewArtifact(branch);

      child1 = ArtifactTypeManager.getType(ARTIFACT_TYPE).makeNewArtifact(branch);
      child1.setSoleAttributeFromString("Name", "a_child");

      child2 = ArtifactTypeManager.getType(ARTIFACT_TYPE).makeNewArtifact(branch);
      child2.setSoleAttributeFromString("Name", "b_child");

      child3 = ArtifactTypeManager.getType(ARTIFACT_TYPE).makeNewArtifact(branch);
      child3.setSoleAttributeFromString("Name", "c_child");

      parent.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, child1);
      parent.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, child2);
      parent.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, child3);
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

      parent.setRelationOrder(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD,
            RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);
      Attribute<Object> attribute = parent.getSoleAttribute(CoreAttributes.RELATION_ORDER.getName());
      assertTrue("Setting the attribute back to the default type did not cause an attribute to be deleted",
            (attribute == null || attribute.isDeleted()));

      checkDesc();

      Artifact child4 = ArtifactTypeManager.getType("User").makeNewArtifact(branch);
      child1.setSoleAttributeFromString("Name", "a_child");
      Artifact child5 = ArtifactTypeManager.getType("User").makeNewArtifact(branch);
      child2.setSoleAttributeFromString("Name", "b_child");
      Artifact child6 = ArtifactTypeManager.getType("User").makeNewArtifact(branch);

      parent.addRelation(CoreRelationEnumeration.Users_User, child4);
      parent.addRelation(CoreRelationEnumeration.Users_User, child5);
      parent.addRelation(CoreRelationEnumeration.Users_User, child6);
      parent.persist();

      parent.setRelationOrder(CoreRelationEnumeration.Users_User, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationEnumeration.Users_Artifact, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      parent.setRelationOrder(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD,
            RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);

      attribute = parent.getSoleAttribute(CoreAttributes.RELATION_ORDER.getName());
      assertTrue("The attribute was deleted even though there was a still a non default sort order on the artifact.",
            (attribute != null));

   }

   private void checkAsc() throws OseeCoreException {
      parent.setRelationOrder(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD,
            RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);
      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);
      Assert.assertEquals(3, children.size());
      Assert.assertEquals(children.get(0).getName(), "a_child");
      Assert.assertEquals(children.get(1).getName(), "b_child");
      Assert.assertEquals(children.get(2).getName(), "c_child");
   }

   private void checkDesc() throws OseeCoreException {
      parent.setRelationOrder(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD,
            RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);
      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);
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
      parent.setRelationOrder(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, children);
      children = parent.getRelatedArtifacts(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);
      Assert.assertEquals(3, children.size());
      Assert.assertEquals(children.get(0).getName(), "b_child");
      Assert.assertEquals(children.get(1).getName(), "c_child");
      Assert.assertEquals(children.get(2).getName(), "a_child");
   }

   @org.junit.Test
   public void testUserDefinedOrderUpdatesListWhenRelationDeleted() throws OseeCoreException {
      checkUserDefined();

      String artifactGuid = child3.getGuid();

      parent.deleteRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, child3);

      String orderString = parent.getSoleAttributeValue(CoreAttributes.RELATION_ORDER.getName());

      assertFalse(orderString.contains(artifactGuid));

      List<Artifact> children = parent.getRelatedArtifacts(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);
      Assert.assertEquals(2, children.size());
      Assert.assertEquals(children.get(0).getName(), "b_child");
      Assert.assertEquals(children.get(1).getName(), "a_child");
   }

}
