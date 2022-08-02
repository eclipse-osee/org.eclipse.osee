/*
/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - Initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the Enumeration Functional Interface Map classes.
 *
 * @author Loren K. Ashley
 */

public class EnumFunctionalInterafaceMapsTest {

   private static enum TestEnum {
      A,
      B,
      C,
      D,
      E,
      F;
   }

   /*
    * AbstractEnumFunctionInterfaceMap Tests
    */

   @Test
   public void testContainsKey() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);
      map.put(TestEnum.B, TestEnum.B::name);

      Assert.assertTrue(map.containsKey(TestEnum.A));
      Assert.assertTrue(map.containsKey(TestEnum.B));
      Assert.assertFalse(map.containsKey(TestEnum.C));
      Assert.assertFalse(map.containsKey(TestEnum.D));
      Assert.assertFalse(map.containsKey(TestEnum.E));
      Assert.assertFalse(map.containsKey(TestEnum.F));
   }

   @Test(expected = NullPointerException.class)
   public void testContainsKeyNull() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.containsKey((TestEnum) null);
   }

   @Test
   public void testGetFunction() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);

      var functionalInterfaceOptional = map.getFunction(TestEnum.A);

      Assert.assertTrue(functionalInterfaceOptional.isPresent());

      var functionalInterface = functionalInterfaceOptional.get();

      Assert.assertEquals("A", functionalInterface.get());

      functionalInterfaceOptional = map.getFunction(TestEnum.B);

      Assert.assertFalse(functionalInterfaceOptional.isPresent());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testGetFunctionNull() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      var functionalInterfaceOptional = map.getFunction((TestEnum) null);
   }

   @Test
   public void testIsEmpty() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      Assert.assertTrue(map.isEmpty());

      map.put(TestEnum.A, TestEnum.A::name);

      Assert.assertFalse(map.isEmpty());
   }

   @Test
   public void testKeySet() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);
      map.put(TestEnum.B, TestEnum.B::name);

      var keySet = map.keySet();

      Assert.assertEquals(2, keySet.size());

      Assert.assertTrue(keySet.contains(TestEnum.A));
      Assert.assertTrue(keySet.contains(TestEnum.B));
      Assert.assertFalse(keySet.contains(TestEnum.C));
      Assert.assertFalse(keySet.contains(TestEnum.D));
      Assert.assertFalse(keySet.contains(TestEnum.E));
      Assert.assertFalse(keySet.contains(TestEnum.F));

      var keySetIterator = keySet.iterator();

      Assert.assertTrue(keySetIterator.hasNext());

      var firstKey = keySetIterator.next();

      Assert.assertTrue(TestEnum.A.equals(firstKey) || TestEnum.B.equals(firstKey));

      Assert.assertTrue(keySetIterator.hasNext());

      var secondKey = keySetIterator.next();

      Assert.assertTrue(TestEnum.A.equals(secondKey) || TestEnum.B.equals(secondKey));

      Assert.assertNotEquals(firstKey, secondKey);

      Assert.assertFalse(keySetIterator.hasNext());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetAdd() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      var keySet = map.keySet();

      keySet.add(TestEnum.A);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetAddAll() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      var keySet = map.keySet();

      var set = Set.of(TestEnum.A, TestEnum.B);

      keySet.addAll(set);
   }

   /*
    * Set must not be empty for the set's iterator to be called. Exception won't be thrown unless the set's iterator is
    * called.
    */

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetRemove() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);

      var keySet = map.keySet();

      keySet.remove(TestEnum.A);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetRemoveAll() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);

      var keySet = map.keySet();

      var set = Set.of(TestEnum.A, TestEnum.B);

      keySet.removeAll(set);
   }

   /*
    * RetainAll won't call the set's iterator remove function unless the set contains an item to be removed. The call
    * won't throw an exception unless it attempts a remove.
    */
   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetRetainAll() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);
      map.put(TestEnum.C, TestEnum.C::name);

      var keySet = map.keySet();

      var set = Set.of(TestEnum.A, TestEnum.B);

      keySet.retainAll(set);
   }

   @Test
   public void testOfEntriesLoader() {

      //@formatter:off
      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class)
      {
         @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      };

      map.ofEntriesLoaderX
         (
            Map.entry(TestEnum.A, TestEnum.A::name),
            Map.entry(TestEnum.B, TestEnum.B::name)
         );
      //@formatter:on

      Assert.assertTrue(map.containsKey(TestEnum.A));

      var functionOptional = map.getFunction(TestEnum.A);

      Assert.assertTrue(functionOptional.isPresent());

      var function = functionOptional.get();

      Assert.assertEquals("A", function.get());

      functionOptional = map.getFunction(TestEnum.B);

      Assert.assertTrue(functionOptional.isPresent());

      function = functionOptional.get();

      Assert.assertEquals("B", function.get());

      Assert.assertFalse(map.containsKey(TestEnum.C));
      Assert.assertFalse(map.containsKey(TestEnum.D));
      Assert.assertFalse(map.containsKey(TestEnum.E));
      Assert.assertFalse(map.containsKey(TestEnum.F));

   }

   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testOfEntriesLoaderDuplicateEntry() {

      //@formatter:off
      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class)
      {
         @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      };

      map.ofEntriesLoaderX
         (
            Map.entry(TestEnum.A, TestEnum.A::name),
            Map.entry(TestEnum.A, TestEnum.A::name)
         );
      //@formatter:on
   }

   @Test(expected = NullPointerException.class)
   public void testOfEntriesLoaderNullArray() {

      //@formatter:off
      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class)
      {
         @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      };

      map.ofEntriesLoaderX
         (
            (Map.Entry<TestEnum,Supplier<String>>[]) null
         );
      //@formatter:on
   }

   @Test(expected = NullPointerException.class)
   public void testOfEntriesLoaderNullEntry() {

      //@formatter:off
      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class)
      {
         @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      };

      map.ofEntriesLoaderX
         (
            (Map.Entry<TestEnum,Supplier<String>>) null
         );
      //@formatter:on
   }

   @Test(expected = NullPointerException.class)
   public void testOfEntriesLoaderOneIsNull() {

      //@formatter:off
      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class)
      {
         @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      };

      map.ofEntriesLoaderX
         (
            Map.entry(TestEnum.A, TestEnum.A::name),
            (Map.Entry<TestEnum,Supplier<String>>) null,
            Map.entry(TestEnum.B, TestEnum.B::name)
         );
      //@formatter:on
   }

   @Test
   public void testPut() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);

      Assert.assertTrue(map.containsKey(TestEnum.A));

      var functionOptional = map.getFunction(TestEnum.A);

      Assert.assertTrue(functionOptional.isPresent());

      var function = functionOptional.get();

      Assert.assertEquals("A", function.get());

      Assert.assertFalse(map.containsKey(TestEnum.B));
      Assert.assertFalse(map.containsKey(TestEnum.C));
      Assert.assertFalse(map.containsKey(TestEnum.D));
      Assert.assertFalse(map.containsKey(TestEnum.E));
      Assert.assertFalse(map.containsKey(TestEnum.F));
   }

   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testPutDuplicate() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);
      map.put(TestEnum.A, TestEnum.A::name);
   }

   @Test
   public void testSize() {

      var map = new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      Assert.assertEquals(0, map.size());

      map.put(TestEnum.A, TestEnum.A::name);

      Assert.assertEquals(1, map.size());

      map.put(TestEnum.B, TestEnum.B::name);

      Assert.assertEquals(2, map.size());

      map.put(TestEnum.C, TestEnum.C::name);

      Assert.assertEquals(3, map.size());

      map.put(TestEnum.D, TestEnum.D::name);

      Assert.assertEquals(4, map.size());

      map.put(TestEnum.E, TestEnum.E::name);

      Assert.assertEquals(5, map.size());

      map.put(TestEnum.F, TestEnum.F::name);

      Assert.assertEquals(6, map.size());
   }

   /*
    * EnumBiConsumerMap Tests
    */

   @Test
   public void testEnumBiConsumerMapAccept() {
      var map = new EnumBiConsumerMap<TestEnum, String, String>(TestEnum.class);
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      map.put(TestEnum.A, (a, b) -> {
         listA.add(a);
         listB.add(b);
      });

      map.accept(TestEnum.A, "A", "B");

      Assert.assertEquals(1, listA.size());
      Assert.assertEquals("A", listA.get(0));

      Assert.assertEquals(1, listB.size());
      Assert.assertEquals("B", listB.get(0));
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumBiConsumerMapAcceptNoSuchElement() {

      var map = new EnumBiConsumerMap<TestEnum, String, String>(TestEnum.class);

      map.accept(TestEnum.A, "A", "B");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumBiConsumerMapAcceptNullKey() {

      var map = new EnumBiConsumerMap<TestEnum, String, String>(TestEnum.class);

      map.accept((TestEnum) null, "A", "B");
   }

   @Test
   public void testEnumBiConsumerMapOfEntries() {
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      //@formatter:off
      var map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); }),
         Map.entry(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); })
      );
      //@formatter:on

      map.accept(TestEnum.A, "A", "A1");
      map.accept(TestEnum.B, "B", "B1");

      Assert.assertEquals(2, listA.size());
      Assert.assertEquals(2, listB.size());

      Assert.assertEquals("A", listA.get(0));
      Assert.assertEquals("A1", listB.get(0));

      Assert.assertEquals("B", listA.get(1));
      Assert.assertEquals("B1", listB.get(1));
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumBiConsumerMapOfEntriesDuplicateEntry() {
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      //@formatter:off
      var map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); }),
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); })
      );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumBiConsumerMapOfEntriesPut() {
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      //@formatter:off
      var map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); }),
         Map.entry(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); })
      );
      //@formatter:on

      map.put(TestEnum.C, (a, b) -> {
         listA.add(a);
         listB.add(b);
      });
   }

   @Test
   public void testEnumBiConsumerMapOfEntriesEmpty() {

      //@formatter:off
      var map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class
      );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumBiConsumerMapOfEntriesNull() {

      //@formatter:off
      var map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         (Map.Entry<TestEnum,BiConsumer<String,String>>) null
      );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumBiConsumerMapOfEntriesOneIsNull() {
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      //@formatter:off
      var map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); }),
         (Map.Entry<TestEnum,BiConsumer<String,String>>) null,
         Map.entry(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); })
      );
      //@formatter:on
   }

   /*
    * EnumBiFunctionMap Tests
    */

   @Test
   public void testEnumBiFunctionMapApply() {
      var map = new EnumBiFunctionMap<TestEnum, String, String, String>(TestEnum.class);
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      map.put(TestEnum.A, (a, b) -> {
         listA.add(a);
         listB.add(b);
         return a.concat(b);
      });

      var result = map.apply(TestEnum.A, "A", "B");

      Assert.assertEquals(1, listA.size());
      Assert.assertEquals("A", listA.get(0));

      Assert.assertEquals(1, listB.size());
      Assert.assertEquals("B", listB.get(0));

      Assert.assertEquals("AB", result);
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumBiFunctionMapAcceptNoSuchElement() {

      var map = new EnumBiFunctionMap<TestEnum, String, String, String>(TestEnum.class);

      map.apply(TestEnum.A, "A", "B");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumBiFunctionMapAcceptNullKey() {

      var map = new EnumBiFunctionMap<TestEnum, String, String, String>(TestEnum.class);

      map.apply((TestEnum) null, "A", "B");
   }

   @Test
   public void testEnumBiFunctionMapOfEntries() {
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      //@formatter:off
      var map = EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); }),
         Map.entry(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); })
      );
      //@formatter:on

      var resultA = map.apply(TestEnum.A, "A", "A1");
      var resultB = map.apply(TestEnum.B, "B", "B1");

      Assert.assertEquals(2, listA.size());
      Assert.assertEquals(2, listB.size());

      Assert.assertEquals("A", listA.get(0));
      Assert.assertEquals("A1", listB.get(0));

      Assert.assertEquals("B", listA.get(1));
      Assert.assertEquals("B1", listB.get(1));

      Assert.assertEquals("AA1", resultA);
      Assert.assertEquals("BB1", resultB);
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumBiFunctionMapOfEntriesDuplicateEntry() {
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      //@formatter:off
      var map = EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); }),
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); })
      );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumBiFunctionMapOfEntriesPut() {
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      //@formatter:off
      var map = EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); }),
         Map.entry(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); })
      );
      //@formatter:on

      map.put(TestEnum.C, (a, b) -> {
         listA.add(a);
         listB.add(b);
         return a.concat(b);
      });
   }

   @Test
   public void testEnumBiFunctionMapOfEntriesEmpty() {

      //@formatter:off
      var map = EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class
      );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumBiFunctionMapOfEntriesNull() {

      //@formatter:off
      var map = EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class,
         (Map.Entry<TestEnum,BiFunction<String,String,String>>) null
      );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumBiFunctionMapOfEntriesOneIsNull() {
      var listA = new ArrayList<String>();
      var listB = new ArrayList<String>();

      //@formatter:off
      var map = EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); }),
         (Map.Entry<TestEnum,BiFunction<String,String,String>>) null,
         Map.entry(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); })
      );
      //@formatter:on
   }

   /*
    * EnumConsumerMap Tests
    */

   @Test
   public void testEnumConsumerMapAccept() {
      var map = new EnumConsumerMap<TestEnum, String>(TestEnum.class);
      var listA = new ArrayList<String>();

      map.put(TestEnum.A, (a) -> listA.add(a));

      map.accept(TestEnum.A, "A");

      Assert.assertEquals(1, listA.size());
      Assert.assertEquals("A", listA.get(0));
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumConsumerMapAcceptNoSuchElement() {

      var map = new EnumConsumerMap<TestEnum, String>(TestEnum.class);

      map.accept(TestEnum.A, "A");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumConsumerMapAcceptNullKey() {

      var map = new EnumConsumerMap<TestEnum, String>(TestEnum.class);

      map.accept((TestEnum) null, "A");
   }

   @Test
   public void testEnumConsumerMapOfEntries() {
      var listA = new ArrayList<String>();

      //@formatter:off
      var map = EnumConsumerMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a) -> listA.add(a) ),
         Map.entry(TestEnum.B, (a) -> listA.add(a) )
      );
      //@formatter:on

      map.accept(TestEnum.A, "A");
      map.accept(TestEnum.B, "B");

      Assert.assertEquals(2, listA.size());

      Assert.assertEquals("A", listA.get(0));

      Assert.assertEquals("B", listA.get(1));
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumConsumerMapOfEntriesDuplicateEntry() {
      var listA = new ArrayList<String>();

      //@formatter:off
      var map = EnumConsumerMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a) -> listA.add(a)),
         Map.entry(TestEnum.A, (a) -> listA.add(a))
      );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumConsumerMapOfEntriesPut() {
      var listA = new ArrayList<String>();

      //@formatter:off
      var map = EnumConsumerMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a) -> listA.add(a)),
         Map.entry(TestEnum.B, (a) -> listA.add(a))
      );
      //@formatter:on

      map.put(TestEnum.C, (a) -> listA.add(a));
   }

   @Test
   public void testEnumConsumerMapOfEntriesEmpty() {

      //@formatter:off
      var map = EnumConsumerMap.<TestEnum,String>ofEntries
      (
         TestEnum.class
      );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumConsumerMapOfEntriesNull() {

      //@formatter:off
      var map = EnumConsumerMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         (Map.Entry<TestEnum,Consumer<String>>) null
      );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumConsumerMapOfEntriesOneIsNull() {
      var listA = new ArrayList<String>();

      //@formatter:off
      var map = EnumConsumerMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, (a) -> listA.add(a)),
         (Map.Entry<TestEnum,Consumer<String>>) null,
         Map.entry(TestEnum.B, (a) -> listA.add(a))
      );
      //@formatter:on
   }

   /*
    * EnumFunctionMap Tests
    */

   @Test
   public void testEnumFunctionMapAccept() {
      var map = new EnumFunctionMap<TestEnum, String, String>(TestEnum.class);

      map.put(TestEnum.A, String::toLowerCase);

      var result = map.apply(TestEnum.A, "A");

      Assert.assertEquals("a", result);
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumFunctionMapAcceptNoSuchElement() {

      var map = new EnumFunctionMap<TestEnum, String, String>(TestEnum.class);

      map.apply(TestEnum.A, "A");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumFunctionMapAcceptNullKey() {

      var map = new EnumFunctionMap<TestEnum, String, String>(TestEnum.class);

      map.apply((TestEnum) null, "A");
   }

   @Test
   public void testEnumFunctionMapOfEntries() {

      //@formatter:off
      var map = EnumFunctionMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, String::toLowerCase ),
         Map.entry(TestEnum.B, String::toLowerCase )
      );
      //@formatter:on

      var resultA = map.apply(TestEnum.A, "A");
      var resultB = map.apply(TestEnum.B, "B");

      Assert.assertEquals("a", resultA);

      Assert.assertEquals("b", resultB);
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumFunctionMapOfEntriesDuplicateEntry() {

      //@formatter:off
      var map = EnumFunctionMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, String::toLowerCase),
         Map.entry(TestEnum.A, String::toLowerCase)
      );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumFunctionMapOfEntriesPut() {

      //@formatter:off
      var map = EnumFunctionMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, String::toLowerCase),
         Map.entry(TestEnum.B, String::toLowerCase)
      );
      //@formatter:on

      map.put(TestEnum.C, String::toLowerCase);
   }

   @Test
   public void testEnumFunctionMapOfEntriesEmpty() {

      //@formatter:off
      var map = EnumFunctionMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class
      );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumFunctionMapOfEntriesNull() {

      //@formatter:off
      var map = EnumFunctionMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         (Map.Entry<TestEnum,Function<String,String>>) null
      );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumFunctionMapOfEntriesOneIsNull() {

      //@formatter:off
      var map = EnumFunctionMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, String::toLowerCase),
         (Map.Entry<TestEnum,Function<String,String>>) null,
         Map.entry(TestEnum.B, String::toLowerCase)
      );
      //@formatter:on
   }

   /*
    * EnumSupplierMap Tests
    */

   @Test
   public void testEnumSupplierMapAccept() {
      var map = new EnumSupplierMap<TestEnum, String>(TestEnum.class);

      map.put(TestEnum.A, () -> {
         return "A";
      });

      var result = map.get(TestEnum.A);

      Assert.assertEquals("A", result);
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumSupplierMapAcceptNoSuchElement() {

      var map = new EnumSupplierMap<TestEnum, String>(TestEnum.class);

      map.get(TestEnum.A);
   }

   @Test(expected = NullPointerException.class)
   public void testEnumSupplierMapAcceptNullKey() {

      var map = new EnumSupplierMap<TestEnum, String>(TestEnum.class);

      map.get((TestEnum) null);
   }

   @Test
   public void testEnumSupplierMapOfEntries() {

      //@formatter:off
      var map = EnumSupplierMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, () -> { return "A"; } ),
         Map.entry(TestEnum.B, () -> { return "B"; } )
      );
      //@formatter:on

      var resultA = map.get(TestEnum.A);
      var resultB = map.get(TestEnum.B);

      Assert.assertEquals("A", resultA);

      Assert.assertEquals("B", resultB);
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumSupplierMapOfEntriesDuplicateEntry() {

      //@formatter:off
      var map = EnumSupplierMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, () -> { return "A"; }),
         Map.entry(TestEnum.A, () -> { return "A"; })
      );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumSupplierMapOfEntriesPut() {

      //@formatter:off
      var map = EnumSupplierMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, () -> { return "A"; }),
         Map.entry(TestEnum.B, () -> { return "B"; })
      );

      map.put(TestEnum.C, () -> { return "C"; });
      //@formatter:on
   }

   @Test
   public void testEnumSupplierMapOfEntriesEmpty() {

      //@formatter:off
      var map = EnumSupplierMap.<TestEnum,String>ofEntries
      (
         TestEnum.class
      );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumSupplierMapOfEntriesNull() {

      //@formatter:off
      var map = EnumSupplierMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         (Map.Entry<TestEnum,Supplier<String>>) null
      );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumSupplierMapOfEntriesOneIsNull() {

      //@formatter:off
      var map = EnumSupplierMap.<TestEnum,String>ofEntries
      (
         TestEnum.class,
         Map.entry(TestEnum.A, () -> { return "A"; }),
         (Map.Entry<TestEnum,Supplier<String>>) null,
         Map.entry(TestEnum.B, () -> { return "B"; })
      );
      //@formatter:on
   }

}

/* EOF */
