/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.List;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderMergeUtility;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class RelationOrderMergeUtilityTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public final OseeHousekeepingRule oseeHousekeeping = new OseeHousekeepingRule();

   private final RelationTypeSide defaultHierarchy = CoreRelationTypes.DefaultHierarchical_Child;
   private final RelationSorter ascOrder = LEXICOGRAPHICAL_ASC;
   private final RelationSorter descOrder = LEXICOGRAPHICAL_DESC;

   private RelationTypeToken hierType;
   private RelationSide hierSide;

   private BranchToken destBranch;

   @Before
   public void createBranch() {
      destBranch =
         BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, "RelationOrderMergeUtilityTest.createBranch");
      hierType = defaultHierarchy;
      hierSide = defaultHierarchy.getSide();
   }

   @After
   public void destroyBranch() {
      BranchManager.purgeBranch(destBranch);
   }

   @Test
   public void testTrivialMerge() {
      Artifact parent = TestUtil.createSimpleArtifact(Artifact, "Parent", destBranch);
      Artifact[] children =
         TestUtil.createSimpleArtifacts(Artifact, 5, "Relative", destBranch).toArray(new Artifact[5]);

      for (int i = 0; i < 5; i++) {
         setAsChild(parent, children[i], ascOrder);
      }
      parent.persist(getClass().getSimpleName() + ".testTrivialMerge()");
      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(parent, parent);
      Assert.assertNotNull(mergedOrder);

      RelationSorter sorter = mergedOrder.getCurrentSorterGuid(hierType, hierSide);
      Assert.assertEquals(ascOrder, sorter);
   }

   @Test
   public void testOrderMerge() {
      Artifact destParent = TestUtil.createSimpleArtifact(Artifact, "Parent", destBranch);
      Artifact[] destChildren =
         TestUtil.createSimpleArtifacts(Artifact, 5, "Relative", destBranch).toArray(new Artifact[5]);

      for (int i = 0; i <= 3; i++) {
         setAsChild(destParent, destChildren[i], USER_DEFINED);
      }
      destParent.persist(getClass().getSimpleName() + ".testOrderMerge()_1");

      BranchToken sourceBranch = BranchManager.createWorkingBranch(destBranch, "Source Branch");
      Artifact srcParent = ArtifactQuery.getArtifactFromId(destParent, sourceBranch);
      Artifact srcChild = ArtifactQuery.getArtifactFromId(destChildren[4], sourceBranch);
      setAsChild(srcParent, srcChild, USER_DEFINED);
      srcParent.persist(getClass().getSimpleName() + ".testOrderMerge()_2");
      SkynetTransaction transaction = TransactionManager.createTransaction(destBranch, "Delete Relations");
      XResultData rd = RelationManager.deleteRelationsAll(destChildren[0], true, transaction, new XResultData());
      if (XResultDataUI.reportIfErrors(rd, "Delete Relations")) {
         transaction.cancel();
         return;
      }
      transaction.execute();

      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(destParent, srcParent);
      Assert.assertNotNull(mergedOrder);

      List<String> orderList = mergedOrder.getOrderList(hierType, hierSide);
      Assert.assertEquals(4, orderList.size());
      Assert.assertEquals(destChildren[1].getGuid(), orderList.get(0));
      Assert.assertEquals(destChildren[2].getGuid(), orderList.get(1));
      Assert.assertEquals(destChildren[3].getGuid(), orderList.get(2));
      Assert.assertEquals(destChildren[4].getGuid(), orderList.get(3));

      BranchManager.purgeBranch(sourceBranch);
   }

   @Test
   public void testStrategyMerge() {

      Artifact ascParent = TestUtil.createSimpleArtifact(Artifact, "Parent", destBranch);
      Artifact[] ascRelatives =
         TestUtil.createSimpleArtifacts(Artifact, 5, "Relative", destBranch).toArray(new Artifact[5]);
      Artifact descParent = TestUtil.createSimpleArtifact(Artifact, "Parent", destBranch);
      Artifact[] descRelatives =
         TestUtil.createSimpleArtifacts(Artifact, 5, "Relative", destBranch).toArray(new Artifact[5]);

      for (int i = 0; i < 5; i++) {
         setAsChild(ascParent, ascRelatives[i], ascOrder);
         setAsChild(descParent, descRelatives[i], descOrder);
      }

      SkynetTransaction transaction1 =
         TransactionManager.createTransaction(destBranch, getClass().getSimpleName() + ".testStrategyMerge()_1");
      ascParent.persist(transaction1);
      transaction1.execute();

      SkynetTransaction transaction2 =
         TransactionManager.createTransaction(destBranch, getClass().getSimpleName() + ".testStrategyMerge()_2");
      descParent.persist(transaction2);
      transaction2.execute();

      RelationOrderData mergedOrder = RelationOrderMergeUtility.mergeRelationOrder(ascParent, descParent);
      Assert.assertNull(mergedOrder);
   }

   private void setAsChild(Artifact parent, Artifact child, RelationSorter sorter) {
      child.deleteRelations(CoreRelationTypes.DefaultHierarchical_Parent);
      parent.addRelation(sorter, CoreRelationTypes.DefaultHierarchical_Child, child);
   }
}
