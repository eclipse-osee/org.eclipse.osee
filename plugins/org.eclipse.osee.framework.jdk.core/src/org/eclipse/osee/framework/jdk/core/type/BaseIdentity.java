/*******************************************************************************
 * Copyright (c) 2009 Boeing.
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
public class BaseIdentity<T> implements Identity<T> {
   private final T id;

   public BaseIdentity(T id) {
      this.id = id;
   }

   @Override
   public T getGuid() {
      return id;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean equals(Object obj) {
      boolean equal = false;
      if (obj instanceof Identity) {
         Identity<T> identity = (Identity<T>) obj;
         if (getGuid() == identity.getGuid()) {
            equal = true;
         } else if (getGuid() != null) {
            equal = getGuid().equals(identity.getGuid());
         }
      } else if (obj instanceof Id) {
         equal = ((Id) obj).getId().equals(id);
      }
      return equal;
   }

   @Override
   public String toString() {
      return String.valueOf(getGuid());
   }

}