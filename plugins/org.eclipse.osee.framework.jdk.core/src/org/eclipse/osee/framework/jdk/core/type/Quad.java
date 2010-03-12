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
 * @author Roberto E. Escobar
 */
public class Quad<T, K, J, V> extends Triplet<T, K, J> {
   private static final long serialVersionUID = -3319956950656820062L;

   private V fourth;

   public Quad(T first, K second, J third, V fourth) {
      super(first, second, third);
      this.fourth = fourth;
   }

   public V getFourth() {
      return fourth;
   }

   public void setFourth(V fourth) {
      this.fourth = fourth;
   }

   public Quad<T, K, J, V> set(T first, K second, J third, V fourth) {
      super.set(first, second, third);
      setFourth(fourth);
      return this;
   }

   @Override
   public String toString() {
      return String.format("[%s, %s, %s, %s]", getFirst(), getSecond(), getThird(), getFourth());
   }

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof Quad<?, ?, ?, ?>) {
         Quad<?, ?, ?, ?> other = (Quad<?, ?, ?, ?>) obj;
         boolean fourthEquals = fourth == null ? other.fourth == null : fourth.equals(other.fourth);
         result = fourthEquals && super.equals(other);
      }
      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = super.hashCode();
      if (fourth != null) {
         result = prime * result + fourth.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }
}