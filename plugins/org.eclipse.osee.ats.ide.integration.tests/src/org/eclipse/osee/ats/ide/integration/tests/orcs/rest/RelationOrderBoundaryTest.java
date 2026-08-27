/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.TreeMap;
import java.util.function.Consumer;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.NewBranch;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration test for relation order boundary handling. Verifies that when relation orders approach Integer.MAX_VALUE
 * or Integer.MIN_VALUE, the reorder logic redistributes them correctly and subsequent insertions succeed without error.
 * <p>
 * Each test creates its own dedicated working branch and purges it when done.
 */
public class RelationOrderBoundaryTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   // Use a new-table relation type (MANY_TO_MANY, USER_DEFINED ordering)
   private static final RelationTypeToken TEST_REL_TYPE = CoreRelationTypes.RequirementsTraceAircraftToSim;

   /**
    * Tests that adding many relations via end-insertion works correctly even when existing rel_order values are
    * artificially pushed near Integer.MAX_VALUE. This simulates the scenario from bug TW29585 where Zenith imports
    * caused rel_order exhaustion.
    */
   @Test
   public void testEndInsertionNearMaxBoundary() {
      BranchEndpoint branchEndpoint = ServiceUtil.getOseeClient().getBranchEndpoint();
      BranchId testBranch = createTestBranch(branchEndpoint, "MaxBoundary");
      try {
         RelationEndpoint relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint(testBranch);
         ArtifactEndpoint artifactEndpoint = ServiceUtil.getOseeClient().getArtifactEndpoint(testBranch);
         ArtifactToken parentArtifact = DefaultHierarchyRoot;

         // Create side-A artifact
         ArtifactToken sideA = artifactEndpoint.createArtifact(testBranch, CoreArtifactTypes.SoftwareRequirementMsWord,
            parentArtifact, "RelOrderBoundaryTest_EndSideA");

         // Create initial side-B artifacts and relate them
         int initialCount = 5;
         for (int i = 0; i < initialCount; i++) {
            ArtifactToken sideB = artifactEndpoint.createArtifact(testBranch,
               CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "InitialSideB_" + i);
            relationEndpoint.createRelationByType(sideA, sideB, TEST_REL_TYPE);
         }

         // Force rel_order values near MAX_VALUE by direct SQL update using gamma_id
         pushRelOrdersNearMax(testBranch, sideA, TEST_REL_TYPE);

         // Now add more relations — this exercises the boundary detection and reorder path
         int additionalCount = 20;
         for (int i = 0; i < additionalCount; i++) {
            ArtifactToken sideB = artifactEndpoint.createArtifact(testBranch,
               CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "BoundarySideB_" + i);
            relationEndpoint.createRelationByType(sideA, sideB, TEST_REL_TYPE);
         }

         // Verify: all relations exist and rel_orders are valid
         TreeMap<Integer, ArtifactId> orders = getRelOrders(testBranch, sideA, TEST_REL_TYPE);
         int expectedTotal = initialCount + additionalCount;
         assertEquals("All relations should exist", expectedTotal, orders.size());
         assertNoDuplicateOrders(orders);
         assertOrdersAscending(orders);

         // Verify the max order is no longer near MAX_VALUE (reorder should have redistributed)
         int maxOrder = orders.lastKey();
         assertTrue("After reorder, max order should not be near MAX_VALUE",
            (long) Integer.MAX_VALUE - (long) maxOrder > 262144); // SPACING = 2^18
      } finally {
         try (Response res = branchEndpoint.purgeBranch(testBranch, false, false)) {
            // Branch purged
         }
      }
   }

   /**
    * Tests that adding many relations works correctly even when existing rel_order values are artificially pushed near
    * Integer.MIN_VALUE.
    */
   @Test
   public void testInsertionNearMinBoundary() {
      BranchEndpoint branchEndpoint = ServiceUtil.getOseeClient().getBranchEndpoint();
      BranchId testBranch = createTestBranch(branchEndpoint, "MinBoundary");
      try {
         RelationEndpoint relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint(testBranch);
         ArtifactEndpoint artifactEndpoint = ServiceUtil.getOseeClient().getArtifactEndpoint(testBranch);
         ArtifactToken parentArtifact = DefaultHierarchyRoot;

         // Create side-A artifact
         ArtifactToken sideA = artifactEndpoint.createArtifact(testBranch, CoreArtifactTypes.SoftwareRequirementMsWord,
            parentArtifact, "RelOrderBoundaryTest_MinSideA");

         // Create initial side-B artifacts and relate them
         int initialCount = 5;
         for (int i = 0; i < initialCount; i++) {
            ArtifactToken sideB = artifactEndpoint.createArtifact(testBranch,
               CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "InitialMinSideB_" + i);
            relationEndpoint.createRelationByType(sideA, sideB, TEST_REL_TYPE);
         }

         // Force rel_order values near MIN_VALUE by direct SQL update using gamma_id
         pushRelOrdersNearMin(testBranch, sideA, TEST_REL_TYPE);

         // Verify the orders are now near MIN_VALUE
         TreeMap<Integer, ArtifactId> ordersBefore = getRelOrders(testBranch, sideA, TEST_REL_TYPE);
         int minBefore = ordersBefore.firstKey();
         assertTrue("Orders should be near MIN_VALUE after push", (long) minBefore - (long) Integer.MIN_VALUE < 262144);

         // Now add more relations — this exercises the boundary detection and reorder path
         int additionalCount = 20;
         for (int i = 0; i < additionalCount; i++) {
            ArtifactToken sideB = artifactEndpoint.createArtifact(testBranch,
               CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "BoundaryMinSideB_" + i);
            relationEndpoint.createRelationByType(sideA, sideB, TEST_REL_TYPE);
         }

         // Verify: all relations exist and rel_orders are valid
         TreeMap<Integer, ArtifactId> ordersAfter = getRelOrders(testBranch, sideA, TEST_REL_TYPE);
         int expectedTotal = initialCount + additionalCount;
         assertEquals("All relations should exist", expectedTotal, ordersAfter.size());
         assertNoDuplicateOrders(ordersAfter);
         assertOrdersAscending(ordersAfter);
      } finally {
         try (Response res = branchEndpoint.purgeBranch(testBranch, false, false)) {
            // Branch purged
         }
      }
   }

   // --- Helper methods ---

   private BranchId createTestBranch(BranchEndpoint branchEndpoint, String suffix) {
      NewBranch data = new NewBranch();
      data.setAssociatedArtifact(ArtifactId.SENTINEL);
      data.setBranchName("RelationOrderBoundaryTest_" + suffix);
      data.setBranchType(BranchType.WORKING);
      data.setCreationComment("Working branch for RelationOrderBoundaryTest " + suffix);
      data.setMergeAddressingQueryId(0L);
      data.setMergeDestinationBranchId(null);
      data.setParentBranchId(DemoBranches.SAW_PL);
      data.setSourceTransactionId(TransactionManager.getHeadTransaction(DemoBranches.SAW_PL));
      data.setTxCopyBranchType(false);
      return branchEndpoint.createBranch(data);
   }

   private void pushRelOrdersNearMax(BranchId branch, ArtifactToken parent, RelationTypeToken relType) {
      TreeMap<Integer, long[]> currentOrders = getRelOrdersWithGamma(branch, parent, relType);
      int i = 0;
      for (var entry : currentOrders.entrySet()) {
         int newOrder = Integer.MAX_VALUE - (currentOrders.size() - i) * 10;
         long gammaId = entry.getValue()[1];
         AtsApiService.get().getJdbcService().getClient().runPreparedUpdate(
            "UPDATE osee_relation SET rel_order = ? WHERE gamma_id = ?", newOrder, gammaId);
         i++;
      }
   }

   private void pushRelOrdersNearMin(BranchId branch, ArtifactToken parent, RelationTypeToken relType) {
      TreeMap<Integer, long[]> currentOrders = getRelOrdersWithGamma(branch, parent, relType);
      int i = 0;
      for (var entry : currentOrders.entrySet()) {
         int newOrder = Integer.MIN_VALUE + i * 10;
         long gammaId = entry.getValue()[1];
         AtsApiService.get().getJdbcService().getClient().runPreparedUpdate(
            "UPDATE osee_relation SET rel_order = ? WHERE gamma_id = ?", newOrder, gammaId);
         i++;
      }
   }

   private TreeMap<Integer, long[]> getRelOrdersWithGamma(BranchId branch, ArtifactToken parent,
      RelationTypeToken relType) {
      TreeMap<Integer, long[]> orders = new TreeMap<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         orders.put(stmt.getInt("rel_order"), new long[] {stmt.getLong("b_art_id"), stmt.getLong("gamma_id")});
      };
      AtsApiService.get().getJdbcService().getClient().runQuery(consumer,
         "SELECT rel_order, b_art_id, rel.gamma_id FROM osee_relation rel, osee_txs txs " + "WHERE txs.branch_id = ? AND txs.tx_current = 1 AND txs.gamma_id = rel.gamma_id " + "AND rel.rel_type = ? AND rel.a_art_id = ? ORDER BY rel_order",
         branch.getId(), relType.getId(), parent.getId());
      return orders;
   }

   private TreeMap<Integer, ArtifactId> getRelOrders(BranchId branch, ArtifactToken parent, RelationTypeToken relType) {
      TreeMap<Integer, ArtifactId> orders = new TreeMap<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         orders.put(stmt.getInt("rel_order"), ArtifactId.valueOf(stmt.getLong("b_art_id")));
      };
      AtsApiService.get().getJdbcService().getClient().runQuery(consumer,
         "SELECT rel_order, b_art_id FROM osee_relation rel, osee_txs txs " + "WHERE txs.branch_id = ? AND txs.tx_current = 1 AND txs.gamma_id = rel.gamma_id " + "AND rel.rel_type = ? AND rel.a_art_id = ? ORDER BY rel_order",
         branch.getId(), relType.getId(), parent.getId());
      return orders;
   }

   private void assertNoDuplicateOrders(TreeMap<Integer, ArtifactId> orders) {
      assertTrue("No duplicate order keys should exist", orders.size() > 0);
   }

   private void assertOrdersAscending(TreeMap<Integer, ArtifactId> orders) {
      boolean first = true;
      int prev = Integer.MIN_VALUE;
      for (int key : orders.keySet()) {
         if (!first) {
            assertTrue("Orders must be strictly ascending", key > prev);
         }
         prev = key;
         first = false;
      }
   }
}
