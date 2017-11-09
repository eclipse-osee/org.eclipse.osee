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
package org.eclipse.osee.ats.api.user;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class AtsUser extends JaxAtsObject implements IAtsUser {

   private String userId;
   private String email;
   private ArtifactToken storeObject;
   private IUserArtLoader userArtLoader;

   public AtsUser() {
      // for jax-rs instantiation
   }

   public AtsUser(UserToken user) {
      this(user.getId(), user.getName(), user.getUserId(), user.getEmail(), user.isActive());
   }

   public AtsUser(Long id, String name, String userId, String email, boolean active) {
      this.userId = userId;
      this.email = email;
      this.active = active;
      this.id = id;
      this.name = name;
   }

   @Override
   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   @Override
   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   @Override
   @JsonIgnore
   public String toStringWithId() {
      return String.format("[%s]-[%d]", getName(), getUserId());
   }

   @Override
   @JsonIgnore
   public ArtifactToken getStoreObject() {
      if (storeObject == null && userArtLoader != null) {
         storeObject = userArtLoader.loadUser(this);
      }
      return storeObject;
   }

   @Override
   public void setStoreObject(ArtifactToken artifact) {
      this.storeObject = artifact;
   }

   @JsonIgnore
   public void setUserArtLoader(IUserArtLoader userArtLoader) {
      this.userArtLoader = userArtLoader;
   }

   @Override
   @JsonIgnore
   public IArtifactType getArtifactType() {
      return CoreArtifactTypes.User;
   }

   @Override
   public boolean isOfType(ArtifactTypeId... artifactTypes) {
      return Collections.asHashSet(artifactTypes).contains(getArtifactType());
   }

   @Override
   public String toString() {
      return String.format("User [%s - %s - %s]", getName(), getUserId(), getEmail());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (userId == null ? 0 : userId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj instanceof Id && (((Id) obj).getId().equals(getId()))) {
         return true;
      }
      try {
         if (obj instanceof IAtsUser) {
            String thisUserId = getUserId();
            String objUserId = ((IAtsUser) obj).getUserId();
            if (thisUserId == null) {
               if (objUserId != null) {
                  return false;
               }
            } else if (!thisUserId.equals(objUserId)) {
               return false;
            }
         }
      } catch (OseeCoreException ex) {
         return false;
      }
      return false;
   }

}