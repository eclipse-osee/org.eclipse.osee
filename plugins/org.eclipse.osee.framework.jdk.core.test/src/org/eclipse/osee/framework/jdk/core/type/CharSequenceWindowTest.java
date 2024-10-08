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

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * JUnit tests for {@link CharSequenceWindow}
 *
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class CharSequenceWindowTest {

   /**
    * All tests are based on this sequence of characters.
    */

   private static String testString = "abcdefghijklmnopqrstuvwxyz";

   /**
    * A {@link List} of arrays with the test parameters for each iteration of the {@link CharSequenceTest} test suite.
    * Each array contains a single entry which is a {@link Supplier} that provides the character sequence backing for
    * the {@link CharSequenceWindow} implementations for that iteration of the test suite.
    *
    * @return {@link List} of test parameters for each iteration of the {@link CharSequeceTest} test suite.
    */

   @Parameters
   public static Collection<Object[]> data() {
      //@formatter:off
      return
         List.of
            (
               (Object[]) new Supplier[] { () -> CharSequenceWindowTest.testString },
               (Object[]) new Supplier[] { () -> new StringBuilder().append( CharSequenceWindowTest.testString ) },
               (Object[]) new Supplier[] { () -> new StringBuffer().append( CharSequenceWindowTest.testString ) },
               (Object[]) new Supplier[] { () -> new CharSequenceWindow( CharSequenceWindowTest.testString, 0, CharSequenceWindowTest.testString.length() ) }
            );
      //@formatter:on
   }

   private final Supplier<CharSequence> testCharSequenceSupplier;

   public CharSequenceWindowTest(Supplier<CharSequence> testCharSequenceSupplier) {
      this.testCharSequenceSupplier = testCharSequenceSupplier;
   }

   @Test
   public void abc() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 0, 3);

      Assert.assertEquals(3, sw.length());
      Assert.assertEquals('a', sw.charAt(0));
      Assert.assertEquals('b', sw.charAt(1));
      Assert.assertEquals('c', sw.charAt(2));
   }

   @Test
   public void stu() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 18, 21);

      Assert.assertEquals(3, sw.length());
      Assert.assertEquals('s', sw.charAt(0));
      Assert.assertEquals('t', sw.charAt(1));
      Assert.assertEquals('u', sw.charAt(2));
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void abcBefore() {
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 0, 3);
      sw.charAt(-1);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void abcAfter() {
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 0, 3);
      sw.charAt(3);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void stuBefore() {
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 18, 21);
      sw.charAt(17);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void stuAfter() {
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 18, 21);
      sw.charAt(21);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void negativeStart() {
      @SuppressWarnings("unused")
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), -3, 3);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void negativeEnd() {
      @SuppressWarnings("unused")
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 3, -3);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void subSequenceEnd() {
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 3, 9);

      Assert.assertEquals("defghi", sw.toString());

      @SuppressWarnings("unused")
      var ss = sw.subSequence(2, 7);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void subSequenceStart() {
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 3, 9);

      Assert.assertEquals("defghi", sw.toString());

      @SuppressWarnings("unused")
      var ss = sw.subSequence(-1, 6);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void subSequenceNegativeRange() {
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 3, 9);

      Assert.assertEquals("defghi", sw.toString());

      @SuppressWarnings("unused")
      var ss = sw.subSequence(0, 7);
   }

   @Test
   public void rang0x3() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 0, 3);

      Assert.assertEquals(3, sw.length());
      Assert.assertEquals("abc", sw.toString());
   }

   @Test
   public void negativeRange3x0() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 3, 0);

      Assert.assertEquals(3, sw.length());
      Assert.assertEquals("cba", sw.toString());
   }

   @Test
   public void range23x26() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 23, 26);

      Assert.assertEquals(3, sw.length());
      Assert.assertEquals("xyz", sw.toString());
   }

   @Test
   public void negativeRange26x23() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 26, 23);

      Assert.assertEquals(3, sw.length());
      Assert.assertEquals("zyx", sw.toString());
   }

   @Test
   public void range3x9SubSequence2x4() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 3, 9);

      Assert.assertEquals(6, sw.length());
      Assert.assertEquals("defghi", sw.toString());

      var ss = sw.subSequence(2, 4);

      Assert.assertEquals(2, ss.length());
      Assert.assertEquals("fg", ss.toString());
   }

   @Test
   public void range3x9NegativeSubSequence4x2() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 3, 9);

      Assert.assertEquals(6, sw.length());
      Assert.assertEquals("defghi", sw.toString());

      var ss = sw.subSequence(4, 2);

      Assert.assertEquals(2, ss.length());
      Assert.assertEquals("gf", ss.toString());
   }

   @Test
   public void negativeRange9x3SubSequence2x4() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 9, 3);

      Assert.assertEquals(6, sw.length());
      Assert.assertEquals("ihgfed", sw.toString());

      var ss = sw.subSequence(2, 4);

      Assert.assertEquals(2, ss.length());
      Assert.assertEquals("gf", ss.toString());
   }

   @Test
   public void negativeRange9x3NegativeSubSequence4x2() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 9, 3);

      Assert.assertEquals(6, sw.length());
      Assert.assertEquals("ihgfed", sw.toString());

      var ss = sw.subSequence(4, 2);

      Assert.assertEquals(2, ss.length());
      Assert.assertEquals("fg", ss.toString());
   }

   @Test
   public void subSequence3x9CharAt() {
      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 3, 9);

      Assert.assertEquals(6, sw.length());
      Assert.assertEquals("defghi", sw.toString());

      var c = sw.charAt(0);
      Assert.assertEquals('d', c);

      c = sw.charAt(1);
      Assert.assertEquals('e', c);

      c = sw.charAt(2);
      Assert.assertEquals('f', c);

      c = sw.charAt(3);
      Assert.assertEquals('g', c);

      c = sw.charAt(4);
      Assert.assertEquals('h', c);

      c = sw.charAt(5);
      Assert.assertEquals('i', c);
   }

   @Test
   public void subSequenceNegativeRange9x3CharAt() {

      var sw = new CharSequenceWindow(this.testCharSequenceSupplier.get(), 9, 3);

      Assert.assertEquals(6, sw.length());
      Assert.assertEquals("ihgfed", sw.toString());

      var c = sw.charAt(0);
      Assert.assertEquals('i', c);

      c = sw.charAt(1);
      Assert.assertEquals('h', c);

      c = sw.charAt(2);
      Assert.assertEquals('g', c);

      c = sw.charAt(3);
      Assert.assertEquals('f', c);

      c = sw.charAt(4);
      Assert.assertEquals('e', c);

      c = sw.charAt(5);
      Assert.assertEquals('d', c);
   }

}

/* EOF */
