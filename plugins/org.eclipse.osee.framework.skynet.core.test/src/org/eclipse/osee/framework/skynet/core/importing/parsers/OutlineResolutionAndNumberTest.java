/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.importing.ReqNumbering;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Karol M. Wilk
 */
public final class OutlineResolutionAndNumberTest {

   private static final String LAST_OUTLINE_NUMBER = "3.1.1.2.1.7"; /* realistic outline number */
   private final OutlineResolution outlineResolution = new OutlineResolution();

   @Test
   public void testLowerOutlineNumbers() {
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.1.1.2.1.6", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.0", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.1.1.1", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.1.13.1", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.1.0", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.9", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.10", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.1", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("1.8", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("0.15", LAST_OUTLINE_NUMBER));
   }

   @Test
   public void testHigherOutlineNumbers() {
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("3.1.1.2.1.8", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.2.1.3", LAST_OUTLINE_NUMBER));
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("4.0", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("4.2", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("4.2.5.10", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.1.1.4.1.1", LAST_OUTLINE_NUMBER));
   }

   @Test
   public void testZeroBasedOutlineNumbers() {
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("3.2.1.0.1", "3.2.1"));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.2.0.0.1", "3.2.1"));
   }

   @Test
   public void testRandomOutlineNumbers() {
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("6.5", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("0.25", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("10.48", LAST_OUTLINE_NUMBER));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("200.12.3", LAST_OUTLINE_NUMBER));
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("4.0", "3.1"));
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("2.1.1.1.2", "2.1.1.1.1"));
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("2.1.1.1.1.1", "2.1.1.1.1"));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("2.1.1.1.1.2", "2.1.1.1.1"));
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("3.0", "2.1.1.1.1"));
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("2.2", "2.1.1.1.1"));
      Assert.assertFalse(outlineResolution.isInvalidOutlineNumber("2.1.2", "2.1.1.1.1"));
      Assert.assertTrue(outlineResolution.isInvalidOutlineNumber("3.2", "2.1.1.1.1"));
   }

   @Test
   public void testNextSetGeneration() {
      Set<String> nextPossibleSet = outlineResolution.generateNextSet(new ReqNumbering(LAST_OUTLINE_NUMBER));
      Set<String> expected = new HashSet<>(Arrays.asList("3.2", "3.1.2", "3.1.1.3", "3.1.1.2.2", "3.1.1.2.1.8",
         "4.0", "3.1.1.2.1.7.1", "3.1.1.2.1.7.0.1"));
      addDotEndingSet(expected);
      expected.removeAll(nextPossibleSet);
      Assert.assertTrue(expected.isEmpty());
   }

   /**
    * <p>
    * Generate next numbers with ending "." by default.
    *
    * <pre>
    * input(current number)         output(generated set)
    * 1.                      ->    [1.1, 1.1., 1.0.1., 1.0.1, 2.0, 2.0.]
    * 3.                      ->    [3.0.1, 3.0.1., 3.1, 3.1., 4.0, 4.0.]
    * </pre>
    * </p>
    */
   @Test
   public void testNextSetGeneration_NonZeroBased() {
      Set<String> nextPossibleSet = outlineResolution.generateNextSet(new ReqNumbering("1."));
      Set<String> expected = new HashSet<>(Arrays.asList("2.0", "1.1", "1.0.1"));
      addDotEndingSet(expected);
      expected.removeAll(nextPossibleSet);
      Assert.assertTrue(expected.isEmpty());
   }

   @Test
   public void testNextSetGeneration_ZeroBased() {
      Set<String> nextPossibleSet = outlineResolution.generateNextSet(new ReqNumbering("1.0"));
      Set<String> expected = new HashSet<>(Arrays.asList("2.0", "1.1", "1.0.1"));
      addDotEndingSet(expected);
      expected.removeAll(nextPossibleSet);
      Assert.assertTrue(expected.isEmpty());
   }

   @Test
   public void testNextSetGeneration_ZeroExtendedBased() {
      Set<String> nextPossibleSet = outlineResolution.generateNextSet(new ReqNumbering("1.0.1"));
      Set<String> expected = new HashSet<>(Arrays.asList("2.0", "1.1", "1.0.2"));
      addDotEndingSet(expected);
      expected.removeAll(nextPossibleSet);
      Assert.assertTrue(expected.isEmpty());
   }

   @Test
   public void testNextSetGeneration_DoubleDigitZeroBased() {
      Set<String> nextPossibleSet = outlineResolution.generateNextSet(new ReqNumbering("1.0.10"));
      Set<String> expected = new HashSet<>(Arrays.asList("2.0", "1.1", "1.0.11", "1.0.10.1", "1.0.10.0.1"));
      addDotEndingSet(expected);
      expected.removeAll(nextPossibleSet);
      Assert.assertTrue(expected.isEmpty());
   }

   @Test
   public void testNextSetGeneration_DoubleDigitHigherBased() {
      Set<String> nextPossibleSet = outlineResolution.generateNextSet(new ReqNumbering("1.23"));
      Set<String> expected = new HashSet<>(Arrays.asList("2.0", "1.24", "1.23.1", "1.23.0.1"));
      addDotEndingSet(expected);
      expected.removeAll(nextPossibleSet);
      Assert.assertTrue(expected.isEmpty());
   }

   @Test
   public void testNextSetGeneration_SubItems() {
      String current = "1.23.1.1";
      Set<String> nextPossibleSet = outlineResolution.generateNextSet(new ReqNumbering("1.23"));
      Assert.assertFalse(nextPossibleSet.contains(current));

      boolean larger_NotGenerated_CorrectInvalid = outlineResolution.isInvalidOutlineNumber(current, "1.23");
      Assert.assertFalse(larger_NotGenerated_CorrectInvalid);

      larger_NotGenerated_CorrectInvalid = outlineResolution.isInvalidOutlineNumber("1.23.1.10", "1.23");
      Assert.assertFalse(larger_NotGenerated_CorrectInvalid);

      larger_NotGenerated_CorrectInvalid = outlineResolution.isInvalidOutlineNumber("1.23.1.0.0.0.1", "1.23");
      Assert.assertFalse(larger_NotGenerated_CorrectInvalid);
   }

   private void addDotEndingSet(Set<String> inputSet) {
      Set<String> dotEnding = new HashSet<>(inputSet.size());
      for (String item : inputSet) {
         dotEnding.add(item + ".");
      }
      inputSet.addAll(dotEnding);
   }
}
