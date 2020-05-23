/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Roberto E. Escobar
 */
public class Triplet<T, K, J> extends Pair<T, K> {
   private static final long serialVersionUID = -3319956950656820062L;

   private J third;

   public Triplet(T first, K second, J third) {
      super(first, second);
      this.third = third;
   }

   public J getThird() {
      return third;
   }

   public void setThird(J third) {
      this.third = third;
   }

   public Triplet<T, K, J> set(T first, K second, J third) {
      super.set(first, second);
      setThird(third);
      return this;
   }

   @Override
   public String toString() {
      return String.format("[%s, %s, %s]", getFirst(), getSecond(), getThird());
   }

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof Triplet<?, ?, ?>) {
         Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
         boolean thirdEquals = third == null ? other.third == null : third.equals(other.third);
         result = thirdEquals && super.equals(other);
      }
      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = super.hashCode();
      if (third != null) {
         result = prime * result + third.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }
}