/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.model.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link AtsObject}
 * 
 * @author Donald G. Dunne
 */
public class AtsObjectTest {

   @Test
   public void testGetNameGuidId() {
      AtsObject obj = new AtsObject("hello", "GUID");
      Assert.assertEquals("hello", obj.getName());
      Assert.assertEquals("GUID", obj.getGuid());

      obj = new AtsObject("hello");
      Assert.assertEquals("hello", obj.getName());
   }

   @Test
   public void testGetSetDescription() {
      AtsObject obj = new AtsObject("hello");
      Assert.assertNull(obj.getDescription());

      obj.setDescription("desc");
      Assert.assertEquals("desc", obj.getDescription());
   }

   @Test
   public void testEqualsObject() {
      AtsObject obj = new AtsObject("hello", "GUID");
      Assert.assertTrue(obj.equals(obj));

      AtsObject obj2 = new AtsObject("hello", "GUID");

      Assert.assertTrue(obj.equals(obj2));
      Assert.assertFalse(obj.equals(null));
      Assert.assertFalse(obj.equals("str"));

      AtsObject obj3 = new AtsObject("hello", "");
      Assert.assertFalse(obj.equals(obj3));
      Assert.assertFalse(obj3.equals(obj));

   }

   @Test
   public void testHashCode() {
      AtsObject obj = new AtsObject("hello", "GUID");
      Assert.assertEquals(2199208, obj.hashCode());

      obj = new AtsObject("hello", "");
      Assert.assertEquals(31, obj.hashCode());
   }

}
