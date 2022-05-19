/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
@JsonSerialize(using = UserTokenSerializer.class)
public interface UserToken extends ArtifactToken, UserId {

   public static final UserToken SENTINEL = create(Id.SENTINEL, Named.SENTINEL, "", "", false);

   @JsonCreator
   public static UserToken create(@JsonProperty("name") String name, @JsonProperty("email") String email, @JsonProperty("userId") String userId) {
      return create(Id.SENTINEL, name, email, userId, true);
   }

   public static UserToken create(long id, String name, String email, String userId, boolean active) {
      return create(id, name, email, userId, active, Collections.emptyList());
   }

   public static UserToken create(long id, String name, String email, String userId, boolean active, List<IUserGroupArtifactToken> roles) {
      return new UserTokenImpl(id, name, userId, active, email, Arrays.asList(userId), roles, "");
   }

   public static @NonNull UserToken create(long id, String name, String email, String userId, boolean active, List<String> loginIds, List<IUserGroupArtifactToken> roles) {
      return new UserTokenImpl(id, name, userId, active, email, loginIds, roles, "");
   }

   public static UserToken create(long id, String name, String email, String userId, boolean active, List<String> loginIds, List<IUserGroupArtifactToken> roles, String phone) {
      return new UserTokenImpl(id, name, userId, active, email, loginIds, roles, phone);
   }

   public String getUserId();

   public boolean isActive();

   public boolean isOseeAdmin();

   public String getPhone();

   public String getEmail();

   public List<IUserGroupArtifactToken> getRoles();

   public List<String> getLoginIds();

   public ArtifactToken getArtifact();

   public void setArtifact(ArtifactToken artifact);

   public final class UserTokenImpl extends NamedIdBase implements UserToken {
      private final String userId;
      private final boolean active;
      private final boolean admin;
      private final String email;
      private final List<IUserGroupArtifactToken> roles;
      private final List<String> loginIds;
      private final String phone;
      private ArtifactToken artifact;

      public UserTokenImpl(long id, String name, String userId, boolean active, String email, List<String> loginIds, List<IUserGroupArtifactToken> roles, String phone) {
         super(id, name);
         this.userId = userId;
         this.active = active;
         this.email = email;
         this.loginIds = loginIds;
         this.roles = roles;
         this.admin = this.roles.contains(CoreUserGroups.OseeAdmin);
         this.phone = phone;
      }

      @Override
      public ArtifactTypeToken getArtifactType() {
         return CoreArtifactTypes.User;
      }

      @Override
      public String getUserId() {
         return userId;
      }

      @Override
      public boolean isActive() {
         return active;
      }

      @Override
      public boolean isOseeAdmin() {
         return admin;
      }

      @Override
      public String getEmail() {
         return email;
      }

      @Override
      public String getPhone() {
         return phone;
      }

      @Override
      public List<IUserGroupArtifactToken> getRoles() {
         return roles;
      }

      @Override
      public String toString() {
         return String.format("UserToken [name [%s], userId=[%s], active=[%s], email=[%s], loginIds=[%s], roles=[%s]",
            getName(), userId, active, email, getLoginIds(), getRoles());
      }

      @Override
      public BranchToken getBranch() {
         return CoreBranches.COMMON;
      }

      @Override
      public List<String> getLoginIds() {
         return loginIds;
      }

      @Override
      public ArtifactToken getArtifact() {
         return artifact;
      }

      @Override
      public void setArtifact(ArtifactToken artifact) {
         this.artifact = artifact;
      }

      @Override
      public int hashCode() {
         if (Strings.isValid(userId)) {
            return userId.hashCode();
         }
         return id.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof UserToken) {
            if (Strings.isValid(userId) && Strings.isValid(((UserToken) obj).getUserId())) {
               return userId.equals(((UserToken) obj).getUserId());
            }
         }
         return super.equals(obj);
      }
   }
}