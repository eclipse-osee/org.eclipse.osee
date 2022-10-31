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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for implementations of the {@link RankMap} interface.
 *
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class RankMapTest {

   static Predicate<Object> keyIsString = new Predicate<Object>() {

      @Override
      public boolean test(Object key) {
         return Objects.nonNull(key) && (key instanceof String);
      }
   };

   @SuppressWarnings("unchecked")
   static Predicate<Object> keysAreStringsRank1[] = new Predicate[] {keyIsString};

   @SuppressWarnings("unchecked")
   static Predicate<Object> keysAreStringsRank2[] = new Predicate[] {keyIsString, keyIsString};

   @SuppressWarnings("unchecked")
   static Predicate<Object> keysAreStringsRank3[] = new Predicate[] {keyIsString, keyIsString, keyIsString};

   /**
    * An {@link List} of arrays with the test parameters for each iteration of the {@link RankMapTest} test suite. Each
    * array contains a single entry which is a {@link Supplier} that provides the implementation of the {@link RankMap}
    * interface for that iteration of the test suite. {@link Supplier}s are used in the parameter array instead of a
    * {@link RankMapTest} implementation so that each test gets a fresh map.
    *
    * @return {@link List} of test parameters for each iteration of the {@link RankMapTest} test suite.
    */

   @Parameters
   public static Collection<Object[]> data() {
      //@formatter:off
      return
         List.of
            (
               (Object[]) new Supplier[] { () -> new RankHashMap<String>("Rank1TestMap", 1, 32, 0.75f, keysAreStringsRank1 ) },
               (Object[]) new Supplier[] { () -> new RankHashMap<String>("Rank2TestMap", 2, 32, 0.75f, keysAreStringsRank2 ) },
               (Object[]) new Supplier[] { () -> new RankHashMap<String>("Rank3TestMap", 3, 32, 0.75f, keysAreStringsRank3 ) }
            );
      //@formatter:on
   }

   private final Supplier<RankMap<String>> rankMapSupplier;
   private RankMap<String> rankMap;
   private RankMap<String> anotherRankMap;
   private static final int nMax = 3;
   private String[][] keyArray;
   private Object[][] badKeysArray;
   private String[] valueArray;
   private String[] notKeysArray;
   private String[] oneBigKeysArray;
   private String[] oneSmallKeysArray;
   private String[] nullKeysArray;
   private String[] emptyKeysArray;
   private RankMap.Entry<String>[] entryArray;
   private boolean assertionsEnabled;

   public RankMapTest(Supplier<RankMap<String>> rankMapSupplier) {
      this.rankMapSupplier = rankMapSupplier;
   }

   private static final String[] keys = {"A", "B", "C"};
   private static final String[] notKeys = {"X", "Y", "Z"};

   private static int pow(int base, int exponent) {
      if (exponent == 0) {
         return 1;
      }

      var rv = base;

      for (int i = 1; i < exponent; i++) {
         rv = rv * base;
      }

      return rv;
   }

   private static void inc(int[] keyIndexBuilder, int maxColumn, int maxValue) {
      for (int c = maxColumn - 1; c >= 0; c--) {
         if (keyIndexBuilder[c] < maxValue) {
            keyIndexBuilder[c] = keyIndexBuilder[c] + 1;
            return;
         }

         keyIndexBuilder[c] = 0;
      }
   }

   private static String buildValue(String[] keys) {
      return new StringBuilder(64).append("VALUE ").append(
         Arrays.stream(keys).collect(Collectors.joining(",", "(", ")"))).toString();
   }

   @Before
   public void testSetup() {

      this.assertionsEnabled = false;
      assert this.assertionsEnabled = true;

      this.rankMap = this.rankMapSupplier.get();
      this.anotherRankMap = this.rankMapSupplier.get();

      var nMax = RankMapTest.nMax;
      var rMax = this.rankMap.rank();
      var eMax = RankMapTest.pow(nMax, rMax);

      this.keyArray = new String[eMax][rMax];
      this.badKeysArray = new Object[rMax][rMax];
      this.notKeysArray = new String[rMax];
      this.oneBigKeysArray = new String[rMax + 1];
      this.oneSmallKeysArray = new String[rMax - 1];
      this.nullKeysArray = null;
      this.emptyKeysArray = new String[0];
      this.valueArray = new String[eMax];

      @SuppressWarnings("unchecked")
      RankMap.Entry<String>[] theEntryArray = new RankMap.Entry[eMax];
      this.entryArray = theEntryArray;

      var keyIndexBuilder = new int[rMax];

      for (int r = 0; r < rMax; r++) {
         keyIndexBuilder[r] = 0;
      }

      for (int r = 0; r < rMax; r++) {
         this.notKeysArray[r] = RankMapTest.notKeys[r % rMax];
      }

      for (int r = 0; r < rMax + 1; r++) {
         this.oneBigKeysArray[r] = RankMapTest.notKeys[r % rMax];
      }

      for (int r = 0; r < rMax - 1; r++) {
         this.oneSmallKeysArray[r] = RankMapTest.notKeys[r % rMax];
      }

      for (int ro = 0; ro < rMax; ro++) {
         for (int r = 0; r < rMax; r++) {
            this.badKeysArray[ro][r] = r != ro ? RankMapTest.notKeys[r % rMax] : 5;
         }
      }

      for (int e = 0; e < eMax; e++) {
         for (int r = 0; r < rMax; r++) {
            this.keyArray[e][r] = keys[keyIndexBuilder[r]];
         }

         RankMapTest.inc(keyIndexBuilder, rMax, nMax - 1);
         this.valueArray[e] = RankMapTest.buildValue(this.keyArray[e]);

         this.rankMap.associate(this.valueArray[e], (Object[]) this.keyArray[e]);
         this.anotherRankMap.associate(this.valueArray[e], (Object[]) this.keyArray[e]);

         this.entryArray[e] = this.rankMap.getEntry((Object[]) this.keyArray[e]).orElseThrow();
      }

   }

   /*
    * associate Tests
    */

   @Test
   public void testInitialSize() {
      Assert.assertEquals(RankMapTest.pow(RankMapTest.nMax, this.rankMap.rank()), this.rankMap.size());
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testAssociateRankMapTooManyKeysException() {
      this.rankMap.associate(this.valueArray[0], (Object[]) this.oneBigKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testAssociateRankMapInsufficientKeysException() {
      this.rankMap.associate(this.valueArray[0], (Object[]) this.oneSmallKeysArray);
   }

   @Test(expected = RankMapNullValueException.class)
   public void testAssociateRankMapNullValueException() {
      this.rankMap.associate(null, (Object[]) this.keyArray[0]);
   }

   @Test
   public void testAssociateBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.associateThrowOnDuplicate("LKA", this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   @Test
   public void testAssociateReplaceValue() {

      var entry = this.rankMap.getEntry((Object[]) this.keyArray[0]).orElseThrow();

      Assert.assertEquals(this.valueArray[0], entry.getValue());

      this.rankMap.associate("LKA", (Object[]) this.keyArray[0]);

      Assert.assertEquals("LKA", entry.getValue());

      this.rankMap.associate(this.valueArray[0], (Object[]) this.keyArray[0]);

      Assert.assertEquals(this.valueArray[0], entry.getValue());
   }

   /*
    * associateThrowOnDuplicate Tests
    */

   @Test(expected = RankMapDuplicateEntryException.class)
   public void testAssociateThrowOnDuplicateRankMapDuplicateEntryException() {
      this.rankMap.associateThrowOnDuplicate(this.valueArray[0], (Object[]) this.keyArray[0]);
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testAssociateThrowOnDuplicateRankMapTooManyKeysException() {
      this.rankMap.associateThrowOnDuplicate(this.valueArray[0], (Object[]) this.oneBigKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testAssociateThrowOnDuplicateRankMapInsufficientKeysException() {
      this.rankMap.associateThrowOnDuplicate(this.valueArray[0], (Object[]) this.oneSmallKeysArray);
   }

   @Test(expected = RankMapNullValueException.class)
   public void testAssociateThrowOnDuplicateRankMapNullValueException() {
      this.rankMap.associateThrowOnDuplicate(null, (Object[]) this.keyArray[0]);
   }

   @Test
   public void testAssociateThrowOnDuplicateBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.associateThrowOnDuplicate("LKA", this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * containsKeys Tests
    */

   @Test
   public void testContainsKeys() {
      for (int e = 0; e < this.keyArray.length; e++) {
         for (int r = this.rankMap.rank(); r >= 1; r--) {
            var keys = new String[r];
            for (int i = 0; i < r; i++) {
               keys[i] = this.keyArray[e][i];
            }
            Assert.assertTrue(this.rankMap.containsKeys((Object[]) keys));
         }
      }
   }

   @Test
   public void testContainsKeysNotPresent() {
      Assert.assertFalse(this.rankMap.containsKeys((Object[]) this.notKeysArray));
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testContainsKeysRankMapTooManyKeysException() {
      this.rankMap.containsKeys((Object[]) this.oneBigKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testContainsKeysRankMapInsufficientKeysExceptionNull() {
      this.rankMap.containsKeys((Object[]) this.nullKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testContainsKeysRankMapInsufficientKeysExceptionEmpty() {
      this.rankMap.containsKeys((Object[]) this.emptyKeysArray);
   }

   @Test
   public void testContainsKeysBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.containsKeys(this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * containsKeysNoExceptions Tests
    */

   @Test
   public void testContainsKeysNoExceptions() {
      for (int e = 0; e < this.keyArray.length; e++) {
         for (int r = this.rankMap.rank(); r >= 1; r--) {
            var keys = new String[r];
            for (int i = 0; i < r; i++) {
               keys[i] = this.keyArray[e][i];
            }
            Assert.assertTrue(this.rankMap.containsKeysNoExceptions((Object[]) keys));
         }
      }
   }

   @Test
   public void testContainsKeysNoExceptionsNotPresent() {
      Assert.assertFalse(this.rankMap.containsKeysNoExceptions((Object[]) this.notKeysArray));
   }

   @Test
   public void testContainsKeysNoExceptionsTooManyKeys() {
      Assert.assertFalse(this.rankMap.containsKeysNoExceptions((Object[]) this.oneBigKeysArray));
   }

   @Test
   public void testContainsKeysInsufficientKeysNull() {
      Assert.assertFalse(this.rankMap.containsKeysNoExceptions((Object[]) this.nullKeysArray));
   }

   @Test
   public void testContainsKeysInsufficientKeysEmpty() {
      Assert.assertFalse(this.rankMap.containsKeysNoExceptions((Object[]) this.emptyKeysArray));
   }

   @Test
   public void testContainsKeysNoExceptionsBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.containsKeysNoExceptions(this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * entrySet Tests
    */

   @Test
   public void testEntrySetContains() {
      var entrySet = this.rankMap.entrySet();
      var entryCounter = new Object() {
         int count = 0;

         void inc() {
            this.count++;
         }

         int get() {
            return this.count;
         }
      };

      //@formatter:off
      entrySet.forEach
         (
            ( entry ) ->
            {
               entryCounter.inc();

               Assert.assertTrue( entrySet.contains( entry ) );
            }
         );
      //@formatter:on

      Assert.assertEquals(this.rankMap.size(), entryCounter.get());
   }

   @SuppressWarnings("unlikely-arg-type")
   @Test
   public void testEntrySetContainsBadObject() {
      var entrySet = this.rankMap.entrySet();

      Assert.assertFalse(entrySet.contains(5));
   }

   @Test
   public void testEntrySetContainsFromWrongMap() {
      var entrySet = this.rankMap.entrySet();
      var anotherEntrySet = this.anotherRankMap.entrySet();

      anotherEntrySet.forEach((anotherEntry) -> Assert.assertFalse(entrySet.contains(anotherEntry)));
   }

   @Test
   public void testEntrySetContainsKeys() {
      var entrySet = this.rankMap.entrySet();

      for (int e = 0; e < this.keyArray.length; e++) {
         Assert.assertTrue(entrySet.containsKeys((Object[]) this.keyArray[e]));
      }
   }

   @Test
   public void testEntrySetSize() {
      var entrySet = this.rankMap.entrySet();

      Assert.assertEquals(this.rankMap.size(), entrySet.size());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEntrySetAdd() {
      var entrySet = this.rankMap.entrySet();
      var anotherEntrySet = this.rankMap.entrySet();

      anotherEntrySet.forEach(entrySet::add);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEntrySetAddAll() {
      var entrySet = this.rankMap.entrySet();
      var anotherEntrySet = this.rankMap.entrySet();

      entrySet.addAll(anotherEntrySet);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEntrySetClear() {
      var entrySet = this.rankMap.entrySet();

      entrySet.clear();
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEntrySetRemove() {
      var entrySet = this.rankMap.entrySet();

      entrySet.forEach(entrySet::remove);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEntrySetRemoveAll() {
      var entrySet = this.rankMap.entrySet();

      entrySet.removeAll(entrySet);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEntrySetRetainAll() {
      var entrySet = this.rankMap.entrySet();
      var anotherSet = new HashSet<RankMap.Entry<String>>();

      entrySet.retainAll(anotherSet);
   }

   /*
    * get Tests
    */

   @Test
   public void testGet() {
      for (int e = 0; e < this.keyArray.length; e++) {
         Assert.assertEquals(this.valueArray[e], this.rankMap.get((Object[]) this.keyArray[e]).orElseThrow());
      }
   }

   @Test
   public void testGetNotPresent() {
      Assert.assertTrue(this.rankMap.get((Object[]) this.notKeysArray).isEmpty());
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testGetRankMapTooManyKeysException() {
      this.rankMap.get((Object[]) this.oneBigKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testGetRankMapInsufficientKeysExceptionOneSmall() {
      this.rankMap.get((Object[]) this.oneSmallKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testGetRankMapInsufficientKeysExceptionNull() {
      this.rankMap.get((Object[]) this.nullKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testGetRankMapInsufficientKeysExceptionEmpty() {
      this.rankMap.get((Object[]) this.emptyKeysArray);
   }

   @Test
   public void testGetBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.get(this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * getEntry Tests
    */

   @Test
   public void testGetEntry() {
      int exceptionCount = 0;
      for (int e = 0; e < this.keyArray.length; e++) {
         var entry = this.rankMap.getEntry((Object[]) this.keyArray[e]).orElseThrow();
         Assert.assertEquals(this.valueArray[e], entry.getValue());
         int i;
         for (i = 0; i < this.keyArray[e].length; i++) {
            Assert.assertEquals(this.keyArray[e][i], entry.getKey(i));
         }
         try {
            entry.getKey(i);
         } catch (ArrayIndexOutOfBoundsException exception) {
            exceptionCount++;
         }
      }
      Assert.assertEquals(this.keyArray.length, exceptionCount);
   }

   @Test
   public void testGetEntryNotPresent() {
      Assert.assertTrue(this.rankMap.getEntry((Object[]) this.notKeysArray).isEmpty());
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testGetEntryRankMapTooManyKeysException() {
      this.rankMap.getEntry((Object[]) this.oneBigKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testGetEntryRankMapInsufficientKeysExceptionOneSmall() {
      this.rankMap.getEntry((Object[]) this.oneSmallKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testGetEntryRankMapInsufficientKeysExceptionNull() {
      this.rankMap.getEntry((Object[]) this.nullKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testGetEntryRankMapInsufficientKeysExceptionEmpty() {
      this.rankMap.getEntry((Object[]) this.emptyKeysArray);
   }

   @Test
   public void testGetEntryBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.getEntry(this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * getEntryNoExceptions Tests
    */

   @Test
   public void testgetEntryNoExceptions() {
      int exceptionCount = 0;
      for (int e = 0; e < this.keyArray.length; e++) {
         var entry = this.rankMap.getEntryNoExceptions((Object[]) this.keyArray[e]).orElseThrow();
         Assert.assertEquals(this.valueArray[e], entry.getValue());
         int i;
         for (i = 0; i < this.keyArray[e].length; i++) {
            Assert.assertEquals(this.keyArray[e][i], entry.getKey(i));
         }
         try {
            entry.getKey(i);
         } catch (ArrayIndexOutOfBoundsException exception) {
            exceptionCount++;
         }
      }
      Assert.assertEquals(this.keyArray.length, exceptionCount);
   }

   @Test
   public void testgetEntryNoExceptionsNotPresent() {
      Assert.assertTrue(this.rankMap.getEntryNoExceptions((Object[]) this.notKeysArray).isEmpty());
   }

   @Test
   public void testgetEntryNoExceptionsTooManyKeys() {
      Assert.assertTrue(this.rankMap.getEntryNoExceptions((Object[]) this.oneBigKeysArray).isEmpty());
   }

   @Test
   public void testgetEntryNoExceptionsInsufficientKeysOneSmall() {
      Assert.assertTrue(this.rankMap.getEntryNoExceptions((Object[]) this.oneSmallKeysArray).isEmpty());
   }

   @Test
   public void testgetEntryNoExceptionsInsufficientKeysNull() {
      Assert.assertTrue(this.rankMap.getEntryNoExceptions((Object[]) this.nullKeysArray).isEmpty());
   }

   @Test
   public void testgetEntryNoExceptionsInsufficientKeysEmpty() {
      Assert.assertTrue(this.rankMap.getEntryNoExceptions((Object[]) this.emptyKeysArray).isEmpty());
   }

   @Test
   public void testgetEntryNoExceptionsBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.getEntryNoExceptions(this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * getNoExceptions Tests
    */

   @Test
   public void testgetNoExceptions() {
      for (int e = 0; e < this.keyArray.length; e++) {
         Assert.assertEquals(this.valueArray[e],
            this.rankMap.getNoExceptions((Object[]) this.keyArray[e]).orElseThrow());
      }
   }

   @Test
   public void testgetNoExceptionsNotPresent() {
      Assert.assertTrue(this.rankMap.getNoExceptions((Object[]) this.notKeysArray).isEmpty());
   }

   @Test
   public void testgetNoExceptionsTooManyKeys() {
      Assert.assertTrue(this.rankMap.getNoExceptions((Object[]) this.oneBigKeysArray).isEmpty());
   }

   @Test
   public void testgetNoExceptionsInsufficientKeysOneSmall() {
      Assert.assertTrue(this.rankMap.getNoExceptions((Object[]) this.oneSmallKeysArray).isEmpty());
   }

   @Test
   public void testgetNoExceptionsInsufficientKeysNull() {
      Assert.assertTrue(this.rankMap.getNoExceptions((Object[]) this.nullKeysArray).isEmpty());
   }

   @Test
   public void testgetNoExceptionsInsufficientKeysEmpty() {
      Assert.assertTrue(this.rankMap.getNoExceptions((Object[]) this.emptyKeysArray).isEmpty());
   }

   @Test
   public void testgetNoExceptionsBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.getNoExceptions(this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * remove Tests
    */

   @Test
   public void testRemove() {
      for (int e = 0; e < this.keyArray.length; e++) {
         Assert.assertTrue(this.rankMap.containsKeys((Object[]) this.keyArray[e]));
         var removedValueOptional = this.rankMap.remove((Object[]) this.keyArray[e]);
         Assert.assertTrue(removedValueOptional.isPresent());
         var removedValue = removedValueOptional.get();
         Assert.assertEquals(this.valueArray[e], removedValue);
         Assert.assertFalse(this.rankMap.containsKeys((Object[]) this.keyArray[e]));
         Assert.assertEquals(this.keyArray.length - e - 1, this.rankMap.size());
      }
      Assert.assertEquals(0, this.rankMap.size());
      for (int e = 0; e < this.keyArray.length; e++) {
         this.rankMap.associate(this.valueArray[e], (Object[]) this.keyArray[e]);
      }
      Assert.assertEquals(this.keyArray.length, this.rankMap.size());
   }

   @Test
   public void testRemoveNotPresent() {
      Assert.assertTrue(this.rankMap.remove((Object[]) this.notKeysArray).isEmpty());
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testRemoveRankMapTooManyKeysException() {
      this.rankMap.remove((Object[]) this.oneBigKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testRemoveRankMapInsufficientKeysExceptionOneSmall() {
      this.rankMap.remove((Object[]) this.oneSmallKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testRemoveRankMapInsufficientKeysExceptionNull() {
      this.rankMap.remove((Object[]) this.nullKeysArray);
   }

   @Test(expected = RankMapInsufficientKeysException.class)
   public void testRemoveRankMapInsufficientKeysExceptionEmpty() {
      this.rankMap.remove((Object[]) this.emptyKeysArray);
   }

   @Test
   public void testRemoveBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.remove(this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * removeNoException Tests
    */

   @Test
   public void testRemoveNoException() {
      for (int e = 0; e < this.keyArray.length; e++) {
         Assert.assertTrue(this.rankMap.containsKeys((Object[]) this.keyArray[e]));
         var removedValueOptional = this.rankMap.removeNoException((Object[]) this.keyArray[e]);
         Assert.assertTrue(removedValueOptional.isPresent());
         var removedValue = removedValueOptional.get();
         Assert.assertEquals(this.valueArray[e], removedValue);
         Assert.assertFalse(this.rankMap.containsKeys((Object[]) this.keyArray[e]));
         Assert.assertEquals(this.keyArray.length - e - 1, this.rankMap.size());
      }
      Assert.assertEquals(0, this.rankMap.size());
      for (int e = 0; e < this.keyArray.length; e++) {
         this.rankMap.associate(this.valueArray[e], (Object[]) this.keyArray[e]);
      }
      Assert.assertEquals(this.keyArray.length, this.rankMap.size());
   }

   @Test
   public void testRemoveNoExceptionNotPresent() {
      Assert.assertTrue(this.rankMap.removeNoException((Object[]) this.notKeysArray).isEmpty());
   }

   @Test
   public void testRemoveNoExceptionTooManyKeys() {
      Assert.assertTrue(this.rankMap.removeNoException((Object[]) this.oneBigKeysArray).isEmpty());
   }

   @Test
   public void testRemoveNoExceptionInsufficientKeysOneSmall() {
      Assert.assertTrue(this.rankMap.removeNoException((Object[]) this.oneSmallKeysArray).isEmpty());
   }

   @Test
   public void testRemoveNoExceptionInsufficientKeysNull() {
      Assert.assertTrue(this.rankMap.removeNoException((Object[]) this.nullKeysArray).isEmpty());
   }

   @Test
   public void testRemoveNoExceptionInsufficientKeysEmpty() {
      Assert.assertTrue(this.rankMap.removeNoException((Object[]) this.emptyKeysArray).isEmpty());
   }

   @Test
   public void testRemoveNoExceptionBadKey() {
      Assume.assumeTrue(this.assertionsEnabled);

      int exceptionCount = 0;
      for (int r = 0; r < this.rankMap.rank(); r++) {
         try {
            this.rankMap.removeNoException(this.badKeysArray[r]);
         } catch (AssertionError e) {
            exceptionCount++;
         }
      }

      Assert.assertEquals(this.rankMap.rank(), exceptionCount);
   }

   /*
    * stream Tests
    */

   @Test
   public void testStream() {

      for (var keyCount = 0; keyCount <= this.rankMap.rank(); keyCount++) {
         var entryCountPerKeySet = RankMapTest.pow(nMax, this.rankMap.rank() - keyCount);
         var testSetCount = RankMapTest.pow(nMax, keyCount);

         var keySet = new Object[keyCount];

         for (int t = 0; t < testSetCount; t++) {
            var entryStart = t * entryCountPerKeySet;
            var entryEnd = entryStart + entryCountPerKeySet - 1;

            for (int k = 0; k < keyCount; k++) {
               keySet[k] = this.keyArray[entryStart][k];
            }

            var set = this.rankMap.stream(keySet).collect(Collectors.toSet());

            Assert.assertEquals(entryCountPerKeySet, set.size());

            for (int e = entryStart; e <= entryEnd; e++) {
               Assert.assertTrue(set.contains(this.valueArray[e]));
            }
         }
      }
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testStreamRankMapTooManyKeysException() {
      this.rankMap.stream((Object[]) this.oneBigKeysArray);
   }

   /*
    * streamEntries Tests
    */

   @Test
   public void testStreamEntries() {

      for (var keyCount = 0; keyCount <= this.rankMap.rank(); keyCount++) {

         var entryCountPerKeySet = RankMapTest.pow(nMax, this.rankMap.rank() - keyCount);
         var testSetCount = RankMapTest.pow(nMax, keyCount);

         var keySet = new Object[keyCount];

         for (int t = 0; t < testSetCount; t++) {
            var entryStart = t * entryCountPerKeySet;
            var entryEnd = entryStart + entryCountPerKeySet - 1;

            for (int k = 0; k < keyCount; k++) {
               keySet[k] = this.keyArray[entryStart][k];
            }

            var set = this.rankMap.streamEntries(keySet).collect(Collectors.toSet());

            Assert.assertEquals(entryCountPerKeySet, set.size());

            for (int e = entryStart; e <= entryEnd; e++) {
               Assert.assertTrue(set.contains(this.entryArray[e]));
            }
         }
      }
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testStreamEntriesRankMapTooManyKeysException() {
      this.rankMap.streamEntries((Object[]) this.oneBigKeysArray);
   }

   /*
    * streamKeysAt Tests
    */

   @Test
   public void testStreamKeysAt() {

      for (var keyCount = 0; keyCount <= this.rankMap.rank() - 1; keyCount++) {

         var entryCountPerKeySet = RankMapTest.pow(nMax, this.rankMap.rank() - keyCount);
         var testSetCount = RankMapTest.pow(nMax, keyCount);

         var keySet = new Object[keyCount];

         for (int t = 0; t < testSetCount; t++) {
            var entryStart = t * entryCountPerKeySet;

            for (int k = 0; k < keyCount; k++) {
               keySet[k] = this.keyArray[entryStart][k];
            }

            var set = this.rankMap.streamKeysAt(keySet).collect(Collectors.toSet());

            Assert.assertEquals(RankMapTest.nMax, set.size());

            for (int i = 0; i < RankMapTest.nMax; i++) {
               Assert.assertTrue(set.contains(RankMapTest.keys[i]));
            }
         }
      }
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testStreamKeysAtRankMapTooManyKeysException() {
      this.rankMap.streamKeysAt((Object[]) this.keyArray[0]);
   }

   /*
    * streamKeysAtAndBelow Tests
    */

   @SuppressWarnings("serial")
   class CatchCounter extends HashMap<Object, Integer> {
      void collect(Object object) {
         var key = (String) object;

         if (!this.containsKey(key)) {
            this.put(key, 1);
         } else {
            var count = this.get(key);
            count++;
            this.put(key, count);
         }
      }
   }

   @Test
   public void testStreamKeysAtAndBelow() {

      for (var keyCount = 0; keyCount <= this.rankMap.rank() - 1; keyCount++) {

         var entryCountPerKeySet = RankMapTest.pow(nMax, this.rankMap.rank() - keyCount);
         var testSetCount = RankMapTest.pow(nMax, keyCount);

         var keySet = new Object[keyCount];

         for (int t = 0; t < testSetCount; t++) {

            var entryStart = t * entryCountPerKeySet;

            for (int k = 0; k < keyCount; k++) {
               keySet[k] = this.keyArray[entryStart][k];
            }

            Integer expectedCount = 0;
            for (int r = 0; r < this.rankMap.rank() - keyCount; r++) {
               expectedCount += RankMapTest.pow(RankMapTest.nMax, r);
            }

            var catchCounter = new CatchCounter();
            this.rankMap.streamKeysAtAndBelow(keySet).forEach(catchCounter::collect);

            Assert.assertEquals(RankMapTest.nMax, catchCounter.size());

            for (int i = 0; i < RankMapTest.nMax; i++) {
               Assert.assertEquals(expectedCount, catchCounter.get(RankMapTest.keys[i]));
            }
         }
      }
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testStreamKeysAtAndBelowRankMapTooManyKeysException() {
      this.rankMap.streamKeysAt((Object[]) this.keyArray[0]);
   }

   /*
    * streamKeySets Tests
    */

   class KeyBucket {
      Object[] keys;

      KeyBucket(Object[] keys) {
         this.keys = keys;
      }

      @Override
      public int hashCode() {
         int hashCode = 0;

         for (int i = 0; i < keys.length; i++) {
            var memberHash = keys[i].hashCode() * 107;
            int shift = i % Integer.SIZE;
            if (shift > 0) {
               memberHash = (memberHash << shift) | (memberHash >> (Integer.SIZE - shift));
            }
            hashCode = hashCode ^ memberHash;

         }

         return hashCode;
      }

      @Override
      public boolean equals(Object other) {

         if (!(other instanceof KeyBucket)) {
            return false;
         }

         var otherKeyBucket = (KeyBucket) other;

         var keyCount = Objects.nonNull(this.keys) ? this.keys.length : 0;
         var otherKeyCount = Objects.nonNull(otherKeyBucket.keys) ? otherKeyBucket.keys.length : 0;

         if (keyCount != otherKeyCount) {
            return false;
         }

         for (int i = 0; i < keyCount; i++) {
            if (!this.keys[i].equals(otherKeyBucket.keys[i])) {
               return false;
            }
         }

         return true;
      }
   }
   @Test
   public void testStreamKeySets() {

      for (var keyCount = 0; keyCount <= this.rankMap.rank(); keyCount++) {
         var entryCountPerKeySet = RankMapTest.pow(nMax, this.rankMap.rank() - keyCount);
         var testSetCount = RankMapTest.pow(nMax, keyCount);

         var keySet = new Object[keyCount];

         for (int t = 0; t < testSetCount; t++) {
            var entryStart = t * entryCountPerKeySet;
            var entryEnd = entryStart + entryCountPerKeySet - 1;

            for (int k = 0; k < keyCount; k++) {
               keySet[k] = this.keyArray[entryStart][k];
            }

            var set = this.rankMap.streamKeySets(keySet).map(KeyBucket::new).collect(Collectors.toSet());

            Assert.assertEquals(entryCountPerKeySet, set.size());

            for (int e = entryStart; e <= entryEnd; e++) {
               Assert.assertTrue(set.contains(new KeyBucket(this.entryArray[e].getKeyArray())));
            }
         }
      }
   }

   @Test(expected = RankMapTooManyKeysException.class)
   public void testStreamKeySetsRankMapTooManyKeysException() {
      this.rankMap.streamKeySets((Object[]) this.oneBigKeysArray);
   }

}

/* EOF */
