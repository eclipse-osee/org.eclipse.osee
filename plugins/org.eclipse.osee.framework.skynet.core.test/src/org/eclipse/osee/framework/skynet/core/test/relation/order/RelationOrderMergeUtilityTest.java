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
package org.eclipse.osee.framework.skynet.core.test.relation.order;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes.USER_DEFINED;
import static org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil.createSimpleArtifact;
import static org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil.createSimpleArtifacts;
import java.util.List;
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderMergeUtility;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class RelationOrderMergeUtilityTest {
   private final CoreRelationTypes defaultHierarchy = CoreRelationTypes.Default_Hierarchical__Child;
   private final IRelationSorterId ascOrder = RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC;
   private final IRelationSorterId descOrder = RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC;

   private RelationType hierType;
   private RelationSide hierSide;

   private Branch destBranch;

   @Before
   public void createBranch() throws OseeCoreException {
      String branchName = GUID.create();
      destBranch = BranchManager.createWorkingBranch(BranchManager.getSystemRootBranch(), branchName, null);
      hierType = RelationTypeManager.getType(defaultHierarchy);
      hierSide = defaultHierarchy.getSide();
   }

   @After
   public void destroyBranch() throws OseeCoreException {
      BranchManager.purgeBranch(destBranch);
   }

   @Test
   public void testTrivialMerge() throws OseeCoreException {
      Artifact parent = FrameworkTestUtil.createSimpleArtifact(Artifact, "Parent", destBranch);
      Artifact[] children = createSimpleArtifacts(Artifact, 5, "Relative", destBranch).toArray(new Artifact[0]);

      for (int i = 0; i < 5; i++) {
         setAsChild(parent, children[i], ascOrder);
      }
      parent.persist();
      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(parent, parent);
      Assert.assertNotNull(mergedOrder);

      String sorter = mergedOrder.getCurrentSorterGuid(hierType, hierSide);
      Assert.assertEquals(ascOrder.getGuid(), sorter);
   }

   @Test
   public void testOrderMerge() throws OseeCoreException {
      Artifact destParent = FrameworkTestUtil.createSimpleArtifact(Artifact, "Parent", destBranch);
      Artifact[] destChildren = createSimpleArtifacts(Artifact, 5, "Relative", destBranch).toArray(new Artifact[0]);

      for (int i = 0; i <= 3; i++) {
         setAsChild(destParent, destChildren[i], USER_DEFINED);
      }
      destParent.persist();

      Branch sourceBranch = BranchManager.createWorkingBranch(destBranch, "Source Branch", null);
      Artifact srcParent = ArtifactQuery.getArtifactFromId(destParent.getGuid(), sourceBranch);
      Artifact srcChild = ArtifactQuery.getArtifactFromId(destChildren[4].getGuid(), sourceBranch);
      setAsChild(srcParent, srcChild, USER_DEFINED);
      srcParent.persist();
      RelationManager.deleteRelationsAll(destChildren[0], true);

      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(destParent, srcParent);
      Assert.assertNotNull(mergedOrder);

      List<String> orderList = mergedOrder.getOrderList(hierType, hierSide);
      Assert.assertEquals(orderList.size(), 4);
      Assert.assertTrue(orderList.get(0).equals(destChildren[1].getGuid()));
      Assert.assertTrue(orderList.get(1).equals(destChildren[2].getGuid()));
      Assert.assertTrue(orderList.get(2).equals(destChildren[3].getGuid()));
      Assert.assertTrue(orderList.get(3).equals(destChildren[4].getGuid()));

      BranchManager.purgeBranch(sourceBranch);
   }

   @Test
   public void testStrategyMerge() throws OseeCoreException {
      Artifact ascParent = createSimpleArtifact(Artifact, "Parent", destBranch);
      Artifact[] ascRelatives = createSimpleArtifacts(Artifact, 5, "Relative", destBranch).toArray(new Artifact[0]);
      Artifact descParent = createSimpleArtifact(Artifact, "Parent", destBranch);
      Artifact[] descRelatives = createSimpleArtifacts(Artifact, 5, "Relative", destBranch).toArray(new Artifact[0]);

      for (int i = 0; i < 5; i++) {
         setAsChild(ascParent, ascRelatives[i], ascOrder);
         setAsChild(descParent, descRelatives[i], descOrder);
      }

      ascParent.persist();
      descParent.persist();
      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(ascParent, descParent);
      Assert.assertNull(mergedOrder);
   }

   private void setAsChild(Artifact parent, Artifact child, IRelationSorterId sorter) throws OseeCoreException {
      child.deleteRelations(CoreRelationTypes.Default_Hierarchical__Parent);
      parent.addRelation(sorter, CoreRelationTypes.Default_Hierarchical__Child, child);
   }
}
