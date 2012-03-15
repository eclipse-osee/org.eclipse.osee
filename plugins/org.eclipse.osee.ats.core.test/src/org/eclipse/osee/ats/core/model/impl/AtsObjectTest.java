/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
   public void testGetNameGuidHrid() {
      AtsObject obj = new AtsObject("hello", "GUID", "HRID");
      Assert.assertEquals("hello", obj.getName());
      Assert.assertEquals("GUID", obj.getGuid());
      Assert.assertEquals("HRID", obj.getHumanReadableId());

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
      AtsObject obj = new AtsObject("hello", "GUID", "HRID");
      Assert.assertTrue(obj.equals(obj));

      AtsObject obj2 = new AtsObject("hello", "GUID", "HRID");

      Assert.assertTrue(obj.equals(obj2));
      Assert.assertFalse(obj.equals(null));
      Assert.assertFalse(obj.equals("str"));

      AtsObject obj3 = new AtsObject("hello", "GUID", "HRID");
      obj3.setGuid(null);
      Assert.assertFalse(obj.equals(obj3));
      Assert.assertFalse(obj3.equals(obj));

      AtsObject obj4 = new AtsObject("hello", "GUID", "HRID");
      obj4.setGuid(null);
      Assert.assertFalse(obj3.equals(obj4));
   }

   @Test
   public void testHashCode() {
      AtsObject obj = new AtsObject("hello", "GUID", "HRID");
      Assert.assertEquals(2199208, obj.hashCode());

      obj = new AtsObject("hello");
      obj.setGuid(null);
      Assert.assertEquals(31, obj.hashCode());
   }

}
