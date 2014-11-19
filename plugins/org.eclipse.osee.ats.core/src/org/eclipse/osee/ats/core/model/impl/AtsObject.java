/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.model.impl;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.jdk.core.type.FullyNamedIdentity;

/**
 * @author Donald G. Dunne
 */
public class AtsObject extends FullyNamedIdentity<String> implements IAtsObject {

   private String desc;
   private Object object;
   private long id;

   public AtsObject(String name, String guid, long id) {
      super(guid, name);
      this.id = id;
   }

   @Override
   public String getDescription() {
      return desc;
   }

   public void setDescription(String desc) {
      this.desc = desc;
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public final String toStringWithId() {
      return String.format("[%s][%s]", getGuid(), getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
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
      AtsObject other = (AtsObject) obj;
      if (getGuid() == null) {
         if (other.getGuid() != null) {
            return false;
         } else {
            return false;
         }
      } else if (!getGuid().equals(other.getGuid())) {
         return false;
      }
      return true;
   }

   @Override
   public Object getStoreObject() {
      return object;
   }

   @Override
   public void setStoreObject(Object object) {
      this.object = object;
   }

   @Override
   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

}
