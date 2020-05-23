/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Arrays;
import org.junit.Assert;

/**
 * {@link Strings}
 *
 * @author Donald G. Dunne
 */
public class StringsTest {

   @org.junit.Test
   public void testTruncate() {
      String name = "Now is the time forall good men";
      Assert.assertEquals(31, name.length());

      Assert.assertEquals(20, Strings.truncate(name, 20).length());
      String withDots = Strings.truncate(name, 20, true);

      Assert.assertEquals(20, Strings.truncate(withDots, 20).length());
      Assert.assertEquals("Now is the time f...", withDots);
   }

   @org.junit.Test
   public void testUnQuote() {
      String actual = Strings.unquote(null);
      Assert.assertNull(actual);

      actual = Strings.unquote(Strings.EMPTY_STRING);
      Assert.assertEquals(Strings.EMPTY_STRING, actual);

      actual = Strings.unquote("hello");
      Assert.assertEquals("hello", actual);

      actual = Strings.unquote("\"hello\"");
      Assert.assertEquals("hello", actual);
   }

   @org.junit.Test
   public void testQuote() {
      String actual = Strings.quote(null);
      Assert.assertNull(actual);

      actual = Strings.quote(Strings.EMPTY_STRING);
      Assert.assertEquals(Strings.EMPTY_STRING, actual);

      actual = Strings.quote("hello");
      Assert.assertEquals("\"hello\"", actual);
   }

   @org.junit.Test
   public void testSafeReplace() {
      String actual = Strings.saferReplace(null, null, null);
      Assert.assertNull(actual);

      actual = Strings.saferReplace(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING);
      Assert.assertEquals(Strings.EMPTY_STRING, actual);

      actual = Strings.saferReplace("hello", "e", "o");
      Assert.assertEquals("hollo", actual);

      actual = Strings.saferReplace(".S.t.r.i.n.g.s.T.e.s.t..", "\\.", "_");
      Assert.assertEquals("_S_t_r_i_n_g_s_T_e_s_t__", actual);
   }

   @org.junit.Test
   public void test_BuildStatement() {
      String actual = Strings.buildStatement(null, null);
      Assert.assertNull(actual);

      actual = Strings.buildStatement(Arrays.asList(Strings.EMPTY_STRING), Strings.EMPTY_STRING);
      Assert.assertEquals(Strings.EMPTY_STRING, actual);

      actual = Strings.buildStatement(Arrays.asList("hello"), "e");
      Assert.assertEquals("hello", actual);

      actual = Strings.buildStatement(Arrays.asList("hello", "hello"), "e");
      Assert.assertEquals("hello e hello", actual);

      actual = Strings.buildStatement(Arrays.asList("hello", "hello", "olleh"), "e");
      Assert.assertEquals("hello, hello e olleh", actual);

      actual = Strings.buildStatment(Arrays.asList("hello", "hello", "olleh"));
      Assert.assertEquals("hello, hello and olleh", actual);
   }

   @org.junit.Test
   public void test_minimize() {
      String actual = Strings.minimize(null);
      Assert.assertNull(actual);

      actual = Strings.minimize(Strings.EMPTY_STRING);
      Assert.assertEquals(Strings.EMPTY_STRING, actual);

      actual = Strings.minimize("hello");
      Assert.assertEquals("hello", actual);

      actual = Strings.minimize("hello\nhello\t\thello\nhello");
      Assert.assertEquals("hellohellohellohello", actual);

      actual = Strings.minimize(System.getProperty("line.separator"));
      Assert.assertEquals(Strings.EMPTY_STRING, actual);

      actual = Strings.minimize("Test\r\ning\n\tstuff");
      Assert.assertEquals("Testingstuff", actual);
   }
}
