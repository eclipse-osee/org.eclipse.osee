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

import java.io.Serializable;

/**
 * @author Roberto E. Escobar
 */
public class Pair<T, K> implements Serializable {
   private static final long serialVersionUID = 1764353834209869140L;
   private T first;
   private K second;

   public Pair(T first, K second) {
      this.first = first;
      this.second = second;
   }

   public T getFirst() {
      return first;
   }

   public K getSecond() {
      return second;
   }

   public void setFirst(T key) {
      this.first = key;
   }

   public void setSecond(K value) {
      this.second = value;
   }

   public Pair<T, K> set(T first, K second) {
      this.first = first;
      this.second = second;
      return this;
   }

   @Override
   public String toString() {
      String firstString = String.valueOf(first);
      String secondString = String.valueOf(second);
      return String.format("[%s, %s]", firstString, secondString);
   }

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof Pair<?, ?>) {
         Pair<?, ?> other = (Pair<?, ?>) obj;
         boolean left = first == null ? other.first == null : first.equals(other.first);
         boolean right = second == null ? other.second == null : second.equals(other.second);
         result = left && right;
      }
      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = 17;
      if (first != null) {
         result = prime * result + first.hashCode();
      } else {
         result = prime * result;
      }
      if (second != null) {
         result = prime * result + second.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   /**
    * @use getFirst()
    */
   @Deprecated
   public T getKey() {
      return first;
   }

   /**
    * @use getSecond()
    */
   @Deprecated
   public K getValue() {
      return second;
   }

   /**
    * @use setFirst(T key)
    */
   @Deprecated
   public void setKey(T key) {
      this.first = key;
   }

   /**
    * @use setSecond(T key)
    */
   @Deprecated
   public void setValue(K value) {
      this.second = value;
   }
}
