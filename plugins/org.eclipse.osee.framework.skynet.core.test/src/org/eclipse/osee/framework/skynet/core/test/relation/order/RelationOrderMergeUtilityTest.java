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
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderMergeUtility;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class RelationOrderMergeUtilityTest {
   @Test
   public void testTrivialMerge() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact parent = FrameworkTestUtil.createSimpleArtifact(Artifact, "Parent", common);
      Artifact[] relatives =
            FrameworkTestUtil.createSimpleArtifacts(Artifact, 5, "Relative", common).toArray(new Artifact[0]);
      IRelationSorterId abc = RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC;
      CoreRelationTypes hierarchical = CoreRelationTypes.Default_Hierarchical__Child;
      RelationType type = RelationTypeManager.getType(hierarchical);
      RelationSide side = hierarchical.getSide();

      for (int i = 0; i < 5; i++) {
         relatives[i].deleteRelations(CoreRelationTypes.Default_Hierarchical__Child);
         relatives[i].deleteRelations(CoreRelationTypes.Default_Hierarchical__Parent);
         parent.addRelation(abc, hierarchical, relatives[i]);
      }
      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(parent, parent);
      Assert.assertNotNull(mergedOrder);
      String sorter = mergedOrder.getCurrentSorterGuid(type, side);
      Assert.assertEquals(RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC.getGuid(), sorter);
   }

   @Test
   public void testOrderMerge() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact parent = FrameworkTestUtil.createSimpleArtifact(Artifact, "Parent", common);
      Artifact[] children =
            FrameworkTestUtil.createSimpleArtifacts(Artifact, 5, "Relative", common).toArray(new Artifact[0]);

      CoreRelationTypes defaultHierarchy = CoreRelationTypes.Default_Hierarchical__Child;
      IRelationSorterId userDefinedOrder = RelationOrderBaseTypes.USER_DEFINED;

      for (int i = 0; i < 4; i++) {
         children[i].deleteRelations(CoreRelationTypes.Default_Hierarchical__Child);
         children[i].deleteRelations(CoreRelationTypes.Default_Hierarchical__Parent);
         parent.addRelation(userDefinedOrder, defaultHierarchy, children[i]);
      }

      parent.persist();
      Branch common2 = BranchManager.createWorkingBranch(common, "Common2", null);
      Artifact branchParent = ArtifactQuery.getArtifactFromId(parent.getGuid(), common2);
      for (int i = 0; i < 5; i++) {
         Artifact relative = ArtifactQuery.getArtifactFromId(children[i].getGuid(), common2);
         relative.deleteRelations(CoreRelationTypes.Default_Hierarchical__Child);
         relative.deleteRelations(CoreRelationTypes.Default_Hierarchical__Parent);
         branchParent.addRelation(userDefinedOrder, defaultHierarchy, relative);
         branchParent.persist();
      }
      children[0].deleteAndPersist();

      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(parent, branchParent);
      Assert.assertNotNull(mergedOrder);

      BranchManager.purgeBranch(common2);
      parent.deleteAndPersist();
      for (int i = 0; i < 5; i++) {
         children[i].deleteAndPersist();
      }
   }

   @Test
   public void testStrategyMerge() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact ascParent = FrameworkTestUtil.createSimpleArtifact(Artifact, "Parent", common);
      Artifact[] ascRelatives =
            FrameworkTestUtil.createSimpleArtifacts(Artifact, 5, "Relative", common).toArray(new Artifact[0]);
      Artifact descParent = FrameworkTestUtil.createSimpleArtifact(Artifact, "Parent", common);
      Artifact[] descRelatives =
            FrameworkTestUtil.createSimpleArtifacts(Artifact, 5, "Relative", common).toArray(new Artifact[0]);

      CoreRelationTypes hierarchical = CoreRelationTypes.Default_Hierarchical__Child;
      IRelationSorterId ascOrder = RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC;
      IRelationSorterId descOrder = RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC;

      for (int i = 0; i < 5; i++) {
         ascRelatives[i].deleteRelations(CoreRelationTypes.Default_Hierarchical__Child);
         ascRelatives[i].deleteRelations(CoreRelationTypes.Default_Hierarchical__Parent);
         descRelatives[i].deleteRelations(CoreRelationTypes.Default_Hierarchical__Child);
         descRelatives[i].deleteRelations(CoreRelationTypes.Default_Hierarchical__Parent);
         ascParent.addRelation(ascOrder, hierarchical, ascRelatives[i]);
         descParent.addRelation(descOrder, hierarchical, descRelatives[i]);
      }
      ascParent.persist();
      descParent.persist();
      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(ascParent, descParent);
      Assert.assertNull(mergedOrder);
   }
}
