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

package org.eclipse.osee.ats.core.model.impl;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link AtsObject}
 *
 * @author Donald G. Dunne
 */
public class AtsObjectTest {

   @Test
   public void testGetNameId() {
      AtsObject obj = new AtsObject("hello", 456);
      Assert.assertEquals("hello", obj.getName());
      Assert.assertEquals(456, obj.getId().longValue());

      obj = new AtsObject("hello", Lib.generateId());
      Assert.assertEquals("hello", obj.getName());
   }

   @Test
   public void testGetSetDescription() {
      AtsObject obj = new AtsObject("hello", Lib.generateId());
      Assert.assertNull(obj.getDescription());

      obj.setDescription("desc");
      Assert.assertEquals("desc", obj.getDescription());
   }

   @Test
   public void testEqualsObject() {
      AtsObject obj = new AtsObject("hello", 456);
      Assert.assertTrue(obj.equals(obj));

      AtsObject obj2 = new AtsObject("hello", 456);

      Assert.assertTrue(obj.equals(obj2));
      Assert.assertFalse(obj.equals(null));
      Assert.assertFalse(obj.equals("str"));

      AtsObject obj3 = new AtsObject("hello", 457);
      Assert.assertFalse(obj.equals(obj3));
      Assert.assertFalse(obj3.equals(obj));

   }
}