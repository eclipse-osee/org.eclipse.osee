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
   private T key;
   private K value;

   public Pair(T key, K value) {
      this.key = key;
      this.value = value;
   }

   public T getKey() {
      return key;
   }

   public K getValue() {
      return value;
   }

   public void setKey(T key) {
      this.key = key;
   }

   public void setValue(K value) {
      this.value = value;
   }

   public String toString() {
      return String.format("[%s, %s]", key.toString(), value.toString());
   }
}
