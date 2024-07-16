/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author Ryan D. Brooks
 */
public class BaseId implements Id, Cloneable {
   public static final Id SENTINEL = Id.valueOf(Id.SENTINEL);

   @JsonSerialize(using = ToStringSerializer.class)
   protected Long id;

   public BaseId(Long id) {
      this.id = id == null ? Id.SENTINEL : id;
   }

   public BaseId(Id id) {
      this(id.getId());
   }

   @Override
   public int hashCode() {
      return id.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Id) {
         return id.equals(((Id) obj).getId());
      }
      if (obj instanceof Identity<?> && this instanceof Identity<?>) {
         return ((Identity<?>) this).getGuid().equals(((Identity<?>) obj).getGuid());
      }
      if (obj instanceof Long) {
         return id.equals(obj);
      }
      if (obj instanceof Integer) {
         return id.longValue() == ((Integer) obj).longValue();
      }
      return false;
   }

   @Override
   public String toString() {
      return String.valueOf(id);
   }

   @Override
   public Long getId() {
      return id;
   }

   @Override
   public BaseId clone() {
      try {
         return (BaseId) super.clone();
      } catch (CloneNotSupportedException ex) {
         return null;
      }
   }

   /**
    * Use this method to construct an instance of an Id that is of the same type as the object this is called on (likely
    * a subclass of BaseId). It will set the numeric id to the given value.
    */
   @SuppressWarnings("unchecked")
   @Override
   public <T extends Id> T clone(Long id) {
      BaseId newId = clone();
      newId.id = id;
      return (T) newId;
   }

   public void setId(Long id) {
      this.id = id;
   }
}
