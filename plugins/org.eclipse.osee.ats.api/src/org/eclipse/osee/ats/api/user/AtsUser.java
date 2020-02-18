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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
@JsonSerialize(as = AtsUser.class)
public class AtsUser extends JaxAtsObject implements IAtsObject, UserId {

   private String userId;
   private String email;
   private List<String> loginIds = new ArrayList<>();
   private List<String> savedSearches = new ArrayList<>();
   private List<ArtifactId> userGroups = new ArrayList<>();

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

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   @Override
   @JsonIgnore
   public String toStringWithId() {
      return String.format("[%s]-[%s]", getName(), getUserId());
   }

   @Override
   public Long getId() {
      return super.getId();
   }

   @Override
   @JsonIgnore
   public ArtifactTypeToken getArtifactType() {
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

   public List<String> getLoginIds() {
      return loginIds;
   }

   public void setLoginIds(List<String> loginIds) {
      this.loginIds = loginIds;
   }

   public List<String> getSavedSearches() {
      return savedSearches;
   }

   public void setSavedSearches(List<String> savedSearches) {
      this.savedSearches = savedSearches;
   }

   public List<ArtifactId> getUserGroups() {
      return userGroups;
   }

   public void setUserGroups(List<ArtifactId> userGroups) {
      this.userGroups = userGroups;
   }

}