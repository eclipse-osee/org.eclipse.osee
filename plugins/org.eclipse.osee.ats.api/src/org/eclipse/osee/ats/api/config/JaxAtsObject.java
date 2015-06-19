/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.UuidIdentity;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxAtsObject {

   private String name;
   private long uuid;
   private boolean active;
   private String description;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public long getUuid() {
      return uuid;
   }

   /**
    * Provided for deserialization. Setting will not apply to new artifact.
    */
   public void setUuid(long uuid) {
      this.uuid = uuid;
   }

   @Override
   public String toString() {
      return getName();
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public boolean matches(JaxAtsObject... identities) {
      for (JaxAtsObject identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   public boolean matches(UuidIdentity... identities) {
      for (UuidIdentity identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (uuid ^ (uuid >>> 32));
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
      if (!(obj instanceof JaxAtsObject)) {
         return false;
      }
      JaxAtsObject other = (JaxAtsObject) obj;
      if (uuid != other.getUuid()) {
         return false;
      }
      return true;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

}
