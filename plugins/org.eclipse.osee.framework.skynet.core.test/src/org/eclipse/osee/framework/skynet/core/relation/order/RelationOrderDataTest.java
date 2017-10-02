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
package org.eclipse.osee.framework.skynet.core.relation.order;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.After;
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
   private RelationType relationType1;
   private RelationType relationType2;
   private RelationType relationType3;

   @Before
   public void setUp()  {
      artifact = new Artifact(CoreBranches.SYSTEM_ROOT);
      accessor = new MockRelationOrderAccessor();
      data = new RelationOrderData(accessor, artifact);

      RelationTypeCache cache = new RelationTypeCache();

      relationType1 = createRelationType(1, cache, "Rel 1", USER_DEFINED);
      relationType2 = createRelationType(2, cache, "Rel 2", UNORDERED);
      relationType3 = createRelationType(3, cache, "Rel 3", LEXICOGRAPHICAL_ASC);

      Assert.assertFalse(data.hasEntries());
      Assert.assertEquals(0, data.size());
   }

   @After
   public void tearDown() {
      artifact = null;
      accessor = null;
      data = null;
   }

   @Test
   public void testGetIArtifact() {
      Assert.assertEquals(artifact, data.getArtifact());
   }

   @Test
   public void testLoad()  {
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
   public void testGetCurrentSorterGuid()  {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);

      RelationSorter actualGuid = data.getCurrentSorterGuid(relationType1, RelationSide.SIDE_A);
      Assert.assertEquals(LEXICOGRAPHICAL_ASC, actualGuid);

      actualGuid = data.getCurrentSorterGuid(relationType2, RelationSide.SIDE_B);
      Assert.assertEquals(UNORDERED, actualGuid);

      // Pair does not exist
      actualGuid = data.getCurrentSorterGuid(relationType2, RelationSide.SIDE_A);
      Assert.assertEquals(relationType2.getDefaultOrderTypeGuid(), actualGuid);

      // Pair does not exist
      actualGuid = data.getCurrentSorterGuid(relationType3, RelationSide.SIDE_B);
      Assert.assertEquals(relationType3.getDefaultOrderTypeGuid(), actualGuid);
   }

   @Test
   public void testGetOrderList()  {
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
   public void testRemoveFromList()  {
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
   public void testStore()  {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);

      accessor.clearStoreCalled();

      // No Change -- Current Sorter Id
      List<Artifact> emptyList = java.util.Collections.emptyList();
      data.store(relationType1, RelationSide.SIDE_A, LEXICOGRAPHICAL_ASC, emptyList);
      Assert.assertFalse(accessor.wasStoreCalled());

      // Store
      accessor.clearStoreCalled();
      data.store(relationType1, RelationSide.SIDE_A, relationType1.getDefaultOrderTypeGuid(), emptyList);
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

   private void addData(RelationOrderData orderData, List<Object[]> expectedData, RelationType relationType, RelationSide side, RelationSorter sorterId, String... guids) {
      List<String> artGuids = Arrays.asList(guids);
      orderData.addOrderList(relationType, side, sorterId, artGuids);
      expectedData.add(new Object[] {relationType, side, sorterId, artGuids});
   }

   private static RelationType createRelationType(long id, RelationTypeCache cache, String name, RelationSorter delationRelationOrderGuid)  {
      RelationType relationType = new RelationType(id, name, name + "_A", name + "_B", Artifact, Artifact,
         RelationTypeMultiplicity.MANY_TO_MANY, delationRelationOrderGuid);
      cache.cache(relationType);
      return relationType;
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