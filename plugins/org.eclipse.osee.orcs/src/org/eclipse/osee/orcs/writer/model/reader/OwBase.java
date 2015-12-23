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
package org.eclipse.osee.orcs.writer.model.reader;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.eclipse.osee.framework.jdk.core.type.UuidBaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.UuidIdentity;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class OwBase implements UuidIdentity {

   @JsonSerialize(using = ToStringSerializer.class)
   long uuid = 0L;
   String data = null;

   @Override
   public long getUuid() {
      return uuid;
   }

   public boolean uuidIsSet() {
      return uuid != 0L;
   }

   public void setUuid(long uuid) {
      this.uuid = uuid;
   }

   @Override
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
      UuidBaseIdentity other = (UuidBaseIdentity) obj;
      if (uuid != other.getUuid()) {
         return false;
      }
      return true;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

}
