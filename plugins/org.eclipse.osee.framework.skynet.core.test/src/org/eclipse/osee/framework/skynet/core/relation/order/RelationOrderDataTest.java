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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationSorter;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.mocks.DataFactory;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderDataTest {
   private static final Random randomGenerator = new Random();

   private IArtifact artifact;
   private MockRelationOrderAccessor accessor;
   private RelationOrderData data;
   private RelationType relationType1;
   private RelationType relationType2;
   private RelationType relationType3;

   @Before
   public void setUp() throws OseeCoreException {
      artifact = createArtifact("art1", GUID.create());
      accessor = new MockRelationOrderAccessor();
      data = new RelationOrderData(accessor, artifact);

      RelationTypeCache cache = new RelationTypeCache();

      relationType1 = createRelationType(cache, "Rel 1", RelationOrderBaseTypes.USER_DEFINED);
      relationType2 = createRelationType(cache, "Rel 2", RelationOrderBaseTypes.UNORDERED);
      relationType3 = createRelationType(cache, "Rel 3", RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC);

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
      Assert.assertEquals(artifact, data.getIArtifact());
   }

   @Test
   public void testLoad() throws OseeCoreException {
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
   public void testGetCurrentSorterGuid() throws OseeCoreException {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);
      try {
         data.getCurrentSorterGuid(relationType1, null);
         Assert.assertNull("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }

      try {
         data.getCurrentSorterGuid(null, RelationSide.SIDE_A);
         Assert.assertNull("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }

      RelationSorter actualGuid = data.getCurrentSorterGuid(relationType1, RelationSide.SIDE_A);
      Assert.assertEquals(RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC, actualGuid);

      actualGuid = data.getCurrentSorterGuid(relationType2, RelationSide.SIDE_B);
      Assert.assertEquals(RelationOrderBaseTypes.UNORDERED, actualGuid);

      // Pair does not exist
      actualGuid = data.getCurrentSorterGuid(relationType2, RelationSide.SIDE_A);
      Assert.assertEquals(relationType2.getDefaultOrderTypeGuid(), actualGuid);

      // Pair does not exist
      actualGuid = data.getCurrentSorterGuid(relationType3, RelationSide.SIDE_B);
      Assert.assertEquals(relationType3.getDefaultOrderTypeGuid(), actualGuid);
   }

   @Test
   public void testGetOrderList() throws OseeCoreException {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);

      try {
         data.getOrderList(relationType1, null);
         Assert.assertNull("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }

      try {
         data.getOrderList(null, RelationSide.SIDE_A);
         Assert.assertNull("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }

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
   public void testRemoveFromList() throws OseeCoreException {
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
   public void testStore() throws OseeCoreException {
      List<Object[]> expected = new ArrayList<>();
      addData(expected);

      accessor.clearStoreCalled();

      // No Change -- Current Sorter Id
      List<IArtifact> emptyList = java.util.Collections.emptyList();
      data.store(relationType1, RelationSide.SIDE_A, RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC, emptyList);
      Assert.assertFalse(accessor.wasStoreCalled());

      // Store
      accessor.clearStoreCalled();
      data.store(relationType1, RelationSide.SIDE_A, relationType1.getDefaultOrderTypeGuid(), emptyList);
      Assert.assertTrue(accessor.wasStoreCalled());

      // Store
      accessor.clearStoreCalled();
      data.store(relationType1, RelationSide.SIDE_A, RelationOrderBaseTypes.UNORDERED, emptyList);
      Assert.assertTrue(accessor.wasStoreCalled());
   }

   @Test
   public void testToString() {
      Assert.assertEquals("Relation Order Data for artifact:" + artifact.toString(), data.toString());
   }

   private void addData(List<Object[]> expected) {
      addData(data, expected, relationType1, RelationSide.SIDE_A, RelationSorter.LEXICOGRAPHICAL_ASC, "1", "2", "3");
      addData(data, expected, relationType2, RelationSide.SIDE_B, RelationSorter.UNORDERED, "4", "5", "6");
      checkData(data, expected);
   }

   private void checkData(RelationOrderData orderData, List<Object[]> expectedValues) {
      int index = 0;
      Assert.assertEquals(expectedValues.size(), orderData.size());
      for (Entry<Pair<String, String>, Pair<RelationSorter, List<String>>> entry : orderData.getOrderedEntrySet()) {
         Object[] actual = new Object[] {
            entry.getKey().getFirst(),
            entry.getKey().getSecond(),
            entry.getValue().getFirst(),
            entry.getValue().getSecond()};
         Object[] expected = expectedValues.get(index++);
         Assert.assertEquals(expected.length, actual.length);
         for (int index2 = 0; index2 < expected.length; index2++) {
            Assert.assertEquals(expected[index2], actual[index2]);
         }
      }
   }

   private void addData(RelationOrderData orderData, List<Object[]> expectedData, RelationType relationType, RelationSide side, RelationSorter relationOrderIdGuid, String... guids) {
      List<String> artGuids = Arrays.asList(guids);
      orderData.addOrderList(relationType.getName(), side.name(), relationOrderIdGuid, artGuids);
      expectedData.add(new Object[] {relationType.getName(), side.name(), relationOrderIdGuid, artGuids});
   }

   private static IArtifact createArtifact(String name, String guid) {
      int uniqueId = randomGenerator.nextInt();
      Branch branch =
         new Branch(Lib.generateUuid(), name + " - branch", BranchType.WORKING, BranchState.MODIFIED, false, false);
      return DataFactory.createArtifact(uniqueId, name, guid, branch);
   }

   private static RelationType createRelationType(RelationTypeCache cache, String name, RelationSorter delationRelationOrderGuid) throws OseeCoreException {
      IArtifactType type1 = new ArtifactType(0x01L, "1", false);
      IArtifactType type2 = new ArtifactType(0x02L, "2", false);
      RelationType relationType = new RelationType(0x03L, name, name + "_A", name + "_B", type1, type2,
         RelationTypeMultiplicity.MANY_TO_MANY, delationRelationOrderGuid);
      Assert.assertNotNull(relationType);
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
      public void load(IArtifact artifact, RelationOrderData orderData) {
         wasLoadCalled = true;
      }

      @Override
      public void store(IArtifact artifact, RelationOrderData orderData, DefaultBasicUuidRelationReorder reorderRecord) {
         wasStoreCalled = true;
      }

   }
}
