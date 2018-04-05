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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
@JsonSerialize(as = AtsUser.class)
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
      setName(name);
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
   public Long getId() {
      return super.getId();
   }

   @Override
   @JsonIgnore
   public ArtifactTypeId getArtifactType() {
      return CoreArtifactTypes.User;
   }

   @Override
   public String toString() {
      return String.format("User [%s - %s - %s]", getName(), getUserId(), getEmail());
   }

   @Override
   @JsonIgnore
   public Long getUuid() {
      return super.getId();
   }

   @Override
   @JsonIgnore
   public ArtifactId getArtifactId() {
      return ArtifactId.valueOf(getId());
   }

}