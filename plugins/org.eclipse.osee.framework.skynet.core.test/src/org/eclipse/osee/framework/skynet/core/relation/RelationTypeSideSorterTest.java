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

package org.eclipse.osee.framework.skynet.core.relation;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_MANY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationOrderAccessor;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Roberto E. Escobar
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class RelationTypeSideSorterTest {
   private final RelationType relationType;
   private final RelationSide relationSide;
   private final RelationOrderData orderData;
   private final RelationSorterProvider sorterProvider;
   private final RelationTypeSideSorter sorter;

   public RelationTypeSideSorterTest(RelationSorterProvider sorterProvider, RelationType relationType, RelationSide relationSide, RelationOrderData orderData, List<Object[]> expected) {
      this.relationType = relationType;
      this.relationSide = relationSide;
      this.orderData = orderData;
      this.sorterProvider = sorterProvider;
      this.sorter = new RelationTypeSideSorter(relationType, relationSide, sorterProvider, orderData);
   }

   @Test
   public void test01Construction() {
      Assert.assertNotNull(sorter);
   }

   @Test
   public void test02GetIArtifact()  {
      Assert.assertNotNull(orderData.getArtifact());
      Assert.assertEquals(orderData.getArtifact(), sorter.getArtifact());
   }

   @Test
   public void test03GetRelationType() {
      Assert.assertEquals(relationType, sorter.getRelationType());
   }

   @Test
   public void test04GetRelationSide() {
      Assert.assertEquals(relationSide, sorter.getSide());
   }

   @Test
   public void test05GetSorterId()  {
      RelationSorter sorterId = orderData.getCurrentSorterGuid(relationType, relationSide);
      RelationSorter expected = sorterProvider.getRelationOrder(sorterId).getSorterId();
      Assert.assertNotNull(sorterId);
      Assert.assertEquals(expected, sorter.getSorterId());
      Assert.assertEquals(expected, sorter.getSorterId());
      Assert.assertEquals(expected.toString(), sorter.getSorterId().toString());
   }

   @Test
   public void test07SetOrder()  {
      Artifact art3 = new MockArtifact("c");
      Artifact art4 = new MockArtifact("d");

      List<Artifact> relatives = Arrays.asList(art3, art4);
      List<String> expected = Artifacts.toGuids(relatives);

      // set same sorter id
      sorter.setOrder(relatives, sorter.getSorterId());
      List<String> actual = orderData.getOrderList(sorter.getRelationType(), sorter.getSide());
      Assert.assertFalse(actual.equals(expected));

      // Set Different sorter id
      sorter.setOrder(relatives, USER_DEFINED);
      actual = orderData.getOrderList(sorter.getRelationType(), sorter.getSide());
      expected = Artifacts.toGuids(relatives);
      Assert.assertTrue(actual.equals(expected));
   }

   @Test
   public void test10AddItem()  {
      Artifact itemToAdd = new MockArtifact("Item to Add");

      List<Artifact> startingArtifacts = new ArrayList<>();
      List<String> startingOrder = orderData.getOrderList(sorter.getRelationType(), sorter.getSide());
      for (int index = 0; index < startingOrder.size(); index++) {
         String artifactGuid = startingOrder.get(index);
         startingArtifacts.add(new MockArtifact("Dummy" + index, artifactGuid));
      }

      // Set Related Artifact Data
      ((MockArtifact) sorter.getArtifact()).setRelations(startingArtifacts);

      for (IRelationSorter relationSorter : sorterProvider.getSorters()) {
         RelationSorter sorterId = relationSorter.getSorterId();
         List<Artifact> itemsToOrder = new ArrayList<>(startingArtifacts);
         itemsToOrder.add(itemToAdd);
         if (USER_DEFINED != sorterId) {
            relationSorter.sort(itemsToOrder, null);
         }

         // Call twice to ensure that the same items is not duplicated in the list
         sorter.addItem(sorterId, itemToAdd);
         sorter.addItem(sorterId, itemToAdd);

         List<String> currentOrder = orderData.getOrderList(sorter.getRelationType(), sorter.getSide());
         if (USER_DEFINED.equals(sorterId)) {
            List<String> expectedOrder = Artifacts.toGuids(itemsToOrder);
            Assert.assertEquals(expectedOrder, currentOrder);
         } else {
            Assert.assertTrue(currentOrder.isEmpty());
         }
      }
   }

   @Test
   public void test11ToString()  {
      RelationSorter sorterGuid = orderData.getCurrentSorterGuid(relationType, relationSide);
      RelationSorter expectedId = sorterProvider.getRelationOrder(sorterGuid).getSorterId();
      String expectedToString =
         String.format("Relation Sorter {relationType=%s, relationSide=[%s], artifact=%s, sorterId=%s}", relationType,
            relationSide, sorter.getArtifact(), expectedId);
      Assert.assertEquals(expectedToString, sorter.toString());
   }

   @Parameters
   public static Collection<Object[]> data()  {
      RelationSorterProvider provider = new RelationSorterProvider();
      IRelationOrderAccessor accessor = new DoNothingAccessor();

      RelationType relationType1 = createRelationType("Rel 1", LEXICOGRAPHICAL_ASC);
      RelationType relationType2 = createRelationType("Rel 2", LEXICOGRAPHICAL_DESC);

      RelationOrderData relOrderdata1 = new RelationOrderData(accessor, new MockArtifact("a"));
      RelationOrderData relOrderdata2 = new RelationOrderData(accessor, new MockArtifact("b"));

      List<Object[]> expected1 = new ArrayList<>();
      List<Object[]> expected2 = new ArrayList<>();

      addData(relationType1, relationType2, relOrderdata1, expected1);
      addData(relationType1, relationType2, relOrderdata2, expected2);

      Collection<Object[]> data = new ArrayList<>();
      data.add(new Object[] {provider, relationType1, RelationSide.SIDE_A, relOrderdata1, expected1});
      data.add(new Object[] {provider, relationType2, RelationSide.SIDE_B, relOrderdata2, expected2});
      return data;
   }

   private static class MockArtifact extends Artifact {
      private static final ArtifactType artifactType = new ArtifactType(Artifact.getGuid(), Artifact.getName(), false);
      private final String name;
      private List<Artifact> artifacts = new ArrayList<>();

      public MockArtifact(String name, String guid) {
         super(guid, COMMON, artifactType);
         this.name = name;
      }

      public MockArtifact(String name) {
         this(name, GUID.create());
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public List<Artifact> getRelatedArtifacts(RelationTypeSide relationEnum) {
         return artifacts;
      }

      public void setRelations(List<Artifact> artifacts) {
         this.artifacts = artifacts;
      }
   }

   private static RelationType createRelationType(String name, RelationSorter defaultRelationSorter)  {
      return new RelationType(0x03L, name, name + "_A", name + "_B", Artifact, Artifact, MANY_TO_MANY,
         defaultRelationSorter);
   }

   private static void addData(RelationType relationType1, RelationType relationType2, RelationOrderData data, List<Object[]> expected)  {
      addData(data, expected, relationType1, RelationSide.SIDE_A, //
         LEXICOGRAPHICAL_ASC, "1", "2", "3");
      addData(data, expected, relationType2, RelationSide.SIDE_B, //
         UNORDERED, "4", "5", "6");

      checkData(data, expected);
   }

   private static void checkData(RelationOrderData orderData, List<Object[]> expectedValues) {
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

   private static void addData(RelationOrderData orderData, List<Object[]> expectedData, RelationType relationType, RelationSide side, RelationSorter sorterId, String... guids) {
      List<String> artGuids = Arrays.asList(guids);
      orderData.addOrderList(relationType, side, sorterId, artGuids);
      expectedData.add(new Object[] {relationType, side, sorterId, artGuids});
   }

   private static final class DoNothingAccessor implements IRelationOrderAccessor {

      @Override
      public void load(Artifact artifact, RelationOrderData orderData) {
         // do nothing
      }

      @Override
      public void store(Artifact artifact, RelationOrderData orderData, DefaultBasicUuidRelationReorder reorderRecord) {
         // do nothing
      }
   }
}