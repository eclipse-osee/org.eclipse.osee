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

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for implementations of the {@link DoubleMap} interface.
 *
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class DoubleMapTest {

   /**
    * An {@link List} of arrays with the test parameters for each iteration of the {@link DoubleMapTest} test suite.
    * Each array contains a single entry which is a {@link Supplier} that provides the implementation of the
    * {@link DoubleMap} interface for that iteration of the test suite. {@link Supplier}s are used in the parameter
    * array instead of a {@link DoubleMap} implementation so that each test gets a fresh map.
    *
    * @return {@link List} of test parameters for each iteration of the {@link DoubleMapTest} test suite.
    */

   @Parameters
   public static Collection<Object[]> data() {
      //@formatter:off
      return
         List.of
            (
               (Object[]) new Supplier[] { () -> new DoubleHashMap<String,String,String>()                     },
               (Object[]) new Supplier[] { () -> new DoubleHashMap<String,String,String>( 32, 0.5f )           },
               (Object[]) new Supplier[] { () -> new DoubleHashMap<String,String,String>( 32, 0.5f, 32, 0.5f ) }
            );
      //@formatter:on
   }

   private final Supplier<DoubleMap<String, String, String>> doubleMapSupplier;
   private DoubleMap<String, String, String> doubleMap;

   public DoubleMapTest(Supplier<DoubleMap<String, String, String>> doubleMapSupplier) {
      this.doubleMapSupplier = doubleMapSupplier;
   }

   @Before
   public void testSetup() {
      this.doubleMap = this.doubleMapSupplier.get();

      this.doubleMap.put("A", "A", "VALUE (A,A)");
      this.doubleMap.put("A", "B", "VALUE (A,B)");
      this.doubleMap.put("A", "C", "VALUE (A,C)");

      this.doubleMap.put("B", "A", "VALUE (B,A)");
      this.doubleMap.put("B", "B", "VALUE (B,B)");
      this.doubleMap.put("B", "C", "VALUE (B,C)");

      this.doubleMap.put("C", "A", "VALUE (C,A)");
      this.doubleMap.put("C", "B", "VALUE (C,B)");
      this.doubleMap.put("C", "C", "VALUE (C,C)");
   }

   @Test
   public void testContainsKeyPrimary() {
      Assert.assertTrue(this.doubleMap.containsKey("A"));
      Assert.assertTrue(this.doubleMap.containsKey("B"));
      Assert.assertTrue(this.doubleMap.containsKey("C"));

      Assert.assertFalse(this.doubleMap.containsKey("X"));
      Assert.assertFalse(this.doubleMap.containsKey("Y"));
      Assert.assertFalse(this.doubleMap.containsKey("Z"));
   }

   @Test
   public void testContainsKeyPrimarySecondary() {
      Assert.assertTrue(this.doubleMap.containsKey("A", "A"));
      Assert.assertTrue(this.doubleMap.containsKey("A", "B"));
      Assert.assertTrue(this.doubleMap.containsKey("A", "C"));

      Assert.assertFalse(this.doubleMap.containsKey("A", "X"));
      Assert.assertFalse(this.doubleMap.containsKey("A", "Y"));
      Assert.assertFalse(this.doubleMap.containsKey("A", "Z"));

      Assert.assertTrue(this.doubleMap.containsKey("B", "A"));
      Assert.assertTrue(this.doubleMap.containsKey("B", "B"));
      Assert.assertTrue(this.doubleMap.containsKey("B", "C"));

      Assert.assertFalse(this.doubleMap.containsKey("B", "X"));
      Assert.assertFalse(this.doubleMap.containsKey("B", "Y"));
      Assert.assertFalse(this.doubleMap.containsKey("B", "Z"));

      Assert.assertTrue(this.doubleMap.containsKey("C", "A"));
      Assert.assertTrue(this.doubleMap.containsKey("C", "B"));
      Assert.assertTrue(this.doubleMap.containsKey("C", "C"));

      Assert.assertFalse(this.doubleMap.containsKey("C", "X"));
      Assert.assertFalse(this.doubleMap.containsKey("C", "Y"));
      Assert.assertFalse(this.doubleMap.containsKey("C", "Z"));

      Assert.assertFalse(this.doubleMap.containsKey("X", "X"));
      Assert.assertFalse(this.doubleMap.containsKey("Y", "Y"));
      Assert.assertFalse(this.doubleMap.containsKey("Z", "Z"));
   }

   @Test
   public void testGetPrimary() {
      var secondaryMappingsOptional = this.doubleMap.get("A");

      Assert.assertTrue(secondaryMappingsOptional.isPresent());

      var secondaryMappings = secondaryMappingsOptional.get();

      Assert.assertEquals(3, secondaryMappings.size());
      Assert.assertEquals(secondaryMappings.get("A"), "VALUE (A,A)");
      Assert.assertEquals(secondaryMappings.get("B"), "VALUE (A,B)");
      Assert.assertEquals(secondaryMappings.get("C"), "VALUE (A,C)");

      secondaryMappingsOptional = this.doubleMap.get("B");

      Assert.assertTrue(secondaryMappingsOptional.isPresent());

      secondaryMappings = secondaryMappingsOptional.get();

      Assert.assertEquals(3, secondaryMappings.size());
      Assert.assertEquals(secondaryMappings.get("A"), "VALUE (B,A)");
      Assert.assertEquals(secondaryMappings.get("B"), "VALUE (B,B)");
      Assert.assertEquals(secondaryMappings.get("C"), "VALUE (B,C)");

      secondaryMappingsOptional = this.doubleMap.get("C");

      Assert.assertTrue(secondaryMappingsOptional.isPresent());

      secondaryMappings = secondaryMappingsOptional.get();

      Assert.assertEquals(3, secondaryMappings.size());
      Assert.assertEquals(secondaryMappings.get("A"), "VALUE (C,A)");
      Assert.assertEquals(secondaryMappings.get("B"), "VALUE (C,B)");
      Assert.assertEquals(secondaryMappings.get("C"), "VALUE (C,C)");

      secondaryMappingsOptional = this.doubleMap.get("X");

      Assert.assertTrue(secondaryMappingsOptional.isEmpty());

      secondaryMappingsOptional = this.doubleMap.get("Y");

      Assert.assertTrue(secondaryMappingsOptional.isEmpty());

      secondaryMappingsOptional = this.doubleMap.get("Z");

      Assert.assertTrue(secondaryMappingsOptional.isEmpty());
   }

   @Test
   public void testGetPrimarySecondary() {
      var valueOptional = this.doubleMap.get("A", "A");

      Assert.assertTrue(valueOptional.isPresent());

      var value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (A,A)");

      valueOptional = this.doubleMap.get("A", "B");

      Assert.assertTrue(valueOptional.isPresent());

      value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (A,B)");

      valueOptional = this.doubleMap.get("A", "C");

      Assert.assertTrue(valueOptional.isPresent());

      value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (A,C)");

      valueOptional = this.doubleMap.get("B", "A");

      Assert.assertTrue(valueOptional.isPresent());

      value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (B,A)");

      valueOptional = this.doubleMap.get("B", "B");

      Assert.assertTrue(valueOptional.isPresent());

      value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (B,B)");

      valueOptional = this.doubleMap.get("B", "C");

      Assert.assertTrue(valueOptional.isPresent());

      value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (B,C)");

      valueOptional = this.doubleMap.get("C", "A");

      Assert.assertTrue(valueOptional.isPresent());

      value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (C,A)");

      valueOptional = this.doubleMap.get("C", "B");

      Assert.assertTrue(valueOptional.isPresent());

      value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (C,B)");

      valueOptional = this.doubleMap.get("C", "C");

      Assert.assertTrue(valueOptional.isPresent());

      value = valueOptional.get();

      Assert.assertEquals(value, "VALUE (C,C)");

      valueOptional = this.doubleMap.get("X", "X");

      Assert.assertTrue(valueOptional.isEmpty());

      valueOptional = this.doubleMap.get("Y", "Y");

      Assert.assertTrue(valueOptional.isEmpty());

      valueOptional = this.doubleMap.get("Z", "Z");

      Assert.assertTrue(valueOptional.isEmpty());
   }

   @Test
   public void testKeySetPrimary() {
      var keySet = this.doubleMap.keySet();

      Assert.assertTrue(keySet.contains("A"));
      Assert.assertTrue(keySet.contains("B"));
      Assert.assertTrue(keySet.contains("C"));

      Assert.assertFalse(keySet.contains("X"));
      Assert.assertFalse(keySet.contains("Y"));
      Assert.assertFalse(keySet.contains("Z"));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetPrimaryAdd() {
      var keySet = this.doubleMap.keySet();

      keySet.add("X");
   }

   @Test
   public void testKeySetPrimaryMapPut() {
      var keySet = this.doubleMap.keySet();

      this.doubleMap.put("D", "A", "VALUE (D,A)");

      Assert.assertTrue(keySet.contains("D"));
   }

   @Test
   public void testKeySetPrimaryRemove() {
      var keySet = this.doubleMap.keySet();

      keySet.remove("A");

      Assert.assertFalse(this.doubleMap.containsKey("A"));
      Assert.assertFalse(this.doubleMap.containsKey("A", "A"));
      Assert.assertFalse(this.doubleMap.containsKey("A", "B"));
      Assert.assertFalse(this.doubleMap.containsKey("A", "C"));

      Assert.assertTrue(this.doubleMap.containsKey("B"));
      Assert.assertTrue(this.doubleMap.containsKey("B", "A"));
      Assert.assertTrue(this.doubleMap.containsKey("B", "B"));
      Assert.assertTrue(this.doubleMap.containsKey("B", "C"));

      Assert.assertTrue(this.doubleMap.containsKey("C"));
      Assert.assertTrue(this.doubleMap.containsKey("C", "A"));
      Assert.assertTrue(this.doubleMap.containsKey("C", "B"));
      Assert.assertTrue(this.doubleMap.containsKey("C", "C"));
   }

   @Test
   public void testKeySetSecondary() {
      var keySetOptional = this.doubleMap.keySet("A");

      Assert.assertTrue(keySetOptional.isPresent());

      var keySet = keySetOptional.get();

      Assert.assertTrue(keySet.contains("A"));
      Assert.assertTrue(keySet.contains("B"));
      Assert.assertTrue(keySet.contains("C"));

      Assert.assertFalse(keySet.contains("X"));
      Assert.assertFalse(keySet.contains("Y"));
      Assert.assertFalse(keySet.contains("Z"));

      keySetOptional = this.doubleMap.keySet("B");

      Assert.assertTrue(keySetOptional.isPresent());

      keySet = keySetOptional.get();

      Assert.assertTrue(keySet.contains("A"));
      Assert.assertTrue(keySet.contains("B"));
      Assert.assertTrue(keySet.contains("C"));

      Assert.assertFalse(keySet.contains("X"));
      Assert.assertFalse(keySet.contains("Y"));
      Assert.assertFalse(keySet.contains("Z"));

      keySetOptional = this.doubleMap.keySet("C");

      Assert.assertTrue(keySetOptional.isPresent());

      keySet = keySetOptional.get();

      Assert.assertTrue(keySet.contains("A"));
      Assert.assertTrue(keySet.contains("B"));
      Assert.assertTrue(keySet.contains("C"));

      Assert.assertFalse(keySet.contains("X"));
      Assert.assertFalse(keySet.contains("Y"));
      Assert.assertFalse(keySet.contains("Z"));

      keySetOptional = this.doubleMap.keySet("X");

      Assert.assertTrue(keySetOptional.isEmpty());

      keySetOptional = this.doubleMap.keySet("Y");

      Assert.assertTrue(keySetOptional.isEmpty());

      keySetOptional = this.doubleMap.keySet("Z");

      Assert.assertTrue(keySetOptional.isEmpty());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetSecondaryAdd() {
      var keySetOptional = this.doubleMap.keySet("A");

      Assert.assertTrue(keySetOptional.isPresent());

      var keySet = keySetOptional.get();

      keySet.add("X");
   }

   @Test
   public void testKeySetSecondaryMapPut() {
      var keySetOptional = this.doubleMap.keySet("A");

      Assert.assertTrue(keySetOptional.isPresent());

      var keySet = keySetOptional.get();

      this.doubleMap.put("A", "D", "VALUE (A,D)");

      Assert.assertTrue(keySet.contains("D"));
   }

   @Test
   public void testKeySetSecondaryRemove() {
      var keySetOptional = this.doubleMap.keySet("A");

      Assert.assertTrue(keySetOptional.isPresent());

      var keySet = keySetOptional.get();

      keySet.remove("A");

      Assert.assertFalse(this.doubleMap.containsKey("A", "A"));
   }

   @Test
   public void testPut() {
      var priorValueOptional = this.doubleMap.put("D", "A", "VALUE (D,A)");

      Assert.assertTrue(priorValueOptional.isEmpty());

      priorValueOptional = this.doubleMap.put("A", "A", "VALUE (A,A) --> 2");

      Assert.assertTrue(priorValueOptional.isPresent());

      var prirorValue = priorValueOptional.get();

      Assert.assertEquals("VALUE (A,A)", prirorValue);
   }

   @Test
   public void testSizeTotal() {
      Assert.assertEquals(9, this.doubleMap.size());
   }

   @Test
   public void testSizeSecondary() {
      var sizeOptional = this.doubleMap.size("A");

      Assert.assertTrue(sizeOptional.isPresent());

      var size = sizeOptional.get();

      Assert.assertEquals(Integer.valueOf(3), size);

      sizeOptional = this.doubleMap.size("B");

      Assert.assertTrue(sizeOptional.isPresent());

      size = sizeOptional.get();

      Assert.assertEquals(Integer.valueOf(3), size);

      sizeOptional = this.doubleMap.size("C");

      Assert.assertTrue(sizeOptional.isPresent());

      size = sizeOptional.get();

      Assert.assertEquals(Integer.valueOf(3), size);

      sizeOptional = this.doubleMap.size("X");

      Assert.assertTrue(sizeOptional.isEmpty());

      sizeOptional = this.doubleMap.size("Y");

      Assert.assertTrue(sizeOptional.isEmpty());

      sizeOptional = this.doubleMap.size("Z");

      Assert.assertTrue(sizeOptional.isEmpty());
   }

   @Test
   public void testValuesAll() {
      var values = this.doubleMap.values();

      Assert.assertTrue(values.contains("VALUE (A,A)"));
      Assert.assertTrue(values.contains("VALUE (A,B)"));
      Assert.assertTrue(values.contains("VALUE (A,C)"));
      Assert.assertTrue(values.contains("VALUE (B,A)"));
      Assert.assertTrue(values.contains("VALUE (B,B)"));
      Assert.assertTrue(values.contains("VALUE (B,C)"));
      Assert.assertTrue(values.contains("VALUE (C,A)"));
      Assert.assertTrue(values.contains("VALUE (C,B)"));
      Assert.assertTrue(values.contains("VALUE (C,C)"));

      Assert.assertFalse(values.contains("VALUE (X,X)"));
      Assert.assertFalse(values.contains("VALUE (Y,Y)"));
      Assert.assertFalse(values.contains("VALUE (Z,Z)"));
   }

   @Test
   public void testValuesSecondary() {
      var valuesOptional = this.doubleMap.values("A");

      Assert.assertTrue(valuesOptional.isPresent());

      var values = valuesOptional.get();

      Assert.assertTrue(values.contains("VALUE (A,A)"));
      Assert.assertTrue(values.contains("VALUE (A,B)"));
      Assert.assertTrue(values.contains("VALUE (A,C)"));

      valuesOptional = this.doubleMap.values("B");

      Assert.assertTrue(valuesOptional.isPresent());

      values = valuesOptional.get();

      Assert.assertTrue(values.contains("VALUE (B,A)"));
      Assert.assertTrue(values.contains("VALUE (B,B)"));
      Assert.assertTrue(values.contains("VALUE (B,C)"));

      valuesOptional = this.doubleMap.values("C");

      Assert.assertTrue(valuesOptional.isPresent());

      values = valuesOptional.get();

      Assert.assertTrue(values.contains("VALUE (C,A)"));
      Assert.assertTrue(values.contains("VALUE (C,B)"));
      Assert.assertTrue(values.contains("VALUE (C,C)"));

      valuesOptional = this.doubleMap.values("X");

      Assert.assertTrue(valuesOptional.isEmpty());

      valuesOptional = this.doubleMap.values("Y");

      Assert.assertTrue(valuesOptional.isEmpty());

      valuesOptional = this.doubleMap.values("Z");

      Assert.assertTrue(valuesOptional.isEmpty());
   }

}

/* EOF */
