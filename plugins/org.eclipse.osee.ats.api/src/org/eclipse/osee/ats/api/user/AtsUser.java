/*********************************************************************
 * Copyright (c) 2015 Boeing
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
   private String phone;
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

   public String getPhone() {
      return phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   @Override
   @JsonIgnore
   public String toStringWithId() {
      return String.format("[%s]-[%s]", getName(), getUserId());
   }

   @Override
   @JsonIgnore
   public ArtifactTypeToken getArtifactType() {
      return CoreArtifactTypes.User;
   }

   @Override
   public String toString() {
      return getName();
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

   public boolean isAnonymous() {
      return AtsCoreUsers.isAnonymousUser(this);
   }

}