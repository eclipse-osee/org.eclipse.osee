package org.eclipse.osee.framework.jdk.core.test.util;

import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;

/**
 * Used for testing object storage and retrieval using guid hashCode/equals
 * 
 * @author Donald G. Dunne
 */
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
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      GuidObject other = (GuidObject) obj;
      if (guid == null) {
         if (other.guid != null) return false;
      } else if (!guid.equals(other.guid)) return false;
      return true;
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

}