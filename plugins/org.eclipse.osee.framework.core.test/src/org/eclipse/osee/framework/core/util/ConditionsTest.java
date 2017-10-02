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
package org.eclipse.osee.framework.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link Conditions}
 * 
 * @author Ryan D. Brooks
 */
public class ConditionsTest {

   @Test
   public void testIn() {
      Assert.assertTrue(Conditions.in("a", "b", "a"));
      Assert.assertFalse(Conditions.in("d", "b", "a"));
   }

   @Test
   public void testNotNull() {
      Assert.assertTrue(Conditions.notNull("a", "b"));
      Assert.assertFalse(Conditions.notNull("a", null));
   }

   @Test
   public void testAnyNull() {
      Assert.assertTrue(Conditions.anyNull("a", null));
      Assert.assertFalse(Conditions.anyNull("a", "b"));
   }

   @Test
   public void testAllNull() {
      Assert.assertTrue(Conditions.allNull(null, null));
      Assert.assertTrue(Conditions.allNull());
      Assert.assertFalse(Conditions.allNull(null, "b"));
   }

   @Test(expected = OseeArgumentException.class)
   public void testCheckNotNullWithException()  {
      Conditions.checkNotNull(null, "test object");
   }

   @Test
   public void testCheckNotNullWithoutException()  {
      Conditions.checkNotNull("a", "test object");
   }

   @Test(expected = OseeArgumentException.class)
   public void testCheckNotNullOrEmptyWithException()  {
      Conditions.checkNotNullOrEmpty("", "empty string");
   }

   @Test
   public void testCheckNotNullOrEmptyWithoutException()  {
      Conditions.checkNotNullOrEmpty("a", "a string");
   }

   @Test
   public void testHasValues() {
      Assert.assertFalse(Conditions.hasValues((Collection<?>) null));
      Assert.assertFalse(Conditions.hasValues(Collections.emptyList()));
      Assert.assertTrue(Conditions.hasValues(Arrays.asList("hello")));

      Assert.assertFalse(Conditions.hasValues((String[]) null));
      Assert.assertFalse(Conditions.hasValues(new Integer[0]));
      Assert.assertTrue(Conditions.hasValues(new String[] {"hello"}));
   }
}