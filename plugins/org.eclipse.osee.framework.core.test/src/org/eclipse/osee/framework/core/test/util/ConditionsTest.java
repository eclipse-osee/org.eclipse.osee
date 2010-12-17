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
package org.eclipse.osee.framework.core.test.util;

import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
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
   public void testCheckNotNullWithException() throws OseeCoreException {
      Conditions.checkNotNull(null, "test object");
   }

   @Test
   public void testCheckNotNullWithoutException() throws OseeCoreException {
      Conditions.checkNotNull("a", "test object");
   }

   @Test(expected = OseeArgumentException.class)
   public void testCheckNotNullOrEmptyWithException() throws OseeCoreException {
      Conditions.checkNotNullOrEmpty("", "empty string");
   }

   @Test
   public void testCheckNotNullOrEmptyWithoutException() throws OseeCoreException {
      Conditions.checkNotNullOrEmpty("a", "a string");
   }
}