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

package org.eclipse.osee.framework.skynet.core.relation.order;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_MANY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderDataTest {

   private Artifact artifact;
   private MockRelationOrderAccessor accessor;
   private RelationOrderData data;
   private RelationTypeToken relationType1;
   private RelationTypeToken relationType2;
   private RelationTypeToken relationType3;

   @Before
   public void setUp() {
      artifact = new Artifact(CoreBranches.SYSTEM_ROOT);
      accessor = new MockRelationOrderAccessor();
      data = new RelationOrderData(accessor, artifact);

      relationType1 = RelationTypeToken.create(9456378923465L, "A Relation Type", MANY_TO_MANY, USER_DEFINED, Artifact,
         "side A", Artifact, "side B");
      relationType2 = CoreRelationTypes.ComponentRequirement;
      relationType3 = CoreRelationTypes.DefaultHierarchical;

      Assert.assertFalse(data.hasEntries());
      Assert.assertEquals(0, data.size());
   }

   @Test
   public void testGetIArtifact() {
      Assert.assertEquals(artifact, data.getArtifact());
   }

   @Test
   public void testLoad() {
      accessor.clearLoadCalled();

      data.load();
      Assert.assertTrue(accessor.wasLoadCalled());

      List<Object[]> expected = new ArrayList<>();
      addData(data, expected, relationType1, RelationSide.SIDE_A, RelationSorter.LEXICOGRAPHICAL_ASC, "1", "2", "3");
      checkData(data, expected);

      accessor.clearLoadCalled();

      Assert.assertEquals(1, data.size());
      data.load();
      Assert.assertTrue(accessor.wasLoadCalled());
      Assert.assertFalse(data.hasEntries());
      Assert.assertEquals(0, data.size());
   }

   @Test
   public void testAddOrderList() {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);
      data.clear();
      Assert.assertFalse(data.hasEntries());
      Assert.assertEquals(0, data.size());

   }

   @Test
   public void testGetCurrentSorterGuid() {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);

      RelationSorter actualGuid = data.getCurrentSorterGuid(relationType1, RelationSide.SIDE_A);
      Assert.assertEquals(LEXICOGRAPHICAL_ASC, actualGuid);

      actualGuid = data.getCurrentSorterGuid(relationType2, RelationSide.SIDE_B);
      Assert.assertEquals(UNORDERED, actualGuid);

      // Pair does not exist
      actualGuid = data.getCurrentSorterGuid(relationType2, RelationSide.SIDE_A);
      Assert.assertEquals(relationType2.getOrder(), actualGuid);

      // Pair does not exist
      actualGuid = data.getCurrentSorterGuid(relationType3, RelationSide.SIDE_B);
      Assert.assertEquals(relationType3.getOrder(), actualGuid);
   }

   @Test
   public void testGetOrderList() {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);

      List<String> actualGuids = data.getOrderList(relationType1, RelationSide.SIDE_A);
      Assert.assertTrue(Collections.isEqual(Arrays.asList("1", "2", "3"), actualGuids));

      actualGuids = data.getOrderList(relationType2, RelationSide.SIDE_B);
      Assert.assertTrue(Collections.isEqual(Arrays.asList("4", "5", "6"), actualGuids));

      // Non-existent combination
      actualGuids = data.getOrderList(relationType1, RelationSide.SIDE_B);
      Assert.assertTrue(actualGuids.isEmpty());

      actualGuids = data.getOrderList(relationType2, RelationSide.SIDE_A);
      Assert.assertTrue(actualGuids.isEmpty());

      actualGuids = data.getOrderList(relationType3, RelationSide.SIDE_A);
      Assert.assertTrue(actualGuids.isEmpty());
   }

   @Test
   public void testRemoveFromList() {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);

      // Remove item that does not exist
      data.removeOrderList(relationType2, RelationSide.SIDE_A);
      checkData(data, expected);

      data.removeOrderList(relationType3, RelationSide.SIDE_A);
      checkData(data, expected);

      // Actual Removal
      data.removeOrderList(relationType1, RelationSide.SIDE_A);
      expected.remove(0);
      checkData(data, expected);

      data.removeOrderList(relationType2, RelationSide.SIDE_B);
      expected.remove(0);
      checkData(data, expected);
   }

   @Test
   public void testStore() {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);

      accessor.clearStoreCalled();

      // No Change -- Current Sorter Id
      List<Artifact> emptyList = java.util.Collections.emptyList();
      data.store(relationType1, RelationSide.SIDE_A, LEXICOGRAPHICAL_ASC, emptyList);
      Assert.assertFalse(accessor.wasStoreCalled());

      // Store
      accessor.clearStoreCalled();
      data.store(relationType1, RelationSide.SIDE_A, relationType1.getOrder(), emptyList);
      Assert.assertTrue(accessor.wasStoreCalled());

      // Store
      accessor.clearStoreCalled();
      data.store(relationType1, RelationSide.SIDE_A, UNORDERED, emptyList);
      Assert.assertTrue(accessor.wasStoreCalled());
   }

   @Test
   public void testToString() {
      Assert.assertEquals("Relation Order Data for artifact:" + artifact.toString(), data.toString());
   }

   private void addData(List<Object[]> expected) {
      addData(data, expected, relationType1, SIDE_A, LEXICOGRAPHICAL_ASC, "1", "2", "3");
      addData(data, expected, relationType2, SIDE_B, UNORDERED, "4", "5", "6");
      checkData(data, expected);
   }

   private void checkData(RelationOrderData orderData, List<Object[]> expectedValues) {
      int index = 0;
      Assert.assertEquals(expectedValues.size(), orderData.size());
      for (Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>> entry : orderData.getOrderedEntrySet()) {
         Object[] actual = new Object[] {
            entry.getKey().getFirst(),
            entry.getKey().getSecond(),
            entry.getValue().getFirst(),
            entry.getValue().getSecond()};
         Object[] expected = expectedValues.get(index++);
         Assert.assertEquals(expected.length, actual.length);
         for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual[i]);
         }
      }
   }

   private void addData(RelationOrderData orderData, List<Object[]> expectedData, RelationTypeToken relationType, RelationSide side, RelationSorter sorterId, String... guids) {
      List<String> artGuids = Arrays.asList(guids);
      orderData.addOrderList(relationType, side, sorterId, artGuids);
      expectedData.add(new Object[] {relationType, side, sorterId, artGuids});
   }

   private final class MockRelationOrderAccessor implements IRelationOrderAccessor {

      private boolean wasLoadCalled;
      private boolean wasStoreCalled;

      public MockRelationOrderAccessor() {
         super();
         clearLoadCalled();
         clearStoreCalled();
      }

      public boolean wasLoadCalled() {
         return wasLoadCalled;
      }

      public void clearLoadCalled() {
         wasLoadCalled = false;
         Assert.assertFalse(wasLoadCalled());
      }

      public boolean wasStoreCalled() {
         return wasStoreCalled;
      }

      public void clearStoreCalled() {
         wasStoreCalled = false;
         Assert.assertFalse(wasStoreCalled());
      }

      @Override
      public void load(Artifact artifact, RelationOrderData orderData) {
         wasLoadCalled = true;
      }

      @Override
      public void store(Artifact artifact, RelationOrderData orderData, DefaultBasicUuidRelationReorder reorderRecord) {
         wasStoreCalled = true;
      }
   }
}