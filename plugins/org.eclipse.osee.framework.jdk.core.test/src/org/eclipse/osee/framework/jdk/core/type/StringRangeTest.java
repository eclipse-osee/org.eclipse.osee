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
      var sr = new StringRange(3, 2, StringRange.NonNegativeRange);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void nonNegativeEndpointsStart() {
      @SuppressWarnings("unused")
      var sr = new StringRange(-3, 7, StringRange.NonNegativeEndpoints);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void nonNegativeEndpointsEnd() {
      @SuppressWarnings("unused")
      var sr = new StringRange(3, -7, StringRange.NonNegativeEndpoints);
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
   public void notEqualStart() {
      var sra = new StringRange(3, 7);
      var srb = new StringRange(4, 7);

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
   public void notEqualStartEnd() {
      var sra = new StringRange(3, 7);
      var srb = new StringRange(4, 8);

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

      Assert.assertTrue(sr.isInRange(2));
      Assert.assertFalse(sr.isInRange(3));
      Assert.assertFalse(sr.isInRange(4));
      Assert.assertFalse(sr.isInRange(5));
      Assert.assertFalse(sr.isInRange(6));
      Assert.assertTrue(sr.isInRange(7));
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

      Assert.assertFalse(sr.isInRange(cra));
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

      Assert.assertFalse(sr.isInRange(cra));
      Assert.assertFalse(sr.isInRange(crb));
      Assert.assertTrue(sr.isInRange(crc));
      Assert.assertFalse(sr.isInRange(crd));
      Assert.assertTrue(sr.isInRange(cre));
      Assert.assertFalse(sr.isInRange(crf));
   }

   @Test
   public void inRangeNegativeNegative() {
      var sr = new StringRange(7, 3);
      var cra = new StringRange(8, 2);
      var crb = new StringRange(7, 3);
      var crc = new StringRange(6, 3);
      var crd = new StringRange(7, 4);

      Assert.assertTrue(sr.isInRange(cra));
      Assert.assertTrue(sr.isInRange(crb));
      Assert.assertFalse(sr.isInRange(crc));
      Assert.assertFalse(sr.isInRange(crd));
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

}

/* EOF */
