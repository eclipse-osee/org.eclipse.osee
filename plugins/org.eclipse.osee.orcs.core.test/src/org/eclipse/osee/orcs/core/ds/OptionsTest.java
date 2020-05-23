/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link Options}
 * 
 * @author Roberto E. Escobar
 */
public class OptionsTest {

   private static final String TEST_KEY = "KEY1";

   private static final String STRING_KEY = "KEY1";
   private static final String DOUBLE_KEY = "KEY2";
   private static final String LONG_KEY = "KEY3";
   private static final String BOOLEAN_KEY = "KEY4";
   private static final String FLOAT_KEY = "KEY5";
   private static final String INT_KEY = "KEY6";
   private static final String DATE_KEY = "KEY7";

   private Options options;

   @Before
   public void setup() {
      options = new Options();
   }

   @Test
   public void testGetSetString() {
      assertTrue(options.isEmpty(TEST_KEY));

      options.put(TEST_KEY, "Hello");

      assertEquals("Hello", options.get(TEST_KEY));
      assertFalse(options.isEmpty(TEST_KEY));
   }

   @Test
   public void testGetSetBoolean() {
      assertTrue(options.isEmpty(TEST_KEY));

      assertEquals(false, options.getBoolean(TEST_KEY));

      options.put(TEST_KEY, true);

      assertEquals(true, options.getBoolean(TEST_KEY));
      assertFalse(options.isEmpty(TEST_KEY));
   }

   @Test
   public void testGetSetDouble() {
      assertTrue(options.isEmpty(TEST_KEY));

      options.put(TEST_KEY, 123.12321);

      assertEquals(123.12321, options.getDouble(TEST_KEY), 0);
      assertFalse(options.isEmpty(TEST_KEY));
   }

   @Test
   public void testGetSetFloat() {
      assertTrue(options.isEmpty(TEST_KEY));

      options.put(TEST_KEY, 123.12321);

      assertEquals(123.12321, options.getFloat(TEST_KEY), 4);
      assertFalse(options.isEmpty(TEST_KEY));
   }

   @Test
   public void testGetSetInteger() {
      assertTrue(options.isEmpty(TEST_KEY));

      options.put(TEST_KEY, 123);

      assertEquals(123, options.getInt(TEST_KEY), 0);
      assertFalse(options.isEmpty(TEST_KEY));
   }

   @Test
   public void testGetSetLong() {
      assertTrue(options.isEmpty(TEST_KEY));

      options.put(TEST_KEY, 123L);

      assertEquals(123L, options.getLong(TEST_KEY), 0);
      assertFalse(options.isEmpty(TEST_KEY));
   }

   @Test
   public void testGetSetDate() {
      assertTrue(options.isEmpty(TEST_KEY));

      assertNull(options.getDateOrNull(TEST_KEY));

      Date date = new Date();
      options.put(TEST_KEY, date);

      assertEquals(date, options.getDate(TEST_KEY));
      assertFalse(options.isEmpty(TEST_KEY));
   }

   @Test
   public void testGetSetObject() {
      assertTrue(options.isEmpty(TEST_KEY));

      assertNull(options.getObject(TEST_KEY));

      Long object = new Long(12312L);
      options.put(TEST_KEY, object);

      assertEquals(object, options.getObject(TEST_KEY));

      Long actual = options.getObject(Long.class, TEST_KEY);
      assertEquals(object, actual);

      assertFalse(options.isEmpty(TEST_KEY));

      options.remove(TEST_KEY);
      assertTrue(options.isEmpty(TEST_KEY));
   }

   @Test
   public void testSetFromSource() {
      Date date = new Date();

      options.put(STRING_KEY, "Hello");
      options.put(BOOLEAN_KEY, true);
      options.put(DOUBLE_KEY, 123.12321);
      options.put(FLOAT_KEY, 123.12321);
      options.put(INT_KEY, 123);
      options.put(LONG_KEY, 123L);
      options.put(DATE_KEY, date);

      assertEquals("Hello", options.get(STRING_KEY));
      assertEquals(true, options.getBoolean(BOOLEAN_KEY));
      assertEquals(123.12321, options.getDouble(DOUBLE_KEY), 0);
      assertEquals(123.12321, options.getFloat(FLOAT_KEY), 4);
      assertEquals(123, options.getInt(INT_KEY), 0);
      assertEquals(123L, options.getLong(LONG_KEY), 0);
      assertEquals(date, options.getDate(DATE_KEY));

      Options other = new Options();
      assertTrue(other.isEmpty(STRING_KEY));
      assertTrue(other.isEmpty(BOOLEAN_KEY));
      assertTrue(other.isEmpty(DOUBLE_KEY));
      assertTrue(other.isEmpty(FLOAT_KEY));
      assertTrue(other.isEmpty(INT_KEY));
      assertTrue(other.isEmpty(LONG_KEY));
      assertTrue(other.isEmpty(DATE_KEY));

      other.setFrom(options);

      assertFalse(other.isEmpty(STRING_KEY));
      assertFalse(other.isEmpty(BOOLEAN_KEY));
      assertFalse(other.isEmpty(DOUBLE_KEY));
      assertFalse(other.isEmpty(FLOAT_KEY));
      assertFalse(other.isEmpty(INT_KEY));
      assertFalse(other.isEmpty(LONG_KEY));
      assertFalse(other.isEmpty(DATE_KEY));

      assertEquals("Hello", other.get(STRING_KEY));
      assertEquals(true, other.getBoolean(BOOLEAN_KEY));
      assertEquals(123.12321, other.getDouble(DOUBLE_KEY), 0);
      assertEquals(123.12321, other.getFloat(FLOAT_KEY), 4);
      assertEquals(123, other.getInt(INT_KEY), 0);
      assertEquals(123L, other.getLong(LONG_KEY), 0);
      assertEquals(date, other.getDate(DATE_KEY));
   }

   @Test
   public void testClone() {
      Date date = new Date();

      options.put(STRING_KEY, "Hello");
      options.put(BOOLEAN_KEY, true);
      options.put(DOUBLE_KEY, 123.12321);
      options.put(FLOAT_KEY, 123.12321);
      options.put(INT_KEY, 123);
      options.put(LONG_KEY, 123L);
      options.put(DATE_KEY, date);

      assertEquals("Hello", options.get(STRING_KEY));
      assertEquals(true, options.getBoolean(BOOLEAN_KEY));
      assertEquals(123.12321, options.getDouble(DOUBLE_KEY), 0);
      assertEquals(123.12321, options.getFloat(FLOAT_KEY), 4);
      assertEquals(123, options.getInt(INT_KEY), 0);
      assertEquals(123L, options.getLong(LONG_KEY), 0);
      assertEquals(date, options.getDate(DATE_KEY));

      Options other = options.clone();

      assertFalse(other.isEmpty(STRING_KEY));
      assertFalse(other.isEmpty(BOOLEAN_KEY));
      assertFalse(other.isEmpty(DOUBLE_KEY));
      assertFalse(other.isEmpty(FLOAT_KEY));
      assertFalse(other.isEmpty(INT_KEY));
      assertFalse(other.isEmpty(LONG_KEY));
      assertFalse(other.isEmpty(DATE_KEY));

      assertEquals("Hello", other.get(STRING_KEY));
      assertEquals(true, other.getBoolean(BOOLEAN_KEY));
      assertEquals(123.12321, other.getDouble(DOUBLE_KEY), 0);
      assertEquals(123.12321, other.getFloat(FLOAT_KEY), 4);
      assertEquals(123, other.getInt(INT_KEY), 0);
      assertEquals(123L, other.getLong(LONG_KEY), 0);
      assertEquals(date, other.getDate(DATE_KEY));
   }

   @Test
   public void testReset() {
      Date date = new Date();
      options.put(STRING_KEY, "Hello");
      options.put(BOOLEAN_KEY, true);
      options.put(DOUBLE_KEY, 123.12321);
      options.put(FLOAT_KEY, 123.12321);
      options.put(INT_KEY, 123);
      options.put(LONG_KEY, 123L);
      options.put(DATE_KEY, date);

      assertEquals("Hello", options.get(STRING_KEY));
      assertEquals(true, options.getBoolean(BOOLEAN_KEY));
      assertEquals(123.12321, options.getDouble(DOUBLE_KEY), 0);
      assertEquals(123.12321, options.getFloat(FLOAT_KEY), 4);
      assertEquals(123, options.getInt(INT_KEY), 0);
      assertEquals(123L, options.getLong(LONG_KEY), 0);
      assertEquals(date, options.getDate(DATE_KEY));

      options.reset();

      assertTrue(options.isEmpty(STRING_KEY));
      assertTrue(options.isEmpty(BOOLEAN_KEY));
      assertTrue(options.isEmpty(DOUBLE_KEY));
      assertTrue(options.isEmpty(FLOAT_KEY));
      assertTrue(options.isEmpty(INT_KEY));
      assertTrue(options.isEmpty(LONG_KEY));
      assertTrue(options.isEmpty(DATE_KEY));
   }
}
