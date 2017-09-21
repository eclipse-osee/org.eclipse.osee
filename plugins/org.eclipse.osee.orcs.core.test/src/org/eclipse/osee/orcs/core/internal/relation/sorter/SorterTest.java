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
package org.eclipse.osee.orcs.core.internal.relation.sorter;

import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link LexicographicalSorter}, {@link UnorderedSorter}, {@link UserDefinedSorter}, and
 * {@link UserDefinedComparator}
 *
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class SorterTest {

   private final String message;
   private final Sorter sorter;
   private final RelationSorter expectedOrderId;
   private final List<Identifiable<String>> expectedOrder;
   private final List<String> currentItems;
   private final List<Identifiable<String>> itemsToOrder;

   public SorterTest(String message, Sorter sorter, RelationSorter expectedOrderId, List<String> currentItems, List<Identifiable<String>> itemsToOrder, List<Identifiable<String>> expectedOrder) {
      this.sorter = sorter;
      this.message = message;
      this.expectedOrderId = expectedOrderId;
      this.currentItems = currentItems;
      this.itemsToOrder = itemsToOrder;
      this.expectedOrder = expectedOrder;
   }

   @Test
   public void testSorterId() {
      Assert.assertNotNull(message, sorter.getId());
      Assert.assertEquals(message, expectedOrderId, sorter.getId());
   }

   @Test
   public void testSort() {
      List<Identifiable<String>> actualToOrder = new ArrayList<>();
      actualToOrder.addAll(itemsToOrder);
      sorter.sort(actualToOrder, currentItems);

      Assert.assertEquals(message, expectedOrder.size(), actualToOrder.size());
      for (int index = 0; index < expectedOrder.size(); index++) {
         Assert.assertEquals(message + " - index:" + index, expectedOrder.get(index), actualToOrder.get(index));
      }
   }

   @Parameters
   public static Collection<Object[]> data() {
      Collection<Object[]> data = new ArrayList<>();
      data.add(createUnorderedSortTest("4", "2", "1", "5"));
      data.add(createUnorderedSortTest("$", "a", "!", "2"));
      data.add(createUserDefinedTest("1", "2", "3", "4"));

      data.add(createLexicographicalTest(SortOrder.ASCENDING, "1", "2", "3", "4"));
      data.add(createLexicographicalTest(SortOrder.ASCENDING, "a", "b", "c", "d"));
      data.add(createLexicographicalTest(SortOrder.ASCENDING, "!", "1", "a", "b"));

      data.add(createLexicographicalTest(SortOrder.DESCENDING, "4", "3", "2", "1"));
      data.add(createLexicographicalTest(SortOrder.DESCENDING, "d", "c", "b", "a"));
      data.add(createLexicographicalTest(SortOrder.DESCENDING, "b", "a", "1", "!"));

      return data;
   }

   private static Object[] createUnorderedSortTest(String... names) {
      Identifiable<String> art1 = createItem(names[0]);
      Identifiable<String> art2 = createItem(names[1]);
      Identifiable<String> art3 = createItem(names[2]);
      Identifiable<String> art4 = createItem(names[3]);

      List<Identifiable<String>> artifacts = Arrays.asList(art1, art2, art3, art4);
      return new Object[] {"Unordered Test", new UnorderedSorter(), UNORDERED, null, artifacts, artifacts};
   }

   private static Object[] createLexicographicalTest(SortOrder mode, String... names) {
      Identifiable<String> art1 = createItem(names[0]);
      Identifiable<String> art2 = createItem(names[1]);
      Identifiable<String> art3 = createItem(names[2]);
      Identifiable<String> art4 = createItem(names[3]);

      RelationSorter orderId;
      if (mode.isAscending()) {
         orderId = LEXICOGRAPHICAL_ASC;
      } else {
         orderId = LEXICOGRAPHICAL_DESC;
      }

      List<Identifiable<String>> itemsToOrder = Arrays.asList(art3, art1, art4, art2);
      List<Identifiable<String>> expectedOrder = Arrays.asList(art1, art2, art3, art4);
      return new Object[] {
         "Lex Test " + mode.name(),
         new LexicographicalSorter(mode),
         orderId,
         null,
         itemsToOrder,
         expectedOrder};
   }

   private static Object[] createUserDefinedTest(String... names) {
      Identifiable<String> art1 = createItem(names[0]);
      Identifiable<String> art2 = createItem(names[1]);
      Identifiable<String> art3 = createItem(names[2]);
      Identifiable<String> art4 = createItem(names[3]);

      List<Identifiable<String>> itemsToOrder = Arrays.asList(art2, art1, art3, art4);
      List<Identifiable<String>> expectedOrder = Arrays.asList(art1, art2, art3, art4);

      List<String> relatives = new ArrayList<>();
      for (Identifiable<String> item : Arrays.asList(art1, art2, art3, art4)) {
         relatives.add(item.getGuid());
      }
      return new Object[] {
         "UserDefined",
         new UserDefinedSorter(),
         USER_DEFINED,
         relatives,
         itemsToOrder,
         expectedOrder};
   }

   private static Identifiable<String> createItem(String name) {
      return new NamedIdentity<String>(GUID.create(), name);
   }
}