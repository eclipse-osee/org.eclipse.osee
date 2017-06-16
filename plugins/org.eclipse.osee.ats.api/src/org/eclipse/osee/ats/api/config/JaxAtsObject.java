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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxAtsObject {

   private String name;
   @JsonSerialize(using = ToStringSerializer.class)
   private Long uuid;
   private String guid;
   private boolean active;
   private String description;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Long getUuid() {
      return uuid;
   }

   /**
    * Provided for deserialization. Setting will not apply to new artifact.
    */
   public void setUuid(Long uuid) {
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
      if (obj instanceof JaxAtsObject) {
         JaxAtsObject other = (JaxAtsObject) obj;
         if (other.getUuid().equals(uuid)) {
            return true;
         }
      }
      if (obj instanceof IAtsObject) {
         IAtsObject other = (IAtsObject) obj;
         if (other.getId().equals(uuid)) {
            return true;
         }
      }
      if (obj instanceof ArtifactId) {
         ArtifactId other = (ArtifactId) obj;
         if (other.getUuid().equals(uuid)) {
            return true;
         }
      }
      return false;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

}
