/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
 * @author Donald G. Dunne
 */
public class UuidBaseIdentity<T> implements UuidIdentity<T> {
   private final T uuid;

   public UuidBaseIdentity(T id) {
      this.uuid = id;
   }

   @Override
   public T getUuid() {
      return uuid;
   }

   @Override
   public int hashCode() {
      return getUuid().hashCode();
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean equals(Object obj) {
      boolean equal = false;
      if (obj instanceof UuidIdentity) {
         UuidIdentity<T> identity = (UuidIdentity<T>) obj;
         if (getUuid() == identity.getUuid()) {
            equal = true;
         } else if (getUuid() != null) {
            equal = getUuid().equals(identity.getUuid());
         }
      }
      return equal;
   }

   @Override
   public String toString() {
      return String.valueOf(getUuid());
   }

   @Override
   public boolean matches(UuidIdentity<?>... identities) {
      for (UuidIdentity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }
}