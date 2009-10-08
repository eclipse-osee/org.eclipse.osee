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
package org.eclipse.osee.framework.skynet.core.test.relation.sorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorterId;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UnorderedRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UserDefinedRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter.SortMode;
import org.eclipse.osee.framework.skynet.core.test.types.MockIArtifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class RelationSorterTest {
   private final static Random randomGenerator = new Random();

   private final String message;
   private final IRelationSorter sorter;
   private final IRelationSorterId expectedOrderId;
   private final List<IArtifact> expectedOrder;
   private final List<String> currentItems;
   private final List<IArtifact> itemsToOrder;

   public RelationSorterTest(String message, IRelationSorter sorter, IRelationSorterId expectedOrderId, List<String> currentItems, List<IArtifact> itemsToOrder, List<IArtifact> expectedOrder) {
      this.sorter = sorter;
      this.message = message;
      this.expectedOrderId = expectedOrderId;
      this.currentItems = currentItems;
      this.itemsToOrder = itemsToOrder;
      this.expectedOrder = expectedOrder;
   }

   @Test
   public void testSorterId() {
      Assert.assertNotNull(message, sorter.getSorterId());
      Assert.assertEquals(message, expectedOrderId.getGuid(), sorter.getSorterId().getGuid());
      Assert.assertEquals(message, expectedOrderId.prettyName(), sorter.getSorterId().prettyName());
   }

   @Test
   public void testSort() {
      List<IArtifact> actualToOrder = new ArrayList<IArtifact>();
      actualToOrder.addAll(itemsToOrder);
      sorter.sort(actualToOrder, currentItems);

      Assert.assertEquals(message, expectedOrder.size(), actualToOrder.size());
      for (int index = 0; index < expectedOrder.size(); index++) {
         Assert.assertEquals(message + " - index:" + index, expectedOrder.get(index).getName(),
               actualToOrder.get(index).getName());
      }
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      Collection<Object[]> data = new ArrayList<Object[]>();
      data.add(createUnorderedSortTest("4", "2", "1", "5"));
      data.add(createUnorderedSortTest("$", "a", "!", "2"));
      data.add(createLexicographicalTest(SortMode.ASCENDING, "1", "2", "3", "4"));
      data.add(createLexicographicalTest(SortMode.ASCENDING, "a", "b", "c", "d"));
      data.add(createLexicographicalTest(SortMode.ASCENDING, "!", "1", "a", "b"));

      data.add(createLexicographicalTest(SortMode.DESCENDING, "4", "3", "2", "1"));
      data.add(createLexicographicalTest(SortMode.DESCENDING, "d", "c", "b", "a"));
      data.add(createLexicographicalTest(SortMode.DESCENDING, "b", "a", "1", "!"));

      data.add(getTestUserDefined("1", "2", "3", "4"));
      return data;
   }

   private static Object[] createUnorderedSortTest(String... names) {
      IArtifact art1 = createArtifact(names[0], GUID.create());
      IArtifact art2 = createArtifact(names[1], GUID.create());
      IArtifact art3 = createArtifact(names[2], GUID.create());
      IArtifact art4 = createArtifact(names[3], GUID.create());

      List<IArtifact> artifacts = Arrays.asList(art1, art2, art3, art4);
      return new Object[] {"Unordered Test", new UnorderedRelationSorter(), RelationOrderBaseTypes.UNORDERED, null,
            artifacts, artifacts};
   }

   private static Object[] createLexicographicalTest(SortMode mode, String... names) {
      IArtifact art1 = createArtifact(names[0], GUID.create());
      IArtifact art2 = createArtifact(names[1], GUID.create());
      IArtifact art3 = createArtifact(names[2], GUID.create());
      IArtifact art4 = createArtifact(names[3], GUID.create());

      IRelationSorterId orderId =
            mode == SortMode.ASCENDING ? RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC : RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC;

      List<IArtifact> itemsToOrder = Arrays.asList(art3, art1, art4, art2);
      List<IArtifact> expectedOrder = Arrays.asList(art1, art2, art3, art4);
      return new Object[] {"Lex Test " + mode.name(), new LexicographicalRelationSorter(mode), orderId, null,
            itemsToOrder, expectedOrder};
   }

   private static Object[] getTestUserDefined(String... names) {
      IArtifact art1 = createArtifact(names[0], GUID.create());
      IArtifact art2 = createArtifact(names[1], GUID.create());
      IArtifact art3 = createArtifact(names[2], GUID.create());
      IArtifact art4 = createArtifact(names[3], GUID.create());

      List<IArtifact> itemsToOrder = Arrays.asList(art2, art1, art3, art4);
      List<IArtifact> expectedOrder = Arrays.asList(art1, art2, art3, art4);
      List<String> relatives = Artifacts.toGuids(Arrays.asList(art1, art2, art3, art4));
      return new Object[] {"UserDefined", new UserDefinedRelationSorter(), RelationOrderBaseTypes.USER_DEFINED,
            relatives, itemsToOrder, expectedOrder};
   }

   private static IArtifact createArtifact(String name, String guid) {
      int uniqueId = randomGenerator.nextInt();
      return new MockIArtifact(uniqueId, name, guid, null, null);
   }
}
