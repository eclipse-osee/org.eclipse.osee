/*
 * Created on Jan 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.test.util;

import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class HashCollectionTest {

   @Test
   public void testHashCollectionString() {
      HashCollection<String, String> hashCollection = new HashCollection<String, String>();

      Assert.assertEquals(0, hashCollection.size());
      Assert.assertEquals(0, hashCollection.getValues().size());
      Assert.assertNull(hashCollection.getValues("this"));

      for (int x = 1; x <= 5; x++) {
         hashCollection.put("key", "value " + x);
      }

      Assert.assertEquals(5, hashCollection.size());
      Assert.assertEquals(5, hashCollection.getValues().size());
      Assert.assertNull(hashCollection.getValues("this"));
      Assert.assertEquals(5, hashCollection.getValues("key").size());
   }

   @Test
   public void testGuidObject() {

      // test GuidObject that will be used for next test
      GuidObject keyObjectA = new GuidObject();
      GuidObject keyObjectB = new GuidObject();
      GuidObject keyObjectA1 = new GuidObject();

      Assert.assertTrue(keyObjectA.equals(keyObjectA));
      Assert.assertFalse(keyObjectA.equals(keyObjectB));
      Assert.assertFalse(keyObjectA.equals(keyObjectA1));

      // This should make them equal
      keyObjectA1.setGuid(keyObjectA.getGuid());

      Assert.assertTrue(keyObjectA.equals(keyObjectA));
      Assert.assertTrue(keyObjectA.equals(keyObjectA1));
   }

   @Test
   public void testHashCollectionObject() {
      HashCollection<GuidObject, String> hashCollection = new HashCollection<GuidObject, String>();

      Assert.assertEquals(0, hashCollection.size());
      Assert.assertEquals(0, hashCollection.getValues().size());
      Assert.assertNull(hashCollection.getValues(new GuidObject()));

      GuidObject keyObject = new GuidObject();
      for (int x = 1; x <= 5; x++) {
         hashCollection.put(keyObject, "value " + x);
      }

      Assert.assertEquals(5, hashCollection.size());
      Assert.assertEquals(5, hashCollection.getValues().size());
      Assert.assertNull(hashCollection.getValues(new GuidObject()));
      Assert.assertEquals(5, hashCollection.getValues(keyObject).size());
   }
   public class GuidObject {
      private String guid = GUID.create();

      public String getGuid() {
         return guid;
      }

      public void setGuid(String guid) {
         this.guid = guid;
      }

      public String toString() {
         return guid;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + ((guid == null) ? 0 : guid.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null) return false;
         if (getClass() != obj.getClass()) return false;
         GuidObject other = (GuidObject) obj;
         if (!getOuterType().equals(other.getOuterType())) return false;
         if (guid == null) {
            if (other.guid != null) return false;
         } else if (!guid.equals(other.guid)) return false;
         return true;
      }

      private HashCollectionTest getOuterType() {
         return HashCollectionTest.this;
      }

   }
}
