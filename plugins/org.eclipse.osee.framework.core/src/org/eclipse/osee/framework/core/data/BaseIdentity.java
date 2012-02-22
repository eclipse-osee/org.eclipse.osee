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

package org.eclipse.osee.framework.core.data;

/**
 * @author Roberto E. Escobar
 */
public class BaseIdentity<T> implements Identity<T> {

   private final T guid;

   public BaseIdentity(T guid) {
      this.guid = guid;
      if (guid == null) {
         throw new IllegalArgumentException("uuid cannot be null");
      }
   }

   @Override
   public T getGuid() {
      return guid;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BaseIdentity) {
         return getGuid().equals(((BaseIdentity<?>) obj).getGuid());
      }
      return false;
   }

   @Override
   public String toString() {
      return String.valueOf(getGuid());
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

}