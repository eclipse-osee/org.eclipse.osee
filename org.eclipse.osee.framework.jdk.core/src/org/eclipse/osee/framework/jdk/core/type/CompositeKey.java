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
         return otherKey.key1.equals(key1) && otherKey.key2.equals(key2);
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hashCode = 11;
      hashCode = hashCode * 31 + key1.hashCode();
      hashCode = hashCode * 31 + key2.hashCode();
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
