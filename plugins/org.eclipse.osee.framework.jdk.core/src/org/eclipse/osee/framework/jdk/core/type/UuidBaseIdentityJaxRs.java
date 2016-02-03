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
 * BaseIdentity with zero parameter constructor needed by Jax-Rs serialization
 *
 * @author Donald G. Dunne
 */
public class UuidBaseIdentityJaxRs implements UuidIdentity {
   private final long uuid;

   public UuidBaseIdentityJaxRs() {
      this(null);
   }

   public UuidBaseIdentityJaxRs(Long id) {
      this.uuid = id;
   }

   @Override
   public Long getUuid() {
      return uuid;
   }

   @Override
   public String toString() {
      return String.valueOf(getUuid());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (uuid ^ uuid >>> 32);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      UuidBaseIdentityJaxRs other = (UuidBaseIdentityJaxRs) obj;
      if (uuid != other.uuid) {
         return false;
      }
      return true;
   }
}