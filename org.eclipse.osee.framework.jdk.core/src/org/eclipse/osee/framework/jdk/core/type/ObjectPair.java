/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Ryan D. Brooks
 */
public class ObjectPair<O, T> {
   public O object1;
   public T object2;

   public ObjectPair(O object1, T object2) {
      this.object1 = object1;
      this.object2 = object2;
   }

   /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CompositeKey) {
         final ObjectPair<O, T> other = (ObjectPair<O, T>) obj;
         boolean result = true;
         if (other.object1 != null && object1 != null) {
            result &= other.object1.equals(object1);
         } else {
            result &= other.object1 == null && object1 == null;
         }
         if (other.object2 != null && object2 != null) {
            result &= other.object2.equals(object2);
         } else {
            result &= other.object2 == null && object2 == null;
         }
         return result;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 37;
      int result = 17;
      if (object1 != null) {
         result = prime * result + object1.hashCode();
      } else {
         result = prime * result;
      }
      if (object2 != null) {
         result = prime * result + object2.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   @Override
   public String toString() {
      return "<" + object1 + ", " + object2 + ">";
   }
}
