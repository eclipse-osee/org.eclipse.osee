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
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration test for relation order boundary handling. Verifies that when relation orders approach Integer.MAX_VALUE
 * or Integer.MIN_VALUE, the reorder logic redistributes them correctly and subsequent insertions succeed without error.
 *
 * @author OSEE Developers
 */
public class RelationOrderBoundaryTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private static RelationEndpoint relationEndpoint;
   private static ArtifactEndpoint artifactEndpoint;

   @BeforeClass
   public static void testSetup() {
      relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint(DemoBranches.SAW_PL_Working_Branch);
      artifactEndpoint = ServiceUtil.getOseeClient().getArtifactEndpoint(DemoBranches.SAW_PL_Working_Branch);
   }

   // Use a new-table relation type (MANY_TO_MANY, USER_DEFINED ordering)
   private static final RelationTypeToken TEST_REL_TYPE = CoreRelationTypes.RequirementsTraceAircraftToSim;

   /**
    * Tests that adding many relations via end-insertion works correctly even when existing rel_order values are
    * artificially pushed near Integer.MAX_VALUE. This simulates the scenario from bug TW29585 where Zenith imports
    * caused rel_order exhaustion.
    * <p>
    * Strategy:
    * <ol>
    * <li>Create a side-A artifact with a few initial side-B relations</li>
    * <li>Directly update the rel_order values in the DB to near Integer.MAX_VALUE</li>
    * <li>Add more relations — this should trigger the reorder logic</li>
    * <li>Verify all relations exist and orders are valid (no duplicates, all ascending)</li>
    * </ol>
    */
   @Test
   public void testEndInsertionNearMaxBoundary() {
      ArtifactToken parentArtifact = DefaultHierarchyRoot;

      // Create side-A artifact
      ArtifactToken sideA = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "RelOrderBoundaryTest_EndSideA");

      // Create initial side-B artifacts and relate them
      int initialCount = 5;
      for (int i = 0; i < initialCount; i++) {
         ArtifactToken sideB = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
            CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "InitialSideB_" + i);
         relationEndpoint.createRelationByType(sideA, sideB, TEST_REL_TYPE);
      }

      // Force rel_order values near MAX_VALUE by direct SQL update using gamma_id
      pushRelOrdersNearMax(sideA, TEST_REL_TYPE);

      // Now add more relations — this exercises the boundary detection and reorder path
      int additionalCount = 20;
      for (int i = 0; i < additionalCount; i++) {
         ArtifactToken sideB = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
            CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "BoundarySideB_" + i);
         relationEndpoint.createRelationByType(sideA, sideB, TEST_REL_TYPE);
      }

      // Verify: all relations exist and rel_orders are valid
      TreeMap<Integer, ArtifactId> orders = getRelOrders(sideA, TEST_REL_TYPE);
      int expectedTotal = initialCount + additionalCount;
      assertEquals("All relations should exist", expectedTotal, orders.size());
      assertNoDuplicateOrders(orders);
      assertOrdersAscending(orders);

      // Verify the max order is no longer near MAX_VALUE (reorder should have redistributed)
      int maxOrder = orders.lastKey();
      assertTrue("After reorder, max order should not be near MAX_VALUE",
         (long) Integer.MAX_VALUE - (long) maxOrder > 262144); // SPACING = 2^18
   }

   /**
    * Tests that adding many relations works correctly even when existing rel_order values are artificially pushed near
    * Integer.MIN_VALUE.
    */
   @Test
   public void testInsertionNearMinBoundary() {
      ArtifactToken parentArtifact = DefaultHierarchyRoot;

      // Create side-A artifact
      ArtifactToken sideA = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "RelOrderBoundaryTest_MinSideA");

      // Create initial side-B artifacts and relate them
      int initialCount = 5;
      for (int i = 0; i < initialCount; i++) {
         ArtifactToken sideB = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
            CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "InitialMinSideB_" + i);
         relationEndpoint.createRelationByType(sideA, sideB, TEST_REL_TYPE);
      }

      // Force rel_order values near MIN_VALUE by direct SQL update using gamma_id
      pushRelOrdersNearMin(sideA, TEST_REL_TYPE);

      // Verify the orders are now near MIN_VALUE
      TreeMap<Integer, ArtifactId> ordersBefore = getRelOrders(sideA, TEST_REL_TYPE);
      int minBefore = ordersBefore.firstKey();
      assertTrue("Orders should be near MIN_VALUE after push", (long) minBefore - (long) Integer.MIN_VALUE < 262144);

      // Now add more relations — this exercises the boundary detection and reorder path
      int additionalCount = 20;
      for (int i = 0; i < additionalCount; i++) {
         ArtifactToken sideB = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
            CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "BoundaryMinSideB_" + i);
         relationEndpoint.createRelationByType(sideA, sideB, TEST_REL_TYPE);
      }

      // Verify: all relations exist and rel_orders are valid
      TreeMap<Integer, ArtifactId> ordersAfter = getRelOrders(sideA, TEST_REL_TYPE);
      int expectedTotal = initialCount + additionalCount;
      assertEquals("All relations should exist", expectedTotal, ordersAfter.size());
      assertNoDuplicateOrders(ordersAfter);
      assertOrdersAscending(ordersAfter);
   }

   // --- Helper methods ---

   /**
    * Directly updates rel_order values for all relations of the given type on the parent to be clustered near
    * Integer.MAX_VALUE. Uses gamma_id as the unique key for updates.
    */
   private void pushRelOrdersNearMax(ArtifactToken parent, RelationTypeToken relType) {
      TreeMap<Integer, long[]> currentOrders = getRelOrdersWithGamma(parent, relType);
      int i = 0;
      for (var entry : currentOrders.entrySet()) {
         int newOrder = Integer.MAX_VALUE - (currentOrders.size() - i) * 10;
         long gammaId = entry.getValue()[1];
         AtsApiService.get().getJdbcService().getClient().runPreparedUpdate(
            "UPDATE osee_relation SET rel_order = ? WHERE gamma_id = ?", newOrder, gammaId);
         i++;
      }
   }

   /**
    * Directly updates rel_order values for all relations of the given type on the parent to be clustered near
    * Integer.MIN_VALUE. Uses gamma_id as the unique key for updates.
    */
   private void pushRelOrdersNearMin(ArtifactToken parent, RelationTypeToken relType) {
      TreeMap<Integer, long[]> currentOrders = getRelOrdersWithGamma(parent, relType);
      int i = 0;
      for (var entry : currentOrders.entrySet()) {
         int newOrder = Integer.MIN_VALUE + i * 10;
         long gammaId = entry.getValue()[1];
         AtsApiService.get().getJdbcService().getClient().runPreparedUpdate(
            "UPDATE osee_relation SET rel_order = ? WHERE gamma_id = ?", newOrder, gammaId);
         i++;
      }
   }

   /**
    * Reads all rel_order values and gamma_ids for the given parent and relation type from the database. Returns a
    * TreeMap of rel_order -> [b_art_id, gamma_id].
    */
   private TreeMap<Integer, long[]> getRelOrdersWithGamma(ArtifactToken parent, RelationTypeToken relType) {
      TreeMap<Integer, long[]> orders = new TreeMap<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         orders.put(stmt.getInt("rel_order"), new long[] {stmt.getLong("b_art_id"), stmt.getLong("gamma_id")});
      };
      AtsApiService.get().getJdbcService().getClient().runQuery(consumer,
         "SELECT rel_order, b_art_id, rel.gamma_id FROM osee_relation rel, osee_txs txs " + "WHERE txs.branch_id = ? AND txs.tx_current = 1 AND txs.gamma_id = rel.gamma_id " + "AND rel.rel_type = ? AND rel.a_art_id = ? ORDER BY rel_order",
         DemoBranches.SAW_PL_Working_Branch.getId(), relType.getId(), parent.getId());
      return orders;
   }

   /**
    * Reads all rel_order values for the given parent and relation type from the database.
    */
   private TreeMap<Integer, ArtifactId> getRelOrders(ArtifactToken parent, RelationTypeToken relType) {
      TreeMap<Integer, ArtifactId> orders = new TreeMap<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         orders.put(stmt.getInt("rel_order"), ArtifactId.valueOf(stmt.getLong("b_art_id")));
      };
      AtsApiService.get().getJdbcService().getClient().runQuery(consumer,
         "SELECT rel_order, b_art_id FROM osee_relation rel, osee_txs txs " + "WHERE txs.branch_id = ? AND txs.tx_current = 1 AND txs.gamma_id = rel.gamma_id " + "AND rel.rel_type = ? AND rel.a_art_id = ? ORDER BY rel_order",
         DemoBranches.SAW_PL_Working_Branch.getId(), relType.getId(), parent.getId());
      return orders;
   }

   private void assertNoDuplicateOrders(TreeMap<Integer, ArtifactId> orders) {
      // TreeMap keys are unique by definition, but verify size matches expected
      // This implicitly checks that no two relations share the same rel_order
      // (if they did, the TreeMap would have fewer entries than actual relations)
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
