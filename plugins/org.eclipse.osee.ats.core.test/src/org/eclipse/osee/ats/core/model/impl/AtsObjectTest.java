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

import org.eclipse.osee.framework.jdk.core.util.GUID;
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
   public void testGetNameGuidId() {
      AtsObject obj = new AtsObject("hello", "GUID", 456);
      Assert.assertEquals("hello", obj.getName());
      Assert.assertEquals("GUID", obj.getGuid());

      obj = new AtsObject("hello", GUID.create(), Lib.generateUuid());
      Assert.assertEquals("hello", obj.getName());
   }

   @Test
   public void testGetSetDescription() {
      AtsObject obj = new AtsObject("hello", GUID.create(), Lib.generateUuid());
      Assert.assertNull(obj.getDescription());

      obj.setDescription("desc");
      Assert.assertEquals("desc", obj.getDescription());
   }

   @Test
   public void testEqualsObject() {
      AtsObject obj = new AtsObject("hello", "GUID", 456);
      Assert.assertTrue(obj.equals(obj));

      AtsObject obj2 = new AtsObject("hello", "GUID", 456);

      Assert.assertTrue(obj.equals(obj2));
      Assert.assertFalse(obj.equals(null));
      Assert.assertFalse(obj.equals("str"));

      AtsObject obj3 = new AtsObject("hello", "", 456);
      Assert.assertFalse(obj.equals(obj3));
      Assert.assertFalse(obj3.equals(obj));

   }

   @Test
   public void testHashCode() {
      AtsObject obj = new AtsObject("hello", "GUID", 465);
      Assert.assertEquals(68174952, obj.hashCode());

      obj = new AtsObject("hello", "", 456);
      Assert.assertEquals(456, obj.hashCode());
   }

}
