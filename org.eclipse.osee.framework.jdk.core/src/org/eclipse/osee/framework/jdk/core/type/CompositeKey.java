/*
 * Created on Nov 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Ken J. Aguilar
 */
public final class CompositeKey<A, B> {
   private A key1;
   private B key2;

   public CompositeKey() {
      this(null, null);
   }

   public CompositeKey(A key1, B key2) {
      setKeys(key1, key2);
   }

   public A getKey1() {
      return key1;
   }

   public B getKey2() {
      return key2;
   }

   public CompositeKey<A, B> setKeys(A key1, B key2) {
      this.key1 = key1;
      this.key2 = key2;
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CompositeKey) {
         final CompositeKey<A, B> otherKey = (CompositeKey<A, B>) obj;
         boolean result = true;
         if (otherKey.key1 != null && key1 != null) {
            result &= otherKey.key1.equals(key1);
         } else {
            result &= otherKey.key1 == null && key1 == null;
         }
         if (otherKey.key2 != null && key2 != null) {
            result &= otherKey.key2.equals(key2);
         } else {
            result &= otherKey.key2 == null && key2 == null;
         }
         return result;
      }
      return false;
   }

   @Override
   public int hashCode() {
      final int constant = 31;
      int hashCode = 11;
      if (key1 != null) {
         hashCode = hashCode * constant + key1.hashCode();
      } else {
         hashCode = hashCode * constant;
      }
      if (key2 != null) {
         hashCode = hashCode * constant + key2.hashCode();
      } else {
         hashCode = hashCode * constant;
      }
      return hashCode;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "key1: \"" + key1 + "\" + key2: \"" + key2 + "\"";
   }
}
