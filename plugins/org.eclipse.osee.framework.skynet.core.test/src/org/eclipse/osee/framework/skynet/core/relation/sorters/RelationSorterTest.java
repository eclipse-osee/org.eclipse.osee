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
package org.eclipse.osee.framework.skynet.core.relation.sorters;

import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter.SortMode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class RelationSorterTest {

   private final String message;
   private final IRelationSorter sorter;
   private final RelationSorter expectedOrderId;
   private final List<ArtifactToken> expectedOrder;
   private final List<String> currentItems;
   private final List<ArtifactToken> itemsToOrder;

   public RelationSorterTest(String message, IRelationSorter sorter, RelationSorter expectedOrderId, List<String> currentItems, List<ArtifactToken> itemsToOrder, List<ArtifactToken> expectedOrder) {
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
      Assert.assertEquals(message, expectedOrderId, sorter.getSorterId());
   }

   @Test
   public void testSort() {
      List<ArtifactToken> actualToOrder = new ArrayList<>(itemsToOrder);
      sorter.sort(actualToOrder, currentItems);

      Assert.assertEquals(message, expectedOrder.size(), actualToOrder.size());
      for (int index = 0; index < expectedOrder.size(); index++) {
         Assert.assertEquals(message + " - index:" + index, expectedOrder.get(index).getId(),
            actualToOrder.get(index).getId());
      }
   }

   @Parameters
   public static Collection<Object[]> data() {
      Collection<Object[]> data = new ArrayList<>();
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
      ArtifactToken art1 = ArtifactToken.valueOf(1, names[0]);
      ArtifactToken art2 = ArtifactToken.valueOf(2, names[1]);
      ArtifactToken art3 = ArtifactToken.valueOf(3, names[2]);
      ArtifactToken art4 = ArtifactToken.valueOf(4, names[3]);

      List<ArtifactToken> artifacts = Arrays.asList(art1, art2, art3, art4);
      return new Object[] {"Unordered Test", new UnorderedRelationSorter(), UNORDERED, null, artifacts, artifacts};
   }

   private static Object[] createLexicographicalTest(SortMode mode, String... names) {
      ArtifactToken art1 = ArtifactToken.valueOf(1, names[0]);
      ArtifactToken art2 = ArtifactToken.valueOf(2, names[1]);
      ArtifactToken art3 = ArtifactToken.valueOf(3, names[2]);
      ArtifactToken art4 = ArtifactToken.valueOf(4, names[3]);

      RelationSorter orderId = mode == SortMode.ASCENDING ? LEXICOGRAPHICAL_ASC : LEXICOGRAPHICAL_DESC;

      List<ArtifactToken> itemsToOrder = Arrays.asList(art3, art1, art4, art2);
      List<ArtifactToken> expectedOrder = Arrays.asList(art1, art2, art3, art4);
      return new Object[] {
         "Lex Test " + mode.name(),
         new LexicographicalRelationSorter(mode),
         orderId,
         null,
         itemsToOrder,
         expectedOrder};
   }

   private static Object[] getTestUserDefined(String... names) {
      ArtifactToken art1 = ArtifactToken.valueOf(1, names[0]);
      ArtifactToken art2 = ArtifactToken.valueOf(2, names[1]);
      ArtifactToken art3 = ArtifactToken.valueOf(3, names[2]);
      ArtifactToken art4 = ArtifactToken.valueOf(4, names[3]);

      List<ArtifactToken> itemsToOrder = Arrays.asList(art2, art1, art3, art4);
      List<ArtifactToken> expectedOrder = Arrays.asList(art1, art2, art3, art4);
      List<String> relatives = Arrays.asList(art1.getGuid(), art2.getGuid(), art3.getGuid(), art4.getGuid());
      return new Object[] {
         "UserDefined",
         new UserDefinedRelationSorter(),
         USER_DEFINED,
         relatives,
         itemsToOrder,
         expectedOrder};
   }
}