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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.UuidNamedIdentity;

/**
 * @author Donald G. Dunne
 */
public class AtsObject extends UuidNamedIdentity implements IAtsObject {

   private String desc;
   private ArtifactId object;

   public AtsObject(String name, long uuid) {
      super(uuid, name);
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
      return String.format("[%d][%s]", getUuid(), getName());
   }

   @Override
   public ArtifactId getStoreObject() {
      return object;
   }

   @Override
   public void setStoreObject(ArtifactId artifact) {
      this.object = artifact;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (int) (getUuid() ^ (getUuid() >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (!(obj instanceof IAtsObject)) {
         return false;
      }
      IAtsObject other = (IAtsObject) obj;
      if (getUuid() == other.getUuid()) {
         return true;
      }
      return false;
   }

}
