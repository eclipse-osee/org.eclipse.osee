/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link StringRange}.
 *
 * @author Loren K. Ashley
 */

public class StringRangeTest {

   @Test
   public void basic() {
      var sr = new StringRange(3, 7);
      Assert.assertEquals(3, sr.start());
      Assert.assertEquals(7, sr.end());
      Assert.assertEquals(4, sr.length());
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void nonNegativeRange() {
      @SuppressWarnings("unused")
      var sr = new StringRange(3, 2, StringRange.NONNEGATIVE_RANGE);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void nonNegativeEndpointsStart() {
      @SuppressWarnings("unused")
      var sr = new StringRange(-3, 7, StringRange.NONNEGATIVE_ENDPOINTS);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void nonNegativeEndpointsEnd() {
      @SuppressWarnings("unused")
      var sr = new StringRange(3, -7, StringRange.NONNEGATIVE_ENDPOINTS);
   }

   @Test
   public void absLength() {
      var sr = new StringRange(2, 14);

      Assert.assertEquals(12, sr.absLength());
   }

   @Test
   public void absLengthNegativeRange() {
      var sr = new StringRange(14, 2);

      Assert.assertEquals(12, sr.absLength());
   }

   @Test
   public void addOffset() {

      var sr = new StringRange(2, 14);

      var sro = sr.addOffset(3);

      Assert.assertEquals(5, sro.start());
      Assert.assertEquals(17, sro.end());
   }

   @Test
   public void addOffsetNegativeRange() {

      var sr = new StringRange(14, 2);

      var sro = sr.addOffset(3);

      Assert.assertEquals(17, sro.start());
      Assert.assertEquals(5, sro.end());
   }

   @Test
   public void baseIndex() {

      var sr = new StringRange(2, 14);

      Assert.assertEquals(2, sr.baseIndex(0));
      Assert.assertEquals(3, sr.baseIndex(1));
      Assert.assertEquals(12, sr.baseIndex(10));
      Assert.assertEquals(13, sr.baseIndex(11));
   }

   @Test
   public void baseIndexNegativeRange() {

      var sr = new StringRange(14, 2);

      Assert.assertEquals(13, sr.baseIndex(0));
      Assert.assertEquals(12, sr.baseIndex(1));
      Assert.assertEquals(3, sr.baseIndex(10));
      Assert.assertEquals(2, sr.baseIndex(11));
   }

   @Test
   public void end() {

      var sr = new StringRange(2, 12);

      Assert.assertEquals(12, sr.end());
   }

   @Test
   public void endNegativeRange() {

      var sr = new StringRange(12, 2);

      Assert.assertEquals(2, sr.end());
   }

   @Test
   public void equal() {
      var sra = new StringRange(3, 7);
      var srb = new StringRange(3, 7);

      Assert.assertFalse(sra == srb);
      Assert.assertTrue(sra.equals(srb));
      Assert.assertTrue(srb.equals(sra));
   }

   @Test
   public void equalNegativeRange() {
      var sra = new StringRange(7, 3);
      var srb = new StringRange(7, 3);

      Assert.assertFalse(sra == srb);
      Assert.assertTrue(sra.equals(srb));
      Assert.assertTrue(srb.equals(sra));
   }

   @Test
   public void notEqualStart() {
      var sra = new StringRange(3, 7);
      var srb = new StringRange(4, 7);

      Assert.assertFalse(sra == srb);
      Assert.assertFalse(sra.equals(srb));
      Assert.assertFalse(srb.equals(sra));
   }

   @Test
   public void notEqualStartNegativeRange() {
      var sra = new StringRange(7, 3);
      var srb = new StringRange(8, 3);

      Assert.assertFalse(sra == srb);
      Assert.assertFalse(sra.equals(srb));
      Assert.assertFalse(srb.equals(sra));
   }

   @Test
   public void notEqualEnd() {
      var sra = new StringRange(3, 7);
      var srb = new StringRange(3, 8);

      Assert.assertFalse(sra == srb);
      Assert.assertFalse(sra.equals(srb));
      Assert.assertFalse(srb.equals(sra));
   }

   @Test
   public void notEqualEndNegativeRange() {
      var sra = new StringRange(7, 3);
      var srb = new StringRange(7, 2);

      Assert.assertFalse(sra == srb);
      Assert.assertFalse(sra.equals(srb));
      Assert.assertFalse(srb.equals(sra));
   }

   @Test
   public void notEqualStartEnd() {
      var sra = new StringRange(3, 7);
      var srb = new StringRange(4, 8);

      Assert.assertFalse(sra == srb);
      Assert.assertFalse(sra.equals(srb));
      Assert.assertFalse(srb.equals(sra));
   }

   @Test
   public void notEqualStartEndNegativeRange() {
      var sra = new StringRange(7, 3);
      var srb = new StringRange(8, 4);

      Assert.assertFalse(sra == srb);
      Assert.assertFalse(sra.equals(srb));
      Assert.assertFalse(srb.equals(sra));
   }

   @Test
   public void notEqualSomethingElse() {
      var sr = new StringRange(3, 7);
      var se = new String("abc");

      Assert.assertNotEquals(sr, se);
      Assert.assertFalse(sr.equals(se));
   }

   @Test
   public void notEqualNull() {
      var sr = new StringRange(3, 7);

      Assert.assertFalse(sr.equals((StringRange) null));
   }

   @Test
   public void inRangeNonNegative() {
      var sr = new StringRange(3, 7);

      Assert.assertFalse(sr.isInRange(2));
      Assert.assertTrue(sr.isInRange(3));
      Assert.assertTrue(sr.isInRange(4));
      Assert.assertTrue(sr.isInRange(5));
      Assert.assertTrue(sr.isInRange(6));
      Assert.assertFalse(sr.isInRange(7));
   }

   @Test
   public void inRangeNegative() {

      var sr = new StringRange(7, 3);

      Assert.assertFalse(sr.isInRange(2));
      Assert.assertTrue(sr.isInRange(3));
      Assert.assertTrue(sr.isInRange(4));
      Assert.assertTrue(sr.isInRange(5));
      Assert.assertTrue(sr.isInRange(6));
      Assert.assertFalse(sr.isInRange(7));
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void requiredInRangeStart() {
      var sr = new StringRange(3, 7);

      sr.requireInRange(2);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void requiredInRangeEnd() {
      var sr = new StringRange(3, 7);

      sr.requireInRange(7);
   }

   @Test
   public void inRangeNonNegativeNonNegative() {

      var sr = new StringRange(3, 7);

      var cra = new StringRange(4, 6);
      var crb = new StringRange(3, 7);
      var crc = new StringRange(2, 6);
      var crd = new StringRange(4, 8);

      Assert.assertTrue(sr.isInRange(cra));
      Assert.assertTrue(sr.isInRange(crb));
      Assert.assertFalse(sr.isInRange(crc));
      Assert.assertFalse(sr.isInRange(crd));
   }

   @Test
   public void inRangeNonNegativeNegative() {

      var sr = new StringRange(3, 7);

      var cra = new StringRange(6, 4);
      var crb = new StringRange(2, 8);

      Assert.assertTrue(sr.isInRange(cra));
      Assert.assertFalse(sr.isInRange(crb));
   }

   @Test
   public void inRangeNegativeNonNegative() {

      var sr = new StringRange(7, 3);

      var cra = new StringRange(4, 6);
      var crb = new StringRange(3, 7);
      var crc = new StringRange(0, 3);
      var crd = new StringRange(0, 4);
      var cre = new StringRange(7, 9);
      var crf = new StringRange(6, 9);

      Assert.assertTrue(sr.isInRange(cra));
      Assert.assertTrue(sr.isInRange(crb));
      Assert.assertFalse(sr.isInRange(crc));
      Assert.assertFalse(sr.isInRange(crd));
      Assert.assertFalse(sr.isInRange(cre));
      Assert.assertFalse(sr.isInRange(crf));
   }

   @Test
   public void inRangeNegativeNegative() {

      var sr = new StringRange(7, 3);

      var cra = new StringRange(8, 2);
      var crb = new StringRange(7, 3);
      var crc = new StringRange(6, 3);
      var crd = new StringRange(7, 4);

      Assert.assertFalse(sr.isInRange(cra));
      Assert.assertTrue(sr.isInRange(crb));
      Assert.assertTrue(sr.isInRange(crc));
      Assert.assertTrue(sr.isInRange(crd));
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void requiredInRangeNonNegativeNonNegativeStart() {
      var sr = new StringRange(3, 7);
      var cr = new StringRange(2, 7);

      sr.requireInRange(cr);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void requiredInRangeNonNegativeNonNegativeEnd() {
      var sr = new StringRange(3, 7);
      var cr = new StringRange(3, 8);

      sr.requireInRange(cr);
   }

   @Test
   public void isPositive() {

      var sr = new StringRange(2, 12);

      Assert.assertTrue(sr.isPositive());
   }

   @Test
   public void isPositiveNegativeRange() {

      var sr = new StringRange(12, 2);

      Assert.assertFalse(sr.isPositive());
   }

   @Test
   public void length() {

      var sr = new StringRange(2, 12);

      Assert.assertEquals(10, sr.length());
   }

   @Test
   public void lengthNegativeRange() {

      var sr = new StringRange(12, 2);

      Assert.assertEquals(-10, sr.length());
   }

   @Test
   public void start() {

      var sr = new StringRange(2, 12);

      Assert.assertEquals(2, sr.start());
   }

   @Test
   public void startNegativeRange() {

      var sr = new StringRange(12, 2);

      Assert.assertEquals(12, sr.start());
   }

   //
   //                     1 1 1 1 1
   // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
   //     |-----------------| X
   //         |-----| X
   //

   @Test
   public void subRangePrPsr() {

      var sr = new StringRange(2, 12);

      var sra = sr.subRange(2, 6);

      Assert.assertEquals(4, sra.baseIndex(0));
      Assert.assertEquals(7, sra.baseIndex(3));
   }

   //
   //                     1 1 1 1 1
   // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
   //     |-----------------| X
   //         |-----| X
   //

   @Test
   public void subRangePrNsr() {

      var sr = new StringRange(2, 12);

      var sra = sr.subRange(6, 2);

      Assert.assertEquals(7, sra.baseIndex(0));
      Assert.assertEquals(4, sra.baseIndex(3));
   }

   //
   //                     1 1 1 1 1
   // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
   //     |-----------------| X
   //             |-----| X

   @Test
   public void subRangeNrPsr() {

      var sr = new StringRange(12, 2);

      var sra = sr.subRange(2, 6);

      Assert.assertEquals(9, sra.baseIndex(0));
      Assert.assertEquals(6, sra.baseIndex(3));
   }

   //
   //                     1 1 1 1 1
   // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
   //     |-----------------| X
   //             |-----| X

   @Test
   public void subRangeNrNsr() {

      var sr = new StringRange(12, 2);

      var sra = sr.subRange(6, 2);

      Assert.assertEquals(6, sra.baseIndex(0));
      Assert.assertEquals(9, sra.baseIndex(3));
   }

}

/* EOF */
